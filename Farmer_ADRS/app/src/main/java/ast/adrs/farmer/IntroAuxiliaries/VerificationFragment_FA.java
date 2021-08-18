package ast.adrs.farmer.IntroAuxiliaries;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ast.adrs.farmer.AppConfig;
import ast.adrs.farmer.IntroAuxiliaries.WebServices.Intro_WebHit_Post_SignUp_Farmer;
import ast.adrs.farmer.IntroAuxiliaries.WebServices.Intro_WebHit_Post_VerifyPinCode_FA;
import ast.adrs.farmer.R;
import ast.adrs.farmer.Utils.AppConstt;
import ast.adrs.farmer.Utils.IWebCallback;
import ast.adrs.farmer.Utils.PinEntry;
import swarajsaaj.smscodereader.interfaces.OTPListener;
import swarajsaaj.smscodereader.receivers.OtpReader;

public class VerificationFragment_FA extends Fragment implements View.OnClickListener, OTPListener {


    final static int resendTries = 2;
    private static final long TIME_COUNTDOWN = 5 * 1000;
    private static final int PERMISSIONS_REQUEST_RECEIVE_SMS = 0;
    RelativeLayout rlLogin;
    int pinCodeTries = 1;
    private String strEnteredPIN, strPIN, strPhone;
    private EditText edtCode;
    private Button btnConfirm;
    private TextView txvResend, txvCountDown;
    private PinEntry mPIN;
    private TextView txvNtReceived;
    private TextView txvNtReceivedShown;
    private TextView txvOTP;
    private CountDownTimer mTimer;
    private Dialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View frg = inflater.inflate(R.layout.fragment_verification_fa, container, false);

        initData();
        bindViews(frg);
        showSoftKeyboardForced();
        startCountDownTimer(TIME_COUNTDOWN);


        String strPhoneNumber = String.valueOf(AppConfig.getInstance().mUserData.getPhone());
        strPhoneNumber = "" + strPhoneNumber.substring(1);
        strPhoneNumber = "0" + strPhoneNumber.substring(1);

        String str_changeTextColot = getColoredSpanned("", "#A0A0A0");
        str_changeTextColot = getColoredSpanned(strPhoneNumber, "#7DD92958");


        String str_firstText = getResources().getString(R.string.verification_code_send);
        String str_lastText = getResources().getString(R.string.verification_code_send_2);

        txvOTP.setText(Html.fromHtml(str_firstText + " " + str_changeTextColot + ". " + str_lastText + ". "));


        Log.d("LOG_AS", "postSignUp: getPhone " + AppConfig.getInstance().mUserData.getPhone());


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


    private void requestVerifyPin(String data) {
        showProgDialog();
        Intro_WebHit_Post_VerifyPinCode_FA intro_webHit_post_verifyPinCode_fa = new Intro_WebHit_Post_VerifyPinCode_FA();

        intro_webHit_post_verifyPinCode_fa.postVerifyPinCode(getContext(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                dismissProgDialog();
                if (isSuccess) {
                    if (Intro_WebHit_Post_VerifyPinCode_FA.responseObject != null &&
                            Intro_WebHit_Post_VerifyPinCode_FA.responseObject.getResult() != null) {


                        navToCompleteFAProfileFragment();


                    }

                } else {
                    AppConfig.getInstance().showErrorMessage(getContext(), strMsg);
                }
            }

            @Override
            public void onWebException(Exception ex) {
//                CustomToast.showToastMessage(IntroActivity.this, AppConfig.getInstance().getNetworkExceptionMessage(ex.getMessage()), Toast.LENGTH_SHORT);
                AppConfig.getInstance().showErrorMessage(getContext(), ex.getLocalizedMessage());
            }
        }, data);

    }

    private void navToCompleteFAProfileFragment() {

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag = new CompleteFarmerProfile();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isComingFromIntro",true);
        ft.add(R.id.act_intro_content_frg, frag, AppConstt.FragTag.FN_CompleteFarmerProfileFragment);
        ft.addToBackStack(AppConstt.FragTag.FN_CompleteFarmerProfileFragment);
        frag.setArguments(bundle);
        ft.hide(this);
        ft.commit();
    }

    private void initData() {
        // Request the permission immediately here for the first time run
        requestPermissions(Manifest.permission.RECEIVE_SMS, PERMISSIONS_REQUEST_RECEIVE_SMS);

        strEnteredPIN = "";
        strPIN = "";
        strPhone = "";
        OtpReader.bind(this, "8700");
    }

    private void requestPermissions(String permission, int requestCode) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getContext(), permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(getContext(), "Granting permission is necessary!", Toast.LENGTH_LONG).show();

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{permission},
                        requestCode);

                // requestCode is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
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

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void otpReceived(String smsText) {
        //Do whatever you want to do with the text

        String number = smsText.replaceAll("\\D+", "");
        try {
            mPIN.setText(number);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Toast.makeText(getContext(),"Got "+number,Toast.LENGTH_LONG).show();
        Log.d("Otp", number);
    }

    private void startCountDownTimer(long mCountDownTime) {
        txvNtReceived.setOnClickListener(null);

        txvNtReceived.setTextColor(getResources().getColor(R.color.gray));

        txvCountDown.setVisibility(View.VISIBLE);
        if (mTimer != null)
            mTimer.cancel();

        mTimer = new CountDownTimer(mCountDownTime, 1000) {

            public void onTick(long millisUntilFinished) {
//                txvCountdown.setText(millisUntilFinished / 1000 + " ");
                if (txvCountDown != null)
                    if ((millisUntilFinished % 60000 / 1000) < 10) {
                        txvCountDown.setText("0" + (millisUntilFinished / 60000) + ":" + "0" + (millisUntilFinished % 60000 / 1000));
                    } else {
                        txvCountDown.setText("0" + (millisUntilFinished / 60000) + ":" + (millisUntilFinished % 60000 / 1000));
                    }

//                remMillis = millisUntilFinished;
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                txvNtReceived.setVisibility(View.VISIBLE);
                hideCountDownTimer();
            }

        }.start();
    }

    private void hideCountDownTimer() {
        try {
            if (mTimer != null)
                mTimer.cancel();

            txvNtReceived.setOnClickListener(this);

            txvNtReceived.setTextColor(getResources().getColor(R.color.black));

            txvCountDown.setVisibility(View.INVISIBLE);

            txvCountDown.setText("00:00");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindViews(View frg) {


        mPIN = frg.findViewById(R.id.frg_sign_up_verifictn_pin_entry);
        txvNtReceived = frg.findViewById(R.id.frg_sign_up_verifictn_txv_nt_received);
        txvNtReceivedShown = frg.findViewById(R.id.frg_sign_up_verifictn_txv_nt_received_showen);
        txvOTP = frg.findViewById(R.id.frg_varification_va_txv_txv_otp);
        txvCountDown = frg.findViewById(R.id.frg_sign_up_verifictn_txv_countdown);
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


        rlLogin = frg.findViewById(R.id.frg_verifiacction_rl_login);


        txvNtReceived.setOnClickListener(this);
        rlLogin.setOnClickListener(this);
//        if (strEnteredPIN.length() == 6) {
//            if (AppConfig.getInstance().isComingFromHome) {
//                progressDilogue.startiOSLoader(getActivity(), R.drawable.image_for_rotation, getString(R.string.please_wait), false);
//                requestValidateCode(AppConfig.getInstance().mUser.getmPhoneNumber(), strEnteredPIN);
//            } else {
//                progressDilogue.startiOSLoader(getActivity(), R.drawable.image_for_rotation, getString(R.string.please_wait), false);
//                requestValidateCode(strPhone, strEnteredPIN);
//            }
////                    hideCountDownTimer();
//        } else {
//            showAlertDialog(getString(R.string.sign_up_enter_account_setup_heading), getString(R.string.enter_valid_pin_message), null, null, false, true, null);
//        }

//        btnConfirm = frg.findViewById(R.id.frg_vrfctn_btn_confirm);
//        txvResend = frg.findViewById(R.id.frg_vrfctn_txv_resend);
//        btnConfirm.setTransformationMethod(null);
//
//        btnConfirm.setOnClickListener(this);
//        txvResend.setOnClickListener(this);
//
//
//        edtCode = frg.findViewById(R.id.frg_vrfctn_edt_code);
//
//        //Language based special cases
//        if (AppConfig.getInstance().isEnglishMode) {
//            edtCode.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
//        } else {
//            edtCode.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
//        }
    }


    private void hideMyKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtCode.getWindowToken(), 0);
    }

    private void showSoftKeyboardForced() {
        try {
            final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            edtCode.postDelayed(new Runnable() {
                @Override
                public void run() {
                    edtCode.requestFocus();
                    imm.showSoftInput(edtCode, InputMethodManager.SHOW_IMPLICIT);
                }
            }, 100);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.frg_sign_up_verifictn_txv_nt_received:
                requestCode();


                break;
            case R.id.frg_verifiacction_rl_login:


                Log.d("LOG_AS", "postSignUp: getPinCode " + AppConfig.getInstance().mUserData.getPinCode());
                Log.d("LOG_AS", "postSignUp: strEnteredPIN " + strEnteredPIN);

                if (mPIN.getText().toString().equalsIgnoreCase(String.valueOf(AppConfig.getInstance().mUserData.getPinCode()))) {
                    String lang = "";

                    if (AppConfig.getInstance().mLanguage.equalsIgnoreCase(AppConstt.AppLang.LANG_UR)) {
                        lang = "u";
                    } else {
                        lang = "e";
                    }


                    Log.d("LOG_AS", "postSignUp: getPhone " + AppConfig.getInstance().mUserData.getPhone());
                    Date date = new Date();
                    String str_DATE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH).format(date);
                    String data = "{" +
                            "\"id\"" + ":" + 0 + "," +
                            "\"pinCode\"" + ":" + AppConfig.getInstance().mUserData.getPinCode() + "," +

                            "\"isDeleted\"" + ":" + true + "," +
                            "\"isVerfied\"" + ":" + true + "," +
                            "\"cnic\"" + ":" + AppConfig.getInstance().mUserData.getCNIC().replaceFirst("^0+(?!$)", "") + "," +
                            "\"farmerName\"" + ":\"" + AppConfig.getInstance().mUserData.getName() + "\"," +
                            "\"createdDate\"" + ":\"" + str_DATE + "\"," +
                            "\"updatedDate\"" + ":\"" + str_DATE + "\"," +
                            "\"mobileNumber\"" + ":" + AppConfig.getInstance().mUserData.getPhone() + "," +
                            "\"preferedLanguage\"" + ":\"" + lang + "\"}";


                    Log.d("LOG_AS", "postSignUp: " + data);


                    requestVerifyPin(data);

                } else
                    AppConfig.getInstance().showErrorMessage(getContext(), getString(R.string.enter_otp_that_send));

                break;
            default:
                break;
        }
    }

    private void requestCode() {


        Log.d("LOG_AS", "Resend " + resendTries);
        Log.d("LOG_AS", "PinCodeTries " + pinCodeTries);
        if (pinCodeTries < resendTries) {
            requestResendOTP();

        } else {

            showOTP();

        }
    }

    private void showOTP() {
        String str_changeTextColot = getColoredSpanned("", "#A0A0A0");

        str_changeTextColot = getColoredSpanned(String.valueOf(AppConfig.getInstance().mUserData.getPinCode()), "#E80F1F");

        txvNtReceived.setVisibility(View.GONE);

        String str_firstText = getResources().getString(R.string.technical_fault_otp);

        txvNtReceivedShown.setText(Html.fromHtml(str_firstText + " " + str_changeTextColot));


        mPIN.setText(Html.fromHtml(str_changeTextColot));

        txvNtReceivedShown.setVisibility(View.VISIBLE);
    }


    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
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

                Log.d("LOG_AS", "Resend " + resendTries);
                Log.d("LOG_AS", "PinCodeTries " + pinCodeTries);

                if (isSuccess) {
                    dismissProgDialog();
                    startCountDownTimer(TIME_COUNTDOWN);
                    pinCodeTries++;
                    AppConfig.getInstance().mUserData.setName(Intro_WebHit_Post_SignUp_Farmer.responseObject.getResult().getFarmerName());
                    AppConfig.getInstance().mUserData.setCNIC(Intro_WebHit_Post_SignUp_Farmer.responseObject.getResult().getCnic());
                    AppConfig.getInstance().mUserData.setPinCode(Intro_WebHit_Post_SignUp_Farmer.responseObject.getResult().getPinCode());

                    AppConfig.getInstance().mUserData.setRefreshToken(Intro_WebHit_Post_SignUp_Farmer.responseObject.getResult().getUserViewModel().getRefreshToken());
                    AppConfig.getInstance().mUserData.setAuthToken(Intro_WebHit_Post_SignUp_Farmer.responseObject.getResult().getUserViewModel().getAuthToken());
                } else {
                    dismissProgDialog();
                    AppConfig.getInstance().showErrorMessage(getContext(), strMsg);
                }
            }

            @Override
            public void onWebException(Exception e) {
                dismissProgDialog();
                Log.d("LOG_AS", "farmer Registration Exception: " + e.getMessage());
                if (pinCodeTries < resendTries) {

                    AppConfig.getInstance().showErrorMessage(getContext(), e.toString());
                    showOTP();
                }
            }
        }, data);
    }

}
