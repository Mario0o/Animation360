package com.example.yyh.animation360.view;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.yyh.animation360.R;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by yyh on 2016/10/17.
 */
public class FloatCircleView extends View {
    public int width=100;
    public int heigth=100;
    private Paint circlePaint;//画圆
    private Paint textPaint; //画字
    private float availMemory; //已用内存
    private float totalMemory; //总内存
    private String text;   //显示的已用内存百分比
    private boolean isDraging=false; //是否在拖动状态。
    private Bitmap src;
    private Bitmap scaledBitmap; //缩放后的图片。

    public FloatCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPatints();
    }

    public FloatCircleView(Context context) {
        this(context, null);
    }
    public FloatCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 初始化画笔以及计算可用内存，总内存，和可用内存百分比。
     */
    public void initPatints() {
        circlePaint = new Paint();
        circlePaint.setColor(Color.CYAN);
        circlePaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(25);
        textPaint.setFakeBoldText(true);
        textPaint.setAntiAlias(true);

        src = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        scaledBitmap = Bitmap.createScaledBitmap(src, width, heigth, true);
        availMemory= (float) getAvailMemory(getContext());
        totalMemory= (float) getTotalMemory(getContext());
        text=(int)((availMemory/totalMemory)*100)+"%";

    }

    //设置小球的宽高。
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(width, heigth);//设置宽高
    }


    /**
     * 画小球及文字。如果小球是在拖动状态就显示android图标，如果不是拖动状态就显示小球。
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {

        if (isDraging){
            canvas.drawBitmap(scaledBitmap,0,0,null);
        }else {

            //1.画圆
            canvas.drawCircle(width / 2, heigth / 2, width / 2, circlePaint);

            //2.画text

            float textwidth = textPaint.measureText(text);//文本宽度
            float x = width / 2 - textwidth / 2;
            Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();

            float dy = -(fontMetrics.ascent + fontMetrics.descent) / 2;
            float y = heigth / 2 + dy;

            canvas.drawText(text, x, y, textPaint);
        }



    }
    public boolean setDrageState(boolean flag){
        isDraging=flag;
        invalidate();


        return flag;
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

}
