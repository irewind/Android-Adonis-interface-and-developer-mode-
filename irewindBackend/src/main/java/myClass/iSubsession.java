package myClass;

import utils.DataBase;
import utils.IrewindBackend;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class iSubsession implements android.os.Parcelable {
	
	private String session_id, subsession_id;
	private iLocation location;
	private iUser user;
	private long TimeStampStart=-1, TimeStampEnd=-1;
	private DataBase dataBase;
	private Context context;
	public static Parcelable.Creator<iSubsession> getCreator() {
		return CREATOR;
	}
	public iSubsession(String session_id, String subsession_id,
			iLocation location, iUser user) {
		super();
		this.session_id = session_id;
		this.subsession_id = subsession_id;
		this.location = location;
		this.user = user;
	}
	
	public void setContext(Context context)
	{this.context=context;
	dataBase=IrewindBackend.Instance.getDatabase();}
	
	public DataBase getDataBase()
	{return dataBase;}
	
	public long getTimeStampStart() {
		if(TimeStampStart==-1) TimeStampStart=dataBase.getMinTimeStamp(session_id, user.getId());
		return TimeStampStart;
	}
	public long getTimeStampEnd() {
		if(TimeStampEnd==-1) TimeStampEnd=dataBase.getMaxTimeStamp(session_id, user.getId());
		return TimeStampEnd;
	}
	public String getSession_id() {
		return session_id;
	}
	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}
	public String getSubsession_id() {
		return subsession_id;
	}
	public void setSubsession_id(String subsession_id) {
		this.subsession_id = subsession_id;
	}
	public iLocation getLocation() {
		return location;
	}
	public void setLocation(iLocation location) {
		this.location = location;
	}
	public iUser getUser() {
		return user;
	}
	public void setUser(iUser user) {
		this.user = user;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(session_id);
		dest.writeString(subsession_id);
		dest.writeParcelable(user, flags);
		dest.writeParcelable(location, flags);
	}
	
	public iSubsession(Parcel in){
     
       session_id=in.readString();
       subsession_id=in.readString();
       in.readString();
       
       user= new iUser(in.readString(),in.readString(),in.readString(),in.readString());
       in.readString();
       location=new iLocation(in.readString(),in.readString(),in.readString(),in.readString());
       System.out.println(location.getLocation_name()+" "+location.getLocation_id()+" "+location.getLocation_url()+" "+location.getLocation_type());
    }
	
	public static final Parcelable.Creator<iSubsession> CREATOR = new Parcelable.Creator<iSubsession>() {
        public iSubsession createFromParcel(Parcel pc) {
            return new iSubsession(pc);
        }
        public iSubsession[] newArray(int size) {
            return new iSubsession[size];
        }
  };

  
  
}
