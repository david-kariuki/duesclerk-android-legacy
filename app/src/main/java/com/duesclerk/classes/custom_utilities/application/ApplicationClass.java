package com.duesclerk.classes.custom_utilities.application;


import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import androidx.multidex.MultiDex;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ApplicationClass extends Application {

    @SuppressLint("StaticFieldLeak")
    private static ApplicationClass mInstance;
    public static final String TAG = ApplicationClass.class.getSimpleName();
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private RequestQueue customRequestQueue;
    private Request.Priority mPriority;

    /**
     * Function to get application context
     *
     * @return context
     */
    public static Context getAppContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mContext = getApplicationContext();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * Function to add to request queue
     *
     * @param customRequest - Request
     */
    public <T> void addToRequestQueue(Request<T> customRequest) {
        // Set the default tag if tag is empty
        customRequest.setTag(TAG);
        getCustomRequestQueue().add(customRequest);
    }

    /**
     * @return The Custom_Volley Custom_Request queue
     */
    public RequestQueue getCustomRequestQueue() {
        if (customRequestQueue == null) {
            customRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return customRequestQueue;
    }

    /**
     * Function to add to request queue
     *
     * @param customRequest - Request
     * @param tag           - Request tag
     */
    public <T> void addToRequestQueue(Request<T> customRequest, String tag) {
        // Set the default tag if tag is empty
        customRequest.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getCustomRequestQueue().add(customRequest);
    }

    /**
     * Function to cancel pending request
     *
     * @param tag - Request tag
     */
    public void cancelPendingRequests(Object tag) {
        if (customRequestQueue != null) {
            customRequestQueue.cancelAll(tag);
        }
    }

    /**
     * Function to get request priority
     */
    public Request.Priority getPriority() {
        // If you didn't use the setPriority method,
        // the priority is automatically set to NORMAL
        return mPriority != null ? mPriority : Request.Priority.NORMAL;
    }

    /**
     * Function to set request priority
     *
     * @param priority - Request priority
     */
    public void setPriority(Request.Priority priority) {
        mPriority = priority;
    }

    /**
     * Function to delete Volley Cache for single url
     *
     * @param url - Request url
     */
    public void deleteUrlVolleyCache(String url) {
        getClassInstance().getCustomRequestQueue().getCache().remove(url);
    }

    /**
     * @return ApplicationClass singleton instance
     */
    public static synchronized ApplicationClass getClassInstance() {
        return mInstance;
    }

    /**
     * Function to delete Volley Cache for all urls
     */
    public void deleteAllUrlsVolleyCache() {
        getClassInstance().getCustomRequestQueue().getCache().clear();
    }

}
