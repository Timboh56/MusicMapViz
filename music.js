

var map = (function($){	
	
	// this map module should gather top artists for a specific country, 
	// parse all the relevant information into an array.
	var my = {};
	
	my.getTopArtistsbyCountry = function(country){
		var url = 'http://ws.audioscrobbler.com/2.0/?method=geo.gettopartists&country=' + country + '&api_key=ddf0769305146dfa3b94043ef8e5cb8d&format=json';
		var artists = [];
		$.ajax({
			type: "GET",
			url: url,
			success: function(r){
				for(var i in r.topartists.artist ){
					artists[i] = r.topartists.artist[i];					
					}

				}
			}
		);
		return artists;	
	}
	return my; 
	
}(jQuery));
