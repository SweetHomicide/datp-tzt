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
								<a href="javascript:void(0)">我的众筹</a>
							</li>
						</ul>
						<div class="col-xs-12 padding-clear padding-top-30">
							<table class="table table-striped">
								<tr class="bg-danger">
									<!-- <td>号码</td> -->
									<td>项目名称</td>
								    <td>状态</td>
								 <!--    <td>支付类型</td> -->
									<td>众筹份数</td>
									<td>众筹单价</td>
									<td>总金额</td>
									<td>中签份数</td>
									<td>每份数量</td>
									<td>中签总数量</td>
									<td class="">众筹时间</td>
								</tr>
								
								<c:forEach items="${subscriptionlogs }" var="v">
									<tr>
									    <%-- <td>
											<c:choose>
											<c:when test="${v.fstatus==1 }">
											${v.fid }
											</c:when>
											<c:when test="${v.fstatus==2 }">
											<font color="green">${v.fid }</font>
											</c:when>
											<c:when test="${v.fstatus==3 }">
											<font color="red">${v.fid }</font>
											</c:when>
											</c:choose>
										</td> --%>
										<td>
											<a href="/crowd/view.html?fid=${v.fsubscription.fid }" target="_blank">${v.fsubscription.ftitle }</a>
										</td>
										<td>
											${v.fstatus_s }
										</td>
										<%-- <td>
											${v.fsubscription.symbol }
										</td> --%>
										<td>
											<fmt:formatNumber value="${v.fcount }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/>
										</td>
										<td>
											<fmt:formatNumber value="${v.fprice }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/>
										</td>
										<td>
											<fmt:formatNumber value="${v.ftotalCost }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/>
										</td>
										<td>
											<fmt:formatNumber value="${v.flastcount }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/>
										</td>
										<td>
											<fmt:formatNumber value="${v.foneqty }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/>
										</td>
										<td>
											<fmt:formatNumber value="${v.flastqty }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/>
										</td>
										<td class="">
											<fmt:formatDate  value="${v.fcreatetime }"  type="both"/>
										</td>
									</tr>
								</c:forEach>
								
								<c:if test="${fn:length(subscriptionlogs)==0 }">
								<tr>
										<td colspan="10" class="no-data-tips">
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