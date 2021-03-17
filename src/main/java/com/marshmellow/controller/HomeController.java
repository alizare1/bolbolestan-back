package com.marshmellow.controller;

import com.marshmellow.model.CourseSelectionSystem;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet("")
public class HomeController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (CourseSelectionSystem.getInstance().getCurrentStudent() == null)
            response.sendRedirect("/login");
        else
            request.getRequestDispatcher("home.jsp").forward(request, response);
    }
}
