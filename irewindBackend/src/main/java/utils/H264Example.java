package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.tracks.H264TrackImpl;

/**
 * Created with IntelliJ IDEA.
 * User: magnus
 * Date: 2012-04-23
 * Time: 12:21
 * To change this template use File | Settings | File Templates.
 */
public class H264Example {
    public static void run() throws IOException {
    	System.out.println("--1");
        FileInputStream fileInputStream = new FileInputStream("/storage/emulated/0/irewind/CameraSegments/source.h264");
        System.out.println("--1.2");
        FileChannel f = fileInputStream.getChannel();
        System.out.println("--1.3");
        H264TrackImpl h264Track = new H264TrackImpl(f,"eng",5,1);
	
		System.out.println("--1.4");
        Movie m = new Movie();
        m.addTrack(h264Track);
        System.out.println("--2");
      

        {
            Container out = new DefaultMp4Builder().build(m);
            FileOutputStream fos = new FileOutputStream(new File("/storage/emulated/0/irewind/CameraSegments/result.mp4"));
            FileChannel fc = fos.getChannel();
            System.out.println("--3");
            out.writeContainer(fc);
            System.out.println("--4");
            fos.close();
            fileInputStream.close();
        }
    }
}