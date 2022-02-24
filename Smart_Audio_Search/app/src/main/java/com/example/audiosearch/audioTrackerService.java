package com.example.audiosearch;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.util.Date;

public class audioTrackerService extends Service {
    String Tag="tilak";
    MediaContentObservers mediaContentObservers;
    Context mContext;
    public audioTrackerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext=getApplicationContext();
         mediaContentObservers=new MediaContentObservers(mContext);
         mediaContentObservers.register();

//        SpeechToTextConverter obj=new SpeechToTextConverter(mContext);
//
//
//        getContentResolver().registerContentObserver(
//                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, true, new ContentObserver(new Handler()) {
//
//                    @Override
//                    public void onChange(boolean selfChange, @Nullable Uri uri, int flags) {
//                        super.onChange(selfChange, uri, flags);
//                        Log.d(Tag,"onChange: Start");
//
//                        String path="";
//                        String dispName="";
//
//                        Log.d(Tag,"readFromMediaStore: Start");
//                        Cursor cursor = mContext.getContentResolver().query(uri, null, null,
//                                null, "date_added DESC");
//
//                        if (cursor.moveToNext()) {
//                            int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
//                            int titleColunm=cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
//                            path = cursor.getString(dataColumn);
//                            dispName=cursor.getString(titleColunm);
//                            int mimeTypeColumn = cursor
//                                    .getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE);
//                            String mimeType = cursor.getString(mimeTypeColumn);
//
//                        }
//                        cursor.close();
//
//                        Log.d(Tag,"readFromMediaStore: End");
//
//
//
//                        //String p=uri.getEncodedPath();
//                        //String filename = "storage/emulated/0/Music/audio_1.opus";
//
//                        Toast.makeText(getApplicationContext(),
//                                "New Audio file Added with path: "+path,Toast.LENGTH_LONG).show();
//
//                        String finalPath = path;
//                        String finalDispName = dispName;
//                        Thread thread=new Thread(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                try {
//                                    obj.asyncRecognizeFile(finalPath,finalDispName);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//
//                            }
//                        });
//                        thread.start();
//
//
//
//
//
//                        Log.d(Tag,"onChange: End : path:"+path);
//
//
//                    }
//                }
//        );


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mediaContentObservers.unregister();
    }






}