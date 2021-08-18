package ast.adrs.farmer.HomeAuxiliares.adapters;



import ast.adrs.farmer.IntroAuxiliaries.DModel_Animals;

/**
 * Created by lenovo on 2/23/2016.
 */
public interface ItemClickListener {
    void itemClicked(DModel_Animals item);
    void itemClicked(Section section);
}
