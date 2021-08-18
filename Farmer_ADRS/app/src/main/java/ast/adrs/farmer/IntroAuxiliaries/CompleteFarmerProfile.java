package ast.adrs.farmer.IntroAuxiliaries;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ast.adrs.farmer.AppConfig;
import ast.adrs.farmer.IntroActivity;
import ast.adrs.farmer.IntroAuxiliaries.WebServices.Intro_WebHit_Get_All_District;
import ast.adrs.farmer.IntroAuxiliaries.WebServices.Intro_WebHit_Get_All_Mozah;
import ast.adrs.farmer.IntroAuxiliaries.WebServices.Intro_WebHit_Get_All_Tehsil;
import ast.adrs.farmer.IntroAuxiliaries.WebServices.Intro_WebHit_Post_Save_Farmer;
import ast.adrs.farmer.IntroAuxiliaries.WebServices.Intro_WebHit_Post_Save_GetFarmerByCNIC;
import ast.adrs.farmer.IntroAuxiliaries.WebServices.Intro_WebHit_Post_Token;
import ast.adrs.farmer.IntroAuxiliaries.WebServices.Intro_WebHit_Post_Update_Farmer;
import ast.adrs.farmer.MainActivity;
import ast.adrs.farmer.R;
import ast.adrs.farmer.Utils.AppConstt;
import ast.adrs.farmer.Utils.CircleImageView;
import ast.adrs.farmer.Utils.CustomToast;
import ast.adrs.farmer.Utils.GPSTracker;
import ast.adrs.farmer.Utils.IBadgeUpdateListener;
import ast.adrs.farmer.Utils.IWebCallback;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;

public class CompleteFarmerProfile extends Fragment implements View.OnClickListener {

    private static final int CAMERA_REQUEST = 1888;
    private static final int PERMISSIONS_REQUEST_RECEIVE_SMS = 0;
    private final int REQUEST_LOCATION_PERMISSION = 1;
    Calendar calendar;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    DatePickerDialog.OnDateSetListener date;
    TextView txvName, txvCNIC, txvPhone, txvGender, txvTehsil, txvDistrict, txvMozah;
    EditText edtWhatsapp, edtFatherName;
    RelativeLayout rlRegisterVA;
    LinearLayout llTehsil, llMozuh;
    Spinner spinnerGender = null;
    Spinner spinnerDistrict = null;
    Spinner spinnerTehsil = null;
    Spinner spinnerMozah = null;
    boolean isCommingFromIntro;
    SpinnerAdapter spinnerAdapter;
    boolean isLoadFirst;
    boolean isLoadFirst_Mozah;
    CircleImageView civFarmerImv, civEmpImv;
    ArrayList<DModel_District> lst_Tehsil = new ArrayList<>();
    ArrayList<DModel_District> lst_Mozah = new ArrayList<>();
    double latitude, longitude;
    private IBadgeUpdateListener mBadgeUpdateListener;

    Bundle bundle;
    private Dialog progressDialog;
    byte[] byteArrayImage=null;
    ArrayList<DModel_District> lst_District;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View frg = inflater.inflate(R.layout.fragment_complete_profile, container, false);
        initData();
        bindViews(frg);
        requestData();
        requestLocationPermission();

        String upperString = AppConfig.getInstance().mUserData.getName().substring(0, 1).toUpperCase() + AppConfig.getInstance().mUserData.getName().substring(1).toLowerCase();
        txvName.setText(upperString);


        String input = String.valueOf(AppConfig.getInstance().mUserData.getCNIC());     //input string
        String sixTewelveChars = "";     //substring containing first 4 characters
        String firstFiveChars = "";     //substring containing first 4 characters
        String lastChars = "";     //substring containing first 4 characters

        if (input.length() >= 10) {
            firstFiveChars = input.substring(0, 5);
            sixTewelveChars = input.substring(5, 12);
            lastChars = input.substring(12, 13);
            txvCNIC.setText(firstFiveChars + "-" + sixTewelveChars + "-" + lastChars);
        }

        String strPhoneNumber = String.valueOf(AppConfig.getInstance().mUserData.getPhone());
        strPhoneNumber = "" + strPhoneNumber.substring(1);
        strPhoneNumber = "0" + strPhoneNumber.substring(1);
        edtWhatsapp.setText(strPhoneNumber);
        txvPhone.setText(strPhoneNumber);
//        txvCNIC.setText(strCnic);
        Log.d("LOG_AS", "auth token: " + AppConfig.getInstance().mUserData.getAuthToken());
        Log.d("LOG_AS", "refresh token: " + AppConfig.getInstance().mUserData.getRefreshToken());

        if (!AppConfig.getInstance().mUserData.getEncorededImage().isEmpty()) {
            byte[] decodedString = Base64.decode(AppConfig.getInstance().mUserData.getEncorededImage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            civFarmerImv.setImageBitmap(decodedByte);
        }

        return frg;
    }


    //region init

    private void initData() {
        lst_District = new ArrayList<>();
        isCommingFromIntro = false;
        bundle = this.getArguments();

        if (bundle != null) {
            isCommingFromIntro = bundle.getBoolean("isComingFromIntro", false);
        }

    }

    private void requestData() {
        requestDistrict();


    }

    void setBottomBar() {

        try {
            mBadgeUpdateListener = (IBadgeUpdateListener) getActivity();
        } catch (ClassCastException castException) {
            castException.printStackTrace(); // The activity does not implement the listener
        }
        if (getActivity() != null && isAdded())
            mBadgeUpdateListener.setToolbarState(AppConstt.INTRO_ToolbarStates.signinFarmer);

    }

    private void bindViews(View frg) {


        txvGender = frg.findViewById(R.id.frg_complete_profile_txv_gender);

        txvDistrict = frg.findViewById(R.id.frg_complete_profile_txv_district);
        txvTehsil = frg.findViewById(R.id.frg_complete_profile_txv_tehsil);
        civFarmerImv = frg.findViewById(R.id.frg_complete_profile_imv_profile);
        txvName = frg.findViewById(R.id.frg_complete_profile_edt_name);
        txvPhone = frg.findViewById(R.id.frg_complete_profile_edt_phone);
        txvCNIC = frg.findViewById(R.id.frg_complete_profile_edt_cnic);
        txvMozah = frg.findViewById(R.id.frg_complete_profile_txv_mozuh);
        edtWhatsapp = frg.findViewById(R.id.frg_complete_profile_edt_whatsappphone);
        edtFatherName = frg.findViewById(R.id.frg_complete_profile_edt_email);


        llTehsil = frg.findViewById(R.id.frg_complete_profile_ll_tehsil);
        llMozuh = frg.findViewById(R.id.frg_complete_profile_ll_mozuh);
        rlRegisterVA = frg.findViewById(R.id.frg_signin_rl_login);

        spinnerGender = frg.findViewById(R.id.frg_complete_profile_spinner_gender);
        spinnerMozah = frg.findViewById(R.id.frg_complete_profile_spinner_mozuh);
        spinnerDistrict = frg.findViewById(R.id.frg_complete_profile_spinner_district);
        spinnerTehsil = frg.findViewById(R.id.frg_complete_profile_spinner_tehsil);

        populateSpinners();


        llTehsil.setOnClickListener(this);
        rlRegisterVA.setOnClickListener(this);
        civFarmerImv.setOnClickListener(this);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!isHidden()) {
            setBottomBar();
        }
    }

    //endregion

    //region  PopulateSpinners
    private void populateSpinners() {
        ArrayList<String> lstGender = new ArrayList<>();
        lstGender.add(getResources().getString(R.string.male));
        lstGender.add(getResources().getString(R.string.female));
        lstGender.add(getResources().getString(R.string.select_gender));
        spinnerAdapter = new SpinnerAdapter(getContext(), lstGender);
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                int Pos = Integer.parseInt(selectedItem);
                txvGender.setText(lstGender.get(position));


            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerGender.setAdapter(spinnerAdapter);
        spinnerGender.setSelection(spinnerAdapter.getCount());


    }

    private void populateSpinnerMozah() {

        SpinnerMozaAdapter spinnerMozahAdapter = null;
        spinnerMozahAdapter = new SpinnerMozaAdapter(getContext(), lst_Mozah);
        spinnerMozah.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                int Pos = Integer.parseInt(selectedItem);
                txvMozah.setText(lst_Mozah.get(position).getName());
                AppConfig.getInstance().mUserData.setMozah(lst_Mozah.get(position).getName());
                AppConfig.getInstance().mUserData.setMozahID(Integer.valueOf(lst_Mozah.get(position).getId()));


            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerMozah.setAdapter(spinnerMozahAdapter);
        spinnerMozah.setSelection(spinnerMozahAdapter.getCount());
    }

    private void populateSpinnerDistrict() {
        SpinnerDistrictAdapter spinnerDistrictAdapter = null;
        spinnerDistrictAdapter = new SpinnerDistrictAdapter(getContext(), lst_District);
        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                int Pos = Integer.parseInt(selectedItem);
                txvDistrict.setText(lst_District.get(position).getName());
                AppConfig.getInstance().mUserData.setDistrictID(Integer.valueOf(lst_District.get(position).getId()));
                AppConfig.getInstance().mUserData.setDestrict((lst_District.get(position).getName()));
                lst_Tehsil.clear();
                requestTehsil(lst_District.get(position).getId());

                try {
                    spinnerMozah.setSelection(0);
//                    spinnerMozah.setVisibility(View.GONE);
                    llMozuh.setVisibility(View.GONE);

                }
                catch (Exception e)
                {

                }

            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerDistrict.setAdapter(spinnerDistrictAdapter);
        spinnerDistrict.setSelection(spinnerDistrictAdapter.getCount());

    }

    private void populateSpinnerTehsil() {

        SpinnerTehsilAdapter spinnerTehsilAdapter = null;
        spinnerTehsilAdapter = new SpinnerTehsilAdapter(getContext(), lst_Tehsil);
        spinnerTehsil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                int Pos = Integer.parseInt(selectedItem);
                txvTehsil.setText(lst_Tehsil.get(position).getName());
                AppConfig.getInstance().mUserData.setTehsilID(Integer.valueOf(lst_Tehsil.get(position).getId()));
                AppConfig.getInstance().mUserData.setTehsil((lst_Tehsil.get(position).getName()));


                lst_Mozah.clear();
                DModel_District dModel_tehsil = new DModel_District("0", getString(R.string.select_mozah));
                lst_Mozah.add(dModel_tehsil);


                requestMozuh(String.valueOf(AppConfig.getInstance().mUserData.getTehsilID()));

            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerTehsil.setAdapter(spinnerTehsilAdapter);
        spinnerTehsil.setSelection(spinnerTehsilAdapter.getCount());


    }
    //endregion

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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {


            case R.id.frg_signin_rl_login:

                checkErrorConditions();
                break;

            case R.id.frg_complete_profile_imv_profile: {

                takePhoto();
            }
            break;
        }
    }


    //region  Permissions
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        Log.d("GPS_TRACKER", " : requestCode " + requestCode);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_RECEIVE_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

//                    NotificationUtil.getInstance().show(this, NotificationUtil.CONTENT_TYPE.INFO,
//                            getResources().getString(R.string.app_name),
//                            "Permission granted!");

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

//                    NotificationUtil.getInstance().show(this, NotificationUtil.CONTENT_TYPE.ERROR,
//                            getResources().getString(R.string.app_name),
//                            "Permission denied! App will not function correctly");
                }
                return;
            }


            case REQUEST_LOCATION_PERMISSION: {
                EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
            }
            break;
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            Bitmap selectedImageBitmap = null;
            try {
                selectedImageBitmap = (Bitmap) data.getExtras().get("data");
            } catch (Exception e) {
                e.printStackTrace();
            }
            civFarmerImv.setImageBitmap(selectedImageBitmap);
            civFarmerImv.setVisibility(View.VISIBLE);


            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byteArrayImage = byteArrayOutputStream.toByteArray();

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void takePhoto() {
        if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        } else {

            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }


    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
//            Toast.makeText(getActivity(), "Permission already granted", Toast.LENGTH_SHORT).show();

            if (GPSTracker.getInstance(getContext()).getLatitude() != 0) {

                longitude = GPSTracker.getInstance(getContext()).getLongitude();
                latitude = GPSTracker.getInstance(getContext()).getLatitude();

                Log.d("GPS_TRACKER", "Lat : " + GPSTracker.getInstance(getContext()).getLatitude());
                Log.d("GPS_TRACKER", "Long : " + GPSTracker.getInstance(getContext()).getLongitude());
                Log.d("GPS_TRACKER", "Loc : " + GPSTracker.getInstance(getContext()).getLocation());
            }

        } else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
        }
    }
    //endregion

    //region  APICALLS
    private void requestTehsil(String _id) {

        if (isLoadFirst) {
            llTehsil.setVisibility(View.VISIBLE);
            showProgDialog();
            Intro_WebHit_Get_All_Tehsil intro_webHit_get_all_tehsil = new Intro_WebHit_Get_All_Tehsil();

            intro_webHit_get_all_tehsil.getTehsil(new IWebCallback() {
                @Override
                public void onWebResult(boolean isSuccess, String strMsg) {
                    dismissProgDialog();
                    if (isSuccess) {
                        if (Intro_WebHit_Get_All_Tehsil.responseObject != null &&
                                Intro_WebHit_Get_All_Tehsil.responseObject.getResult() != null &&
                                Intro_WebHit_Get_All_Tehsil.responseObject.getResult().size() > 0) {

                            lst_Tehsil.clear();


                            for (int i = 0; i < Intro_WebHit_Get_All_Tehsil.responseObject.getResult().size(); i++) {


                                DModel_District dModel_tehsil = new DModel_District(Intro_WebHit_Get_All_Tehsil.responseObject.getResult().get(i).getValueMemeber()
                                        , Intro_WebHit_Get_All_Tehsil.responseObject.getResult().get(i).getDisplayMember());

                                lst_Tehsil.add(dModel_tehsil);
                            }

                            DModel_District dModel_tehsil = new DModel_District("0", getString(R.string.select_tehsil));
                            lst_Tehsil.add(dModel_tehsil);


                            populateSpinnerTehsil();
                        }

                    } else {
                    }
                }

                @Override
                public void onWebException(Exception ex) {
//                CustomToast.showToastMessage(IntroActivity.this, AppConfig.getInstance().getNetworkExceptionMessage(ex.getMessage()), Toast.LENGTH_SHORT);

                }
            }, _id);


        }

        isLoadFirst = true;

    }

    private void requestMozuh(String _id) {

        if (isLoadFirst_Mozah) {
            llMozuh.setVisibility(View.VISIBLE);
            showProgDialog();
            Intro_WebHit_Get_All_Mozah intro_webHit_get_all_mozah = new Intro_WebHit_Get_All_Mozah();

            intro_webHit_get_all_mozah.getMouza(new IWebCallback() {
                @Override
                public void onWebResult(boolean isSuccess, String strMsg) {
                    dismissProgDialog();
                    if (isSuccess) {
                        if (Intro_WebHit_Get_All_Mozah.responseObject != null &&
                                Intro_WebHit_Get_All_Mozah.responseObject.getResult() != null &&
                                Intro_WebHit_Get_All_Mozah.responseObject.getResult().size() > 0) {

                            lst_Mozah.clear();


                            for (int i = 0; i < Intro_WebHit_Get_All_Mozah.responseObject.getResult().size(); i++) {


                                DModel_District dModel_tehsil = new DModel_District(Intro_WebHit_Get_All_Mozah.responseObject.getResult().get(i).getValueMemeber()
                                        , Intro_WebHit_Get_All_Mozah.responseObject.getResult().get(i).getDisplayMember());

                                lst_Mozah.add(dModel_tehsil);
                            }

                            DModel_District dModel_tehsil = new DModel_District("0", getString(R.string.select_mozah));
                            lst_Mozah.add(dModel_tehsil);

                            llMozuh.setVisibility(View.VISIBLE);
                            populateSpinnerMozah();
                        }

                    } else {
                    }
                }

                @Override
                public void onWebException(Exception ex) {
//                CustomToast.showToastMessage(IntroActivity.this, AppConfig.getInstance().getNetworkExceptionMessage(ex.getMessage()), Toast.LENGTH_SHORT);

                }
            }, _id);


        }

        isLoadFirst_Mozah = true;

    }

    private void requestDistrict() {

        Intro_WebHit_Get_All_District intro_webHit_get_all_district = new Intro_WebHit_Get_All_District();

        intro_webHit_get_all_district.getDistrict(new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {

                if (isSuccess) {
                    if (Intro_WebHit_Get_All_District.responseObject != null &&
                            Intro_WebHit_Get_All_District.responseObject.getResult() != null &&
                            Intro_WebHit_Get_All_District.responseObject.getResult().size() > 0) {


                        for (int i = 0; i < Intro_WebHit_Get_All_District.responseObject.getResult().size(); i++) {


                            DModel_District dModel_district = new DModel_District(Intro_WebHit_Get_All_District.responseObject.getResult().get(i).getValueMemeber()
                                    , Intro_WebHit_Get_All_District.responseObject.getResult().get(i).getDisplayMember());

                            lst_District.add(dModel_district);
                        }


                        if (AppConfig.getInstance().getDistrictList() != null) {
                            AppConfig.getInstance().getDistrictList().clear();
                        }
                        AppConfig.getInstance().saveDistrictList(lst_District);

                        DModel_District dModel_district = new DModel_District("0", getString(R.string.select_district));
                        lst_District.add(dModel_district);


                        populateSpinnerDistrict();

                    }

                }
            }

            @Override
            public void onWebException(Exception ex) {
//                CustomToast.showToastMessage(IntroActivity.this, AppConfig.getInstance().getNetworkExceptionMessage(ex.getMessage()), Toast.LENGTH_SHORT);

            }
        });

    }


    private void requestFarmerUpdate() {

        String no_data = "nofarm";
        String data = "{" +
                "\"id\"" + ":" + AppConfig.getInstance().mUserData.getId() + "," +
                "\"latitude\"" + ":" + latitude + "," +
                "\"longitude\"" + ":" + longitude + "," +
                "\"cnic\"" + ":" + AppConfig.getInstance().mUserData.getCNIC() + "," +
                "\"farmerName\"" + ":\"" + AppConfig.getInstance().mUserData.getName() + "\"," +
                "\"fatherName\"" + ":\"" + AppConfig.getInstance().mUserData.getName() + "\"," +
                "\"email\"" + ":\"" + AppConfig.getInstance().mUserData.getEmail() + "\"," +


                "\"farms\"" + ":\"" + no_data + "\"," +
                "\"gps\"" + ":\"" + latitude + "," + longitude + "\"," +

                "\"mobileNumber\"" + ":" + AppConfig.getInstance().mUserData.getPhone() + "," +
                "\"gender\"" + ":" + AppConfig.getInstance().mUserData.getGenderID() + "," +

                "\"district\"" + ":\"" + AppConfig.getInstance().mUserData.getDestrict() + "\"," +
                "\"teshil\"" + ":\"" + AppConfig.getInstance().mUserData.getTehsil() + "\"," +
                "\"mouza\"" + ":\"" + AppConfig.getInstance().mUserData.getMozah() + "\"," +

                "\"designation\"" + ":\"" + AppConfig.getInstance().mUserData.getDesignation() + "\"," +
                "\"mouzaID\"" + ":" + AppConfig.getInstance().mUserData.getMozahID() + "," +
                "\"whatsAppMobileNumber\"" + ":" + AppConfig.getInstance().mUserData.getWhatsapp()
                + "}";

        showProgDialog();
        Intro_WebHit_Post_Update_Farmer intro_webHit_post_update_farmer = new Intro_WebHit_Post_Update_Farmer();
        intro_webHit_post_update_farmer.UpdateFarmer(getContext(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {
                    dismissProgDialog();

                    CustomToast.showToastMessage(getActivity(), getString(R.string.profile_updated), Toast.LENGTH_SHORT);

                    AppConfig.getInstance().mUserData.setGenderID(Integer.parseInt(Intro_WebHit_Post_Update_Farmer.responseObject.getResult().getGender()));
                    AppConfig.getInstance().mUserData.setDestrict((Intro_WebHit_Post_Update_Farmer.responseObject.getResult().getDistrict()));
                    AppConfig.getInstance().mUserData.setTehsil((txvTehsil.getText().toString()));
                    AppConfig.getInstance().mUserData.setMozah((Intro_WebHit_Post_Update_Farmer.responseObject.getResult().getMouza()));
                    AppConfig.getInstance().mUserData.setMozahID(Integer.parseInt(Intro_WebHit_Post_Update_Farmer.responseObject.getResult().getMouzaID()));
                    AppConfig.getInstance().mUserData.setEncorededImage(Base64.encodeToString(byteArrayImage, Base64.NO_WRAP));

                    AppConfig.getInstance().saveUserProfileData();
                    ((MainActivity) getActivity()).setFirstFragment();
                } else {
                    dismissProgDialog();
                    AppConfig.getInstance().showErrorMessage(getContext(), strMsg);

//                    if (strMsg.equalsIgnoreCase("510")) {
//                        Log.d("LOG_AS", "requestFarmerUpdate 510: " + strMsg);
//                        requestrefressToken();
//
//                    } else
//                        AppConfig.getInstance().showErrorMessage(getContext(), strMsg);
                }
            }

            @Override
            public void onWebException(Exception e) {
                dismissProgDialog();
                Log.d("LOG_AS", "requestFarmerUpdate Exception: " + e.getMessage());

                AppConfig.getInstance().showErrorMessage(getContext(), e.toString());
            }
        }, data);

    }

    private void requestGetFarmerByCNIC(String _signUpEntity) {
        showProgDialog();
        Intro_WebHit_Post_Save_GetFarmerByCNIC intro_webHit_post_save_getFarmerByCNIC = new Intro_WebHit_Post_Save_GetFarmerByCNIC();
        intro_webHit_post_save_getFarmerByCNIC.getFarmerByCNIC(getContext(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {
                    dismissProgDialog();


                    if (Intro_WebHit_Post_Save_GetFarmerByCNIC.responseObject != null && Intro_WebHit_Post_Save_GetFarmerByCNIC.responseObject.getResult().size() > 0) {
                        AppConfig.getInstance().mUserData.setId(
                                Intro_WebHit_Post_Save_GetFarmerByCNIC.responseObject.getResult().get(0).getId());
                        AppConfig.getInstance().mUserData.setLoggedIn(true);
                        AppConfig.getInstance().mUserData.setLoggedInTemp(true);
                        AppConfig.getInstance().mUserData.setFarmer(true);
                        AppConfig.getInstance().mUserData.setVA(false);
                        AppConfig.getInstance().saveUserProfileData();
                        ((IntroActivity) getActivity()).navtoMainActivity();

                    } else {
                        requestFarmerSave(_signUpEntity);
                    }

                } else {
                    dismissProgDialog();
                    requestFarmerSave(_signUpEntity);
                }
            }

            @Override
            public void onWebException(Exception e) {
                dismissProgDialog();
                Log.d("LOG_AS", "requestFarmerUpdate Exception: " + e.getMessage());

                AppConfig.getInstance().showErrorMessage(getContext(), e.toString());
            }
        }, AppConfig.getInstance().mUserData.getCNIC());
    }

    private void requestFarmerSave(String _signUpEntity) {
        showProgDialog();
        Intro_WebHit_Post_Save_Farmer intro_webHit_post_save_farmer = new Intro_WebHit_Post_Save_Farmer();
        intro_webHit_post_save_farmer.postFarmerSave(getContext(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {
                    dismissProgDialog();

                    AppConfig.getInstance().mUserData.setId(
                            Intro_WebHit_Post_Save_Farmer.responseObject.getResult().getId());
                    AppConfig.getInstance().mUserData.setLoggedIn(true);
                    AppConfig.getInstance().mUserData.setLoggedInTemp(true);
                    AppConfig.getInstance().mUserData.setFarmer(true);
                    AppConfig.getInstance().mUserData.setVA(false);
                    AppConfig.getInstance().saveUserProfileData();
                    navToAddFarmDetailFragment();
                } else {
                    dismissProgDialog();


                    if (strMsg.equalsIgnoreCase("510")) {
                        Log.d("LOG_AS", "requestFarmerUpdate 510: " + strMsg);
                        requestrefressToken();

                    } else
                        AppConfig.getInstance().showErrorMessage(getContext(), strMsg);
                }
            }

            @Override
            public void onWebException(Exception e) {
                dismissProgDialog();
                Log.d("LOG_AS", "requestFarmerUpdate Exception: " + e.getMessage());

                AppConfig.getInstance().showErrorMessage(getContext(), e.toString());
            }
        }, _signUpEntity);
    }

    private void requestrefressToken() {
        showProgDialog();
        Intro_WebHit_Post_Token intro_webHit_post_token = new Intro_WebHit_Post_Token();
        intro_webHit_post_token.refreshToken(getContext(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {
                    dismissProgDialog();
                    Log.d("LOG_AS", "refreshToken isSuccess: " + strMsg);

                    navToAddFarmDetailFragment();
                } else {
                    Log.d("LOG_AS", "refreshToken isfail: " + strMsg);
                    dismissProgDialog();

                }
            }

            @Override
            public void onWebException(Exception e) {
                dismissProgDialog();
                Log.d("LOG_AS", "refreshToken Exception: " + e.getMessage());

            }
        }, AppConfig.getInstance().mUserData.getRefreshToken());
    }

    //endregion


    //region  Check Validations
    private void checkErrorConditions() {
        if (checkCNICErrorCondition() && checkNumberErrorCondition() &&checkMauzaErrorCondition() &&checkGenderErrorCondition() && checkDesignationErrorCondition() && checkDestrictErrorCondition() && checkTehsilErrorCondition()) {


            if (txvGender.getText().toString().equalsIgnoreCase(getString(R.string.male))) {
                AppConfig.getInstance().mUserData.setGenderID(1);
            } else {
                AppConfig.getInstance().mUserData.setGenderID(2);
            }


            String strPhoneNumber = edtWhatsapp.getText().toString();
            try {
                strPhoneNumber = "92" + strPhoneNumber.substring(1);
            } catch (Exception e) {

            }

            String lang = "";

            if (AppConfig.getInstance().mLanguage.equalsIgnoreCase(AppConstt.AppLang.LANG_UR)) {
                lang = "u";
            } else {
                lang = "e";
            }



            AppConfig.getInstance().mUserData.setWhatsapp(strPhoneNumber);
            Date date = new Date();
            String str_DATE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH).format(date);

            String no_data = "nofarm";
            String data = "{" +
//                "\"id\"" + ":" + 0 + "," +
                    "\"latitude\"" + ":" + latitude + "," +
                    "\"longitude\"" + ":" + longitude + "," +
                    "\"cnic\"" + ":" + AppConfig.getInstance().mUserData.getCNIC() + "," +
                    "\"farmerName\"" + ":\"" + AppConfig.getInstance().mUserData.getName() + "\"," +
                    "\"FarmName\"" + ":\"" + no_data + "\"," +

                    "\"FarmerID\"" + ":" + 1 + "," +

                    "\"fatherName\"" + ":\"" + edtFatherName.getText().toString() + "\"," +
                    "\"mobileNumber\"" + ":" + AppConfig.getInstance().mUserData.getPhone() + "," +
                    "\"gender\"" + ":" + AppConfig.getInstance().mUserData.getGenderID() + "," +
                    "\"createdDate\"" + ":\"" + str_DATE + "\"," +
                    "\"district\"" + ":\"" + AppConfig.getInstance().mUserData.getDestrict() + "\"," +
                    "\"teshil\"" + ":\"" + AppConfig.getInstance().mUserData.getTehsil() + "\"," +
                    "\"mouza\"" + ":\"" + AppConfig.getInstance().mUserData.getMozah() + "\"," +
                    "\"designation\"" + ":\"" + AppConfig.getInstance().mUserData.getDesignation() + "\"," +
                    "\"mouzaID\"" + ":" + AppConfig.getInstance().mUserData.getMozahID() + "," +
                    "\"whatsAppMobileNumber\"" + ":" + AppConfig.getInstance().mUserData.getWhatsapp() + "," +
                    "\"preferedLanguage\"" + ":\"" + lang +
                    "\"}";


            if (isCommingFromIntro)
                requestGetFarmerByCNIC(data);
            else
                requestFarmerUpdate();

        }
    }

    private boolean checkNumberErrorCondition() {
        if (edtWhatsapp.getText().toString().isEmpty()) {
            AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.enter_mobile_whatsapp));
            return false;
        } else {
            return true;
        }
    }


    private boolean checkCNICErrorCondition() {
        if (txvCNIC.getText().toString().isEmpty()) {
            AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.enter_cnic));
//        Toast.makeText(getActivity(), "Empty Name Feild", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }


    private boolean checkGenderErrorCondition() {
        if (txvGender.getText().toString().isEmpty()) {
            AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.select_gender));
//        Toast.makeText(getActivity(), "Empty Name Feild", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkDesignationErrorCondition() {
        if (txvGender.getText().toString().isEmpty()) {
            AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.select_Designation));
//        Toast.makeText(getActivity(), "Empty Name Feild", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkDestrictErrorCondition() {
        if (txvDistrict.getText().toString().isEmpty() || txvDistrict.getText().toString().equalsIgnoreCase(getString(R.string.select_district)) ) {
            AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.select_district));
//        Toast.makeText(getActivity(), "Empty Name Feild", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkTehsilErrorCondition() {
        if (txvTehsil.getText().toString().isEmpty() || txvTehsil.getText().toString().equalsIgnoreCase(getString(R.string.select_tehsil))) {
            AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.select_tehsil));
//        Toast.makeText(getActivity(), "Empty Name Feild", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkMauzaErrorCondition() {
        if (txvMozah.getText().toString().isEmpty() || txvMozah.getText().toString().equalsIgnoreCase(getString(R.string.select_mozah))) {
            AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.select_mozah));
//        Toast.makeText(getActivity(), "Empty Name Feild", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
    //endregion

    private void navToAddFarmDetailFragment() {

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag = new FarmProfileFragment();
        AppConfig.getInstance().isComingfromIntro = true;
        ft.add(R.id.act_intro_content_frg, frag, AppConstt.FragTag.FN_FarmProfileFragment);
        ft.addToBackStack(AppConstt.FragTag.FN_FarmProfileFragment);
        ft.hide(this);
        ft.commit();
    }
}
