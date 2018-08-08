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
<link rel="stylesheet" href="/static/front/css/finance/accountrecord.css" type="text/css"></link>
</head>
<body>

 <%@include file="../comm/header.jsp" %>

	<div class="container-full padding-top-30">
		<div class="container">
			
<%@include file="../comm/left_menu.jsp" %>

			<div class="col-xs-10 padding-right-clear">
				<div class="col-xs-12 padding-right-clear padding-left-clear rightarea assetsrecord record">
					<div class="col-xs-12 rightarea-con">
						<div class="col-xs-12 padding-clear">
						<div class="form-group">
								<span >起始时间：</span>
								<input class="databtn datainput" id="begindate" value="${begindate }" readonly="readonly" placeholder="选择时间"></input>
								<span class="databtn datatips">—</span>
								<input class="databtn datainput" id="enddate" value="${enddate }" readonly="readonly"  placeholder="选择时间"></input>
								<span class="databtn datatime ${datetype==1?'datatime-sel':'' }" data-type="1">今天</span>
								<span class="databtn datatime ${datetype==2?'datatime-sel':'' }" data-type="2">7天</span>
								<span class="databtn datatime ${datetype==3?'datatime-sel':'' }" data-type="3">15天</span>
								<span class="databtn datatime ${datetype==4?'datatime-sel':'' }" data-type="4">30天</span>
								<input type="hidden" id="datetype" value="${datetype }">
							</div>
							<table class="table table-striped text-center">
								<tbody>
								<tr class="bg-danger">
									<td>日期</td>
									<td>类型</td>
									<td>人民币(￥)</td>
									<%-- <c:forEach var="v" varStatus="vs" items="${fvirtualcointypes }">
										<td>${v.fname }(${v.fSymbol })</td>
									</c:forEach> --%>
									<td>数字总资产</td>
									<td>预估总资产(￥)</td>
								</tr>
								
								<c:forEach items="${list }" var="v" varStatus="vs">
									<tr>
										<td>
											<span class="second borderright">
												<fmt:formatDate value="${v.flastupdatetime }" pattern="yyyy-MM-dd"/>
											</span>
										</td>
										<td>
											<span class="borderbottom borderright">可用</span>
											<span class="borderright">冻结</span>
										</td>
										
										<td>
											<span class="borderbottom borderright">
													<fmt:formatNumber value="${v.list.get(0).value1 }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/>
												</span>
												<span class="borderbottom borderright">
													<fmt:formatNumber value="${v.list.get(0).value2 }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/>
												</span>
										</td>
										<td>
											<span class="second borderright">
													<a href='financial/assetsrecordvirual.html?time=<fmt:formatDate value="${v.flastupdatetime }" pattern="yyyy-MM-dd"/>'><fmt:formatNumber value="${v.ftotal-v.list.get(0).value2-v.list.get(0).value1 }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/></a>
												</span>
												
										</td>
										<td>
											<span class="second">
												<fmt:formatNumber value="${v.ftotal }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/>
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
<script type="text/javascript" src="/static/front/js/plugin/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="/static/front/js/finance/assets.record.js"></script>

</body>
</html>