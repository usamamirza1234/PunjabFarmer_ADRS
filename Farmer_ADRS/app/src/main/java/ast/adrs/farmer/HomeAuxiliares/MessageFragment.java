package ast.adrs.farmer.HomeAuxiliares;


import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ast.adrs.farmer.AppConfig;
import ast.adrs.farmer.HomeAuxiliares.WebServices.Home_WebHit_Post_Messages_of_Employee;
import ast.adrs.farmer.IntimateDiseaseAuxiliaries.MessageDiseaseReportFragment;
import ast.adrs.farmer.R;
import ast.adrs.farmer.Utils.AppConstt;
import ast.adrs.farmer.Utils.IAdapterCallback;
import ast.adrs.farmer.Utils.IBadgeUpdateListener;
import ast.adrs.farmer.Utils.IWebCallback;


public class MessageFragment extends Fragment implements View.OnClickListener {


    MessagesListingRcvAdapter messagesListingRcvAdapter;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    RecyclerView rcv_populatin;
    private IBadgeUpdateListener mBadgeUpdateListener;
    private ArrayList<DModel_Message> lstPopulation;
    private Dialog progressDialog;

    public MessageFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View frg = inflater.inflate(R.layout.fragment_message, container, false);
        initData();
        bindViews(frg);
        requestEmpMsges();
        return frg;
    }

    private void requestEmpMsges() {
        showProgDialog();
        Home_WebHit_Post_Messages_of_Employee home_webHit_post_messages_of_employee = new Home_WebHit_Post_Messages_of_Employee();
        home_webHit_post_messages_of_employee.getMsgofEmp(getContext(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {
                    dismissProgDialog();
                    lstPopulation = new ArrayList<>();
                    lstPopulation.clear();


                    int size =( Home_WebHit_Post_Messages_of_Employee.responseObject.getResult().size() -1);
                    for (int i = size; i >=0 ; i--) {

//                    for (int i = 0; i < Home_WebHit_Post_Messages_of_Employee.responseObject.getResult().size(); i++) {


                        String strdate = "";
                        String strtime = "";

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH);
                        try {
                            Date date = format.parse(Home_WebHit_Post_Messages_of_Employee.responseObject.getResult().get(i).getCreatedDate());


                            Log.d("DATE", "date : " + date);

                            SimpleDateFormat dayMonth = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                            strdate = dayMonth.format(date);
                            Log.d("DATE", "strdate : " + strdate);


                            SimpleDateFormat time = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
                            strtime = time.format(date);
                            Log.d("DATE", "strtime : " + strtime);


                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        DModel_Message dModel_message = new DModel_Message(strdate,
                                (strtime + " Hrs")
                                , Home_WebHit_Post_Messages_of_Employee.responseObject.getResult().get(i).getMessageDesc(),
                                Home_WebHit_Post_Messages_of_Employee.responseObject.getResult().get(i).getReportID()
                        );


//                        DModel_Message dModel_message = new DModel_Message(Home_WebHit_Post_Messages_of_Employee.responseObject.getResult().get(i).getCreatedDate(),
//                                Home_WebHit_Post_Messages_of_Employee.responseObject.getResult().get(i).getCreatedDate()
//                                , Home_WebHit_Post_Messages_of_Employee.responseObject.getResult().get(i).getMessageDesc()
//                        );

                        lstPopulation.add(dModel_message);

                    }


                    if (messagesListingRcvAdapter == null) {

                        messagesListingRcvAdapter = new MessagesListingRcvAdapter(getActivity(), lstPopulation, (eventId, position) -> {
                            switch (eventId) {
                                case IAdapterCallback.EVENT_A:
                                    navtoSummaryFragment(lstPopulation.get(position).getReportId());

                                    break;
                            }
                        });


                        rcv_populatin.setLayoutManager(linearLayoutManager);
                        rcv_populatin.setAdapter(messagesListingRcvAdapter);

                    } else {
                        messagesListingRcvAdapter.notifyDataSetChanged();
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

    private void navtoSummaryFragment(String reportid) {
        Bundle bundle = new Bundle();
        Fragment frg = new MessageDiseaseReportFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        bundle.putString("key_report_id", reportid);

        ft.add(R.id.act_main_content_frg, frg, AppConstt.FragTag.FN_SummeryDiseaseFragment);
        ft.addToBackStack(AppConstt.FragTag.FN_SummeryDiseaseFragment);
        frg.setArguments(bundle);
        ft.hide(this);
        ft.commit();
    }


    private void initData() {
        setBottomBar();
    }

    private void bindViews(View frg) {


        rcv_populatin = frg.findViewById(R.id.frg_mesg_rcv_msges);
    }

    void setBottomBar() {


        try {
            mBadgeUpdateListener = (IBadgeUpdateListener) getActivity();
        } catch (ClassCastException castException) {
            castException.printStackTrace(); // The activity does not implement the listener
        }
        if (getActivity() != null && isAdded()) {
            mBadgeUpdateListener.setToolbarState(AppConstt.INTRO_ToolbarStates.signinFarmer);
            mBadgeUpdateListener.setHeaderTitle(getString(R.string.message_box));
        }

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


            default:
                break;
        }
    }


}


