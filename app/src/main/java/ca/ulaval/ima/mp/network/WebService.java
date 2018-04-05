package ca.ulaval.ima.mp.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import ca.ulaval.ima.mp.R;

public class WebService {
    private static String TAG = WebService.class.getSimpleName();
    private static String API_BASE_URL;
    private RequestQueue mRequestQueue;

    public WebService(Context context) {
        API_BASE_URL = context.getResources().getString(R.string.api_base_url);
        initRequestQueue(context);
    }

    private RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public void initRequestQueue(Context context) {
        if (mRequestQueue == null)
            mRequestQueue = Volley.newRequestQueue(context);
    }

    public void cancelAll() {
        this.getRequestQueue().cancelAll("");
    }

    public String GETRequest(String url, WSResponse<JSONObject> response) {
        String URL = API_BASE_URL + url;
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,
                URL, null, response.onSuccess(), response.onError());
        this.getRequestQueue().add(jsonRequest);
        return jsonRequest.getUrl();
    }

    public String POSTRequest(String url, WSResponse<JSONObject> response, JSONObject params) {
        String URL = API_BASE_URL + url;
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                URL, params, response.onSuccess(), response.onError());
        this.getRequestQueue().add(jsonRequest);
        return jsonRequest.getUrl();
    }
}
