package com.example.ewaew.muzyka;
import java.util.ArrayList;
import java.util.Random;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Ewa Lyko on 11.05.2018.
 */

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private MediaPlayer player;
    private ArrayList<Song> songs;
    private int songPos;
    private final IBinder musicBind = new MusicBinder();
    private String songTitle="";
    private static final int NOTIFY_ID=1;



    private static final float FAST = 2.0f ;
    private static final float SLOW = 0.5f;
    private static final float NORMAL = 1.0f;

    private static final String FAST_TXT = "fast" ;
    private static final String SLOW_TXT = "slow";


    public void onCreate(){
        super.onCreate();
        songPos=0;
        player = new MediaPlayer();
        initMusicPlayer();


    }

    public void initMusicPlayer(){
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setList(ArrayList<Song> theSongs){
        songs=theSongs;
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void playSong(){
        player.reset();
        Song playSong = songs.get(songPos);
        songTitle=playSong.getTitle();
        long currSong = playSong.getID();

        String fileName = findString(currSong);
        Uri trackUri = Uri.parse("android.resource://"+getPackageName()+fileName);
        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();

    }

    private String findString(long currSong) {
        String name;
        if(currSong==1)
            name = "/raw/here_without_you";
        else if(currSong ==2)
            name ="/raw/here_and_now";
        else if(currSong ==3)
            name ="/raw/everything_sucks";
        else if(currSong ==4)
            name ="/raw/its_my_life";
        else
            name ="/raw/the_wall";
        return name;

    }

    public void setSong(int songIndex){
        songPos=songIndex;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCompletion(MediaPlayer mp) {
        if(player.getCurrentPosition()>0){
            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.v("MUSIC PLAYER", "Playback Error");
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mp.isPlaying())
            mp.release();
        mp.start();
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(songTitle);
        Notification not = builder.build();
        startForeground(NOTIFY_ID, not);
    }

    public int getPosition(){
        return player.getCurrentPosition();
    }

    public int getDuration(){
        return player.getDuration();
    }

    public boolean isPlaying(){
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();
    }

    public void seek(int pos){
        player.seekTo(pos);
    }

    public void go(){
        player.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void playPrevios(){
        songPos--;
        if(songPos<0) songPos=songs.size()-1;
        playSong();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void playNext(){
        songPos++;
        if(songPos>=songs.size()) songPos=0;
        playSong();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setSpeed(String speed){
        if(player!=null) {
            if (speed.equals(FAST_TXT)) {
                player.setPlaybackParams(player.getPlaybackParams().setSpeed(FAST));
            } else if (speed.equals(SLOW_TXT))
                player.setPlaybackParams(player.getPlaybackParams().setSpeed(SLOW));
            else
                player.setPlaybackParams(player.getPlaybackParams().setSpeed(NORMAL));
        }
        else
        {
            Toast.makeText(getApplicationContext(),"NOTHING IS PLAYING",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }
}