package com.ruoyi.wms.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * 客户端 IP 过滤器：
 * 前端在请求头 X-Client-IP 中上报本机 IP，这里把 remoteAddr 替换为该值，
 * 使操作日志（操作地址）与登录日志（登录 IP）记录到真实客户端 IP。
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ClientIpFilter implements Filter {

    public static final String CLIENT_IP_HEADER = "X-Client-IP";

    private static final Pattern IP_PATTERN = Pattern.compile("^[0-9a-fA-F:.]{3,45}$");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest) {
            String clientIp = httpRequest.getHeader(CLIENT_IP_HEADER);
            if (clientIp != null && IP_PATTERN.matcher(clientIp).matches()) {
                chain.doFilter(new ClientIpRequestWrapper(httpRequest, clientIp), response);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private static class ClientIpRequestWrapper extends HttpServletRequestWrapper {

        private final String clientIp;

        ClientIpRequestWrapper(HttpServletRequest request, String clientIp) {
            super(request);
            this.clientIp = clientIp;
        }

        @Override
        public String getRemoteAddr() {
            return clientIp;
        }

        @Override
        public String getRemoteHost() {
            return clientIp;
        }
    }
}
