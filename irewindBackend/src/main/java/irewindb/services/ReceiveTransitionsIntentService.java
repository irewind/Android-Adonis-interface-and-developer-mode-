package irewindb.services;

import android.app.IntentService;
import android.content.Intent;

public class ReceiveTransitionsIntentService extends IntentService {
    
    /**
     * Sets an identifier for the service
     */
    public ReceiveTransitionsIntentService() {
        super("ReceiveTransitionsIntentService");
        System.out.println("constructor receiveT..");
    }
    /**
     * Handles incoming intents
     *@param intent The Intent sent by Location Services. This
     * Intent is provided
     * to Location Services (inside a PendingIntent) when you call
     * addGeofences()
     */
    String mesaj="";
    @Override
    protected void onHandleIntent(Intent intent) {
  //  	try {
			
		
    	System.out.println("<<<<<<<<<<<<<<<<<<<<, onhandle intent <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    	
       /* // First check for errors
        if (GoogleApiClient.hasError(intent)) {
            // Get the error code with a static method
            int errorCode = LocationClient.getErrorCode(intent);
            // Log the error
            Log.e("ReceiveTransitionsIntentService",
                    "Location Services error: " +
                    Integer.toString(errorCode));
            /*
             * You can also send the error code to an Activity or
             * Fragment with a broadcast Intent
             */
        /*
         * If there's no error, get the transition type and the IDs
         * of the geofence or geofences that triggered the transition
         */
    //    } else {
            // Get the type of transition (entry or exit)
   /*         int transitionType =
                    LocationClient.getGeofenceTransition(intent);
            // Test that a valid transition was reported
            if (
                (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER)
                 ||
                (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT)
               ) {
                List <Geofence> triggerList =LocationClient.getTriggeringGeofences(intent);
           if(transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) mesaj="enter";
           else mesaj="exit";
           for(int i=0;i<triggerList.size();i++)
           mesaj+="--trigger_id="+triggerList.get(i).getRequestId();
           
   /*    ( (MyApp)    getApplication()).getMainActivityRefference().runOnUiThread(new Runnable() {
		
		@Override
		public void run() {
		
			Toast.makeText( ( (MyApp)    getApplication()).getMainActivityRefference(), mesaj, Toast.LENGTH_LONG).show();
		}
	});
        */       
  //          }
        // An invalid transition was reported
 //       } 
  //  	} catch (NullPointerException e) {
			// TODO: handle exception
//		}
    	}
    	
  
}