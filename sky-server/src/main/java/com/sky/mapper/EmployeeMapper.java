package com.sky.mapper;

import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    void insert(Employee employee);

    List<Employee> find(EmployeePageQueryDTO employeePageQueryDTO);

    void update(Employee employee);

    @Select("select id, name, username, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user from employee where id = #{id}")
    Employee getById(Long id);
}
