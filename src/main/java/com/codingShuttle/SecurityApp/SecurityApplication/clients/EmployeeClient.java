package com.codingShuttle.SecurityApp.SecurityApplication.clients;


import com.codingShuttle.SecurityApp.SecurityApplication.dto.EmployeeDTO;

import java.util.List;

public interface EmployeeClient {

    List<EmployeeDTO> getAllEmployees();

    EmployeeDTO getEmployeeById(Long employeeId);

    EmployeeDTO createNewEmployee(EmployeeDTO employeeDTO);
}
