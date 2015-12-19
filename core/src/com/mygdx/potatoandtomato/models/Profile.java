package com.mygdx.potatoandtomato.models;

import com.mygdx.potatoandtomato.enums.MascotEnum;
import com.shaded.fasterxml.jackson.annotation.JsonIgnore;
import com.shaded.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class Profile {

    String facebookUserId;
    String facebookName;
    String userId;
    String gameName;
    MascotEnum mascotEnum;

    public Profile() {
    }

    @JsonIgnore
    public String getDisplayName(){
        String returnName;
        if(gameName == null) returnName = facebookName;
        else returnName = gameName;

        if(returnName == null) returnName = "Anonymous";
        return returnName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String _userId) {
        this.userId = _userId;
    }

    public String getFacebookUserId() {
        return facebookUserId;
    }

    public void setFacebookUserId(String facebookUserId) {
        this.facebookUserId = facebookUserId;
    }

    public MascotEnum getMascotEnum(){
        return mascotEnum;
    }

    public void setMascotEnum(MascotEnum mascotEnum){
        this.mascotEnum = mascotEnum;
    }

    public String getFacebookName() {
        return facebookName;
    }

    public void setFacebookName(String facebookName) {
        this.facebookName = facebookName;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Profile){
            Profile p = (Profile) o;
            if(p.getUserId() == null && this.getUserId() == null){
                return true;
            }
            else if((p.getUserId() != null && this.getUserId() == null) || (p.getUserId() == null && this.getUserId() != null)){
                return false;
            }
            else{
                return ((Profile) o).getUserId().equals(this.getUserId());
            }


        }
        return super.equals(o);
    }
}