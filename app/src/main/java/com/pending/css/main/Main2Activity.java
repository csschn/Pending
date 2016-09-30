package com.pending.css.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.pending.css.adapter.ScheduleAdapter;
import com.pending.css.bean.Schedule;
import com.pending.css.bean.ScheduleFolder;
import com.pending.css.bean.User;
import com.pending.css.config.Constants;
import com.pending.css.dao.ScheduleFolderDao;
import com.pending.css.login.LoginActivity;
import com.pending.css.login.R;
import com.pending.css.login.SetLockActivity;
import com.pending.css.remind.InitAlarmService;
import com.pending.css.util.ActivityCollector;
import com.pending.css.util.ImageLoadHelper;
import com.pending.css.util.PreferenceUtil;
import com.pending.css.util.ScreenUtils;
import com.pending.css.util.T;
import com.pending.css.widget.SlideCutListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 *
 */
public class Main2Activity  extends     AppCompatActivity
                            implements  NavigationView.OnNavigationItemSelectedListener ,
                                        SlideCutListView.RemoveListener {
    public static Main2Activity instance = null;
    private ImageView ivAvatar;
    private TextView nickShowLeft;
    private TextView emailShowLeft;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private SlideCutListView scheduleNoFinished;
    private PopupWindow popupwindow;
    private EditText ed;
    private TextView current_status;

    private User user;//当前用户
    private String isLoadAvater = "N";//启动时是否加载图像
    private String userInfoIsChangeed = "N";//用户的信息是否改变
    private int mBackKeyPressedTimes = 0;


    private ScheduleAdapter adapter;//代办事项ListView的适配器
    private ArrayList<String> scheduleFolderNameList = new ArrayList<String>();//清单名称列表
    private Map map = new HashMap();//用来存放ScheduleFolder的objectId
//    private int[] scheduleFolderNameItemList = new int[50];//标示清单Item的id
    private ArrayList<Integer> scheduleFolderNameItemList = new ArrayList<Integer>();
    private List<Schedule> scheduleList = new ArrayList<Schedule>();//代办事项列表
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main2);
        user = BmobUser.getCurrentUser(Main2Activity.this, User.class);
        startService(new Intent(this, InitAlarmService.class));
        setTitle("今天");
        instance = this;
        getItemListData(getTitle().toString());
        //初始化Bmob sdk
        Bmob.initialize(this, Constants.BMOB_APP_KEY);

        initView();//初始化控件
        initData();//初始化数据
        getMenuList();//初始化清单文件夹数据
    }

    @Override
    protected void onResume() {
//        getItemListData(getTitle().toString());
        super.onResume();
    }

    private void initSchedule()
    {
        adapter = new ScheduleAdapter(Main2Activity.this, scheduleList, R.layout.schedule_list_view);
        scheduleNoFinished = (SlideCutListView) findViewById(R.id.pending_no_finished);
        scheduleNoFinished.setRemoveListener(Main2Activity.this);//设置监听
        scheduleNoFinished.setAdapter(adapter);
        scheduleNoFinished.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Schedule schedule = scheduleList.get(position);
//                T.showDefind(Main2Activity.this, "你点击的位置是第" + (position + 1) + "个,内容时:" + schedule.getContent());
                Intent intent = new Intent(Main2Activity.this, AddPendingActivity.class);
                intent.putExtra("schedule_data", schedule);
                intent.putExtra("sFolderName", getTitle().toString());
                intent.putExtra("type", "edit");
                startActivityForResult(intent, 0);
            }
        });
    }

    /**
     *刷新左侧清单的列表显示
     *@auther css
     *created at 2016/4/21 9:53
     */
    private void refreshMuneList()
    {
        scheduleFolderNameItemList.clear();
        final Menu menu = navigationView.getMenu();
        menu.removeGroup(R.id.add_sFolder);
        String sql = "select * from ScheduleFolder where user_id ='"+user.getObjectId()+"'";
        new BmobQuery<ScheduleFolder>().doSQLQuery(getApplicationContext(), sql, new SQLQueryListener<ScheduleFolder>() {

            @Override
            public void done(BmobQueryResult<ScheduleFolder> result, BmobException e) {
                if (e == null) {
                    List<ScheduleFolder> list = result.getResults();
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            Log.d("list", i + "");
                            Log.d("list", i + "9999" + list.get(i).getSchedule_folder_name());
                            scheduleFolderNameList.add(i, list.get(i).getSchedule_folder_name());
                            menu.add(R.id.add_sFolder, i, 0, list.get(i).getSchedule_folder_name()).setIcon(R.mipmap.s_folder_list).setCheckable(true);
                            scheduleFolderNameItemList.add(i);
                            Log.d("scheduleFolder", scheduleFolderNameItemList.size() + "");
                        }
                    } else {
                        Log.i("smile", "查询成功，无数据返回");
                    }
                } else {
                    Log.i("smile", "错误码：" + e.getErrorCode() + "，错误描述：" + e.getMessage());
                }
            }
        });
    }


    /**
     *
     *@auther css
     *created at 2016/4/22 23:22
     */
    public void getItemListDataForIsFinished(String sFolderName)
    {
        scheduleList.clear();
        String sql;
        if (sFolderName.equals("今天")) {
            Calendar calendar = Calendar.getInstance();
            String remindTimeLableTemp = calendar.get(Calendar.YEAR) + "年" +
                    timeFormatString(calendar.get(Calendar.MONTH) + 1) + "月" +
                    timeFormatString(calendar.get(Calendar.DAY_OF_MONTH)) + "日";
            sql = "select * from Schedule   where  remind_time_lable like'" + remindTimeLableTemp + "%'" +
                    "                       and user_id = '" + user.getObjectId() + "'" +
                    "                       and status = true" +
                    "                       order by priority desc";
            Log.d("smile", "sql1  true   " + sql);
        } else {
            sql = "select * from Schedule where schedule_folder_name = '" + sFolderName + "'" +
                    "                           and user_id = '" + user.getObjectId() + "'" +
                    "                           and status = true" +
                    "                           order by priority desc";
            Log.d("smile", "sql2" + sql);
        }
        final ProgressDialog progress = new ProgressDialog(Main2Activity.this);
        progress.setMessage("正在加载中...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        new BmobQuery<Schedule>().doSQLQuery(Main2Activity.this, sql, new SQLQueryListener<Schedule>() {
            @Override
            public void done(BmobQueryResult<Schedule> result, BmobException e) {
                if (e == null) {
                    List<Schedule> list = result.getResults();
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            scheduleList.add(list.get(i));
                            Log.d("lili", "true   " + scheduleList.toString());
                            progress.dismiss();
                        }
                        Main2Activity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initSchedule();
                                progress.dismiss();
                            }
                        });
                    } else {
                        Main2Activity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initSchedule();
                                progress.dismiss();
                            }
                        });
                        Log.i("smile", "ture     查询成功，无数据返回");
                    }
                } else {
                    Log.i("smile", "ture     错误码：" + e.getErrorCode() + "，错误描述：" + e.getMessage());
                }
            }
        });
    }
    /**
     *获取主界面中待办事项的列表内容，并且显示出来
     *@auther css
     *created at 2016/4/21 9:54
     */
    public void getItemListData(String sFolderName)
    {
        scheduleList.clear();
        String sql;
        if (sFolderName.equals("今天"))
        {
            Calendar calendar = Calendar.getInstance();
            String remindTimeLableTemp = calendar.get(Calendar.YEAR)+"年"+
                    timeFormatString(calendar.get(Calendar.MONTH) + 1)+"月"+
                    timeFormatString(calendar.get(Calendar.DAY_OF_MONTH))+"日";
            sql = "select * from Schedule   where  remind_time_lable like'"+remindTimeLableTemp+"%'" +
                    "                       and user_id = '"+user.getObjectId()+"'" +
                    "                       and status = false" +
                    "                       order by priority desc";
            Log.d("smile","sql1"+sql);
        }
        else
        {
            sql = "select * from Schedule where schedule_folder_name = '"+sFolderName+"'" +
                    "                           and user_id = '"+user.getObjectId()+"'" +
                    "                           and status = false" +
                    "                           order by priority desc";
            Log.d("smile", "sql2" + sql);
        }
        final ProgressDialog progress = new ProgressDialog(Main2Activity.this);
        progress.setMessage("正在加载中...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        new BmobQuery<Schedule>().doSQLQuery(getApplicationContext(), sql, new SQLQueryListener<Schedule>() {
            @Override
            public void done(BmobQueryResult<Schedule> result, BmobException e) {
                if (e == null) {
                    List<Schedule> list = result.getResults();
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            scheduleList.add(list.get(i));
                            progress.dismiss();
                        }
                        Main2Activity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initSchedule();
                            }
                        });
                    } else {
                        initSchedule();
                        progress.dismiss();
                    }
                } else {
                    progress.dismiss();
                    Log.d("listfor", getTitle().toString() + "2");
                    Log.i("smile", "错误码：" + e.getErrorCode() + "，错误描述：" + e.getMessage());
                }
            }
        });
    }

    /**
     *加载左侧清单的列表显示
     *@auther css
     *created at 2016/4/21 9:53
     */
    private void getMenuList()
    {
        String sql = "select * from ScheduleFolder where user_id ='"+user.getObjectId()+"'";
        new BmobQuery<ScheduleFolder>().doSQLQuery(getApplicationContext(), sql, new SQLQueryListener<ScheduleFolder>() {
            @Override
            public void done(BmobQueryResult<ScheduleFolder> result, BmobException e) {
                if (e == null) {
                    List<ScheduleFolder> list = result.getResults();
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            Log.d("list", i + "");
                            Log.d("list", i + "9999" + list.get(i).getSchedule_folder_name());
                            scheduleFolderNameList.add(i, list.get(i).getSchedule_folder_name());
                            Menu menu = navigationView.getMenu();
//                            menu.removeGroup(R.id.edit_sFolder);
                            menu.add(R.id.add_sFolder, i, 0, list.get(i).getSchedule_folder_name()).setIcon(R.mipmap.s_folder_list).setCheckable(true);
                            scheduleFolderNameItemList.add(i);
//                            scheduleFolderNameItemObjId.put(i,list.get(i).getObjectId());
                            map.put(list.get(i).getSchedule_folder_name(), list.get(i).getObjectId());
                        }
//                        Log.d("size",scheduleFolderNameItemObjId.size()+"");
                    } else {
                        Log.i("smile", "查询成功，无数据返回");
                    }
                } else {
                    Log.i("smile", "错误码：" + e.getErrorCode() + "，错误描述：" + e.getMessage());
                }
            }
        });

    }

    /**
     *重写finish()方法，关闭所有活动
     *@auther css
     *created at 2016/4/21 9:55
     */
    @Override
    public void finish() {
        super.finish();
        ActivityCollector.finishAll();
    }

    /**
     *初始化显示数据
     *@auther css
     *created at 2016/4/21 9:55
     */
    public void initView()
    {
        current_status = (TextView) findViewById(R.id.current_status);
        //实例化Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        toolbar.setOverflowIcon();
        //桌面悬浮的菜单
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main2Activity.this, AddPendingActivity.class);
                intent.putExtra("sFolderName", getTitle().toString());
                intent.putExtra("type", "new");
                startActivityForResult(intent, 0);
//                Snackbar.make(view, user.getObjectId().toString(), Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
        initNavigationView();
    }

    /**
     *初始化initNavigationView
     *@auther css
     *created at 2016/4/21 9:56
     */
    private void initNavigationView()
    {
        //实例化DrawerLayout布局控件
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        /*ActionBarDrawerToggle实现了DrawerListener，所以他能做DrawerListener可以做的任何事情，
        同时他还能将drawerLayout的展开和隐藏与actionbar的app 图标关联起来，当展开与隐藏的时候图标
        有一定的平移效果，点击图标的时候还能展开或者隐藏菜单。*/
        //使用ActionBarDrawerToggle来监听
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
        //实例化NavigationView
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        //调用getHeaderView方法获得Header
        View headerView = navigationView.getHeaderView(0);


//        T.showDefind(Main2Activity.this, m.getItem(2).toString());
        //通过Header来获取自定义控件
        ivAvatar = (ImageView) headerView.findViewById(R.id.right_login_head1);
        nickShowLeft = (TextView) headerView.findViewById(R.id.nick_show_left);
        emailShowLeft = (TextView) headerView.findViewById(R.id.email_show_left);
        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main2Activity.this, UserInfoActivity.class);
                intent.putExtra("isLoadAvater", isLoadAvater);
                startActivityForResult(intent, 1);
            }
        });
        //downloadTest.setImageResource(R.drawable.logo);
        //设置ImageView的点击事件
        navigationView.setNavigationItemSelectedListener(this);
    }


    /**
     *初始化数据
     *@auther css
     *created at 2016/4/21 9:56
     */
    public void initData()
    {
        if (user.getPictureUrl()!=null && user.getPictureUrl() !="")
        {
            String path = Environment.getExternalStorageDirectory()+"/Pending";
            String pictureName = "Avatar.jpeg";
            ImageLoadHelper imageLoadHelper = new ImageLoadHelper(ivAvatar,path,"images",pictureName);
            imageLoadHelper.downloadFromNetToLocal(user.getPictureUrl());
            isLoadAvater = "Y";
        }
        else
        {
            isLoadAvater = "N";
            ivAvatar.setImageResource(R.drawable.people_bg_default);
        }
        //设置昵称和邮件信息
        if (user.getNickName() != null )
        {
            nickShowLeft.setText(user.getNickName().toString());
        }
        if (user.getEmail() != null && user.getEmailVerified()==true)
        {
            emailShowLeft.setText(user.getEmail().toString());
        }
        else
        {
            emailShowLeft.setText("邮箱未验证！");
        }
    }
    private void initDataFromLocal()
    {
        String path = Environment.getExternalStorageDirectory() + "/Pending/images/Avatar.jpeg";
        try {
            Bitmap bitmap = ImageLoadHelper.decodeBitmap(path, 200, 200);
            ivAvatar.setImageBitmap(bitmap);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        //设置昵称和邮件信息
        if (user.getNickName() != null )
        {
            nickShowLeft.setText(user.getNickName().toString());
        }
        if (user.getEmail() != null && user.getEmailVerified()==true) {
            emailShowLeft.setText(user.getEmail().toString());
        }
        else {
            emailShowLeft.setText("邮箱未验证！");
        }
    }

//    /**
//     *重写按下返回键时触发的事件
//     *@auther css
//     *created at 2016/4/21 9:56
//     */
//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }

    /**
     *设置菜单的加载选项。
     *@auther css
     *created at 2016/4/21 9:57
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }


    /**
     *设置菜单按钮的点击事件
     *@auther css
     *created at 2016/4/21 9:57
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_finished:
                if (item.getTitle().equals("未完成"))
                {
                    current_status.setText("未完成");
                    item.setTitle("已完成");
                    getItemListData(getTitle().toString());
                }
                else
                {
                    current_status.setText("已完成");
                    item.setTitle("未完成");
                    getItemListDataForIsFinished(getTitle().toString());
                }
                break;
            case R.id.action_seach:
                if (popupwindow != null&&popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    return true;
                } else {
                    View view = new View(this);
                    Rect frame = new Rect();
                    getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
                    //状态栏高度：frame.top
                    int xOffset = toolbar.getHeight();//减去阴影宽度，适配UI.
                    initmPopupWindowView(xOffset);
                    popupwindow.showAsDropDown(view, 0, ScreenUtils.getStatusHeight(Main2Activity.this));
                    openKeyboard(new Handler(), 300);
                }
                break;

            case R.id.action_refresh:
                if (current_status.getText().toString().equals("未完成"))
                {
                    getItemListData(getTitle().toString());
                }
                else
                {
                    getItemListDataForIsFinished(getTitle().toString());
                }
                break;

            case R.id.action_settings:
                PreferenceUtil.setGesturePassword(this, "");
                Intent intent = new Intent(this, SetLockActivity.class);
//                intent.putExtra("LockPattern","reset");
                startActivity(intent);
                break;

            case R.id.action_close:
                PreferenceUtil.setGesturePassword(this, "");
                T.showDefind(Main2Activity.this,"图形解锁密码已关闭");
                break;

            case R.id.login_out:
                ActivityCollector.finishAll();
                this.finish();
                BmobUser.logOut(this);   //清除缓存用户对象
                BmobUser currentUser = BmobUser.getCurrentUser(this); // 现在的currentUser是null了
                Intent intent1 = new Intent(this, LoginActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent1.putExtra("flag",1);
                startActivity(intent1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initmPopupWindowView(int height1)
    {
        // // 获取自定义布局文件pop.xml的视图
        View customView = getLayoutInflater().inflate(R.layout.popview_item,
                null, false);
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        // 创建PopupWindow实例,200,150分别是宽度和高度
        popupwindow = new PopupWindow(customView, width, height1);
        //设置可以获取焦点，否则弹出菜单中的EditText是无法获取输入的
        popupwindow.setFocusable(true);
        //这句是为了防止弹出菜单获取焦点之后，点击activity的其他组件没有响应
        popupwindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
        popupwindow.setAnimationStyle(R.style.AnimationFade);
        // 自定义view添加触摸事件，
        customView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    popupwindow = null;
                }

                return false;
            }
        });

        /** 在这里可以实现自定义视图的功能 */
        ed = (EditText) customView.findViewById(R.id.edit);
        TextView confirm = (TextView) customView.findViewById(R.id.confirm);
        ed.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    getMenuListDataForSearch(getTitle().toString(),ed.getText().toString());
                    popupwindow.dismiss();
                }
                return false;
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMenuListDataForSearch(getTitle().toString(),ed.getText().toString());
                popupwindow.dismiss();
            }
        });
    }
    /**
     *自动打开软键盘
     *@auther css
     *created at 2016/4/23 21:58
     */
    private void openKeyboard(Handler mHandler, int s) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, s);
    }
    private void  getMenuListDataForSearch(String sFolderName,String content)
    {
        scheduleList.clear();
        String sql;
        if (current_status.getText().equals("已完成"))
        {
            if (sFolderName.equals("今天"))
            {
                Calendar calendar = Calendar.getInstance();
                String remindTimeLableTemp = calendar.get(Calendar.YEAR)+"年"+
                        timeFormatString(calendar.get(Calendar.MONTH) + 1)+"月"+
                        timeFormatString(calendar.get(Calendar.DAY_OF_MONTH))+"日";
                sql = "select * from Schedule   where  remind_time_lable like'"+remindTimeLableTemp+"%'" +
                        "                       and user_id = '"+user.getObjectId()+"'" +
                        "                       and status = true" +
                        "                       and content like'%"+content+"%'" +
                        "                       order by priority desc";
                Log.d("smile","sql1"+sql);
            }
            else
            {
                sql = "select * from Schedule where schedule_folder_name = '"+sFolderName+"'" +
                        "                           and user_id = '"+user.getObjectId()+"'" +
                        "                           and status = true" +
                        "                           and content like'%"+content+"%'" +
                        "                           order by priority desc";
                Log.d("smile", "sql2" + sql);
            }
        }
        else
        {
            if (sFolderName.equals("今天"))
            {
                Calendar calendar = Calendar.getInstance();
                String remindTimeLableTemp = calendar.get(Calendar.YEAR)+"年"+
                        timeFormatString(calendar.get(Calendar.MONTH) + 1)+"月"+
                        timeFormatString(calendar.get(Calendar.DAY_OF_MONTH))+"日";
                sql = "select * from Schedule   where  remind_time_lable like'"+remindTimeLableTemp+"%'" +
                        "                       and user_id = '"+user.getObjectId()+"'" +
                        "                       and status = false" +
                        "                       and content like'%"+content+"%'" +
                        "                       order by priority desc";
                Log.d("smile","sql1"+sql);
            }
            else
            {
                sql = "select * from Schedule where schedule_folder_name = '"+sFolderName+"'" +
                        "                           and user_id = '"+user.getObjectId()+"'" +
                        "                           and status = false" +
                        "                           and content like'%"+content+"%'" +
                        "                           order by priority desc";
                Log.d("smile", "sql2" + sql);
            }
        }
        final ProgressDialog progress = new ProgressDialog(Main2Activity.this);
        progress.setMessage("正在查询中...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        new BmobQuery<Schedule>().doSQLQuery(getApplicationContext(), sql, new SQLQueryListener<Schedule>() {

            @Override
            public void done(BmobQueryResult<Schedule> result, BmobException e) {
                if (e == null) {
                    List<Schedule> list = result.getResults();
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            scheduleList.add(list.get(i));
                            Log.d("lili", scheduleList.toString());
                        }
                        Main2Activity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initSchedule();
                            }
                        });
                    } else {
                        Main2Activity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                initSchedule();
                            }
                        });
                        T.showDefind(Main2Activity.this, "没有您要找的数据，请重新查询。");
                    }
                } else {
                    T.showDefind(Main2Activity.this, "网络可能不好，请稍后查询。");
                }
                progress.dismiss();
            }
        });
    }


    @Override
    public void onCreateSupportNavigateUpTaskStack(TaskStackBuilder builder) {
        super.onCreateSupportNavigateUpTaskStack(builder);
    }

    /**
     *设置左侧菜单被点击时的事件
     *@auther css
     *created at 2016/4/21 9:58
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.nav_today_task:
                current_status.setText("未完成");
                setTitle(item.getTitle());
                getItemListData(getTitle().toString());
                break;

            case R.id.nav_collect_box:
                current_status.setText("未完成");
                setTitle(item.getTitle());
                getItemListData(getTitle().toString());
                break;

            case R.id.nav_add_sfolder_list:
                item.setCheckable(false);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("请输入清单名称");
                final LayoutInflater inflater = LayoutInflater.from(this);
                final View view = inflater.inflate(R.layout.dialog_schedule_folder,null);
                builder.setView(view);
                builder.setIcon(R.mipmap.s_folder_list);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText sFolder = (EditText) view.findViewById(R.id.et_edit_sFolder);
                        final String sFolderNameTemp = sFolder.getText().toString();
                        if (TextUtils.isEmpty(sFolderNameTemp))
                        {
                            T.showDefind(Main2Activity.this,"清单名称为空，新建清单失败。");
                            return;
                        }
                        String sql1 = "select * from ScheduleFolder  where schedule_folder_name = '"+sFolderNameTemp+"'" +
                                "                                   and user_id = '"+user.getObjectId()+"'";
                        new BmobQuery<ScheduleFolder>().doSQLQuery(Main2Activity.this, sql1, new SQLQueryListener<ScheduleFolder>() {
                            @Override
                            public void done(BmobQueryResult<ScheduleFolder> bmobQueryResult, BmobException e) {
                                if (e == null) {
                                    List<ScheduleFolder> list = bmobQueryResult.getResults();
                                    if (list != null && list.size() > 0) {
                                        T.showDefind(Main2Activity.this,"清单名称已存在，新建清单失败。");
                                        return;
                                    } else {
                                        final ScheduleFolder scheduleFolder = new ScheduleFolder();
                                        scheduleFolder.setSchedule_folder_name(sFolderNameTemp);
                                        scheduleFolder.setUser_id(user.getObjectId());
                                        scheduleFolder.save(Main2Activity.this, new SaveListener() {
                                            @Override
                                            public void onSuccess() {
                                                T.showDefind(Main2Activity.this, "添加成功！");
                                                map.put(sFolderNameTemp, scheduleFolder.getObjectId());
                                                setTitle(sFolderNameTemp);
                                                getItemListData(sFolderNameTemp);
                                                //TODO 刷新左侧列表
                                                refreshMuneList();
                                            }

                                            @Override
                                            public void onFailure(int i, String s) {
                                                T.showDefind(Main2Activity.this,"添加失败！");
                                            }
                                        });
                                    }
                                }
                                else {
                                    Log.i("smile", "错误码：" + e.getErrorCode() + "，错误描述：" + e.getMessage());
                                }
                            }
                        });
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                Dialog dialog = builder.create();
                dialog.show();
                break;

            case R.id.nav_edit_sfolder_list:
                item.setCheckable(false);
                final String currentFolder = getTitle().toString();
                if (currentFolder.equals("今天")||currentFolder.equals("收集箱"))
                {
                    T.showDefind(this,"系统默认清单不可删除，只支持删除用户新建清单");
                    return false;
                }
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setTitle("修改或者删除该清单");
                final LayoutInflater inflater1 = LayoutInflater.from(this);
                final View view1 = inflater1.inflate(R.layout.dialog_schedule_folder,null);
                builder1.setView(view1);
                final EditText sFolder = (EditText) view1.findViewById(R.id.et_edit_sFolder);
                sFolder.setText(currentFolder);
                sFolder.setSelection(currentFolder.length());//设置编辑框内容和光标位置

                builder1.setIcon(R.mipmap.s_folder_list);
                builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String sFolderNameTemp = sFolder.getText().toString();
                        final String sfObjectId = map.get(getTitle().toString()).toString();
                        if (TextUtils.isEmpty(sFolderNameTemp)) {
                            T.showDefind(Main2Activity.this, "清单名称为空！");
                        }
                        final ScheduleFolder scheduleFolder = new ScheduleFolder();
                        scheduleFolder.setSchedule_folder_name(sFolderNameTemp);
//                        scheduleFolder.setUser_id(user.getObjectId());
                        scheduleFolder.update(Main2Activity.this,sfObjectId, new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                T.showDefind(Main2Activity.this, "更新成功！");
                                setTitle(sFolderNameTemp);
                                map.put(sFolderNameTemp,sfObjectId);
                                //TODO 刷新左侧列表
                                refreshMuneList();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                T.showDefind(Main2Activity.this, "更新失败！");
                            }
                        });
                    }
                });
                builder1.setNeutralButton("删除", new DialogInterface.OnClickListener() {//设置删除按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ScheduleFolderDao.deleteScheduleFolderData(Main2Activity.this, map.get(getTitle().toString()).toString());
//                        scheduleFolderNameItemObjId.get(item.getItemId());
                        setTitle("今天");

                        //TODO 刷新左侧列表
                        refreshMuneList();
                        getItemListData(getTitle().toString());
                    }
                });
                builder1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                Dialog dialog1 = builder1.create();
                dialog1.show();
                break;
        }




        /**
         * scheduleFolderNameItemList数组中放了item的id标记，用于寻找item，
         * 以便执行item对应的操作
         */
        if (scheduleFolderNameItemList!=null)
        {
            for (int i = 0 ; i < scheduleFolderNameItemList.size();i++)
            {
                if (scheduleFolderNameItemList.get(i) == item.getItemId())
                {
                    current_status.setText("未完成");
                    setTitle(item.getTitle().toString());
                    getItemListData(getTitle().toString());
                    break;
                }
            }

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (current_status.getText().equals("未完成"))
        {
            menu.findItem(R.id.action_finished).setTitle("已完成");
        }
        return true;
    }

    /**
     *根据返回结果进行处理
     *@auther css
     *created at 2016/4/21 9:58
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case 0://新建待办事项成功的请求码
                if (resultCode == RESULT_OK)
                {
                    String result = data.getStringExtra("add_result");
                    if (result.equals("changed")||result.equals("yes"))
                    {
                        getItemListData(getTitle().toString());
//                        adapter.refreshAdapter();
                    }
                }
                break;
            case 1://点击头像进入个人信息设置界面的请求码
                if (resultCode == RESULT_OK)
                {
                    userInfoIsChangeed = data.getStringExtra("isChanged");
                    Log.d("aaa",userInfoIsChangeed+"11111");
                    if (userInfoIsChangeed.equals("Y"))
                    {
                        initDataFromLocal();
                        user = BmobUser.getCurrentUser(this,User.class);
                        //设置昵称和邮件信息
                        if (user.getNickName() != null )
                        {
                            nickShowLeft.setText(user.getNickName().toString());
                        }
                        if (user.getEmail() != null && user.getEmailVerified()==true)
                        {
                            emailShowLeft.setText(user.getEmail().toString());
                        }
                        else
                        {
                            emailShowLeft.setText("邮箱未验证！");
                        }
                    }
                }
                break;
        }
    }

    /**
     *实现接口中的方法，实现向左滑动删除，向右活动新增
     *@auther css
     *created at 2016/4/19 19:37
     */
    @Override
    public void removeItem(SlideCutListView.RemoveDirection direction, int position) {
        switch (direction) {
            case RIGHT:
                Intent intent = new Intent(Main2Activity.this,AddPendingActivity.class);
                intent.putExtra("sFolderName",getTitle().toString());
                intent.putExtra("type", "new");
                startActivityForResult(intent,0);
                break;
            case LEFT:
                adapter.remove(position);
                Toast.makeText(this, "删除成功  "+ position, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    /**
     *格式化日期显示格式
     *@auther css
     *created at 2016/4/21 10:12
     */
    public String timeFormatString(int value)
    {
        return value >= 10 ? value+"" : "0"+value;
    }

    // 双击两次Back键盘退出程序
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if (mBackKeyPressedTimes == 0) {
            T.showDefind(Main2Activity.this, "再按一次退出程序");
            mBackKeyPressedTimes = 1;
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        mBackKeyPressedTimes = 0;
                    }
                }
            }.start();
            return;
        } else {
            ActivityCollector.finishAll();
            finish();
        }
        super.onBackPressed();
    }
}
