package com.example.practicaRestaurante.service.tableService;

import com.example.practicaRestaurante.model.Booking;
import com.example.practicaRestaurante.model.Result;
import com.example.practicaRestaurante.model.Table;
import com.example.practicaRestaurante.service.bookService.BookingService;
import com.example.practicaRestaurante.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TableService implements ITableService{

    private static Logger logger = LoggerFactory.getLogger(TableService.class);
    private static int nextTableID = 1;
    private static int totalCapacity = 0;
    private static ConcurrentHashMap<Integer, Table> tableData = new ConcurrentHashMap<>();

    private  final BookingService bookingService;

    @Autowired
    public TableService(@Lazy BookingService bookingService) {
        this.bookingService = bookingService;
    }


    @Override
    public ResponseEntity<Result> createTable(Table table) {

        return checkTable(table);
    }

    @Override
    public Collection<Table> listTable() {
        return tableData.values();
    }

    @Override
    public Table searchTable(int id) {
       if(!Util.isInteger(String.valueOf(id))){
           logger.error("Table's ID isn't int.");
           return null;
       }
        return tableData.get(id);
    }

    @Override
    public ResponseEntity<Result> updateTable(Integer id, Table table) {
        Result result = new Result();

        ResponseEntity<Result> BAD_REQUEST = checkEnteredId(id, result);
        if (BAD_REQUEST != null) return BAD_REQUEST;

        ResponseEntity<Result> BAD_REQUEST1 = checkEnteredTable(id, table, result);
        if (BAD_REQUEST1 != null) return BAD_REQUEST1;

        Table oldTable = tableData.get(id);

        ResponseEntity<Result> NOT_FOUND = checkIfTableExist(id, oldTable, result);
        if (NOT_FOUND != null) return NOT_FOUND;

        // Restar la capacidad antigua de la capacidad total
        totalCapacity -= oldTable.getCapacity();
        System.out.println("capacity(updateTable): "+totalCapacity);


        ResponseEntity<Result> BAD_REQUEST2 = isCapacityExceed(table, result);
        if (BAD_REQUEST2 != null) return BAD_REQUEST2;

        ResponseEntity<Result> BAD_REQUEST3 = validGuestNumber(table, result);
        if (BAD_REQUEST3 != null) return BAD_REQUEST3;

        // Agregar la nueva capacidad de la mesa a la capacidad total
        totalCapacity += table.getCapacity();
        System.out.println("capacity(updateTable): "+totalCapacity);

        // Actualizar la capacidad de la mesa en los datos de la mesa
        oldTable.setCapacity(table.getCapacity());
        tableData.replace(id, oldTable);
        logger.info("Table is updated: "+table);
        result.setResult(true);
        result.setMessage("update success");
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    private static ResponseEntity<Result> validGuestNumber(Table table, Result result) {
        // Verificar si la capacidad de la mesa actualizada está dentro del rango válido (1 a 8)
        if (!Util.isValidNumberOfGuests(table.getCapacity())) {
            logger.error("Table's capacity is out of range.");
            result.setResult(false);
            result.setMessage("Exceeds the table's capacity range");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return null;
    }

    private static ResponseEntity<Result> checkIfTableExist(Integer id, Table oldTable, Result result) {
        // Verificar si la mesa a actualizar existe
        if (oldTable == null) {
            logger.error("Table with id " + id + " not found");
            result.setResult(false);
            result.setMessage("Table with id " + id + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
        return null;
    }

    private static ResponseEntity<Result> checkEnteredTable(Integer id, Table table, Result result) {
        // Verificar si la mesa a actualizar existe
        if (table == null) {
            logger.error("Table with id: " + id + " doesn't exist.");
            result.setResult(false);
            result.setMessage("table is empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return null;
    }

    private static ResponseEntity<Result> checkEnteredId(Integer id, Result result) {
        // Verificar si el ID es nulo o no es un entero
        if (id == null || !Util.isInteger(String.valueOf(id))) {
            logger.error("Table's ID is null or isn't int.");
            result.setResult(false);
            result.setMessage("id cannot be empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return null;
    }

    @Override
    public ResponseEntity<Result> deleteTable(int id) {
        Result result = new Result();

        ResponseEntity<Result> NOT_FOUND = ifTableWithIdExist(id, result);
        if (NOT_FOUND != null) return NOT_FOUND;

        // Obtener la capacidad de la mesa a eliminar
        Table tableToDelete = tableData.get(id);
        int capacityToDelete = tableToDelete.getCapacity();

        // Restar la capacidad de la mesa eliminada de la capacidad total
        totalCapacity -= capacityToDelete;
        System.out.println("capacity(delete): "+totalCapacity);

        // Obtener todas las reservas asociadas a esta mesa
        Collection<Booking> bookingsToRemove = bookingService.getBookingsForTable(id);

        // Eliminar todas las reservas asociadas
        for (Booking booking : bookingsToRemove) {
            bookingService.cancelBooking(booking.getBookingID());
        }

        // Eliminar la mesa de los datos de la mesa
        tableData.remove(id);
        logger.info("Table is deleted: "+tableToDelete);
        result.setResult(true);
        result.setMessage("Table is deleted successfully.");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
    }

    private ResponseEntity<Result> ifTableWithIdExist(int id, Result result) {
        // Verificar si la mesa a eliminar existe
        if (!isTableExist(id)) {
            logger.error("Table with id: "+ id +" doesn't exist.");
            result.setResult(false);
            result.setMessage("Table with id: "+ id +" doesn't exist.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
        return null;
    }


    public ResponseEntity<Result> checkTable(Table table) {
        Result result = new Result();

        ResponseEntity<Result> BAD_REQUEST = ifCapacityIsEmpty(table, result);
        if (BAD_REQUEST != null) return BAD_REQUEST;

        ResponseEntity<Result> BAD_REQUEST1 = isCapacityInteger(table, result);
        if (BAD_REQUEST1 != null) return BAD_REQUEST1;

        ResponseEntity<Result> BAD_REQUEST2 = isCapacityExceed(table, result);
        if (BAD_REQUEST2 != null) return BAD_REQUEST2;

        ResponseEntity<Result> BAD_REQUEST3 = validGuestNumber(table, result);
        if (BAD_REQUEST3 != null) return BAD_REQUEST3;

        totalCapacity += table.getCapacity();
        System.out.println("capacity(checkTable): "+totalCapacity);

        table.setTableID(nextTableID++);
        tableData.put(table.getTableID(), table);
        logger.info("Table is created: "+table);

        result.setResult(true);
        result.setMessage("Table is created successfully.");
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    private static ResponseEntity<Result> isCapacityExceed(Table table, Result result) {
        //si capacidad es menor que 30
        if (totalCapacity + table.getCapacity() > 30) {
            logger.error("Total capacity exceeds 30. Current capacity(isCapacityExceed) is: "+totalCapacity);
            result.setResult(false);
            result.setMessage("Total capacity exceeds 30.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return null;
    }

    private static ResponseEntity<Result> isCapacityInteger(Table table, Result result) {
        // si capacidad es entero
        if (!Util.isInteger(String.valueOf(table.getCapacity()))) {
            logger.error("Table's capacity isn't int.");
            result.setResult(false);
            result.setMessage("Table's capacity isn't int.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return null;
    }

    private static ResponseEntity<Result> ifCapacityIsEmpty(Table table, Result result) {
        // si capacidad está vacio
        if (String.valueOf(table.getCapacity()).isEmpty()) {
            logger.error("Table's capacity is empty.");
            result.setResult(false);
            result.setMessage("Table's capacity is empty.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return null;
    }

    public boolean isTableExist(int tableID){
        return tableData.containsKey(tableID);
    }
}
