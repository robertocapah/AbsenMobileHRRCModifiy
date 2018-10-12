package absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.common;

import java.io.Serializable;

/**
 * Created by Robert on 9/3/2018.
 */

public class Event implements Serializable {

    public String email;
    public String name;
    public String location;
    public String from;
    public String to;
    public Boolean is_allday;
    public String timezone;

}
