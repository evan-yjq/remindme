package com.evan.remindme.tasks.domain.model;

import com.evan.remindme.util.Objects;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Keep;

import java.util.Comparator;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/25
 * Time: 上午2:18
 */
@Entity
public class Sort implements Comparable<Sort>{
    @Id
    private String id;

    private String name;

    public Sort(Sort sort){
        this.id = sort.id;
        this.name = sort.name;
    }

    @Keep
    @Generated(hash = 504468038)
    public Sort(String id, String name) {
        this.name = name;
        this.id = this.hashCode()+"";
    }

    @Generated(hash = 1984197757)
    public Sort() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sort sort = (Sort)o;
        return Objects.equal(this.id,sort.id)&&
                Objects.equal(this.name,sort.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Sort name is " + name;
    }


    @Override
    public int compareTo(Sort sort) {
        return this.id.compareTo(sort.id);
    }
}
