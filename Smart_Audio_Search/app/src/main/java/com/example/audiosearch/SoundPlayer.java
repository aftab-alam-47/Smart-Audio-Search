package com.example.audiosearch;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

public class SoundPlayer  {


    public SoundPlayer(){

    }

    public void play(Context context, String name,MediaPlayer mMediaPlayer) {

        try {
            if (mMediaPlayer!=null){
                mMediaPlayer.stop();
                mMediaPlayer.reset();
            }
            mMediaPlayer.setDataSource(context,Uri.parse(name));
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //mMediaPlayer.start();


        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.stop();
                mediaPlayer.reset();

                //mediaPlayer.release();
                //mediaPlayer=null;
            }
        });

        mMediaPlayer.start();
    }


}
