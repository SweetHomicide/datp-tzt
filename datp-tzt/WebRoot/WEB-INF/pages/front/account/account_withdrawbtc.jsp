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

<link rel="stylesheet" href="/static/front/css/finance/withdraw.css"
	type="text/css"></link>
<link href="/static/front/css/trade/trade.css" rel="stylesheet"
	type="text/css" media="screen, projection" />
<style>
ul, li {
	list-style: none;
	padding: 0;
	margin: 0;
}

#dropdown {
	width: 400px;
	position: relative;
	z-index: 200;
	background: #fff
}

.input_select {
	width: 400px;
	height: 24px;
	line-height: 24px;
	padding-left: 4px;
	padding-right: 30px;
	border: 1px solid #a9c9e2;
	background: #e8f5fe url(arrow.gif) no-repeat rightright 4px;
	color: #807a62;
}

#dropdown ul {
	width: 400px;
	margin-top: 0px;
	border: 1px solid #a9c9e2;
	position: absolute;
	display: none;
	background: #fff
}

#dropdown ul li {
	height: 24px;
	line-height: 24px;
	text-indent: 10px
}

#dropdown ul li a {
	display: block;
	height: 24px;
	color: #807a62;
	text-decoration: none
}

#dropdown ul li a:hover {
	background: #c6dbfc;
	color: #369
}
</style>
</head>
<body>
	<input type="hidden" id="max_double"
		value="${requestScope.constant['maxwithdrawbtc'] }">
	<input type="hidden" id="min_double"
		value="${requestScope.constant['minwithdrawbtc'] }">


	<%@include file="../comm/header.jsp"%>

	<div class="container-full padding-top-30">
		<div class="container">

			<%@include file="../comm/left_menu.jsp"%>

			<div class="col-xs-10 padding-right-clear">
				<div
					class="col-xs-12 padding-right-clear padding-left-clear rightarea withdraw">
					<div class="col-xs-12 rightarea-con">
						<div class="col-xs-4" style="background: #fff;" id="divy3">
							<div class="all_coin_info2">
								<span class="lefticon col-xs-2"
									style="top:5px;margin-right:5px;width:50px;height:50px;background-image: url('${fvirtualcointype.furl }') ;background-size:100%;"></span>
								<a href="javascript:void(0);" rel="drevil" title="请选择" class="cointype"
									id="cointype"> <!-- onmouseover="changeDown()" onmouseout="changeUp()" -->${fvirtualcointype.fname }</a>
								<span style="float: right; margin-top: 20px;" class="arrow-down"
									id="icon1"></span>
							</div>

							<div id="divy4"
								style="display: none; z-index: 1000; width: 500px; background: #fff; position: absolute; border: 1px solid #ccc; top: 50px; left: 0px;">
								<div class="row" style="padding-top: 15px;">
									<div class="col-xs-6 col-xs-offset-6" style="right: 10px">

										<div class="input-group form-group">
											<input type="text" class="form-control searchname"
												name="searchName" id="searchname" placeholder="输入名称"
												onkeypress="if(event.keyCode==13) {searchType2(1);}" /> <span
												class="input-group-btn"> <input type="hidden"
												id="hidlog" />
												<button class="btn btn-primary" id="searchType"
													onclick="searchType2(1);"><i class="glyphicon glyphicon-search"></i></button>
										</div>
									</div>

								</div>
								<div style="border: 1px solid #f8f8f8; margin-bottom: 10px;"></div>
								<div id="divy2"></div>
								<div class="row" style="padding-bottom: 10px;">
									<div class="col-xs-12">
										<input type="hidden" value="${cur_page }" name="currentPage"
											id="currentPage"></input> <span
											style="float: right; margin-right: 10px;" id="pagey">
											${page} </span>
									</div>
								</div>
							</div>
						</div>
						<div class="row" style="border-bottom: 2px solid #f8f8f8"></div>
						<div class="col-xs-12 padding-clear padding-top-30">
							<div class="col-xs-7 padding-clear form-horizontal">
								<div class="form-group ">
									<label for="accountBalance" class="col-xs-3 control-label">账户余额</label>
									<div class="col-xs-6">
										<span class="form-control border-fff" id="accountBalance"><fmt:formatNumber
												value="${fvirtualwallet.ftotal }" pattern="##.##"
												maxIntegerDigits="10" maxFractionDigits="4" /></span>
									</div>
								</div>
								<div class="form-group ">
									<label for="withdrawAddr" class="col-xs-3 control-label">转出地址</label>

									<div class="col-xs-9">
										<div id="dropdown">
											<input id="withdrawAddr" class="input_select" type="text"
												class="form-control" data="" contenteditable="fasle" />
											<ul>
												<c:forEach items="${fvirtualaddressWithdraws }" var="v">
													<li><a href="javascript:void(0)" value="${v.fid }">${v.fremark}-${v.fadderess }</a></li>
												</c:forEach>
											</ul>
											<%-- 	<select id="withdrawAddr" class="form-control" >
											<c:forEach items="${fvirtualaddressWithdraws }" var="v">
												<option value="${v.fid }" style="z-index:200;">${v.fremark}-${v.fadderess } </option>
											</c:forEach>
										</select> --%>

											<a href="#" class="text-primary addtips" data-toggle="modal"
												data-target="#address">去新增>></a>
										</div>
									</div>
								</div>
								<div class="form-group ">
									<label for="withdrawAmount" class="col-xs-3 control-label">转出数量</label>
									<div class="col-xs-6">
										<input id="withdrawAmount" class="form-control" type="text"
											onBlur="valicount()">
									</div>
								</div>
								<div class="form-group ">
									<label for="password" class="col-xs-3 control-label">交易密码</label>
									<div class="col-xs-6">
										<input id="tradePwd" class="form-control" type="password">
									</div>
								</div>

								<c:if test="${isBindTelephone == true }">
									<div class="form-group">
										<label for="withdrawPhoneCode" class="col-xs-3 control-label">短信验证码</label>
										<div class="col-xs-6">
											<input id="withdrawPhoneCode" class="form-control"
												type="text">
											<button id="withdrawsendmessage" data-msgtype="5"
												data-tipsid="withdrawerrortips" class="btn btn-sendmsg">发送验证码</button>
										</div>
									</div>
								</c:if>

								<c:if test="${isBindGoogle ==true}">
									<div class="form-group">
										<label for="withdrawTotpCode" class="col-xs-3 control-label">谷歌验证码</label>
										<div class="col-xs-6">
											<input id="withdrawTotpCode" class="form-control" type="text">
										</div>
									</div>
								</c:if>

								<div class="form-group">
									<label for="diyMoney" class="col-xs-3 control-label"></label>
									<div class="col-xs-6">
										<span id="withdrawerrortips" class="text-danger"> </span>
									</div>
								</div>
								<div class="form-group">
									<label for="diyMoney" class="col-xs-3 control-label"></label>
									<div class="col-xs-6">
										<button id="withdrawBtcButton"
											class="btn btn-danger btn-block">立即转出</button>
									</div>
								</div>
							</div>
							<div class="col-xs-12 padding-clear padding-top-30">
								<div class="panel panel-tips">
									<div class="panel-header text-center text-danger">
										<span class="panel-title">转出须知</span>
									</div>
									<div class="panel-body">
										<p>&lt ${requestScope.constant['withdrawCOINDesc'] }</p>
										<p>&lt 最小转出数量为${requestScope.constant['minwithdrawbtc']
											}个。</p>
									</div>
								</div>
							</div>
							<div class="col-xs-12 padding-clear padding-top-30">
								<div class="panel border">
									<div class="panel-heading">
										<span class="text-danger">${fvirtualcointype.fname }转出记录</span>
										<span class="pull-right recordtitle" data-type="0"
											data-value="0">收起 -</span>
									</div>
									<div class="panel-body" id="recordbody0">
										<table class="table">
											<tr>
												<td>转出时间</td>
												<td>转出地址</td>
												<td>转出数量</td>
												<td>手续费</td>
												<td>转出状态</td>
												<!-- <td>备注号</td> -->
											</tr>

											<c:forEach items="${fvirtualcaptualoperations }"
												varStatus="vs" var="v">
												<tr>
													<td ><fmt:formatDate
															value="${v.fcreateTime }" pattern="yyyy-MM-dd HH:mm:ss" /></td>
													<td >${v.withdraw_virtual_address }</td>
													<td>${fvirtualcointype.fSymbol}${v.famount }</td>
													<td >${fvirtualcointype.fSymbol}${v.ffees }</td>
													<td >${v.fstatus_s }<%-- <c:if
															test="${v.fstatus==1 }">
								&nbsp;|&nbsp;
									<a class="cancelWithdrawBtc" href="javascript:void(0);"
																data-fid="${v.fid }">取消</a>
														</c:if> --%>
													</td>
													<%-- <td width="100">${v.fid }</td> --%>
												</tr>
											</c:forEach> 

											<c:if test="${fn:length(fvirtualcaptualoperations)==0 }">
												<tr>
													<td colspan="6" class="no-data-tips"><span>
															您暂时没有转出记录。 </span></td>
												</tr>
											</c:if>

										</table>

										<input type="hidden" value="${cur_page }" name="currentPage"
											id="currentPage"></input>
										<div class="text-right">${pagin }</div>

									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="modal modal-custom fade" id="address" tabindex="-1"
		role="dialog" aria-labelledby="exampleModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-mark"></div>
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close btn-modal" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<span class="modal-title" id="exampleModalLabel">转出地址</span>
				</div>
				<div class="modal-body form-horizontal">
					<div class="form-group ">
						<label for="withdrawBtcAddr" class="col-xs-3 control-label">转出地址</label>
						<div class="col-xs-8">
							<input id="withdrawBtcAddr" class="form-control" type="text">
						</div>
					</div>
					<div class="form-group ">
						<label for="withdrawBtcRemark" class="col-xs-3 control-label">备注</label>
						<div class="col-xs-8">
							<input id="withdrawBtcRemark" class="form-control" type="" text>
						</div>
					</div>
					<div class="form-group ">
						<label for="withdrawBtcPass" class="col-xs-3 control-label">交易密码</label>
						<div class="col-xs-8">
							<input id="withdrawBtcPass" class="form-control" type="password">
						</div>
					</div>
					<!-- 2017-02-21 liuruichen 去掉短信验证与谷歌验证 -->
					<!--  		<c:if test="${isBindTelephone == true }">
						<div class="form-group">
							<label for="withdrawBtcAddrPhoneCode" class="col-xs-3 control-label">短信验证码</label>
							<div class="col-xs-8">
								<input id="withdrawBtcAddrPhoneCode" class="form-control" type="text">
								<button id="bindsendmessage" data-msgtype="8" data-tipsid="binderrortips" class="btn btn-sendmsg">发送验证码</button>
							</div>
						</div>
					</c:if>	
					
					<c:if test="${isBindGoogle ==true}">
						<div class="form-group">
							<label for="withdrawBtcAddrTotpCode" class="col-xs-3 control-label">谷歌验证码</label>
							<div class="col-xs-8">
								<input id="withdrawBtcAddrTotpCode" class="form-control" type="text">
							</div>
						</div>
					</c:if>
					-->
					<div class="form-group">
						<label for="diyMoney" class="col-xs-3 control-label"></label>
						<div class="col-xs-8">
							<span id="binderrortips" class="text-danger"></span>
						</div>
					</div>
					<div class="form-group">
						<label for="diyMoney" class="col-xs-3 control-label"></label>
						<div class="col-xs-8">
							<button id="withdrawBtcAddrBtn" class="btn btn-danger btn-block">确定提交</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<input type="hidden" id="isEmptyAuth" value="${isEmptyAuth }">
	<input type="hidden" id="symbol" value="${fvirtualcointype.fid }">
	<input type="hidden"
		value="<fmt:formatNumber value="${fvirtualwallet.ftotal }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/>"
		id="btcbalance" name="btcbalance">
	<input type="hidden" value="${fvirtualcointype.fShortName }"
		id="coinName" name="coinName">


	<%@include file="../comm/footer.jsp"%>

	<script type="text/javascript" src="/static/front/js/comm/msg.js"></script>
	<script type="text/javascript"
		src="/static/front/js/finance/account.withdraw.js"></script>
	<script type="text/javascript" src="/static/front/js/trade/dongtai.js"></script>
	<script>
		$(function() {
			$(".input_select").click(function() {
				var ul = $("#dropdown ul");
				if (ul.css("display") == "none") {
					ul.slideDown("fast");
				} else {
					ul.slideUp("fast");
				}
			});
			/* $(".input_select").blur(function(){
				alert("离开");
			}) */
			$("#dropdown ul li a").click(function() {
				var txt = $(this).text();
				var data = $(this).attr("value");
				$(".input_select").val(txt);
				$(".input_select").attr("data", data);
				$("#dropdown ul").hide();

			});

			$("#dropdown ul li").blur(function() {

			});

		});
	</script>
</body>
</html>
