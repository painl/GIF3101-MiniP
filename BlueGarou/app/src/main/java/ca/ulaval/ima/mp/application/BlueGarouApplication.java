package ca.ulaval.ima.mp.application;

import android.app.Application;

import ca.ulaval.ima.mp.network.WebService;

public class BlueGarouApplication extends Application {
    private static BlueGarouApplication mInstance;
    private WebService mWebService;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mInstance.mWebService = new WebService(mInstance.getApplicationContext());
    }

    public synchronized WebService getWebService()
    {
        return this.mWebService;
    }

    public static synchronized BlueGarouApplication getInstance() {
        mInstance.mWebService.initRequestQueue(mInstance.getApplicationContext());
        return mInstance;
    }
}
