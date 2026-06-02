package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.entity.Customer;
import com.entity.Evaluation;
import com.entity.Reserveinfo;
import com.mapper.CustomerMapper;
import com.mapper.EvaluationMapper;
import com.mapper.ReserveinfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("Reserve")
public class ReserveinfoController {
    @Autowired
    private ReserveinfoMapper reserveinfoMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private EvaluationMapper evalMapper;

    @GetMapping("/list")
    public List<Reserveinfo> list(){
        List<Reserveinfo> list = reserveinfoMapper.selectList(
                new LambdaQueryWrapper<Reserveinfo>()
                        .last("ORDER BY CASE WHEN State = '已确认' THEN 0 ELSE 1 END, ReservationDate DESC")
        );
        for (Reserveinfo info : list) {
            Customer customer = customerMapper.selectOne(
                    new LambdaQueryWrapper<Customer>()
                            .eq(Customer::getName, info.getCname())
            );
            if (customer != null) {
                info.setPhone(customer.getPhone());
                info.setCardlevel(customer.getCardlevel());
            }
        }
        return list;
    }

    @PostMapping("/cancel")
    public Map<String,Object> cancel(@RequestBody Reserveinfo reserveinfo){
        Reserveinfo existing = reserveinfoMapper.selectOne(
                new LambdaQueryWrapper<Reserveinfo>()
                        .eq(Reserveinfo::getReserveId, reserveinfo.getReserveId())
        );
        if (existing == null) {
            return Map.of(
                    "status", "404",
                    "message", "未找到编号为 " + reserveinfo.getReserveId() + " 的预约记录"
            );
        }
        existing.setState("已取消");
        reserveinfoMapper.updateById(existing);
        return Map.of(
                "status", "200",
                "message", "取消成功"
        );
    }
    @PostMapping("/update")
    public Map<String,Object> update(@RequestBody Reserveinfo reserveinfo){
        System.out.println(reserveinfo);
        Reserveinfo existing = reserveinfoMapper.selectOne(
                new LambdaQueryWrapper<Reserveinfo>()
                        .eq(Reserveinfo::getReserveId,reserveinfo.getReserveId())
        );
        if (existing == null) {
            return Map.of(
                    "status", "404",
                    "message", "未找到编号为 " + reserveinfo.getReserveId() + " 的记录"
            );
        }
        existing.setCheckIn(reserveinfo.getCheckIn());
        existing.setCheckOut(reserveinfo.getCheckOut());
        existing.setRemark(reserveinfo.getRemark());
        reserveinfoMapper.updateById(existing);
        return Map.of(
                "status", "200",
                "message", "更新成功"
        );
    }
    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody Reserveinfo reserveinfo) {
        System.out.println(reserveinfo);

        // 生成 ReserveId：RES + 时间戳
        String reserveId = "RES" + System.currentTimeMillis();
        reserveinfo.setReserveId(reserveId);

        // 设置默认状态
        reserveinfo.setState("已确认");

        // 检查是否已存在相同 ReserveId 的记录（理论上不会重复，但保留校验）
        Reserveinfo existing = reserveinfoMapper.selectOne(
                new LambdaQueryWrapper<Reserveinfo>()
                        .eq(Reserveinfo::getReserveId, reserveinfo.getReserveId())
        );
        if (existing != null) {
            return Map.of(
                    "status", "404",
                    "message", "编号为 " + reserveinfo.getReserveId() + " 的记录已存在"
            );
        }

        reserveinfoMapper.insert(reserveinfo);

        return Map.of(
                "status", "200",
                "message", "新增成功",
                "reserveId", reserveId
        );
    }
    @PostMapping("/myReserve")
    public Map<String, Object> myReserve(@RequestBody Reserveinfo reserveinfo) {
        Map<String, Object> result = new HashMap<>();

        // 先通过手机号查客户姓名
        Customer customer = customerMapper.selectOne(
                new LambdaQueryWrapper<Customer>()
                        .eq(Customer::getPhone, reserveinfo.getPhone())
        );

        if (customer == null) {
            result.put("status", "400");
            result.put("message", "未找到该手机号对应的客户");
            return result;
        }

        // 再用客户姓名查预约记录
        List<Reserveinfo> list = reserveinfoMapper.selectList(
                new LambdaQueryWrapper<Reserveinfo>()
                        .eq(Reserveinfo::getCname, customer.getName())
        );

        if (list == null || list.isEmpty()) {
            result.put("status", "400");
            result.put("message", "暂无预约记录");
            return result;
        }

        // 只提取房型、价格、时间
        List<Map<String, Object>> records = new ArrayList<>();
        for (Reserveinfo item : list) {
            Map<String, Object> record = new HashMap<>();
            record.put("rtype", item.getRoomType());
            record.put("price", item.getPrice());
            record.put("checkIn", item.getCheckIn());
            record.put("checkOut", item.getCheckOut());
            record.put("reserveId", item.getReserveId());
            record.put("state", item.getState());

            // 查询该预约是否已评价
            Evaluation evaluation = evalMapper.selectOne(
                    new LambdaQueryWrapper<Evaluation>()
                            .eq(Evaluation::getCheckid, item.getReserveId())
            );
            record.put("evaluated", evaluation != null);

            records.add(record);
        }

        result.put("status", "200");
        result.put("message", "查询成功");
        result.put("data", records);
        return result;
    }
    @PostMapping("/myReserve2")
    public Map<String, Object> myReserve2(@RequestBody Reserveinfo reserveinfo) {
        Map<String, Object> result = new HashMap<>();

        // 先通过手机号查客户姓名
        Customer customer = customerMapper.selectOne(
                new LambdaQueryWrapper<Customer>()
                        .eq(Customer::getPhone, reserveinfo.getPhone())
        );

        if (customer == null) {
            result.put("status", "400");
            result.put("message", "未找到该手机号对应的客户");
            return result;
        }

        // 再用客户姓名查预约记录
        List<Reserveinfo> list = reserveinfoMapper.selectList(
                new LambdaQueryWrapper<Reserveinfo>()
                        .eq(Reserveinfo::getCname, customer.getName())
                        .eq(Reserveinfo::getState, "已确认")
        );

        if (list == null || list.isEmpty()) {
            result.put("status", "400");
            result.put("message", "暂无预约记录");
            return result;
        }

        // 只提取房型、价格、时间
        List<Map<String, Object>> records = new ArrayList<>();
        for (Reserveinfo item : list) {
            Map<String, Object> record = new HashMap<>();
            record.put("rtype", item.getRoomType());
            record.put("price", item.getPrice());
            record.put("checkIn", item.getCheckIn());
            record.put("checkOut", item.getCheckOut());
            record.put("reserveId", item.getReserveId());
            record.put("state", item.getState());

            // 查询该预约是否已评价
            Evaluation evaluation = evalMapper.selectOne(
                    new LambdaQueryWrapper<Evaluation>()
                            .eq(Evaluation::getCheckid, item.getReserveId())
            );
            record.put("evaluated", evaluation != null);

            records.add(record);
        }

        result.put("status", "200");
        result.put("message", "查询成功");
        result.put("data", records);
        return result;
    }




}
