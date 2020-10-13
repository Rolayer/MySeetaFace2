# MySeetaFace2
本项目对外提供http接口注册人脸、搜索人脸返回人脸对应信息。

人脸识别运行环境配置：
首先现在对应的支持包：
bindata 下载地址：链接：https://pan.baidu.com/s/1OH-JaQXPEhiDoKkPdZTrEg  提取码：p9aa 
libs 下载地址（包含windows64位和Linux的lib）：链接：https://pan.baidu.com/s/11r5U4yR13A7B-Pu_wkAARQ 提取码：h7rq 
下载完成后，根据自己的环境对seetaface.properties文件的配置做修改，配置如下：

#依赖的lib名称，注意依赖关系顺序，用逗号隔开
#linux os
#libs=holiday,SeetaFaceDetector200,SeetaPointDetector200,SeetaFaceRecognizer200,SeetaFaceCropper200,SeetaFace2JNI
#windows os
libs=libgcc_s_sjlj-1,libeay32,libquadmath-0,ssleay32,libgfortran-3,libopenblas,holiday,SeetaFaceDetector200,SeetaPointDetector200,SeetaFaceRecognizer200,SeetaFaceCropper200,SeetaFace2JNI

#依赖的lib存放目录, 等同于-Djava.library.path=
#linux os
#libs.path=/usr/local/seetaface2/lib
#windows os
libs.path=G://pengbenlei//Works//seetafaceJNI//doc//lib-win-x64//lib

#seetaface model目录
#linux os
#bindata.dir=/usr/local/seetaface2/bindata
#windows os
bindata.dir=G://BaiduNetdiskDownload//bindata

其中linux的执行环境相对复杂一点，需要搭建编译环境，具体流程见：https://my.oschina.net/u/1580184/blog/3042404 照着操作就行了。

项目中未主项目中的某个模块，关于数据库中的操作部分，需要自己整合。


