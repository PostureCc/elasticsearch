package com.chan.dao;

import com.chan.model.Employee;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EmployeeDao extends ElasticsearchRepository<Employee, String> {
    /**
     * 查询雇员信息
     */
    Employee queryEmployeeById(String id);

}
