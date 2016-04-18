package mvp.alaske.com.sufaceviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.ScrollView;


/**
 * Created by zl on 16/3/1.
 */
public class KeyboardRelativeLayout extends RelativeLayout implements ViewTreeObserver.OnGlobalLayoutListener{

    private static final String TAG = "KeyboardRelativeLayout";

    private Rect mRect = new Rect();
    private Rect mOrginRect = new Rect();
    private int mKeyboardHeight = -1;
    private OnKeyboardStateChangeListener mListener;

    private boolean autoResize = false;

    private boolean autoScroll = false;

    public KeyboardRelativeLayout(Context context) {
        this(context, null);
    }

    public KeyboardRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyboardRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.KeyboardRelativeLayout);
//        autoResize = ta.getBoolean(R.styleable.KeyboardRelativeLayout_keyboard_auto_resize, false);
//        autoScroll = ta.getBoolean(R.styleable.KeyboardRelativeLayout_keyboard_auto_scroll, false);
//        ta.recycle();

//        keyboardshowWithoutMoveWindow();
    }

    /**
     * 规避当 edittext 输入控件内容很长的时候，窗口自动移动的问题
     */
    private void keyboardshowWithoutMoveWindow(){
        ScrollView scrollView = new ScrollView(getContext());
        scrollView.setFillViewport(true);
        addView(scrollView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(Build.VERSION.SDK_INT >= 16){
            getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }else {
            getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    }

    public void setOnKeyboardStateListener(OnKeyboardStateChangeListener listener) {
        this.mListener = listener;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Rect rect = new Rect();
        getWindowVisibleDisplayFrame(rect);
        if (rect.height() > mOrginRect.height()) {
            mOrginRect.set(rect);
        }
    }

    @Override
    public void getFocusedRect(Rect r) {
        super.getFocusedRect(r);
    }

    @Override
    public void onGlobalLayout() {

        if (mOrginRect.height() > 0 ) {

            getWindowVisibleDisplayFrame(mRect);
            Log.d(TAG, "onGlobalLayout: " + mRect.height());

            int height = mOrginRect.height() - mRect.height();

            InputMethodManager im;
            if (height != mKeyboardHeight ) {
                if (mKeyboardHeight != -1) {
                    if (height > 0) {
                        if (autoResize) {
                            MarginLayoutParams params = ((MarginLayoutParams) getLayoutParams());
                            params.bottomMargin = height;
                            setLayoutParams(params);
                        } else if (autoScroll) {
                            Rect rect = new Rect();
                            getWindowVisibleDisplayFrame(rect);
                            View focus = findFocus();
                            if (null != focus) {
                                int[] pos = new int[2];
                                focus.getLocationInWindow(pos);
                                if (pos[1] + focus.getHeight() > rect.bottom) {
                                    for (int i = 0; i < getChildCount(); i++) {
                                        getChildAt(i).offsetTopAndBottom(rect.bottom - (pos[1] + focus.getHeight()));
                                    }
                                }
                            }
                        }

                        if (mListener != null) {
                            mListener.onKeyBoardShow(height);
                        }
                    } else {
                        if (autoResize) {
                            MarginLayoutParams params = ((MarginLayoutParams) getLayoutParams());
                            params.bottomMargin = 0;
                            setLayoutParams(params);

                        }

                        if (mListener != null) {
                            mListener.onKeyBoardHide();
                        }
                    }
                }

                mKeyboardHeight = height;
            }
        }
    }



    public interface  OnKeyboardStateChangeListener{
        void onKeyBoardShow(int height);
        void onKeyBoardHide();
    }


}