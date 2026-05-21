package com.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.entity.Inventory;
import com.mapper.InventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Inventory")
public class InventoryController {
    @Autowired
    private InventoryMapper inventoryMapper;

    @GetMapping("/list")
    public List<Inventory> list() {
        return inventoryMapper.selectList(null);
    }
    @PostMapping("/update")
    public Map<String,Object> update(@RequestBody Inventory inventory){
        Inventory existing = inventoryMapper.selectOne(
                new LambdaQueryWrapper<Inventory>()
                        .eq(Inventory::getNum, inventory.getNum())
        );
         if (existing == null) {
        return Map.of(
                "status", "404",
                "message", "未找到编号为 " + inventory.getNum() + " 的库存记录"
        );
    }
         existing.setQuantity(inventory.getQuantity());
         existing.setUnit(inventory.getUnit());
         existing.setName(inventory.getName());
         existing.setType(inventory.getType());
        inventoryMapper.updateById(existing);
        return Map.of(
                "status", "200",
                "message", "更新成功"
        );
    }
    @PostMapping("/add")
    public Map<String,Object> add(@RequestBody Inventory inventory){
         Inventory existing = inventoryMapper.selectOne(
                new LambdaQueryWrapper<Inventory>()
                        .eq(Inventory::getName, inventory.getName())
        );
        if (existing != null) {
            return Map.of(
                    "status", "400",
                    "message", "名称已存在"
            );
        }
        inventory.setId(null);
        inventory.setNum("INV" + System.currentTimeMillis() / 1000);
        inventoryMapper.insert(inventory);
        return Map.of(
                "status", "200",
                "message", "添加成功"
        );
    }
    @PostMapping("/del")
    public Map<String,Object> delete(@RequestBody Inventory inventory){
        Inventory existing = inventoryMapper.selectOne(
                new LambdaQueryWrapper<Inventory>()
                        .eq(Inventory::getNum, inventory.getNum())
        );
        if (existing == null) {
            return Map.of(
                    "status", "404",
                    "message", "未找到编号为 " + inventory.getNum() + " 的库存记录"
            );
        }
        inventoryMapper.deleteById(existing.getId());
        return Map.of(
                "status", "200",
                "message", "删除成功"
        );
    }
}
