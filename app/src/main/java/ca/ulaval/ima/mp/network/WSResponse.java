package ca.ulaval.ima.mp.network;

import com.android.volley.Response;

public final class WSResponse<T> {
    private Response.Listener<T> mSuccess;
    private Response.ErrorListener mError;

    public WSResponse(Response.Listener<T> onSuccess, Response.ErrorListener onError) {
        this.mSuccess = onSuccess;
        this.mError = onError;
    }

    Response.Listener<T> onSuccess() {
        return this.mSuccess;
    }

    Response.ErrorListener onError() {
        return this.mError;
    }
}
