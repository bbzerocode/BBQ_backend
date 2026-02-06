package com.bbqpos.backend.service;

import com.bbqpos.backend.dto.table.TableCreateDto;
import com.bbqpos.backend.dto.table.TableDto;
import com.bbqpos.backend.dto.table.TableUpdateDto;
import com.bbqpos.backend.model.RestaurantTable;
import com.bbqpos.backend.repository.RestaurantTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TableService {

    private final RestaurantTableRepository tableRepository;

    @Autowired
    public TableService(RestaurantTableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    public List<TableDto> getAllTables() {
        return tableRepository.findAll().stream()
                .map(TableDto::new)
                .collect(Collectors.toList());
    }

    public TableDto getTableById(Long id) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found"));
        return new TableDto(table);
    }

    public TableDto createTable(TableCreateDto dto) {
        RestaurantTable table = new RestaurantTable();
        table.setName(dto.getName());
        table.setType(RestaurantTable.Type.valueOf(dto.getType().toUpperCase()));
        table.setStatus(RestaurantTable.Status.valueOf(dto.getStatus().toUpperCase()));
        table.setPax(dto.getPax());
        table.setQrEnabled(dto.isQrEnabled());

        RestaurantTable savedTable = tableRepository.save(table);
        return new TableDto(savedTable);
    }

    public TableDto updateTable(Long id, TableUpdateDto dto) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found"));

        table.setName(dto.getName());
        table.setType(RestaurantTable.Type.valueOf(dto.getType().toUpperCase()));
        table.setStatus(RestaurantTable.Status.valueOf(dto.getStatus().toUpperCase()));
        table.setPax(dto.getPax());
        table.setQrEnabled(dto.isQrEnabled());

        RestaurantTable updatedTable = tableRepository.save(table);
        return new TableDto(updatedTable);
    }

    public void deleteTable(Long id) {
        tableRepository.deleteById(id);
    }

    public TableDto updateTableStatus(Long id, String status) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found"));

        table.setStatus(RestaurantTable.Status.valueOf(status.toUpperCase()));
        RestaurantTable updatedTable = tableRepository.save(table);
        return new TableDto(updatedTable);
    }

}
