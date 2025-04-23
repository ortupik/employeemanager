package com.employee;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("employeeBean")
@SessionScoped 
public class EmployeeBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private Employee employee = new Employee();
    private List<Employee> employees;
    private boolean editMode = false;
    private Employee selectedEmployee; 

    @Inject
    private EmployeeDAO employeeDAO;

    @PostConstruct
    public void init() {
        refreshEmployeeList();
    }

    public void refreshEmployeeList() {
        employees = employeeDAO.getAllEmployees();
    }

    public String saveEmployee() {
        try {
            if (editMode) {
                employeeDAO.updateEmployee(employee);
                addMessage(FacesMessage.SEVERITY_INFO, "Success", "Employee updated successfully");
            } else {
                employeeDAO.addEmployee(employee);
                addMessage(FacesMessage.SEVERITY_INFO, "Success", "Employee added successfully");
            }
            
            resetForm();
            refreshEmployeeList();
            return null;
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "An error occurred: " + e.getMessage());
            return null;
        }
    }

    public void editEmployee(Employee emp) {
        this.employee = new Employee();
        this.employee.setId(emp.getId());
        this.employee.setFirstName(emp.getFirstName());
        this.employee.setLastName(emp.getLastName());
        this.employee.setEmail(emp.getEmail());
        this.employee.setDepartment(emp.getDepartment());
        this.employee.setSalary(emp.getSalary());
        this.editMode = true;
    }

    // Modified to work with the confirmation dialog
    public void deleteEmployee() {
        try {
            if (selectedEmployee != null) {
                employeeDAO.deleteEmployee(selectedEmployee.getId());
                addMessage(FacesMessage.SEVERITY_INFO, "Success", "Employee deleted successfully");
                refreshEmployeeList();
                selectedEmployee = null; // Clear the selected employee
            }
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to delete employee: " + e.getMessage());
        }
    }

    public void resetForm() {
        this.employee = new Employee();
        this.editMode = false;
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }
    
    public Employee getSelectedEmployee() {
        return selectedEmployee;
    }
    
    public void setSelectedEmployee(Employee selectedEmployee) {
        this.selectedEmployee = selectedEmployee;
    }
}