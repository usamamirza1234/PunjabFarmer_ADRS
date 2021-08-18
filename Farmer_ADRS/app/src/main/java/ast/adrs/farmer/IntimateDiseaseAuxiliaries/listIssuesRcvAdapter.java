package ast.adrs.farmer.IntimateDiseaseAuxiliaries;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ast.adrs.farmer.HomeAuxiliares.WebServices.Home_WebHit_Post_Get_DiseaseDefinationDetails;
import ast.adrs.farmer.R;

public class listIssuesRcvAdapter extends RecyclerView.Adapter<listIssuesRcvAdapter.ViewHolder> {

    private final Context context;
    private final List<Home_WebHit_Post_Get_DiseaseDefinationDetails.ResponseModel.Symptoms>  listData;



    public listIssuesRcvAdapter(Context context, List<Home_WebHit_Post_Get_DiseaseDefinationDetails.ResponseModel.Symptoms>  listData) {
        this.context = context;
        this.listData = listData;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lay_issues_animal, null);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


        if (listData != null) {
            holder.txv_Option.setText(listData.get(position).getName());
            holder.txv_selected.setText("" +(position+1));


        }


    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txv_selected;
        TextView txv_Option;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txv_selected = itemView.findViewById(R.id.lay_survy_item_txv_item);
            txv_Option = itemView.findViewById(R.id.lay_survy_item_txv_option);
        }
    }
}
