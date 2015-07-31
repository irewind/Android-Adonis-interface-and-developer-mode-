package myClass;

import java.util.ArrayList;

import android.os.Parcel;

import com.google.android.gms.maps.model.LatLng;
//TODO REMOVE PARCELABE
public class iLocation implements android.os.Parcelable {
	 private String location_id,location_name, location_type, location_url;
	 
	 
		
		public double lat,lng;
		public String imagine;
		public LatLng l;
		
		public int area_radius;
		public double maxLat ,minLat, maxLng,minLng;
	public int max_accuracy, location_area_min_interval, location_area_max_interval,area_radius_interval,outside_area_radius_interval;	
    public ArrayList<LatLng> location_gps;
	 

	public String getLocation_id() {
		return location_id;
	}

	public void setLocation_id(String location_id) {
		this.location_id = location_id;
	}

	public String getLocation_name() {
		return location_name;
	}

	public void setLocation_name(String location_name) {
		this.location_name = location_name;
	}

	public String getLocation_type() {
		return location_type;
	}

	public void setLocation_type(String location_type) {
		this.location_type = location_type;
	}

	public String getLocation_url() {
		return location_url;
	}

	public void setLocation_url(String location_url) {
		this.location_url = location_url;
	}

	public iLocation()
	{super();}
	
	public iLocation(String location_id, String location_name,
			String location_type, String location_url) {
		super();
		this.location_id = location_id;
		this.location_name = location_name;
		this.location_type = location_type;
		this.location_url = location_url;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
	    dest.writeString(location_id);
		dest.writeString(location_name);
		dest.writeString(location_type);
		dest.writeString(location_url);
	}
	 
	public String toString()
	  {
		return location_id+"."+location_name+" type:"+location_type;}
	

}
