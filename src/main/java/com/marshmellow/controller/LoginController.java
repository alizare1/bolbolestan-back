package com.marshmellow.controller;

import com.marshmellow.model.CourseSelectionSystem;
import com.marshmellow.Exception.StudentNotFound;
import com.marshmellow.model.Student;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       request.getRequestDispatcher("login.jsp").forward(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String studentId =  request.getParameter("std_id");
        try {
            Student std = CourseSelectionSystem.getInstance().getStudent(studentId);
            CourseSelectionSystem.getInstance().setCurrentStudent(std);
            response.sendRedirect("/");

        } catch(StudentNotFound exp){
            throw new ServletException(exp);
        }

    }
}
