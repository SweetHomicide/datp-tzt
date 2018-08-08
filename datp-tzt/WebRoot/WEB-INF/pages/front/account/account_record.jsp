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

<link rel="stylesheet" href="/static/front/css/finance/accountrecord.css" type="text/css"></link>
</head>
<body>
	




  <%@include file="../comm/header.jsp" %>

	<div class="container-full padding-top-30">
		<div class="container">
			
			<%@include file="../comm/left_menu.jsp" %>
			
			
			<div class="col-xs-10 padding-right-clear">
				<div class="col-xs-12 padding-right-clear padding-left-clear rightarea record">
					<ul class="nav nav-tabs rightarea-tabs">
							<li class="active">
								<a href="javascript:void(0)">人民币明细</a>
							</li>
							<li class="">
								<a href="/account/record.html?isRMB=1">数字资产明细</a>
							</li>
					</ul>
					<div class="panel">
						<div class="panel-heading padding-bottom-clear">
							<div class="form-group">
								<span >起始时间：</span>
								<input class="databtn datainput" id="begindate" value="${begindate }" readonly="readonly"></input>
								<span class="databtn datatips">—</span>
								<input class="databtn datainput" id="enddate" value="${enddate }" readonly="readonly"></input>
								<span class="databtn datatime ${datetype==1?'datatime-sel':'' }" data-type="1">今天</span>
								<span class="databtn datatime ${datetype==2?'datatime-sel':'' }" data-type="2">7天</span>
								<span class="databtn datatime ${datetype==3?'datatime-sel':'' }" data-type="3">15天</span>
								<span class="databtn datatime ${datetype==4?'datatime-sel':'' }" data-type="4">30天</span>
								<input type="hidden" id="datetype" value="${datetype }">
							</div>
							<div class="form-group">
								<span>操作类型：</span>
								<select class="form-control typeselect" id="recordType">
									<c:forEach items="${filters1 }" var="v">
									 	<c:choose>
									 		<c:when test="${select==v.value}">
												<option value="${v.key }" selected="selected">${v.value }</option>
									 		</c:when>
									 		<c:otherwise>
												<option value="${v.key }">${v.value }</option>
									 		</c:otherwise>
									 	</c:choose>
									</c:forEach>
								</select>
							</div>
						</div>
						<div class="panel-body" id="fentrustsbody0">
							<table class="table table-striped" style="text-align:left;">
								<tr class="bg-danger">
									<th width="200">
										交易时间
									</th>
									<!-- <th width="160">
										类型
									</th> -->
									<!-- <th width="150">
										委托数量
									</th> -->
									<th width="120">
										金额
									</th>
									<th width="120">
										手续费
									</th>
									<!-- <th width="150">
										平均成交价
									</th> -->
									<th width="120">
										状态
									</th>
								</tr>
								
								<c:choose>
						<c:when test="${recordType==1 || recordType==2 }">
							<%--人民币充值、提现--%>
							<c:forEach items="${list }" var="v">
							<tr>
								<td><fmt:formatDate value="${v.fcreateTime }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
								<%-- <td>${select }</td> --%>
								<!-- <td class="red">--</td> -->
								<td class="red">￥<fmt:formatNumber value="${v.famount }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/></td>
								<td>￥<fmt:formatNumber value="${v.ffees }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/></td>
								<!-- <td>--</td> -->
								<td>${v.fstatus_s }</td>
							</tr>
							</c:forEach>
						</c:when>
						
						<c:otherwise>
							<%--交易--%>
							<c:forEach items="${list }" var="v">
							<tr>
								<td><fmt:formatDate value="${v.fcreateTime }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
								<td>${select }</td>
								<td class="red">${v.fvirtualcointype.fSymbol }<fmt:formatNumber value="${v.fcount }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/></td>
								<td class="red">￥<fmt:formatNumber value="${v.famount }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/></td>
								<td>
								<c:choose>
								<c:when test="${v.fentrustType==0 }">
								${v.fvirtualcointype.fSymbol }<fmt:formatNumber value="${v.ffees }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/>
								</c:when>
								<c:otherwise>
								￥<fmt:formatNumber value="${v.ffees }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/>
								</c:otherwise>
								</c:choose>
								</td>
								<td>
								￥<fmt:formatNumber value="${((v.fcount-v.fleftCount)==0)?0:(v.famount/(v.fcount-v.fleftCount)) }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/>
								</td>
								<td>${v.fstatus_s }</td>
							</tr>
							</c:forEach>
						</c:otherwise>
					    </c:choose>
									
								
						<c:if test="${fn:length(list)==0 }">		
							<tr>
								<td colspan="7" class="no-data-tips">
									<span >
										您暂时没有账单记录
									</span>
								</td>
							</tr>
						</c:if>
						
								
								</tbody>
							</table>
							
						</div>
						
<c:if test="${!empty(pagin) }">
<div class="text-right">
${pagin }
</div>
</c:if>						
						
						
					</div>
				</div>
			</div>
		</div>
	</div>
	
 
<%@include file="../comm/footer.jsp" %>	


	<script type="text/javascript" src="/static/front/js/plugin/My97DatePicker/WdatePicker.js"></script>
	<script type="text/javascript" src="/static/front/js/finance/account.record.js"></script>
</body>
</html>
