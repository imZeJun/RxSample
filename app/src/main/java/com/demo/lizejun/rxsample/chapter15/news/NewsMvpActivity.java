package com.demo.lizejun.rxsample.chapter15.news;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.demo.lizejun.rxsample.R;
import com.demo.lizejun.rxsample.network.entity.NewsEntity;
import com.demo.lizejun.rxsample.network.entity.NewsResultEntity;
import java.util.List;

public class NewsMvpActivity extends AppCompatActivity implements NewsMvpContract.View {


    private RecyclerView mRecyclerView;
    private NewsMvpAdapter mRecyclerAdapter;
    private NewsEntity mNewsEntity = new NewsEntity();
    private NewsMvpContract.Presenter mPresenter;
    private TextView mTipsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_mvp);
        initView();
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_news);
        mTipsView = (TextView) findViewById(R.id.tv_loading);
        mRecyclerAdapter = new NewsMvpAdapter();
        mRecyclerAdapter.setNewsResult(mNewsEntity);
        LinearLayoutManager layoutMgr = new LinearLayoutManager(this);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(layoutMgr);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mPresenter = new NewsPresenter(this);
        mPresenter.loadNews(true);
    }

    @Override
    public void showLoadingIndicator(boolean loading) {
        if (loading) {
            mRecyclerView.setVisibility(View.GONE);
            mTipsView.setVisibility(View.VISIBLE);
            mTipsView.setText("正在加载...");
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mTipsView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showNews(NewsEntity newsEntity) {
        mNewsEntity.setError(newsEntity.isError());
        List<NewsResultEntity> newResults = newsEntity.getResults();
        mNewsEntity.getResults().clear();
        mNewsEntity.getResults().addAll(newResults);
        mRecyclerAdapter.notifyDataSetChanged();
    }


}
