package com.zhuangxiuweibao.app.common.audio;

import android.media.MediaPlayer;

/**
 * 播放，暂停录音
 */
public class MediaManager {
    private static MediaPlayer mMediaPlayer;
    private static boolean isPause;

    /**
     * 播放音乐
     *
     * @param filePath
     * @param onCompletionListener
     */
    public static void playSound(String filePath, MediaPlayer.OnCompletionListener
            onCompletionListener) {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            //设置一个error监听器
            mMediaPlayer.setOnErrorListener((arg0, arg1, arg2) -> {
                mMediaPlayer.reset();
                return false;
            });
        } else {
            mMediaPlayer.reset();
        }
        try {
            mMediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(onCompletionListener);
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Exception e) {

        }
    }

    /**
     * 暂停播放
     */
    public static void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            //正在播放的时候
            mMediaPlayer.pause();
            isPause = true;
        }
    }

    public static boolean isPlaying() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 当前是isPause状态
     */
    public static void resume() {
        if (mMediaPlayer != null && isPause) {
            mMediaPlayer.start();
            isPause = false;
        }
    }

    /**
     * 释放资源
     */
    public static void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
