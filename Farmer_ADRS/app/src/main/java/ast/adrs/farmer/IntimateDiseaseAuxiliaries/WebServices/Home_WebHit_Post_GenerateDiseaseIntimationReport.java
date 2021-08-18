package ast.adrs.farmer.IntimateDiseaseAuxiliaries.WebServices;

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

public class Home_WebHit_Post_GenerateDiseaseIntimationReport {
    public static ResponseModel responseObject = null;
    private final AsyncHttpClient mClient = new AsyncHttpClient();
    public Context mContext;

    public void generateDiseaseIntimationReport(Context context, final IWebCallback iWebCallback, String json) {

        mContext = context;
        String myUrl = "";
        if (AppConfig.getInstance().mUserData.isFarmer())
            myUrl = AppConfig.getInstance().getBaseUrlApi() + ApiMethod.POST.generateDiseaseIntimationReport + "?diseaseIntimationId=" + json;
        else
            myUrl = AppConfig.getInstance().getBaseUrlApi() + ApiMethod.POST.generateDiseaseIntimationReport + "?diseaseIntimationId=" + json;


        mClient.setMaxRetriesAndTimeout(AppConstt.LIMIT_API_RETRY, AppConstt.LIMIT_TIMOUT_MILLIS);


        mClient.addHeader(ApiMethod.HEADER.Authorization_TOKEN, AppConfig.getInstance().mUserData.getAuthToken());


        Log.d("LOG_AS", " generateDiseaseIntimationReport  myUrl: " + myUrl);
        mClient.post(myUrl, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                        String strResponse = null;
                        try {
                            Gson gson = new Gson();
                            strResponse = new String(responseBody, StandardCharsets.UTF_8);
                            responseObject = gson.fromJson(strResponse, ResponseModel.class);
                            Log.d("LOG_AS", "generateDiseaseIntimationReport  strResponse: " + strResponse);
                            switch (responseObject.getStatusCode()) {

                                case AppConstt.ServerStatus.OK:
                                    if (responseObject.errorMessage != null)
                                        iWebCallback.onWebResult(true, "");
                                    break;

                                default:
                                    if (responseObject.errorMessage != null)
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
                        Log.d("LOG_AS", "generateDiseaseIntimationReport  onFailure: " + statusCode);
                        String strResponse = null;
                        try {
                            Gson gson = new Gson();
                            strResponse = new String(responseBody, StandardCharsets.UTF_8);

                            responseObject = gson.fromJson(strResponse, ResponseModel.class);
                            Log.d("LOG_AS", "sendDiseaseIntimationByFarmer  strResponse: " + strResponse);
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
                        catch (Exception e)
                        {

                        }


                    }
                }

        );
    }


    public class ResponseModel {


        public class Result
        {
            private int id;

            private String exception;

            private int status;

            private boolean isCanceled;

            private boolean isCompleted;

            private boolean isCompletedSuccessfully;

            private int creationOptions;

            private String asyncState;

            private boolean isFaulted;

            public void setId(int id){
                this.id = id;
            }
            public int getId(){
                return this.id;
            }
            public void setException(String exception){
                this.exception = exception;
            }
            public String getException(){
                return this.exception;
            }
            public void setStatus(int status){
                this.status = status;
            }
            public int getStatus(){
                return this.status;
            }
            public void setIsCanceled(boolean isCanceled){
                this.isCanceled = isCanceled;
            }
            public boolean getIsCanceled(){
                return this.isCanceled;
            }
            public void setIsCompleted(boolean isCompleted){
                this.isCompleted = isCompleted;
            }
            public boolean getIsCompleted(){
                return this.isCompleted;
            }
            public void setIsCompletedSuccessfully(boolean isCompletedSuccessfully){
                this.isCompletedSuccessfully = isCompletedSuccessfully;
            }
            public boolean getIsCompletedSuccessfully(){
                return this.isCompletedSuccessfully;
            }
            public void setCreationOptions(int creationOptions){
                this.creationOptions = creationOptions;
            }
            public int getCreationOptions(){
                return this.creationOptions;
            }
            public void setAsyncState(String asyncState){
                this.asyncState = asyncState;
            }
            public String getAsyncState(){
                return this.asyncState;
            }
            public void setIsFaulted(boolean isFaulted){
                this.isFaulted = isFaulted;
            }
            public boolean getIsFaulted(){
                return this.isFaulted;
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
