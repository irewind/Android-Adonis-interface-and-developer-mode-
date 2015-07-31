package myClass;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class myScrollView extends ScrollView {
Context context;
OnScrollGestureListener listener;

	public myScrollView(Context context, View ViewToDispatch) {
		super(context);
		this.context=context;
		this.ViewToDispatch=ViewToDispatch;
	}
	public void setOnScrollGestureListener(OnScrollGestureListener listener) {
	    this.listener = listener;
	}
	

	
	float old_y=-9999;
	float medium_delta=0;
	float curent_delta;
	
	float y_refresh_start=-1;
	boolean first_touch=true;
	View ViewToDispatch;
	
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
    	
    	super.onTouchEvent(ev);
    if(ViewToDispatch!=null)
    	ViewToDispatch.dispatchTouchEvent(ev);
    
    
    	curent_delta=old_y-ev.getY();
    	if(old_y!=-9999)  {medium_delta=(medium_delta*2+curent_delta)/3;}
    	old_y=ev.getY();
    	
    	
    //	System.out.println("scrollY="+getScrollY()+" y_refresh_start="+  y_refresh_start+" y="+ev.getY());
    //	System.out.println("ev.action="+ev.getAction());
    	if(getScrollY()==0&&first_touch)
    	{ y_refresh_start=ev.getY(); }
    		first_touch=false;
    	if(y_refresh_start!=-1&&ev.getY()>y_refresh_start+200)
    	{y_refresh_start=-1;
    	listener.refresh();}
    	
    	if(ev.getAction()==MotionEvent.ACTION_UP){first_touch=true; listener.endTouch(medium_delta); old_y=-9999;  medium_delta=0;}
    	
    	return true;
    };
	
	
	@Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
		
        super.onScrollChanged(x, y, oldx, oldy);
        
      if(y!=0){first_touch=true;y_refresh_start=-1;}
    	   listener.moveTitle(y);
       

	}

public interface OnScrollGestureListener
{
	
    public void moveTitle(double y);
    public void endTouch(float d);
    public void refresh();
}
}

