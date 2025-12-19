package com.chiho.bitvision.config;

import com.chiho.bitvision.entity.user.User;
import com.chiho.bitvision.holder.UserHolder;
import com.chiho.bitvision.service.user.UserService;
import com.chiho.bitvision.util.JwtUtils;
import com.chiho.bitvision.util.R;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 全局Handler请求拦截器
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final UserService userService;

    public AdminInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }
        if (!JwtUtils.checkToken(request)) {
            response(R.error().message("请登录后再操作"),response);
            return false;
        }

        final Long userId = JwtUtils.getUserId(request);
        final User user = userService.getById(userId);
        if (ObjectUtils.isEmpty(user)){
            response(R.error().message("用户不存在"),response);
            return false;
        }

        if (userId != null) {
            UserHolder.set(userId);
        }
        return true;
    }


    private boolean response(R r, HttpServletResponse response) throws IOException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().println(objectMapper.writeValueAsString(r));
        response.getWriter().flush();
        return false;
    }
}
