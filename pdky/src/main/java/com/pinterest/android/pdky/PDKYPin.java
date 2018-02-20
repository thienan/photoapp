package com.pinterest.android.pdky;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class PDKYPin extends PDKYModel {

    private String uid;
    private PDKYBoard board;
    private PDKYUser user;
    private String link;
    private String note;
    private String metadata;
    private Date createdAt;
    private Integer likeCount;
    private Integer commentCount;
    private Integer repinCount;
    private String imageUrl;

    public static PDKYPin makePin(Object obj) {
        PDKYPin pin = new PDKYPin();
        try {
            if (obj instanceof JSONObject) {
                JSONObject dataObj = (JSONObject)obj;
                if (dataObj.has("id")) {
                    pin.setUid(dataObj.getString("id"));
                }
                if (dataObj.has("link")) {
                    pin.setLink(dataObj.getString("link"));
                }
                if (dataObj.has("note")) {
                    pin.setNote(dataObj.getString("note"));
                }
                if (dataObj.has("metadata")) {
                   pin.setMetadata(dataObj.get("metadata").toString());
                }
                if (dataObj.has("counts")) {
                    JSONObject countsObj = dataObj.getJSONObject("counts");
                    if (countsObj.has("likes")) {
                        pin.setLikeCount(countsObj.getInt("likes"));
                    }
                    if (countsObj.has("comments")) {
                        pin.setCommentCount(countsObj.getInt("comments"));
                    }
                    if (countsObj.has("repins")) {
                        pin.setRepinCount(countsObj.getInt("repins"));
                    }
                }
                if (dataObj.has("metadata")) {
                    pin.setMetadata(dataObj.getString("metadata"));
                }
                if (dataObj.has("creator")) {
                    PDKYUser.makeUser(dataObj.getJSONObject("creator"));
                }
                if (dataObj.has("board")) {
                    PDKYBoard.makeBoard(dataObj.getJSONObject("board"));
                }
                if (dataObj.has("created_at")) {
                    pin.setCreatedAt(
                        Utils.getDateFormatter().parse(dataObj.getString("created_at")));
                }
                if (dataObj.has("image")) {
                    JSONObject imageObj = dataObj.getJSONObject("image");
                    Iterator<String> keys = imageObj.keys();

                    //TODO: for now we'll have just one image map. We will change this logic after appathon
                    while(keys.hasNext()) {
                        String key = keys.next();
                        if (imageObj.get(key) instanceof JSONObject) {
                            JSONObject iObj = imageObj.getJSONObject(key);
                            if (iObj.has("url")) {
                                pin.setImageUrl(iObj.getString("url"));
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Utils.loge("PDKY: PDKYPin JSON parse error %s", e.getLocalizedMessage());
        } catch (ParseException e) {
            Utils.loge("PDKY: PDKYPin Text parse error %s", e.getLocalizedMessage());
        }
        return pin;
    }

    public static List<PDKYPin> makePinList(Object obj) {
        List<PDKYPin> pinList = new ArrayList<PDKYPin>();
        try {
            if (obj instanceof JSONArray) {

                JSONArray jAarray = (JSONArray)obj;
                int size = jAarray.length();
                for (int i = 0; i < size; i++) {
                    JSONObject dataObj = jAarray.getJSONObject(i);
                    pinList.add(makePin(dataObj));
                }
            }
        } catch (JSONException e) {
            Utils.loge("PDKY: PDKYPinList parse JSON error %s", e.getLocalizedMessage());
        }
        return pinList;
    }

    @Override
    public String getUid() {
        return uid;
    }

    public PDKYBoard getBoard() {
        return board;
    }

    public PDKYUser getUser() {
        return user;
    }

    public String getLink() {
        return link;
    }

    public String getNote() {
        return note;
    }

    public String getMetadata() {
        return metadata;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public Integer getRepinCount() {
        return repinCount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setUid(String uid) {

        this.uid = uid;
    }

    public void setBoard(PDKYBoard board) {
        this.board = board;
    }

    public void setUser(PDKYUser user) {
        this.user = user;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public void setRepinCount(Integer repinCount) {
        this.repinCount = repinCount;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
