package com.evan.remindme.tasks.com.evan;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/24
 * Time: 下午5:56
 */
public interface BaseView<T extends BasePresenter> {

    void setPresenter(T presenter);
}
