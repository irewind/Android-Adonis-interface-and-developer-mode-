package utils;

import myClass.iUser;
import myClass.iVideo;
import android.app.Activity;

public interface SimpleInterface {
	
	
	
	public void SetContext(Activity context); //call at application start
	
	public String authGuest(); 
	
	public String auth(iUser user);
	
	public iUser getUser();
	
	public  void startLocationTracking();
	
	public  void stopLocationTracking();
	
	public  void UpdateLocalDatabase() ;
	
	public  iVideo[] listVideos();
	
	
	

}
