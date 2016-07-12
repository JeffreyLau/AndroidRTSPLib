package com.example.ljd.mylibstreaming.LibRTSP.encorder.video;

import android.media.projection.MediaProjection;

import com.example.ljd.mylibstreaming.LibRTSP.encorder.AbstractEncorderFactory;
import com.example.ljd.mylibstreaming.LibRTSP.encorder.MediaEncorder;
import com.example.ljd.mylibstreaming.LibRTSP.quality.MediaQuality;
import com.example.ljd.mylibstreaming.LibRTSP.quality.VideoQuality;

/**
 * Created by ljd-pc on 2016/7/4.
 */
public class VideoEncorderFactory extends AbstractEncorderFactory {

    private static volatile VideoEncorderFactory videoEncorderFactory;

    private VideoEncorderFactory(){}

    public static VideoEncorderFactory getInstance(){
        if(videoEncorderFactory == null){
            synchronized (VideoEncorderFactory.class){
                if(videoEncorderFactory == null){
                    videoEncorderFactory = new VideoEncorderFactory();
                }
            }
        }
        return videoEncorderFactory;
    }

    @Override
    public MediaEncorder CreateEncorder(MediaQuality mRequestedQuality, MediaProjection mMediaProjection) {
        return new H264Encorder((VideoQuality) mRequestedQuality,mMediaProjection);
    }
}
