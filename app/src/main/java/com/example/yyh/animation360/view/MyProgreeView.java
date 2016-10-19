package com.example.yyh.animation360.view;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by yyh on 2016/10/18.
 */
public class MyProgreeView extends View {
    private int width=150;
    private int heigth=150;
    private Paint textPaint;
    private Bitmap bitmap;
    private Canvas bitmapCanvas;
    private Paint circlepaint;

    private Path path=new Path();
    private int count=50;

    private Paint progerssPaint;
    private GestureDetector gestureDetector;

    private boolean isSingleTap=false;



    private int currentProgress= (int) getAvailMemory(getContext());
    private int max= (int) getTotalMemory(getContext());
    private int num=(int)(((float)currentProgress/max)*100);


    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {

        }
    };

    public MyProgreeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();

    }

    private void initPaint() {
        //画圆画笔
        circlepaint = new Paint();
        circlepaint.setColor(Color.argb(0xff, 0x3a, 0x8c, 0x6c));
        circlepaint.setAntiAlias(true);
        //画进度条画笔
        progerssPaint = new Paint();
        progerssPaint.setAntiAlias(true);
        progerssPaint.setColor(Color.argb(0xff, 0x4e, 0xcc, 0x66));
        progerssPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));//绘制重叠部分
        //画进度画笔
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(25);

        //画布
        bitmap = Bitmap.createBitmap(width, heigth, Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);

        gestureDetector = new GestureDetector(new MyGertureDetectorListener());
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
        setClickable(true);


    }
    class MyGertureDetectorListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDoubleTap(MotionEvent e) {
          //  Toast.makeText(getContext(),"双击",Toast.LENGTH_SHORT).show();
            startDoubleTapAnimation();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
           // Toast.makeText(getContext(),"单击",Toast.LENGTH_SHORT).show();
            isSingleTap=true;
            num= (int) ((float) getAvailMemory(getContext())/getTotalMemory(getContext())*100);
            startSingleTapAnimation();
            return super.onSingleTapConfirmed(e);
        }


    }

    private void startSingleTapAnimation() {
        handler.postDelayed(singleTapRunnable,200);

    }
    private SingleTapRunnable singleTapRunnable=new SingleTapRunnable();
    class SingleTapRunnable implements Runnable{
        @Override
        public void run() {
            count--;
            if (count>=0) {
                invalidate();
                handler.postDelayed(singleTapRunnable,200);
            }else {
                handler.removeCallbacks(singleTapRunnable);
                count=50;
            }
        }
    }
    private void startDoubleTapAnimation() {
        handler.postDelayed(runnbale,50);
    }
    private DoubleTapRunnable runnbale=new DoubleTapRunnable();

    class DoubleTapRunnable implements Runnable{
        @Override
        public void run() {
            num--;
            if (num>=0){
                invalidate();
                handler.postDelayed(runnbale,50);
            }else {
                handler.removeCallbacks(runnbale);
             killprocess();
                num=(int)(((float)currentProgress/max)*100);
            }
        }
    }

    public MyProgreeView(Context context, AttributeSet attrs) {
        this(context, null, 0);
    }

    public MyProgreeView(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(width, heigth);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        bitmapCanvas.drawCircle(width / 2, heigth / 2, width / 2, circlepaint);
        path.reset();
        float y =(1-(float)num/100)*heigth;
        path.moveTo(width, y);
        path.lineTo(width, heigth);
        path.lineTo(0, heigth);
        path.lineTo(0, y);
        if (!isSingleTap){
        float d=(1-(float)num/(100/2))*10;
            for (int i=0;i<3;i++){
            path.rQuadTo(10,-d,20,0);
            path.rQuadTo(10,d,20,0);
             }
        }else {
            float d=(float)count/50*10;
            if (count%2==0){
                for (int i=0;i<=3;i++){
                    path.rQuadTo(10,-d,30,0);
                    path.rQuadTo(10,d,30,0);
                }
            }else {
                for (int i=0;i<=3;i++){
                    path.rQuadTo(10,d,30,0);
                    path.rQuadTo(10,-d,30,0);
                }

            }
        }
        path.close();
        bitmapCanvas.drawPath(path,progerssPaint);
        String text =num+"%";
        float textWidth=textPaint.measureText(text);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float baseLine = heigth/2-(fontMetrics.ascent + fontMetrics.descent)/2;
        bitmapCanvas.drawText(text, width / 2 - textWidth / 2, baseLine, textPaint);
        canvas.drawBitmap(bitmap, 0, 0, null);


    }

    public long getAvailMemory(Context context)
    {
        // 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        //mi.availMem; 当前系统的可用内存
        //return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
        return mi.availMem/(1024*1024);
    }
    public long getTotalMemory(Context context)
    {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;
        try
        {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                Log.i(str2, num + "\t");
            }
            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();
        } catch (IOException e) {
        }
        //return Formatter.formatFileSize(context, initial_memory);// Byte转换为KB或者MB，内存大小规格化
        return initial_memory/(1024*1024);
    }


    public void killprocess(){
        ActivityManager activityManger=(ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list=activityManger.getRunningAppProcesses();
        if(list!=null)
            for(int i=0;i<list.size();i++)
            {
                ActivityManager.RunningAppProcessInfo apinfo=list.get(i);

                System.out.println("pid"+apinfo.pid);
                System.out.println("processName "+apinfo.processName);
                System.out.println("importance   "+apinfo.importance);
                String[] pkgList=apinfo.pkgList;

                if(apinfo.importance>ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE)
                {
                    // Process.killProcess(apinfo.pid);

                    for(int j=0;j<pkgList.length;j++) {
                        boolean flag=pkgList[j].contains("com.example.yyh.animation360");
                        System.out.println("pkgList...123123123123123123   " +flag);

                        if(!flag){
                        System.out.println("pkgList...   " + pkgList[j]);
                        //2.2以上是过时的,请用killBackgroundProcesses代替
                        // activityManger.restartPackage(pkgList[j]);
                        activityManger.killBackgroundProcesses(pkgList[j]);
                    }
                    }
                }
            }
    }








}
