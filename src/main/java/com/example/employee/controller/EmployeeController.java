package com.example.employee.controller;

import com.example.employee.exceptions.EmptyResultDataAccessException;
import com.example.employee.model.Employee;
import com.example.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/employees")
public class EmployeeController {


    private EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("add")
    public ResponseEntity<String> addEmployee(@RequestBody Employee employee) {
        String response = employeeService.addEmployee(employee);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("getall")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employee = employeeService.getAllEmployees();
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Integer id) {

        Optional<Employee> employee = employeeService.getEmployeeById(id);

        return employee.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    @PutMapping("update")
    public ResponseEntity<Employee> updateEmployee(@RequestParam Integer id, @RequestParam String column, @RequestParam String dataToEdit) {
        Optional<Employee> updatedEmployee = employeeService.updateEmployee(id, column, dataToEdit);

        return updatedEmployee.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable int id) {
        try {
            employeeService.deleteEmployee(id);
            return new ResponseEntity<>("Record Deleted", HttpStatus.OK);
        } catch (EmptyResultDataAccessException e) {
            return new ResponseEntity<>("Record Not Found", HttpStatus.NOT_FOUND);
        }


    }

}

