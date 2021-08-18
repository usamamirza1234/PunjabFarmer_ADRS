package ast.adrs.farmer.IntroAuxiliaries;

public class DModelUserData {

    private String name;
    private String phone;
    private String Designation;

    public String getEncorededImage() {
        return encorededImage;
    }

    public void setEncorededImage(String encorededImage) {
        this.encorededImage = encorededImage;
    }

    private String encorededImage;
    private String Tehsil;
    private String Mozah;
    private String farmerID;
    private String refreshToken;
    private String Gender;    //TODO Get the vat from api in final version
    private String vat = "5";
    private String residency_city;
    private String DOB;
    private String device_token;
    private String status;
    private String authToken;
    private String selected_city;
    private String whatsapp;
    private String Destrict;
    private String email;
    private String CNIC;
    private String selected_city_id;
    private int id;
    private int districtID, designationID, tehsilID, genderID;
    private int pinCode;
    private boolean isLoggedIn = false;

    public String getFarmerID() {
        return farmerID;
    }

    public boolean isLoggedInTemp() {
        return isLoggedInTemp;
    }

    public void setLoggedInTemp(boolean loggedInTemp) {
        isLoggedInTemp = loggedInTemp;
    }

    private boolean isLoggedInTemp = false;
    private boolean isVA = false;
    private boolean isFarmer = false;
    private int mozahID;
    public String getFarmerIDForVA() {
        return farmerID;
    }

    public void setFarmerID(String farmerID) {
        this.farmerID = farmerID;
    }
    public String getMozah() {
        return Mozah;
    }

    public void setMozah(String mozah) {

        if (mozah.length() > 1) {
            String upperString = mozah.substring(0, 1).toUpperCase() + mozah.substring(1).toLowerCase();
            this.Mozah = upperString;
        }
        Mozah = mozah;
    }

    public boolean isVA() {
        return isVA;
    }

    public void setVA(boolean VA) {
        isVA = VA;
    }

    public boolean isFarmer() {
        return isFarmer;
    }

    public void setFarmer(boolean farmer) {
        isFarmer = farmer;
    }

    public int getMozahID() {
        return mozahID;
    }


    public void setMozahID(int mozahID) {
        this.mozahID = mozahID;
    }

    public int getDistrictID() {
        return districtID;
    }

    public void setDistrictID(int districtID) {
        this.districtID = districtID;
    }

    public int getDesignationID() {
        return designationID;
    }

    public void setDesignationID(int designationID) {
        this.designationID = designationID;
    }

    public int getTehsilID() {
        return tehsilID;
    }

    public void setTehsilID(int tehsilID) {
        this.tehsilID = tehsilID;
    }

    public int getGenderID() {
        return genderID;
    }

    public void setGenderID(int genderID) {
        this.genderID = genderID;
    }

    public int getPinCode() {
        return pinCode;
    }

    public void setPinCode(int pinCode) {
        this.pinCode = pinCode;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }


    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }


    public String getDesignation() {
        return Designation;
    }

    public void setDesignation(String designation) {

        if (designation.length() > 1) {
            String upperString = designation.substring(0, 1).toUpperCase() + designation.substring(1).toLowerCase();
            this.Designation = upperString;
        }
        Designation = designation;
    }

    public String getTehsil() {
        return Tehsil;
    }

    public void setTehsil(String tehsil) {


        if (tehsil.length() > 1) {
            String upperString = tehsil.substring(0, 1).toUpperCase() + tehsil.substring(1).toLowerCase();
            this.Tehsil = upperString;
        }
        Tehsil = tehsil;
    }

    public String getDestrict() {
        return Destrict;
    }

    public void setDestrict(String destrict) {


        if (destrict.length() > 1) {
            String upperString = destrict.substring(0, 1).toUpperCase() + destrict.substring(1).toLowerCase();
            this.Destrict = upperString;
        }
        Destrict = destrict;
    }


    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getCNIC() {
        return CNIC;
    }

    public void setCNIC(String CNIC) {
        this.CNIC = CNIC;
    }


    public String getResidency_city() {
        return residency_city;
    }

    public void setResidency_city(String residency_city) {
        this.residency_city = residency_city;
    }


    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String _name) {

        if (_name.length() > 1) {
            String upperString = _name.substring(0, 1).toUpperCase() + _name.substring(1).toLowerCase();
            this.name = upperString;
        }
        this.name = _name;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoneWithCode() {
        String strPhoneNumber = phone;
        strPhoneNumber = "92" + strPhoneNumber.substring(1);
        return strPhoneNumber;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSelected_city() {
        return this.selected_city;
    }

    public void setSelected_city(String selected_city) {
        this.selected_city = selected_city;
    }

    public String getDOB() {
        return this.DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getDevice_token() {
        return this.device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVat() {
        return vat;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }

    public String getSelected_city_id() {
        return selected_city_id;
    }

    public void setSelected_city_id(String selected_city_id) {
        this.selected_city_id = selected_city_id;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }
}
