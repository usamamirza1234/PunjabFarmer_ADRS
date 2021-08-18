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
import cz.msebera.android.httpclient.entity.StringEntity;

public class Home_WebHit_Post_DiseaseByFarmer {
    public static ResponseModel responseObject = null;
    private final AsyncHttpClient mClient = new AsyncHttpClient();
    public Context mContext;

    public void sendDiseaseIntimationByFarmer(Context context, final IWebCallback iWebCallback, String json) {

        mContext = context;
        String myUrl ="";
        if (AppConfig.getInstance().mUserData.isFarmer())
         myUrl = AppConfig.getInstance().getBaseUrlApi() + ApiMethod.POST.DiseaseIntimationByFarmer;
        else
            myUrl = AppConfig.getInstance().getBaseUrlApi() + ApiMethod.POST.DiseaseIntimationByVA;


        mClient.setMaxRetriesAndTimeout(AppConstt.LIMIT_API_RETRY, AppConstt.LIMIT_TIMOUT_MILLIS);


        mClient.addHeader(ApiMethod.HEADER.Authorization_TOKEN, AppConfig.getInstance().mUserData.getAuthToken());
        StringEntity entity = null;
        entity = new StringEntity(json, "UTF-8");

        Log.d("LOG_AS", json + " sendDiseaseIntimationByFarmer  myUrl: " + myUrl);
        mClient.post(mContext, myUrl, entity, "application/json", new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d("LOG_AS", "sendDiseaseIntimationByFarmer  onSuccess: " + statusCode);
                        String strResponse = null;


                        try {

                            Gson gson = new Gson();
                            strResponse = new String(responseBody, StandardCharsets.UTF_8);

                            responseObject = gson.fromJson(strResponse, ResponseModel.class);
                            Log.d("LOG_AS", "sendDiseaseIntimationByFarmer  strResponse: " + strResponse);
                            switch (responseObject.getStatusCode()) {

                                case AppConstt.ServerStatus.OK:
                                    iWebCallback.onWebResult(true, "");
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
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d("LOG_AS", "sendDiseaseIntimationByFarmer  onFailure: " + statusCode);
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
            private String farmerID;

            private String cnic;

            private String mobileNumber;

            private String name;

            private String gender;

            private String mouzaID;

            private double longitude;

            private double latitude;

            private String diseaseID;

            private List<String> selectedSymptoms;

            private String specieID;

            private String totalAnimals;

            private String sickAnimals;

            private String deadAnimals;

            private String sickAnimalImage;

            private String farmId;

            private String reportID;

            private String diseaseIntimationID;

            public String getFarmerID() {
                return farmerID;
            }

            public void setFarmerID(String farmerID) {
                this.farmerID = farmerID;
            }

            public String getCnic() {
                return cnic;
            }

            public void setCnic(String cnic) {
                this.cnic = cnic;
            }

            public String getMobileNumber() {
                return mobileNumber;
            }

            public void setMobileNumber(String mobileNumber) {
                this.mobileNumber = mobileNumber;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getGender() {
                return gender;
            }

            public void setGender(String gender) {
                this.gender = gender;
            }

            public String getMouzaID() {
                return mouzaID;
            }

            public void setMouzaID(String mouzaID) {
                this.mouzaID = mouzaID;
            }

            public double getLongitude() {
                return longitude;
            }

            public void setLongitude(double longitude) {
                this.longitude = longitude;
            }

            public double getLatitude() {
                return latitude;
            }

            public void setLatitude(double latitude) {
                this.latitude = latitude;
            }

            public String getDiseaseID() {
                return diseaseID;
            }

            public void setDiseaseID(String diseaseID) {
                this.diseaseID = diseaseID;
            }

            public List<String> getSelectedSymptoms() {
                return selectedSymptoms;
            }

            public void setSelectedSymptoms(List<String> selectedSymptoms) {
                this.selectedSymptoms = selectedSymptoms;
            }

            public String getSpecieID() {
                return specieID;
            }

            public void setSpecieID(String specieID) {
                this.specieID = specieID;
            }

            public String getTotalAnimals() {
                return totalAnimals;
            }

            public void setTotalAnimals(String totalAnimals) {
                this.totalAnimals = totalAnimals;
            }

            public String getSickAnimals() {
                return sickAnimals;
            }

            public void setSickAnimals(String sickAnimals) {
                this.sickAnimals = sickAnimals;
            }

            public String getDeadAnimals() {
                return deadAnimals;
            }

            public void setDeadAnimals(String deadAnimals) {
                this.deadAnimals = deadAnimals;
            }

            public String getSickAnimalImage() {
                return sickAnimalImage;
            }

            public void setSickAnimalImage(String sickAnimalImage) {
                this.sickAnimalImage = sickAnimalImage;
            }

            public String getFarmId() {
                return farmId;
            }

            public void setFarmId(String farmId) {
                this.farmId = farmId;
            }

            public String getReportID() {
                return reportID;
            }

            public void setReportID(String reportID) {
                this.reportID = reportID;
            }

            public String getDiseaseIntimationID() {
                return diseaseIntimationID;
            }

            public void setDiseaseIntimationID(String diseaseIntimationID) {
                this.diseaseIntimationID = diseaseIntimationID;
            }
        }


    }
}
