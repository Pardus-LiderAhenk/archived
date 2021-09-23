<%@ page language="java" contentType="text/html; charset=ISO-8859-9" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1" />

<!-- All the files that are required -->
<link rel="stylesheet" href="//maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css">
<link href='http://fonts.googleapis.com/css?family=Varela+Round' rel='stylesheet' type='text/css'>
<link href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css" rel="stylesheet" id="bootstrap-css">
<link href=" <c:url value="/resources/css/jquery.treegrid.css" />" rel="stylesheet">
<link href=" <c:url value="/resources/css/metisMenu.css" />" rel="stylesheet">
<link href=" <c:url value="/resources/css/sb-admin-2.css" />" rel="stylesheet">

<script src="<c:url value="/resources/js/jquery-3.1.1.min.js" />"></script>

<script src="<c:url value="/resources/js/jquery.treegrid.js" />"></script>
<script src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-validate/1.13.1/jquery.validate.min.js"></script>

<script src="<c:url value="/resources/js/metisMenu.min.js" />"></script>
<script src="<c:url value="/resources/js/sb-admin-2.js" />"></script>
<script src="<c:url value="/resources/js/strophe.js" />"></script>

<script type="text/javascript">

      $(document).ready(function() {
            $('.tree').treegrid({
            onChange: function() {
                alert("Changed: "+$(this).attr("id"));
            }, 
            onCollapse: function() {
                alert("Collapsed: "+$(this).attr("id"));
            }, 
            onExpand: function() {
                alert("Expanded "+$(this).attr("id"));
            }});
            
            $('#node-1').on("change", function() {
                alert("Event from " + $(this).attr("id"));
            });       
            
      });
    </script>
</head>
<body>

</body>
</html>