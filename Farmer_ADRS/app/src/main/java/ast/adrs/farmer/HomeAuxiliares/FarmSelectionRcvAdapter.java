package ast.adrs.farmer.HomeAuxiliares;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import ast.adrs.farmer.IntroAuxiliaries.DModel_Animals;
import ast.adrs.farmer.R;
import ast.adrs.farmer.Utils.IAdapterCallback;

public class FarmSelectionRcvAdapter extends RecyclerView.Adapter<FarmSelectionRcvAdapter.ViewHolder> {
    private final Context context;
    private final List<DModel_Animals> listData;
    private Integer selectedPosition = null;
    private IAdapterCallback iAdapterCallback;


    public FarmSelectionRcvAdapter(Context context, List<DModel_Animals> listData, IAdapterCallback iAdapterCallback) {
        this.context = context;
        this.listData = listData;
        this.iAdapterCallback = iAdapterCallback;
    }


    @NonNull
    @Override
    public FarmSelectionRcvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lay_item_farm_selection, null);

        return new FarmSelectionRcvAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final FarmSelectionRcvAdapter.ViewHolder holder, final int position) {



        holder.txv_Option.setText(listData.get(position).getName());

        holder.itemView.setOnClickListener(v -> {

            if (selectedPosition != null) {

                notifyItemChanged(selectedPosition);
            }
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged();
            iAdapterCallback.onAdapterEventFired(IAdapterCallback.EVENT_A, position);
        });

        holder.styleViewSection((selectedPosition != null && selectedPosition == holder.getAdapterPosition()),position);


    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {


        ImageView imv_Option;
        TextView txv_Option;
        LinearLayout llParent;
        public ViewHolder(@NonNull View itemView) {


            super(itemView);


            llParent= itemView.findViewById(R.id.lay_survy_item_ll_option);
            imv_Option = itemView.findViewById(R.id.lay_survy_item_imv_option);
            txv_Option = itemView.findViewById(R.id.lay_survy_item_txv_option);
        }

        public void styleViewSection(boolean b,int Position) {
            if (!b)
            {
                llParent.setBackgroundColor(context.getResources().getColor(R.color.white));
                txv_Option.setTextColor(context.getResources().getColor(R.color.green_govt));
                imv_Option.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_unsld));
            } else {
                llParent.setBackgroundColor(context.getResources().getColor(R.color.green_govt));
                txv_Option.setTextColor(context.getResources().getColor(R.color.white));
                imv_Option.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_sltd));
            }

        }
    }


}
