package com.app.android.repeatedquestionidentifier.database;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "task")
public class TaskEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;//id is the primary key here which is auto generated because it is written right below @Primary Key

    private String question1,question2;
    private int priority,indicator;
    private Date checkedAt;

    @Ignore
    public TaskEntry(String question1,String question2, int priority, Date checkedAt,int indicator) {
        this.question1 = question1;
        this.question2 = question2;
        this.priority = priority;
        this.checkedAt = checkedAt;
        this.indicator = indicator;
    }

    public TaskEntry(int id, String question1,String question2, int priority, Date checkedAt,int indicator) {
        this.id = id;
        this.question1 = question1;
        this.question2 = question2;
        this.priority = priority;
        this.checkedAt = checkedAt;
        this.indicator = indicator;
    }

    //getters and setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion1() {
        return question1;
    }
    public void setQuestion1(String question1) {
        this.question1 = question1;
    }

    public String getQuestion2(){
        return question2;
    }
    public void setQuestion2(String question2) {
        this.question2 = question2;
    }

    public int getIndicator() {
        return indicator;
    }
    public void setIndicator(int indicator) {
        this.indicator = indicator;
    }

    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Date getCheckedAt() {
        return checkedAt;
    }
    public void setCheckedAt(Date checkedAt) { this.checkedAt = checkedAt; }
}