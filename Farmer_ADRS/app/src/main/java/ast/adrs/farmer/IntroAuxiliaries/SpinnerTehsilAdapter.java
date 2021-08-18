package ast.adrs.farmer.IntroAuxiliaries;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import ast.adrs.farmer.AppConfig;
import ast.adrs.farmer.R;
import ast.adrs.farmer.Utils.AppConstt;
import ast.adrs.farmer.Utils.IAdapterCallback;

public class SpinnerTehsilAdapter extends BaseAdapter {
    Context context;

    List<DModel_District> listData;
    LayoutInflater inflter;
    IAdapterCallback iAdapterCallback;

    public SpinnerTehsilAdapter(Context context, List<DModel_District> listData) {
        this.context = context;
        this.listData = listData;

        inflter = (LayoutInflater.from(context));
    }


    @Override
    public int getCount() {
        int count = listData.size();
        return count > 0 ? count - 1 : count;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflter.inflate(R.layout.adapter_spinner_tehsil_item_list, null);
            viewHolder = new ViewHolder();
            viewHolder.txtName = convertView.findViewById(R.id.adptr_spnnr_itm_txt_item);
            viewHolder.txtName.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.spinner_item_bg));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

//        switch (Intro_WebHit_POST_FarmFarmPop_GetByFarmerID.responseObject.getResult().get(i).getSpecieID()) {
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
//        viewHolder.txtName.setText(listData.get(position).getName());

        if (!listData.get(position).getName().equalsIgnoreCase(context.getString(R.string.select_tehsil)))
            viewHolder.txtName.setText(listData.get(position).getName());
        else
        {
            if (AppConfig.getInstance().mLanguage.equalsIgnoreCase(AppConstt.AppLang.LANG_EN))
            {
                viewHolder.txtName.setText("Select Tehsil");
            }
            else   viewHolder.txtName.setText("تحصیل کو منتخب کریں");
        }



        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflter.inflate(R.layout.adapter_spinner_item_list, null);

            viewHolder = new ViewHolder();
            viewHolder.txtName = convertView.findViewById(R.id.adptr_spnnr_itm_txt_item);
            viewHolder.rlSpinner = convertView.findViewById(R.id.adptr_spnnr_itm_rlSpinner);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtName.setText(listData.get(position).getName());

        return convertView;
    }

    public static class ViewHolder {
        TextView txtName;
        RelativeLayout rlSpinner;
    }
}
