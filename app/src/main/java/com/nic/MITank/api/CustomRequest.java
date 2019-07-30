package com.nic.MITank.api;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by AchanthiSundar on 28-12-2018.
 */

public class CustomRequest<T> extends Request<T> { //implements Response.Listener<T>, Response.ErrorListener{
    Response.ErrorListener errorListener;
    private Response.Listener<T> listener;
    private Map<String, String> mParams;
    private int request;
    private int mMethod;
    private String mUrl;
    private String api;
    private String type;


    public CustomRequest(int forResponse, int method, String url, Map<String, String> params,
                         Response.Listener<T> reponseListener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.request = forResponse;
        this.mMethod = method;
        this.mUrl = url;
        this.mParams = params;
        this.listener = reponseListener;
        this.errorListener = errorListener;


    }


    public CustomRequest(String api, int method, String url, Map<String, String> params, String type,
                         Response.Listener<T> reponseListener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.api = api;
        this.mMethod = method;
        this.mUrl = url;
        this.mParams = params;
        this.listener = reponseListener;
        this.errorListener = errorListener;

        this.type = type;
    }


    public void setTimeout() {
        int socketTimeout = 30000;// 30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        setRetryPolicy(policy);
    }

    public CustomRequest(int method, String url, Map<String, String> params, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mMethod = method;
        this.mUrl = url;
        this.errorListener = errorListener;
        this.mParams = params;
    }


    @Override
    public String getUrl() {
        if (mMethod == Method.GET) {
            try {
                StringBuilder stringBuilder = new StringBuilder(mUrl);
                Iterator<Map.Entry<String, String>> iterator = mParams.entrySet().iterator();
                int i = 1;
                while (iterator.hasNext()) {
                    Map.Entry<String, String> entry = iterator.next();
                    if (i == 1) {
                        stringBuilder.append("?" + entry.getKey() + "=" + entry.getValue());
                    } else {
                        stringBuilder.append("&" + entry.getKey() + "=" + entry.getValue());
                    }
                    iterator.remove(); // avoids a ConcurrentModificationException
                    i++;
                }
                mUrl = stringBuilder.toString();
                try {
                    mUrl = URLEncoder.encode(mUrl, "UTF-8");
                } catch (UnsupportedEncodingException e) {

                }
            } catch (NullPointerException e) {

            }
        }
        return mUrl;
    }

    @Override
    protected Map<String, String> getParams()
            throws AuthFailureError {
        return mParams;
    }

    private Map<String, String> checkParams(Map<String, String> map) {
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> pairs = it.next();
            if (pairs.getValue() == null) {
                map.put(pairs.getKey(), "");
            }
        }
        return map;
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
