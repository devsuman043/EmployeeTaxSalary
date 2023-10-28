package com.employeeSalary.service.Impl;

import com.employeeSalary.entity.Employee;
import com.employeeSalary.repository.EmployeeRepo;
import com.employeeSalary.service.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmpServiceImpl implements EmpService {

    @Autowired
    private EmployeeRepo employeeRepo;

    public void add(Employee employee){
        employeeRepo.save(employee);
    }
}

