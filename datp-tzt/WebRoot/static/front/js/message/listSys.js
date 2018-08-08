var messageList={
		getList:function(){
				$("#messageList").bootstrapTable({
					url: '/stationMail/searchSys.html',
					contentType : "application/x-www-form-urlencoded",// 必须要有！！！！
					height : "auto",// 高度调整
					toolbar : '#toolbar',// 指定工具栏
					striped : true,// 是否显示行间隔色
					pagination : true,// 是否分页
					pageNumber : 1,// 初始化加载第一页，默认第一页
					pageSize : 10, // 单页记录数
					pageList : [ 10],// 分页步进值
					paginationLoop:false,//禁止循环分页
					search : false,
					searchAlign : 'left',
					showExport : false, // 是否显示导出
					sidePagination : "server", // 指定服务器端分页
					queryParams : queryParams,// 前端调用服务时，会默认传递上边提到的参数，如果需要添加自定义参数，可以自定义一个函数返回请求参数
					queryParamsType : '',// 设置为 'limit' 则会发送符合 RESTFul 格式的参数.
					editable : false,
					clickToSelect : true,
					detailView : false,
					minimunCountColumns : 2,
					paginationPreText : "上一页",
					paginationNextText : "下一页",
					dataField : "rows",
					columns : [
						{
							checkbox : true
						}, {
							title : "ID",
							field : "fid",
							visible : false
						}, {
							title : "是否已读",
							field : "fstatus",
							formatter : operateFormatter
							
						}, {
							title : "消息主题",
							field : "ftitle",
							formatter :operateFormatterdetail
						}, {
							title : "发送人",
							field : "fsendUserName"
						
						}, {
							title : "创建时间",
							field : "ftime"
						
						}],
					responseHandler : function(res) {
						// 在ajax获取到数据，渲染表格之前，修改数据源
						let
						rowData = {};
						rowData = {
							rows : res.data.content,
							total : res.data.totalElements
						};
						return rowData;
					}
				});
		}
}
function queryParams(queryParams) {
	let
	temp = {
		pageIndex : queryParams.pageNumber,
		pageSize : queryParams.pageSize
	};
	return temp;
}

function operateFormatter(value, row, index) {
	if (value == 0) {
		return "<img src='/static/front/images/message/messageNone.png' >";
	} else {
		return "<img src='/static/front/images/message/messageOpen.png' >";
	}
}

function operateFormatterdetail(value, row, index){
	let res=`<a href="/stationMail/getSys.html?fid=${row.fid}" >${row.ftitle}</a>`;
	return res;
}

function delMessage(){
	
	let ids=$.map($("#messageList").bootstrapTable('getSelections'), function(row) {
		return row.fid;
	});
	if(ids==""||ids==null){
		alert("请选择数据！");
		return false;
	}
	if(confirm("确定删除?")){
		
		$.ajax({
			url:"/stationMail/del.html",
			data:{
				ids:ids.toString()
			},
			success:function(res){
				if(res.success){
					alert("删除成功");
					$("#messageList").bootstrapTable('refresh');
				}
			}
		});
	}
}

$(function(){
	messageList.getList();
	$("#btnDelMessage").on("click",delMessage);
});