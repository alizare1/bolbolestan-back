<%@page import="com.marshmellow.model.CourseSelectionSystem"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Home</title>
</head>
<body>
<ul>
    <li id="std_id">Student Id:<%=CourseSelectionSystem.getInstance().getCurrentStudent().getStudentId()%> </li>
    <li>
        <a href="/courses">Select Courses</a>
    </li>
    <li>
        <a href="/plan">Submitted plan</a>
    </li>
    <li>
        <a href="/profile">Profile</a>
    </li>
    <li>
        <a href="/logout">Log Out</a>
    </li>
</ul>
</body>
</html>