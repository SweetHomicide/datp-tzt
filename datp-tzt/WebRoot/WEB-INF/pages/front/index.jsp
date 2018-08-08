<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="comm/include.inc.jsp"%>
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
<%@include file="comm/link.inc.jsp"%>
<link rel="stylesheet" href="/static/front/css/index/index.css"
	type="text/css"></link>
</head>
<body class="gray-bg">
	<%@include file="comm/header.jsp"%>
	<div class="container-full ">
		<div class="container login-box">
			<c:if test="${login_user!=null }">
				<div class="login loginsuccess">
					<div class="login-bg loginin"></div>
					<div class="login-cn form-horizontal">

						<div class="login-login-cn">
							<div class="form-group">
								<div class="col-xs-12">
									<a href="/financial/index.html">
										<h3 class="margin-top-clear font-size-18">${login_user.floginName}</h3>
									</a>
								</div>
							</div>
							<div class="form-group">
								<div class="col-xs-12">
									<span class="infobox"> <span class="info-left">预估总资产</span>
										<span class="info-right"> ￥<fmt:formatNumber
												value="${totalCapitalTrade }" pattern="##.##"
												maxIntegerDigits="10" maxFractionDigits="4" />
									</span>
									</span>
								</div>
								<div class="col-xs-12">
									<span class="infobox"> <span class="info-left">可用人民币</span>
										<span class="info-right"> ￥<fmt:formatNumber
												value="${requestScope.fwallet.ftotalRmb }" pattern="##.##"
												maxIntegerDigits="10" maxFractionDigits="4" />
									</span>
									</span>
								</div>
							</div>
						</div>
						<c:if test='${requestScope.constant["isHiddenDeal"] eq "false" }'>
							<div class="form-group">
								<div class="col-xs-12">
									<a href="/trade/coin.html" class="btn btn-primary btn-block">进入交易中心</a>
								</div>
							</div>
						</c:if>
						<div class="form-group">
							<div class="col-xs-6 btn-right">
								<a href="/account/rechargeCny.html"
									class="btn btn-primary btn-block">充值</a>
							</div>
							<div class="col-xs-6 btn-left">
								<a href="/account/withdrawCny.html"
									class="btn btn-primary btn-block">提现</a>
							</div>
						</div>
					</div>
				</div>
			</c:if>

			<c:if test="${login_user==null }">
				<div class="login">
					<div class="login-bg"></div>
					<div class="login-other-bg"></div>
					<div class="login-cn">
						<div class="form-group">
							<h3 class="margin-top-clear">
								登 录
								<%-- ${requestScope.constant['webName']} --%>
							</h3>
						</div>
						<div class="form-group">
							<input class="form-control" id="indexLoginName" value=""
								placeholder="输入邮件或手机" type="text">
						</div>
						<div class="form-group">
							<input class="form-control" id="indexLoginPwd" value=""
								placeholder="输入密码" type="password">
						</div>
						<div class="form-group has-error">
							<span id="indexLoginTips"
								class="errortips text-danger help-block"></span>
						</div>
						<div class="form-group">
							<button id="loginbtn" class="btn btn-block btn-primary">
								登录</button>
						</div>
						<div class="form-group">
							<a href="validate/reset.html">忘记密码？</a> <a
								href="user/register.html" class="pull-right">注册</a>
						</div>
					</div>

					<!-- <div class="login-other">
						<a href="/validate/reset.html">忘记密码？</a><span class="split"></span> 
						<a href="/user/register.html" class="pull-right">注册</a>
					</div> -->
					<!-- <div class="login-other">
						<a href="/link/qq/call.html">QQ登录</a>
						<a href="javascript:weixin();" class="pull-right">微信登录</a>
					</div> -->
				</div>
			</c:if>

		</div>
		<div id="shuffling" class="carousel slide" data-ride="carousel">
			<ol class="carousel-indicators">
				<li data-target="#shuffling" data-slide-to="0" class="active"></li>
				<li data-target="#shuffling" data-slide-to="1"></li>
				<li data-target="#shuffling" data-slide-to="2"></li>
			</ol>
			<div class="carousel-inner">
				<a class="item active"
					href="${requestScope.constant['bigImageURL1'] }"
					style="background: url('${requestScope.constant['bigImage1'] }') no-repeat 50% 50%;height: 400px;"></a>
				<a class="item" href="${requestScope.constant['bigImageURL2'] }"
					style="background: url('${requestScope.constant['bigImage2'] }') no-repeat 50% 50%;height: 400px;"></a>
				<a class="item" href="${requestScope.constant['bigImageURL3'] }"
					style="background: url('${requestScope.constant['bigImage3'] }') no-repeat 50% 50%;height:400px;"></a>
			</div>
		</div>
	</div>


	<div class="container-full notice" style="overflow: hidden;">
		<div class="container text-center" id="newstoplist"
			style="overflow: hidden; height: 45px; width: auto; color: #ffffff;">
			<div id="newsList">
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

	<c:if test='${isHiddenDeal eq "false" }'>
		<div class="container-full index market">
			<div class="container">
				<div class="row" style="margin-bottom: 10px;">
					<div class="col-xs-12"
						style="padding-left: 10px; border-left: 7px solid #215591;">
						<div class="row">
							<div class="col-xs-12">
								<span style="font-size: 22px">交易类型</span>
							</div>
						</div>
						<div class="row">
							<div class="col-xs-4" style="font-size: 10px">
								<span style="color: red;">Transaction </span><span> Type</span>
							</div>
							<div class="col-xs-3 col-xs-offset-5">
								<div class="input-group">
									<input type="text" class="form-control input" id="searchCon"
										onkeypress="if(event.keyCode==13) {searchfirst();}"><span
										class="input-group-addon btn btn-primary" id="btnSearch"><i
										class="glyphicon glyphicon-search"></i></span>
								</div>
							</div>
						</div>
					</div>
				</div>

				<table class="table table-striped table-hover" id="row-value">
					<tr class="th">
						<td class="col-xs-2 text-center">数字资产</td>
						<td class="col-xs-2 text-center" id="_price">最新成交价<i
							class="cagret cagret-down"></i><i class="cagret cagret-up"></i></td>
						<td class="col-xs-2 text-center" id="_totalAmt">24H成交额<i
							class="cagret cagret-down"></i><i class="cagret cagret-up"></i></td>
						<td class="col-xs-2 text-center" id="_total">24H成交量<i
							class="cagret cagret-down"></i><i class="cagret cagret-up"></i></td>
						<td class="col-xs-1 text-center" id="_rose">24H涨跌<i
							class="cagret cagret-down"></i><i class="cagret cagret-up"></i></td>
						<td class="col-xs-2 text-center">价格趋势(3日)</td>
						<td class="col-xs-1 text-center">操作</td>
					</tr>
				</table>
				<input type="hidden" id="hidCol" value="" /> <input type="hidden"
					id="hidOrder" value="" /> <input type="hidden" id="hidSearch"
					value="" /> <input type="hidden" id="forwardUrl"
					value="${forwardUrl}" />
				<div id="fenye" style="float: right"></div>
			</div>
		</div>
	</c:if>
	<div class="container-full index news">
		<div class="container">
			<div class="row" style="margin-bottom: 10px;">
				<div class="col-xs-12"
					style="padding-left: 10px; border-left: 7px solid #215591;">
					<div class="row">
						<div class="col-xs-12">
							<span style="font-size: 22px">新闻资讯</span>
						</div>
					</div>
					<div class="row">
						<div class="col-xs-12" style="font-size: 10px">
							<span style="color: red;">News</span><span> Information </span>
						</div>
					</div>
				</div>
			</div>

			<div class="row">
				<div class="col-xs-4">
					<div class="announcement"></div>
					<div class="news-top clearfix">
						<span class="news-fist">${articles[1].key.fname}</span> <span
							class="news-more"> <a
							href="/service/ourService.html?id=${articles[1].key.fid }"
							style="color: Red;">更多>></a>
						</span>
					</div>

					<div class="news-all">
						<c:forEach items="${articles[1].value }" var="v" varStatus="n">
							<a href="${v.url }" class="news-font clearfix" target="_blank">

								<div class="news-content ${n.index <=2?'speliy':'' }">${v.ftitle_short }</div>
								<div class="news-date date">
									<fmt:formatDate value="${v.fcreateDate }" pattern="yyyy-MM-dd" />
								</div>
							</a>
						</c:forEach>
					</div>
				</div>



				<div class="col-xs-4">
					<div class="advisory"></div>
					<div class="news-top clearfix">
						<span class="news-fist">${articles[0].key.fname}</span> <span
							class="news-more"> <a
							href="/service/ourService.html?id=${articles[0].key.fid }"
							style="color: Red;">更多>></a>
						</span>
					</div>

					<div class="news-all">
						<c:forEach items="${articles[0].value }" var="v" varStatus="n">
							<a href="${v.url }" class="news-font clearfix" target="_blank">
								<div class="news-content ${n.index <=2?'speliy':'' }">${v.ftitle_short }</div>
								<div class="news-date date">
									<fmt:formatDate value="${v.fcreateDate }" pattern="yyyy-MM-dd" />
								</div>
							</a>
						</c:forEach>
					</div>
				</div>


				<div class="col-xs-4">
					<div class="information"></div>
					<div class="news-top clearfix">
						<span class="news-fist">${articles[2].key.fname}</span> <span
							class="news-more"> <a
							href="/service/ourService.html?id=${articles[2].key.fid }"
							style="color: Red;">更多>></a>
						</span>
					</div>

					<div class="news-all">
						<c:forEach items="${articles[2].value }" var="v" varStatus="n">
							<a href="${v.url }" class="news-font clearfix" target="_blank">
								<div class="news-content ${n.index <=2?'speliy':'' }">${v.ftitle_short }</div>
								<div class="news-date date">
									<fmt:formatDate value="${v.fcreateDate }" pattern="yyyy-MM-dd" />
								</div>
							</a>
						</c:forEach>
					</div>
				</div>
			</div>

			<div class="clearfix"></div>


		</div>
	</div>
	</div>
	<div class="container-full about index">

		<div class="container">
			<div class="row" style="margin-bottom: 10px;">
				<div class="col-xs-12"
					style="padding-left: 10px; border-left: 7px solid #215591;">
					<div class="row">
						<div class="col-xs-12">
							<span style="font-size: 22px">平台优势</span>
						</div>
					</div>
					<div class="row">
						<div class="col-xs-12" style="font-size: 10px">
							<span style="color: red;">Platform </span><span>Advantage</span>
						</div>
					</div>
				</div>
			</div>
			<div class="row about-con-box clearfix">
				<div class="col-xs-4 about-item">
					<div class="about-con">
						<div class="row">
							<div class="col-xs-12 ">
								<div class="about-img security"></div>
							</div>
						</div>
						<h3 class=" about-title">安全</h3>
						<p class="text-left">SSL、冷钱包机制、多重加密存储等银行级安全技术，流程化、责任化的多重审核管理体系。</p>
					</div>
				</div>
				<div class="col-xs-4 about-item">
					<div class="col-xs-12 about-con">

						<div class="row">
							<div class="col-xs-12 ">
								<div class="about-img quick"></div>
							</div>
						</div>
						<h3 class=" about-title">便捷</h3>
						<p class="text-left">界面简约易懂，操作方便。系统稳定高效，速度响应快。手机电脑等客户端，随时随地均可交易。</p>
					</div>
				</div>
				<div class="col-xs-4 about-item">
					<div class="col-xs-12 about-con">
						<div class="row">
							<div class="col-xs-12 ">
								<div class="about-img professional"></div>
							</div>
						</div>
						<h3 class=" about-title">平等</h3>
						<p class="text-left">交易有前景的投资资产，减少投资风险；致力于数字资产落地化，惠及更多线下交易者，享有平等投资机会。</p>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="container-full index news">
		<div class="container">
			<div class="row" style="margin-bottom: 10px;">
				<div class="col-xs-12"
					style="padding-left: 10px; border-left: 7px solid #215591;">
					<div class="row">
						<div class="col-xs-12">
							<span style="font-size: 22px">产品介绍</span>
						</div>
					</div>
					<div class="row">
						<div class="col-xs-12" style="font-size: 10px">
							<span style="color: red;">Project</span><span> Introduce </span>
						</div>
					</div>
				</div>
			</div>

			<div class="row" style="height: 250px;">
				<div class="col-xs-4">
					<div class="news-top clearfix">
						<a href="/about/index.html?id=402881f15e60d649015e60dd7b220434"
							style="text-decoration: none;"><p class="product"></p>
							<div style="color: #000; text-align: center; font-size: 20px;">产品信息
							</div></a>
					</div>
				</div>

				<div class="col-xs-4">
					<div class="news-top clearfix">
						<a href="http://localhost:8080/about/index.html?id=402881f15e60d649015e60de4ad3043a"
							style="text-decoration: none;"><p class="asserInfo"></p>
							<div style="color: #000; text-align: center; font-size: 20px;">数字资产信息
							</div></a>
					</div>
				</div>

				<div class="col-xs-4">
					<div class="news-top clearfix">
						<a href="http://localhost:8080/about/index.html?id=402881f15e60d649015e60deab92043d"
							style="text-decoration: none;"><p class="jys"></p>
							<div style="color: #000; text-align: center; font-size: 20px;">交易所IT运营指导信息
							</div></a>
					</div>
				</div>
			</div>

			<div class="clearfix"></div>


		</div>
	</div>
	<div class="container-full about index">

		<div class="container">
			<div class="row" style="margin-bottom: 10px;">
				<div class="col-xs-12"
					style="padding-left: 10px; border-left: 7px solid #215591;">
					<div class="row">
						<div class="col-xs-12">
							<span style="font-size: 22px">新手指引</span>
						</div>
					</div>
					<div class="row">
						<div class="col-xs-12" style="font-size: 10px">
							<span style="color: red;">Novice </span><span>Guide</span>
						</div>
					</div>
				</div>
			</div>
			<div class="row" style="margin-top: 50px; margin-bottom: 50px;">
				<div class="col-xs-2" style="margin: 0 auto;">
					<div id="hexagon">
						<div class="icon-regist"></div>
					</div>
					<div class="row" style="margin-top: 60px;">
						<div class="col-xs-12">

							<span style="font-size: 18px; margin-left: 30px">注册</span>
						</div>
					</div>
				</div>
				<div class="col-xs-2" style="margin: 0 auto;">
					<div id="hexagon">
						<div class="icon-authentication"></div>
					</div>
					<div class="row" style="margin-top: 60px;">
						<div class="col-xs-12">

							<span style="font-size: 18px; margin-left: 15px">实名认证</span>
						</div>
					</div>
				</div>
				<div class="col-xs-2" style="margin: 0 auto;">
					<div id="hexagon">
						<div class="icon-recharge"></div>
					</div>
					<div class="row" style="margin-top: 60px;">
						<div class="col-xs-12">

							<span style="font-size: 18px; margin-left: 30px">充值</span>
						</div>
					</div>
				</div>
				<div class="col-xs-2" style="margin: 0 auto;">
					<div id="hexagon">
						<div class="icon-Crowd"></div>
					</div>
					<div class="row" style="margin-top: 60px;">
						<div class="col-xs-12">

							<span style="font-size: 18px; margin-left: 30px">众筹</span>
						</div>
					</div>
				</div>
				<div class="col-xs-2" style="margin: 0 auto;">
					<div id="hexagon">
						<div class="icon-exchange"></div>
					</div>
					<div class="row" style="margin-top: 60px;">
						<div class="col-xs-12">

							<span style="font-size: 18px; margin-left: 30px">兑换</span>
						</div>
					</div>
				</div>
				<div class="col-xs-2" style="margin: 0 auto;">
					<div id="hexagon">
						<div class="icon-transaction"></div>
					</div>
					<div class="row" style="margin-top: 60px;">
						<div class="col-xs-12">

							<span style="font-size: 18px; margin-left: 30px">交易</span>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<div class="container-full index " style="background: #D8D9DE">
		<div class="container">
			<div class="row" style="padding-top: 10px; text-align: center;">

				<div class="col-xs-1 col-xs-offset-5 warn-image">
					<!-- 	<img src="../../images/index/warn.png" /> -->
				</div>

				<div class="col-xs-3 warn">风险提示！</div>

			</div>
			<div class="sostips">
				<p class="wea">${requestScope.constant['indexTips'] }</p>
			</div>
		</div>
	</div>

	<!--底部开始-->
	<%@include file="comm/footer.jsp"%>
	<input type="hidden" id="errormsg" value= />
	<input type="hidden" id="isHiddenDeal" value="${isHiddenDeal}" />
	<script type="text/javascript" src="/static/front/js/index/index.js"></script>
	<script type="text/javascript"
		src="/static/front/js/plugin/jquery.flot.js"></script>
</body>
</html>