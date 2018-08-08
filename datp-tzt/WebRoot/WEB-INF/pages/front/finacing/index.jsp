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

.finacingList tr {
	height: 92px;
	font-size: 16px;
}

.finacingList span {
	font-size: 16px;
	margin-top: 20px;
}

.finacingList th {
	text-align: left;
}

.operat {
	color: #ff5d5b;
	padding: 10px 40px;
	border-radius: 4px;
	border: 1px solid #ff5d5b;
}

.operat:hover {
	color: #fff;
	padding: 10px 40px;
	background: #ff5d5b;
	border-radius: 4px;
	border: 1px solid #ff5d5b;
	text-decoration:none;
}

.finacing-block {
	border-bottom:1px solid #eee;
	margin:15px auto;
}
</style>

	<jsp:include page="../comm/header.jsp"></jsp:include>
	<div class="container-full padding-top-30">
		<div class="container">
			<div class="col-xs-12 padding-right-clear">
				<ul class="nav nav-pills nav-stacked ">
					<li class="cur" style="font-size: 18px; float: left;"><a
						href="/finacing/index.html?coinType="> 理财产品 </a></li>
					<li class=" trademenu${v.fid }"
						style="font-size: 18px; float: left;"><a
						href="/finaWallet/view.html"> 我的理财 </a></li>
				</ul>
			</div>
			
			<div class="col-xs-12"
				style="background: #fff; margin-top: 20px;padding-left:20px;padding-top:30px;">
				<c:forEach items="${list}" var="finac" varStatus="num">
					<div class="col-xs-12"><span>${finac.fname}</span></div>
					<div class="col-xs-12 finacing-block" style="width:100%;height:100px;padding-top:10px;">
						<div class="col-xs-2" >
							<div class="row" style="font-size:20px;color:red;">
								<span>${finac.fproportion}%</span>
							</div>
							<div class="row" style="margin-top:10px;">
								<span>收益</span>
							</div>
						</div>
						<div class="col-xs-2">
							<div class="row" style="font-size:20px;">
								<span>${finac.fcycle}</span>
							</div>
							<div class="row" style="margin-top:10px;">
								<span>结算周期(天)</span>
							</div>
						</div>
						<div class="col-xs-2" >
						<div class="row" style="font-size:20px;color:red;">
								<span><fmt:formatNumber
									value="${finac.tenThsPro}" pattern="##.####" maxIntegerDigits="10"
									 /></span>
							</div>
						<div class="row" style="margin-top:10px;">
								<span>万份收益(${finac.fsymbol})</span>
							</div>
						</div>
						<div class="col-xs-2">
							<div class="row" style="font-size:20px;">
								<c:if test="${finac.ftype eq '00101' }">
								<span>灵活存取</span>
								</c:if>
								<c:if test="${finac.ftype eq '00102' }">
								<span>固定结算</span>
								</c:if>
							</div>
							<div class="row" style="margin-top:10px;">
								<span>产品期限</span>
							</div>
						</div>
						<div class="col-xs-4" style="line-height:50px;">
							<a href="/finacing/getFacingById.html?fid=${finac.fid}"
												class="operat">买入</a>
						</div>
					</div>
				</c:forEach>
				<div id="fenye" style="float: right">${pagin}</div>
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

