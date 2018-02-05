package com.evan.remindme.allclassify.domain.model;

import java.io.Serializable;
import java.util.Objects;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/25
 * Time: 上午2:18
 */
@Entity
public class Classify implements Comparable<Classify>,Serializable{
    private static final long serialVersionUID = 1L;
    @Id(autoincrement = true)
    private Long id;

    private String name;

    private int sort;

    private boolean isOpen;

    public Classify(Classify classify){
        this.id = classify.id;
        this.name = classify.name;
        this.isOpen = classify.isOpen;
        this.sort = classify.sort;
    }

    @Generated(hash = 2071821727)
    public Classify(Long id, String name, int sort, boolean isOpen) {
        this.id = id;
        this.name = name;
        this.sort = sort;
        this.isOpen = isOpen;
    }

    @Generated(hash = 767880343)
    public Classify() {
    }

    public Classify(String name,int sort){
        this(null, name,sort);
    }

    public Classify(Long id, String name,int sort){
        this(id,name,sort,false);
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

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Classify classify = (Classify) o;
        return sort == classify.sort &&
                isOpen == classify.isOpen &&
                Objects.equals(id, classify.id) &&
                Objects.equals(name, classify.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Classify classify) {
        if (sort==0&&classify.sort==0)return 0;
        if (sort==0) return 1;
        if (classify.sort==0)return -1;

        if (sort>classify.sort) return 1;
        else if(sort == classify.sort){
            if (id > classify.id)return 1;
            else return -1;
        }else return -1;
    }

    public boolean getIsOpen() {
        return this.isOpen;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }
}
