package ast.adrs.farmer.IntroAuxiliaries;

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
import ast.adrs.farmer.IntroActivity;
import ast.adrs.farmer.IntroAuxiliaries.WebServices.Intro_WebHit_Get_All_Spices;
import ast.adrs.farmer.IntroAuxiliaries.WebServices.Intro_WebHit_POST_FarmPopulation_GetByFarmID_FarmerId;
import ast.adrs.farmer.IntroAuxiliaries.WebServices.Intro_WebHit_Post_Add_Farm;
import ast.adrs.farmer.IntroAuxiliaries.WebServices.Intro_WebHit_Post_Add_Farm_Population;
import ast.adrs.farmer.R;
import ast.adrs.farmer.Utils.AppConstt;
import ast.adrs.farmer.Utils.GPSTracker;
import ast.adrs.farmer.Utils.IBadgeUpdateListener;
import ast.adrs.farmer.Utils.IWebCallback;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class FarmProfileFragment extends Fragment implements View.OnClickListener {

    private static final int PERMISSIONS_REQUEST_RECEIVE_SMS = 0;
    private final int REQUEST_LOCATION_PERMISSION = 1;
    EditText edtFarmName, edtNoAnimal;
    TextView txvSpices;
    Spinner spinnerSpices = null;
    int farmID = 0;
    boolean isAddClicked = true;
    RelativeLayout rlBack;
    TextView txvSelected, txvFarm;
    int spicesID = 0;
    AnimalPopulationRcvAdapter animalPopulationRcvAdapter;
    RelativeLayout rlAddFarm, rlSubmit, rlAddSpices;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    RecyclerView rcv_populatin;
    LinearLayout llSpices, llPopulaion,llNext;
    RelativeLayout rlDone;
    double latitude, longitude;
    private ArrayList<DModel_Animals> lstPopulation;
    private IBadgeUpdateListener mBadgeUpdateListener;
    private Dialog progressDialog;
    ArrayList<DModel_District> lstSpices;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View frg = inflater.inflate(R.layout.fragment_farm_profile, container, false);

        init();
        bindviews(frg);

        if (!AppConfig.getInstance().isComingfromIntro) {
            rlDone.setVisibility(View.VISIBLE);
            llNext.setVisibility(View.GONE);
            populateSpinnerSpices();
        }
        else {
            rlDone.setVisibility(View.GONE); llNext.setVisibility(View.VISIBLE);
            requestSpices();
        }



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

    //region  APIS
    private void requestSpices() {

        Intro_WebHit_Get_All_Spices intro_webHit_get_all_spices = new Intro_WebHit_Get_All_Spices();

        intro_webHit_get_all_spices.getSpices(new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
//                dismissProgDialog();
                if (isSuccess) {
//                    llSync.setVisibility(View.GONE);
//                    llNotSync.setVisibility(View.VISIBLE);

                    if (Intro_WebHit_Get_All_Spices.responseObject != null &&
                            Intro_WebHit_Get_All_Spices.responseObject.getResult() != null &&
                            Intro_WebHit_Get_All_Spices.responseObject.getResult().size() > 0) {

                        lstSpices.clear();
                        for (int i = 0; i < Intro_WebHit_Get_All_Spices.responseObject.getResult().size(); i++) {


                            DModel_District dModel_spices = new DModel_District(Intro_WebHit_Get_All_Spices.responseObject.getResult().get(i).getValueMemeber()
                                    , Intro_WebHit_Get_All_Spices.responseObject.getResult().get(i).getDisplayMember());

                            lstSpices.add(dModel_spices);
                        }


                        if (AppConfig.getInstance().getSpicesList().size() > 0) {
                            AppConfig.getInstance().getSpicesList().clear();
                        }
                        AppConfig.getInstance().saveSpicesList(lstSpices);

                        populateSpinnerSpices();

                    }
                }
            }

            @Override
            public void onWebException(Exception ex) {
//                CustomToast.showToastMessage(IntroActivity.this, AppConfig.getInstance().getNetworkExceptionMessage(ex.getMessage()), Toast.LENGTH_SHORT);

            }
        });

    }

    private void requestFarmPopulation() {


        showProgDialog();
        Intro_WebHit_POST_FarmPopulation_GetByFarmID_FarmerId intro_webHit_post_farmPopulation_getByFarmIDFarmerId = new Intro_WebHit_POST_FarmPopulation_GetByFarmID_FarmerId();
        intro_webHit_post_farmPopulation_getByFarmIDFarmerId.getFarmPopulation_GetByFarmID(getContext(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {
                    dismissProgDialog();

                    if (Intro_WebHit_POST_FarmPopulation_GetByFarmID_FarmerId.responseObject != null &&
                            Intro_WebHit_POST_FarmPopulation_GetByFarmID_FarmerId.responseObject.getResult() != null &&
                            Intro_WebHit_POST_FarmPopulation_GetByFarmID_FarmerId.responseObject.getResult().size() > 0) {
                        lstPopulation.clear();
                        for (int i = 0; i < Intro_WebHit_POST_FarmPopulation_GetByFarmID_FarmerId.responseObject.getResult().size(); i++) {

//                            Log.d("LOG_AS", "getName  " + AppConfig.getInstance().getSpicesList().get((Intro_WebHit_POST_FarmPopulation_GetByFarmID_FarmerId.responseObject.getResult().get(i).getSpecieID()-1)).getName() + AppConfig.getInstance().getSpicesList().get((Intro_WebHit_POST_FarmPopulation_GetByFarmID_FarmerId.responseObject.getResult().get(i).getSpecieID()-1)).getId());


                            String s_name = "";

                            switch (Intro_WebHit_POST_FarmPopulation_GetByFarmID_FarmerId.responseObject.getResult().get(i).getSpecieID()) {
                                case 1:
                                    s_name = "Cattle";
                                    break;


                                case 2:
                                    s_name = "Buffalo";
                                    break;
                                case 3:
                                    s_name = "Sheep";
                                    break;
                                case 4:
                                    s_name = "Goat";
                                    break;
                                case 5:
                                    s_name = "Camel";
                                    break;
                                case 6:
                                    s_name = "Horse";
                                    break;
                                case 7:
                                    s_name = "Donkey";
                                    break;
                                case 8:
                                    s_name = "Mule";
                                    break;
                                case 9:
                                    s_name = "Dog";
                                    break;
                                case 10:
                                    s_name = "Poultry";
                                    break;
                            }


                            DModel_Animals dModel_animals = new DModel_Animals(
                                    s_name + "",
                                    Intro_WebHit_POST_FarmPopulation_GetByFarmID_FarmerId.responseObject.getResult().get(i).getNoOfAnimals() + ""

                            );
                            lstPopulation.add(dModel_animals);


                        }
                        populatePopulationList();
                    }

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
        }, farmID);


    }

    private void requestAddFarmPopulation(String _signUpEntity) {
        showProgDialog();
        Intro_WebHit_Post_Add_Farm_Population intro_webHit_post_add_farm_population = new Intro_WebHit_Post_Add_Farm_Population();
        intro_webHit_post_add_farm_population.addFarm(getContext(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {
                    dismissProgDialog();


                    llPopulaion.setVisibility(View.VISIBLE);
                    llSpices.setVisibility(View.VISIBLE);
                    rlAddFarm.setVisibility(View.GONE);
                    edtNoAnimal.setText("");

                    requestFarmPopulation();

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
                "\"FarmName\"" + ":\"" + edtFarmName.getText().toString() + "\"," +
                "\"specieID\"" + ":" + AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.getSpecieID() + "," +
                "\"createdBy\"" + ":" + AppConfig.getInstance().mUserData.getId() + "," +
                "\"latitude\"" + ":" + latitude + "," +
                "\"longitude\"" + ":" + longitude + "," +
                "\"id\"" + ":" + 0 +

                "}";


        requestAddFarm(data);
    }


    private void requestAddFarm(String _signUpEntity) {
        showProgDialog();
        Intro_WebHit_Post_Add_Farm intro_webHit_post_add_farm = new Intro_WebHit_Post_Add_Farm();
        intro_webHit_post_add_farm.addFarm(getContext(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {
                    dismissProgDialog();
                    edtFarmName.setFocusable(false);
                    edtNoAnimal.setText("");

                    farmID = Intro_WebHit_Post_Add_Farm.responseObject.getResult().getId();

                    llSpices.setVisibility(View.VISIBLE);
                    rlAddFarm.setVisibility(View.GONE);

//                    populatePopulationList();
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
    private void addSpicesinFarm() {
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
                "\"noOfAnimals\"" + ":" + edtNoAnimal.getText().toString() + "," +
                "\"specieID\"" + ":" + spicesID + "," +
                "\"id\"" + ":" + 0 + "," +
                "\"createdBy\"" + ":" + AppConfig.getInstance().mUserData.getId() + "," +
                "\"farmID\"" + ":" + farmID + "," +
                "\"preferedLanguage\"" + ":\"" + lang +
                "\"}";


        requestAddFarmPopulation(data);
    }
    //endregion



    //region  PopulateSpinners
    private void populatePopulationList() {
        if (animalPopulationRcvAdapter == null) {

            animalPopulationRcvAdapter = new AnimalPopulationRcvAdapter(getActivity(), lstPopulation, (eventId, position) -> {


            });


            rcv_populatin.setLayoutManager(linearLayoutManager);
            rcv_populatin.setAdapter(animalPopulationRcvAdapter);

        } else {
            animalPopulationRcvAdapter.notifyDataSetChanged();
        }
    }

    private void populateSpinnerSpices() {

        lstSpices.addAll(AppConfig.getInstance().getSpicesList());
        DModel_District dModel_spices = new DModel_District("0", getString(R.string.select_spices));
        lstSpices .add(dModel_spices);

        SpinnerSpicesAdapter spinnerSpicesAdapter = null;
        spinnerSpicesAdapter = new SpinnerSpicesAdapter(getContext(), lstSpices);
        spinnerSpices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                int Pos = Integer.parseInt(selectedItem);
                txvSpices.setText(lstSpices.get(position).getName());
//                AppConfig.getInstance().mUserData.designationID = Integer.valueOf(AppConfig.getInstance().getSpicesList().get(position).getId());
                spicesID = Integer.valueOf(lstSpices.get(position).getId());

                Log.d("populateSpinnerSpices", " spicesID: " + spicesID);


            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerSpices.setAdapter(spinnerSpicesAdapter);
        spinnerSpices.setSelection(spinnerSpicesAdapter.getCount());
    }

    //endregion


    //region  Init
    private void bindviews(View frg) {
        txvSpices = frg.findViewById(R.id.frg_farm_profile_txv_spices);
        edtFarmName = frg.findViewById(R.id.txv_add_farm);
        edtNoAnimal = frg.findViewById(R.id.frg_farm_profile_farmer_edt_animals);
        rcv_populatin = frg.findViewById(R.id.frg_complete_profile_rcv_populatin);
        spinnerSpices = frg.findViewById(R.id.frg_farm_profile_spinner_spices);
        txvFarm = frg.findViewById(R.id.frg_sigin_va_txv_gender);


        rlAddFarm = frg.findViewById(R.id.frg_farm_profile_farmer_add_farm);
        rlAddSpices = frg.findViewById(R.id.frg_farm_profile_farmer_add_spices);
        rlSubmit = frg.findViewById(R.id.frg_farm_profile_farmer_rl_sumary);
        rlDone = frg.findViewById(R.id.frg_farm_profile_farmer_rlDone);

        llPopulaion = frg.findViewById(R.id.ll_population);
        llSpices = frg.findViewById(R.id.llSpices);
        llNext = frg.findViewById(R.id.frg_farm_profile_llNext);


        rlBack = frg.findViewById(R.id.rlBack);

        rlBack.setOnClickListener(this);
        llNext.setOnClickListener(this);
        rlDone.setOnClickListener(this);
        rlAddFarm.setOnClickListener(this);
        rlSubmit.setOnClickListener(this);
        rlAddSpices.setOnClickListener(this);


    }

    private void init() {
        lstPopulation = new ArrayList<>();
        lstSpices = new ArrayList<>();
        setBottomBar();
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
    //endregion



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frg_farm_profile_farmer_add_farm:

                if (edtFarmName.getText().toString().isEmpty())
                    AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.farm_name_not_null));
                else
                    requestAddFarm();


                break;
            case R.id.frg_farm_profile_farmer_rl_sumary:
                if (lstPopulation.size() > 0) {
                    AppConfig.getInstance().mUserData.setLoggedIn(true);
                    AppConfig.getInstance().mUserData.setLoggedInTemp(true);
                    AppConfig.getInstance().mUserData.setVA(false);
                    AppConfig.getInstance().mUserData.setFarmer(true);
                    AppConfig.getInstance().saveUserProfileData();
                    ((IntroActivity) getActivity()).navtoMainActivity();
//                    navtoMyProfileFragment();
                }
                else
                    AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.farm_name_not_null));
                break;
            case R.id.frg_farm_profile_farmer_add_spices:
                if (!edtNoAnimal.getText().toString().isEmpty()) {
                    if (!txvSpices.getText().toString().isEmpty())
                        addSpicesinFarm();
                    else
                        AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.farm_spices_not_null));
                } else
                    AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.farm_population_not_null));
                break;
            case R.id.rlBack:
                getActivity().onBackPressed();

                break;            case R.id.frg_farm_profile_farmer_rlDone:
                getActivity().onBackPressed();

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
