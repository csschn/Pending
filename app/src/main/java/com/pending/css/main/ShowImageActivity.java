package com.pending.css.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ImageView;

import com.pending.css.login.R;
import com.pending.css.util.BaseActivity;

import java.io.FileNotFoundException;

/**
 * Created by Administrator on 2016/4/27.
 */
public class ShowImageActivity extends BaseActivity {
    private ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_show_image);
        Uri uri = getIntent().getData();
//        Log.d("uri", uri.toString());
        imageView = (ImageView) findViewById(R.id.show_image);
//        Log.d("uri",uri.toString());
        imageView.setImageBitmap(decodeUriAsBitmap(uri));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
}
