<%@ page language="java" contentType="text/html; charset=ISO-8859-9"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1" />


<style>
.log {
	width: 250px;
	word-wrap: break-word;
	margin-left: 6px;
}
</style>


</head>
<body>

	<jsp:include page="header.jsp"></jsp:include>

	<div id="wrapper">

		<!-- Navigation -->
		<nav class="navbar navbar-default navbar-static-top" role="navigation"
			style="margin-bottom: 0">
			<!-- Banner Menu -->
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".navbar-collapse">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="#">Lider Ahenk MYS</a>
			</div>
			<!-- /.navbar-header -->

			<!-- Ust menu sag kisim -->
			<ul class="nav navbar-top-links navbar-right">
				<!-- /.dropdown -->
				<li class="dropdown"><a class="dropdown-toggle"
					data-toggle="dropdown" href="#"> <i class="fa fa-user fa-fw"></i>
						<i class="fa fa-caret-down"></i>
				</a>
					<ul class="dropdown-menu dropdown-user">
						<li><a href="#"><i class="fa fa-user fa-fw"></i> User
								Profile</a></li>
						<li><a href="#"><i class="fa fa-gear fa-fw"></i> Settings</a>
						
						
						</li>
						<li class="divider"></li>
						<li><a href="#" id="logout" ><i class="fa fa-sign-out fa-fw">
						
						</i>Logout</a></li>
						
						<li><input type="hidden" id="userName" value="${sessionScope.userNameJid}" >  </li>
                	 <li><input type="hidden" id="userPassword" value="${sessionScope.userPassword}" >  </li>
						
					</ul> <!-- /.dropdown-user --></li>
				<!-- /.dropdown -->
			</ul>
			<!-- /.navbar-top-links -->

			<!-- Sol Menu -->
			<div class="navbar-default sidebar" role="navigation">
				<div class="sidebar-nav navbar-collapse">
					<ul class="nav" id="side-menu">
						<li class="sidebar-search">
							<div class="input-group custom-search-form">
								<input type="text" class="form-control" placeholder="Ara...">
								<span class="input-group-btn">
									<button class="btn btn-default" type="button">
										<i class="fa fa-search"></i>
									</button>
								</span>
							</div> <!-- /input-group -->
						</li>

						<li id="tablelist">

							<table class="tree table " id="mytable">
								<thead>
									<tr>
										<td align="center">Organizasyon Yapýsý</td>
									</tr>
								</thead>
								<c:forEach items="${sessionScope.ldapTreeList}" var="entry">
									<tr align="left"
										class="treegrid-${entry.entryUUID} treegrid-parent-${entry.parent}"
										id="${entry.entryUUID}">
										<td><img alt="" src="/resources/img/folder.png"> <a href="#"
											class="gonder" id="send" data-id="${entry.distinguishedName}">
												${entry.name} </a></td>

									</tr>
								</c:forEach>

							</table>

						</li>

					</ul>

				</div>
				<!-- /.sidebar-collapse -->

				<div id='log' class="log"></div>


			</div>
			<!-- /.navbar-static-side -->
		</nav>

		<div id="page-wrapper">

			<div class="row">

				<div class="col-lg-9">
				
				
					<div class="panel panel-info">
                        <div class="panel-heading">
                        
                        	<div class="row">
                        
							<input type="checkbox" id="checkAll" value="Tümünü Seç">
						Tümünü Seç
						
						</div>
                        </div>
                        
                        <div class="panel-body">
                        
                        		<div class="row">

									<div id="entryTable" class="col-lg-6"></div>
								</div>

						<div class="row">
	
							<a id="addSelectedEntry"> Ekle </a>
	
						</div>

						<hr>

							<div class="row">
		
								<div class="col-lg-6">
									<h4 id="agents"></h4>
		
									<div id="selectedEntriesHolder"></div>
								</div>
							</div>

                        </div>
                        
                    </div>
				
				</div>

				<div class="col-lg-3">

					<div class="panel panel-info">
                        <div class="panel-heading">
                            Görevler
                        </div>
                        <div class="panel-body">
                        
                        	<c:forEach items="${sessionScope.pluginList}" var="plugin">

							<a id="plugin" class="plugin" data-datac=${plugin.name} >
								${plugin.name} </a>

							<hr>

						</c:forEach>
                        
                        </div>
                        
                    </div>
				</div>

			</div>

			<!-- end page wrapper div -->
		</div>

		<!-- MODALL -->
		<div class="modal fade" id="randevuListModal" tabindex="-1"
			role="dialog" aria-labelledby="exampleModalLabel"
			style="color: black; font-size: 11px;">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>

						<h4 class="modal-title" id="exampleModalLabel"></h4>
					</div>

					<div class="modal-body">

						<div id="render"></div>

					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default"
							id="randevuListKapat" data-dismiss="modal">Kapat</button>
					</div>

				</div>
			</div>
		</div>

		<!-- end wrapper div -->
	</div>

<script src="<c:url value="/resources/js/main.js" />"></script>

</body>
</html>