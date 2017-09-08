package com.demo.lizejun.rxsample;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.demo.lizejun.rxsample.chapter1.DownloadActivity;
import com.demo.lizejun.rxsample.chapter10.RotationPersistActivity;
import com.demo.lizejun.rxsample.chapter11.WeatherActivity;
import com.demo.lizejun.rxsample.chapter12.HotObservableActivity;
import com.demo.lizejun.rxsample.chapter13.ErrorActivity;
import com.demo.lizejun.rxsample.chapter14.TokenActivity;
import com.demo.lizejun.rxsample.chapter15.news.NewsMvpActivity;
import com.demo.lizejun.rxsample.chapter16.UsingActivity;
import com.demo.lizejun.rxsample.chapter2.BufferActivity;
import com.demo.lizejun.rxsample.chapter3.SearchActivity;
import com.demo.lizejun.rxsample.chapter4.NewsActivity;
import com.demo.lizejun.rxsample.chapter5.PollingActivity;
import com.demo.lizejun.rxsample.chapter6.RetryActivity;
import com.demo.lizejun.rxsample.chapter7.CombineLatestActivity;
import com.demo.lizejun.rxsample.chapter8.CacheActivity;
import com.demo.lizejun.rxsample.chapter9.TimeActivity;
import com.demo.lizejun.rxsample.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

    private List<String> mTitles = new ArrayList<>();


    public MainAdapter(List<String> titles) {
        mTitles = titles;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_news_item, parent, false);
        return new MainViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final MainViewHolder holder, int position) {
        final String title = mTitles.get(position);
        TextView titleView = holder.mTvTitle;
        titleView.setText(title);
        titleView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                switch (position) {
                    case 0:
                        Utils.startActivity(v.getContext(), DownloadActivity.class);
                        break;
                    case 1:
                        Utils.startActivity(v.getContext(), BufferActivity.class);
                        break;
                    case 2:
                        Utils.startActivity(v.getContext(), SearchActivity.class);
                        break;
                    case 3:
                        Utils.startActivity(v.getContext(), NewsActivity.class);
                        break;
                    case 4:
                        Utils.startActivity(v.getContext(), PollingActivity.class);
                        break;
                    case 5:
                        Utils.startActivity(v.getContext(), RetryActivity.class);
                        break;
                    case 6:
                        Utils.startActivity(v.getContext(), CombineLatestActivity.class);
                        break;
                    case 7:
                        Utils.startActivity(v.getContext(), CacheActivity.class);
                        break;
                    case 8:
                        Utils.startActivity(v.getContext(), TimeActivity.class);
                        break;
                    case 9:
                        Utils.startActivity(v.getContext(), RotationPersistActivity.class);
                        break;
                    case 10:
                        Utils.startActivity(v.getContext(), WeatherActivity.class);
                        break;
                    case 11:
                        Utils.startActivity(v.getContext(), HotObservableActivity.class);
                        break;
                    case 12:
                        Utils.startActivity(v.getContext(), ErrorActivity.class);
                        break;
                    case 13:
                        Utils.startActivity(v.getContext(), TokenActivity.class);
                        break;
                    case 14:
                        Utils.startActivity(v.getContext(), NewsMvpActivity.class);
                        break;
                    case 15:
                        Utils.startActivity(v.getContext(), UsingActivity.class);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTitles.size();
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder {

        public TextView mTvTitle;

        public MainViewHolder(View rootView) {
            super(rootView);
            mTvTitle = (TextView) rootView.findViewById(R.id.tv_title);
        }
    }
}
