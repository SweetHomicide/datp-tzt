var  record= {
	search : function(begindate, enddate) {
		var url = "/web/buyRecord.html";
		var finaName = $("#finaName").val();
		var begindate = begindate ? begindate : $("#begindate").val();
		var enddate = enddate ? enddate : $("#enddate").val();
		url=url + "?finaName=" + finaName + "&beginTime=" + begindate + "&endTime=" + enddate;
		window.location.href = url;
		
	}
};
$(function() {
	$('#begindate').click(function() {
		WdatePicker({
			el : 'begindate',
			maxDate : '#F{$dp.$D(\'enddate\')}',
			dchanged : function() {
				var d = $dp.cal.newdate['d'];
				var m = $dp.cal.newdate['M'];
				var y = $dp.cal.newdate['y'];
				if (m < 0) {
					m = "07";
				}
			}
		});
	});
	$('#enddate').click(function() {
		WdatePicker({
			el : 'enddate',
			minDate : '#F{$dp.$D(\'begindate\')}',
			dchanged : function() {
				var d = $dp.cal.newdate['d'];
				var m = $dp.cal.newdate['M'];
				var y = $dp.cal.newdate['y'];
				if (m < 0) {
					m = "07";
				}
			}
		});
	});
	$("#btnSearchRecord").on("click",function() {
		record.search();
	});
});