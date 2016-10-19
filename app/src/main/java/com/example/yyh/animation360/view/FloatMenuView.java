package com.example.yyh.animation360.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.example.yyh.animation360.R;
import com.example.yyh.animation360.activity.FloatViewManager;

/**
 * Created by yyh on 2016/10/18.
 */
public class FloatMenuView extends LinearLayout {

    private TranslateAnimation translateAnimation;

    public FloatMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view =View.inflate(getContext(), R.layout.float_menuview,null);
        LinearLayout linearLayout= (LinearLayout) view.findViewById(R.id.ll);
        translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,1.0f,Animation.RELATIVE_TO_SELF,0);
        translateAnimation.setDuration(500);
        translateAnimation.setFillAfter(true);
        linearLayout.setAnimation(translateAnimation);
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FloatViewManager manager=FloatViewManager.getInstance(getContext());
                manager.hideFloatMenuView();
                manager.showFloatCircleView();

                return false;
            }
        });
        addView(view);

    }

    public FloatMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatMenuView(Context context) {
        this(context, null);
    }
    public void startAnimation(){
        translateAnimation.start();

    }
}
