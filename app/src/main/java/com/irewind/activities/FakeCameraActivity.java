package com.irewind.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.irewind.R;

import java.util.ArrayList;

import myClass.iLocation;
import myClass.myCamera;
import utils.IrewindBackend;
import utils.request;

public class FakeCameraActivity extends ActionBarActivity {
    ArrayList<iLocation> a;
    String[] s;
    Spinner spinner;
    ArrayList<myCamera> cameras;
    ListView listView;
   String session_id = "null";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_camera);
         spinner = (Spinner) findViewById(R.id.spinner);
         listView =(ListView) findViewById(R.id.listView);
        new Thread(new Runnable() {
            @Override
            public void run() {
               a = request.getLocationsList(IrewindBackend.Instance.getUser().getUsername(), IrewindBackend.Instance.getUser().getAuth_service(), IrewindBackend.Instance.getUser().getPassword());
                s= new String[a.size()];
                for(int i = 0;i < a.size();i++) {
                    s[i]= a.get(i).getLocation_id()+"." +a.get(i).getLocation_name();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(FakeCameraActivity.this, android.R.layout.simple_spinner_item, s);
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(spinnerArrayAdapter);
                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                   populateCameras(a.get(position).getLocation_id());
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                    });
                }
            }
        }).start();




    }
String location_aux="",camera_aux="";
    public void populateCameras(final String location_id){
        location_aux=location_id;

       cameras = IrewindBackend.Instance.getDatabase().getCameras(location_id);
        String[] cs = new String[cameras.size()];
        for (int i = 0; i< cameras.size(); i++) {
            cs[i] = "cam "+cameras.get(i).camera_id;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, cs);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(cameras.get(position).camera_id + "");
                camera_aux = cameras.get(position).camera_id+"";
                new Thread(new Runnable() {
                    @Override
                    public void run() {


              session_id =  request.createPointNow(IrewindBackend.Instance.getUser().getUsername(), IrewindBackend.Instance.getUser().getAuth_service(),IrewindBackend.Instance.getUser().getPassword(),IrewindBackend.Instance.getUser().getId(),location_aux,camera_aux,session_id);
                    }
                }).start();
            }
        });

    }


}
