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
			<div class="row" style="margin-top: 70px;">
					<div class="row"
						style="background: #fff; margin: 15px; padding: 15px;">
						<div class="row" style="padding-bottom: 20px;">
							<div class="col-xs-4">
								<span style="font-size: 18px; fong-weight: bold;">${listWalletRead[0].finaName }详情信息</span>
							</div>
						</div>
						<div class="row">
							<div class="col-xs-12"">
								<table class="table ">
									<tbody>
										<tr>
											<td width="100" orderField="fname">持有资产(${listWalletRead[0].fsymbol })</td>
											<td width="100" orderField="fproportion">购买记录</td>
											<td width="60">操作</td>
										</tr>
										<c:forEach items="${listWalletRead}" var="list" varStatus="num">
											<tr target="sid_user" rel="${list.fid}">
												<td>${list.ftotal}</td>
												<td>${list.fcreateTime}</td>
												<td width="60"><a href="/finacing/getFacingById.html?fid=${list.ffinaId}">
													继续购买</a></td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
						</div>
					</div>
				

				<div id="fenye" style="float: right">${pagin}</div>
			</div>
		</div>
	</div>



	<%-- <input id="coinshortName" type="hidden"
		value="${fvirtualcointype.fShortName }">
	<input id="symbol" type="hidden" value="${fvirtualcointype.fid }">
	<input id="isopen" type="hidden" value="${needTradePasswd }">
	<input id="tradeType" type="hidden" value="0">
	<input id="userid" type="hidden" value="${userid }">

	<input id="tradePassword" type="hidden" value="${isTradePassword }">
	<input id="isTelephoneBind" type="hidden" value="${isTelephoneBind }">
	<input id="fpostRealValidate" type="hidden"
		value="${fpostRealValidate }">
	<input id="coinCount" type="hidden" value="${fvirtualcointype.fcount }">
	<input id="exprice" type="hidden" value="${fsubscription.fprice }">
	<input id="limitedType" type="hidden" value="0"> --%>


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

