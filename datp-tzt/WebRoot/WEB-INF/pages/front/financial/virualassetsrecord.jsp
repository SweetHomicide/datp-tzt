<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="../comm/include.inc.jsp"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;
%>

<!doctype html>
<html>
<head> 
<base href="<%=basePath%>"/>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<%@include file="../comm/link.inc.jsp" %>

<link rel="stylesheet" href="/static/front/css/finance/assetsrecord.css" type="text/css"></link>
<style>
#goup{
	margin-top:20px;
	
}
</style>
</head>
<body>

 <%@include file="../comm/header.jsp" %>

	<div class="container-full padding-top-30">
		<div class="container">
			<%@include file="../comm/left_menu.jsp" %>
			
			<div class="col-xs-12 padding-right-clear">
				<div class="col-xs-12 padding-right-clear padding-left-clear rightarea assetsrecord">
					<a class="btn " href="/financial/assetsrecord.html" id="goup"><-返回上一页</a>
					<div class="col-xs-12 rightarea-con">
						<div class="col-xs-3 padding-clear">
							<table class="table table-striped text-center">
								<tbody>
								<tr class="bg-danger">
									<td>数字资产</td>									
								</tr>
								
								<c:forEach items="${fvirtualcointypes}" var="v" varStatus="vs">
									<tr>
										<td>
											<span class="second borderright">
												${v.fname }
											</span>
										</td>
									</tr>
								
								</c:forEach>
								
								
								<c:if test="${fn:length(list) == 0 }">			
									<tr>
										<td colspan="${fn:length(fvirtualcointypes)+4 }" class="no-data-tips">
											<span>
												您暂时没有记录。
											</span>
										</td>
									</tr>
								</c:if>	
							</tbody></table>
						</div>
						<div class="col-xs-9 padding-clear">
							<table class="table table-striped text-center">
								<tbody>
								<tr class="bg-danger">
									<td>冻结</td>
									<td>可用</td>
									<td>总额</td>
								</tr>
								<c:forEach items="${list.get(0).list }" var="v" varStatus="vs">
									<tr>
										<td>
											<span class="second">
												<fmt:formatNumber value="${v.value1 }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/>
											</span>
										</td>
										
										<td>
											<span class="second">
												<fmt:formatNumber value="${v.value2 }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/>
											</span>
										</td>
										<td>
											<span class="second">
												<fmt:formatNumber value="${v.value1+v.value2 }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/>
											</span>
										</td>
									</tr>
								
								</c:forEach>
								
								<c:if test="${fn:length(list) == 0 }">			
									<tr>
										<td colspan="${fn:length(fvirtualcointypes)+4 }" class="no-data-tips">
											<span>
												您暂时没有记录。
											</span>
										</td>
									</tr>
								</c:if>	
							</tbody></table>
							<div class="text-right">${pagin }</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
<%@include file="../comm/footer.jsp" %>	
</body>
</html>