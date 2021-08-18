package ast.adrs.farmer.ParentFragments;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ast.adrs.farmer.AppConfig;
import ast.adrs.farmer.IntroAuxiliaries.WebServices.Intro_WebHit_Post_SignUp_Farmer;
import ast.adrs.farmer.R;
import ast.adrs.farmer.Utils.AppConstt;
import ast.adrs.farmer.Utils.IBadgeUpdateListener;
import ast.adrs.farmer.Utils.IWebCallback;
import ast.adrs.farmer.Utils.PinEntry;

public class LoginbackFragment extends Fragment implements View.OnClickListener {
    private TextView txvNtReceived;
    RelativeLayout rlLogin;
    private String strEnteredPIN, strPIN, strPhone;
    private PinEntry mPIN;
    private IBadgeUpdateListener mBadgeUpdateListener;

    public LoginbackFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View frg = inflater.inflate(R.layout.fragment_login_back, container, false);


        initData();
        bindViews(frg);


        Log.d("LOG_AS", "Pin is: " + AppConfig.getInstance().mUserData.getPinCode());

        return frg;
    }

    void setBottomBar() {

        try {
            mBadgeUpdateListener = (IBadgeUpdateListener) getActivity();
        } catch (ClassCastException castException) {
            castException.printStackTrace(); // The activity does not implement the listener
        }
        if (getActivity() != null && isAdded())
            mBadgeUpdateListener.setToolbarState(AppConstt.ToolbarState.TOOLBAR_HIDDEN);

    }

    private void initData() {
        setBottomBar();

        strEnteredPIN = "";
        strPIN = "";
        strPhone = "";

    }

    //region  functions for Dialog
    private void dismissProgDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private Dialog progressDialog;

    private void showProgDialog() {
        progressDialog = new Dialog(getActivity(), R.style.AppTheme);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setContentView(R.layout.dialog_progress);

        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    //endregion
    private void bindViews(View frg) {
        rlLogin = frg.findViewById(R.id.frg_verifiacction_rl_login);
        mPIN = frg.findViewById(R.id.frg_sign_up_verifictn_pin_entry);

        txvNtReceived = frg.findViewById(R.id.frg_sign_up_verifictn_txv_nt_received);


        mPIN.setOnPinEnteredListener(new PinEntry.OnPinEnteredListener() {
            @Override
            public void onPinEntered(String pin) {
                if (pin.length() == 4) {
                    strEnteredPIN = pin;
                    AppConfig.getInstance().closeKeyboard(getActivity());
                } else {
                    strEnteredPIN = "";
                }
            }
        });


        rlLogin.setOnClickListener(this);
        txvNtReceived.setOnClickListener(this);
    }

    private void requestResendOTP() {


        String lang = "";

        if (AppConfig.getInstance().mLanguage.equalsIgnoreCase(AppConstt.AppLang.LANG_UR)) {
            lang = "u";
        } else {
            lang = "e";
        }

        Date date = new Date();
        String str_DATE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH).format(date);
        String data = "{" +
                "\"id\"" + ":" + 0 + "," +
                "\"pinCode\"" + ":" + 0 + "," +

                "\"isDeleted\"" + ":" + true + "," +
                "\"isVerfied\"" + ":" + true + "," +
                "\"cnic\"" + ":" + AppConfig.getInstance().mUserData.getCNIC().replaceFirst("^0+(?!$)", "") + "," +
                "\"farmerName\"" + ":\"" + AppConfig.getInstance().mUserData.getName() + "\"," +
                "\"createdDate\"" + ":\"" + str_DATE + "\"," +
                "\"updatedDate\"" + ":\"" + str_DATE + "\"," +
                "\"mobileNumber\"" + ":" + AppConfig.getInstance().mUserData.getPhone() + "," +
                "\"preferedLanguage\"" + ":\"" + lang + "\"}";

        showProgDialog();
        Intro_WebHit_Post_SignUp_Farmer intro_webHit_post_signinFarmer = new Intro_WebHit_Post_SignUp_Farmer();
        intro_webHit_post_signinFarmer.postSignUp(getContext(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {


                if (isSuccess) {
                    dismissProgDialog();

                    AppConfig.getInstance().mUserData.setName(Intro_WebHit_Post_SignUp_Farmer.responseObject.getResult().getFarmerName());
                    AppConfig.getInstance().mUserData.setCNIC(Intro_WebHit_Post_SignUp_Farmer.responseObject.getResult().getCnic());
                    AppConfig.getInstance().mUserData.setPinCode(Intro_WebHit_Post_SignUp_Farmer.responseObject.getResult().getPinCode());

                    AppConfig.getInstance().mUserData.setRefreshToken(Intro_WebHit_Post_SignUp_Farmer.responseObject.getResult().getUserViewModel().getRefreshToken());
                    AppConfig.getInstance().mUserData.setAuthToken(Intro_WebHit_Post_SignUp_Farmer.responseObject.getResult().getUserViewModel().getAuthToken());


                    AppConfig.getInstance().saveUserProfileData();

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
        }, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.frg_sign_up_verifictn_txv_nt_received:
                requestResendOTP();


                break;


            case R.id.frg_verifiacction_rl_login:
                if (mPIN.getText().toString().equalsIgnoreCase(String.valueOf(AppConfig.getInstance().mUserData.getPinCode()))) {
                    AppConfig.getInstance().mUserData.setLoggedInTemp(true);
                    AppConfig.getInstance().saveUserProfileData();
                    navtoHomeFragmnet();
                } else
                    AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.enter_otp__old));
        }
    }

    private void navtoHomeFragmnet() {

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft;
        Fragment frg = new HomeFragment();
        ft = fm.beginTransaction();
        ft.replace(R.id.act_main_content_frg, frg, AppConstt.FragTag.FN_HomeFragment);
//        ft.addToBackStack(AppConstt.FragTag.FN_HomeFragment);
//        hideLastStackFragment(ft);
        ft.commit();
    }


}
