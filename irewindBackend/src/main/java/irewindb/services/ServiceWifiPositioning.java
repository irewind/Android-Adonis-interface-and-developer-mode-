package irewindb.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import myClass.iUser;
import myClass.myBeacon;
import utils.request;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

public class ServiceWifiPositioning extends Service{
	WifiManager wifi; 
//	MyWifi[] wifis;
	iUser user;
	public static String fileName;
	
	ArrayList<myBeacon> beacons;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	int count=0;
	int state=0;
	
	public static void write(String text)
	{
		 File myFile = new File(fileName);

			if (!myFile.exists()) {try {
				File myFileDir = new File(Environment
						.getExternalStorageDirectory().getAbsolutePath()
						+ "/irewindb");
				myFileDir.mkdirs();
				
					myFile.createNewFile();
				
				
				

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}}
		
		try {FileOutputStream fOut = new FileOutputStream(myFile, true);
		OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
		myOutWriter.append(text);
		myOutWriter.close();
		fOut.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}}
	
	 @Override
	 public int onStartCommand(Intent it, int a, int b)
	 {
		 String mydate = java.text.DateFormat.getDateTimeInstance().format(
					Calendar.getInstance().getTime());
		 
	
fileName=Environment.getExternalStorageDirectory()
.getAbsolutePath() + "/irewindb/log" +mydate+".txt";
	write("Session started at " + mydate+"\n");		

 
	//	 wifis=new MyWifi[4];
	//	 wifis[0]=new MyWifi("irw-35266", "f4:dc:f9:ea:12:ed", 5,1);
	//	 wifis[1]=new MyWifi("irw-35265", "f4:dc:f9:ea:13:0b", 5,2);
	//	 wifis[2]=new MyWifi("irw-35227", "f4:dc:f9:ea:14:41", 4,2);
	//	 wifis[3]=new MyWifi("irewind.4g.3", "90:4e:2b:13:49:2d", 4,1);
		 
		
		 
		 wifi = (WifiManager) getSystemService(this.WIFI_SERVICE);
	 wifi.setWifiEnabled(true);
	 
	 
	 
	  SharedPreferences prefs = getSharedPreferences(
			  "irewindb", Context.MODE_PRIVATE);
	 
		user=new iUser(prefs.getString("user","" ), prefs.getString("id", ""), prefs.getString("password", ""), prefs.getString("auth_service", ""));
	
	 new Thread(new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
		beacons=request.getBeaconsList(user, "32");	
		
		 wifi.startScan();
		}
	}).start();
	 
	
	
	 registerReceiver(new BroadcastReceiver()
     {
         @Override
         public void onReceive(Context c, Intent intent) 
         { String print="";
         String toW="Scan "+java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime())+"\n";
         
          System.out.println(wifi.getScanResults().size()+" results"); 
         List<ScanResult> wlist = wifi.getScanResults();
       
         for(int i=0;i<wlist.size();i++)
          {myBeacon w=getWifi(wlist.get(i).SSID, wlist.get(i).BSSID);
          System.out.println(wlist.get(i).SSID+"  "+ wlist.get(i).BSSID);
          if(w!=null)
          { print+="etaj="+w.beacon_alt+" cam="+w.camera_id+" - "+wlist.get(i).level+"\n";
        	  toW+="rssi("+w.camera_id+")="+wlist.get(i).level+"  ";
          w.rssi=wlist.get(i).level;}
          }
      //   int level=-1,roomNo=-1, maxL=-9999,maxR=-9999;
         int wNo=-1,maxW=-9999;
         
         for(int i=0;i<beacons.size();i++)
        	 if(beacons.get(i).rssi!=0&&beacons.get(i).rssi>maxW){maxW=beacons.get(i).rssi; wNo=i;}
         
         int siguranta=100;
         for(int i=0;i<beacons.size();i++)
          if(beacons.get(i).rssi!=0&&i!=wNo&&beacons.get(wNo).rssi<beacons.get(i).rssi+siguranta){siguranta=beacons.get(wNo).rssi-beacons.get(i).rssi;}
         if(wNo>=0&&siguranta>beacons.get(wNo).min_dist)
         { print+="etaj "+beacons.get(wNo).beacon_alt+" camera "+beacons.get(wNo).camera_id+" diferenta="+siguranta+"\n\n";
         toW+="\n=>cam="+beacons.get(wNo).camera_id+" etaj="+beacons.get(wNo).beacon_alt+" diferenta="+siguranta+"\n";
         }
         
         else if(wNo>=0)
         { print+="unknown"+" diferenta="+siguranta+"\n\n";
         toW+="\n=>unknown"+beacons.get(wNo).camera_id+" etaj="+beacons.get(wNo).beacon_alt+" diferenta="+siguranta+"\n";
         }
        	 
			write(toW);
		
       /*  else{ 
          int[] levels=new int[6]; levels[1]=0; levels[2]=0; levels[3]=0; levels[4]=0; levels[5]=0; 
          int[] rooms=new int[3];  rooms[1]=0; rooms[2]=0;
          
          for(int i=0;i<beacons.size();i++)
          {levels[beacons.get(i).beacon_alt]+=beacons.get(i).rssi;
          rooms[beacons.get(i).roomNo]+=beacons.get(i).rssi;}
          
         
          
          for(int i=0;i<levels.length;i++)
        	  if(levels[i]!=0&&levels[i]>maxL)
        	  {level=i;
        	  maxL=levels[i];}
          
          for(int i=0;i<levels.length;i++)
          if(levels[i]!=0&&i!=level&&levels[level]-levels[i]<15) {level=-1; i=levels.length;}
          
          
          for(int i=0;i<rooms.length;i++)
        	  if(rooms[i]!=0&&rooms[i]>maxR)
        	  {roomNo=i;
        	  maxR=rooms[i];}
          
          for(int i=0;i<rooms.length;i++)
              if(rooms[i]!=0&&i!=roomNo&&rooms[roomNo]-rooms[i]<12) {roomNo=-1; i=rooms.length;}
     
              if(level==-1)
              {print+="SCARI\n\n";
           }
             else if(roomNo==-1)
          { print+="HOL\n\n";}
          else  {print+="etaj "+level+" camera "+roomNo+"\n\n";
        	 }}*/
               Toast t=  Toast.makeText(ServiceWifiPositioning.this, print, Toast.LENGTH_LONG);
             	t.setDuration(2500);
             	t.show();
          wifi.startScan();
         }
     }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));   
	 
	 
	
	 
	 
		return START_STICKY; }
	 
	 public void entered()
	 {state=1;
	 Toast t=  Toast.makeText(ServiceWifiPositioning.this, "entered", Toast.LENGTH_SHORT);
 	t.setDuration(300);
 	t.show();
		 try {
		    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
		    r.play();
		} catch (Exception e) {
		    e.printStackTrace();
		}}
	 
	 public void left()
	 {state=0;
	 Toast t=  Toast.makeText(ServiceWifiPositioning.this, "left", Toast.LENGTH_SHORT);
 	t.setDuration(300);
 	t.show();
 	ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
 	toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200); }
	 
	 protected static double calculateAccuracy(int txPower, double rssi) {
		  if (rssi == 0) {
		    return -1.0; // if we cannot determine accuracy, return -1.
		  }

		  double ratio = rssi*1.0/txPower;
		  if (ratio < 1.0) {
		    return Math.pow(ratio,10);
		  }
		  else {
		    double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;    
		    return accuracy;
		  }
		}   
	 
	 public myBeacon getWifi(String ssid,String bssid)
	 {for(int i=0;i<beacons.size();i++)
		try{ if(bssid.equalsIgnoreCase(beacons.get(i).beacon_uuid))
			 return beacons.get(i);}catch (Exception e){}
	 return null;
	 }
	 class MyWifi
	 {String ssid,bssid;
		 int roomNo,level;
	 int rssi;
	 
	 public MyWifi(String ssid,String bssid,int level,int roomNo)
	 {this.ssid=ssid;
	 this.bssid=bssid;
	 this.roomNo=roomNo;
	 this.level=level;}
	
	 
	 }
	 
	 
	
	 
}
