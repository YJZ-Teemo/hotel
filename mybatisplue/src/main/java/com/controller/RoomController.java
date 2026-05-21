package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.entity.Room;
import com.mapper.RoomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Room")
public class RoomController {
    @Autowired
    private RoomMapper roomMapper;

    @GetMapping("/list")
    public List<Room> getlist() {
        return roomMapper.selectList(null);
    }
    @GetMapping("/gettypelist")
    public List<Room> gettypelist() {
        return roomMapper.selectList(
                new QueryWrapper<Room>()
                        .select("type", "people", "area", "details","price")
                        .inSql("id", "SELECT MAX(id) FROM room GROUP BY type")
        );
    }

    @GetMapping("/gettypecount")
    public List<Map<String, Object>> gettypecount() {
        return roomMapper.selectMaps(
                new QueryWrapper<Room>()
                        .select(
                                "type",
                                "SUM(CASE WHEN status = '可入住' THEN 1 ELSE 0 END) AS available_count",
                                "SUM(CASE WHEN status = '待收拾' THEN 1 ELSE 0 END) AS clean_count"
                        )
                        .groupBy("type")
        );
    }

    @PostMapping("/add")
    public Map<String,Object> add(@RequestBody Room room){
        Room existing = roomMapper.selectOne(
                new LambdaQueryWrapper<Room>()
                        .eq(Room::getNum, room.getNum())
        );
        if(existing!=null){
            return Map.of(
                    "status", "400",
                    "message", "房间已存在"
            );
        }
        room.setId(null);
        room.setStatus("待收拾");
        roomMapper.insert(room);
        return Map.of(
                "status", "200",
                "message", "添加成功"
        );
    }
    @PostMapping("/del")
    public Map<String,Object> del(@RequestBody Room room){
        Room existing = roomMapper.selectOne(
                new LambdaQueryWrapper<Room>()
                        .eq(Room::getNum, room.getNum())
        );
        if(existing==null){
            return Map.of(
                    "status", "400",
                    "message", "房间不存在"
            );
        }

        roomMapper.deleteById(existing.getId());
        return Map.of(
                "status", "200",
                "message", "删除成功"
        );

    }
}
