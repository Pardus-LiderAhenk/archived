<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css"	rel="stylesheet" id="bootstrap-css">
<link rel="stylesheet"	href="//maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css">
<link href='http://fonts.googleapis.com/css?family=Varela+Round'	rel='stylesheet' type='text/css'>
<link href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css"	rel="stylesheet" id="bootstrap-css">

<script src="<c:url value="/resources/js/jquery-3.1.1.min.js" />"></script>
<script	src="//maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>

<title>Lider Ahenk Merkezi Yonetim Sistemi</title>
</head>
<body>

	<div class="text-center" style="padding: 50px 0">
		<div class="logo">LOGIN</div>
		<div class="container">
			<div class="row">
				<div class="col-lg-6">
				
				<a href="hello.jnlp">Launch</a>
				<hr>

					<div class="login-form-1">
						<form id="login-form" action="/login" class="text-center"
							method="POST">
							<div class="login-form-main-message"></div>
							<div class="main-login-form">
								<div class="login-group">
									<div class="form-group">
										<label for="lg_xmppserver" class="sr-only">XMPP Server</label>
										<input type="text" class="form-control" id="xmppserver"
											name="lg_xmppserver" placeholder="xmpp server" value="localhost">
									</div>
									<div class="form-group">
										<label for="lg_username" class="sr-only">Username</label> <input
											type="text" class="form-control" id="username"
											name="lg_username" placeholder="username" value="lider_console" >
									</div>
									<div class="form-group">
										<label for="lg_password" class="sr-only">Password</label> <input
											type="password" class="form-control" id="password"
											name="lg_password" placeholder="password" value="1">
									</div>

								</div>
								<button type="submit" class="login-button">Login</button>
							</div>
						</form>
					</div>
				</div>

			</div>
		</div>
	</div>

</body>
</html>