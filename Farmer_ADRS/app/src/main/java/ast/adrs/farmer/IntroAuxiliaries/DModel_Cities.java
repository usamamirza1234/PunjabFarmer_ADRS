package ast.adrs.farmer.IntroAuxiliaries;

public class DModel_Cities {

    public DModel_Cities(String _id, String _name,String _name_ar) {
        this.name = _name;
        this.name_ar = _name_ar;
        this.id = _id;
    }

    String id;
    String name;

    public String getName_ar() {
        return name_ar;
    }

    public void setName_ar(String name_ar) {
        this.name_ar = name_ar;
    }

    String name_ar;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
