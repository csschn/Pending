package com.pending.css.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pending.css.main.Main2Activity;

import com.pending.css.bean.User;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;

public class MainActivity extends Activity {

    @Bind(R.id.iv_left)
    ImageView ivLeft;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_user)
    TextView tvUser;
    @Bind(R.id.btn_bind)
    Button btnBind;
    @Bind(R.id.btn_reset)
    Button btnReset;
    String from;
    @Bind(R.id.btn_bind_cancel)
    Button btnBindCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        from = getIntent().getStringExtra("from");
        if (from.equals("login")) {
            btnBind.setVisibility(View.VISIBLE);
        } else {
            btnBind.setVisibility(View.GONE);
        }
        ivLeft.setVisibility(View.VISIBLE);
        tvTitle.setText("首页");
    }


    private void UpdateUser() {
        User user = BmobUser.getCurrentUser(this, User.class);
        //用户只有绑定过手机号或者用手机号注册登录过就可以直接通过手机号码来重置用户密码了，所以加下这个判断
        if (user != null && user.getMobilePhoneNumberVerified() != null && user.getMobilePhoneNumberVerified()) {
            btnReset.setVisibility(View.VISIBLE);
            btnBindCancel.setVisibility(View.INVISIBLE);
        } else {
            btnBindCancel.setVisibility(View.VISIBLE);
            btnReset.setVisibility(View.INVISIBLE);
        }
        tvUser.setText(user.getUsername() + "-" + user.getAge() + "-" + user.getMobilePhoneNumberVerified() + "-" + user.getMobilePhoneNumber());
    }


    @Override
    protected void onResume() {//每次活动处于可见状态的时候调用此方法判断是否注册
        // TODO Auto-generated method stub
        super.onResume();
        UpdateUser();
    }


    @OnClick({R.id.iv_left, R.id.btn_bind, R.id.btn_reset})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.btn_bind:
                startActivity(new Intent(this, UserBindPhoneActivity.class));
                break;
            case R.id.btn_reset:
                startActivity(new Intent(this, ResetPasswordActivity.class));
                break;
        }
    }

    @OnClick(R.id.btn_bind_cancel)
    public void onClick() {
        startActivity(new Intent(this, Main2Activity.class));
    }
}
