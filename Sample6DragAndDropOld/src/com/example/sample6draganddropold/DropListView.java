package com.example.sample6draganddropold;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ListView;

public class DropListView extends ListView implements DropTarget {

	public DropListView(Context context) {
		super(context);
	}

	public DropListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public interface OnDropListener {
		public void onDrop(Object dragInfo, int position);
	}
	OnDropListener mListener;
	public void setOnDropListener(OnDropListener listener) {
		mListener = listener;
	}
	
	@Override
	public void onDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		int position = pointToPosition(x, y);
		if (mListener != null) {
			mListener.onDrop(dragInfo, position);
		}
	}

	@Override
	public void onDragEnter(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		
	}

	@Override
	public void onDragOver(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		
	}

	@Override
	public void onDragExit(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		
	}

	@Override
	public boolean acceptDrop(DragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		return true;
	}

	@Override
	public Rect estimateDropLocation(DragSource source, int x, int y,
			int xOffset, int yOffset, DragView dragView, Object dragInfo,
			Rect recycle) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
