package utils;

import java.util.ArrayList;

import myClass.iLocation;



public class algorithms {
	
	static int MAXIMUM_DISTANCE=300;
	public static int getClosestLocation(ArrayList<iLocation> locs,double lat,double lng)   
	{return getClosestLocation(locs, lat, lng, MAXIMUM_DISTANCE);}
	
	public static int getClosestLocation(ArrayList<iLocation> locs,double lat,double lng, int MAX_DISTANCE)//euclidian dist
	{double min_dist=measure(locs.get(0).lat, locs.get(0).lng, lat, lng);
	int best_index=0;
		
		
	for(int i=0;i<locs.size();i++)
	{double dist=measure(locs.get(i).lat, locs.get(i).lng, lat, lng);
	//Math.sqrt((locs.get(i).lat-lat)*(locs.get(i).lat-lat)+(locs.get(i).lng-lng)*(locs.get(i).lng-lng));
	if(dist<min_dist)
	{min_dist=dist;
	best_index=i;}
	}
	//System.out.println("best index="+best_index);
	//System.out.println("min_dist="+min_dist);
		if(min_dist<MAX_DISTANCE)
		
		return best_index;
		
		else return -1;
	}
	
	
	
	
	
	
	public static double measure(double lat1,double lon1,double lat2,double lon2){  
		double R = 6378.137; // Radius of earth in KM
		double dLat = (lat2 - lat1) * Math.PI / 180;
		double dLon = (lon2 - lon1) * Math.PI / 180;
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	    Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
	    Math.sin(dLon/2) * Math.sin(dLon/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double d = R * c;
	    return d * 1000; // meters
	}
	
	
	
	
	
	
	

}
