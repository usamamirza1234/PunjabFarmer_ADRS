package ast.adrs.farmer.ParentFragments.WebServices;

import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.nio.charset.StandardCharsets;

import ast.adrs.farmer.AppConfig;
import ast.adrs.farmer.Utils.ApiMethod;
import ast.adrs.farmer.Utils.AppConstt;
import ast.adrs.farmer.Utils.IWebCallback;
import cz.msebera.android.httpclient.Header;


/**
 * Created by bilalahmed on 04/05/2018.
 * bilalahmed.cs@live.com
 */

public class Home_WebHit_Post_Update_User {

    public static ResponseModel responseObject = null;
    private final AsyncHttpClient mClient = new AsyncHttpClient();

    public void postUserUpdate(final IWebCallback iWebCallback, final String _name, final String _email,
                               final String _phone, final String _city, final String _dob) {

        String myUrl; myUrl = AppConfig.getInstance().getBaseUrlApi() + ApiMethod.POST.updateUser;

        RequestParams params = new RequestParams();
        params.put("name", _name);
        params.put("email", _email);
        params.put("phone", _phone);
        params.put("city", _city);
        params.put("DOB", _dob);
//        params.put("device_type", "android");
//        params.put("type", AppConfig.getInstance().mUser.Type);
//        params.put("token", AppConfig.getInstance().loadFCMDeviceToken());

        Log.d("logApiData", "postUserUpdate: " + myUrl);
        Log.d("logApiData", "postUserUpdate: params " + params);

        mClient.addHeader(ApiMethod.HEADER.Authorization_TOKEN, AppConfig.getInstance().mUserData.getAuthToken());
        mClient.addHeader(ApiMethod.HEADER.Lang, AppConfig.getInstance().loadDefLanguage());
        mClient.setMaxRetriesAndTimeout(AppConstt.LIMIT_API_RETRY, AppConstt.LIMIT_TIMOUT_MILLIS);
        mClient.post(myUrl, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {


                        String strResponse;
                        try {
                            Gson gson = new Gson();
                            strResponse = new String(responseBody, StandardCharsets.UTF_8);

                            responseObject = gson.fromJson(strResponse, ResponseModel.class);

                            switch (statusCode) {

                                case AppConstt.ServerStatus.OK:
                                case AppConstt.ServerStatus.CREATED:
                                    Log.d("logApiData", "postUserUpdate: 1onSuccess " + statusCode);
                                    AppConfig.getInstance().mUserData.setName(_name);
                                    AppConfig.getInstance().mUserData.setEmail(_email);
                                    AppConfig.getInstance().mUserData.setPhone(_phone);
                                    AppConfig.getInstance().mUserData.setResidency_city(_city);
                                    AppConfig.getInstance().mUserData.setDOB(_dob);
                                    AppConfig.getInstance().mUserData.setLoggedIn(true);    AppConfig.getInstance().mUserData.setLoggedInTemp(true);
                                    AppConfig.getInstance().saveUserProfileData();
                                    Log.d("logApiData", "postUserUpdate: 2onSuccess " + statusCode);
                                    iWebCallback.onWebResult(true, responseObject.getMessage());
                                    break;

                                default:
                                    AppConfig.getInstance().parsErrorMessage(iWebCallback, responseBody);
                                    break;
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                            iWebCallback.onWebException(ex);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                            error) {
                        Log.d("logApiData", "postUserUpdate: onFailure " + statusCode);

                        switch (statusCode) {
                            case AppConstt.ServerStatus.NETWORK_ERROR:
                                iWebCallback.onWebResult(false, AppConfig.getInstance().getNetworkErrorMessage());
                                break;

                            case AppConstt.ServerStatus.FORBIDDEN:
                            case AppConstt.ServerStatus.UNAUTHORIZED:
                            default:
                                AppConfig.getInstance().parsErrorMessage(iWebCallback, responseBody);
                                break;
                        }
                    }
                }

        );
    }


    public final class ResponseModel {

        private int status;
        private String message;
        private Data data;

        public int getStatus() {
            return this.status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMessage() {
            return this.message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Data getData() {
            return this.data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public class Data {
            private String name;

            private String phone;

            private String email;

            private int id;

            private String category_id;

            private String type;

            private boolean band;

            private boolean solo;

            private String city;

            private String DOB;

            private String device_token;

            private String device_type;

            private int rating;

            private String lang;

            private boolean allowPush;

            private String verified;

            private String status;

            private String userType;

            public String getName() {
                return this.name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getPhone() {
                return this.phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getEmail() {
                return this.email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public int getId() {
                return this.id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getCategory_id() {
                return this.category_id;
            }

            public void setCategory_id(String category_id) {
                this.category_id = category_id;
            }

            public String getType() {
                return this.type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public boolean getBand() {
                return this.band;
            }

            public void setBand(boolean band) {
                this.band = band;
            }

            public boolean getSolo() {
                return this.solo;
            }

            public void setSolo(boolean solo) {
                this.solo = solo;
            }

            public String getCity() {
                return this.city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getDOB() {
                return this.DOB;
            }

            public void setDOB(String DOB) {
                this.DOB = DOB;
            }

            public String getDevice_token() {
                return this.device_token;
            }

            public void setDevice_token(String device_token) {
                this.device_token = device_token;
            }

            public String getDevice_type() {
                return this.device_type;
            }

            public void setDevice_type(String device_type) {
                this.device_type = device_type;
            }

            public int getRating() {
                return this.rating;
            }

            public void setRating(int rating) {
                this.rating = rating;
            }

            public String getLang() {
                return this.lang;
            }

            public void setLang(String lang) {
                this.lang = lang;
            }

            public boolean getAllowPush() {
                return this.allowPush;
            }

            public void setAllowPush(boolean allowPush) {
                this.allowPush = allowPush;
            }

            public String getVerified() {
                return this.verified;
            }

            public void setVerified(String verified) {
                this.verified = verified;
            }

            public String getStatus() {
                return this.status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getUserType() {
                return this.userType;
            }

            public void setUserType(String userType) {
                this.userType = userType;
            }
        }


    }


}
