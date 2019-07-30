package com.nic.MITank.api;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by AchanthiSundar on 28-12-2018.
 */
public class ServerResponse {

    private String api; // To find which API Calls
    private String response; // This is string response

    public ServerResponse(String request, String response) {
        this.api = request;
        this.response = response;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String url) {
        this.api = url;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public JSONObject getJsonResponse()throws JSONException {
        return new JSONObject(this.response);
    }
}
