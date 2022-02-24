package com.example.audiosearch;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.api.client.util.Lists;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.LongRunningRecognizeMetadata;
import com.google.cloud.speech.v1.LongRunningRecognizeResponse;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.protobuf.ByteString;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class SpeechToTextConverter {
    Context mContext;
    public SpeechToTextConverter(Context context){
        mContext=context;
    }

    /**
     * Performs non-blocking speech recognition on raw PCM audio and prints the transcription. Note
     * that transcription is limited to 60 seconds audio.
     *
     * @param fileName the path to a PCM audio file to transcribe.
     */
    public void asyncRecognizeFile(String fileName,String name) throws Exception {
        // Instantiates a client with GOOGLE_APPLICATION_CREDENTIALS
        InputStream stream=mContext.getResources().openRawResource(R.raw.credentials);
        GoogleCredentials credentials = GoogleCredentials.fromStream(stream).
                createScoped(Lists.newArrayList(Collections.singleton("https://www.googleapis.com/auth/cloud-platform")));
        SpeechSettings settings=SpeechSettings.newBuilder().setCredentialsProvider(
                FixedCredentialsProvider.create(credentials)
        ).build();

        try (SpeechClient speech = SpeechClient.create(settings)) {

            Path path = Paths.get(fileName);
            byte[] data = Files.readAllBytes(path);
            ByteString audioBytes = ByteString.copyFrom(data);

            // Configure request with local raw PCM audio
            RecognitionConfig config=null;
            String ext=getExtension(fileName);
            if(ext.equals("opus")){
                Log.d("tilak","extension: "+ext);
                config =
                        RecognitionConfig.newBuilder()
                                .setEncoding(RecognitionConfig.AudioEncoding.OGG_OPUS)
                                .setLanguageCode("en-IN")
                                .setSampleRateHertz(16000)
                                .setEnableWordTimeOffsets(true)
                                .build();

            }else if(ext.equals("wav")){
                Log.d("tilak","extension: "+ext);
                config =
                        RecognitionConfig.newBuilder()
                                .setEncoding(RecognitionConfig.AudioEncoding.ENCODING_UNSPECIFIED)
                                .setLanguageCode("en-IN")
                                .setEnableWordTimeOffsets(true)
                                .build();
            }else if(ext.equals("flac")){
                Log.d("tilak","extension: "+ext);
                config =
                        RecognitionConfig.newBuilder()
                                .setEncoding(RecognitionConfig.AudioEncoding.FLAC)
                                .setLanguageCode("en-IN")
                                .setEnableWordTimeOffsets(true)
                                .build();
            }else if(ext.equals("amr")){
                Log.d("tilak","extension: "+ext);
                config =
                        RecognitionConfig.newBuilder()
                                .setEncoding(RecognitionConfig.AudioEncoding.AMR_WB)
                                .setLanguageCode("en-IN")
                                .setEnableWordTimeOffsets(true)
                                .setSampleRateHertz(16000)
                                .build();
            }else{
                Log.d("tilak","extension:else "+ext);
                config =
                        RecognitionConfig.newBuilder()
                                .setEncoding(RecognitionConfig.AudioEncoding.ENCODING_UNSPECIFIED)
                                .setLanguageCode("en-IN")
                                .setEnableWordTimeOffsets(true)

                                .build();
            }
            RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();




            // Use non-blocking call for getting file transcription

            OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> response =
                    speech.longRunningRecognizeAsync(config, audio);

            while (!response.isDone()) {
                System.out.println("Waiting for response...");
                Thread.sleep(10000);
            }

            List<SpeechRecognitionResult> results = response.get().getResultsList();
            //String savepath=mContext.getApplicationInfo().dataDir;
            int counter=0;
            SharedPreferences.Editor editor=mContext.getSharedPreferences("MAP_PERF",Context.MODE_PRIVATE).edit();
            SharedPreferences cnt= mContext.getSharedPreferences("COUNT_PERF",Context.MODE_PRIVATE);

            counter=cnt.getInt("counter",0);
            counter++;

            String savepath="storage/emulated/0/Android/"+mContext.getPackageName()+"/converted/";
            Log.d("tilak","save dir :"+savepath);
            String savePathFinal=savepath+"output_speech_"+counter+".json";
            File myFile = new File(savePathFinal);
//            try {
                myFile.createNewFile();
                FileOutputStream fOut = new FileOutputStream(myFile);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

//            } catch (Exception e) {
//                Log.e("ERRR", "Could not create file",e);
//            }

            editor.putString(savePathFinal,fileName);
            editor.commit();
            editor=cnt.edit();
            editor.putInt("counter",counter);
            editor.commit();

            for (SpeechRecognitionResult result : results) {
                // There can be several alternative transcripts for a given chunk of speech. Just use the
                // first (most likely) one here.
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                System.out.printf("Transcription: %s%n", alternative.getTranscript());
                System.out.print(alternative.getWordsList());
                myOutWriter.append(result.toString());
                //myOutWriter.append(alternative.getWordsList().get(0).getStartTime().toString());

            }
            myOutWriter.close();
            fOut.close();
        }
    }


    private String getExtension(String path){
        String result="";
        for(int i=0;i<path.length();i++){
            if(path.charAt(i)=='.'){
                result ="";
            }else{
                result=result+path.charAt(i);
            }
        }
        return result;
    }





}
