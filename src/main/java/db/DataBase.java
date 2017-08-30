package db;

import exceptions.PathException;
import exceptions.WrongParameterException;
import model.*;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Created by tookuk on 8/16/17.
 */
public class DataBase {

    private HashMap<Integer, Location> locationsIdIndex;
    private HashMap<Integer, User> usersIdIndex;
    private HashMap<Integer, Visit> visitsIdIndex;
    private HashMap<Integer,List<Visit>> userVisits;
    private HashMap<Integer,List<Visit>> locationVisits;

    private static Long currentTimestamp = null;

    public static Long getCurrentTimestamp() {
        return currentTimestamp;
    }

    public static void setCurrentTimestamp(Long currentTimestamp) {
        DataBase.currentTimestamp = currentTimestamp;
    }

    public DataBase() {
        this.locationsIdIndex = new HashMap<Integer, Location>();
        this.usersIdIndex = new HashMap<Integer, User>();
        this.visitsIdIndex = new HashMap<Integer, Visit>();
        this.userVisits = new HashMap<Integer, List<Visit>>();
        this.locationVisits = new HashMap<>();
    }

    public void addLocation(Location location){
        locationsIdIndex.put(location.getId(), location);
    }

    public void addUser(User user){
        usersIdIndex.put(user.getId(),user);
    }

    public void addVisit(Visit visit){
        visitsIdIndex.put(visit.getId(),visit);
        if (userVisits.get(visit.getUser()) != null){
            userVisits.get(visit.getUser()).add(visit);
        } else {
            ArrayList<Visit> visits = new ArrayList<Visit>();
            visits.add(visit);
            userVisits.put(visit.getUser(),visits);
        }
        if (locationVisits.get(visit.getLocationId()) != null){
            locationVisits.get(visit.getLocationId()).add(visit);
        } else {
            ArrayList<Visit> visits = new ArrayList<Visit>();
            visits.add(visit);
            locationVisits.put(visit.getLocationId(),visits);
        }
    }


    public User getUserById(int param) {
        if (usersIdIndex.get(param) != null){
            return usersIdIndex.get(param);
        } else {
            return null;
        }
    }

    public Visit getVisitById(int id){
        if (visitsIdIndex.get(id) != null){
            return visitsIdIndex.get(id);
        }
        return null;
    }

    public Location getLocationById(int id){
        if (locationsIdIndex.get(id) != null){
            return locationsIdIndex.get(id);
        }
        return null;
    }

    public JsonableList getVisitsOfUser(String userId, String fromDate, String toDate, String country, String toDistance) {

        if(StringUtils.isNotBlank(fromDate) && !StringUtils.isNumeric(fromDate)){
            throw new WrongParameterException();
        }
        if(StringUtils.isNotBlank(toDate) && !StringUtils.isNumeric(toDate)){
            throw new WrongParameterException();
        }
        if(StringUtils.isNotBlank(toDistance) && !StringUtils.isNumeric(toDistance)){
            throw new WrongParameterException();
        }

        if(usersIdIndex.get(Integer.parseInt(userId)) == null){
            return null;
        }
        List<Visit> visits = userVisits.get(Integer.parseInt(userId));

        ArrayList<UserVisit> selected = new ArrayList<>();
        if (visits != null) {
            for (Visit v : visits) {
                if ( (fromDate == null || v.getVisitedAt() > Integer.parseInt(fromDate))
                        && (StringUtils.isBlank(toDate) || v.getVisitedAt() < Integer.parseInt(toDate))
                        && (StringUtils.isBlank(country) || country.equals(v.getCountry()))
                        && (StringUtils.isBlank(country) || country.equals(v.getCountry()))
                        && (StringUtils.isBlank(toDistance) || Integer.parseInt(toDistance) > v.getDistance())
                        ){
                    selected.add(new UserVisit(locationsIdIndex.get(v.getLocationId()).getPlace(),v.getMark(),v.getVisitedAt()));
                }
            }
            selected.sort(Comparator.comparingInt(UserVisit::getVisitedAt));
        }
        return new JsonableList(selected,"visits");
    }

    public Jsonable getLocationAvg(String locationIdStr, String fromDate, String toDate, String fromAge, String toAge, String gender) {

        if(gender != null && !("f".equals(gender) || "m".equals(gender))){
            throw new WrongParameterException();
        }
        if(StringUtils.isNotBlank(fromAge) && !StringUtils.isNumeric(fromAge)){
            throw new WrongParameterException();
        }
        if(StringUtils.isNotBlank(fromDate) && !StringUtils.isNumeric(fromDate)){
            throw new WrongParameterException();
        }
        if(StringUtils.isNotBlank(toDate) && !StringUtils.isNumeric(toDate)){
            throw new WrongParameterException();
        }
        if(StringUtils.isNotBlank(toAge) && !StringUtils.isNumeric(toAge)){
            throw new WrongParameterException();
        }

        Double ttl = 0D;
        int cnt = 0;
        if(locationsIdIndex.get(Integer.parseInt(locationIdStr)) == null){
            return null;
        }
        List<Visit> visits = locationVisits.get(Integer.parseInt(locationIdStr));
        if(visits != null) {
            for (Visit v : visits) {
                if ( (StringUtils.isBlank(fromDate) || Integer.parseInt(fromDate) < v.getVisitedAt())
                        && (StringUtils.isBlank(toDate) || Integer.parseInt(toDate) > v.getVisitedAt())
                        && (StringUtils.isBlank(toAge) || Integer.parseInt(toAge) > v.getAge())
                        && (StringUtils.isBlank(fromAge) || Integer.parseInt(fromAge) <= v.getAge())
                        && (StringUtils.isBlank(gender) || gender.equals(v.getGender()))) {
                    cnt++;
                    ttl += v.getMark();
                }
            }
        }
        final double avg;
        if(cnt == 0){
            avg = 0;
        } else {
            avg = ttl / cnt;
        }
        return () -> "{\"avg\": "+round(avg)+"}";
    }

    private int getUserAge(int user) {
        User u = usersIdIndex.get(user);
        return u.getAge(currentTimestamp);
    }

    private static double round(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(5, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void updateUser(int i, JSONObject obj) {
        User user = usersIdIndex.get(i);
        Integer birthDate = null;
        String firstName = null;
        String lastName = null;
        String email = null;
        String gender = null;

        if(user == null){
            throw new PathException();
        }
        if (obj.has("birth_date")) {
            birthDate = obj.getInt("birth_date");
        }
        if (obj.has("first_name")) {
            firstName = obj.getString("first_name");
        }
        if (obj.has("last_name")) {
            lastName = obj.getString("last_name");
        }
        if (obj.has("email")) {
            email = obj.getString("email");
        }
        if (obj.has("gender")) {
            if(!("f".equals(obj.getString("gender"))||"m".equals(obj.getString("gender")))){
                throw new WrongParameterException();
            }
            gender = obj.getString("gender");
        }

        if (birthDate != null) {
            user.setBirthDate(birthDate);
            if (userVisits.get(i) != null && !userVisits.get(i).isEmpty()){
                for(Visit v : userVisits.get(i)){
                    v.setAge(User.calcAge(currentTimestamp,user.getBirthDate()));
                    v.setBirthdate(user.getBirthDate());
                }
            }
        }
        if (StringUtils.isNotBlank(firstName)){
            user.setFirstName(firstName);
        }
        if (StringUtils.isNotBlank(lastName)){
            user.setLastName(lastName);
        }
        if (StringUtils.isNotBlank(email)){
            user.setEmail(email);
        }
        if(gender != null){
            user.setGender(gender);
            if (userVisits.get(i) != null && !userVisits.get(i).isEmpty()){
                for(Visit v : userVisits.get(i)){
                    v.setGender(user.getGender());
                }
            }
        }

    }

    public void updateVisit(int i, JSONObject obj) {
        Visit visit = visitsIdIndex.get(i);
        if (visit == null){
            throw new PathException();
        }

        Integer visitedAt = null;
        Integer user = null;
        Integer mark = null;
        Integer location = null;

        if (obj.has("visited_at")) {
            visitedAt = obj.getInt("visited_at");
        }

        if (obj.has("user")) {
            user = obj.getInt("user");
        }

        if (obj.has("mark")) {
            mark = obj.getInt("mark");
        }

        if (obj.has("location")) {
            location = obj.getInt("location");
        }

        if (visitedAt != null) {
            visit.setVisitedAt(visitedAt);
        }
        if (user != null) {
            int newUserId = obj.getInt("user");
            if(userVisits.get(visit.getUser()) != null){
                userVisits.get(visit.getUser()).removeIf(v -> v.getUser() != newUserId && v.getId()==visit.getId());
            }
            if(userVisits.get(newUserId) == null){
                ArrayList<Visit> v = new ArrayList<>();
                v.add(visit);
                userVisits.put(newUserId, v);
            } else {
                userVisits.get(newUserId).add(visit);
            }

            visit.setUser(newUserId);

            User userById = getUserById(visit.getUser());

            visit.setGender(userById.getGender());
            visit.setAge(User.calcAge(currentTimestamp, userById.getBirthDate()));
            visit.setBirthdate(userById.getBirthDate());
        }
        if (mark != null) {
            visit.setMark(mark);
        }
        if (location != null) {
            int newLocationId = location;

            if(locationVisits.get(visit.getLocationId()) != null){
                locationVisits.get(visit.getLocationId()).removeIf(v -> v.getLocationId() != newLocationId
                        && v.getId()==visit.getId());
            }
            if(locationVisits.get(newLocationId) == null){
                ArrayList<Visit> v = new ArrayList<>();
                v.add(visit);
                locationVisits.put(newLocationId, v);
            } else {
                locationVisits.get(newLocationId).add(visit);
            }
            visit.setLocationId(newLocationId);

            Location locationById = getLocationById(visit.getLocationId());
            visit.setCountry(locationById.getCountry());
            visit.setDistance(locationById.getDistance());
        }

    }

    public void updateLocation(int i, JSONObject obj) {
        Location l = locationsIdIndex.get(i);
        if(l == null){
            throw new PathException();
        }
        String place = null;
        String city = null;
        String country = null;
        Integer distance = null;

        if (obj.has("place")) {
            place = obj.getString("place");
        }
        if (obj.has("city")) {
            city = obj.getString("city");
        }
        if (obj.has("country")) {
            country = obj.getString("country");
        }
        if (obj.has("distance")) {
            distance = obj.getInt("distance");
        }

        if (StringUtils.isNotBlank(place)){
            l.setPlace(place);
        }
        if (StringUtils.isNotBlank(city)){
            l.setCity(city);
        }
        if (StringUtils.isNotBlank(country)){
            l.setCountry(country);
            if(locationVisits.get(i) != null){
                for (Visit v : locationVisits.get(i)){
                    v.setCountry(l.getCountry());
                }
            }
        }
        if (distance != null){
            l.setDistance(distance);
            if(locationVisits.get(i) != null){
                for (Visit v : locationVisits.get(i)){
                    v.setDistance(l.getDistance());
                }
            }
        }

    }
}
