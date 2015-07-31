package myClass;

public class myBeacon
{public String location_id,id, camera_id, beacon_major, beacon_minor,beacon_uuid;
public double max_dist,min_dist,beacon_alt,beacon_lat,beacon_long;
public long start_time=-1;  //timestamp a which beacon became visible
public long last_seen=-1;
public int rssi;
}
