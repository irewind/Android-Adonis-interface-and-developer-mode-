package myClass;

import android.os.Parcel;
import android.os.Parcelable;

public class iUser implements android.os.Parcelable  {
	
	private String username, id, password, auth_service;

	public static Parcelable.Creator<iUser> getCreator() {
		return CREATOR;
	}

	public iUser() {
		super();
		
	}
	
	public iUser(String username, String id, String password, String auth_service) {
		super();
		this.username = username;
		this.id = id;
		this.password = password;
		this.auth_service = auth_service;
	}
	public String getUsername() {
		return username;
	}
	

	public void setUsername(String username) {
		this.username = username;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAuth_service() {
		return auth_service;
	}

	public void setAuth_service(String auth_service) {
		this.auth_service = auth_service;
	}


	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(username);
		dest.writeString(id);
		dest.writeString(password);
		dest.writeString(auth_service);
	}
	
	public iUser(Parcel in){
		this.username = in.readString();
		this.id = in.readString();
		this.password = in.readString();
		this.auth_service = in.readString(); }

	public static final Parcelable.Creator<iUser> CREATOR = new Parcelable.Creator<iUser>() {
        public iUser createFromParcel(Parcel pc) {
            return new iUser(pc);
        }
        public iUser[] newArray(int size) {
            return new iUser[size];
        }
  };
}
