package com.duesclerk.classes.network;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.widget.ProgressBar;

@SuppressLint("StaticFieldLeak")
public class UploadMedia {

    private final String KEY_PICTURE_UPLOADED = "PictureUploaded";
    private ProgressBar progressBar;
    private Context mContext;
    private String backendUrl;
    private String networkTag;

    public UploadMedia(Activity activity){
        this.mContext = activity.getApplicationContext();
        progressBar = new ProgressBar(activity);
    }

    public void setProgressBar(ProgressBar progressBar){
        this.progressBar = progressBar;
    }

    public void setBackendUrl(String url) {
        this.backendUrl = url;
    }

    public void setNetworkTag(String networkTag) {
        this.networkTag = networkTag;
    }


    /*
      Function to upload picture
      @param activity - to access function getBitmap
     * @param pictureUri - to get BitMap
     * @throws IOException - Exception
     */
/*

    public HashMap<String, String> UploadPicture(Activity activity, Uri pictureUri, String pictureType) {
        HashMap<String, String> uploadResponse = new HashMap<>();

        if (!DataUtils.isEmptyString(backendUrl) && !DataUtils.isEmptyString(networkTag)) {
            if (InternetConnectivity.isConnectedToAnyNetwork(mContext)) {
                if (InternetConnectivity.isConnectionFast(mContext)) {
                    // Get bitmap from URI
                    Bitmap[] bitmap = {null};
                    try {
                        bitmap[0] = MediaStore.Images.Media.getBitmap(activity.getContentResolver(),
                                pictureUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, this.backendUrl,
                            response -> {
                        String profilePictureId, profilePictureUrl, coverPictureId, coverPictureUrl;
                                try {
                                    JSONObject jsonObject = new JSONObject(new String(response.data));
                                    boolean error = jsonObject.getBoolean(VolleyUtils.KEY_ERROR);
                                    if (!error) {

                                        // Cover picture
                                        if (pictureType.equals(ImagePickerActivity.TYPE_COVER_PICTURE)){
                                            coverPictureId = jsonObject.getString(UserAccountUtils.KEY_PROFILE_PICTURE_ID);
                                            coverPictureUrl = jsonObject.getString(UserAccountUtils.KEY_PROFILE_PICTURE_URL);

                                            if (!DataUtils.isEmptyString(coverPictureId) && !DataUtils.isEmptyString(coverPictureUrl)){
                                                uploadResponse.put(KEY_PICTURE_UPLOADED, Boolean.toString(true));
                                                uploadResponse.put(UserAccountUtils.KEY_COVER_PICTURE_ID, coverPictureId);
                                                uploadResponse.put(UserAccountUtils.KEY_COVER_PICTURE_URL, coverPictureUrl);
                                            }
                                        }

                                        // Profile picture
                                        if (pictureType.equals(ImagePickerActivity.TYPE_PROFILE_PICTURE)){
                                            profilePictureId = jsonObject.getString(UserAccountUtils.KEY_PROFILE_PICTURE_ID);
                                            profilePictureUrl = jsonObject.getString(UserAccountUtils.KEY_PROFILE_PICTURE_URL);

                                            if (!DataUtils.isEmptyString(profilePictureId) && !DataUtils.isEmptyString(profilePictureUrl)){
                                                uploadResponse.put(KEY_PICTURE_UPLOADED, Boolean.toString(true));
                                                uploadResponse.put(UserAccountUtils.KEY_PROFILE_PICTURE_ID, profilePictureId);
                                                uploadResponse.put(UserAccountUtils.KEY_PROFILE_PICTURE_URL, profilePictureUrl);
                                            }
                                        }

                                        Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Error uploading pictures
                                        uploadResponse.put(KEY_PICTURE_UPLOADED, Boolean.toString(false));
                                        // Cancel Pending Request
                                        ApplicationClass.getClassInstance().cancelPendingRequests(networkTag);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            },
                            error -> {
                                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
                                Log.e("GotError", "" + error.getMessage());
                            }) {


                        @Override
                        protected Map<String, DataPart> getByteData() {
                            Map<String, DataPart> params = new HashMap<>();
                            String pictureName = "picture_" + System.currentTimeMillis();
                            params.put("image", new DataPart(pictureName + ".png",
                                    DataUtils.getFileDataFromDrawable(bitmap[0])));
                            return params;
                        }

                        @Override
                        public void deliverError(VolleyError error) {
                            super.deliverError(error);
                        }

                        @Override
                        protected VolleyError parseNetworkError(VolleyError volleyError) {
                            return super.parseNetworkError(volleyError);
                        }

                        */
/*
                          Passing request headers
                         *//*

                        @Override
                        public Map<String, String> getHeaders() {
                            HashMap<String, String> headers = new HashMap<>();
                            //headers.put("Content-Type", "application/json");
                            //headers.put(VolleyUtils.KEY_API_KEY, "xxxxxxxxxxxxxxx");
                            return headers;
                        }
                    };

                    //adding the request to volley
                    Volley.newRequestQueue(mContext).add(volleyMultipartRequest);
                }
            }
        }
        return uploadResponse;
    }
*/


}
