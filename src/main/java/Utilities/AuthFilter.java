package Utilities;

import com.marshmellow.controller.AuthController;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpHeaders;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

@WebFilter("/*")
public class AuthFilter implements Filter {
    private static final ArrayList<String> excludedUrls = new ArrayList<>() {
        {
            add("token");
            add("signup");
            add("resetPassword");
        }
    };

    @Override
    public void init(FilterConfig fc) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        String[] path = ((HttpServletRequest) request).getRequestURI().split("/");
        if (path.length > 2 && path[1].equals("api") && excludedUrls.contains(path[2])) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = ((HttpServletRequest) request).getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || authHeader.split(" ").length < 2) {
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"No JWT token was provided\"}");
            ((HttpServletResponse) response).setHeader("Content-Type", "application/json;charset=UTF-8");
            return;
        }

        String jwt = authHeader.split(" ")[1];
        SecretKey key = new SecretKeySpec(AuthController.KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Jws<Claims> jwsClaims;
        try {
            jwsClaims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt);
            if (jwsClaims.getBody().getExpiration().before(new Date()))
                throw new JwtException("Token is expired");
            System.out.println("FILTER SID FROM JWT: " + jwsClaims.getBody().get("sid"));
            request.setAttribute("sid", jwsClaims.getBody().get("sid"));
            if (!studentHasAccess(path, jwsClaims.getBody().get("sid").toString())) {
                ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("{\"error\": \"You don't have access to this resource\"}");
                ((HttpServletResponse) response).setHeader("Content-Type", "application/json;charset=UTF-8");
                return;
            }
        } catch (JwtException e) {
            System.out.println(e.getMessage());
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\": \"BAD JWT\"}");
            ((HttpServletResponse) response).setHeader("Content-Type", "application/json;charset=UTF-8");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean studentHasAccess(String[] path, String sid) {
        return path.length < 4 || !path[2].equals("students") || path[3].equals(sid);
    }

    @Override
    public void destroy() {}
}
