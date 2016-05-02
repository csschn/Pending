package bean.dao;

import android.content.Context;
import android.util.Log;

import com.pending.css.bean.ScheduleFolder;
import com.pending.css.util.T;

import cn.bmob.v3.listener.DeleteListener;

/**
 * Created by Administrator on 2016/4/23.
 */
public class ScheduleFolderDao {


    /**
     *接收Context和scheduleFolderId用来删除数据库中的数据
     *@auther css
     *created at 2016/4/23 16:46
     */
    public static void deleteScheduleFolderData(final Context context, String scheduleFolderId) {
        ScheduleFolder scheduleFolder = new ScheduleFolder();
        scheduleFolder.delete(context, scheduleFolderId, new DeleteListener() {
            @Override
            public void onSuccess() {
                T.showDefind(context, "删除成功！");
            }
            @Override
            public void onFailure(int i, String s) {
                Log.d("false","sf"+i+"    "+s);
                T.showDefind(context, "删除失败！");
            }
        });
    }
}
