package com.zachariasz.springapp.conversion.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Exchange {
    @Id
    private String id;
    private Double value;

    public Exchange(){

    }
    public Exchange(String id, Double value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
