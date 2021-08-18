package ast.adrs.farmer.ParentFragments.WebServices;

import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.List;

import ast.adrs.farmer.AppConfig;
import ast.adrs.farmer.Utils.ApiMethod;
import ast.adrs.farmer.Utils.AppConstt;
import ast.adrs.farmer.Utils.IWebCallback;
import cz.msebera.android.httpclient.Header;


/**
 * Created by bilalahmed on 04/05/2018.
 * bilalahmed.cs@live.com
 */

public class Home_WebHit_Get_Contact_us {

    private AsyncHttpClient mClient = new AsyncHttpClient();
    public static ResponseModel responseObject = null;

    public void getContactus(final IWebCallback iWebCallback) {

        String myUrl;
        myUrl = AppConfig.getInstance().getBaseUrlApi() + ApiMethod.GET.contactus;
//        RequestParams params = new RequestParams();
//        params.put("name", _name);
//        params.put("email", _email);
//        params.put("phone", _phone);
//        params.put("city", _city);
//        params.put("dob", _dob);
//        params.put("deviceType", "android");
//        params.put("type", AppConfig.getInstance().mUser.Type);
//        params.put("token", AppConfig.getInstance().loadFCMDeviceToken());

        Log.d("LOG_AS", "getContactus: " + myUrl);
        mClient.addHeader(ApiMethod.HEADER.Authorization_TOKEN, AppConfig.getInstance().mUserData.getAuthToken());

        mClient.addHeader(ApiMethod.HEADER.Lang, AppConfig.getInstance().loadDefLanguage());
        Log.d("currentLang", AppConfig.getInstance().loadDefLanguage());
        mClient.setMaxRetriesAndTimeout(AppConstt.LIMIT_API_RETRY, AppConstt.LIMIT_TIMOUT_MILLIS);
        mClient.get(myUrl, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String strResponse;
                        try {
                            Gson gson = new Gson();
                            strResponse = new String(responseBody, "UTF-8");

                            responseObject = gson.fromJson(strResponse, ResponseModel.class);

                            switch (statusCode) {

                                case AppConstt.ServerStatus.OK:


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

            private List<Data> data;

            public void setStatus(int status){
                this.status = status;
            }
            public int getStatus(){
                return this.status;
            }
            public void setMessage(String message){
                this.message = message;
            }
            public String getMessage(){
                return this.message;
            }
            public void setData(List<Data> data){
                this.data = data;
            }
            public List<Data> getData(){
                return this.data;
            }
        public class Data
        {
            private int id;

            private String email;

            private String number;

            private String website;

            private String createdAt;

            private String updatedAt;

            public void setId(int id){
                this.id = id;
            }
            public int getId(){
                return this.id;
            }
            public void setEmail(String email){
                this.email = email;
            }
            public String getEmail(){
                return this.email;
            }
            public void setNumber(String number){
                this.number = number;
            }
            public String getNumber(){
                return this.number;
            }
            public void setWebsite(String website){
                this.website = website;
            }
            public String getWebsite(){
                return this.website;
            }
            public void setCreatedAt(String createdAt){
                this.createdAt = createdAt;
            }
            public String getCreatedAt(){
                return this.createdAt;
            }
            public void setUpdatedAt(String updatedAt){
                this.updatedAt = updatedAt;
            }
            public String getUpdatedAt(){
                return this.updatedAt;
            }
        }

    }


}
