package com.example.crudapp.model;

import androidx.annotation.NonNull;

import java.util.Date;

public class User {
    private int id;
    private String name;
    private String email;
    private String birthdate;

    private String salary;


    public User(int id, String name, String email, String birthdate, String salary) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.birthdate = birthdate;
        this.salary = salary;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getBirthdate() {
        return birthdate;
    }
    public String getSalary() {
        return salary;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

     //toString method
    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", birthdate=" + birthdate +
                ", salary='" + salary + '\'' +
                '}';
    }
    //get date as string
    public String getBirthdateAsString() {
        return birthdate.toString();
    }

    //set date as string

}


