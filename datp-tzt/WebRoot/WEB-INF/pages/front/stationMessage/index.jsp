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

	<div class="container-full">
		<div class="container" style="background:#fff">
			<div class="row" style="margin:30px auto">
				<div class="col-xs-6">
					<div class="news-top clearfix">
						<span class="news-fist">系统消息</span> <span
							class="news-more"> <a
							href="/stationMail/viewSys.html"
							style="color: Red;">更多>></a>
						</span>
					</div>

					<div class="news-all" style="margin-left:5px;">
						<c:forEach items="${listSys.content}" var="list">
							<a href="/stationMail/getSys.html?fid=${list.fid}" class="news-font clearfix">
								<c:if test="${list.fstatus==0}">
								
									<div class="news-content" style="color:red;"> ${list.ftitle }</div>
									<div class="news-date date">
										${list.ftime }
										<%-- <fmt:formatDate value="${list.ftime }" pattern="yyyy-MM-dd" /> --%>
									</div>
								</c:if>
								<c:if test="${list.fstatus==1}">
								
									<div class="news-content"> ${list.ftitle }</div>
									<div class="news-date date">
										${list.ftime }
										<%-- <fmt:formatDate value="${list.ftime }" pattern="yyyy-MM-dd" /> --%>
									</div>
								</c:if>
							</a>
						</c:forEach>
					</div>
				</div>

				<div class="col-xs-6">
					<div class="news-top clearfix" >
						<span class="news-fist">个人消息</span> <span
							class="news-more"> <a
							href="/stationMail/view.html"
							style="color: Red;">更多>></a>
						</span>
					</div>

					<div class="news-all" style="margin-left:5px;">
						<c:forEach items="${listMy.content}" var="list">
							<a href="/stationMail/get.html?fid=${list.fid} " class="news-font clearfix">
								<c:if test="${list.fstatus==0}">
								
									<div class="news-content" style="color:red;"> ${list.ftitle }</div>
									<div class="news-date date">
										${list.ftime }
										<%-- <fmt:formatDate value="${list.ftime }" pattern="yyyy-MM-dd" /> --%>
									</div>
								</c:if>
								<c:if test="${list.fstatus==1}">
								
									<div class="news-content"> ${list.ftitle }</div>
									<div class="news-date date">
										${list.ftime }
										<%-- <fmt:formatDate value="${list.ftime }" pattern="yyyy-MM-dd" /> --%>
									</div>
								</c:if>
							</a>
						</c:forEach>
					</div>
				</div>
			</div>

			<div class="clearfix"></div>


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