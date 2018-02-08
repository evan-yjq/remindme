package com.evan.remindme.nexttimelistener;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import com.evan.remindme.*;
import com.evan.remindme.addedittask.TasksCircleType;
import com.evan.remindme.addedittask.domain.usecase.SaveTask;
import com.evan.remindme.tasks.TasksDisplayType;
import com.evan.remindme.tasks.domain.model.Task;
import com.evan.remindme.tasks.domain.usecase.GetTasks;
import com.evan.remindme.util.DateUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static android.app.PendingIntent.FLAG_NO_CREATE;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/2/7
 * Time: 下午5:36
 */
public class NextTimeListener extends Service {

    private static final int NOTIFICATION_DOWNLOAD_PROGRESS_ID = 0x0001;

    private List<Task>mTasks;
    private Date mNextTime;
    private boolean forceUpdate;
    private final UseCaseHandler useCaseHandler = Injection.provideUseCaseHandler();
    private GetTasks getTasks;
    private SaveTask saveTask;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        getTasks = Injection.provideGetTasks(getApplicationContext());
        saveTask = Injection.provideSaveTask(getApplicationContext());
        startListener();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        showId = new ArrayList<>();
        forceUpdate = true;
        return START_STICKY;
    }

    public void startListener() {
        useCaseHandler.execute(getTasks, new GetTasks.RequestValues(false, TasksDisplayType.TASKS_BY_TIME),
                new UseCase.UseCaseCallback<GetTasks.ResponseValue>() {
                    @Override
                    public void onSuccess(GetTasks.ResponseValue response) {
                        mTasks = response.getTasks().get(null);
                        getNextTime();
                        listener();
                    }

                    @Override
                    public void onError() {
                        startListener();
                    }
                });
    }

    //计算并写入nextTime
    private void getNextTime(){
        Date date = new Date();
        if (mTasks==null){
            forceUpdate = false;
            return;
        }
        for (Task task : mTasks) {
            Date createTime = task.getTime();
            Date nextTime = task.getNextTime();
            Date tmp = null;
            int circle = task.getCircle();
            switch (circle){
                case TasksCircleType.CIRCLE_:
                    if (nextTime.before(date))task.setTurnOn(false);
                    break;
                case TasksCircleType.CIRCLE_D:
                    tmp = getNextTimeDay(createTime);
                    break;
                case TasksCircleType.CIRCLE_W:
                    tmp = getNextTimeWeek(createTime);
                    break;
                case TasksCircleType.CIRCLE_M:
                    tmp = getNextTimeMonth(createTime);
                    break;
                case TasksCircleType.CIRCLE_Y:
                    tmp = getNextTimeYear(createTime);
                    break;
            }
            if (tmp!=null&&!nextTime.equals(tmp)){
                task.setNextTime(tmp);
                updateDB(task);
            }
        }
        Collections.sort(mTasks);
        for (Task task : mTasks) {
            if (task.isTurnOn() && task.getNextTime().after(date)) {
                mNextTime = task.getNextTime();
                break;
            }
        }
        forceUpdate = false;
    }

    private void listener(){
        Thread thread = new Thread() {
            @Override
            public void run() {
                //每隔一分钟监听获取当前时间比对
                while (!forceUpdate){
                    System.out.println("Next Time:"+new DateUtils().Date2String(mNextTime));
                    Date now = new Date();
                    if (mNextTime!=null && equal(mNextTime,now)) {
                        for (int i = 0, j = 0; i < 1 && j < mTasks.size(); j++, i++) {
                            if ((j + 1) < mTasks.size() && mTasks.get(j + 1).getNextTime() == mNextTime) i--;
                            if (mTasks.get(j).isTurnOn()) {
                                show(mTasks.get(j).getId(),mTasks.get(j).getTitle());
                                getNextTime();
                            }
                        }
                    }
                    try{Thread.sleep(1500);}
                    catch(Exception ignored){}
                }
                startListener();
            }
        };
        thread.start();
    }

    private List<String>showId;
    private void show(final String id, final String title){
//        MediaPlayer mediaPlayer = new MediaPlayer();
//        AssetFileDescriptor file = this.getResources().openRawResourceFd(R.raw.haha);
//        try {
//            mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
//            file.close();
//            mediaPlayer.prepare();
//        } catch (IOException ioe) {
//            mediaPlayer = null;
//        }
//        if (mediaPlayer != null) {
//            mediaPlayer.start();
//        }
        if (!showId.contains(id)){
            showId.add(id);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    int spacing = 1500;
                    int num = 60/(spacing/1000);
                    for (int i = 0; i < num; i++) {
                        NotificationManager mNotificationManager;
                        NotificationCompat.Builder mBuilder;
                        mBuilder = new NotificationCompat.Builder(NextTimeListener.this);
                        mNotificationManager = (NotificationManager) NextTimeListener.this.getSystemService(NOTIFICATION_SERVICE);
                        mBuilder.setContentTitle("提醒我！")
                                .setContentText(title)
                                .setTicker("时间到啦")//通知首次出现在通知栏，带上升动画效果的
                                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
                                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                                .setDefaults(Notification.DEFAULT_ALL)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
                                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                                .setSmallIcon(R.mipmap.logo);
                        //创建通知
                        Notification notification = mBuilder.build();
                        //设置为前台服务
                        mNotificationManager.notify(NOTIFICATION_DOWNLOAD_PROGRESS_ID,notification);
                        try{Thread.sleep(spacing);}
                        catch(Exception ignored){}
                    }
                    showId.remove(id);
                }
            };
            runnable.run();
        }
    }

    private void updateDB(Task task){
        useCaseHandler.execute(saveTask, new SaveTask.RequestValues(task, false),
                new UseCase.UseCaseCallback<SaveTask.ResponseValue>() {
                    @Override
                    public void onSuccess(SaveTask.ResponseValue response) {}

                    @Override
                    public void onError() {}
                });

    }

    public static Date getNextTimeYear(Date date){
        Date d = (Date) date.clone();
        while (d.before(new Date())){
            d.setYear(d.getYear()+1);
        }
        return d;
    }

    public static Date getNextTimeWeek(Date date){
        Date d = (Date) date.clone();
        while (d.before(new Date())){
            for (int i = 0; i < 7; i++) {
                d = addDay(d);
            }
        }
        return d;
    }

    public static Date getNextTimeMonth(Date date){
        Date d = (Date) date.clone();
        while (d.before(new Date())){
            d = addMonth(d);
        }
        return d;
    }

    public static Date getNextTimeDay(Date date){
        Date d = (Date) date.clone();
        while (d.before(new Date())) {
            d = addDay(d);
        }
        return d;
    }

    public static Date addDay(Date date){
        int day = getDaysByYearMonth(date.getYear()+1900,date.getMonth()+1);
        if (date.getDate()+1==day){
            date = addMonth(date);
            date.setDate(0);
        }else {
            date.setDate(date.getDate() + 1);
        }
        return date;
    }

    public static Date addMonth(Date date){
        if(date.getMonth()==11){
            date.setMonth(0);
        }else{
            date.setMonth(date.getMonth()+1);
        }
        return date;
    }

    //根据年 月 获取对应的月份 天数
    public static int getDaysByYearMonth(int year, int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        return a.get(Calendar.DATE);
    }

    private boolean equal(Date d1,Date d2){
        return new DateUtils().Date2String(d1).equals(new DateUtils().Date2String(d2));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
