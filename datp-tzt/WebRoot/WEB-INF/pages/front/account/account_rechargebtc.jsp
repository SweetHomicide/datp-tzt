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

<link rel="stylesheet" href="/static/front/css/finance/recharge.css" type="text/css"></link>
<link href="/static/front/css/trade/trade.css" rel="stylesheet" type="text/css" media="screen, projection" />
</head>
<body>
	



 <%@include file="../comm/header.jsp" %>

	<div class="container-full padding-top-30">
		<div class="container">
		
		
			<%@include file="../comm/left_menu.jsp" %>
			
			
			<div class="col-xs-10 padding-right-clear">
				<div class="col-xs-12 padding-right-clear padding-left-clear rightarea recharge">
					<div class="col-xs-12 rightarea-con">
						<%-- <ul class="nav nav-tabs rightarea-tabs">
							<li>
								<a href="/account/rechargeCny.html?type=1">人民币转入</a>
							</li>
							<c:forEach items="${requestScope.constant['allWithdrawCoins'] }" var="v">
								<li class="${v.fid==symbol?'active':'' }">
									<a href="/account/rechargeBtc.html?symbol=${v.fid }">${v.fShortName } 转入</a>
								</li>
							</c:forEach>
							
						</ul> --%>
						<div class="col-xs-4" style="background:#fff;" id="div-checkCoin">	
						<div class="all_coin_info2">
							<span class="lefticon col-xs-2"
					style="top:5px;margin-right:5px;width:50px;height:50px;background-image: url('${fvirtualcointype.furl }') ;background-size:100%;"></span>
							<a href="javascript:void(0);" rel="drevil" title="请选择"  class="cointype" id="cointype"> <!-- onmouseover="changeDown()" onmouseout="changeUp()" -->${fvirtualcointype.fname }</a>
							<span  style="float:right;margin-top:20px;" class="arrow-down" id="icon1"></span>
						</div>
						
						<div id="divx4"
							style="display: none; z-index: 1000; width: 500px; background: #fff; position: absolute; border: 1px solid #ccc; top: 45px; left: 0px;">
							<div class="row" style="padding-top: 15px;">
								<div class="col-xs-6 col-xs-offset-5">

									<div class="input-group form-group">
										<input type="text" class="form-control searchname"
											name="searchName" id="searchname" placeholder="输入名称" /> <span
											class="input-group-btn">
											<input type="hidden" id="hidlog"/>
											<button class="btn btn-primary" id="searchType"
												onclick="searchType1(1);"><i class="glyphicon glyphicon-search"></i></button>
									</div>
								</div>

							</div>
							<div style="border:1px solid #f8f8f8;margin-bottom:10px;"></div>
							<%-- <div id="div5">
								<c:forEach items="${requestScope.constant['allWithdrawCoins'] }" var="v">
									<div class="col-xs-4 ${v.fid==symbol?'active':'' }">
										<span class=" col-xs-2"
											style="line-height:40px;width:30px;height:30px;background-image: url('${v.furl }') ;background-size:100%;"></span>
										<a href="/account/rechargeBtc.html?symbol=${v.fid }">${v.fShortName } 转入</a>
									</div>
								</c:forEach>
							</div>	 --%>
							<div id="divx2">
								
							</div>
							<div class="row" style="padding-bottom:10px;">
									<div class="col-xs-12">
										<input type="hidden" value="${cur_page }" name="currentPage" id="currentPage"></input>
											<span style="float:right; margin-right:10px;" id="pagex1">
												${page}
											</span>
									</div>
							</div>
						</div>
					</div>
					<div class="row" style="border-bottom:2px solid #f8f8f8"></div>
					
						<div class="col-xs-12 padding-clear padding-top-30 recharge-qrcodetext">
							<div class="col-xs-2 text-right">
								<span>转入地址</span>
							</div>
							<div class="col-xs-7 recharge-qrcodecon address">
								
									<span class="code-txt">${fvirtualaddress.fadderess}</span>
									<span class="code-box">
										<span class="qrcode" id="qrcode"></span>
									</span>
								
								
							</div>
						</div>
						<div class="col-xs-12 padding-clear padding-top-30">
							<div class="panel panel-tips">
								<div class="panel-header text-center text-danger">
									<span class="panel-title">转入须知</span>
								</div>
								<div class="panel-body">
									<p>&lt ${fvirtualcointype.fname }转入须知。</p>
									<p>&lt 您转入${fvirtualcointype.fname }上述地址后，需要至少${virConfirs}个${fvirtualcointype.fname }网络节点的确认。</p>
									<p>&lt 最小转入金额是：1 您的${fvirtualcointype.fname }地址不会改变，可以重复转入，如有更改，我们会通过网站公告或邮件通知您。</p>
								</div>
							</div>
						</div>
						<div class="col-xs-12 padding-clear padding-top-30">
							<div class="panel border">
								<div class="panel-heading">
									<span class="text-danger">${fvirtualcointype.fname }转入记录</span>
								</div>
								<div class="panel-body" id="recordbody0">
									<table class="table">
										<tr>
											<td>最后更新</td>
											<!-- <td>转入地址</td> -->
											<td>转入数量</td>
											<td>确认数</td>
											<td>状态</td>
										</tr>
										<c:forEach items="${fvirtualcaptualoperations }" var="v" varStatus="vs">
											<tr>
											    <td width="200"><fmt:formatDate value="${v.flastUpdateTime }"
																	pattern="yyyy-MM-dd HH:mm:ss" /></td>
												<!-- <td width="350">${v.recharge_virtual_address }</td>-->
												<td width="100"><fmt:formatNumber value="${v.famount }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/></td>
												<td width="100">${v.fconfirmations }</td>
												<td width="100">${v.fstatus_s }</td>
											</tr>
										</c:forEach>
										<c:if test="${fn:length(fvirtualcaptualoperations)==0 }">
											<tr>
												<td align="center" colspan="5">
													<span class="no-data-tips">
														您暂时没有转入数据
													</span>
												</td>
											</tr>
										</c:if>
										
									</table>
									
									<input type="hidden" value="${cur_page }" name="currentPage" id="currentPage"></input>
											<div class="text-right">
												${pagin }
											</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	




 <%@include file="../comm/footer.jsp" %>


	<input type="hidden" id="symbol" value="4">
	<script type="text/javascript" src="/static/front/js/finance/account.recharge.js"></script>
	<script type="text/javascript" src="/static/front/js/plugin/jquery.qrcode.min.js"></script>
		<script type="text/javascript" src="/static/front/js/trade/dongtai.js"></script>
	<script type="text/javascript">
		jQuery(document).ready(function() {
			if (navigator.userAgent.indexOf("MSIE") > 0) {
				jQuery('#qrcode').qrcode({
					text : '${fvirtualaddress.fadderess}',
					width : "149",
					height : "143",
					render : "table"
				});
			} else {
				jQuery('#qrcode').qrcode({
					text : '${fvirtualaddress.fadderess}',
					width : "149",
					height : "143"
				});
			}
		});
	</script>
</body>
</html>
