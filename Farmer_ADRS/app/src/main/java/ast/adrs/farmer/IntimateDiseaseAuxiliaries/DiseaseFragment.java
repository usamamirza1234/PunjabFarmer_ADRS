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

import ast.adrs.farmer.AppConfig;
import ast.adrs.farmer.HomeAuxiliares.WebServices.Home_WebHit_Post_Get_DiseaseDefinationDetails;
import ast.adrs.farmer.R;
import ast.adrs.farmer.Utils.AppConstt;
import ast.adrs.farmer.Utils.CircleImageView;
import ast.adrs.farmer.Utils.IAdapterCallback;
import ast.adrs.farmer.Utils.IBadgeUpdateListener;

public class DiseaseFragment extends Fragment implements View.OnClickListener {


    View view;
    IBadgeUpdateListener mBadgeUpdateListener;
    RelativeLayout rlNext;
    RecyclerView rcv_option;
    RelativeLayout rlBack;
    AnimalDisesaseDefinationRcvAdapter animalDisesaseDefinationRcvAdapter;
    String selectedAnimalType;
    Bundle bundle;
    CircleImageView civ_selctedAnimal;

    TextView txvSelected, txvNext;
    TextView txvSelected_Disease;
    ArrayList<Home_WebHit_Post_Get_DiseaseDefinationDetails.ResponseModel.Diseases> lstDisease;

    int animalType = 0;
    int position_ = 0;
    Integer selection = null;
    private Dialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_disease, container, false);


        init();
        bindViews();
//        String upperString = selectedAnimalType.substring(0, 1).toUpperCase() + selectedAnimalType.substring(1).toLowerCase();
        txvSelected.setText(selectedAnimalType);

        try {
            if (animalType==0) {
                civ_selctedAnimal.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_large));

            } else if (animalType==1) {
                civ_selctedAnimal.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_small));
                animalType = 1;
            } else if (animalType==2) {
                civ_selctedAnimal.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_camel));
                animalType = 2;
            } else if (animalType==3) {
                civ_selctedAnimal.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_equine));
                animalType = 3;
            } else if (animalType==4) {
                civ_selctedAnimal.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_poultry));
                animalType = 4;

            }


            AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.setSpecieID((animalType + 1) + "");

            AppConfig.getInstance().lst_DiseasesDef.clear();

            for (int i = 0; i < AppConfig.getInstance().getDiseasesList().get(animalType).getResult().get(animalType).getDiseases().size(); i++) {
                AppConfig.getInstance().lst_DiseasesDef.add(AppConfig.getInstance().getDiseasesList().get(animalType).getResult().get(animalType).getDiseases().get(i));
            }

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            animalDisesaseDefinationRcvAdapter = new AnimalDisesaseDefinationRcvAdapter(getActivity(), AppConfig.getInstance().lst_DiseasesDef, (eventId, position) -> {
                showProgDialog();
                switch (eventId) {
                    case IAdapterCallback.EVENT_A: {
                        position_ = position;
                        selection = position;
                        txvSelected_Disease.setVisibility(View.VISIBLE);
//                        txvSelected_Disease.setText();

                        String upperStringDisease = AppConfig.getInstance().lst_DiseasesDef.get(position).getDiseaseName().substring(0, 1).toUpperCase() + AppConfig.getInstance().lst_DiseasesDef.get(position).getDiseaseName().substring(1).toLowerCase();
                        txvSelected_Disease.setText(AppConfig.getInstance().lst_DiseasesDef.get(position).getDiseaseName());


                        AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.setDiseaseID(AppConfig.getInstance().lst_DiseasesDef.get(position).getDiseaseId() + "");
                    }
                    break;
                }
                if (progressDialog != null)
                    progressDialog.dismiss();
            });
            rcv_option.setLayoutManager(linearLayoutManager);
            rcv_option.setAdapter(animalDisesaseDefinationRcvAdapter);
        } catch (Exception e) {

        }


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

                if (selection != null)
                    navtoSignSybmtomsFragment(txvSelected.getText().toString(), position_);
                else
                    AppConfig.getInstance().showErrorMessage(getContext(), getResources().getString(R.string.select_any_disease));
            }
            break;
            case R.id.rlBack:
                getActivity().onBackPressed();
                break;
        }
    }

    private void navtoSignSybmtomsFragment(String _animalType, int selectedPosition) {
        AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.setDisease(txvSelected_Disease.getText().toString());
        Fragment frg = new Sign_SymptomsFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("key_selectedAnimalType", _animalType);
        bundle.putString("key_selectedDieseaseType", txvSelected_Disease.getText().toString());
        bundle.putInt("key_selected_symptoms", selectedPosition);
        bundle.putInt("key_selected_aniamal_numb", animalType);
        ft.add(R.id.act_main_content_frg, frg, AppConstt.FragTag.FN_Sign_SymptomsFragment);
        ft.addToBackStack(AppConstt.FragTag.FN_Sign_SymptomsFragment);
        frg.setArguments(bundle);
        ft.hide(this);
        ft.commit();
    }

    //region init
    private void bindViews() {

        rlNext = view.findViewById(R.id.frg_risk_app_servy_rlNext);
        rcv_option = view.findViewById(R.id.frg_risk_app_servy_rcv);
        rlBack = view.findViewById(R.id.rlBack);
        rlBack.setOnClickListener(this);
        txvNext = view.findViewById(R.id.frg_risk_app_servy_txv_next);
        txvSelected = view.findViewById(R.id.frg_disease_txv_selected);
        txvSelected_Disease = view.findViewById(R.id.frg_disease_txv_selected_disease);
        civ_selctedAnimal = view.findViewById(R.id.frg_disease_img_selected_animal);


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
            animalType = bundle.getInt("key_selected_aniamal_numb",0);
        }
        Log.d("Animaltype", "selectedAnimalType " + selectedAnimalType);
        setBottomBar();
        lstDisease = new ArrayList<>();

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