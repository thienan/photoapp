package org.quantum.silverphoto.data.local;

import io.realm.RealmObject;

/**
 * Created by SilverLive Team on 16/08/17.
 */

public class UploadHistoryRealmModel extends RealmObject{

    String name;
    String pathname;
    String datetime;
    String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPathname() {
        return pathname;
    }

    public void setPathname(String pathname) {
        this.pathname = pathname;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
