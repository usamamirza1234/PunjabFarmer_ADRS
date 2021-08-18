package ast.adrs.farmer.HomeAuxiliares;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ast.adrs.farmer.IntroAuxiliaries.DModel_Animals;
import ast.adrs.farmer.R;
import ast.adrs.farmer.Utils.CircleImageView;
import ast.adrs.farmer.Utils.IAdapterCallback;

public class SpicesTypeRcvAdapter extends RecyclerView.Adapter<SpicesTypeRcvAdapter.ViewHolder> {


    private final ArrayList<DModel_Animals> mData;
    private final Context mContext;
    private final IAdapterCallback iAdapterCallback;



    public SpicesTypeRcvAdapter(Context mContext, ArrayList<DModel_Animals> mData,
                                IAdapterCallback iAdapterCallback) {
        this.mContext = mContext;
        this.mData = mData;

        this.iAdapterCallback = iAdapterCallback;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lay_item_spices, null);


        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.txv_large.setText(mData.get(position).getName());


        Log.d("AnimalList", mData.get(position).getId() + " : " + mData.get(position).getName());


        switch (mData.get(position).getId())
        {
            case "1":
                holder.civ_large.setBackground(mContext.getResources().getDrawable(  R.drawable.ic_large));
            break;
            case "2":
                holder.civ_large.setBackground(mContext.getResources().getDrawable(  R.drawable.ic_small));
                break;
            case "3":
                holder.civ_large.setBackground(mContext.getResources().getDrawable(  R.drawable.ic_poultry));
                break;
            case "4":
                holder.civ_large.setBackground(mContext.getResources().getDrawable(  R.drawable.ic_equine));
                break;
            case "5":
                holder.civ_large.setBackground(mContext.getResources().getDrawable(  R.drawable.ic_camel));
                break;

            default:

                break;

        }


//        if (position==0)
//        {
//            holder.civ_large.setBackground(mContext.getResources().getDrawable(  R.drawable.ic_large));
//        }
//
//        if (position==4)
//        {
//            holder.civ_large.setBackground(mContext.getResources().getDrawable(  R.drawable.ic_camel));
//        }

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
        TextView txv_large;
        CircleImageView civ_large;
//        LinearLayout llLarge;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            llLarge = itemView.findViewById(R.id.frg_intiamte_diesease_llLarge);
            txv_large = itemView.findViewById(R.id.txv_large);
            civ_large = itemView.findViewById(R.id.civ_large);


        }
    }

}
