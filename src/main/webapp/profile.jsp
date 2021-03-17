<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="Grade"%>
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
    <li id="std_id">Student Id: </li>
    <li id="first_name">First Name: </li>
    <li id="last_name">Last Name: </li>
    <li id="birthdate">Birthdate: </li>
    <li id="gpa">GPA: </li>
    <li id="tpu">Total Passed Units: </li>
</ul>
<table>
    <tr>
        <th>Code</th>
        <th>Grade</th>
    </tr>


    <%
   // for (Grade grade : student.getGrades()) {
        %>
    <tr>
        <td> </td>
        <td> </td>
    </tr>

   <% //} %>

</table>
</body>
</html>

