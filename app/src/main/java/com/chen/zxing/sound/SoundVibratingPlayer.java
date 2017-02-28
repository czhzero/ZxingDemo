package com.chen.zxing.sound;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;


import com.chen.zxing.R;

import java.io.IOException;

/**
 * Created by chenzhaohua on 16/3/24.
 */
public class SoundVibratingPlayer {

    private static final float BEEP_VOLUME = 0.10f;
    private static final long VIBRATE_DURATION = 200L;


    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private boolean playBeep = true;
    private boolean vibrate = true;


    public SoundVibratingPlayer(Context context) {
        initBeepSoundAndVibrate(context);
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };


    private void initBeepSoundAndVibrate(Context context) {

        AudioManager audioService = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }

        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = context.getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
        if (vibrator == null) {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
    }


    public void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }



}
