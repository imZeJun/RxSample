package com.demo.lizejun.rxsample.chapter15.news;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.demo.lizejun.rxsample.R;
import com.demo.lizejun.rxsample.chapter15.data.bean.NewsBean;
import java.util.ArrayList;
import java.util.List;

public class NewsMvpActivity extends AppCompatActivity implements NewsMvpContract.View {

    private CoordinatorLayout mRootLayout;
    private RecyclerView mRecyclerView;
    private NewsMvpAdapter mRecyclerAdapter;
    private List<NewsBean> mNewsBeans = new ArrayList<>();
    private NewsMvpContract.Presenter mPresenter;
    private LinearLayoutManager mLayoutMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_mvp);
        initView();
        dispatchRefresh(NewsMvpContract.REFRESH_CACHE);
    }

    private void initView() {
        mRootLayout = (CoordinatorLayout) findViewById(R.id.cl_root);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_news);
        mRecyclerAdapter = new NewsMvpAdapter();
        mRecyclerAdapter.setNewsResult(mNewsBeans);
        mLayoutMgr = new LinearLayoutManager(this);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(mLayoutMgr);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mPresenter = new NewsPresenter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dispatchRefresh(NewsMvpContract.REFRESH_AUTO);
    }

    private void dispatchRefresh(@NewsMvpContract.RefreshType int refreshType) {
        mPresenter.refresh(refreshType);
    }

    @Override
    public void onRefreshFinished(@NewsMvpContract.RefreshType int refreshType, List<NewsBean> newsBeans) {
        mNewsBeans.clear();
        mNewsBeans.addAll(newsBeans);
        mRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void showTips(String message) {
        Snackbar.make(mRootLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }
}
