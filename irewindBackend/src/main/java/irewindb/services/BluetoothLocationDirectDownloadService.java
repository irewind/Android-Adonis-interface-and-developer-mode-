package irewindb.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BluetoothLocationDirectDownloadService extends Service{/*
	static { 
    	System.loadLibrary("ffmpeg");
    	System.loadLibrary("ffmpeg-test-jni");  
    } 
	 
	
	public static native void DownloadSegment(String camera_url, String save_path, String miliseconds_length);
	int MAXIMUM_DISTANCE=300;
    
	public double SEGMENT_LENGTH= 30000;
	public double fps=30;
	public String SESSION_PATH;
	boolean first_time=true;
	ArrayList<myCamera> Cameras;
	long startDiscoveryTime=0;
//	ArrayList<myBluetoohDevice> bluetooths;
	ArrayList<Boolean> discovered= new ArrayList<Boolean>();
	
	
CountDownTimer ct= new CountDownTimer(36000000,300) {
		
		@Override
		public void onTick(long arg0) {
			if(!mBluetoothAdapter.isEnabled()) mBluetoothAdapter.enable();
			else if(!mBluetoothAdapter.isDiscovering()) {
				for(int i=0;i<Cameras.size();i++)
				{SetVisibility(i, discovered.get(i));
				discovered.remove(i);
				discovered.add(i,false);
				}
				mBluetoothAdapter.startDiscovery();
			 Log.i("bluetooth","Start Discovery");
			startDiscoveryTime=System.currentTimeMillis();}
			if(System.currentTimeMillis()>startDiscoveryTime+10000 && mBluetoothAdapter.isDiscovering())
			{mBluetoothAdapter.cancelDiscovery();
			
			 Log.i("bluetooth","Force cancel Discovery");
			}
				
		}
		
		
		
		
		@Override
		public void onFinish() {}
	};
			int user_id, session_id;
			String user, password;
	//		MyApp appState;
	@Override
	 public int onStartCommand(Intent it, int a, int b)
	 { Log.i("bluetooth","BluetoothLocDD on stard command");
  //  myBluetoohDevice bl1=new myBluetoohDevice("94:63:D1:C4:AC:A1", 1);
   //	myBluetoohDevice bl2=new myBluetoohDevice("04:E4:51:B8:41:AD",2);
//	bluetooths=new ArrayList<myBluetoohDevice>();
//	bluetooths.add(bl1);
//	bluetooths.add(bl2);
   
   if(it!=null){
		 user_id=it.getIntExtra("user_id", 0);
	 session_id=it.getIntExtra("session_id",0);
	 Log.i("bluetooth","session_id ="+session_id);
		user=it.getStringExtra("user");
		password=it.getStringExtra("password");
	
		 Log.i("bluetooth","BluetoothLocDD started with location_index="+999);
		 SESSION_PATH=Environment.getExternalStorageDirectory().getPath()+"/Irewind/irw_"+session_id+"/";
		    File f=new File(SESSION_PATH);
		    if(!f.exists()) f.mkdirs();
		
		
//	appState	 = ((MyApp)getApplicationContext());
	
//	v = (Vibrator) appState.getSystemService(Context.VIBRATOR_SERVICE);
	
	
	Cameras=new ArrayList<myCamera>();
//	myCamera c= new myCamera("rtsp://192.168.1.100:8554/proxyStream");
//	myCamera c2= new myCamera("rtsp://predeal.irewind.com:8208/proxyStream");
	//rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov
//	Cameras.add(c);
//	Cameras.add(c2);
	
	ct.start();
		 }
		setupBluetooth();
		 return START_STICKY;}
	
*/	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	public void onDestroy() {
	    super.onDestroy();
	    unregisterReceiver(mReceiver);
	    
	    ct.cancel();}
	
	
	 
	BluetoothAdapter mBluetoothAdapter;
	public void setupBluetooth()
	{ mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	 

	
	IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
	registerReceiver(mReceiver, filter);
	
	
	
	
	}
	
	boolean discovered1=false,discovered2=false;
	
	Vibrator v;
	 // Vibrate for 500 milliseconds
	
	
	public void SetVisibility(int cam,boolean visibility)
	{ Log.i("bluetooth","SET VISIBILITY = "+visibility);
	//appState.getSettingsFragmentRefference().turn(cam+1, visibility);
	
	if(visibility)
	{if(Long.parseLong(Cameras.get(cam).last_start)+SEGMENT_LENGTH<System.currentTimeMillis())
		{
		 
		Cameras.get(cam).last_start=System.currentTimeMillis()+"";
		
		int curent_segment=++Cameras.get(cam).curent_segment_index;
		
		
//		Toast.makeText(appState.getMainActivityRefference(), "Recording from: "+ Cameras.get(cam).url,Toast.LENGTH_LONG).show();
	//	Rec r=new Rec(Cameras.get(cam).url,cam,curent_segment , SESSION_PATH+"camera"+cam+"_segment"+curent_segment+".mp4", SEGMENT_LENGTH/1000*fps,SEGMENT_LENGTH ,user_id+"",session_id+"","999",appState, false);
		
//       r.run();
      first_time=false;
		
		}}
		
		
		}
	
	// Create a BroadcastReceiver for ACTION_FOUND
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        try{
	    
	        // When discovery finds a device
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            // Get the BluetoothDevice object from the Intent
	        	
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	          if(device.getName().startsWith("rtsp")){  
	            int index=-1;
	            for(int i=0;i<Cameras.size();i++)
	            {if(Cameras.get(i).url.equalsIgnoreCase(device.getName())) index=i;}
	            if(index==-1)
	            {  Cameras.add(new myCamera(device.getName(),"Na"));
	            discovered.add(true);	}
	            else {discovered.remove(index);
	            discovered.add(index, true);
	            }	
	          }
	            
	            Log.i("bluetooth",device.getName() + "  --  " + device.getAddress());
	        //    if(device.getName().startsWith("GT-S5570")) {discovered1=true; SetVisibility(0, true);}
	        //    if(device.getName().startsWith("XT910")) {discovered2=true; SetVisibility(1, true);}
	        }}catch(NullPointerException e){}
	    }
	};
}
 


class myBluetoohDevice
{public myBluetoohDevice(String mAC, int camera_id) {
		super();
		MAC = mAC;
		this.camera_id = camera_id;
	}
String MAC;
int camera_id;*/}



