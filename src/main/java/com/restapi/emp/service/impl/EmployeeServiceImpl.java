package com.restapi.emp.service.impl;

import com.restapi.emp.dto.EmployeeDto;
import com.restapi.emp.dto.mapper.EmployeeMapper;
import com.restapi.emp.entity.Department;
import com.restapi.emp.entity.Employee;
import com.restapi.emp.exception.ResourceNotFoundException;
import com.restapi.emp.repository.DepartmentRepository;
import com.restapi.emp.repository.EmployeeRepository;
import com.restapi.emp.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {
        //DTO => Entity 로 변환    
        Employee employee = EmployeeMapper.mapToEmployee(employeeDto);
        
        //EmployeeDto에 포함된 departmentId로 department 객체가 있는지를 먼저 체크함 
        Department department = departmentRepository.findById(employeeDto.getDepartmentId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Department is not exists with id: " +
                                employeeDto.getDepartmentId(),
                                HttpStatus.NOT_FOUND));
        //Employee 와 Department 연결
        employee.setDepartment(department);
        
        //Employee 객체를 DB에 저장
        Employee savedEmployee = employeeRepository.save(employee);
        //Entity => Dto 로 변환
        return EmployeeMapper.mapToEmployeeDto(savedEmployee);
    }

    @Transactional(readOnly = true)
    @Override
    public EmployeeDto getEmployeeById(Long employeeId) {
        return employeeRepository.findById(employeeId)
                //.map(employee -> EmployeeMapper.mapToEmployeeDto(employee))
                .map(EmployeeMapper::mapToEmployeeDto)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee is not exists with given id : " + employeeId,
                                HttpStatus.NOT_FOUND));
        //return EmployeeMapper.mapToEmployeeDto(employee);
    }

    @Transactional(readOnly = true)
    @Override
    //List<Employee> => Stream<Employee> => Stream<EmployeeDto> => List<EmployeeDto>
    public List<EmployeeDto> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream()
                .map(EmployeeMapper::mapToEmployeeDto)
                .toList();
                //.map((employee) -> EmployeeMapper.mapToEmployeeDto(employee))
                //.collect(Collectors.toList());
    }

    //Employee 정보와 Department 정보를 같이 조회하는 메서드
    @Transactional(readOnly = true)
    public List<EmployeeDto> getAllEmployeesDepartment() {
        return employeeRepository.findAll()
                .stream()
                .map(EmployeeMapper::mapToEmployeeDepartmentDto)
                .toList();
    }

    @Override
    public EmployeeDto updateEmployee(Long employeeId, EmployeeDto updatedEmployee) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee is not exists with given id: " + employeeId,
                                HttpStatus.NOT_FOUND)
        );

        //Employee 정보를 수정
        employee.setFirstName(updatedEmployee.getFirstName());
        employee.setLastName(updatedEmployee.getLastName());
        employee.setEmail(updatedEmployee.getEmail());

        Department department = departmentRepository.findById(updatedEmployee.getDepartmentId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department is not exists with id: " + updatedEmployee.getDepartmentId(),
                                HttpStatus.NOT_FOUND
                                ));
        //Employee가 참조하는 Department 정보도 수정
        employee.setDepartment(department);

        //Employee updatedEmployeeObj = employeeRepository.save(employee);

        return EmployeeMapper.mapToEmployeeDto(employee);
    }

    @Override
    public void deleteEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee is not exists with given id: " + employeeId,
                        HttpStatus.NOT_FOUND)
        );

        employeeRepository.deleteById(employeeId);
    }
}