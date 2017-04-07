package pojos;

import java.util.List;

/**
 * Created by seyi on 1/16/17.
 */
public class Location {
    public long id;
    public String name;
    public boolean hidden;

    public static class State extends Location{
        public List<Axis> axises;
    }

    public static class Axis extends Location{
        public List<Area> areas;
    }

    public static class Area extends Location{
    }
}
