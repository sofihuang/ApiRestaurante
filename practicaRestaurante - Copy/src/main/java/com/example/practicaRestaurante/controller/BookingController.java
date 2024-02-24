package com.example.practicaRestaurante.controller;

import com.example.practicaRestaurante.model.Booking;
import com.example.practicaRestaurante.model.Customer;
import com.example.practicaRestaurante.model.Result;
import com.example.practicaRestaurante.model.Table;
import com.example.practicaRestaurante.service.bookService.IBookingService;
import com.example.practicaRestaurante.service.tableService.ITableService;
import com.example.practicaRestaurante.service.tableService.TableService;
import com.example.practicaRestaurante.util.Util;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("api/restaurante")

public class BookingController {

    @Autowired
    public IBookingService bookingService;


    @PostMapping("/reservas")
    public ResponseEntity<Result> createNewBooking(@RequestBody Booking booking) {
        return bookingService.makeBooking(booking);
    }


    @GetMapping("/reservas")
    public ResponseEntity<Collection<Booking>> getAllBooking() {
        Collection<Booking> bookings = bookingService.listBooking();
        if (bookings.isEmpty()) {
            return new ResponseEntity<>(bookings, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }


    @GetMapping("/reservas/{id}")
    public ResponseEntity<Booking> searchBookingById(@PathVariable String id) {
        if (!Util.isInteger(id)) {
            Result outcome = new Result(false, "Booking not found.");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        Booking booking = bookingService.searchBooking(Integer.parseInt(id));
        if (booking == null) {
            Result outcome = new Result(false, "Booking not found.");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            Result outcome = new Result(true, booking.toString());
            return new ResponseEntity<>(booking, HttpStatus.OK);
        }
    }


    @GetMapping("/reservas/hoy")
    public ResponseEntity<Collection<Booking>> getTodayBooking() {
        Collection<Booking> todayBooking = bookingService.getBookingForDay((Util.getDay()));
        if (todayBooking == null || todayBooking.isEmpty()) {
            Result outcome = new Result(false, "Booking not found.");
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        } else {
            Result outcome = new Result(true, todayBooking.toString());
            return new ResponseEntity<>(todayBooking, HttpStatus.OK);
        }
    }


    @GetMapping("/mesas/disponibles/{day}/{startTime}/{endTime}")
    public ResponseEntity<Collection<Table>> getBookingForDayAndTime(@PathVariable String day, @PathVariable String startTime, @PathVariable String endTime) {
        if (!Util.isInteger(day) || !Util.isInteger(startTime) || !Util.isInteger(endTime)) {
            Result outcome = new Result(false, "Invalid data.");
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
        if (!Util.isValidDay(Integer.parseInt(day)) || !Util.isValidHour(Integer.parseInt(startTime)) || !Util.isValidHour(Integer.parseInt(endTime))) {
            Result outcome = new Result(false, "Invalid data.");
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }

        Collection<Table> availableTables = bookingService.getBookingForDayTime(Integer.parseInt(day), Integer.parseInt(startTime), Integer.parseInt(endTime));
        if (availableTables == null || availableTables.isEmpty()) {
            Result outcome = new Result(false, "Any table is available.");
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        } else {
            Result outcome = new Result(true, availableTables.toString());
            return new ResponseEntity<>(availableTables, HttpStatus.OK);
        }
    }


    @PutMapping("/reservas/{id}")
    public ResponseEntity<Result> updateBookingData(@PathVariable String id, @RequestBody Booking booking) {
        if (!Util.isInteger(id) || booking == null) {
            Result outcome = new Result(false, "Booking not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(outcome);
        }
        return bookingService.updateBooking(Integer.parseInt(id), booking);
    }

    @DeleteMapping("/reservas/{id}")
    public ResponseEntity<Result> deleteBooking(@PathVariable String id) {
        if (!Util.isInteger(id)) {
            Result outcome = new Result(false, "Booking not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(outcome);
        }

        return bookingService.cancelBooking(Integer.parseInt(id));

    }


}
