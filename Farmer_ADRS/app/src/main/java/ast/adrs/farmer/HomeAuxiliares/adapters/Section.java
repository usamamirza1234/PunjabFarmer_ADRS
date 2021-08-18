package ast.adrs.farmer.HomeAuxiliares.adapters;

/**
 * Created by bpncool on 2/23/2016.
 */
public class Section {

    private  String name;
    private  int no_ofAnimal;

    public boolean isExpanded;

    public int getNo_ofAnimal() {
        return no_ofAnimal;
    }

    public Section(String name, int no_ofAnimal, boolean isExpanded) {
        this.name = name;
        this.no_ofAnimal = no_ofAnimal;
        this.isExpanded = isExpanded;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNo_ofAnimal(int no_ofAnimal) {
        this.no_ofAnimal = no_ofAnimal;
    }

    public Section(String name) {
        this.name = name;
        isExpanded = false;
    }

    public String getName() {
        return name;
    }
}
