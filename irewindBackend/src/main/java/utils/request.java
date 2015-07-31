package utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;

import com.coremedia.iso.Hex;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import irewindb.services.ServiciuDownloadVideos;
import myClass.MyPoint;
import myClass.MySSLSocketFactory;
import myClass.iLocation;
import myClass.iUser;
import myClass.myBeacon;
import myClass.mySegment;
import myClass.track;
import myClass.videoclip;

public class request {
public static String adresa="https://vps.irewind.com"; //"http://46.108.140.102:8080/";// 


 

public static ArrayList<mySegment> getSegments(iUser user, String location_id, String cam_id, String start_time, String end_time )
{ArrayList<mySegment> segments=new ArrayList<mySegment>();


List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
nameValuePairs.add(new BasicNameValuePair("auth_user", user.getUsername()));
nameValuePairs.add(new BasicNameValuePair("location_id", location_id));
nameValuePairs.add(new BasicNameValuePair("auth_service", user.getAuth_service()));
nameValuePairs.add(new BasicNameValuePair("timestamp_start", start_time));
nameValuePairs.add(new BasicNameValuePair("timestamp_end", end_time));
nameValuePairs.add(new BasicNameValuePair("cam_id", cam_id));

try {
String s=request.GetRequest(nameValuePairs, user.getPassword(), "/video-processor-secured/services/rest-sql/cameras/getSegments");
System.out.println(s);
JSONObject j= new JSONObject(s);

JSONArray ja=j.getJSONArray("results");

for(int i=0;i<ja.length();i++)
{mySegment seg=new mySegment();
seg.cam_id=ja.getJSONObject(i).getString("cam_id");
seg.res_id=ja.getJSONObject(i).getString("res_id");
seg.segm_start=ja.getJSONObject(i).getString("segm_start");
seg.segm_end=ja.getJSONObject(i).getString("segm_end");
seg.location_id=location_id;

segments.add(seg);
}

} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}


return segments;
}

public static JSONObject getRecordingParams(iUser user)
{JSONObject j=null;

List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
nameValuePairs.add(new BasicNameValuePair("auth_user", user.getUsername()));
nameValuePairs.add(new BasicNameValuePair("user_id", user.getId()));
nameValuePairs.add(new BasicNameValuePair("auth_service", user.getAuth_service()));

try {
String s=request.GetRequest(nameValuePairs, user.getPassword(), "/video-processor-secured/services/rest-sql/cameras/getRecordingParams");
System.out.println(s);
 j= (new JSONObject(s)).getJSONArray("results").getJSONObject(0);

}catch(Exception e){e.printStackTrace();}

return j;}


public static void mapCameraPerimeter(iUser user, String location_id, String camera_id, String camera_lat, String camera_long, String p1_lat, String p1_long, String p2_lat,String p2_long,String p3_lat, String p3_long, String p4_lat, String p4_long)
{
	
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	nameValuePairs.add(new BasicNameValuePair("auth_user", user.getUsername()));
	nameValuePairs.add(new BasicNameValuePair("auth_service", user.getAuth_service()));
	/*nameValuePairs.add(new BasicNameValuePair("location_id", location_id));
	nameValuePairs.add(new BasicNameValuePair("cam_id", camera_id));
	nameValuePairs.add(new BasicNameValuePair("cam_v1_p1_lat", p1_lat));
	nameValuePairs.add(new BasicNameValuePair("cam_v1_p1_long", p1_long));
	nameValuePairs.add(new BasicNameValuePair("cam_v1_p2_lat", p2_lat));
	nameValuePairs.add(new BasicNameValuePair("cam_v1_p2_long", p2_long));
	nameValuePairs.add(new BasicNameValuePair("cam_v1_p3_lat", p3_lat));
	nameValuePairs.add(new BasicNameValuePair("cam_v1_p3_long", p3_long));
	nameValuePairs.add(new BasicNameValuePair("cam_v1_p4_lat", p4_lat));
	nameValuePairs.add(new BasicNameValuePair("cam_v1_p4_long", p4_long));*/

    nameValuePairs.add(new BasicNameValuePair("first_index", "0"));
	nameValuePairs.add(new BasicNameValuePair("sqlstatement", "UPDATE irw_cameras "
			+ "SET cam_lat="+camera_lat+", cam_long="+camera_long+
			",cam_v1_p1_lat="+p1_lat+",cam_v1_p1_long="+p1_long+
			",cam_v1_p2_lat="+p2_lat+",cam_v1_p2_long="+p2_long+
			",cam_v1_p3_lat="+p3_lat+",cam_v1_p3_long="+p3_long+
			",cam_v1_p4_lat="+p4_lat+",cam_v1_p4_long="+p4_long+
			",cam_v2_p1_lat="+p1_lat+",cam_v2_p1_long="+p1_long+
			",cam_v2_p2_lat="+p2_lat+",cam_v2_p2_long="+p2_long+
			",cam_v2_p3_lat="+p3_lat+",cam_v2_p3_long="+p3_long+
			",cam_v2_p4_lat="+p4_lat+",cam_v2_p4_long="+p4_long+
			" WHERE location_id="+location_id+" AND cam_id="+camera_id));

	
	try {
	request.PostRequest(nameValuePairs, user.getPassword(), "/video-processor-secured/services/utils/sql");
	request.PostRequest(nameValuePairs, user.getPassword(), "/video-processor-secured/services/utils/sql","https://cg.irewind.com");
	}catch(Exception e){}
	
	
	
	
}

public static ArrayList<myBeacon> getBeaconsList(iUser user, String location_id)
{ArrayList<myBeacon> beacons=new ArrayList<myBeacon>();


List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
nameValuePairs.add(new BasicNameValuePair("auth_user", user.getUsername()));
nameValuePairs.add(new BasicNameValuePair("location_id", location_id));
nameValuePairs.add(new BasicNameValuePair("auth_service", user.getAuth_service()));

try {
String s=request.GetRequest(nameValuePairs, user.getPassword(), "/video-processor-secured/services/rest-sql/beacons/list");
System.out.println(s);
JSONObject j= new JSONObject(s);

JSONArray ja=j.getJSONArray("results");

for(int i=0;i<ja.length();i++)
{myBeacon b=new myBeacon();
b.max_dist=Double.parseDouble(ja.getJSONObject(i).getString("max_dist"));
b.min_dist=Double.parseDouble(ja.getJSONObject(i).getString("min_dist"));
b.beacon_alt=Double.parseDouble(ja.getJSONObject(i).getString("beacon_alt"));
b.beacon_lat=Double.parseDouble(ja.getJSONObject(i).getString("beacon_lat"));
b.beacon_long=Double.parseDouble(ja.getJSONObject(i).getString("beacon_long"));
b.location_id=ja.getJSONObject(i).getString("location_id");
b.id=ja.getJSONObject(i).getString("id");
b.camera_id=ja.getJSONObject(i).getString("camera_id");
b.beacon_major=ja.getJSONObject(i).getString("beacon_major");
b.beacon_minor=ja.getJSONObject(i).getString("beacon_minor");
b.beacon_uuid=ja.getJSONObject(i).getString("beacon_uuid");

beacons.add(b);
}

} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}

return beacons;}
 
public static ArrayList<iLocation> getLocationsList(String user,String service, String password)
{
	Log.d("request.getLocationsList", "begin");



	ArrayList<iLocation> locations=new ArrayList<iLocation>();
	
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
nameValuePairs.add(new BasicNameValuePair("auth_user", user));
nameValuePairs.add(new BasicNameValuePair("first_index", "0"));
nameValuePairs.add(new BasicNameValuePair("auth_service", service));
nameValuePairs.add(new BasicNameValuePair("sqlstatement", "SELECT location_lat,location_long,name,web_url,id,type_id FROM irw_locations"));
try {
String s=request.PostRequest(nameValuePairs, password, "/video-processor-secured/services/utils/sql");
JSONObject j= new JSONObject(s);
String r= j.getString("resultset");
System.out.println("result set: "+r);
JSONArray ja=new JSONArray(r);

for(int i=0;i<ja.length();i++)
{iLocation l=new iLocation();
l.lat=Double.parseDouble(ja.getJSONObject(i).getString("location_lat"));
l.lng=Double.parseDouble(ja.getJSONObject(i).getString("location_long"));
l.l=new LatLng(l.lat, l.lng);
l.setLocation_name(ja.getJSONObject(i).getString("name"));
l.setLocation_url(ja.getJSONObject(i).getString("web_url"));
l.setLocation_id(ja.getJSONObject(i).getString("id"));
l.setLocation_type(ja.getJSONObject(i).getString("type_id"));
locations.add(l);
}

} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}

	
Log.d("request.getLocationsList", "finish");
return locations;

	
}	





public static void reDownloadClips(Context context,String uid, String password, String user,String auth_service)
{	
DataBase db=IrewindBackend.Instance.getDatabase();
	







ArrayList<videoclip> l=db.subsessions("All");


for(int i=0;i<l.size();i++)
{System.out.println("full_res="+l.get(i).full_res);
System.out.println("low_res="+l.get(i).preview_res);



String nume="Clip"+l.get(i).ses_id+"_"+l.get(i).subsession_id+".mp4";
String numeHD="Clip"+l.get(i).ses_id+"_"+l.get(i).subsession_id+"hd.mp4";

if(l.get(i).full_res!=null&&!l.get(i).full_res.equals("")&&!l.get(i).full_res.equalsIgnoreCase("null"))download(l.get(i).full_res, context,  IrewindBackend.Instance.getLocation(l.get(i).location).getLocation_url(), l.get(i).ses_id, l.get(i).subsession_id, true, user,auth_service, uid, password, Environment.getExternalStorageDirectory().toString()+ "/irewindb/" +numeHD);
else if(l.get(i).preview_res!=null&&!l.get(i).preview_res.equals("")&&!l.get(i).preview_res.equalsIgnoreCase("null"))download(l.get(i).preview_res, context, IrewindBackend.Instance.getLocation(l.get(i).location).getLocation_url(), l.get(i).ses_id, l.get(i).subsession_id, false, user,auth_service, uid, password, Environment.getExternalStorageDirectory().toString()+ "/irewindb/" +nume);
}
//final DataBase db_aux;
//final String uid_aux,user_aux,password_aux;
//final Hashtable<String, iLocation> locations_hash_aux=locations_hash;
//db_aux=db;uid_aux=uid;password_aux=password;user_aux=user; 

}



public static void SyncronousDownloadSegmentsFunction(String session_id,ArrayList<mySegment> segs, String location_url, DataBase db, String user_id, String location_id){
	
	File folder = new File(Environment.getExternalStorageDirectory().toString()+"/Irewind/irwTenis"+session_id);
	
	  folder.mkdirs();
for(int index=0;index<segs.size();index++)
{System.out.println("dl index= "+index);
	if(segs.get(index).state==0)
	{segs.get(index).state=1;
	
try {

	
  
	URL url = new URL(location_url+"/video-processor-secured/services/resource/"+segs.get(index).res_id );

 Log.d("irw",url+"");
     URLConnection connection = url.openConnection();  //replace this for https with self signed certificate
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





    
 	db.addCustomSegment("Tenis", session_id+"", user_id+"", segs.get(index).location_id, segs.get(index).path, segs.get(index).cam_id, segs.get(index).segm_start, segs.get(index).segm_end,index);
 	if(index==0) {db.addCustomSession("Tenis", session_id+"","1", user_id+"", location_id, "multi_view");
 	String nume="thumb"+session_id+"_1.jpg"; 
	   
	File file = new File(Environment.getExternalStorageDirectory().toString()+"/Irewind/thumbs/"+nume);
	
	
	File dir=new File(Environment.getExternalStorageDirectory().toString()+"/Irewind/thumbs");
		dir.mkdirs();
		try{
		MediaMetadataRetriever med=new MediaMetadataRetriever();
	
		med.setDataSource(segs.get(index).path);
		
	ServiciuDownloadVideos.writeExternalToCache(Bitmap.createScaledBitmap(med.getFrameAtTime(5000000)  ,60,60,false),file);
		}catch(NoSuchMethodError e) {Log.d("ServiciulDownloadVideos.DownloadSegmentsFunction","ERROR CAUGHT:"+e.getMessage());}
	
 	
 	}
 	segs.get(index).state=2;
  
 	
 
 	
 	 db.updateField(session_id+"", "1", "download_progress", (((index+1)*100)/segs.size())+"");
 	
 	



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


	
}

public static void getUserVideos(iUser user)
{   
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	nameValuePairs.add(new BasicNameValuePair("auth_user", user.getUsername()));
	nameValuePairs.add(new BasicNameValuePair("auth_service", user.getAuth_service()));
	nameValuePairs.add(new BasicNameValuePair("user_id", user.getId()));
	try {
	String s=request.GetRequest(nameValuePairs, user.getPassword(), "/video-processor-secured/services/rest-sql/videos/getUserVideos");
	System.out.println(s);
	JSONObject j= new JSONObject(s);
	JSONArray ja=j.getJSONArray("results");	
	if(ja!=null && ja.length()>0)
	{DataBase db = IrewindBackend.Instance.getDatabase();
	db.ClearTabel("irw_sessions");
	for (int i=0;i< ja.length();i++){
		JSONObject jo= (JSONObject) ja.get(i);
		ContentValues values= new ContentValues();
		values.put("session_id", jo.getString("session_id"));
		values.put("subsession_id", jo.getString("subsession_id"));
		values.put("user_id", user.getId());
		values.put("start_time", jo.getString("start_time"));
		values.put("stop_time", jo.getString("stop_time"));
		values.put("location_id", jo.getString("location_id"));
		values.put("thumbnail_res_id", jo.getString("thumbnail"));
		values.put("full_res_id", jo.getString("hdvideo"));
		values.put("preview_res_id", jo.getString("preview"));
		values.put("demo_path", Environment.getExternalStorageDirectory().toString()+ "/irewindb/" +jo.getString("session_id")+"_"+jo.getString("subsession_id")+".mp4");
		values.put("hd_path", Environment.getExternalStorageDirectory().toString()+ "/irewindb/" +jo.getString("session_id")+"_"+jo.getString("subsession_id")+"_hd.mp4");
		db.AddContentValues(values, "irw_sessions");
	}}
	
	}catch (Exception e){e.printStackTrace();}

}

public static void SincronizeSessions(String user,String auth_service,int user_id,String password,Context context, String server_address)
{	Log.d("request.SincronizeSessions", "enter");
	
DataBase d=IrewindBackend.Instance.getDatabase();
//int initial_movies_count=d.getMoviesCount();

int i=d.maxSessionInSessions(user_id);
int s=d.maxSessionInLogs(user_id);
if(i+10<s)
	i=s-10;
Log.d("request.SincronizeSession","max_ses in user_sessions="+i+" max_ses in user_logs= "+s);
for(;i<=s;i++)
{ String rsp=  analyzeGPS(user,auth_service, password, user_id, Integer.parseInt(d.getLocationIdForSessionFromLogs(i+"")), i, context, server_address);

try {
	JSONObject json=new JSONObject(rsp);
//if(	json.getInt("created_subsessions")+json.getInt("updated_subsessions")>0)
for(int j=1;j<=json.getInt("detected_subsessions");j++)
	Compose(user, auth_service, password, user_id+"", d.getLocationIdForSessionFromLogs(i+""), i+"", j+"", "320", "240", context, request.adresa);//compose preview - 720p in V_ADONIS
	
} catch (JSONException e1) {
	// TODO Auto-generated catch block
	e1.printStackTrace();
//	Compose(user, auth_service, password, user_id+"", location_id+"", i+"", 1+"", "320", "240", context, server_address);//compose preview - 720p in V_ADONIS

}

try{analizeTrack(user,auth_service, user_id+"", d.getLocationIdForSessionFromLogs(i+""), password, context, i+"",server_address);
}catch(Exception e){}


}

//d.ClearErrorSessions();
//updateTabelWithUserId("irw_sessions", user,auth_service,password, context,user_id+"",server_address,false);
getUserVideos(new iUser(user,user_id+"",password,auth_service));

//updateSubsession("irw_sessions", user, password, context, user_id+"", server_address);

//if(d.getMoviesCount()>initial_movies_count && isApplicationSentToBackground(context))
//	appState.getMainActivityRefference().send_notification();

Log.d("request.SincronizeSessions", "finish");
}

static Comparator comparator=new Comparator<NameValuePair>() {public int compare(NameValuePair lhs, NameValuePair rhs) {return lhs.getName().compareTo(rhs.getName());}};
static HttpClient httpclient = getNewHttpClient();



public static void transferSessionInfo(iUser user, String location_id)
{Log.d("request.transferSessionInfo", "enter");
List<NameValuePair> nameValuePairs2 = new ArrayList<NameValuePair>(2);
nameValuePairs2.add(new BasicNameValuePair("auth_user", user.getUsername()));
nameValuePairs2.add(new BasicNameValuePair("auth_service", user.getAuth_service()));
nameValuePairs2.add(new BasicNameValuePair("user_id", user.getId()));
nameValuePairs2.add(new BasicNameValuePair("location_id",location_id+""));
String raspuns2="";
try {
	raspuns2 = PostRequest(nameValuePairs2, user.getPassword(), "/video-processor-secured/services/user/transferSessionInfo");
	Log.d("irw","raspuns transferSessionInfo:"+raspuns2);
	} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
Log.d("request.transferSessionInfo", "finish");


}


public static void userBindclip(iUser user, String location_id, String res_id, String clip_start, String clip_end, String clip_width,String clip_height, String latitude, String longitude)
{Log.d("request.userBindclip", "enter");
List<NameValuePair> nameValuePairs2 = new ArrayList<NameValuePair>(2);
nameValuePairs2.add(new BasicNameValuePair("auth_user", user.getUsername()));
nameValuePairs2.add(new BasicNameValuePair("auth_service", user.getAuth_service()));
nameValuePairs2.add(new BasicNameValuePair("res_id", res_id));
nameValuePairs2.add(new BasicNameValuePair("location_id",location_id+""));
nameValuePairs2.add(new BasicNameValuePair("clip_start", clip_start));
nameValuePairs2.add(new BasicNameValuePair("clip_end", clip_end));
nameValuePairs2.add(new BasicNameValuePair("clip_width", clip_width));
nameValuePairs2.add(new BasicNameValuePair("clip_height",clip_height));
nameValuePairs2.add(new BasicNameValuePair("latitude", latitude));
nameValuePairs2.add(new BasicNameValuePair("longitude", longitude));
String raspuns2="";
try {
	raspuns2 = PostRequest(nameValuePairs2, user.getPassword(), "/video-processor-secured/services/rest-sql/users/bind_clip");
	Log.d("irw","raspuns userBindclip:"+raspuns2);
	} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
Log.d("request.userBindclip", "finish");


}

public static boolean resourceCreate( iUser user, String path) {
	
	String file_name;
	file_name = path.substring(path.length() - Math.min(12, path.length()));
	MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
	metaRetriver.setDataSource(path);
	System.out.println("METADATA_KEY_DATE ="+metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE));
	System.out.println("METADATA_KEY_DURATION ="+metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
	
	String location = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_LOCATION);
	String lat="0", lng="0";
	if(location != "null") {
		int index = location.substring(1).indexOf("-");
		if (index == -1)
			index = location.substring(1).indexOf("+");
	
	lat = location.substring(0,index+1);
	lat = lat.replace("+", "");
	lng = location.substring(index+3);
	lng = lng.replace("+", "");
	}
	
	System.out.println("lat="+lat);
	System.out.println("lng="+lng);
	System.out.println("METADATA_KEY_VIDEO_HEIGHT ="+metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
	System.out.println("METADATA_KEY_VIDEO_WIDTH ="+metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
	System.out.println("METADATA_KEY_YEAR ="+metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR));
	
	
	
	try{
	
	String dateS = metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
	SimpleDateFormat  format = new SimpleDateFormat("yyyyMMdd'T'HHmmss.SSS'Z'");
	Date date = format.parse(dateS);
	System.out.println("date ="+date);
	System.out.println("timestamp END = "+date.getTime());

	        HttpPost httppost = new HttpPost(adresa+"/video-processor-secured/services/resource/create");
	        
	        
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("auth_service", user.getAuth_service()));
	        nameValuePairs.add(new BasicNameValuePair("auth_user", user.getUsername()));
	        nameValuePairs.add(new BasicNameValuePair("storage_server_id", "1"));
	        nameValuePairs.add(new BasicNameValuePair("file_name", file_name));
	        nameValuePairs.add(new BasicNameValuePair("use_id_as_name", "true"));
	        nameValuePairs.add(new BasicNameValuePair("use_name_as_id", "false"));
	        nameValuePairs.add(new BasicNameValuePair("type", "USER_CLIP"));

	        
	        
	        
	        String valoare="";
	        Collections.sort(nameValuePairs, comparator);	
	        for(int i=0;i<nameValuePairs.size();i++)
	        {valoare+=nameValuePairs.get(i).getName()+nameValuePairs.get(i).getValue().replace(" ","+");
	        } 
	        System.out.println("resourceCreate params: "+valoare);
	        System.out.println("resourceCreate pass: "+user.getPassword());
	        
	       String signature=encode(user.getPassword(),valoare); 
	        
           MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  
	        multipartEntity.addPart("auth_service", new org.apache.http.entity.mime.content.StringBody(user.getAuth_service()));
	        multipartEntity.addPart("auth_user", new org.apache.http.entity.mime.content.StringBody(user.getUsername()));
	        multipartEntity.addPart("storage_server_id", new org.apache.http.entity.mime.content.StringBody("1"));
	        multipartEntity.addPart("file_name[]", new org.apache.http.entity.mime.content.StringBody(file_name));
	        multipartEntity.addPart("use_id_as_name", new org.apache.http.entity.mime.content.StringBody("true"));
	        multipartEntity.addPart("use_name_as_id", new org.apache.http.entity.mime.content.StringBody("false"));
	        multipartEntity.addPart("type", new org.apache.http.entity.mime.content.StringBody("USER_CLIP"));
	        multipartEntity.addPart("file[]", new FileBody(new File(path)));
	        multipartEntity.addPart("signature", new org.apache.http.entity.mime.content.StringBody(signature));
		       
	        
	         
	        httppost.setEntity(multipartEntity);
	        Log.d("SegmentUploader", "will start request");
	        String response=httpclient.execute(httppost, new BasicResponseHandler());
	        Log.d("SegmentUploader", "response="+response); 
	        String res_id = ((JSONObject) (new JSONObject(response)).getJSONArray("resources").get(0)).getString("id");
	        
	        userBindclip(user, "32", res_id,(date.getTime() -Long.parseLong( metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)))+"",date.getTime()+"",metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH),metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT),lat,lng);
	        transferSessionInfo(user, "32");
	        return true;} catch (Exception e) {
	       e.printStackTrace();
	       return false;
	    }
	
}





public static boolean UploadSegment(File segment, String location_id, String cam_id, String server_id, String recording_profile,String segm_start,String segm_end)
{
	//segment=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/test.mp4");
	
	
	 try {

	        HttpPost httppost = new HttpPost(adresa+"/video-processor-secured/services/record/uploadSegment");
	        
	        
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("auth_service", "iRewind"));
	        nameValuePairs.add(new BasicNameValuePair("auth_user", "nicolescu.mihai@gmail.com"));
	        nameValuePairs.add(new BasicNameValuePair("location_id", location_id));
	        nameValuePairs.add(new BasicNameValuePair("camera_id", cam_id));
	        nameValuePairs.add(new BasicNameValuePair("storage_server_id", server_id));
	        nameValuePairs.add(new BasicNameValuePair("recording_profile_id", recording_profile));
	        nameValuePairs.add(new BasicNameValuePair("segment_start", segm_start));
	        nameValuePairs.add(new BasicNameValuePair("segment_end", segm_end));
	        String valoare="";
	        Collections.sort(nameValuePairs, comparator);	
	        for(int i=0;i<nameValuePairs.size();i++)
	        {valoare+=nameValuePairs.get(i).getName()+nameValuePairs.get(i).getValue().replace(" ","+");
	        } 
	       String signature=encode("irw-user",valoare); 
	        
            @SuppressWarnings("deprecation")
			MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  
            
	        multipartEntity.addPart("auth_service", new org.apache.http.entity.mime.content.StringBody("iRewind"));
	        multipartEntity.addPart("auth_user", new org.apache.http.entity.mime.content.StringBody("nicolescu.mihai@gmail.com"));
	        multipartEntity.addPart("location_id", new org.apache.http.entity.mime.content.StringBody(location_id));
	        multipartEntity.addPart("camera_id", new org.apache.http.entity.mime.content.StringBody(cam_id));
	        multipartEntity.addPart("storage_server_id", new org.apache.http.entity.mime.content.StringBody(server_id));
	        multipartEntity.addPart("recording_profile_id", new org.apache.http.entity.mime.content.StringBody(recording_profile));
	        multipartEntity.addPart("segment_start", new org.apache.http.entity.mime.content.StringBody(segm_start));
	        multipartEntity.addPart("segment_end", new org.apache.http.entity.mime.content.StringBody(segm_end));
	        multipartEntity.addPart("file", new FileBody(segment));
	        multipartEntity.addPart("signature", new org.apache.http.entity.mime.content.StringBody(signature));
		       
	        
	         
	        httppost.setEntity(multipartEntity);
	        Log.d("SegmentUploader", "will start request");
	        String response=httpclient.execute(httppost, new BasicResponseHandler());
	        Log.d("SegmentUploader", "response="+response); 
	        return true;} catch (Exception e) {
	       e.printStackTrace();
	       return false;
	    }
	
	}

public static String PostRequest( List<NameValuePair> lista, String password, String path, String local_server) throws Exception
{ try{
HttpPost httppost = new HttpPost(local_server+path);

String valoare="";
String humanReadable="";
Collections.sort(lista, comparator);	
for(int i=0;i<lista.size();i++)
{valoare+=lista.get(i).getName()+lista.get(i).getValue().replace(" ","+");
if(lista.get(i).getName().equalsIgnoreCase("log"))
humanReadable+="log : log_value is not written in the log"+System.getProperty("line.separator");
else
 humanReadable+=lista.get(i).getName()+" : "+lista.get(i).getValue().replace(" ","+")+System.getProperty("line.separator");
}
Log.d("request.PostRequest","will make request with url:"+local_server+path+System.getProperty("line.separator")+"password="+password+System.getProperty("line.separator")+humanReadable);
String signature=encode(password,valoare);
Log.d("request.PostRequest", "signature= "+signature);
lista.add(new BasicNameValuePair("signature", signature));
  httppost.setEntity(new UrlEncodedFormEntity(lista));
 ResponseHandler<String> hnd=new BasicResponseHandler();
String response = httpclient.execute(httppost,hnd);
Log.d("request.PostRequest", "request finished with result= "+response);
return response;
}catch(Exception e){e.printStackTrace(); return "Exception";}
catch(Error er){er.printStackTrace(); return "Error";}}


public static String PostRequest( List<NameValuePair> lista, String password, String path) throws Exception
{ return PostRequest(lista, password, path, adresa);}


public static String GetRequest( List<NameValuePair> lista, String password, String path,String local_server) throws Exception
{ 
String valoare="";
Collections.sort(lista, comparator);	
for(int i=0;i<lista.size();i++)
{valoare+=lista.get(i).getName()+lista.get(i).getValue().replace(" ","+");}
String signature=encode(password,valoare);
lista.add(new BasicNameValuePair("signature", signature));
String parametrii=new String();
for(int i=0;i<lista.size();i++)
{if(i==0)parametrii+="?"; else parametrii+="&";
parametrii+=lista.get(i).getName()+"="+lista.get(i).getValue().replace(" ","+").replace("=","%3D");}
 HttpGet httpget = new HttpGet((local_server+path+parametrii));
 Log.d("request.GetRequest","will make request with url= "+local_server+path+parametrii);
 ResponseHandler<String> hnd=new BasicResponseHandler();
String response = httpclient.execute(httpget,hnd);

return response;}

public static String GetRequest( List<NameValuePair> lista, String password, String path) throws Exception
{return GetRequest(lista, password, path, adresa);	}




public static void updateThumbs(Context context)
{ Log.d("request.updateThumbs", "enter");
	
DataBase db=IrewindBackend.Instance.getDatabase();
ArrayList<videoclip> l=db.subsessions("All");

System.out.println("thumb count = "+l.size());
Log.d("irw","update thumbs<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

for(int i=0;i<l.size();i++)
{DownloadThumb(l.get(i), adresa);}

	
	
	Log.d("request.updateThumbs", "finish");
}

public static void DownloadThumb(videoclip v, String address)
{File folder = new File(Environment.getExternalStorageDirectory().toString()+ "/irewindb/thumbs");
folder.mkdirs();
if(!(v.thumb_res.equalsIgnoreCase("null")))
{String nume;
	nume="thumb"+v.ses_id+"_"+v.subsession_id+".jpg";
File file = new File(Environment.getExternalStorageDirectory().toString()+ "/irewindb/thumbs/" +nume);
if(!file.exists()) 
{   
        try {Log.d("irw","downloading thumb "+nume);
       
     //   Log.d("irw",address+"/video-processor-secured/services/resource/thumbnail/"+v.preview_res);
      //  	URL url = new URL(address+"/video-processor-secured/services/resource/thumbnail/"+v.preview_res);
        URL url = new URL(v.thumb_res);
        	HttpURLConnection http = null;
          if (url.getProtocol().toLowerCase().equals("https")) {
        	  System.out.println("is https");
      	    trustAllHosts();
      		HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
      		https.setHostnameVerifier(DO_NOT_VERIFY);
      		http = https;
      	} else {
      		http = (HttpURLConnection) url.openConnection();
      	}
          
          
            http.connect();
           
     
          InputStream input = http.getInputStream();
          File film= new File(folder, nume);
            OutputStream output = new FileOutputStream(film);
 byte data[] = new byte[1024];
            int count;
           while (((count = input.read(data)) != -1)) {
        	   Log.d("irw","count:"+count);
            output.write(data, 0, count);
            } output.flush();
            output.close();
            input.close();
        } catch (Exception e) {e.printStackTrace(); } //return sUrl[1]; }}

 }}}


public static void analizeTracks(String user,String auth_service,String user_id,String password,Context context, String server_address) 
{	Log.d("request.analizeTracks", "enter");
	
DataBase d=IrewindBackend.Instance.getDatabase();
ArrayList<track> l=d.SimpleTrackList();
for(int i=0;i<l.size();i++)
	try {
		analizeTrack(user,auth_service,user_id,l.get(i).location_id,password,context,l.get(i).ses_id,server_address);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
Log.d("request.analizeTracks", "finish");
}

public static void analizeTrack(String user,String auth_service,String user_id,String location_id,String password,Context context, String track, String web_address) throws Exception
{Log.d("request.analizeTrack","enter ... analize of track "+track);
	ContentValues valori=request.getSesStatistics(user,auth_service,password,user_id, track, location_id,web_address);


DataBase d=IrewindBackend.Instance.getDatabase();
d.AddContentValues(valori, "trackuri");
Log.d("request.analizeTrack", "finish");

}



public static void uploadGPS(String user,String auth_service , String password, int user_id, Context context)
{
	uploadGPS(user,auth_service, password, user_id, context, adresa);

}


public static void uploadGPS(String user,String auth_service ,String password, int user_id, Context context, String local_server)
{Log.d("request.uploadGPS", "enter");





if(	isNetworkAvailable(context))	
{DataBase db=IrewindBackend.Instance.getDatabase();
long delta=0;

	try {if(	IrewindBackend.Instance.deltaTime==0) IrewindBackend.Instance.calculateDeltaTime();
		 delta=IrewindBackend.Instance.deltaTime;

} catch (Exception e1) {}
	
	
if(IrewindBackend.Instance.deltaTime!=0){	
	int session_id=db.maxSessionInSessions(user_id);
int s=db.maxSessionInLogs(user_id);
if(session_id+50<s)
	session_id=s-50;
for(;session_id<=s;session_id++){
	System.out.println("uploading "+session_id);
	StringBuilder gps_data=new StringBuilder("[");
			
	
	
	ArrayList<MyPoint> pt=db.unsynincronisedPoints(session_id, user_id);
	
	if (pt!=null&&pt.size()>3){
	for(int i=0;i<pt.size();i++)// TODO Auto-generated method stub
	{//Log.d("irw","addindg "+i+" out of "+ pt.size());
		if(i>0) gps_data.append(",");
		gps_data.append("{");
	gps_data.append("\"time\":"+(Long.parseLong( pt.get(i).time)+delta));
	gps_data.append(",\"alt\":"+pt.get(i).altitudine);
	gps_data.append(",\"latitude\":"+pt.get(i).latitudine);
	gps_data.append(",\"longitude\":"+pt.get(i).longitudine);
	gps_data.append(",\"accuracy\":"+pt.get(i).accuracy);
	gps_data.append(",\"heading\":"+pt.get(i).heading);
	gps_data.append(",\"speed\":"+pt.get(i).speed);
	gps_data.append("}");}
	gps_data.append("]");
	
	Log.d("irw","upload..");
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
nameValuePairs.add(new BasicNameValuePair("auth_user", user));
nameValuePairs.add(new BasicNameValuePair("auth_service", auth_service));
nameValuePairs.add(new BasicNameValuePair("user_id", user_id+""));
nameValuePairs.add(new BasicNameValuePair("location_id",db.getLocationIdForSessionFromLogs(session_id+"")));
nameValuePairs.add(new BasicNameValuePair("session_id",session_id+""));
nameValuePairs.add(new BasicNameValuePair("gps_data",gps_data.toString()));

System.out.println("location="+db.getLocationIdForSessionFromLogs(session_id+"")+" for session"+session_id);

//Log.d("irw",gps_data.toString());

try {Log.d("irw","request1 waiting for response...");
	String raspuns= PostRequest(nameValuePairs, password, "/video-processor-secured/services/user/appendGPSLog",local_server);
	Log.d("irw","raspuns1 pentru session_id="+session_id+" is " +raspuns);
JSONObject js=new JSONObject(raspuns);
Log.d("irw","inserted= "+js.get("inserted_points")+"");
if(!js.get("inserted_points").toString().equalsIgnoreCase("0"))
{Log.d("irw","synching");
analizeTrack(user,auth_service, user_id+"", db.getLocationIdForSessionFromLogs(session_id+""), password, context, session_id+"",local_server);//REMOVE IN V_ADONIS

	db.sync(session_id+"", user_id+"", pt.get(0).time,pt.get(pt.size()-1).time);
	
System.out.println("syncronised session "+session_id);
}


} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}}}}}
else Log.d("request.uploadGPS", "network unavailable");
Log.d("request.uploadGPS", "finish");

}



public static String analyzeGPS(String user,String auth_service, String password, int user_id, int location_id,int session_id, Context context, String server_address)
{Log.d("request.analyzeGPS", "enter");
List<NameValuePair> nameValuePairs2 = new ArrayList<NameValuePair>(2);
nameValuePairs2.add(new BasicNameValuePair("auth_user", user));
nameValuePairs2.add(new BasicNameValuePair("auth_service", auth_service));
nameValuePairs2.add(new BasicNameValuePair("user_id", user_id+""));
nameValuePairs2.add(new BasicNameValuePair("location_id",location_id+""));
nameValuePairs2.add(new BasicNameValuePair("session_id",session_id+""));
String raspuns2="";
try {
	//updateTrack(user, user_id+"", location_id+"", password, context, session_id+"");
	raspuns2 = PostRequest(nameValuePairs2, password, "/video-processor-secured/services/user/analyzeGPSLog",server_address);
	JSONObject js=new JSONObject(raspuns2);
	/*if(js.getString("created_subsessions").equalsIgnoreCase("0")) {MyApp appState = ((MyApp)context.getApplicationContext());
	DataBase db=appState.getDataBase();
	db.addEmptySession(session_id+"", user_id+"",location_id+"");}*/
	Log.d("irw","raspuns2:"+raspuns2);
	} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
Log.d("request.analyzeGPS", "finish");

return raspuns2;

}
public static String Compose(String user,String auth_service, String password, String user_id, String location_id,String session_id, String subsession_id,String width, String height, Context context)
{return Compose(user,auth_service, password, user_id, location_id, session_id, subsession_id,width,height, width, height, context,adresa);}

public static String Compose(String user,String auth_service, String password, String user_id, String location_id,String session_id, String subsession_id,String width, String height, Context context, String server_address)
{return Compose( user, auth_service,  password,  user_id,  location_id, session_id,  subsession_id, width,  height, width,  height,  context,  server_address);
}

public static String Compose(String user,String auth_service, String password, String user_id, String location_id,String session_id, String subsession_id,String source_width, String source_height,String width, String height, Context context, String server_address)
{System.out.println("enter, session="+session_id);
	Log.i("request.Compose","enter, session="+session_id);
	String raspuns="";
	List<NameValuePair> nameValuePairs2 = new ArrayList<NameValuePair>(2);
nameValuePairs2.add(new BasicNameValuePair("auth_user", user));
nameValuePairs2.add(new BasicNameValuePair("auth_service", auth_service));
nameValuePairs2.add(new BasicNameValuePair("user", user_id));
nameValuePairs2.add(new BasicNameValuePair("location",location_id));
nameValuePairs2.add(new BasicNameValuePair("session",session_id));
nameValuePairs2.add(new BasicNameValuePair("subsession", subsession_id));
nameValuePairs2.add(new BasicNameValuePair("width", width));
nameValuePairs2.add(new BasicNameValuePair("height",height));
nameValuePairs2.add(new BasicNameValuePair("source_width", "1280"));
nameValuePairs2.add(new BasicNameValuePair("source_height","720"));
nameValuePairs2.add(new BasicNameValuePair("format","mp4"));
nameValuePairs2.add(new BasicNameValuePair("sound_id", "1"));
nameValuePairs2.add(new BasicNameValuePair("stream_copy","False"));
nameValuePairs2.add(new BasicNameValuePair("exact_fit","False"));
if (height.equalsIgnoreCase("720"))
	nameValuePairs2.add(new BasicNameValuePair("profile_name", "hi"));
else nameValuePairs2.add(new BasicNameValuePair("profile_name", "low"));
nameValuePairs2.add(new BasicNameValuePair("destination_storage_server_id","12"));
nameValuePairs2.add(new BasicNameValuePair("debug_picture","False"));
nameValuePairs2.add(new BasicNameValuePair("simulate","False"));
nameValuePairs2.add(new BasicNameValuePair("intro","False"));
nameValuePairs2.add(new BasicNameValuePair("trailer","False"));
try {Log.i("irw","compose waiting for response...");
Log.i("irw",nameValuePairs2+"");
 raspuns= PostRequest(nameValuePairs2, password, "/video-processor-secured/services/compose/composeWithSession",server_address);
Log.i("irw","raspuns1:" +raspuns);


} catch (HttpResponseException e) {
	//Intent intent= new Intent(context,Ticket_Dialog.class);
	//context.startActivity(intent);
e.printStackTrace();
} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
Log.i("request.Compose", "finish");
return raspuns;
}

	public static String createPointNow(String user,String auth_service, String password, String user_id, String location_id,String camera_id,String session_id){
		String raspuns="";
		try {
		List<NameValuePair> nameValuePairs2 = new ArrayList<NameValuePair>(2);
		nameValuePairs2.add(new BasicNameValuePair("auth_user", user));
		nameValuePairs2.add(new BasicNameValuePair("auth_service", auth_service));
		nameValuePairs2.add(new BasicNameValuePair("user_id", user_id));
		nameValuePairs2.add(new BasicNameValuePair("location_id",location_id));
		nameValuePairs2.add(new BasicNameValuePair("cam_id",camera_id));
			if(!session_id.equalsIgnoreCase("null")) nameValuePairs2.add(new BasicNameValuePair("session_id",session_id));
			nameValuePairs2.add(new BasicNameValuePair("point_time",System.currentTimeMillis()+""));
		raspuns= PostRequest(nameValuePairs2, password, "/video-processor-secured/services/rest-sql/users/createPointAtTimestamp");
			JSONObject j = new JSONObject(raspuns);
			raspuns = j.getJSONArray("results").getJSONObject(0).getInt("MAX(session_id)")+"";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return raspuns;
	}

	
public static void updateTable(String tabel, String user,String auth_service,String password, Context t, boolean drop_table)
{updateTable(tabel, user,auth_service, password, t, drop_table, adresa);}
	
	public static void updateTable(String tabel, String user,String auth_service,String password, Context t, boolean drop_table, String server_address)
	{Log.d("request.updateTabel", "enter");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	nameValuePairs.add(new BasicNameValuePair("auth_user", user));
    nameValuePairs.add(new BasicNameValuePair("auth_service", auth_service));
	nameValuePairs.add(new BasicNameValuePair("first_index", "0"));
	nameValuePairs.add(new BasicNameValuePair("sqlstatement", "SELECT* FROM "+tabel));
	try {
	String s=request.PostRequest(nameValuePairs, password, "/video-processor-secured/services/utils/sql",server_address);
	JSONObject o=new JSONObject(s);
	
	DataBase db=IrewindBackend.Instance.getDatabase();
	if(drop_table) db.ClearTabel(tabel);
	db.AddJson(o.getString("resultset"),tabel);
	} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
	}
	Log.d("request.updateTabel", "finish");
	}
	
	public static void updateTabelWithUserId(String tabel, String user,String auth_service,String password, Context t,String id)
	{updateTabelWithUserId(tabel, user,auth_service, password, t, id, adresa, true);}
	
	public static void updateTabelWithUserId(String tabel, String user,String auth_service,String password, Context t,String id,String server_address)
	{updateTabelWithUserId(tabel, user, auth_service,password, t, id, server_address, true);}
	
	public static void updateTabelWithUserId(String tabel, String user,String auth_service,String password, Context t,String id,String server_address, boolean clear)
	{Log.d("request.updateTabelWithUserId", "enter");
	DataBase db=IrewindBackend.Instance.getDatabase();
	int nr=0;
	if(clear) db.ClearTabel(tabel);
		
		int curent=0;
	do{
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	nameValuePairs.add(new BasicNameValuePair("auth_user", user));
	nameValuePairs.add(new BasicNameValuePair("auth_service", auth_service));
	nameValuePairs.add(new BasicNameValuePair("count", "3000"));
	nameValuePairs.add(new BasicNameValuePair("first_index", curent+""));
	nameValuePairs.add(new BasicNameValuePair("sqlstatement", "SELECT* FROM "+tabel +" WHERE user_id="+id));
	try {
		Log.d("irw","incepe requestul");
	String s=request.PostRequest(nameValuePairs, password, "/video-processor-secured/services/utils/sql",server_address);
Log.d("irw","dupa request");
	//JSONObject o=new JSONObject(s);

	//Log.d(o.getString("resultset"));
	
	
	 nr=db.AddJson(s.substring(s.indexOf("resultset")+11, s.length()-1),tabel,clear);
	curent+=nr;

	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
	//Log.d("elemente primite:"+nr+"   <<<<<<<<<");
	}
	
	while(nr==3000);
	
	//if(tabel.equalsIgnoreCase("irw_sessions")) updateThumbs(user, Integer.parseInt(id), 20, password,t);
	
	
	Log.d("request.updateTabelWithUserId", "finish");
	}

	
	
	/*public static void updateSubsession(String tabel, String user,String auth_service,String password, Context t,String id, String server_address)
	{	Log.d("request.updateSubsession", "enter");
		MyApp appState = ((MyApp)t.getApplicationContext());
	DataBase db=appState.getDataBase();
	int nr=0;
	
		
		int curent=0;
	do{
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	nameValuePairs.add(new BasicNameValuePair("auth_user", user));
	nameValuePairs.add(new BasicNameValuePair("auth_service", auth_service));
	nameValuePairs.add(new BasicNameValuePair("count", "3000"));
	nameValuePairs.add(new BasicNameValuePair("first_index", curent+""));
	nameValuePairs.add(new BasicNameValuePair("sqlstatement", "SELECT* FROM "+tabel +" WHERE user_id="+id));
	try {
	//	Log.d("incepe requestul");
	String s=request.PostRequest(nameValuePairs, password, "/video-processor-secured/services/utils/sql",server_address);
//Log.d("incepe parsarea");
	//JSONObject o=new JSONObject(s);

	//Log.d(o.getString("resultset"));
	
	System.out.println(s);
	 nr=db.AddSessions(s.substring(s.indexOf("resultset")+11, s.length()-1),tabel);
	curent+=nr;

	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
	//Log.d("elemente primite:"+nr+"   <<<<<<<<<");
	}
	
	while(nr==3000);
	
	Log.d("request.updateSubsession", "finish");
	
	//if(tabel.equalsIgnoreCase("irw_sessions")) updateThumbs(user, Integer.parseInt(id), 20, password,t);
	}
	*/
	public static boolean isNetworkAvailable(Context context) {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    if (activeNetworkInfo != null && activeNetworkInfo.isConnected())
	    	return true;
	    else return false;
	}
	
	static int retur = 0;
  static Context context;
	public static boolean isConnectionToServerAvailable(Context context_) {
		context=context_;
		retur = 0;
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<NameValuePair> lista = new ArrayList<NameValuePair>(2);
				lista.add(new BasicNameValuePair("auth_user", "test"));
				lista.add(new BasicNameValuePair("auth_service", "iRewind"));

				try {
					request.GetRequest(lista, "1234",
							"/video-processor-secured/services/utils/timestamp");
					synchronized (context) {
						retur = 1;
						context.notify();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					synchronized (context) {
						retur = -1;
						context.notify();
					}
				}

			}
		}).start();

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				synchronized (context) {
					retur = -1;
					context.notify();
				}
			}
		}, 3500);

		while (retur == 0)
			try {
				synchronized (context) {
					System.out.println("will wait");
					context.wait();
					System.out.println("waited");
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		if (retur == 1)
			return true;
		else
			return false;
	}
	
	
	public static boolean updateCards(Context context,String uid, String password, String user, String auth_service,int type_id)
	{   System.out.println("<<<<<<<<<<<<< << << updateCardsRequest --1");
		if(isNetworkAvailable(context)){
			System.out.println("<<<<<<<<<<<<< << << updateCardsRequest --2");
	DataBase db=IrewindBackend.Instance.getDatabase();

  

	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	nameValuePairs.add(new BasicNameValuePair("auth_user", user));
	nameValuePairs.add(new BasicNameValuePair("auth_service", auth_service));
	nameValuePairs.add(new BasicNameValuePair("card_id", ""));
	nameValuePairs.add(new BasicNameValuePair("card_type", type_id+""));
	nameValuePairs.add(new BasicNameValuePair("locale", ""));
	
	
	try {
		Log.d("irw","incepe requestul");
	String s=request.PostRequest(nameValuePairs, password, "/video-processor-secured/services/card/list");
Log.d("irw","dupa request");
	//JSONObject o=new JSONObject(s);

	//Log.d(o.getString("resultset"));
    if(s!=null&&s.contains("cards"))
    db.clearCards(type_id);
    else return false;
    
	db.AddJson(s.substring(s.indexOf("cards")+7, s.length()-1),"cards");
	

	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
	
return true;
		}
		return false;
	}
	
	

public static String encode(String key, String data)  {
	System.out.println("data="+data);
	String result="encoding error";
	try{
	Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
	  SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
	  sha256_HMAC.init(secret_key);
result=(new String(Hex.encodeHex(sha256_HMAC.doFinal(data.getBytes())))).toLowerCase();
	}catch(NoSuchAlgorithmException e)
	{}catch(InvalidKeyException e){}
	System.out.println("result="+result);
	return result;
	}



public static void bindCamera(iUser user,Context context, String location_id, Double latitude, Double longitude)
{JSONObject j2=null;
	String lat = latitude +"";
	String lng = longitude +"";
	if(lat.endsWith(".0")) {lat = lat.replace(".0","");}
	if(lng.endsWith(".0")){ lng = lng.replace(".0","");}
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
nameValuePairs.add(new BasicNameValuePair("auth_user", user.getUsername()));
nameValuePairs.add(new BasicNameValuePair("auth_service", user.getAuth_service()));
nameValuePairs.add(new BasicNameValuePair("location_id", location_id));
nameValuePairs.add(new BasicNameValuePair("cam_lat", lat));
nameValuePairs.add(new BasicNameValuePair("cam_long", lng));
nameValuePairs.add(new BasicNameValuePair("device_uuid", Secure.getString(context.getContentResolver(),Secure.ANDROID_ID)));
nameValuePairs.add(new BasicNameValuePair("cam_model_id", "11"));


	try {
		String raspuns=request.PostRequest(nameValuePairs,user.getPassword(), "/video-processor-secured/services/rest-sql/users/bindCamera");
	/*	Log.d("irw","response createGuestUser: "+raspuns);
		if (raspuns!=null&&!raspuns.equalsIgnoreCase("")){
		JSONObject j=new JSONObject(raspuns);
		String usr=j.getString("user");
		if (usr!=null&&!usr.equalsIgnoreCase("")){
		j2=new JSONObject(usr);
		
		}}*/
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
//	return j2;
	}

public static JSONObject createGuestUser(Context context)
{JSONObject j2=null;
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
nameValuePairs.add(new BasicNameValuePair("auth_user", "system"));
nameValuePairs.add(new BasicNameValuePair("auth_service", "iRewind"));
nameValuePairs.add(new BasicNameValuePair("device_make", android.os.Build.MANUFACTURER));
nameValuePairs.add(new BasicNameValuePair("device_model", android.os.Build.MODEL));
nameValuePairs.add(new BasicNameValuePair("device_osver", android.os.Build.VERSION.RELEASE));
nameValuePairs.add(new BasicNameValuePair("device_os", "android"));
nameValuePairs.add(new BasicNameValuePair("session_code", "0")); 
nameValuePairs.add(new BasicNameValuePair("device_uuid", Secure.getString(context.getContentResolver(),Secure.ANDROID_ID)));

	try {
		String raspuns=request.PostRequest(nameValuePairs, "irewindb", "/video-processor-secured/services/user/createGuestUser");
		Log.d("irw","response createGuestUser: "+raspuns);
		if (raspuns!=null&&!raspuns.equalsIgnoreCase("")){
		JSONObject j=new JSONObject(raspuns);
		String usr=j.getString("user");
		if (usr!=null&&!usr.equalsIgnoreCase("")){
		j2=new JSONObject(usr);
		
		}}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return j2;
	}
static String aux_status=null;
static Context aux_context=null;


public static void resetEmail(String email,Context context)   //TODO
{
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
nameValuePairs.add(new BasicNameValuePair("auth_user", "system"));
nameValuePairs.add(new BasicNameValuePair("email", email));
//nameValuePairs.add(new BasicNameValuePair("auth_service", auth_service));


	try {
		String raspuns=request.GetRequest(nameValuePairs, "irewindb", "/video-processor-secured/services/user/login");
		Log.d("irw","response bindService: "+raspuns);
		
	} catch (Exception e) {
	
		e.printStackTrace();
	}
	
	}


public static int userLogin(String auth_user, String auth_service, String auth_password,Context context)
{int ID=-1;
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
nameValuePairs.add(new BasicNameValuePair("auth_user", auth_user));
nameValuePairs.add(new BasicNameValuePair("auth_service", auth_service));


	try {
		String raspuns=request.GetRequest(nameValuePairs,auth_password, "/video-processor-secured/services/user/login");
		Log.d("irw","response bindService: "+raspuns);
		if (raspuns!=null&&!raspuns.equalsIgnoreCase("")){
			JSONObject j=new JSONObject(raspuns);
			String usr=j.getString("user");
			if (usr!=null&&!usr.equalsIgnoreCase("")){
				JSONObject	j2=new JSONObject(usr);
				ID=j2.getInt("id");
			}		}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return ID;
	}


public static JSONObject bindService(String auth_user, String auth_service, String auth_password, String new_user, String new_service, String new_password,Context context)
{JSONObject j2=null;
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
nameValuePairs.add(new BasicNameValuePair("auth_user", auth_user));
nameValuePairs.add(new BasicNameValuePair("auth_service", auth_service));
nameValuePairs.add(new BasicNameValuePair("email", new_user));
nameValuePairs.add(new BasicNameValuePair("service_name", new_service));
nameValuePairs.add(new BasicNameValuePair("new_password", new_password));

	try {
		String raspuns=request.PostRequest(nameValuePairs,auth_password, "/video-processor-secured/services/user/bindService");
		Log.d("irw","response bindService: "+raspuns);
		if (raspuns!=null&&!raspuns.equalsIgnoreCase("")){
		JSONObject j=new JSONObject(raspuns);
		String Status=j.getString("Status");
		aux_status=Status;
		aux_context=context;
	((Activity)context).runOnUiThread(new Runnable() {public void run() {
		Toast.makeText(aux_context, aux_status, Toast.LENGTH_SHORT).show();}});
	
		}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return j2;
	}


public static void bindDevice(String auth_user, String auth_service, String auth_password, String new_user, String new_service, String new_password,Context context)
{JSONObject j2=null;
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
nameValuePairs.add(new BasicNameValuePair("auth_user", auth_user));
nameValuePairs.add(new BasicNameValuePair("auth_service", auth_service));
nameValuePairs.add(new BasicNameValuePair("device_make", android.os.Build.MANUFACTURER));
nameValuePairs.add(new BasicNameValuePair("device_model", android.os.Build.MODEL));
nameValuePairs.add(new BasicNameValuePair("device_osver", android.os.Build.VERSION.RELEASE));
nameValuePairs.add(new BasicNameValuePair("device_os", "android"));
nameValuePairs.add(new BasicNameValuePair("device_uuid", Secure.getString(context.getContentResolver(),Secure.ANDROID_ID)));

try {
		String raspuns=request.PostRequest(nameValuePairs,auth_password, "/video-processor-secured/services/user/bindDevice");
		Log.d("irw","response bindService: "+raspuns);
		if (raspuns!=null&&!raspuns.equalsIgnoreCase("")){
		JSONObject j=new JSONObject(raspuns);
		String Status=j.getString("Status");
		aux_status=Status;
		aux_context=context;
	((Activity)context).runOnUiThread(new Runnable() {public void run() {
		
		
	}});
	
		}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	}



public static long TimeError(iUser user) 
{Log.d("request.TimeError", "enter");
try{	
List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
nameValuePairs.add(new BasicNameValuePair("auth_user", user.getUsername()));
nameValuePairs.add(new BasicNameValuePair("auth_service", user.getAuth_service()));
long t1=System.currentTimeMillis();
String r=GetRequest(nameValuePairs, user.getPassword(), "/video-processor-secured/services/utils/timestamp");
long t2= System.currentTimeMillis();
Log.d("irw",r);
Log.d("irw",t1+"");
JSONObject j1=new JSONObject(r);
long d1=Long.parseLong(j1.getString("timestamp"))-(long)((t1+t2)/2);

System.out.println("server_time="+j1.getString("timestamp"));
System.out.println("local time="+((t1+t2)/2));
System.out.println("delta="+d1);


List<NameValuePair> nameValuePairs2 = new ArrayList<NameValuePair>(2);
nameValuePairs2.add(new BasicNameValuePair("auth_user", user.getUsername()));
nameValuePairs2.add(new BasicNameValuePair("auth_service", user.getAuth_service()));
long at1=System.currentTimeMillis();
String ar=GetRequest(nameValuePairs2, user.getPassword(), "/video-processor-secured/services/utils/timestamp");
long at2= System.currentTimeMillis();
JSONObject aj1=new JSONObject(ar);
long ad1=Long.parseLong(aj1.getString("timestamp"))-(long)((at1+at2)/2);


List<NameValuePair> nameValuePairs3 = new ArrayList<NameValuePair>(2);
nameValuePairs3.add(new BasicNameValuePair("auth_user", user.getUsername()));
nameValuePairs3.add(new BasicNameValuePair("auth_service", user.getAuth_service()));
long bt1=System.currentTimeMillis();
String br=GetRequest(nameValuePairs3, user.getPassword(), "/video-processor-secured/services/utils/timestamp");
long bt2= System.currentTimeMillis();
JSONObject bj1=new JSONObject(br);
long bd1=Long.parseLong(bj1.getString("timestamp"))-(long)((bt1+bt2)/2);

Log.d("request.TimeError", "finish");
return (int) ((d1+ad1+bd1)/3);
}catch (Exception e){e.printStackTrace();
	return 0;}
}


public static int TimeError(String user,String auth_service, String password) throws Exception
{Log.d("request.TimeError", "enter");
	
List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
nameValuePairs.add(new BasicNameValuePair("auth_user", user));
nameValuePairs.add(new BasicNameValuePair("auth_service", auth_service));
long t1=System.currentTimeMillis();
String r=GetRequest(nameValuePairs, password, "/video-processor-secured/services/utils/timestamp");
long t2= System.currentTimeMillis();
Log.d("irw",r);
Log.d("irw",t1+"");
JSONObject j1=new JSONObject(r);
long d1=Long.parseLong(j1.getString("timestamp"))-(long)((t1+t2)/2);

System.out.println("server_time="+j1.getString("timestamp"));
System.out.println("local time="+((t1+t2)/2));
System.out.println("delta="+d1);


List<NameValuePair> nameValuePairs2 = new ArrayList<NameValuePair>(2);
nameValuePairs2.add(new BasicNameValuePair("auth_user", user));
nameValuePairs2.add(new BasicNameValuePair("auth_service", auth_service));
long at1=System.currentTimeMillis();
String ar=GetRequest(nameValuePairs2, password, "/video-processor-secured/services/utils/timestamp");
long at2= System.currentTimeMillis();
JSONObject aj1=new JSONObject(ar);
long ad1=Long.parseLong(aj1.getString("timestamp"))-(long)((at1+at2)/2);


List<NameValuePair> nameValuePairs3 = new ArrayList<NameValuePair>(2);
nameValuePairs3.add(new BasicNameValuePair("auth_user", user));
nameValuePairs3.add(new BasicNameValuePair("auth_service", auth_service));
long bt1=System.currentTimeMillis();
String br=GetRequest(nameValuePairs3, password, "/video-processor-secured/services/utils/timestamp");
long bt2= System.currentTimeMillis();
JSONObject bj1=new JSONObject(br);
long bd1=Long.parseLong(bj1.getString("timestamp"))-(long)((bt1+bt2)/2);

Log.d("request.TimeError", "finish");
return (int) ((d1+ad1+bd1)/3);
}

public static ContentValues getSesStatistics(String user,String auth_service,String password,String user_id,String session_id, String location_id, String web_address) throws Exception
{Log.d("request.getSesStatistics", "enter");
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
nameValuePairs.add(new BasicNameValuePair("auth_user", user));
nameValuePairs.add(new BasicNameValuePair("auth_service", auth_service));
nameValuePairs.add(new BasicNameValuePair("where_clause",("user_id="+user_id+" AND session_id="+session_id)));
nameValuePairs.add(new BasicNameValuePair("location_id", location_id));
String raspuns=GetRequest(nameValuePairs, password, "/video-processor-secured/services/user/gpsAnalytics",web_address);
Log.d("irw",raspuns);
ContentValues valori = new ContentValues();
JSONObject j=new JSONObject(raspuns);
String val=j.getString("analytics");
JSONObject js=new JSONObject(val);
long d=Long.parseLong(js.getString("duration"));
valori.put("duration",""+d/60000);
valori.put("distance",js.getString("distance").substring(0, js.getString("distance").indexOf(".")+2));
long dv = Long.valueOf(js.getString("start_time"));// its need to be in milisecond
Date df = new java.util.Date(dv);

String vv = new SimpleDateFormat("MM/dd, hh:mm").format(df);

valori.put("start_time",vv);
long dv2 = Long.valueOf(js.getString("stop_time"));// its need to be in milisecond
Date df2 = new java.util.Date(dv2);
valori.put("stop_time",new SimpleDateFormat("MM/dd, hh:mm").format(df2));
valori.put("average_speed",js.getString("average_speed"));
valori.put("user_id", user_id);
valori.put("session_id", session_id);
valori.put("location_id", location_id);
Log.d("request.getSesStatistics", "finish");
return valori;
}


static Context c;

public static void downloadWhenReady(iUser user,String session_id,String subsession_id,String location_id, Context context, String save_path, String server_adress)
{System.out.println("Download when ready <<>>");
String res_id=null;
while(res_id==null||res_id.equalsIgnoreCase("null"))	
try{	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
nameValuePairs.add(new BasicNameValuePair("auth_user", user.getUsername()));
nameValuePairs.add(new BasicNameValuePair("auth_service", user.getAuth_service()));
nameValuePairs.add(new BasicNameValuePair("location_id", location_id));
nameValuePairs.add(new BasicNameValuePair("user_id", user.getId()));
nameValuePairs.add(new BasicNameValuePair("session_id", session_id));

String raspuns=GetRequest(nameValuePairs, user.getPassword(), "/video-processor-secured/services/rest-sql/users/getSessions",server_adress);
Log.d("irw",raspuns);
JSONObject j=new JSONObject(raspuns);
for(int i=0;i<j.getJSONArray("results").getJSONObject(0).length();i++)
	if(j.getJSONArray("results").getJSONObject(i).getString("subsession_id").equalsIgnoreCase(subsession_id))
res_id=j.getJSONArray("results").getJSONObject(i).getString("full_res_id");
synchronized (context) {
	context.wait(10000);
}

}catch(Exception e){System.out.println("job not finished, retry");}
c=context;
((Activity)context).runOnUiThread(new Runnable() {
	
	@Override
	public void run() {
		Toast.makeText(c, "Your video has been generated! Go to HD tab to download it!", Toast.LENGTH_LONG).show();

		
	}
});
//download(res_id, context, server_adress, session_id, subsession_id, true, user.getUsername(), user.getAuth_service(), user.getId(), user.getPassword(), save_path);


}

public static void download( String res_id,Context context,String server_address, String session_id, String subsession_id, boolean hd, String user,String auth_service, String user_id, String password, String save_path) 
{//DownloadFile dl= new DownloadFile(context,in);
 //dl.execute(local_host+"/video-processor-secured/services/resource/"+res,nume,tip,pozitie+"",res);
 
DownloadClip dl=new DownloadClip(context, session_id, subsession_id, hd, user,auth_service, user_id, password, res_id, res_id, server_address, save_path);
dl.execute();
}

/*public static void download( String res,Context context,String nume,String tip, int pozitie) 
{DownloadFile dl= new DownloadFile(context);

 dl.execute(adresa+"/video-processor-secured/services/resource/"+res,nume,tip,pozitie+"");
 

}*/

/*public static String getUID(String user, String password)
{Log.d("request.getUID", "enter");
	String rez="";
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
nameValuePairs.add(new BasicNameValuePair("auth_user", user));
nameValuePairs.add(new BasicNameValuePair("first_index", "0"));
//nameValuePairs.add(new BasicNameValuePair("count", ""));
nameValuePairs.add(new BasicNameValuePair("sqlstatement", "SELECT id FROM irw_users where email='"+user+"'"));
try {
String s=request.PostRequest(nameValuePairs, password, "/video-processor-secured/services/utils/sql");
JSONObject j= new JSONObject(s);
String r= j.getString("resultset");
JSONArray j2=new JSONArray(r);
JSONObject j3=(JSONObject) j2.get(0);
rez=j3.getString("id");

} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
Log.d("request.getUID", "finish returning UID= "+rez);
return rez;
}
*/


public static int getUserAuthority(String user,String auth_service, String password, String user_id)
{Log.d("request.getUserAuthority", "enter. user_id="+user_id);
	String rez="";
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
nameValuePairs.add(new BasicNameValuePair("auth_user", user));
nameValuePairs.add(new BasicNameValuePair("auth_service", auth_service));
nameValuePairs.add(new BasicNameValuePair("first_index", "0"));
//nameValuePairs.add(new BasicNameValuePair("count", ""));
nameValuePairs.add(new BasicNameValuePair("sqlstatement", "SELECT * FROM irw_users_authorities where user_id='"+user_id+"'"));
try {
String s=request.PostRequest(nameValuePairs, password, "/video-processor-secured/services/utils/sql");
JSONObject j= new JSONObject(s);
String r= j.getString("resultset");
JSONArray j2=new JSONArray(r);
if(j2.length()>0){
JSONObject j3=(JSONObject) j2.get(0);
rez=j3.getString("authority_id");
}
} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
if (rez.equalsIgnoreCase("")) rez="-1";

Log.d("request.getUserAuthority", "finish. Authority= "+rez);
return Integer.parseInt(rez);
}

/*public static String getTicketTypeForCardId(String user, String password, String card_id)
{String rez="";
List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
nameValuePairs.add(new BasicNameValuePair("auth_user", user));
nameValuePairs.add(new BasicNameValuePair("first_index", "0"));
//nameValuePairs.add(new BasicNameValuePair("count", ""));
nameValuePairs.add(new BasicNameValuePair("sqlstatement", "SELECT max(session_id) FROM irw_users_logs where user_id="+user_id));
try {
String s=request.PostRequest(nameValuePairs, password, "/video-processor-secured/services/utils/sql");
JSONObject j= new JSONObject(s);
String r= j.getString("resultset");
JSONArray j2=new JSONArray(r);
JSONObject j3=(JSONObject) j2.get(0);
rez=j3.getString("max(session_id)");

} catch (Exception e) {
// TODO Auto-generated catch block
e.printStackTrace();
}

Log.d("request.getMaxSess", "finish. Max_ses="+rez);
return rez;}*/


public static String getMaxSess(String user,String auth_service, String password, String user_id)
{Log.d("request.getMaxSess", "enter. user_id="+user_id);
	String rez="";
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
nameValuePairs.add(new BasicNameValuePair("auth_user", user));
nameValuePairs.add(new BasicNameValuePair("auth_service", auth_service));
nameValuePairs.add(new BasicNameValuePair("first_index", "0"));
//nameValuePairs.add(new BasicNameValuePair("count", ""));
nameValuePairs.add(new BasicNameValuePair("sqlstatement", "SELECT max(session_id) FROM irw_users_logs where user_id="+user_id));
try {
String s=request.PostRequest(nameValuePairs, password, "/video-processor-secured/services/utils/sql");
JSONObject j= new JSONObject(s);
String r= j.getString("resultset");
JSONArray j2=new JSONArray(r);
JSONObject j3=(JSONObject) j2.get(0);
rez=j3.getString("max(session_id)");

} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}

Log.d("request.getMaxSess", "finish. Max_ses="+rez);
return rez;
}



public static String getLastError(String user,String auth_service,String user_id, String password, String sesiune, String subsesiune, String server_address)
{Log.d("request.getLastError", "enter. session="+sesiune+" subsession="+subsesiune);
	String rez="";
	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
nameValuePairs.add(new BasicNameValuePair("auth_user", user));
nameValuePairs.add(new BasicNameValuePair("auth_service", auth_service));
nameValuePairs.add(new BasicNameValuePair("first_index", "0"));
nameValuePairs.add(new BasicNameValuePair("sqlstatement", "SELECT last_error FROM irw_sessions where user_id="+user_id+" AND session_id="+sesiune+" AND subsession_id="+subsesiune));
try {
String s=request.PostRequest(nameValuePairs, password, "/video-processor-secured/services/utils/sql",server_address);
JSONObject j= new JSONObject(s);
String r= j.getString("resultset");
JSONArray j2=new JSONArray(r);
JSONObject j3=(JSONObject) j2.get(0);
rez=j3.getString("last_error");

} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
Log.d("request.getLastError", "finish. last_error="+rez);
return rez;
}



public static void FacebookUpload(String user,String auth_service,String password,String user_id,String session_id, String subsession_id, String acces_token,String location_id,Context context, String server_address, String full_res_id) {
Log.d("request.FacebookUpload", "enter");



	if(full_res_id==null||full_res_id.equalsIgnoreCase("")){
String raspuns=request.Compose(user,auth_service, password,user_id , location_id,session_id, subsession_id, "1280", "720", context, server_address);
   JSONObject j;
try {
	j = new JSONObject(raspuns);
String clip=j.getString("user_clip");
		JSONObject j2= new JSONObject(clip);
		full_res_id= j2.getString("id");}
	 catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();}}
		
		List<NameValuePair> nameValuePairs2 = new ArrayList<NameValuePair>(2);
		nameValuePairs2.add(new BasicNameValuePair("auth_user", user));
		nameValuePairs2.add(new BasicNameValuePair("auth_service", auth_service));
		nameValuePairs2.add(new BasicNameValuePair("resource_id", full_res_id));
		nameValuePairs2.add(new BasicNameValuePair("access_token",acces_token));
		try {
			request.PostRequest(nameValuePairs2, password, "/video-processor-secured/services/user/facebookUpload",server_address);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		


Log.d("request.FacebookUpload", "finish");

}




public static HttpClient getNewHttpClient() {
    try {
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);

        SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", sf, 443));

        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

        return new DefaultHttpClient(ccm, params);
    } catch (Exception e) {
        return new DefaultHttpClient();
    }
}

public static HttpClient getNewHttpClient(HttpParams params) {
    try {
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);

        SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

      
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", sf, 443));

        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

        return new DefaultHttpClient(ccm, params);
    } catch (Exception e) {
        return new DefaultHttpClient();
    }
}


//always verify the host - dont check for certificate
public static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
	public boolean verify(String hostname, SSLSession session) {
		Log.d("request.HostnameVerifier", "approving "+hostname);
		return true;
	}
};

/**
* Trust every server - dont check for any certificate
*/
public static void trustAllHosts() {
	// Create a trust manager that does not validate certificate chains
	TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return new java.security.cert.X509Certificate[] {};
		}

		public void checkClientTrusted(X509Certificate[] chain,
				String authType) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain,
				String authType) throws CertificateException {
		}
	} };

	// Install the all-trusting trust manager
	try {HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection
				.setDefaultSSLSocketFactory(sc.getSocketFactory());
	} catch (Exception e) {
		e.printStackTrace();
	}
}


public static boolean isApplicationSentToBackground(final Context context) {
    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    List<RunningTaskInfo> tasks = am.getRunningTasks(1);
    if (!tasks.isEmpty()) {
      ComponentName topActivity = tasks.get(0).topActivity;
      if (!topActivity.getPackageName().equals(context.getPackageName())) {
        return true;
      }
    }

    return false;
  }

}
 class NullHostNameVerifier implements HostnameVerifier {

    public boolean verify(String hostname, SSLSession session) {
       return true;
    }
}