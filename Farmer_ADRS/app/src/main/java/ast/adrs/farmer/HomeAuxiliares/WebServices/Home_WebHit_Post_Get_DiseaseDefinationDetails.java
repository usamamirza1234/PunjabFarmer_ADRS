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

public class Home_WebHit_Post_Get_DiseaseDefinationDetails {
    public static ResponseModel responseObject = null;
    private final AsyncHttpClient mClient = new AsyncHttpClient();
    public Context mContext;

    public void getDiseaseDefinationDetails(Context context, final IWebCallback iWebCallback) {

        mContext = context;

        String myUrl = AppConfig.getInstance().getBaseUrlApi() + ApiMethod.POST.getDiseaseDefinition;


        mClient.setMaxRetriesAndTimeout(AppConstt.LIMIT_API_RETRY, AppConstt.LIMIT_TIMOUT_MILLIS);

        mClient.addHeader(ApiMethod.HEADER.Authorization_TOKEN, AppConfig.getInstance().mUserData.getAuthToken());

        mClient.post(myUrl, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d("LOG_AS", "getDiseaseDefinationDetails  onSuccess: " + statusCode);
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
                        Log.d("LOG_AS", "getDiseaseDefinationDetails  onFailure: " + statusCode);
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

        public class Symptoms {
            private int id;

            private String name;

            private boolean notifiable;

            private int notifiableBy;

            private int type;

            public int getId() {
                return this.id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return this.name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public boolean getNotifiable() {
                return this.notifiable;
            }

            public void setNotifiable(boolean notifiable) {
                this.notifiable = notifiable;
            }

            public int getNotifiableBy() {
                return this.notifiableBy;
            }

            public void setNotifiableBy(int notifiableBy) {
                this.notifiableBy = notifiableBy;
            }

            public int getType() {
                return this.type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public  boolean isSelected;

            public boolean isSelected() {
                return isSelected;
            }

            public void setSelected(boolean selected) {
                isSelected = selected;
            }
        }

        public class Diseases {
            private int diseaseId;

            private String diseaseName;

            private List<Symptoms> symptoms;

            public int getDiseaseId() {
                return this.diseaseId;
            }

            public void setDiseaseId(int diseaseId) {
                this.diseaseId = diseaseId;
            }

            public String getDiseaseName() {
                return this.diseaseName;
            }

            public void setDiseaseName(String diseaseName) {
                this.diseaseName = diseaseName;
            }

            public List<Symptoms> getSymptoms() {
                return this.symptoms;
            }

            public void setSymptoms(List<Symptoms> symptoms) {
                this.symptoms = symptoms;
            }


        }

        public class Result {
            private int specieId;

            private String specieGroupName;

            private String animalType;

            private List<Diseases> diseases;

            public int getSpecieId() {
                return this.specieId;
            }

            public void setSpecieId(int specieId) {
                this.specieId = specieId;
            }

            public String getSpecieGroupName() {
                return this.specieGroupName;
            }

            public void setSpecieGroupName(String specieGroupName) {
                this.specieGroupName = specieGroupName;
            }

            public String getAnimalType() {
                return this.animalType;
            }

            public void setAnimalType(String animalType) {
                this.animalType = animalType;
            }

            public List<Diseases> getDiseases() {
                return this.diseases;
            }

            public void setDiseases(List<Diseases> diseases) {
                this.diseases = diseases;
            }
        }


    }
}
