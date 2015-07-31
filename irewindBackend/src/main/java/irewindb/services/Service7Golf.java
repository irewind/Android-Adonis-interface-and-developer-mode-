package irewindb.services;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import myClass.iUser;
import myClass.myBeacon;
import myClass.mySegment;
import utils.DataBase;
import utils.IrewindBackend;
import utils.request;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;


import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.RangeNotifier;
import com.radiusnetworks.ibeacon.Region;

public class Service7Golf extends Service implements IBeaconConsumer{
    DataBase db;
 //   MyApp app;
    Region region=new Region("myRangingUniqueId", null, null, null);
    boolean suports_beacon=false;
	ArrayList<myBeacon> beacons;
	ArrayList<Long> last_reques_times=new ArrayList<Long>();
	iUser user;
	String location_id,session_id;
	private IBeaconManager iBeaconManager; 
	private BluetoothAdapter mBluetoothAdapter;
	String SESSION_PATH;
	long  interval=10000;  //interval between requests
	long remain_active_interval=15000;
	
	CountDownTimer ct_no_beacon= new CountDownTimer(990000000,7000) {
		
		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			
		}
	};
	
	@Override
	public void onIBeaconServiceConnect() {
		System.out.println("<<<<<<<<<<<ibeacons connect");
		//app=((MyApp)getApplication());
		db=IrewindBackend.Instance.getDatabase();
		iBeaconManager.setRangeNotifier(new RangeNotifier() {
			
			@Override
			public void didRangeBeaconsInRegion(Collection<IBeacon> becs, Region region) {
				System.out.println("<<<<<<<<< didrangeBeacons");
				
				ArrayList<IBeacon> tbeacons=new ArrayList<IBeacon>(becs);
				for(int i=0;i<tbeacons.size();i++)
					System.out.print(tbeacons.get(i).getProximityUuid()+"."+tbeacons.get(i).getMinor()+" - ");
				System.out.println();
				for(int i=0;i<beacons.size();i++)
					System.out.print(beacons.get(i).beacon_uuid+"."+beacons.get(i).beacon_minor+" - ");
				System.out.println();	
				for(int i=0;i<beacons.size();i++)
				{int index=-1;
					for(int j=0;j<tbeacons.size();j++)
					if(tbeacons.get(j).getProximityUuid().equalsIgnoreCase(beacons.get(i).beacon_uuid)&&tbeacons.get(j).getMinor()==Integer.parseInt(beacons.get(i).beacon_minor)) index=j;
				
					if(index!=-1)
				{System.out.print("cam="+beacons.get(i).camera_id);
				System.out.println(" proximity="+tbeacons.get(index).getAccuracy());
						if(beacons.get(i).start_time==-1) {if(tbeacons.get(index).getAccuracy()<beacons.get(i).min_dist)  {beacons.get(i).start_time=System.currentTimeMillis(); beacons.get(i).last_seen=System.currentTimeMillis();}}
				else {if(tbeacons.get(index).getAccuracy()<beacons.get(i).max_dist)  beacons.get(i).last_seen=System.currentTimeMillis();} ;//toDO	
			}				
				
			
				if(beacons.get(i).start_time!=-1)
				{System.out.println(beacons.get(i).camera_id+" last seen="+(System.currentTimeMillis()-beacons.get(i).last_seen));
					if(System.currentTimeMillis()-beacons.get(i).last_seen>remain_active_interval) beacons.get(i).start_time=-1;
				else {
					System.out.println("last request ="+(System.currentTimeMillis()-last_reques_times.get(i)));
					if(System.currentTimeMillis()-last_reques_times.get(i)>interval)
				{last_reques_times.remove(i); last_reques_times.add(i,System.currentTimeMillis());
				System.out.println("will download cam_id="+beacons.get(i).camera_id);
				downloadSegments(request.getSegments(user, location_id, beacons.get(i).camera_id, beacons.get(i).start_time+"", beacons.get(i).last_seen+""));
				}}
				}
				
					
				}
				double lng=0,lat=0;
				for(int i=0;i<beacons.size();i++)
				if(beacons.get(i).start_time!=-1)
				{System.out.println("beacon "+i+" lat="+beacons.get(i).beacon_lat);
					lng=beacons.get(i).beacon_long;
				lat=beacons.get(i).beacon_lat;
				i=beacons.size();}
				//app.setBeacon_lat(lat);
				//app.setBeacon_long(lng);
			}
		});
		  try {
	            iBeaconManager.startRangingBeaconsInRegion(region);
	        } catch (RemoteException e) {  e.printStackTrace(); }
	}

	@Override
	 public int onStartCommand(Intent it, int a, int b)
	 { 
		
		suports_beacon= (android.os.Build.VERSION.SDK_INT >= 18
				&& Service7Golf.this
						.getPackageManager()
						.hasSystemFeature(
								PackageManager.FEATURE_BLUETOOTH_LE));
		
		try{if(it!=null){
			iBeaconManager = IBeaconManager.getInstanceForApplication(this);
	   location_id=it.getStringExtra("location_id");
		session_id=it.getStringExtra("session_id");
		user=it.getParcelableExtra("user1");
		System.out.println("Service7Golf started with session="+session_id);

		if(suports_beacon){
		 		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(!mBluetoothAdapter.isEnabled()) mBluetoothAdapter.enable();
		iBeaconManager.bind(this);
		}
		else ct_no_beacon.start();
new Thread(new Runnable() {
			
			@Override
			public void run() {
				beacons=request.getBeaconsList(user, location_id);
				for(int i=0;i<beacons.size();i++)
					last_reques_times.add(0l);
			}
		}).start();

		SESSION_PATH=Environment.getExternalStorageDirectory().getPath()+"/Irewind/irw_"+session_id+"/";
	    File f=new File(SESSION_PATH);
	    if(!f.exists()) f.mkdirs();
	   
		}}catch(Exception e){e.printStackTrace(); System.out.println("service7Golf failed");}
		return START_STICKY; }
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
		public void onDestroy() {
		        super.onDestroy();
		        try{iBeaconManager.stopMonitoringBeaconsInRegion(region);
		        iBeaconManager.unBind(this);}catch(Exception e){e.printStackTrace();}
		        stop=true;
	 }
	
	
	int index=-1;
	boolean stop=false;
	
	ArrayList<mySegment> segmentsToDl=new ArrayList<mySegment>();
	HashMap<String, Object> map=new HashMap<String, Object>();
	
	public void downloadSegments(ArrayList<mySegment> segments)
	{System.out.println("downloadSegments add "+segments.size());
		for(int i=0;i<segments.size();i++)
		if(!map.containsKey(segments.get(i).res_id)){map.put(segments.get(i).res_id, 0); segmentsToDl.add(segments.get(i));}
	
	if(index==-1&&segmentsToDl.size()>0)
	{new Thread(new Runnable() {
		
		@Override
		public void run() {
			index=0;
			while(!stop||segmentsToDl.size()-index>1)
			{System.out.println("download index ="+index+" of "+segmentsToDl.size());
			for(int i=0;i<segmentsToDl.size();i++)
			{System.out.println(i+". cam "+segmentsToDl.get(i).cam_id+": "+segmentsToDl.get(i).segm_start+" - "+segmentsToDl.get(i).segm_end);}
			
				if(index==1) db.addCustomSession("7", session_id+"","1", user.getId()+"", location_id, "multi_view");
				if(index<segmentsToDl.size())
				{downloadSegment(segmentsToDl.get(index));
				index++;} 
			else
				try {synchronized (this) {
					System.out.println("will wait");
					wait(500);
					System.out.println("waited");}} catch (Exception e) {e.printStackTrace();}	
				}
			}
	}).start();
		
		
		
		
	}
	
	}
	
	public void downloadSegment(mySegment s)
	{String path=SESSION_PATH+location_id+"-"+s.cam_id+"-low-"+s.segm_start+"-"+s.segm_end+".mp4";
		System.out.println("downloading segment "+s.res_id+" from cam "+s.cam_id+" path="+path);
	
	request.trustAllHosts();
	
	try{  
	URL url = new URL(request.adresa.replace("https", "http")+"/video-processor-secured/services/resource/"+s.res_id );
    request.trustAllHosts();
 
     URLConnection connection = url.openConnection();

     connection.connect();

    // int fileLength = connection.getContentLength();

     // download the file
     InputStream input = new BufferedInputStream(url.openStream());
   
     File film= new File(path);
     
     OutputStream output = new FileOutputStream(film);

     byte data[] = new byte[10240];
   //  long total = 0;
     int count;
   
     while (((count = input.read(data)) != -1)) {
     	
       //  total += count;
 
         
        
         output.write(data, 0, count);
     }

     output.flush();
     output.close();
     input.close();
	
	db.addCustomSegment("7", session_id+"", user.getId()+"", location_id, path, s.cam_id, s.segm_start, s.segm_end, 1);
	if(index==1)
	{
		
		String nume="thumb"+session_id+"_1.jpg"; 
		   
		File file = new File(Environment.getExternalStorageDirectory().toString()+ "/irewindb/thumbs/" +nume);
	if(!file.exists())	
	try{
	
		
		File dir=new File(Environment.getExternalStorageDirectory().toString()+ "/irewindb/thumbs");
		dir.mkdirs();
		
		MediaMetadataRetriever med=new MediaMetadataRetriever();
	
			med.setDataSource(path);
			
		ServiciuDownloadVideos.writeExternalToCache(Bitmap.createScaledBitmap(med.getFrameAtTime(5000000)  ,320,240,false),file);
			}catch(NoSuchMethodError e) {System.out.println("Service7Golf"+"ERROR CAUGHT:"+e.getMessage());}
		
		
		
	}
	}catch(Exception e){ e.printStackTrace();}
	
	}
}
