package com.restapi.emp.controller;

import com.restapi.emp.dto.DepartmentDto;
import com.restapi.emp.dto.EmployeeDto;
import com.restapi.emp.security.model.CurrentUser;
import com.restapi.emp.security.model.UserInfo;
import com.restapi.emp.service.DepartmentService;
import com.restapi.emp.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    //인증 없이 접근 가능한 메서드
    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secure";
    }

    // Build Add Employee REST API
    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@RequestBody EmployeeDto employeeDto){
        EmployeeDto savedEmployee = employeeService.createEmployee(employeeDto);
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }

    // Build Get Employee REST API
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable("id") Long employeeId){
        EmployeeDto employeeDto = employeeService.getEmployeeById(employeeId);
        return ResponseEntity.ok(employeeDto);
    }

    // Build Get All Employees REST API
    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAllEmployees(){
        List<EmployeeDto> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);

    }

    @GetMapping("/departments")
    public ResponseEntity<List<EmployeeDto>> getAllEmployeesDepartment() {
        return ResponseEntity.ok(employeeService.getAllEmployeesDepartment());
    }

    @GetMapping("/departmentNames")
    public ResponseEntity<List<DepartmentDto>> getAllDepartmentNames() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    // Build Update Employee REST API
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable("id") Long employeeId,
                                                      @RequestBody EmployeeDto updatedEmployee,
                                                      @CurrentUser UserInfo currentUser){
        EmployeeDto employeeDto = null;
        if (currentUser != null) {
            System.out.println(">>>> Current User + " +  currentUser.getEmail());
            employeeDto = employeeService.updateEmployee(employeeId, updatedEmployee);
        }
        return ResponseEntity.ok(employeeDto);
    }

    // Build Delete Employee REST API
    @DeleteMapping("/{id}")
    //@PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<String> deleteEmployee(@PathVariable("id") Long employeeId,
                                                 @CurrentUser UserInfo currentUser){
        if (currentUser != null) {
            employeeService.deleteEmployee(employeeId);
        }
        return ResponseEntity.ok("Employee deleted successfully!.");
    }
}
