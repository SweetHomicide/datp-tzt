<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="include.inc.jsp"%>

<div id="allheader">
	<div class="container-full header clearfix ">
		<div class="container-full top bg-danger">
			<div class="container ">
				<div class="col-xs-12">
					<div class="pull-left">
						<a target="_blank"
							href="http://wpa.qq.com/msgrd?v=3&uin=${requestScope.constant['serviceQQ'] }&menu=yes"
							style="text-decoration: none; color: #000"> <img alt="QQ客服"
							src="/static/front/images/comm/qq.png">客服
						</a> <span class="top-item padding-left-clear">|</span> <span
							class="top-item wechat"> &nbsp; <span class="hide-item">
								<i class="topimg"></i> <img alt="微信二维码"
								src="${requestScope.constant['topWeixinImages']}">
								<p>
									扫描关注<br />${requestScope.constant['webName']}微信</p>
						</span>
						</span>
						<!-- <span class="top-item padding-left-clear">|</span>
                       <a href="/upload/cer/ci.rar">
							<span class="top-item cer">根证书下载 </span>
						</a> -->
						<span class="top-item padding-left-clear">|</span> <a
							href="/about/index.html?id=59" style="color: #000">下载中心</a> </span>
					</div>

					<c:if test="${login_user != null }">
						<div class="pull-right">
							<a href="/stationMail/searchIndex.html"><img alt="" src="/static/front/images/message/message1.png" id="messageicon"></a>
							<span class="top-item UID"> &nbsp;推荐码 <span
								style="margin-left: 17px;" class="hide-item"> <i
									class="topimg"></i>
									<div style="vertical-align: middle" id="uidqrcode"></div>
									<p>
										扫描获取推荐码<br />
									</p>
							</span>
							</span> </span> <span class="top-item user-slide"> <a
								href="/financial/index.html">您好，${login_user.floginName}</a> <span
								class="caret"></span> <span class="slide-box">
									<div class="slide-box-mark"></div>
									<div class="rows">
										<div class="slide-box-top">
											<span class="slide-levelBox"> <span
												class="slide_vip vip${login_user.fscore.flevel }"></span> <span
												class="slide_vip-hint">会员</span>


											</span> <span class="slide-con"> <span class="loginname">${login_user.floginName}</span>
												<span class="uid"> UID:${login_user.fid} </span>

											</span> <span class="slide-con"> <a
												href="/user/security.html"
												class="btn btn-link pull-right slide-link">设置&gt;&gt;</a>
											</span>
										</div>
									</div>
									<div class="slide-box-con">
										<div class="assets">
											<span> 总资产： <span id="headertotalasset"
												class="text-danger"> ￥<fmt:formatNumber
														value="${totalCapitalTrade }" pattern="##.##"
														maxIntegerDigits="10" maxFractionDigits="4" />
											</span> <a href="/financial/index.html" style="float: right">更多>></a>
											</span>

										</div>
										<div class="assets-detail">
											<ul class="first title clearfix">
												<li class="col-xs-4 padding-left-clear">资产</li>
												<li class="col-xs-4 text-center">可用</li>
												<li class="col-xs-4 text-center">冻结</li>
											</ul>
											<ul id="headercny0" class="clearfix">
												<li class="col-xs-4 padding-left-clear">人民币</li>
												<li class="col-xs-4 text-center text-danger"><fmt:formatNumber
														value="${requestScope.fwallet.ftotalRmb }" pattern="##.##"
														maxIntegerDigits="10" maxFractionDigits="4" /></li>
												<li class="col-xs-4 text-center"><fmt:formatNumber
														value="${requestScope.fwallet.ffrozenRmb }"
														pattern="##.##" maxIntegerDigits="10"
														maxFractionDigits="4" /></li>
											</ul>
											<c:forEach items="${requestScope.fvirtualwallets }" var="v"
												varStatus="vs" begin="0">
												<ul id="headercoin3729" class="clearfix first">
													<li class="col-xs-4 padding-left-clear"><div
															style="display: inline-block; line-height: 15px;">${v.fvirtualcointype.fname }</div></li>
													<li class="col-xs-4 text-center text-danger"><fmt:formatNumber
															value="${v.ftotal }" pattern="##.##"
															maxIntegerDigits="10" maxFractionDigits="4" /></li>
													<li class="col-xs-4 text-center"><fmt:formatNumber
															value="${v.ffrozen }" pattern="##.##"
															maxIntegerDigits="10" maxFractionDigits="4" /></li>
												</ul>
											</c:forEach>

										</div>
										<div class="assets-btn">
											<a href="/account/rechargeCny.html"
												class="btn btn-danger btn-block">充值</a> <a
												href="/account/withdrawCny.html"
												class="btn btn-success btn-block">提现</a>
										</div>
									</div>
							</span>
							</span> <span class="top-item padding-left-clear padding-right-clear">|</span>
							<a href="/user/logout.html" class="top-item">退出</a>
						</div>
					</c:if>

					<c:if test="${login_user == null }">
						<div class="pull-right">
							<a href="/" class="top-item">登录</a> <span
								class="top-item padding-left-clear padding-right-clear">|</span>
							<a href="/user/register.html" class="top-item"
								style="margin-left: -10px;">注册</a>
						</div>
					</c:if>
				</div>
			</div>
		</div>
		<div class="container-full header-nav">
			<div class="container">
				<div class="col-xs-12">
					<div class="navbar">
						<div class="navbar-header navbar-default">
							<a class="navbar-brand" href="/index.html"> <img
								alt="${requestScope.constant['webName']}"
								src="${requestScope.constant['logoImage'] }">
							</a>
						</div>
						<div class="collapse navbar-collapse navbar-right">
							<ul class="nav navbar-nav ">
								<li class=""><a href="/index.html">首页</a> <span
									class="split hidden-xs">|</span></li>
								<c:if
									test='${requestScope.constant["isHiddenDeal"] eq "false" }'>
									<li class=""><a href="/trade/coin.html">交易中心</a> <span
										class="split hidden-xs">|</span></li>
								</c:if>
								<c:if test='${requestScope.constant["isHiddenDeal"] eq "true" }'>
									<li></li>
								</c:if>
								<c:if
									test='${requestScope.constant["ISHIDDEN_CROWDFUNDING"] eq "false" }'>
									<li class=""><a href="/crowd/index.html">众筹</a> <span
										class="split hidden-xs">|</span></li>
								</c:if>
								<c:if
									test='${requestScope.constant["ISHIDDEN_CROWDFUNDING"] eq "true" }'>
									<li></li>
								</c:if>
								<c:if test='${requestScope.constant["isHiddenEX"] eq "false" }'>
									<li class=""><a href="/exchange/index.html?type=0">兑换中心</a>
										<span class="split hidden-xs">|</span></li>
								</c:if>
								<c:if test='${requestScope.constant["isHiddenEX"] eq "true" }'>
									<li></li>
								</c:if>
								<li class=""><a href="/finacing/index.html">利息理财</a> <span
									class="split hidden-xs">|</span></li>
										<li class=""><a href="/financial/index.html">财务中心</a> <span
									class="split hidden-xs">|</span></li>
								<li class=""><a href="/user/security.html">个人中心</a> <span
									class="split hidden-xs">|</span></li>
								<li class=""><a href="/about/index.html?id=1">帮助中心</a></li>
							</ul>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<c:if test="${isIndex !=1 }">
		<div class="container-full notice" style="overflow: hidden;">
			<div class="container text-center" id="newstoplist"
				style="overflow: hidden; height: 50px; width: auto; color: #ffffff;">
				<div id=newsList>

					<c:forEach items="${requestScope.constant['news']}" var="v">
						<p>
							<a href="${v.url }" class="notice-item" target="_blank"> <i
								class="notice-item-icon"></i> ${v.ftitle }
							</a>
						</p>
					</c:forEach>
				</div>
			</div>
		</div>
	</c:if>
</div>


<script type="text/javascript"
	src="/static/front/js/plugin/jquery.qrcode.min.js"></script>

<script type="text/javascript">
	$('#uidqrcode').qrcode({
		text : '${login_user.floginName}',
		width : "100",
		height : "100",
		render : "table"
	});
	//
	/* $('#iosapp').qrcode({
	 text : 'http://fusion.qq.com/cgi-bin/qzapps/unified_jump?appid=52453794&from=wx&isTimeline=false&actionFlag=0&params=pname%3Dcom.zhongji.tzt%26versioncode%3D1%26channelid%3D%26actionflag%3D0',
	 width : "300",
	 height : "300",
	 render : "table"
	 }); */
	var headerpath = window.location.pathname;
	var lis = $(".navbar-nav li");
	if (headerpath == "/index.html" || headerpath.startWith("/service/")
			|| headerpath.startWith("/question/")) {
		lis.eq(0).addClass("active");
	} else if (headerpath == "/index.html" || headerpath.startWith("/about/")) {
		lis.eq(7).addClass("active");
	} else if (headerpath.startWith("/trade/")) {
		lis.eq(1).addClass("active");
	}else if (headerpath.startWith("/crowd/")
			&& !headerpath.startWith("/crowd/logs")) {
		lis.eq(2).addClass("active");
	} else if (headerpath.startWith("/financial/")
			|| headerpath.startWith("/account/")
			|| headerpath.startWith("/crowd/logs")
			|| headerpath.startWith("/introl/")
			|| headerpath.startWith("/lottery/logs")) {
		lis.eq(5).addClass("active");
	}else if(headerpath.startWith("/finacing/")){
		lis.eq(4).addClass("active");
	}else if(headerpath.startWith("/stationMail/")){
	 lis.eq(6).addClass("active") ;
	 } 
	else if (headerpath.startWith("/user/")) {
		lis.eq(6).addClass("active");
	} else if (headerpath.startWith("/exchange/index")) {
		lis.eq(3).addClass("active");
	}
	
	function getMessage(){
		$.ajax({
			url:"/stationMail/unread.html",
			data:{},
			success:function(res){
				if(res.data>0){
					$("#messageicon").attr("src","/static/front/images/message/message.png");
				}else{
					$("#messageicon").attr("src","/static/front/images/message/messageNone.png")
				}
				setTimeout(getMessage,2000);
			}
		})
	}
	$(function(){
	getMessage();
	});
</script>