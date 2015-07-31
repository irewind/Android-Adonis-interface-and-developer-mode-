package myClass;

public class myCamera
{public String url;
public int camera_id;
public String last_start="0";
public int curent_segment_index=0;
public boolean recording=false;
public long beacon_last_seen=0;
public String minor;
public double lat,lng,radius;


public myCamera(double lat, double lng, double radius) {
	super();
	this.lat = lat;
	this.lng = lng;
	this.radius = radius;
}

	public myCamera(int camera_id, double lat, double lng, double radius) {
		super();
		this.camera_id = camera_id;
		this.lat = lat;
		this.lng = lng;
		this.radius = radius;
	}

public myCamera(String Url, String minor)
{url=Url;
this.minor=minor;}

public myCamera(String Url, int camera_id, String minor)
{url=Url;
this.camera_id=camera_id;
this.minor=minor;}

}