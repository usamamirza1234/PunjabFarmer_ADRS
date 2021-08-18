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
import cz.msebera.android.httpclient.entity.StringEntity;

public class Intro_WebHit_Post_Token {
    public static ResponseModel responseObject = null;
    public Context mContext;
    private final AsyncHttpClient mClient = new AsyncHttpClient();

    public void refreshToken(Context context, final IWebCallback iWebCallback,
                             final String _signUpEntity) {

        mContext = context;
        String myUrl = AppConfig.getInstance().getBaseUrlApi() + ApiMethod.POST.refreshToken;


        mClient.setMaxRetriesAndTimeout(AppConstt.LIMIT_API_RETRY, AppConstt.LIMIT_TIMOUT_MILLIS);


        String data = "{" +
                "\"id\"" + ":" + 0 + "," +
                "\"refreshToken\"" + ":\"" + _signUpEntity +
                "\"}";
        StringEntity entity = null;
        entity = new StringEntity(data, "UTF-8");
        Log.d("LOG_AS", "refreshToken: " + myUrl + " " + data);


        //   mClient.addHeader(ApiMethod.HEADER.Authorization_TOKEN, AppConfig.getInstance().mUserData.authToken);

        mClient.post(mContext, myUrl, entity, "application/json", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String strResponse = null;


                        try {
                            Log.d("LOG_AS", "refreshToken onSuccess: " + statusCode);

                            Gson gson = new Gson();

                            strResponse = new String(responseBody, StandardCharsets.UTF_8);


                            responseObject = gson.fromJson(strResponse, ResponseModel.class);


                            switch (responseObject.getStatusCode()) {

                                case AppConstt.ServerStatus.OK:

                                    iWebCallback.onWebResult(true, statusCode + "");

                                    AppConfig.getInstance().mUserData.setAuthToken(responseObject.getResult());

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
                        Log.d("LOG_AS", "refreshToken onFailure: " + statusCode);
                        switch (statusCode) {
                            case AppConstt.ServerStatus.NETWORK_ERROR:
                                iWebCallback.onWebResult(false, AppConfig.getInstance().getNetworkErrorMessage());
                                break;

                            case AppConstt.ServerStatus.FORBIDDEN:
                                break;
                            case AppConstt.ServerStatus.UNAUTHORIZED:
                                iWebCallback.onWebResult(false, "" + statusCode);
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


        private String version;

        private int statusCode;

        private String errorMessage;

        private String result;

        private String timestamp;

        private List<String> errors;

        public String getVersion() {
            return this.version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public int getStatusCode() {
            return this.statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public String getErrorMessage() {
            return this.errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getResult() {
            return this.result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public String getTimestamp() {
            return this.timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public List<String> getErrors() {
            return this.errors;
        }

        public void setErrors(List<String> errors) {
            this.errors = errors;
        }


    }

}
