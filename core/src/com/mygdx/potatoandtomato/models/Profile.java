package com.mygdx.potatoandtomato.models;

import com.shaded.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class Profile {

    String facebookUserId;
    String facebookName;
    String userId;
    String gameName;
    String gcmId;
    UserPlayingState userPlayingState;

    public Profile() {
    }

    @JsonIgnore
    public String getDisplayName(int limit){
        if(limit == 0) limit = 9999;
        String returnName;
        if(gameName == null || gameName.trim().equals("")) returnName = facebookName;
        else returnName = gameName;

        if(returnName == null || returnName.trim().equals("")){
            return "No name";
        }
        if(returnName.length() > limit) {
            returnName = returnName.substring(0, limit);
            returnName+="..";
        }
        return returnName;
    }

    public UserPlayingState getUserPlayingState() {
        if(userPlayingState == null ||  userPlayingState.getRoomId() == null) userPlayingState = new UserPlayingState("0", false, -1);
        return userPlayingState;
    }

    public void setUserPlayingState(UserPlayingState userPlayingState) {
        this.userPlayingState = userPlayingState;
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

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
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
