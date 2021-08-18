package ast.adrs.farmer.HomeAuxiliares;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ast.adrs.farmer.R;
import ast.adrs.farmer.Utils.IAdapterCallback;

public class SuggestionListingRcvAdapter extends RecyclerView.Adapter<SuggestionListingRcvAdapter.ViewHolder> {


    private final ArrayList<DModel_Message> mData;
    private final Context mContext;
    private final IAdapterCallback iAdapterCallback;



    public SuggestionListingRcvAdapter(Context mContext, ArrayList<DModel_Message> mData,
                                       IAdapterCallback iAdapterCallback) {
        this.mContext = mContext;
        this.mData = mData;

        this.iAdapterCallback = iAdapterCallback;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lay_item_suggestion, null);


        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.txv_date.setText(mData.get(position).getDate());

        holder.txv_desc.setText(mData.get(position).getDesc());


        holder.itemView.setOnClickListener(v -> iAdapterCallback.onAdapterEventFired(IAdapterCallback.EVENT_A,position));
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
        TextView txv_date, txv_desc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txv_date = itemView.findViewById(R.id.lay_item_messages_txv_date);

            txv_desc = itemView.findViewById(R.id.lay_item_messages_txv_desc);


        }
    }

}
