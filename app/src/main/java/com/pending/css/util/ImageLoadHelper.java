package com.pending.css.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *这个类用来加载网络图片，并且可以调用方法实现保存图片到本地
 *@auther css
 *created at 2016/4/22 16:46
 */
public  class ImageLoadHelper {

    private ImageView mImageView;
    private String localPath;
    private String pictureName;
    private String localPathNext;

    public ImageLoadHelper(ImageView imageView)
    {
        mImageView = imageView;
    }

    public ImageLoadHelper(ImageView imageView, String localPath,String localPathNext, String pictureName)
    {
        mImageView = imageView;
        this.localPath = localPath;
        this.localPathNext = localPathNext;
        this.pictureName = pictureName;

    }


    public void downloadFromNetToLocal(String Url)
    {
        new LoadAsyncTaskAndLoadToLocal().execute(Url);
    }


    /**
     *只用来加载图片显示，不保存到本地
     *@auther css
     *created at 2016/4/22 16:49
     */
    class LoadAsyncTask extends AsyncTask<String, Void, Bitmap>
    {
        @Override
        protected Bitmap doInBackground(String... arg0) {
            String url = arg0[0];
            URL u = null;
            try {
                u = new URL(url);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) u.openConnection();
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(8000);
                conn.setRequestProperty("Accept-Charset", "utf-8");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            InputStream inputStream = null;
            try {
                inputStream = conn.getInputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            mImageView.setImageBitmap(result);
        }
    }

    class LoadAsyncTaskAndLoadToLocal extends AsyncTask<String, Void, Bitmap>
    {
        @Override
        protected Bitmap doInBackground(String... arg0) {
            String url = arg0[0];
            URL u = null;
            try {
                u = new URL(url);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) u.openConnection();
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(8000);
                conn.setRequestProperty("Accept-Charset", "utf-8");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            InputStream inputStream = null;
            try {
                inputStream = conn.getInputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            mImageView.setImageBitmap(result);
            try {
                saveBitmap(result,localPath,localPathNext,pictureName);
                Log.d("TAGtag", "result!");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    /**
     * @param bitmap:图片的bitmap
     * @param src：要保存到本地的路径
     * @param bitName：要保存到本地的文件名
     * @throws IOException
     */
    private void saveBitmap(Bitmap bitmap,String src,String src1 , String bitName) throws IOException
    {
        File pictureFileDir = new File(src);
        if (!pictureFileDir.exists())
        {
            pictureFileDir.mkdir();
        }
        File pictureFileDirImage =  new File(pictureFileDir.getAbsolutePath()+"/"+src1);
        if (!pictureFileDirImage.exists())
        {
            pictureFileDirImage.mkdir();
        }
        File picFile = new File(pictureFileDirImage.getAbsolutePath()+"/"+ bitName);
        if(picFile.exists()){
            picFile.delete();
        }
        picFile.createNewFile();
        FileOutputStream out;
        try{
            out = new FileOutputStream(picFile);
            if(bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out))
            {
                out.flush();
                out.close();
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * @param path:图片的物理路径
     * @param display_width：设置图片的宽
     * @param display_height：设置图片的高
     * @return
     * @throws Exception
     */
    public static Bitmap decodeBitmap(String path,float display_width,float display_height) throws Exception {
        if (Float.compare(display_width,0.0f) == 0 || Float.compare(display_width,0.0f) == 0) {
            throw new Exception("图片的宽高均不能为0！");
        }
        BitmapFactory.Options op = new BitmapFactory.Options();
        //inJustDecodeBounds
        //If set to true, the decoder will return null (no bitmap), but the out…
        op.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(path, op); //获取尺寸信息
        //获取比例大小
        int wRatio = (int)Math.ceil(op.outWidth/display_width);
        int hRatio = (int)Math.ceil(op.outHeight/display_height);
        //如果超出指定大小，则缩小相应的比例
        if(wRatio > 1 && hRatio > 1){
            if(wRatio > hRatio){
                op.inSampleSize = wRatio;
            }else{
                op.inSampleSize = hRatio;
            }
        }
        op.inJustDecodeBounds = false;
        bmp = BitmapFactory.decodeFile(path, op);
        return bmp;
    }

















    /*ImageLoader mImageLoader;

    public ImageLoader imageLoadHelper(Context context)
    {
        return initImageLoader(context, mImageLoader, "test");
    }
    *//**
     * 初始化图片下载器，图片缓存地址<i>("/Android/data/[app_package_name]/cache/dirName")</i>
     *//*
    public static ImageLoader initImageLoader(Context context,
                                       ImageLoader imageLoader, String dirName) {
        imageLoader = ImageLoader.getInstance();
        if (imageLoader.isInited()) {
            // 重新初始化ImageLoader时,需要释放资源.
            imageLoader.destroy();
        }
        imageLoader.init(initImageLoaderConfig(context, dirName));
        return imageLoader;
    }

    *//**
     * 配置图片下载器
     *
     * @param dirName
     *            文件名
     *//*
    private static ImageLoaderConfiguration initImageLoaderConfig(
            Context context, String dirName) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .threadPoolSize(3).memoryCacheSize(getMemoryCacheSize(context))
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .discCache(new UnlimitedDiscCache(new File(dirName)))
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        return config;
    }

    public static int getMemoryCacheSize(Context context) {
        int memoryCacheSize;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            int memClass = ((ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE))
                    .getMemoryClass();
            memoryCacheSize = (memClass / 8) * 1024 * 1024; // 1/8 of app memory
            // limit
        } else {
            memoryCacheSize = 2 * 1024 * 1024;
        }
        return memoryCacheSize;
    }*/
}
