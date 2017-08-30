import db.DataBase;
import model.Location;
import model.User;
import model.Visit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by tookuk on 8/14/17.
 */
public class FileLoader {
    public static DataBase load() throws IOException {
        DataBase db = new DataBase();

        List<InputStream> locationStreams = new ArrayList<>();
        List<InputStream> userStreams = new ArrayList<>();
        List<InputStream> visitStreams = new ArrayList<>();

        String pathToZip = Main.test ? "/home/tookuk/go/highloadcup_tester/FULL/data/data.zip" : "/tmp/data/data.zip";
        try (ZipFile zipFile = new ZipFile(new File(pathToZip))) {

            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                InputStream is = zipFile.getInputStream(entry);
                if (entry.getName().contains("locations")) {
                    locationStreams.add(is);
                } else if (entry.getName().contains("users")) {
                    userStreams.add(is);
                } else if (entry.getName().contains("visits")) {
                    visitStreams.add(is);
                } else if (entry.getName().contains("options") && Main.test) {
                    DataBase.setCurrentTimestamp(Long.parseLong(new BufferedReader(new InputStreamReader(is)).readLine()));
                }
            }

            locationStreams.forEach(f -> {
                try {
                    processLocations(f, db);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            userStreams.forEach(f -> {
                try {
                    processUsers(f, db);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            visitStreams.forEach(f -> {
                try {
                    processVisits(f, db);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        }

        return db;
    }

    private static void processVisits(InputStream f, DataBase db) throws IOException {
        JSONObject jsonObject = new JSONObject(IOUtils.toString(f));
        JSONArray locations = (JSONArray) jsonObject.get("visits");
        for (Object o : locations){
            JSONObject obj = (JSONObject) o;
            Visit visit = parseVisit(db, obj);
            db.addVisit(visit);
        }
    }

    public static Visit parseVisit(DataBase db, JSONObject obj) {
        Visit visit = new Visit();

        visit.setVisitedAt(obj.getInt("visited_at"));
        visit.setId(obj.getInt("id"));
        visit.setLocationId(obj.getInt("location"));
        visit.setUser(obj.getInt("user"));
        User userById = db.getUserById(visit.getUser());
        visit.setMark(obj.getInt("mark"));
        visit.setGender(userById.getGender());
        Location locationById = db.getLocationById(visit.getLocationId());
        visit.setCountry(locationById.getCountry());
        visit.setDistance(locationById.getDistance());
        visit.setAge(User.calcAge(DataBase.getCurrentTimestamp(), userById.getBirthDate()));
        visit.setBirthdate(userById.getBirthDate());
        return visit;
    }

    private static void processUsers(InputStream f, DataBase db) throws IOException {
        JSONObject jsonObject = new JSONObject(IOUtils.toString(f));
        JSONArray locations = (JSONArray) jsonObject.get("users");
        for (Object o : locations){
            JSONObject obj = (JSONObject) o;
            User user = parseUser(obj);
            db.addUser(user);
        }
    }

    public static User parseUser(JSONObject obj) {
        User user = new User();
        user.setId(obj.getInt("id"));
        user.setBirthDate(obj.getInt("birth_date"));
        user.setFirstName(obj.getString("first_name"));
        user.setLastName(obj.getString("last_name"));
        user.setEmail(obj.getString("email"));
        user.setGender(obj.getString("gender"));
        return user;
    }

    private static void processLocations(InputStream f, DataBase db) throws IOException {
        JSONObject jsonObject = new JSONObject(IOUtils.toString(f));
        JSONArray locations = (JSONArray) jsonObject.get("locations");
        for (Object o : locations){
            JSONObject obj = (JSONObject) o;
            Location l = parseLocation(obj);
            db.addLocation(l);
        }
    }

    public static Location parseLocation(JSONObject obj) {
        Location l = new Location();
        l.setId(obj.getInt("id"));
        l.setPlace(obj.getString("place"));
        l.setCity(obj.getString("city"));
        l.setCountry(obj.getString("country"));
        l.setDistance(obj.getInt("distance"));
        return l;
    }

}
