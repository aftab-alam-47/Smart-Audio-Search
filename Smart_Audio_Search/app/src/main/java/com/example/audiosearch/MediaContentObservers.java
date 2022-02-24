package com.example.audiosearch;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;

public class MediaContentObservers {
    private Context mContext;
    String Tag="tilak: "+MediaContentObservers.class.getName();
    private static final boolean NOTIFY_FOR_DECENDANTS = true;
    SpeechToTextConverter obj;

    public MediaContentObservers(Context context){
        mContext=context;
        obj=new SpeechToTextConverter(mContext);
    }

    public final ContentObserver externalAudioContentObserver =
            new ContentObserver(new Handler()) {
                @Override
                public void onChange(boolean selfChange, @Nullable Uri uri, int flags) {
                    super.onChange(selfChange, uri, flags);
                    if(flags== ContentResolver.NOTIFY_INSERT) {
                        Log.d(Tag, "onChange: Start");

                        String path = "";
                        String dispName = "";

                        Log.d(Tag, "readFromMediaStore: Start");
                        Cursor cursor = mContext.getContentResolver().query(uri, null, null,
                                null, "date_added DESC");

                        if (cursor.moveToNext()) {
                            int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                            int titleColunm = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
                            path = cursor.getString(dataColumn);
                            dispName = cursor.getString(titleColunm);
                            int mimeTypeColumn = cursor
                                    .getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE);
                            String mimeType = cursor.getString(mimeTypeColumn);

                        }
                        cursor.close();

                        Log.d(Tag, "readFromMediaStore: End");

                        Toast.makeText(mContext,
                                "New Audio file Added with path: " + path, Toast.LENGTH_LONG).show();

                        String finalPath = path;
                        String finalDispName = dispName;
                        Thread thread = new Thread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    obj.asyncRecognizeFile(finalPath, finalDispName);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        thread.start();
                        Log.d(Tag, "onChange: End : path:" + path);
                    }else{
                        //To do on deletion
                    }
                }
            };

    public final ContentObserver externalVideoContentObserver =
            new ContentObserver(new Handler()) {
                @Override
                public void onChange(boolean selfChange, @Nullable Uri uri, int flags) {
                    super.onChange(selfChange, uri, flags);
                    if(flags== ContentResolver.NOTIFY_INSERT) {
                        Log.d(Tag, "onChange: Start");
                        String path = "";
                        String dispName = "";

                        Log.d(Tag, "readFromMediaStore: Start");
                        Cursor cursor = mContext.getContentResolver().query(uri, null, null,
                                null, "date_added DESC");

                        if (cursor.moveToNext()) {
                            int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                            int titleColunm = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
                            path = cursor.getString(dataColumn);
                            dispName = cursor.getString(titleColunm);
                            int mimeTypeColumn = cursor
                                    .getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE);
                            String mimeType = cursor.getString(mimeTypeColumn);

                        }
                        cursor.close();

                        Log.d(Tag, "readFromMediaStore: End");

                        Toast.makeText(mContext,
                                "New Video file Added with path: " + path, Toast.LENGTH_LONG).show();

                        String f_in = path;
                        String f_out = path + ".wav";

                        int rc = FFmpeg.execute("-i " + f_in + " -map_channel 0.1.0 " + f_out);

                        if (rc == RETURN_CODE_SUCCESS) {
                            Log.i(Config.TAG, "Command execution completed successfully.");
                        } else if (rc == RETURN_CODE_CANCEL) {
                            Log.i(Config.TAG, "Command execution cancelled by user.");
                        } else {
                            Log.i(Config.TAG, String.format("Command execution failed with rc=%d and the output below.", rc));
                            Config.printLastCommandOutput(Log.INFO);
                        }

                        String finalPath = path;
                        String finalDispName = dispName;
                        Thread thread = new Thread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    obj.asyncRecognizeFile(f_out, finalDispName);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        thread.start();
                        Log.d(Tag, "onChange: End : path:" + path);


                    }else{
                        //To do on deletion
                    }

                }
            };

    public void register(){
        getContext().getContentResolver().registerContentObserver(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                NOTIFY_FOR_DECENDANTS,
                externalAudioContentObserver
        );

        getContext().getContentResolver().registerContentObserver(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                NOTIFY_FOR_DECENDANTS,
                externalVideoContentObserver
        );
    }

    public void unregister(){
        getContext().getContentResolver().unregisterContentObserver(externalAudioContentObserver);
        getContext().getContentResolver().unregisterContentObserver(externalVideoContentObserver);
    }


    public Context getContext() {
        return mContext;
    }


}
