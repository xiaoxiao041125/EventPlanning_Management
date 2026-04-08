package edu.wtbu.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

/**
 * 编码过滤器 - 统一设置请求和响应的编码为UTF-8
 */
@WebFilter("/*")
public class EncodingFilter implements Filter {

    private String encoding = "utf-8";

    public EncodingFilter() {
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // 设置请求编码
        if (request.getCharacterEncoding() == null) {
            request.setCharacterEncoding(encoding);
        }
        // 设置响应编码
        response.setCharacterEncoding(encoding);

        chain.doFilter(request, response);
    }

    public void init(FilterConfig fConfig) throws ServletException {
        // 可以从配置文件中读取编码设置
        String configEncoding = fConfig.getInitParameter("encoding");
        if (configEncoding != null && !configEncoding.trim().isEmpty()) {
            this.encoding = configEncoding;
        }
    }
}