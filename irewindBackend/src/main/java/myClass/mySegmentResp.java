package myClass;

public class mySegmentResp {
	
	public int camera, segment_id;
	public String path;
	public long toSeek, start, end;
	public mySegmentResp(int camera, int segment_id, long toSeek, long start,long end, String path) {
		super();
		this.camera = camera;
		this.segment_id = segment_id;
		this.toSeek = toSeek;
		this.start=start;
		this.end=end;
		this.path= path;
	}
	

}
