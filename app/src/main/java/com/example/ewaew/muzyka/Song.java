package com.example.ewaew.muzyka;

import android.net.Uri;

/**
 * Created by Ewa Lyko on 11.05.2018.
 */

public class Song {
    private long id;
    private String title;
    private String artist;
    private int image;

    public Song(long songID, String songTitle, String songArtist, int image) {
        id=songID;
        title=songTitle;
        artist=songArtist;
        this.image=image;
    }

    public long getID(){return id;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}
    public int getImage(){return image;}
}