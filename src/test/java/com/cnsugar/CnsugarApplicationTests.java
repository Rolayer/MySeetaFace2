package com.cnsugar;

import com.face.FaceHelper;
import com.face.SeetafaceBuilder;
import com.face.dto.Result;
import com.face.service.IFaceIndexService;
import com.leenleda.common.data.FaceIndex;
import com.leenleda.common.mapper.FaceIndexMapper;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

@SpringBootTest
class CnsugarApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    IFaceIndexService faceIndexService;

    private void init() {
        SeetafaceBuilder.build();//系统启动时先调用初始化方法

        // 初始化人脸库
        faceIndexService.loadFaceDb();
    }

    @Test
    void test01() throws IOException {
        init();

        String img1 = "D:\\Users\\Coder\\Desktop\\新建文件夹\\4.jpg";
        String img2 = "D:\\Users\\Coder\\Desktop\\新建文件夹\\3.jpg";
        System.out.println("result:" + FaceHelper.compare(new File(img1), new File(img2)));
    }

    @Test
    void test02() {
        // 注册人脸
        Collection<File> files = FileUtils.listFiles(new File("D:\\Users\\Coder\\Desktop\\人脸"), new String[]{"jpg", "png"}, false);
        for (File file : files) {
            String key = file.getName();
            try {
//                faceIndexService.faceRegister(key, FileUtils.readFileToByteArray(file));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    void test03() throws IOException {
        init();
        long l = System.currentTimeMillis();
        Result result = faceIndexService.search(FileUtils.readFileToByteArray(new File("D:\\Users\\Coder\\Desktop\\新建文件夹\\8.jpg")));
        System.out.println("搜索结果：" + result + "， 耗时：" + (System.currentTimeMillis() - l));
    }

    @Test
    void test04() {
        faceIndexService.clear();
    }

    @Resource
    FaceIndexMapper faceIndexMapper;

    @Test
    void test05() {
        FaceIndex faceIndex = faceIndexMapper.selectByPrimaryKey(1);
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File("G:");
            if (!dir.exists() && dir.isDirectory()) {// 判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File("1.jpg");
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(faceIndex.getImageData());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
