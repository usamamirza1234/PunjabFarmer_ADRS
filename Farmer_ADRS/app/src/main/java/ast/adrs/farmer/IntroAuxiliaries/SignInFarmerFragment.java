package ast.adrs.farmer.IntroAuxiliaries;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ast.adrs.farmer.AppConfig;
import ast.adrs.farmer.HomeAuxiliares.WebServices.Home_WebHit_Post_Get_DiseaseDefinationDetails;
import ast.adrs.farmer.IntroActivity;
import ast.adrs.farmer.IntroAuxiliaries.WebServices.Intro_WebHit_Get_All_Designations;
import ast.adrs.farmer.IntroAuxiliaries.WebServices.Intro_WebHit_Get_All_District;
import ast.adrs.farmer.IntroAuxiliaries.WebServices.Intro_WebHit_Get_All_Spices;
import ast.adrs.farmer.IntroAuxiliaries.WebServices.Intro_WebHit_Post_SignUp_Farmer;
import ast.adrs.farmer.MyApplication;
import ast.adrs.farmer.R;
import ast.adrs.farmer.Utils.AppConstt;
import ast.adrs.farmer.Utils.CustomAlertDialog;
import ast.adrs.farmer.Utils.GPSTracker;
import ast.adrs.farmer.Utils.IBadgeUpdateListener;
import ast.adrs.farmer.Utils.IWebCallback;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by UsamaMirza on 30/04/2021.
 * usamamirza@veroke.com
 */

public class SignInFarmerFragment extends Fragment implements View.OnClickListener {
    private static final int REQUEST_CODE_PERMISSION = 2;
    private final int REQUEST_LOCATION_PERMISSION = 1;
    CustomAlertDialog customAlertDialog;
    EditText edtPhone, edtName, edtCNIC;
    RelativeLayout rlLogin;
    // GPSTracker class
    GPSTracker gps;
    ArrayList<DModel_District> lstDesgnation;
    ArrayList<DModel_District> lstSpices;
    List<Home_WebHit_Post_Get_DiseaseDefinationDetails.ResponseModel> lst_AllDiseaseDefination;
    ArrayList<DModel_District> lst_District;
    private IBadgeUpdateListener mBadgeUpdateListener;
    private Dialog progressDialog;
    private LinearLayout llSync, llNotSync;
    private RelativeLayout rlSwitchLang, rlContinue;

    public SignInFarmerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View frg = inflater.inflate(R.layout.fragment_signin_farmer, container, false);

        init();
        bindviews(frg);

        //
        Typeface tfEng = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Poppins_Regular.ttf");
        Typeface tfAr = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Almarai_Light.ttf");

//        txvVA.setTypeface(tfEng);
//        txvFarmer.setPadding(0, 0, 0, 0);//ltrb
//        txvFarmer.setTypeface(tfAr);
//        txvFarmer.setPadding(0, 0, 0, dpToPix(2));//ltrb


        if (AppConfig.getInstance().getDiseasesList().size() <= 0) {
            requestDiseaseDefinition();
        }

        return frg;
    }




    private void requestDiseaseDefinition() {
//        showProgDialog();
        Home_WebHit_Post_Get_DiseaseDefinationDetails home_webHit_post_get_diseaseDefinationDetails = new Home_WebHit_Post_Get_DiseaseDefinationDetails();
        home_webHit_post_get_diseaseDefinationDetails.getDiseaseDefinationDetails(getContext(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {
//                    llSync.setVisibility(View.GONE);
//                    llNotSync.setVisibility(View.VISIBLE);
                    Log.d("OFFLINE_DB", "getSpicesList()  " +
                            AppConfig.getInstance().getSpicesList().size());



                    if (Home_WebHit_Post_Get_DiseaseDefinationDetails.responseObject != null &&
                            Home_WebHit_Post_Get_DiseaseDefinationDetails.responseObject.getResult() != null) {
                        for (int i = 0; i < Home_WebHit_Post_Get_DiseaseDefinationDetails.responseObject.getResult().size(); i++) {
                            Home_WebHit_Post_Get_DiseaseDefinationDetails.ResponseModel dModel = Home_WebHit_Post_Get_DiseaseDefinationDetails.responseObject;
                            lst_AllDiseaseDefination.add(dModel);


                            if (AppConfig.getInstance().getDiseasesList() != null) {
                                AppConfig.getInstance().getDiseasesList().clear();
                            }
                            AppConfig.getInstance().saveDiseasesList(lst_AllDiseaseDefination);
                        }
                    }
//                    dismissProgDialog();
                } else {
//                    dismissProgDialog();
                    AppConfig.getInstance().showErrorMessage(getContext(), strMsg);
                }
            }

            @Override
            public void onWebException(Exception e) {
//                dismissProgDialog();
                AppConfig.getInstance().showErrorMessage(getContext(), e.toString());
            }
        });
    }

    private void init() {
        setBottomBar();
        requestLocationPermission();
        this.lstDesgnation = new ArrayList<>();
        this.lst_District = new ArrayList<>();
        this.lst_AllDiseaseDefination = new ArrayList<>();
        this.lstSpices = new ArrayList<>();
//        try {
//            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (GPSTracker.getInstance(getContext()).getLatitude() != 0) {
//            Log.d("GPS_TRACKER","Lat : "+ GPSTracker.getInstance(getContext()).getLatitude());
//            Log.d("GPS_TRACKER","Long : "+ GPSTracker.getInstance(getContext()).getLongitude());
//            Log.d("GPS_TRACKER","Loc : "+ GPSTracker.getInstance(getContext()).getLocation());
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("GPS_TRACKER", " : requestCode " + requestCode);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
//            Toast.makeText(getActivity(), "Permission already granted", Toast.LENGTH_SHORT).show();

            if (GPSTracker.getInstance(getContext()).getLatitude() != 0) {
                Log.d("GPS_TRACKER", "Lat : " + GPSTracker.getInstance(getContext()).getLatitude());
                Log.d("GPS_TRACKER", "Long : " + GPSTracker.getInstance(getContext()).getLongitude());
                Log.d("GPS_TRACKER", "Loc : " + GPSTracker.getInstance(getContext()).getLocation());
            }

        } else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
        }
    }


    void setBottomBar() {

        try {
            mBadgeUpdateListener = (IBadgeUpdateListener) getActivity();
        } catch (ClassCastException castException) {
            castException.printStackTrace(); // The activity does not implement the listener
        }
        if (getActivity() != null && isAdded())
            mBadgeUpdateListener.setToolbarState(AppConstt.INTRO_ToolbarStates.signinVA);

    }

    private void bindviews(View frg) {
        edtName = frg.findViewById(R.id.frg_sigin_farmer_edt_name);
        edtPhone = frg.findViewById(R.id.frg_sigin_farmer_edt_number);
        edtCNIC = frg.findViewById(R.id.frg_sigin_farmer_edt_cnic);
        rlSwitchLang = frg.findViewById(R.id.frg_sgnin_rl_switchlang);
        llSync = frg.findViewById(R.id.frg_choose_ll_when_sync);
        llNotSync = frg.findViewById(R.id.frg_choose_ll_when_not_sync);
        rlSwitchLang.setOnClickListener(this);
        rlLogin = frg.findViewById(R.id.frg_signin_farmer_rl_login);
        rlLogin.setOnClickListener(this);
        editTextWatchers();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.frg_signin_farmer_rl_login:
                closeKeyboard();
                checkErrorConditions();
                break;
            case R.id.frg_sgnin_rl_switchlang:
                switchLang();
                break;
            default:
                break;
        }
    }

    private void switchLang() {
        Log.d("Locale", "mLanguage: " + AppConfig.getInstance().mLanguage);

        if (AppConfig.getInstance().mLanguage.equalsIgnoreCase(AppConstt.AppLang.LANG_UR)) {
            MyApplication.getInstance().setAppLanguage(AppConstt.AppLang.LANG_EN);
        } else {
            MyApplication.getInstance().setAppLanguage(AppConstt.AppLang.LANG_UR);
        }

        reStartActivity();
    }

    private void reStartActivity() {
        if (getActivity() != null) {
//            getActivity().recreate();
            AppConfig.getInstance().shouldSkipSplash = true;
            Intent i = new Intent(getActivity(), IntroActivity.class);
            startActivity(i);
            getActivity().finish();
        }
    }

    private void editTextWatchers() {
        edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().startsWith(" ")) {
                    edtName.setText("");
                }
            }
        });
        edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().startsWith(" ")) {
                    edtPhone.setText("");
                }
            }
        });
        edtCNIC.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().startsWith(" ")) {
                    edtCNIC.setText("");
                }
            }
        });

    }

    private void closeKeyboard() {
        AppConfig.getInstance().closeKeyboard(getActivity());

    }

    private void checkErrorConditions() {


        if (checkNameErrorCondition() && checkNumberErrorCondition() && checkCNICErrorCondition()) {


            String lang = "";


            if (AppConfig.getInstance().mLanguage.equalsIgnoreCase(AppConstt.AppLang.LANG_UR)) {
                lang = "u";
            } else {
                lang = "e";
            }

            String strPhoneNumber = edtPhone.getText().toString();
            strPhoneNumber = "92" + strPhoneNumber.substring(1);

            AppConfig.getInstance().mUserData.setPhone(strPhoneNumber);

            Date date = new Date();
            String str_DATE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH).format(date);
            String data = "{" +
                    "\"id\"" + ":" + 0 + "," +
                    "\"pinCode\"" + ":" + 0 + "," +
//                    "\"updatedBy\"" + ":" + 0 + "," +
//                    "\"createdBy\"" + ":" + 0 + "," +
                    "\"isDeleted\"" + ":" + true + "," +
                    "\"isVerfied\"" + ":" + true + "," +
                    "\"cnic\"" + ":" + edtCNIC.getText().toString().replaceFirst("^0+(?!$)", "") + "," +
                    "\"farmerName\"" + ":\"" + edtName.getText().toString() + "\"," +
                    "\"createdDate\"" + ":\"" + str_DATE + "\"," +
                    "\"updatedDate\"" + ":\"" + str_DATE + "\"," +
                    "\"mobileNumber\"" + ":" + strPhoneNumber + "," +
                    "\"preferedLanguage\"" + ":\"" + lang + "\"}";


            Log.d("LOG_AS", "postSignUp: " + data);
            requestUserRegister(data);
//            showRequestPermission(getContext(), "Please allow the location", data);


        }
    }

    private void requestUserRegister(String _signUpEntity) {
        showProgDialog();
        Intro_WebHit_Post_SignUp_Farmer intro_webHit_post_signinFarmer = new Intro_WebHit_Post_SignUp_Farmer();
        intro_webHit_post_signinFarmer.postSignUp(getContext(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {
                    dismissProgDialog();


                    AppConfig.getInstance().mUserData.setName(Intro_WebHit_Post_SignUp_Farmer.responseObject.getResult().getFarmerName());
                    AppConfig.getInstance().mUserData.setCNIC(edtCNIC.getText().toString().replaceFirst("^0+(?!$)", ""));


                    AppConfig.getInstance().mUserData.setPinCode(Intro_WebHit_Post_SignUp_Farmer.responseObject.getResult().getPinCode());


                    AppConfig.getInstance().mUserData.setRefreshToken(Intro_WebHit_Post_SignUp_Farmer.responseObject.getResult().getUserViewModel().getRefreshToken());
                    AppConfig.getInstance().mUserData.setAuthToken(Intro_WebHit_Post_SignUp_Farmer.responseObject.getResult().getUserViewModel().getAuthToken());


                    navToVerificationFAFragment();


                } else {
                    dismissProgDialog();

                    AppConfig.getInstance().showErrorMessage(getContext(), strMsg);
                }
            }

            @Override
            public void onWebException(Exception e) {
                dismissProgDialog();
                Log.d("LOG_AS", "farmer Registration Exception: " + e.getMessage());

                AppConfig.getInstance().showErrorMessage(getContext(), e.toString());
            }
        }, _signUpEntity);
    }

    private void navToVerificationFAFragment() {

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag = new VerificationFragment_FA();
        ft.add(R.id.act_intro_content_frg, frag, AppConstt.FragTag.FN_VerificationFragment_FA);
        ft.addToBackStack(AppConstt.FragTag.FN_VerificationFragment_FA);
        ft.hide(this);
        ft.commit();
    }

    private boolean checkNumberErrorCondition() {
        if (edtPhone.getText().toString().isEmpty()) {
            AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.enter_mobile_number));
            return false;
        } else {
            return true;
        }
    }

    private boolean checkNameErrorCondition() {
        if (edtName.getText().toString().isEmpty()) {
            AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.enter_name));
//        Toast.makeText(getActivity(), "Empty Name Feild", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    //endregion

    private boolean checkCNICErrorCondition() {

        String str = edtCNIC.getText().toString().replaceFirst("^0+(?!$)", "");

        if (edtCNIC.getText().toString().isEmpty()) {


            AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.enter_cnic));
//        Toast.makeText(getActivity(), "Empty Name Feild", Toast.LENGTH_SHORT).show();
            return false;
        } else if (str.substring(1).equalsIgnoreCase("O")) {
            AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.invalid_cnic));
//        Toast.makeText(getActivity(), "Empty Name Feild", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    //region  functions for Dialog
    private void dismissProgDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void showProgDialog() {
        progressDialog = new Dialog(getActivity(), R.style.AppTheme);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setContentView(R.layout.dialog_progress);

        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    //endregion


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
            return false;
        } else {
            return true;
        }
    }
}



