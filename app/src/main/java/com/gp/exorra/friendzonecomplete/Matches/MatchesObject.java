package com.gp.exorra.friendzonecomplete.Matches;

public class MatchesObject {
    private String uID;
    private String name;
    private String surName;
    private String profileImageUrl;

    //deze constructor maakt een nieuwe match aan die wordt weergegeven in de activity_matches.xml
    public MatchesObject(String uIDParameter, String name, String surName, String town, String profileImageUrl)
    {
        this.surName = surName;
        this.uID = uIDParameter;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    public String getUserId() {
        return uID;
    }
    public void setuID(String uID) { this.uID = uID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public String getSurName() { return surName; }
    public void setSurName(String surName) { this.surName = surName; }
}
