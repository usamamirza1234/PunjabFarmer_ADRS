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

public class Intro_WebHit_POST_FarmPopulation_GetByFarmID_FarmerId {
    public static ResponseModel responseObject = null;
    private final AsyncHttpClient mClient = new AsyncHttpClient();
    public Context mContext;

    public void getFarmPopulation_GetByFarmID(Context context, final IWebCallback iWebCallback, int farmid) {

        mContext = context;
        String myUrl = "";


        if (AppConfig.getInstance().mUserData.isVA())
            myUrl = AppConfig.getInstance().getBaseUrlApi() + ApiMethod.POST.getFarmPopulation_GetByFarmID + "?FarmerID=" + AppConfig.getInstance().mUserData.getFarmerIDForVA() + "&FarmId=" + farmid;
        else
            myUrl = AppConfig.getInstance().getBaseUrlApi() + ApiMethod.POST.getFarmPopulation_GetByFarmID + "?FarmerID=" + AppConfig.getInstance().mUserData.getId() + "&FarmId=" + farmid;


        mClient.setMaxRetriesAndTimeout(AppConstt.LIMIT_API_RETRY, AppConstt.LIMIT_TIMOUT_MILLIS);


        Log.d("LOG_AS", "getFarmPopulation_GetByFarmID: " + myUrl + " ");


        mClient.addHeader(ApiMethod.HEADER.Authorization_TOKEN, AppConfig.getInstance().mUserData.getAuthToken());

        mClient.post(myUrl, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String strResponse = null;

                        try {

                            Log.d("LOG_AS", "getFarmPopulation_GetByFarmID onSuccess: " + statusCode);

                            Gson gson = new Gson();

                            strResponse = new String(responseBody, StandardCharsets.UTF_8);


                            responseObject = gson.fromJson(strResponse, ResponseModel.class);
                            Log.d("LOG_AS", "getFarmPopulation_GetByFarmID strResponse: " + strResponse);


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
                        Log.d("LOG_AS", "getFarmPopulation_GetByFarmID onFailure: " + statusCode);
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
            private int farmerID;

            private int specieID;

            private int noOfAnimals;

            private int farmID;

            private String createdDate;

            private int createdBy;

            private String updatedDate;

            private String updatedBy;

            private boolean isDeleted;

            private int id;

            public int getFarmerID() {
                return this.farmerID;
            }

            public void setFarmerID(int farmerID) {
                this.farmerID = farmerID;
            }

            public int getSpecieID() {
                return this.specieID;
            }

            public void setSpecieID(int specieID) {
                this.specieID = specieID;
            }

            public int getNoOfAnimals() {
                return this.noOfAnimals;
            }

            public void setNoOfAnimals(int noOfAnimals) {
                this.noOfAnimals = noOfAnimals;
            }

            public int getFarmID() {
                return this.farmID;
            }

            public void setFarmID(int farmID) {
                this.farmID = farmID;
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
