var map = (function ($) {
  'use strict';

  var lastfm = {};

   lastfm.getTop = function(country,list){
	country.replace(/\s+/g," ");
	var url = "http://ws.audioscrobbler.com/2.0/?method=geo.gettop" + list + "&country=" + country +  "&api_key=ddf0769305146dfa3b94043ef8e5cb8d&format=json";
	$.ajax({
	  type: "GET",
	  url: url,
	  success: function(r){
		var list_elems = [];
		var recv_list;
		var i;
		if (list == "artists") {
		    recv_list = r.topartists.artist;
		} else {
			recv_list = r.toptracks.track;
		}
		
		for (i in recv_list) {
			var text = "#text";
			if (list == "artists") {
				list_elems[i] = "<li> " + i + ". <img src='" + recv_list[i].image[0][text] +"' >   <a href='" + recv_list[i].url + "'> " + recv_list[i].name+"</a>, " + recv_list[i].listeners + " listeners \n </li>";
			} else { 
				list_elems[i] = "<li> " + i +". " + recv_list[i].name+" , " + recv_list[i].artist.name + " , " +  recv_list[i].listeners + " listeners \n </li>";
			}
		}
		showForCountry(list_elems,list,country);	  
	  }
	});
	
  }
  
  lastfm.getTopChart = function() { 
   var url = "http://ws.audioscrobbler.com/2.0/?method=chart.gettopartists&api_key=ddf0769305146dfa3b94043ef8e5cb8d&format=json";
   $.ajax({
   	type: "GET",
   	url: url,
   	success: function(response){ 
   		$("#topchart").append("<ul>");
   		for ( var v in response.artists.artist ) {
				var name = response.artists.artist[v].name;
   				$("#topchart").append(" <li> <a href=\"#\" onclick=\"select('" + name + "');\" > " + name + "</a> </li> ");
   			}  		
   		}
   	 });
	}
	

  return lastfm; 

}(jQuery));

