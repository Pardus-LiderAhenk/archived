 $(document).ready(function(){ 
		
		var selectedEntries=[];
		
		$('.plugin').on('click', function() {
			
			 var d = $(this).attr("data-datac");
			 
			   $('#randevuListModal').modal('show');
			 
			   $('#render').load('/getPluginHtmlPage/conky');
			
		});
		
		
		$('#addSelectedEntry').on('click', function() {
			
			var rowCount = $('#entryTable tr').filter(':has(:checkbox:checked)').length;
			
			if(rowCount>0){
				
				$('#entryTable tr').filter(':has(:checkbox:checked)').each(function(i) {
					
					if(	jQuery.inArray( this.id, selectedEntries ) == -1 ){
						selectedEntries.push(this.id);   
						
					}
					
				});
				
				writeTable();
				
			}
			
			
		});
		
		function writeTable() {
			
		    var html = '<table class="table table-striped table-bordered " id="selectedEntryTables">';
		    
		    for (var i = 0; i < selectedEntries.length ; i++) {
		        	html += '<tr>';
		        
		            html += '<td>' + selectedEntries[i] + '</td>';
		            
		            html += '<td> <a class="removeEntry"> Kaldır </a>  </td>';   
		       		
		            html += '</tr>';
		    }
		    html += '</table>';
		    
		    $('#selectedEntriesHolder').html(html);
		}
		
	
			$('.gonder').on('click', function() {

				
				var entry=$(this).attr("data-id");
				
				$.ajax({
				      type: "POST",
				      
				      url: "/getEntry",
				      
				      data: {entry},
				      
				      success: function(result) {
				    	  
				    	// var data= JSON.stringify(result);
				    	
				    	// alert(data);
				    	
				    	// $.map(result , function( item, i ) {
				    	// alert(i+" = "+item[0]);
				    	// });
				    	
				    	  document.getElementById('entryTable').innerHTML = json2table(result, 'table table-striped table-bordered');
				    	
				      },
				      
				      error: function(result) {
				        alert(result);
				      }
				    });

			});
		
			
			function json2table(json, classes) {
				  var cols = Object.keys(json[0]);
				  var headerRow = '';
				  var bodyRows = '';
				  
				  classes = classes || '';

				  function capitalizeFirstLetter(string) {
				    return string.charAt(0).toUpperCase() + string.slice(1);
				  }
				  
				  headerRow += '<th>  </th>';
				  headerRow += '<th> Dist Name</th>';
				  headerRow += '<th> CN </th>';
				  headerRow += '<th> UID</th>';
				  headerRow += '<th> O </th>';
				  

				  json.map(function(row) {
					  var distName=row['distinguishedName'];
					  
				    bodyRows += '<tr id= '+ distName  +'  >';
				    bodyRows += ' <td>';
				    
				    bodyRows += ' <input id="checkBox" class="selectedEntry" type="checkbox" name="selectedEntry" value="' +distName +'" />';
				    bodyRows += ' </td>';
				    
				    bodyRows += '<td>' + row['distinguishedName'] + '</td>';
				    bodyRows += '<td>' + row['cn'] + '</td>';
				    bodyRows += '<td>' + row['uid'] + '</td>';
				    bodyRows += '<td>' + row['o'] + '</td>';
				   

				    bodyRows += '</tr>';
				  });

				  return '<table class="' +
				         classes +
				         '"><thead><tr>' +
				         headerRow +
				         '</tr></thead><tbody>' +
				         bodyRows +
				         '</tbody></table>';
				}
			
			
	
				 function log(msg) 
					{
					    $('#log').append(document.createTextNode(msg));
					}
				 
				 
				 function onConnect(status)
					{
					// alert("on connect");
					    if (status == Strophe.Status.CONNECTING) {
						log('Connecting to lider');
					    } else if (status == Strophe.Status.CONNFAIL) {
						log('Failed to connect.');
						$('#connect').get(0).value = 'connect';
					    } else if (status == Strophe.Status.DISCONNECTING) {
						log('Disconnecting.');
					    } else if (status == Strophe.Status.DISCONNECTED) {
						log('Disconnected.');
						$('#connect').get(0).value = 'connect';
					    } else if (status == Strophe.Status.CONNECTED) {
					    	log('Connected.');
					
					
					    	connection.addHandler(onMessage, null, 'message', null, null,  null); 
					    	connection.send($pres().tree());
					    	
					    }
					}
					
				 function onMessage(msg) 
					{
					    var to = msg.getAttribute('to');
					    var from = msg.getAttribute('from');
					    var type = msg.getAttribute('type');
					    var elems = msg.getElementsByTagName('body');
					    
					    alert("--- Message Come Type : "+ type + " content: " +elems[0]);
					
					    if (type == "chat" && elems.length > 0) {
							var body = elems[0];
							
							var resul={
									"name":"John",
									"age":30,
									"cars":[ "Ford", "BMW", "Fiat" ]
									};
							var result=Strophe.getText(resul);
							
							log('Message from ' + from + ': ' + result);
							
							
							// $('#plugin-result').append('<div></div>').append(document.createTextNode(msg));
							
					    
							var reply = $msg({to: from, from: to, type: 'chat'}).cnode(Strophe.copyElement(body));
					 		connection.send(reply.tree());
							
						// log('ECHOBOT: I sent ' + from + ': ' +
						// Strophe.getText(body));
						}
					
					    // we must return true to keep the handler alive.
					    // returning false would remove it after it finishes.
					    return true;
					}
				 
				 $('#checkAll').click(function () {    
				     $('input:checkbox').prop('checked', this.checked);    
				 });
				
				$('#logout').click(function () {    
					 if(connection!=null) {
						connection.disconnect();
					 }
				 });
				 
					 var BOSH_SERVICE = 'http://localhost:5280/bosh';
				 	 var connection = null;
					 
						// alert("connecting xmmp server....");
					 	 
					 connection = new Strophe.Connection(BOSH_SERVICE);
						
					var userName= $('#userName').val();
				 	var userPassword= $('#userPassword').val();
				 		
				 	alert(userName+ " "+ userPassword);
					connection.connect(userName, userPassword, onConnect);
		
				});