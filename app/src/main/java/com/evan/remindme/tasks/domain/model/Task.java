package com.evan.remindme.tasks.domain.model;

import com.evan.remindme.addedittask.TasksCircleType;
import com.evan.remindme.addedittask.TasksRepeatType;
import com.evan.remindme.util.Objects;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;
import java.util.UUID;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Keep;


/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/24
 * Time: 下午9:51
 */

@Entity
public class Task implements Comparable<Task>{
    @Id
    private String id;

    //标题
    private String title;

    /**
     * 是否循环
     * 不循环：-1
     * 每日循环：0
     * 每周循环：1
     * 每月循环：2
     * 每年循环：3
     */
    private int circle;

    /**
     * 是否重复
     * 不重复：-1
     * 一分钟后重复：0
     * 五分钟后重复：1
     * 十分钟后重复：2
     * 半小时后重复：3
     */
    private int repeat;

    //提醒设立时间
    private Date time;

    //下次提醒时间
    private Date nextTime;

    //分类
    private Long classifyId;

    //铃声所在地
    private String bell;

    //是否开启
    private boolean turnOn;

    public boolean isTurnOn() {
        return turnOn;
    }

    public void setTurnOn(boolean turnOn) {
        this.turnOn = turnOn;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Date getNextTime() {
        return nextTime;
    }

    public void setNextTime(Date nextTime) {
        this.nextTime = nextTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public String getBell() {
        return bell;
    }

    public void setBell(String bell) {
        this.bell = bell;
    }

    public int getCircle() {
        return circle;
    }

    public void setCircle(int circle) {
        this.circle = circle;
    }

    public Long getClassifyId() {
        return classifyId;
    }

    public void setClassifyId(Long classifyId) {
        this.classifyId = classifyId;
    }

    public boolean isRepeat(){
        return repeat==TasksRepeatType.REPEAT_;
    }

    public boolean isCircle(){
        return circle==TasksCircleType.CIRCLE_;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equal(id, task.id) &&
                Objects.equal(title, task.title) &&
                Objects.equal(circle, task.circle) &&
                Objects.equal(time, task.time) &&
                Objects.equal(classifyId,task.classifyId)&&
                Objects.equal(repeat, task.repeat)&&
                Objects.equal(bell, task.bell);
    }

    public Task(Task task){
        this.time = task.time;
        this.nextTime = task.nextTime;
        this.repeat = task.repeat;
        this.title = task.title;
        this.bell = task.bell;
        this.circle = task.circle;
        this.id = task.id;
        this.classifyId = task.classifyId;
        this.turnOn = task.turnOn;
    }

    public Task(String title, int circle, int repeat, Date time,
                Date nextTime, Long classifyId, String bell) {
        this(UUID.randomUUID().toString(),title,circle,repeat,time,nextTime,classifyId,bell,true);
    }

    public Task(String title,Long id,Date time){
        this(title,-1,-1,time,null,id,"");
    }

    public Task(String title,Date time){
        this(title,(long)1,time);
    }

    @Generated(hash = 733837707)
    public Task() {
    }

    @Keep
    @Generated(hash = 191800848)
    public Task(String id, String title, int circle, int repeat, Date time,
            Date nextTime, Long classifyId, String bell, boolean turnOn) {
        this.title = title;
        this.circle = circle;
        this.repeat = repeat;
        this.time = time;
        this.nextTime = nextTime;
        this.classifyId = classifyId;
        this.bell = bell;
        this.turnOn = turnOn;
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(title,classifyId);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", circle=" + circle +
                ", repeat=" + repeat +
                ", time=" + time +
                ", nextTime=" + nextTime +
                ", classifyId=" + classifyId +
                ", bell='" + bell + '\'' +
                ", turnOn=" + turnOn +
                '}';
    }

    @Override
    public int compareTo(Task task) {
        Date date = new Date();
        if (this.nextTime.before(date))return 1;
        if (task.nextTime.before(date))return -1;
        if(this.nextTime.equals(task.nextTime)) {
            if (this.time.equals(task.time)) {
                return 0;
            }else if(this.time.before(task.time)){
                return -1;
            }else{
                return 1;
            }
        }else if (this.nextTime.before(task.nextTime)) {
            return -1;
        }else {
            return 1;
        }
    }

    public boolean getTurnOn() {
        return this.turnOn;
    }
}
