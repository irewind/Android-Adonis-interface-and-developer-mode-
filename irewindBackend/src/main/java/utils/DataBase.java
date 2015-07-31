package utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.google.android.gms.maps.model.LatLng;
import irewindb.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import myClass.MyPoint;
import myClass.Ruta;
import myClass.iLocation;
import myClass.myCamera;
import myClass.myCard;
import myClass.mySegment;
import myClass.mySegmentResp;
import myClass.track;
import myClass.videoclip;
@SuppressWarnings("deprecation")
public class DataBase extends SQLiteOpenHelper {
	 private static final String NUME_BD = "baza";
SQLiteDatabase db;

	public DataBase(Context context) {
    	super(context, NUME_BD, null, 5);
    	db=getWritableDatabase();
    
	}
	
	
	

	
	
	
@Override
	public void onOpen(SQLiteDatabase db) {
db.execSQL("drop table if exists irw_cameras");		 //TODO
		
		
		
		
db.execSQL("CREATE TABLE IF NOT EXISTS versions (ver INTEGER NOT NULL)");


db.execSQL("CREATE TABLE IF NOT EXISTS irw_user_uploads (" +
		  "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
		  "path TEXT,"+
		  "thumb_path TEXT,"+
		  "upload_progress INTEGER NOT NULL)"); 


db.execSQL("CREATE TABLE IF NOT EXISTS irw_card_types (" +
  "id INTEGER NOT NULL,"+
  "name TEXT,"+
  "record_ttl NOT NULL, PRIMARY KEY(id))"); 

db.execSQL("CREATE TABLE IF NOT EXISTS cards (" +
		"card_id INTEGER NOT NULL," +
		"card_type INTEGER NOT NULL," +
		"comment TEXT," +
		"css TEXT," +
		"default_locale boolean," +
		"expiration_date INTEGER," +
		"html TEXT NOT NULL," +
		"language TEXT," +
		"location_id INTEGER," +
		"order_id INTEGER," +
		"start_date INTEGER, PRIMARY KEY(card_id, language))");


db.execSQL("CREATE TABLE IF NOT EXISTS irw_locations_gps ("
		+"location_id INTEGER NOT NULL,"
		+"point_id INTEGER NOT NULL,"
	    +"point_alt INTEGER,"
		+"point_lat TEXT,"
		+"point_long TEXT,  PRIMARY KEY (location_id,point_id))");

db.execSQL("CREATE TABLE IF NOT EXISTS irw_location_status ("
		+ "id INTEGER NOT NULL,"
		+ "status_text INTEGER NOT NULL, PRIMARY KEY (id))");


db.execSQL("CREATE TABLE IF NOT EXISTS trackuri ("
		+"user_id INTEGER NOT NULL,"
		+"location_id INTEGER NOT NULL,"
		+"session_id INTEGER NOT NULL,"
		+"max_speed INTEGER,"
		+"distance INTEGER,"
		+"average_speed INTEGER,"
		+"duration INTEGER,"
		+"start_time INTEGER,"
		+"stop_time INTEGER, PRIMARY KEY (user_id,location_id,session_id))");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS irw_users_logs ("
					+"user_id INTEGER NOT NULL,"
					+"location_id INTEGER NOT NULL,"
					+"session_id INTEGER NOT NULL,"
					+"subsession_id INTEGER,"
					+"point_id INTEGER NOT NULL,"
					+"point_time TEXT,"
					+"point_alt INTEGER,"
					+"point_lat TEXT,"
					+"point_long TEXT,"
					+"point_accuracy INTEGER,"
					+"point_heading REAL,"
					+"point_speed REAL,"
					+"sync INTEGER, PRIMARY KEY (user_id,location_id,session_id,point_id,point_time))"); //fara point_time ..l-am pus pentru ca erau puncte duplicate pe server
		 db.execSQL("CREATE INDEX IF NOT EXISTS irw_users_logs_1 ON irw_users_logs (sync ASC)");

		 db.execSQL("CREATE TABLE IF NOT EXISTS irw_users_clips ("
					+"location_id INTEGER NOT NULL,"
					+"user_id INTEGER NOT NULL,"
					+"job_id INTEGER,"
					+"clip_start INTEGER,"
					+"clip_end INTEGER,"
					+"clip_duration INTEGER,"
					+"clip_fps INTEGER,"
					+"clip_width INTEGER,"
					+"ticket_code TEXT,"
					+"deleted TEXT,"
					+"composed_with_interval TEXT,"
					+"clip_height REAL,"
					+"clip_codec TEXT,"
					+"res_id TEXT,"
					+"thumb_id TEXT,"
					+"expiration_date TEXT,"
					+"creation_date TEXT,"
					+"facebook_post_id TEXT,"
					+"facebook_is_uploaded TEXT,"
					+"size INTEGER,"
					+"intro_duration INTEGER,"
					+"trailer_duration INTEGER, PRIMARY KEY (location_id,user_id,job_id,res_id))");
		 db.execSQL("CREATE INDEX IF NOT EXISTS irw_users_clips_1 ON irw_users_clips (location_id ASC, clip_start ASC, clip_end ASC)");

		 db.execSQL("CREATE TABLE IF NOT EXISTS irw_locations ("
					+"id INTEGER NOT NULL,"
					+"name TEXT,"
					+"country TEXT,"
					+"town TEXT,"
					+"address TEXT,"
					+"web_url TEXT,"
					+"type_id INTEGER,"
					+"location_lat REAL,"
					+"location_long REAL,"
					+"location_alt INTEGER,"
					+"area_radius REAL,"
					+"location_area_max_shift REAL,"
					+"location_area_max_accuracy INTEGER,"
					+"location_area_min_interval INTEGER,"
					+"location_area_max_interval INTEGER,"
					+"area_radius_interval INTEGER,"
					+"outside_area_radius_interval INTEGER,"
					+"correction_algorithm INTEGER,"
					+"acceleration_gps REAL,"
					+"measurement_noise_gps REAL,"
					+"acceleration_noise_gps REAL,"
					+"deviation_limit REAL,"
					+"details TEXT, PRIMARY KEY (id))");

		 db.execSQL("CREATE TABLE IF NOT EXISTS irw_cameras ("
					+"location_id INTEGER NOT NULL,"
					+"cam_id INTEGER NOT NULL,"
					+"cam_alt INTEGER,"
					+"cam_lat REAL,"
					+"cam_long REAL,"
					+"cam_compas INTEGER,"
					+"cam_v1_p1_alt INTEGER,"
					+"cam_v1_p1_lat REAL,"
					+"cam_v1_p1_long REAL,"
					+"cam_v1_p2_alt INTEGER,"
					+"cam_v1_p2_lat REAL,"
					+"cam_v1_p2_long REAL,"
					+"cam_v1_p3_alt INTEGER,"
					+"cam_v1_p3_lat REAL,"
					+"cam_v1_p3_long REAL,"
					+"cam_v1_p4_alt INTEGER,"
					+"cam_v1_p4_lat REAL,"
					+"cam_v1_p4_long REAL,"
					+"cam_v2_p1_alt INTEGER,"
					+"cam_v2_p1_lat REAL,"
					+"cam_v2_p1_long REAL,"
					+"cam_v2_p2_alt INTEGER,"
					+"cam_v2_p2_lat REAL,"
					+"cam_v2_p2_long REAL,"
					+"cam_v2_p3_alt INTEGER,"
					+"cam_v2_p3_lat REAL,"
					+"cam_v2_p3_long REAL,"
					+"cam_v2_p4_alt INTEGER,"
					+"cam_v2_p4_lat REAL,"
					+"cam_v2_p4_long REAL,"
					+"cam_status TEXT,"
					+"cam_ip TEXT,"
					+"cam_model_id INTEGER,"
					+"cam_pan_angle INTEGER,"
					+"cam_tilt_angle INTEGER,"
					+"cam_zoom_step INTEGER,"
					+"cam_focus_step INTEGER,"
					+"cam_user TEXT,"
					+"cam_radius REAL,"
					+"cam_btn_orientation TEXT,"
					+"cam_password TEXT, PRIMARY KEY (location_id,cam_id))");

		 db.execSQL("CREATE TABLE IF NOT EXISTS irw_locations_routes ("
					+"location_id INTEGER NOT NULL,"
					+"route_id INTEGER NOT NULL,"
					+"route_name TEXT,"
					+"route_type TEXT,"
					+"route_color TEXT, PRIMARY KEY (location_id,route_id))");

		 db.execSQL("CREATE TABLE IF NOT EXISTS irw_locations_routes_gps ("
					+"location_id INTEGER NOT NULL,"
					+"route_id INTEGER NOT NULL,"
					+"point_id INTEGER NOT NULL,"
					+"point_lat REAL,"
					+"point_long REAL,"
					+"point_alt INTEGER, PRIMARY KEY (location_id,route_id,point_id))");

		 db.execSQL("CREATE TABLE IF NOT EXISTS irw_locations_pois ("
					+"location_id INTEGER NOT NULL,"
					+"poi_id INTEGER NOT NULL,"
					+"poi_lat REAL,"
					+"poi_long REAL,"
					+"poi_alt INTEGER,"
					+"poi_name TEXT,"
					+"poi_image TEXT,"
					+"poi_type INTEGER, PRIMARY KEY (location_id,poi_id))");
		
		 db.execSQL("CREATE TABLE IF NOT EXISTS irw_sessions ("
				    +"sport TEXT,"
					+"user_id INTEGER NOT NULL,"
					+"session_id INTEGER NOT NULL,"
					+"subsession_id INTEGER,"
					+"start_time INTEGER,"
					+"stop_time INTEGER,"
					+"location_id INTEGER,"
					+"device_id TEXT,"
					+"thumbnail_res_id TEXT,"
					+"preview_res_id TEXT,"
					+"full_res_id TEXT,"
					+"ticket_code TEXT,"
					+"edited INTEGER,"
					+"last_error TEXT,"
					+"demo_path TEXT,"
					+"hd_path TEXT,"
					+"facebook_is_uploaded INTEGER,"
					+"download_progress INTEGER,"  // 100 means download finished
					+"expiration_date TEXT, PRIMARY KEY (user_id,location_id,session_id,subsession_id))");
		 
		 db.execSQL("CREATE TABLE IF NOT EXISTS irw_camera_segments ("
				 +"sport TEXT,"
				 +"user_id INTEGER NOT NULL,"
				 +"session_id INTEGER NOT NULL,"
				 +"segm_path TEXT,"
				 +"location_id INTEGER,"
				 +"cam_id INTEGER,"
				 +"job_id INTEGER,"
			    +"segm_start TEXT,"
				 +"segm_end TEXT,"
				 +"segm_fps INTEGER,"
				 +"segm_width INTEGER,"
				 +"segm_height INTEGER,"
				 +"segm_codec TEXT,"
				 +"res_id TEXT,"	
				 +"segment_id,"
				 +"profile_id INTEGER, PRIMARY KEY (user_id,location_id,session_id,cam_id,segment_id))"
				 
				 );
		
		 
		 
		 
		 
	}

	
	public void updateField(String session_id, String subsession_id,String Field, String Value)
	{//db.execSQL("UPDATE 'irw_sessions' SET '"+Field+"'='"+Value+"' WHERE 'subsession_id'='"+subsession_id+"' AND 'session_id'='"+session_id+"'");
		
	try{
		ContentValues newValues= new ContentValues();
		newValues.put(Field,Value);

		String[] args = new String[]{session_id, subsession_id};
		db.update("irw_sessions", newValues, "session_id=? AND subsession_id=?", args);
	}catch(NullPointerException e){}
	}
	
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	
		db.execSQL("DROP TABLE irw_locations");
	}
	
	public void clearCards(int type_id)
	{
	db.delete("cards", "card_type=?", new String[] { type_id+""});
	}
	public void AddContentValues(ContentValues val,String tabel)
	{ try {
		
		db.replaceOrThrow(tabel, null, val);
		
	/*	if(tabel.equalsIgnoreCase("trackuri"))
		{MyApp app=(MyApp)mainActivity.getApplication();
		try {app.getTrackFragment().refresh();} catch (NullPointerException e) {}}*/
		
	} catch (SQLiteException e) {}catch(NullPointerException e){}
		}
	
	
	public void ClearErrorSessions()
	{//if(cursor.getString(0)!=null&&!cursor.getString(0).equalsIgnoreCase("")&&!cursor.getString(0).equalsIgnoreCase("null"))
		try {
			
		
		System.out.println("clearing sessions");	
		db.delete("irw_sessions", "length(last_error) > 0 AND last_error != 'null'", null);
		
		
		System.out.println("<<<<<<<<<<<remaining sessions:");
		subsessions("");
		} catch (NullPointerException e) {
			// TODO: handle exception
		}catch (IllegalStateException e) {
			// TODO: handle exception
		}
	}
	
	public void ClearTabel(String tabel)
	{try{
		db.delete(tabel, null, null);
	}catch(NullPointerException e){}
		}
	
	
	public void disableLock()
	{db.setLockingEnabled(false);}
	public void enableLock()
	{db.setLockingEnabled(true);}
	

	public int AddJson(String JsonArray, String tabel)
	{return AddJson(JsonArray, tabel, true);}
	
	public int AddJson(String JsonArray, String tabel, boolean replace)
	{try {
		InsertHelper ih = new InsertHelper(db, tabel);
		JSONArray names = new JSONArray();
		ArrayList<Integer> indexArray=new ArrayList<Integer>();
		
		Cursor ti = db.rawQuery("PRAGMA table_info("+tabel+")", null);
	    if ( ti.moveToFirst() ) {
	        do {names.put(ti.getString(1));
	        indexArray.add(ih.getColumnIndex(ti.getString(1)));
	        } while (ti.moveToNext());
	    }
		
		
	JSONArray Ja=new JSONArray(JsonArray);
	
	
	
	
	JSONArray values;
	db.beginTransaction();
	
	for(int i=0;i<Ja.length();i++)
	{try {
	if(replace)	ih.prepareForReplace();
	else ih.prepareForInsert();
	values=Ja.getJSONObject(i).toJSONArray(names);
	 
		 for(int t=0;t<values.length();t++)
		try{ ih.bind(indexArray.get(t), values.getString(t));} catch (JSONException e) {/*this occurs very often*/}
		 ih.execute();
	}catch(SQLiteConstraintException e)  {}
	}
	
	ih.close();
	db.setTransactionSuccessful();
	db.endTransaction();
	ti.close();
	return Ja.length();
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
		return 0;}
	
	
	
	
	public void addEmptySession(String session_id,String user_id, String location_id)
	{ try{
		ContentValues valori = new ContentValues();
	valori.put("session_id", session_id);
	valori.put("user_id",user_id);
	valori.put("subsession_id", "1");
	valori.put("last_error", "no subsessions");
	valori.put("location_id",location_id);
	try{
	db.replaceOrThrow("irw_sessions", null, valori);	
	} catch (SQLiteException e) {
	}
	}catch(NullPointerException e){}
	}
	
	
	
	
	
	
	
	
	public void addCustomSession(String sport,String session_id,String subsession_id,String user_id,String location_id, String prev_path)
	{ try{
		ContentValues valori = new ContentValues();
	valori.put("sport", sport);
	valori.put("demo_path",prev_path);
	valori.put("session_id", session_id);
	valori.put("user_id",user_id);
	valori.put("subsession_id", subsession_id);
	valori.put("last_error", "null");
	valori.put("location_id",location_id);
	valori.put("download_progress", 0);
try {
	

	db.replaceOrThrow("irw_sessions", null, valori);
	
	
} catch (SQLiteException e) {
	
}}catch(NullPointerException e){}
	}
	public void addCustomSession(String sport,String session_id,String subsession_id,String user_id,String location_id, String hd_path, String start_time, String full_res_id)
	{ try{ContentValues valori = new ContentValues();
	valori.put("sport", sport);
	valori.put("hd_path",hd_path);
	valori.put("session_id", session_id);
	valori.put("user_id",user_id);
	valori.put("subsession_id", subsession_id);
	valori.put("last_error", "null");
	valori.put("location_id",location_id);
	valori.put("start_time",start_time);
	valori.put("full_res_id",full_res_id);
	valori.put("download_progress", 0);
	try{
	db.replaceOrThrow("irw_sessions", null, valori);	
	} catch (SQLiteException e) {}
	
	}catch(NullPointerException e){}
	}
	
	 public void addCustomSegment(String sport,String session_id,String user_id,String location_id, String segm_path,String cam_id,String segm_start, String segm_end, int segment_id)
		{ 
			Cursor cursor =db.rawQuery("SELECT max(segment_id), count(segment_id) FROM  irw_camera_segments where session_id="+session_id+" and cam_id="+cam_id+" and user_id="+user_id, null);
		segment_id=0;
		 if (cursor != null)
	         {cursor.moveToFirst();
	         System.out.println("max = "+cursor.getInt(0)+" count = "+cursor.getInt(1));
	       
	         segment_id=cursor.getInt(1);
	         
	         }
		 cursor.close();
		
		
		 System.out.println("REAL SEGMENT ID = "+segment_id);
		 try{ContentValues valori = new ContentValues();
		valori.put("sport", sport);
		valori.put("segm_path",segm_path);
		valori.put("session_id", session_id);
		valori.put("user_id",user_id);

		valori.put("segment_id", segment_id);
			
		valori.put("location_id",location_id);
		
		valori.put("cam_id", cam_id);
		System.out.println("adding segment with segm_start="+segm_start);
		valori.put("segm_start",segm_start);
		valori.put("segm_end", segm_end);
		
		try{
		db.replaceOrThrow("irw_camera_segments", null, valori);	
		} catch (SQLiteException e) {}
		}catch(NullPointerException e){}}
	
	
	public int AddSessions(String Json, String tabel) throws JSONException
	{ try{
	
	db.beginTransaction();
	JSONArray Ja=new JSONArray(Json);
	JSONObject j;
	for (int i=0;i<Ja.length();i++)
	{j=Ja.getJSONObject(i);
	 ContentValues valori = new ContentValues();
    JSONArray nume=  j.names();
	for(int y=0;y<nume.length();y++)
	{
		valori.put(nume.getString(y),j.getString(nume.getString(y)));
	}
	
	try {
		db.insertOrThrow(tabel, null, valori);
	} catch (SQLiteConstraintException e) {}
	
    
    
	}
	db.setTransactionSuccessful();
	db.endTransaction();

	;
	return Ja.length();
	}catch(NullPointerException e){return 0;}
	}
	
	
	public void deleteSegment(String session_id, String cam_id, String segment_id)
	{System.out.println("<<< delete segment cam_id="+cam_id+" segment_id="+segment_id);
		db.delete("irw_camera_segments","session_id = "+session_id+" and cam_id="+cam_id+" and segment_id = "+segment_id,null);}

	public void updateDataBaseSegments(String session, ArrayList<ArrayList<mySegment>> segs, String user_id,String location_id)
	{	db.delete("irw_camera_segments", "session_id = "+session, null);

	for(int i=0;i<segs.size();i++)
	for(int j=0;j<segs.get(i).size();j++)
	{//System.out.println("i="+i+" j="+j);
		addCustomSegment("unknown", session, user_id, location_id, segs.get(i).get(j).path, segs.get(i).get(j).cam_id, segs.get(i).get(j).segm_start, segs.get(i).get(j).segm_end, j);
	}

		
	}

//         ^  ^
//        SETTERS 

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		
	}

	
//	         GETTERS 
//            \/  \/
	
	public ArrayList<Ruta> RutesLocations ()
	{ArrayList<Ruta> ar=new ArrayList<Ruta>();
		
	 Cursor cursor = db.query("irw_locations_routes", new String[] {"route_id","route_color" },null,null, null, null, null, null);
	 if (cursor != null)
         cursor.moveToFirst();
	for(int i=0;i<cursor.getCount();i++)
	{Ruta r= new Ruta();
	r.culoare=Integer.decode(cursor.getString(1));
	r.id=Integer.parseInt(cursor.getString(0));
	Cursor cursor2=db.query("irw_locations_routes_gps", new String[] { "point_lat","point_long" }, "route_id=?",
            new String[] { cursor.getString(0) }, null, null, null, null);
	 if (cursor2 != null)
         cursor2.moveToFirst();   
	 for(int j=0;j<cursor2.getCount();j++)
	 {  r.puncte.add(new LatLng(Double.parseDouble(cursor2.getString(0)), Double.parseDouble(cursor2.getString(1))));
	    cursor2.moveToNext();}
	 cursor2.close();
	 ar.add(r);
	 cursor.moveToNext();
		
	}
	cursor.close();
	;
	return ar;
	}
	
	
	public ArrayList<LatLng> CamerasLocations()
	{ArrayList<LatLng> p=new ArrayList<LatLng>();
	
	 Cursor cursor = db.query("irw_cameras", new String[] {"cam_lat","cam_long" },null,null, null, null, null, null);
	 if (cursor != null)
        cursor.moveToFirst();
	for(int i=0;i<cursor.getCount();i++)
	{p.add(new LatLng(Double.parseDouble(cursor.getString(0)),Double.parseDouble(cursor.getString(1))));
	cursor.moveToNext();}
	cursor.close();
	;
	return p;}
	
	public mySegmentResp getCameraSegment(String session, String user_id, String time_stamp, int cam_id, boolean fixed_camera, ArrayList<Integer> selected_cameras)
	{mySegmentResp rsp=null;
	try{ Cursor cursor = db.query("irw_camera_segments", new String[] {"segm_start","cam_id", "segment_id","segm_path", "segm_end" },"session_id="+session+" and user_id ="+user_id+" and "+time_stamp+"> segm_start and segm_end > "+time_stamp+" and cam_id="+cam_id ,null, null, null, "segm_start", null);
		 if (cursor != null)
		 {	if(cursor.getCount()>0){
			 cursor.moveToLast();
		rsp=new mySegmentResp(cursor.getInt(1), cursor.getInt(2),Long.parseLong(time_stamp)-Long.parseLong(cursor.getString(0)), cursor.getLong(0),cursor.getLong(4),cursor.getString(3));
		 } cursor.close();}
	if(rsp==null&&!fixed_camera)
	{
		Cursor cursor2 = db.query("irw_camera_segments", new String[] {"segm_start","cam_id", "segment_id","segm_path","segm_end" },"session_id="+session+" and user_id ="+user_id+" and "+time_stamp+"> segm_start and segm_end > "+time_stamp ,null, null, null, "segm_start", null);
		 if (cursor2 != null)
		 {	if(cursor2.getCount()>0){
		boolean ok=false;	
			 cursor2.moveToFirst();
			ok=!selected_cameras.contains(cursor2.getInt(1));
		//	System.out.println("--1");
		while(!ok&&cursor2.moveToNext())	 
		{//System.out.println("--2");
			ok=!selected_cameras.contains(cursor2.getInt(1));}
		//System.out.println("ok="+ok+" camera="+cursor2.getInt(1));
	/*	System.out.print("Selected cameras: ");
		for(int i=0;i<selected_cameras.size();i++)
			System.out.print(selected_cameras.get(i)+" ");
		System.out.println();
		System.out.println("contains="+selected_cameras.contains(cursor2.getInt(1)));*/
  if(ok) rsp=new mySegmentResp(cursor2.getInt(1), cursor2.getInt(2), Long.parseLong(time_stamp)-Long.parseLong(cursor2.getString(0)), cursor2.getLong(0),cursor2.getLong(4),cursor2.getString(3));
		 } cursor2.close();}
		
		
	}}catch(Exception e){System.out.println("db error");}
		 
			return rsp;
	}
	
	public ArrayList<mySegmentResp> getAvailableCameraSegments(String session, String user_id, String time_stamp)
	{
		
//		Log.i("dataBase","selected segments: "+ time_stamp);
		ArrayList<mySegmentResp> rsp=new ArrayList<mySegmentResp>();
		 Cursor cursor = db.query("irw_camera_segments", new String[] {"segm_start","cam_id", "segment_id", "segm_path","segm_end" },"session_id="+session+" and user_id ="+user_id+" and "+time_stamp+"> segm_start and segm_end > "+time_stamp ,null, null, null, null, null);
		 if (cursor != null)
		        cursor.moveToFirst();
		 for(int i=0;i<cursor.getCount();i++)
			{rsp.add(new mySegmentResp(cursor.getInt(1)-1, cursor.getInt(2), Long.parseLong(time_stamp)-Long.parseLong(cursor.getString(0)), cursor.getLong(0),cursor.getLong(4),cursor.getString(3)));
	//		 Log.i("dataBase",i+". "+(cursor.getInt(1)-1)+"-"+cursor.getInt(2)+":"+cursor.getString(0)+":"+cursor.getString(3)+" toSeek="+ (Long.parseLong(time_stamp)-Long.parseLong(cursor.getString(0))))	; 
			 cursor.moveToNext();}
			cursor.close();
			
			return rsp;
	}
	
	public long getMaxTimeStamp(String session, String user_id)
	{long r=-1;
		Cursor c=db.query("irw_camera_segments", new String[]{"max(segm_end)"}, "session_id="+session+" and user_id ="+user_id, null, "session_id, user_id", null, null);
		if(c!=null&&c.getCount()>0)
			{c.moveToFirst();
		r= c.getLong(0);
		c.close();}
		return r;}
	
	public long getMinTimeStamp(String session, String user_id)
	{long r=-1;
		Cursor c=db.query("irw_camera_segments", new String[]{"min(segm_start)"}, "session_id="+session+" and user_id ="+user_id, null, "session_id, user_id", null, null);
		if(c!=null&&c.getCount()>0)
			{c.moveToFirst();
		r= c.getLong(0);
		c.close();}
		return r;}

	public ArrayList<LatLng> getLocationGPSforTrackDebugging(String location_id)
	{ArrayList<LatLng> result=new ArrayList<LatLng>();
	 Cursor cursor = db.query("irw_locations_gps", new String[] {"point_lat","point_long" },"location_id="+location_id,null, null, null, null, null);
	 if (cursor != null)
        cursor.moveToFirst();
	 LatLng first=new LatLng(Double.parseDouble(cursor.getString(0)),Double.parseDouble(cursor.getString(1)));
	for(int i=0;i<cursor.getCount();i++)
	{
	
	result.add(new LatLng(Double.parseDouble(cursor.getString(0)),Double.parseDouble(cursor.getString(1))));
	cursor.moveToNext();}
	cursor.close();
	result.add(first);  //to close the poligon
	return result;}
	
	
	public ArrayList<myCamera> getCameras(String location_id)
	{ArrayList<myCamera>  result=new ArrayList<myCamera> ();
	 Cursor cursor = db.query("irw_cameras", new String[] {"cam_lat","cam_long","cam_radius","cam_id" },"location_id="+location_id,null, null, null, null, null);
	 if (cursor != null)
        cursor.moveToFirst();
	for(int i=0;i<cursor.getCount();i++)
	{myCamera cam=new myCamera(cursor.getInt(3),Double.parseDouble(cursor.getString(0)),Double.parseDouble(cursor.getString(1)),Double.parseDouble(cursor.getString(2)));
	
	result.add(cam);
	cursor.moveToNext();}
	cursor.close();
	return result;}
	
	
	public ArrayList<ArrayList<LatLng>> getLocationCamerasforTrackDebuggingV1(String location_id)
	{ArrayList<ArrayList<LatLng>>  result=new ArrayList<ArrayList<LatLng>> ();
	 Cursor cursor = db.query("irw_cameras", new String[] {"cam_v1_p1_lat","cam_v1_p1_long","cam_v1_p2_lat","cam_v1_p2_long","cam_v1_p3_lat","cam_v1_p3_long","cam_v1_p4_lat","cam_v1_p4_long" },"location_id="+location_id,null, null, null, null, null);
	 if (cursor != null)
        cursor.moveToFirst();
	for(int i=0;i<cursor.getCount();i++)
	{ArrayList<LatLng> cam=new ArrayList<LatLng>();
	cam.add(new LatLng(Double.parseDouble(cursor.getString(0)),Double.parseDouble(cursor.getString(1))));
	cam.add(new LatLng(Double.parseDouble(cursor.getString(2)),Double.parseDouble(cursor.getString(3))));
	cam.add(new LatLng(Double.parseDouble(cursor.getString(4)),Double.parseDouble(cursor.getString(5))));
	cam.add(new LatLng(Double.parseDouble(cursor.getString(6)),Double.parseDouble(cursor.getString(7))));
	cam.add(new LatLng(Double.parseDouble(cursor.getString(0)),Double.parseDouble(cursor.getString(1))));
	result.add(cam);
	cursor.moveToNext();}
	cursor.close();
	return result;}
	
	public ArrayList<ArrayList<LatLng>> getLocationCamerasforTrackDebuggingV2(String location_id)
	{ArrayList<ArrayList<LatLng>>  result=new ArrayList<ArrayList<LatLng>> ();
	 Cursor cursor = db.query("irw_cameras", new String[] {"cam_v2_p1_lat","cam_v2_p1_long","cam_v2_p2_lat","cam_v2_p2_long","cam_v2_p3_lat","cam_v2_p3_long","cam_v2_p4_lat","cam_v2_p4_long" },"location_id="+location_id,null, null, null, null, null);
	System.out.println("cursor size="+cursor.getCount());
	 if (cursor != null)
        cursor.moveToFirst();
	for(int i=0;i<cursor.getCount();i++)
	{ArrayList<LatLng> cam=new ArrayList<LatLng>();
	cam.add(new LatLng(Double.parseDouble(cursor.getString(0)),Double.parseDouble(cursor.getString(1))));
	cam.add(new LatLng(Double.parseDouble(cursor.getString(2)),Double.parseDouble(cursor.getString(3))));
	cam.add(new LatLng(Double.parseDouble(cursor.getString(4)),Double.parseDouble(cursor.getString(5))));
	cam.add(new LatLng(Double.parseDouble(cursor.getString(6)),Double.parseDouble(cursor.getString(7))));
	cam.add(new LatLng(Double.parseDouble(cursor.getString(0)),Double.parseDouble(cursor.getString(1))));
	result.add(cam);
	cursor.moveToNext();}
	cursor.close();
	return result;}

	
	public ArrayList<iLocation> LocationsPoints()
	{ArrayList<iLocation> p=new ArrayList<iLocation>();
	
	 Cursor cursor = db.query("irw_locations_pois", new String[] {"poi_lat","poi_long","poi_image","poi_name" },null,null, null, null, null, null);
	 if (cursor != null)
        cursor.moveToFirst();
	for(int i=0;i<cursor.getCount();i++)
	{iLocation l=new iLocation();
	l.l=new LatLng(Double.parseDouble(cursor.getString(0)),Double.parseDouble(cursor.getString(1)));
	l.imagine=cursor.getString(2);
	l.setLocation_name(cursor.getString(3));
		p.add(l);
	cursor.moveToNext();}
	cursor.close();
	;
	return p;}

	
	
	
	
	
	public ArrayList<iLocation> Locations()
	{ArrayList<iLocation> p=new ArrayList<iLocation>();
	
	 Cursor cursor ;
	
		cursor =db.rawQuery("SELECT location_lat,location_long,name,web_url,irw_locations.id,type_id, area_radius,min(irw_locations_gps.point_lat),max(irw_locations_gps.point_lat),min(irw_locations_gps.point_long),max(irw_locations_gps.point_long),location_area_max_accuracy,location_area_min_interval,location_area_max_interval,area_radius_interval,outside_area_radius_interval  FROM  irw_location_status ,irw_locations left join irw_locations_gps on  irw_locations.id=irw_locations_gps.location_id where irw_locations.id=irw_location_status.id and status_text = 'ON' group by irw_locations.id", null);
	 
	 if (cursor != null)
        cursor.moveToFirst();
	for(int i=0;i<cursor.getCount();i++)
	{iLocation l=new iLocation();
	
	l.lat=Double.parseDouble(cursor.getString(0));
	l.lng=Double.parseDouble(cursor.getString(1));
	l.l=new LatLng(Double.parseDouble(cursor.getString(0)),Double.parseDouble(cursor.getString(1)));
	
	l.setLocation_name(cursor.getString(2));
	l.setLocation_url(cursor.getString(3));
	l.setLocation_id(cursor.getString(4));
	l.setLocation_type(cursor.getString(5));
	l.area_radius=Integer.parseInt(cursor.getString(6));
	try {l.minLat=Double.parseDouble(cursor.getString(7));} catch (NullPointerException e) {}
	try {l.maxLat=Double.parseDouble(cursor.getString(8));} catch (NullPointerException e) {}
	try {l.minLng=Double.parseDouble(cursor.getString(9));} catch (NullPointerException e) {}
	try {l.maxLng=Double.parseDouble(cursor.getString(10));} catch (NullPointerException e) {}
	
	try {l.max_accuracy=Integer.parseInt(cursor.getString(11));} catch (NullPointerException e) {}
	try {l.location_area_min_interval=Integer.parseInt(cursor.getString(12));} catch (NullPointerException e) {}
	try {l.location_area_max_interval=Integer.parseInt(cursor.getString(13));} catch (NullPointerException e) {}
	try {l.area_radius_interval=Integer.parseInt(cursor.getString(14));} catch (NullPointerException e) {}
	try {l.outside_area_radius_interval=Integer.parseInt(cursor.getString(15));} catch (NullPointerException e) {}
	
	l.location_gps=getLocationGps(l.getLocation_id());
	
	p.add(l);
	cursor.moveToNext();}
	cursor.close();
	;
	return p;}
	
	
	
	
	ArrayList<LatLng> getLocationGps(String location_id)
	{ArrayList<LatLng> locationGps=new ArrayList<LatLng>();
	Cursor cursor ;
	
	cursor =db.rawQuery("SELECT point_lat,point_long FROM irw_locations_gps where location_id="+location_id, null);
 
 if (cursor != null)
    cursor.moveToFirst();	
 for(int i=0;i<cursor.getCount();i++)
	{locationGps.add(new LatLng(Double.parseDouble(cursor.getString(0)), Double.parseDouble(cursor.getString(1))));
	 
	 cursor.moveToNext();}
	cursor.close();
	return locationGps;
		
	}
	


public ArrayList<track> SimpleTrackList()
{ArrayList<track> p=new ArrayList<track>();

 Cursor cursor = db.query("irw_users_logs", new String[] {"session_id","location_id"},null,null, "session_id", null, null, null);
 if (cursor != null)
    cursor.moveToFirst();
for(int i=0;i<cursor.getCount();i++)
{track t=new track();
t.ses_id=cursor.getString(0);
t.location_id=cursor.getString(1);
t.name="Track "+t.ses_id;
	p.add(t);
cursor.moveToNext();}
cursor.close();
;
return p;}



public ArrayList<track> DetailedTrackList()
{ArrayList<track> p=new ArrayList<track>();

 Cursor cursor = db.query("trackuri", new String[] {"session_id","max_speed","distance","start_time","user_id"},null,null, null, null, null, null);
 if (cursor != null)
    cursor.moveToFirst();
for(int i=0;i<cursor.getCount();i++)
{track t=new track();
t.ses_id=cursor.getString(0);
t.max_speed=cursor.getString(1);
t.distance=cursor.getString(2);
t.start_time=cursor.getString(3);
t.name="Track "+t.ses_id;
	p.add(t);
cursor.moveToNext();}
cursor.close();
;
return p;}


public track trackStats(String sesiune, String user_id)
{

 Cursor cursor = db.query("trackuri", new String[] {"session_id","max_speed","distance","start_time","stop_time","duration","average_speed"},"session_id=? AND user_id=?",new String[] { sesiune,user_id }, null, null, null, null);
 if (cursor != null)
    cursor.moveToFirst();
track t=new track();
t.ses_id=cursor.getString(0);
t.max_speed=cursor.getString(1);
t.distance=cursor.getString(2);
t.start_time=cursor.getString(3);
t.end_time=cursor.getString(4);
t.duration=cursor.getString(5);
t.name="Track "+t.ses_id;
t.avg_speed=cursor.getString(6);

cursor.close();
return t;}


public ArrayList<myCard> CardList(int type_id)
{ArrayList<myCard> p=new ArrayList<myCard>();


 Cursor cursor = db.query("cards", new String[] {"card_id","html","css","order_id","expiration_date","start_date"},"card_type="+type_id+" AND location_id=0",null, null, null, "order_id", null);
 if (cursor != null)
    cursor.moveToFirst();
for(int i=0;i<cursor.getCount();i++)
{myCard c=new myCard();
c.id=Integer.parseInt(cursor.getString(0));
c.html=cursor.getString(1);
c.css=cursor.getString(2);
c.order=Integer.parseInt(cursor.getString(3));


	p.add(c);
cursor.moveToNext();}
cursor.close();
;
return p;}


public ArrayList<myCard> CardList(int type_id, String location_id)
{ArrayList<myCard> p=new ArrayList<myCard>();


 Cursor cursor = db.query("cards", new String[] {"card_id","html","css","order_id","expiration_date","start_date"},"card_type="+type_id+" AND (location_id="+location_id+" OR location_id=0 )",null, null, null, "order_id", null);
 if (cursor != null)
    cursor.moveToFirst();
for(int i=0;i<cursor.getCount();i++)
{myCard c=new myCard();
c.id=Integer.parseInt(cursor.getString(0));
c.html=cursor.getString(1);
c.css=cursor.getString(2);
c.order=Integer.parseInt(cursor.getString(3));


	p.add(c);
cursor.moveToNext();}
cursor.close();
;
return p;}


public videoclip subsession(String ses_id,String subsess)
{

 Cursor cursor = db.query("irw_sessions", new String[] {"session_id","start_time","stop_time","subsession_id","last_error","preview_res_id","full_res_id","demo_path","hd_path","expiration_date","thumbnail_res_id","sport" },"session_id=? AND subsession_id=?",new String[] { ses_id,subsess }, null, null, null, null);
 if (cursor != null)
    cursor.moveToFirst();

videoclip v=new videoclip();
v.ses_id=cursor.getString(0);
if(cursor.getString(1)!=null){
long dv = Long.valueOf(cursor.getString(1));// its need to be in milisecond
Date df = new java.util.Date(dv);

String vv = new SimpleDateFormat("dd/MM/yyyy").format(df);
v.start_time=vv;
v.ora=new SimpleDateFormat("hh:mm:ss").format(df);
v.end_time=cursor.getString(2);}
v.subsession_id=cursor.getString(3);
v.last_error=cursor.getString(4);
v.preview_res=cursor.getString(5);
v.full_res=cursor.getString(6);

v.nume="Film "+v.ses_id+" Edit "+v.subsession_id;
v.demo_path=cursor.getString(7);
v.hd_path=cursor.getString(8);

v.thumb_res=cursor.getString(10);
if(cursor.getString(9)!=null&&!cursor.getString(9).equalsIgnoreCase("")&&!cursor.getString(9).equalsIgnoreCase("null")){


SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
Date date;
try {
	date = dateFormat.parse(cursor.getString(9));

long d1=date.getTime();
Calendar today = Calendar.getInstance();
long d2=today.getTimeInMillis();
int day = (int)((d1-d2)/86400000);
v.expiration_date=day+"";} 
catch (ParseException e) {
	v.expiration_date=cursor.getString(9);
	
}}
else v.expiration_date="null";
//if(v.last_error.equalsIgnoreCase("User has no tickets"))v.expiration_date="Click to add ticket";
cursor.close();
return v;}




public ArrayList<ArrayList<mySegment>> SegmentsInSessionFromFile(String session, String user_id)
{ArrayList<ArrayList<mySegment>> segs=new ArrayList<ArrayList<mySegment>>();
String location_id="999";
File directory=new File(Environment.getExternalStorageDirectory().toString()
					+ "/irewindb/irw_" +session);
String[] files = directory.list();
System.out.println("here tag");
for (int i=0;i<files.length;i++)
{String[] comp= files[i].split("[-,\\.]");
if(comp.length==6)	
{mySegment seg=new mySegment();
seg.path=directory.getAbsolutePath()+"/"+files[i];
seg.cam_id=comp[1];
seg.segm_start=comp[3]; 
seg.segm_end=comp[4];
seg.location_id=comp[0];

System.out.println("adding segment cam_id="+seg.cam_id);

if(seg.location_id!=null) location_id=seg.location_id;

while(segs.size()<=Integer.parseInt(seg.cam_id)) segs.add(new ArrayList<mySegment>());
if(segs.get(Integer.parseInt(seg.cam_id)).size()>0){
double over=Double.parseDouble(segs.get(Integer.parseInt(seg.cam_id)).get(segs.get(Integer.parseInt(seg.cam_id)).size()-1).segm_end)-Double.parseDouble(seg.segm_start);
seg.start_overlap=over;
segs.get(Integer.parseInt(seg.cam_id)).get(segs.get(Integer.parseInt(seg.cam_id)).size()-1).end_overlap=over;
}
segs.get(Integer.parseInt(seg.cam_id)).add(seg);
}




}

for(int i=0;i<segs.size();i++)
	 if(segs.get(i).size()==0)
		 segs.remove(i);

System.out.println("segs.size="+segs.size());

for(int i=0;i<segs.size();i++)
{System.out.println("----");
	 for(int j=0;j<segs.get(i).size();j++)
	 {  System.out.print("i= "+i+" j= "+j+"  ");
		 System.out.println(segs.get(i).get(j).cam_id); }
}

updateDataBaseSegments(session, segs, user_id, location_id);

return segs;
}


public ArrayList<ArrayList<mySegment>> SegmentsInSession(String session)
{ArrayList<ArrayList<mySegment>> segs=new ArrayList<ArrayList<mySegment>>();


	
	Cursor cursor = db.query("irw_camera_segments", new String[] {"segm_path","cam_id","segm_start","segm_end","location_id" },"session_id=?",new String[] { session }, null, null, null, null);
 if (cursor != null)
    cursor.moveToFirst();

 
for(int i=0;i<cursor.getCount();i++)
 {mySegment seg=new mySegment();
 seg.path=cursor.getString(0);
 seg.cam_id=cursor.getString(1);
 seg.segm_start=cursor.getString(2); 
 seg.segm_end=cursor.getString(3);
 
 seg.location_id=cursor.getString(4);
 System.out.println("path="+seg.path);
 System.out.println("cam_id="+seg.cam_id);
 while(segs.size()<=Integer.parseInt(seg.cam_id)) segs.add(new ArrayList<mySegment>());
 if(segs.get(Integer.parseInt(seg.cam_id)).size()>0){
 double over=Double.parseDouble(segs.get(Integer.parseInt(seg.cam_id)).get(segs.get(Integer.parseInt(seg.cam_id)).size()-1).segm_end)-Double.parseDouble(seg.segm_start);
 seg.start_overlap=over;
 segs.get(Integer.parseInt(seg.cam_id)).get(segs.get(Integer.parseInt(seg.cam_id)).size()-1).end_overlap=over;
 }
 segs.get(Integer.parseInt(seg.cam_id)).add(seg);
 cursor.moveToNext(); 
 }
 cursor.close();
 
 for(int i=0;i<segs.size();i++)
 if(segs.get(i).size()==0)
	 segs.remove(i);
 
 
 
 return segs;
 
}


public ArrayList<videoclip> LogSubsessions()
{ArrayList<videoclip> p=new ArrayList<videoclip>();
	
	 System.out.println("grouped by:");
	 Cursor cursor2=null;
	 try{
		  cursor2 = db.query("irw_users_logs", new String[] {"session_id","subsession_id", "min(point_time)","max(point_time)","location_id"},null,null, "session_id", null, null);

				
	} catch(SQLiteException e) {}
		 if (cursor2 != null){
		    cursor2.moveToFirst();
		    System.out.println("size="+cursor2.getCount());
		for(int i=0;i<cursor2.getCount();i++)
		{videoclip v=new videoclip();
		System.out.println(cursor2.getString(0)+"--"+cursor2.getString(1)+"--"+cursor2.getString(2)+"--"+cursor2.getString(3));
		v.ses_id=cursor2.getString(0);
		v.subsession_id=cursor2.getString(1);
		v.start_time=cursor2.getString(2);
		v.end_time=cursor2.getString(3);
		v.location=cursor2.getString(4);
		p.add(v);
		cursor2.moveToNext();
		}
		 }
	 
	cursor2.close();
	return p;}


public int getMoviesCount()
{int result=0;

Cursor cursor=null;
try{
  cursor = db.query("irw_sessions", new String[] {"session_id","start_time","stop_time","subsession_id","last_error","preview_res_id","full_res_id","demo_path","hd_path","expiration_date","thumbnail_res_id","sport","location_id" , "download_progress","user_id"},"preview_res_id is not null or demo_path is not null or full_res_id is not null or hd_path is not null",null, null, null, null, null);
result = cursor.getCount();
} catch(SQLiteException e) {} catch (IllegalStateException e ){}

cursor.close();
return result;}

public int getHDCount()
{int result=0;

Cursor cursor=null;
try{
  cursor = db.query("irw_sessions", new String[] {"session_id","start_time","stop_time","subsession_id","last_error","preview_res_id","full_res_id","demo_path","hd_path","expiration_date","thumbnail_res_id","sport","location_id" , "download_progress","user_id"},"full_res_id is not null or hd_path is not null",null, null, null, null, null);
result = cursor.getCount();
} catch(SQLiteException e) {}
cursor.close();
return result;}

public int getLocationsCount()
{int result=0;

Cursor cursor=null;
try{
  cursor = db.query("irw_sessions", new String[] {"session_id","start_time","stop_time","subsession_id","last_error","preview_res_id","full_res_id","demo_path","hd_path","expiration_date","thumbnail_res_id","sport","location_id" , "download_progress","user_id"},null,null, "location_id", null, null, null);
result = cursor.getCount();
} catch(SQLiteException e) {}
cursor.close();
return result;}


public ArrayList<videoclip> subsessions(String Sport, boolean edited, ArrayList<iLocation> locations)
{return subsessions(Sport, edited, locations, false);}
public ArrayList<videoclip> subsessions(String Sport)
{return subsessions(Sport, false, null, true);}


//videoclip video_aux=null;
private ArrayList<videoclip> subsessions(String Sport, boolean edited, ArrayList<iLocation> locations,  boolean all)
{ArrayList<videoclip> p=new ArrayList<videoclip>();


Cursor cursor=null;
try{if(all) cursor = db.query("irw_sessions", new String[] {"session_id","start_time","stop_time","subsession_id","last_error","preview_res_id","full_res_id","demo_path","hd_path","expiration_date","thumbnail_res_id","sport","location_id" , "download_progress","user_id"},null,null, null, null, null, null);

else if(edited)
  cursor = db.query("irw_sessions", new String[] {"session_id","start_time","stop_time","subsession_id","last_error","preview_res_id","full_res_id","demo_path","hd_path","expiration_date","thumbnail_res_id","sport","location_id" , "download_progress","user_id"},"full_res_id IS NOT NULL and full_res_id!='null'",null, null, null, null, null);
	else cursor = db.query("irw_sessions", new String[] {"session_id","start_time","stop_time","subsession_id","last_error","preview_res_id","full_res_id","demo_path","hd_path","expiration_date","thumbnail_res_id","sport","location_id" , "download_progress","user_id"},"preview_res_id IS NOT NULL and preview_res_id!='null'" /*and (full_res_id = 'null')*/,null, null, null, null, null);

} catch(SQLiteException e) {e.printStackTrace();}
 if (cursor != null){
	 System.out.println("cursor size="+cursor.getCount());
    cursor.moveToFirst();
for(int i=0;i<cursor.getCount();i++)
{System.out.println("full_res="+cursor.getString(6));
	videoclip v=new videoclip();
v.ses_id=cursor.getString(0);
v.user_id=cursor.getString(14);
if(cursor.getString(1)!=null){
long dv = Long.valueOf(cursor.getString(1));// its need to be in milisecond
Date df = new java.util.Date(dv);

String vv = new SimpleDateFormat("dd/MM/yyyy").format(df);
v.start_time=vv;
v.ora=new SimpleDateFormat("hh:mm:ss").format(df);
v.end_time=cursor.getString(2);}
v.subsession_id=cursor.getString(3);
v.preview_res=cursor.getString(5);
v.full_res=cursor.getString(6);
v.demo_path=cursor.getString(7);
v.hd_path=cursor.getString(8);
v.location=cursor.getString(12);
//.println("all="+all+" edited="+edited);

//System.out.println("uid="+cursor.getString(14)+" ses="+v.ses_id+" subsess"+v.subsession_id+ " full_res= "+v.full_res+ " last_error="+cursor.getString(4));
//System.out.println("demo="+v.demo_path+" hd="+v.hd_path);
//if(v.demo_path==null&&v.hd_path==null&&((edited&&Integer.parseInt(v.subsession_id)>1)||(!edited&&Integer.parseInt(v.subsession_id)==1))||(edited&&v.hd_path!=null)||(!edited&&v.demo_path!=null)||all)

//if(all||(!edited&&(v.hd_path==null||v.hd_path.equalsIgnoreCase("multi_view")))||(edited&&(v.hd_path!=null&&!v.hd_path.equalsIgnoreCase("multi_view"))))
{
v.last_error=cursor.getString(4);


//if(cursor.getString(11).equalsIgnoreCase("Tenis")&&!v.subsession_id.equalsIgnoreCase("1")) v.nume="Tenis " +v.ses_id+ " cut "+(-1+Integer.parseInt(v.subsession_id));
//else v.nume=cursor.getString(11)+" "+v.ses_id;

String location_name="irw";

if(locations!=null)
for(int ii=0;ii<locations.size();ii++)
	if(locations.get(ii).getLocation_id().equalsIgnoreCase(cursor.getString(12)))
         location_name=locations.get(ii).getLocation_name();
if(location_name!=null)
if(location_name.length()>10)
	{location_name=location_name.substring(0,10);
	location_name+=".";
	}
if(v.subsession_id.equalsIgnoreCase("1"))			
{if (location_name!=null)
	v.nume=location_name+" "+v.ses_id;
else
	v.nume=cursor.getString(11).substring(0, 3)+" "+v.ses_id+" IRW";}
else {if (location_name!=null)
{if(location_name.length()>8)
{location_name=location_name.substring(0,8);
location_name+=".";
}
	v.nume=location_name+" "+v.ses_id+"."+v.subsession_id;}
else v.nume=cursor.getString(11).substring(0, 3)+" "+v.ses_id+" Edit "+v.subsession_id;}

//System.out.println("<<<<<<<<<<<<<<dmeo:"+cursor.getString(7)+"<<<"+cursor.getString(8));
v.thumb_res=cursor.getString(10);
v.sport=cursor.getString(11);
if(cursor.getString(9)!=null&&!cursor.getString(9).equalsIgnoreCase("")&&!cursor.getString(9).equalsIgnoreCase("null")){


SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
Date date;
try {
	date = dateFormat.parse(cursor.getString(9));

long d1=date.getTime();
Calendar today = Calendar.getInstance();
long d2=today.getTimeInMillis();
int day = (int)((d1-d2)/86400000);
v.expiration_date=day+"";
if(day<0) v.expiration_date=IrewindBackend.Instance.context.getResources().getString(R.string.expired);

} 
catch (ParseException e) {
	v.expiration_date=cursor.getString(9);
	
}}
else v.expiration_date="null";
//if(v.last_error.equalsIgnoreCase("User has no tickets"))v.expiration_date="Click to add ticket";
v.download_progress=cursor.getInt(13);

//System.out.println("v.error="+v.last_error);
//if(all||v.last_error.equalsIgnoreCase("")||v.last_error.equalsIgnoreCase("null")||v.last_error.contains("icket")||v.last_error.contains("Insufficient segments available"))
//{if(all||v.download_progress!=0||(v.sport!=null&&v.sport.equalsIgnoreCase("Ski")));
	{//System.out.println("adding session "+v.ses_id+" to result list");
	p.add(v);}

//video_aux=v;
//}
}








cursor.moveToNext();}
cursor.close();
 }


//tenis

/* if(MainActivity.versiune.startsWith("Tenis"))
{videoclip v=new videoclip();
v.nume="Tenis 1";
p.add(v);}*/
/* if(p.size()>0)
if(p.get(p.size()-1)!=video_aux)
	p.add(video_aux);*/

return p;}


public videoclip getLastSubsession()
{Cursor cursor=null;
videoclip v=new videoclip();
	try{
	  cursor = db.query("irw_sessions", new String[] {"session_id","subsession_id","location_id" },null,null, null, null, "start_time", null);
	} catch(SQLiteException e) {}
	
	if(cursor!=null&&cursor.getCount()>0)
	{	cursor.moveToLast();
v.ses_id=cursor.getString(0);
v.subsession_id=cursor.getString(1);
v.location=cursor.getString(2);
	}

	
	
return v;
}


public String getLocationIdForSessionFromLogs(String session)
{String location="0";
try{
Cursor cursor= 	db.rawQuery("SELECT  location_id from irw_users_logs where session_id="+session+" and location_id IS NOT NULL",null);
if (cursor != null&&cursor.getCount()>0)
{  cursor.moveToFirst();
location=cursor.getString(0);}
cursor.close();
}catch (Exception e){}
return location;
}


public ArrayList<MyPoint> trackPoints(String sesiune,String user_id)
{
	
ArrayList<MyPoint> p=new ArrayList<MyPoint>();
   
System.out.println("user_id="+user_id);

Cursor cursor= db.rawQuery("SELECT point_time, point_alt, point_lat, point_long,point_accuracy,point_heading,point_speed, irw_sessions.subsession_id,irw_users_logs.session_id, irw_users_logs.location_id from  irw_users_logs left join irw_sessions on irw_sessions.session_id=irw_users_logs.session_id and irw_sessions.user_id=irw_users_logs.user_id  and irw_users_logs.point_time < irw_sessions.stop_time and irw_users_logs.point_time>irw_sessions.start_time where irw_users_logs.user_id="+user_id+" and irw_users_logs.session_id="+sesiune, null);//compare time point
//Cursor cursor = db.query("irw_users_logs", new String[] {"point_time","point_alt","point_lat","point_long","point_accuracy","point_heading","point_speed","subsession_id","session_id" },"session_id=? AND user_id=?",new String[] { sesiune,user_id }, null, null, null, null);  //original query, for normal usage
 if (cursor != null)
    cursor.moveToFirst();
for(int i=0;i<cursor.getCount();i++)     // i+=4  only displays one in 4 point, for better speed
{MyPoint pt=new MyPoint();

if(cursor.getString(0)!=null)pt.time=cursor.getString(0);
pt.altitudine=Integer.parseInt(cursor.getString(1));
pt.latitudine=Double.parseDouble(cursor.getString(2));
pt.longitudine=Double.parseDouble(cursor.getString(3));
pt.accuracy=Integer.parseInt(cursor.getString(4));
pt.heading=Double.parseDouble(cursor.getString(5));
pt.speed=Double.parseDouble(cursor.getString(6));
pt.session_id=Integer.parseInt(cursor.getString(8));
pt.location_id=Integer.parseInt(cursor.getString(9));
if(cursor.getString(7)==null) pt.subsession_id=0;
else
pt.subsession_id=Integer.parseInt(cursor.getString(7));
	p.add(pt);
cursor.moveToNext();}
cursor.close();
;
return p;}



public int ttlForCardType(int type)
{Cursor cursor= db.query( "irw_card_types", new String[] {"id,record_ttl" }, "id="+type, null, null, null, null, null);
try {
	

if (cursor != null)
{  cursor.moveToFirst();
System.out.println("card of type "+type+" has ttl= "+cursor.getInt(1));
int rez=cursor.getInt(1);
cursor.close();
return rez;
}
else {return 0;}

} catch (CursorIndexOutOfBoundsException e) {
	cursor.close();
	return 0;

}}

public int maxSessionInLogs (int user_id)
{ try{
Cursor cursor = db.query("irw_users_logs", new String[] {"max(session_id)" },"user_id=?",
        new String[] { ""+user_id }, null, null, null, null);
if (cursor != null)
   cursor.moveToFirst();
//MainActivity.scrieLog("cursor size: "+cursor.getCount()+" valoare:"+cursor.getString(0)+" uid: "+user_id);
int r=1;
if(cursor.getString(0)!=null)r=Integer.parseInt(cursor.getString(0));
cursor.close();
;
return r;
}catch(Exception e){e.printStackTrace(); return 0;}

}



public int maxSessionInSessions(int user_id)
{ try{
Cursor cursor = db.query("irw_sessions", new String[] {"max(session_id)" },"user_id=?",
        new String[] { ""+user_id }, null, null, null, null);
if (cursor != null)
   cursor.moveToFirst();
//MainActivity.scrieLog("cursor size: "+cursor.getCount()+" valoare:"+cursor.getString(0));
int r=1;
if(cursor.getString(0)!=null)
 r=Integer.parseInt(cursor.getString(0));
cursor.close();
;
return r;}catch (IllegalStateException e) {return 1;}catch (NullPointerException e) {return 1;}}



public int maxSessionInSessions()
{ try{
Cursor cursor = db.query("irw_sessions", new String[] {"max(session_id)" },null,null, null, null, null, null);
if (cursor != null)
   cursor.moveToFirst();
//MainActivity.scrieLog("cursor size: "+cursor.getCount()+" valoare:"+cursor.getString(0));
int r=1;
if(cursor.getString(0)!=null)
 r=Integer.parseInt(cursor.getString(0));
cursor.close();
;
return r;}catch (IllegalStateException e) {return 1;}catch (NullPointerException e) {return 1;}}


public int maxSubsessionForSessions(String user_id, String session)
{ try{
Cursor cursor = db.query("irw_sessions", new String[] {"max(subsession_id)" },"user_id=? AND  session_id=?",
        new String[] { user_id,session }, null, null, null, null);
if (cursor != null)
   cursor.moveToFirst();
//MainActivity.scrieLog("cursor size: "+cursor.getCount()+" valoare:"+cursor.getString(0));
int r=1;
if(cursor.getString(0)!=null)
 r=Integer.parseInt(cursor.getString(0));
cursor.close();
;
return r;}catch (IllegalStateException e) {return 1;}catch (NullPointerException e) {return 1;}}

public ArrayList<MyPoint> unsynincronisedPoints(int session_id,int user_id)
{	ArrayList<MyPoint> p=new ArrayList<MyPoint>();
	try {
	



   

 Cursor cursor = db.query("irw_users_logs", new String[] {"point_time","point_alt","point_lat","point_long","point_accuracy","point_heading","point_speed","subsession_id","session_id", "point_id" },"sync=0 AND session_id=?",
        new String[] {session_id+""}, null, null, null, null);
 if (cursor != null)
    cursor.moveToFirst();
for(int i=0;i<cursor.getCount();i++)
{MyPoint pt=new MyPoint();
//MainActivity.scrieLog("lat "+cursor.getString(2)+" long "+cursor.getString(3)+" sync "+cursor.getString(9) );
if(cursor.getString(0)!=null)pt.time=cursor.getString(0);
//MainActivity.scrieLog(pt.time);
pt.altitudine=Integer.parseInt(cursor.getString(1));
pt.latitudine=Double.parseDouble(cursor.getString(2));
//System.out.println("latitude from dataBase:"+cursor.getString(2)+" pentru sesiunea "+cursor.getString(8));
pt.longitudine=Double.parseDouble(cursor.getString(3));
pt.accuracy=Integer.parseInt(cursor.getString(4));
pt.heading=Double.parseDouble(cursor.getString(5));
pt.speed=Double.parseDouble(cursor.getString(6));
pt.session_id=Integer.parseInt(cursor.getString(8));
//pt.subsession_id=Integer.parseInt(cursor.getString(7));
pt.point_id=Integer.parseInt(cursor.getString(9));
	p.add(pt);
cursor.moveToNext();}
cursor.close();
;

} catch (IllegalStateException e) {
	// TODO: handle exception
}
return p;

}

/*public void develCheckSync()
{ Cursor cursor = db.query("irw_users_logs", new String[] {"point_time","point_alt","point_lat","point_long","point_accuracy","point_heading","point_speed","subsession_id","session_id", "point_id" },"sync=0 AND session_id=?",
        new String[] {session_id+""}, null, null, null, null);


}*/

/*public void syncTime(String location_id,long delta)  //TODO conditia de where intre doua timestamp-uri
{MainActivity.scrieLog("DataBase.syncTime", "syncStart for location "+location_id);

db.rawQuery("UPDATE irw_users_logs SET sync =0 AND point_time=point_time + "+delta+" WHERE sync=-1 AND location_id="+location_id, null);
MainActivity.scrieLog("DataBase.syncTime", "sync Ended");
}*/

public void sync(String session_id,String user_id,ArrayList<MyPoint> pt)  //TODO conditia de where intre doua timestamp-uri
{
ContentValues args = new ContentValues();
args.put("sync", "1");
for(int i=0;i<pt.size();i++)
{//MainActivity.scrieLog(pt.get(i).point_id);
 //MainActivity.scrieLog("update punct "+pt.get(i).point_id);
	db.update("irw_users_logs", args, "session_id="+session_id+" AND user_id="+user_id+" AND point_id="+pt.get(i).point_id, null);}
;
}



public void sync(String session_id,String user_id,String time_start,String time_finish)
		{
ContentValues args = new ContentValues();
args.put("sync", 1);

	db.update("irw_users_logs", args, "session_id="+session_id+" AND user_id="+user_id+" AND point_time >="+time_start + " AND point_time <="+time_finish+" AND sync=0" , null);

}

/*
public void UpdateTrack(ContentValues val,String session_id,String user_id)
{ 

db.update("trackuri", val, "session_id="+session_id+" AND user_id="+user_id, null);

;}*/




/*public void test(String session_id,String subsession_id,String user_id)
{ContentValues val=new ContentValues();
val.put("demo_path","irw_test/test_irw.mp4");
db.update("irw_sessions", val, "session_id="+session_id+" AND user_id="+user_id+" AND subsession_id="+subsession_id, null);
System.out.println("updated ses="+session_id+" usr="+user_id);
subsessions("All");
}*/


public void UpdateVideoStatus(String status,String session_id,String subsession_id,String user_id,String path_hd,String path_prev)
{ System.out.println("update video status, uid="+user_id+"status="+status+" session="+session_id+" subsession="+subsession_id+" path_hd="+path_hd+" path_low="+path_prev);
ContentValues val=new ContentValues();
val.put("expiration_date", status);
if(path_hd==null) val.put("demo_path",path_prev);
else val.put("hd_path", path_hd);


 db.update("irw_sessions", val, "session_id="+session_id+" AND user_id="+user_id+" AND subsession_id="+subsession_id, null);

}






public void UpdateVideoError(String error,String session_id,String subsession_id,String user_id)
{ 
ContentValues val=new ContentValues();
val.put("last_error", error);
db.update("irw_sessions", val, "session_id="+session_id+" AND user_id="+user_id+" AND subsession_id="+subsession_id, null);
}





private boolean existsColumnInTable(SQLiteDatabase inDatabase, String inTable, String columnToCheck) {
    try{
        //query 1 row
        Cursor mCursor  = inDatabase.rawQuery( "SELECT * FROM " + inTable + " LIMIT 0", null );

        //getColumnIndex gives us the index (0 to ...) of the column - otherwise we get a -1
        if(mCursor.getColumnIndex(columnToCheck) != -1)
            return true;
        else
            return false;

    }catch (Exception Exp){
        return false;
    }
}


}




