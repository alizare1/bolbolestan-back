package com.marshmellow.controller;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "profile", value = "/profile")
public class ProfileController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        /*request.setAttribute("FirstName",);
        request.setAttribute("LastName",);
        request.setAttribute("GPA: " , String.format("%.2f", student.getGpa()));
        request.setAttribute("grades",student.getGrades());
        request.setAttribute("Total Passed Units: " , student.getPassedUnitsCount()); */
        request.getRequestDispatcher("profile.jsp").forward(request,response);
    }

}
