package model;

/**
 * Created by tookuk on 8/20/17.
 */
public class UserVisit implements Jsonable {
    private String place;
    private int mark;
    private int visitedAt;

    public UserVisit(String place, int mark, int visitedAt) {
        this.place = place;
        this.mark = mark;
        this.visitedAt = visitedAt;
    }

    @Override
    public String toJson() {
        return "{\"mark\": "+mark+",\"visited_at\": "+visitedAt+",\"place\": \""+place+"\"}";
    }

    public int getVisitedAt() {
        return visitedAt;
    }
}
