package utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaMetadataRetriever;
import android.os.Environment;

import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.TimeToSampleBox;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;



public class LocalCompose {
	
	
	public static void Compose(ArrayList<myRecord> records,String outFile, String outThumb) throws FileNotFoundException, IOException
	{
	CroppedTrack cropTrack=null;
	 List<Track> videoTracks = new LinkedList<Track>();
	/*	Movie movie;
	movie = MovieCreator.build(new FileInputStream(records.get(0).path).getChannel());
	System.out.println(records.get(0).path+"");
	List<Track> tracks = movie.getTracks();
    movie.setTracks(new LinkedList<Track>());
    double startTime1 =(Double.parseDouble(records.get(0).segm_start)-Double.parseDouble(records.get(0).segm_reference_start))/1000;
    double endTime1 =(Double.parseDouble(records.get(0).segm_end)-Double.parseDouble(records.get(0).segm_reference_start))/1000;
    System.out.println("start="+startTime1+" end="+endTime1);
  	for (Track track : tracks) {
        if (track.getSyncSamples() != null && track.getSyncSamples().length > 0) {
            startTime1 = correctTimeToSyncSample(track, startTime1, false);
            endTime1 = correctTimeToSyncSample(track, endTime1, true);}}
    
		
    for (Track track : tracks) {
        long currentSample = 0;
        double currentTime = 0;
        double lastTime = 0;
        long startSample1 = -1;
        long endSample1 = -1;
        

        for (int i = 0; i < track.getDecodingTimeEntries().size(); i++) {
            TimeToSampleBox.Entry entry = track.getDecodingTimeEntries().get(i);
            for (int j = 0; j < entry.getCount(); j++) {


                if (currentTime > lastTime && currentTime <= startTime1) {
                    // current sample is still before the new starttime
                    startSample1 = currentSample;
                }
                if (currentTime > lastTime && currentTime <= endTime1) {
                    // current sample is after the new start time and still before the new endtime
                    endSample1= currentSample;
                }
              
                lastTime = currentTime;
                currentTime += (double) entry.getDelta() / (double) track.getTrackMetaData().getTimescale();
                currentSample++;
            }
        }

        //  movie.addTrack(new AppendTrack(new CroppedTrack(track, startSample1, endSample1), new CroppedTrack(track, startSample2, endSample2)));
        cropTrack=new CroppedTrack(track, startSample1, endSample1);
	    // movie.addTrack(cropTrack); 
    }
    */
	 ArrayList<Movie> movies=new ArrayList<Movie>();
	 
    for(int j=0;j<records.size();j++)
    {
    	
    
    	Movie movie = MovieCreator.build(new FileInputStream(records.get(j).path).getChannel());
    	System.out.println("j="+j);
    	System.out.println("path="+records.get(j).path+"");
    	List<Track> tracks_aux = movie.getTracks();
        movie.setTracks(new LinkedList<Track>());
        double startTime1_aux =(Double.parseDouble(records.get(j).segm_start)-Double.parseDouble(records.get(j).segm_reference_start))/1000;
        double endTime1_aux =(Double.parseDouble(records.get(j).segm_end)-Double.parseDouble(records.get(j).segm_reference_start))/1000;
     
        System.out.println("start="+startTime1_aux+" end="+endTime1_aux);
        if(startTime1_aux<0) startTime1_aux=0;
       if (endTime1_aux<startTime1_aux+0.5) endTime1_aux=startTime1_aux+0.5;
       
       if(j==0)
       {MediaMetadataRetriever med=new MediaMetadataRetriever();
  	 med.setDataSource(records.get(0).path);
  	 writeExternalToCache(Bitmap.createScaledBitmap(med.getFrameAtTime((long)(startTime1_aux*1000000))  ,360,240,false),new File(Environment.getExternalStorageDirectory().toString()+"/Irewind/thumbs/"+outThumb));}
       
        System.out.println("corected-> start="+startTime1_aux+" end="+endTime1_aux);
     /*	for (Track track : tracks_aux) {
            if (track.getSyncSamples() != null && track.getSyncSamples().length > 0) {
                startTime1_aux = correctTimeToSyncSample(track, startTime1_aux, false);
                endTime1_aux = correctTimeToSyncSample(track, endTime1_aux, true);}}
        */
    		
        for (Track track : tracks_aux) {
        	System.out.println("type="+track.getHandler());
        	   if (track.getHandler().equals("vide"))
        	{ /*  long currentSample = 0;
            double currentTime = 0;
            double lastTime = 0;
            long startSample1 = -1;
            long endSample1 = -1;
            

            for (int i = 0; i < track.getDecodingTimeEntries().size(); i++) {
                TimeToSampleBox.Entry entry = track.getDecodingTimeEntries().get(i);
                for (int jj = 0; jj < entry.getCount(); jj++) {


                    if (currentTime > lastTime && currentTime <= startTime1_aux) {
                        // current sample is still before the new starttime
                        startSample1 = currentSample;
                    }
                    if (currentTime > lastTime && currentTime <= endTime1_aux) {
                        // current sample is after the new start time and still before the new endtime
                        endSample1= currentSample;
                    }
                  
                    lastTime = currentTime;
                    currentTime += (double) entry.getDelta() / (double) track.getTrackMetaData().getTimescale();
                    currentSample++;
                }
            }*/
            
            int startSample1=(int)(startTime1_aux*10);
            int endSample1=(int)(endTime1_aux*10);
         System.out.println("startS1="+startSample1+" endS1="+endSample1);
           movie.addTrack(new CroppedTrack(track, startSample1, endSample1));
           movies.add(movie);
          
//videoTracks.add(m.getTracks().get(0));
     
        	
        }}
     /*   Container out = new DefaultMp4Builder().build(movie);
        FileOutputStream fos = new FileOutputStream(String.format(Environment.getExternalStorageDirectory().getPath()+"/Irewind/output"+j+".mp4"));
        FileChannel fc = fos.getChannel();
        out.writeContainer(fc);

        fc.close();
        fos.close();*/
    }
    
    
    Movie result=new Movie();
    if(movies.size()>1)
    {   AppendTrack apTrack= new AppendTrack(movies.get(0).getTracks().get(0),movies.get(1).getTracks().get(0));
    
    for(int i=2;i<movies.size();i++)
    {AppendTrack aux=apTrack;
    apTrack=new AppendTrack(aux,movies.get(i).getTracks().get(0));}
    
    	result.addTrack(apTrack);}
    else result.addTrack(movies.get(0).getTracks().get(0));
    Container out = new DefaultMp4Builder().build(result);
    FileOutputStream fos = new FileOutputStream(String.format(Environment.getExternalStorageDirectory().getPath()+ "/irewindb/" +outFile));
    FileChannel fc = fos.getChannel();
    out.writeContainer(fc);

    fc.close();
    fos.close();
    
    
    
  /*  System.out.println("size="+videoTracks.size());
    
    Movie result = new Movie();

    if (videoTracks.size() > 0) {
        result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
    }
   
   Container out = new DefaultMp4Builder().build(result);
    
  
    FileOutputStream fos = new FileOutputStream(String.format(Environment.getExternalStorageDirectory().getPath()+"/Irewind/output.mp4"));
    FileChannel fc = fos.getChannel();
    out.writeContainer(fc);

    fc.close();
    fos.close();*/
		
		
		
		
	}
	





	 protected static long getDuration(Track track) {
	        long duration = 0;
	        for (TimeToSampleBox.Entry entry : track.getDecodingTimeEntries()) {
	            duration += entry.getCount() * entry.getDelta();
	        }
	        return duration;
	    }

	    private static double correctTimeToSyncSample(Track track, double cutHere, boolean next) {
	        double[] timeOfSyncSamples = new double[track.getSyncSamples().length];
	        long currentSample = 0;
	        double currentTime = 0;
	        for (int i = 0; i < track.getDecodingTimeEntries().size(); i++) {
	            TimeToSampleBox.Entry entry = track.getDecodingTimeEntries().get(i);
	            for (int j = 0; j < entry.getCount(); j++) {
	                if (Arrays.binarySearch(track.getSyncSamples(), currentSample + 1) >= 0) {
	                    // samples always start with 1 but we start with zero therefore +1
	                    timeOfSyncSamples[Arrays.binarySearch(track.getSyncSamples(), currentSample + 1)] = currentTime;
	                }
	                currentTime += (double) entry.getDelta() / (double) track.getTrackMetaData().getTimescale();
	                currentSample++;
	            }
	        }
	        double previous = 0;
	        for (double timeOfSyncSample : timeOfSyncSamples) {
	            if (timeOfSyncSample > cutHere) {
	                if (next) {
	                    return timeOfSyncSample;
	                } else {
	                    return previous;
	                }
	            }
	            previous = timeOfSyncSample;
	        }
	        return timeOfSyncSamples[timeOfSyncSamples.length - 1];
	    }

	    
	    
	    public static final int BUFFER_SIZE = 1024 * 8;
		 static void writeExternalToCache(Bitmap bitmap, File file) {
		    try {
		        file.createNewFile();
		        FileOutputStream fos = new FileOutputStream(file);
		        final BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER_SIZE);
		        bitmap.compress(CompressFormat.JPEG, 100, bos);
		        bos.flush();
		        bos.close();
		        fos.close();
		    } catch (FileNotFoundException e) {
		        e.printStackTrace();
		    } catch (IOException e) {

		    }

		}
	    

}
class myRecord {
	String segm_start, segm_end, cam_id, profile, path,segm_reference_start;

	public myRecord(String start, String end, String cam, String prof,String _path, String _segm_reference_start) {
		segm_start = start;
		segm_end = end;
		cam_id = cam;
		profile = prof;
		path=_path;
		segm_reference_start=_segm_reference_start;

	}}