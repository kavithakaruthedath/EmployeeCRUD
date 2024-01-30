package com.example.employee.service;

import com.example.employee.exceptions.EmptyResultDataAccessException;
import com.example.employee.model.Employee;
import com.example.employee.repository.EmployeeDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    EmployeeDao employeeDao;
    @InjectMocks
    EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddEmployee() {

        // Given
        // Create an Employee object with sample data
        Employee employee = new Employee("Kavitha", "Anooj", 30, "TechnologyAnalyst");

        // Expected response when saving the employee
        String expectedResponse = "Date inserted successfully";

        // When
        // Invoke the addEmployee method in the service layer
        String actualResponse = employeeService.addEmployee(employee);

        // Then
        // Verify that the save method on the employeeDao is called exactly once with the provided employee
        Mockito.verify(employeeDao, Mockito.times(1)).save(employee);

        // Assert that the actual response matches the expected response
        Assertions.assertEquals(expectedResponse, actualResponse, "EmployeeService-addEmploye");
    }

    @Test
    void testGetAllEmployees() {

        // Given
        // Create an Employee object with sample data
        Employee employee = new Employee("Kavitha", "Anooj", 30, "TechnologyAnalyst");

        // Create a list of employees containing the sample employee
        List<Employee> employees = new ArrayList<>();
        employees.add(employee);

        // Mock the behavior of the employeeService to return the list of employees when getAllEmployees is called
        Mockito.when(employeeService.getAllEmployees()).thenReturn(employees);

        // When
        // Invoke the getAllEmployees method in the service layer
        List<Employee> actualEmployee = employeeService.getAllEmployees();

        // Then
        // Verify that the findAll method on the employeeDao is called exactly once
        Mockito.verify(employeeDao, Mockito.times(1)).findAll();

        // Assert that the actual list of employees matches the expected list
        Assertions.assertEquals(employees, actualEmployee, "EmployeeService-GetAllEmployees");
    }

    @Test
    void testGetEmployeeById() {

        // Given
        // Create an Employee object with sample data
        Employee employee = new Employee("Kavitha", "Anooj", 30, "TechnologyAnalyst");

        // Mock the behavior of the employeeService to return the Optional of the sample employee when getEmployeeById is called with ID 1
        Mockito.when(employeeService.getEmployeeById(1)).thenReturn(Optional.of(employee));

        // When
        // Invoke the getEmployeeById method in the service layer with ID 1
        Optional<Employee> actualEmployee = employeeService.getEmployeeById(1);

        // Then
        // Verify that the findById method on the employeeDao is called exactly once with ID 1
        Mockito.verify(employeeDao, Mockito.times(1)).findById(1);

        // Assert that the actual employee returned by the service matches the expected employee
        Assertions.assertEquals(employee, actualEmployee.get(), "EmployeeService-getEmployeeByID");
    }

    @Mock
    private EntityManager entityManager;

    @Test
    public void testUpdateEmployeeAge() {
        // Given
        // Set up test data
        int id = 1;
        String column = "age";
        String dataToEdit = "25";

        // Create an SQL query to update the age of an employee with a specified ID
        String query = "UPDATE Employee SET age = :dataToEdit WHERE id = :id";

        // Create a mock for the Query interface
        Query nativeQuery = Mockito.mock(Query.class);

        // Create a mock Employee object that represents the expected result after the update
        Employee expectedEmployee = new Employee();

        // Mock the behavior of the EntityManager:
        // - When createNativeQuery is called with the specified query, return the mock Query object
        Mockito.when(entityManager.createNativeQuery(query)).thenReturn(nativeQuery);
        // - When findById is called with the specified ID, return an Optional containing the expectedEmployee
        Mockito.when(employeeDao.findById(id)).thenReturn(Optional.of(expectedEmployee));

        // When
        // Call the updateEmployee method in the service layer
        Optional<Employee> updatedEmployee = employeeService.updateEmployee(id, column, dataToEdit);
        // Then
        //Ensure that the updatedEmployee is not null, is present and matches with expected value
        Assertions.assertNotNull(updatedEmployee);
        Assertions.assertTrue(updatedEmployee.isPresent());
        Assertions.assertSame(expectedEmployee, updatedEmployee.get());

        // Verify interactions with entityManager
        Mockito.verify(entityManager, Mockito.times(1)).createNativeQuery(query);
        Mockito.verify(nativeQuery, Mockito.times(1)).setParameter("id", id);
        Mockito.verify(nativeQuery, Mockito.times(1)).setParameter("dataToEdit", Integer.parseInt(dataToEdit));
        Mockito.verify(nativeQuery, Mockito.times(1)).executeUpdate();

        // Verify interactions with employeeDao
        Mockito.verify(employeeDao, Mockito.times(1)).findById(id);
    }

    @Test
    public void testUpdateEmployeeOtherColumn() {
        // Given
        // Set up test data
        int id = 1;
        String column = "otherColumn";
        String dataToEdit = "newValue";

        // Create an SQL query to update the age of an employee with a specified ID
        String query = "UPDATE Employee SET otherColumn = :dataToEdit WHERE id = :id";
        // Create a mock for the Query interface
        Query nativeQuery = Mockito.mock(Query.class);
        // Create a mock Employee object that represents the expected result after the update
        Employee mockEmployee = new Employee();

        // Mock the behavior of the EntityManager:
        // - When createNativeQuery is called with the specified query, return the mock Query object
        Mockito.when(entityManager.createNativeQuery(query)).thenReturn(nativeQuery);
        // - When findById is called with the specified ID, return an Optional containing the expectedEmployee
        Mockito.when(employeeDao.findById(id)).thenReturn(Optional.of(mockEmployee));

        // When
        // Call the updateEmployee method in the service layer
        Optional<Employee> updatedEmployee = employeeService.updateEmployee(id, column, dataToEdit);

        // Then
        //Checking for the existence of updatedEmployee and ensures that the the service method returned the expected result.
        Assertions.assertNotNull(updatedEmployee);
        Assertions.assertTrue(updatedEmployee.isPresent());
        Assertions.assertSame(mockEmployee, updatedEmployee.get());

        // Verify interactions with entityManager
        Mockito.verify(entityManager, Mockito.times(1)).createNativeQuery(query);
        Mockito.verify(nativeQuery, Mockito.times(1)).setParameter("id", id);
        Mockito.verify(nativeQuery, Mockito.times(1)).setParameter("dataToEdit", dataToEdit);
        Mockito.verify(nativeQuery, Mockito.times(1)).executeUpdate();

        // Verify interactions with employeeDao
        Mockito.verify(employeeDao, Mockito.times(1)).findById(id);
    }

    @Test
    public void testUpdateEmployeeNumberFormatException() {
        // Given
        int id = 1;
        String column = "age";
        String dataToEdit = "invalidNumber";

        // Mock the createNativeQuery method to return a Query mock
        Query nativeQuery = Mockito.mock(Query.class);
        Mockito.when(entityManager.createNativeQuery(Mockito.anyString())).thenReturn(nativeQuery);

        // When and Then
        assertThrows(IllegalArgumentException.class, () -> {
            employeeService.updateEmployee(id, column, dataToEdit);
        });

        // Verify interactions with entityManager
        Mockito.verify(entityManager, Mockito.times(1)).createNativeQuery(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(entityManager);

        // Verify interactions with employeeDao
        Mockito.verifyNoInteractions(employeeDao);
    }

    @Test
    void testDeleteEmployeeWhenEmployeeFound() throws EmptyResultDataAccessException {
        // Arrange
        int id = 1;
        Employee employee = new Employee("John", "Doe", 25, "Developer");

        // Mock the behavior of employeeDao.findById(1)
        Mockito.when(employeeDao.findById(id)).thenReturn(Optional.of(employee));

        // Act
        employeeService.deleteEmployee(id);

        // Assert
        Mockito.verify(employeeDao, Mockito.times(1)).findById(id);
        Mockito.verify(employeeDao, Mockito.times(1)).deleteById(id);
    }

    @Test
    void testDeleteEmployeeWhenEmployeeNotFound() {
        // Arrange
        int id = 2;

        // Mock the behavior of employeeDao.findById(2)
        Mockito.when(employeeDao.findById(id)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(EmptyResultDataAccessException.class, () -> {
            employeeService.deleteEmployee(id);
        });

        // Verify interactions with the mock
        Mockito.verify(employeeDao, Mockito.times(1)).findById(id);
        // No interaction with deleteById since employee was not found
        Mockito.verify(employeeDao, Mockito.times(0)).deleteById(id);
    }
}
