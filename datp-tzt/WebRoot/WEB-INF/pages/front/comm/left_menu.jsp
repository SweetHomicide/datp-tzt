<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="include.inc.jsp"%>

<!-- 用戶中心 -->
<div class="col-xs-2 leftmenu" style="display: none" id="user">
	<ul class="nav nav-pills nav-stacked">
		<span class="leftmenu-title top"> <i class="lefticon security"></i>
			安全中心
		</span>
		<li class=""><a href="/user/security.html"> 安全设置 </a></li>
		<li class=""><a href="/user/userloginlog.html?type=1"> 登录记录 </a>
		</li>
		<span class="leftmenu-title"> <i class="lefticon user"></i>
			用户中心
		</span>
		<li class=""><a href="/user/userinfo.html"> 基本信息 </a></li>
		<li><a href="/user/realCertification.html">实名认证 </a></li>
		<span class="leftmenu-title"> <i class="lefticon user"></i>
			消息中心
		</span>
		<li class=""><a href="/stationMail/view.html">个人消息 </a></li>
		<li class=""><a href="/stationMail/viewSys.html">系统消息 </a></li>
	</ul>
</div>

<!-- 财务中心 -->
<div class="col-xs-2 leftmenu" style="display: none" id="account">
	<ul class="nav nav-pills nav-stacked">
		<span class="leftmenu-title top"> <i class="lefticon financial"></i>
			财务管理
		</span>
		<li class=""><a href="/account/rechargeCny.html"> 充值 </a></li>
		<li class=""><a href="/account/rechargeBtc.html"> 数字资产转入 </a></li>
		<li class=""><a href="/account/withdrawCny.html"> 提现 </a></li>
		<li class=""><a href="/account/withdrawBtc.html"> 数字资产转出</a></li>
		<c:if
									test='${requestScope.constant["ISHIDDEN_CROWDFUNDING"] eq "false" }'>
		<li class=""><a href="/crowd/logs.html"> 我的众筹 </a></li>
		</c:if>
		<!-- <li class=""><a href="/exchange/index.html"> 兑换中心</a> -->
		<!-- javascript:showTips('暂无兑换'); --></li>
		<li class=""><a href="/financial/index.html"> 个人资产 </a></li>
		<li class=""><a href="/account/record.html"> 账单明细 </a></li>
		<li class=""><a href="/financial/accountbank.html"> 资金帐号 </a></li>
		<li class=""><a href="/introl/mydivide.html"> 推广收益 </a></li>
		<li class=""><a href="/financial/assetsrecord.html"> 资产记录 </a></li>
		
	</ul>
</div>

<!-- 交易中心 -->

<div class="col-xs-2 leftmenu" id="trade" style="display: none">
	<ul class="nav nav-pills nav-stacked ">

		<c:forEach var="v" varStatus="vs" items="${realTimePrize }">
			<c:if test="${v.fisShare==true }">
				<span class="leftmenu-title leftmenu-folding top"
					data-folding="trademenu${v.fid }"> <i class="lefticon"
					style="background: url('${v.furl }') no-repeat 0 0;background-size:100% 100%;"></i>
					<%-- (${v.fname })交易 --%>${v.fname }
				</span>
				<li class=" trademenu${v.fid }"
					style="display:${fvirtualcointype.fid!=v.fid?'none':''}"><a
					href="/trade/coin.html?coinType=${v.fid }&tradeType=0"> 买入/卖出 </a></li>
				<li class=" trademenu${v.fid }"
					style="display:${fvirtualcointype.fid!=v.fid?'none':''}"><a
					href="/trade/entrust.html?symbol=${v.fid }&status=0"> 委托管理 </a></li>
				<li class=" trademenu${v.fid }"
					style="display:${fvirtualcointype.fid!=v.fid?'none':''}"><a
					href="/trade/entrust.html?symbol=${v.fid }&status=1"> 交易记录 </a></li>
				<li class=" trademenu${v.fid }"
					style="display:${fvirtualcointype.fid!=v.fid?'none':''}"><a
					href="/about/index.html?id=${v.fweburl }"> 介绍</a></li>
			</c:if>
		</c:forEach>

	</ul>
</div>
<div class="col-xs-12" id="div1" style="display: none;">
	<div id="coinNames">
		<c:forEach var="v" varStatus="vs" items="${fList }">
			<c:if test="${v.fisShare==true }">
				<div class="col-xs-4" style="line-height: 40px;">
					<a href="/trade/coin.html?coinType=${v.fid }"> <i
						class="lefticon col-xs-2"
						style="margin-right:5px;top:5px;width:20px;height:30px;background-image: url('${v.furl }') ;background-size:100%;"></i>${v.fname }
					</a>
				</div>
			</c:if>
		</c:forEach>
	</div>
</div>
<div class="col-xs-12" id="divx1" style="display: none;">
	<div id="coinNamex1">
		<c:forEach var="v" varStatus="vs" items="${fList }">
			<c:if test="${v.fisShare==true }">
				<div class="col-xs-4" style="line-height: 40px;">
					<a href="/account/rechargeBtc.html?symbol=${v.fid }"> <i
						class="lefticon col-xs-2"
						style="margin-right:5px;top:5px;width:20px;height:30px;background-image: url('${v.furl }') ;background-size:100%;"></i>${v.fname }
					</a>
				</div>
			</c:if>
		</c:forEach>
	</div>
</div>
<div class="col-xs-12" id="divy1" style="display: none;">
	<div id="coinNamey1">
		<c:forEach var="v" varStatus="vs" items="${fList }">

			<c:if test="${v.fisShare==true }">


				<div class="col-xs-4" style="line-height: 40px;">
					<a href="/account/withdrawBtc.html?symbol=${v.fid }"> <i
						class="lefticon col-xs-2"
						style="margin-right:5px;top:5px;width:20px;height:30px;background-image: url('${v.furl }') ;background-size:100%;"></i>${v.fname }
					</a>
				</div>
			</c:if>
		</c:forEach>
	</div>
</div>


<div class="col-xs-12" id="divxx1" style="display: none;">
	<div id="coinNamexx1">
		<c:forEach var="v" varStatus="vs" items="${fList }">
			<c:if test="${v.fisShare==true }">
				<div class="col-xs-4" style="line-height: 40px;">
					<a href="/financial/accountcointype.html?symbol=${v.fid }"> <i
						class="lefticon col-xs-2"
						style="margin-right:5px;top:5px;width:20px;height:30px;background-image: url('${v.furl }') ;background-size:100%;"></i>${v.fname }
					</a>
				</div>
			</c:if>
		</c:forEach>
	</div>
</div>

<!-- 兑换中心 -->
<div class="col-xs-12" id="divxy1" style="display: none;">
	<div id="coinNamexy1">
		<c:forEach var="v" varStatus="vs" items="${fsubscription1 }">
			<c:if test="${v.fvirtualcointype.fisShare==true }">
				<div class="col-xs-4" style="line-height: 40px;">
					<a href="/exchange/index.html?id=${v.fid }"> <%-- <i
						class="lefticon col-xs-2"
						style="margin-right:5px;top:5px;width:20px;height:30px;background-image: url('${v.fvirtualcointype.furl }') ;background-size:100%;"></i> --%>${v.ftitle }
					</a>
				</div>
			</c:if>
		</c:forEach>
	</div>
</div>
<!--  介绍-->
<div class="col-xs-12" id="divjs" style="display: none;">
	<div id="coinNamexy1">
		<c:forEach var="v" varStatus="vs" items="${fList }">
			<c:if test="${v.fisShare==true }">
				<div class="col-xs-4" style="line-height: 40px;">
					<a href="/trade/coin.html?coinType=${v.fid }"> <i
						class="lefticon col-xs-2"
						style="margin-right:5px;top:5px;width:20px;height:30px;background-image: url('${v.furl }') ;background-size:100%;"></i>${v.fname }
					</a>
				</div>
			</c:if>
		</c:forEach>
	</div>
</div>

<!--  资产明细-->
<div class="col-xs-12" id="hid_detail_coinName" style="display: none;">
	<div id="detail_coinName">
		<c:forEach var="v" varStatus="vs" items="${result1 }">
		
				<div class="col-xs-4" style="line-height: 40px;">
					<a href="/account/record.html?isRMB=1&recordType=3&symbol=${v.fid }"> <i
						class="lefticon col-xs-2"
						style="margin-right:5px;top:5px;width:20px;height:30px;background-image: url('${v.furl }') ;background-size:100%;"></i>${v.fname }
					</a>
				</div>
			
		</c:forEach>
	</div>
</div>

<script type="text/javascript">
	var leftpath = window.location.pathname;
	var path = window.location.href.replace('http://'+window.location.host,'') ;
	if(leftpath.startWith("/trade/")){//交易中心特殊处理
		//$("#user").attr("display", "none");
		//$("#account").attr("display", "none");
		//alert(document.getElementById("trade").style.display);
		document.getElementById("user").style.display="none";
		document.getElementById("account").style.display="none";
		/* document.getElementById("trade").style.display="block"; */
		//$("#trade").style.display="block";
	var count = 0 ;
	var isshow = false;
	$(".leftmenu").each(function(){
			var flag = false ;
			$(this).find("a").each(function(){
				if(path.indexOf($(this).attr("href")) != -1){
					$(this).parent().addClass("active") ;
					flag = true ;
					count++ ;
				}
			}) ;
			if(flag == false ){
				$(this).remove();
			}
			
	}) ;

	//if(count==0){
//		$(".leftmenu").each(function(){
//				var flag = false ;
//				$(this).find("a").each(function(){
//					if($(this).attr("href").startWith(leftpath)){
//						if(isshow == false ){
//							$(this).parent().addClass("active") ;
//						}
//						flag = true ;
//						isshow = true ;
//						count++ ;
//					}
//				}) ;
//				if(flag == false ){
//					$(this).remove();
//				}
//		});
	//}
//				
				
	}else{//除交易中心的其他菜单
	var left = "${leftMenu}";
	if(left.startWith("userinfo")){
		document.getElementById("trade").style.display="none";
		document.getElementById("account").style.display="none";
		document.getElementById("user").style.display="block";
		
	} else {
		document.getElementById("user").style.display="none";
		document.getElementById("trade").style.display="none";
		document.getElementById("account").style.display="block";
	}
		$(".leftmenu").each(function(){		
		
				var flag = false ;
				$(this).find("a").each(function(){
						
					if($(this).attr("href").indexOf(leftpath) != -1){
						$(this).parent().addClass("active") ;
						flag = true ;
					}else{
					   if(($(this).attr("href").indexOf("/account/rechargeCny") != -1
					    || $(this).attr("href").indexOf("/account/proxyCode") != -1
					    || $(this).attr("href").indexOf("/account/rechargeOnline") != -1
					    || $(this).attr("href").indexOf("/account/weChatPay") != -1
					    || $(this).attr("href").indexOf("/account/AliPay") != -1
					    || $(this).attr("href").indexOf("/account/payCode") != -1)
					     && left == "recharge"){
					      $(this).parent().addClass("active") ;
					      flag = true ;
					   }else  if($(this).attr("href").indexOf("/account/rechargeBtc") != -1 && left == "rechargeBtc"){
					      $(this).parent().addClass("active") ;
					      flag = true ;
					   }else  if(($(this).attr("href").indexOf("/account/withdrawCny") != -1||$(this).attr("href").indexOf("/account/withdrawbtcToCny") != -1)&& left == "withdraw"){ 
						   $(this).parent().addClass("active") ;
						      flag = true ;
						}else  if($(this).attr("href").indexOf("/account/withdrawBtc") != -1 && left == "withdrawBtc"){
						  $(this).parent().addClass("active") ;
						  flag = true ;
					   }else  if($(this).attr("href").indexOf("/stationMail/view") != -1 && left == "message"){
							$(this).parent().addClass("active") ;
							  flag = true ;
						}else  if($(this).attr("href").indexOf("/financial/accountbank") != -1 && left == "accountAdd"){
					      $(this).parent().addClass("active") ;
					      flag = true ;
					   } else  if($(this).attr("href").indexOf("/divide/") != -1  && left == "divide" ){
					      $(this).parent().addClass("active") ;
					      flag = true ;
					   } else  if($(this).attr("href").indexOf("/crowd/") != -1  && left == "mylogs" ){
					      $(this).parent().addClass("active") ;
					      flag = true ;
					   }else if($(this).attr("href").indexOf("/account/rechargeOnline") != -1 && left == "rechargeCny"){
						 /*   $(this).parent().addClass("active")  */
						   flag = true ;
					   }
					}
				}) ;
				
				if(flag == false){
					$(this).remove();
				}
		}) ;
	} 
	
</script>