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
<link rel="stylesheet" href="/static/front/css/finance/accountassets.css" type="text/css"></link>
</head>
<body>
	




  <%@include file="../comm/header.jsp" %>

	<div class="container-full padding-top-30">
		<div class="container">
			
			<%@include file="../comm/left_menu.jsp" %>

			<div class="col-xs-10 padding-right-clear">
				<div class="col-xs-12 padding-right-clear padding-left-clear rightarea assets">
					<div class="col-xs-12 rightarea-con">
						<ul class="nav nav-tabs rightarea-tabs">
							<li class="">
								<a href="/financial/accountbank.html">银行卡管理</a>
							</li>
							<li class="active">
								<a href="javascript:void(0)">支付宝管理</a>
							</li>
							<c:forEach items="${requestScope.constant['allWithdrawCoins'] }" var="v">
								<li class="${v.fid==symbol?'active':'' }">
									<a href="/financial/accountcoin.html?symbol=${v.fid }">${v.fShortName } 提现管理</a>
								</li>
							</c:forEach>
							
						</ul>
						<div class="col-xs-12 padding-clear padding-top-30">
							
								<c:forEach items="${bankinfos }" var="v">
								<div class="col-xs-4">
									<div class="bank-item alipay-item">
										<div class="bank-item-top">
											<div class="col-xs-2">
											<i class="banklogo banklogo-item${v.fbankType }"></i>
											</div>
											<div class="col-xs-8">
												<p>${v.faddress }</p>
											</div>
											<div class="col-xs-2">
												<span class="bank-item-del" data-fid="${v.fid }"></span>
											</div>
										</div>
										<div class="bank-item-bot">
											<div class="col-xs-12">
												<p>${v.fname }</p>
												<p>${v.fbankNumber }</p>
											</div>
										</div>
									</div>
								</div>
							</c:forEach>
							
							<div class="col-xs-4">
								<div class="bank-add text-center" data-toggle="modal" data-target="#addAlipayAddress">
									<span class="icon"></span>
									<br>
									<span>添加支付宝</span>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="modal modal-custom fade" id="addAlipayAddress" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-mark"></div>
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close btn-modal" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<span class="modal-title" id="exampleModalLabel">添加支付宝</span>
				</div>
				<div class="modal-body form-horizontal">
					<div class="form-group ">
						<label for="payeeAddr" class="col-xs-3 control-label">支付宝姓名</label>
						<div class="col-xs-6">
							<input id="payeeAddr" class="form-control" type="text" placeholder="请输入您的支付宝开户人" />
							<span class="help-block text-danger">*支付宝姓名必须与您的实名认证姓名一致</span>
						</div>
					</div>
					<div class="form-group ">
						<label for="withdrawAccountAddr" class="col-xs-3 control-label">支付宝帐号</label>
						<div class="col-xs-6">
							<input id="withdrawAccountAddr" class="form-control" type="text" placeholder="请输入您的详细地址" />
						</div>
					</div>
					
					<c:if test="${isBindTelephone == true }">
						<div class="form-group">
							<label for="addressPhoneCode" class="col-xs-3 control-label">短信验证码</label>
							<div class="col-xs-6">
								<input id="addressPhoneCode" class="form-control" type="text">
								<button id="bindsendmessage" data-msgtype="10" data-tipsid="binderrortips" class="btn btn-sendmsg">发送验证码</button>
							</div>
						</div>
					</c:if>	
					
					<c:if test="${isBindGoogle ==true}">
						<div class="form-group">
							<label for="addressTotpCode" class="col-xs-3 control-label">谷歌验证码</label>
							<div class="col-xs-6">
								<input id="addressTotpCode" class="form-control" type="text">
							</div>
						</div>
					</c:if>	
					
					<div class="form-group">
						<label for="binderrortips" class="col-xs-3 control-label"></label>
						<div class="col-xs-6">
							<span id="binderrortips" class="text-danger"></span>
						</div>
					</div>
					<div class="form-group">
						<label for="withdrawCnyAddrBtn" class="col-xs-3 control-label"></label>
						<div class="col-xs-6">
							<button id="addAlipayAddrBtn" class="btn btn-danger btn-block">确定提交</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	




 <%@include file="../comm/footer.jsp" %>	

	<script type="text/javascript" src="/static/front/js/comm/msg.js"></script>
	<script type="text/javascript" src="/static/front/js/finance/account.assets.js"></script>
</body>
</html>
