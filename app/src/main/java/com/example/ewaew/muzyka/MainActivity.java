package com.example.ewaew.muzyka;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.MediaController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {
    @BindView(R.id.song_list) ListView songView;
    private ArrayList<Song> songList;

    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound=false;
    private boolean isShown = false;

    private SongAdapter adapter;

    private MusicController controller;
    private boolean paused=false, playbackPaused=false;



    private static final String FAST_TXT = "fast" ;
    private static final String SLOW_TXT = "slow";
    private static final String NORMAL_TXT = "normal";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ButterKnife.bind(this);
        initSongList();
        setAdapter();

        if(savedInstanceState!=null)
        {
            setController();
            if(musicService!=null && musicService.isPlaying())
                controller.setEnabled(false);
            else
                controller.setEnabled(true);
            controller.setMediaPlayer(this);
            controller.setAnchorView(songView);
        }
        else
            setController();
    }

    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            musicService = binder.getService();
            musicService.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
            if(musicService!=null && musicService.isPlaying())
                controller.setEnabled(true);
        }
    }

    private void setAdapter() {
        adapter = new SongAdapter(this, songList);
        songView.setAdapter(adapter);
    }

    private void setController() {
        controller = new MusicController(this);
        controller.setPrevNextListeners(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                playNextSong();
            }
        }, new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                playPrevSong();
            }
        });
        controller.setMediaPlayer(this);
        controller.setAnchorView(songView);
        controller.setEnabled(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("enabled",controller.isEnabled());
        if((Integer)musicService.getPosition()!=null)
            outState.putInt("position",musicService.getPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        savedInstanceState.getInt("position");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void songPicked(View view){
        SongAdapter.ViewHolder holder = (SongAdapter.ViewHolder) view.getTag();
        musicService.setSong(Integer.parseInt(Integer.toString(holder.pos)));

        musicService.playSong();
        if(!isShown) {
            controller.requestFocus();
            controller.show(0);
            isShown=true;
        }
        if(playbackPaused){
            setController();
            controller.show(musicService.getPosition());
            controller.requestFocus();
            playbackPaused=false;
        }
        controller.requestFocus();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void playNextSong(){
        musicService.playNext();
        if(playbackPaused){
            setController();
            controller.requestFocus();
            playbackPaused=false;
        }
        controller.requestFocus();
        controller.show(0);
        controller.requestFocus();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void playPrevSong(){
        musicService.playPrevios();
        if(playbackPaused){
            setController();
            controller.requestFocus();
            playbackPaused=false;
        }
        controller.requestFocus();
        controller.show(0);
        controller.requestFocus();
    }


    private void initSongList() {
        songList = new ArrayList<>();

        songList.add(new Song(1,"Here without you","3 Door Down",R.drawable.door_down_picture));
        songList.add(new Song(4,"It's my life","Bon Jovi",R.drawable.bon_jovi_picture));
        songList.add(new Song(5,"The wall","Pink Floyd",R.drawable.pink_floyd_picture));
        songList.add(new Song(2,"Here and now","Seether",R.drawable.seether_picture));
        songList.add(new Song(3,"Everything sucks","Simple plan",R.drawable.simple_plan_picture));


    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if(musicService !=null && musicBound && musicService.isPlaying())
            return musicService.getPosition();
        else return 0;
    }

    @Override
    public int getDuration() {
        if(musicService !=null && musicBound && musicService.isPlaying())
            return musicService.getDuration();
        else return 0;
    }

    @Override
    public boolean isPlaying() {
        if(musicService !=null && musicBound)
            return musicService.isPlaying();
        return false;
    }

    @Override
    public void pause() {
        playbackPaused=true;
        musicService.pausePlayer();
    }

    @Override
    public void seekTo(int pos) {
        musicService.seek(pos);
    }

    @Override
    public void start() {
        musicService.go();
    }

    @Override
    protected void onPause(){
        super.onPause();
        paused=true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(paused){
           setController();
            controller.requestFocus();
            paused=false;
        }
    }

    @Override
    protected void onStop() {
        controller.hide();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicService=null;
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.my_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.author:
                final Intent intent = new Intent(this, AbouthAutor.class);
                startActivity(intent);
                return true;
            case R.id.slow:
                musicService.setSpeed(SLOW_TXT);
                return true;
            case R.id.fast:
                musicService.setSpeed(FAST_TXT);
                return true;
            case R.id.normal:
                musicService.setSpeed(NORMAL_TXT);
                return true;
            case R.id.sort:
                Collections.sort(songList, new Comparator<Song>() {
                    @Override
                    public int compare(Song m1, Song m2) {
                        return m1.getArtist().compareToIgnoreCase(m2.getArtist());
                    }
                });
                adapter.notifyDataSetChanged();
                return true;
            case R.id.sort1:
                Collections.sort(songList, new Comparator<Song>() {
                    @Override
                    public int compare(Song m1, Song m2) {
                        return m1.getTitle().compareToIgnoreCase(m2.getTitle());
                    }
                });
                adapter.notifyDataSetChanged();
                return true;
        }
        return true;
    }



}
