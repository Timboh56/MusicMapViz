var listener_count = {};
var selected = true;
var selected_type = "artists";
var artist_selected;
var info;
var track_selected;

function artistorTrackSelected(){
	return selected;	
}

function selectTrack(track) {
	selectReset();
	changeType("tracks");
	track_selected = track;
	$("#chosen_item").append(" Track chosen: <b>" + track_selected + "</b>");
	if (info != null) {
		$("#chosen_item").append("<p> " + info + " </p>");				
	}
}

function selectReset() {
	if (artistorTrackSelected() == false) {
		changeContext();
	}else {
		resetMap();
	}
}

function selectArtist(artist) {
	selectReset();
	changeType("artists");
	artist_selected = artist;
	$("#chosen_item").append(" Artist chosen: <b>" + artist_selected + "</b>");
	if (info != null) {
		$("#chosen_item").append("<p> " + info + " </p>");				
	}
}

// show the list and save it to cache
function showForCountry(items,list_type, country) {	
	if (artistorTrackSelected() == false) { 					
		$("#top_listlist").empty();
		$("#top_listlist").append("<h2>Top " + list_type + " for " + country + "</h2>");
		$("#top_listlist").append("<ul>");
		var count = 0;
		for ( var i in items ) {
			count++;
			//if ( count > 10){
				// place holder: create some "Show more results" button
				//} else{ 								
				if (list_type == "artists") {
					$("#top_listlist").append(items[i]["html"] + " <a href='#' onclick=\"selectArtist('" + i + "');\">Select </a>");
				} else {							
					$("#top_listlist").append(items[i]["html"] + " <a href='#' onclick=\"selectTrack('" + i + "');\">Select </a>");
				}
				//}
		}
		$("#top_listlist").append("</ul>");
	}	
}

function saveList(items, code) {	
	var listeners = 0,
		worldmap = $("#map").vectorMap('get','mapObject');
	if (selected_type == "artists") {
		// artist selected is on the top 50 list you got from last fm					
		if (items[String(artist_selected)] != null ) {
			listeners = items[String(artist_selected)]["listeners"];
		}
	} else {
		if (items[String(track_selected)] != null ) {
			listeners = items[String(track_selected)]["listeners"];
		}
	}
	listener_count[code] = listeners;
	$("#top_list").empty();
	
	if (selected_type == "artists") {
		$("#top_list").append("Artist " + 
			artist_selected + " has " + 
			listeners + " listeners in " + 
			worldmap.getRegionName(code));
	} else {
		$("#top_list").append("Track \"" + 
			track_selected + "\" has " + 
			listeners + " listeners in " + 
			worldmap.getRegionName(code));				
	}
	
	var max = 0;
	for ( var v in listener_count ) {
		if (parseInt(listener_count[v]) > max) {
			max = parseInt(listener_count[v]);
		}
	}
	
	var listener_array = [];
	var count = 0;
	for ( var i in listener_count ) {
		listener_array[count] = { "code" : i, "listeners" : listener_count[i]};
		var rgb = colorize(parseInt(listener_count[i]),max);
		var data = {};
		data[i] ="RGB(" + rgb + "," + rgb + ",255)";
		worldmap.series.regions[0].setValues(data);
		count++;
	}
	
	drawBarGraph(listener_array);					
}

function colorize(count,max) {
	var div = 1 - parseInt(count)/max;
	var rgb = parseInt(div*255);
	return rgb;
}

function resetMap() {
	$("#chosen_item").empty();
	$("#top_list").empty();
	$("#top_listlist").empty();
	
	resetErrors();
	listener_count = {};
	$("#map").vectorMap('get','mapObject').clearSelectedRegions();				
	$("#map").vectorMap('get','mapObject').remove();
	generateMap();
}

function changeContext() {
	$("#bargraph").empty();
	selected = !selected;
	resetMap();
}

function getTopArtists(code,country_name,show) {
	map.getTop(code,country_name,"artists",show);
}

function getTopTracks(code,country_name,show){
	map.getTop(code,country_name,"tracks",show);
}

function submitText() {
	resetMap();
	if (selected_type == "artists") {
		selectArtist($("#text_form").val());
	} else {
		selectTrack($("#text_form").val());
	}
}

function changeType(type) {
	resetMap();
	selected_type = type;
	$("#type_selected").html("<u> " + selected_type + " mode </u> ");
}

function resetErrors(){
	$("#chosen_item_error").empty();
}

function generateMap(){
	var worldmap = new jvm.WorldMap({
		backgroundColor: "#444444",
		container: $("#map"),
		map: 'world_mill_en',
	    series: {
	      regions: [{
	        attribute: 'fill'
	      }]
	    },
		
		regionStyle: {
			initial: {
				fill: "black"						
			}
		},
		onRegionClick: function(event,code){
			resetErrors();
			var country_name = worldmap.getRegionName(code);
			if (country_name == "United States of America"){
				country_name = "USA";
			}
			if ( artistorTrackSelected() == false) {
				$("#top_list").empty();
   				$("#top_list").append(" <a href=\"#\" > " + country_name + "</a> ");
						
				$("#top_list").append(" | " + 
					"<a href=\"#\" onclick=\"getTopArtists('" + 
					code + "','" + 
					country_name + 
					"',true); \" > Show Top Artists </a>");
				
				$("#top_list").append(" | " + 
					"<a href=\"#\" onclick=\"getTopTracks('" + 
					code + "','" + 
					country_name + 
					"',true);\" > Show Top Tracks </a>");
				
				map.getTopChart();
			} else {
				if (selected_type == "artists") {
					if (artist_selected != null) {
						map.getTop(code,country_name,"artists",false);
					} else {
						$("#chosen_item_error").append("<p>You did not select an artist.</p>");
					}
				} else {
					if (track_selected != null) {
						map.getTop(code,country_name,"tracks",false);
					} else {
						$("#chosen_item_error").append("<p>You did not select a track.</p>");
					}
				}
			}
		
		} 
	});
}

$(document).ready(function()
{
	if ($.browser.webkit) {
		$('body').addClass('webkit');
	}

	generateMap()
});