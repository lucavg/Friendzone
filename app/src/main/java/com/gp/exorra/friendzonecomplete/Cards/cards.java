package com.gp.exorra.friendzonecomplete.Cards;

public class cards {
    private String uID, name, surName, profileImageUrl, age, town;

    public cards(String uIDParameter, String nameParameter, String surNameParameter, String profileImageUrl, String age, String town) {
        this.uID = uIDParameter;
        this.name = nameParameter;
        this.surName = surNameParameter;
        this.profileImageUrl = profileImageUrl;
        this.age = age;
        this.town = town;
    }

    public String getuID() {
        return uID;
    }
    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getSurName() { return surName; }
    public void setSurName(String surName) { this.surName = surName; }

    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    public String getProfileImageUrl() { return profileImageUrl; }

    public void setAge(String age) { this.age = age; }
    public String getAge() { return age; }

    public void setTown(String town) { this.town = town; }
    public String getTown() { return town; }
}