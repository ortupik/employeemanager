package com.employee;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ApplicationScoped
public class EmployeeDAO implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(EmployeeDAO.class.getName());
    
    private static final String SELECT_ALL_EMPLOYEES = "SELECT * FROM employees ORDER BY id DESC";
    private static final String INSERT_EMPLOYEE = "INSERT INTO employees (first_name, last_name, email, department, salary) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_EMPLOYEE = "UPDATE employees SET first_name = ?, last_name = ?, email = ?, department = ?, salary = ? WHERE id = ?";
    private static final String DELETE_EMPLOYEE = "DELETE FROM employees WHERE id = ?";
    private static final String SELECT_EMPLOYEE_BY_ID = "SELECT * FROM employees WHERE id = ?";
    private static final String SELECT_EMPLOYEE_BY_EMAIL = "SELECT * FROM employees WHERE email = ? AND id != ?";

    private static final String CREATE_TABLE_IF_NOT_EXISTS = 
      "CREATE TABLE IF NOT EXISTS employees (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "first_name VARCHAR(50) NOT NULL, " +
            "last_name VARCHAR(50) NOT NULL, " +
            "email VARCHAR(100) NOT NULL UNIQUE, " +
            "department VARCHAR(50) NOT NULL, " +
            "salary DECIMAL(10,2) NOT NULL" +
        ")";

    @Inject
    private DatabaseConfig dbConfig;
    
    @PostConstruct
    public void init() {
        try {
            Class.forName(dbConfig.getDriver());
            
            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.execute(CREATE_TABLE_IF_NOT_EXISTS);
                LOGGER.log(Level.INFO, "Database initialized successfully");
            }
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "MySQL JDBC Driver not found", e);
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize database", e);
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(
                dbConfig.getUrl(), 
                dbConfig.getUsername(), 
                dbConfig.getPassword()
            );
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to connect to database", e);
            throw e;
        }
    }

    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_EMPLOYEES); 
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Employee employee = mapResultSetToEmployee(rs);
                employees.add(employee);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching employees", e);
            throw new RuntimeException("Error fetching employees", e);
        }
        return employees;
    }

    public void addEmployee(Employee employee) {
        if (isEmailExists(employee.getEmail(), 0)) {
            throw new RuntimeException("Email already exists");
        }
        
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(INSERT_EMPLOYEE, Statement.RETURN_GENERATED_KEYS)) {
            
            prepareEmployeeStatement(stmt, employee);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating employee failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    employee.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating employee failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding employee", e);
            throw new RuntimeException("Error adding employee", e);
        }
    }

    public void updateEmployee(Employee employee) {
        if (isEmailExists(employee.getEmail(), employee.getId())) {
            throw new RuntimeException("Email already exists for another employee");
        }
        
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(UPDATE_EMPLOYEE)) {
            
            prepareEmployeeStatement(stmt, employee);
            stmt.setInt(6, employee.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating employee failed, no rows affected.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating employee", e);
            throw new RuntimeException("Error updating employee", e);
        }
    }

    public void deleteEmployee(int id) {
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(DELETE_EMPLOYEE)) {
            
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Deleting employee failed, no rows affected.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting employee", e);
            throw new RuntimeException("Error deleting employee", e);
        }
    }

    public Employee getEmployeeById(int id) {
        Employee employee = null;
        
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(SELECT_EMPLOYEE_BY_ID)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    employee = mapResultSetToEmployee(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching employee by ID", e);
            throw new RuntimeException("Error fetching employee by ID", e);
        }
        
        return employee;
    }

    private boolean isEmailExists(String email, int employeeId) {
        try (Connection conn = getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(SELECT_EMPLOYEE_BY_EMAIL)) {
            
            stmt.setString(1, email);
            stmt.setInt(2, employeeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking email existence", e);
            throw new RuntimeException("Error checking email existence", e);
        }
    }

    private void prepareEmployeeStatement(PreparedStatement stmt, Employee employee) throws SQLException {
        stmt.setString(1, employee.getFirstName());
        stmt.setString(2, employee.getLastName());
        stmt.setString(3, employee.getEmail());
        stmt.setString(4, employee.getDepartment());
        stmt.setBigDecimal(5, employee.getSalary());
    }

    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setId(rs.getInt("id"));
        employee.setFirstName(rs.getString("first_name"));
        employee.setLastName(rs.getString("last_name"));
        employee.setEmail(rs.getString("email"));
        employee.setDepartment(rs.getString("department"));
        employee.setSalary(rs.getBigDecimal("salary"));
        return employee;
    }
}