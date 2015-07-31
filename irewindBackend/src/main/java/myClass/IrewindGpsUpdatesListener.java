package myClass;

public interface IrewindGpsUpdatesListener {
public void noGPS();
public void initEnded(String status); //ok, fail - no internet
public void locationChanged(iLocation location);
public void notInAnIrwLocation(String city_name);

}
