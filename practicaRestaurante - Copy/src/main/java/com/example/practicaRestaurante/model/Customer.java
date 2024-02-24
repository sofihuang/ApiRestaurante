package com.example.practicaRestaurante.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class Customer {

    @NonNull
    private String name;
    @NonNull
    private String surname;
    @NonNull
    private String tel;

    public Customer(String name, String surname, String tel) {
        this.name = name;
        this.surname = surname;
        this.tel = tel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
}
