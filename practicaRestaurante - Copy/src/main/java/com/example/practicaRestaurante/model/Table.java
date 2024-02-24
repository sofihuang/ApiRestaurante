package com.example.practicaRestaurante.model;

import lombok.Data;
import lombok.NonNull;


@Data
public class Table {

    @NonNull
    private int tableID;
    @NonNull
    private int capacity;


    public int getTableID() {
        return tableID;
    }

    public void setTableID(int tableID) {
        this.tableID = tableID;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
