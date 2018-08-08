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

</head>
<body>
	

 
<%@include file="../comm/header.jsp" %>




	<div class="container-full padding-top-30">
		<div class="container">
			
			<%@include file="../comm/left_menu.jsp" %>
			
			<div class="col-xs-10 padding-right-clear">
				<div class="col-xs-12 padding-right-clear padding-left-clear rightarea user">
					<div class="col-xs-12 rightarea-con">
						<ul class="nav nav-tabs rightarea-tabs">
							<li >
								<a href="/user/userloginlog.html?type=1">最近登录历史</a>
							</li>
							<li class="active">
								<a href="javascript:void(0)">安全设置历史</a>
							</li>
						</ul>
						<div class="col-xs-12 padding-clear padding-top-30">
							<table class="table table-striped">
								<tr class="bg-danger">
									<td class="col-xs-4">登录时间</td>
									<td class="col-xs-4">最近安全设置</td>
									<td class="col-xs-4 text-right">登录IP</td>
								</tr>
								<c:forEach items="${logs }" var="v">
									<tr>
										<td>
											<fmt:formatDate value="${v.fcreateTime }" pattern="yyyy-MM-dd HH:mm:ss"/>
										</td>
										<td>${v.ftype_s }</td>
										<td class="text-right">${v.fkey3 }</td>
									</tr>
								</c:forEach>
								
								<c:if test="${fn:length(logs)==0 }">
								<tr>
										<td colspan="3" class="no-data-tips">
											<span> 暂无记录 </span>
										</td>
									</tr>
								</c:if>	
							</table>
							
								<div class="text-right">
									${pagin }
								</div>
							
						</div>
					</div>
				</div>
			</div>
			
			
		</div>
	</div>
	


<%@include file="../comm/footer.jsp" %>	


</body>
</html>