
一、这是一个基于Android平台的RTSP直播、点播服务器。

二、实现三个功能：
    1、将屏幕图像实时推流
    2、将摄像头图像实时推流
    3、将本地mp4、3gp格式的视频文件推流。

三、使用方式
    1、客户端可以使用VLC等播放器播放视频流。
    2、播放器输入的链接格式为 rtsp://IP地址:端口号/选项
    3、选项包括：screen（实时获取Android手机、平板屏幕图像）
              camera（实时获取Android手机、平板摄像头图像）
              movie（点播Android手机、平板本地视频文件）
                    文件路径为SD卡根目录下的"/AndroidRTSPLib/movie.mp4",
                    需要将视频文件名称更改为movie.mp4。
                    同时也支持.3gp文件，通过修改源码“MainActivity.java”中的 “VIDEO_PATH = SDCARD_PATH+"/AndroidRTSPLib/movie.mp4";”实现。
              
    4、连接示例：rtsp://192.168.1.20:1234/screen   （实时获取Android手机、平板屏幕图像）
              rtsp://192.168.1.20:1234/camera   （实时获取Android手机、平板摄像头图像）
              rtsp://192.168.1.20:1234/movie    （点播Android手机、平板本地视频文件）
              rtsp://192.168.1.20:1234          （默认情况下，实时获取Android手机、平板屏幕图像）
    
    
  四、参考fyhertz的libstreaming制作
        libstreaming连接：
              https://github.com/fyhertz/libstreaming
    
