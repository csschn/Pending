package com.pending.css.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pending.css.util.BaseActivity;
import com.pending.css.util.PreferenceUtil;
import com.pending.css.util.T;

public class SetLockActivity extends BaseActivity {

    private TextView mTitleTv;
    private LockPatternView mLockPatternView;
    //    private LinearLayout mBottomLayout;
    private Button mClearBtn;
//    private Button mConfirmBtn;

    private String mPassword;
    /**
     * 是否是第一次输入密码
     */
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_lock);
        initViews();
        initEvents();
    }

    private void initEvents() {
        mLockPatternView.setLockListener(new LockPatternView.OnLockListener() {
            @Override
            public void getStringPassword(String password) {
                if (isFirst) {
                    mPassword = password;
                    mTitleTv.setText("再次输入手势密码");
                    isFirst = false;
                    mClearBtn.setVisibility(View.VISIBLE);
                } else {
                    if (password.equals(mPassword)) {
                        setPasswordToPreference(password);
                        T.showDefind(SetLockActivity.this,"设置成功！");
//                        startActivity(new Intent(SetLockActivity.this, LoginActivity.class));
                        SetLockActivity.this.finish();
                    }else {
                        Toast.makeText(SetLockActivity.this,"两次密码不一致，请重新设置",Toast.LENGTH_SHORT).show();
                        mPassword = "";
                        mTitleTv.setText("设置手势密码");
                        isFirst = false;
                        mClearBtn.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public boolean isPassword() {
                return false;
            }
        });

        mClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPassword = "";
                isFirst = true;
                mTitleTv.setText("设置手势密码");
                mClearBtn.setVisibility(View.GONE);
            }
        });

    }

    private void setPasswordToPreference(String password) {
        PreferenceUtil.setGesturePassword(SetLockActivity.this, password);
    }

    private void initViews() {
        mTitleTv = (TextView) findViewById(R.id.tv_activity_set_lock_title);
        mLockPatternView = (LockPatternView) findViewById(R.id.lockView);
        mClearBtn = (Button) findViewById(R.id.btn_password_clear);
    }
}
