package com.example.practicaRestaurante.service.bookService;

import com.example.practicaRestaurante.model.Booking;
import com.example.practicaRestaurante.model.Result;
import com.example.practicaRestaurante.model.Table;
import com.example.practicaRestaurante.service.tableService.TableService;
import com.example.practicaRestaurante.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BookingService implements IBookingService {
    private static int nextBookingID = 1;

    private final static Logger logger = LoggerFactory.getLogger(BookingService.class);
    private final static ConcurrentHashMap<Integer, Booking> bookingData = new ConcurrentHashMap<>();
    private final TableService tableService;

    @Autowired
    public BookingService(@Lazy TableService tableService) {
        this.tableService = tableService;
    }

    @Override
    public ResponseEntity<Result> makeBooking(Booking booking) {
        ResponseEntity<Result> result = checkNewBooking(booking, "Booking is made successfully.",HttpStatus.CREATED);
        if (result.getStatusCode() == HttpStatus.CREATED) {
            booking.setBookingID(nextBookingID++);
            bookingData.put(booking.getBookingID(), booking);
            logger.info("Created booking: {}", booking);
        }
        return result;
    }

    @Override
    public Collection<Booking> listBooking() {
        return bookingData.values();
    }

    @Override
    public Booking searchBooking(int id) {
        if (!Util.isInteger(String.valueOf(id))) {
            logger.error("Booking's ID isn't id,so we can't find relative booking ");
            return null;
        }
        return bookingData.get(id);
    }

    @Override
    public ResponseEntity<Result> updateBooking(Integer id, Booking booking) {
        //comprobar reserva antigua
        ResponseEntity<Result> result = checkOldBooking(id, booking);
        if(result.getStatusCode() == HttpStatus.NOT_FOUND){
            return result;
        } else if (result.getStatusCode() == HttpStatus.NOT_FOUND) {
            return result;
        }
        //comprobar reserva nueva
        ResponseEntity<Result> outcome = checkNewBooking(booking, "Booking updated success.", HttpStatus.OK);
        if (result.getStatusCode() == HttpStatus.OK && outcome.getStatusCode() == HttpStatus.OK) {
            bookingData.replace(id, booking);
            logger.info("Updated booking: "+booking);
        }
        return outcome;
    }

    @Override
    public ResponseEntity<Result> cancelBooking(int id) {
        Result result = new Result();

        ResponseEntity<Result> NOT_FOUND = checkId(id, result);
        if (NOT_FOUND != null) {
            return NOT_FOUND;
        }

        Booking booking = bookingData.get(id);

        ResponseEntity<Result> NOT_FOUND1 = checkBooking(id, booking, result);
        if (NOT_FOUND1 != null) {
            return NOT_FOUND1;
        }

        bookingData.remove(id);
        logger.info("Cacelled booking: "+booking);
        result.setResult(true);
        result.setMessage("Booking is cancelled successfully.");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
    }

    private static ResponseEntity<Result> checkBooking(int id, Booking booking, Result result) {
        if (booking == null) {
            logger.error("Booking with id: "+ id +" doesn't exist.");
            result.setResult(false);
            result.setMessage("Booking with id: "+id+" doesn't exist.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
        return null;
    }

    private static ResponseEntity<Result> checkId(int id, Result result) {
        if (!Util.isInteger(String.valueOf(id))) {
            logger.error("Booking's ID isn't int, so we can't cancel booking.");
            result.setResult(false);
            result.setMessage("Booking's ID isn't int, so we can't cancel booking.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
        return null;
    }

    @Override
    public Collection<Booking> getBookingForDay(int day) {

        Collection<Booking> todayBooking = new ArrayList<>();
        for (Booking booking : bookingData.values()) {
            if (booking.getDay() == day) {
                todayBooking.add(booking);
            }
        }
        logger.info("Booking for day:"+day+": "+todayBooking);
        return todayBooking;

    }

    @Override
    public List<Table> getBookingForDayTime(Integer day, Integer startTime, Integer endTime) {
        List<Table> availableTables = new ArrayList<>();
        Collection<Table> allTables = tableService.listTable();

        // Verificar disponibilidad para cada mesa
        for (Table table : allTables) {
            if (checkAvailableTable(table, day, startTime, endTime)) {
                availableTables.add(table);
            }
        }
        logger.info("Table available for day:{}, start time: {}, end time: {}, availableTables: {}", day, startTime, endTime, availableTables);
        return availableTables;
    }

    private boolean checkAvailableTable(Table table, Integer day, Integer startTime, Integer endTime) {
        for (Booking booking : bookingData.values()) {
            if (checkTimeSlot(booking, table, day, startTime, endTime)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkTimeSlot(Booking booking, Table table, Integer day, Integer startTime, Integer endTime) {
        return booking.getTableID() == table.getTableID() &&
                booking.getDay() == day &&
                booking.getStartTime() < endTime &&
                booking.getStartTime() + booking.getBookingDuration() > startTime;
    }


    public ResponseEntity<Result> checkOldBooking(Integer id, Booking booking) {
        Result result = new Result();

        ResponseEntity<Result> BAD_REQUEST = validID(id, result);
        if (BAD_REQUEST != null) {
            return BAD_REQUEST;
        }

        ResponseEntity<Result> BAD_REQUEST1 = checkBooking(booking, result);
        if (BAD_REQUEST1 != null) {
            return BAD_REQUEST1;
        }

        Booking oldBooking = bookingData.get(id);

        return checkOldBooking(id,booking, oldBooking, result);

    }

    private static ResponseEntity<Result> checkOldBooking(Integer id,Booking booking, Booking oldBooking, Result result) {
        if (oldBooking == null) {
            logger.error("Can't find booking with id: {} for updating.", id);
            result.setResult(false);
            result.setMessage("table with id " + id + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        } else {
            booking.setBookingID(oldBooking.getBookingID());
            result.setResult(true);
            result.setMessage("");
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }
    }

    private static ResponseEntity<Result> checkBooking(Booking booking, Result result) {
        if (booking == null) {
            logger.error("Booking is empty, so we can't update it.");
            result.setResult(false);
            result.setMessage("booking is empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return null;
    }

    private static ResponseEntity<Result> validID(Integer id, Result result) {
        if (id == null) {
            logger.error("Booking's ID is null, so we can't find booking for updating.");
            result.setResult(false);
            result.setMessage("id cannot be empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return null;
    }

    public ResponseEntity<Result> checkNewBooking(Booking booking, String msg, HttpStatus status) {
        Result result = new Result();

        ResponseEntity<Result> BAD_REQUEST5 = validBookingId(booking, result);
        if (BAD_REQUEST5 != null) {
            return BAD_REQUEST5;
        }

        ResponseEntity<Result> BAD_REQUEST6 = isTableExiste(booking, result);
        if (BAD_REQUEST6 != null) {
            return BAD_REQUEST6;
        }

        ResponseEntity<Result> BAD_REQUEST = checkDayTime(booking, result);
        if (BAD_REQUEST != null) {
            return BAD_REQUEST;
        }

        ResponseEntity<Result> BAD_REQUEST1 = checkCustomer(booking, result);
        if (BAD_REQUEST1 != null) {
            return BAD_REQUEST1;
        }

        ResponseEntity<Result> BAD_REQUEST2 = checkCustomerName(booking, result);
        if (BAD_REQUEST2 != null) {
            return BAD_REQUEST2;
        }

        ResponseEntity<Result> BAD_REQUEST3 = checkTel(booking, result);
        if (BAD_REQUEST3 != null) {
            return BAD_REQUEST3;
        }

        ResponseEntity<Result> BAD_REQUEST4 = checkGuestNumber(booking, result);
        if (BAD_REQUEST4 != null) {
            return BAD_REQUEST4;
        }

        // Verificar si el número de invitados es mayor que la capacidad de la mesa
        Table table = tableService.searchTable(booking.getTableID());

        ResponseEntity<Result> BAD_REQUEST7 = isCapacityExceed(booking, table, result);
        if (BAD_REQUEST7 != null) {
            return BAD_REQUEST7;
        }

        ResponseEntity<Result> CONFLICT = isBookingExisted(booking, result);
        if (CONFLICT != null) {
            return CONFLICT;
        }

        result.setResult(true);
        result.setMessage(msg);
        return ResponseEntity.status(status).body(result);
    }

    private ResponseEntity<Result> isBookingExisted(Booking booking, Result result) {
        // Verificar si hay conflicto de disponibilidad con otra reserva previa
        if (hasBookingConflict(booking)) {
            logger.error("Availability conflict with another previous booking,so creating/updating booking is failed.");
            result.setResult(false);
            result.setMessage("Availability conflict with another previous booking.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }
        return null;
    }

    private static ResponseEntity<Result> isCapacityExceed(Booking booking, Table table, Result result) {
        if (table != null && booking.getGuestNumber() > table.getCapacity()) {
            logger.error("Exceeds table' capacity,so creating/updating booking is failed.");
            result.setResult(false);
            result.setMessage("Number of guests exceeds table capacity.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return null;
    }

    private static ResponseEntity<Result> checkGuestNumber(Booking booking, Result result) {
        if (!Util.isValidNumberOfGuests(booking.getGuestNumber())) {
            logger.error("Guest's number is out of range,so creating/updating booking is failed.");
            result.setResult(false);
            result.setMessage("Guest's number isn't within the table's capacity range");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return null;
    }

    private static ResponseEntity<Result> checkTel(Booking booking, Result result) {
        if (!Util.isValidPhoneNumber(booking.getCustomer().getTel())) {
            logger.error("Customer's telephone has incorrect format,so creating/updating booking is failed.");
            result.setResult(false);
            result.setMessage("Invalid phone number entered");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return null;
    }

    private static ResponseEntity<Result> checkCustomerName(Booking booking, Result result) {
        if ((!Util.isValidStringLength(booking.getCustomer().getName()) || (!Util.isValidStringLength(booking.getCustomer().getSurname())))) {
            logger.error("Customer's name/surname exceedes maximum length,so creating/updating booking is failed.");
            result.setResult(false);
            result.setMessage("Invalid name or surname entered");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return null;
    }

    private static ResponseEntity<Result> checkCustomer(Booking booking, Result result) {
        if (booking.getCustomer() == null) {
            logger.error("Customer's data is null,so creating/updating booking is failed.");
            result.setResult(false);
            result.setMessage("Customer is empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return null;
    }

    private static ResponseEntity<Result> checkDayTime(Booking booking, Result result) {
        if (!Util.isValidDateAndTime(booking.getDay(), booking.getStartTime())) {
            logger.error("Day or Time isn't int,so creating/updating booking is failed.");
            result.setResult(false);
            result.setMessage("Invalid date or hour entered");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return null;
    }

    private ResponseEntity<Result> isTableExiste(Booking booking, Result result) {
        if (!tableService.isTableExist(booking.getTableID())) {
            logger.error("Table's ID: {} doesn't exist,so creating/updating booking is failed.", booking.getTableID());
            result.setResult(false);
            result.setMessage("Table with id " + booking.getTableID() + " doesn't exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return null;
    }

    private static ResponseEntity<Result> validBookingId(Booking booking, Result result) {
        if (!Util.isInteger(String.valueOf(booking.getBookingID()))) {
            logger.error("Booking's ID isn't int,so creating/updating booking is failed.");
            result.setResult(false);
            result.setMessage("Table' id must be integer");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return null;
    }

    //verificar si hay conflicto de disponibilidad con otra reserva previa
    private boolean hasBookingConflict(Booking booking) {
        Collection<Booking> existingBookings = bookingData.values();
        for (Booking existingBooking : existingBookings) {
            if (existingBooking.getTableID() == booking.getTableID() &&
                    existingBooking.getDay() == booking.getDay() &&
                    existingBooking.getStartTime() == booking.getStartTime()) {
                return true;
            }
        }
        return false;
    }

    public Collection<Booking> getBookingsForTable(int tableId) {
        Collection<Booking> bookingsForTable = new ArrayList<>();
        for (Booking booking : bookingData.values()) {
            if (booking.getTableID() == tableId) {
                bookingsForTable.add(booking);
            }
        }
        return bookingsForTable;
    }

}
