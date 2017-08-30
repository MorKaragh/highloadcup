package model;

import db.DataBase;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by tookuk on 8/16/17.
 */
public class Visit implements Jsonable {
    private int visitedAt;
    private int locationId;
    private int id;
    private int user;
    private int mark;
    private int age;
    private String gender;
    private String country;
    private int distance;
    private long birthdate;

    public int getVisitedAt() {
        return visitedAt;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setVisitedAt(int visitedAt) {
        this.visitedAt = visitedAt;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public String toJson() {
        return "{\"visited_at\":"+visitedAt+",\"location\":"+locationId+",\"id\":"+id+",\"user\":"+user+",\"mark\":"+mark+"}";
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public long getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(long birthdate) {
        this.birthdate = birthdate;
    }

    public boolean visitedToAge(int i) {
        return false;
    }

    public boolean visitedFromAge(int i) {
        return false;
    }


}
