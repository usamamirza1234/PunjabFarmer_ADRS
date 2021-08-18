package ast.adrs.farmer.HomeAuxiliares;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ast.adrs.farmer.IntroAuxiliaries.DModel_Performance;
import ast.adrs.farmer.R;

public class PerformanceRcvAdapter extends RecyclerView.Adapter<PerformanceRcvAdapter.ViewHolder> {


    private final ArrayList<DModel_Performance> mData;
    private final Context mContext;


    public PerformanceRcvAdapter(Context mContext, ArrayList<DModel_Performance> mData) {
        this.mContext = mContext;
        this.mData = mData;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lay_item_performance, null);


        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


        holder.txvReallocateID.setText((position + 1) + " ");
        holder.txvToFund.setText(mData.get(position).getName() + " ");
        holder.txvReport.setText(mData.get(position).getCount() + " ");
        Log.d("findData", "position: " + position);
        Log.d("findData", "mData.size(): " + mData.size());
//        if ((position+1)==mData.size()) {
////            holder.llMain.setVisibility(View.VISIBLE);
////            holder.llTotal.setVisibility(View.GONE);
////            holder.txvReport.setText( " ");
////            holder.txvReallocateID.setText( " ");
////            holder.txvToFund.setText( " ");
////            holder.txvTotalSum.setText( mData.get(position).getTotal() + " ");
//        }
//        else {
////            holder.llMain.setVisibility(View.GONE);
////            holder.llTotal.setVisibility(View.VISIBLE);
//            holder.txvReallocateID.setText((position + 1) + " ");
//            holder.txvToFund.setText(mData.get(position).getName() + " ");
//            holder.txvReport.setText(mData.get(position).getCount() + " ");
//        }
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txvReallocateID, txvToFund, txvReport,txvTotalSum;
        LinearLayout llMain,llTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txvReallocateID = itemView.findViewById(R.id.lay_allocation_txvfrom_id);
            txvToFund = itemView.findViewById(R.id.lay_allocation_txvto_id);
            txvReport = itemView.findViewById(R.id.lay_allocation_txvto_report);
//            txvTotalSum = itemView.findViewById(R.id.lay_item_performance_txv_total);
//            llMain = itemView.findViewById(R.id.llDefault);
//            llTotal = itemView.findViewById(R.id.llTotal);


        }
    }

}
