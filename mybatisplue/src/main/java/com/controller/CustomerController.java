package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.entity.Customer;
import com.mapper.CustomerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Customer")
public class CustomerController {

    @Autowired
    private CustomerMapper customerMapper;

    @GetMapping("/list")
    public List<Customer> getlist() {
        return customerMapper.selectList(
                new LambdaQueryWrapper<Customer>()
                        .select(Customer::getId,
                                Customer::getName,
                                Customer::getPhone,
                                Customer::getCardlevel,
                                Customer::getPoints
                                )
        );
    }
    @PostMapping("/update")
    public Map<String,Object> update(@RequestBody Customer customer){
        Map<String,Object> result = new HashMap<>();
        Customer existing =  customerMapper.selectOne(
                new LambdaQueryWrapper<Customer>()
                        .eq(Customer::getId,customer.getId())
        );
        if (existing == null) {
            result.put("status", "400");
            result.put("message", "会员信息不存在");
            return result;
        }
        System.out.println(customer.getPoints()+","+existing.getPoints());
        int Points = 0;
        if (existing.getPoints()==null || existing.getPoints().isEmpty()){
            Points = 0;
        }else{
            Points = Integer.parseInt(existing.getPoints());
        }
        int sum = Integer.parseInt(customer.getPoints()) + Points;
        existing.setPoints(String.valueOf(sum));
        customerMapper.updateById(existing);
        result.put("status", "200");
        result.put("message", "赠送成功");
        return result;
    }
    @PostMapping("/register")
    public Map<String, Object> customerRegister(@RequestBody Customer customer) {

        // 用 phone 查重
        Long existing = customerMapper.selectCount(
                new LambdaQueryWrapper<Customer>()
                        .eq(Customer::getPhone, customer.getPhone())
        );

        if (existing > 0) {
            return Map.of(
                    "status", "400",
                    "message", "该手机号已注册"
            );
        }

        // 加密密码
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encryptedPassword = encoder.encode(customer.getPassword());
        customer.setPassword(encryptedPassword);

        customer.setCardlevel("星会员");
        customer.setPoints("100");

        customerMapper.insert(customer);

        return Map.of(
                "status", "200",
                "message", "注册成功"
        );
    }

    @PostMapping("/login")
    public Map<String, Object> customerLogin(@RequestBody Customer customer) {
        Map<String, Object> result = new HashMap<>();
        // 用 phone 查找用户
        Customer existing = customerMapper.selectOne(
                new LambdaQueryWrapper<Customer>()
                        .eq(Customer::getPhone, customer.getPhone())
        );

        if (existing == null) {
            return Map.of(
                    "status", "401",
                    "message", "用户不存在"
            );
        }

        // 验证密码
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean isMatch = encoder.matches(customer.getPassword(), existing.getPassword());

        if (isMatch) {
            result.put("status", "200");
            result.put("message", "登录成功");
            result.put("Name", existing.getName());
            result.put("Phone", existing.getPhone());
            result.put("Cardlevel", existing.getCardlevel());
            result.put("Points", existing.getPoints());
            result.put("Hobby", existing.getHobby());
            result.put("time", existing.getTime());
            return result;
        } else {
            return Map.of(
                    "status", "402",
                    "message", "密码错误"
            );
        }
    }


}

