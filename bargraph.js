Object.size = function(obj) {
    var size = 0, key;
    for (key in obj) {
        if (obj.hasOwnProperty(key)) size++;
    }
    return size;
};

// items will take in listener_count
function drawBarGraph(items){         
  var max = d3.max(items,function(d) { return d.listeners; });
  var min = d3.min(items,function(d) { return d.listeners; });
  var x_margin = 40;
  
  // width and height
  var w = 250;
  
  // height of each bar
  var h = 30;
  
  var chart_height = Object.size(items) * h;
    
  $("#bargraph").empty();
  $("#bargraph").append("<p><h2>Listener count</h2></p>");
  
  items.sort(	function (a,b) { 
	  return a.listeners - b.listeners; 
  });

 	var chart = d3.select("#top_list").append("svg")
 		.attr("id", "chart")
		.attr("width", w)
		.attr("height", chart_height + 30);
			
  	var x_scale = d3.scale.linear()
  		.domain([x_margin, max])
   		.range([0, w]);
		
	var y_scale = d3.scale.ordinal()
		.domain([0, Object.size(items)])
		.rangeBands([0,chart_height]);
	
  	chart.selectAll("line")
		.data(x_scale.ticks(10))
		.enter().append("line")
		.attr("x1", function(d,i) { sort_ticks(i,w,x_margin) })
		.attr("x2", function(d,i) { sort_ticks(i,w,x_margin) })
		.attr("y1",0)
		.attr("y2", chart_height + 10)
		.style("stroke", "#FFFFFF");

	chart.append("line")
		.attr("x1",x_margin)
		.attr("x2",x_margin)		
		.attr("y1",0)
		.attr("y2", chart_height + 10)
		.style("stroke", "#FFFFFF");
	
    chart.selectAll("rect")
    	.data(items)
	  	.enter().append("rect")
		.attr("x", x_margin)
	  	.attr("y", function(d, i) { 
			return chart_height + 10 - (i) * h; 
		})
	  	.attr("width", function(d) {
	  		return x_scale(d.listeners);
	  	})
	  	.attr("height",h)		
	  	.attr("fill", function(d,i) { 
	  		var rgb = colorize(d.listeners,max); 					
	  		var data = {};
	  		return "RGB(" + rgb + "," + rgb + ",255)";
	  	})
	
	
  chart.selectAll("text")
    .data(items)
    .enter().append("text")
    .attr("x", function(d,i) {
		return x_scale(d.listeners) + x_margin;
	})
	.attr("y", function(d,i) {
		return chart_height + 10 - (i) * h; 
 	})
	.attr("dx", -3)
	.attr("dy", ".35em")
    .attr("text-anchor", "end")
	.style("stroke","#FFFFFF")
	.style("font-size","10px")
    .text(function(d,i) {
		return d.listeners + " listeners";
	});
}

function sort_ticks(i,w,x_margin) {
	return i*(w/10) + x_margin;
}
      