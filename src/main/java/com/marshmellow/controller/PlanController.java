package com.marshmellow.controller;

import com.marshmellow.model.CourseSelectionSystem;
import com.marshmellow.model.Offering;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.*;

@WebServlet(name = "PlanController", value = "/plan")
public class PlanController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (CourseSelectionSystem.getInstance().getCurrentStudent() == null) {
            response.sendRedirect("/login");
            return;
        }
        Map<String, Map<String, String>> plan = new HashMap<>();
        ArrayList<Offering> courses = CourseSelectionSystem.getInstance().getCurrentStudent().getSchedule();
        for (Offering c : courses) {
            for (String d : c.getClassDays()) {
                if (!plan.containsKey(d))
                    plan.put(d, new HashMap<>());
                plan.get(d).put(c.getClassHour(), c.getName());
            }
        }
        request.setAttribute("plan", plan);
        request.getRequestDispatcher("plan.jsp").forward(request, response);
    }
}
