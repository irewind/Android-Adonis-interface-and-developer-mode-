package com.irewind.services;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.irewind.activities.CameraRecActivity;

import java.io.File;

import myClass.iUser;
import utils.request;

public class SegmentUploaderService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    boolean finish=false;

    iUser user;
    String location_id,cam_id,server_id="1",recording_profile_id="1";

    @Override
    public int onStartCommand(Intent it, int a, int b)
    { user=it.getParcelableExtra("user");
        cam_id=it.getStringExtra("cam_id");
        location_id=it.getStringExtra("location_id");

        Log.d("SegmentUploader", "onStartCommand");
        new Thread(new Runnable() {
            public void run() {
                try{	File dir=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/irewindb/CameraSegments");
                    dir.mkdirs();

                    while(!finish)
                    {Log.d("SegmentUploader", "while new cycle");
                        File[] files=dir.listFiles();
                        CameraRecActivity.setUploaderText("Uploadig; "+files.length+" segm left");
                        if(files.length>0)
                        {try{File f=files[0];
                            int i=0;
                            while(f.getName().split("-").length<5)
                            { f=files[i++];}

                            Log.d("SegmentUploader", "uploading "+f.getName());
                            if(request.UploadSegment(f, location_id, cam_id, server_id, recording_profile_id, f.getName().split("-")[3], f.getName().split("-")[4].replace(".h264", "").replace(".mp4", "")))
                                f.delete();
                        }
                        catch(Exception e){
                            //	Log.d("SegmentUploader", "Error: "+e.getMessage());
                            //		e.printStackTrace();synchronized (this)
                            {
                                try{synchronized (this) {	wait(5000);}}catch(Exception e2){e2.printStackTrace();}
                            }}
                        }
                        else
                        {synchronized (this) {
                            try{	wait(5000);}catch(Exception e){e.printStackTrace();}
                        }}

                    }


                }catch(Exception e){e.printStackTrace();}}}).start();
        return START_STICKY; }




    @Override
    public void onDestroy() {
        CameraRecActivity.setUploaderText("Uploader Off");

        finish=true;
        super.onDestroy();



    }
}
