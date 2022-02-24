package com.example.audiosearch;

import android.content.res.Resources;
import android.content.res.loader.ResourcesProvider;
import android.net.Uri;

import androidx.core.graphics.PathUtils;

import com.google.api.Context;
import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SpeechCredentialProvider implements CredentialsProvider {
    //Context mContext;
    public SpeechCredentialProvider(){

    }
    String pacName="com.example.audiosearch";
    @Override
    public Credentials getCredentials() throws IOException {

        //InputStream filestream= Resources.getSystem().openRawResource(R.raw.credentials);
        String path = "android.resource://" + pacName + "/raw/credentials";
        Uri uri = Uri.parse(path);
        String filepath= uri.getPath();
        FileInputStream file=new FileInputStream(filepath);
        return ServiceAccountCredentials.fromStream(file);
    }
}
