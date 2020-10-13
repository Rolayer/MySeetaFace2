package com.face;

import com.seetaface2.SeetaFace2JNI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * @Author Sugar
 * @Version 2019/4/22 14:28
 */
public class SeetafaceBuilder {
    private static Logger logger = LoggerFactory.getLogger(SeetafaceBuilder.class);
    public volatile static SeetaFace2JNI seeta = null;


    public static SeetaFace2JNI build() {
        if (seeta == null) {
            synchronized (SeetafaceBuilder.class) {
                if (seeta != null) {
                    return seeta;
                }
                init();
            }
        }
        return seeta;
    }



    private static void init() {
        Properties prop = getConfig();
        String separator = System.getProperty("path.separator");
        String sysLib = System.getProperty("java.library.path");
        if (sysLib.endsWith(separator)) {
            System.setProperty("java.library.path", sysLib + prop.getProperty("libs.path", ""));
        } else {
            System.setProperty("java.library.path", sysLib + separator + prop.getProperty("libs.path", ""));
        }
        try {
            //使java.library.path生效
            Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
            sysPathsField.setAccessible(true);
            sysPathsField.set(null, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        String[] libs = prop.getProperty("libs", "").split(",");
        for (String lib : libs) {
            logger.debug("load library: {}", lib);
            System.loadLibrary(lib);
        }
        String bindata = prop.getProperty("bindata.dir");
        logger.debug("bindata dir: {}", bindata);
        seeta = new SeetaFace2JNI();
        seeta.initModel(bindata);
        logger.info("Seetaface init completed!!!");
    }


    private static Properties getConfig() {
        Properties properties = new Properties();
        String location = "classpath:/seetaface.properties";
        try (InputStream is = new DefaultResourceLoader().getResource(location).getInputStream()) {
            properties.load(is);
            logger.debug("seetaface config: {}", properties.toString());
        } catch (IOException ex) {
            logger.error("Could not load property file:" + location, ex);
        }
        return properties;
    }
}
