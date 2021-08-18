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

public class Home_WebHit_Post_SuggestionByUser {
    public static ResponseModel responseObject = null;
    private final AsyncHttpClient mClient = new AsyncHttpClient();
    public Context mContext;

    public void getSuggestions(Context context, final IWebCallback iWebCallback) {

        mContext = context;
        String myUrl;

        if (AppConfig.getInstance().mUserData.isFarmer())
            myUrl = AppConfig.getInstance().getBaseUrlApi() + ApiMethod.POST.getSuggestions;
        else
            myUrl = AppConfig.getInstance().getBaseUrlApi() + ApiMethod.POST.getSuggestions;

        mClient.setMaxRetriesAndTimeout(AppConstt.LIMIT_API_RETRY, AppConstt.LIMIT_TIMOUT_MILLIS);


        mClient.addHeader(ApiMethod.HEADER.Authorization_TOKEN, AppConfig.getInstance().mUserData.getAuthToken());


        Log.d("LOG_AS", " getSuggestions  myUrl: " + myUrl);
        mClient.post(myUrl, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d("LOG_AS", "getSuggestions  onSuccess: " + statusCode);
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
                        Log.d("LOG_AS", "getSuggestions  onFailure: " + statusCode);
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


        private String version;
        private int statusCode;
        private String errorMessage;
        private List<Result> result;
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

        public List<Result> getResult() {
            return this.result;
        }

        public void setResult(List<Result> result) {
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

        public class Result {
            private String subject;

            private String suggestion;

            private int userType;

            private String createdDate;

            private int createdBy;

            private String updatedDate;

            private String updatedBy;

            private boolean isDeleted;

            private int id;

            public String getSubject() {
                return this.subject;
            }

            public void setSubject(String subject) {
                this.subject = subject;
            }

            public String getSuggestion() {
                return this.suggestion;
            }

            public void setSuggestion(String suggestion) {
                this.suggestion = suggestion;
            }

            public int getUserType() {
                return this.userType;
            }

            public void setUserType(int userType) {
                this.userType = userType;
            }

            public String getCreatedDate() {
                return this.createdDate;
            }

            public void setCreatedDate(String createdDate) {
                this.createdDate = createdDate;
            }

            public int getCreatedBy() {
                return this.createdBy;
            }

            public void setCreatedBy(int createdBy) {
                this.createdBy = createdBy;
            }

            public String getUpdatedDate() {
                return this.updatedDate;
            }

            public void setUpdatedDate(String updatedDate) {
                this.updatedDate = updatedDate;
            }

            public String getUpdatedBy() {
                return this.updatedBy;
            }

            public void setUpdatedBy(String updatedBy) {
                this.updatedBy = updatedBy;
            }

            public boolean getIsDeleted() {
                return this.isDeleted;
            }

            public void setIsDeleted(boolean isDeleted) {
                this.isDeleted = isDeleted;
            }

            public int getId() {
                return this.id;
            }

            public void setId(int id) {
                this.id = id;
            }
        }
       

    }
}
