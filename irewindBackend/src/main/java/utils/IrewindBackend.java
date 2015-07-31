package utils;

import irewindb.services.ServiciuGpsLogging;

import java.util.ArrayList;

import myClass.DownloadProgressListener;
import myClass.IrewindGpsUpdatesListener;
import myClass.iLocation;
import myClass.iUser;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class IrewindBackend {
	public static IrewindBackend Instance;
	ArrayList<iLocation> iLocations;
	DataBase db;
	public long deltaTime = 0;
	iUser user;

	public iUser getUser() {
		return user;
	}

	public boolean recordingState = false;
	iLocation curentLocation;
	IrewindGpsUpdatesListener updatesListener;
	DownloadProgressListener downloadProgressListener;
	Object lock = new Object();
	volatile Activity context;

	public IrewindBackend(Activity context, String user, String password,
			String user_id, String service,
			IrewindGpsUpdatesListener irewindGpsUpdatesListener) {
		System.out.println("IrewindBackend created with user="+user+" user_id="+user_id+" password="+password);
		
		synchronized (this.lock) {
			this.user = new iUser(user, user_id, password, service);
			updatesListener = irewindGpsUpdatesListener;
			if (Instance != null)
				throw new IllegalStateException("Already instantiated...");
			Instance = this;
			this.context = context;
			db = new DataBase(context);
		}

	}

	public IrewindBackend(Activity context, String user, String user_id,
			String password, IrewindGpsUpdatesListener irewindGpsUpdatesListener) {
		System.out.println("IrewindBackend created with user="+user+" user_id="+user_id+" password="+password);
		
		synchronized (this.lock) {
			this.user = new iUser(user, user_id, password, "iRewind");
			updatesListener = irewindGpsUpdatesListener;
			if (Instance != null)
				throw new IllegalStateException("Already instantiated...");
			Instance = this;
			this.context = context;
		}
	db = new DataBase(context);
		new Thread(new Runnable() {

			@Override
			public void run() {
				init();

			}
		}).start();
	}

	public void setContext(Activity context) {
		synchronized (this.lock) {
			this.context = context;
		}
	}

	public Activity getContext() {
		synchronized (this.lock) {
			return context;
		}
	}

	public void setGpsUpdatesListener(IrewindGpsUpdatesListener listener) {
		synchronized (this.lock) {
			updatesListener = listener;
		}
	}
	
	public void setDownloadProgressListener(DownloadProgressListener listener) {
		downloadProgressListener = listener;
		
	}
	
	public void setDwonloadProgress (float p)
	{try{downloadProgressListener.setProgress(p);}catch(Exception e){}}

	public void syncTables() {
		request.updateTable("irw_location_status", user.getUsername(),
				user.getAuth_service(), user.getPassword(), context, true);
		request.updateTable("irw_locations", user.getUsername(),
				user.getAuth_service(), user.getPassword(), context, true);
		request.updateTable("irw_locations_gps", user.getUsername(),
				user.getAuth_service(), user.getPassword(), context, true);
		request.updateTable("irw_cameras", user.getUsername(),
				user.getAuth_service(), user.getPassword(), context, true);
	/*	 request.updateTabelWithUserId("irw_users_logs",
				 user.getUsername(),user.getAuth_service(), user.getPassword(), context,
				 user.getId(),request.adresa);
		*/ 
		// user.getAuth_service(),
		// user.getPassword(), context, true);
		synchronized (this.lock) {
			updatesListener.initEnded("OK");
		}
	}

	/*
	 * public void retriveAuthData() {prefs =
	 * MainActivity.Instance.getSharedPreferences("irewind",
	 * Context.MODE_PRIVATE); user=new iUser(prefs.getString("user", ""),
	 * prefs.getString("id", ""), prefs.getString("password", ""),
	 * prefs.getString("auth_service", "")); }
	 */
	String Max_ses_server = "0";
	 BroadcastReceiver mNetworkStateReceiver;
	public void init() {
		if (request.isNetworkAvailable(context)) {

			Max_ses_server = request.getMaxSess(user.getUsername(),
					user.getAuth_service(), user.getPassword(), user.getId());
			syncTables();
		} else
			synchronized (this.lock) {
				updatesListener.initEnded("FAIL - NO INTERNET");
			}
		
		//Callback  
		 mNetworkStateReceiver = new BroadcastReceiver() {
		    @Override
		        public void onReceive(Context context, Intent intent) {
		    	if(intent == null || intent.getExtras() == null)
		            return;

		        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		        NetworkInfo ni = manager.getActiveNetworkInfo();

		        if(ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
		          onInternetConnection();
		        } 

		        }
		    };

		//registering recievr in actibity or in service
		IntentFilter mNetworkStateFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		context.registerReceiver(mNetworkStateReceiver , mNetworkStateFilter);
		
	}
	
	public void destroy()
	{context.unregisterReceiver(mNetworkStateReceiver);}

	public DataBase getDatabase() {
		return db;
	}

	public ArrayList<iLocation> getILocations() {
		if (iLocations != null)
			return iLocations;
		else {
			iLocations = db.Locations();
			if (iLocations == null)
				syncTables();

			return iLocations;
		}
	}

	public iLocation getLocation(String id) {
		if (iLocations == null)
			getILocations();

		for (int i = 0; i < iLocations.size(); i++)
			if (iLocations.get(i).getLocation_id().equalsIgnoreCase(id))
				return iLocations.get(i);
		return null;
	}

	public void calculateDeltaTime() {
		if (deltaTime == 0)
			deltaTime = request.TimeError(user);
		System.out.println("<<Time Delta=" + deltaTime);
	}

	public iLocation getCurentLocation() {
		synchronized (this.lock) {
			return curentLocation;
		}
	}

	public void location_changed(iLocation location) {
		synchronized (this.lock) {
			curentLocation = location;
			updatesListener.locationChanged(location);
		}
	}

	public void location_change_Out(String city) {
		synchronized (this.lock) {
			curentLocation = null;
			updatesListener.notInAnIrwLocation(city);
		}
	}

	public void stopRecording() // TODO LOCATOIN ID
	{
		new Thread(new Runnable() {
			public void run() {
				
					try {
						request.uploadGPS(user.getUsername(), user
								.getAuth_service(), user.getPassword(), Integer
								.parseInt(user.getId()),
								context);

						request.SincronizeSessions(user.getUsername(), user
								.getAuth_service() ,Integer.parseInt( user.getId()), user.getPassword(),
								context,request.adresa);
					} catch (Exception e) {e.printStackTrace();
					}
				
			}
		}).start();
		recordingState = false;
		context.stopService(new Intent(context, ServiciuGpsLogging.class));

	}

	public void onInternetConnection() {
		new Thread(new Runnable() {
			public void run() {
				
					try {
						request.uploadGPS(user.getUsername(), user
								.getAuth_service(), user.getPassword(), Integer
								.parseInt(user.getId()),
								context);

						request.SincronizeSessions(user.getUsername(), user
								.getAuth_service() , Integer.parseInt( user.getId()), user.getPassword(),
								context,request.adresa);
					} catch (Exception e) {e.printStackTrace();
					}
			
			}
		}).start();
	}

	public void startRecording() {
		recordingState = true;
		startGpsService();
	}

	void startGpsService() {

		LocationManager locationManager = (LocationManager) context
				.getSystemService(Activity.LOCATION_SERVICE);

		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
			synchronized (this.lock) {
				updatesListener.noGPS();
			}

		new Thread(new Runnable() {
			public void run() {
				System.out.println("<<<<<<<,GPS SERVICE 1");

				Intent intn = new Intent(context, ServiciuGpsLogging.class);

				if (user.getId() != null && !user.getId().equalsIgnoreCase("")) {
					System.out.println("<<<<<<<,GPS SERVICE 2");
					intn.putExtra("user_id", Integer.parseInt(user.getId()));
					intn.putExtra("session_id", newSession());
					intn.putExtra("user", user.getUsername());
					intn.putExtra("auth_service", user.getAuth_service());
					intn.putExtra("password", user.getPassword());
					context.startService(intn);
				} else {
				} // TODO no id, request login or smth

			}
		}).start();
	}

	int lastSession = -1;

	public int newSession() {
		if (lastSession == -1) {
			int sesiune = 1;

			sesiune = db.maxSessionInLogs(Integer.parseInt(user.getId()));
			int max_ses = 0;
			try {
				max_ses = Integer.parseInt(Max_ses_server);
			} catch (NumberFormatException e) {

			}
			sesiune = Math.max(sesiune, max_ses);
			int ses = db.maxSessionInSessions(Integer.parseInt(user.getId()));

			sesiune = Math.max(ses, sesiune) + 1;
			lastSession = sesiune;
			return sesiune;
		} else {
			lastSession++;
			return lastSession;
		}
	}

	// iLocation location_aux;
	public void startCustomService(iLocation location) {
		/*
		 * location_aux = location;
		 * System.out.println("start service called with type =" +
		 * location.getLocation_type());
		 * 
		 * new Thread(new Runnable() {
		 * 
		 * @Override public void run() { Intent intn;
		 * 
		 * // MyApp appState = ((MyApp) getApplicationContext()); // int
		 * type_index=appState.getLocationType();
		 * 
		 * if
		 * (!location_aux.getLocation_type().equalsIgnoreCase("1")&&!location_aux
		 * .getLocation_type().equalsIgnoreCase("6")) { if
		 * (location_aux.getLocation_type().equalsIgnoreCase("5")) { if
		 * (android.os.Build.VERSION.SDK_INT >= 18 && context
		 * .getPackageManager() .hasSystemFeature(
		 * PackageManager.FEATURE_BLUETOOTH_LE)) { if
		 * (location_aux.getLocation_type() .equalsIgnoreCase("5")) intn = new
		 * Intent(context, BluetoothRecordingV2.class); else intn = new
		 * Intent(context, Service7Golf.class); if (user.getId() != null &&
		 * !user.getId().equalsIgnoreCase("")) { intn.putExtra("location_id",
		 * location_aux.getLocation_id()); intn.putExtra("session_id",
		 * newSession()+""); intn.putExtra("user1", user); //change to user
		 * object intn.putExtra("user_id", Integer.parseInt(user.getId()));
		 * intn.putExtra("user", user.getUsername());
		 * intn.putExtra("auth_service", user.getAuth_service());
		 * intn.putExtra("password", user.getPassword()); //
		 * 
		 * context.startService(intn); } } else { context.runOnUiThread(new
		 * Runnable() { public void run() { Toast.makeText( context,
		 * "Your phone doesn't support Irewind Bluetooth Locations",
		 * Toast.LENGTH_LONG).show(); } }); } }
		 * 
		 * else if (location_aux.getLocation_type().equalsIgnoreCase("7")) {
		 * intn = new Intent(context, Service7Golf.class); if (user.getId() !=
		 * null && !user.getId().equalsIgnoreCase("")) {
		 * intn.putExtra("location_id", location_aux.getLocation_id());
		 * intn.putExtra("session_id", newSession()+""); intn.putExtra("user1",
		 * user); context.startService(intn);}}
		 * 
		 * else { if (location_aux.getLocation_type().equalsIgnoreCase("3"))
		 * intn = new Intent(context, ServiciuDownloadVideos.class); else intn =
		 * new Intent(context, DirectVideoDownloadService.class);
		 * 
		 * if (user.getId() != null && !user.getId().equalsIgnoreCase(""))
		 * intn.putExtra("user_id", Integer.parseInt(user.getId()));
		 * 
		 * 
		 * int sesiune = 1; if (user.getId() != null &&
		 * !user.getId().equalsIgnoreCase("")) sesiune =
		 * db.maxSessionInLogs(Integer.parseInt(user.getId())); else sesiune =
		 * db.maxSessionInSessions(); if (Max_ses_server == null ||
		 * Max_ses_server.equalsIgnoreCase("null")) Max_ses_server = "1"; //
		 * first use try {
		 * 
		 * sesiune = Math.max(sesiune, Integer.parseInt(Max_ses_server)); }
		 * catch (NumberFormatException e) { // TODO: handle exception } int
		 * ses; if (user.getId() != null && !user.getId().equalsIgnoreCase(""))
		 * ses = db.maxSessionInSessions(Integer.parseInt(user.getId())); else
		 * ses = db.maxSessionInSessions(); sesiune = Math.max(sesiune, ses) +
		 * 1; intn.putExtra("session_id", sesiune);
		 * 
		 * intn.putExtra("user", user.getUsername());
		 * intn.putExtra("auth_service", user.getAuth_service());
		 * intn.putExtra("password", user.getPassword());
		 * intn.putExtra("location_url", location_aux.getLocation_url());
		 * intn.putExtra("location_id", location_aux.getLocation_id() + "");
		 * System.out.println(user.getUsername() + " --" + user.getPassword());
		 * 
		 * if (location_aux.getLocation_type().equalsIgnoreCase( "-1")
		 * &&location_aux.getLocation_type() .equalsIgnoreCase("3")) {
		 * TenisServiceConnection = new ServiceConnection() {
		 * 
		 * @Override public void onServiceDisconnected( ComponentName name) {
		 * System.out .println(
		 * "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< << << << << on service disconected");
		 * 
		 * }
		 * 
		 * @Override public void onServiceConnected( ComponentName name, IBinder
		 * service) { System.out .println(
		 * "<<<<<<<<<<<<<<  <<  <<  <<<<  <<  <<  <<<<  <<  <<  << onServiceConnected"
		 * ); LocalBinder lb = (LocalBinder) service; Refference_to_dlService =
		 * lb .getServerInstance();
		 * 
		 * } }; System.out.println("will bind to SDV");
		 * context.bindService(intn, TenisServiceConnection, BIND_AUTO_CREATE);
		 * System.out.println("binded to SDV");
		 * 
		 * } else context.startService(intn); } } } }).start();
		 */}

	public void stopLastCustomService() {/*
										 * if (location_aux != null) { if
										 * (location_aux
										 * .getLocation_type().equalsIgnoreCase
										 * ("4")) stopService(new
										 * Intent(MainActivity.this,
										 * DirectVideoDownloadService.class));
										 * else if
										 * (location_aux.getLocation_type
										 * ().equalsIgnoreCase("3")) { try {
										 * Refference_to_dlService
										 * .StopRecording();
										 * Refference_to_dlService = null; }
										 * catch (NullPointerException e) { }
										 * try {
										 * MainActivity.this.unbindService(
										 * TenisServiceConnection); } catch
										 * (IllegalArgumentException e) {
										 * e.printStackTrace(); }
										 * 
										 * } else if
										 * (location_aux.getLocation_type
										 * ().equalsIgnoreCase("5")) {
										 * stopService(new
										 * Intent(MainActivity.this,
										 * BluetoothRecordingV2.class)); }else
										 * if (location_aux.getLocation_type().
										 * equalsIgnoreCase("7")) {
										 * stopService(new
										 * Intent(MainActivity.this,
										 * Service7Golf.class)); } }
										 * 
										 * location_aux = null;
										 */
	}

}
