//package tz.go.mof.trab.config;
//
//
//import org.springframework.context.annotation.Configuration;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//
//@Configuration
//public class CorsConfig implements Filter {
//
//    @Override
//    public void doFilter(
//            ServletRequest servletRequest,
//            ServletResponse servletResponse,
//            FilterChain filterChain) throws IOException, ServletException {
//        HttpServletResponse response = (HttpServletResponse) servletResponse;
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//        response.setHeader("Access-Control-Allow-Origin", "*");
//        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, PATCH, DELETE, OPTIONS");
//        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, Authorization, X-Requested-With");
//        response.setHeader("Access-Control-Allow-Credentials", "true");
//
//        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
//            response.setStatus(HttpServletResponse.SC_OK);
//        } else {
//            filterChain.doFilter(request, response);
//        }
//    }
//}
