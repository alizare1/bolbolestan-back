package com.marshmellow.controller;

import com.marshmellow.model.CourseSelectionSystem;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CourseSelectionSystem.getInstance().setCurrentStudent(null);
        response.sendRedirect("/login");
    }
}
