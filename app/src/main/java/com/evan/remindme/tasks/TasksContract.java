package com.evan.remindme.tasks;

import com.evan.remindme.BasePresenter;
import com.evan.remindme.BaseView;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/24
 * Time: 下午6:03
 */
public interface TasksContract {

    interface View extends BaseView<Presenter>{

    }

    interface Presenter extends BasePresenter{

    }
}
