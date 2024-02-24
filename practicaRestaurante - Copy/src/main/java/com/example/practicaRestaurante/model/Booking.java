package com.example.practicaRestaurante.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class Booking {

    @NonNull
    private int bookingID;
    @NonNull
    private int tableID;
    @NonNull
    private int day;
    @NonNull
    private int startTime;
    @NonNull
    private Customer customer;
    @NonNull
    private int guestNumber;
    private final int bookingDuration = 1;


    public int getBookingID() {
        return bookingID;
    }

    public void setBookingID(int bookingID) {
        this.bookingID = bookingID;
    }

    public int getTableID() {
        return tableID;
    }

    public void setTableID(int tableID) {
        this.tableID = tableID;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getGuestNumber() {
        return guestNumber;
    }

    public void setGuestNumber(int guestNumber) {
        this.guestNumber = guestNumber;
    }

    public int getBookingDuration() {
        return bookingDuration;
    }
}
