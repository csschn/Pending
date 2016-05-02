package com.pending.css.login;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.pending.css.bean.User;
import com.pending.css.util.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends BaseActivity {

    @Bind(R.id.iv_left)
    ImageView ivLeft;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.et_account)
    EditText etAccount;
    @Bind(R.id.et_password)
    EditText etPassword;
    @Bind(R.id.et_pwd_again)
    EditText etPwdAgain;
    @Bind(R.id.btn_register)
    Button btnRegister;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        ivLeft.setVisibility(View.VISIBLE);
        tvTitle.setText("注册");
    }

    @OnClick({R.id.iv_left, R.id.btn_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:finish();
                break;
            case R.id.btn_register:registerUser();
                break;
        }
    }


    private void registerUser() {
        final String account = etAccount.getText().toString();
        final String password = etPassword.getText().toString();
        String pwd = etPwdAgain.getText().toString();
        if (TextUtils.isEmpty(account)) {
            showToast("用户名不能为空");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showToast("密码不能为空");
            return;
        }
        if (!password.equals(pwd)) {
            showToast("两次密码不一样");
            return;
        }
        final ProgressDialog progress = new ProgressDialog(RegisterActivity.this);
        progress.setMessage("正在登录中...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        final User user = new User();
        user.setUsername(account);
        user.setPassword(password);
        user.signUp(this, new SaveListener() {
            @Override
            public void onSuccess() {
                progress.dismiss();
                sp = getSharedPreferences("userInfo", 0);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("USER_NAME", account);
                editor.putString("PASSWORD", password);


                    editor.putBoolean("remember", true);
                    editor.putBoolean("remember", false);

                //是否自动登录
                    editor.putBoolean("autologin", true);
                    editor.putBoolean("autologin", false);
                editor.commit();
                toast("注册成功---用户名："+user.getUsername()+"，年龄："+user.getAge());
                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                intent.putExtra("from", "login");
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                toast("注册失败：code="+i+"，错误描述："+s);
            }
        });
    }
}
