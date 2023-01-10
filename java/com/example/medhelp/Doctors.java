package com.example.medhelp;

public class Doctors {
    private String username;
    private String email;
    private String password;
    private String type;
    private String salt;

    public Doctors(){ }

    public Doctors(String username, String email, String password, String type, String salt){
        this.username = username;
        this.email = email;
        this.password = password;
        this.type = type;
        this.salt = salt;
    }

    public String getUsername(){
        return this.username;
    }

    public String getEmail(){
        return this.email;
    }

    public String getPassword(){
        return this.password;
    }

    public String getType(){ return this.type; }

    public String getSalt() {
        return salt;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setEmail(String email){ this.email = email; }

    public void setPassword(String password){
        this.password = password;
    }

    public void setType(String type){ this.type = type; }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
