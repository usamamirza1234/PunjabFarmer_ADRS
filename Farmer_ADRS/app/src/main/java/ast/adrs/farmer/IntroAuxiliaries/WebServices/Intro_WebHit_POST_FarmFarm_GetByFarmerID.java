package ast.adrs.farmer.IntroAuxiliaries.WebServices;

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

public class Intro_WebHit_POST_FarmFarm_GetByFarmerID {
    public static ResponseModel responseObject = null;
    private final AsyncHttpClient mClient = new AsyncHttpClient();
    public Context mContext;

    public void getFarmFarm_GetByFarmerID(Context context, final IWebCallback iWebCallback,int id) {

        mContext = context;
        String myUrl = AppConfig.getInstance().getBaseUrlApi() + ApiMethod.POST.getFarmFarm_GetByFarmerID+ "?ID="+id;


        mClient.setMaxRetriesAndTimeout(AppConstt.LIMIT_API_RETRY, AppConstt.LIMIT_TIMOUT_MILLIS);




        Log.d("LOG_AS", "getFarmFarm_GetByFarmerID: " + myUrl + " " );


        mClient.addHeader(ApiMethod.HEADER.Authorization_TOKEN, AppConfig.getInstance().mUserData.getAuthToken());

        mClient.post( myUrl, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String strResponse = null;

                        try {

                            Log.d("LOG_AS", "getFarmFarm_GetByFarmerID onSuccess: " + statusCode);

                            Gson gson = new Gson();

                            strResponse = new String(responseBody, StandardCharsets.UTF_8);


                            responseObject = gson.fromJson(strResponse, ResponseModel.class);


                            switch (responseObject.getStatusCode()){

                                case AppConstt.ServerStatus.OK:

                                    iWebCallback.onWebResult(true, "");
                                    break;

                                default:
//                                    iWebCallback.onWebResult(false, "" +statusCode);
                                    AppConfig.getInstance().parsErrorMessage(iWebCallback, responseBody);
                                    break;
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                            iWebCallback.onWebException(ex);

                        }
                    }


                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d("LOG_AS", "getFarmFarm_GetByFarmerID onFailure: " + statusCode);
                        switch (statusCode) {
                            case AppConstt.ServerStatus.NETWORK_ERROR:
                                iWebCallback.onWebResult(false, AppConfig.getInstance().getNetworkErrorMessage());
                                break;
                            case AppConstt.ServerStatus.REFRESH_TOKEN:
                                iWebCallback.onWebResult(false, statusCode + "");
                                break;
                            case AppConstt.ServerStatus.FORBIDDEN:
                                break;
                            case AppConstt.ServerStatus.UNAUTHORIZED:
                                iWebCallback.onWebResult(false, "AUTH REQUIRED " + statusCode);
                                break;


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
            private String farmName;

            private int farmerID;

            private double latitude;

            private double longitude;

            private String createdDate;

            private int createdBy;

            private int id;

            public void setFarmName(String farmName){
                this.farmName = farmName;
            }
            public String getFarmName(){
                return this.farmName;
            }
            public void setFarmerID(int farmerID){
                this.farmerID = farmerID;
            }
            public int getFarmerID(){
                return this.farmerID;
            }
            public void setLatitude(double latitude){
                this.latitude = latitude;
            }
            public double getLatitude(){
                return this.latitude;
            }
            public void setLongitude(double longitude){
                this.longitude = longitude;
            }
            public double getLongitude(){
                return this.longitude;
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
            public void setId(int id){
                this.id = id;
            }
            public int getId(){
                return this.id;
            }
        }



            private String version;

            private int statusCode;

            private String errorMessage;

            private List<Result> result;

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
            public void setResult(List<Result> result){
                this.result = result;
            }
            public List<Result> getResult(){
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
