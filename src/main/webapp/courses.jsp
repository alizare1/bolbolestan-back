<%@ page import="com.marshmellow.model.CourseSelectionSystem" %>
<%@ page import="com.marshmellow.model.Student" %>
<%@ page import="com.marshmellow.model.Offering" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Courses</title>
    <style>
        .course_table {
            width: 100%;
            text-align: center;
        }
        .search_form {
            text-align: center;
        }
    </style>
</head>
<body>
<a href="/">Home</a>
<%
    Student student = CourseSelectionSystem.getInstance().getCurrentStudent();
    String searchFilter = (String)request.getAttribute("search filter");
    ArrayList<Offering> courses = CourseSelectionSystem.getInstance().getCourses(searchFilter);
%>
<li id="code">Student Id: <%=student.getStudentId()%></li>
<li id="units">Total Selected Units: <%=student.getCurrentUnitCount()%></li>

<br>

<table>
    <tr>
        <th>Code</th>
        <th>Class Code</th>
        <th>Name</th>
        <th>Units</th>
        <th></th>
    </tr>
    <%
        for(Offering offering : student.getInProgressSchedule()){ %>
            <tr>
                <td><%= offering.getCode() %></td>
                <td><%= offering.getClassCode() %></td>
                <td><%= offering.getName() %></td>
                <td><%= offering.getUnits() %></td>
                <td>
                    <form action="" method="POST" >
                        <input id="form_action" type="hidden" name="action" value="remove">
                        <input id="form_course_code" type="hidden" name="course_code" value= <%= offering.getCode()%>>
                        <input id="form_class_code" type="hidden" name="class_code" value="01">
                        <button type="submit">Remove</button>
                    </form>
                </td>
            </tr>

   <% } %>

</table>

<br>

<form action="" method="POST">
    <button type="submit" name="action" value="submit">Submit Plan</button>
    <button type="submit" name="action" value="reset">Reset</button>
</form>

<br>

<form class="search_form" action="" method="POST">
    <label>Search:</label>
    <input type="text" name="search" value="<%=searchFilter%>">
    <button type="submit" name="action" value="search">Search</button>
    <button type="submit" name="action" value="clear">Clear Search</button>
</form>

<br>

<table class="course_table">
    <tr>
        <th>Code</th>
        <th>Class Code</th>
        <th>Name</th>
        <th>Units</th>
        <th>Signed Up</th>
        <th>Capacity</th>
        <th>Type</th>
        <th>Days</th>
        <th>Time</th>
        <th>Exam Start</th>
        <th>Exam End</th>
        <th>Prerequisites</th>
        <th></th>
    </tr>
    <%
        for(Offering offering : courses){
        %>
    <tr>
        <td><%=offering.getCode()%></td>
        <td><%=offering.getClassCode()%></td>
        <td><%=offering.getName()%></td>
        <td><%=offering.getUnits()%></td>
        <td><%=offering.signedUp()%></td>
        <td><%=offering.getCapacity()%></td>
        <td><%=offering.getType()%></td>
        <td><%=String.join("|", offering.getClassDays())%></td>
        <td><%=offering.getClassHour()%></td>
        <% SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); %>
        <td><%=dateFormat.format(offering.getExamStart())%></td>
        <td><%=dateFormat.format(offering.getExamEnd())%></td>
        <td><%=  String.join( "|",offering.getPrerequisites()) %></td>
        <td>
            <form action="" method="POST" >
                <input  type="hidden" name="action" value="add">
                <input  type="hidden" name="course_code" value="<%= offering.getCode()%>">
                <input  type="hidden" name="class_code" value="<%= offering.getClassCode()%>">
                <button type="submit">Add</button>
            </form>
        </td>
    </tr>
  <%}%>
</table>
</body>
</html>