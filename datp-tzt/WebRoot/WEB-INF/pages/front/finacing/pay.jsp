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

.strong {
	font-size: 28px;
	font-weight: normal
}

.strong-num {
	font-size: 30px;
	font-weight: normal;
	color: red;
}

.form-horizontal span {
	font-size: 16px;
}

.text-left {
	font-size: 16px;
	text-align: left;
}

.text-right {
	font-size: 16px;
	text-align: right;
}
</style>

	<jsp:include page="../comm/header.jsp"></jsp:include>


	<div class="container-full padding-top-30">
		<div class="container">
			<div class="col-xs-12 padding-right-clear">
				<ul class="nav nav-pills nav-stacked ">
					<li class="cur" style="font-size: 18px; float: left;"><a
						href="/finacing/index.html?coinType="> 理财产品 </a></li>
					<li style="font-size: 18px; float: left;"><a
						href="/finaWallet/view.html"> 我的理财 </a></li>
				</ul>
			</div>
			<div class="row"
				style="background: #fff; margin-top: 60px; padding-top: 20px; padding-bottom: 20px;">
				<div class="col-xs-12" style="border-bottom: 1px solid #eee">
					<div class="form-group">
						<span class="strong" style="padding-bottom: 10px;">${ finacing.fname}</span>
					</div>
				</div>
				<div class="col-xs-12">
					<div class="col-xs-7">
						<form role="form" class="form-horizontal"
							style="margin: 25px; padding: 20px;">
							<div class="form-group">
								<div class="col-xs-4 text-right">
									<span for="name">万份收益:</span>
								</div>
								<div class="col-xs-4 text-left">
									<span style="color: #FB5A5C"><fmt:formatNumber
									value="${ finacing.tenThsPro}" pattern="##.####" maxIntegerDigits="10"
									 />(${fvirtualwallet.fvirtualcointype.fSymbol})</span>
								</div>
							</div>
							<div class="form-group">
								<div class="col-xs-4 text-right">
									<span for="name"line-height:30px;>收益率:</span>
								</div>
								<div class="col-xs-4 text-left">
									<span style="color: #FB5A5C;">${ finacing.fproportion}%</span>
								</div>
							</div>
							<div class="form-group">
								<div class="col-xs-4 text-right">
									<span for="name">结算周期:</span>
								</div>
								<div class="col-xs-4 text-left">
									<span>${finacing.fcycle}天</span>
								</div>
							</div>
							<div class="form-group">
								<div class="col-xs-4 text-right">
									<span for="name">产品期限:</span>
								</div>
								<div class="col-xs-4 text-left">
									<span class="text-left">${finacing.fdtype}</span>
								</div>
							</div>
						</form>
					</div>
					<div class="col-xs-5">
						<div
							style="background: #FEFAFA; margin: 25px; padding: 20px; min-height: 200px; border-top: 4px solid #337AB7; border-left: 2px solid #eee; border-right: 2px solid #eee; border-bottom: 2px solid #eee;">

							<form role="form" class="form-horizontal">
								<div class="form-group">
									<div class="col-xs-4 text-right">
										<span for="name">当前余额</span>
									</div>
									<span><fmt:formatNumber
									value="${fvirtualwallet.ftotal}" pattern="##.####" maxIntegerDigits="10"
									 />(${fvirtualwallet.fvirtualcointype.fSymbol})</span>
								</div>
								<div class="form-group">
									<div class="col-xs-4 text-right">
										<span for="finacingCount">买入金额</span>
									</div>
									<input type="text" style="padding: 3px;" id="finacingCount" onkeyup="checkNum(this)">
								</div>
								<div class="form-group">
									<div class="col-xs-4 text-right">
										<span for="tradePwd">交易密码</span>
									</div>
									<input type="password" style="padding: 3px;" id="tradePwd">
								</div>
							</form>
							<button id="btnPay"
								class="btn btn-primary col-xs-offset-1 col-xs-8">买入</button>
						</div>
					</div>
				</div>
			</div>
			<div class="row"
				style="background: #fff; margin-top: 30px; padding-top: 20px;">
				<ul id="myTab" class="nav nav-tabs">
					<c:forEach items="${list}" var="finac" varStatus="num">
						<c:if test="${ num.index==0}">
							<li class="active"><a href="#${finac.fid}" data-toggle="tab"
								style="font-size: 18px;">${finac.ftitle}</a></li>
						</c:if>
						<c:if test="${ num.index!=0}">

							<li><a href="#${finac.fid}" data-toggle="tab"
								style="font-size: 18px;">${finac.ftitle}</a></li>
						</c:if>
					</c:forEach>
				</ul>
				<div id="myTabContent" class="tab-content">
					<c:forEach items="${list}" var="finac" varStatus="num">
						<c:if test="${ num.index==0}">
							<div class="tab-pane fade in active" id="${finac.fid}"
								style="min-height: 100px; padding: 20px;text-indent:25px">
								${finac.fcontent}</div>
						</c:if>
						<c:if test="${ num.index!=0}">
							<div class="tab-pane fade in " id="${finac.fid}"
								style="min-height: 100px; padding: 20px;text-indent:25px">
								${finac.fcontent}</div>
						</c:if>
					</c:forEach>
				</div>
			</div>
		</div>
	</div>



	<input id="hid_ffinaId" type="hidden" value="${finacing.fid }">

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
		$(function() {
			$("#btnPay").on(
					"click",
					function() {
						var pwd = $("#tradePwd").val(), count = $(
								"#finacingCount").val(), ffinaId = $(
								"#hid_ffinaId").val();
						if (count == "") {
							alert("购买金额不能为空");
							return false;
						}
						if (pwd == "") {
							alert("交易密码不能为空");
							return false;
						}
						$.ajax({
							type : "POST",
							url : "/web/trade/save.html",
							dataType : "json",
							data : {
								pwd : pwd,
								famount : count,
								ffinaId : ffinaId
							},
							success : function(data) {
								if (data.success) {
									alert(data.message);
									location.href = "/web/buyRecord.html"
								} else {
									alert(data.message);
								}
							}
						});

					});
		});
	</script>
</body>
</html>

