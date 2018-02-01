package com.evan.remindme.settings.domain.model;

import com.evan.remindme.settings.SettingDisplay;
import com.evan.remindme.settings.SettingKey;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.Objects;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Keep;

/**
 * Created by IntelliJ IDEA
 * User: Evan
 * Date: 2018/1/30
 * Time: 下午6:14
 */
@Entity
public class Setting {

    @Id
    private Long id;

    private String title;

    private String value;

    private int display;

    public Setting(Setting setting){
        this.id = setting.id;
        this.title = setting.title;
        this.value = setting.value;
        this.display = setting.display;
    }

    @Keep
    @Generated(hash = 1913017900)
    public Setting(Long id, String title, String value, int display) {
        this.id = id;
        this.title = title;
        this.value = value;
        this.display = display;
    }

    @Generated(hash = 909716735)
    public Setting() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Setting setting = (Setting) o;
        return Objects.equals(id, setting.id) &&
                Objects.equals(title, setting.title) &&
                Objects.equals(value, setting.value)&&
                Objects.equals(display,setting.display);
    }

    @Override
    public String toString() {
        return "Setting{" +
                "title='" + title + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, title, value, display);
    }

    public int getDisplay() {
        return display;
    }

    public void setDisplay(int display) {
        this.display = display;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
