package ast.adrs.farmer.HomeAuxiliares;


import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import ast.adrs.farmer.AppConfig;
import ast.adrs.farmer.HomeAuxiliares.adapters.ItemClickListener;
import ast.adrs.farmer.HomeAuxiliares.adapters.Section;
import ast.adrs.farmer.HomeAuxiliares.adapters.SectionedExpandableLayoutHelper;
import ast.adrs.farmer.IntimateDiseaseAuxiliaries.AnimalDisesaseDefinationRcvAdapter;
import ast.adrs.farmer.IntroAuxiliaries.AnimalPopulationRcvAdapter;
import ast.adrs.farmer.IntroAuxiliaries.DModel_Animals;
import ast.adrs.farmer.IntroAuxiliaries.DModel_District;
import ast.adrs.farmer.IntroAuxiliaries.WebServices.Intro_WebHit_POST_FarmFarmPop_GetByFarmID;
import ast.adrs.farmer.IntroAuxiliaries.WebServices.Intro_WebHit_POST_FarmFarmPop_GetByFarmerID;
import ast.adrs.farmer.R;
import ast.adrs.farmer.Utils.AppConstt;
import ast.adrs.farmer.Utils.IAdapterCallback;
import ast.adrs.farmer.Utils.IBadgeUpdateListener;
import ast.adrs.farmer.Utils.IWebCallback;


public class MyFarmFragment extends Fragment implements ItemClickListener, View.OnClickListener {
    RecyclerView rcv_populatin, rcv_editFarm;
    AnimalPopulationRcvAdapter animalPopulationRcvAdapter;
    String strSelectedFarmID;
    String strSelectedFarmName;

    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    ArrayList<DModel_District> lstSpices = new ArrayList<>();
    SectionedExpandableLayoutHelper sectionedExpandableLayoutHelper;
    ArrayList<Integer> lstfarmId = new ArrayList<>();
    private ArrayList<DModel_Animals> lstPopulation;
    private ArrayList<DModel_Animals> lstFarms;
    LinearLayout llSelectFarmToEdit;

    RelativeLayout rlselectFarm;
    private IBadgeUpdateListener mBadgeUpdateListener;
    private Dialog progressDialog;

    boolean isExpended = true;

    RelativeLayout rleditProfile;

    public MyFarmFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View frg = inflater.inflate(R.layout.fragment_my_farm, container, false);


        initData();
        bindViews(frg);

        requestFarmWisePop();


        return frg;
    }


    void requestFarmWisePop() {
        sectionedExpandableLayoutHelper = new SectionedExpandableLayoutHelper(getActivity(),
                rcv_populatin, this, 1);


        showProgDialog();
        Intro_WebHit_POST_FarmFarmPop_GetByFarmerID intro_webHit_post_farmFarmPop_getByFarmerID = new Intro_WebHit_POST_FarmFarmPop_GetByFarmerID();
        intro_webHit_post_farmFarmPop_getByFarmerID.getFarmPop(getActivity(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {


                    if (Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject != null &&
                            Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult() != null &&
                            Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult().size() > 0) {


                        lstPopulation.clear();
                        for (int i = 0; i < Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult().size(); i++) {


                            DModel_Animals dModel_animals = new DModel_Animals(
                                    Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult().get(i).getFarmID() + "",
                                    Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult().get(i).getNoOfAnimals() + "");


                            lstPopulation.add(dModel_animals);
                        }


                        for (int i = 0; i < lstPopulation.size(); i++) {
                            lstfarmId.add(Integer.parseInt(lstPopulation.get(i).getId()));
                        }


                        List<Integer> numbers = lstfarmId;
                        Log.d("listing", "lstfarmId numbers" + numbers);


                        Set<Integer> hashSet = new LinkedHashSet(numbers);
                        ArrayList<Integer> removedDuplicates = new ArrayList(hashSet);


                        for (int i = 0; i < removedDuplicates.size(); i++) {
                            requestFarmsbyFarmID(String.valueOf(removedDuplicates.get(i)));

                        }

//                        for (int i = 0; i < lstPopulation.size(); i++)
//                        {
//                            Log.d("listing", "lstfarmId size" + lstPopulation.size());
//
//                        }


                    }
                } else {
                    dismissProgDialog();

                    AppConfig.getInstance().showErrorMessage(getActivity(), strMsg);
                }
            }

            @Override
            public void onWebException(Exception e) {
                dismissProgDialog();
                Log.d("LOG_AS", "getFarmFarm_GetByFarmerID Exception: " + e.getMessage());

                AppConfig.getInstance().showErrorMessage(getActivity(), e.toString());
            }
        }, AppConfig.getInstance().mUserData.getId());

    }

    private void requestFarmsbyFarmID(String s) {

//        showProgDialog();
        Intro_WebHit_POST_FarmFarmPop_GetByFarmID intro_webHit_post_farmFarmPop_getByFarmID = new Intro_WebHit_POST_FarmFarmPop_GetByFarmID();
        intro_webHit_post_farmFarmPop_getByFarmID.getFarmPop(getActivity(), new IWebCallback() {
            @Override
            public void onWebResult(boolean isSuccess, String strMsg) {
                if (isSuccess) {
                    dismissProgDialog();


                    if (Intro_WebHit_POST_FarmFarmPop_GetByFarmID.responseObject != null &&
                            Intro_WebHit_POST_FarmFarmPop_GetByFarmID.responseObject.getResult() != null &&
                            Intro_WebHit_POST_FarmFarmPop_GetByFarmID.responseObject.getResult().size() > 0) {
                        int noOfanimals = 0;
                        ArrayList<DModel_Animals> arrayList = new ArrayList<>();
                        for (int i = 0; i < Intro_WebHit_POST_FarmFarmPop_GetByFarmID.responseObject.getResult().size(); i++) {
                            arrayList.add(new DModel_Animals(Intro_WebHit_POST_FarmFarmPop_GetByFarmID.responseObject.getResult().get(i).getSpecieID() + "",
                                    Intro_WebHit_POST_FarmFarmPop_GetByFarmID.responseObject.getResult().get(i).getNoOfAnimals() + ""));

                            noOfanimals = noOfanimals + Intro_WebHit_POST_FarmFarmPop_GetByFarmID.responseObject.getResult().get(i).getNoOfAnimals();
                        }


                        String name = "";
                        String id = "";

                        for (int i = 0; i < AppConfig.getInstance().getFarmList().size(); i++) {
                            for (int j = 0; j < Intro_WebHit_POST_FarmFarmPop_GetByFarmID.responseObject.getResult().size(); j++) {

                                int x = (Intro_WebHit_POST_FarmFarmPop_GetByFarmID.responseObject.getResult().get(j).getFarmID());
                                int y = Integer.parseInt(AppConfig.getInstance().getFarmList().get(i).getId());

                                if (x == y) {
                                    //do something for not equals

                                    Log.d("listing", "getFarmList equals" + AppConfig.getInstance().getFarmList().get(i).getName());
                                    Log.d("listing", "farmID" + AppConfig.getInstance().getFarmList().get(i).getId());
                                    name = AppConfig.getInstance().getFarmList().get(i).getName();
                                    id = AppConfig.getInstance().getFarmList().get(i).getId();


                                }
                            }
                        }

//                        Log.d("Expandable","Pos: " + i);

                        lstFarms.add(new DModel_Animals(id, name));
                        if (isExpended) {
                            sectionedExpandableLayoutHelper.addSection(name, noOfanimals, true, arrayList);
                            isExpended = false;
                        } else
                            sectionedExpandableLayoutHelper.addSection(name, noOfanimals, false, arrayList);
                        sectionedExpandableLayoutHelper.notifyDataSetChanged();
                    }

                } else {
                    dismissProgDialog();

                    AppConfig.getInstance().showErrorMessage(getActivity(), strMsg);
                }
            }

            @Override
            public void onWebException(Exception e) {
                dismissProgDialog();
                Log.d("LOG_AS", "VA Registration Exception: " + e.getMessage());

                AppConfig.getInstance().showErrorMessage(getActivity(), e.toString());
            }
        }, Integer.parseInt(s));


    }

    @Override
    public void itemClicked(DModel_Animals item) {
        Toast.makeText(getActivity(), "Item: " + item.getName() + " clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void itemClicked(Section section) {
        Toast.makeText(getActivity(), "Section: " + section.getName() + " clicked", Toast.LENGTH_SHORT).show();
    }

    private void initData() {
        setBottomBar();
        lstPopulation = new ArrayList<>();
        lstFarms = new ArrayList<>();

    }

    void setBottomBar() {

        try {
            mBadgeUpdateListener = (IBadgeUpdateListener) getActivity();
        } catch (ClassCastException castException) {
            castException.printStackTrace(); // The activity does not implement the listener
        }
        if (getActivity() != null && isAdded()) {
            mBadgeUpdateListener.setToolbarState(AppConstt.INTRO_ToolbarStates.signinFarmer);
            mBadgeUpdateListener.setHeaderTitle("My Farm");
        }

    }

    private void bindViews(View frg) {

        rcv_populatin = frg.findViewById(R.id.frg_complete_profile_rcv_populatin);
        llSelectFarmToEdit = frg.findViewById(R.id.llSelectFarmToEdit);
        rlselectFarm = frg.findViewById(R.id.rlselectFarm);
        rcv_editFarm = frg.findViewById(R.id.rcv_editFarm);
        rleditProfile = frg.findViewById(R.id.rleditProfile);
        rleditProfile.setOnClickListener(this);
        rlselectFarm.setOnClickListener(this);

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


    public void editFarm() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        FarmSelectionRcvAdapter farmSelectionRcvAdapter = new FarmSelectionRcvAdapter(getActivity(), lstFarms, (eventId, position) -> {
            showProgDialog();
            switch (eventId) {
                case IAdapterCallback.EVENT_A: {

                     strSelectedFarmID = lstFarms.get(position).getId();
                     strSelectedFarmName = lstFarms.get(position).getName();
                }
                break;
            }
            if (progressDialog != null)
                progressDialog.dismiss();
        });
        rcv_editFarm.setLayoutManager(linearLayoutManager);
        rcv_editFarm.setAdapter(farmSelectionRcvAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rleditProfile:
                editFarm();
                llSelectFarmToEdit.setVisibility(View.VISIBLE);
                rcv_populatin.setVisibility(View.GONE);

                break;
            case R.id.rlselectFarm:
               navToeditFarmerFarm(strSelectedFarmID,strSelectedFarmName);

                break;
        }
    }

    private void navToeditFarmerFarm(String id,String name) {
        Fragment frg = new FarmUpdateFragment();
        FragmentManager fm = getFragmentManager();
        Bundle bundle = new Bundle();
        FragmentTransaction ft = fm.beginTransaction();
        bundle.putString("key_farm_id",id);
        bundle.putString("key_farm_name",name);
        ft.add(R.id.act_main_content_frg, frg, AppConstt.FragTag.FN_FarmUpdateFragment);
        ft.addToBackStack(AppConstt.FragTag.FN_FarmUpdateFragment);
        frg.setArguments(bundle);
        ft.hide(this);
        ft.commit();
    }

}

