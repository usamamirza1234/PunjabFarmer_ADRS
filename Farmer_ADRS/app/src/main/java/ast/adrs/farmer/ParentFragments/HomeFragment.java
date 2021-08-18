package ast.adrs.farmer.ParentFragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import ast.adrs.farmer.AppConfig;
import ast.adrs.farmer.HomeAuxiliares.IntimateDiseaseFragment;
import ast.adrs.farmer.HomeAuxiliares.MessageFragment;
import ast.adrs.farmer.HomeAuxiliares.MyFarmFragment;

import ast.adrs.farmer.HomeAuxiliares.SuggestionFragment;
import ast.adrs.farmer.IntroActivity;
import ast.adrs.farmer.IntroAuxiliaries.DModel_Animals;
import ast.adrs.farmer.IntroAuxiliaries.MyProfileFragment;
import ast.adrs.farmer.MainActivity;
import ast.adrs.farmer.MyApplication;
import ast.adrs.farmer.R;
import ast.adrs.farmer.Utils.AppConstt;
import ast.adrs.farmer.Utils.CircleImageView;
import ast.adrs.farmer.Utils.CustomAlertConfirmationInterface;
import ast.adrs.farmer.Utils.CustomAlertDialog;
import ast.adrs.farmer.Utils.IBadgeUpdateListener;

public class HomeFragment extends Fragment implements View.OnClickListener {
    public CustomAlertDialog customAlertDialog;
    public ArrayList<DModel_Animals> lstAnimal;
    ImageView imv_logo;
    TextView txv_Name;
    Button btnLogout;
    private RelativeLayout rlSwitchLang, rlContinue;
    TextView txv_address;
    LinearLayoutManager linearLayoutManager;
    LinearLayout llSuggestion, llMyfarm, llDiesase, llMyProfile, llMsgBox, llsync;
    private IBadgeUpdateListener mBadgeUpdateListener;
    private Dialog progressDialog;
    CircleImageView civFarmerImv;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View frg = inflater.inflate(R.layout.fragment_home, container, false);
        initData();
        bindViews(frg);


        Log.d("LOG_AS", "ID: " + AppConfig.getInstance().mUserData.getId());
        Log.d("LOG_AS", "ID: " + AppConfig.getInstance().mUserData.getAuthToken());
        txv_Name.setText(AppConfig.getInstance().mUserData.getName());
        txv_address.setText(
                "M: " + AppConfig.getInstance().mUserData.getMozah().trim()
                        + " T: " + AppConfig.getInstance().mUserData.getTehsil().trim()
                        + "\nD: " + AppConfig.getInstance().mUserData.getDestrict().trim()
        );
        llMyfarm.setVisibility(View.VISIBLE);


        if (!AppConfig.getInstance().mUserData.getEncorededImage().isEmpty()) {
            byte[] decodedString = Base64.decode(AppConfig.getInstance().mUserData.getEncorededImage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            civFarmerImv.setImageBitmap(decodedByte);
        }
        return frg;
    }


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
    private void initData() {
        setBottomBar();
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        this.lstAnimal = new ArrayList<>();

    }

    void setBottomBar() {
        try {
            mBadgeUpdateListener = (IBadgeUpdateListener) getActivity();
        } catch (ClassCastException castException) {
            castException.printStackTrace(); // The activity does not implement the listener
        }
        if (getActivity() != null && isAdded()) {
            mBadgeUpdateListener.setToolbarState(AppConstt.ToolbarState.TOOLBAR_BACK_TITLE_HIDDEN);
            mBadgeUpdateListener.setHeaderTitle("HOME");
        }
    }

    private void bindViews(View frg) {

        civFarmerImv = frg.findViewById(R.id.frg_complete_profile_imv_profile);

        imv_logo = frg.findViewById(R.id.act_intro_rl_toolbar_logo);
        llSuggestion = frg.findViewById(R.id.frg_home_llSuggestion);
        llMsgBox = frg.findViewById(R.id.frg_home_llMsg);
        llMyfarm = frg.findViewById(R.id.frg_home_llMyFarm);
        llMyProfile = frg.findViewById(R.id.frg_home_llMyProfile);
        llDiesase = frg.findViewById(R.id.frg_home_llDisease);
        llsync = frg.findViewById(R.id.frg_home_ll_sync);

        btnLogout = frg.findViewById(R.id.frg_home_btn_logout);
        txv_Name = frg.findViewById(R.id.frg_home_txv_next);
        txv_address = frg.findViewById(R.id.frg_home_txv_address);

        rlSwitchLang = frg.findViewById(R.id.frg_sgnin_rl_switchlang);

        btnLogout.setOnClickListener(this);
        llSuggestion.setOnClickListener(this);
        llMsgBox.setOnClickListener(this);
        llMyfarm.setOnClickListener(this);
        llMyProfile.setOnClickListener(this);
        llDiesase.setOnClickListener(this);
        llsync.setOnClickListener(this);
        rlSwitchLang.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frg_home_llDisease:
                if (AppConfig.getInstance().mUserData.isFarmer()) {
                    navtoDieaseFragment();
                }

                break;

            case R.id.frg_home_llMyProfile:
                navtoMyProfileFragment();
                break;

            case R.id.frg_home_llMyFarm:
                navtoMyFarm();
                break;
            case R.id.frg_home_llSuggestion:
                navtoMySuggestion();
                break;

            case R.id.frg_home_llMsg:
                navtoMsgFragment();
                break;
            case R.id.frg_home_btn_logout:
                showLogoutMessage(getContext(), getString(R.string.are_sure_logout));

                break;
            case R.id.frg_home_ll_sync:
                ((MainActivity) getActivity()).requestDesignations();
                break;

            case R.id.frg_sgnin_rl_switchlang:
                switchLang();
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

    private void navtoMsgFragment() {
        Fragment frg = new MessageFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.act_main_content_frg, frg, AppConstt.FragTag.FN_MessageFragment);
        ft.addToBackStack(AppConstt.FragTag.FN_MessageFragment);
        ft.hide(this);
        ft.commit();
    }

    private void navtoMySuggestion() {
        Fragment frg = new SuggestionFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.act_main_content_frg, frg, AppConstt.FragTag.FN_SuggestionFragment);
        ft.addToBackStack(AppConstt.FragTag.FN_SuggestionFragment);
        ft.hide(this);
        ft.commit();
    }

    private void navtoDieaseFragment() {
        IntimateDiseaseFragment frg = new IntimateDiseaseFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.act_main_content_frg, frg, AppConstt.FragTag.FN_IntimateDiseaseFragment);
        ft.addToBackStack(AppConstt.FragTag.FN_IntimateDiseaseFragment);
        ft.hide(this);
        ft.commit();

    }

    private void navtoMyFarm() {
        Fragment frg = new MyFarmFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.act_main_content_frg, frg, AppConstt.FragTag.FN_MyFarmFragment);
        ft.addToBackStack(AppConstt.FragTag.FN_MyFarmFragment);
        ft.hide(this);
        ft.commit();

    }

    private void navtoMyProfileFragment() {
        Fragment frg = new MyProfileFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isComingFromMain", true);
        ft.add(R.id.act_main_content_frg, frg, AppConstt.FragTag.FN_MyProfileFragment);
        ft.addToBackStack(AppConstt.FragTag.FN_MyProfileFragment);
        Log.d("LOG_AS", "isComingFromMain " + true);
        frg.setArguments(bundle);
        ft.hide(this);
        ft.commit();

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            setBottomBar();
        }
    }

    public void showLogoutMessage(Context context, String _errorMsg) {
        customAlertDialog = new CustomAlertDialog(context, "", _errorMsg, "Yes", "No", true, new CustomAlertConfirmationInterface() {
            @Override
            public void callConfirmationDialogPositive() {
//                AppConfig.getInstance().navtoLogin();
                AppConfig.getInstance().mUserData.setLoggedInTemp(false);
                AppConfig.getInstance().saveUserProfileData();
                getActivity().finishAffinity();
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
