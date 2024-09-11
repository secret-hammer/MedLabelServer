package com.vipa.medlabel.config.websocketconfig;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.WebSocketHandler;

import com.vipa.medlabel.config.securityconfig.JwtTokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    @SuppressWarnings("null")
    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse,
            WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
        if (serverHttpRequest instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) serverHttpRequest).getServletRequest();

            // 从 URL 查询参数中获取 token
            String token = servletRequest.getParameter("token");

            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                String userInfo = jwtTokenProvider.getUserInfo(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(userInfo);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(servletRequest));

                // 将认证信息放入 SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return true;
            }
        }
        return false; // 如果验证失败，阻止握手
    }

    @SuppressWarnings("null")
    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse,
            WebSocketHandler webSocketHandler, Exception e) {
        // 这个方法是空的，暂时不需要在握手后做任何操作。
    }
}
