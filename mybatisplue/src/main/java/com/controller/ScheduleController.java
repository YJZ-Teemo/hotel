package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.entity.Schedule;
import com.entity.Room;
import com.mapper.ScheduleMapper;
import com.mapper.RoomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cleaning")
public class ScheduleController {

    @Autowired
    private ScheduleMapper scheduleMapper;
    @Autowired
    private RoomMapper roomMapper;

    @GetMapping("/list")
    public List<Schedule> list() {
        return scheduleMapper.selectList(
                new LambdaQueryWrapper<Schedule>()
                        .orderByDesc(Schedule::getStatus)
                        .orderByDesc(Schedule::getUrgent)
        );
    }

    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody Schedule schedule) {
        scheduleMapper.insert(schedule);
        Map<String, Object> res = new HashMap<>();
        res.put("status", 200);
        res.put("message", "添加成功");
        return res;
    }

    @PostMapping("/completed")
    public Map<String, Object> update(@RequestBody Schedule schedule) {
        Schedule existing = scheduleMapper.selectById(schedule.getId());
        if (existing != null) {
            existing.setStatus("已完成");
            existing.setCompletionTime(LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            scheduleMapper.updateById(existing);
            Map<String, Object> res = new HashMap<>();
            res.put("status", 200);
            res.put("message", "更新成功");
            return res;
        } else {
            Map<String, Object> res = new HashMap<>();
            res.put("status", 404);
            res.put("message", "任务未找到");
            return res;
        }
    }
    @PostMapping("/start")
    public Map<String, Object> start(@RequestBody Schedule schedule) {
        Schedule existing = scheduleMapper.selectById(schedule.getId());
        if (existing != null) {
            existing.setAssignee(schedule.getAssignee());
            existing.setAssignTime(LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            existing.setStatus("打扫中");
            scheduleMapper.updateById(existing);
            Map<String, Object> res = new HashMap<>();
            res.put("status", 200);
            res.put("message", "任务已开始");
            return res;
        } else {
            Map<String, Object> res = new HashMap<>();
            res.put("status", 404);
            res.put("message", "任务未找到");
            return res;
        }
    }
    @PostMapping("/over")
    public Map<String, Object> over(@RequestBody Schedule schedule) {
        Schedule existing = scheduleMapper.selectById(schedule.getId());
        if (existing != null) {

            existing.setStatus("已完成");
            existing.setCompletionTime(LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            scheduleMapper.updateById(existing);

            Room room = roomMapper.selectOne(
                    new LambdaQueryWrapper<Room>()
                            .eq(Room::getNum, existing.getRNumber())
            );
            if (room != null) {
                room.setStatus("可入住");
                roomMapper.updateById(room);
            }
            

            Map<String, Object> res = new HashMap<>();
            res.put("status", 200);
            res.put("message", "任务已完成");
            return res;
        } else {
            Map<String, Object> res = new HashMap<>();
            res.put("status", 404);
            res.put("message", "任务未找到");
            return res;
        }
    }

//    @PostMapping("/delete")
//    public Map<String, Object> delete(@RequestBody Schedule schedule) {
//        scheduleMapper.deleteById(schedule.getId());
//        Map<String, Object> res = new HashMap<>();
//        res.put("status", 200);
//        res.put("message", "删除成功");
//        return res;
//    }

    @GetMapping("/myCleaning")
    public List<Schedule> myCleaning(@RequestParam String assignee) {
        return scheduleMapper.selectList(
                new LambdaQueryWrapper<Schedule>()
                        .eq(Schedule::getAssignee, assignee)
                        .eq(Schedule::getStatus, "打扫中")
        );
    }
}
