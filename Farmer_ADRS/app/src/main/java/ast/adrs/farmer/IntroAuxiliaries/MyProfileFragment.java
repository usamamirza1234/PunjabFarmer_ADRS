package ast.adrs.farmer.IntroAuxiliaries;

import android.Manifest;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import ast.adrs.farmer.AppConfig;
import ast.adrs.farmer.HomeAuxiliares.PerformanceRcvAdapter;
import ast.adrs.farmer.IntroActivity;
import ast.adrs.farmer.IntroAuxiliaries.WebServices.Intro_WebHit_POST_FarmFarmPop_GetByFarmID;
import ast.adrs.farmer.IntroAuxiliaries.WebServices.Intro_WebHit_POST_FarmFarmPop_GetByFarmerID;
import ast.adrs.farmer.R;
import ast.adrs.farmer.Utils.AppConstt;
import ast.adrs.farmer.Utils.CircleImageView;
import ast.adrs.farmer.Utils.IBadgeUpdateListener;
import ast.adrs.farmer.Utils.IWebCallback;

import static android.app.Activity.RESULT_OK;

public class MyProfileFragment extends Fragment implements View.OnClickListener {

    public static final int PICK_IMAGE = 1;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int SELECT_PICTURE = 1;
    PerformanceRcvAdapter performanceRcvAdapter;
    TextView txvName, txvCNIC, txvPhone, txvTehsil, txvDistrict, txvMozah;

    RelativeLayout rlDone;
    Bundle bundle;
    ArrayList<Integer> lstfarmId;
    boolean isComingFromMain;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    CircleImageView civFarmerImv;
    RecyclerView rcv_populatin;
    AnimalPopulationRcvAdapter animalPopulationRcvAdapter;
    LinearLayout llFarmer, llfarm_wise;
    ArrayList<DModel_Animals> lstNewData;
    RelativeLayout rleditProfile;
    private ArrayList<DModel_Animals> lstPopulation;
    private ArrayList<DModel_Performance> lstPerformance;
    private ArrayList<String> lstdiseaseIntimationDiseases;
    private IBadgeUpdateListener mBadgeUpdateListener;
    private Dialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View frg = inflater.inflate(R.layout.fragment_my_profile, container, false);
        initData();
        bindViews(frg);


        if (AppConfig.getInstance().mUserData.isFarmer()) {

            llFarmer.setVisibility(View.VISIBLE);

            if (isComingFromMain)
                requestFarmWisePop();
            else {
                llfarm_wise.setVisibility(View.GONE);
            }

            String upperString = AppConfig.getInstance().mUserData.getName().substring(0, 1).toUpperCase() + AppConfig.getInstance().mUserData.getName().substring(1).toLowerCase();

            txvName.setText(upperString);

            String strPhoneNumber = String.valueOf(AppConfig.getInstance().mUserData.getPhone());
            strPhoneNumber = "" + strPhoneNumber.substring(1);
            strPhoneNumber = "0" + strPhoneNumber.substring(1);

            txvPhone.setText(strPhoneNumber);


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


            txvMozah.setText("M: " + AppConfig.getInstance().mUserData.getMozah().trim());
            txvDistrict.setText("D: " + AppConfig.getInstance().mUserData.getDestrict().trim());
            txvTehsil.setText("T: " + AppConfig.getInstance().mUserData.getTehsil().trim());
            Log.d("OFFLINE_DB", "getDestrict()  " + AppConfig.getInstance().mUserData.getDestrict().trim());
            Log.d("OFFLINE_DB", "getTehsil()  " + AppConfig.getInstance().mUserData.getTehsil().trim());

        }

//
//        if (AppConfig.getInstance().mUserData.isFarmer()) {
//
//
//            txv_address.setText(
//                    "M: " + AppConfig.getInstance().mUserData.getMozah().trim()
//                            + " T: " + AppConfig.getInstance().mUserData.getTehsil().trim()
//                            + "\nD: " + AppConfig.getInstance().mUserData.getDestrict().trim()
//            );
//
//        }


        if (!AppConfig.getInstance().mUserData.getEncorededImage().isEmpty()) {
            byte[] decodedString = Base64.decode(AppConfig.getInstance().mUserData.getEncorededImage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            civFarmerImv.setImageBitmap(decodedByte);
        }


        return frg;
    }


    //region Farmer


    //region FarmerFarmList
    void requestFarmWisePop() {
        showProgDialog();
        Intro_WebHit_POST_FarmFarmPop_GetByFarmerID intro_webHit_post_farmFarmPop_getByFarmerID = new Intro_WebHit_POST_FarmFarmPop_GetByFarmerID();
        intro_webHit_post_farmFarmPop_getByFarmerID.getFarmPop(getActivity(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {


                    if (Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject != null &&
                            Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult() != null &&
                            Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult().size() > 0) {


                        lstPopulation.clear();
                        for (int i = 0; i < Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult().size(); i++) {


                            DModel_Animals dModel_animals = new DModel_Animals(
                                    Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult().get(i).getFarmID() + "",
                                    Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult().get(i).getNoOfAnimals() + "");


                            lstPopulation.add(dModel_animals);
                        }


                        for (int i = 0; i < lstPopulation.size(); i++) {
                            lstfarmId.add(Integer.parseInt(lstPopulation.get(i).getId()));
                        }


                        List<Integer> numbers = lstfarmId;
                        Log.d("listing", "lstfarmId numbers" + numbers);


                        Set<Integer> hashSet = new LinkedHashSet(numbers);
                        ArrayList<Integer> removedDuplicates = new ArrayList(hashSet);


                        for (int i = 0; i < removedDuplicates.size(); i++) {
                            requestFarmsbyFarmID(String.valueOf(removedDuplicates.get(i)));
                        }
                    }
                } else {
                    dismissProgDialog();
                    AppConfig.getInstance().showErrorMessage(getActivity(), strMsg);
                }
            }

            @Override
            public void onWebException(Exception e) {
                dismissProgDialog();
                Log.d("LOG_AS", "getFarmFarm_GetByFarmerID Exception: " + e.getMessage());

                AppConfig.getInstance().showErrorMessage(getActivity(), e.toString());
            }
        }, AppConfig.getInstance().mUserData.getId());

    }

    private void requestFarmsbyFarmID(String s) {

        Intro_WebHit_POST_FarmFarmPop_GetByFarmID intro_webHit_post_farmFarmPop_getByFarmID = new Intro_WebHit_POST_FarmFarmPop_GetByFarmID();
        intro_webHit_post_farmFarmPop_getByFarmID.getFarmPop(getActivity(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {
                    dismissProgDialog();
                    if (Intro_WebHit_POST_FarmFarmPop_GetByFarmID.responseObject != null &&
                            Intro_WebHit_POST_FarmFarmPop_GetByFarmID.responseObject.getResult() != null &&
                            Intro_WebHit_POST_FarmFarmPop_GetByFarmID.responseObject.getResult().size() > 0) {
                        int noOfanimals = 0;

                        for (int i = 0; i < Intro_WebHit_POST_FarmFarmPop_GetByFarmID.responseObject.getResult().size(); i++) {
                            noOfanimals = noOfanimals + Intro_WebHit_POST_FarmFarmPop_GetByFarmID.responseObject.getResult().get(i).getNoOfAnimals();
                        }


                        String name = "";

                        for (int i = 0; i < AppConfig.getInstance().getFarmList().size(); i++) {
                            for (int j = 0; j < Intro_WebHit_POST_FarmFarmPop_GetByFarmID.responseObject.getResult().size(); j++) {

                                int x = (Intro_WebHit_POST_FarmFarmPop_GetByFarmID.responseObject.getResult().get(j).getFarmID());
                                int y = Integer.parseInt(AppConfig.getInstance().getFarmList().get(i).getId());

                                if (x == y) {
                                    //do something for not equals

                                    Log.d("listing", "getFarmList equals" + AppConfig.getInstance().getFarmList().get(i).getName());
                                    name = AppConfig.getInstance().getFarmList().get(i).getName();

                                }
                            }
                        }

                        lstNewData.add(new DModel_Animals(name, noOfanimals + ""));
                    }

                    populateData();
                } else {
                    dismissProgDialog();

                    AppConfig.getInstance().showErrorMessage(getActivity(), strMsg);
                }
            }

            @Override
            public void onWebException(Exception e) {
                dismissProgDialog();
                Log.d("LOG_AS", "VA Registration Exception: " + e.getMessage());

                AppConfig.getInstance().showErrorMessage(getActivity(), e.toString());
            }
        }, Integer.parseInt(s));


    }

    private void populateData() {
        if (animalPopulationRcvAdapter == null) {
            animalPopulationRcvAdapter = new AnimalPopulationRcvAdapter(getActivity(), lstNewData, (eventId, position) -> {
            });
            rcv_populatin.setLayoutManager(linearLayoutManager);
            rcv_populatin.setAdapter(animalPopulationRcvAdapter);
        } else {
            animalPopulationRcvAdapter.notifyDataSetChanged();
        }
    }
    //endregion


    //endregion

    //region init

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
            byte[] byteArrayImage = byteArrayOutputStream.toByteArray();
            AppConfig.getInstance().mUserData.setEncorededImage(Base64.encodeToString(byteArrayImage, Base64.NO_WRAP));
            AppConfig.getInstance().saveUserProfileData();

        }

    }

    void setBottomBar() {
        try {
            mBadgeUpdateListener = (IBadgeUpdateListener) getActivity();
        } catch (ClassCastException castException) {
            castException.printStackTrace(); // The activity does not implement the listener
        }
        if (getActivity() != null && isAdded()) {
            mBadgeUpdateListener.setToolbarState(AppConstt.INTRO_ToolbarStates.signinFarmer);
            mBadgeUpdateListener.setHeaderTitle(getString(R.string.my_profile));
        }

    }

    private void bindViews(View frg) {

        rcv_populatin = frg.findViewById(R.id.frg_complete_profile_rcv_populatin);
        rleditProfile = frg.findViewById(R.id.rleditProfile);

        civFarmerImv = frg.findViewById(R.id.frg_complete_profile_imv_profile);

        rlDone = frg.findViewById(R.id.frg_my_profile_rl_done);
        txvMozah = frg.findViewById(R.id.frg_my_profile_txv_address);
        txvDistrict = frg.findViewById(R.id.frg_my_profile_txv_district);
        txvTehsil = frg.findViewById(R.id.frg_my_profile_txv_tehsil);

        llFarmer = frg.findViewById(R.id.llFarmer);
        llfarm_wise = frg.findViewById(R.id.llfarm_wise);

        txvName = frg.findViewById(R.id.frg_my_profile_txv_name);
        txvPhone = frg.findViewById(R.id.frg_my_profile_txv_phone);


        txvCNIC = frg.findViewById(R.id.frg_my_profile_txv_cnic);


        rlDone.setOnClickListener(this);
        rleditProfile.setOnClickListener(this);
        civFarmerImv.setOnClickListener(this);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!isHidden()) {
            setBottomBar();
        }
    }


    private void navToCompleteFAProfileFragment() {

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag = new CompleteFarmerProfile();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isComingFromIntro",false);
        ft.add(R.id.act_main_rl_MainContainer, frag, AppConstt.FragTag.FN_CompleteFarmerProfileFragment);
        ft.addToBackStack(AppConstt.FragTag.FN_CompleteFarmerProfileFragment);
        frag.setArguments(bundle);
        ft.hide(this);
        ft.commit();
    }

    private void initData() {
        lstfarmId = new ArrayList<>();
        bundle = this.getArguments();
        if (bundle != null) {

            isComingFromMain = bundle.getBoolean("isComingFromMain", false);

            Log.d("LOG_AS", "isComingFromMain " + isComingFromMain);

        }
        setBottomBar();
        lstPopulation = new ArrayList<>();
        lstPerformance = new ArrayList<>();
        lstdiseaseIntimationDiseases = new ArrayList<>();
        lstNewData = new ArrayList();
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
    //endregion


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.frg_my_profile_rl_done: {

                if (isComingFromMain) {
                    getActivity().onBackPressed();
                } else {
                    AppConfig.getInstance().mUserData.setLoggedIn(true);
                    AppConfig.getInstance().mUserData.setLoggedInTemp(true);
                    AppConfig.getInstance().mUserData.setVA(false);
                    AppConfig.getInstance().mUserData.setFarmer(true);
                    AppConfig.getInstance().saveUserProfileData();
                    ((IntroActivity) getActivity()).navtoMainActivity();
                }


            }
            break;

            case R.id.rleditProfile:
                navToCompleteFAProfileFragment();
                break;

//            case R.id.frg_complete_profile_imv_profile: {
//
//                takePhoto();
//            }
//            break;
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


}

