package org.example.controller;

import org.example.model.Employee;
import org.example.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EmployeeController {

    final EmployeeService empService;

    @Autowired
    public EmployeeController(EmployeeService empService) {
        this.empService = empService;
    }

    @GetMapping(value = "/employees")
    public List<Employee> getEmployees() {
        return empService.getAllEmployees();
    }

    @PostMapping(value = "/insert")
    public void insertEmployee(@RequestBody Employee employee) {
        empService.insertEmployee(employee);
    }
}