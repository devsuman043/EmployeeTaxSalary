package com.employeeSalary.controller;

import com.employeeSalary.entity.Employee;
import com.employeeSalary.entity.TaxInfo;
import com.employeeSalary.repository.EmployeeRepo;
import com.employeeSalary.service.Impl.EmpServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/tax")
public class TaxController {
    @Autowired
    private EmpServiceImpl empSer;
    @Autowired
    private EmployeeRepo employeeRepo;

    @GetMapping("/deduction")
    public ResponseEntity<TaxInfo> calculateTaxDeduction(
            @RequestParam int employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate doj,
            @RequestParam double salary) {


        int startingMonth = (doj.getMonthValue() >= Month.APRIL.getValue()) ? Month.APRIL.getValue() : Month.APRIL.getValue() - 12;
        int currentMonth = LocalDate.now().getMonthValue();
        int taxableMonths = (currentMonth - startingMonth) + 1;

        double yearlySalary = salary * taxableMonths;


        double taxAmount = calculateTaxAmount(yearlySalary);


        double cessAmount = (yearlySalary > 2500000) ? yearlySalary * 0.02 : 0;
        Employee e=employeeRepo.findById(employeeId).get();

        TaxInfo taxInfo = new TaxInfo();
        taxInfo.setFirstName(e.getFirstName());
        taxInfo.setLastName(e.getLastName());
        taxInfo.setEmployeeCode(e.getEmployeeId());
        taxInfo.setYearlySalary(yearlySalary);
        taxInfo.setTaxAmount(taxAmount);
        taxInfo.setCessAmount(cessAmount);

        return ResponseEntity.ok(taxInfo);
    }

    private double calculateTaxAmount(double yearlySalary) {
        if (yearlySalary <= 250000) {
            return 0;
        } else if (yearlySalary <= 500000) {
            return (yearlySalary - 250000) * 0.05;
        } else if (yearlySalary <= 1000000) {
            return 12500 + (yearlySalary - 500000) * 0.10;
        } else {
            return 12500 + 50000 + (yearlySalary - 1000000) * 0.20;
        }
    }
    @PostMapping("/save")
    public ResponseEntity<?> saveEmployee(@Valid @RequestBody Employee employee, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = new ArrayList<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.add(error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }

        empSer.add(employee);
        return ResponseEntity.ok("Employee saved successfully");
    }
}
