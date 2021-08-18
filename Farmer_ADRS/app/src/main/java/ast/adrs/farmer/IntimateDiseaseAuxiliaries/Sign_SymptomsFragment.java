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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ast.adrs.farmer.AppConfig;
import ast.adrs.farmer.HomeAuxiliares.WebServices.Home_WebHit_Post_Get_DiseaseDefinationDetails;
import ast.adrs.farmer.MainActivity;
import ast.adrs.farmer.R;
import ast.adrs.farmer.Utils.AppConstt;
import ast.adrs.farmer.Utils.CircleImageView;
import ast.adrs.farmer.Utils.IBadgeUpdateListener;

public class Sign_SymptomsFragment extends Fragment implements View.OnClickListener {
    RelativeLayout rlBack;

    public List<Home_WebHit_Post_Get_DiseaseDefinationDetails.ResponseModel.Symptoms> lst_Symptoms;
    View view;
    IBadgeUpdateListener mBadgeUpdateListener;
    RelativeLayout rlNext;
    RecyclerView rcv_option;
    AnimalSignSybmtomsRcvAdapter animalSignSybmtomsRcvAdapter;
    String selectedAnimalType;
    String selectedDieseaseType;
    int selectedSybtomsIndex = 0;
    int selectedAnimalIndex = 0;
    Bundle bundle;
    CircleImageView civ_selctedAnimal;

    TextView txvSelected, txvNext;
    TextView txvSelected_Disease;

    private Dialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_sign_symptoms, container, false);


        init();
        bindViews();
        txvSelected.setText(selectedAnimalType);
        txvSelected_Disease.setVisibility(View.VISIBLE);
        txvSelected_Disease.setText(selectedDieseaseType);

        try {

            switch (selectedAnimalIndex) {
                case 0:
                    civ_selctedAnimal.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_large));
                    break;
                case 1:
                    civ_selctedAnimal.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_small));
                    break;
                case 2:
                    civ_selctedAnimal.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_camel));
                    break;
                case 3:
                    civ_selctedAnimal.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_equine));
                    break;
                case 4:
                    civ_selctedAnimal.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_poultry));
                    break;
            }


        } catch (Exception e) {

        }


        for (int i = 0; i < AppConfig.getInstance().lst_DiseasesDef.get(selectedSybtomsIndex).getSymptoms().size(); i++)
        {


            if (AppConfig.getInstance().lst_DiseasesDef.get(selectedSybtomsIndex).getSymptoms().get(i).getType()==1)
            {
                Log.d("Animaltype", "selectedAnimalIndex getSymptoms:  " + AppConfig.getInstance().lst_DiseasesDef.get(selectedSybtomsIndex).getSymptoms().get(i).getName());
                lst_Symptoms.add(AppConfig.getInstance().lst_DiseasesDef.get(selectedSybtomsIndex).getSymptoms().get(i));

            }

        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        animalSignSybmtomsRcvAdapter = new AnimalSignSybmtomsRcvAdapter(getActivity(), lst_Symptoms);
        rcv_option.setLayoutManager(linearLayoutManager);
        rcv_option.setAdapter(animalSignSybmtomsRcvAdapter);


        return view;
    }



    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!isHidden()) {
            if (getActivity() != null) {
                try {
                    setBottomBar();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frg_risk_app_servy_rlNext: {




//                if (selection != null)
                {
                    AppConfig.getInstance().lst_SelectedSyb.clear();

                    for (int i = 0; i < lst_Symptoms.size(); i++) {

                        if (lst_Symptoms.get(i).isSelected()) {
                            AppConfig.getInstance().lst_SelectedSyb.add(lst_Symptoms.get(i));
                            Log.d("Animaltype", "SelectedSym Siz:  " +   AppConfig.getInstance().lst_SelectedSyb.size());
                        }

                    }


                    navToSumerizaeDiseaseFragment();
                }
//                else AppConfig.getInstance().showErrorMessage(getContext(),"Please select any symptoms");


            }
            break;

            case R.id.rlBack:
                ((MainActivity)getActivity()).onBackPressed();
                break;
        }
    }


    private void navToSumerizaeDiseaseFragment() {
        Fragment frg = new IntimateDiseaseSumeraizeFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();


        ft.add(R.id.act_main_content_frg, frg, AppConstt.FragTag.FN_IntimateDiseaseSumeraizeFragment);
        ft.addToBackStack(AppConstt.FragTag.FN_IntimateDiseaseSumeraizeFragment);

        ft.hide(this);
        ft.commit();
    }


    //region init
    private void bindViews() {

        rlNext = view.findViewById(R.id.frg_risk_app_servy_rlNext);
        rcv_option = view.findViewById(R.id.frg_risk_app_servy_rcv);

        txvNext = view.findViewById(R.id.frg_risk_app_servy_txv_next);
        txvSelected = view.findViewById(R.id.frg_disease_txv_selected);
        txvSelected_Disease = view.findViewById(R.id.frg_disease_txv_selected_disease);
        civ_selctedAnimal = view.findViewById(R.id.frg_disease_img_selected_animal);

        rlBack = view.findViewById(R.id.rlBack);
        rlBack.setOnClickListener(this);

        rlNext.setOnClickListener(this);


    }


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


    private void init() {

        bundle = this.getArguments();

        if (bundle != null) {
            selectedAnimalType = bundle.getString("key_selectedAnimalType", "");
            selectedDieseaseType = bundle.getString("key_selectedDieseaseType", "");
            selectedSybtomsIndex = bundle.getInt("key_selected_symptoms", 0);
            selectedAnimalIndex = bundle.getInt("key_selected_aniamal_numb", 0);
        }
        Log.d("Sign_SymptomsFragment", "selectedAnimalType: " + selectedAnimalType + "\n selectedAnimalIndex: " + selectedAnimalIndex + "\n selectedSybtomsIndex: " + selectedSybtomsIndex  );
        AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.setAnimalIndex(selectedAnimalIndex+"");


        setBottomBar();


        lst_Symptoms = new ArrayList<>();



    }
    //endregion

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

}