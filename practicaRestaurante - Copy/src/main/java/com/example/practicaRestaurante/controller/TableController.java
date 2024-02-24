package com.example.practicaRestaurante.controller;

import com.example.practicaRestaurante.model.Result;
import com.example.practicaRestaurante.model.Table;
import com.example.practicaRestaurante.service.tableService.ITableService;
import com.example.practicaRestaurante.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;


@RestController
@RequestMapping("/api/restaurante")
public class TableController {

    @Autowired
    public ITableService tableService;


    @PostMapping("/mesas")
    public ResponseEntity<Result> createNewTable(@RequestBody Table table) {
        return tableService.createTable(table);
    }


    @GetMapping("/mesas")
    public ResponseEntity<Collection<Table>> getAllTables() {
        Collection<Table> tables = tableService.listTable();
        if (tables.isEmpty()) {
            return new ResponseEntity<>(tables, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(tables, HttpStatus.OK);
    }


    @GetMapping("/mesas/{id}")
    public ResponseEntity<Table> searchTableById(@PathVariable String id) {
        if (!Util.isInteger(String.valueOf(id))) {
            Result outcome = new Result(false, "Table not found.");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        Table table = tableService.searchTable(Integer.parseInt(id));
        if (table == null) {
            Result outcome = new Result(false, "Table not found.");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            Result outcome = new Result(true, table.toString());
            return new ResponseEntity<>(table, HttpStatus.OK);
        }
    }


    @PutMapping("/mesas/{id}")
    public ResponseEntity<Result> updateTableData(@PathVariable String id, @RequestBody Table table) {
        if (!Util.isInteger(id) || table == null) {
            Result outcome = new Result(false, "Table not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(outcome);
        }
        return tableService.updateTable(Integer.parseInt(id), table);
    }


    @DeleteMapping("/mesas/{id}")
    public ResponseEntity<Result> deleteTable(@PathVariable String id) {
        if (!Util.isInteger(id)) {
            Result outcome = new Result(false, "Table not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(outcome);
        }

        return tableService.deleteTable(Integer.parseInt(id));

    }
}
