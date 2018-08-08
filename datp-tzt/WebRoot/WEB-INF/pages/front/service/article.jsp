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

			<div class="col-xs-9 article-leftbg ">
				<div class="cols-xs-12 text-left">
					<p>
						<a href="/">首页</a>/<a
							href="/service/ourService.html?id=${farticle.farticletype.fid }">${farticle.farticletype.fname
							}</a>
					</p>
				</div>
				<div class="cols-xs-12">
					<h2 class="text-center ">${farticle.ftitle }</h2>
				</div>
				<div class="cols-xs-12 text-right article-info">
					<span style="color: gray;padding-right:35px;">
					<fmt:formatDate value="${farticle.fcreateDate }" pattern="yyyy-MM-dd HH:mm:ss"/>
					</span>
				</div>
				<div class="cols-xs-12  article-content">${farticle.fcontent }
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
