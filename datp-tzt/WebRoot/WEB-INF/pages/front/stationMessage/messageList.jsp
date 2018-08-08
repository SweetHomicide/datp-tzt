<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="../comm/include.inc.jsp"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path;
%>

<!doctype html>
<html>
<head>
<base href="<%=basePath%>" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<%@include file="../comm/link.inc.jsp"%>

<link rel="stylesheet" href="/static/front/css/finance/assetsrecord.css"
	type="text/css"></link>
<link rel="stylesheet"
	href="/static/front/css/finance/accountrecord.css" type="text/css"></link>
<link rel="stylesheet"
	href="/static/front\css\plugin\bootstrap-table.css" type="text/css"></link>
</head>
<body>

	<%@include file="../comm/header.jsp"%>

	<div class="container-full padding-top-30">
		<div class="container">

			<%@include file="../comm/left_menu.jsp"%>

			<div class="col-xs-10 padding-right-clear">
				<div
					class="col-xs-12 padding-right-clear padding-left-clear rightarea assetsrecord record">
					<div class="col-xs-12 rightarea-con">
						<div class="col-xs-12 padding-clear">
							<div class="col-xs-1 col-xs-offset-11">
								<div class="form-group ">
									<button class="btn btn-default" id="btnDelMessage">删除</button>
								</div>
							</div>
							<table class="table table-striped text-center" id="messageList">
							</table>


						</div>
					</div>
				</div>
			</div>
		</div>
	</div>




	<%@include file="../comm/footer.jsp"%>
	<script type="text/javascript"
		src="/static/front/js/plugin/My97DatePicker/WdatePicker.js"></script>
	<script type="text/javascript"
		src="/static/front/js/finance/assets.record.js"></script>
	<script type="text/javascript"
		src="/static\front\js\plugin\bootstrap-table.js"></script>
	<script type="text/javascript"
		src="/static\front\js\plugin\bootstrap-table-zh-CN.js"></script>
	<script type="text/javascript"
		src="/static\front\js\message\list.js"></script>
</body>
</html>