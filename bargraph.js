Object.size = function(obj) {
    var size = 0, key;
    for (key in obj) {
        if (obj.hasOwnProperty(key)) size++;
    }
    return size;
};

// items will take in listener_count
function drawBarGraph(items){         
  
  items.sort( function (a,b) { 
  	 return a.listeners - b.listeners; 
  });
  
  var max = items[Object.size(items) - 1].listeners;

  var x_margin = 60;
  var text_space = 100;
  
  // width and height
  var w = 250;
  
  // height of each bar
  var h = 30;
  
  var chart_height = Object.size(items) * h;
    
  $("#bargraph").empty();
  $("#bargraph").append("<center><p><h2> <u> Listener count </u></h2></center></p>");
 	var chart = d3.select("#bargraph").append("svg")
 		.attr("id", "chart")
		.attr("width", w + x_margin + text_space)
		.attr("height", chart_height + 30);
			
  	var x_scale = d3.scale.linear()
  		.domain([0, max])
   		.range([0, w]);
		
  	chart.selectAll("line")
		.data(x_scale.ticks(10))
		.enter().append("line")
		.attr("x1", function(d,i) { return sort_ticks(i,w,x_margin); })
		.attr("x2", function(d,i) { return sort_ticks(i,w,x_margin); })
		.attr("y1",0)
		.attr("y2", chart_height)
		.style("stroke", "#555555");

	chart.append("line")
		.attr("x1",x_margin)
		.attr("x2",x_margin)		
		.attr("y1",0)
		.attr("y2", chart_height + 10)
		.style("stroke", "#555555");
	
    chart.selectAll("rect")
    	.data(items)
	  	.enter().append("rect")
		.attr("x", x_margin)
	  	.attr("y", function(d, i) { 
			return chart_height - h*i - h; 
		})
	  	.attr("width", function(d) {
	  		return x_scale(d.listeners);
	  	})
	  	.attr("height",h)		
	  	.attr("fill", function(d,i) { 
	  		var rgb = colorize(d.listeners,max); 					
	  		var data = {};
	  		return "RGB(" + rgb + "," + rgb + ",255)";
	  	});
		
  chart.selectAll("text")
    .data(items)
    .enter().append("text")
    .attr("x", function(d,i) {
		return x_scale(d.listeners) + x_margin + text_space - 5;
	})
	.attr("y", function(d,i) {
		return chart_height - (i) * h - 5 ; 
 	})
    .attr("text-anchor", "end")
    .attr("font-family", "sans-serif")
    .attr("font-size", "11px")
	.style("fill","#FFFFFF")	
    .text(function(d,i) {
		return d.code + ": " + d.listeners + " listeners";
	});
	
	
	

}

function sort_ticks(i,w,x_margin) {
	return i*(w/10) + x_margin;
}
      