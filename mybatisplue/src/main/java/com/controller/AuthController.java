package com.controller;

import com.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JWTUtil jwtUtil;

    @PostMapping("/refresh")
    public Map<String, Object> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");

        if (refreshToken == null || refreshToken.isEmpty()) {
            return Map.of("status", "400", "message", "refreshToken 不能为空");
        }

        if (!jwtUtil.validateToken(refreshToken)) {
            return Map.of("status", "401", "message", "refreshToken 无效或已过期");
        }

        if (!"refresh".equals(jwtUtil.getTokenType(refreshToken))) {
            return Map.of("status", "401", "message", "token 类型错误，需要 refreshToken");
        }

        String userId = jwtUtil.getUserId(refreshToken);
        String role = jwtUtil.getRole(refreshToken);
        String accessToken = jwtUtil.generateAccessToken(userId, role);

        return Map.of(
                "status", "200",
                "message", "刷新成功",
                "accessToken", accessToken
        );
    }
}
