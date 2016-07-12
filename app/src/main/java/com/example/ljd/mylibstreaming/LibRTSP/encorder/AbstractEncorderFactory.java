package com.example.ljd.mylibstreaming.LibRTSP.encorder;

import android.media.projection.MediaProjection;

import com.example.ljd.mylibstreaming.LibRTSP.quality.MediaQuality;

/**
 * Created by ljd-pc on 2016/7/4.
 */
abstract public class AbstractEncorderFactory {
    abstract public MediaEncorder CreateEncorder(MediaQuality mRequestedQuality, MediaProjection mMediaProjection);
}
