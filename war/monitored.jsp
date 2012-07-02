<%@page import="java.util.Iterator"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.google.appengine.api.memcache.MemcacheServiceFactory"%>
<%@page import="com.google.appengine.api.memcache.MemcacheService"%>
<%@page import="com.google.appengine.api.users.User"%>
<%@page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@page import="com.google.appengine.api.users.UserService"%>
<%@page import="java.util.Date"%>
<%@page import="de.egore911.drilog.server.model.Monitored"%>
<%@page import="de.egore911.drilog.server.PreferencesService"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>dri-log-server - Preferences</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <link href="../../bootstrap/css/bootstrap.css" rel="stylesheet">
    <link href="../../bootstrap/css/bootstrap-responsive.css" rel="stylesheet">

    <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
  </head>
<body onload="document.preferencesform.monitored.focus();">
<div class="container-fluid">

<%

	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();

	if (user == null) {
	    response.sendRedirect(userService.createLoginURL(request.getRequestURI()));
	    return;
	}

%>
<header class="jumbotron subhead" id="overview">
  <h1>Your monitored users</h1>
  <p class="lead">Preferences for <%=user.getEmail()%></p>
</header>

<form name="preferencesform" method="post" action="monitored.jsp" class="well form-inline">
  <input type="text" name="monitored" class="span3" placeholder="Username">
  <button type="submit" class="btn btn-primary">Add</button>
</form>

<%
		//Caching to MemCache
	    MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		List<Monitored> monitoreds = (List<Monitored>) syncCache.get(user.getEmail());


		PreferencesService ps = new PreferencesService();

		if (monitoreds == null) {
			monitoreds = ps.getMonitoreds();
			syncCache.put(user.getEmail(), new ArrayList(monitoreds));
		}

		//Add monitored
		String monitoredUsername = request.getParameter("monitored");
		if (monitoredUsername != null && !monitoredUsername.isEmpty()) {
			Monitored monitored = new Monitored();
			monitored.setUsername(monitoredUsername);
			monitored.setAdded(new Date());
			monitored.setBy(user.getEmail());

			if (ps.addMonitored(monitored)) {
				monitoreds.add(monitored);
				Collections.sort(monitoreds);
				syncCache.put(user.getEmail(), new ArrayList(monitoreds));
			}
		}

		//Delete monitored
		String remove = request.getParameter("remove");
		if (remove != null && !remove.isEmpty()) {
			Monitored monitored = ps.getMonitored(Long.parseLong(remove));
			if (monitored != null) {
				ps.removeMonitored(monitored);
				monitoreds.remove(monitored);
			}
			syncCache.put(user.getEmail(), new ArrayList(monitoreds));
		}
		
		//List monitoreds
		if (monitoreds != null && !monitoreds.isEmpty()) {
			out.println ("<table class=\"table table-striped table-bordered table-condensed\" border=\"1\" align=\"center\">");
			out.println ("<thead><tr><th>Username</th><th>Date added</th><th></th></tr></thead><tbody>");
			for (Monitored monitored : monitoreds) {
				out.println("<tr>");
				out.println("<td>" + monitored.getUsername() + "</td>"
						+ "<td>"+ monitored.getAdded() + "</td>"
						);
				out.println("<td><a class=\"btn btn-danger\" href=\"/monitored.jsp?remove="
						+ monitored.getId() + "\">" +
						"<i class=\"icon-trash icon-white\"></i> </a></td>");

				out.println("</tr>");
			}
			out.println ("</tbody></table>");
		}

%>

</div>
</body>


</html>