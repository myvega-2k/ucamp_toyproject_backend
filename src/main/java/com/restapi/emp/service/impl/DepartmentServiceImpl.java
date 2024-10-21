package com.restapi.emp.service.impl;

import com.restapi.emp.dto.DepartmentDto;
import com.restapi.emp.dto.mapper.DepartmentMapper;
import com.restapi.emp.entity.Department;
import com.restapi.emp.exception.ResourceNotFoundException;
import com.restapi.emp.repository.DepartmentRepository;
import com.restapi.emp.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;

    @Override
    public DepartmentDto createDepartment(DepartmentDto departmentDto) {
        //DTO => Entity 로 변환
        Department department = DepartmentMapper.mapToDepartment(departmentDto);
        //DB에 저장
        Department savedDepartment = departmentRepository.save(department);
        //Entity => DTO 로 변환
        return DepartmentMapper.mapToDepartmentDto(savedDepartment);
    }

    @Transactional(readOnly = true)
    @Override
    public DepartmentDto getDepartmentById(Long departmentId) {
        return departmentRepository.findById(departmentId)
                //.map(entity -> DepartmentMapper.mapToDepartmentDto(entity))
                .map(DepartmentMapper::mapToDepartmentDto)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Department is not exists with a given id: " + departmentId)
        );
        //return DepartmentMapper.mapToDepartmentDto(department);
    }

    @Transactional(readOnly = true)
    @Override
    public List<DepartmentDto> getAllDepartments() {
        //List<Department> => Stream<Department> => Stream<DepartmentDto> => List<DepartmentDto>
        List<Department> departments = departmentRepository.findAll();
        return departments.stream()
                .map(DepartmentMapper::mapToDepartmentDto)
                .toList();
                //.map((department) -> DepartmentMapper.mapToDepartmentDto(department))
                //.collect(Collectors.toList());
    }

    @Override
    public DepartmentDto updateDepartment(Long departmentId, DepartmentDto updatedDepartment) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department is not exists with a given id:"+ departmentId,
                                HttpStatus.NOT_FOUND)
        );
        //setter method - Dirty Checking
        department.setDepartmentName(updatedDepartment.getDepartmentName());
        department.setDepartmentDescription(updatedDepartment.getDepartmentDescription());

        //Department savedDepartment = departmentRepository.save(department);
        return DepartmentMapper.mapToDepartmentDto(department);
    }


    @Override
    public void deleteDepartment(Long departmentId) {
        departmentRepository.findById(departmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department is not exists with a given id: " + departmentId,
                                HttpStatus.NOT_FOUND)
        );
        departmentRepository.deleteById(departmentId);
    }
}