package ast.adrs.farmer.HomeAuxiliares;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import ast.adrs.farmer.AppConfig;
import ast.adrs.farmer.IntimateDiseaseAuxiliaries.DiseaseFragment;
import ast.adrs.farmer.R;
import ast.adrs.farmer.Utils.AppConstt;
import ast.adrs.farmer.Utils.IBadgeUpdateListener;


/**
 * Created by Usama Khalid Mirza on 06/05/2021.
 * usamamirza.andpercent@gmail.com
 */

public class IntimateDiseaseFragment extends Fragment implements View.OnClickListener {
    int animalType = 0;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    SpicesTypeRcvAdapter spicesTypeRcvAdapter;
    View view;
    LinearLayout llLarge, llSmall, llPoularty, llCamel, llEquine;
//    RecyclerView rcv_animals;
    private IBadgeUpdateListener mBadgeUpdateListener;
    private Dialog progressDialog;


    public IntimateDiseaseFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_intimate_diease, container, false);

        initData();
        bindViews();
//        requestDisease();
//        populateAnimals();

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


    }

    private void bindViews() {
//        rcv_animals = view.findViewById(R.id.frg_home_rcv_animals);


        llLarge = view.findViewById(R.id.frg_intiamte_diesease_llLarge);
        llSmall = view.findViewById(R.id.frg_intiamte_diesease_llSmall);
        llCamel = view.findViewById(R.id.frg_intiamte_diesease_llCamel);
        llPoularty = view.findViewById(R.id.frg_intiamte_diesease_llPoularty);
        llEquine = view.findViewById(R.id.frg_intiamte_diesease_llEquine);


        llLarge.setOnClickListener(this);
        llSmall.setOnClickListener(this);
        llCamel.setOnClickListener(this);
        llPoularty.setOnClickListener(this);
        llEquine.setOnClickListener(this);
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
            case R.id.frg_intiamte_diesease_llLarge: {
                navtoDiseaseFragment(getString(R.string.large_animal),0);
            }
            break;
            case R.id.frg_intiamte_diesease_llSmall: {
                navtoDiseaseFragment(getString(R.string.small_animals),1);
            }
            break;
            case R.id.frg_intiamte_diesease_llCamel: {
                navtoDiseaseFragment(getString(R.string.camael),2);
            }
            break;
            case R.id.frg_intiamte_diesease_llPoularty: {
                navtoDiseaseFragment(getString(R.string.poularty),3);
            }
            break;
            case R.id.frg_intiamte_diesease_llEquine: {
                navtoDiseaseFragment(getString(R.string.equine),4);
            }
            default:
                break;
        }
    }


    private void populateAnimals() {

//        if (spicesTypeRcvAdapter == null) {
//
//            spicesTypeRcvAdapter = new SpicesTypeRcvAdapter(getActivity(), AppConfig.getInstance().getAnimalList(), (eventId, position) -> {
//                switch (eventId) {
//                    case EVENT_A: {
//                        switch (position) {
//                            case 0:
//                                navtoDiseaseFragment("large");
//                                break;
//
//                            case 1:
//                                navtoDiseaseFragment("small");
//                                break;
//
//                            case 2:
//                                navtoDiseaseFragment("Poularty");
//                                break;
//
//
//                            case 3:
//                                navtoDiseaseFragment("Equine");
//                                break;
//
//                            case 4:
//                                navtoDiseaseFragment("Camel");
//                                break;
//                        }
//                    }
//                    break;
//                }
//
//            });
//
//
//            rcv_animals.setLayoutManager(linearLayoutManager);
//            rcv_animals.setAdapter(spicesTypeRcvAdapter);
//
//
//        } else {
//            spicesTypeRcvAdapter.notifyDataSetChanged();
//        }
    }


    private void navtoDiseaseFragment(String _animalType,int animalType) {
        AppConfig.getInstance().dModel_farmerAnimalIntimateDisease.setAnimalType(_animalType);
        Fragment frg = new DiseaseFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Bundle bundle = new Bundle();

        bundle.putString("key_selectedAnimalType", _animalType);
        bundle.putInt("key_selected_aniamal_numb", animalType);
        ft.add(R.id.act_main_content_frg, frg, AppConstt.FragTag.FN_DiseaseFragment);
        ft.addToBackStack(AppConstt.FragTag.FN_DiseaseFragment);
        frg.setArguments(bundle);
        ft.hide(this);
        ft.commit();
    }

}

