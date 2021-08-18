package ast.adrs.farmer.IntroAuxiliaries;

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

public class AnimalPopulationRcvAdapter extends RecyclerView.Adapter<AnimalPopulationRcvAdapter.ViewHolder> {


    private final ArrayList<DModel_Animals> mData;
    private final Context mContext;
    private final IAdapterCallback iAdapterCallback;



    public AnimalPopulationRcvAdapter(Context mContext, ArrayList<DModel_Animals> mData,
                                      IAdapterCallback iAdapterCallback) {
        this.mContext = mContext;
        this.mData = mData;

        this.iAdapterCallback = iAdapterCallback;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lay_item_population, null);


        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


//        String s_name="";
//        switch (Integer.valueOf(mData.get(position).getId())) {
//            case 1:
//                s_name = "Cattle";
//                break;
//
//            case 2:
//                s_name = "Buffalo";
//                break;
//            case 3:
//                s_name = "Sheep";
//                break;
//            case 4:
//                s_name = "Goat";
//                break;
//            case 5:
//                s_name = "Camel";
//                break;
//            case 6:
//                s_name = "Horse";
//                break;
//            case 7:
//                s_name = "Donkey";
//                break;
//            case 8:
//                s_name = "Mule";
//                break;
//            case 9:
//                s_name = "Dog";
//                break;
//            case 10:
//                s_name = "Poultry";
//                break;
//        }
//        holder.txvReallocateID.setText(s_name+" ");
        holder.txvReallocateID.setText(mData.get(position).getId()+" ");
        holder.txvToFund.setText(mData.get(position).getName()+" ");
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
        TextView txvReallocateID, txvToFund;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txvReallocateID = itemView.findViewById(R.id.lay_allocation_txvfrom_id);
            txvToFund = itemView.findViewById(R.id.lay_allocation_txvto_id);


        }
    }

}
