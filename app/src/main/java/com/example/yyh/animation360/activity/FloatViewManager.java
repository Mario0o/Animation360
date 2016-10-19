package com.example.yyh.animation360.activity;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;


import com.example.yyh.animation360.view.FloatCircleView;
import com.example.yyh.animation360.view.FloatMenuView;

/**
 * Created by yyh on 2016/10/17.
 */
public class FloatViewManager {
    //浮窗管理类用来管理悬浮窗的显示和隐藏。
    private Context context;
    private static FloatViewManager inStance;
    private  WindowManager wm;//通過WindowManager 來操控浮窗的顯示和隱藏。
    private FloatCircleView circleView;

    //初始的位置
    private float startX;
    private float startY;
    //按下的位置
    private float downX;
    private float downY;

    //移动后的位置
    private float moveX;
    private float moveY;


    private WindowManager.LayoutParams params;

    private  FloatMenuView floatMenuView;

 //给circleView设置touch监听。
    private View.OnTouchListener circleViewOnTouchListener=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    //最后按下时的坐标。看ACTION_MOVE就会理解。
                    startX = event.getRawX();
                    startY = event.getRawY();

                    //按下时的坐标。
                    downX = event.getRawX();
                    downY = event.getRawY();

                    break;
                case MotionEvent.ACTION_MOVE:
                    circleView.setDrageState(true);
                      moveX = event.getRawX();
                      moveY=event.getRawY();

                    float dx = moveX -startX;
                    float dy=moveY-startY;
                    params.x+=dx;
                    params.y+=dy;

                    wm.updateViewLayout(circleView,params);
                    startX=moveX;
                    startY=moveY;

                    break;
                case MotionEvent.ACTION_UP:
                    float upx=event.getRawX();
                    if (upx>getScreenWidth()/2){
                        params.x=getScreenWidth()-circleView.width;
                    }else {
                        params.x=0;
                    }
                    circleView.setDrageState(false);
                   wm.updateViewLayout(circleView, params);

                    if (Math.abs(moveX-downX)>10){
                        return true;
                    }else {
                        return false;
                    }
                default:
                    break;
            }
            return false;
        }
    };


    private int getScreenWidth() {


      return wm.getDefaultDisplay().getWidth();

    }

    //得到屏幕的高度。
    private int getScreenHeigth() {

        return wm.getDefaultDisplay().getHeight();
    }

    //得到状态栏的高度。
    private int getStatusHeight(){
        Class<?> clazz = null;
         Object object;
        try {
            clazz = Class.forName("com.android.internal.R$dimen");
            object = clazz.newInstance();
            int x = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            return context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            return 0;
        }

    }


    private FloatViewManager( Context context){
        this.context=context;
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        circleView =new FloatCircleView(context);
        circleView.setOnTouchListener(circleViewOnTouchListener);
        circleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(, "onclick", Toast.LENGTH_SHORT).show();
                //隱藏circleView，顯示菜单栏。
                wm.removeView(circleView);
                showFloatMenuView();
                floatMenuView.startAnimation();
            }
        });
        floatMenuView = new FloatMenuView(context);


    }

    private void showFloatMenuView() {
       WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width=getScreenWidth();
        params.height=getScreenHeigth()-getStatusHeight();
        params.gravity= Gravity.BOTTOM|Gravity.LEFT;
        params.x=0;
        params.y=0;
        params.type=WindowManager.LayoutParams.TYPE_TOAST;
        params.flags= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        params.format= PixelFormat.RGBA_8888;


        wm.addView(floatMenuView, params);

    }

    public static FloatViewManager getInstance(Context context){
        if (inStance==null){
            synchronized(FloatViewManager.class){
                if (inStance==null){
                    inStance=new FloatViewManager(context);
                }
            }
        }
        return inStance;
    }

    /**
     * 展示浮窗
     */
    public void showFloatCircleView(){
        //参数设置
        if (params==null){
            params = new WindowManager.LayoutParams();
            params.width=circleView.width;
            params.height=circleView.heigth;
            params.gravity= Gravity.TOP|Gravity.LEFT;
            params.x=0;
            params.y=0;
            params.type=WindowManager.LayoutParams.TYPE_TOAST;
            params.flags= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            params.format= PixelFormat.RGBA_8888;
        }
        //将小球加入窗体中。
        wm.addView(circleView, params);
    }
    public void hideFloatMenuView(){
        wm.removeView(floatMenuView);


    }





}
