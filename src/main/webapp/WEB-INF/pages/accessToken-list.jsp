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
		<div class="full_width big">Access Tokens overview</div>
		
		<h1>Introduction</h1>
		<p>Here you can see all access tokens that are registered in the system.</p>
		<p>return to the <a href="${pageContext.request.contextPath}/">Home page</a>.</p>
		
		<p>
		<i>${message}</i>
		</p>
		
		<div id="demo">
	
			<table cellpadding="0" cellspacing="0" border="0" class="display" id="example" width="100%">
				<thead>
					<tr>
						<th>AccessToken</th>
						<th>Expriration</th>
						<th>Expires In</th>
						<th>RefreshToken</th>	
						<th>ClientID</th>
						<th>User</th>																	
						<th></th>					
					</tr>
				</thead>
				




				
				<tbody>
					<c:forEach var="token" items="${accessTokenList}">
					
					<tr class="odd gradeX">
						<td>${token.tokenValue}</td>
						<td>${token.tokenExpiration}</td>
						<td>${token.tokenExpirationIn}</td>
						<td>${token.refreshTokenValue}</td>
						<td>${token.clientId}</td>
						<td>${token.userName}</td>																	
						
						<td>
						<a href="${pageContext.request.contextPath}/token/edit/${token.id}.html">Edit</a><br/>
						<a href="${pageContext.request.contextPath}/token/delete/${token.id}.html">Delete</a><br/>
						</td>
					</tr>
					</c:forEach>
				</tbody>
			</table>
	
		</div>
</body>
</html>