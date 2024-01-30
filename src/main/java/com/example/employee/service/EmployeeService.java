package com.example.employee.service;

import com.example.employee.exceptions.EmptyResultDataAccessException;
import com.example.employee.model.Employee;
import com.example.employee.repository.EmployeeDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class EmployeeService {

    private static final Logger LOGGER = Logger.getLogger(EmployeeService.class.getName());

    @Autowired
    EmployeeDao employeeDao;
    public EmployeeService(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }

    public String addEmployee(Employee employee) {
        employeeDao.save(employee);
        LOGGER.log(Level.INFO," Employee record inserted successfully");
        return "Data inserted successfully";

    }

    public Optional<Employee> getEmployeeById(Integer id) {
        Optional<Employee> employee = employeeDao.findById(id);
        LOGGER.log(Level.INFO, String.format("Retrieved employee record with ID %s ", id));
        return employee;
    }

    public List<Employee> getAllEmployees() {
        List<Employee> employeeList = employeeDao.findAll();
        LOGGER.log(Level.INFO, String.format("Retrieved  %s employee records ", employeeList.size()));
        return employeeList;
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Optional<Employee> updateEmployee(int id, String column, String dataToEdit) {
        String query = "UPDATE Employee SET " + column + " = :dataToEdit WHERE id = :id";
        Query nativeQuery = entityManager.createNativeQuery(query);

        if ("age".equalsIgnoreCase(column)) {
            try {
                int ageValue = Integer.parseInt(dataToEdit);
                nativeQuery.setParameter("dataToEdit", ageValue);
            } catch (NumberFormatException e) {
                // Handle the case where dataToEdit is not a valid integer
                LOGGER.log(Level.WARNING, String.format("Invalid age value ", id));
                throw new IllegalArgumentException("Invalid age value: " + dataToEdit, e);
            }
        }
        else {
            nativeQuery.setParameter("dataToEdit", dataToEdit);
        }
        nativeQuery.setParameter("id", id);


        nativeQuery.executeUpdate();
        LOGGER.log(Level.INFO, String.format(" Employee record with  ID %s is updated in database.", id));

        return employeeDao.findById(id);

    }

    public void deleteEmployee(int id) throws EmptyResultDataAccessException {
        Optional <Employee> data = employeeDao.findById(id) ;
        if(data.isEmpty()){
            LOGGER.log(Level.WARNING, String.format("Cannot perform DELETE operation - ID %s is Not found in the database.", id));
            throw new EmptyResultDataAccessException("Record Not found");
        }

        employeeDao.deleteById(id);
        LOGGER.log(Level.INFO, String.format(" Employee record with  ID %s is deleted from the database.", id));

  }
}
