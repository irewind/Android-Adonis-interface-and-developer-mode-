package myClass;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.widget.ListView;

public class myListView extends ListView{
	private OnSwipeGestureListener listener;
	public myListView(Context context, int width) {
		super(context);
		ViewWidth=width;
		this.context=context;
		// TODO Auto-generated constructor stub
	}
	Context context;
	double mLastX,mLastY;
	MotionEvent mLastEv;
	int swipe_state=-1;    
	public int ViewWidth;
	public void setOnSwipeGestureListener(OnSwipeGestureListener listener) {
	    this.listener = listener;
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent ev) {
		double x=ev.getX();
		double y=ev.getY();
	
		if(ev.getAction()==MotionEvent.ACTION_DOWN&&x<ViewWidth/7)
		           {swipe_state=0;
	                mLastX=x;
	                mLastY=y;
	                mLastEv=ev;
	                new Thread(new Runnable() {
						
						@Override
						public void run() {
						try {
							synchronized (this) {
								wait(500);
								if(swipe_state==0){
								swipe_state=1;
								listener.swipeing((int) mLastX);
								mLastEv.setAction(MotionEvent.ACTION_CANCEL);
								((Activity)context).runOnUiThread(new Runnable() {
									
									@Override
									public void run() {
										myListView.this.onTouchEvent(mLastEv);
										
									}
								});
								}
							}
							
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
							
						}
					}).start();
		           }
		
		else if (ev.getAction()==MotionEvent.ACTION_UP)
		{ if(swipe_state==1)  listener.swipe_ended((int)x);
			swipe_state=-1;}
		
		else if(ev.getAction()==MotionEvent.ACTION_MOVE&&swipe_state==0)
		{ if((y-mLastY)>2*(x-mLastX)) swipe_state=-1;
		       else swipe_state=1;}
		
		
		if(swipe_state==1) {
			listener.swipeing((int) x);
		
		   
		   ev.setAction(MotionEvent.ACTION_CANCEL);
		}
		
		super.onTouchEvent(ev);
       return true;}
	
	
	
	
	public interface OnSwipeGestureListener
	{
		public void swipeing(int x);
	    public void swipe_ended(int x);	
	
	
	}
}






