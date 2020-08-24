package org.example.dao;

import org.example.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class EmployeeDaoImpl extends JdbcDaoSupport implements EmployeeDao {

    final DataSource dataSource;

    @Autowired
    public EmployeeDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }

    @Override
    public List<Employee> getAllEmployees() {
        String sql = "SELECT * FROM employee";
        List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql);

        List<Employee> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Employee emp = new Employee();
            emp.setId((String) row.get("id"));
            emp.setFirstName((String) row.get("firstName"));
            emp.setLastName((String) row.get("lastName"));
            emp.setAge((Integer) row.get("age"));
            result.add(emp);
        }

        return result;
    }

    @Override
    public void insertEmployee(Employee employee) {
        String sql = "INSERT INTO employee " + "(id, firstName, lastName, age) VALUES (?,?,?,?)";
        getJdbcTemplate().update(sql, new Object[] {
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getAge()
        });
    }
}
