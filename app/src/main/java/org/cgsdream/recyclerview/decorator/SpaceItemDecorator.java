package org.cgsdream.recyclerview.decorator;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by sm on 2015/6/7.
 */
public class SpaceItemDecorator extends RecyclerView.ItemDecoration {
	private Context mContext;

	private int mInsetLeft;
	private int mInsetTop;
	private int mInsetRight;
	private int mInsetBottom;

	public SpaceItemDecorator(Context context){
		mContext = context;
		setInsets(0);
	}

	/**
	 *
	 * @param insets
	 */
	public void setInsets(int insets){
		setInsets(insets,insets,insets,insets);
	}

	/**
	 *
	 * @param insetLeft
	 * @param insetTop
	 * @param insetRight
	 * @param insetBottom
	 */
	public void setInsets(int insetLeft,int insetTop,int insetRight,int insetBottom){
		mInsetLeft = insetLeft;
		mInsetTop = insetTop;
		mInsetRight = insetRight;
		mInsetBottom = insetBottom;
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(parent.getChildAdapterPosition(view)%2==0){
            outRect.set(mInsetLeft*3,mInsetTop*3,mInsetRight*3,mInsetBottom*3);
        }else{
            outRect.set(mInsetLeft,mInsetTop,mInsetRight,mInsetBottom);
        }

	}
}
