package com.marshmellow.controller;

import com.marshmellow.model    .CourseSelectionSystem;
import com.marshmellow.Exception.ClassTimeCollisionError;
import com.marshmellow.Exception.ExamTimeCollisionError;
import com.marshmellow.model.Student;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;


@WebServlet("/courses")
public class CoursesController extends HttpServlet {
    private String searchFilter = "";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      if (CourseSelectionSystem.getInstance().getCurrentStudent() == null)
          response.sendRedirect("/login");
      else {
          request.setAttribute("search filter", searchFilter);
          request.getRequestDispatcher("courses.jsp").forward(request,response);
      }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Student student = CourseSelectionSystem.getInstance().getCurrentStudent();
        String stdId = student.getStudentId();
        String action = request.getParameter("action");
        String destination = "courses.jsp";
        switch (action){
            case "remove":
                try {
                    CourseSelectionSystem.getInstance().removeFromWeeklySched(stdId,
                            request.getParameter("course_code"), request.getParameter("class_code"));
                } catch (Exception exception){
                    throw new ServletException(exception);
                }
                break;
            case "submit":
                try {
                    student.finalizeSelection();
                }catch (Exception exception){
                    throw new ServletException(exception);
                }
                break;
            case "reset":
                break;
            case "search":
                searchFilter = request.getParameter("search");
                break;
            case "clear":
                searchFilter = "";
                break;
            case "add":
                try {
                    CourseSelectionSystem.getInstance().addtoWeeklySched(stdId,
                            request.getParameter("course_code"), request.getParameter("class_code"));
                }catch(Exception exception){
                     throw new ServletException(exception);
                }
                break;

        }
        request.setAttribute("search filter",searchFilter);
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(destination);
        requestDispatcher.forward(request, response);
    }
}
