package myClass;





import java.util.List;
import java.util.Locale;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import utils.IrewindBackend;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

public class GeocoderHelper
{
    private static final AndroidHttpClient ANDROID_HTTP_CLIENT = AndroidHttpClient.newInstance(GeocoderHelper.class.getName());

    private boolean running = false;
   
    public void fetchCityName( final Location location)
    {
        if (running)
            return;

        new AsyncTask<Void, Void, String>()
        { 
            protected void onPreExecute()
            {   
                running = true;
            };

            @Override
            protected String doInBackground(Void... params)
            {
                String cityName = null;

                boolean is_present=true;
                try {
                	is_present=Geocoder.isPresent();
				} catch (NoSuchMethodError e) {
					// TODO: handle exception
				}
                if (is_present)
                {
                    try
                    {Context context= IrewindBackend.Instance.getContext();
                    if(context!=null){
                        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        if (addresses.size() > 0)
                        {
                            cityName = addresses.get(0).getLocality();
                        }}
                    }
                    catch (Exception ignored)
                    {
                        // after a while, Geocoder start to trhow "Service not availalbe" exception. really weird since it was working before (same device, same Android version etc..
                    }
                }

                if (cityName != null) // i.e., Geocoder succeed
                {
                    return cityName;
                }
                else // i.e., Geocoder failed
                {
                    return fetchCityNameUsingGoogleMap();
                }
            }

            // Geocoder failed :-(
            // Our B Plan : Google Map
            private String fetchCityNameUsingGoogleMap()
            {
                String googleMapUrl = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + location.getLatitude() + ","
                        + location.getLongitude() + "&sensor=false&language=fr";

                try
                {
                    JSONObject googleMapResponse = new JSONObject(ANDROID_HTTP_CLIENT.execute(new HttpGet(googleMapUrl),
                            new BasicResponseHandler()));

                    // many nested loops.. not great -> use expression instead
                    // loop among all results
                    JSONArray results = (JSONArray) googleMapResponse.get("results");
                    for (int i = 0; i < results.length(); i++)
                    {
                        // loop among all addresses within this result
                        JSONObject result = results.getJSONObject(i);
                        if (result.has("address_components"))
                        {
                            JSONArray addressComponents = result.getJSONArray("address_components");
                            // loop among all address component to find a 'locality' or 'sublocality'
                            for (int j = 0; j < addressComponents.length(); j++)
                            {
                                JSONObject addressComponent = addressComponents.getJSONObject(j);
                                if (result.has("types"))
                                {
                                    JSONArray types = addressComponent.getJSONArray("types");

                                    // search for locality and sublocality
                                    String cityName = null;

                                    for (int k = 0; k < types.length(); k++)
                                    {
                                        if ("locality".equals(types.getString(k)) && cityName == null)
                                        {
                                            if (addressComponent.has("long_name"))
                                            {
                                                cityName = addressComponent.getString("long_name");
                                            }
                                            else if (addressComponent.has("short_name"))
                                            {
                                                cityName = addressComponent.getString("short_name");
                                            }
                                        }
                                        if ("sublocality".equals(types.getString(k)))
                                        {
                                            if (addressComponent.has("long_name"))
                                            {
                                                cityName = addressComponent.getString("long_name");
                                            }
                                            else if (addressComponent.has("short_name"))
                                            {
                                                cityName = addressComponent.getString("short_name");
                                            }
                                        }
                                    }
                                    if (cityName != null)
                                    {
                                        return cityName;
                                    }
                                }
                            }
                        }
                    }
                }
                catch (Exception ignored)
                {
                    ignored.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(String cityName)
            {
                running = false;
                if (cityName != null)
                { 
                	
                	IrewindBackend.Instance.location_change_Out(cityName);
                	
                    // Do something with cityName
                    Log.i("GeocoderHelper", cityName);
                }
            };
        }.execute();
    }
}