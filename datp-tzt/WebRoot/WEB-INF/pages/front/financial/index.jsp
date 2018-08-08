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

<link rel="stylesheet" href="/static/front/css/finance/index.css"
	type="text/css"></link>
</head>
<body>

	<%@include file="../comm/header.jsp"%>

	<div class="container-full padding-top-30">
		<div class="container">

			<%@include file="../comm/left_menu.jsp"%>

			<div class="col-xs-10 padding-right-clear">
				<div
					class="col-xs-12 padding-right-clear padding-left-clear rightarea user">
					<div class="col-xs-12 rightarea-con ">
						<div class="index-top-icon clearfix">
							<div class="col-xs-5 text-center padding-top-30">
								<span>预估总资产(￥)</span> <span class="text-danger font-size-40">
									￥<fmt:formatNumber value="${totalCapitalTrade }"
										pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4" />
								</span>
							</div>
							<div class="col-xs-5 text-center padding-top-30">
								<span></span> <span class="text-danger font-size-40"> </span>
							</div>
							<div class="col-xs-2 text-center">
								<a href="/account/rechargeCny.html"
									class="btn btn-danger btn-recharge">充值</a>
							</div>
						</div>
						<div class="row" style="margin-top:10px;">
						<div class="col-xs-4 col-xs-offset-8">
							<div class="input-group">
								<input type="text" class="form-control input" id="searchCon"><span
									class="input-group-addon btn btn-primary" id="btnSearch"><i class="glyphicon glyphicon-search"></i></span>
										<inpupt type="hidden" id="hidSearch" value="" />
							</div>
						</div>
					</div>
						<div class="col-xs-12 padding-clear padding-top-30">
							<table class="table table-striped text-center" id="zichan">
								<tr class="bg-danger">
									<td class="col-xs-3 border-top-clear">资产名称</td>
									<td class="col-xs-3 border-top-clear">可用资产</td>
									<td class="col-xs-3 border-top-clear">冻结资产</td>
									<td class="col-xs-3 border-top-clear">总量</td>
								</tr>
								<tr>
									<td class="col-xs-3 border-top-clear">人民币</td>
									<td class="col-xs-3 border-top-clear">￥<fmt:formatNumber
											value="${requestScope.fwallet.ftotalRmb }" pattern="##.##"
											maxIntegerDigits="10" maxFractionDigits="4" />
									</td>
									<td class="col-xs-3 border-top-clear">￥<fmt:formatNumber
											value="${requestScope.fwallet.ffrozenRmb }" pattern="##.##"
											maxIntegerDigits="10" maxFractionDigits="4" />
									</td>
									<td class="col-xs-3 border-top-clear">￥<fmt:formatNumber
											value="${requestScope.fwallet.ftotalRmb+requestScope.fwallet.ffrozenRmb }"
											pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4" />
									</td>
								</tr>

								<c:forEach items="${requestScope.fvirtualwallet }" var="v"
									varStatus="vs" begin="0">
									<tr class="td1">
										<td class="col-xs-3 border-top-clear">${v.fvirtualcointype.fname }</td>
										<td class="col-xs-3 border-top-clear"><fmt:formatNumber
												value="${v.ftotal }" pattern="##.##" maxIntegerDigits="10"
												maxFractionDigits="4" /></td>
										<td class="col-xs-3 border-top-clear"><fmt:formatNumber
												value="${v.ffrozen }" pattern="##.##" maxIntegerDigits="10"
												maxFractionDigits="4" /></td>
										<td class="col-xs-3 border-top-clear"><fmt:formatNumber
												value="${v.ftotal+v.ffrozen }" pattern="##.##"
												maxIntegerDigits="10" maxFractionDigits="4" /></td>
									</tr>
								</c:forEach>
							</table>
							<div id="fenye" style="float: right">${pagin}</div>
							<input type="hidden" class="hidcount" id="hidcount" value="${count}" />
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>




	<%@include file="../comm/footer.jsp"%>
	<script>
	
	$(function(){
		$("#btnSearch").on("click",function(){
			var con=$("#searchCon").val();
			$("#hidSearch").val(con);
			var count=$("#hidcount").val();
			var url="/financial/index1.html?random=" + Math.round(Math.random() * 100);
			$.ajax({
				url:url,
				data:{
					keyWord:con,
					count:count
				},
				success: function(data){
					$(".td1").remove();
					$.each(data.result,function(key,value){
						$("#zichan").append("<tr class='td1'>" +
								"<td class='col-xs-3 border-top-clear'>"+value.fcoinname+"</td>"+
								"<td class='col-xs-3 border-top-clear'>"+Number(value.ftotal)+"</td>"+
								"<td class='col-xs-3 border-top-clear'>"+value.frozen+"</td>"+
								"<td class='col-xs-3 border-top-clear'>"+value.ftotalandfrozen+"</td>"+
								"</tr>");
					});
					$("#fenye").html("");
					$("#fenye").html(data.pagin);
				},
				error:function(data){
					alert("数据查询异常！");
				}
			});
		});
	});
	
	function ajaxPersonalAssets(current){
		var hidSearch=$("#hidSearch").val();
		var count=$("#hidcount").val();
		var url="/financial/index1.html?random=" + Math.round(Math.random() * 100);
		
		$.ajax({
			url:url,
			data:{
				currentPage:current,
				count:count,
				keyWord:hidSearch
			},
			success: function(data){
				$(".td1").remove();
				$.each(data.result,function(key,value){
					$("#zichan").append("<tr class='td1'>" +
							"<td class='col-xs-3 border-top-clear'>"+value.fcoinname+"</td>"+
							"<td class='col-xs-3 border-top-clear'>"+Number(value.ftotal)+"</td>"+
							"<td class='col-xs-3 border-top-clear'>"+value.frozen+"</td>"+
							"<td class='col-xs-3 border-top-clear'>"+value.ftotalandfrozen+"</td>"+
							"</tr>");
				});
				$("#fenye").html("");
				$("#fenye").html(data.pagin);
			},
			error:function(data){
				alert("数据查询异常！");
			}
		
		});
	
	}
</script>
</body>
</html>