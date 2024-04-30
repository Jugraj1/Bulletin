package com.example.app_2100;

public class Account {
    private String email;
    private String firstNameString;
    private String lastNameString;
    private String password;

    public Account(String email, String firstNameString, String lastNameString, String password){
        this.email = email;
        this.firstNameString = firstNameString;
        this.lastNameString = lastNameString;
        this.password = password;
    }

    public String getEmail(){
        return email;
    }

    public String getFirstName(){
        return firstNameString;
    }

    public String getLastName(){
        return lastNameString;
    }

    public String getPassword(){
        return password;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setFirstName(String firstNameString){
        this.firstNameString = firstNameString;
    }

    public void setLastName(String lastNameString){
        this.lastNameString = lastNameString;
    }

    public void setPassword(String password){
        this.password = password;
    }
}
