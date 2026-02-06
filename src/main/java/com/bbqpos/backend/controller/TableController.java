package com.bbqpos.backend.controller;

import com.bbqpos.backend.dto.table.TableCreateDto;
import com.bbqpos.backend.dto.table.TableDto;
import com.bbqpos.backend.dto.table.TableUpdateDto;
import com.bbqpos.backend.service.TableService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tables")
public class TableController {

    private final TableService tableService;

    @Autowired
    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping
    public ResponseEntity<List<TableDto>> getAllTables() {
        List<TableDto> tables = tableService.getAllTables();
        return ResponseEntity.ok(tables);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TableDto> getTableById(@PathVariable Long id) {
        TableDto table = tableService.getTableById(id);
        return ResponseEntity.ok(table);
    }

    @PostMapping
    public ResponseEntity<TableDto> createTable(@Valid @RequestBody TableCreateDto dto) {
        TableDto table = tableService.createTable(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(table);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TableDto> updateTable(@PathVariable Long id, @Valid @RequestBody TableUpdateDto dto) {
        TableDto table = tableService.updateTable(id, dto);
        return ResponseEntity.ok(table);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        tableService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TableDto> updateTableStatus(@PathVariable Long id, @RequestParam String status) {
        TableDto table = tableService.updateTableStatus(id, status);
        return ResponseEntity.ok(table);
    }

}
