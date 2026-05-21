package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.entity.Customer;
import com.entity.Employeeaccount;
import com.mapper.EmployeeaccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Employeeaccount")
public class EmployeeaccountController {
    @Autowired
    private EmployeeaccountMapper empMapper;

    @GetMapping("/list")
    public List<Employeeaccount> getlist() {
        return empMapper.selectList(
                new LambdaQueryWrapper<Employeeaccount>()
                        .select(Employeeaccount::getId,
                                Employeeaccount::getUsername,
                                Employeeaccount::getManagement,
                                Employeeaccount::getPhone,
                                Employeeaccount::getName
                        )
        );
    }

    @PostMapping("/register")
    public Map<String,Object> register(@RequestBody Employeeaccount emp){

        Long existing = empMapper.selectCount(
                new LambdaQueryWrapper<Employeeaccount>()
                        .eq(Employeeaccount::getUsername, emp.getUsername())
        );

        if (existing>0) {
            return Map.of(
                    "status", "400",
                    "message", "用户名已存在"
            );
        }
        // 获取输入密码
        String ipPassword = emp.getPassword();

        // 加密密码
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encryptedPassword = encoder.encode(ipPassword);

        // 存储到数据库
        emp.setManagement(emp.getManagement());
        emp.setName(emp.getName());
        emp.setUsername(emp.getUsername());
        emp.setPhone(emp.getPhone());
        emp.setPassword(encryptedPassword);
        empMapper.insert(emp);

        // 数据库中存储的是加密后的密码，不是明文！
        return Map.of(
                "status", "200",
                "message", "注册成功"
        );

    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Employeeaccount emp){

        //从数据库查询用户
        Employeeaccount existing = empMapper.selectOne(
                new LambdaQueryWrapper<Employeeaccount>()
                        .eq(Employeeaccount::getUsername, emp.getUsername())
        );
        if (existing == null) {
            return Map.of("status", "401", "message", "用户不存在");
        }

        // 验证密码
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean isMatch = encoder.matches(emp.getPassword(), existing.getPassword());

        if (isMatch) {
            return Map.of(
                    "status", "200",
                    "message", "登录成功",
                    "Username", existing.getName(),
                    "Management",existing.getManagement()
            );
        } else {
            return Map.of("status", "402", "message", "密码错误");
        }

    }
    @PostMapping("/update")
    public Map<String,Object> update(@RequestBody Employeeaccount emp){
        Map<String,Object> result = new HashMap<>();
        Employeeaccount existing = empMapper.selectOne(
                new LambdaQueryWrapper<Employeeaccount>()
                        .eq(Employeeaccount::getId, emp.getId())
        );
        if (existing == null) {
            result.put("status", "400");
            result.put("message", "信息不存在");
            return result;
        }
        existing.setPhone(emp.getPhone());
        existing.setManagement(emp.getManagement());
        existing.setName(emp.getName());
        empMapper.updateById(existing);
        result.put("status", "200");
        result.put("message", "更改成功");
        return result;

    }
    @PostMapping("/del")
    public Map<String,Object> del(@RequestBody Employeeaccount emp){
        Map<String,Object> result = new HashMap<>();
        Employeeaccount existing = empMapper.selectOne(
                new LambdaQueryWrapper<Employeeaccount>()
                        .eq(Employeeaccount::getId, emp.getId())
        );
        if (existing == null) {
            result.put("status", "400");
            result.put("message", "信息不存在");
            return result;
        }
        empMapper.deleteById(emp.getId());
        result.put("status", "200");
        result.put("message", "删除成功");
        return result;
    }



}
