package com.example.ljd.mylibstreaming.LibRTSP.encorder;

import android.media.MediaCodec;

/**
 * Created by ljd-pc on 2016/7/4.
 */
abstract public class MediaEncorder {
    public abstract void encodeWithMediaCodec();
    public abstract void stop();
    public abstract MediaCodec getMediaEncorder();
}
