package model;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.util.StringUtil;

import java.util.List;

/**
 * Created by tookuk on 8/20/17.
 */
public class JsonableList implements Jsonable{
    private final List<? extends Jsonable> list;
    private final String name;

    public JsonableList(List<? extends Jsonable> list, String name){
        this.list = list;
        this.name = name;
    }

    @Override
    public String toJson() {
        String result = "{\"" + name + "\":[";
        for(Jsonable j : list){
            result += j.toJson() + ",";
        }
        return StringUtils.stripEnd(result,",") + "]}";
    }
}
