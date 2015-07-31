package irewindb.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import irewindb.R;

import java.util.ArrayList;

import myClass.GeocoderHelper;
import myClass.iLocation;
import myClass.myCamera;
import utils.DataBase;
import utils.IrewindBackend;
import utils.algorithms;
import utils.request;

public class ServiciuGpsLogging extends Service implements LocationListener,
		com.google.android.gms.location.LocationListener, ConnectionCallbacks, OnConnectionFailedListener {

	public static boolean timeSyncRequired = true;
	public static double last_lat = 0, last_long = 0;
	public static iLocation currentIrwLocation;
	static double HARDCODED_LATITUDE = -1; // -1 TO DISABLE
	static double HARDCODED_LONGITUDE = -1;
	static int MINIMUM_ACCURACY = 40;

	boolean uploading = false; // used as a semaphore for uploading points
	boolean isInLocationArea = false;
	int user_id, session_id;
	String user, auth_service, password;
	int point_id = 1;
	int closest_index = 0; // index of closest location
	int retriedConnection = 0; // counts how many times the
	int current_GPS_listening_mode = -1; // state 1 = high relevance area; state
											// 2 = area radius; state 3 =
											// outside area radius
	long lastTimeLocationClientConnected = 0;
	long last_location_timeStamp = -1;
	long first_timestamp = 0; // the timestamp of the first location point that
								// has not been uploaded
	long last_point = 0;
	boolean received_an_update = false; // true if at least one location update
										// has been received. Used to reset
										// connection to the location service
										// after a timeout.
	LocationManager mlocManager;
	Location last_location_recieved;
	ArrayList<myCamera> cameras; // list of irewind camera in the curent
									// location
	ArrayList<iLocation> locations;
	Thread UploadingThread = null;

	public GoogleApiClient mLocationClient;

	// These settings are the same as the settings for the map. They will in
	// fact give you updates at
	// the maximal rates currently possible.
	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(50).setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	// The CountDownTimer checks for failures and resets connection when necessary
	CountDownTimer ct = new CountDownTimer(900000000, 1000) {

		@Override
		public void onTick(long millisUntilFinished) {
			// if we didn't received any update, reset connection
			if (!received_an_update
					&& lastTimeLocationClientConnected + 6000 < System
							.currentTimeMillis() && retriedConnection < 3) // if
																			// no
																			// point
																			// received,
																			// retry
																			// GPS
																			// connection
			{
				retriedConnection++;
				setUpLocationClientIfNeeded();
				if (mLocationClient != null) {
					mLocationClient.disconnect();
					mLocationClient.connect();
					lastTimeLocationClientConnected = System
							.currentTimeMillis();
				}
			}
			// if we didn't received any update and already reseted connection 3
			// times, use the last known location of the device
			if (!received_an_update && retriedConnection >= 3) {
				try {

					if (LocationServices.FusedLocationApi.getLastLocation(mLocationClient) != null)
						onLocationChanged(LocationServices.FusedLocationApi.getLastLocation(mLocationClient));
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			// if the location list is missing, retry getting it
			if (locations == null) {

				try {

					locations = IrewindBackend.Instance.getILocations();

				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}

			// if the time interval between two points is greater than
			// area_radius_interval, then force maximum accuracy
			if (currentIrwLocation != null
					&& (last_location_timeStamp
							+ currentIrwLocation.area_radius_interval < System
								.currentTimeMillis())) {
				REQUEST.setInterval(50).setFastestInterval(16)
						.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
				try {
					LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, REQUEST, ServiciuGpsLogging.this);
				
					current_GPS_listening_mode = 1;
				} catch (IllegalStateException e) {
				}
			}
		}

		@Override
		public void onFinish() {
		}
	};

	public void setIrwLocation(iLocation newLocation) {
		cameras = IrewindBackend.Instance.getDatabase().getCameras( // get the
																	// camera
																	// list for
																	// the new
																	// location
				newLocation.getLocation_id());
		IrewindBackend.Instance.location_changed(newLocation); // set the new
																// location in
																// IrewindBackend
		new Thread(new Runnable() {
			public void run() {
				if (IrewindBackend.Instance.deltaTime == 0)
					IrewindBackend.Instance.calculateDeltaTime(); // recalculate
																	// time
																	// shift
																	// between
																	// the
																	// device
																	// and the
																	// server
			}
		}).start();
		currentIrwLocation = newLocation;
	}

	// returns true if the device has internet connectivity
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	@Override
	public IBinder onBind(Intent arg0) {

		return null;
	}

	@Override
	public int onStartCommand(Intent it, int a, int b) {

		if (it != null) {									//Retrieve authentication and session data from the calling intent.
			user_id = it.getIntExtra("user_id", 0);
			session_id = it.getIntExtra("session_id", 0);

			user = it.getStringExtra("user");              
			auth_service = it.getStringExtra("auth_service");
			password = it.getStringExtra("password");

			ct.start();  //start the CountDownTimer, that periodically checks for failures.
		}
		return START_STICKY;
	}

	final static int myID = 1234;

	public void runInForeground() { // run in foreground so the process does not
									// get spontaneously killed by the OS

		// The intent to launch when the user clicks the expanded notification
		Intent intent = new Intent(this, IrewindBackend.Instance.getContext()
				.getClass());
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendIntent = PendingIntent
				.getActivity(this, 0, intent, 0);

		// This constructor is deprecated. Use Notification.Builder instead
		Notification notice = new Notification(R.drawable.irw_launcher_notification,
				"Irewind is recording", System.currentTimeMillis());

		// This method is deprecated. Use Notification.Builder instead.
		notice.setLatestEventInfo(this, "Irewind", "GPS recording", pendIntent);

		notice.flags |= Notification.FLAG_NO_CLEAR;
		startForeground(myID, notice);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		runInForeground(); // run in foreground so the process does not get
							// spontaneously killed by the OS
		mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		setUpLocationClientIfNeeded();
		mLocationClient.connect(); // connect to location updates
		lastTimeLocationClientConnected = System.currentTimeMillis(); // lastTimeLocationClientConnected
																		// is
																		// used
																		// to
																		// know
																		// when
																		// to
																		// retry
																		// connection
	}

	@Override
	public void onLocationChanged(Location l) {

		if (currentIrwLocation == null
				|| (l.getAccuracy() < currentIrwLocation.max_accuracy)) { // if
																			// the
																			// accuracy
																			// is
																			// better
																			// than
																			// max_accuracy
			last_lat = l.getLatitude();
			last_long = l.getLongitude(); // used for camera mapping
			received_an_update = true;
			last_location_recieved = l;

			
			System.out.println("lat = "+l.getLatitude()+"; lng="+l.getLongitude());
			
			if (currentIrwLocation == null // if the time difference is greater
											// than location_area_min_interval
					|| last_location_timeStamp
							+ currentIrwLocation.location_area_min_interval < System
								.currentTimeMillis()) {
				last_location_timeStamp = System.currentTimeMillis();
				try {

					double latitude, longitude;
					if (HARDCODED_LATITUDE != -1) { // hardcoded values used for
													// testing; -1 means
													// disabled
						latitude = HARDCODED_LATITUDE + Math.random() * 0.00001;
						longitude = HARDCODED_LONGITUDE;
					} else {
						latitude = l.getLatitude();
						longitude = l.getLongitude();
					}
					if (locations == null) {
						try {
							locations = IrewindBackend.Instance.getILocations();

						} catch (NullPointerException e) {
							e.printStackTrace();
						}
					}

					double distance = 9999999;

					if (locations != null && locations.size() > 0) {

						closest_index = algorithms.getClosestLocation(
								locations, latitude, longitude, 9999999); // pick
																			// closest
																			// irewind
																			// location

						iLocation closest_location = locations
								.get(closest_index);
						distance = algorithms.measure(latitude, longitude, // calculate
																			// distance
																			// to
																			// closest
																			// irewind
																			// location
								closest_location.lat, closest_location.lng);
						if (currentIrwLocation != closest_location
								&& distance >= closest_location.area_radius) {
							try {
								new GeocoderHelper().fetchCityName(l);
							} catch (Exception e) {
							}

						}
						if (distance < closest_location.area_radius) // if the
																		// point
																		// is
																		// inside
																		// the
																		// area_radius

						{
							if (currentIrwLocation != closest_location) {
								IrewindBackend.Instance
										.location_changed(closest_location);
								if (currentIrwLocation != null)
									IrewindBackend.Instance
											.stopLastCustomService();
								setIrwLocation(closest_location);
								IrewindBackend.Instance
										.startCustomService(closest_location);
							}
						} else {
							if (currentIrwLocation != null) {
								currentIrwLocation = null;
								IrewindBackend.Instance.stopLastCustomService();
							}
						}

						if (isInPolygonDummy(closest_location, latitude, // if
																			// the
																			// user
																			// is
																			// in
																			// the
																			// high
																			// relevance
																			// area
								longitude)) {
							if (current_GPS_listening_mode != 1) { // use mode 1
																	// recording
																	// =
																	// high_accuracy,
																	// location_area_min_interval
																	// < time
																	// between
																	// location
																	// updates <
																	// location_area_max_interval
								current_GPS_listening_mode = 1;
								isInLocationArea = true;
								REQUEST.setFastestInterval(
										closest_location.location_area_min_interval)
										.setInterval(
												closest_location.location_area_max_interval)
										.setPriority(
												LocationRequest.PRIORITY_HIGH_ACCURACY);
								try {
									LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, REQUEST, ServiciuGpsLogging.this);
									
								} catch (IllegalStateException e) {
								}
							}
						} else if (distance < closest_location.area_radius) { // if
																				// outside
																				// high
																				// relevance
																				// area,
																				// but
																				// inside
																				// area_radius
							if (current_GPS_listening_mode != 2) { // use mode 2
																	// recording
																	// =
																	// balanced
																	// accuracy,
																	// time
																	// between
																	// location
																	// updates <
																	// area_radius_interval
								current_GPS_listening_mode = 2;
								isInLocationArea = false;
								System.out
										.println("Request time will be "
												+ closest_location.area_radius_interval);
								REQUEST.setFastestInterval(
										closest_location.area_radius_interval)
										.setInterval(
												closest_location.area_radius_interval)
										.setPriority(
												LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
								try {
									LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, REQUEST, ServiciuGpsLogging.this);
									
								} catch (IllegalStateException e) {
									e.printStackTrace();
								}
							}
						} else {
							if (current_GPS_listening_mode != 3) { // if outside
																	// area_radius
								current_GPS_listening_mode = 3; // use mode 3
																// recording =
																// balanced
																// accuracy,
																// time between
																// location
																// updates <
																// outside_area_radius_interval
								isInLocationArea = false;
								System.out.println("Request 150000");
								REQUEST.setInterval(
										closest_location.outside_area_radius_interval)
										.setFastestInterval(
												closest_location.outside_area_radius_interval)
										.setPriority(
												LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
								try {
									LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, REQUEST, ServiciuGpsLogging.this);
									
								} catch (IllegalStateException e) {
								}
							}
						}
						//

						last_point = System.currentTimeMillis();

						if (isInPolygonDummy(closest_location, latitude,
								longitude)) // if in high relevance area add
											// point to log

						{
							ContentValues valori = new ContentValues(); // generate
																		// the
																		// database
																		// row
							valori.put("user_id", user_id);
							valori.put("session_id", session_id);
							valori.put("location_id",
									locations.get(closest_index)
											.getLocation_id());
							valori.put("point_alt", (int) l.getAltitude());
							valori.put("point_lat", latitude + "");
							valori.put("point_long", longitude + "");
							valori.put("point_speed", l.getSpeed());
							valori.put("point_id", point_id);
							valori.put("point_time",
									((long) (System.currentTimeMillis())) + "");

							point_id++;
							valori.put("point_accuracy", (int) l.getAccuracy());
							valori.put("point_heading", l.getBearing());

							valori.put("sync", 0);

							DataBase db = IrewindBackend.Instance.getDatabase();
							db.AddContentValues(valori, "irw_users_logs"); // insert
																			// point
																			// into
																			// database

						}

						if (!uploading // uploading is used as a semaphore with
										// count = 1
								&& (first_timestamp + 30000 < System // once in
																		// 30
																		// seconds
																		// upload
																		// the
																		// log
																		// to
																		// server
										.currentTimeMillis())) {

							first_timestamp = System.currentTimeMillis();

							UploadingThread = new Thread(new Runnable() {

								@Override
								public void run() {

									uploading = true;
									request.uploadGPS(user, auth_service,
											password, user_id,
											ServiciuGpsLogging.this);

									uploading = false;
								}
							});
							UploadingThread.start();

						}
					} else {
						new Thread(new Runnable() {
							public void run() {
								try {
									locations = IrewindBackend.Instance // if
																		// the
																		// location
																		// list
																		// is
																		// empty,
																		// retry
																		// getting
																		// it
											.getILocations();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).start();
					}

				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void setUpLocationClientIfNeeded() {
		if (mLocationClient == null) {
			mLocationClient = new GoogleApiClient.Builder(this)
	        .addApi(LocationServices.API)
	        .addConnectionCallbacks(ServiciuGpsLogging.this)
	        .addOnConnectionFailedListener(ServiciuGpsLogging.this)
	        .build();
			
			
		}
	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	public void onDestroy() {
		try {

			UploadingThread.interrupt();
			UploadingThread = null;

		} catch (NullPointerException e) {
			// TODO: handle exception
		} catch (UnsupportedOperationException e) {
			// TODO: handle exception
		}

		if (ct != null)
			ct.cancel();
		super.onDestroy();
		if (ct != null)
			ct.cancel();
		// mlocManager.removeUpdates(this);

		if (mLocationClient != null) {
			mLocationClient.disconnect();
		}
		// updateGPS(0); //TODO
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle connectionHint) {

		LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, REQUEST, ServiciuGpsLogging.this);
		 // when the
																// location
																// client is
																// connected,
																// request
																// location
																// updates

	}

	

	public boolean isInCameraRadius(iLocation location, double latitude,
			double longitude) {
		System.out.println("isInCameraRadius called");
		if (cameras == null && location != null) {
			cameras = IrewindBackend.Instance.getDatabase().getCameras(
					location.getLocation_id());
		}

		for (int i = 0; i < cameras.size(); i++) {
			System.out.println("distance to cam"
					+ cameras.get(i).camera_id
					+ " distance= "
					+ algorithms.measure(latitude, longitude,
							cameras.get(i).lat, cameras.get(i).lng));
			if (algorithms.measure(latitude, longitude, cameras.get(i).lat,
					cameras.get(i).lng) < cameras.get(i).radius) {
				return true;
			}

		}

		System.out.println("isInCameraRadius return false;");
		return false;
	}

	public boolean isInPolygonDummy(iLocation location, double latitude, // It
																			// is
																			// called
																			// dummy
																			// because
																			// it
																			// is
																			// only
																			// considering
																			// the
																			// exterior
																			// rectangle
																			// of
																			// the
																			// polygon
			double longitude) { // It is more efficient

		if (location.minLat != 0) {
			if (latitude > location.minLat && latitude < location.maxLat
					&& longitude > location.minLng
					&& longitude < location.maxLng) {
				return true;
			}
			return false;
		} else
			return isInCameraRadius(location, latitude, longitude); // if no
																	// polygon
																	// is
																	// defined,
																	// check if
																	// the user
																	// is in the
																	// radius of
																	// one of
																	// the
																	// cameras

	}

	public boolean isInPolygon(iLocation location, double latitude, // Only
																	// works for
																	// convex
																	// polygons!
			double longitude) {

		int i;
		int j;
		ArrayList<LatLng> points = location.location_gps;
		if (points.size() != 0) {
			boolean result = false;
			for (i = 0, j = points.size() - 1; i < points.size(); j = i++) {
				if ((points.get(i).latitude > latitude) != (points.get(j).latitude > latitude)
						&& (longitude < (points.get(j).longitude - points
								.get(i).longitude)
								* (latitude - points.get(i).latitude)
								/ (points.get(j).latitude - points.get(i).latitude)
								+ points.get(i).longitude)) {
					result = !result;
				}
			}
			return result;
		} else
			return isInCameraRadius(location, latitude, longitude); // if no
																	// polygon
																	// is
																	// defined,
																	// check if
																	// the user
																	// is in the
																	// radius of
																	// one of
																	// the
																	// cameras
	}

	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
		
	}

}
