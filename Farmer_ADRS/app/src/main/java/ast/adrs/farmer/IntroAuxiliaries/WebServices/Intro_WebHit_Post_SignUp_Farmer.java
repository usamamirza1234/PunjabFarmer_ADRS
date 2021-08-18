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
import ast.adrs.farmer.Utils.RModel_onFailureError;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class Intro_WebHit_Post_SignUp_Farmer {
    public static ResponseModel responseObject = null;
    private final AsyncHttpClient mClient = new AsyncHttpClient();
    public Context mContext;

    public void postSignUp(Context context, final IWebCallback iWebCallback,
                           final String _signUpEntity) {

        mContext = context;
        String myUrl = AppConfig.getInstance().getBaseUrlApi() + ApiMethod.POST.farmerRegisteration;
        mClient.setMaxRetriesAndTimeout(AppConstt.LIMIT_API_RETRY, AppConstt.LIMIT_TIMOUT_MILLIS);

        StringEntity entity = null;
        entity = new StringEntity(_signUpEntity, "UTF-8");


        Log.d("LOG_AS", "farmerRegisteration: " + myUrl + " " + _signUpEntity);
        mClient.post(mContext, myUrl, entity, "application/json", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {


                        try {
                            String strResponse = null;


                            Gson gson = new Gson();

                            strResponse = new String(responseBody, StandardCharsets.UTF_8);
                            Log.d("LOG_AS", "farmer Registeration strResponse: " + strResponse);

                            responseObject = gson.fromJson(strResponse, ResponseModel.class);

                            switch (responseObject.getStatusCode()) {

                                case AppConstt.ServerStatus.OK:

                                    iWebCallback.onWebResult(true, statusCode + "");
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
                        Log.d("LOG_AS", "API onFailure: " + statusCode);

                        try {

                            switch (statusCode) {
                                case AppConstt.ServerStatus.NETWORK_ERROR:
                                    iWebCallback.onWebResult(false, AppConfig.getInstance().getNetworkErrorMessage());
                                    break;


                                case AppConstt.ServerStatus.BAD_REQUEST: {

                                    try {

                                        String strResponse = null;
                                        Gson gson = new Gson();
                                        strResponse = new String(responseBody, StandardCharsets.UTF_8);
                                        RModel_onFailureError responseObject = gson.fromJson(strResponse, RModel_onFailureError.class);
                                        Log.d("LOG_AS", "farmerRegisteration onFailure strResponse: " + strResponse);


                                        iWebCallback.onWebResult(false, "Invalid CNIC");

                                    } catch (Exception e) {

                                    }

                                }
                                break;


                                case AppConstt.ServerStatus.FORBIDDEN:
                                case AppConstt.ServerStatus.UNAUTHORIZED:
                                default:
                                    AppConfig.getInstance().parsErrorMessageOnFailure(iWebCallback, responseBody);
//                                    iWebCallback.onWebResult(false, "Invalid CNIC");
                                    break;
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                            iWebCallback.onWebException(ex);

                        }


                    }
                }

        );
    }


    public class ResponseModel {


        private String version;
        private int statusCode;
        private String errorMessage;
        private Result result;
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

        public Result getResult() {
            return this.result;
        }

        public void setResult(Result result) {
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

        public class UserViewModel {
            private String userName;

            private String authToken;

            private String refreshToken;

            private String id;

            public String getUserName() {
                return this.userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public String getAuthToken() {
                return this.authToken;
            }

            public void setAuthToken(String authToken) {
                this.authToken = authToken;
            }

            public String getRefreshToken() {
                return this.refreshToken;
            }

            public void setRefreshToken(String refreshToken) {
                this.refreshToken = refreshToken;
            }

            public String getId() {
                return this.id;
            }

            public void setId(String id) {
                this.id = id;
            }
        }

        public class Result {
            private String farmerName;

            private String mobileNumber;

            private String preferredLanguage;

            private int pinCode;

            private String cnic;

            private UserViewModel userViewModel;

            public String getFarmerName() {
                return this.farmerName;
            }

            public void setFarmerName(String farmerName) {
                this.farmerName = farmerName;
            }

            public String getMobileNumber() {
                return this.mobileNumber;
            }

            public void setMobileNumber(String mobileNumber) {
                this.mobileNumber = mobileNumber;
            }

            public String getPreferredLanguage() {
                return this.preferredLanguage;
            }

            public void setPreferredLanguage(String preferredLanguage) {
                this.preferredLanguage = preferredLanguage;
            }

            public int getPinCode() {
                return this.pinCode;
            }

            public void setPinCode(int pinCode) {
                this.pinCode = pinCode;
            }

            public String getCnic() {
                return this.cnic;
            }

            public void setCnic(String cnic) {
                this.cnic = cnic;
            }

            public UserViewModel getUserViewModel() {
                return this.userViewModel;
            }

            public void setUserViewModel(UserViewModel userViewModel) {
                this.userViewModel = userViewModel;
            }
        }
    }

}
