package com.evan.remindme.tasks.domain.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/24
 * Time: 下午10:23
 */
@Entity
public class Sort {
    @Id(autoincrement = true)
    private Long id;

    private String name;

    @Generated(hash = 492124304)
    public Sort(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Generated(hash = 1984197757)
    public Sort() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
