package ast.adrs.farmer.IntimateDiseaseAuxiliaries;

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
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ast.adrs.farmer.AppConfig;
import ast.adrs.farmer.IntimateDiseaseAuxiliaries.WebServices.Home_WebHit_Post_Get_Report_By_ReportID;
import ast.adrs.farmer.MainActivity;
import ast.adrs.farmer.R;
import ast.adrs.farmer.Utils.AppConstt;
import ast.adrs.farmer.Utils.IBadgeUpdateListener;
import ast.adrs.farmer.Utils.IWebCallback;


/**
 * Created by Usama Khalid Mirza on 06/05/2021.
 * usamamirza.andpercent@gmail.com
 */

public class MessageDiseaseReportFragment extends Fragment implements View.OnClickListener {
    RecyclerView rcv_option;
    View view;
    RelativeLayout rlHome;
    TextView txvName, txvSymbtoms, txvPhone, txvFarmId, txvSpices, txvDisease, txvReportID, txv_barcode, txvTotalAniamals, txv_address,
            txvTehsil, txvDistrict, txvMozah, txvDate, txvLatLong;
    String getReport = "";
    private IBadgeUpdateListener mBadgeUpdateListener;
    private Dialog progressDialog;

    public MessageDiseaseReportFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_summary_disease, container, false);

        initData();
        bindViews();


        requestReport(getReport);


        return view;
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

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            getReport = bundle.getString("key_report_id", "");
        }


    }

    private void bindViews() {
        rcv_option = view.findViewById(R.id.frg_complete_profile_rcv_populatin);
        rlHome = view.findViewById(R.id.frg_signin_rl_login);
        txvName = view.findViewById(R.id.frg_txv_name);
        txvPhone = view.findViewById(R.id.frg_txv_mobile);
        txvDisease = view.findViewById(R.id.frg_txv_dease);
        txvSpices = view.findViewById(R.id.frg_txv_spices);
        txvFarmId = view.findViewById(R.id.frg_txv_farmname);
        txvSymbtoms = view.findViewById(R.id.frg_txv_symbtoms);
        txvReportID = view.findViewById(R.id.frg_txv_report);
        txv_barcode = view.findViewById(R.id.frg_txv_txv_barcode);
        txvTotalAniamals = view.findViewById(R.id.frg_txv_total_animal);
        txvTehsil = view.findViewById(R.id.frg_txv_teh);
        txvMozah = view.findViewById(R.id.frg_txv_address);
        txvDistrict = view.findViewById(R.id.frg_txv_district);
        txvDate = view.findViewById(R.id.frg_txv_date);
        txvLatLong = view.findViewById(R.id.txv_lat_long);
        rlHome.setOnClickListener(this::onClick);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            setBottomBar();
        }
    }
    //endregion


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.frg_signin_rl_login:

                ((MainActivity) getActivity()).onBackPressed();
                break;
            default:
                break;
        }
    }

    private void requestReport(String reportId) {
        showProgDialog();
        Home_WebHit_Post_Get_Report_By_ReportID home_webHit_post_getReportByReportID = new Home_WebHit_Post_Get_Report_By_ReportID();
        home_webHit_post_getReportByReportID.getReport(getContext(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {
                    dismissProgDialog();
                    String upperString = Home_WebHit_Post_Get_Report_By_ReportID.responseObject.getResult().getFarmerName().substring(0, 1).toUpperCase()
                            + Home_WebHit_Post_Get_Report_By_ReportID.responseObject.getResult().getFarmerName().substring(1).toLowerCase();

                    txvName.setText(upperString);
                    String strPhoneNumber = String.valueOf(Home_WebHit_Post_Get_Report_By_ReportID.responseObject.getResult().getMobileNumber());
                    strPhoneNumber = "" + strPhoneNumber.substring(1);
                    strPhoneNumber = "0" + strPhoneNumber.substring(1);

                    txvPhone.setText(strPhoneNumber);

                    txvReportID.setText(Home_WebHit_Post_Get_Report_By_ReportID.responseObject.getResult().getReportId());
                    txv_barcode.setText(Home_WebHit_Post_Get_Report_By_ReportID.responseObject.getResult().getReportId());
                    txvFarmId.setText(Home_WebHit_Post_Get_Report_By_ReportID.responseObject.getResult().getFarmName());
                    txvSpices.setText(Home_WebHit_Post_Get_Report_By_ReportID.responseObject.getResult().getSpeciesName());
                    txvDisease.setText(Home_WebHit_Post_Get_Report_By_ReportID.responseObject.getResult().getDiseaseName());


                    txvTotalAniamals.setText(Home_WebHit_Post_Get_Report_By_ReportID.responseObject.getResult().getAnimalPopulation());


//                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);

                    try {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        Date date = simpleDateFormat.parse(Home_WebHit_Post_Get_Report_By_ReportID.responseObject.getResult().getCreatedDate());
                        SimpleDateFormat dayMonth = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                        String str_DATE = dayMonth.format(date);


                        Log.d("logApiData", "date1:  " + date);

//                        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
//                        Date  date1 = simpleDateFormat1.parse(Home_WebHit_Post_Get_Report_By_ReportID.responseObject.getResult().getCreatedDate());
//                        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
//                        String str_Time =  time.format(date1);


                        if (Home_WebHit_Post_Get_Report_By_ReportID.responseObject.getResult().getCreatedDate().length() > 3) {
                            String firstFourChars = Home_WebHit_Post_Get_Report_By_ReportID.responseObject.getResult().getCreatedDate().substring(11, 19);


                            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
                            Date date1 = simpleDateFormat1.parse(firstFourChars);
                            SimpleDateFormat time = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
                            String str_Time = time.format(date1);

                            txvDate.setText(str_DATE + " (" + str_Time + ")");
                        }


                    } catch (ParseException e) {
                        Log.d("logApiData", "date1:  " + e.getMessage());
                        e.printStackTrace();
                    }


                    txvLatLong.setText(Home_WebHit_Post_Get_Report_By_ReportID.responseObject.getResult().getLatitude() + ",\n" + Home_WebHit_Post_Get_Report_By_ReportID.responseObject.getResult().getLongitude());

//            for (int i = 0; i < AppConfig.getInstance().lst_SelectedSyb.size(); i++) {
//                txvSymbtoms.append(AppConfig.getInstance().lst_SelectedSyb.get(i).getName() + ",");
//            }
//                    String result = Home_WebHit_Post_Get_Report_By_ReportID.responseObject.getResult().getLocation().trim().
//                            replaceAll("[,]",".\n");
//
//                    txvMozah.setText(result);


                    StringBuilder str = new StringBuilder();

                    // Traversing the ArrayList
                    for (String eachstring : Home_WebHit_Post_Get_Report_By_ReportID.responseObject.getResult().getSymptomsName()) {

                        // Each element in ArrayList is appended
                        // followed by comma
                        str.append("* " + eachstring).append(",\n ");
                    }

                    // StringBuffer to String conversion
                    String commaseparatedlist = str.toString();

                    // By following condition you can remove the last
                    // comma
                    if (commaseparatedlist.length() > 0)
                        commaseparatedlist
                                = commaseparatedlist.substring(
                                0, commaseparatedlist.length() - 1);


                    txvSymbtoms.setText(commaseparatedlist);

                    try {
                        if (AppConfig.getInstance().mUserData.getMozah() != null)
                            txvMozah.setText("M: " + Home_WebHit_Post_Get_Report_By_ReportID.responseObject.getResult().getMouza().trim());
//                        if (AppConfig.getInstance().mUserData.getDestrict() != null)
//                            txvDistrict.setText("D: " +Home_WebHit_Post_Get_Report_By_ReportID.responseObject.getResult().get().trim());
                        if (AppConfig.getInstance().mUserData.getTehsil() != null)
                            txvTehsil.setText("T: " + Home_WebHit_Post_Get_Report_By_ReportID.responseObject.getResult().getTehsil().trim());
                    } catch (Exception e) {

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
        }, reportId);
    }
}

