package Utilities;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter("/*")
public class CORSFilter implements Filter {

    @Override
    public void init(FilterConfig fc) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        ((HttpServletResponse) response).setHeader("Access-Control-Allow-Origin", "*");

        if (((HttpServletRequest) request).getMethod().equals("OPTIONS")) {
            ((HttpServletResponse) response).setHeader("Access-Control-Allow-Headers", "*");
            ((HttpServletResponse) response).setHeader("Access-Control-Allow-Methods", "*");
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
