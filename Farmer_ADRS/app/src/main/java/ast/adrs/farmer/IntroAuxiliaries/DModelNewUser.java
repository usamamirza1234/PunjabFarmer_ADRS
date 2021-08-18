package ast.adrs.farmer.IntroAuxiliaries;

/**
 * Created by bilalahmed on 04/05/2018.
 * bilalahmed.cs@live.com
 */

public class DModelNewUser {

    public String Name;
    public String Email;
    public String Phone;
    public String Password;
    public String VerfctnCode;

    public DModelNewUser() {
        this.Name = "";
        this.Email = "";
        this.Phone = "";
        this.Password = "";
        this.VerfctnCode = "";
    }

    public DModelNewUser(String name, String email, String phone, String password, String verfctnCode) {
        Name = name;
        Email = email;
        Phone = phone;
        Password = password;
        VerfctnCode = verfctnCode;
    }
}