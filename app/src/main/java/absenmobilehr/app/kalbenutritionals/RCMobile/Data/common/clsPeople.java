package absenmobilehr.app.kalbenutritionals.RCMobile.Data.common;

import android.graphics.drawable.Drawable;

/**
 * Created by Robert on 9/3/2018.
 */

public class clsPeople {

    public int image;
    public Drawable imageDrw;
    public String name;
    public String email;
    public boolean section = false;

    public clsPeople() {
    }

    public clsPeople(String name, boolean section) {
        this.name = name;
        this.section = section;
    }

}
