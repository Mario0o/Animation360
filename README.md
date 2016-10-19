# Animation360
仿360加速球（内存释放）
[TOC]
>现在手机上的悬浮窗应用越来越多，对用户来说，最常见的悬浮窗应用就是安全软件的悬浮小控件，拿360卫士来说，当开启悬浮窗时，它是一个小球，小球可以拖动，当点击小球出现大窗体控件，可以进行进一步的操作如：释放手机内存等等。于是借着慕课网的视频，仿着实现了360加速球，增加了点击小球进行释放内存的功能。
>由于是手机只有频幕截图：实现后如下图所示：点击开启按钮，出现悬浮窗小球控件上面显示手机的可用内存百分比；当拖动小球时，小球变为android图标；松开小球，小球依附在频幕两侧；点击小球，手机底部出现大窗体控件，点击里面的小球，进行手机内存的释放；点击手机屏幕的其他区域，大窗体消失，小球重新出现。

![开始界面](http://img.blog.csdn.net/20161018214820058)
![悬浮小球](http://img.blog.csdn.net/20161018215149068)
![大窗体显示](http://img.blog.csdn.net/20161018215215783)

接下来就是实现的一些重要步骤：

###1.FloatCircleView的实现（自定义view）
>实现FloatCircleView的过程就是自定义view的过程。1、自定义View的属性 2、在View的构造方法中获得我们自定义的属性 3、重写onMesure 4、重写onDraw。我们没有自定义其他属性所以省了好多步骤。

>**各种变量的初始化，设置拖动小球时要显示的图标，已经计算各种内存。（用于显示在小球上）**
```
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
        //设置图片
        src = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        //缩放后的图片（将图标设置的和悬浮小球一样大小。）
        scaledBitmap = Bitmap.createScaledBitmap(src, width, heigth, true);
        //计算已用内存，总内存，已用内存百分比，
        availMemory= (float) getAvailMemory(getContext());
        totalMemory= (float) getTotalMemory(getContext());
        text=(int)((availMemory/totalMemory)*100)+"%";
    }
```
>onMeasure();就是将固定的宽高写死，通过 `setMeasuredDimension(width, heigth);`传入。
>onDraw();进行悬浮小球绘制。定义一个boolean变量判断当前状态是否为拖动小球状态，如果是拖动小球状态，就在该位置绘制android图标，如果不是拖动状态，就进行小球绘制。画小球没有难度，关键是画字。下面的2个图可以加深对画字时的理解。
![这里写图片描述](http://img.blog.csdn.net/20161018224705454)
![这里写图片描述](http://img.blog.csdn.net/20161018224739700)
1.画字时的x坐标（1.`textPaint.measureText(text);得到字的宽度`2.小球的宽度/2-字的宽度/2。）
2.画字时的y坐标（1.`Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();`得到字体属性测量类。2.`(fontMetrics.ascent + fontMetrics.descent) / 2` 得到字的高度。3.小球的高度/2-字体的高度/2）
画个图就很好理解了：
![这里写图片描述](http://img.blog.csdn.net/20161019092715756)

```
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
```
>获得手机已用内存及总内存的方法：
```
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
```

###2.创建WindowManager窗体管理类，管理悬浮小球和底部大窗体。
>WindowManager类。用来管理整个悬浮小球和手机底部大窗体的显示和隐藏。
>必须在Manifest文件中增加`<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />`权限。
>通过 `WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);`获取窗体管理类；
>利用`wm.addView(view, params);`将view增加到窗体中。
>利用`wm.remove(view,params);`将view从窗体中移除。
> 利用`wm.updateViewLayout(view,params);`来更新view.
>`WindowManager.LayoutParams`用来设置view的各种属性。

>1.创建FloatViewManager实例。
```
 //单例模式创建
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
```
2.展示悬浮小球和展示底部窗体的方法。（展示窗体的方法同展示悬浮小球类似。）
```
  /**
     * 展示浮窗
     */
    public void showFloatCircleView(){
    //参数设置
        if (params==null){
            params = new WindowManager.LayoutParams();
            //宽高
            params.width=circleView.width;
            params.height=circleView.heigth;
            //对齐方式
            params.gravity= Gravity.TOP|Gravity.LEFT;
            //偏移量
            params.x=0;
            params.y=0;
            //类型
            params.type=WindowManager.LayoutParams.TYPE_TOAST;
            //设置该window属性。
            params.flags= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            //像素格式
            params.format= PixelFormat.RGBA_8888;
        }
        //将小球加入窗体中。
        wm.addView(circleView, params);
    }

 public void showFloatCircleView(){
 ......
 }
```
>3.当启动程序，首先创建悬浮小球，小球可以拖拽，点击小球，手机底部窗体显示（`FloatMenuView`），小球隐藏。所以，对小球（circleView）要对其进行`setOnTouchListener`和`setOnClickListener`事件监听。
>分析小球的事件分发; 对于小球：
>当ACTION_DOWN时，记录小球的downX,downY,以及startX,startY，
>当ACTION_MOVE时，将circleView是否拖拽状态置为true,记录小球的moveX,moveY,计算小球移动的距离（dx,dy），然后根据 `wm.updateViewLayout(circleView,params);`更新小球位置。最后将最后move的坐标赋值给startX,startY。
>当ACTION_UP时，将circleView是否拖拽置为false,记录抬起时的坐标，upx，根据upx和手机屏幕宽度/2，进行判断，来觉得最终小球是贴在屏幕左侧，还是右侧。后面为小球拖拽的误差。当小球拖拽的距离小于10个像素时，可以触发小球的点击事件。（小球的Touch事件，优先于小球的点击事件，当Touch事件返回true时，此事件被消费，不再向下传递事件。当Touch事件返回false时，此事件继续向下传递，从而触发小球的点击事件。）
>小球的点击事件：点击小球，悬浮小球隐藏，手机底部窗体出现。并设置有底部窗体出现时的过渡动画。
```
//给circleView设置touch监听。
    private View.OnTouchListener circleViewOnTouchListener=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                //最后按下时的坐标，根据ACTION_MOVE理解。
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
                    startX= moveX;
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
                   wm.updateViewLayout(circleView,params);
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
```
###3.MyProgreeView（手机底部窗体中小球的实现）。
>1.初始化画笔，对view进行手势监听。监听单击和双击事件。（必须设置view是可以点击的）
```
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
		//手势监听。
        gestureDetector = new GestureDetector(new MyGertureDetectorListener());
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
        //设置view可以点击。
        setClickable(true);
    }
    class MyGertureDetectorListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onDoubleTap(MotionEvent e) {
        ......
          //双击事件的逻辑
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
        ......
         //单击事件的逻辑
            return super.onSingleTapConfirmed(e);
        }
    }
```
>2.用handler交互进行单击和双击事件的状态更新。单击时，利用贝塞尔曲线，实现波纹荡漾效果。双击时，波纹不断下降，进行内存释放，最后显示内存释放后的已用内存百分比。handler发送周期消息，让单击事件和双击事件的小球不断进行重绘。（重绘在下一小节讲）。

```
//单击事件发送周期handler.
private void startSingleTapAnimation() {
        handler.postDelayed(singleTapRunnable,200);  
    }
    private SingleTapRunnable singleTapRunnable=new SingleTapRunnable();
    class SingleTapRunnable implements Runnable{
        @Override
        public void run() {
            count--;
            if (count>=0) {
                invalidate();//不断进行重绘。
                handler.postDelayed(singleTapRunnable,200);
            }else {
                handler.removeCallbacks(singleTapRunnable);
                count=50;
            }
        }
    }
    //双击事件发送周期handler。
    private void startDoubleTapAnimation() {
        handler.postDelayed(runnbale,50);
    }
    private DoubleTapRunnable runnbale=new DoubleTapRunnable();

    class DoubleTapRunnable implements Runnable{
        @Override
        public void run() {
            num--;
            if (num>=0){
                invalidate();//不断进行重绘。
                handler.postDelayed(runnbale,50);
            }else {
                handler.removeCallbacks(runnbale);
                //释放内存。
             killprocess();
             //计算释放后的已用内存百分比。
                num=(int)(((float)currentProgress/max)*100);
            }
        }
    }
```
>3.单击事件和双击事件的重绘。
>首先是小球的绘制，和波纹路径的绘制。

```
		//绘制小球
		bitmapCanvas.drawCircle(width / 2, heigth / 2, width / 2, circlepaint);
		//根据path,绘制波纹路径。每次绘制前将上次的path,reset.
        path.reset();
        float y =(1-(float)num/100)*heigth;
        path.moveTo(width, y);
        path.lineTo(width, heigth);
        path.lineTo(0, heigth);
        path.lineTo(0, y);
```
>接着利用贝塞尔曲线将波纹路径绘制。
>[Android-贝塞尔曲线](http://blog.csdn.net/z82367825/article/details/51599245 "贝塞尔曲线的讲解")
>[贝塞尔曲线在android中的应用](http://blog.csdn.net/z82367825/article/details/51599245)
>这里有详细的讲解贝塞尔曲线。其实不需要深入的理解。只要知道能用它来实现水波纹效果就行了（贝塞尔曲线用处很多，翻书效果也可以用它实现。）主要利用 `path.rQuadTo(x1,y1,x2,y2);` 终点（x2，y2），辅助控制点（x1，y1）的贝塞尔曲线。因此，通过不断改变y1的位置，我们可以绘制出水波纹的效果。
>首先判断它是否为双击击事件：
> 若是双击：设置一个变量d,通过不断改变d的值（d的值的改变由num引起，而num实在handler中不断减小的。num--;），来绘制贝塞尔曲线。实现水波纹的下降效果。
>若是单击：设置一个count值，通过不断改变count值（count值的改变是在handler中实现的。count--;），首先判断count是否能被2整除，交替绘制这两条贝塞尔曲线。（这两条贝塞尔曲线正好相反），从而实现水波荡漾的效果。   
>（用for循环是实现水波的波数，一对path.rQuadTo();只能实现一次波纹。可以自己去验证）                                  
```
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
```
>最后是释放内存的方法。记得要在Manifest文件中增加`  <uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES"/>`权限。
```
  public void killprocess(){
        ActivityManager activityManger=(ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list=activityManger.getRunningAppProcesses();
        if(list!=null)
            for(int i=0;i<list.size();i++)
            {
                ActivityManager.RunningAppProcessInfo apinfo=list.get(i);
                String[] pkgList=apinfo.pkgList;
  if(apinfo.importance>ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE)
                {
                    // Process.killProcess(apinfo.pid);
                    for(int j=0;j<pkgList.length;j++) {
                        boolean flag=pkgList[j].contains("com.example.yyh.animation360");//这里要判断是否为当前应用，要不然也可能会结束当前应用。
                        if(!flag){
                        activityManger.killBackgroundProcesses(pkgList[j]);
                    }
                    }
                }
            }
```
###4.FloatMenuView的实现。
>1.创建一个float_menuview.xml；其中包括一个ImageView+TextView+自定义的MyProgreeView。
>底部窗体要被设置能被点击。`android:clickable="true"`；

```
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#33000000"
    >
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:background="#F02F3942"
        android:layout_alignParentBottom="true"
        android:id="@+id/ll"
        android:clickable="true"
        >
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            >
            <ImageView
                android:layout_width="50dp"
                android:layout_height="50dp"
                android:src="@mipmap/ic_launcher"
                android:layout_gravity="center_vertical"
                />
            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:textSize="15sp"
                android:textColor="#c93944"
                android:text="360加速球"
                android:layout_gravity="center_vertical"
                />
        </LinearLayout>
        <com.example.yyh.animation360.view.MyProgreeView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center_horizontal"
            android:layout_marginTop="10dp"
            />
    </LinearLayout>
</RelativeLayout>
```
>2.将FloatMenuView 根据条件，利用（`wm.addView(view, params);`将view增加到窗体中。 
>利用`wm.remove(view,params);`将view从窗体中移除。）方法，进行底部窗体view的显示和隐藏 
>`TranslateAnimation`类用来设置底部窗体进入时的动画效果。`TranslateAnimation(int fromXType,float fromXValue,int toXType,float toXValue,int fromYType,float fromYValue,int toYType,float toYValue)`
>int fromXType:x轴方向起始的参照值有3个选项。（1.`Animation.ABSOLUTE`：具体的坐标值，指绝对的屏幕像素单位。2.`Animation.RELATIVE_TO_SELF`：相对自己的坐标值。3.`Animation.RELATIVE_TO_PARENT`：相对父容器的坐标值。）
>float fromXValue 第二个参数是第一个参数类型的起始值(例如若第一个参数设置为Animation.RELATIVE_TO_SELF，第二个参数为0.1f,就表示为自己的坐标值乘以0.1)；
>int toXType：x轴方向终点的参照值有3个选项同第一个参数。
>float toValue:第四个参数是第三个参数类型的起始值。
>Y轴方向的参数同理。起点+终点；（每个参数后一个参数为前一个参数的起始值。）
>并对此view设置OnTouchListener，<font color=Crimson >OnTouch事件最后必须返回false,表示此事件仍然需要向下传递。</font>从而实现点击手机其他区域时，手机底部窗体隐藏，悬浮小球显示，点击底部窗体时无变化，点击底部窗体中的小球时，触发其单击和双击事件。
```
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
```
###5.MyFloatService
>用来创建FloatVIewManager单例，管理悬浮小球+手机底部窗体的创建和移除。
```
public class MyFloatService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        //用来开启FloatViewManager
        FloatViewManager manager=FloatViewManager.getInstance(this);
        manager.showFloatCircleView();
        super.onCreate();
    }

}
```
###6.MainActivity的实现
>定义一个intent，开启服务（在服务中创建WindowManager单例对象，进行悬浮小球和手机底部窗体的管理。），关闭当前的activity。

```
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void startService(View view){
        Intent intent=new Intent(this, MyFloatService.class);
        startService(intent);
        finish();
    }
}
```
>完结。
>源码：仿360加速球（内存释放）[github](https://github.com/Mario0o/Animation360/tree/master)...........[csdn](http://download.csdn.net/detail/yyh448522331/9658300)





