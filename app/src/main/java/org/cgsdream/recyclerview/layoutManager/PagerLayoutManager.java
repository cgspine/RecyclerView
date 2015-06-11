package org.cgsdream.recyclerview.layoutManager;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashSet;
import java.util.List;



/**
 * Created by sm on 2015/5/3.
 */
public class PagerLayoutManager extends RecyclerView.LayoutManager{

    private static final String TAG = PagerLayoutManager.class.getSimpleName();
    private Context mContext;
    private boolean mIsVertical;
    private int mItemMargin;
    private RecyclerView mRecyclerView;

    public static enum Orientation {
        HORIZONTAL,
        VERTICAL
    }
    public static enum Direction {
        NONE,
        START,
        END
    }


    private int mVisiblePosition;
    private int mFirstChangedPosition;
    private int mChangedPositionCount;

    private int mDecoratedChildWidth;
    private int mDecoratedChildHeight;

    private static final int REMOVE_VISIBLE = 0;
    private static final int REMOVE_INVISIBLE = 1;

    public PagerLayoutManager(Context context) {
        this(context,Orientation.HORIZONTAL,0);
    }

    public PagerLayoutManager(Context context, Orientation orientation) {
        this(context, orientation, 0);
    }

    public PagerLayoutManager(Context context, Orientation orientation, int itemMargin) {
        mContext = context;
        mIsVertical = orientation == Orientation.VERTICAL;
        mItemMargin = itemMargin;
    }
    //================================================
    // 统一借口，屏蔽vertical 和 horizontal 之间的差异性
    //================================================

    /**
     * 获取子view宽度
     * @return
     */
    private int getSize(){
        return mIsVertical?mDecoratedChildHeight:mDecoratedChildWidth;
    }

    /**
     * RecyclerView总空闲空间
     * @return
     */

    private int getTotalSpace() {
        if (mIsVertical) {
            return getHorizontalSpace();
        } else {
            return getVerticalSpace();
        }
    }

    /**
     * Recycler开始位置
     * @return
     */
    protected int getStartWithPadding() {
        return (mIsVertical ? getPaddingTop() : getPaddingLeft());
    }

    /**
     * Recycler结束位置
     * @return
     */
    protected int getEndWithPadding() {
        if (mIsVertical) {
            return (getHeight() - getPaddingBottom());
        } else {
            return (getWidth() - getPaddingRight());
        }
    }
    protected int getEndPadding(){
        return mIsVertical?getPaddingBottom():getPaddingRight();
    }
    protected int getStartPadding(){
        return mIsVertical?getPaddingTop():getPaddingLeft();
    }

    /**
     * 子元素开始位置
     * @param child
     * @return
     */
    protected int getChildStart(View child) {
        return (mIsVertical ? getDecoratedTop(child) : getDecoratedLeft(child));
    }

    /**
     * 子元素结束位置
     * @param child
     * @return
     */
    protected int getChildEnd(View child) {
        return (mIsVertical ?  getDecoratedBottom(child) : getDecoratedRight(child));
    }

    //辅助工具
    private int getHorizontalSpace() {
        return getWidth() - getPaddingRight() - getPaddingLeft();
    }

    private int getVerticalSpace() {
        return getHeight() - getPaddingBottom() - getPaddingTop();
    }

    //================================================
    // 对外的 get - set 接口
    //================================================

    public Orientation getOrientation() {
        return (mIsVertical ? Orientation.VERTICAL : Orientation.HORIZONTAL);
    }

    public void setOrientation(Orientation orientation) {
        final boolean isVertical = (orientation == Orientation.VERTICAL);
        if (this.mIsVertical == isVertical) {
            return;
        }

        this.mIsVertical = isVertical;
        requestLayout();
    }

    public int getFirstVisiblePosition() {
        if (getChildCount() == 0) {
            return 0;
        }

        return getPosition(getChildAt(0));
    }

    public int getLastVisiblePosition() {
        final int childCount = getChildCount();
        if (childCount == 0) {
            return 0;
        }

        return getPosition(getChildAt(childCount - 1));
    }


    /**
     * 获取item边距
     * @param margin
     */
    public void setItemMargin(int margin) {
        mItemMargin = margin;
    }

    public int getItemMargin() {
        return mItemMargin;
    }

    /**
     * 用来设置滑动是后是向前滚动或者是向后滚动得分界点
     */
    private double mPageMoveDirectionPoint = 0.5;
    public void setPageMoveDirectionPoint(double point){
        mPageMoveDirectionPoint = 0.5;
    }




    //================================================
    // scroll
    //================================================

    @Override
    public boolean canScrollHorizontally() {
        return !mIsVertical;
    }

    @Override
    public boolean canScrollVertically() {
        return mIsVertical;
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        mRecyclerView = view;
        super.onAttachedToWindow(view);
    }

    public void smoothScrollToPosition(int position){
        if(mRecyclerView != null){
            smoothScrollToPosition(mRecyclerView,null,position);
        }
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        final LinearSmoothScroller scroller = new LinearSmoothScroller(recyclerView.getContext()) {
            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {
                if (getChildCount() == 0) {
                    return null;
                }

                final int direction = targetPosition < getFirstVisiblePosition() ? -1 : 1;
                if (mIsVertical) {
                    return new PointF(0, direction);
                } else {
                    return new PointF(direction, 0);
                }
            }

            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }

            @Override
            protected int getHorizontalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };

        scroller.setTargetPosition(position);
        startSmoothScroll(scroller);
    }


    private int tempOffsetRecord;
    private Direction scrollDirection;
    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        final View firstVisiableView = getChildAt(getFirstVisiblePosition());
        int itemSize = getSize();
        int firstOffset = getChildStart(firstVisiableView);//获取第一个元素的偏移量
        switch (state){
            case RecyclerView.SCROLL_STATE_IDLE:
                //滚动停止时
                if(firstOffset+tempOffsetRecord <0){
                    scrollDirection = Direction.END;
                }else{
                    scrollDirection = Direction.START;
                }
                boolean isForword = false; //在滑动方向不同的情况下，向前滑动是相对的

                //在滑动方向向反时，向前滚动的判断刚好相反
                if((Math.abs(firstOffset)>=itemSize*mPageMoveDirectionPoint && scrollDirection == Direction.END)||(Math.abs(firstOffset)<=itemSize*mPageMoveDirectionPoint && scrollDirection == Direction.START)){
                    isForword = true;
                }
                if(isForword){
                    if(getFirstVisiblePosition()+1<=getLastVisiblePosition()){
                        smoothScrollToPosition(getFirstVisiblePosition()+1);
                    }else{
                        smoothScrollToPosition(getFirstVisiblePosition());
                    }
                }else{
                    smoothScrollToPosition(getFirstVisiblePosition());
                }
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                tempOffsetRecord = firstOffset;
                //拖拽滚动时
                break;
            case RecyclerView.SCROLL_STATE_SETTLING:
                //动画滚动时
                break;
            default:
                break;
        }
    }

    private int scrollByHander(int dxdy,RecyclerView.Recycler recycler, RecyclerView.State state){
        if (getChildCount() == 0) {
            return 0;
        }
        final View startView = getChildAt(0);
        final View endView = getChildAt(getChildCount()-1);
        int viewSpan = getChildEnd(endView) - getChildStart(startView);
        if (viewSpan <= getVerticalSpace()) {
            return 0;
        }
        int delta = -dxdy;

        boolean endBoundReached = getLastVisiblePosition() == getItemCount()-1;
        boolean firstBoundReached = getFirstVisiblePosition() == 0;

        if (dxdy > 0) { // Contents are scrolling up
            //Check against bottom bound
            if (endBoundReached) {
                //If we've reached the last row, enforce limits
                int endOffset;
                //We are truly at the bottom, determine how far
                endOffset = getVerticalSpace() - getChildEnd(endView) + getEndPadding();

                delta = Math.max(-dxdy, endOffset);
            } else {
                //No limits while the last row isn't visible
                delta = -dxdy;
            }
        } else { // Contents are scrolling down
            //Check against top bound
            if (firstBoundReached) {
                int topOffset = -getChildStart(startView) + getStartPadding();

                delta = Math.min(-dxdy, topOffset);
            } else {
                delta =-dxdy;
            }
        }

        if(mIsVertical){
            offsetChildrenVertical(delta);
        }else{
            offsetChildrenHorizontal(delta);
        }
        if (dxdy > 0) {
            if (getChildEnd(startView) < 0 && !endBoundReached) {
                fillGrid(Direction.END, recycler, state);
            } else if (!endBoundReached) {
                fillGrid(Direction.NONE, recycler, state);
            }
        } else {
            if (getChildStart(startView) > 0 && !firstBoundReached) {
                fillGrid(Direction.START, recycler, state);
            } else if (!firstBoundReached) {
                fillGrid(Direction.NONE, recycler, state);
            }
        }

        return delta;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return scrollByHander(dy,recycler,state);
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return scrollByHander(dx,recycler,state);
    }

    @Override
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        removeAllViews();
    }
    //
    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        RecyclerView.LayoutParams lp;
        lp = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        return lp;
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return true;
    }

    @Override
    public void onItemsRemoved(RecyclerView recyclerView, int positionStart, int itemCount) {
        mFirstChangedPosition = positionStart;
        mChangedPositionCount = itemCount;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        Log.i("cgs", "PagerLayoutManager onLayoutChildren start");
        if (getItemCount() == 0) {
            detachAndScrapAttachedViews(recycler);
            return;
        }
        if (getChildCount() == 0 && state.isPreLayout()) {
            //prelayout期间view为空
            return;
        }
        //在第二次（真正）布局的时候清除改变值
        if (!state.isPreLayout()) {
            mFirstChangedPosition = mChangedPositionCount = 0;
        }

        if (getChildCount() == 0) {//首次或者为空
            mDecoratedChildHeight = getVerticalSpace();
            mDecoratedChildWidth =  getHorizontalSpace();
        }



        SparseIntArray removedCache = null;
        /*
         * pre-layout期间，处理子view移除的动画因素
         */
        if (state.isPreLayout()) {
            removedCache = new SparseIntArray(getChildCount());
            for (int i=0; i < getChildCount(); i++) {
                final View view = getChildAt(i);
                RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) view.getLayoutParams();

                if (lp.isItemRemoved()) {
                    //使要移除的view可见，来做动画
                    removedCache.put(lp.getViewAdapterPosition(), REMOVE_VISIBLE);
                }
            }

            //使要新添加的view不可见，也是用于动画
            if (removedCache.size() == 0 && mChangedPositionCount > 0) {
                for (int i = mFirstChangedPosition; i < (mFirstChangedPosition + mChangedPositionCount); i++) {
                    removedCache.put(i, REMOVE_INVISIBLE);
                }
            }
        }
        int start;
        if (getChildCount() == 0) {
            mVisiblePosition = 0;
            start = 0;
        } else if (!state.isPreLayout()
                && state.getItemCount() == 1) {
            //数据只有一个
            mVisiblePosition = 0;
            start = 0;
        } else {
            /*
             * Keep the existing initial position, and save off
             * the current scrolled offset.
             */
            final View firstChild = getChildAt(0);
            if(mIsVertical){
                start = getDecoratedTop(firstChild);
            }else{
                start = getDecoratedLeft(firstChild);
            }
        }

        detachAndScrapAttachedViews(recycler);

        if (mVisiblePosition < 0) mVisiblePosition = 0;
        if (mVisiblePosition >= getItemCount()) mVisiblePosition = (getItemCount() - 1);

        Log.i("cgs","childCount:" +getChildCount() +" ,start:" + start + " ,mVisiblePosition:" + mVisiblePosition);
        //Fill the grid for the initial layout of views
        fillGrid(Direction.NONE, start,  recycler, state, removedCache);

        Log.i("cgs","childCount:" +getChildCount() + ",width:" + getChildAt(0).getWidth());
        //Evaluate any disappearing views that may exist
        if (!state.isPreLayout() && !recycler.getScrapList().isEmpty()) {
            final List<RecyclerView.ViewHolder> scrapList = recycler.getScrapList();
            final HashSet<View> disappearingViews = new HashSet<View>(scrapList.size());

            for (RecyclerView.ViewHolder holder : scrapList) {
                final View child = holder.itemView;
                final RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
                if (!lp.isItemRemoved()) {
                    disappearingViews.add(child);
                }
            }
        }

    }

    private void fillGrid(Direction direction,RecyclerView.Recycler recycler,RecyclerView.State state){
        fillGrid(direction,0,recycler,state,null);
    }


    private void fillGrid(Direction direction, int start,RecyclerView.Recycler recycler,RecyclerView.State state,SparseIntArray removedPositions) {
        if (mVisiblePosition < 0) mVisiblePosition = 0;
        if (mVisiblePosition >= getItemCount()) mVisiblePosition = (getItemCount() - 1);


        SparseArray<View> viewCache = new SparseArray<View>(getChildCount());
        int startOffset;
        if(mIsVertical){
            startOffset = getPaddingTop() + start;
        }else{
            startOffset = getPaddingLeft() + start;
        }
        Log.i("fillGrid","childCount:" +getChildCount() +" ,startOffset:" + startOffset + " ,mVisiblePosition:" + mVisiblePosition);
        if (getChildCount() != 0) {
            final View startView = getChildAt(0);
            start = getChildStart(startView);

            switch (direction) {
                case START:
                    if(mIsVertical){
                        start -= mDecoratedChildHeight;
                    }else{
                        start -= mDecoratedChildWidth;
                    }
                    break;
                case END:
                    if(mIsVertical){
                        start += mDecoratedChildHeight;
                    }else{
                        start += mDecoratedChildWidth;
                    }
                    break;
                default:
                    break;
            }

            for (int i=0; i < getChildCount(); i++) {
                final View child = getChildAt(i);
                viewCache.put(i, child);
            }


            for (int i=0; i < viewCache.size(); i++) {
                detachView(viewCache.valueAt(i));
            }
        }


        /*
         * Next, we supply the grid of items that are deemed visible.
         * If these items were previously there, they will simply be
         * re-attached. New views that must be created are obtained
         * from the Recycler and added.
         */
        int mOffset = startOffset;

        for (int i = 0; i < getChildCount(); i++) {


            int nextPosition = i;
            /*
             * When a removal happens out of bounds, the pre-layout positions of items
             * after the removal are shifted to their final positions ahead of schedule.
             * We have to track off-screen removals and shift those positions back
             * so we can properly lay out all current (and appearing) views in their
             * initial locations.
             */
            if (state.isPreLayout()) {
                int offsetPosition = nextPosition;

                for (int offset = 0; offset < removedPositions.size(); offset++) {
                    //Look for off-screen removals that are less-than this
                    if (removedPositions.valueAt(offset) == REMOVE_INVISIBLE
                            && removedPositions.keyAt(offset) < nextPosition) {
                        //Offset position to match
                        offsetPosition--;
                    }
                }
                nextPosition = offsetPosition;
            }

            if (nextPosition < 0 || nextPosition >= state.getItemCount()) {
                //Item space beyond the data set, don't attempt to add a view
                continue;
            }

            //Layout this position
            View view = viewCache.get(nextPosition);
            if (view == null) {
                /*
                 * The Recycler will give us either a newly constructed view,
                 * or a recycled view it has on-hand. In either case, the
                 * view will already be fully bound to the data by the
                 * adapter for us.
                 */
                view = recycler.getViewForPosition(nextPosition);
                addView(view);

                /*
                 * Update the new view's metadata, but only when this is a real
                 * layout pass.
                 */
                if (!state.isPreLayout()) {
                    RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) view.getLayoutParams();
                }

                /*
                 * It is prudent to measure/layout each new view we
                 * receive from the Recycler. We don't have to do
                 * this for views we are just re-arranging.
                 */
//                if(getPosition(view)<getItemCount() - 1){
//                    if(mIsVertical){
//                        measureChildWithMargins(view,0,mItemMargin);
//                    }else{
//                        measureChildWithMargins(view,0,mItemMargin);
//                    }
//                }else{
                measureChildWithMargins(view,0,0);
//                }
                if(mIsVertical){
                    layoutDecorated(view, 0, mOffset,
                            mDecoratedChildWidth,
                            mOffset + mDecoratedChildHeight);
                    Log.i("view","width:" + view.getWidth() + ", mOffset:" + mOffset +",mDecoratedChildWidth:" + mDecoratedChildWidth + ",mDecoratedChildHeight:" + mDecoratedChildHeight);
                }else{
                    layoutDecorated(view, mOffset, 0,
                            mOffset + mDecoratedChildWidth,
                            mDecoratedChildHeight);
                }


            } else {
                //Re-attach the cached view at its new index
                attachView(view);
                viewCache.remove(nextPosition);
            }
            if(mIsVertical){
                mOffset += mDecoratedChildHeight;
            }else{
                mOffset += mDecoratedChildWidth;
            }

        }

        /*
         * Finally, we ask the Recycler to scrap and store any views
         * that we did not re-attach. These are views that are not currently
         * necessary because they are no longer visible.
         */
        for (int i=0; i < viewCache.size(); i++) {
            final View removingView = viewCache.valueAt(i);
            recycler.recycleView(removingView);
        }
    }


}