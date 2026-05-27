package com.campus.canteen.config;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) { this.jwtUtil = jwtUtil; }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        // 放行登录接口和静态资源
        if (path.contains("/api/auth/login") || path.contains("/api/public/") || path.startsWith("/static/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // OPTIONS 预检请求放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtUtil.validateToken(token)) {
                request.setAttribute("userId", jwtUtil.getUserId(token));
                request.setAttribute("role", jwtUtil.getRole(token));
                request.setAttribute("username", jwtUtil.getUsername(token));
                filterChain.doFilter(request, response);
                return;
            }
        }

        // 开发阶段：无 token 也放行（方便直接调试前端页面）
        // 生产环境应改为返回 401
        filterChain.doFilter(request, response);
    }
}
