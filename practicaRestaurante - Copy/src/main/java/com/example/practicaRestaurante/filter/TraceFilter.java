package com.example.practicaRestaurante.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * <b><u>TraceFilter功能说明：</u></b>
 * <p>添加链路调用id</p>
 * @author sofihuang
 * 2024/2/24 21:27
 */
@Component
public class TraceFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 添加traceId
        String traceId = UUID.randomUUID().toString().replace("-", "");
        // 写入MDC，供日志使用
        MDC.put("traceId", traceId);
        filterChain.doFilter(request, response);
    }
}
