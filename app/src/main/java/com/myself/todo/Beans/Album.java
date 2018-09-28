package com.myself.todo.Beans;

import com.myself.todo.Utils.Utilities;

public class Album {
    private String fotouri;
    private String description;
    private String dia;
    private String status;


    private int id;

    public String getFotouri() {
        return fotouri;
    }

    public void setFotouri(String fotouri) {
        this.fotouri = fotouri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = Utilities.convertDate(dia);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
