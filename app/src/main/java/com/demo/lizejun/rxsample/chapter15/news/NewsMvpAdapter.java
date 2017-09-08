package com.demo.lizejun.rxsample.chapter15.news;

import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.demo.lizejun.rxsample.R;
import com.demo.lizejun.rxsample.network.entity.NewsEntity;
import com.demo.lizejun.rxsample.network.entity.NewsResultEntity;
import java.util.List;

public class NewsMvpAdapter extends RecyclerView.Adapter<NewsMvpAdapter.NewsMvpHolder> {


    private NewsEntity mNewsEntity;

    @Override
    public NewsMvpHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NewsMvpHolder(parent, R.layout.layout_news_mvp_item);
    }

    @Override
    public void onBindViewHolder(NewsMvpHolder holder, int position) {
        if (mNewsEntity != null) {
            List<NewsResultEntity> mNewsResult = mNewsEntity.getResults();
            if (mNewsResult != null) {
                NewsResultEntity resultEntity = mNewsResult.get(position);
                TextView titleView = holder.getView(R.id.tv_title);
                titleView.setText("[" + resultEntity.getType() + "] " + resultEntity.getDesc() + " -- " + resultEntity.getWho());
            }
        }
    }

    @Override
    public int getItemCount() {
        return mNewsEntity != null ? mNewsEntity.getResults() != null ? mNewsEntity.getResults().size() : 0 : 0;
    }

    public void setNewsResult(NewsEntity newsEntity) {
        mNewsEntity = newsEntity;
    }

    class NewsMvpHolder extends RecyclerView.ViewHolder {

        NewsMvpHolder(ViewGroup holder, @LayoutRes int layoutId) {
            super(LayoutInflater.from(holder.getContext()).inflate(layoutId, holder, false));
        }

        <T extends View> T getView(@IdRes int resId) {
            Object childView = itemView.getTag(resId);
            if (childView == null) {
                childView = itemView.findViewById(resId);
                itemView.setTag(resId, childView);
            }
            return (T) childView;
        }
    }
}
