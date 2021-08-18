package ast.adrs.farmer;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import ast.adrs.farmer.HomeAuxiliares.WebServices.Home_WebHit_Post_Get_DiseaseDefinationDetails;
import ast.adrs.farmer.IntimateDiseaseAuxiliaries.DModel_FarmerAnimalIntimateDisease;
import ast.adrs.farmer.IntimateDiseaseAuxiliaries.DModel_FarmerAnimalIntimateDiseaseRepoty;
import ast.adrs.farmer.IntroAuxiliaries.DModelNewUser;
import ast.adrs.farmer.IntroAuxiliaries.DModelUserData;
import ast.adrs.farmer.IntroAuxiliaries.DModel_Animals;
import ast.adrs.farmer.IntroAuxiliaries.DModel_District;
import ast.adrs.farmer.Utils.AppConstt;
import ast.adrs.farmer.Utils.CustomAlertConfirmationInterface;
import ast.adrs.farmer.Utils.CustomAlertDialog;
import ast.adrs.farmer.Utils.IWebCallback;
import ast.adrs.farmer.Utils.IWebIndexedCallback;
import ast.adrs.farmer.Utils.IWebPaginationCallback;
import ast.adrs.farmer.Utils.RModel_Error;
import ast.adrs.farmer.Utils.RModel_Message;
import ast.adrs.farmer.Utils.RModel_onFailureError;


public class AppConfig {
    private static AppConfig ourInstance;// = new AppConfig(null);
    public CustomAlertDialog customAlertDialog;
    //endregion
    public Gson gson;
    public String mRole;
    public byte mStateApp;
    public int scrnWidth, scrnHeight;
    //Custom Font Type Face
    public Typeface tfAppDefault;
    public DModelUserData mUserData;
    public DModelNewUser mTempNewUser;
    public boolean isAppRunnig, isCommingFromSplash, isSwitchingLang, isEnglishMode, shouldSkipSplash, isComingFromForgotPassword;
    public int marginToast;
    public int nFCMCounter;
    public String urlWebview;
    public String currentAppVersion;
    public String mLanguage;
    public int NavgationItemState = 0;
    public List<Home_WebHit_Post_Get_DiseaseDefinationDetails.ResponseModel.Symptoms> lst_SelectedSyb;
    public boolean connectionAvailable;
    public List<Home_WebHit_Post_Get_DiseaseDefinationDetails.ResponseModel.Diseases> lst_DiseasesDef;
    public List<Home_WebHit_Post_Get_DiseaseDefinationDetails.ResponseModel.Symptoms> lst_Symptoms;
    public boolean isComingfromIntro;
    ArrayList<String> lstpendingFormData;

    public DModel_FarmerAnimalIntimateDisease dModel_farmerAnimalIntimateDisease;
    public DModel_FarmerAnimalIntimateDiseaseRepoty dModelFarmerAnimalReport;
    //General golbal data for the App
    private Context mContext;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private AppConfig(Context _mContext) {
        if (_mContext != null) {

            this.mContext = _mContext;
            this.scrnWidth = 0;
            this.scrnHeight = 0;
            this.tfAppDefault = Typeface.DEFAULT;


            this.marginToast = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100
                    , mContext.getResources().getDisplayMetrics()));


            Random random = new Random();
            this.nFCMCounter = random.nextInt(9999 - 1000) + 1000;

            initUserSessionData();
        }
    }

    public static void initInstance(Context _mContext) {
        if (ourInstance == null) {
            // Create the instance
            ourInstance = new AppConfig(_mContext);
        }
    }

    public static AppConfig getInstance() {
        return ourInstance;
    }


    //region Parsing Web Error Messages
    public void parsErrorMessage(final IWebCallback iWebCallback, byte[] responseBody) {
        try {
            Gson gson = new Gson();
            String strResponse = new String(responseBody, StandardCharsets.UTF_8);
            RModel_Error responseObjectLocal = gson.fromJson(strResponse, RModel_Error.class);

            StringBuilder str = new StringBuilder();

            // Traversing the ArrayList
            for (String eachstring : responseObjectLocal.getErrors()) {

                // Each element in ArrayList is appended
                // followed by comma
                str.append(eachstring).append(",");
            }

            // StringBuffer to String conversion
            String commaseparatedlist = str.toString();

            // By following condition you can remove the last
            // comma
            if (commaseparatedlist.length() > 0)
                commaseparatedlist
                        = commaseparatedlist.substring(
                        0, commaseparatedlist.length() - 1);

            System.out.println(commaseparatedlist);

//            for (int i=0;i<responseObjectLocal.getErrors().size();i++)
//            {
//
//            }

            iWebCallback.onWebResult(false, commaseparatedlist);
        } catch (Exception ex) {
            ex.printStackTrace();
            iWebCallback.onWebException(ex);
        }
    }

    private void parseJson(JSONObject data) {
        Log.d("paserdJSON", "JSONObject: e " + data);
        if (data != null) {
            Iterator<String> it = data.keys();
            while (it.hasNext()) {
                String key = it.next();
                try {
                    if (data.get(key) instanceof JSONArray) {
                        JSONArray arry = data.getJSONArray(key);
                        int size = arry.length();
                        for (int i = 0; i < size; i++) {
                            parseJson(arry.getJSONObject(i));
                        }
                    } else if (data.get(key) instanceof JSONObject) {
                        parseJson(data.getJSONObject(key));
                    } else {
                        Log.d("paserdJSON", key + ":" + data.getString(key));
                        System.out.println(key + ":" + data.getString(key));
                    }
                } catch (Throwable e) {
                    Log.d("paserdJSON", "Throwable: e " + e.getMessage());
                    try {
                        Log.d("paserdJSON", "Throwable: e " + key + ":" + data.getString(key));
                        System.out.println(key + ":" + data.getString(key));
                    } catch (Exception ee) {
                        Log.d("paserdJSON", "Throwable: ee " + ee.getMessage());
                    }
                    e.printStackTrace();

                }
            }
        }
    }

    public void parsErrorMessageOnFailure(final IWebCallback iWebCallback, byte[] responseBody) {
        try {
            Gson gson = new Gson();
            String strResponse = new String(responseBody, StandardCharsets.UTF_8);
            RModel_onFailureError responseObjectLocal = gson.fromJson(strResponse, RModel_onFailureError.class);


            Log.d("paserdJSON", "JSONObject: responseObjectLocal.getErrors() " + responseObjectLocal.getErrors());
//            parseJson(responseObjectLocal.getErrors());


            iWebCallback.onWebResult(false, responseObjectLocal.getErrors().toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            iWebCallback.onWebException(ex);
        }
    }

//    public void performLangCheck(Window _window) {
//        if (_window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
//            _window.getDecorView().setLayoutDirection(TextUtilsCompat
//                    .getLayoutDirectionFromLocale(Locale.getDefault()));
//    }
    //endregion

    public void parsErrorMessage(final IWebIndexedCallback iWebIndexedCallback, byte[] responseBody, int _index) {
        try {
            Gson gson = new Gson();
            String strResponse = new String(responseBody, StandardCharsets.UTF_8);
            RModel_Message responseObjectLocal = gson.fromJson(strResponse, RModel_Message.class);
            iWebIndexedCallback.onWebResult(false, responseObjectLocal.getMessage(), _index);
        } catch (Exception ex) {
            ex.printStackTrace();
            iWebIndexedCallback.onWebException(ex, _index);
        }
    }

    public void parsErrorMessage(final IWebPaginationCallback iWebPaginationCallback, byte[] responseBody,
                                 final int _index, final int _currIndex) {
        try {
            Gson gson = new Gson();
            String strResponse = new String(responseBody, StandardCharsets.UTF_8);
            RModel_Message responseObjectLocal = gson.fromJson(strResponse, RModel_Message.class);
            if (_index == _currIndex)
                iWebPaginationCallback.onWebInitialResult(false, responseObjectLocal.getMessage());
            else
                iWebPaginationCallback.onWebSuccessiveResult(false, false, responseObjectLocal.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            if (_index == _currIndex)
                iWebPaginationCallback.onWebInitialException(ex);
            else
                iWebPaginationCallback.onWebSuccessiveException(ex);
        }
    }

    public void closeKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window User_AccessToken from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window User_AccessToken from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showErrorMessage(Context context, String _errorMsg) {
        customAlertDialog = new CustomAlertDialog(context, "", _errorMsg, "Ok", "", false, new CustomAlertConfirmationInterface() {
            @Override
            public void callConfirmationDialogPositive() {
                customAlertDialog.dismiss();
            }

            @Override
            public void callConfirmationDialogNegative() {
                customAlertDialog.dismiss();
            }
        });
        customAlertDialog.show();
    }
    //endregion


    private void initUserSessionData() {
        gson = new Gson();
        this.isAppRunnig = false;
        this.isCommingFromSplash = false;
        this.isSwitchingLang = false;
        this.isComingFromForgotPassword = false;
        this.connectionAvailable = false;
        this.shouldSkipSplash = false;
        isComingfromIntro=false;

        this.sharedPref = mContext.getSharedPreferences(AppConstt.PREF_FILE_NAME, Context.MODE_PRIVATE);
        this.editor = sharedPref.edit();

        this.mLanguage = "";
        this.isEnglishMode = true;
        this.mRole = "";
        this.mUserData = new DModelUserData();
        this.mTempNewUser = new DModelNewUser();
        this.lst_DiseasesDef = new ArrayList<>();
        this.lst_SelectedSyb = new ArrayList<>();
        this.lst_Symptoms = new ArrayList<>();
        this.lstpendingFormData = new ArrayList<>();
        this.dModel_farmerAnimalIntimateDisease = new DModel_FarmerAnimalIntimateDisease();
        this.dModelFarmerAnimalReport = new DModel_FarmerAnimalIntimateDiseaseRepoty();

        loadUserProfileData();


    }

    //region Regulate user custom screen settings
    public Context regulateDisplayScale(final Context baseContext) {
        Context newContext;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            DisplayMetrics displayMetrics = baseContext.getResources().getDisplayMetrics();
            Configuration configuration = baseContext.getResources().getConfiguration();
            String deviceInfo = Build.MANUFACTURER;
            float scaledDensity = ((float) displayMetrics.densityDpi / (float) DisplayMetrics.DENSITY_DEFAULT);

            if (deviceInfo.equalsIgnoreCase(AppConstt.DeviceManufacturer.Samsung) ||
                    deviceInfo.equalsIgnoreCase(AppConstt.DeviceManufacturer.Huawei)) {

                //Logic based on Assets/phone_data.txt file
                if (scaledDensity < 2.5) {
                    configuration.densityDpi = 320;//DisplayMetrics.DENSITY_XHIGH
                } else if (scaledDensity < 3.5) {
                    configuration.densityDpi = 480;
                    if (scaledDensity > 2.9 && scaledDensity < 3.0) {
                        //HuaweiNexus_6P One & Only Special Case
                        configuration.densityDpi = 640;
                    }
                } else if (scaledDensity <= 4.5) {
                    configuration.densityDpi = 640;
                } else {
                    //Future devices (D.N.E as per Dec 2019)
                    configuration.densityDpi = 800;
                }
                newContext = baseContext;//baseContext.createConfigurationContext(configuration);

            } else {
                //Xiaomi and all other unknown devices
                if (displayMetrics.densityDpi != DisplayMetrics.DENSITY_DEVICE_STABLE) {
                    // Current density is different from Default Density. Override it
                    displayMetrics.densityDpi = DisplayMetrics.DENSITY_DEVICE_STABLE;
                    configuration.densityDpi = DisplayMetrics.DENSITY_DEVICE_STABLE;
                    newContext = baseContext;//baseContext.createConfigurationContext(configuration);
                } else {
                    // Same density. Just use same context
                    newContext = baseContext;
                }
            }
        } else {
            // Old API. Screen zoom not supported
            newContext = baseContext;
        }

        return newContext;
    }
    //endregion

    public Context regulateDisplayScaleObselete(final Context baseContext) {
        Context newContext;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            DisplayMetrics displayMetrics = baseContext.getResources().getDisplayMetrics();
            Configuration configuration = baseContext.getResources().getConfiguration();
            if (displayMetrics.densityDpi != DisplayMetrics.DENSITY_DEVICE_STABLE) {
                // Current density is different from Default Density. Override it
                displayMetrics.densityDpi = DisplayMetrics.DENSITY_DEVICE_STABLE;
                configuration.densityDpi = DisplayMetrics.DENSITY_DEVICE_STABLE;
                newContext = baseContext;//baseContext.createConfigurationContext(configuration);
            } else {
                // Same density. Just use same context
                newContext = baseContext;
            }
        } else {
            // Old API. Screen zoom not supported
            newContext = baseContext;
        }
        return newContext;
    }

    public void regulateFontScale(final Configuration configuration, final Context baseContext) {
        //The maximum scale/size of font allowed in this app, if user has changed that from his/her phone settings
        final float MAX_FONTSCALE = 1.0f;
        if (configuration.fontScale != MAX_FONTSCALE) {
            configuration.fontScale = MAX_FONTSCALE;
            DisplayMetrics metrics = baseContext.getResources().getDisplayMetrics();
            WindowManager wm = (WindowManager) baseContext.getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = configuration.fontScale * metrics.density;
//            Log.d("regulateDisplayScale", "fontScale: " + configuration.fontScale);
//            Log.d("regulateDisplayScale", "density: " + metrics.density);
//            Log.d("regulateDisplayScale", "scaledDensity: " + metrics.scaledDensity);
            baseContext.getResources().updateConfiguration(configuration, metrics);
        }
    }

    public void regulateFontScaleObselete(final Configuration configuration, final Context baseContext) {
        //The maximum scale/size of font allowed in this app, if user has changed that from his/her phone settings
        final float MAX_FONTSCALE = 1.0f;
        if (configuration.fontScale != MAX_FONTSCALE) {
            configuration.fontScale = MAX_FONTSCALE;
            DisplayMetrics metrics = baseContext.getResources().getDisplayMetrics();
            WindowManager wm = (WindowManager) baseContext.getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = configuration.fontScale * metrics.density;
            baseContext.getResources().updateConfiguration(configuration, metrics);
        }
    }

    //region Units conversions
    public String numberToCurrLang(String strNumber) {
        StringBuilder builder = new StringBuilder();

        if (isEnglishMode) {
            builder.append(strNumber);
        } else {
            char[] arabicChars = {'٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩'};
//        char[] arabicChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
            for (int i = 0; i < strNumber.length(); i++) {
                if (Character.isDigit(strNumber.charAt(i))) {
                    builder.append(arabicChars[(int) (strNumber.charAt(i)) - 48]);
                } else {
                    builder.append(strNumber.charAt(i));
                }
            }
        }

        return builder.toString();
    }

    public String priceToCurrLang(double _price, boolean isUnitRequired) {
        StringBuilder builder = new StringBuilder();
        if (isEnglishMode) {
            builder.append((AppConstt.UNIT_CURRENCY_EN + " "));
        } else {
            builder.append((AppConstt.UNIT_CURRENCY_AR + " "));
        }

//        builder.append(numberToCurrLang(String.format("%.2f", _price)));//Not working for arabic etc where , is being used
        NumberFormat formatter = NumberFormat.getNumberInstance();
        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(2);
        builder.append(numberToCurrLang("" + formatter.format(_price)));
//        builder.append(numberToCurrLang("" +
//                new BigDecimal(_price).setScale(2, RoundingMode.HALF_UP).doubleValue()));

        return builder.toString();
    }
    //endregion

    //region Language Settings

    public String distanceToCurrLang(double _distance) {
        StringBuilder builder = new StringBuilder();


//        builder.append(numberToCurrLang(String.format("%.2f", _price)));//Not working for arabic etc where , is being used
        NumberFormat formatter = NumberFormat.getNumberInstance();
        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(1);
        builder.append(numberToCurrLang("" + formatter.format(_distance)));
//        builder.append(numberToCurrLang("" +
//                new BigDecimal(_distance).setScale(2, RoundingMode.HALF_UP).doubleValue()));

        if (isEnglishMode) {
            builder.append((" " + AppConstt.UNIT_DISTANCE_EN));
        } else {
            builder.append((" " + AppConstt.UNIT_DISTANCE_AR));
        }
        return builder.toString();
    }

    public String durationToCurrLang(double _distance) {
        StringBuilder builder = new StringBuilder();


//        builder.append(numberToCurrLang(String.format("%.2f", _price)));//Not working for arabic etc where , is being used
        NumberFormat formatter = NumberFormat.getNumberInstance();
        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(1);
        builder.append(numberToCurrLang("" + formatter.format(_distance)));
//        builder.append(numberToCurrLang("" +
//                new BigDecimal(_distance).setScale(2, RoundingMode.HALF_UP).doubleValue()));

        if (isEnglishMode) {
            builder.append((" " + AppConstt.UNIT_DURATION_EN));
        } else {
            builder.append((" " + AppConstt.UNIT_DURATION_AR));
        }
        return builder.toString();
    }

    //region Profile
    public void loadUserProfileData() {

        mUserData.setId(sharedPref.getInt("key_user_id", 0));

        mUserData.setName(sharedPref.getString("key_user_name", ""));

        mUserData.setEmail(sharedPref.getString("key_user_email", ""));
        mUserData.setPhone(sharedPref.getString("key_user_phone", ""));
        mUserData.setDesignation(sharedPref.getString("key_user_designation", ""));
        mUserData.setDestrict(sharedPref.getString("key_user_destrict", ""));
        mUserData.setGender(sharedPref.getString("key_user_gender", ""));
        mUserData.setTehsil(sharedPref.getString("key_user_tehsil", ""));
        mUserData.setWhatsapp(sharedPref.getString("key_user_whatsapp", ""));
        mUserData.setCNIC(sharedPref.getString("key_user_cnic", ""));
        mUserData.setEncorededImage(sharedPref.getString("key_user_enc_image", ""));


        mUserData.setPinCode(sharedPref.getInt("key_user_pinCode", 0));
        mUserData.setDistrictID(sharedPref.getInt("key_user_districtID", 0));
        mUserData.setDesignationID(sharedPref.getInt("key_user_designationID", 0));
        mUserData.setTehsilID(sharedPref.getInt("key_user_tehsilID", 0));
        mUserData.setGenderID(sharedPref.getInt("key_user_genderID", 0));
        mUserData.setMozahID(sharedPref.getInt("key_user_mozahID", 0));


        mUserData.setMozah(sharedPref.getString("key_user_Mozah", ""));
        mUserData.setRefreshToken(sharedPref.getString("key_user_refreshToken", ""));
        mUserData.setDOB(sharedPref.getString("key_user_dob", ""));
        mUserData.setDevice_token(sharedPref.getString("key_user_device_token", ""));
        mUserData.setAuthToken(sharedPref.getString("key_user_authorization", ""));
        mUserData.setLoggedIn(sharedPref.getBoolean("key_user_isloggedin", false));
        mUserData.setLoggedInTemp(sharedPref.getBoolean("key_user_isloggedinTemp", false));
        mUserData.setFarmer(sharedPref.getBoolean("key_user_isFarmer", false));
        mUserData.setVA(sharedPref.getBoolean("key_user_isVA", false));

    }

    public void saveUserProfileData() {
        try {
            byte[] ptext2 = mUserData.getAuthToken().getBytes();
            mUserData.setAuthToken(new String(ptext2, StandardCharsets.UTF_8));


        } catch (Exception e) {
            e.printStackTrace();
        }

        editor.putInt("key_user_id", mUserData.getId());
        editor.putString("key_user_name", mUserData.getName());
        editor.putString("key_user_email", mUserData.getEmail());
        editor.putString("key_user_phone", mUserData.getPhone());
        editor.putString("key_user_designation", mUserData.getDesignation());
        editor.putString("key_user_destrict", mUserData.getDestrict());
        editor.putString("key_user_gender", mUserData.getGender());
        editor.putString("key_user_tehsil", mUserData.getTehsil());
        editor.putString("key_user_whatsapp", mUserData.getWhatsapp());
        editor.putString("key_user_Mozah", mUserData.getMozah());
        editor.putString("key_user_enc_image", mUserData.getEncorededImage());
        editor.putString("key_user_cnic", mUserData.getCNIC());
        editor.putInt("key_user_pinCode", mUserData.getPinCode());
        editor.putInt("key_user_districtID", mUserData.getDistrictID());
        editor.putInt("key_user_designationID", mUserData.getDesignationID());
        editor.putInt("key_user_tehsilID", mUserData.getTehsilID());
        editor.putInt("key_user_genderID", mUserData.getGenderID());
        editor.putInt("key_user_mozahID", mUserData.getMozahID());
        editor.putString("key_user_refreshToken", mUserData.getRefreshToken());
        editor.putString("key_user_dob", mUserData.getDOB());
        editor.putString("key_user_device_token", mUserData.getDevice_token());
        editor.putString("key_user_authorization", mUserData.getAuthToken());
        editor.putBoolean("key_user_isloggedin", mUserData.isLoggedIn());
        editor.putBoolean("key_user_isloggedinTemp", mUserData.isLoggedInTemp());
        editor.putBoolean("key_user_isFarmer", mUserData.isFarmer());
        editor.putBoolean("key_user_isVA", mUserData.isVA());

        editor.commit();
        loadUserProfileData();
    }





    public void  clearCommits()
    {
        editor.clear();
        editor.commit();

    }

    public void deleteUserData() {
        //retain FCM token only
        String tmpToken = loadFCMDeviceToken();
        //retain Language as well
        String appLangTemp = loadDefLanguage();

        editor.clear();
        editor.commit();

        initUserSessionData();

        if (mContext != null) {
            // Clear all notifications
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        }

        saveFCMDeviceToken(tmpToken);
        saveDefLanguage(appLangTemp);
    }
    //endregion


    public void saveInternetStatus(boolean isEnable)
    {
        editor.putBoolean("key_user_is_Internet_enable",isEnable);
        editor.commit();
    }

    public boolean isInternetEnabled()
    {
        return  sharedPref.getBoolean("key_user_is_Internet_enable",true);
    }


    public void saveFarmPopList(ArrayList<DModel_Animals> lstFarm) {
        String json = gson.toJson(lstFarm);

        Log.d("sharedPref","saveFarmPopList: " + json);

        editor.putString("key_lst_lstFarmPopList", json);
        editor.commit();
    }

    public ArrayList<DModel_Animals> getFarmPopList() {
        Type type = new TypeToken<List<DModel_Animals>>() {
        }.getType();

        ArrayList<DModel_Animals> list =
                gson.fromJson(sharedPref.getString("key_lst_lstFarmPopList", ""), type);
        if (list == null)
            list = new ArrayList<>();
        else
            saveFarmPopList(list);

        return list;
    }



    public void saveFarmList(ArrayList<DModel_Animals> lstFarm) {
        String json = gson.toJson(lstFarm); Log.d("sharedPref","saveFarmList: " + json);
        editor.putString("key_lst_lstFarm", json);
        editor.commit();
    }

    public ArrayList<DModel_Animals> getFarmList() {
        Type type = new TypeToken<List<DModel_Animals>>() {
        }.getType();

        ArrayList<DModel_Animals> list =
                gson.fromJson(sharedPref.getString("key_lst_lstFarm", ""), type);
        if (list == null)
            list = new ArrayList<>();
        else
            saveFarmList(list);

        return list;
    }


    public void saveSpicesList(ArrayList<DModel_District> lstSpices) {
        String json = gson.toJson(lstSpices);
Log.d("sharedPref","saveSpicesList: " + json);
        editor.putString("key_lst_Spices", json);
        editor.commit();
    }

    public ArrayList<DModel_District> getSpicesList() {
        Type type = new TypeToken<List<DModel_District>>() {
        }.getType();

        ArrayList<DModel_District> list =
                gson.fromJson(sharedPref.getString("key_lst_Spices", ""), type);
        if (list == null)
            list = new ArrayList<>();
        else
            saveSpicesList(list);

        return list;
    }


    public void saveAnimalList(ArrayList<DModel_Animals> lstAnimal) {
        String json = gson.toJson(lstAnimal);
        Log.d("sharedPref","saveAnimalList: " + json);
        editor.putString("key_lst_Animal", json);
        editor.commit();
    }

    public ArrayList<DModel_Animals> getAnimalList() {
        Type type = new TypeToken<List<DModel_Animals>>() {
        }.getType();

        ArrayList<DModel_Animals> list =
                gson.fromJson(sharedPref.getString("key_lst_Animal", ""), type);
        if (list == null)
            list = new ArrayList<>();
        else
            saveAnimalList(list);
        return list;
    }


    public void saveDesignationList(ArrayList<DModel_District> lst_Designation) {
        String json = gson.toJson(lst_Designation);
        Log.d("sharedPref","saveDesignationList: " + json);
        editor.putString("key_lst_Designation", json);
        editor.commit();
    }

    public ArrayList<DModel_District> getDesignationList() {
        Type type = new TypeToken<List<DModel_District>>() {
        }.getType();

        ArrayList<DModel_District> list =
                gson.fromJson(sharedPref.getString("key_lst_Designation", ""), type);
        if (list == null)
            list = new ArrayList<>();
        else
            saveDesignationList(list);

        return list;
    }


    public void saveDistrictList(ArrayList<DModel_District> lst_District) {
        String json = gson.toJson(lst_District);
        Log.d("sharedPref","saveDistrictList: " + json);
        editor.putString("key_lst_District", json);
        editor.commit();
    }

    public ArrayList<DModel_District> getDistrictList() {
        Type type = new TypeToken<List<DModel_District>>() {
        }.getType();

        ArrayList<DModel_District> list =
                gson.fromJson(sharedPref.getString("key_lst_District", ""), type);
        if (list == null)
            list = new ArrayList<>();
        else
            saveDistrictList(list);

        return list;
    }


    public void saveDiseasesList(List<Home_WebHit_Post_Get_DiseaseDefinationDetails.ResponseModel> lst_Diease) {
        String json = gson.toJson(lst_Diease);
        Log.d("sharedPref","saveDiseasesList: " + json);
        editor.putString("key_lst_Diease", json);
        editor.commit();
    }

    public List<Home_WebHit_Post_Get_DiseaseDefinationDetails.ResponseModel> getDiseasesList() {
        Type type = new TypeToken<List<Home_WebHit_Post_Get_DiseaseDefinationDetails.ResponseModel>>() {
        }.getType();

        List<Home_WebHit_Post_Get_DiseaseDefinationDetails.ResponseModel> list =
                gson.fromJson(sharedPref.getString("key_lst_Diease", ""), type);
        if (list == null)
            list = new ArrayList<>();
        else
            saveDiseasesList(list);


        return list;
    }


    public void saveDefRole(String role) {
        mRole = role;

    }

    public String getDefRole() {


        return mRole;
    }

    public void navtoLogin() {

        if (mUserData.isLoggedIn()) {
            deleteUserData();

            try {
                Intent intent = new Intent(MyApplication.mContext,
                        IntroActivity.class);

                // set the new task and clear flags
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                MyApplication.mContext.startActivity(intent);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    //region Base URLs
    public String getBaseUrlApi() {
        if (!BuildConfig.DEBUG)
            return AppConstt.ServerUrl.REL_URL_API;
        else
            return AppConstt.ServerUrl.DEB_URL_API;
    }
    //endregion

    //region Api's Encryption

    public String getBaseUrlImage() {
        if (!BuildConfig.DEBUG)
            return AppConstt.ServerUrl.REL_URL_IMG;
        else
            return AppConstt.ServerUrl.DEB_URL_IMG;
    }

    public String getBaseUrlThumbs() {
        if (!BuildConfig.DEBUG)
            return AppConstt.ServerUrl.REL_URL_THUMBS;
        else
            return AppConstt.ServerUrl.DEB_URL_THUMBS;
    }

    public String getBaseUrlSocket() {
        if (!BuildConfig.DEBUG)
            return AppConstt.ServerUrl.REL_URL_SOCKET;
        else
            return AppConstt.ServerUrl.DEB_URL_SOCKET;
    }

    public void saveIsEnglish(boolean _isEnglishMode) {
        isEnglishMode = _isEnglishMode;
        editor.putBoolean("key_is_eng", _isEnglishMode);
        editor.commit();
    }


    public void saveDefLanguage(String lang) {
        editor.putString("app_deflt_lang", lang);
        editor.commit();
    }

    public String loadDefLanguage() {
        mLanguage = sharedPref.getString("app_deflt_lang", "en");
        isEnglishMode = !mLanguage.equalsIgnoreCase("ur");
        return mLanguage;
    }

    public String getNetworkErrorMessage() {
        if (!isEnglishMode)
            return AppConstt.NetworkMsg.ERROR_NETWORK_AR;
        else
            return AppConstt.NetworkMsg.ERROR_NETWORK_EN;
    }

    public String getNetworkExceptionMessage(String _msg) {
        if (!BuildConfig.DEBUG) {
            if (!isEnglishMode)
                return AppConstt.NetworkMsg.EXCEPTION_NETWORK_AR;
            else
                return AppConstt.NetworkMsg.EXCEPTION_NETWORK_EN;
        } else {
            return _msg;
        }
    }

    //region Notifications Archieving
    public void saveNotifications(String strJsonOrders, String _lang) {
        editor.putString("key_Notifications" + "_" + _lang, strJsonOrders);
        editor.commit();
    }

    public String loadNotifications(String _lang) {
        return sharedPref.getString("key_Notifications" + "_" + _lang, null);
    }




    //endregion

    //region FCM Token Archieving
    public void saveFCMDeviceToken(String _token) {
        editor.putString("key_fcm_token", _token);
        editor.commit();
    }

    public String loadFCMDeviceToken() {
        return sharedPref.getString("key_fcm_token", "");
    }
    //endregion


    public boolean loadIsRevised() {
        return sharedPref.getBoolean("key_is_revised_v1", false);
    }

    public void saveIsRevised(boolean _isRevised) {
        editor.putBoolean("key_is_revised_v1", _isRevised);
        editor.commit();
    }


    public void setPreHomeOnce(boolean _isShowOnces) {
        editor.putBoolean("key_prehome_once", _isShowOnces);
        editor.commit();

    }

    public boolean isPreHomeShownOnce() {
        return sharedPref.getBoolean("key_prehome_once", false);
    }

    public void setSelectedRiskType(int _fundId, String _riskType) {
        editor.putInt("key_fund_id", _fundId);

        editor.putString("key_riskType", _riskType);
        editor.commit();

    }

    public Boolean getNotificationsSettings() {
        return sharedPref.getBoolean("key_notification_settings", true);
    }

    public void setNotificationsSettings(boolean _notification) {
        editor.putBoolean("key_notification_settings", _notification);
        editor.commit();

    }

    public void saveSubmittionOFDisease(String data)
    {
        lstpendingFormData.add(data);


        String json = gson.toJson(lstpendingFormData);
        editor.putString("key_notification_data_json_array", json);
        editor.commit();

    }
    public ArrayList<String> getSubmittionOFDisease() {
        Type type = new TypeToken<List<String>>() {
        }.getType();

        ArrayList<String> list =
                gson.fromJson(sharedPref.getString("key_notification_data_json_array", ""), type);
//        if (list == null)
//            list = new ArrayList<>();
//        else
//            saveSubmittionOFDisease(list);


        return list;


//        return sharedPref.getString("key_notification_data_json_array", "");
    }

    public Boolean isFormSubmittionPending() {
        return sharedPref.getBoolean("key_notification_FormSubmittionPending", false);
    }


    public void setFormSubmittionPending(boolean isForm) {
        editor.putBoolean("key_notification_FormSubmittionPending", isForm);
        editor.commit();

    }




}
