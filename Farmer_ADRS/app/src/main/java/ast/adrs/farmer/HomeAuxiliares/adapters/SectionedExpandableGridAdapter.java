package ast.adrs.farmer.HomeAuxiliares.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ast.adrs.farmer.IntroAuxiliaries.DModel_Animals;
import ast.adrs.farmer.R;

/**
 * Created by lenovo on 2/23/2016.
 */
public class SectionedExpandableGridAdapter extends RecyclerView.Adapter<SectionedExpandableGridAdapter.ViewHolder> {

    //view type
    private static final int VIEW_TYPE_SECTION = R.layout.layout_section;
    private static final int VIEW_TYPE_ITEM = R.layout.layout_item; //TODO : change this
    //context
    private final Context mContext;
    //listeners
    private final ItemClickListener mItemClickListener;
    private final SectionStateChangeListener mSectionStateChangeListener;
    //data array
    private final ArrayList<Object> mDataArrayList;

    public SectionedExpandableGridAdapter(Context context, ArrayList<Object> dataArrayList,
                                          final GridLayoutManager gridLayoutManager, ItemClickListener itemClickListener,
                                          SectionStateChangeListener sectionStateChangeListener) {
        mContext = context;
        mItemClickListener = itemClickListener;
        mSectionStateChangeListener = sectionStateChangeListener;
        mDataArrayList = dataArrayList;

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                return isSection(position) ? gridLayoutManager.getSpanCount() : 1;
            }
        });
    }

    private boolean isSection(int position) {
        return mDataArrayList.get(position) instanceof Section;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(viewType, parent, false), viewType);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        switch (holder.viewType) {
            case VIEW_TYPE_ITEM:
                final DModel_Animals item = (DModel_Animals) mDataArrayList.get(position);


                String s_name = "";
                switch (Integer.valueOf(item.getId())) {
                    case 1:
                        s_name = "Cattle";
                        break;

                    case 2:
                        s_name = "Buffalo";
                        break;
                    case 3:
                        s_name = "Sheep";
                        break;
                    case 4:
                        s_name = "Goat";
                        break;
                    case 5:
                        s_name = "Camel";
                        break;
                    case 6:
                        s_name = "Horse";
                        break;
                    case 7:
                        s_name = "Donkey";
                        break;
                    case 8:
                        s_name = "Mule";
                        break;
                    case 9:
                        s_name = "Dog";
                        break;
                    case 10:
                        s_name = "Poultry";
                        break;
                }


                holder.itemTextView.setText(s_name);
                holder.itemTextView_population.setText(item.getName() + "");
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.itemClicked(item);
                    }
                });
                break;
            case VIEW_TYPE_SECTION:
                final Section section = (Section) mDataArrayList.get(position);


                holder.sectionTextView.setText(section.getName());
                holder.noOfAnimals.setText(section.getNo_ofAnimal()+"");

                if (!section.isExpanded)
                    holder.sectionToggleButton.setBackgroundDrawable(mContext.getDrawable(R.drawable.ic_arrow_down));
                else
                    holder.sectionToggleButton.setBackgroundDrawable(mContext.getDrawable(R.drawable.ic_arrow_up_24));

                holder.sectionTextView.setOnClickListener(v -> mItemClickListener.itemClicked(section));

                Log.d("Section", " section.isExpanded " + section.isExpanded);

                holder.sectionToggleButton.setChecked(section.isExpanded());
//                holder.sectionToggleButton.setChecked(section.isExpanded);



                holder.sectionToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mSectionStateChangeListener.onSectionStateChanged(section, isChecked);
                    }
                });


//                if (position == 0) {
//                    Log.d("Expandable", "Pos: " + position);
//                    section.setExpanded(true);
//                } else section.setExpanded(false);



                break;

        }


    }

    @Override
    public int getItemCount() {
        return mDataArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isSection(position))
            return VIEW_TYPE_SECTION;
        else return VIEW_TYPE_ITEM;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        //common
        View view;
        int viewType;

        //for section
        TextView sectionTextView,noOfAnimals;
        ToggleButton sectionToggleButton;

        //for item
        TextView itemTextView, itemTextView_population;

        public ViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;
            this.view = view;
            if (viewType == VIEW_TYPE_ITEM) {
                itemTextView = view.findViewById(R.id.text_item);
                itemTextView_population = view.findViewById(R.id.lay_survy_item_txv_item);
            } else {
                sectionTextView = view.findViewById(R.id.text_section);
                noOfAnimals = view.findViewById(R.id.text_section_numb);
                sectionToggleButton = view.findViewById(R.id.toggle_button_section);
            }
        }
    }
}
