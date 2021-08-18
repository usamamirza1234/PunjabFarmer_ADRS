package ast.adrs.farmer.HomeAuxiliares.WebServices;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.nio.charset.StandardCharsets;
import java.util.List;

import ast.adrs.farmer.AppConfig;
import ast.adrs.farmer.Utils.ApiMethod;
import ast.adrs.farmer.Utils.AppConstt;
import ast.adrs.farmer.Utils.IWebCallback;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class Home_WebHit_Post_Farmer_Suggestions {
    public static ResponseModel responseObject = null;
    public Context mContext;
    private final AsyncHttpClient mClient = new AsyncHttpClient();

    public void sendSuggestion(Context context, final IWebCallback iWebCallback,String json) {

        mContext = context;
        String myUrl = AppConfig.getInstance().getBaseUrlApi() + ApiMethod.POST.sendSuggestion;
        mClient.setMaxRetriesAndTimeout(AppConstt.LIMIT_API_RETRY, AppConstt.LIMIT_TIMOUT_MILLIS);

        mClient.addHeader(ApiMethod.HEADER.Authorization_TOKEN, AppConfig.getInstance().mUserData.getAuthToken());
        StringEntity entity = null;
        entity = new StringEntity(json, "UTF-8");

        Log.d("LOG_AS", json +" sendSuggestion  myUrl: " + myUrl);
        mClient.post(mContext, myUrl, entity, "application/json", new AsyncHttpResponseHandler() {

            @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d("LOG_AS", "sendSuggestion  onSuccess: " + statusCode);
                        String strResponse = null;


                        try {

                            Gson gson = new Gson();
                            strResponse = new String(responseBody, StandardCharsets.UTF_8);

                            responseObject = gson.fromJson(strResponse, ResponseModel.class);

                            switch (statusCode) {

                                case AppConstt.ServerStatus.OK:
                                    iWebCallback.onWebResult(true, responseObject.errorMessage);
                                    break;

                                default:
                                    iWebCallback.onWebResult(false, responseObject.errorMessage);
                                    break;
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                            iWebCallback.onWebException(ex);
                        }
                    }


                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d("LOG_AS", "sendSuggestion  onFailure: " + statusCode);
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


    public class ResponseModel {


        public class Result
        {
            private int id;

            private boolean isDeleted;

            private String createdDate;

            private int createdBy;

            private String updatedDate;

            private int updatedBy;

            private String subject;

            private String suggestion;

            private int userType;

            public void setId(int id){
                this.id = id;
            }
            public int getId(){
                return this.id;
            }
            public void setIsDeleted(boolean isDeleted){
                this.isDeleted = isDeleted;
            }
            public boolean getIsDeleted(){
                return this.isDeleted;
            }
            public void setCreatedDate(String createdDate){
                this.createdDate = createdDate;
            }
            public String getCreatedDate(){
                return this.createdDate;
            }
            public void setCreatedBy(int createdBy){
                this.createdBy = createdBy;
            }
            public int getCreatedBy(){
                return this.createdBy;
            }
            public void setUpdatedDate(String updatedDate){
                this.updatedDate = updatedDate;
            }
            public String getUpdatedDate(){
                return this.updatedDate;
            }
            public void setUpdatedBy(int updatedBy){
                this.updatedBy = updatedBy;
            }
            public int getUpdatedBy(){
                return this.updatedBy;
            }
            public void setSubject(String subject){
                this.subject = subject;
            }
            public String getSubject(){
                return this.subject;
            }
            public void setSuggestion(String suggestion){
                this.suggestion = suggestion;
            }
            public String getSuggestion(){
                return this.suggestion;
            }
            public void setUserType(int userType){
                this.userType = userType;
            }
            public int getUserType(){
                return this.userType;
            }
        }



            private String version;

            private int statusCode;

            private String errorMessage;

            private Result result;

            private String timestamp;

            private List<String> errors;

            public void setVersion(String version){
                this.version = version;
            }
            public String getVersion(){
                return this.version;
            }
            public void setStatusCode(int statusCode){
                this.statusCode = statusCode;
            }
            public int getStatusCode(){
                return this.statusCode;
            }
            public void setErrorMessage(String errorMessage){
                this.errorMessage = errorMessage;
            }
            public String getErrorMessage(){
                return this.errorMessage;
            }
            public void setResult(Result result){
                this.result = result;
            }
            public Result getResult(){
                return this.result;
            }
            public void setTimestamp(String timestamp){
                this.timestamp = timestamp;
            }
            public String getTimestamp(){
                return this.timestamp;
            }
            public void setErrors(List<String> errors){
                this.errors = errors;
            }
            public List<String> getErrors(){
                return this.errors;
            }



    }
}
