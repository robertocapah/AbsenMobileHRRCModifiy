package absenmobilehr.app.kalbenutritionals.RCMobile.Data.common;

import android.graphics.drawable.Drawable;

/**
 * Created by Robert on 9/3/2018.
 */

public class Inbox {
    public int id;
    public Integer image = null;
    public Drawable imageDrw;
    public String from;
    public String email;
    public String message;
    public String date;
    public int color = -1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }

    public Drawable getImageDrw() {
        return imageDrw;
    }

    public void setImageDrw(Drawable imageDrw) {
        this.imageDrw = imageDrw;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
