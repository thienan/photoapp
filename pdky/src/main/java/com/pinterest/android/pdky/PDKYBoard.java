package com.pinterest.android.pdky;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class PDKYBoard extends PDKYModel {

    private String uid;
    private String name;
    private String description;
    private PDKYUser creator;
    private Date createdAt;
    private Integer pinsCount;
    private Integer collaboratorsCount;
    private Integer followersCount;
    private String imageUrl;

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreator(PDKYUser creator) {
        this.creator = creator;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setPinsCount(Integer pinsCount) {
        this.pinsCount = pinsCount;
    }

    public void setCollaboratorsCount(Integer collaboratorsCount) {
        this.collaboratorsCount = collaboratorsCount;
    }

    public void setFollowersCount(Integer followersCount) {
        this.followersCount = followersCount;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public PDKYUser getCreator() {
        return creator;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Integer getPinsCount() {
        return pinsCount;
    }

    public Integer getCollaboratorsCount() {
        return collaboratorsCount;
    }

    public Integer getFollowersCount() {
        return followersCount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public static PDKYBoard makeBoard(Object obj) {
        PDKYBoard board = new PDKYBoard();
        try {
            if (obj instanceof JSONObject) {
                JSONObject dataObj = (JSONObject)obj;
                if (dataObj.has("id")) {
                    board.setUid(dataObj.getString("id"));
                }
                if (dataObj.has("name")) {
                    board.setName(dataObj.getString("name"));
                }
                if (dataObj.has("description")) {
                    board.setDescription(dataObj.getString("description"));
                }
                if (dataObj.has("creator")) {
                    PDKYUser.makeUser(dataObj.getJSONObject("creator"));
                }
                if (dataObj.has("created_at")) {
                    board.createdAt = Utils.getDateFormatter().parse(dataObj.getString("created_at"));
                }
                if (dataObj.has("counts")) {
                    JSONObject countsObj = dataObj.getJSONObject("counts");
                    if (countsObj.has("pins")) {
                        board.setPinsCount(countsObj.getInt("pins"));
                    }
                    if (countsObj.has("collaborators")) {
                        board.setCollaboratorsCount(countsObj.getInt("collaborators"));
                    }
                    if (countsObj.has("followers")) {
                        board.setFollowersCount(countsObj.getInt("followers"));
                    }
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
                                board.setImageUrl(iObj.getString("url"));
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Utils.loge("PDKY: PDKYBoard parse JSON error %s", e.getLocalizedMessage());
        } catch (ParseException e) {
            Utils.loge("PDKY: PDKYBoard parse error %s", e.getLocalizedMessage());
        }
        return board;
    }

    public static List<PDKYBoard> makeBoardList(Object obj) {
        List<PDKYBoard> boardList = new ArrayList<PDKYBoard>();
        try {
            if (obj instanceof JSONArray) {

                JSONArray jAarray = (JSONArray)obj;
                int size = jAarray.length();
                for (int i = 0; i < size; i++) {
                    JSONObject dataObj = jAarray.getJSONObject(i);
                    boardList.add(makeBoard(dataObj));
                }
            }
        } catch (JSONException e) {
            Utils.loge("PDKY: PDKYBoard parse JSON error %s", e.getLocalizedMessage());
        }
        return boardList;
    }
}
