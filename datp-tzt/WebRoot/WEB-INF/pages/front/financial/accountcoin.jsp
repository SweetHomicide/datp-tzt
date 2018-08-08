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
<link rel="stylesheet"
	href="/static/front/css/finance/accountassets.css" type="text/css"></link>
<link href="/static/front/css/trade/trade.css" rel="stylesheet"
	type="text/css" media="screen, projection" />

</head>
<body>





	<%@include file="../comm/header.jsp"%>

	<div class="container-full padding-top-30">
		<div class="container">

			<%@include file="../comm/left_menu.jsp"%>

			<div class="col-xs-10 padding-right-clear">
				<div
					class="col-xs-12 padding-right-clear padding-left-clear rightarea assets">
					<div class="col-xs-12 rightarea-con">
						<ul class="nav nav-tabs rightarea-tabs">
							<li class=""><a href="/financial/accountbank.html">银行卡管理</a>
							</li>
							<!-- <li>
								<a href="/financial/accountalipay.html">支付宝管理</a>
							</li> -->
							<li class="active"><a href="/financial/accountcointype.html">虚拟地址管理</a>
							</li>

						</ul>
						<div class="row" style="background: #fff">
							<div class="col-xs-4" style="background: #fff;" id="divxx3">
								<div class="all_coin_info1">
									<span class="lefticon col-xs-2"
										style="margin-right:5px;top:5px;width:50px;height:50px;background-image: url('${fvirtualcointype.furl }') ;background-size:100%;"></span>
									<a href="javascript:void(0);" rel="drevil" title="请选择"
										class="cointype" id="cointype"> <!-- onmouseover="changeDown()" onmouseout="changeUp()" -->${fvirtualcointype.fname }</a>
									<span class="arrow-down" id="iconxx1"></span>
								</div>


								<div id="divxx4"
									style="display: none; z-index: 1000; width: 500px; background: #fff; position: absolute; border: 1px solid #ccc; top: 65px; left: 0px;">
									<div class="row" style="padding-top: 15px;">
										<div class="col-xs-6 col-xs-offset-6" style="right: 10px">

											<div class="input-group form-group">
												<input type="text" class="form-control searchname" 
													name="searchName" id="searchname" placeholder="输入名称"
													onkeypress="if(event.keyCode==13) {searchType3(1);}" /> <span
													class="input-group-btn"> <input type="hidden"
													id="hidlog" />
													<button class="btn btn-primary" id="searchType"
														onclick="searchType3(1);">
														<i class="glyphicon glyphicon-search"></i>
													</button>
											</div>
										</div>

									</div>
									<hr>
									<div id="divxx2"></div>
									<div class="row" style="padding-bottom: 10px;">
										<div class="col-xs-12">
											<input type="hidden" value="${cur_page }" name="currentPage"
												id="currentPage"></input> <span
												style="float: right; margin-right: 10px;" id="pagexx">
												${page} </span>
										</div>
									</div>
								</div>
							</div>

							<div class="col-xs-8" style="top: 35px;">
								<div class="col-xs-6 col-xs-offset-4" style="margin-right:-45px;">
									<div class="input-group form-group">
										<input type="text" class="form-control searchname"
											name="searchAccountName" id="searchAccountName"
											placeholder="输入备注名称" value="${searchname }"
											onkeypress="if(event.keyCode==13) {searchAccount(1);}" /> <span
											class="input-group-btn"> <input type="hidden"
											id="hidlog1" value="${searchname }" />
											<button class="btn btn-primary" id="searchType"
												onclick="btnSearchAccount(1);">
												<i class="glyphicon glyphicon-search"></i>
											</button>
									</div>
								</div>
								<div class="col-xs-2">
									<div class=" bank-add coin-add text-center" data-toggle="modal"
										data-target="#addCoinAddress">
										<span class="icon"></span>
									</div>
								</div>


							</div>


						</div>
						<div class="col-xs-12 padding-clear padding-top-30">

							<c:forEach items="${alls }" var="v">
								<div class="col-xs-6">
									<div class="coin-item">
										<div class="coin-item-top">
											<div class="col-xs-11">
												<p>${v.fadderess }</p>
											</div>
											<div class="col-xs-1 text-center padding-clear">
												<span class="coin-item-del" data-fid="${v.fid }"></span>
											</div>
										</div>
										<div class="coin-item-bot">
											<div class="col-xs-12">
												<span class="coin-item-code"> <span
													class="addresscode" data-text="${v.fadderess }"
													data-fid="${v.fid }"> <span class="qrcode"
														id="qrcode${v.fid }"></span>
												</span> ${v.fremark }
												</span>
											</div>
										</div>
									</div>
								</div>
							</c:forEach>

							<!-- <div class="col-xs-6">
								<div class="bank-add coin-add text-center" data-toggle="modal" data-target="#addCoinAddress">
									<span class="icon"></span>
									<br>
									<span>添加地址</span>
								</div>
							</div> -->
						</div>
							<div class="text-right">${pagin }</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="modal modal-custom fade" id="addCoinAddress" tabindex="-1"
		role="dialog" aria-labelledby="exampleModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-mark"></div>
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close btn-modal" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<span class="modal-title" id="exampleModalLabel">提现地址</span>
				</div>
				<div class="modal-body form-horizontal">
					<div class="form-group ">
						<label for="withdrawBtcAddr" class="col-xs-3 control-label">提现地址</label>
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

					<c:if test="${isBindTelephone == true }">
						<div class="form-group">
							<label for="withdrawBtcAddrPhoneCode"
								class="col-xs-3 control-label">短信验证码</label>
							<div class="col-xs-8">
								<input id="withdrawBtcAddrPhoneCode" class="form-control"
									type="text">
								<button id="bindsendmessage" data-msgtype="8"
									data-tipsid="binderrortips" class="btn btn-sendmsg">发送验证码</button>
							</div>
						</div>
					</c:if>

					<c:if test="${isBindGoogle ==true}">
						<div class="form-group">
							<label for="withdrawBtcAddrTotpCode"
								class="col-xs-3 control-label">谷歌验证码</label>
							<div class="col-xs-8">
								<input id="withdrawBtcAddrTotpCode" class="form-control"
									type="text">
							</div>
						</div>
					</c:if>

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




	<%@include file="../comm/footer.jsp"%>
	<input type="hidden" id="symbol" value="${fvirtualcointype.fid }">
	<input type="hidden" value="${fvirtualcointype.fname }" id="coinName"
		name="coinName">
	<input type="hidden" id="currentPage" value="${currentPage }">
	<script type="text/javascript" src="/static/front/js/comm/msg.js"></script>
	<script type="text/javascript"
		src="/static/front/js/plugin/jquery.qrcode.min.js"></script>
	<script type="text/javascript"
		src="/static/front/js/finance/account.assets.js"></script>
	<script type="text/javascript" src="/static/front/js/trade/dongtai.js"></script>
</body>
</html>
