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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ast.adrs.farmer.AppConfig;
import ast.adrs.farmer.IntimateDiseaseAuxiliaries.WebServices.Home_WebHit_Post_DiseaseByFarmer;
import ast.adrs.farmer.IntimateDiseaseAuxiliaries.WebServices.Home_WebHit_Post_GenerateDiseaseIntimationReport;
import ast.adrs.farmer.MainActivity;
import ast.adrs.farmer.R;
import ast.adrs.farmer.Utils.AppConstt;
import ast.adrs.farmer.Utils.IBadgeUpdateListener;
import ast.adrs.farmer.Utils.IWebCallback;


/**
 * Created by Usama Khalid Mirza on 06/05/2021.
 * usamamirza.andpercent@gmail.com
 */

public class GenerateReportIntemationFragment extends Fragment implements View.OnClickListener {
    RecyclerView rcv_option;
    View view;
    RelativeLayout rlBack;
    RelativeLayout rlHome;
    TextView txvName, txvPhone, txvFarmId, txvSpices, txvDisease, txvReportID, txv_barcode, txvTotalAniamals, txv_address,
            txvTehsil, txvDistrict, txvMozah, txvDate, txvLatLong;
    private IBadgeUpdateListener mBadgeUpdateListener;
    private Dialog progressDialog;


    public GenerateReportIntemationFragment() {
    }


    private void requestGenerateReport(String id) {
        Log.d("LOG_AS", " generateDiseaseIntimationReport  id: " + id);

        Home_WebHit_Post_GenerateDiseaseIntimationReport home_webHit_post_generateDiseaseIntimationReport = new Home_WebHit_Post_GenerateDiseaseIntimationReport();

        home_webHit_post_generateDiseaseIntimationReport.generateDiseaseIntimationReport(getContext(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {

                if (isSuccess) {
                    Log.d("LOG_AS", " generateDiseaseIntimationReport  isSuccess: " + strMsg);

                } else Log.d("LOG_AS", " generateDiseaseIntimationReport  failure: " + strMsg);
            }

            @Override
            public void onWebException(Exception ex) {
                Log.d("LOG_AS", " generateDiseaseIntimationReport  Exception: " + ex.getMessage());
            }
        }, id);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_generate_report, container, false);

        initData();
        bindViews();
        if (AppConfig.getInstance().dModelFarmerAnimalReport != null) {
//            CustomToast.showToastMessage(getActivity(),
//                    AppConfig.getInstance().dModelFarmerAnimalReport.getReportID(), Toast.LENGTH_SHORT);

//            if (Home_WebHit_Post_DiseaseByFarmer.responseObject!=null&&Home_WebHit_Post_DiseaseByFarmer.responseObject.getResult()!=null
//                    &&Home_WebHit_Post_DiseaseByFarmer.responseObject.getResult().getDiseaseID()!=null)
//                requestGenerateReport(Home_WebHit_Post_DiseaseByFarmer.responseObject.getResult().getDiseaseID());
            requestGenerateReport(Home_WebHit_Post_DiseaseByFarmer.responseObject.getResult().getDiseaseIntimationID());
            String upperString = AppConfig.getInstance().dModelFarmerAnimalReport.getName().substring(0, 1).toUpperCase() + AppConfig.getInstance().dModelFarmerAnimalReport.getName().substring(1).toLowerCase();
            txvName.setText(upperString);


            String strPhoneNumber = String.valueOf(AppConfig.getInstance().dModelFarmerAnimalReport.getMobileNumber());
            strPhoneNumber = "" + strPhoneNumber.substring(1);
            strPhoneNumber = "0" + strPhoneNumber.substring(1);

            txvPhone.setText(strPhoneNumber);
            txvReportID.setText(AppConfig.getInstance().dModelFarmerAnimalReport.getReportID());
            txv_barcode.setText(AppConfig.getInstance().dModelFarmerAnimalReport.getReportID());
            txvFarmId.setText(AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.getFarm());
            txvSpices.setText(AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.getAnimalType());
            txvDisease.setText(AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.getDisease());


            txvTotalAniamals.setText("");

            txvTotalAniamals.setText("Total: " + AppConfig.getInstance().dModelFarmerAnimalReport.getTotalAnimals() + ", Sick: "
                    + AppConfig.getInstance().dModelFarmerAnimalReport.getSickAnimals() + ", Dead: "
                    + AppConfig.getInstance().dModelFarmerAnimalReport.getDeadAnimals()
            );
            Date date = new Date();
            String str_DATE = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(date);
            String str_Time = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(date);
            txvDate.setText(str_DATE + " (" + str_Time + ")");
            txvLatLong.setText(AppConfig.getInstance().dModelFarmerAnimalReport.getLatitude() + ",\n" + AppConfig.getInstance().dModelFarmerAnimalReport.getLongitude());

//            for (int i = 0; i < AppConfig.getInstance().lst_SelectedSyb.size(); i++) {
//                txvSymbtoms.append(AppConfig.getInstance().lst_SelectedSyb.get(i).getName() + ",");
//            }


            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            DiseaseSummeryFarmerFragmentRcvAdapter listIssuesRcvAdapter = new DiseaseSummeryFarmerFragmentRcvAdapter(getActivity(), AppConfig.getInstance().lst_SelectedSyb);
            rcv_option.setLayoutManager(linearLayoutManager);
            rcv_option.setAdapter(listIssuesRcvAdapter);


            if (AppConfig.getInstance().mUserData.isFarmer()) {
                txvMozah.setText("M: " + AppConfig.getInstance().mUserData.getMozah().trim());
                txvDistrict.setText("D: " + AppConfig.getInstance().mUserData.getDestrict().trim());
                txvTehsil.setText("T: " + AppConfig.getInstance().mUserData.getTehsil().trim());

            } else {

                try{
                    txvMozah.setText("M: " +  AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.getMozah().trim());
                    txvDistrict.setText("D: " + AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.getDestrict().trim());
                    txvTehsil.setText("T: " + AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.getTehsil().trim());



                    Log.d("Farmer_data",
                            AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.getMozah().trim()+"\n"+
                                    AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.getDestrict().trim()+"\n"+
                                    AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.getTehsil().trim()
                    );
                }
                catch (Exception e)
                {
                    Log.d("Farmer_data",
                            e.getLocalizedMessage()
                    );
                }


            }


        }


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
//        lstPopulation = new ArrayList<>();


    }

    private void bindViews() {
        rcv_option = view.findViewById(R.id.frg_complete_profile_rcv_populatin);
        rlHome = view.findViewById(R.id.frg_signin_rl_login);
        txvName = view.findViewById(R.id.frg_txv_name);
        txvPhone = view.findViewById(R.id.frg_txv_mobile);
        txvDisease = view.findViewById(R.id.frg_txv_dease);
        txvSpices = view.findViewById(R.id.frg_txv_spices);
        txvFarmId = view.findViewById(R.id.frg_txv_farmname);

        txvReportID = view.findViewById(R.id.frg_txv_report);
        txv_barcode = view.findViewById(R.id.frg_txv_txv_barcode);
        txvTotalAniamals = view.findViewById(R.id.frg_txv_total_animal);
        txvTehsil = view.findViewById(R.id.frg_txv_teh);
        txvMozah = view.findViewById(R.id.frg_txv_address);
        txvDistrict = view.findViewById(R.id.frg_txv_district);
        txvDate = view.findViewById(R.id.frg_txv_date);
        txvLatLong = view.findViewById(R.id.txv_lat_long);
        rlHome.setOnClickListener(this::onClick);


        rlBack = view.findViewById(R.id.rlBack);
        rlBack.setOnClickListener(this);

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
//
                ((MainActivity) getActivity()).setFirstFragment();
                break;

            case R.id.rlBack:
                ((MainActivity) getActivity()).setFirstFragment();
                break;
            default:
                break;
        }
    }


}

