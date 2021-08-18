package ast.adrs.farmer.HomeAuxiliares;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ast.adrs.farmer.AppConfig;
import ast.adrs.farmer.IntroAuxiliaries.AnimalPopulationRcvAdapter;
import ast.adrs.farmer.IntroAuxiliaries.DModel_Animals;
import ast.adrs.farmer.IntroAuxiliaries.DModel_District;
import ast.adrs.farmer.IntroAuxiliaries.MyProfileFragment;
import ast.adrs.farmer.IntroAuxiliaries.SpinnerSpicesAdapter;
import ast.adrs.farmer.IntroAuxiliaries.WebServices.Intro_WebHit_POST_FarmPopulation_GetByFarmID_FarmerId;
import ast.adrs.farmer.IntroAuxiliaries.WebServices.Intro_WebHit_Post_Add_Farm;
import ast.adrs.farmer.IntroAuxiliaries.WebServices.Intro_WebHit_Post_Add_Farm_Population;
import ast.adrs.farmer.MainActivity;
import ast.adrs.farmer.R;
import ast.adrs.farmer.Utils.AppConstt;
import ast.adrs.farmer.Utils.GPSTracker;
import ast.adrs.farmer.Utils.IBadgeUpdateListener;
import ast.adrs.farmer.Utils.IWebCallback;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class FarmUpdateFragment extends Fragment implements View.OnClickListener {

    private static final int PERMISSIONS_REQUEST_RECEIVE_SMS = 0;
    private final int REQUEST_LOCATION_PERMISSION = 1;
    EditText edtNoAnimal;
    TextView txvSpices, txvFarmNamr;
    Spinner spinnerSpices = null;
    int farmID = 0;
    boolean isAddClicked = true;

    int spicesID = 0;
    AnimalPopulationRcvAdapter animalPopulationRcvAdapter;
    RelativeLayout rlSubmit, rlAddSpices;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

    LinearLayout llSpices;
    RelativeLayout rlDone;
    double latitude, longitude;
    private ArrayList<DModel_Animals> lstPopulation;
    private IBadgeUpdateListener mBadgeUpdateListener;
    private Dialog progressDialog;


    String strFarmName;
    String strFarmID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View frg = inflater.inflate(R.layout.fragment_farm_update, container, false);

        init();
        bindviews(frg);

        txvFarmNamr.setText(strFarmName);

        populateSpinnerSpices();
        requestLocationPermission();

        return frg;
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


    private void requestAddFarmPopulation(String _signUpEntity) {
        showProgDialog();
        Intro_WebHit_Post_Add_Farm_Population intro_webHit_post_add_farm_population = new Intro_WebHit_Post_Add_Farm_Population();
        intro_webHit_post_add_farm_population.addFarm(getContext(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {
                    dismissProgDialog();


                    llSpices.setVisibility(View.VISIBLE);


//                    if (AppConfig.getInstance().isComingfromIntro) {
//                        llPopulaion.setVisibility(View.VISIBLE);
//                        llSpices.setVisibility(View.VISIBLE);
//                        rlAddFarm.setVisibility(View.GONE);
//
//
//                        requestFarmPopulation();
//                    } else if (!AppConfig.getInstance().isComingfromIntro) {
//                        ((MainActivity) getActivity()).onBackPressed();
//                    }
                } else {
                    dismissProgDialog();

                    AppConfig.getInstance().showErrorMessage(getContext(), strMsg);
                }
            }

            @Override
            public void onWebException(Exception e) {
                dismissProgDialog();
                Log.d("LOG_AS", "VA Registration Exception: " + e.getMessage());

                AppConfig.getInstance().showErrorMessage(getContext(), e.toString());
            }
        }, _signUpEntity);
    }

    private void requestAddFarm() {

        String lang = "";

        if (AppConfig.getInstance().mLanguage.equalsIgnoreCase(AppConstt.AppLang.LANG_UR)) {
            lang = "u";
        } else {
            lang = "e";
        }
        Bundle bundle;
        bundle = this.getArguments();


        String farmerID = "";

        if (AppConfig.getInstance().mUserData.isFarmer()) {

            farmerID = String.valueOf(AppConfig.getInstance().mUserData.getId());

        } else {

            farmerID = String.valueOf(AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.getFarmerID());


        }


        String data = "{" +
                "\"farmerID\"" + ":" + farmerID + "," +
                "\"FarmName\"" + ":\"" + txvFarmNamr.getText().toString() + "\"," +
                "\"specieID\"" + ":" + AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.getSpecieID() + "," +
                "\"createdBy\"" + ":" + AppConfig.getInstance().mUserData.getId() + "," +
                "\"latitude\"" + ":" + latitude + "," +
                "\"longitude\"" + ":" + longitude + "," +
                "\"id\"" + ":" + 0 +

                "}";

    }


    //endregion
    private void addSpicesinFarm() {
        String lang = "";
        if (AppConfig.getInstance().mLanguage.equalsIgnoreCase(AppConstt.AppLang.LANG_UR)) {
            lang = "u";
        } else {
            lang = "e";
        }

        String farmerID = "";

        if (AppConfig.getInstance().mUserData.isFarmer()) {

            farmerID = String.valueOf(AppConfig.getInstance().mUserData.getId());

        } else {

            farmerID = String.valueOf(AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.getFarmerID());


        }

        String data = "{" +
                "\"farmerID\"" + ":" + farmerID + "," +
                "\"noOfAnimals\"" + ":" + edtNoAnimal.getText().toString() + "," +
                "\"specieID\"" + ":" + spicesID + "," +
                "\"id\"" + ":" + 0 + "," +
                "\"createdBy\"" + ":" + AppConfig.getInstance().mUserData.getId() + "," +
                "\"farmID\"" + ":" + farmID + "," +
                "\"preferedLanguage\"" + ":\"" + lang +
                "\"}";


        requestAddFarmPopulation(data);
    }

    ArrayList<DModel_District> lstSpices;

    private void populateSpinnerSpices() {

        lstSpices.clear();
        lstSpices.addAll(AppConfig.getInstance().getSpicesList());
        DModel_District dModel_spices = new DModel_District("0", getString(R.string.select_spices));
        lstSpices.add(dModel_spices);


        SpinnerSpicesAdapter spinnerSpicesAdapter = null;
        spinnerSpicesAdapter = new SpinnerSpicesAdapter(getContext(), lstSpices);
        spinnerSpices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                int Pos = Integer.parseInt(selectedItem);
                txvSpices.setText(AppConfig.getInstance().getSpicesList().get(position).getName());
//                AppConfig.getInstance().mUserData.designationID = Integer.valueOf(AppConfig.getInstance().getSpicesList().get(position).getId());
                spicesID = Integer.valueOf(AppConfig.getInstance().getSpicesList().get(position).getId());

                Log.d("populateSpinnerSpices", " spicesID: " + spicesID);


            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerSpices.setAdapter(spinnerSpicesAdapter);
        spinnerSpices.setSelection(spinnerSpicesAdapter.getCount());
    }

    private void bindviews(View frg) {

        txvSpices = frg.findViewById(R.id.frg_farm_profile_txv_spices);

        txvFarmNamr = frg.findViewById(R.id.txv_add_farm);

        edtNoAnimal = frg.findViewById(R.id.frg_farm_profile_farmer_edt_animals);

        spinnerSpices = frg.findViewById(R.id.frg_farm_profile_spinner_spices);

        rlAddSpices = frg.findViewById(R.id.frg_farm_profile_farmer_add_spices);

        rlDone = frg.findViewById(R.id.frg_farm_profile_farmer_rlDone);

        llSpices = frg.findViewById(R.id.llSpices);


        rlDone.setOnClickListener(this);

        rlAddSpices.setOnClickListener(this);

    }

    private void init() {
        lstPopulation = new ArrayList<>();
        lstSpices = new ArrayList<>();
        setBottomBar();
        Bundle bundle;
        bundle = this.getArguments();

        if (bundle != null) {
            strFarmID = bundle.getString("key_farm_id", "0");
            strFarmName = bundle.getString("key_farm_name", "0");
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!isHidden()) {
            setBottomBar();
        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frg_farm_profile_farmer_rlDone:
                ((MainActivity) getActivity()).setFirstFragment();

                break;
        }
    }

    private void navtoMyProfileFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag = new MyProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isComingFromMain", false);
        ft.add(R.id.act_intro_content_frg, frag, AppConstt.FragTag.FN_MyProfileFragment);
        ft.addToBackStack(AppConstt.FragTag.FN_MyProfileFragment);
        frag.setArguments(bundle);
        ft.hide(this);
        ft.commit();
    }

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
}
