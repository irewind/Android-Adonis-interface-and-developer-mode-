package irewindb.services;


import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;

public class DirectVideoDownloadService extends Service implements LocationListener{

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
/*	public static boolean DEVELOPMENT=false;
	public static boolean JOHN_DEMETER_VERSION=false;
	static { 
    	System.loadLibrary("ffmpeg");
    	System.loadLibrary("ffmpeg-test-jni");  
    } 
	public static native void DownloadSegment(String camera_url, String save_path, String frame_length, String miliseconds_length, String protocol);
	int MAXIMUM_DISTANCE=300;

	public double SEGMENT_LENGTH= 30000;
	public double fps=25;
	public String SESSION_PATH;
	
	
	String SPORT="FlyBoard";
	int location_index=-1;
	int stareGPS=0;
	String user, password;
	int user_id,session_id;
	String location_id;
	MyApp appState;
	LocationManager mlocManager;
	ArrayList<myLocation> locations;
	ArrayList<myCamera> Cameras;
	
	boolean firstTime=true;
	
CountDownTimer ct= new CountDownTimer(36000000,300) {
		
		@Override
		public void onTick(long arg0) {
			for(int i=0;i<Cameras.size();i++)
			{if(Cameras.get(i).last_start+SEGMENT_LENGTH<System.currentTimeMillis())
			{
			Cameras.get(i).last_start=System.currentTimeMillis();
			
			int curent_segment=++Cameras.get(i).curent_segment_index;
			
			Rec r=new Rec(Cameras.get(i).url,i,curent_segment , SESSION_PATH+"camera"+i+"_segment"+curent_segment+".mp4", SEGMENT_LENGTH/1000*fps,SEGMENT_LENGTH ,user_id+"",session_id+"",location_id,appState, firstTime);
			
	       r.run();
	       firstTime=false;
	       i=Cameras.size();  //so it will not start multiple recordings in the exact same time (it tends to fail when this happens)
	       
			}			
			
			
			}
			
			
			
		}
		
		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			
		}
	};
	

	public boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}
	
	public void updateGPS(int stare)
	{if(stare!=stareGPS) {
	MyApp appState = ((MyApp)getApplicationContext());
	if(appState!=null && appState.getMainActivityRefference()!=null)
	{((MainActivity)appState.getMainActivityRefference()).refreshGpsState(stare);
	
	stareGPS=stare;}}}
	
	
	
	
	
	@Override
	 public int onStartCommand(Intent it, int a, int b)
	 {
     
		 if(it!=null){
		 user_id=it.getIntExtra("user_id", 0);
	 session_id=it.getIntExtra("session_id",0);
	 System.out.println("session_id ="+session_id);
		user=it.getStringExtra("user");
		password=it.getStringExtra("password");
		location_index=it.getIntExtra("location_index", -1);
		System.out.println("DirectVideoDownloadService started with location_index="+location_index);
		 SESSION_PATH=Environment.getExternalStorageDirectory().getPath()+"/Irewind/irw_"+session_id+"/";
		    File f=new File(SESSION_PATH);
		    if(!f.exists()) f.mkdirs();
		
		
	appState	 = ((MyApp)getApplicationContext());
	
if(location_index>-1)
	
	{ updateGPS(3);
	new Thread(new Runnable() {
		
		@Override
		public void run() {
			try {
		if(isOnline()) locations=getLocationsList();	
				
	DataBase db=appState.getDataBase();            //only for testing
		functieDeTest(db);                             //only for testing
	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		}
	}).start();}
	
	
else{
		if(DEVELOPMENT) 
		 updateGPS(3);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
				if(isOnline()) locations=getLocationsList();	
				System.out.println("mesaj<<<<<<<<<<<<<<<");
				
				
			if(DEVELOPMENT){	
			DataBase db=appState.getDataBase();            //only for testing
				functieDeTest(db);                             //only for testing
			}
				
				
				
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				}
			}).start();
		if(!DEVELOPMENT){ 
		 mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		   if( mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) updateGPS(2);
		   else updateGPS(1);
		   mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, DirectVideoDownloadService.this);	
			 mlocManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 0, 0, DirectVideoDownloadService.this);	
		}}
		 }
		
		 return START_STICKY;}
	
	
int aux_index=0;
	@Override
	public void onLocationChanged(Location l) {
System.out.println(l.getLatitude() +"  "+l.getLongitude()+" acc:"+l.getAccuracy()+"Provider:"+l.getProvider());
		
		if(l.getAccuracy()<=ServiciuGpsLogging.MINIMUM_ACCURACY&&locations!=null&&locations.size()>0){
		
			mlocManager.removeUpdates(this);
		
		
		int index=getClosestLocation(locations, l.getLatitude(), l.getLongitude());
		System.out.println("index="+index);
		if(index!=-1){DataBase db=appState.getDataBase();
		System.out.println("added session");
	//	db.addCustomSession(SPORT, session_id+"","1", user_id+"", locations.get(index).id, "multi_view");
		updateGPS(3);
		location_id=locations.get(index).id;
		//String url=locations.get(index).web_adress;
		aux_index=index;
		new Thread(new Runnable() {
			
			
			@Override
			public void run() {
			Cameras=getCamerasList(locations.get(aux_index).id);
				ct.start();
			} 
		}).start();
		
		
		
		 
}
		else {updateGPS(22);
		
		 mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 2000, 1, DirectVideoDownloadService.this);	
		 mlocManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 5000, 2, DirectVideoDownloadService.this);	
		
		}	
		
		  
		}
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		if(provider.equalsIgnoreCase("gps")) updateGPS(1);
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		if(provider.equalsIgnoreCase("gps")&&stareGPS<=1) updateGPS(2);
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	public ArrayList<myCamera> getCamerasList(String location_id)
	{ArrayList<myCamera> cameras=new ArrayList<myCamera>();
	
	
	
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
nameValuePairs.add(new BasicNameValuePair("auth_user", user));
nameValuePairs.add(new BasicNameValuePair("first_index", "0"));
//nameValuePairs.add(new BasicNameValuePair("count", ""));
nameValuePairs.add(new BasicNameValuePair("sqlstatement", "SELECT cam_model_id,cam_ip FROM irw_cameras WHERE location_id="+location_id));
try {
String s=request.PostRequest(nameValuePairs, password, "/video-processor-secured/services/utils/sql");

JSONObject j= new JSONObject(s);
String r= j.getString("resultset");
System.out.println("result set: "+r);
JSONArray ja=new JSONArray(r);

for(int i=0;i<ja.length();i++)
{ myCamera c;
	if(ja.getJSONObject(i).getString("cam_model_id").equalsIgnoreCase("1"))c=new myCamera("rtsp://"+ja.getJSONObject(i).getString("cam_ip")+"/axis-media/media.amp?resolution=1280x720&videokeyframeinterval=25&videocodec=h264&fps="+(int)fps+"&compression=25&clock=0&date=0");
	else if(ja.getJSONObject(i).getString("cam_model_id").equalsIgnoreCase("2"))c=new myCamera(ja.getJSONObject(i).getString("cam_ip"));
	else c=null;
cameras.add(c);
}

} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
	
	
	return cameras;}
	
	
	public ArrayList<myLocation> getLocationsList()
	{
		ArrayList<myLocation> locations=new ArrayList<myLocation>();
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	nameValuePairs.add(new BasicNameValuePair("auth_user", user));
	nameValuePairs.add(new BasicNameValuePair("first_index", "0"));
	//nameValuePairs.add(new BasicNameValuePair("count", ""));
	nameValuePairs.add(new BasicNameValuePair("sqlstatement", "SELECT location_lat,location_long,name,web_url,id FROM irw_locations"));
	try {
	String s=request.PostRequest(nameValuePairs, password, "/video-processor-secured/services/utils/sql");
	JSONObject j= new JSONObject(s);
	String r= j.getString("resultset");
	System.out.println("result set: "+r);
	JSONArray ja=new JSONArray(r);
	
	for(int i=0;i<ja.length();i++)
	{myLocation l=new myLocation();
	l.lat=Double.parseDouble(ja.getJSONObject(i).getString("location_lat"));
	l.lng=Double.parseDouble(ja.getJSONObject(i).getString("location_long"));
	l.name=	ja.getJSONObject(i).getString("name");
	l.web_adress=ja.getJSONObject(i).getString("web_url");
	l.id=ja.getJSONObject(i).getString("id");
	locations.add(l);
	}

	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
		
		
	return locations;
	
		
	}	
	
	
	
	
	
	
	
	public int getClosestLocation(ArrayList<myLocation> locs,double lat,double lng)   //euclidian dist
	{double min_dist=measure(locs.get(0).lat, locs.get(0).lng, lat, lng);
	int best_index=0;
		
		
	for(int i=0;i<locs.size();i++)
	{double dist=measure(locs.get(i).lat, locs.get(i).lng, lat, lng);
	//Math.sqrt((locs.get(i).lat-lat)*(locs.get(i).lat-lat)+(locs.get(i).lng-lng)*(locs.get(i).lng-lng));
	if(dist<min_dist)
	{min_dist=dist;
	best_index=i;}
	}
	System.out.println("DVDS min_dist="+min_dist);
		if(min_dist<MAXIMUM_DISTANCE)
		
		return best_index;
		
		else return -1;
	}
	
	
	
	
	class myLocation
	{String name,web_adress,id;
	double lat,lng;}
	
	
	
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
	    ct.cancel();
	    System.out.println("download service destroy");
	    if(!DEVELOPMENT)
	   try {
		
	mlocManager.removeUpdates(this);
	   } catch (NullPointerException e) {
			// TODO: handle exception
		} 
	
	    updateGPS(0);
	    ct.cancel();
	   }
	
	public void functieDeTest(DataBase db )                         //testing only
	{
		if(!JOHN_DEMETER_VERSION)
	{ int index=3;
			if (!DEVELOPMENT) index=location_index;
		
	location_id=locations.get(index).id;
	String url=locations.get(index).web_adress;
	aux_index=index;}
	new Thread(new Runnable() {
		
		@Override
		public void run() {
		
	if(JOHN_DEMETER_VERSION){
			Cameras=new ArrayList<myCamera>();
		
		myCamera c1=new myCamera("rtsp://192.168.0.51/axis-media/media.amp?resolution=1280x720&videokeyframeinterval=25&videocodec=h264&fps="+fps+"&compression=25&clock=0&date=0");
		myCamera c2=new myCamera("rtsp://192.168.0.52/axis-media/media.amp?resolution=1280x720&videokeyframeinterval=25&videocodec=h264&fps="+fps+"&compression=25&clock=0&date=0");
		
		Cameras.add(c1);
		Cameras.add(c2);}
	else Cameras=getCamerasList(locations.get(aux_index).id);
			ct.start();
		} 
	}).start();}
	
	
}



class Rec implements Runnable
{  int camera_index;
	int curent_segment;
	String path;
	double SEGMENT_LENGTH;
	double frame_count;
	String session_id;
	String user_id;
	String location_id;
	MyApp appState;
	String url;
	boolean firstTime=false;
	
	public Rec(String Url,int Camera_index,int Segment_index, String Path,double _frame_count, double Segment_length,String USER_ID, String SESSION_ID,String LOCATION_ID,MyApp MyApp, boolean FirstCam)
{camera_index=Camera_index;
curent_segment=Segment_index;
path=Path;
SEGMENT_LENGTH=Segment_length;
url=Url;
frame_count=_frame_count;
session_id=SESSION_ID;
user_id=USER_ID;
location_id=LOCATION_ID;
appState=MyApp;
firstTime=FirstCam;

}
	
	
	@Override
	public void run() {
	//	System.out.println("first time = "+firstTime+" camera_index = "+camera_index);
		
		   
			
		   
		
	if(firstTime)	Toast.makeText(appState.getMainActivityRefference(), "Your recording, \"Fly "+session_id+" IRW\" will be available soon in movies", Toast.LENGTH_LONG).show();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
			
			
		//		System.out.println("frame_count="+frame_count);
				DirectVideoDownloadService.DownloadSegment(url,path,frame_count+"", SEGMENT_LENGTH+"", "tcp"); 
				
		//		System.out.println("first time="+firstTime+" camera index="+camera_index);
				 String nume="thumb"+session_id+"_1.jpg"; 
				   
				File file = new File(Environment.getExternalStorageDirectory().toString()+"/Irewind/thumbs/"+nume);
			     
		if(!file.exists())		
		((MainActivity)	appState.getMainActivityRefference()).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Toast.makeText(appState.getMainActivityRefference(), " \"Fly "+session_id+" IRW\" is now available", Toast.LENGTH_LONG).show();
				
				
			}
		});		
				double end_time=System.currentTimeMillis();
				File f=new File(path);
				System.out.println("checking if file exists");
				if(f.exists()){System.out.println("..it does");
				  DataBase db=appState.getDataBase();
			     	db.addCustomSegment("FlyBoard", session_id, user_id, location_id, path, camera_index+"", (end_time-SEGMENT_LENGTH)+"", end_time+"",curent_segment);
		//	    System.out.println("first time="+firstTime);
			    	db.addCustomSession("FlyBoard", session_id+"","1", user_id+"", location_id, "multi_view", end_time+"","null");
			     	
			     	if(!file.exists()){
			     		File dir=new File(Environment.getExternalStorageDirectory().toString()+"/Irewind/thumbs");
			     		dir.mkdirs();
			     		MediaMetadataRetriever med=new MediaMetadataRetriever();
			     		try {
							med.setDataSource(path);
						writeExternalToCache(Bitmap.createScaledBitmap(med.getFrameAtTime(5000000)  ,60,60,false),file);
						} catch (Exception e) {
						}
			     	}			     	
			     	VideoPlayMulti vp=appState.getMultiPlayer();
			    	if(vp!=null) {vp.reload(); System.out.println("vp reload called");}
			    	else System.out.println("vp is null");
				}
				else {System.out.println("missing:"+path);
				if(firstTime) System.out.println("missing the first segment<<<<<<<<<<<<<<<<>>>>>><");}
				
			}
		}).start();
		
	//	addSegmentToSQL();
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

	}*/


}

