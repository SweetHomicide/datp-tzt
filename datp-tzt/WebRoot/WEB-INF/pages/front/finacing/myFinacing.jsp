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
			<div class="row" style="margin-bottom:20px;margin-left:10px;">
				<ul class="nav nav-pills nav-stacked ">
					<li class="" style="font-size: 18px; float: left;"><a
						href="/finacing/index.html?coinType="> 理财产品 </a></li>
					<li class="cur " style="font-size: 18px; float: left;"><a
						href="/finaWallet/view.html"> 我的理财 </a></li>
				</ul>
			</div>
			<div class="col-xs-12"
				style="padding: 20px auto; margin: 20px auto;">
				<ul class="nav nav-tabs">
					<li class="active"><a href="#">持有中</a></li>
					<!-- <li ><a href="#">已结束的理财</a></li> -->
					<li><a href="/web/buyRecord.html"> 交易记录</a></li>
					<li><a href="/profitLog/websearch.html"> 收益明细 </a></li>
				</ul>
			</div>
			<div class="row" style="background: #fff">
				<div class="col-xs-12">
					<c:forEach items="${listWalletRead}" var="list" varStatus="num">
						<div class="col-xs-12">
							<span style="font-size: 18px; fong-weight: bold;">${list.finaName }</span>
						</div>
						<div class="col-xs-12 finacing-block"
							style="width: 100%; height: 140px; padding-top: 10px; padding-bottom: 30px; border-bottom: 1px solid #eee;">
							<div class="col-xs-4">
								<div class="row" style="font-size: 20px; color: red;">
									<span><fmt:formatNumber
									value="${list.ftotal}" pattern="##.####" maxIntegerDigits="10"
									 /></span>
								</div>
								<div class="row" style="margin-top: 10px;">
									<span>持有资产(${list.fsymbol})</span>
								</div>
							</div>
							<div class="col-xs-4">
								<div class="row" style="font-size: 20px;">
									<span><fmt:formatNumber
									value="${list.profamount}" pattern="##.####" maxIntegerDigits="10"
									 /></span>
								</div>
								<div class="row" style="margin-top: 10px;">
									<span>昨日固定收益(${list.fsymbol})</span>
								</div>
							</div>
							<div class="col-xs-4">
								<div class="row" style="font-size: 20px; color: red;">
									<span><fmt:formatNumber
									value="${list.sumprofamount}" pattern="##.####" maxIntegerDigits="10"
									 /></span>
								</div>
								<div class="row" style="margin-top: 10px;">
									<span>累计固定收益(${list.fsymbol})</span>
								</div>
							</div>
							<c:if test='${list.fintype == "00101"}'>
								<div class="col-xs-4 col-xs-offset-8"
									style="font-size: 18px; fong-weight: bold; margin-top: 10px;">
									<div class="btn-group">
										<a href="/finacing/getFacingById.html?fid=${list.ffinaId}"
											class=" btn btn-default "> 继续购买</a> <a
											href="/finaWallet/kitingView.html?fid=${list.ffinaId}"
											class=" btn btn-default "> 提取 </a> <a
											href="/web/buyRecord.html?ffinaId=${list.ffinaId}"
											class=" btn btn-default "> 交易记录 </a> <a
											href="/profitLog/websearch.html?ffinaId=${list.ffinaId}"
											class=" btn btn-default "> 收益明细 </a>
									</div>
								</div>
							</c:if>
							<c:if test='${list.fintype=="00102" }'>
								<div class="col-xs-4 col-xs-offset-8"
									style="font-size: 18px; fong-weight: bold; margin-top: 10px;">
									<div class="btn-group">
										<a href="/finacing/getFacingById.html?fid=${list.ffinaId}"
											class=" btn btn-default "> 继续购买</a> <a
											href="/finaWallet/getDetailsFinacing.html?ffinaId=${list.ffinaId}"
											class=" btn btn-default "> 查看详情 </a>
									</div>
								</div>
							</c:if>
						</div>
					</c:forEach>

					<div id="fenye" style="float: right">${pagin}</div>
				</div>
			</div>
		</div>
	</div>

	<jsp:include page="../comm/footer.jsp"></jsp:include>

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

