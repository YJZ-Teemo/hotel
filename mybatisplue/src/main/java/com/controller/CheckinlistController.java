package com.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.entity.*;
import com.mapper.CheckinlistMapper;
import com.mapper.EvaluationMapper;
import com.mapper.ReserveinfoMapper;
import com.mapper.RoomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/check")
public class CheckinlistController {
    @Autowired
    private CheckinlistMapper checkinMapper;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private ReserveinfoMapper reserveinfoMapper;
    @Autowired
    private EvaluationMapper evalMapper;

     @GetMapping("/list")
    public List<Checkinlist> list() {
        return checkinMapper.selectList(
                new LambdaQueryWrapper<Checkinlist>()
                        .last("ORDER BY FIELD(status, 'checkedIn') DESC, checkindate DESC")
        );
    }

    @PostMapping("/in")
    public Map<String,Object> checkinlist(@RequestBody Checkinlist checkin)
    {
        Map<String, Object> result = new HashMap<>();
        if (checkin.getCheckoutdate().isBefore(checkin.getCheckindate())) {
            result.put("status", "400");
            result.put("message", "离店日期不能早于入住日期");
            return result;
        }
        Room existing = roomMapper.selectOne(
                new LambdaQueryWrapper<Room>()
                        .eq(Room::getNum, checkin.getRnumber())
        );
        Reserveinfo existing2 = reserveinfoMapper.selectOne(
                new LambdaQueryWrapper<Reserveinfo>()
                        .eq(Reserveinfo::getReserveId, checkin.getRid())
        );

        if (existing == null) {
            result.put("status", "400");
            result.put("message", "房间不存在");
            return result;
        }
        // 3. 房间不可入住
        if (!"可入住".equals(existing.getStatus())) {
            result.put("status", "400");
            result.put("message", "该房间当前不可入住，状态：" + existing.getStatus());
            return result;
        }


        String checkinNum = "CHK" + System.currentTimeMillis() / 1000;
        checkin.setCheckinNum(checkinNum);
        checkin.setStatus("checkedIn");

        checkinMapper.insert(checkin);

        existing.setStatus("已入住");
        roomMapper.updateById(existing);

        existing2.setState("completed");
        reserveinfoMapper.updateById(existing2);

        result.put("status", "200");
        result.put("message", "入住成功");
        return result;
    }
    @PostMapping("/out")
    public Map<String,Object> checkoutlist(@RequestBody Checkinlist checkout)
    {
        Map<String, Object> result = new HashMap<>();
        Checkinlist existingCheckin = checkinMapper.selectOne(
                new LambdaQueryWrapper<Checkinlist>()
                        .eq(Checkinlist::getCheckinNum, checkout.getCheckinNum())
                        .eq(Checkinlist::getRnumber, checkout.getRnumber())
        );
        if (existingCheckin == null) {
            result.put("status", "400");
            result.put("message", "入住记录不存在");
            return result;
        }
        existingCheckin.setStatus("checkedOut");
        checkinMapper.updateById(existingCheckin);

        Room existingRoom = roomMapper.selectOne(
                new LambdaQueryWrapper<Room>()
                        .eq(Room::getNum, checkout.getRnumber())
        );
        if ("已入住".equals(existingRoom.getStatus())) {
            existingRoom.setStatus("待收拾");
            roomMapper.updateById(existingRoom);
        }else{
            result.put("status", "400");
            result.put("message", "该房间当前不可入住，状态：" + existingRoom.getStatus());
            return result;
        }

        result.put("status", "200");
        result.put("message", "退房成功");
        return result;
    }
    @PostMapping("/history")
    public Map<String, Object> history(@RequestBody Checkinlist checkinlist) {
        Map<String, Object> result = new HashMap<>();

        List<Checkinlist> list = checkinMapper.selectList(
                new LambdaQueryWrapper<Checkinlist>()
                        .eq(Checkinlist::getCphone, checkinlist.getCphone())
                        .eq(Checkinlist::getStatus, "checkedOut")
        );

        if (list == null || list.isEmpty()) {
            result.put("status", "400");
            result.put("message", "暂无退房记录");
            return result;
        }

        List<Map<String, Object>> records = new ArrayList<>();
        for (Checkinlist item : list) {
            Map<String, Object> record = new HashMap<>();
            record.put("rtype", item.getRtype());
            record.put("totalamount", item.getTotalamount());
            record.put("checkindate", item.getCheckindate());
            record.put("checkoutdate", item.getCheckoutdate());
            record.put("checkinNum", item.getCheckinNum());

            // 查询该订单是否已评价
            Evaluation evaluation = evalMapper.selectOne(
                    new LambdaQueryWrapper<Evaluation>()
                            .eq(Evaluation::getCheckid, item.getCheckinNum())
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
