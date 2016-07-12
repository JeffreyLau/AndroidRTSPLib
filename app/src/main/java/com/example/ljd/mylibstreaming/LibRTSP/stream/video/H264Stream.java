package com.example.ljd.mylibstreaming.LibRTSP.stream.video;

import android.media.MediaCodec;
import android.util.Base64;

import com.example.ljd.mylibstreaming.LibRTSP.mp4.PpsSpsGetter;
import com.example.ljd.mylibstreaming.LibRTSP.rtp.H264Packetizer;
import com.example.ljd.mylibstreaming.LibRTSP.rtp.MediaCodecInputStream;
import com.example.ljd.mylibstreaming.LibRTSP.session.Session;

import java.io.IOException;

/**
 * Created by ljd-pc on 2016/6/22.
 */
public class H264Stream extends VideoStream {

    public final static String TAG = "H264Stream";
    PpsSpsGetter mPpsSpsGetter;

    public H264Stream(MediaCodec mediaCodec, Session mSession){
        super(mSession);
        this.mMediaCodec = mediaCodec;
        mPacketizer = new H264Packetizer();

    }

    /**
     * Returns a description of the stream using SDP. It can then be included in an SDP file.
     */
    public synchronized String getSessionDescription() throws IllegalStateException {
        if (mPpsSpsGetter == null) throw new IllegalStateException("You need to call configure() first !");
        return "m=video "+String.valueOf(getDestinationPorts()[0])+" RTP/AVP 96\r\n" +
                "a=rtpmap:96 H264/90000\r\n" +
                "a=fmtp:96 packetization-mode=1;profile-level-id="+mPpsSpsGetter.getProfileLevel()+";sprop-parameter-sets="+mPpsSpsGetter.getB64SPS()+","+mPpsSpsGetter.getB64PPS()+";\r\n"+
                "a=control:trackID="+1+"\r\n";
    }

    /**
     * Configures the stream. You need to call this before calling {@link #getSessionDescription()} to apply
     * your configuration of the stream.
     */
    public synchronized void configure() throws IllegalStateException, IOException {
        super.configure();
        mPpsSpsGetter = new PpsSpsGetter(mRequestedQuality);
    }

    /**
     * Starts the stream.
     *
     */
    public synchronized void start() throws IllegalStateException, IOException {
        if (!mStreaming) {
            //configure();//生成本地视频文件提取H.264的sps pps
            byte[] pps = Base64.decode(mPpsSpsGetter.getB64PPS(), Base64.NO_WRAP);
            byte[] sps = Base64.decode(mPpsSpsGetter.getB64SPS(), Base64.NO_WRAP);
            ((H264Packetizer)mPacketizer).setStreamParameters(pps, sps);
            mPacketizer.setInputStream(new MediaCodecInputStream(mMediaCodec));
            mPacketizer.start();//每次start，新建一个线程
            mStreaming = true;
            super.start();
        }
    }


}
