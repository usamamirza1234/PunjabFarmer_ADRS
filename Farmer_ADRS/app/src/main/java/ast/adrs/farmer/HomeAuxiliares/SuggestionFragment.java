package ast.adrs.farmer.HomeAuxiliares;


import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ast.adrs.farmer.AppConfig;
import ast.adrs.farmer.HomeAuxiliares.WebServices.Home_WebHit_Post_Farmer_Suggestions;
import ast.adrs.farmer.HomeAuxiliares.WebServices.Home_WebHit_Post_SuggestionByUser;
import ast.adrs.farmer.R;
import ast.adrs.farmer.Utils.AppConstt;
import ast.adrs.farmer.Utils.CustomToast;
import ast.adrs.farmer.Utils.IAdapterCallback;
import ast.adrs.farmer.Utils.IBadgeUpdateListener;
import ast.adrs.farmer.Utils.IWebCallback;


public class SuggestionFragment extends Fragment implements View.OnClickListener {
    SuggestionListingRcvAdapter suggestionListingRcvAdapter = null;
    Button txvSend;
    EditText edtSuggestion;

    RecyclerView rcv_populatin;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    private ArrayList<DModel_Message> lstPopulation;
    private IBadgeUpdateListener mBadgeUpdateListener;
    private Dialog progressDialog;

    public SuggestionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View frg = inflater.inflate(R.layout.fragment_suggestion, container, false);
        initData();
        bindViews(frg);
        requestSuggestions();

        return frg;
    }

    private void initData() {
        setBottomBar();
        lstPopulation = new ArrayList<>();
    }

    private void bindViews(View frg) {
        txvSend = frg.findViewById(R.id.frg_contact_us_txv_send);
        edtSuggestion = frg.findViewById(R.id.frg_contact_us_edt_subcribe);


        rcv_populatin = frg.findViewById(R.id.frg_mesg_rcv_msges);
        txvSend.setOnClickListener(this);
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


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.frg_contact_us_txv_send:
                if (!edtSuggestion.getText().toString().equalsIgnoreCase("")) {
                    requestSendSuggestion();
                }
                else    AppConfig.getInstance().showErrorMessage(getContext(), getResources().getString(R.string.enter_your_suggestions));

                break;

            default:
                break;
        }
    }

    private void requestSendSuggestion() {

        String subject = "";

        if (AppConfig.getInstance().mUserData.isFarmer()) {
            subject = "farmer";
        } else {
            subject = "va";
        }

        String data = "{" +
                "\"id\"" + ":" + 0 + "," +
                "\"subject\"" + ":\"" + subject + "\"," +
                "\"suggestion\"" + ":\"" + edtSuggestion.getText().toString() + "\"," +
                "\"userType\"" + ":" + 1 +
                "}";

        showProgDialog();
        Home_WebHit_Post_Farmer_Suggestions home_webHit_post_farmer_suggestions = new Home_WebHit_Post_Farmer_Suggestions();
        home_webHit_post_farmer_suggestions.sendSuggestion(getContext(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {
                    dismissProgDialog();

                    CustomToast.showToastMessage(getActivity(), "Successfully Received ", Toast.LENGTH_SHORT);
                    edtSuggestion.setText("");
                    requestSuggestions();


                } else {

                    dismissProgDialog();

                    AppConfig.getInstance().showErrorMessage(getContext(), strMsg);
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


    void setBottomBar() {
        try {
            mBadgeUpdateListener = (IBadgeUpdateListener) getActivity();
        } catch (ClassCastException castException) {
            castException.printStackTrace(); // The activity does not implement the listener
        }
        if (getActivity() != null && isAdded()) {
            mBadgeUpdateListener.setToolbarState(AppConstt.INTRO_ToolbarStates.signinFarmer);
            mBadgeUpdateListener.setHeaderTitle(getString(R.string.suggestion));
        }

    }


    private void requestSuggestions() {

        showProgDialog();
        Home_WebHit_Post_SuggestionByUser home_webHit_post_suggestionByUser = new Home_WebHit_Post_SuggestionByUser();
        home_webHit_post_suggestionByUser.getSuggestions(getContext(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {
                    dismissProgDialog();

                    lstPopulation.clear();
                    for (int i = 0; i < Home_WebHit_Post_SuggestionByUser.responseObject.getResult().size(); i++) {


                        String strdate = "";
                        String strtime = "";

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH);
                        try {
                            Date date = format.parse(Home_WebHit_Post_SuggestionByUser.responseObject.getResult().get(i).getCreatedDate());


                            Log.d("DATE", "date : " + date);

                            SimpleDateFormat dayMonth = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                            strdate = dayMonth.format(date);
                            Log.d("DATE", "strdate : " + strdate);


                            SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
                            strtime = time.format(date);
                            Log.d("DATE", "strtime : " + strtime);


                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        DModel_Message dModel_message = new DModel_Message(strdate,
                                Home_WebHit_Post_SuggestionByUser.responseObject.getResult().get(i).getSubject()
                                , Home_WebHit_Post_SuggestionByUser.responseObject.getResult().get(i).getSuggestion(),
                                Home_WebHit_Post_SuggestionByUser.responseObject.getResult().get(i).getSubject()
                        );


                        lstPopulation.add(dModel_message);

                    }


                    if (suggestionListingRcvAdapter == null) {

                        suggestionListingRcvAdapter = new SuggestionListingRcvAdapter(getActivity(), lstPopulation, (eventId, position) -> {
                            switch (eventId) {
                                case IAdapterCallback.EVENT_A:


                                    break;
                            }
                        });


                        rcv_populatin.setLayoutManager(linearLayoutManager);
                        rcv_populatin.setAdapter(suggestionListingRcvAdapter);

                    } else {
                        suggestionListingRcvAdapter.notifyDataSetChanged();
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
        });
    }
}


