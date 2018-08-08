<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="../comm/include.inc.jsp"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;
%>




<!doctype html>
<html>
<head>
<meta http-equiv= "X-UA-Compatible"  content = "IE=edge,chrome=1" /> 
<meta http-equiv="pragram" content="no-cache"> 
<meta http-equiv="cache-control" content="no-cache, must-revalidate">
<meta http-equiv="expires" content="0">  
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" /> 
<jsp:include page="../comm/link.inc.jsp"></jsp:include>
<link href="/static/front/css/trade/trade.css" rel="stylesheet" type="text/css" media="screen, projection" />
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
    width:40px;
}


</style>	

<jsp:include page="../comm/header.jsp"></jsp:include>


	<div class="container-full padding-top-30">
		<div class="container">
			
			<%@include file="../comm/left_menu.jsp" %>
			
			<div class="col-xs-12 padding-right-clear" >
				<div class="row">
					<div class="col-xs-3" style="background:#fff;height:112px;" id="div3">	
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
											name="searchName" id="searchname" placeholder="输入名称" onkeypress="if(event.keyCode==13) {searchType(1);}"   
											 /> <span
											class="input-group-btn">
											<input type="hidden" id="hidlog"/>
											<button class="btn btn-primary" id="searchType"
												onclick="searchType(1);"><i class="glyphicon glyphicon-search"></i></button>
									</div>
								</div>

							</div>
							<div style="border:1px solid #f8f8f8;margin-bottom:10px;"></div>
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
				
		<ul class="nav nav-pills nav-stacked ">
		<c:forEach var="v" varStatus="vs" items="${realTimePrize }">
			<c:if test="${v.fisShare==true }">
			
				<li class=" trademenu${v.fid } cur" style="font-size:18px;float:left;display:${fvirtualcointype.fid!=v.fid?'none':''}"><a 
					href="/trade/coin.html?coinType=${v.fid }&tradeType=0"> 买入/卖出 </a></li>
				<li class=" trademenu${v.fid }" style="font-size:18px;float:left;display:${fvirtualcointype.fid!=v.fid?'none':''}"><a 
					href="/trade/entrust.html?symbol=${v.fid }&status=0"> 委托管理 </a></li>
				<li class=" trademenu${v.fid }" style="font-size:18px;float:left;display:${fvirtualcointype.fid!=v.fid?'none':''}"><a 
					href="/trade/entrust2.html?symbol=${v.fid }&status=1"> 交易记录 </a></li>
				<li class=" trademenu${v.fid }" style="font-size:18px;float:left;display:${fvirtualcointype.fid!=v.fid?'none':''}"><a
					href="/about/index1.html?id=${v.fweburl }&symbol=${v.fid }"> 介绍</a></li>	
			</c:if>
		</c:forEach>
		
	</ul>
	<div  class="row" style="border-top:1px solid #fff;margin-top:5px;"></div>
				<div class="row padding-right-clear padding-left-clear rightarea trade">
				<dl class="all_coin_info" style="height: 475px;border: 0px solid #ccc;">
					<div class="max-width" style="height: 475px;display:block;" id="kline1">
					<div id="k" class="btn-group2">
						<div class="Kline-shift-btn" style="color: #e55600;">普通版</div>
						<div class="Kline-shift-btn"
							style="background: #f8f8f8;">
							<a href="javascript:void(0);" class="openZYKline" id="openZYKline">专业版</a>
						</div>
					</div>
					<iframe frameborder="0" border="0" width="100%" height="100%" id="klineFullScreen1" src="/kline/trade.html?id=${fvirtualcointype.fid }"></iframe>
					</div>
					<div class="max-width" style="height: 460px;display:none;" id="kline2">
					<input type="hidden" id="fvirtualcointypefid" value="${fvirtualcointype.fid }">
					    <iframe frameborder="0" border="0" width="100%" height="100%" id="klineFullScreen" src=""></iframe>
					    <div class="kline-back"><a style="color:white;writing-mode: tb-rl;" href="javascript:void(0);"  class="openPTKline" id="openPTKline">返回普通版</a></div>
						<a class="openfullscreen" id="openfullscreen" href="javascript:void(0);" title="全屏" style="display:block;"></a>
						<a class="closefullscreen" id="closefullscreen" href="javascript:void(0);" title="退出全屏" style="display:none"></a>
					</div>
			    </dl>		
				
				<div class="col-xs-12 rightarea-con" style="border-top:1px solid #dcdcdc;">
					<div class="col-xs-8">
					
					<div class=" trade-buysell col-xs-6">
							<div class="max-width trade-amount clearfix">
									<c:if test="${defAssetWallet != null }">
										<div class="col-xs-12">
											<span>可用</span>
											<span id="toptradecnymoney" class="redtips">
												${defAssetWallet.ftotal}
											</span>
											<span class="redtips">${defAssetWallet.fvirtualcointype.fSymbol}</span>
										</div>
								
										<div class="col-xs-12">
											<span>冻结</span>
											<span id="toptradelevercny">${defAssetWallet.ffrozen}</span>
											<span>${defAssetWallet.fvirtualcointype.fSymbol}</span>
											<i></i>
										</div>
									</c:if>
									<c:if test="${defAssetWallet == null }">
										<div class="col-xs-12">
										<span>可用</span>
										<span id="toptradecnymoney" class="redtips">
											0
										</span>
										<span class="redtips">CNY</span>
									</div>
							
									<div class="col-xs-12">
										<span>冻结</span>
										<span id="toptradelevercny">0.00</span>
										<span>CNY</span>
										<i></i>
									</div>
									
									</c:if>
								
							</div>
							<div class="max-width clearfix" id="buypricediv">
								<div class="col-xs-12">
									<div class="form-group">
										<label for="tradebuyprice" class="trade-inputtips">买入价CNY/${fvirtualcointype.fShortName }</label>
										<input id="tradebuyprice" class="form-control" type="text" value="${recommendPrizebuy }" onkeyup="checkNum1(this)" maxlength="15">
										
									</div>
									<div class="form-group">
										<span class="form-control trade-tips padding-right-clear">
											<span id="buyBar" class="col-xs-12 buysellbar">
												<div class="buysellbar-box">
													<div id="buyslider" class="slider" data-role="slider" data-param-marker="marker" data-param-complete="complete" data-param-type="0"></div>
													<div class="slider-points">
														<div class="proportioncircle proportion0" data-points="0"></div>
														<div class="proportioncircle proportion1" data-points="25"></div>
														<div class="proportioncircle proportion2" data-points="50"></div>
														<div class="proportioncircle proportion3" data-points="75"></div>
														<div class="proportioncircle proportion4" data-points="100"></div>
													</div>
												</div>
											</span>
											<span id="buyslidertext" class="col-xs-12 text-center">0%</span>
										</span>
									</div>
								</div>
								
								
								<div class="col-xs-12">
									<div class="form-group">
										<label for="tradebuyamount" class="trade-inputtips">买入量${fvirtualcointype.fShortName }</label>
										<input id="tradebuyamount" class="form-control" type="text"  value=""  onkeyup="checkNum(this)" maxlength="15">
									</div>
									<div class="form-group">
										<span class="form-control trade-tips">
											<span class="col-xs-3 text-left padding-right-clear">交易额</span>
											<span class="col-xs-12 padding-right-clear ">
												<c:if test="${defAssetWallet == null }">
													<span id="tradebuyTurnover"><fmt:formatNumber value="0"  pattern="#.##" minFractionDigits="2"/></span>
													CNY
													
												</c:if>
												<c:if test="${defAssetWallet != null }">
												<span id="tradebuyTurnover"><fmt:formatNumber value="0"  pattern="#.##" minFractionDigits="2"/></span>
													CNY<br/>
													<span id="tradebuyTurnover1"><fmt:formatNumber value="0"  pattern="#.##" minFractionDigits="2"/></span>
														${defAssetWallet.fvirtualcointype.fSymbol}
												</c:if>
											</span>
										</span>
									</div>
								</div>
								
								<div class="col-xs-12">
									<div class="form-group">
										<span id="buy-errortips" class="text-danger trade-error"></span>
									</div>
								</div>
								<div class="col-xs-6">
									<div class="form-group">
										<button id="buybtn" class="btn btn-danger btn-block">买入<%-- ${fvirtualcointype.fShortName } --%></button>
									</div>
								</div>
								<div class="col-xs-6">
									<div class="form-group">
										<a href="/account/rechargeCny.html" class="text-danger">立即充值>></a>
									</div>
								</div>
							</div>
							<div class="max-width clearfix" id="buymarketdiv" style="display: none;">
								<div class="col-xs-6">
									<div class="form-group">
										<span class="form-control trade-tips padding-right-clear">
											<span id="buyBar" class="col-xs-12 buysellbar">
												<div class="buysellbar-box">
													<div id="buyslider2" class="slider" data-role="slider" data-param-marker="marker" data-param-complete="complete" data-param-type="0"></div>
													<div class="slider-points">
														<div class="proportioncircle proportion0" data-points="0"></div>
														<div class="proportioncircle proportion1" data-points="25"></div>
														<div class="proportioncircle proportion2" data-points="50"></div>
														<div class="proportioncircle proportion3" data-points="75"></div>
														<div class="proportioncircle proportion4" data-points="100"></div>
													</div>
												</div>
											</span>
											<span id="buyslidertext2" class="col-xs-12 text-center">0%</span>
										</span>
									</div>
								</div>
								<div class="col-xs-6">
									<div class="form-group">
										<span class="form-control trade-tips">
											<span class="col-xs-12 padding-right-clear ">
												<label for="tradebuyprice2" class="trade-inputtips">交易额/CNY</label>
												<input id="tradebuyprice2" class="form-control" value="0" type="text">
											</span>
										</span>
									</div>
								</div>
								<div class="col-xs-12">
									<div class="form-group">
										<span id="buy-errortips2" class="text-danger trade-error"></span>
									</div>
								</div>
								<div class="col-xs-12">
									<div class="form-group">
										<button id="buybtn2" class="btn btn-danger btn-block">买入${fvirtualcointype.fShortName }</button>
									</div>
								</div>
								<div class="col-xs-12">
									<div class="form-group">
										<a href="/account/rechargeCny.html" class="text-danger">立即充值>></a>
									</div>
								</div>
							</div>
						</div>
						<!--卖出 start -->
						<div class="trade-buysell col-xs-6">
							<div class="max-width trade-amount clearfix">
								<div class="col-xs-12">
									<span>可用</span>
									<span id="toptrademtccoin" class="greentips">
										0
									</span>
									<span class="greentips">${fvirtualcointype.fShortName }</span>
								</div>
								
								<!-- <span class="col-xs-4 text-center">
									<span class="databtn datatime datatime-sel" data-type="3">限价</span>
									<span class="databtn datatime" data-type="4">市价</span>
								</span> -->
								
								<span class="col-xs-12">
									<span>冻结</span>
									<span id="toptradelevercoin">0.0</span>
									<span>${fvirtualcointype.fShortName }</span>
									<i></i>
								</span>
							</div>
							<div class="max-width clearfix" id="sellpricediv">
								<div class="col-xs-12">
									<div class="form-group">
										<label for="tradesellprice" class="trade-inputtips">卖出价CNY/${fvirtualcointype.fShortName }</label>
										<input id="tradesellprice" class="form-control" type="text" value="${recommendPrizesell }"  onkeyup="checkNum1(this)" maxlength="15">
									</div>
									<div class="form-group">
										<span class="form-control trade-tips padding-right-clear">
											<span id="sellBar" class="col-xs-12 buysellbar">
												<div class="buysellbar-box">
													<div id="sellslider" class="slider" data-role="slider" data-param-marker="marker sell-marker" data-param-complete="complete sell-complete" data-param-type="1"></div>
													<div class="slider-points">
														<div class="proportioncircle proportion0" data-points="0"></div>
														<div class="proportioncircle proportion1" data-points="25"></div>
														<div class="proportioncircle proportion2" data-points="50"></div>
														<div class="proportioncircle proportion3" data-points="75"></div>
														<div class="proportioncircle proportion4" data-points="100"></div>
													</div>
												</div>
											</span>
											<span id="sellslidertext" class="col-xs-12 text-center">0%</span>
										</span>
									</div>
								</div>
								<div class="col-xs-12">
									<div class="form-group">
										<label for="tradesellamount" class="trade-inputtips">卖出量${fvirtualcointype.fShortName }</label>
										<input id="tradesellamount" class="form-control" type="text"  value=""  onkeyup="checkNum(this)" maxlength="15">
									</div>
									<div class="form-group">
										<span class="form-control trade-tips">
											<span class="col-xs-3 text-left padding-right-clear">交易额</span>
											<span class="col-xs-12 padding-right-clear ">
												<c:if test="${defAssetWallet == null }">
												<span id="tradesellTurnover"><fmt:formatNumber value="0"  pattern="#.##" minFractionDigits="2"/></span>
													CNY
												</c:if>
												<c:if test="${defAssetWallet != null }">
												<span id="tradesellTurnover"><fmt:formatNumber value="0"  pattern="#.##" minFractionDigits="2"/></span>
													CNY<br/>
													<span id="tradesellTurnover1"><fmt:formatNumber value="0"  pattern="#.##" minFractionDigits="2"/></span>
														${defAssetWallet.fvirtualcointype.fSymbol}
												</c:if>
											</span>
										</span>
									</div>
								</div>
								<div class="col-xs-12">
									<div class="form-group">
										<span id="sell-errortips" class="text-danger trade-error"></span>
									</div>
								</div>
								<div class="col-xs-6">
									<div class="form-group">
										<button id="sellbtn" class="btn btn-success btn-block">卖出<%-- ${fvirtualcointype.fShortName } --%></button>
									</div>
								</div>
								<div class="col-xs-6">
									<div class="form-group">
										<a href="/account/rechargeBtc.html?symbol=${fvirtualcointype.fid }" class="text-success">立即充值>></a>
									</div>
								</div>
							</div>
							
							<div class="max-width clearfix" id="sellmarketdiv" style="display: none;">
								<div class="col-xs-6">
									<div class="form-group">
										<span class="form-control trade-tips padding-right-clear">
											<span id="sellBar" class="col-xs-12 buysellbar">
												<div class="buysellbar-box">
													<div id="sellslider2" class="slider" data-role="slider" data-param-marker="marker sell-marker" data-param-complete="complete sell-complete" data-param-type="1"></div>
													<div class="slider-points">
														<div class="proportioncircle proportion0" data-points="0"></div>
														<div class="proportioncircle proportion1" data-points="25"></div>
														<div class="proportioncircle proportion2" data-points="50"></div>
														<div class="proportioncircle proportion3" data-points="75"></div>
														<div class="proportioncircle proportion4" data-points="100"></div>
													</div>
												</div>
											</span>
											<span id="sellslidertext2" class="col-xs-12 text-center">0%</span>
										</span>
									</div>
								</div>
								<div class="col-xs-6">
									<div class="form-group">
										<span class="form-control trade-tips">
											<span class="col-xs-12 padding-right-clear ">
												<label for="tradesellprice2" class="trade-inputtips">交易数量/${fvirtualcointype.fShortName }</label>
												<input id="tradesellprice2" class="form-control" value="0" type="text">
											</span>
										</span>
									</div>
								</div>
								<div class="col-xs-12">
									<div class="form-group">
										<span id="sell-errortips2" class="text-danger trade-error"></span>
									</div>
								</div>
								<div class="col-xs-6">
									<div class="form-group">
										<button id="sellbtn2" class="btn btn-success btn-block">卖出${fvirtualcointype.fShortName }</button>
									</div>
								</div>
								<div class="col-xs-6">
									<div class="form-group">
										<a href="/account/rechargeBtc.html?symbol=${fvirtualcointype.fid }" class="text-success">立即充值>></a>
									</div>
								</div>
							</div>
							
						</div><!--卖出end  -->
					
					
						
						
						
						 
					</div>
					<div id="coinBoxbuybtc" class="col-xs-4">
					<c:if test="${isLimittrade == true}">
					    <span class="trade-depth">
					           涨停价：<span style="color:red;">￥<fmt:formatNumber
									value="${upPrice }" pattern="##.##" maxIntegerDigits="10"
									maxFractionDigits="${fvirtualcointype.fcount}" /> </span>, 跌停价：<span  style="color:red;">￥<fmt:formatNumber
									value="${downPrice }" pattern="##.##" maxIntegerDigits="10"
									maxFractionDigits="${fvirtualcointype.fcount}" /> </span>
					    </span>
					</c:if>
					<span class="trade-depth">
							<span class="col-xs-2 padding-clear" style="width:10%;">买卖</span>
							<span class="col-xs-5 text-right padding-clear" style="width:20%;">价格</span>
							<span class="col-xs-5 text-right padding-clear" style="width:35%;">数量</span>
							<span class="col-xs-5 text-right padding-clear" style="width:35%;">金额</span>
                    </span>
                    
						<ul id="sellbox" class="list-group first-child">
						</ul>
						<ul id="buybox" class="list-group ">
						</ul>
						<!-- <span class="trade-depth"> 最近成交记录</span>
						<ul id="logbox" class="list-group ">
						</ul> -->
					</div>
				</div>
				<div id="entrustInfo">
				</div>
			</div>
		</div>
	</div>
	<div class="modal modal-custom fade" id="tradepass" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel">
		<div class="modal-dialog modal-trading-dialog" role="document">
			<div class="modal-mark"></div>
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close btn-modal" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<span class="modal-title" id="exampleModalLabel">交易密码</span>
				</div>
				<div class="modal-body">
					<div class="form-group">
						<input type="password" class="form-control" id="tradePwd" placeholder="请输入交易密码">
					</div>
				</div>
				<div class="modal-footer">
					<button id="modalbtn" type="button" class="btn btn-primary">确定</button>
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
	
	<input id="coinshortName" type="hidden" value="${fvirtualcointype.fShortName }">
	<input id="symbol" type="hidden" value="${fvirtualcointype.fid }">
	<input id="isopen" type="hidden" value="${needTradePasswd }">
	<input id="tradeType" type="hidden" value="0">
	<input id="userid" type="hidden" value="${userid }">
	
	<input id="tradePassword" type="hidden" value="${isTradePassword }">
	<input id="isTelephoneBind" type="hidden" value="${isTelephoneBind }">
	<input id="fpostRealValidate" type="hidden" value="${fpostRealValidate }">
    <input id="coinCount" type="hidden" value="${fvirtualcointype.fcount }">
    <input id="exprice" type="hidden" value="${fsubscription.fprice }">
    <input id="limitedType" type="hidden" value="0">

 
	<jsp:include page="../comm/footer.jsp"></jsp:include>
	<script type="text/javascript" src="/static/front/js/plugin/jquery.jslider.js"></script>
	<script type="text/javascript" src="/static/front/js/trade/trade.js"></script>
	<script type="text/javascript" src="/static/front/js/trade/trademarket.js"></script>
	<script type="text/javascript" src="/static/front/js/trade/dongtai.js"></script>
	<script>
	function checkNum(obj) {  
		obj.value = obj.value.replace(/[^\d.]/g,"");  //清除“数字”和“.”以外的字符  
	    obj.value = obj.value.replace(/\.{2,}/g,"."); //只保留第一个. 清除多余的  
	    obj.value = obj.value.replace(".","$#$").replace(/\./g,"").replace("$#$","."); 
	    obj.value = obj.value.replace(/^(\-)*(\d+)\.(\d\d\d\d).*$/,'$1$2.$3');//只能输入两个小数  
	    if(obj.value.indexOf(".")< 0 && obj.value !=""){//以上已经过滤，此处控制的是如果没有小数点，首位不能为类似于 01、02的金额 
	        obj.value= parseFloat(obj.value); 
	    } 
	 } 
	function checkNum1(obj) {  
		obj.value = obj.value.replace(/[^\d.]/g,"");  //清除“数字”和“.”以外的字符  
	    obj.value = obj.value.replace(/\.{2,}/g,"."); //只保留第一个. 清除多余的  
	    obj.value = obj.value.replace(".","$#$").replace(/\./g,"").replace("$#$","."); 
	    obj.value = obj.value.replace(/^(\-)*(\d+)\.(\d\d).*$/,'$1$2.$3');//只能输入两个小数  
	    if(obj.value.indexOf(".")< 0 && obj.value !=""){//以上已经过滤，此处控制的是如果没有小数点，首位不能为类似于 01、02的金额 
	        obj.value= parseFloat(obj.value); 
	    } 
	 } 
	</script>
</body>
</html>

