package com.example.practicaRestaurante.service.bookService;

import com.example.practicaRestaurante.model.Booking;
import com.example.practicaRestaurante.model.Result;
import com.example.practicaRestaurante.model.Table;
import org.springframework.http.ResponseEntity;

import java.util.Collection;
import java.util.List;

public interface IBookingService {

    ResponseEntity<Result> makeBooking(Booking booking);

    Collection<Booking> listBooking();

    Booking searchBooking(int id);

    ResponseEntity<Result> updateBooking(Integer id,Booking booking);

    ResponseEntity<Result> cancelBooking(int id);

    Collection<Booking> getBookingForDay(int day);
    List<Table> getBookingForDayTime(Integer day, Integer startTime, Integer endTime);

}
