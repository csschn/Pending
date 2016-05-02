package com.pending.css.login;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.pending.css.util.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.exception.BmobException;
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.ResetPasswordByCodeListener;

public class ResetPasswordActivity extends BaseActivity {

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
    @Bind(R.id.et_pwd)
    EditText etPwd;
    @Bind(R.id.btn_reset)
    Button btnReset;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);
        ButterKnife.bind(this);
        ivLeft.setVisibility(View.VISIBLE);
        tvTitle.setText("重置密码");
    }

    @OnClick({R.id.iv_left, R.id.btn_send, R.id.btn_reset})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:finish();
                break;
            case R.id.btn_send:requestSMSCode();
                break;
            case R.id.btn_reset:resetPwd();
                break;
        }
    }

    private void requestSMSCode() {
        String number = etPhone.getText().toString();
        if (!TextUtils.isEmpty(number)) {
            timer = new MyCountTimer(60000, 1000);
            timer.start();
            BmobSMS.requestSMSCode(this, number, "手机号码登陆模板", new RequestSMSCodeListener() {

                @Override
                public void done(Integer smsId, BmobException ex) {
                    // TODO Auto-generated method stub
                    if (ex == null) {// 验证码发送成功
                        toast("验证码发送成功");// 用于查询本次短信发送详情
                    } else {//如果验证码发送错误，可停止计时
                        toast("密码重置失败：code=" + ex.getErrorCode()+"，错误描述："+ex.getLocalizedMessage());
                      //  timer.cancel();
                    }
                }
            });
        } else {
            toast("请输入手机号码");
        }
    }


    class MyCountTimer extends CountDownTimer {

        public MyCountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            btnSend.setText((millisUntilFinished / 1000) + "秒后重发");
        }

        @Override
        public void onFinish() {
            btnSend.setText("重新发送验证码");
        }
    }


    private void resetPwd() {
        final String code = etVerifyCode.getText().toString();
        final String pwd = etPwd.getText().toString();
        if (TextUtils.isEmpty(code)) {
            showToast("验证码不能为空");
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            showToast("密码不能为空");
            return;
        }
        final ProgressDialog progress = new ProgressDialog(ResetPasswordActivity.this);
        progress.setMessage("正在重置密码...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        // V3.3.9提供的重置密码功能，只需要输入验证码和新密码即可
        BmobUser.resetPasswordBySMSCode(this, code, pwd, new ResetPasswordByCodeListener() {
            @Override
            public void done(cn.bmob.v3.exception.BmobException e) {
                progress.dismiss();
                if(e==null){
                    toast("密码重置成功");
                    finish();
                }else{
                    toast("密码重置失败：code="+e.getErrorCode()+"，错误描述："+e.getLocalizedMessage());
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
