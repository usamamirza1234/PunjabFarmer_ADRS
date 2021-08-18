package ast.adrs.farmer.IntroAuxiliaries;

public class DModel_District {

    public DModel_District(String _id, String _name) {
        this.name = _name;

        this.id = _id;
    }

    String id;

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

    String name;

}
