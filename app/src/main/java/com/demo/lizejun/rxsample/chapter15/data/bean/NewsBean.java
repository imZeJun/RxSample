package com.demo.lizejun.rxsample.chapter15.data.bean;

import java.util.Objects;

public class NewsBean {

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj.getClass() != getClass()) {
            return false;
        } else {
            NewsBean bean = (NewsBean) obj;
            return Objects.equals(bean.title, title);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(title);
    }
}
