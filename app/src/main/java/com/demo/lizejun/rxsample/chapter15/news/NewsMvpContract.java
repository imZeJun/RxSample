package com.demo.lizejun.rxsample.chapter15.news;

import android.support.annotation.IntDef;
import com.demo.lizejun.rxsample.chapter15.data.bean.NewsBean;
import java.util.List;

public class NewsMvpContract {

    public static final int REFRESH_AUTO = 0;
    public static final int REFRESH_CACHE = 1;

    @IntDef ({REFRESH_AUTO, REFRESH_CACHE})
    public @interface RefreshType {}

    public interface View {
        void onRefreshFinished(@RefreshType int refreshType, List<NewsBean> newsEntity);
        void showTips(String message);
    }

    public interface Presenter {
        void refresh(@RefreshType int refreshType);
        void destroy();
    }

}
