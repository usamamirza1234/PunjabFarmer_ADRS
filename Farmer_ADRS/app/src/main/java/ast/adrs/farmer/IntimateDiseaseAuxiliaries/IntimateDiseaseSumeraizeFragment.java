package ast.adrs.farmer.IntimateDiseaseAuxiliaries;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import ast.adrs.farmer.AppConfig;
import ast.adrs.farmer.IntimateDiseaseAuxiliaries.WebServices.Home_WebHit_Post_DiseaseByFarmer;
import ast.adrs.farmer.IntroAuxiliaries.DModel_Animals;
import ast.adrs.farmer.IntroAuxiliaries.FarmProfileFragment;
import ast.adrs.farmer.IntroAuxiliaries.WebServices.Intro_WebHit_POST_FarmFarm_GetByFarmerID;
import ast.adrs.farmer.IntroAuxiliaries.WebServices.Intro_WebHit_Post_Add_Farm;
import ast.adrs.farmer.MainActivity;
import ast.adrs.farmer.R;
import ast.adrs.farmer.Utils.AppConstt;
import ast.adrs.farmer.Utils.CircleImageView;
import ast.adrs.farmer.Utils.CustomAlertConfirmationInterface;
import ast.adrs.farmer.Utils.CustomAlertDialog;
import ast.adrs.farmer.Utils.CustomToast;
import ast.adrs.farmer.Utils.GPSTracker;
import ast.adrs.farmer.Utils.IBadgeUpdateListener;
import ast.adrs.farmer.Utils.IWebCallback;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

//import com.github.dhaval2404.imagepicker.ImagePicker;


/*
 * Created by Usama Khalid Mirza on 06/05/2021.
 * usamamirza.andpercent@gmail.com
 */

public class IntimateDiseaseSumeraizeFragment extends Fragment implements View.OnClickListener {
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int PERMISSIONS_REQUEST_RECEIVE_SMS = 0;
    private final int REQUEST_LOCATION_PERMISSION = 1;
    RelativeLayout rlBack;
    RecyclerView rcv_option;
    View view;
    CircleImageView civ_selctedAnimal;
    ImageView imv_selctedAnimal;
    RelativeLayout rlTakePic;
    RelativeLayout rlSubmit, rlChosseExisting, rlAddNewFarm;
    RelativeLayout rlAddFarmDone;
    CustomAlertDialog customAlertDialog;
    LinearLayout ll_Choose_Existing_Farm, ll_Add_New_farm;
    TextView txvSelected, txvFarm;
    TextView txvSelected_Disease;
    Spinner spinnerFarm = null;
    EditText edt_sick, edt_dead, edt_total, edtAddafarm;
    double latitude, longitude;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    private ArrayList<DModel_Animals> lstFarms;
    private IBadgeUpdateListener mBadgeUpdateListener;
    private Dialog progressDialog;
    String encodedImage;

    public IntimateDiseaseSumeraizeFragment() {
    }
    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_intimate_diease_summerize, container, false);

        initData();
        bindViews();
        requestLocationPermission();


        if (!AppConfig.getInstance().isInternetEnabled()) {
            if (AppConfig.getInstance().mUserData.isFarmer()) {
                populatespinnerFarm();
            }
        } else {
            populateFarm();

        }


        LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);


        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }


        Log.d("GPS_TRACKER", "gps_enabled : " + gps_enabled);
        Log.d("GPS_TRACKER", "network_enabled : " + network_enabled);
        try {

            String upperString = AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.getAnimalType().substring(0, 1).toUpperCase() + AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.getAnimalType().substring(1).toLowerCase();
            txvSelected.setText(upperString);


            txvSelected_Disease.setVisibility(View.VISIBLE);
            txvSelected_Disease.setText(AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.getDisease());


            switch (AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.getAnimalIndex()) {
                case "0":
                    civ_selctedAnimal.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_large));
                    break;
                case "1":
                    civ_selctedAnimal.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_small));
                    break;
                case "2":
                    civ_selctedAnimal.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_camel));
                    break;
                case "3":
                    civ_selctedAnimal.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_equine));
                    break;
                case "4":
                    civ_selctedAnimal.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_poultry));
                    break;
            }


            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            listIssuesRcvAdapter listIssuesRcvAdapter = new listIssuesRcvAdapter(getActivity(), AppConfig.getInstance().lst_SelectedSyb);
            rcv_option.setLayoutManager(linearLayoutManager);
            rcv_option.setAdapter(listIssuesRcvAdapter);


        } catch (Exception e) {

        }


        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);


        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }


        try {
            GPSTracker.getInstance(getContext());
            longitude = GPSTracker.getInstance(getContext()).getLongitude();
            latitude = GPSTracker.getInstance(getContext()).getLatitude();

            Log.d("GPS_TRACKER", "Lat : " + GPSTracker.getInstance(getContext()).getLatitude());
            Log.d("GPS_TRACKER", "Long : " + GPSTracker.getInstance(getContext()).getLongitude());
            Log.d("GPS_TRACKER", "Loc : " + GPSTracker.getInstance(getContext()).getLocation());
        } catch (Exception e) {
            Log.d("GPS_TRACKER", "e : " + e.getMessage());
        }

        Log.d("GPS_TRACKER", "onResume gps_enabled : " + gps_enabled);
        Log.d("GPS_TRACKER", "onResume network_enabled : " + network_enabled);
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

    //region Init
    void setBottomBar() {
        try {
            mBadgeUpdateListener = (IBadgeUpdateListener) getActivity();
        } catch (ClassCastException castException) {
            castException.printStackTrace(); // The activity does not implement the listener
        }
        if (getActivity() != null && isAdded()) {

            mBadgeUpdateListener.setToolbarState(AppConstt.ToolbarState.TOOLBAR_BACK_TITLE_VISIBLE);
            mBadgeUpdateListener.setHeaderTitle("Intimate Disease");
        }
    }


    private void initData() {
        setBottomBar();
        lstFarms = new ArrayList<>();

    }

    private void bindViews() {


        edt_dead = view.findViewById(R.id.frg_intimate_disease_summary_edt_dead_animal);
        edt_sick = view.findViewById(R.id.frg_intimate_disease_summary_edt_sick_animal);
        edt_total = view.findViewById(R.id.frg_intimate_disease_summary_edt_total_animal);
        edtAddafarm = view.findViewById(R.id.frg_intimate_disease_summary_txv_add_farm);

        rcv_option = view.findViewById(R.id.frg_intimate_disease_summary_rcv_populatin);
        txvFarm = view.findViewById(R.id.frg_intimate_disease_summary_txv_gender);
        civ_selctedAnimal = view.findViewById(R.id.frg_intimate_disease_summary_img_selected_animal);
        imv_selctedAnimal = view.findViewById(R.id.frg_intimate_disease_summary_imv_aniamal);
        txvSelected = view.findViewById(R.id.frg_intimate_disease_summary_txv_selected);
        txvSelected_Disease = view.findViewById(R.id.frg_intimate_disease_summary_txv_selected_disease);
        spinnerFarm = view.findViewById(R.id.frg_intimate_disease_summary_spinner_gender);


        rlTakePic = view.findViewById(R.id.frg_intimate_disease_summary_rl_take_picture);
        rlSubmit = view.findViewById(R.id.frg_intimate_disease_summary_rl_Submit);
        rlChosseExisting = view.findViewById(R.id.frg_intimate_disease_summary_rl_choose_exisiting_farm);
        rlAddNewFarm = view.findViewById(R.id.frg_intimate_disease_summary_rl_add_new_farm);
        rlAddFarmDone = view.findViewById(R.id.frg_intimate_disease_summary_rl_add_new_farm_done);

        ll_Choose_Existing_Farm = view.findViewById(R.id.frg_intimate_disease_summary_ll_choose_existing_farm);
        ll_Add_New_farm = view.findViewById(R.id.frg_intimate_disease_summary_ll_add_new_farm);

        rlBack = view.findViewById(R.id.rlBack);
        rlBack.setOnClickListener(this);

        rlAddNewFarm.setOnClickListener(this);
        rlChosseExisting.setOnClickListener(this);
        rlTakePic.setOnClickListener(this);
        rlSubmit.setOnClickListener(this);
        rlAddFarmDone.setOnClickListener(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            setBottomBar();
            populateFarm();
        }
    }
    //endregion

    //region  ApiRequests


    private void populateFarm() {

        int id = 0;

        if (AppConfig.getInstance().mUserData.isFarmer()) {
            id = AppConfig.getInstance().mUserData.getId();
        } else
            id = Integer.parseInt(AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.getFarmerID());

        showProgDialog();
        Intro_WebHit_POST_FarmFarm_GetByFarmerID intro_webHit_post_farmFarm_getByFarmerID = new Intro_WebHit_POST_FarmFarm_GetByFarmerID();
        intro_webHit_post_farmFarm_getByFarmerID.getFarmFarm_GetByFarmerID(getContext(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (getActivity() != null) {
                    if (isSuccess) {
                        lstFarms.clear();
                        if (Intro_WebHit_POST_FarmFarm_GetByFarmerID.responseObject != null &&
                                Intro_WebHit_POST_FarmFarm_GetByFarmerID.responseObject.getResult() != null)
                        {


                            if (Intro_WebHit_POST_FarmFarm_GetByFarmerID.responseObject.getResult().size() > 0)
                            {

                                for (int i = 0; i < Intro_WebHit_POST_FarmFarm_GetByFarmerID.responseObject.getResult().size(); i++) {


                                    DModel_Animals dModel_animals = new DModel_Animals(String.valueOf(Intro_WebHit_POST_FarmFarm_GetByFarmerID.responseObject.getResult().get(i)
                                            .getId()),
                                            String.valueOf(Intro_WebHit_POST_FarmFarm_GetByFarmerID.responseObject.getResult().get(i).getFarmName()));

                                    Log.d("listing", "getFarmList equals " + Intro_WebHit_POST_FarmFarm_GetByFarmerID.responseObject.getResult().get(i)
                                            .getId());
                                    lstFarms.add(dModel_animals);

                                }


                                DModel_Animals dModel_animals = new DModel_Animals("0", getString(R.string.select_farm));
                                lstFarms.add(dModel_animals);


                                if (AppConfig.getInstance().getFarmList().size() > 0) {
                                    AppConfig.getInstance().getFarmList().clear();
                                }
                                AppConfig.getInstance().saveFarmList(lstFarms);

                                populatespinnerFarm();

                            }
                            else
                            {
                                dismissProgDialog();
                                AppConfig.getInstance().showErrorMessage(getContext(), getResources().getString(R.string.no_farm_exist));

                            }

                        }

                    }
                    else {
                        dismissProgDialog();
                        AppConfig.getInstance().showErrorMessage(getContext(), strMsg);
                    }
                }
                dismissProgDialog();
            }

            @Override
            public void onWebException(Exception e) {
                dismissProgDialog();
                Log.d("LOG_AS", "VA Registration Exception: " + e.getMessage());

                AppConfig.getInstance().showErrorMessage(getContext(), e.toString());
            }
        }, id);


    }

    private void navToGenerateReportIntemationFragment() {
        Fragment frg = new GenerateReportIntemationFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();


        ft.add(R.id.act_main_content_frg, frg, AppConstt.FragTag.FN_DiseaseSummeryFarmerFragment);
        ft.addToBackStack(AppConstt.FragTag.FN_DiseaseSummeryFarmerFragment);

        ft.hide(this);
        ft.commit();
    }

    private void populatespinnerFarm() {
        SpinnerFarmAdapter spinnerFarmAdapter = null;

        if (!AppConfig.getInstance().isInternetEnabled()) {
            if (AppConfig.getInstance().mUserData.isFarmer()) {
                spinnerFarmAdapter = new SpinnerFarmAdapter(getContext(), AppConfig.getInstance().getFarmList());
            }
        } else {

            spinnerFarmAdapter = new SpinnerFarmAdapter(getContext(), lstFarms);

        }


        spinnerFarm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                int Pos = Integer.parseInt(selectedItem);
                txvFarm.setText(AppConfig.getInstance().getFarmList().get(position).getName());
                edtAddafarm.setText(AppConfig.getInstance().getFarmList().get(position).getName());


                AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.setFarmId(AppConfig.getInstance().getFarmList().get(position).getId());
                AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.setFarm(AppConfig.getInstance().getFarmList().get(position).getName());


            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerFarm.setAdapter(spinnerFarmAdapter);
        spinnerFarm.setSelection(spinnerFarm.getCount());
    }
    //endregion

    //region  ValidationChecks
    private boolean checkSpinnerAndEditTestCondition() {

        if (txvFarm.getText().toString().isEmpty() && edtAddafarm.getText().toString().isEmpty()) {
            AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.add_farm_please));
            return false;
        } else {
            return true;
        }
    }

    private boolean checkSpinnerErrorCondition() {
        if (txvFarm.getText().toString().equalsIgnoreCase(getContext().getString(R.string.select_farm))) {
            AppConfig.getInstance().showErrorMessage(getContext(), getContext().getString(R.string.select_farm));
            return false;
        } else {
            return true;
        }
    }

    private boolean checkSickErrorCondition() {
        if (edt_sick.getText().toString().isEmpty()) {
            AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.sick_animal_cant));
            return false;
        } else {
            return true;
        }
    }

    private boolean checkTotalErrorCondition() {
        if (edt_total.getText().toString().isEmpty()) {
            AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.total_animal_cant));
            return false;
        } else {
            return true;
        }
    }

    private boolean checkTotalSickGreaterErrorCondition() {


        long total, sick, dead;
        try {
            total = Integer.valueOf(edt_total.getText().toString());
            sick = Integer.valueOf(edt_sick.getText().toString());

            if (total < sick) {
                AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.total_never_less));
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.invalid_animal_count));
        }
        return false;
    }

//    private boolean checkTotalDeadGreaterErrorCondition() {
//
//
//        long total, sick, dead;
//        try {
//            total = Integer.valueOf(edt_total.getText().toString());
//
//            dead = Integer.valueOf(edt_dead.getText().toString());
//            if (total < dead) {
//                AppConfig.getInstance().showErrorMessage(getContext(), "Total never Less than dead animal");
//                return false;
//            } else {
//                return true;
//            }
//        } catch (Exception e) {
//            AppConfig.getInstance().showErrorMessage(getContext(), "Invalid Animal Count entered");
//        }
//        return false;
//    }

//    private boolean checkSpinner_2ErrorCondition() {
//
//        if (lstFarms.size() <= 0 && edtAddafarm.getText().toString().isEmpty()) {
//            AppConfig.getInstance().showErrorMessage(getContext(), "Add a farm");
//            return false;
//        } else {
//            return true;
//        }
//    }


//    private boolean checkDeadErrorCondition() {
//        if (edt_dead.getText().toString().isEmpty()) {
//            AppConfig.getInstance().showErrorMessage(getContext(), "Empty Dead Animal field");
//            return false;
//        } else {
//            return true;
//        }
//    }
    //endregion

    //region PermissionsAccess
    //region Permissions


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
//            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            imv_selctedAnimal.setImageBitmap(photo);
//            imv_selctedAnimal.setVisibility(View.VISIBLE);


            Uri selectedImageUri = data.getData();
            Bitmap selectedImageBitmap = null;
            try {
                selectedImageBitmap = (Bitmap) data.getExtras().get("data");
            } catch (Exception e) {
                e.printStackTrace();
            }
            imv_selctedAnimal.setImageBitmap(selectedImageBitmap);
            imv_selctedAnimal.setVisibility(View.VISIBLE);


            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArrayImage = byteArrayOutputStream.toByteArray();
            encodedImage = Base64.encodeToString(byteArrayImage, Base64.NO_WRAP);
            Log.d("Vicky", "I'm in.");


//            try {
//                Log.d("imv_selctedAnimal", "data: " + data.getExtras().get("data"));
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                byte[] byteArray = stream.toByteArray();
////                if (photo != null && !photo.isRecycled()) {
////                    photo.recycle();
////                    photo = null;
////                }
//
////                photo.recycle();
//                Log.d("imv_selctedAnimal", "byteArray: " + byteArray);
//            } catch (Exception e) {
//                Log.d("imv_selctedAnimal", "Exception: Failed to convert img to byte " + e.getMessage());
//            }
//
//
//            try {
//                Log.d("imv_selctedAnimal", "data: " + data.getExtras().get("data"));
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                photo.compress(Bitmap.CompressFormat.JPEG, 100, stream); // bm is the bitmap object
//                byte[] b = stream.toByteArray();
////                if (photo != null && !photo.isRecycled()) {
////                    photo.recycle();
////                    photo = null;
////                }
//
////                photo.recycle();
//                Log.d("imv_selctedAnimal", "byteArray: " + b);
//
//
//                String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
//
//                Log.d("imv_selctedAnimal", "byteArray:encodedImage " + encodedImage.trim());
//            } catch (Exception e) {
//                Log.d("imv_selctedAnimal", "Exception: Failed to convert img to byte " + e.getMessage());
//            }


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        Log.d("GPS_TRACKER", " : requestCode " + requestCode);
        switch (requestCode) {

            case MY_CAMERA_PERMISSION_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CustomToast.showToastMessage(getActivity(), "Camera permission granted", Toast.LENGTH_LONG);
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                } else {
                    CustomToast.showToastMessage(getActivity(), "Camera permission denied", Toast.LENGTH_LONG);

                }
            }
            break;
            case REQUEST_LOCATION_PERMISSION: {
                EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
            }
            break;
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


            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
//            Toast.makeText(getActivity(), "Permission already granted", Toast.LENGTH_SHORT).show();
            GPSTracker.getInstance(getContext());
            if (GPSTracker.getInstance(getContext()).getLatitude() != 0) {

                longitude = GPSTracker.getInstance(getContext()).getLongitude();
                latitude = GPSTracker.getInstance(getContext()).getLatitude();

                Log.d("GPS_TRACKER", "Lat : " + GPSTracker.getInstance(getContext()).getLatitude());
                Log.d("GPS_TRACKER", "Long : " + GPSTracker.getInstance(getContext()).getLongitude());
                Log.d("GPS_TRACKER", "Loc : " + GPSTracker.getInstance(getContext()).getLocation());
//                return true;
            }
//            return false;
        } else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
//            return false;
        }
    }

    //endregion
    //region  Camera
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void takePhoto() {
        if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        } else {

            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }


    }
    //endregion
    //endregion


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.frg_intimate_disease_summary_rl_take_picture:
                takePhoto();
                break;


            case R.id.frg_intimate_disease_summary_rl_Submit:
                if (network_enabled && gps_enabled) {
                    if (checkSpinnerAndEditTestCondition())
                        if (checkSpinnerErrorCondition())
                            if (checkSickErrorCondition())
                                if (checkTotalErrorCondition())
                                    if (checkTotalSickGreaterErrorCondition())
                                        requestSubmitDisease();
                } else {
                    showErrorForLocationMessage(getContext(), getResources().getString(R.string.gps_network_not_enabled));
                }
                break;

            case R.id.frg_intimate_disease_summary_rl_choose_exisiting_farm:
                ll_Choose_Existing_Farm.setVisibility(View.VISIBLE);
                ll_Add_New_farm.setVisibility(View.GONE);
                populatespinnerFarm();
                break;


            case R.id.frg_intimate_disease_summary_rl_add_new_farm:
                edtAddafarm.setText("");
                txvFarm.setText("");
                if (AppConfig.getInstance().getFarmList().size() > 0)
                    spinnerFarm.setSelection(spinnerFarm.getCount());
                navToAddFarmDetailFragment();
                break;
            case R.id.rlBack:
                getActivity().onBackPressed();
                break;
            default:
                break;
        }
    }

    private void navToAddFarmDetailFragment() {

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag = new FarmProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putDouble("key_lat", 0);
        bundle.putDouble("key_long", 0);
        AppConfig.getInstance().isComingfromIntro = false;
        ft.add(R.id.act_main_content_frg, frag, AppConstt.FragTag.FN_FarmProfileFragment);
        ft.addToBackStack(AppConstt.FragTag.FN_FarmProfileFragment);
        frag.setArguments(bundle);
        ft.hide(this);
        ft.commit();
    }

    private void requestSubmitDisease() {


        String id = "";
        for (int i = 0; i < AppConfig.getInstance().lst_SelectedSyb.size(); i++) {

            if (i == 0) {
                id = id + AppConfig.getInstance().lst_SelectedSyb.get(i).getId();
            } else
                id = id + "," + AppConfig.getInstance().lst_SelectedSyb.get(i).getId();

        }
        Log.d("LOG_AS", "id " + id);


        String name = "", cnic = "", mobileNumber = "", farmerID = "", gender = "", mouzaID = "";

        if (AppConfig.getInstance().mUserData.isFarmer()) {
            name = AppConfig.getInstance().mUserData.getName();
            cnic = AppConfig.getInstance().mUserData.getCNIC();
            mobileNumber = AppConfig.getInstance().mUserData.getPhone();
            farmerID = String.valueOf(AppConfig.getInstance().mUserData.getId());
            mouzaID = String.valueOf(AppConfig.getInstance().mUserData.getMozahID());
            gender = String.valueOf(AppConfig.getInstance().mUserData.getGenderID());
        }


        String dead = edt_dead.getText().toString();

        if (edt_dead.getText().toString().isEmpty()) {
            dead = "0";
        }
        GPSTracker.getInstance(getContext());
        if (GPSTracker.getInstance(getContext()).getLatitude() != 0) {

            longitude = GPSTracker.getInstance(getContext()).getLongitude();
            latitude = GPSTracker.getInstance(getContext()).getLatitude();

            Log.d("GPS_TRACKER", "Lat : " + GPSTracker.getInstance(getContext()).getLatitude());
            Log.d("GPS_TRACKER", "Long : " + GPSTracker.getInstance(getContext()).getLongitude());
            Log.d("GPS_TRACKER", "Loc : " + GPSTracker.getInstance(getContext()).getLocation());
//                return true;
        }
        String data = "{" +


                "\"name\"" + ":\"" + name + "\"," +

                "\"sickAnimalImage\"" + ":\"" + encodedImage + "\"," +
                "\"cnic\"" + ":\"" + cnic + "\"," +
                "\"mobileNumber\"" + ":\"" + mobileNumber + "\"," +


                "\"farmerID\"" + ":" + farmerID + "," +
                "\"selectedSymptoms\"" + ":[" + id + "]," +


                "\"longitude\"" + ":" + longitude + "," +
                "\"latitude\"" + ":" + latitude + "," +
                "\"diseaseID\"" + ":" + AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.getDiseaseID() + "," +
                "\"specieID\"" + ":" + AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.getSpecieID() + "," +
                "\"farmId\"" + ":" + AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.getFarmId() + "," +


                "\"totalAnimals\"" + ":" + edt_total.getText().toString() + "," +
                "\"sickAnimals\"" + ":" + edt_sick.getText().toString() + "," +
                "\"deadAnimals\"" + ":" + dead + "," +


                "\"gender\"" + ":" + gender + "," +
                "\"mouzaID\"" + ":" + mouzaID +


                "}";


        Log.d("LOG_AS", "postSignUp: " + data);


        if (AppConfig.getInstance().isInternetEnabled()) {
            showProgDialog();
            Home_WebHit_Post_DiseaseByFarmer home_webHit_post_diseaseByFarmer = new Home_WebHit_Post_DiseaseByFarmer();

            home_webHit_post_diseaseByFarmer.sendDiseaseIntimationByFarmer(getContext(), new IWebCallback() {
                @Override
                public void onWebResult(boolean isSuccess, String strMsg) {
                    dismissProgDialog();
                    if (isSuccess) {
                        if (Home_WebHit_Post_DiseaseByFarmer.responseObject != null &&
                                Home_WebHit_Post_DiseaseByFarmer.responseObject.getResult() != null) {

                            AppConfig.getInstance().dModelFarmerAnimalReport.setReportID(Home_WebHit_Post_DiseaseByFarmer.responseObject.getResult().getReportID());
                            AppConfig.getInstance().dModelFarmerAnimalReport.setName(Home_WebHit_Post_DiseaseByFarmer.responseObject.getResult().getName());
                            AppConfig.getInstance().dModelFarmerAnimalReport.setFarmId(Home_WebHit_Post_DiseaseByFarmer.responseObject.getResult().getFarmId());
                            AppConfig.getInstance().dModelFarmerAnimalReport.setSpecieID(Home_WebHit_Post_DiseaseByFarmer.responseObject.getResult().getSpecieID());
                            AppConfig.getInstance().dModelFarmerAnimalReport.setDiseaseID(Home_WebHit_Post_DiseaseByFarmer.responseObject.getResult().getDiseaseID());
                            AppConfig.getInstance().dModelFarmerAnimalReport.setTotalAnimals(Home_WebHit_Post_DiseaseByFarmer.responseObject.getResult().getTotalAnimals());
                            AppConfig.getInstance().dModelFarmerAnimalReport.setDeadAnimals(Home_WebHit_Post_DiseaseByFarmer.responseObject.getResult().getDeadAnimals());
                            AppConfig.getInstance().dModelFarmerAnimalReport.setSickAnimals(Home_WebHit_Post_DiseaseByFarmer.responseObject.getResult().getSickAnimals());
                            if (Home_WebHit_Post_DiseaseByFarmer.responseObject.getResult().getSickAnimalImage() != null)
                                AppConfig.getInstance().dModelFarmerAnimalReport.setSickAnimalImage(Home_WebHit_Post_DiseaseByFarmer.responseObject.getResult().getSickAnimalImage());
                            AppConfig.getInstance().dModelFarmerAnimalReport.setDiseaseIntimationID(Home_WebHit_Post_DiseaseByFarmer.responseObject.getResult().getDiseaseIntimationID());
                            AppConfig.getInstance().dModelFarmerAnimalReport.setSelectedSymptoms(Home_WebHit_Post_DiseaseByFarmer.responseObject.getResult().getSelectedSymptoms());
                            AppConfig.getInstance().dModelFarmerAnimalReport.setLatitude(Home_WebHit_Post_DiseaseByFarmer.responseObject.getResult().getLatitude());
                            AppConfig.getInstance().dModelFarmerAnimalReport.setLongitude(Home_WebHit_Post_DiseaseByFarmer.responseObject.getResult().getLongitude());
                            AppConfig.getInstance().dModelFarmerAnimalReport.setMouzaID(Home_WebHit_Post_DiseaseByFarmer.responseObject.getResult().getMouzaID());
                            AppConfig.getInstance().dModelFarmerAnimalReport.setGender(Home_WebHit_Post_DiseaseByFarmer.responseObject.getResult().getGender());
                            AppConfig.getInstance().dModelFarmerAnimalReport.setMobileNumber(Home_WebHit_Post_DiseaseByFarmer.responseObject.getResult().getMobileNumber());
                            AppConfig.getInstance().dModelFarmerAnimalReport.setCnic(Home_WebHit_Post_DiseaseByFarmer.responseObject.getResult().getCnic());
                            AppConfig.getInstance().dModelFarmerAnimalReport.setFarmerID(Home_WebHit_Post_DiseaseByFarmer.responseObject.getResult().getFarmerID());


                            navToGenerateReportIntemationFragment();
                            Log.d("LOG_AS", "postSignUp:isSuccess  " + data);
                        } else {
                            AppConfig.getInstance().showErrorMessage(getContext(), strMsg);
                        }

                    } else {
                        dismissProgDialog();
                        AppConfig.getInstance().showErrorMessage(getContext(), strMsg);
                    }
                }

                @Override
                public void onWebException(Exception ex) {
                    dismissProgDialog();
//                CustomToast.showToastMessage(IntroActivity.this, AppConfig.getInstance().getNetworkExceptionMessage(ex.getMessage()), Toast.LENGTH_SHORT);
                    AppConfig.getInstance().showErrorMessage(getContext(), ex.getLocalizedMessage());
                }
            }, data);
        } else {
            AppConfig.getInstance().setFormSubmittionPending(true);
            AppConfig.getInstance().saveSubmittionOFDisease(data);
            showInternetConnectionStableMessage(getContext(), "You have no internet Connection, your report would send as you connected to internet.");

        }


    }


    public void showInternetConnectionStableMessage(Context context, String _errorMsg) {
        customAlertDialog = new CustomAlertDialog(context, "", _errorMsg, "OK", "Deny", false, new CustomAlertConfirmationInterface() {
            @Override
            public void callConfirmationDialogPositive() {


                ((MainActivity) getActivity()).setFirstFragment();
                customAlertDialog.dismiss();
            }

            @Override
            public void callConfirmationDialogNegative() {


                customAlertDialog.dismiss();
            }
        });
        customAlertDialog.show();

    }

    public void showErrorForLocationMessage(Context context, String _errorMsg) {
        customAlertDialog = new CustomAlertDialog(context, "", _errorMsg, "Open location Settings", "Cancel", false, new CustomAlertConfirmationInterface() {
            @Override
            public void callConfirmationDialogPositive() {
                getContext().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                try {
                    requestLocationPermission();
                } catch (Exception e) {
                }
                customAlertDialog.dismiss();
            }

            @Override
            public void callConfirmationDialogNegative() {
                customAlertDialog.dismiss();
            }
        });
        customAlertDialog.show();
    }
}

