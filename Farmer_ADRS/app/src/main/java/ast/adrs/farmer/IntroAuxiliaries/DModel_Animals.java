package ast.adrs.farmer.IntroAuxiliaries;

public class DModel_Animals {

    public boolean isExpanded;
    String id;
    String name;
    String no_animals;

    public DModel_Animals(String _id, String _name) {
        this.name = _name;
        this.id = _id;
        isExpanded = false;
    }

    public DModel_Animals(String farm_id, String name, String no_animals) {
        this.id = farm_id;
        this.name = name;
        this.no_animals = no_animals;
        isExpanded = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNo_animals() {
        return no_animals;
    }

    public void setNo_animals(String no_animals) {
        this.no_animals = no_animals;
    }

}
