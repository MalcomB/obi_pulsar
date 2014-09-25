$.ajaxSetup({
	beforeSend:function(){
		$("#imgAjaxLoader").show();
	},
	complete:function(){
		$("#imgAjaxLoader").hide();
	}
});

$(document).ready(function(){
	$("#btnSynopsis").click(function(event){     		
		var textURL = $("#url").val();
		var text = $("#text").val();

		if(text != "" && textURL != ""){
			alert("Enter either a url OR a block of text.")
		}else if(text == "" && textURL == ""){
			document.getElementById('brief-synopsis').style.display = 'none';
		}else{
			$( "#brief-synopsis" ).empty().append( '<img id="imgAjaxLoader"  '
					+ 'src="img/ajax-loader.gif" style="position:fixed; top:50%;'
					+ 'left: 50%;margin-top: -24px;margin-left: -24px;z-index: '
					+ '100;"/>');
			var posting = $.post("Pulsar", {url: textURL, text: text})
			posting.done(function( data ) {
				if(data != ""){
					$( "#brief-synopsis" ).empty().append( data );
				}else{
					$( "#brief-synopsis" ).empty().append( "<h5>Unsupported "
							+ "website.</h5><br/><br/>Supported websites include"
							+ "the following:<br/><br/>" 
							+ "<a href='http://www.aol.com' target='_blank'>"
							+ "<img src='img/aol.jpg'/></a><br/><br/>"
							+ "<a href='http://www.techcrunch.com' target="
							+ "'_blank'><img src='img/techcrunch.svg' /></a><br/><br/>"
							+ "<a href='http://www.huffingtonpost.com' target="
							+ "'_blank'><img src='img/huffington.gif' /><div></div>");
				}
				document.getElementById('brief-synopsis').style.display = 'block';
			});
		}
	});
});