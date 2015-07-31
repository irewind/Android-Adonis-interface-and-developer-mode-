package myClass;

public class retry_compose
{public String user,auth_service,uid,password,location_id,sess_id, subsess_id, server_address, path;

public retry_compose(String user,String auth_service, String uid, String password,
		String location_id, String sess_id, String subsess_id,
		String server_address, String path) {
	super();
	this.user = user;
	this.auth_service=auth_service;
	this.uid = uid;
	this.password = password;
	this.location_id = location_id;
	this.sess_id = sess_id;
	this.subsess_id = subsess_id;
	this.server_address = server_address;
	this.path = path;
}
	
}