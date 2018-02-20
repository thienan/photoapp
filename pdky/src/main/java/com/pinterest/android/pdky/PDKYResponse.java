package com.pinterest.android.pdky;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.List;

public class PDKYResponse {


    protected Object _data;
    protected String _path;
    protected HashMap<String, String> _params;
    protected String _cursor = null;
    protected int _statusCode = -1;

    public PDKYResponse(JSONObject obj) {
        if (obj == null)
            return;

        if (obj.has("data")) {
            try {
                setData(obj.get("data"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (obj.has("page")) {
            try {
                JSONObject pageObj = obj.getJSONObject("page");
                if (pageObj.has("cursor")) {
                    _cursor = pageObj.getString("cursor");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isValid() {
        return _data != null;
    }
    public PDKYPin getPin() {
        return PDKYPin.makePin(_data);
    }

    public List<PDKYPin> getPinList() {
        return PDKYPin.makePinList(_data);
    }

    public PDKYUser getUser() {
        return PDKYUser.makeUser(_data);
    }

    public List<PDKYUser> getUserList() {
        return PDKYUser.makeUserList(_data);
    }

    public PDKYBoard getBoard() {
        return PDKYBoard.makeBoard(_data);
    }

    public List<PDKYBoard> getBoardList() {
        return PDKYBoard.makeBoardList(_data);
    }

    public PDKYInterest getInterest() {
        return PDKYInterest.makeInterest(_data);
    }

    public List<PDKYInterest> getInterestList() {
        return PDKYInterest.makeInterestList(_data);
    }

    public void loadNext(PDKYCallback callback) {
        _params.put(PDKYClient.PDKY_QUERY_PARAM_CURSOR, _cursor);
        PDKYClient.getInstance().getPath(_path, _params, callback);
    }

    public boolean hasNext() {
        return _cursor != null && _cursor.length() > 0 && !_cursor.equalsIgnoreCase("null");
    }

    //Setter & Getters

    public void setData(Object data) {
        _data = data;
    }

    public Object getData() {
        return _data;
    }

    public final void setStatusCode(int code) {
        _statusCode = code;
    }

    public final int getStatusCode() {
        return _statusCode;
    }

    public void setPath(String path) { _path = path; }

    public void setParams(HashMap<String, String> map) { _params = map; }
}
