<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="../comm/include.inc.jsp"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;
%>

<!doctype html>
<html>
<head>
<jsp:include page="../comm/link.inc.jsp"></jsp:include>
<link href="/static/front/css/trade/trade.css" rel="stylesheet" type="text/css" media="screen, projection" />
<link rel="stylesheet" href="/static/front/css/user/about.css" type="text/css"></link>
</head>
<body>
	
<jsp:include page="../comm/header.jsp"></jsp:include>
	<div class="container-full padding-top-30">
		<div class="container">
			<%@include file="../comm/left_menu.jsp" %>
			<div class="col-xs-12  padding-right-clear">
				<div class="row" style="background:#fff">
			<div class="col-xs-3" style="background:#fff;" id="popupjieshao">	
						<div class="all_coin_info1">
						<span class="lefticon col-xs-2"
					style="margin-right:5px;top:5px;width:50px;height:50px;background-image: url('${fvirtualcointype.furl }') ;background-size:100%;"></span>
							<a href="#" rel="drevil" title="请选择"  class="cointype" id="cointype"> <!-- onmouseover="changeDown()" onmouseout="changeUp()" -->${fvirtualcointype.fname }</a>
							<span  style="float:right;margin-top:20px;" class="arrow-down" id="icon1"></span>
						</div>

						<div id="Popup"
							style="display: none; z-index: 1000; width: 500px; background: #fff; position: absolute; border: 1px solid #ccc; top: 65px; left: 0px;">
							<div class="row" style="padding-top: 15px;">
								<div class="col-xs-6 col-xs-offset-6" style="right:10px">

									<div class="input-group form-group">
										<input type="text" class="form-control searchname"
											name="searchName" id="searchname" placeholder="输入名称" onkeypress="if(event.keyCode==13) {searchType(1);}"  /> <span
											class="input-group-btn">
											<input type="hidden" id="hidlog"/>
											<button class="btn btn-primary" id="searchType"
												onclick="introduceSearchType(1);"><i class="glyphicon glyphicon-search"></i></button>
									</div>
								</div>

							</div>
							<hr>
							<div id="divz2"></div>
							<div class="row" style="padding-bottom:10px;">
									<div class="col-xs-12">
									<input type="hidden" value="${cur_page }" name="currentPage" id="currentPage"></input>
										<span style="float:right; margin-right:10px;" id="pagez">
											${pagins}
										</span>
			</div>
		</div>
						</div>
					</div>
					<div class="col-xs-9" style="background:#fff">
						<dl class="all_coin_info">
							<dt id="" >
								<span class="trade-depth1"> 最新价： <span class=""> <span
										id="lastprice"> </span> <span id="lastpriceicon">↑</span> </span> </span>
							</dt>
							<dd><p class="red" id="marketHigh"></p>最高价</dd>
							<dd><p class="green" id="marketLow"></p>最低价</dd>
							<dd><p id="marketBuy"></p>买一价</dd>
							<dd><p id="marketSell"></p>卖一价</dd>
							<dd class="w150"><p id="marketVol"></p>24小时成交量</dd>
						</dl>
					</div>
				</div>
				<ul class="nav nav-pills nav-stacked ulchange" style="background:#F8F8F8;">
					
					<c:forEach var="v" varStatus="vs" items="${realTimePrize }">
						<c:if test="${v.fisShare==true }">
						
							<li class=" trademenu${v.fid } " style="font-size:18px;float:left;display:${fvirtualcointype.fid!=v.fid?'none':''}"><a
								href="/trade/coin.html?coinType=${v.fid }&tradeType=0"> 买入/卖出 </a></li>
							<li class=" trademenu${v.fid }" style="font-size:18px;float:left;display:${fvirtualcointype.fid!=v.fid?'none':''}"><a
								href="/trade/entrust.html?symbol=${v.fid }&status=0"> 委托管理 </a></li>
							<li class=" trademenu${v.fid }" style="font-size:18px;float:left;display:${fvirtualcointype.fid!=v.fid?'none':''}"><a 
					href="/trade/entrust2.html?symbol=${v.fid }&status=1"> 交易记录 </a></li>
							<li class=" trademenu${v.fid } cur" style="font-size:18px;float:left;display:${fvirtualcointype.fid!=v.fid?'none':''}"><a
								href="/about/index1.html?id=${v.fweburl }&symbol=${v.fid }"> 介绍</a></li>	
						</c:if>
					</c:forEach>
				
			</ul>
			<div class="row padding-bottom-50" style="background:#fff">
					<div class="col-xs-12 bg-white split" style="margin:10px auto;padding:20px 30px;border-bottom:1px solid #eee;">
						<h4>${fabout.ftitle}</h4>
					</div>
					<div class="col-xs-12" style="min-height:300px;padding:20px 30px;">
					<c:if test="${fabout.fcontent==''}">
					
						无
					</c:if>
					<c:if test="${fabout.fcontent!=null}">
					
							${fabout.fcontent}
					</c:if>
					</div>
				</div>
		</div>
	</div>
	
	

<input type="hidden" id="symbol" value="${fvirtualcointype.fid }">
<input id="coinCount" type="hidden" value="${fvirtualcointype.fcount }">
<jsp:include page="../comm/footer.jsp"></jsp:include>
<script type="text/javascript" src="/static/front/js/trade/trade.js"></script>
<script type="text/javascript" src="/static/front/js/trade/dongtai.js"></script>

	<script type="text/javascript">
		$(function() {
			$('.collapse').on('hide.bs.collapse', function(ele) {
				$("#"+$(ele.currentTarget).data().icon).html("+");
				return true;
			})
			$('.collapse').on('show.bs.collapse', function(ele) {
				$("#"+$(ele.currentTarget).data().icon).html("-");
				return true;
			})
		})
	</script>
</body>
</html>
