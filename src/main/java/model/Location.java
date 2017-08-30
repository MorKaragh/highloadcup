package model;

/**
 * Created by tookuk on 8/16/17.
 */
public class Location implements Jsonable {
    private String country;
    private int id;
    private String place;
    private String city;
    private int distance;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String toJson() {
        return "{\"country\":\""+country+"\",\"distance\":"+distance+",\"city\":\""+city+"\",\"place\":\""+place+"\",\"id\":"+id+"}";
    }

}
