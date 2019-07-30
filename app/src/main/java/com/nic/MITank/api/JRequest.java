package com.nic.MITank.api;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by AchanthiSundar on 28-12-2018.
 */

public class JRequest<T> extends com.android.volley.toolbox.JsonRequest<T> {

    Response.ErrorListener errorListener;
    private Response.Listener<T> listener;
    private JSONArray mParams;
    private JSONObject jsonObject;
    private List mParamsJSON;
    private int request;
    private int mMethod;
    private String mUrl;
    private String api;
    private String type;

    public JRequest(String api, int method, String url, JSONArray jsonRequest, String type, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener, errorListener);
        this.api = api;
        this.mMethod = method;
        this.mUrl = url;
        this.listener = listener;
        this.errorListener = errorListener;
        this.mParams = jsonRequest;
        this.type = type;
        this.errorListener = errorListener;
    }

    public JRequest(String api, int method, String url, JSONObject jsonRequest, String type, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener, errorListener);
        this.api = api;
        this.mMethod = method;
        this.mUrl = url;
        this.listener = listener;
        this.errorListener = errorListener;
        this.jsonObject = jsonRequest;
        this.type = type;
        this.errorListener = errorListener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Accept","application/json");
        headers.put("Content-Type", "application/json");
        return headers;
    }

    public interface ServerResponseListener {
        void OnResponse(ServerResponse serverResponse);

        void OnError(VolleyError volleyError);
    }

    public void setTimeout() {
        int socketTimeout = 30000;// 30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        setRetryPolicy(policy);
    }


    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            if ("cache".equalsIgnoreCase(type))
                return Response.success(new ServerResponse(this.api, jsonString),
                        VolleyUtils.parseIgnoreCacheHeaders(response));
            else
                return Response.success(new ServerResponse(this.api, jsonString),
                        HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T t) {
        listener.onResponse(t);

    }
}
