package ast.adrs.farmer.IntroAuxiliaries;

import ast.adrs.farmer.Utils.AppConstt;

/**
 * Created by bilalahmed on 04/05/2018.
 * bilalahmed.cs@live.com
 */

public class DModelUser {

    public int User_Id;
    public String Name;
    public String Email;
    public String Phone;
    public String Image;
    public int Type;
    public boolean isPushOn;
    public boolean isLoggedIn;
    public String Authorization;

    public DModelUser() {
        this.User_Id = 0;
        this.Name = "";
        this.Email = "";
        this.Phone = "";
        this.Image = "";
        this.Type = AppConstt.UserType.DRIVER;
        this.isPushOn = true;
        this.isLoggedIn = false;
        this.Authorization = "";
    }
}