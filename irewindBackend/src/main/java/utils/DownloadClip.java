package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import myClass.videoclip;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import irewindb.R;



class DownloadClip extends AsyncTask<String, Integer, String> {
	Context context;
	String session_id, subsession_id;
	int procent=-18;
	Boolean hd=false;
	String user, user_id, password,auth_service;
	
	String res_id;
	String String_url;
	String server_address;
	String save_path;

    public DownloadClip(Context context, String session_id,
			String subsession_id, Boolean hd, String user,String auth_service, String user_id,
			String password, String res_id, String string_url,
			String server_address, String save_path) {
		super();
		this.context = context;
		this.session_id = session_id;
		this.subsession_id = subsession_id;
		this.hd = hd;
		this.user = user;
		this.auth_service= auth_service;
		this.user_id = user_id;
		this.password = password;
		
		this.res_id = res_id;
		String_url = string_url;
		this.server_address = server_address;
		this.save_path = save_path;
	}



	@Override
    protected String doInBackground(String...useless) { //not used anywhere. the code is better organised if all the data comes throgh the constructor
    	try {
    	
		} catch (NullPointerException e) {
			// TODO: handle exception
		}
        try {
       
        	
            URL url = new URL(String_url);
            Log.d("DownloadFile.doInBackground","starting downloading from "+url);
           
            
            HttpURLConnection http = null;
            if (url.getProtocol().toLowerCase().equals("https")) {
        	    request.trustAllHosts();
        		HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
        		https.setHostnameVerifier(request.DO_NOT_VERIFY);
        		http = https;
        	} else {
        		http = (HttpURLConnection) url.openConnection();
        	}
            
            
            http.connect();
            
            // this will be useful so that you can show a typical 0-100% progress bar
            long fileLength = http.getContentLength();
            System.out.println("fileLenght="+fileLength);
if(fileLength<0) fileLength=2146551996;
            // download the file
            InputStream input =http.getInputStream();
       
            File film= new File(save_path);
            film.getParentFile().mkdirs();
          
            OutputStream output = new FileOutputStream(film);

            byte data[] = new byte[10240];
            long total = 0;
            int count;
            DataBase db=IrewindBackend.Instance.getDatabase();
            while (((count = input.read(data)) != -1)) {
            	
                total += count;
          IrewindBackend.Instance.setDwonloadProgress((float)((double)total/(double)fileLength));
                if((int)(total*100/fileLength)>=procent+10){ 
                	System.out.println("procent="+procent+" curent="+(int)(total*100/fileLength));
              procent= (int)(total*100/fileLength);
              
             
             
             
              System.out.println("hd="+hd);
              if(hd){
              db.updateField(session_id, subsession_id, "download_progress", procent+"");
            //  appState.getEditedMoviesFragmentRefference().refresh();
              }
              else{
                  db.updateField(session_id, subsession_id, "download_progress", procent+"");
             //     appState.getMoviesFragmentRefference().refresh();
                  }}
                 
               
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
        }
        return "useless";      
    }
   
   
   
    @Override
    protected void onPostExecute(String useless) {
        super.onPostExecute(useless);
        
        IrewindBackend.Instance.setDwonloadProgress(1.0f);
      	
    	if(hd) {
    		Toast.makeText(context,R.string.download_finished_hd,  Toast.LENGTH_SHORT).show();
            
		DataBase db=IrewindBackend.Instance.getDatabase();
		System.out.println("will update video with save path="+save_path);
        db.UpdateVideoStatus(context.getResources().getString(R.string.saved), session_id, subsession_id, user_id,save_path,null);
     //   appState.getEditedMoviesFragmentRefference().refresh();
        }
        else
       {
        	Toast.makeText(context,R.string.download_finished_prev,  Toast.LENGTH_SHORT).show();
            
   		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			   	nameValuePairs.add(new BasicNameValuePair("auth_user", user));
			   	nameValuePairs.add(new BasicNameValuePair("auth_service", auth_service));
			   	nameValuePairs.add(new BasicNameValuePair("first_index", "0"));
			   	nameValuePairs.add(new BasicNameValuePair("sqlstatement", "SELECT* FROM irw_users_clips WHERE res_id='"+res_id+"'"));
			 
			   	try {
				String str=request.PostRequest(nameValuePairs, password, "/video-processor-secured/services/utils/sql",server_address);
		
				JSONObject o=new JSONObject(str);
				JSONArray ar=new JSONArray(o.getString("resultset"));
				JSONObject ob=(JSONObject) ar.get(0);
				String exp=ob.getString("expiration_date");
			
				DataBase db=IrewindBackend.Instance.getDatabase();
				System.out.println("will update path low with:"+save_path);
				
				if(user_id==null||user_id.equalsIgnoreCase(""))
				{SharedPreferences prefs = context.getSharedPreferences("irewindb", Context.MODE_PRIVATE);
				user_id=prefs.getString("id", "");
				}
				videoclip v=db.subsession(session_id, subsession_id);
				v.preview_res=res_id;
				request.DownloadThumb(v,server_address);
				
			       db.UpdateVideoStatus(exp, session_id, subsession_id, user_id,null,save_path);
			    //   appState.getMoviesFragmentRefference().refresh();
			     
			    } catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
			}
		}).start();
		
        	
       
  
   
   }
      
    }




}