package com.pinterest.android.pdky;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;


import java.util.HashMap;
import java.util.Map;

public class PDKYCallback implements Response.Listener<JSONObject>, Response.ErrorListener {

    private int _statusCode;
    private Map<String, String> _responseHeaders;
    private String _path;
    private HashMap<String, String> _params;

    @Override
    public void onResponse(JSONObject response) {
        try {
            onSuccess(response);
        } catch (Exception e) {
        }
    }

    public void onErrorResponse(VolleyError error) {
        onFailure(new PDKYException(error));
    }

    public void onSuccess(JSONObject response) {
        final PDKYResponse apiResponse = new PDKYResponse(response);
        apiResponse.setStatusCode(_statusCode);
        apiResponse.setPath(_path);
        apiResponse.setParams(_params);
        onSuccess(apiResponse);
    }

    public void onSuccess(PDKYResponse response) {
    }

    public void onFailure(PDKYException exception) {
    }

    public void setResponseHeaders(Map<String, String> map) {
        _responseHeaders = map;
    }

    public void setStatusCode(int code) {
        _statusCode = code;
    }

    public void setPath(String path) {
        _path = path;
    }

    public void setParams(HashMap<String, String> params) {
        _params = params;
    }
}
