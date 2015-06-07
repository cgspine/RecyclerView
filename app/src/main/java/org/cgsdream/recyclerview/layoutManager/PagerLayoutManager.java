package org.cgsdream.recyclerview.layoutManager;

import android.content.Context;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by sm on 2015/6/7.
 */
public class PagerLayoutManager extends RecyclerView.LayoutManager {
    private static final String TAG = "PagerLayoutManager";

    public static final int HORIZONTAL = OrientationHelper.HORIZONTAL;

    public static final int VERTICAL = OrientationHelper.VERTICAL;

    private Context mContext;
    private int mOrientation;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void scrollToPosition(int position) {
        super.scrollToPosition(position);
    }


}
