<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.marshmellow.model.Student" %>
<%@ page import="com.marshmellow.model.CourseSelectionSystem" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.Map" %>
<%
    Student student = CourseSelectionSystem.getInstance().getCurrentStudent();
    List<String> dayNames = Arrays.asList("Saturday", "Sunday", "Monday", "Tuesday", "Wednesday");
    List<String> hours = Arrays.asList("7:30-9:00", "9:00-10:30", "10:30-12:00", "14:00-15:30", "16:00-17:30");
    @SuppressWarnings("unchecked")
    Map<String, Map<String, String>> plan = (Map<String, Map<String, String>>) request.getAttribute("plan");
%>

<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Plan</title>
    <style>
        table{
            width: 100%;
            text-align: center;

        }
        table, th, td{
            border: 1px solid black;
            border-collapse: collapse;
        }
    </style>
</head>
<body>
<a href="/">Home</a>
<li id="code">Student Id: <%=student.getStudentId()%></li>
<br>
<table>
    <tr>
        <th></th>
        <th>7:30-9:00</th>
        <th>9:00-10:30</th>
        <th>10:30-12:00</th>
        <th>14:00-15:30</th>
        <th>16:00-17:30</th>
    </tr>
    <% for (String dayName : dayNames) { %>
    <tr>
        <td><%=dayName%></td>
        <% for (String hour : hours) { %>
            <td>
                <% if(plan.containsKey(dayName) && plan.get(dayName).containsKey(hour)) { %>
                    <%=plan.get(dayName).get(hour)%>
                <% } %>
            </td>
        <% } %>
    </tr>
    <% } %>
</table>
</body>
</html>
