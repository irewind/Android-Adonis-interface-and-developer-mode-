package utils;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class AvcEncoder {
    public static boolean repairColor = true;
private MediaCodec mediaCodec;
private BufferedOutputStream outputStream;
File file;
int width,height;
String header;
long segment_lenght;
long last_start=0;
MediaFormat mediaFormat;
    int frame_id = 0;

public void newSegment()
{
    long timeStamp=System.currentTimeMillis();
    frame_id = 0;
	if(outputStream!=null)
{ try{outputStream.flush();
outputStream.close();}catch(Exception e){e.printStackTrace();}
try{file.renameTo(new File(file.getAbsoluteFile()+"-"+timeStamp+".h264"));


}catch (Exception e){e.printStackTrace();}
	}

	if(mediaCodec!=null)
	{mediaCodec.stop();
    mediaCodec.release();}
	try {
		mediaCodec = MediaCodec.createEncoderByType("video/avc");
	} catch (Exception e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	}
	mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
    mediaCodec.start();  	
	
file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/irewindb/CameraSegments/" +header+timeStamp);

try {
	new FileOutputStream(file).close();
} catch (FileNotFoundException e1) {
	// TODO Auto-generated catch block
	e1.printStackTrace();
} catch (IOException e1) {
	// TODO Auto-generated catch block
	e1.printStackTrace();
}
try {
    outputStream = new BufferedOutputStream(new FileOutputStream(file));
    Log.i("AvcEncoder", "outputStream initialized"); 
} catch (Exception e){ 
    e.printStackTrace();
}}

public AvcEncoder(int width, int height, int bit_rate, int frame_rate, int key_frame_interval,long segment_length,String cam_id,String location_id,String profile) { 
    this.segment_lenght=segment_length;
    header=location_id+"-"+cam_id+"-"+profile+"-";
  this.width = width;
  this.height = height;
    
   ( new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/irewindb/CameraSegments")).mkdirs();
    
     mediaFormat = MediaFormat.createVideoFormat("video/avc", width, height);
   mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bit_rate);
    mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, frame_rate);
    mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);  // GALAXY NEXUS -> #0x7f000100 COLOR_TI_FormatYUV420PackedSemiPlanar (also also NV12)
                                                                                                                       // nexus5, htc ->21.COLOR_FormatYUV420SemiPlanar NV12
    mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, key_frame_interval);
    System.out.println("<< << << COLOR_FormatYUV420Planar");
}

public void close() {
    try {
        mediaCodec.stop();
        mediaCodec.release();
        outputStream.flush();
        outputStream.close();
        try{file.renameTo(new File(file.getAbsoluteFile()+"-"+System.currentTimeMillis()+".h264"));}catch (Exception e){e.printStackTrace();}
	
    } catch (Exception e){ 
        e.printStackTrace();
    }
}

public byte[] swapYV12toI420(byte[] yv12bytes, int width, int height) {
    byte[] i420bytes = new byte[yv12bytes.length];
    for (int i = 0; i < width*height; i++)
        i420bytes[i] = yv12bytes[i];
    for (int i = width*height; i < width*height + (width/2*height/2); i++)
        i420bytes[i] = yv12bytes[i + (width/2*height/2)];
    for (int i = width*height + (width/2*height/2); i < width*height + 2*(width/2*height/2); i++)
        i420bytes[i] = yv12bytes[i - (width/2*height/2)];
    return i420bytes;
}

    public byte[] swapYV12toI420(byte[] yv12bytes) {

        for(int i = yv12bytes.length/12*8; i < yv12bytes.length/12*12; i +=2 )
        {byte aux = yv12bytes[i];
            yv12bytes[i] = yv12bytes[i + 1];
              yv12bytes[i + 1] = aux;

        }


        return yv12bytes;
    }


    public static byte[] YV12toYUV420PackedSemiPlanar(final byte[] input, final byte[] output, final int width, final int height) {
    /*
     * COLOR_TI_FormatYUV420PackedSemiPlanar is NV12
     * We convert by putting the corresponding U and V bytes together (interleaved).
     */
        final int frameSize = width * height;
        final int qFrameSize = frameSize/4;

        System.arraycopy(input, 0, output, 0, frameSize); // Y

        for (int i = 0; i < qFrameSize; i++) {
            output[frameSize + i*2] = input[frameSize + i + qFrameSize]; // Cb (U)
            output[frameSize + i*2 + 1] = input[frameSize + i]; // Cr (V)
        }
        return output;
    }


// called from Camera.setPreviewCallbackWithBuffer(...) in other class
public synchronized void offerEncoder(byte[] pre_input) {
    //frame_id++;
    byte[] input;
    if(repairColor){input = swapYV12toI420(pre_input);}
  else { input = pre_input;}

	if(System.currentTimeMillis()-last_start>segment_lenght)
	{last_start=System.currentTimeMillis();
	newSegment();}

    try {
    	ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
		ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
		int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
	//	System.out.println("inputBufferIndex = "+inputBufferIndex);
		if (inputBufferIndex >= 0) {
			ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
			inputBuffer.clear();
			inputBuffer.put(input);
			mediaCodec.queueInputBuffer(inputBufferIndex, 0, input.length,System.currentTimeMillis() /*+ (long)((double)frame_id * 3)*/ , 0);
		}

        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo,0);
 //       System.out.println("outputBufferIndex = "+outputBufferIndex);
        while (outputBufferIndex >= 0) {
        	
            ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
            byte[] outData = new byte[bufferInfo.size];
            outputBuffer.get(outData);
            outputStream.write(outData, 0, outData.length);
            //Log.i("AvcEncoder", outData.length + " bytes written");

            mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
            outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 0);
     //       System.out.println("outputBufferIndex = "+outputBufferIndex);

        }
    } catch (Throwable t) {
        t.printStackTrace();
    }

}
}
