<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="comm/include.inc.jsp"%>
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
<%@include file="comm/link.inc.jsp"%>

<link rel="stylesheet" href="/static/front/css/zhongchou/zc.css"
	type="text/css"></link>
</head>
<body>
	<%@include file="comm/header.jsp"%>
	<div class="container-full">
		<div class="container">
			<c:if test="${type== 0}">
			<ul class="nav nav-pills nav-stacked ">
				<li
					style="border-bottom: 2px solid red; font-size: 18px; float: left;"><a
					href="/crowd/index.html?type=0"> 未开始</a></li>
				<li style="font-size: 18px; float: left;"><a
					href="/crowd/index.html?type=1"> 进行中</a></li>
				<li style="font-size: 18px; float: left;"><a
					href="/crowd/index.html?type=2"> 已结束 </a></li>
			</ul>
			<div class="autobox">
				<div class="crowd_con crowd_con_list clear">
					<div class="crowd_bi clear">
						<span class="crowd_bi_hlb"> </span>
						<h2>提示</h2>
						<div class="crowd_hui crowd_hui_red">
							<em></em>${msg }
						</div>
					</div>
				</div>
			</div>
			</c:if>
			<c:if test="${type==1}">
			<ul class="nav nav-pills nav-stacked ">
				<li
					style="font-size: 18px; float: left;"><a
					href="/crowd/index.html?type=0"> 未开始</a></li>
				<li style="border-bottom: 2px solid red; font-size: 18px; float: left;"><a
					href="/crowd/index.html?type=1"> 进行中</a></li>
				<li style="font-size: 18px; float: left;"><a
					href="/crowd/index.html?type=2"> 已结束 </a></li>
			</ul>
			<div class="autobox">
				<div class="crowd_con crowd_con_list clear">
					<div class="crowd_bi clear">
						<span class="crowd_bi_hlb"> </span>
						<h2>提示</h2>
						<div class="crowd_hui crowd_hui_red">
							<em></em>${msg }
						</div>
					</div>
				</div>
			</div>
			</c:if>
			<c:if test="${type==2}">
			<ul class="nav nav-pills nav-stacked ">
				<li
					style="font-size: 18px; float: left;"><a
					href="/crowd/index.html?type=0"> 未开始</a></li>
				<li style="font-size: 18px; float: left;"><a
					href="/crowd/index.html?type=1"> 进行中</a></li>
				<li style="border-bottom: 2px solid red;font-size: 18px; float: left;"><a
					href="/crowd/index.html?type=2"> 已结束 </a></li>
			</ul>
			<div class="autobox">
				<div class="crowd_con crowd_con_list clear">
					<div class="crowd_bi clear">
						<span class="crowd_bi_hlb"> </span>
						<h2>提示</h2>
						<div class="crowd_hui crowd_hui_red">
							<em></em>${msg }
						</div>
					</div>
				</div>
			</div>
			</c:if>
		</div>
	</div>
	<%@include file="comm/footer.jsp"%>
</body>
</html>