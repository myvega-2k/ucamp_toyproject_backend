package com.restapi.emp.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDto {
    private Long id;

    @NotEmpty(message = "departmentName은 필수입력 항목입니다!")
    private String departmentName;

    @NotEmpty(message = "departmentDescription은 필수입력 항목입니다!")
    private String departmentDescription;
}