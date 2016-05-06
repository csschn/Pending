package com.pending.css.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pending.css.bean.User;
import com.pending.css.config.Constants;
import com.pending.css.login.LoginActivity;
import com.pending.css.login.R;
import com.pending.css.login.ResetPasswordActivity;
import com.pending.css.login.UserBindPhoneActivity;
import com.pending.css.util.ActivityCollector;
import com.pending.css.util.BaseActivity;
import com.pending.css.util.ImageLoadHelper;

import java.io.File;
import java.io.FileNotFoundException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by css on 2016/3/22.
 */
public class UserInfoActivity extends BaseActivity {
    private final int PIC_FROM_CAMERA = 1;
    private final int PIC_FROM＿LOCALPHOTO = 0;
    private User user;
    private Uri photoUri;
    private String tempUrlForDelete;
    private String isChangedImage = "N";
    @Bind(R.id.user_nick_show)
    TextView userNickShow;
    @Bind(R.id.user_nick_text)
    TextView userNickText;
    @Bind(R.id.user_email_text)
    TextView userEmailText;
    @Bind(R.id.iv_left)
    ImageView ivLeft;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.bind_phone_text)
    TextView bindPhoneText;
    @Bind(R.id.bind_phone)
    LinearLayout bindPhone;
    @Bind(R.id.user_nick)
    LinearLayout userNick;
    @Bind(R.id.user_email)
    LinearLayout userEmail;
    @Bind(R.id.change_password)
    LinearLayout changePassword;
    @Bind(R.id.exit_login)
    LinearLayout exitLogin;
    private TextView nickShow;
    private TextView emailShow;
    private View changeUserInfoView;
    private LinearLayout liearNick;
    private LinearLayout liearEmail;
    private ImageView rightLoginHead2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        Bmob.initialize(this, Constants.BMOB_APP_KEY);
        ButterKnife.bind(this);
        isChangedImage = "N";
        ivLeft.setVisibility(View.VISIBLE);
        tvTitle.setText("个人资料");
        user = BmobUser.getCurrentUser(UserInfoActivity.this, User.class);
        initView();
        getPicture();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        isShowBindPhone();
    }

    /**
     * 初始化控件
     */
    public void initView()
    {
        nickShow = (TextView) findViewById(R.id.nick_show);
        emailShow = (TextView) findViewById(R.id.email_show);
        bindPhone = (LinearLayout) findViewById(R.id.bind_phone);
        rightLoginHead2 = (ImageView) findViewById(R.id.right_login_head2);
    }


    /**
     *设置头像的显示方式,如果头像加载过，就从本地读取；否则读取默认图片
     *@auther css
     *created at 2016/4/22 13:11
     */
    private void getPicture()
    {
        Intent intent = getIntent();
        String isLoadAvater = intent.getStringExtra("isLoadAvater");
        if (isLoadAvater.equals("Y"))
        {
            String path = Environment.getExternalStorageDirectory() + "/Pending/images/Avatar.jpeg";
            try {
                Bitmap bitmap = ImageLoadHelper.decodeBitmap(path, 200, 200);
                rightLoginHead2.setImageBitmap(bitmap);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            rightLoginHead2.setImageResource(R.drawable.people_bg_default);
        }
    }

    /**
     * 控件的点击事件
     */
    @OnClick({R.id.right_login_head2, R.id.user_nick, R.id.user_email, R.id.change_password, R.id.exit_login,R.id.iv_left, R.id.bind_phone})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.right_login_head2:
                avaterEvent();
                break;
            case R.id.user_nick:
                nickDialog();
                break;
            case R.id.user_email:
                emailDialog();
                break;
            case R.id.change_password:
                startActivity(new Intent(this, ResetPasswordActivity.class));
                break;
            case R.id.exit_login:
                ActivityCollector.finishAll();
                this.finish();
                BmobUser.logOut(UserInfoActivity.this);   //清除缓存用户对象
                BmobUser currentUser = BmobUser.getCurrentUser(UserInfoActivity.this); // 现在的currentUser是null了
                Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("flag",1);
                startActivity(intent);
                break;
            case R.id.iv_left:
                Intent intent1 = new Intent();
                intent1.putExtra("isChanged",isChangedImage);
                setResult(RESULT_OK,intent1);
                finish();
                break;
            case R.id.bind_phone:
                startActivity(new Intent(this, UserBindPhoneActivity.class));
                break;
        }
    }


    /**
     * 初始化数据
     */
    private void initData() {
        if (user.getNickName() != null )//昵称存在
        {
            nickShow.setText(user.getNickName().toString());
            userNickShow.setText(user.getNickName().toString());
        }
        if (user.getEmail() != null  )//邮箱存在，不同的状态
        {
            if (user.getEmailVerified()==true)
            {
                emailShow.setText(user.getEmail().toString());
            }
            else
            {
                emailShow.setText(user.getEmail().toString() + "(未验证)");
            }
        }
    }

    /**
     *判断当前用户是否绑定了手机
     *@auther css
     *created at 2016/4/22 13:13
     */
    public void isShowBindPhone()
    {
        if (user != null && user.getMobilePhoneNumberVerified() != null && user.getMobilePhoneNumberVerified()) {
            bindPhone.setVisibility(View.GONE);
            changePassword.setVisibility(View.VISIBLE);
        } else {
            bindPhone.setVisibility(View.VISIBLE);
            changePassword.setVisibility(View.GONE);
        }
    }

    /**
     *点击头像事件
     *@auther css
     *created at 2016/4/22 13:18
     */
    private void avaterEvent()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoActivity.this);
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
    /**
     *点击昵称事件
     *@auther css
     *created at 2016/4/22 13:19
     */
    private void nickDialog()
    {
        LayoutInflater factory1 = LayoutInflater.from(this);
        changeUserInfoView = factory1.inflate(R.layout.activity_change_userinfo, null);
        liearEmail = (LinearLayout) changeUserInfoView.findViewById(R.id.linear_email);
        liearEmail.setVisibility(View.GONE);
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(UserInfoActivity.this);
        builder1.setIcon(R.drawable.icon1);
        builder1.setTitle("请输入昵称：");
        builder1.setView(changeUserInfoView);
        builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog1, int whichButton) {

                EditText nickEdit = (EditText) changeUserInfoView.findViewById(R.id.nick_edit);
                String nickName = nickEdit.getText().toString();
                if (TextUtils.isEmpty(nickName))
                    return;
                user.setNickName(nickName);
                user.update(UserInfoActivity.this,user.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(UserInfoActivity.this, "昵称修改成功！", Toast.LENGTH_SHORT).show();
                        isChangedImage = "Y";
                        nickShow.setText(user.getNickName().toString());
                        userNickShow.setText(user.getNickName().toString());
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Toast.makeText(UserInfoActivity.this, "失败——昵称", Toast.LENGTH_SHORT).show();
                        return;
                    }
                });

            }
        });
        builder1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog1, int whichButton) {
                return;
            }
        });
        builder1.create().show();
    }
    /**
     *点击邮箱事件
     *@auther css
     *created at 2016/4/22 13:19
     */
    private void emailDialog()
    {
        final AlertDialog.Builder builder2 = new AlertDialog.Builder(UserInfoActivity.this);
        LayoutInflater factory2 = LayoutInflater.from(this);
        changeUserInfoView = factory2.inflate(R.layout.activity_change_userinfo, null);
        liearNick = (LinearLayout) changeUserInfoView.findViewById(R.id.linear_nick);
        liearNick.setVisibility(View.GONE);
        builder2.setIcon(R.drawable.icon1);
        builder2.setTitle("请输入邮箱：");
        builder2.setView(changeUserInfoView);
        builder2.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog1, int whichButton) {
                EditText emailEdit = (EditText) changeUserInfoView.findViewById(R.id.email_edit);
                final String emailEditText = emailEdit.getText().toString();
                if (TextUtils.isEmpty(emailEditText))
                    return;
                user.setEmail(emailEditText);
                user.update(UserInfoActivity.this, user.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        emailShow.setText(emailEditText + "(未验证)");
                        isChangedImage = "Y";
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });
            }
        });
        builder2.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog1, int whichButton) {
                return;
            }
        });
        builder2.create().show();
    }

    /**
     *重写返回键点击事件
     *@auther css
     *created at 2016/4/22 13:19
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("isChanged", isChangedImage);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
    /**
     * @param requestCode：请求码
     * @param resultCode：返回码
     * @param data：返回数据
     */
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
                    if (photoUri != null) {
                        final String path = photoUri.toString().substring(8);
                        uploadAvater(path);//根据本地的路径上传图片
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    /**
     * 根据不同方式选择图片设置ImageView
     *
     * @param type 0-本地相册选择，非0为拍照
     */
    private void doHandlerPhoto(int type) {
        try {
            //保存裁剪后的图片文件
            File pictureFileDir = new File(Environment.getExternalStorageDirectory(), "/Pending/images");
            if (!pictureFileDir.exists()) {
                pictureFileDir.mkdirs();
            }
            File picFile = new File(pictureFileDir, "Avatar.jpeg");
            if (!picFile.exists()) {
                picFile.createNewFile();
            }
            photoUri = Uri.fromFile(picFile);
            if (type == PIC_FROM＿LOCALPHOTO) {
                Intent intent = getCropImageIntent();
                startActivityForResult(intent, PIC_FROM＿LOCALPHOTO);
            } else {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(cameraIntent, PIC_FROM_CAMERA);
            }
        } catch (Exception e) {
            Log.i("HandlerPicError", "处理图片出现错误");
        }
    }


    /**
     * 调用图片剪辑程序
     */
    public Intent getCropImageIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        setIntentParams(intent);
        return intent;
    }

    /**
     * 启动裁剪
     */
    private void cropImageUriByTakePhoto() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");
        setIntentParams(intent);
        startActivityForResult(intent, PIC_FROM＿LOCALPHOTO);
    }

    /**
     * 设置公用参数
     */
    private void setIntentParams(Intent intent) {
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 600);
        intent.putExtra("outputY", 600);
        intent.putExtra("noFaceDetection", true); // no face detection
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
    }




    /**
     * @param uri：图片的本地url地址
     * @return Bitmap；
     */
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

    /**
     * 上传图片到Bmob
     * @param path:图片的物理路径
     */
    private void uploadAvater(String path)
    {
        final BmobFile bmobFile = new BmobFile(new File(path));
        tempUrlForDelete = user.getPictureUrl();
        bmobFile.upload(this, new UploadFileListener() {
            @Override
            public void onSuccess() {
                user.setPictureUrl( bmobFile.getUrl().toString());
                user.update(UserInfoActivity.this, user.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        Bitmap bitmap = decodeUriAsBitmap(photoUri);
                        rightLoginHead2.setImageBitmap(bitmap);
                        isChangedImage = "Y";
                        if (TextUtils.isEmpty(tempUrlForDelete))
                        {
                            Toast.makeText(UserInfoActivity.this, "更新成功！1", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else
                        {
                            BmobFile file = new BmobFile();
//                            tempUrlForDelete = tempUrlForDelete.substring(19);
                            file.setUrl(tempUrlForDelete);
                            file.delete(UserInfoActivity.this, new DeleteListener() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(UserInfoActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    Toast.makeText(UserInfoActivity.this, "文件删除失败：" + i + ",msg = " + s, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            });
                        }


                    }
                    @Override
                    public void onFailure(int i, String s) {
                        Toast.makeText(UserInfoActivity.this, "上传失败！222", Toast.LENGTH_SHORT).show();
                        return;
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(UserInfoActivity.this, "设置失败！", Toast.LENGTH_SHORT).show();
            }
        });
    }



}

