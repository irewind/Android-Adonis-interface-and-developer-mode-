package utils;

import myClass.iUser;
import myClass.iVideo;
import android.app.Activity;

public class SimpleImplementation implements SimpleInterface {
	
	public static iUser  user;
	public static Activity context;
	private static final SimpleImplementation INSTANCE = new SimpleImplementation();

	    private SimpleImplementation() {
	        if (INSTANCE != null) {
	            throw new IllegalStateException("Already instantiated");
	        }
	    }

	    public static SimpleImplementation getInstance() {
	        return INSTANCE;
	    }
	
	    
	public void SetContext(Activity context)
	{SimpleImplementation.context=context;}
	    
	public String authGuest()
	{return "error text";}
	
	public String auth(iUser user)
	{return "error text";}
	
	public iUser getUser()
	{return user;}
	
	public void startLocationTracking()
	{}
	
	public void stopLocationTracking()
	{}
	
	public void UpdateLocalDatabase()  
	{}
	
	public iVideo[] listVideos()
	{return null;}
	
	
	

}
