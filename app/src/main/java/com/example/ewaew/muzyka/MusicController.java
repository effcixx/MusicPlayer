package com.example.ewaew.muzyka;

import android.content.Context;
import android.widget.MediaController;

/**
 * Created by Ewa Lyko on 11.05.2018.
 */

public class MusicController extends MediaController {
    Context c;
    public MusicController(Context context) {
        super(context);
        c=context;
    }

    @Override
    public void hide(){}

}