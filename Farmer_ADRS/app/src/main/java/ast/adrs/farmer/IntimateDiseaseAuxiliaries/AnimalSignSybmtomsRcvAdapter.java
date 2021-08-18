package ast.adrs.farmer.IntimateDiseaseAuxiliaries;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ast.adrs.farmer.HomeAuxiliares.WebServices.Home_WebHit_Post_Get_DiseaseDefinationDetails;
import ast.adrs.farmer.R;

public class AnimalSignSybmtomsRcvAdapter extends RecyclerView.Adapter<AnimalSignSybmtomsRcvAdapter.ViewHolder> {

    private final Context context;
    private final List<Home_WebHit_Post_Get_DiseaseDefinationDetails.ResponseModel.Symptoms>  listData;



    public AnimalSignSybmtomsRcvAdapter(Context context, List<Home_WebHit_Post_Get_DiseaseDefinationDetails.ResponseModel.Symptoms>  listData) {
        this.context = context;
        this.listData = listData;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lay_sign_symbtoms, null);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


        if (listData != null) {
            holder.cb_selected.setOnCheckedChangeListener(null);


            holder.txv_Option.setText(listData.get(position).getName());


            holder.cb_selected.setChecked(listData.get(position).isSelected());
            holder.cb_selected.setTag(listData.get(position));


            holder.cb_selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        listData.get(position).setSelected(true);

                    } else {
                        listData.get(position).setSelected(false);

                    }
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cb_selected;
        TextView txv_Option;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cb_selected = itemView.findViewById(R.id.lay_survy_item_imv_option);
            txv_Option = itemView.findViewById(R.id.lay_survy_item_txv_option);
        }
    }
}
