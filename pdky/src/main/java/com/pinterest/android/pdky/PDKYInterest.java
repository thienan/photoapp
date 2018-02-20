package com.pinterest.android.pdky;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by sahilm on 6/3/15.
 */
public class PDKYInterest extends PDKYModel {

    private String uid;
    private String name;


    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public static PDKYInterest makeInterest(Object obj) {
        PDKYInterest interest = new PDKYInterest();
        try {
            if (obj instanceof JSONObject) {
                JSONObject dataObj = (JSONObject)obj;
                if (dataObj.has("id")) {
                    interest.setUid(dataObj.getString("id"));
                }
                if (dataObj.has("name")) {
                    interest.setName(dataObj.getString("name"));
                }
            }
        } catch (JSONException e) {
            Utils.loge("PDKY: PDKYInterest parse JSON error %s", e.getLocalizedMessage());
        }
        return interest;
    }

    public static List<PDKYInterest> makeInterestList(Object obj) {
        List<PDKYInterest> interestList = new ArrayList<PDKYInterest>();
        try {
            if (obj instanceof JSONArray) {

                JSONArray jAarray = (JSONArray)obj;
                int size = jAarray.length();
                for (int i = 0; i < size; i++) {
                    JSONObject dataObj = jAarray.getJSONObject(i);
                    interestList.add(makeInterest(dataObj));
                }
            }
        } catch (JSONException e) {
            Utils.loge("PDKY: PDKYInterst parse JSON error %s", e.getLocalizedMessage());
        }
        return interestList;
    }

}
