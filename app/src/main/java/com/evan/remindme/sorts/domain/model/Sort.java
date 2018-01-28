package com.evan.remindme.sorts.domain.model;

import com.evan.remindme.util.Objects;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Keep;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/25
 * Time: 上午2:18
 */
@Entity
public class Sort implements Comparable<Sort>{
    @Id(autoincrement = true)
    private Long id;

    private String name;

    private boolean isOpen;

    public Sort(Sort sort){
        this.id = sort.id;
        this.name = sort.name;
        this.isOpen = sort.isOpen;
    }

    @Keep
    @Generated(hash = 75960416)
    public Sort(Long id, String name, boolean isOpen) {
        this.id = id;
        this.name = name;
        this.isOpen = isOpen;
    }

    @Generated(hash = 1984197757)
    public Sort() {
    }

    public Sort(String name){
        this(null, name);
    }

    public Sort(Long id,String name){
        this(id,name,false);
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
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
        return this.name.compareTo(sort.name);
    }

    public boolean getIsOpen() {
        return this.isOpen;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }
}
