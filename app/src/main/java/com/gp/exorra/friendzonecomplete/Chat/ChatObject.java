package com.gp.exorra.friendzonecomplete.Chat;

//deze klasse is het bericht dat we te zien krijgen op het chatscherm
//het bevat enkel de tekst van het bericht en de boolean die aantoont van wie het bericht kwam
public class ChatObject {
    private String message;
    private boolean currentUserBool;
    public ChatObject(String message, boolean currentUserBool){
        this.message = message;
        this.currentUserBool = currentUserBool;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean getCurrentUserBool() { return currentUserBool; }
    public void setCurrentUserBool(boolean currentUserBool) { this.currentUserBool = currentUserBool; }
}
