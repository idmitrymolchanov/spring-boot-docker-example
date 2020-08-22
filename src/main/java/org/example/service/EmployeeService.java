package org.example.service;

import org.example.model.Employee;

import java.util.List;

public interface EmployeeService {
    List<Employee> getAllEmployees();
    void insertEmployee(Employee employee);
}
