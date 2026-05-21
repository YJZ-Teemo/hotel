package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.entity.Evaluation;
import com.mapper.EvaluationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Evaluation")
public class EvaluationController {
    @Autowired
    private EvaluationMapper evalMapper;
    @GetMapping("/list")
    public List<Evaluation> getEvaluation() {
        return evalMapper.selectList(null);
    }
    @PostMapping("/add")
    public Map<String, Object> addEvaluation(@RequestBody Evaluation evaluation) {
        Map<String, Object> result = new HashMap<>();

        if (evaluation.getCheckid() == null || evaluation.getStar() == null) {
            result.put("status", "400");
            result.put("message", "订单编号和评分不能为空");
            return result;
        }

        // 检查是否已评价过
        Evaluation existing = evalMapper.selectOne(
                new LambdaQueryWrapper<Evaluation>()
                        .eq(Evaluation::getCheckid, evaluation.getCheckid())
        );
        if (existing != null) {
            result.put("status", "400");
            result.put("message", "该订单已评价，不能重复评价");
            return result;
        }

        evalMapper.insert(evaluation);

        result.put("status", "200");
        result.put("message", "评价成功");
        return result;
    }


}
