<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
		<style type="text/css" title="currentStyle">
			@import "${pageContext.request.contextPath}/resources/css/demo_page.css";
			@import "${pageContext.request.contextPath}/resources/css/demo_table.css";
		</style>
		<script type="text/javascript" language="javascript" src="${pageContext.request.contextPath}/resources/js/jquery.min.js"></script>
		<script type="text/javascript" language="javascript" src="${pageContext.request.contextPath}/resources/js/jquery.dataTables.min.js"></script>
		<script type="text/javascript" charset="utf-8">
			$(document).ready(function() {
				$('#example').dataTable();
			} );
		</script>	
		<title>User List page</title>
</head>
<body id="dt_example">
	<div id="container">
		<div class="full_width big">User overview</div>
		
		<h1>Introduction</h1>
		<p>Here you can see all users that have registered in the system. You can also <a href="${pageContext.request.contextPath}/user/create.html">Create a new user</a> or return to the <a href="${pageContext.request.contextPath}/">Home page</a>.</p>
		
		<p>
		<i>${message}</i>
		</p>
		
		<div id="demo">
	
			<table cellpadding="0" cellspacing="0" border="0" class="display" id="example" width="100%">
				<thead>
					<tr>
					<th>id</th>
					<th>email</th>
					<th>password</th>
					<th>registered</th>
					<th></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="user" items="${userList}">
					
					<tr class="odd gradeX">
						<td>${user.id}</td>
						<td>${user.email}</td>
						<td>${user.password}</td>
						<td>${user.registered}</td>
						<td>
						<a href="${pageContext.request.contextPath}/user/edit/${user.id}.html">Edit</a><br/>
						<a href="${pageContext.request.contextPath}/user/delete/${user.id}.html">Delete</a><br/>
						</td>
					</tr>
					</c:forEach>
				</tbody>
			</table>
	
		</div>
		
		<h1>New user</h1>
		<p>
		<a href="${pageContext.request.contextPath}/user/create.html">Create a new user</a>
		</p>
	</body>
</html>