package org.example.dao;

import org.example.model.Employee;

import java.util.List;

public interface EmployeeDao {
    List<Employee> getAllEmployees();
    void insertEmployee(Employee employee);
}
