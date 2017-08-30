package model;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by tookuk on 8/16/17.
 */
public class User implements Jsonable{
    private String gender;
    private long birthDate;
    private String lastName;
    private int id;
    private String firstName;
    private String email;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public long getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(long birthDate) {
        this.birthDate = birthDate;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String toJson() {
        return "{\"id\": "+id+",\"email\": \""+email+"\",\"first_name\": \""+firstName+"\"," +
                "\"last_name\": \""+lastName+"\",\"gender\": \""+gender+"\",\"birth_date\": "+birthDate+"}";
    }

    public static int calcAge(Long currentTimestamp, long birthDate) {
        //return (int) ((currentTimestamp - birthDate) / 60 / 60 / 24 / 365.25);
        return calculateAge(new Date(birthDate*1000),
                currentTimestamp != null ? new Date(currentTimestamp*1000) : null);
    }

    public static Integer calculateAge(final Date birthday, final Date currDate)
    {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        if (currDate != null){
            today.setTime(currDate);
        }

        dob.setTime(birthday);
        // include day of birth
        dob.add(Calendar.DAY_OF_MONTH, -1);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) <= dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }

    public int getAge(long time) {
        return (int) ((time - birthDate) / 60 / 60 / 24 / 365.25);
    }
}
