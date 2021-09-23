<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>



</head>
<body>

MASAUSTU MESAJ GONDER 

<hr>

${deneme}

<hr>
<div class="login-form-1">
				<div class="login-group">
					<div class="form-group">
					Mesaj : 
						<input type="text" class="form-control" id="username" name="param" >
					</div>
				</div>
				<button type="submit" class="login-button" id="gonder"><i class="fa fa-chevron-right"></i></button>
				
				<div id="plugin-result"></div>
			
</div>


<script type="text/javascript">

			$(document).ready(function(){
				
				var data = {
						"pluginName":"conky",
						"dnList":["cn=253ef0c4-6cfb-387f-81a9-8198f78afaff,ou=Ahenkler,dc=mys,dc=pardus,dc=org"],
						"pluginVersion":"1.0.0",
						"timestamp":1523430976357,
						"dnType":"AHENK",
						"parameterMap":{"conkyMessage":"DENEEM","removeConkyMessage":false},
						"commandId":"EXECUTE_CONKY"
						}
				
				var params = JSON.stringify(data);

				$("#gonder").click(function(e) {
				    e.preventDefault();
				    
				    $.ajax({
				      type: "POST",
				      url: "/lider/task/execute",
				      headers: {
				          'Content-Type':'application/json',
				          'username':'${sessionScope.userName}',
				          'password':'${sessionScope.userPassword}',
				      },
				      data: params,
				      contentType: "application/json",
				      dataType: "json",
				      converters: {
				        'text json': true
				      },
				      success: function(result) {
				    	  
				        alert(result);
				      },
				      error: function(result) {
				        alert(result);
				      }
				    });
				    
				    
				  });
				
				 

				});
			
			
	
	</script>


</body>
</html>