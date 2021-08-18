package ast.adrs.farmer;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ast.adrs.farmer.HomeAuxiliares.IntimateDiseaseFragment;
import ast.adrs.farmer.HomeAuxiliares.MyFarmFragment;
import ast.adrs.farmer.HomeAuxiliares.SuggestionFragment;
import ast.adrs.farmer.HomeAuxiliares.WebServices.Home_WebHit_Post_GetSpices;
import ast.adrs.farmer.HomeAuxiliares.WebServices.Home_WebHit_Post_Get_DiseaseDefinationDetails;
import ast.adrs.farmer.IntimateDiseaseAuxiliaries.WebServices.Home_WebHit_Post_DiseaseByFarmer;
import ast.adrs.farmer.IntimateDiseaseAuxiliaries.WebServices.Home_WebHit_Post_GenerateDiseaseIntimationReport;
import ast.adrs.farmer.IntroAuxiliaries.DModel_Animals;
import ast.adrs.farmer.IntroAuxiliaries.DModel_District;
import ast.adrs.farmer.IntroAuxiliaries.WebServices.Intro_WebHit_Get_All_Designations;
import ast.adrs.farmer.IntroAuxiliaries.WebServices.Intro_WebHit_Get_All_District;
import ast.adrs.farmer.IntroAuxiliaries.WebServices.Intro_WebHit_Get_All_Spices;
import ast.adrs.farmer.IntroAuxiliaries.WebServices.Intro_WebHit_POST_FarmFarmPop_GetByFarmerID;
import ast.adrs.farmer.IntroAuxiliaries.WebServices.Intro_WebHit_POST_FarmFarm_GetByFarmerID;
import ast.adrs.farmer.ParentFragments.HomeFragment;
import ast.adrs.farmer.ParentFragments.LoginbackFragment;
import ast.adrs.farmer.Utils.AppConstt;
import ast.adrs.farmer.Utils.CheckInternetConnection;
import ast.adrs.farmer.Utils.ConnectionChangeListener;
import ast.adrs.farmer.Utils.CustomAlertConfirmationInterface;
import ast.adrs.farmer.Utils.CustomAlertDialog;
import ast.adrs.farmer.Utils.IBadgeUpdateListener;
import ast.adrs.farmer.Utils.IWebCallback;
import ast.adrs.farmer.Utils.LocaleHelper;

/**
 * Created by Usama Khalid Mirza on 06/05/2021.
 * usamamirza.andpercent@gmail.com
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener, IBadgeUpdateListener {
    CheckInternetConnection checkInternetConnection = new CheckInternetConnection();
    RelativeLayout rlMainContainer, rlBotmbar;
    RelativeLayout rlWifiEnabled, rlWifiDisabled;
    LinearLayout llWifiSync;
    ImageView imv_logo;
    ImageView imv_back;
    TextView txv_title;

    ArrayList<String> lst_generateReportId;

    CustomAlertDialog customAlertDialog;
    ArrayList<DModel_District> lstDesgnation;
    ArrayList<DModel_District> lstSpices;
    List<Home_WebHit_Post_Get_DiseaseDefinationDetails.ResponseModel> lst_AllDiseaseDefination;
    ArrayList<DModel_Animals> lstAnimal;
    ArrayList<DModel_District> lst_District;
    private ArrayList<DModel_Animals> lstFarms;

    private Dialog progressDialog;
    private RelativeLayout rlToolbar;
    private FragmentTransaction ft;
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        AppConfig.getInstance().performLangCheck(getWindow());
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            return;
        }



        AppConfig.getInstance().regulateFontScale(getResources().getConfiguration(), getBaseContext());
        initData();
        bindViews();
        AppConfig.getInstance().loadUserProfileData();



        if(AppConfig.getInstance().mLanguage.equalsIgnoreCase(AppConstt.AppLang.LANG_UR)) {
            imv_back.setBackground(getDrawable(R.drawable.ic_arrow_back_white_ar));

//            imv_back.setVisibility(View.GONE);
//            imv_back_ur.setVisibility(View.VISIBLE);

        }
        else {
            imv_back.setBackground(getDrawable(R.drawable.ic_arrow_back_white_en));
//            imv_back.setVisibility(View.GONE);
//            imv_back_ur.setVisibility(View.VISIBLE);
        }

        Log.d("OFFLINE_DB", "getAnimalList()  " + AppConfig.getInstance().mLanguage);

        Log.d("OFFLINE_DB", "getAnimalList()  " + AppConfig.getInstance().getAnimalList().size());




        setFirstFragment();
        if (AppConfig.getInstance().getAnimalList().size() <= 0) {
            requestDisease();
        } else if (AppConfig.getInstance().mUserData.isFarmer()) {
            if (AppConfig.getInstance().getFarmList().size() <= 0) {
                requestFarmList();
            }
        }


    }

    //region  functions for Dialog
    private void dismissProgDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void showProgDialog() {
        progressDialog = new Dialog(this, R.style.AppTheme);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setContentView(R.layout.dialog_progress);

        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    //endregion

    public void setFirstFragment() {
        if (AppConfig.getInstance().mUserData.isLoggedInTemp())
            navtoHomeFragment();
        else
            navToSignUpMain();
    }

    private void navToSignUpMain() {

        clearMyBackStack();
        Fragment frg = new LoginbackFragment();
        ft = fm.beginTransaction();


        ft.replace(R.id.act_main_content_frg, frg, AppConstt.FragTag.FN_SignUpMain);

        ft.commit();
    }

    private void navtoHomeFragment() {
        clearMyBackStack();
        Fragment frg = new HomeFragment();
        ft = fm.beginTransaction();


        ft.replace(R.id.act_main_content_frg, frg, AppConstt.FragTag.FN_HomeFragment);
//        ft.addToBackStack(AppConstt.FragTag.FN_HomeFragment);
//        hideLastStackFragment(ft);
        ft.commit();
    }


    //region Init
    @Override
    protected void attachBaseContext(final Context baseContext) {
        //Handle custom font settings and screen size
        super.attachBaseContext(LocaleHelper.wrap(AppConfig.getInstance().regulateDisplayScale(baseContext),
                new Locale(AppConfig.getInstance().loadDefLanguage())));
    }

    private void initData() {
        AppConfig.getInstance().loadUserProfileData();
        fm = getSupportFragmentManager();

        this.lstDesgnation = new ArrayList<>();
        this.lst_District = new ArrayList<>();
        this.lst_AllDiseaseDefination = new ArrayList<>();
        this.lstSpices = new ArrayList<>();
        this.lstAnimal = new ArrayList<>();
        this.lst_generateReportId = new ArrayList<>();
        this.lstFarms = new ArrayList<>();
    }

    public void bindViews() {

        rlToolbar = findViewById(R.id.act_main_rl_toolbar);

        rlMainContainer = findViewById(R.id.act_main_rl_MainContainer);
        rlBotmbar = findViewById(R.id.act_main_rl_bttabbar);


        rlWifiEnabled = findViewById(R.id.lay_item_btmbr_rl_wifi_enabled);
        rlWifiDisabled = findViewById(R.id.lay_item_btmbr_rl_wifi_disabled);


        llWifiSync = findViewById(R.id.lay_item_btmbr_ll_wifi_sync);


        imv_logo = findViewById(R.id.act_intro_rl_toolbar_logo);
        imv_back = findViewById(R.id.act_intro_rl_toolbar_back);

        txv_title = findViewById(R.id.act_intro_txv_title);


        imv_logo.setOnClickListener(this);
        imv_back.setOnClickListener(this);
       
        llWifiSync.setOnClickListener(this);


    }
    //endregion


    //region Toolbar/BottomBar


    public void updateToolbar() {


    }
    //endregion

    //region Dialogue


    //endregion

    //region Navigations


    public void hideLastStackFragment(FragmentTransaction ft) {
        Fragment frg = null;
        frg = getSupportFragmentManager().findFragmentById(R.id.act_main_content_frg);

        if (frg != null) {
            if (frg instanceof HomeFragment && frg.isVisible()) {
                ft.hide(frg);
            }

            if (frg instanceof IntimateDiseaseFragment && frg.isVisible()) {
                ft.hide(frg);
            } else if (frg instanceof SuggestionFragment && frg.isVisible()) {
                ft.hide(frg);
            } else if (frg instanceof MyFarmFragment && frg.isVisible()) {
                ft.hide(frg);
            }

        }
    }

    public void clearMyBackStack() {
        int count = fm.getBackStackEntryCount();
        for (int i = 0; i < count; ++i) {
            fm.popBackStackImmediate();

        }

    }


    //endregion

    public void clearMyBackStack(int baselevel) {
        int count = fm.getBackStackEntryCount();
        for (int i = baselevel; i < count; ++i) {
            fm.popBackStackImmediate();

        }

//        txvTitle.setText(getResources().getString(R.string.frg_hom_ttl));
    }

    public String returnStackFragmentTag() {
        int index = fm.getBackStackEntryCount() - 1;
        FragmentManager.BackStackEntry backEntry = null;
        String tag = "";
        if (index >= 0) {
            backEntry = fm.getBackStackEntryAt(index);
            tag = backEntry.getName();
        }
        return tag;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.act_intro_rl_toolbar_logo:
                setFirstFragment();
                break;
            case R.id.act_intro_rl_toolbar_back:

                onBackPressed();
                break;
            case R.id.lay_item_btmbr_ll_wifi_sync:
                requestDesignations();
                break;

        }


    }


    //region BackPressFuntions
    @Override
    public void onBackPressed() {

        whileBackPress();


    }

    private void whileBackPress() {


        if (returnStackFragmentTag().equalsIgnoreCase(AppConstt.FragTag.FN_HomeFragment)) {
            this.finish();
        } else if (returnStackFragmentTag().equalsIgnoreCase(AppConstt.FragTag.FN_DiseaseSummeryFarmerFragment)) {
            navtoHomeFragment();
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();

        } else {

            super.onBackPressed();
        }


    }
    //endregion

    //region IBadgeUpdateListener

    @Override
    public void setBottomTabVisiblity(int mVisibility) {

        if (mVisibility == View.GONE) {
//            rlBottomTabContainer.setVisibility(View.GONE);

        } else {
//            rlBottomTabContainer.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void setToolbarVisiblity(int mVisibility) {

        if (mVisibility == View.GONE) {
            rlToolbar.setVisibility(View.GONE);
        } else {
            rlToolbar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setToolbarState(byte mState) {
        switch (mState) {


            case AppConstt.ToolbarState.TOOLBAR_BACK_TITLE_HIDDEN:
                txv_title.setVisibility(View.VISIBLE);
                imv_back.setVisibility(View.GONE);

                imv_logo.setVisibility(View.VISIBLE);
                rlToolbar.setVisibility(View.VISIBLE);
                break;

            case AppConstt.ToolbarState.TOOLBAR_BACK_TITLE_VISIBLE:
                txv_title.setVisibility(View.VISIBLE);
                imv_back.setVisibility(View.VISIBLE);

                imv_logo.setVisibility(View.GONE);
                rlToolbar.setVisibility(View.VISIBLE);
                break;

            case AppConstt.ToolbarState.TOOLBAR_BACK_TITLE_LOGO_VISIBLE:

                txv_title.setVisibility(View.VISIBLE);
                imv_back.setVisibility(View.VISIBLE);

                imv_logo.setVisibility(View.GONE);
                rlToolbar.setVisibility(View.VISIBLE);
                break;

            case AppConstt.ToolbarState.TOOLBAR_HIDDEN:
                rlToolbar.setVisibility(View.GONE);
                txv_title.setVisibility(View.GONE);
                imv_back.setVisibility(View.GONE);

                imv_logo.setVisibility(View.GONE);
                break;
            default:
                txv_title.setVisibility(View.VISIBLE);
                imv_back.setVisibility(View.VISIBLE);

                imv_logo.setVisibility(View.VISIBLE);
                break;

        }
    }

    @Override
    public void switchStatusBarColor(boolean isDark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
//                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if (isDark)
                window.setStatusBarColor(Color.BLACK);
            else
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.thm_light_green));
        }
    }

    @Override
    public boolean setHeaderTitle(String strAppTitle) {
        try {
            txv_title.setText(strAppTitle);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    //endregion


    public void navToIntroActivity() {
        Intent in = new Intent(this, IntroActivity.class);
        startActivity(in);
    }


    //region ApiCalls
    private void requestSpices() {
        showProgDialog();
        Intro_WebHit_Get_All_Spices intro_webHit_get_all_spices = new Intro_WebHit_Get_All_Spices();

        intro_webHit_get_all_spices.getSpices(new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                dismissProgDialog();
                if (isSuccess) {
                    requestDisease();
                    if (Intro_WebHit_Get_All_Spices.responseObject != null &&
                            Intro_WebHit_Get_All_Spices.responseObject.getResult() != null &&
                            Intro_WebHit_Get_All_Spices.responseObject.getResult().size() > 0) {

                        lstSpices.clear();
                        for (int i = 0; i < Intro_WebHit_Get_All_Spices.responseObject.getResult().size(); i++) {


                            DModel_District dModel_spices = new DModel_District(Intro_WebHit_Get_All_Spices.responseObject.getResult().get(i).getValueMemeber()
                                    , Intro_WebHit_Get_All_Spices.responseObject.getResult().get(i).getDisplayMember());

                            lstSpices.add(dModel_spices);
                        }

                        DModel_District dModel_spices = new DModel_District("0", getString(R.string.select_spices));
                        lstSpices.add(dModel_spices);


                        if (AppConfig.getInstance().getSpicesList().size() > 0) {
                            AppConfig.getInstance().getSpicesList().clear();
                        }
                        AppConfig.getInstance().saveSpicesList(lstSpices);


                    }
                }
            }

            @Override
            public void onWebException(Exception ex) {
                dismissProgDialog();
//                CustomToast.showToastMessage(IntroActivity.this, AppConfig.getInstance().getNetworkExceptionMessage(ex.getMessage()), Toast.LENGTH_SHORT);

            }
        });

    }

    private void requestDisease() {
        showProgDialog();
        Home_WebHit_Post_GetSpices home_webHit_post_getSpices = new Home_WebHit_Post_GetSpices();
        home_webHit_post_getSpices.getAnimalType(MainActivity.this, new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {
                    dismissProgDialog();
                    if (AppConfig.getInstance().mUserData.isFarmer()) {
                        if (AppConfig.getInstance().getFarmList().size() <= 0) {
                            requestFarmList();
                        }
                    }
                    for (int i = 0; i < Home_WebHit_Post_GetSpices.responseObject.getResult().size(); i++) {


                        lstAnimal.add(new DModel_Animals(Home_WebHit_Post_GetSpices.responseObject.getResult().get(i).getValueMemeber(), Home_WebHit_Post_GetSpices.responseObject.getResult().get(i).getDisplayMember()));

                    }
                    if (AppConfig.getInstance().getAnimalList().size() > 0) {
                        AppConfig.getInstance().getAnimalList().clear();
                    }
                    AppConfig.getInstance().saveAnimalList(lstAnimal);
                    Log.d("OFFLINE_DB", "getAnimalList() inside " + AppConfig.getInstance().getAnimalList().size());


                } else {
                    dismissProgDialog();
                    AppConfig.getInstance().showErrorMessage(MainActivity.this, strMsg);
                }
            }

            @Override
            public void onWebException(Exception e) {
                dismissProgDialog();
                Log.d("LOG_AS", "getAnimalList() Exception: " + e.getMessage());

                AppConfig.getInstance().showErrorMessage(MainActivity.this, e.toString());
            }
        });
    }

    public void requestDesignations() {

        showProgDialog();
        Intro_WebHit_Get_All_Designations intro_webHit_get_all_designations = new Intro_WebHit_Get_All_Designations();

        intro_webHit_get_all_designations.getDesignations(new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                dismissProgDialog();
                if (isSuccess) {

                    requestDistrict();


                    if (Intro_WebHit_Get_All_Designations.responseObject != null &&
                            Intro_WebHit_Get_All_Designations.responseObject.getResult() != null &&
                            Intro_WebHit_Get_All_Designations.responseObject.getResult().size() > 0) {


                        for (int i = 0; i < Intro_WebHit_Get_All_Designations.responseObject.getResult().size(); i++) {


                            Log.d("LOG_AS", "getDesignations: getValueMemeber " + Intro_WebHit_Get_All_Designations.responseObject.getResult().get(i).getDisplayMember());

                            if (Intro_WebHit_Get_All_Designations.responseObject.getResult().get(i).getDisplayMember().equalsIgnoreCase("va") ||
                                    Intro_WebHit_Get_All_Designations.responseObject.getResult().get(i).getDisplayMember().equalsIgnoreCase("ait")
                            ) {
                                DModel_District dModel_designation = new DModel_District(Intro_WebHit_Get_All_Designations.responseObject.getResult().get(i).getValueMemeber()
                                        , Intro_WebHit_Get_All_Designations.responseObject.getResult().get(i).getDisplayMember());

                                lstDesgnation.add(dModel_designation);
                            }


                        }

                        DModel_District dModel_designation = new DModel_District("0", getString(R.string.select_Designation));
                        lstDesgnation.add(dModel_designation);

                        if (AppConfig.getInstance().getDesignationList().size() > 0) {
                            AppConfig.getInstance().getDesignationList().clear();
                        }
                        AppConfig.getInstance().saveDesignationList(lstDesgnation);


                    }

                } else {
                }
            }

            @Override
            public void onWebException(Exception ex) {
                dismissProgDialog();
//                CustomToast.showToastMessage(IntroActivity.this, AppConfig.getInstance().getNetworkExceptionMessage(ex.getMessage()), Toast.LENGTH_SHORT);

            }
        });

    }

    private void requestDistrict() {
        showProgDialog();
        Intro_WebHit_Get_All_District intro_webHit_get_all_district = new Intro_WebHit_Get_All_District();

        intro_webHit_get_all_district.getDistrict(new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                dismissProgDialog();
                if (isSuccess) {

                    requestDiseaseDefinition();


                    if (Intro_WebHit_Get_All_District.responseObject != null &&
                            Intro_WebHit_Get_All_District.responseObject.getResult() != null &&
                            Intro_WebHit_Get_All_District.responseObject.getResult().size() > 0) {


                        for (int i = 0; i < Intro_WebHit_Get_All_District.responseObject.getResult().size(); i++) {


                            DModel_District dModel_district = new DModel_District(Intro_WebHit_Get_All_District.responseObject.getResult().get(i).getValueMemeber()
                                    , Intro_WebHit_Get_All_District.responseObject.getResult().get(i).getDisplayMember());

                            lst_District.add(dModel_district);
                        }

                        DModel_District dModel_district = new DModel_District("0", getString(R.string.select_district));
                        lst_District.add(dModel_district);

                        if (AppConfig.getInstance().getDistrictList().size() > 0) {
                            AppConfig.getInstance().getDistrictList().clear();
                        }
                        AppConfig.getInstance().saveDistrictList(lst_District);

                    }

                } else {
                }
            }

            @Override
            public void onWebException(Exception ex) {
                dismissProgDialog();
//                CustomToast.showToastMessage(IntroActivity.this, AppConfig.getInstance().getNetworkExceptionMessage(ex.getMessage()), Toast.LENGTH_SHORT);

            }
        });

    }

    private void requestFarmList() {
        int id = 0;

        if (AppConfig.getInstance().mUserData.isFarmer()) {
            id = AppConfig.getInstance().mUserData.getId();
        } else
            id = Integer.parseInt(AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.getFarmerID());

        showProgDialog();
        Intro_WebHit_POST_FarmFarm_GetByFarmerID intro_webHit_post_farmFarm_getByFarmerID = new Intro_WebHit_POST_FarmFarm_GetByFarmerID();
        intro_webHit_post_farmFarm_getByFarmerID.getFarmFarm_GetByFarmerID(MainActivity.this, new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {

                    dismissProgDialog();
                    requestFarmWisePop();
                    if (Intro_WebHit_POST_FarmFarm_GetByFarmerID.responseObject != null &&
                            Intro_WebHit_POST_FarmFarm_GetByFarmerID.responseObject.getResult() != null &&
                            Intro_WebHit_POST_FarmFarm_GetByFarmerID.responseObject.getResult().size() > 0) {


                        lstFarms.clear();
                        for (int i = 0; i < Intro_WebHit_POST_FarmFarm_GetByFarmerID.responseObject.getResult().size(); i++) {


                            DModel_Animals dModel_animals = new DModel_Animals(String.valueOf(Intro_WebHit_POST_FarmFarm_GetByFarmerID.responseObject.getResult().get(i)
                                    .getId()),
                                    String.valueOf(Intro_WebHit_POST_FarmFarm_GetByFarmerID.responseObject.getResult().get(i).getFarmName()));


                            lstFarms.add(dModel_animals);

                        }


                        DModel_Animals dModel_animals = new DModel_Animals("0", getString(R.string.select_farm));
                        lstFarms.add(dModel_animals);


                        if (AppConfig.getInstance().getFarmList().size() > 0) {
                            AppConfig.getInstance().getFarmList().clear();
                        }
                        AppConfig.getInstance().saveFarmList(lstFarms);
                    }

                } else {
                    dismissProgDialog();

                    AppConfig.getInstance().showErrorMessage(MainActivity.this, strMsg);
                }
            }

            @Override
            public void onWebException(Exception e) {
                dismissProgDialog();
                Log.d("LOG_AS", "VA Registration Exception: " + e.getMessage());

                AppConfig.getInstance().showErrorMessage(MainActivity.this, e.toString());
            }
        }, id);


    }


    void requestFarmWisePop() {
        ArrayList<DModel_Animals> lstPopulation;
        lstPopulation = new ArrayList<>();
        showProgDialog();
        Intro_WebHit_POST_FarmFarmPop_GetByFarmerID intro_webHit_post_farmFarmPop_getByFarmerID = new Intro_WebHit_POST_FarmFarmPop_GetByFarmerID();
        intro_webHit_post_farmFarmPop_getByFarmerID.getFarmPop(MainActivity.this, new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {
                    dismissProgDialog();

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


                        if (AppConfig.getInstance().getFarmPopList().size() > 0) {
                            AppConfig.getInstance().getFarmPopList().clear();
                        }
                        AppConfig.getInstance().saveFarmPopList(lstPopulation);


                    }
                } else {
                    dismissProgDialog();

                    AppConfig.getInstance().showErrorMessage(MainActivity.this, strMsg);
                }
            }

            @Override
            public void onWebException(Exception e) {
                dismissProgDialog();
                Log.d("LOG_AS", "getFarmFarm_GetByFarmerID Exception: " + e.getMessage());

                AppConfig.getInstance().showErrorMessage(MainActivity.this, e.toString());
            }
        }, AppConfig.getInstance().mUserData.getId());

    }

    private void requestDiseaseDefinition() {
        showProgDialog();
        Home_WebHit_Post_Get_DiseaseDefinationDetails home_webHit_post_get_diseaseDefinationDetails = new Home_WebHit_Post_Get_DiseaseDefinationDetails();
        home_webHit_post_get_diseaseDefinationDetails.getDiseaseDefinationDetails(MainActivity.this, new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {
                    dismissProgDialog();
                    requestSpices();

                    if (Home_WebHit_Post_Get_DiseaseDefinationDetails.responseObject != null &&
                            Home_WebHit_Post_Get_DiseaseDefinationDetails.responseObject.getResult() != null) {
                        for (int i = 0; i < Home_WebHit_Post_Get_DiseaseDefinationDetails.responseObject.getResult().size(); i++) {
                            Home_WebHit_Post_Get_DiseaseDefinationDetails.ResponseModel dModel = Home_WebHit_Post_Get_DiseaseDefinationDetails.responseObject;
                            lst_AllDiseaseDefination.add(dModel);


                            if (AppConfig.getInstance().getDiseasesList().size() > 0) {
                                AppConfig.getInstance().getDiseasesList().clear();
                            }
                            AppConfig.getInstance().saveDiseasesList(lst_AllDiseaseDefination);
                        }
                    }

                } else {
//                    dismissProgDialog();
                    AppConfig.getInstance().showErrorMessage(MainActivity.this, strMsg);
                }
            }

            @Override
            public void onWebException(Exception e) {
                dismissProgDialog();
//                dismissProgDialog();
                AppConfig.getInstance().showErrorMessage(MainActivity.this, e.toString());
            }
        });
    }
    //endregion


    public void showInternetConnectionStableMessage(Context context, String _errorMsg) {
        customAlertDialog = new CustomAlertDialog(context, "", _errorMsg, "Allow", "Deny", true, new CustomAlertConfirmationInterface() {
            @Override
            public void callConfirmationDialogPositive() {

                requestDesignations();
                customAlertDialog.dismiss();
            }

            @Override
            public void callConfirmationDialogNegative() {


                customAlertDialog.dismiss();
            }
        });
        customAlertDialog.show();

    }


    //region InternetConnectionHandle
    @Override
    protected void onStart() {
        super.onStart();


        checkInternetConnection.addConnectionChangeListener(new ConnectionChangeListener() {
            @Override
            public void onConnectionChanged(boolean isConnectionAvailable) {
                if (AppConfig.getInstance().isInternetEnabled() && !isConnectionAvailable) {
//                    Toast.makeText(MainActivity.this, "Internet connection unavailable!", Toast.LENGTH_SHORT).show();


//                    AppConfig.getInstance().showErrorMessage(MainActivity.this, "Internet connection unavailable!\n Connect to internet in order to get latest updates");

//                    CustomToast.showToastMessage(MainActivity.this, "Internet connection unavailable!\n Connect to internet in order to get latest updates", Toast.LENGTH_LONG);
                    rlBotmbar.setVisibility(View.VISIBLE);
                    rlWifiDisabled.setVisibility(View.VISIBLE);
                    rlWifiEnabled.setVisibility(View.GONE);

                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            rlBotmbar.setVisibility(View.GONE);
                        }
                    }, 5000);


                    AppConfig.getInstance().saveInternetStatus(false);
//                    connectionAvailable = false;
                } else if (!AppConfig.getInstance().isInternetEnabled() && isConnectionAvailable) {

//                    Toast.makeText(MainActivity.this, "Internet connection is back again.", Toast.LENGTH_SHORT).show();

//                    showInternetConnectionStableMessage(MainActivity.this, "Internet connection is back again.\n Do you want to sync with internet?");

                    rlBotmbar.setVisibility(View.VISIBLE);
                    rlWifiDisabled.setVisibility(View.GONE);
                    rlWifiEnabled.setVisibility(View.VISIBLE);
                    requestWaitSubmit();
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            rlBotmbar.setVisibility(View.GONE);

                            if (AppConfig.getInstance().isFormSubmittionPending()) {

                                Log.d("sizeList", "lst_generateReportId: " + lst_generateReportId.size());

                                for (int i = 0; i < lst_generateReportId.size(); i++) {
                                    int finalI = i;
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {


                                            Home_WebHit_Post_GenerateDiseaseIntimationReport home_webHit_post_generateDiseaseIntimationReport = new Home_WebHit_Post_GenerateDiseaseIntimationReport();

                                            home_webHit_post_generateDiseaseIntimationReport.generateDiseaseIntimationReport(MainActivity.this,
                                                    new IWebCallback() {
                                                        @Override
                                                        public void onWebResult(boolean isSuccess, String strMsg) {

                                                            if (isSuccess) {
                                                                Log.d("LOG_AS", " generateDiseaseIntimationReport  isSuccess: " + Home_WebHit_Post_GenerateDiseaseIntimationReport.responseObject.getResult().toString());

                                                            } else
                                                                Log.d("LOG_AS", " generateDiseaseIntimationReport  failure: " + strMsg);
                                                        }

                                                        @Override
                                                        public void onWebException(Exception ex) {
                                                            Log.d("LOG_AS", " generateDiseaseIntimationReport  Exception: " + ex.getMessage());
                                                        }
                                                    }, lst_generateReportId.get(finalI));


                                        }
                                    }, AppConstt.LIMIT_TIMOUT_MILLIS);

                                }


//                        lst_generateReportId.clear();
                                AppConfig.getInstance().setFormSubmittionPending(false);
                            }

                        }
                    }, 5000);


                    AppConfig.getInstance().saveInternetStatus(true);
                }
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        checkInternetConnection.removeConnectionChangeListener();
    }
//endregion

    public void requestWaitSubmit() {

        if (AppConfig.getInstance().isFormSubmittionPending()) {
            Log.d("sizeList", "getSubmittionOFDisease: " + AppConfig.getInstance().getSubmittionOFDisease().size());
            for (int i = 0; i < AppConfig.getInstance().getSubmittionOFDisease().size(); i++) {

                Home_WebHit_Post_DiseaseByFarmer home_webHit_post_diseaseByFarmer = new Home_WebHit_Post_DiseaseByFarmer();

                home_webHit_post_diseaseByFarmer.sendDiseaseIntimationByFarmer(MainActivity.this, new IWebCallback() {
                    @Override
                    public void onWebResult(boolean isSuccess, String strMsg) {
                        dismissProgDialog();
                        if (isSuccess) {
                            if (Home_WebHit_Post_DiseaseByFarmer.responseObject != null &&
                                    Home_WebHit_Post_DiseaseByFarmer.responseObject.getResult() != null) {


                                lst_generateReportId.add(Home_WebHit_Post_DiseaseByFarmer.responseObject.getResult().getDiseaseIntimationID());
                                Log.d("sizeList", "lst_generateReportId ID: " + Home_WebHit_Post_DiseaseByFarmer.responseObject.getResult().getDiseaseIntimationID());

                                requestGenerateReport(Home_WebHit_Post_DiseaseByFarmer.responseObject.getResult().getDiseaseIntimationID());

                            } else {
                                AppConfig.getInstance().showErrorMessage(MainActivity.this, strMsg);
                            }

                        } else {
                            AppConfig.getInstance().showErrorMessage(MainActivity.this, strMsg);
                        }
                    }

                    @Override
                    public void onWebException(Exception ex) {
//                CustomToast.showToastMessage(IntroActivity.this, AppConfig.getInstance().getNetworkExceptionMessage(ex.getMessage()), Toast.LENGTH_SHORT);
                        AppConfig.getInstance().showErrorMessage(MainActivity.this, ex.getLocalizedMessage());
                    }
                }, AppConfig.getInstance().getSubmittionOFDisease().get(i));


            }


            if (AppConfig.getInstance().getSubmittionOFDisease().size() > 0) {
                AppConfig.getInstance().getSubmittionOFDisease().clear();
            }


            AppConfig.getInstance().lstpendingFormData.clear();
            AppConfig.getInstance().setFormSubmittionPending(false);

        }


//        if (AppConfig.getInstance().isFormSubmittionPending()) {
//
//            Log.d("sizeList","lst_generateReportId: " + lst_generateReportId.size());
//
//            for (int i=0;i<lst_generateReportId.size();i++)
//            {
//                int finalI = i;
//                final Handler handler = new Handler(Looper.getMainLooper());
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//
//
//                        Home_WebHit_Post_GenerateDiseaseIntimationReport home_webHit_post_generateDiseaseIntimationReport = new Home_WebHit_Post_GenerateDiseaseIntimationReport();
//
//                        home_webHit_post_generateDiseaseIntimationReport.generateDiseaseIntimationReport(MainActivity.this,
//                                new IWebCallback() {
//                                    @Override
//                                    public void onWebResult(boolean isSuccess, String strMsg) {
//
//                                        if (isSuccess) {
//                                            Log.d("LOG_AS", " generateDiseaseIntimationReport  isSuccess: " + Home_WebHit_Post_GenerateDiseaseIntimationReport.responseObject.getResult().toString());
//
//                                        } else Log.d("LOG_AS", " generateDiseaseIntimationReport  failure: " + strMsg);
//                                    }
//
//                                    @Override
//                                    public void onWebException(Exception ex) {
//                                        Log.d("LOG_AS", " generateDiseaseIntimationReport  Exception: " + ex.getMessage());
//                                    }
//                                }, lst_generateReportId.get(finalI));
//
//
//                    }
//                }, AppConstt.LIMIT_TIMOUT_MILLIS);
//
//            }
//
//
////                        lst_generateReportId.clear();
//            AppConfig.getInstance().setFormSubmittionPending(false);
//        }
    }

    private void requestGenerateReport(String id) {


        Home_WebHit_Post_GenerateDiseaseIntimationReport home_webHit_post_generateDiseaseIntimationReport = new Home_WebHit_Post_GenerateDiseaseIntimationReport();

        home_webHit_post_generateDiseaseIntimationReport.generateDiseaseIntimationReport(MainActivity.this,
                new IWebCallback() {
                    @Override
                    public void onWebResult(boolean isSuccess, String strMsg) {

                        if (isSuccess) {
                            Log.d("LOG_AS", " generateDiseaseIntimationReport  isSuccess: " + Home_WebHit_Post_GenerateDiseaseIntimationReport.responseObject.getResult().toString());

                        } else
                            Log.d("LOG_AS", " generateDiseaseIntimationReport  failure: " + strMsg);
                    }

                    @Override
                    public void onWebException(Exception ex) {
                        Log.d("LOG_AS", " generateDiseaseIntimationReport  Exception: " + ex.getMessage());
                    }
                }, id);
    }
}

