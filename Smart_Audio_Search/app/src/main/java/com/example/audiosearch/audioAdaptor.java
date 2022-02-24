package com.example.audiosearch;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.TintableBackgroundView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

import es.claucookie.miniequalizerlibrary.EqualizerView;

public class audioAdaptor extends RecyclerView.Adapter<audioAdaptor.ViewHolder> {

    private ArrayList<String> mAudioList;
    private ArrayList<Integer> mTimeArray;
    private ArrayList<Integer> mCountArray;
    SearchAudioFiles searchAudioFiles;
    Context mContext;
    MediaPlayer mMediaPlayer;
    SoundPlayer soundPlayer;
    int indexOfPlaying=-1;


    public audioAdaptor(ArrayList<String> list,ArrayList<Integer> TimeArray,ArrayList<Integer> CountArray,Context context,MediaPlayer player){
        mAudioList=list;
        mContext=context;
        mMediaPlayer=player;
        soundPlayer=new SoundPlayer();
        mTimeArray=TimeArray;
        mCountArray=CountArray;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View itemView=inflater.inflate(R.layout.audio_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(itemView);

        return viewHolder;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item=mAudioList.get(position);
        Integer timeStamp=mTimeArray.get(position);
        Integer count=mCountArray.get(position);

        holder.bind(position);
        holder.itemTextView.setText(getTitle(item));
        holder.countTextView.setText("Occurance: "+count);
        holder.timeTextView.setText("Time Stamp: "+timeStamp/1000+" sec");
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("tilak","item: Clicked: "+holder.getAdapterPosition());
                //searchAudioFiles=new SearchAudioFiles(mContext);
                String audioPath=(mAudioList.get(holder.getAdapterPosition()));
                Log.d("tilak","index of playing: "+indexOfPlaying);
                indexOfPlaying=holder.getAdapterPosition();


                Log.d("tilak","position : " +holder.getAdapterPosition());

//                if(mMediaPlayer!=null){
//                    mMediaPlayer.stop();
//                    mMediaPlayer.reset();
//                }
                if(audioPath.contains(".mp4")){

                    String vidPath=audioPath.substring(0,audioPath.length()-4);

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(audioPath));
                    intent.setDataAndType(Uri.parse(vidPath), "video/mp4");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }else
                {

                    try {
                        if (mMediaPlayer!=null){
                            mMediaPlayer.stop();
                            mMediaPlayer.reset();

                        }
                        mMediaPlayer.setDataSource(mContext, Uri.parse(audioPath));
                        mMediaPlayer.prepare();
                        mMediaPlayer.seekTo((timeStamp-1000)<0?0:timeStamp-1000);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            Log.d("tilak","onCompletion called");
                            Log.d("tilak","visibility before: " +holder.equalizerView.getVisibility());
                            mp.stop();
                            mp.reset();
                            //notifyDataSetChanged();
                            holder.equalizerView.stopBars();
                            holder.equalizerView.setVisibility(View.INVISIBLE);
                            holder.itemTextView.setTextColor(mContext.getColor(R.color.white));
                            holder.timeTextView.setTextColor(mContext.getColor(R.color.white));
                            holder.countTextView.setTextColor(mContext.getColor(R.color.white));

                            Log.d("tilak","visibility: " +holder.equalizerView.getVisibility());



                        }
                    });
                    mMediaPlayer.start();
                    notifyDataSetChanged();


                }



               // soundPlayer.play(mContext.getApplicationContext(), audioPath,mMediaPlayer);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mAudioList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemTextView,countTextView,timeTextView;
        EqualizerView equalizerView;
        ConstraintLayout layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTextView=itemView.findViewById(R.id.item_textview);
            countTextView=itemView.findViewById(R.id.textview_count);
            timeTextView=itemView.findViewById(R.id.textview_timeStamp);
            equalizerView=itemView.findViewById(R.id.equalizer_anim);
            layout=itemView.findViewById(R.id.layout_constraint);

        }
        public void bind(int index){
            if(indexOfPlaying==index){
                equalizerView.setVisibility(View.VISIBLE);
                equalizerView.animateBars();
                itemTextView.setTextColor(mContext.getResources().getColor(R.color.cherry_red));
                timeTextView.setTextColor(mContext.getResources().getColor(R.color.cherry_red));
                countTextView.setTextColor(mContext.getResources().getColor(R.color.cherry_red));
            }else{
                equalizerView.stopBars();
                equalizerView.setVisibility(View.INVISIBLE);
                itemTextView.setTextColor(mContext.getResources().getColor(R.color.white));
                timeTextView.setTextColor(mContext.getResources().getColor(R.color.white));
                countTextView.setTextColor(mContext.getResources().getColor(R.color.white));
            }
        }


    }

    public String getTitle(String path){
        String title="";

        for(int i=0;i<path.length();i++){
            if(path.charAt(i)=='/'){
                title="";
            }else{
                title=title+path.charAt(i);
            }
        }

        return title;
    }


}
