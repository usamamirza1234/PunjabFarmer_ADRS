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

public class Home_WebHit_Post_Get_Report_By_ReportID {
    public static ResponseModel responseObject = null;
    private final AsyncHttpClient mClient = new AsyncHttpClient();
    public Context mContext;

    public void getReport(Context context, final IWebCallback iWebCallback, String json) {

        mContext = context;
        String myUrl = "";
        if (AppConfig.getInstance().mUserData.isFarmer())
            myUrl = AppConfig.getInstance().getBaseUrlApi() + ApiMethod.POST.getReport + "?reportId=" + json;
        else
            myUrl = AppConfig.getInstance().getBaseUrlApi() + ApiMethod.POST.getReport + "?reportId=" + json;


        mClient.setMaxRetriesAndTimeout(AppConstt.LIMIT_API_RETRY, AppConstt.LIMIT_TIMOUT_MILLIS);


        mClient.addHeader(ApiMethod.HEADER.Authorization_TOKEN, AppConfig.getInstance().mUserData.getAuthToken());


        Log.d("LOG_AS", " getReport  myUrl: " + myUrl);
        mClient.post(myUrl, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d("LOG_AS", "getReport  onSuccess: " + statusCode);
                        String strResponse = null;


                        try {

                            Gson gson = new Gson();
                            strResponse = new String(responseBody, StandardCharsets.UTF_8);

                            responseObject = gson.fromJson(strResponse, ResponseModel.class);
                            Log.d("LOG_AS", "getReport  strResponse: " + strResponse);
                            switch (responseObject.getStatusCode()) {

                                case AppConstt.ServerStatus.OK:
                                    if (responseObject.errorMessage != null)
                                        iWebCallback.onWebResult(true, responseObject.errorMessage);
                                    break;

                                default:
                                    if (responseObject.errorMessage != null)
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
                        Log.d("LOG_AS", "getReport  onFailure: " + statusCode);
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
            private String reportId;

            private String farmerName;

            private String mobileNumber;

            private String tehsil;

            private String mouzaId;

            private String mouza;

            private String location;

            private String farmName;

            private String speciesName;

            private String diseaseName;

            private List<String> symptomsName;

            private String totalAnimal;

            private String sickAnimals;

            private String deadAnimals;

            private String animalPopulation;

            private String createdDate;

            private String latitude;

            private String longitude;

            public String getReportId() {
                return this.reportId;
            }

            public void setReportId(String reportId) {
                this.reportId = reportId;
            }

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

            public String getLocation() {
                return this.location;
            }

            public void setLocation(String location) {
                this.location = location;
            }

            public String getFarmName() {
                return this.farmName;
            }

            public void setFarmName(String farmName) {
                this.farmName = farmName;
            }

            public String getSpeciesName() {
                return this.speciesName;
            }

            public void setSpeciesName(String speciesName) {
                this.speciesName = speciesName;
            }

            public String getDiseaseName() {
                return this.diseaseName;
            }

            public void setDiseaseName(String diseaseName) {
                this.diseaseName = diseaseName;
            }

            public List<String> getSymptomsName() {
                return this.symptomsName;
            }

            public void setSymptomsName(List<String> symptomsName) {
                this.symptomsName = symptomsName;
            }



            public String getAnimalPopulation() {
                return this.animalPopulation;
            }

            public void setAnimalPopulation(String animalPopulation) {
                this.animalPopulation = animalPopulation;
            }

            public String getCreatedDate() {
                return this.createdDate;
            }

            public void setCreatedDate(String createdDate) {
                this.createdDate = createdDate;
            }

            public String getMouzaId() {
                return mouzaId;
            }

            public void setMouzaId(String mouzaId) {
                this.mouzaId = mouzaId;
            }

            public String getTotalAnimal() {
                return totalAnimal;
            }

            public void setTotalAnimal(String totalAnimal) {
                this.totalAnimal = totalAnimal;
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

            public String getLatitude() {
                return latitude;
            }

            public void setLatitude(String latitude) {
                this.latitude = latitude;
            }

            public String getLongitude() {
                return longitude;
            }

            public void setLongitude(String longitude) {
                this.longitude = longitude;
            }
        }
    }


}
