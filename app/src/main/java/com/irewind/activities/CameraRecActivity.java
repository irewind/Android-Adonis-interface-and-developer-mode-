package com.irewind.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.ActionBarActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.irewind.R;
import com.irewind.services.SegmentUploaderService;

import org.json.JSONObject;

import irewindb.services.ServiciuGpsLogging;
import myClass.iUser;
import utils.AvcEncoder;
import utils.IrewindBackend;
import utils.request;

public class CameraRecActivity extends ActionBarActivity {


    iUser user;
    TextView t;
    String text;
    AvcEncoder encoder;
    static Activity context;
    Camera camera;
    int fps, width, height;
    SurfaceHolder holder;
    boolean recording = false;
    PowerManager.WakeLock mWakeLock;
    static TextView txUp;
    static String aux_text;
    String location_id, cam_id;
    volatile byte[] data_aux = null;
    long startTS =-1;
    double dropFps;
    int frame_count = 0;
    EditText eText;

    public static void setUploaderText(String text) {
        aux_text = text;
        context.runOnUiThread(new Runnable() {
            public void run() {
                try {
                    txUp.setText(aux_text);
                } catch (Exception e) {
                }
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_rec);

        context = CameraRecActivity.this;
        user = IrewindBackend.Instance.getUser();
        SurfaceView cameraView = (SurfaceView) findViewById(R.id.surfaceView1);
        holder = cameraView.getHolder();
        text = "";
        t = (TextView) findViewById(R.id.textView1);
        txUp = (TextView) findViewById(R.id.textView2);
        Button bStartRecord = (Button) findViewById(R.id.button1);
        Button bStopRecord = (Button) findViewById(R.id.button2);
        Button bStartUpload = (Button) findViewById(R.id.button3);
        Button bStopUpload = (Button) findViewById(R.id.button4);
        Button bBindCamera = (Button) findViewById(R.id.button5);
        Button bColorRepair = (Button) findViewById(R.id.button6);
        eText = (EditText) findViewById(R.id.editText);

        bBindCamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        String location_id = eText.getEditableText().toString();
                        if (ServiciuGpsLogging.currentIrwLocation != null)
                            location_id = ServiciuGpsLogging.currentIrwLocation.getLocation_id();
                        request.bindCamera(user, context,location_id , ServiciuGpsLogging.last_lat, ServiciuGpsLogging.last_long);

                    }
                }).start();

            }
        });

        bStartRecord.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!recording) {
                    recording = true;
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            JSONObject j = request.getRecordingParams(user);
                            try {
                                if (j == null) {
                                    width = 720;
                                    height = 480;
                                    fps = 25;
                                    location_id = eText.getEditableText().toString();
                                    cam_id = "2";
                                    text = "Camera not registered! Turn GPS Recording On and BIND the camera."
                                            + cam_id
                                            + ", location "
                                            + location_id;
                                    encoder = new AvcEncoder(width, height,
                                            2000000, fps, 1, 10000, location_id,
                                            cam_id, "hi");
                                    System.out.println("bitrate = DEFAULT");
                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {t.setText(text);}});

                                } else {
                                    System.out.println("bitrate = "+ (j.getInt("bitrate") * 1000));

                                    text = "Camera id=" + j.getString("cam_id")
                                            + " location_id="
                                            + j.getString("location_id");
                                    width = j.getInt("segm_width");
                                    height = j.getInt("segm_height");
                                    camera = Camera
                                            .open();
                                    Camera.Parameters mParameters = camera
                                            .getParameters();
                                    int best_index = 0, delta=20000;
                                    for(int i = 0; i < mParameters.getSupportedPreviewSizes().size(); i++)
                                        if (Math.abs(mParameters.getSupportedPreviewSizes().get(i).width - width)<delta) {
                                            delta = Math.abs(mParameters.getSupportedPreviewSizes().get(i).width - width);
                                            best_index = i;
                                        }
                                    System.out.println("Actual withxheigh= " + mParameters.getSupportedPreviewSizes().get(best_index).width+"x"+mParameters.getSupportedPreviewSizes().get(best_index).height);
                                    encoder = new AvcEncoder(mParameters.getSupportedPreviewSizes().get(best_index).width, mParameters.getSupportedPreviewSizes().get(best_index).height, j
                                            .getInt("bitrate") * 1000, j
                                            .getInt("segm_fps"), j.getInt("keyframe_interval")/ j
                                            .getInt("segm_fps")
                                            , j
                                            .getInt("segm_duration"), j
                                            .getString("cam_id"), j
                                            .getString("location_id"), j
                                            .getString("profile_name"));

                                    fps = j.getInt("segm_fps");
                                    location_id = j.getString("location_id");
                                    cam_id = j.getString("cam_id");



                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            t.setText(text);
                                            try {

                                                final PowerManager pm = (PowerManager) getSystemService(
                                                        Context.POWER_SERVICE);
                                                mWakeLock = pm
                                                        .newWakeLock(
                                                                PowerManager.SCREEN_DIM_WAKE_LOCK,
                                                                "My Tag");
                                                mWakeLock.acquire();
                                                Camera.Parameters mParameters = camera
                                                        .getParameters();
                                                int best_index = 0, delta=20000;
                                                for(int i = 0; i < mParameters.getSupportedPreviewSizes().size(); i++)
                                                    if (Math.abs(mParameters.getSupportedPreviewSizes().get(i).width - width)<delta) {
                                                        delta = Math.abs(mParameters.getSupportedPreviewSizes().get(i).width - width);
                                                        best_index = i;
                                                    }


                                                String print="";
                                                for(int i = 0; i < mParameters.getSupportedPreviewSizes().size(); i++){
                                                    if (i == best_index)
                                                        print+="*";
                                                    print+=mParameters.getSupportedPreviewSizes().get(i).width+"x"+mParameters.getSupportedPreviewSizes().get(i).height+"|";
                                                }
                                                Toast.makeText(CameraRecActivity.this,print,Toast.LENGTH_LONG).show();


                                                mParameters.setPreviewSize(mParameters.getSupportedPreviewSizes().get(best_index).width,
                                                        mParameters.getSupportedPreviewSizes().get(best_index).height);

                                                best_index = 0;
                                                delta = 200000;

                                                for(int i = 0; i < mParameters.getSupportedPreviewFpsRange().size(); i++)
                                                    if (Math.abs(mParameters.getSupportedPreviewFpsRange().get(i)[1] - fps * 1000)<delta) {
                                                        delta = Math.abs(mParameters.getSupportedPreviewFpsRange().get(i)[1] - fps * 1000);
                                                        best_index = i;
                                                    }


                                                mParameters.setPreviewFpsRange(
                                                        mParameters.getSupportedPreviewFpsRange().get(best_index)[0], mParameters.getSupportedPreviewFpsRange().get(best_index)[1]);
                                                camera.setParameters(mParameters);
                                                camera.setPreviewDisplay(holder);
                                                camera.startPreview();

                                                new Thread(new Runnable() {

                                                    @Override
                                                    public void run() {

                                                        while (recording) {
                                                            if (data_aux != null) {

                                                               byte[] data = data_aux;
                                                                data_aux = null;
                                                                encoder.offerEncoder(data);
                                                            }

                                                        }

                                                    }
                                                }).start();


                                                camera.setPreviewCallback(new Camera.PreviewCallback() {

                                                    @Override
                                                    public void onPreviewFrame(
                                                            byte[] data,
                                                            Camera camera) {
                                                       /* if (startTS == -1)  startTS=System.currentTimeMillis();
                                                       else if ( frame_count > 250 ) {
                                                            double cfps = (frame_count*1000/((System.currentTimeMillis() -startTS)));
                                                            System.out.println("Fps = " + cfps);
                                                            startTS=System.currentTimeMillis(); frame_count = 0;
                                                        dropFps = (double)fps/(cfps - (double)fps);}
                                                        else {frame_count++;
                                                        }

                                                        if (dropFps>0 && Math.abs(frame_count - ((double)((int)(frame_count/dropFps)))*dropFps)<1)
                                                            System.out.println("drop");
                                                        else*/
                                                       // frame_count++;
                                                       // if (frame_count == 12) frame_count = 0;
                                                       // else
                                                        data_aux = data;
                                                    }
                                                });

                                                Intent intn = new Intent(
                                                        CameraRecActivity.this,
                                                        SegmentUploaderService.class);
                                                intn.putExtra("location_id",
                                                        location_id);
                                                intn.putExtra("cam_id", cam_id);
                                                startService(intn);

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });
                                }} catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                }
            }
        });

        bStopRecord.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                stopRecording();
            }
        });

        bStartUpload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            JSONObject j = request.getRecordingParams(user);
                            location_id = j.getString("location_id");
                            cam_id = j.getString("cam_id");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            public void run() {
                                System.out.println("start Service");
                                Intent intn = new Intent(CameraRecActivity.this,
                                        SegmentUploaderService.class);
                                intn.putExtra("location_id", location_id);
                                intn.putExtra("cam_id", cam_id);
                                startService(intn);

                            }
                        });

                    }
                }).start();
            }
        });

        bStopUpload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                stopService(
                        new Intent(CameraRecActivity.this,
                                SegmentUploaderService.class));
            }
        });

        bColorRepair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AvcEncoder.repairColor) ((Button)v).setText("Start Color Repair");
                else ((Button)v).setText("Stop Color Repair");
                AvcEncoder.repairColor = !AvcEncoder.repairColor;
            }

        });


    }

    public void stopRecording() {
        if (recording) {
            try {
                camera.stopPreview();
                // camera.unlock();
                camera.setPreviewCallback(null);
                camera.release();
                camera = null;
                encoder.close();
                recording = false;
                mWakeLock.release();
                stopService(
                        new Intent(CameraRecActivity.this,
                                SegmentUploaderService.class));
                t.setText("Recording Off");
            } catch (Exception e) {
            }
        }


    }
    @Override
    public void onPause() {
        stopRecording();
        super.onPause();
    }

}
