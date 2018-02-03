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

    private boolean isOpen;

    public Classify(Classify classify){
        this.id = classify.id;
        this.name = classify.name;
        this.isOpen = classify.isOpen;
    }

    @Generated(hash = 538268575)
    public Classify(Long id, String name, boolean isOpen) {
        this.id = id;
        this.name = name;
        this.isOpen = isOpen;
    }

    @Generated(hash = 767880343)
    public Classify() {
    }

    public Classify(String name){
        this(null, name);
    }

    public Classify(Long id, String name){
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
        Classify classify = (Classify)o;
        return Objects.equals(this.id, classify.id)&&
                Objects.equals(this.name, classify.name);
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
        return this.id.compareTo(classify.id);
    }

    public boolean getIsOpen() {
        return this.isOpen;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }
}
