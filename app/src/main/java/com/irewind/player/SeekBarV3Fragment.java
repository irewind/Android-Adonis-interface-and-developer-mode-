package com.irewind.player;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.irewind.R;
import com.irewind.activities.IRFullScreenMovieActivity;
import com.irewind.sdk.model.Video;

public class SeekBarV3Fragment extends Fragment {

    LinearLayout seek_layout;
    LinearLayout.LayoutParams seekPar;
    ImageView seek, cursor;
    TextView tCurent, tTotal;
    onSeekBarEvent listener;
    ImageButton fullScreen;
    double total_time;

    private Video video;

    public void setOnSeekBarEventListener(onSeekBarEvent listener) {
        this.listener = listener;
    }


    public void setTotalTime(double total) {
        total_time = total;
        String aux = "";
        if ((int) (total / 1000) % 60 < 10)
            aux = "0";
        tTotal.setText((int) (total / 60000)
                + ":"
                + aux
                + (int) (total / 1000) % 60);
    }

    public void setCurentTime(double curent) {
        seekPar.width = (int) (curent / total_time * ((double) seek_layout.getWidth() - cursor.getWidth()));
        seek.setLayoutParams(seekPar);
        String aux = "";

        if ((int) (curent / 1000) % 60 < 10)
            aux = "0";
        tCurent.setText((int) (curent / 60000)
                + ":"
                + aux
                + (int) (curent / 1000) % 60);
    }

    public void setTime(double curent, double total) {
        seekPar.width = (int) (curent / total * ((double) seek_layout.getWidth() - cursor.getWidth()));
        seek.setLayoutParams(seekPar);
        String aux = "";

        if ((int) (curent / 1000) % 60 < 10)
            aux = "0";
        tCurent.setText((int) (curent / 60000)
                + ":"
                + aux
                + (int) (curent / 1000) % 60);

        aux = "";
        if ((int) (total / 1000) % 60 < 10)
            aux = "0";
        tTotal.setText((int) (total / 60000)
                + ":"
                + aux
                + (int) (total / 1000) % 60);


    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.seek_bar_third, container, false);

        tCurent = (TextView) v.findViewById(R.id.textViewCurent);
        tTotal = (TextView) v.findViewById(R.id.textViewTotal);
        seek_layout = (LinearLayout) v.findViewById(R.id.layout_seek_bar);
        seek = (ImageView) v.findViewById(R.id.seek);
        cursor = (ImageView) v.findViewById(R.id.imageViewCursor);
        seekPar = (LinearLayout.LayoutParams) seek.getLayoutParams();
        fullScreen = (ImageButton) v.findViewById(R.id.fullScreen);

        fullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), IRFullScreenMovieActivity.class);
                intent.putExtra("video", video.getMp4HighResolutionURL());
                startActivity(intent);
            }
        });

        seek_layout.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {


                seekPar.width = Math.min((int) event.getX() - cursor.getWidth() / 2, seek_layout.getWidth() - cursor.getWidth());
                seek.setLayoutParams(seekPar);


                listener.seek((double) seekPar.width / ((double) seek_layout.getWidth() - cursor.getWidth()));

                return true;
            }
        });

        return v;
    }


    public interface onSeekBarEvent {
        public void seek(double x);

    }

    public void setVideo(Video video){
        this.video = video;
    }
}
