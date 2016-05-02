package com.pending.css.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.pending.css.util.BaseActivity;
import com.pending.css.util.PreferenceUtil;

public class UnlockActivity extends BaseActivity {

    private LockPatternView mLockPatternView;
    private String mPasswordStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        mLockPatternView = (LockPatternView) findViewById(R.id.lockView);

        mLockPatternView.setLockListener(new LockPatternView.OnLockListener() {
            String password = PreferenceUtil.getGesturePassword(UnlockActivity.this);

            @Override
            public void getStringPassword(String password) {
                mPasswordStr = password;
            }

            @Override
            public boolean isPassword() {
                if (mPasswordStr.equals(password)) {
                    Intent intent = new Intent(UnlockActivity.this, LoginActivity.class);
                    startActivity(intent);
                    UnlockActivity.this.finish();
//                    return true;
                } else {
                    Toast.makeText(UnlockActivity.this, "密码不正确", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

    }

}
