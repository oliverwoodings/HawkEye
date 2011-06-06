var filterState = true;
var resultState = true;
$(document).ready(function(){
	$.get("items.txt",
		function (data) {
			items = data.split("\n");
			itemstring = "";
			for (i in items) {
				it = items[i].split(',');
				itemstring += '<option value="' + it[0] + '">' + it[1] + "</option>";
			}
			$("[name=item]").html(itemstring);

		});
	$.datepicker.setDefaults({ dateFormat: 'yy-mm-dd' });
	$('.ui-state-default').hover(
		function(){ $(this).addClass('ui-state-hover'); }, 
		function(){ $(this).removeClass('ui-state-hover'); }
	);
	$('.ui-state-default').click(function(){ $(this).toggleClass('ui-state-active'); });
	$('#filterMin').click(function () { $('.filter').slideToggle(); filterState = !filterState; });
	$('#resultsMin').click(function () { $('.results').slideToggle(); resultState = !resultState; });
	$("select, input:checkbox, input:radio, input:file").uniform();
	$("button").button();
	$("[title]").tooltip({
		position: "center right",
		offset: [-2, 10],
		effect: "fade",
		opacity: 0.7
	});
	$("#timeFrom").timepicker();
	$("#timeTo").timepicker();
	$("#dateFrom").datepicker();
	$("#dateTo").datepicker();
	
	$(".searchButton").click(
		function () {
			
			if (filterState)
				$("#filterMin").trigger('click');
			if (!resultState)
				$("#resultsMin").trigger('click');
			
			var filter = {
				actions: new Array(),
				password: $("[name=password]").val(),
				players: $("[name=players]").val().split(","),
				loc: new Array($("[name=x]").val(), $("[name=y]").val(), $("[name=z]").val()),
				range: $("[name=range]").val(),
				keywords: $("[name=keywords]").val().split(","),
				worlds: $("[name=worlds]").val().split(","),
				dateFrom: $("#dateFrom").val() + " " + $("#timeFrom").val(),
				dateTo: $("#dateTo").val() + " " + $("#timeTo").val(),
				block: $("#item").val()
			};
			
			if ($("#timeFrom").val() != "")
				filter.dateFrom += ":00";
			if ($("#timeTo").val() != "")
				filter.dateTo += ":00";
				
			$(".filter").find("input:checked").each(function (index) { filter.actions[index] = $(this).val(); });
			
			//for (i = 0; i < 17; i++) {
				//if (document.searchForm.action[i].checked)
					//filter.actions[filter.actions.length] = document.searchForm.action[i].value;
			//}
			var dataString = JSON.stringify(filter);
			$(".results").html('<div class="loading"></div>');
			$.ajax({
			  url: "interface.php",
			  cache: false,
			  data: "data=" + dataString,
			  success: function(html) {
				$(".results").html(html);
				$(".resultsTable").find("[title]").tooltip({
					position: "center left",
					offset: [-2, 10],
					effect: "fade",
					opacity: 0.7
				});
			  }
			});
			return false;
								   
		});
});

