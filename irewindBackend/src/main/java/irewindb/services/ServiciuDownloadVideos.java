package irewindb.services;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import myClass.iLocation;
import myClass.mySegment;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import utils.DataBase;
import utils.IrewindBackend;
import utils.request;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;

public class ServiciuDownloadVideos extends Service {


	int user_id,session_id;
	String Start_time=        "0";    // 0 sau 1377362620034
	String end_time_hardcodat="-1";  // -1 sau 1377362846034
	String user,auth_service, password;
	long lastRequest=0;
	String location_id, location_url;
	int counter=0;
	ArrayList<mySegment> segments;
	
	
	int stareGPS=3;
	long delta=0;
	boolean Recording=true;
	
	int MAXIMUM_DISTANCE=300;
	
	
	 
	//public String fromJni(String s)
	//{System.out.println("<<< CALL IN JAVA <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
	//return "aaaa";}
	
	public void updateGPS(int stare)
	{/*if(stare!=stareGPS) {
	MyApp appState = ((MyApp)getApplicationContext());
	if(appState!=null && appState.getMainActivityRefference()!=null)
	{((MainActivity)appState.getMainActivityRefference()).refreshGpsState(stare);
	
	stareGPS=stare;}}*/}
	
	public void updateGPS(String mesaj)
	{/*System.out.println("update gps cu mesaj<<<<<<<<< "+mesaj);
		MyApp appState = ((MyApp)getApplicationContext());
	if(appState!=null && appState.getMainActivityRefference()!=null)
	{((MainActivity)appState.getMainActivityRefference()).refreshGpsMessage(mesaj);
	}*/}
	
	int point_id=1;
	long primul=0;
	
	
	
	CountDownTimer countDown= new CountDownTimer(999999999,10000) {
		
		@Override
		public void onTick(long millisUntilFinished) {
			if(delta!=0&&Recording)
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
				
					
					request.uploadGPS(user,auth_service, password, user_id, ServiciuDownloadVideos.this,location_url);
					System.out.println("done uploading logs");
					
			System.out.println("request started <<<<<<<");
			
			if(end_time_hardcodat.equalsIgnoreCase("-1"))segments=getSegmentList(location_url,user_id+"",user,auth_service,password,session_id+"",location_id,Start_time+"",(System.currentTimeMillis()+delta)+"");
			else segments=getSegmentList(location_url,user_id+"",user,auth_service,password,session_id+"",location_id,Start_time+"",end_time_hardcodat);
		
			if(end_time_hardcodat.equalsIgnoreCase("-1"))
			System.out.println("stop time="+(System.currentTimeMillis()+delta));
			else System.out.println("stop time="+end_time_hardcodat);
			System.out.println("delta="+delta);
			System.out.println("Segments size="+segments.size());
			downloadSegments(segments);
				}
			}).start();
			
		}
		
		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			
		}
	};
	
	
	

	
	IBinder mBinder = new LocalBinder();
	@Override
	public IBinder onBind(Intent it) {
	
		System.out.println("ServiciuDownloadVideos onBind");
		
		
		
		
		 if(it!=null){
			 user_id=it.getIntExtra("user_id", 0);
		 session_id=it.getIntExtra("session_id",0);
		 
			user=it.getStringExtra("user");
			auth_service=it.getStringExtra("auth_service");
			password=it.getStringExtra("password");
			
			location_id=it.getStringExtra("location_id");
			location_url=it.getStringExtra("location_url");
		
			
		Start_time=System.currentTimeMillis()+"";
			
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
				if(isOnline())	 delta=request.TimeError(user,auth_service, password);
						countDown.start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				}
			}).start();
			
			
			
			
			
	//	appState	 = ((MyApp)getApplicationContext());
			 
			
			 
			
				
			 }
		
		
		System.out.println("<<<<<<<will return mBinder");
		System.out.println("mbinder="+mBinder);
		return mBinder; 
	}
	
	
	 public class LocalBinder extends Binder {
	        public ServiciuDownloadVideos getServerInstance() {
	            return ServiciuDownloadVideos.this;
	        }
	    }
	 
	 public void StopRecording()
	 {Recording=false;
	 System.out.println("stop recording called, downloading="+downloading);
	 if (downloading>0)	  updateGPS(4);
	 else {updateGPS(0); updateGPS(""); stopSelf();}}
	 
	
//	MyApp appState;
	
	
	  public void onCreate() {
	        // Called when your service is first created.
		 super.onCreate();
		 System.out.println("ServiciuDownloadVideos onCreate()");
		
	}
	
	
	
	@Override
	 public int onStartCommand(Intent it, int a, int b)
	 {System.out.println("ServiciuDownloadVideos started");
	 
	 
	 
	 
	 
      
		
		
		 return START_STICKY;}

	
	public  static ArrayList<mySegment> getSegmentList(String local_server,String uid, String user,String auth_service, String password, String session_id, String location_id,String timestamp_start, String timestamp_stop)
	{	ArrayList<mySegment> segs=new ArrayList<mySegment>();
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	   	nameValuePairs.add(new BasicNameValuePair("auth_user", user));
	   	nameValuePairs.add(new BasicNameValuePair("auth_service", auth_service));
	   	nameValuePairs.add(new BasicNameValuePair("user_id", uid));
	   	nameValuePairs.add(new BasicNameValuePair("location_id", location_id));
	 	nameValuePairs.add(new BasicNameValuePair("timestamp_start", timestamp_start));
	 	nameValuePairs.add(new BasicNameValuePair("timestamp_stop", timestamp_stop));
	   
	 	
	 //	((MainActivity)appState.getContext()).PublishDownloadProgress("uid="+uid+" url="+local_server+"services/utils/cameraSegmentsList"+" start="+timestamp_start+" stop="+timestamp_stop);
	 	
		try {
			System.out.println("host="+local_server);
			String str=request.PostRequest(nameValuePairs, password, "/video-processor-secured/services/utils/cameraSegmentsList",local_server);
	   System.out.println(str);
	   
	  
	   
			JSONObject tmp=new JSONObject(str);
			JSONArray ja=tmp.getJSONArray("Segments");
			int[] aux_seg={1,1,1,1,1,1,1,1,1,1,1,1};
			for(int i=0;i<ja.length();i++)
			if(ja.getJSONObject(i).getInt("segm_height")<720)
			{mySegment s=new mySegment();
			s.res_id=ja.getJSONObject(i).getString("res_id");
			
			int cam=ja.getJSONObject(i).getInt("cam_id");
			s.cam_id=cam+"";
			s.segm_start=ja.getJSONObject(i).getLong("segm_start")+"";
			s.segm_end=ja.getJSONObject(i).getLong("segm_end")+"";
			s.location_id=ja.getJSONObject(i).getLong("location_id")+"";
		
			s.name="cam"+cam+"_seg"+(aux_seg[cam]++)+".mp4";
			s.path=Environment.getExternalStorageDirectory().getPath()+"/Irewind/irwTenis"+session_id+"/"+s.name;
			segs.add(s);
			
			}
			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();}
	
		Collections.sort(segs, new Comparator<mySegment>() {

			@Override
			public int compare(mySegment lhs, mySegment rhs) {
				// TODO Auto-generated method stub
				return lhs.segm_start.compareTo(rhs.segm_start);
			}
		});
		
		for(int i=0;i<segs.size();i++)
			System.out.println(segs.get(i).segm_start);
		
		
		return segs;
}
	





	public boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}

	
	int procent=-18;
	int downloading=0;
	int numar_fire=2;
	int real_index=0;
	ArrayList<mySegment> segs=new ArrayList<mySegment>();
	//int index=0;
	 
	
	
	
	@SuppressLint("NewApi")
	public void DownloadSegmentsFunction(){
			if(downloading<numar_fire)
			{downloading++;
			File folder = new File(Environment.getExternalStorageDirectory().toString()+"/Irewind/irwTenis"+session_id);
			
			  folder.mkdirs();
		for(int index=0;index<segs.size();index++)
		{System.out.println("dl index= "+index);
			if(segs.get(index).state==0)
			{segs.get(index).state=1;
			procent=-18;
		try {
		    request.trustAllHosts();
			
		  
			URL url = new URL(location_url+"/video-processor-secured/services/resource/"+segs.get(index).res_id );
		    request.trustAllHosts();
		
	         URLConnection connection = url.openConnection();
	  
	         connection.connect();

	         int fileLength = connection.getContentLength();

	         // download the file
	         InputStream input = new BufferedInputStream(url.openStream());
	       
	         File film= new File(folder, segs.get(index).name);
	         System.out.println(segs.get(index).name);
	         OutputStream output = new FileOutputStream(film);

	         byte data[] = new byte[10240];
	         long total = 0;
	         int count;
	       
	         while (((count = input.read(data)) != -1)) {
	         	
	             total += count;
	     
	             
	            
	             output.write(data, 0, count);
	         }

	         output.flush();
	         output.close();
	         input.close();
		
		
		
		
		
	         DataBase db=IrewindBackend.Instance.getDatabase();
	     	db.addCustomSegment("Tenis", session_id+"", user_id+"", segs.get(index).location_id, segs.get(index).path, segs.get(index).cam_id, segs.get(index).segm_start, segs.get(index).segm_end, index);
	     	if(index==0) {db.addCustomSession("Tenis", session_id+"","1", user_id+"", location_id, "multi_view");
	     	String nume="thumb"+session_id+"_1.jpg"; 
			   
			File file = new File(Environment.getExternalStorageDirectory().toString()+ "/irewindb/thumbs/" +nume);
			
			
			File dir=new File(Environment.getExternalStorageDirectory().toString()+ "/irewindb/thumbs");
     		dir.mkdirs();
     		try{
     		MediaMetadataRetriever med=new MediaMetadataRetriever();
     	
				med.setDataSource(segs.get(index).path);
				
			writeExternalToCache(Bitmap.createScaledBitmap(med.getFrameAtTime(5000000)  ,60,60,false),file);
				}catch(NoSuchMethodError e) {}
			
	     	
	     	}
	     	segs.get(index).state=2;
	      
	     	
	     
	     	updateGPS((++real_index)+"/"+segs.size());
	     	 db.updateField(session_id+"", "1", "download_progress", ((real_index*100)/segs.size())+"");
	     	
	     	
//	    MyApp appState = ((MyApp)getApplicationContext());	
//	try{     appState.getMoviesFragmentRefference().refresh(); }catch(NullPointerException e){}
//		 catch(IllegalAccessError e){}
	    
	
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			segs.get(index).state=0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			segs.get(index).state=0;
		}
		
			}
		
		
		}// end of for(;;)
		
			
		downloading--;	
			}
			
		}
	
	
	public void downloadSegments(ArrayList<mySegment> _segs)
	{ System.out.println("download segments called with "+_segs.size()+" segments");
		if(_segs.size()>segs.size())
	{System.out.println("_segs.size()>segs.size()");
		segs=_segs;
		
		for(int i=downloading;i<=numar_fire;i++)
		{new Thread(new Runnable() {public void run() {
			
				DownloadSegmentsFunction();
				if(!Recording&&downloading==0) {updateGPS(0); updateGPS("");stopSelf();	}
				
				}}).start();}
		
    	}
		
	}
	
	
	
	
	
	public int getClosestLocation(ArrayList<iLocation> locs,double lat,double lng)   //euclidian dist
	{double min_dist=measure(locs.get(0).lat, locs.get(0).lng, lat, lng);
	int best_index=0;
		
		
	for(int i=0;i<locs.size();i++)
	{double dist=measure(locs.get(i).lat, locs.get(i).lng, lat, lng);
	//Math.sqrt((locs.get(i).lat-lat)*(locs.get(i).lat-lat)+(locs.get(i).lng-lng)*(locs.get(i).lng-lng));
	if(dist<min_dist)
	{min_dist=dist;
	best_index=i;}
	}
	System.out.println("SDV - best index="+best_index);
	System.out.println("SDV - min_dist="+min_dist);
		if(min_dist<MAXIMUM_DISTANCE)
		
		return best_index;
		
		else return -1;
	}
	
	
	
	
	
	
	double measure(double lat1,double lon1,double lat2,double lon2){  
		double R = 6378.137; // Radius of earth in KM
		double dLat = (lat2 - lat1) * Math.PI / 180;
		double dLon = (lon2 - lon1) * Math.PI / 180;
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	    Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
	    Math.sin(dLon/2) * Math.sin(dLon/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double d = R * c;
	    return d * 1000; // meters
	}
	
	
	public void onDestroy() {
	    super.onDestroy();
	    System.out.println("download service destroy");
	    try {
			
		
	 
	    
	    } catch (NullPointerException e) {
			// TODO: handle exception
		}
	    updateGPS(0);
	   }
	
	 public static final int BUFFER_SIZE = 1024 * 8;
	 public static void writeExternalToCache(Bitmap bitmap, File file) {
	    try {
	        file.createNewFile();
	        FileOutputStream fos = new FileOutputStream(file);
	        final BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER_SIZE);
	        bitmap.compress(CompressFormat.JPEG, 100, bos);
	        bos.flush();
	        bos.close();
	        fos.close();
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {

	    }

	}
	 
	
	
}
