package gbw.sdu.msd.backend.middleware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class RequestLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Log the incoming request URI and source here
        String requestURI = request.getRequestURI();
        String remoteAddress = request.getRemoteAddr();
        System.out.println("Incoming request, Source: " + remoteAddress + " URI: " + requestURI);
        return true;
    }

    // You can implement postHandle and afterCompletion methods if needed
}