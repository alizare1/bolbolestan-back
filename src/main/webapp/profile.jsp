<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.marshmellow.model.Student" %>
<%@ page import="com.marshmellow.model.CourseSelectionSystem" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.Map" %>
<%
    Student student = CourseSelectionSystem.getInstance().getCurrentStudent();
    Map<String, Integer> grades = student.getGrades();
%>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Profile</title>
    <style>
        li {
            padding: 5px
        }
        table{
            width: 10%;
            text-align: center;
        }
    </style>
</head>
<body>
<a href="/">Home</a>
<ul>
    <li id="std_id">Student Id: <%=student.getStudentId()%></li>
    <li id="first_name">First Name: <%=student.getName()%></li>
    <li id="last_name">Last Name: <%=student.getSecondName()%></li>
    <li id="birthdate">Birthdate: <%=student.getBirthDate()%></li>
    <li id="gpa">GPA: <%=String.format("%.2f", student.getGpa())%></li>
    <li id="tpu">Total Passed Units: <%=student.getPassedUnitsCount()%></li>
</ul>
<table>
    <tr>
        <th>Code</th>
        <th>Grade</th>
    </tr>


    <% for (String code : grades.keySet()) { %>
    <tr>
        <td> <%=code%> </td>
        <td> <%=grades.get(code)%> </td>
    </tr>

   <% } %>

</table>
</body>
</html>

