package com.pending.css.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.pending.css.bean.User;
import com.pending.css.config.Constants;
import com.pending.css.index.Main2Activity;
import com.pending.css.util.ActivityCollector;
import com.pending.css.util.BaseActivity;
import com.pending.css.util.T;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;


/**
 * 登录
 *
 * @author css
 * @class LoginActivity
 * @date 2016-3-19 上午11:16:04
 */
public class LoginActivity extends BaseActivity {


    private int mBackKeyPressedTimes = 0;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    @Bind(R.id.iv_left)
    ImageView iv_left;
    @Bind(R.id.et_account)
    EditText et_account;
    @Bind(R.id.et_password)
    EditText et_password;
    @Bind(R.id.btn_login)
    Button btn_login;
    @Bind(R.id.btn_onekey)
    Button btn_onekey;
    @Bind(R.id.btn_register)
    TextView btnRegister;
    @Bind(R.id.remember)
    CheckBox remember;
    @Bind(R.id.autologin)
    CheckBox autologin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        //初始化Bmob sdk
        Bmob.initialize(this, Constants.BMOB_APP_KEY);

        sp = getSharedPreferences("userInfo", 0);
        String name=sp.getString("USER_NAME", "");
        String pass =sp.getString("PASSWORD", "");

        boolean choseRemember =sp.getBoolean("remember", false);
        boolean choseAutoLogin =sp.getBoolean("autologin", false);
        //      Toast.makeText(this, name, Toast.LENGTH_SHORT).show();



        //如果上次选了记住密码，那进入登录页面也自动勾选记住密码，并填上用户名和密码
        if(choseRemember){
            et_account.setText(name);
            et_password.setText(pass);
            remember.setChecked(true);
        }
        //如果上次登录选了自动登录，那进入登录页面也自动勾选自动登录
        if(choseAutoLogin){
            autologin.setChecked(true);
            Intent getIntent = getIntent();
            int tag = getIntent.getIntExtra("flag",0);
            if (tag != 1)
            {
                String username = sp.getString("USER_NAME","1");
                String password = sp.getString("PASSWORD","1");
                autoLogin(username, password);
            }

        }

    }


    public void autoLogin(String username,String password)
    {
        final ProgressDialog progress = new ProgressDialog(LoginActivity.this);
        progress.setMessage("正在登录中...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        BmobUser.loginByAccount(this, username, password, new LogInListener<User>() {
            @Override
            public void done(User user, BmobException ex) {
                // TODO Auto-generated method stub
                if (ex == null) {
                    Intent intent = new Intent(LoginActivity.this, Main2Activity.class);
                    intent.putExtra("from", "login");
                    startActivity(intent);
                    progress.dismiss();
                } else {
                    progress.dismiss();
                    toast("登录失败：code=" + ex.getErrorCode() + "，错误描述：" + ex.getLocalizedMessage());
                }
            }
        });
    }

    @OnClick({R.id.remember, R.id.autologin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.remember:
                break;
            case R.id.autologin:
                if (autologin.isChecked())
                    remember.setChecked(true);
                break;
        }
    }

    @OnClick(R.id.iv_left)
    public void back() {
        finish();
    }

    @OnClick(R.id.btn_login)
    public void login(View view) {
        login();
    }

    @OnClick(R.id.btn_register)
    public void onClick() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }


    @OnClick(R.id.btn_onekey)
    public void oneKey() {
        Intent intent = new Intent(LoginActivity.this, LoginOneKeyActivity.class);
        startActivity(intent);
    }

    /**
     * 登陆操作
     *
     * @return void
     * @throws
     * @method login
     */
    private void login() {
        final String account = et_account.getText().toString();
        final String password = et_password.getText().toString();
        if (TextUtils.isEmpty(account)) {
            showToast("账号不能为空");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showToast("密码不能为空");
            return;
        }

        final ProgressDialog progress = new ProgressDialog(LoginActivity.this);
        progress.setMessage("正在登录中...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        //V3.3.9提供的新的登录方式，可传用户名/邮箱/手机号码
        BmobUser.loginByAccount(this, account, password, new LogInListener<User>() {

            @Override
            public void done(User user, BmobException ex) {
                // TODO Auto-generated method stub
                progress.dismiss();
                SharedPreferences.Editor editor = sp.edit();
                if (ex == null) {

                    editor.putString("USER_NAME", account);
                    editor.putString("PASSWORD", password);

                    //是否记住密码
                    if (remember.isChecked()) {
                        editor.putBoolean("remember", true);
                    } else {
                        editor.putBoolean("remember", false);
                    }


                    //是否自动登录
                    if (autologin.isChecked()) {
                        editor.putBoolean("autologin", true);
                    } else {
                        editor.putBoolean("autologin", false);
                    }
                    editor.commit();
//                    toast("登录成功---用户名：" + user.getUsername() + "，年龄：" + user.getAge());
                    Intent intent = new Intent(LoginActivity.this, Main2Activity.class);
                    intent.putExtra("from", "login");
                    startActivity(intent);
                    finish();
                } else {
                    progress.dismiss();
                    toast("登录失败：code=" + ex.getErrorCode() + "，错误描述：" + ex.getLocalizedMessage());
                }
            }
        });
    }

    // 双击两次Back键盘退出程序
    @Override
    public void onBackPressed() {
        if (mBackKeyPressedTimes == 0) {
            T.showLong(LoginActivity.this, "再按一次退出程序");
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
            Main2Activity.instance.finish();
            ActivityCollector.finishAll();
        }
        super.onBackPressed();
    }
}
