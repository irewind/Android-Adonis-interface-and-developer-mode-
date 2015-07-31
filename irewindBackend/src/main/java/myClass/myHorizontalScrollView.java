package myClass;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class myHorizontalScrollView extends HorizontalScrollView {
Context context;
OnScrollGestureListener listener;

	public myHorizontalScrollView(Context context) {
		super(context);
		this.context=context;
	}
	public void setOnScrollGestureListener(OnScrollGestureListener listener) {
	    this.listener = listener;
	}
	

	
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
    	
    	super.onTouchEvent(ev);
    
    	
    	
    	if(ev.getAction()==MotionEvent.ACTION_UP){listener.endTouch(ev.getX());}
    	
    	return true;
    };
	
	
	@Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
		
        super.onScrollChanged(x, y, oldx, oldy);
        
      
    try{	   listener.Scroll(y); } catch(ArithmeticException e){}
       

	}

public interface OnScrollGestureListener
{
	
    public void Scroll(double x);
    public void endTouch(float x);
}
}

