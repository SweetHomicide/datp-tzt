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
</head>
<body>
	
<jsp:include page="../comm/header.jsp"></jsp:include>



	<div class="container-full padding-top-30">
		<div class="container">
			<%@include file="../comm/left_menu.jsp" %>
			<div class="col-xs-12  padding-right-clear">
				<div class="row" style="background:#fff">
			<div class="col-xs-3" style="background:#fff;" id="div3">	
						<div class="all_coin_info1">
							<span class="lefticon col-xs-2"
					style="margin-right:5px;top:5px;width:50px;height:50px;background-image: url('${fvirtualcointype.furl }') ;background-size:100%;"></span>
							<a href="javascript:void(0);" rel="drevil" title="请选择"  class="cointype" id="cointype"> <!-- onmouseover="changeDown()" onmouseout="changeUp()" -->${fvirtualcointype.fname }</a>
							<span class="arrow-down" id="icon1"></span>
						</div>

						<div id="div4"
							style="display: none; z-index: 1000; width: 500px; background: #fff; position: absolute; border: 1px solid #ccc; top: 65px; left: 0px;">
							<div class="row" style="padding-top: 15px;">
								<div class="col-xs-6 col-xs-offset-6" style="right:10px">

									<div class="input-group form-group">
										<input type="text" class="form-control searchname"
											name="searchName" id="searchname" placeholder="输入名称" onkeypress="if(event.keyCode==13) {searchType(1);}"  /> <span
											class="input-group-btn">
											<input type="hidden" id="hidlog"/>
											<button class="btn btn-primary" id="searchType"
												onclick="searchType(1);"><i class="glyphicon glyphicon-search"></i></button>
									</div>
								</div>

							</div>
							<hr>
							<div id="div2"></div>
							<div class="row" style="padding-bottom:10px;">
									<div class="col-xs-12">
									<input type="hidden" value="${cur_page }" name="currentPage" id="currentPage"></input>
										<span style="float:right; margin-right:10px;" id="pagex">
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
				<li class=" trademenu${v.fid } cur" style="font-size:18px;float:left;display:${fvirtualcointype.fid!=v.fid?'none':''}"><a
					href="/trade/entrust2.html?symbol=${v.fid }&status=1"> 交易记录 </a></li>
				<li class=" trademenu${v.fid }" style="font-size:18px;float:left;display:${fvirtualcointype.fid!=v.fid?'none':''}"><a
					href="/about/index1.html?id=${v.fweburl }&symbol=${v.fid }"> 介绍</a></li>	
			</c:if>
		</c:forEach>
		
	</ul>
			<div class="row padding-right-clear">
				<div class="col-xs-12 padding-right-clear padding-left-clear rightarea user">
					<div class="col-xs-12 rightarea-con">
						<div class="col-xs-12 padding-clear">
							<table class="table table-striped text-left">
					<tr class="bg-danger">
									<td>
										委托时间
									</td>
									<td>
										类型
									</td>
									<td>
										数量
									</td>
									<td>
										金额
									</td>
									<td>
										手续费
									</td>
									<td>
										价格
									</td>
									<td>
										成交量
									</td>
									<td>
										成交金额
									</td>
									<td>
										平均成交价
									</td>
									<td>
										状态/操作
									</td>
								</tr>
					
									<c:if test="${fn:length(fentrusts)==0 }">
										<tr>
										<td class="no-data-tips" colspan="10">
											<span>
												暂无委托
											</span>
										</td>
									</tr>
									</c:if>
									
									<c:forEach items="${fentrusts }" var="v" varStatus="vs">
					<tr>
						<td class="gray"><fmt:formatDate value="${v.fcreateTime }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
						<td class="${v.fentrustType==0?'text-danger':'text-success' }">${v.fentrustType_s}${v.fisLimit==true?'[市价]':'' }</td>
						<td>${fvirtualcointype.fSymbol}<fmt:formatNumber value="${v.fcount }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/></td>
						<td>￥<fmt:formatNumber value="${v.famount}" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/></td>
						<c:choose>
						<c:when test="${v.fentrustType==0 }">
						<td>${fvirtualcointype.fSymbol}<fmt:formatNumber value="${v.ffees}" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/></td>
						</c:when>
						<c:otherwise>
						<td>￥<fmt:formatNumber value="${v.ffees}" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/></td>
						</c:otherwise>
						</c:choose>
						<td>￥<fmt:formatNumber value="${v.fprize }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="${fvirtualcointype.fcount }"/></td>
						<td>${fvirtualcointype.fSymbol}<fmt:formatNumber value="${v.fcount-v.fleftCount }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/></td>
						<td>￥<fmt:formatNumber value="${v.fsuccessAmount }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/></td>
						<td>￥<fmt:formatNumber value="${((v.fcount-v.fleftCount)==0)?0:  v.fsuccessAmount/((v.fcount-v.fleftCount)) }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/></td>
						<td>
						${v.fstatus_s }
						<c:if test="${v.fstatus==1 || v.fstatus==2}">
						&nbsp;|&nbsp;<a href="javascript:void(0);" class="tradecancel" data-value="${v.fid}" refresh="1">取消</a>
						</c:if>
						<c:if test="${v.fstatus==3}">
						&nbsp;|&nbsp;<a href="javascript:void(0);" class="tradelook" data-value="${v.fid}">查看</a>
						</c:if>
						</td>
                          </tr>
			</c:forEach>
			
								
							</table>
							<div class="text-right">
								${pagin}
							</div>
							
						</div>
						<input type="hidden" value="${currentPage}" id="currentPage">
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="modal modal-custom fade" id="entrustsdetail" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-mark"></div>
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close btn-modal" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<span class="modal-title" id="exampleModalLabel"></span>
				</div>
				<div class="modal-body"></div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
				</div>
			</div>
		</div>
	</div>
	
<input id="coinCount" type="hidden" value="${fvirtualcointype.fcount }">
<input type="hidden" id="symbol" value="${fvirtualcointype.fid }">
<jsp:include page="../comm/footer.jsp"></jsp:include>
<script type="text/javascript" src="/static/front/js/trade/trade.js"></script>
<script type="text/javascript" src="/static/front/js/trade/dongtai.js"></script>
</body>
</html>
