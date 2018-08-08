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
			<div class="col-xs-9 service-leftbg ">
				<div class="col-xs-12 service-img">
					<div class="service-newsimg"></div>
				</div>
				<div class="col-xs-12 service-newsnav">
					<div>
						<p>
							<span class="text-center"
								style="${id==1?'border-bottom:2px solid #c93835;':'border-bottom:0px solid #c93835;'}">
								<a href="/service/ourService.html?id=1" style="color: #c83935;">${articles[0].key.fname}</a>
							</span> <span class="text-center"
								style="${id==2?'border-bottom:2px solid #c93835;':'border-bottom:0px solid #c93835;'}">
								<a href="/service/ourService.html?id=2" style="color: #c83935;">${articles[1].key.fname}</a>
							</span> <span class="text-center"
								style="${id==3?'border-bottom:2px solid #c93835;':'border-bottom:0px solid #c93835;'}">
								<a href="/service/ourService.html?id=3" style="color: #c83935;">${articles[2].key.fname}</a>
							</span>
						
							<%-- <span class="news-fist">${articles[2].key.fname}</span> <span class="news-more">
							<a  href="/service/ourService.html?id=${articles[2].key.fid }" style="color:Red;">更多>></a>
		                    </span> --%>
				
						</p>

					</div>
				</div>
				<!-- 新闻列表 -->
				<div class="col-xs-12 service-newscontent">

					<c:forEach items="${farticles }" var="v">
						<div class="snc-max">
							<div class="col-xs-3 snc-left">
									<a href="${v.url }" target="_blank">

									<img src="${v.furl }" class="snc-newsimg" /></a>
							</div>
							<div class="col-xs-9 snc-right">
								<a href="${v.url }" target="_blank">

								<h3 class="snc-newscontent">${v.ftitle }</h3>
								</a>
								<p>${v.fcontent_m }</p>
								<div class="rows snc-newsinfo">
									<%-- <div class="col-xs-2 sncnc-lookinfo ">
										<p>${v.fadminByFcreateAdmin.fname }</p>
									</div> --%>
									<div class="col-xs-4 ">
										<p>
										<fmt:formatDate value="${v.fcreateDate }" pattern="yyyy-MM-dd HH:mm:ss"/>
										</p>
									</div>
									<%-- <div class="col-xs-2 sncnc-lookinfo pull-right">
										<p>
											阅读：<span>${v.fcount }</span>
										</p>
									</div> --%>
								</div>
							</div>
						</div>
					</c:forEach>



					<div class="text-right">${pagin }</div>
				</div>
			</div>
			<!-- 右侧 -->
			<div class="col-xs-3">
				<div class="service-rightbg">
					<div class="col-xs-12 servive-right-contentbg">
						<!-- 扫码关注 -->
						<%-- <div class="service-rightmax">
							<div class="service-rtop">
								<span class="topsmgz">扫码关注</span>
							</div>
							<div class="service-right-ewm">
								<img src="${requestScope.constant['downWeixinImages'] }">
								</img>
								<p>关注${requestScope.constant['webName']}官方微信</p>
								<p>查看币种最新行情</p>
							</div>
						</div> --%>
						<!-- 热门话题 -->
						<div class="service-rightmax rmht">
							<div class="service-rtop service-border-bottom">
								<span class="downrmht">热门话题</span>
							</div>
							<div class="service-rtop-content ">
								<ul class="text-left">
									<c:forEach items="${hotsArticles }" var="v">
										<a href="${v.url }" target="_blank">
										<li><fmt:formatDate value="${v.fcreateDate }"
												pattern="yyyyMMdd" /> ${v.ftitle }</li>
										</a>
									</c:forEach>
								</ul>
							</div>
						</div>
					</div>
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
