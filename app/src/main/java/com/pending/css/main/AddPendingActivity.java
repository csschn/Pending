package com.pending.css.main;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.pending.css.bean.Schedule;
import com.pending.css.bean.User;
import com.pending.css.config.Constants;
import com.pending.css.login.R;
import com.pending.css.remind.InitAlarmService;
import com.pending.css.util.T;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by Administrator on 2016/4/11.
 */
public class AddPendingActivity extends FragmentActivity implements View.OnClickListener {
    private static int CHOOSE_DATE_IS_PASE = 0;//标记选择的时间是否过期
    private static int chooseDataIsCurrentYear = 1;//标记当前选择年份是否是当前年份，1是，0不是
    private SimpleDateFormat mFormatter = new SimpleDateFormat("MM月dd日,hh:mm");
    private SimpleDateFormat mFormatterForLaterYear = new SimpleDateFormat("yyyy年MM月dd日,hh:mm");
    private final int PIC_FROM_CAMERA = 1;
    private final int PIC_FROM＿LOCALPHOTO = 0;


    private String currentPageStatus;
    private User user;
    private Schedule scheduleData;
    private int isTimeChanged = 0;
    private int isRecordChanged = 0;
    private int isPhotoChanged = 0;
    private String flag = "";
    private String flag1 = "";

    boolean isLongClick = false;
    private int isRecordExixt = 0;

    private String path = null;
    private URecorder recorder;
    private UPlayer player;
    private String tempUrlForDelete;
    private String tempUrlForPhoto;

    //定义控件
    private LinearLayout ll_left;
    private LinearLayout ll_right;
    private ImageView iv_priority;
    private LinearLayout ll_set_time;
    private CheckBox cb_is_finish;
    private EditText ed_content;
    private TextView tv_set_time;
    private TextView tv_title;
    private Button btn_record;
    private Button btn_take_photo;
    private Button btn_photo;
    private Button btn_repeat;
    private ImageView user_photo_accessory;

    private String sFolderName ;
    private String dateTime;
    //Schedule表数据
    private long time;
    private int priority = 0;
    private String content;
    private Uri photeAccessoryUri ;//照片附件
    private String photoForAccessory;

    String[] SelectList = new String[]{"重新录制", "播放录音"};
    private String[] priorityItemsList = {"高优先级","中优先级","低优先级","无优先级"};

    private Calendar calendar = Calendar.getInstance();
    private int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
    private int currentMonth = calendar.get(Calendar.MONTH);
    private int currentYear = calendar.get(Calendar.YEAR);
    private int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
    private int currentMinute = calendar.get(Calendar.MINUTE);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pending);
        initView();
        //初始化Bmob sdk
        Bmob.initialize(this, Constants.BMOB_APP_KEY);
        user = BmobUser.getCurrentUser(AddPendingActivity.this, User.class);
        initData();
        initRecord();
    }
    private void initRecord()
    {
        path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/pending/record";
        File file = new File(path);
        if (!file.exists())
        {
            file.mkdir();
        }
//        File path += new File(file.getAbsolutePath(),"/ione.mp3");
        path = file.getAbsolutePath()+"/record.mp3";
        recorder = new URecorder(path);
        player = new UPlayer(path);
    }

    private void downloadFile(BmobFile file){
        //允许设置下载文件的存储路径，默认下载文件的目录为：context.getApplicationContext().getCacheDir()+"/bmob/"
        File saveFile = new File(Environment.getExternalStorageDirectory()+"/pending/record");
        if (!saveFile.exists())
                saveFile.mkdir();
        File saveFile1 = new File(saveFile.getAbsolutePath(), file.getFilename());
        if (saveFile1.exists())
        {
            saveFile1.delete();
        }
        try {
            saveFile1.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        file.download(getApplicationContext(), saveFile1, new DownloadFileListener() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(String savePath) {

            }

            @Override
            public void onProgress(Integer value, long newworkSpeed) {

            }

            @Override
            public void onFailure(int code, String msg) {
            }
        });
    }
    private void downloadRecord(String recordPath)
    {
        BmobFile bmobFile = new BmobFile("record.MP3","",recordPath);
        downloadFile(bmobFile);
    }


    private void initData()
    {
        Intent intent = getIntent();
        sFolderName = intent.getStringExtra("sFolderName");
        currentPageStatus = intent.getStringExtra("type");
        if (currentPageStatus.equals("new"))
        {
            cb_is_finish.setEnabled(false);
            btn_record.setOnTouchListener((View.OnTouchListener) new MyClickListener());
            btn_record.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (isRecordExixt == 0) {
                        recorder.start();
                        AddPendingActivity.this.flag = "talk";
                        btn_record.setBackgroundColor(Color.parseColor("#419DE4"));
                        btn_record.setText("限时30秒");
                        isLongClick = true;
                        return true;
                    } else {
                        T.showDefind(AddPendingActivity.this, "录音已存在！");
                        return true;
                    }
                }
            });
        }
        else
        {
            scheduleData = (Schedule) intent.getSerializableExtra("schedule_data");
            String sql =    "select * from Schedule where user_id = '"+user.getObjectId()+"'" +
                    "                               and schedule_folder_name = '"+scheduleData.getSchedule_folder_name()+"'" +
                    "                               and objectId = '"+scheduleData.getObjectId()+"'";
            new BmobQuery<Schedule>().doSQLQuery(this, sql, new SQLQueryListener<Schedule>() {
                @Override
                public void done(BmobQueryResult<Schedule> bmobQueryResult, BmobException e) {
                    if (e == null) {
                        List<Schedule> list = (List<Schedule>) bmobQueryResult.getResults();
                        if (list != null && list.size() > 0) {
                            for (int i = 0; i < list.size(); i++) {
                                if (!TextUtils.isEmpty(list.get(i).getRecord_url()))
                                {
                                    tempUrlForDelete = list.get(i).getRecord_url();
                                    btn_record.setText("播放 重录");
                                    downloadRecord(tempUrlForDelete);
                                }
                                if (!TextUtils.isEmpty(list.get(i).getPicture_url()))
                                {
                                    tempUrlForPhoto  = list.get(i).getPicture_url();
                                    btn_take_photo.setText("修改照片");
                                    String path = Environment.getExternalStorageDirectory()+"/Pending";
                                    String pictureName = "photo.jpeg";
                                    DisplayImageOptions options = new DisplayImageOptions.Builder()
                                            .showImageOnLoading(R.mipmap.loading)
                                            .showImageOnFail(R.mipmap.error)
                                            .cacheInMemory(true)
                                            .cacheOnDisk(true)
                                            .bitmapConfig(Bitmap.Config.RGB_565)
                                            .build();
                                    ImageLoader.getInstance().displayImage(list.get(i).getPicture_url()
                                            ,user_photo_accessory
                                            ,new SimpleImageLoadingListener());
//                                    ImageLoadHelper imageLoadHelper = new ImageLoadHelper(user_photo_accessory,path,"photos",pictureName);
//                                    imageLoadHelper.downloadFromNetToLocal(list.get(i).getPicture_url());
                                }
                            }
                        } else {
                            Log.i("sean", "1");
                            Log.i("smile", "查询成功，无数据返回");
                            return;
                        }
                    } else {
                        Log.i("sean", "1");
                        Log.i("smile", "错误码：" + e.getErrorCode() + "，错误描述：" + e.getMessage());
                    }
                }
            });

            btn_record.setOnTouchListener((View.OnTouchListener) new MyClickListener());
            btn_record.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (isRecordExixt == 0)
                    {
                        recorder.start();
                        AddPendingActivity.this.flag = "talk";
                        btn_record.setBackgroundColor(Color.parseColor("#419DE4"));
                        isLongClick = true;
                        return true;
                    }
                    else
                    {
                        T.showDefind(AddPendingActivity.this,"录音已存在！");
                        return true;
                    }
                }
            });


            if (scheduleData.getIs_current_year() == 1)
            {
                tv_set_time.setText(scheduleData.getRemind_time_lable().substring(5));
            }
            else
            {
                tv_set_time.setText(scheduleData.getRemind_time_lable());
            }
            if(scheduleData.getStatus() == true)
                cb_is_finish.setChecked(true);
            switch (scheduleData.getPriority())
            {
                case 0:
                    cb_is_finish.setButtonDrawable(R.drawable.checkbox_for_addpending_0);
                    break;
                case 1:
                    cb_is_finish.setButtonDrawable(R.drawable.checkbox_for_addpending_1);
                    break;
                case 2:
                    cb_is_finish.setButtonDrawable(R.drawable.checkbox_for_addpending_2);
                    break;
                case 3:
                    cb_is_finish.setButtonDrawable(R.drawable.checkbox_for_addpending_3);
                    break;
            }
            tv_title.setText("修改待办事项");
            priority = scheduleData.getPriority();
            ed_content.setText(scheduleData.getContent());
            ed_content.setSelection(scheduleData.getContent().length());
        }
    }


    private void editPending()
    {

        Schedule schedule = new Schedule();
        schedule = scheduleData;
        if (isTimeChanged == 1)
        {
            schedule.setRemind_time_id(time);
            schedule.setRemind_time(time+"");
            schedule.setRemind_time_lable(time);
            schedule.setIs_current_year(chooseDataIsCurrentYear);
        }
        if (isRecordChanged == 1)
        {
            schedule.setRecord_url(tempUrlForDelete);
        }
        if (isPhotoChanged == 1)
        {
            schedule.setPicture_url(photoForAccessory);
        }

        schedule.setStatus(cb_is_finish.isChecked());
        if (TextUtils.isEmpty(ed_content.getText().toString().trim()))
        {
            T.showShort(AddPendingActivity.this,"你还没有添加提醒内容！");
            return;
        }
        if (CHOOSE_DATE_IS_PASE == 1 )
        {
            T.showShort(AddPendingActivity.this,"你设置的时间已经过期，请重新选择！");
            return;
        }
        content = ed_content.getText().toString();
        schedule.setContent(content);
        schedule.setPriority(priority);

        schedule.setSchedule_folder_name(schedule.getSchedule_folder_name());
        schedule.setUser_id(user.getObjectId());
        schedule.update(this, schedule.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent();
                intent.putExtra("add_result","changed");
                setResult(RESULT_OK,intent);
                T.showDefind(getApplicationContext(),"已成功更新待办事项");
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                Log.d("update", i + "---------" + s);
                T.showDefind(getApplicationContext(), "更新失败");
            }
        });
    }

    /**
     *用于添加新的代办事项，
     *@auther css
     *created at 2016/4/21 8:53
     */
    private void addPending()
    {

        Schedule schedule = new Schedule(time);
        schedule.setPicture_url(photoForAccessory);
        if (TextUtils.isEmpty(ed_content.getText().toString().trim()))
        {
            T.showShort(AddPendingActivity.this,"你还没有添加提醒内容！");
            return;
        }
        if (CHOOSE_DATE_IS_PASE == 1 )
        {
            T.showShort(AddPendingActivity.this,"你设置的时间已经过期，请重新选择！");
            return;
        }
        if (!TextUtils.isEmpty(photoForAccessory))
        {
            schedule.setPicture_url(photoForAccessory);
        }
        content = ed_content.getText().toString();
        schedule.setContent(content);
        schedule.setRecord_url(tempUrlForDelete);
        schedule.setPriority(priority);
        schedule.setIs_current_year(chooseDataIsCurrentYear);
        if (sFolderName.equals("今天"))
        {
            sFolderName = "收集箱";
        }
        schedule.setSchedule_folder_name(sFolderName);
        schedule.setUser_id(user.getObjectId());
        schedule.save(AddPendingActivity.this, new SaveListener() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent();
                intent.putExtra("add_result", "yes");
                setResult(RESULT_OK, intent);
                T.showDefind(getApplicationContext(), "已成功添加待办事项");
                startService(new Intent(AddPendingActivity.this, InitAlarmService.class));
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                T.showLong(AddPendingActivity.this, "添加失败！");
            }
        });


    }
    @Override
    public void onClick(final View v) {
        switch (v.getId())
        {
            case R.id.ll_left:
                    finish();
                break;

            case R.id.ll_right:
                if (currentPageStatus.equals("new"))
                {
                    addPending();
                    startService(new Intent(AddPendingActivity.this,InitAlarmService.class));
                }
                else
                {
                    editPending();
                    startService(new Intent(AddPendingActivity.this, InitAlarmService.class));
                }

                break;

            case R.id.ll_set_time:

            break;

            case R.id.iv_priority:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("选择优先级");
                builder.setIcon(R.mipmap.priority_pressed);//设置图标
                //设置点击事件
                builder.setItems(priorityItemsList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which)
                        {
                            case 0:
                                priority = 3;
                                cb_is_finish.setButtonDrawable(R.drawable.checkbox_for_addpending_3);
                                break;
                            case 1:
                                priority = 2;
                                cb_is_finish.setButtonDrawable(R.drawable.checkbox_for_addpending_2);
                                break;
                            case 2:
                                priority = 1;
                                cb_is_finish.setButtonDrawable(R.drawable.checkbox_for_addpending_1);
                                break;
                            case 3:
                                priority = 0;
                                cb_is_finish.setButtonDrawable(R.drawable.checkbox_for_addpending_0);
                                break;
                        }
                        Toast.makeText(AddPendingActivity.this, "您选的优先级是：" + priorityItemsList[which], Toast.LENGTH_SHORT).show();

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;

            case R.id.cb_is_finish:

                break;

            case R.id.ed_content:

                break;

            case R.id.tv_set_time:
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
//                                .setMinDate(minDate)
//                                .setMaxDate(maxDate)
                        .setIs24HourTime(true)
//                        .setTheme(SlideDateTimePicker.HOLO_DARK)
//                        .setIndicatorColor(Color.parseColor("#990000"))
                        .build()
                        .show();
                break;

            case R.id.btn_record:

                if (btn_record.getText().toString().equals("播放 重录"))
                {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(AddPendingActivity.this);
                    builder1.setTitle("选择操作");
                    builder1.setItems(SelectList, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (SelectList[which].equals("重新录制")) {
                                isRecordExixt = 0;
                                btn_record.setText("按住 说话");
                                initRecord();
                            } else {
                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                                File audioFile = new File(path);//此处需要在sd卡中放置一个文件名为good的mp3文件。
                                intent.setDataAndType(Uri.fromFile(audioFile), "audio/mp3");
                                startActivity(intent);
                            }
                        }
                    });
                    builder1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog dialog1 = builder1.create();
                    dialog1.show();
                }
                break;

            case R.id.btn_take_photo:
                avaterEvent();

                break;

            case R.id.btn_photo:

                break;

            case R.id.btn_repeat:
//                final String[] repeatItemList =  {"每天","每月","每年"};
//                String time;
//                String dateForMonth;
//                String dateForYear;
//                String temp = tv_set_time.getText().toString();
//                if (TextUtils.isEmpty(temp))
//                {
//                    T.showShort(getApplicationContext(),"您还没设置提醒时间！");
//                    return;
//                }
//                if (CHOOSE_DATE_IS_PASE == 1)
//                {
//                    T.showShort(getApplicationContext(),"您设置的时间已经到期，请重新选择！");
//                    return;
//                }
//                if (chooseDataIsCurrentYear == 1)
//                {
//                    time = temp.substring(7);
//                    dateForMonth = temp.substring(3,6);
//                    dateForYear = temp.substring(0,6);
//                }
//                else
//                {
//                    time = temp.substring(12);
//                    dateForMonth = temp.substring(8,11);
//                    dateForYear = temp.substring(5,11);
//                }
//                repeatItemList[0] = repeatItemList[0] +"("+time+")";
//                repeatItemList[1] = repeatItemList[1] +"("+dateForMonth+")";
//                repeatItemList[2] = repeatItemList[2] +"("+dateForYear+")";
//                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
//                builder1.setTitle("请选择重复类型");
//                builder1.setItems(repeatItemList, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//
//
//                        switch (which)
//                        {
//                            case 0:
//
//                                T.showDefind(getApplicationContext(),"      设置了 "+repeatItemList[0]+" 重复        ");
//                                break;
//
//                            case 1:
//                                T.showDefind(getApplicationContext(),"      设置了 "+repeatItemList[1]+" 重复        ");
//                                break;
//
//                            case 2:
//                                T.showDefind(getApplicationContext(),"      设置了 "+repeatItemList[2]+" 重复        ");
//                                break;
//                        }
//                    }
//                }).create().show();
//                break;
                case R.id.user_photo_accessory:
//                    T.showDefind(AddPendingActivity.this, "click the picture");
//                    Intent intent = new Intent(AddPendingActivity.this,ShowImageActivity.class);
//                    intent.setData(getFile());
//                    startActivity(intent);
                    break;
        }
    }



    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date)
        {
            Calendar cal=Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND,0);
            int chooseDay = cal.get(Calendar.DAY_OF_MONTH);
            int  chooseMonth = cal.get(Calendar.MONTH);
            int chooseYear = cal.get(Calendar.YEAR);
            int chooseHour = cal.get(Calendar.HOUR_OF_DAY);
            int chooseMinute = cal.get(Calendar.MINUTE);
            int result = cal.compareTo(calendar);

            if(result < 0)
            {
                T.showLong(AddPendingActivity.this,"选择的时间小于当前时间，请重新选择！");
                CHOOSE_DATE_IS_PASE = 1;
                chooseDataIsCurrentYear = 0;
                tv_set_time.setText(mFormatterForLaterYear.format(date));
                tv_set_time.setTextColor(getResources().getColor(R.color.text_show_red));
            }
            else
            {
                isTimeChanged = 1;
                time = cal.getTimeInMillis();
                if (chooseYear > currentYear)
                {
                    chooseDataIsCurrentYear = 0;
                    CHOOSE_DATE_IS_PASE = 0;
                    T.showLong(AddPendingActivity.this, chooseYear+" ---  "+currentYear);
                    dateTime = mFormatterForLaterYear.format(date);
                    tv_set_time.setText(mFormatterForLaterYear.format(date));
                    tv_set_time.setTextColor(getResources().getColor(R.color.text_show_blue));
                }
                else
                {
                    chooseDataIsCurrentYear = 1;
                    CHOOSE_DATE_IS_PASE = 0;
                    dateTime = mFormatter.format(date);
                    tv_set_time.setText(mFormatter.format(date));
                    tv_set_time.setTextColor(getResources().getColor(R.color.text_show_blue));
                }
            }

        }

        // Optional cancel listener
        @Override
        public void onDateTimeCancel()
        {
            return ;
        }
    };

    /**
     *初始化控件
     */
    private void initView()
    {
        ll_left = (LinearLayout) findViewById(R.id.ll_left);
        ll_right = (LinearLayout) findViewById(R.id.ll_right);
        iv_priority = (ImageView) findViewById(R.id.iv_priority);
        ll_set_time = (LinearLayout) findViewById(R.id.ll_set_time);
        cb_is_finish = (CheckBox) findViewById(R.id.cb_is_finish);
        ed_content = (EditText) findViewById(R.id.ed_content);
        tv_set_time = (TextView) findViewById(R.id.tv_set_time);
        tv_title = (TextView) findViewById(R.id.tv_title);
        btn_record = (Button) findViewById(R.id.btn_record);
        btn_take_photo = (Button) findViewById(R.id.btn_take_photo);
        btn_photo = (Button) findViewById(R.id.btn_photo);
        btn_repeat= (Button) findViewById(R.id.btn_repeat);
        user_photo_accessory = (ImageView) findViewById(R.id.user_photo_accessory);

        ll_left.setOnClickListener(this);
        ll_right.setOnClickListener(this);
        iv_priority.setOnClickListener(this);
        ll_set_time.setOnClickListener(this);
        cb_is_finish.setOnClickListener(this);
        ed_content.setOnClickListener(this);
        tv_set_time.setOnClickListener(this);
        btn_record.setOnClickListener(this);
        btn_take_photo.setOnClickListener(this);
        btn_photo.setOnClickListener(this);
        btn_repeat.setOnClickListener(this);
        tv_title.setOnClickListener(this);
        user_photo_accessory.setOnClickListener(this);
    }

    class MyClickListener implements View.OnTouchListener {
        public boolean onTouch(View v, MotionEvent event) {
            if (isLongClick)
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        flag = "listen";
                        btn_record.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        recorder.stop();
                        btn_record.setText("播放 重录");
                        isRecordExixt = 1;
                        isLongClick = false;
                        uploadRecord(path);
                        break;
                    default:
                        break;
                }
            return false;
        }
    }

    private void uploadRecord(String path)
    {
        final BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.upload(this, new UploadFileListener() {
            @Override
            public void onSuccess() {
//                Toast.makeText(AddPendingActivity.this, "上传成功！", Toast.LENGTH_SHORT).show();
                tempUrlForDelete = bmobFile.getUrl().toString();
                isRecordChanged = 1;
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(AddPendingActivity.this, "上传失败！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void avaterEvent()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(AddPendingActivity.this);
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.picture_selected_defind, null);
        builder.setIcon(R.drawable.icon1);
        builder.setTitle("请选择方式");
        builder.setView(textEntryView);
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        builder.create();
        final AlertDialog dialog = builder.show();
        LinearLayout picture_local = (LinearLayout) textEntryView.findViewById(R.id.picture_local);
        LinearLayout picture_camera = (LinearLayout) textEntryView.findViewById(R.id.picture_camera);
        picture_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doHandlerPhoto(PIC_FROM_CAMERA);// 用户点击了从照相机获取
                dialog.dismiss();
            }
        });

        picture_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doHandlerPhoto(PIC_FROM＿LOCALPHOTO);// 从相册中去获取
                dialog.dismiss();
            }
        });
    }

    private Uri getFile()
    {
        //保存裁剪后的图片文件
        File pictureFileDir = new File(Environment.getExternalStorageDirectory(), "/Pending/photos");
        if (!pictureFileDir.exists()) {
            pictureFileDir.mkdirs();
        }
        File picFile = new File(pictureFileDir, "photo.jpeg");
        if (!picFile.exists()) {
            try {
                picFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Uri photeAccessoryUri = Uri.fromFile(picFile);

        return photeAccessoryUri;
    }

    private void doHandlerPhoto(int type) {
        photeAccessoryUri = getFile();
        if (type == PIC_FROM＿LOCALPHOTO) {
            Intent intent = getCropImageIntent();
            startActivityForResult(intent, PIC_FROM＿LOCALPHOTO);
        } else {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photeAccessoryUri);
            startActivityForResult(cameraIntent, PIC_FROM_CAMERA);
        }

    }

    public Intent getCropImageIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        setIntentParams(intent);
        return intent;
    }

    private void setIntentParams(Intent intent) {
        WindowManager wm = this.getWindowManager();
        intent.putExtra("crop", "false");
        //注释掉之后可以任意大小的选取图片
//        intent.putExtra("aspectX", wm.getDefaultDisplay().getWidth());
//        intent.putExtra("aspectY", wm.getDefaultDisplay().getHeight());
//        intent.putExtra("outputX", wm.getDefaultDisplay().getWidth());
//        intent.putExtra("outputY", wm.getDefaultDisplay().getHeight());
        intent.putExtra("noFaceDetection", true); // no face detection
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photeAccessoryUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PIC_FROM_CAMERA: // 拍照
                try {
                    cropImageUriByTakePhoto();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case PIC_FROM＿LOCALPHOTO:
                try {
                    if (photeAccessoryUri != null) {
                        final String path = photeAccessoryUri.toString().substring(8);
                        uploadPhoto(path);//根据本地的路径上传图片
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
    /**
     * 启动裁剪
     */
    private void cropImageUriByTakePhoto() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photeAccessoryUri, "image/*");
        setIntentParams(intent);
        startActivityForResult(intent, PIC_FROM＿LOCALPHOTO);
    }

    private void uploadPhoto(String path)
    {
        final BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.upload(this, new UploadFileListener() {
            @Override
            public void onSuccess() {
                photoForAccessory = bmobFile.getUrl().toString();
                isPhotoChanged = 1 ;
                user_photo_accessory.setImageBitmap(decodeUriAsBitmap(photeAccessoryUri));
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(AddPendingActivity.this, "设置失败！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }
}
