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
	$(".loginButton").button();
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
    
    $(".selectAll").click(function() {
        $('#actions input').prop('checked', $("#selectAll").attr("checked")?true:false);
        $('#selectAllText').html($("#selectAll").attr("checked")?"De-select all":"Select all");
        $.uniform.update(); 
    });

	$(".searchButton").click(
		function () {
			
			if (filterState)
				$("#filterMin").trigger('click');
			if (!resultState)
				$("#resultsMin").trigger('click');
			
			var filter = {
				actions: new Array(),
				players: $("[name=players]").val().split(","),
				loc: new Array($("[name=x]").val(), $("[name=y]").val(), $("[name=z]").val()),
				range: $("[name=range]").val(),
				keywords: $("[name=keywords]").val().split(","),
				exclude: $("[name=exclude]").val().split(","),
				worlds: $("[name=worlds]").val().split(","),
				dateFrom: $("#dateFrom").val() + " " + $("#timeFrom").val(),
				dateTo: $("#dateTo").val() + " " + $("#timeTo").val(),
				block: $("#item").val()
			};
			
			if ($("#timeFrom").val() != "")
				filter.dateFrom += ":00";
			if ($("#timeTo").val() != "")
				filter.dateTo += ":00";
				
			$(".actions").find("input:checked").each(function (index) { filter.actions[index] = $(this).val(); });
			
			var dataString = JSON.stringify(filter);
			$(".results").html('<div class="loading"></div>');
			$.getJSON(
				"interface.php?data=" + dataString,
				function (data) {
					if (data.error.length > 0)
						$(".results").html(data.error);
					else {
						$(".results").html('<table class="restable"></table>');
						$(".restable").dataTable( {
							"bJQueryUI": true,
							"sPaginationType": "full_numbers",
							"aaData": data.data,
							"aaSorting": [[ 0, "asc" ]],
							"aLengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
							"bAutoWidth": false,
                            "iDisplayLength": 25,
                            "sDom": '<"H"ipr>t<"F"lf>',
							"aoColumns": [
								{ "sTitle": data.columns.id },
								{ "sTitle": data.columns.date, "sWidth": "160px" },
								{ "sTitle": data.columns.player, "bSearchable": true },
								{ "sTitle": data.columns.action, "bSearchable": true },
								{ "sTitle": data.columns.world },
								{ "sTitle": data.columns.xyz },
								{ "sTitle": data.columns.data, "bSearchable": true }
							] } );
					}
				});
			
			return false;
								   
		});
});

