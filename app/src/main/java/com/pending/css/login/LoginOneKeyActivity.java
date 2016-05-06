package com.pending.css.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.pending.css.bean.User;
import com.pending.css.main.Main2Activity;
import com.pending.css.util.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.exception.BmobException;
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.LogInListener;


public class LoginOneKeyActivity extends BaseActivity {

    MyCountTimer timer;

    @Bind(R.id.iv_left)
    ImageView ivLeft;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.et_verify_code)
    EditText etVerifyCode;
    @Bind(R.id.btn_send)
    Button btnSend;
    @Bind(R.id.btn_login)
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_onekey);
        ButterKnife.bind(this);
        ivLeft.setVisibility(View.VISIBLE);
        tvTitle.setText("手机号码一键登录");
    }

    @OnClick({R.id.iv_left, R.id.btn_send, R.id.btn_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:finish();
                break;
            case R.id.btn_send:requestSMSCode();
                break;
            case R.id.btn_login:oneKeyLogin();
                break;
        }
    }
    class MyCountTimer extends CountDownTimer {

        public MyCountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            btnSend.setText((millisUntilFinished / 1000) +"秒后重发");
        }
        @Override
        public void onFinish() {
            btnSend.setText("重新发送验证码");
        }
    }
    private void requestSMSCode()
    {
        String number = etPhone.getText().toString();
        if (!TextUtils.isEmpty(number))
        {
            timer = new MyCountTimer(60000,1000);
            timer.start();
            BmobSMS.requestSMSCode(LoginOneKeyActivity.this, number, "1122", new RequestSMSCodeListener() {
                @Override
                public void done(Integer integer, BmobException e) {
                    if (e == null)
                        toast("验证码发送成功！");
                    else
                        timer.cancel();
                }
            });
        }else toast("请输入手机号！");
    }


    //一键登录操作
    private void oneKeyLogin()
    {
        final String phone = etPhone.getText().toString();
        final String code = etVerifyCode.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            showToast("手机号码不能为空");
            return;
        }

        if (TextUtils.isEmpty(code)) {
            showToast("验证码不能为空");
            return;
        }
        final ProgressDialog progress = new ProgressDialog(LoginOneKeyActivity.this);
        progress.setMessage("正在验证短信验证码...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();


        // V3.3.9提供的一键注册或登录方式，可传手机号码和验证码
        BmobUser.signOrLoginByMobilePhone(LoginOneKeyActivity.this, phone, code, new LogInListener<User>() {

            @Override
            public void done(User user, cn.bmob.v3.exception.BmobException e) {
                progress.dismiss();
                if (e == null) {
                    toast("登录成功");

                    Intent intent = new Intent(LoginOneKeyActivity.this, Main2Activity.class);
                    intent.putExtra("from", "loginonekey");
                    startActivity(intent);
                    finish();
                } else {
                    toast("登录失败！");
//                    toast("登录失败：code=" + e.getErrorCode() + "，错误描述：" + e.getLocalizedMessage());
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if(timer!=null){
            timer.cancel();
        }
    }
}
