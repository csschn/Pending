package com.pending.css.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.pending.css.bean.User;
import com.pending.css.index.Main2Activity;
import com.pending.css.util.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.exception.BmobException;
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.sms.listener.VerifySMSCodeListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 绑定手机号
 */
public class UserBindPhoneActivity extends BaseActivity {


    MyCountTimer timer;
    @Bind(R.id.iv_left)
    ImageView ivLeft;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.et_number)
    EditText etNumber;
    @Bind(R.id.et_input)
    EditText etInput;
    @Bind(R.id.tv_send)
    TextView tvSend;
    @Bind(R.id.tv_bind)
    TextView tvBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind);
        ButterKnife.bind(this);
        ivLeft.setVisibility(View.VISIBLE);
        tvTitle.setText("绑定手机号");
    }

    @OnClick({R.id.iv_left, R.id.tv_send, R.id.tv_bind})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:finish();
                break;
            case R.id.tv_send:requestSMSCode();
                break;
            case R.id.tv_bind:verifyOrBind();
                break;
        }
    }

    //请求验证码
    private void requestSMSCode() {
        String number = etNumber.getText().toString();
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
                        timer.cancel();
                    }
                }
            });
        } else {
            toast("请输入手机号码");
        }
    }


    private void verifyOrBind(){
        final String phone = etNumber.getText().toString();
        String code = etInput.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            showToast("手机号码不能为空");
            return;
        }

        if (TextUtils.isEmpty(code)) {
            showToast("验证码不能为空");
            return;
        }
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("正在验证短信验证码...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        // V3.3.9提供的一键注册或登录方式，可传手机号码和验证码
        BmobSMS.verifySmsCode(this,phone, code, new VerifySMSCodeListener() {

            @Override
            public void done(BmobException ex) {
                // TODO Auto-generated method stub
                progress.dismiss();
                if(ex==null){
                    toast("手机号码已验证");
                    bindMobilePhone(phone);
                    startActivity(new Intent(UserBindPhoneActivity.this, Main2Activity.class));
                }else{
                    toast("验证失败：code="+ex.getErrorCode()+"，错误描述："+ex.getLocalizedMessage());
                }
            }


        });
    }


    private void bindMobilePhone(String phone){
        //开发者在给用户绑定手机号码的时候需要提交两个字段的值：mobilePhoneNumber、mobilePhoneNumberVerified
        User user =new User();
        user.setMobilePhoneNumber(phone);
        user.setMobilePhoneNumberVerified(true);
        User cur = BmobUser.getCurrentUser(UserBindPhoneActivity.this, User.class);
        user.update(UserBindPhoneActivity.this, cur.getObjectId(), new UpdateListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                toast("手机号码绑定成功");
                finish();
            }

            @Override
            public void onFailure(int arg0, String arg1) {
                // TODO Auto-generated method stub
                toast("手机号码绑定失败：" + arg0 + "-" + arg1);
            }
        });
    }

    class MyCountTimer extends CountDownTimer {

        public MyCountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tvSend.setText((millisUntilFinished / 1000) + "秒后重发");
        }

        @Override
        public void onFinish() {
            tvSend.setText("重新发送验证码");
        }
    }
}
