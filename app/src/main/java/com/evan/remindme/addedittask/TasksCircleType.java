package com.evan.remindme.addedittask;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/24
 * Time: 下午11:13
 */
public class TasksCircleType {
    public static final String[] TASKS_CIRCLE_TYPE_LIST = new String[]{"不循环","一天后循环","一周后循环","一月后循环","一年后循环"};
    /**
     * 不循环
     */
    public static final int CIRCLE_ = -1;
    /**
     * 一天后循环
     */
    public static final int CIRCLE_D = 0;
    /**
     * 一周后循环
     */
    public static final int CIRCLE_W = 1;
    /**
     * 一月后循环
     */
    public static final int CIRCLE_M = 2;
    /**
     * 一年后循环
     */
    public static final int CIRCLE_Y = 3;
}
