package mrfu.refreshgot;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ListAdapter;
import android.widget.ListView;

import mrfu.refreshgot.utils.FooterView;
import mrfu.refreshgot.GotRefresh.OnEventType;

/**
 * Created by MrFu on 16/3/18.
 */
public class GotListView extends ListView implements GotRefresh.SomeTouchListener {
    /**
     * 预定义高度
     */
    private static final int PRE_HEIGHT = 250;
    private final FooterView mFooterView;

    private GotRefresh mGotRefresh;
    /**
     * 是否在加载中 ( 上拉加载更多 )
     */
    private boolean isLoading = false;

    private float mLastMotionX;
    private float mLastMotionY;

    private float ly;
    private float lx;
    /**
     * 滑动到最下面时的上拉操作
     */
    private int mTouchSlop;
    /**
     * 按下时的y坐标
     */
    private int mYDown;
    /**
     * 抬起时的y坐标, 与mYDown一起用于滑动到底部时判断是上拉还是下拉
     */
    private int mLastY;

    public GotListView(Context context) {
        this(context, null);
    }

    public GotListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GotListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mFooterView = new FooterView(getContext());
    }

    public void setAdapter(GotRefresh gotRefresh, ListAdapter adapter) {
        if (gotRefresh == null) return;
        mGotRefresh = gotRefresh;
        if (mFooterView != null){
            addFooterView(mFooterView);//一定要先添加一次，不然会报错，setAdapter 之后 remove 掉   看这里：http://blog.csdn.net/Take_all/article/details/7635116
        }
        super.setAdapter(adapter);
        if (mFooterView != null){
            removeFooterView(mFooterView);
        }

        mGotRefresh.setSomeTouchListener(this);
    }

    @Override
    public void setAdapter(ListAdapter adapter){
        throw new IllegalArgumentException("Please call setAdapter(LxRefresh lxRefresh, ListAdapter adapter) method");
//        super.setAdapter(adapter);
    }



    @Override
    public OnEventType onInterceptTouchEventLxRefresh(MotionEvent event) {
        int action = event.getAction();
        if (isReadyForPullUp() && !isLoading && !mGotRefresh.isRefreshing()){// read to refresh
            float x = event.getX();
            float y = event.getY();
            switch (action) {
                case MotionEvent.ACTION_MOVE:{
                    if (y < mLastMotionY) {
                        mGotRefresh.doOnPullUpRefresh();
                    }
                    break;
                }
                case MotionEvent.ACTION_DOWN: {
                    mLastMotionY = event.getY();
                    mLastMotionX = event.getX();
                    break;
                }
                case MotionEvent.ACTION_UP:
                    break;
            }

        }

        //处理 viewpager 的事件冲突
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lx = event.getRawX();
                ly = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mGotRefresh != null && !mGotRefresh.canChildScrollUp()) {
                    float dy = event.getRawY() - ly;
                    float dx = event.getRawX() - lx;
                    if (dy > 0 && Math.abs(dy) > Math.abs(dx)) {
                        return OnEventType.SUPER;
                    } else {
                        return OnEventType.FALSE;
                    }
                }
                break;
        }
        return OnEventType.SUPER;
    }

    @Override
    public OnEventType dispatchTouchEventLxRefresh(MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 按下
                mYDown = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                // 移动
                mLastY = (int) event.getRawY();
                break;
            default:
                break;
        }
        return OnEventType.SUPER;
    }

    @Override
    public OnEventType onTouchEventLxRefresh(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_MOVE:
                mLastMotionY = ev.getY();
                break;
        }
        return OnEventType.SUPER;
    }

    /**
     * ListView
     * 是否是上拉操作
     *
     * @return
     */
    private boolean isLvPullUp() {
        return (mYDown - mLastY) >= mTouchSlop;
    }

    public boolean isReadyForPullUp(){
        int count = getCount();
        if(getLastVisiblePosition() >= count - 2){
            final int childIndex = getLastVisiblePosition() - getFirstVisiblePosition();
            final View lastVisibleChild = getChildAt(childIndex);
            if (lastVisibleChild != null) {
                return lastVisibleChild.getBottom() - PRE_HEIGHT <= getBottom()-getTop();
            }
        }
        return false;
    }

    /**
     * ListView
     * @param loading
     */
    public void setLvLoading(boolean loading) {
        isLoading = loading;
        if (isLoading) {
            addFooterView(mFooterView);
        } else {
            removeFooterView(mFooterView);
            mYDown = 0;
            mLastY = 0;
        }
    }

    /**
     * ListView
     * 判断是否到了最底部
     */
    private boolean isLvBottom() {
        if (getAdapter() != null) {
            boolean isBottom = getLastVisiblePosition() == (getAdapter().getCount() - 1);
            return isBottom;
        }
        return false;
    }

    public boolean canLoad(){
        return !isLoading && isLvPullUp() && isReadyForPullUp();
    }

    public void setAttributeSet(AttributeSet attributeSet) {
        mFooterView.setAttributeSet(attributeSet);
    }
}
