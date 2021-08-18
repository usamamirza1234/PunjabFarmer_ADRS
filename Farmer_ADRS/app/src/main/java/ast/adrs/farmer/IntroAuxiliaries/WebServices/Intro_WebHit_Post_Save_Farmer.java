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

public class Intro_WebHit_Post_Save_Farmer {
    public static ResponseModel responseObject = null;
    private final AsyncHttpClient mClient = new AsyncHttpClient();
    public Context mContext;

    public void postFarmerSave(Context context, final IWebCallback iWebCallback,
                               final String _signUpEntity) {

        mContext = context;
        String myUrl = AppConfig.getInstance().getBaseUrlApi() + ApiMethod.POST.SaveFarmer;


        mClient.setMaxRetriesAndTimeout(AppConstt.LIMIT_API_RETRY, AppConstt.LIMIT_TIMOUT_MILLIS);

        StringEntity entity = null;
        entity = new StringEntity(_signUpEntity, "UTF-8");


        Log.d("LOG_AS", "postFarmerUpdate: " + myUrl + " " + _signUpEntity);

        mClient.addHeader(ApiMethod.HEADER.Authorization_TOKEN, AppConfig.getInstance().mUserData.getAuthToken());

        mClient.post(mContext, myUrl, entity, "application/json", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String strResponse = null;


                        try {
                            Log.d("LOG_AS", "postFarmerUpdate onSuccess: " + statusCode);

                            Gson gson = new Gson();

                            strResponse = new String(responseBody, StandardCharsets.UTF_8);


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
                        Log.d("LOG_AS", "postFarmerUpdate onFailure: " + statusCode);
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

        public class Result {
            private String farmerName;

            private String fatherName;

            private String cnic;

            private String mobileNumber;

            private String whatsAppMobileNumber;

            private int mouzaID;

            private int gender;

            private double latitude;

            private double longitude;

            private String createdDate;

            private String district;

            private String tehsil;

            private String mouza;

            private int id;

            public String getFarmerName() {
                return this.farmerName;
            }

            public void setFarmerName(String farmerName) {
                this.farmerName = farmerName;
            }

            public String getFatherName() {
                return this.fatherName;
            }

            public void setFatherName(String fatherName) {
                this.fatherName = fatherName;
            }

            public String getCnic() {
                return this.cnic;
            }

            public void setCnic(String cnic) {
                this.cnic = cnic;
            }

            public String getMobileNumber() {
                return this.mobileNumber;
            }

            public void setMobileNumber(String mobileNumber) {
                this.mobileNumber = mobileNumber;
            }

            public String getWhatsAppMobileNumber() {
                return this.whatsAppMobileNumber;
            }

            public void setWhatsAppMobileNumber(String whatsAppMobileNumber) {
                this.whatsAppMobileNumber = whatsAppMobileNumber;
            }

            public int getMouzaID() {
                return this.mouzaID;
            }

            public void setMouzaID(int mouzaID) {
                this.mouzaID = mouzaID;
            }

            public int getGender() {
                return this.gender;
            }

            public void setGender(int gender) {
                this.gender = gender;
            }

            public double getLatitude() {
                return this.latitude;
            }

            public void setLatitude(double latitude) {
                this.latitude = latitude;
            }

            public double getLongitude() {
                return this.longitude;
            }

            public void setLongitude(double longitude) {
                this.longitude = longitude;
            }

            public String getCreatedDate() {
                return this.createdDate;
            }

            public void setCreatedDate(String createdDate) {
                this.createdDate = createdDate;
            }

            public String getDistrict() {
                return this.district;
            }

            public void setDistrict(String district) {
                this.district = district;
            }

            public String getTehsil() {
                return this.tehsil;
            }

            public void setTehsil(String tehsil) {
                this.tehsil = tehsil;
            }

            public String getMouza() {
                return this.mouza;
            }

            public void setMouza(String mouza) {
                this.mouza = mouza;
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
