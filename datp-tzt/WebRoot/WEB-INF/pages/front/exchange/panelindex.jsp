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

<link rel="stylesheet" href="/static/front/css/finance/withdraw.css"
	type="text/css"></link>
<link href="/static/front/css/trade/trade.css" rel="stylesheet"
	type="text/css" media="screen, projection" />
</head>
<body>

	<%@include file="../comm/header.jsp"%>

	<div class="container-full padding-top-30">
		<div class="container" style="background: #fff">

			<div class="row" style="margin-top: 10px;">
				<div class="col-xs-3 col-xs-offset-9" style="right: 10px">

					<form action="/exchange/index.html?type=0">
						<div class="input-group form-group">
							  
							<input type="text" class="form-control searchname"
								name="searchName" id="searchname" placeholder="输入兑换名称/资产名称"
								onkeypress="if(event.keyCode==13) {pageForEx(1);}"
								value="${searchName}" /> <span class="input-group-btn">
								<input type="hidden" name="type" id="type" value="0" />
								<button type="submit" class="btn btn-danger" id="searchType"
									onclick="searchEx(1);"><i class="glyphicon glyphicon-search"></i></button>
						</div>
					</form>
				</div>
			</div>
			<div style="min-height: 100px; background: #fff">
				<c:forEach items="${fsubscription1 }" var="v" varStatus="vs">
					<div class="col-xs-4">
						<div class="imgpanel">
							<div style="padding-top: 26px; padding-left: 80px;">
								<a href="/exchange/index.html?id=${v.fid }&type=1" style="text-decoration:none"><span
									style="line-height: 65px; color: #fff; font-size: 15px">${v.ftitle }</span></a>
							</div>
							<div class="" style="padding-top: 15px; padding-left: 225px;">
								
								<span class="lefticon col-xs-1"
									style="width:30px;height:30px;background-image: url('${v.fvirtualcointypeCost.furl }') ;background-size:100%;"></span>
								<span class="lefticon col-xs-1"
									style="width: 50px; height: 30px; line-height: 30px;">-&gt</span>
								<span class="lefticon col-xs-1"
									style="width:30px;height:30px;background-image: url('${v.fvirtualcointype.furl }') ;background-size:100%;"></span>
							</div>
							
							<div class="col-xs-12 jianjie" >
							
							<div class="col-xs-12">
									<div class="timeicon">
										<fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss"
											value="${v.fbeginTime }" />
										~
										<fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss"
											value="${v.fendTime }" />
									</div>
							</div>
							
								<div class="col-xs-12">
								
									<p
										class="priceicon ">
									换购价：${v.fprice }
										${v.fvirtualcointypeCost.fShortName}=1.0
										${v.fvirtualcointype.fShortName }
										</p>
								</div>
								<div class="col-xs-12">
										<c:if
											test="${v.ftotal-v.fAlreadyByCount >0 }">
											<div
										class="counticon">
										可换购数量：
											<fmt:formatNumber
												value="${v.ftotal-v.fAlreadyByCount }"
												pattern="##.##" maxIntegerDigits="20" maxFractionDigits="4" />
												</div>
										</c:if>
										<c:if
											test="${v.ftotal-v.fAlreadyByCount <=0 }">
											<div
										class="counticon">
										可换购数量：
											<fmt:formatNumber value="0" pattern="##.##"
												maxIntegerDigits="20" maxFractionDigits="4" /></div>
										</c:if>
									</div>
									<div class="col-xs-3 col-xs-offset-8 goin" >
										<a href="/exchange/index.html?id=${v.fid }&type=1" class="btn ">立即兑换</a>
									</div> 
								</div>
						</div>
					</div>
				</c:forEach>
			</div>
			<div class="row" style="padding-bottom: 10px;">
				<div class="col-xs-12">
					<input type="hidden" value="${cur_page }" name="currentPage"
						id="currentPage"></input> <span
						style="float: right; margin-right: 10px;" id="pagex">
						${page} </span>
				</div>
			</div>
			
		</div>

		<input type="hidden" value="${fsubscription.fprice }" id="subRate" />
		<input type="hidden" value="${fsubscription.fid }" id="fid" /> <input
			type="hidden" value="${count}" id="count" />

		<%@include file="../comm/footer.jsp"%>
		<script type="text/javascript" src="/static/front/js/comm/msg.js"></script>
		<script type="text/javascript" src="/static/front/js/exchange/ex.js"></script>
		<script type="text/javascript" src="/static/front/js/trade/dongtai.js"></script>
</body>
</html>
