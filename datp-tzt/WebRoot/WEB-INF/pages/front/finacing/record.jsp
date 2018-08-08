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
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<meta http-equiv="pragram" content="no-cache">
<meta http-equiv="cache-control" content="no-cache, must-revalidate">
<meta http-equiv="expires" content="0">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<jsp:include page="../comm/link.inc.jsp"></jsp:include>
<link href="/static/front/css/trade/trade.css" rel="stylesheet"
	type="text/css" media="screen, projection" />
</head>
<body>
	<style>
.trade-amount .datatime-sel, .trade-amount .datatime:hover {
	background: #41c7f9 none repeat scroll 0 0;
	color: #fff;
}

.trade-amount .databtn {
	cursor: pointer;
	display: inline-block;
	margin: 0 10px;
	padding: 5px;
	height: 55px;
	width: 40px;
}
</style>

	<jsp:include page="../comm/header.jsp"></jsp:include>
	<div class="container-full padding-top-30">
		<div class="container">
			<div class="col-xs-12 padding-right-clear">
				<ul class="nav nav-pills nav-stacked ">
					<li class="" style="font-size: 18px; float: left;"><a
						href="/finacing/index.html?coinType="> 理财产品 </a></li>
					<li class="cur " style="font-size: 18px; float: left;"><a
						href="/finaWallet/view.html"> 我的理财 </a></li>
				</ul>
			</div>
			<div class="col-xs-12"
				style="margin: 20px auto; padding-top: 10px; background: #fff">
				<ul class="nav nav-tabs">
					<li><a href="/finaWallet/view.html">持有中</a></li>
					<!-- <li ><a href="#">已结束的理财</a></li> -->
					<li class="active"><a href="/web/buyRecord.html"> 交易记录</a></li>
					<li><a href="/profitLog/websearch.html"> 收益明细 </a></li>
				</ul>
			</div>
			<div class="row" style="margin-top: 70px;">

				<div class="row"
					style="background: #fff; margin: 15px; padding: 15px;">
					<div class="col-xs-7 col-xs-offset-5">
						<div class="form-group">
							<span>起始时间：</span> <input class="databtn datainput"
								id="begindate" value="${tradeLog.beginTime }"
								readonly="readonly"></input> <span class="databtn datatips">—</span>
							<input class="databtn datainput" id="enddate"
								value="${tradeLog.endTime }" readonly="readonly"></input> <span>理财产品：</span>
							<input id="finaName" value="${tradeLog.finaName }"></input>
							<button class="" id="btnSearchRecord">搜索</button>
						</div>
					</div>
					<div class="row">
						<div class="col-xs-12"">
							<table class="table table-striped">
								<tbody>
									<tr>
										<td width="100" orderField="fname">时间</td>
										<td width="100" orderField="fproportion">产品</td>
										<td width="100" orderField="ftype">操作类型</td>
										<td width="60">金额</td>
									</tr>
									<c:forEach items="${list}" var="list" varStatus="num">
										<tr target="sid_user" rel="${list.fid}">
											<td>${list.fcreateTime}</td>
											<td>${list.finaName}</td>
											<c:if test="${list.ftype eq '00301'}">
												<td>买入</td>
											
											</c:if>
											<c:if test="${list.ftype eq '00302'}">
												<td>提取</td>
											
											</c:if>
											<td><fmt:formatNumber value="${list.famount}"
													pattern="##.####" maxIntegerDigits="10" />(${list.fsymbol})</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
							<div id="fenye" style="float: right">${pagin}</div>
						</div>
					</div>
				</div>



			</div>
		</div>
	</div>

	<jsp:include page="../comm/footer.jsp"></jsp:include>
	<script type="text/javascript"
		src="/static/front/js/plugin/My97DatePicker/WdatePicker.js"></script>
	<script type="text/javascript"
		src="/static/front/js/finacing/record.js"></script>

	<script>
		function checkNum(obj) {
			obj.value = obj.value.replace(/[^\d.]/g, ""); //清除“数字”和“.”以外的字符  
			obj.value = obj.value.replace(/\.{2,}/g, "."); //只保留第一个. 清除多余的  
			obj.value = obj.value.replace(".", "$#$").replace(/\./g, "")
					.replace("$#$", ".");
			obj.value = obj.value.replace(/^(\-)*(\d+)\.(\d\d\d\d).*$/,
					'$1$2.$3');//只能输入两个小数  
			if (obj.value.indexOf(".") < 0 && obj.value != "") {//以上已经过滤，此处控制的是如果没有小数点，首位不能为类似于 01、02的金额 
				obj.value = parseFloat(obj.value);
			}
		}
		function checkNum1(obj) {
			obj.value = obj.value.replace(/[^\d.]/g, ""); //清除“数字”和“.”以外的字符  
			obj.value = obj.value.replace(/\.{2,}/g, "."); //只保留第一个. 清除多余的  
			obj.value = obj.value.replace(".", "$#$").replace(/\./g, "")
					.replace("$#$", ".");
			obj.value = obj.value.replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3');//只能输入两个小数  
			if (obj.value.indexOf(".") < 0 && obj.value != "") {//以上已经过滤，此处控制的是如果没有小数点，首位不能为类似于 01、02的金额 
				obj.value = parseFloat(obj.value);
			}
		}
	</script>
</body>
</html>

