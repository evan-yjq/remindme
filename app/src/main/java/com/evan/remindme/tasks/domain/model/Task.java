package com.evan.remindme.tasks.domain.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/24
 * Time: 下午9:51
 */

@Entity
public class Task {
    @Id(autoincrement = true)
    private Long id;

    //
    private String title;

    //
    private int repeat;

    //
    private Long sortId;

    //
    private String bell;

    @Generated(hash = 1902981430)
    public Task(Long id, String title, int repeat, Long sortId, String bell) {
        this.id = id;
        this.title = title;
        this.repeat = repeat;
        this.sortId = sortId;
        this.bell = bell;
    }

    @Generated(hash = 733837707)
    public Task() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Long getSortId() {
        return sortId;
    }

    public void setSortId(Long sortId) {
        this.sortId = sortId;
    }

    public String getBell() {
        return bell;
    }

    public void setBell(String bell) {
        this.bell = bell;
    }
}
