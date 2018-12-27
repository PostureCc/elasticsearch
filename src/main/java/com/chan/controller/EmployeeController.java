package com.chan.controller;

import com.chan.dao.EmployeeDao;
import com.chan.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

@RestController
public class EmployeeController {

    @Autowired
    EmployeeDao employeeRepository;

    /**
     * 添加
     *
     * @return
     */
    @RequestMapping("/add")
    public String add() {
        Employee employee = new Employee();
        employee.setId("1");
        employee.setFirstName("xuxu");
        employee.setLastName("zh");
        employee.setAge(26);
        employee.setAbout("i am in peking");
        employeeRepository.save(employee);
        System.err.println("add a obj");
        return "success";
    }

    /**
     * 删除
     *
     * @return
     */
    @RequestMapping("delete")
    public String delete() {
        Employee employee = employeeRepository.queryEmployeeById("1");
        employeeRepository.delete(employee);
        return "success";
    }

    /**
     * 局部更新
     *
     * @return
     */
    @RequestMapping("update")
    public String update() {
        Employee employee = employeeRepository.queryEmployeeById("1");
        employee.setFirstName("哈哈");
        employeeRepository.save(employee);
        System.err.println("update a obj");
        return "success";
    }

    /**
     * 查询
     *
     * @return
     */
    @RequestMapping("query")
    public Employee query() {
        int count = 1000;
        List<Employee> accountInfo = new ArrayList<>(count);
        final CountDownLatch countDownLatch = new CountDownLatch(count);
        long startTime = System.currentTimeMillis();
        for (int i = 1; i <= count; i++) {
            int finalI1 = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    accountInfo.add(employeeRepository.queryEmployeeById("1"));
                    countDownLatch.countDown();
                }
            }).start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();

        System.err.println(endTime - startTime);
        return accountInfo.get(0);
    }

}
