var map = (function ($) {
  'use strict';
  // this map module should gather top artists for a specific country, 
  // parse all the relevant information into an array.
  var my = {};
	   
  my.getTopArtistsbyCountry = function(country){
    var i;
	var url = 'http://ws.audioscrobbler.com/2.0/?method=geo.gettopartists&country=' + country + '&api_key=ddf0769305146dfa3b94043ef8e5cb8d&format=json';
	var artists = new Array();		
	
	$.ajax({
	  type: "GET",
	  url: url,
	  success: function(r){
		var c = 1;
		$("#topartists").append("<ul>");
		for (i in r.topartists.artist) {
			$("#topartists").append("<li> " + c +". " + r.topartists.artist[i].name+", \n </li>");
		    //artists[i] = r.topartists.artist[i];
		    c++;
		  }
		$("#topartists").append("</ul>");
		
	  }
	});
	
  }

  return my; 

}(jQuery));

