package com.roadrunner.android.roadrunner;


public class Notifications {

    private String username;
    private String profimage;

    public Notifications(){

    }

    public Notifications(String username, String profimage){

        this.username = username;
        this.profimage = profimage;
    }

    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getProfimage(){
        return profimage;
    }
    public void setProfimage(String profimage){
        this.profimage = profimage;
    }

}
