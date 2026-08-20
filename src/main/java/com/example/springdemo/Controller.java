package com.example.springdemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
//    @Autowired
//    private Employee employee;
//
//    //setter injection
//    @Autowired
//    public void setEmployee(Employee employee) {
//        this.employee = employee;
//
//    }
//
    // field injection //
//    @Autowired
//    private Employee employee1;

    private final Employee employee;

    @Autowired
    public Controller(Employee employee) {
        this.employee = employee;
    }

    @GetMapping("/employee")
      public Employee getEmployee()
    {
//       employee1.setId(1l);
//       employee = employee1;
        return employee;
    }
}
