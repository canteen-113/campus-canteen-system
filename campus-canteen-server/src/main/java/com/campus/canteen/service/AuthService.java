package com.campus.canteen.service;

import com.campus.canteen.config.JwtUtil;
import com.campus.canteen.dto.ApiResponse;
import com.campus.canteen.dto.LoginRequest;
import com.campus.canteen.dto.LoginResponse;
import com.campus.canteen.entity.Admin;
import com.campus.canteen.entity.User;
import com.campus.canteen.repository.AdminRepository;
import com.campus.canteen.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public ApiResponse<LoginResponse> login(LoginRequest req) {
        // First try to find admin by username
        Optional<Admin> adminOpt = adminRepository.findByUsername(req.getUsername());
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (!passwordEncoder.matches(req.getPassword(), admin.getPassword())) {
                throw new RuntimeException("密码错误");
            }
            if (admin.getStatus() == Admin.Status.DISABLED) {
                throw new RuntimeException("账号已被禁用");
            }
            String token = jwtUtil.generateToken(admin.getUsername(), admin.getRole().name(), admin.getId());
            return ApiResponse.ok(buildAdminResponse(admin, token));
        }

        // Try user by userNo
        Optional<User> userOpt = userRepository.findByUserNo(req.getUsername());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
                throw new RuntimeException("密码错误");
            }
            if (user.getStatus() == User.Status.DISABLED) {
                throw new RuntimeException("账号已被禁用");
            }
            String token = jwtUtil.generateToken(user.getUserNo(), user.getRole().name(), user.getId());
            return ApiResponse.ok(buildUserResponse(user, token));
        }

        throw new RuntimeException("账号不存在");
    }

    public ApiResponse<?> getCurrentUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ApiResponse.error(401, "未登录");
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ApiResponse.error(401, "token已过期");
        }
        String role = jwtUtil.getRole(token);
        Long userId = jwtUtil.getUserId(token);
        if ("STUDENT".equals(role) || "TEACHER".equals(role)) {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) return ApiResponse.error(404, "用户不存在");
            return ApiResponse.ok(buildUserResponse(user, token));
        }
        Admin admin = adminRepository.findById(userId).orElse(null);
        if (admin == null) return ApiResponse.error(404, "管理员不存在");
        return ApiResponse.ok(buildAdminResponse(admin, token));
    }

    private LoginResponse buildAdminResponse(Admin admin, String token) {
        return LoginResponse.builder()
                .token(token)
                .role(admin.getRole().name())
                .name(admin.getName())
                .userId(admin.getId())
                .canteenId(admin.getCanteenId())
                .build();
    }

    private LoginResponse buildUserResponse(User user, String token) {
        return LoginResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .name(user.getName())
                .userId(user.getId())
                .userNo(user.getUserNo())
                .department(user.getDepartment())
                .build();
    }
}
