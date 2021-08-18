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

public class Home_WebHit_Post_GetSpices {
    public static ResponseModel responseObject = null;
    public Context mContext;
    private final AsyncHttpClient mClient = new AsyncHttpClient();

    public void getAnimalType(Context context, final IWebCallback iWebCallback) {

        mContext = context;
        String myUrl = AppConfig.getInstance().getBaseUrlApi() + ApiMethod.POST.getAniimalType;
        
        
        mClient.setMaxRetriesAndTimeout(AppConstt.LIMIT_API_RETRY, AppConstt.LIMIT_TIMOUT_MILLIS);

        mClient.addHeader(ApiMethod.HEADER.Authorization_TOKEN, AppConfig.getInstance().mUserData.getAuthToken());

        mClient.post(myUrl, new AsyncHttpResponseHandler() {

            @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d("LOG_AS", "getAnimalType  onSuccess: " + statusCode);
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
                        Log.d("LOG_AS", "getAnimalType  onFailure: " + statusCode);
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
            private String valueMemeber;

            private String displayMember;

            public void setValueMemeber(String valueMemeber){
                this.valueMemeber = valueMemeber;
            }
            public String getValueMemeber(){
                return this.valueMemeber;
            }
            public void setDisplayMember(String displayMember){
                this.displayMember = displayMember;
            }
            public String getDisplayMember(){
                return this.displayMember;
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
