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
							<li class="active">
								<a href="javascript:void(0)">推广明细</a>
							</li>
							<li>
								<a href="/introl/mydivide.html?type=2">收益明细</a>
							</li>
						</ul>
						
						<div class="col-xs-12 padding-clear padding-top-30">
							<table class="table table-striped">
								<tr class="bg-danger">
									<td class="col-xs-4">会员</td>
									<td class="col-xs-4">推荐时间</td>
									<td class="col-xs-4 text-right">是否实名认证</td>
								</tr>
								
								<c:forEach items="${fusers }" var="v" >
								<tr>
									<td>${v.floginName }</td>
									<td><fmt:formatDate
									value="${v.fregisterTime }" pattern="yyyy-MM-dd HH:mm:ss" /></td>
									<td  class="text-right">${v.fisValid ==true?'已实名认证':'用户未实名认证' }</td>
								</tr>
								</c:forEach>
								
								<c:if test="${fn:length(fusers)==0 }">
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
						
						
						<div class="col-xs-12 padding-clear padding-top-30 padding-bottom-30">
								<div class="panel panel-tips">
									<div class="panel-header text-center text-danger">
										<span class="panel-title">推广须知</span>
									</div>
									<div class="panel-body">
									    <p>
									    &lt
									           您的专属推广地址
									    <input value="${spreadLink }" readonly="" type="text" style="width: 230px;height: 30px;color: red;background-color: #ffffff;background-image: none;border: 1px solid #cccccc;"/>
									    </p>
										<p><textarea readonly="" type="text" style="width: 80%;height: 60px;color: red;background-color: #ffffff;background-image: none;border: 1px solid #cccccc;">${requestScope.constant['introlDesc'] }</textarea></p>
									</div>
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