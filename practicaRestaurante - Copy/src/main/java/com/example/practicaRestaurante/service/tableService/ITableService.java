package com.example.practicaRestaurante.service.tableService;

import com.example.practicaRestaurante.model.Result;
import com.example.practicaRestaurante.model.Table;
import org.springframework.http.ResponseEntity;

import java.util.Collection;

public interface ITableService {

    ResponseEntity<Result> createTable(Table table);

    Collection<Table> listTable();

    Table searchTable(int id);

    ResponseEntity<Result> updateTable(Integer id, Table table);

    ResponseEntity<Result> deleteTable(int id);
}
