package com.irewind.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;


public class EllipsingTextView extends TextView {

	public EllipsingTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public EllipsingTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public EllipsingTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	CharSequence origText = "";
	int maxLines = 2;
	 
	@Override 
	public void setText(CharSequence text, BufferType type) {
	    super.setText(text, type);
	    origText = text;
	} 
	 
	@Override 
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	    CharSequence text = origText;
	    onPreDraw(); 
	 
	    while(getLineCount() > maxLines) {
	        text = text.length() == 0? "" : text.subSequence(0, text.length()-1);
	        super.setText(text + "[...]");
	        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	        onPreDraw(); 
	    }
	} 
}
