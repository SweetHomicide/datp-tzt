<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="../comm/include.inc.jsp"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path;
%>

<!doctype html>
<html>
<head>
<base href="<%=basePath%>" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<%@include file="../comm/link.inc.jsp"%>

<link rel="stylesheet" href="/static/front/css/service/service.css"
	type="text/css"></link>
</head>
<body>
	<%@include file="../comm/header.jsp"%>

	<div class="container-full padding-top-30">

		<div class="container service-max">
	<%@include file="../comm/left_menu.jsp"%>
			<div class="col-xs-10 article-leftbg ">
				<div class="cols-xs-12 text-left">
					<p>
						<c:if test="${stationMailRead.ftype eq '00607' }">
						<a href="/stationMail/viewSys.html">系统消息列表</a>/<a
							href="javascript:void(0)">消息详情</a>
						</c:if>
						<c:if test="${stationMailRead.ftype ne '00607' }">
						<a href="/stationMail/view.html">个人消息列表</a>/<a
							href="javascript:void(0)">消息详情</a>
						</c:if>
					</p>
				</div>
				<div class="cols-xs-12">
					<h2 class="text-center ">${stationMailRead.ftitle }</h2>
				</div>
				<div class="cols-xs-12 text-right article-info">
					<span style="color: gray;padding-right:35px;">
					${stationMailRead.ftime }
					<%-- <fmt:formatDate value="${stationMailRead.ftime }" pattern="yyyy-MM-dd HH:mm:ss"/> --%>
					</span>
				</div>
				<div class="cols-xs-12  article-content" style="text-indent:25px;">${stationMailRead.fcontent }
				</div>
			</div>
		</div>


	</div>








	<%@include file="../comm/footer.jsp"%>

	<script type="text/javascript" src="/static/front/js/comm/msg.js"></script>
	<script type="text/javascript"
		src="/static/front/js/plugin/jquery.qrcode.min.js"></script>
</body>
</html>
