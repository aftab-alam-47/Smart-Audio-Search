package com.example.audiosearch;

import android.content.Context;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.*;
import java.lang.String.*;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchAudioFiles {
    ArrayList<Integer> occurences=new ArrayList<Integer>(1024);
    ArrayList<Integer> timestamp = new ArrayList<Integer>(1024);
    Context mContext;
    Map<String, ?> allEntries;
    public SearchAudioFiles(Context context){
        mContext=context;
    }

    public void getFileMap(){
        allEntries= mContext.getSharedPreferences("MAP_PERF", Context.MODE_PRIVATE).getAll();

    }
    int KMPSearch(String pat, String txt)
    {
        int M = pat.length();
        int N = txt.length();
        int lps[] = new int[M];
        int j = 0;
        computeLPSArray(pat,M,lps);

        int i = 0; // index for txt[]
        int res = 0;
        int next_i = 0;

        while (i < N)
        {
            if (pat.charAt(j) == txt.charAt(i))
            {
                j++;
                i++;
            }
            if (j == M)
            {
                j = lps[j-1];
                res++;

                if (lps[j]!=0)
                    i = ++next_i;
                j = 0;
            }

            else if (i < N && pat.charAt(j) != txt.charAt(i))
            {
                if (j != 0)
                    j = lps[j-1];
                else
                    i = i+1;
            }
        }
        return res;
    }

    void computeLPSArray(String pat, int M, int lps[])
    {
        int len = 0;
        int i = 1;
        lps[0] = 0;

        while (i < M)
        {
            if (pat.charAt(i) == pat.charAt(len))
            {
                len++;
                lps[i] = len;
                i++;
            }
            else
            {

                if (len != 0)
                {
                    len = lps[len-1];

                }
                else
                {
                    lps[i] = len;
                    i++;
                }
            }
        }
    }

    int nthOccurrence(String str1, String str2, int n) {

        String tempStr = str1;//main string
        int tempIndex = -1;
        int finalIndex = 0;
        for(int occurrence = 0; occurrence < n ; ++occurrence){
            tempIndex = tempStr.indexOf(str2);
            if(tempIndex==-1){
                finalIndex = 0;
                break;
            }
            tempStr = tempStr.substring(++tempIndex);
            finalIndex+=tempIndex;
        }
        return --finalIndex;
    }

    int ordinalIndexOf(String str, String substr, int n) {
        int pos = str.indexOf(substr);
        while (--n > 0 && pos != -1)
            pos = str.indexOf(substr, pos + 1);
        return pos;
    }

    public ArrayList<Integer> getCountArray(){
        return occurences;
    }
    public ArrayList<Integer> getTimeArray(){
        return timestamp;
    }


    public ArrayList<String> getMatchedFiles(String searchedWord){
        ArrayList<String> matchedFiles=new ArrayList<>();
        occurences.clear();
        timestamp.clear();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
            String textPath=entry.getKey();
            try {
                File file=new File(textPath);
                FileInputStream in = new FileInputStream(file);
                int len = 0;
                byte[] data1 = new byte[1024000];//this is the file
                int count=0, checking=0;
                int time_in_milli=0;
                while ( -1 != (len = in.read(data1)) ){
                    String data=new String(data1, 0, len);//this is string

//                    System.out.println(data);
                    String searchedWord1 = '"'+searchedWord+" ";
                    String searchedWord2 = " "+searchedWord+'"';
                    String searchedWord3 = " "+searchedWord+" ";

                    if(data.contains(searchedWord3)||data.contains(searchedWord1)||data.contains(searchedWord2)){
                        int answer = KMPSearch(searchedWord,data);
                        count+=answer;

                        int new_check=0, d=0;
                        int token_count = 0, token_check=0, nth_occ_sec = 0;

                        //occurences.add((answer/2));
                        //System.out.print("Counting is: ");
                        System.out.println("original count from kmp is: " + count);
                        if(d==0){
                            String str1 = "";
                            int colon_check =0;
                            while(data.charAt(colon_check)!='"'){
                                colon_check++;
                                continue;
                            }
                            colon_check++;
                            while(data.charAt(colon_check)!='"'){
                                str1 = str1 + data.charAt(colon_check);
                                colon_check++;
                            }
                            System.out.println("string is : " + str1);
                            //StringTokenizer str = new StringTokenizer(str1, " ");
                            //System.out.println("search word is: " + searchedWord);
                            String[] tokens = str1.split(" ");
                            for(String token : tokens){
                                //System.out.println("tokes : " + token + " searchWord : " + searchedWord);
                                if(!(token.equals(searchedWord)) && !(token.equals(searchedWord1)) && !(token.equals(searchedWord2)) && !(token.equals(searchedWord3))) token_count++;
                                else break;
                            }
                            token_count++;
//                            while(str.hasMoreTokens()){
//                                token_count++;//gives number of tokens before searchword
////                            System.out.println("next token is " + str.nextToken());
//                                if(str.nextToken()==searchedWord){
//                                    //token_count++;
//                                    System.out.println("breaking here ");
//                                    break;
//                                }
//                            }
                            //token_count++;
                            d++;
                        }
                        System.out.println("token counts before search words: " + token_count);
                        //System.out.println(data);
                        int sec = 0, nth_occ_nano=0, nano=0;
                        nth_occ_sec = ordinalIndexOf(data, "seconds:", ((2*token_count)-1));
                        System.out.println("nth occurence index is: " + nth_occ_sec);
                        String c = "";
                        int skip = 9;
                        System.out.println(data.charAt(nth_occ_sec));
                        while(data.charAt(nth_occ_sec+skip)!=' '&&data.charAt(nth_occ_sec+skip)!='n'&&data.charAt(nth_occ_sec+skip)!='\n'){
                            c+=data.charAt(nth_occ_sec+skip);
                            skip++;
                        }
                        if(c!="") {
                            sec = Integer.parseInt(c);
                        }
                        //nth_occ_nano = nthOccurrence(data, "nanos:", token_count);
                        //String st = data.substring(nth_occ_nano+7, nth_occ_nano+10);
                        System.out.println("sec is : " + sec);
                        //nano = Integer.parseInt(st);
                        //System.out.println("nano is : " + nano);
                        //time_in_milli = sec*1000 + nano;

                        time_in_milli = sec*1000;
                        System.out.println("time in milli is: " + time_in_milli);
                    }
                }
//                len = 0;
//                File file1=new File(textPath);
//                FileInputStream in1 = new FileInputStream(file1);
//                byte[] data3 = new byte[1024];//this is the file
//                int new_check=0, d=0;
//                int token_count = 0, token_check=0, nth_occ_sec = 0;
//                while ( -1 != (len = in1.read(data3)) ){
//                    String data=new String(data3, 0, len);//this is string
//                    if(d==0){
//                        String str1 = "";
//                        int colon_check =0;
//                        while(data.charAt(colon_check)!='"'){
//                            colon_check++;
//                            continue;
//                        }
//                        colon_check++;
//                        while(data.charAt(colon_check)!='"'){
//                            str1 = str1 + data.charAt(colon_check);
//                            colon_check++;
//                        }
//                        System.out.println("string is " + str1);
//                        StringTokenizer str = new StringTokenizer(str1, " ");
//
//                        while(str.hasMoreTokens()){
//                            token_count++;//gives number of tokens before searchword
////                            System.out.println("next token is " + str.nextToken());
//                            if(str.nextToken()==searchedWord){
//                                //token_count++;
//                                System.out.println("breaking here ");
//                                break;
//                            }
//                        }
//                        //token_count++;
//                        d++;
//                    }
//                    System.out.println("token counts before search words" + token_count);
//                }
//                byte[] data2 = new byte[102400];
//                int sec = 0, nth_occ_nano=0, nano=0, time_in_milli=0;
//                len = 0;
//                while ( -1 != (len = in.read(data2)) ){
//                    String data=new String(data2, 0, len);
//
//                    nth_occ_sec = nthOccurrence(data, "second:", token_count);
//                    System.out.println("nth occurence" + nth_occ_sec);
//                    String c = "";
//                    int skip = 9;
//                    while(data.charAt(nth_occ_sec+skip)!=' '&&data.charAt(nth_occ_sec+skip)!='\n'){
//                        c+=data.charAt(nth_occ_sec+skip);
//                        skip++;
//                    }
//                    if(c!="") {
//                        sec = Integer.parseInt(c);
//                    }
//                    nth_occ_nano = nthOccurrence(data, "nanos:", token_count);
//                    String st = data.substring(nth_occ_nano+7, nth_occ_nano+10);
//                    System.out.println("sec is" + sec);
//                    nano = Integer.parseInt(st);
//                    System.out.println("nano is" + nano);
//                    time_in_milli = sec*1000 + nano;
//                }

                if(count>0){
                    //int x = indexOf(searchedWord);
                    Log.d("tilak data ",textPath);
                    matchedFiles.add(entry.getValue().toString());
                    System.out.println("count is " + count/2);
//                    System.out.println("time in milli is" + time_in_milli);
                    occurences.add(count/2);
                    timestamp.add(time_in_milli);

                }

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return matchedFiles;
    }

    public ArrayList<String> searchFiles(String searchWord){
        ArrayList<String> files=new ArrayList<>();
        getFileMap();
        files=getMatchedFiles(searchWord);

        return files;
    }

    public String getAudioFilePath(String textFilePath){
        getFileMap();
        return allEntries.get(textFilePath).toString();
    }


}
