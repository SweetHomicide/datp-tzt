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
							<li class="active">
								<a href="javascript:void(0)">银行卡管理</a>
							</li>
							<!-- <li class="">
								<a href="/financial/accountalipay.html">支付宝管理</a>
							</li> -->
							<li class="">
								<a href="/financial/accountcointype.html">虚拟地址管理</a>
							</li>
							<%-- <c:forEach items="${requestScope.constant['allWithdrawCoins'] }" var="v">
								<li class="${v.fid==symbol?'active':'' }">
									<a href="/financial/accountcoin.html?symbol=${v.fid }">${v.fShortName } 提现管理</a>
								</li>
							</c:forEach> --%>
						</ul>
						<div class="rows padding-clear padding-top-30">
							
							<c:forEach items="${bankinfos }" var="v">
								<div class="col-xs-4">
									<div class="bank-item item1">
										<div class="bank-item-top">
											<div class="col-xs-2">
											<i class="banklogo banklogo-item${v.fbankType }"></i>
											</div>
											<div class="col-xs-8">
												<p>${v.fname }</p>
												<p>${v.fbankNumber }</p>
											</div>
											<div class="col-xs-2">
												<span class="bank-item-del" data-fid="${v.fid }"></span>
											</div>
										</div>
										<div class="bank-item-bot">
											<div class="col-xs-12">
												<p>${v.fname },${v.fothers }</p>
												<p>${v.faddress }</p>
											</div>
										</div>
									</div>
								</div>
							</c:forEach>	
							
							<div class="col-xs-4">
								<div class="bank-add text-center" data-toggle="modal" data-target="#withdrawCnyAddress">
									<span class="icon"></span>
									<br>
									<span>添加银行卡</span>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="modal modal-custom fade" id="withdrawCnyAddress" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-mark"></div>
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close btn-modal" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<span class="modal-title" id="exampleModalLabel">添加银行卡</span>
				</div>
				<div class="modal-body form-horizontal">
					<div class="form-group ">
						<label for="payeeAddr" class="col-xs-3 control-label" >开户姓名</label>
						<div class="col-xs-8">
							<input id="payeeAddr" class="form-control" type="text" value="${fuser.frealName }" readonly="readonly"/>
							<span class="help-block text-danger">银行卡账号名必须与您的实名认证姓名一致</span>
						</div>
					</div>
					<div class="form-group ">
						<label for="withdrawAccountAddr" class="col-xs-3 control-label">银行卡号</label>
						<div class="col-xs-8">
							<input id="withdrawAccountAddr" class="form-control" type="" text>
						</div>
					</div>
					<div class="form-group ">
						<label for="withdrawAccountAddr2" class="col-xs-3 control-label">确认卡号</label>
						<div class="col-xs-8">
							<input id="withdrawAccountAddr2" class="form-control" type="" text>
						</div>
					</div>
					<div class="form-group ">
						<label for="openBankTypeAddr" class="col-xs-3 control-label">开户银行</label>
						<div class="col-xs-8">
							<select id="openBankTypeAddr" class="form-control">
								<option value="-1">
									请选择银行类型
								</option>
								<c:forEach items="${bankTypes }" var="v">
									<option value="${v.key }">${v.value }</option>
								</c:forEach>
							</select>
						</div>
					</div>
					<div id="prov_city" class="form-group ">
						<label for="prov" class="col-xs-3 control-label">开户地址</label>
						<div class="col-xs-8 ">
							<div class="col-xs-4 padding-right-clear padding-left-clear margin-bottom-15">
								<select id="prov" class="form-control">
								</select>
							</div>
							<div class="col-xs-4 padding-right-clear margin-bottom-15">
								<select id="city" class="form-control">
								</select>
							</div>
							<div class="col-xs-4 padding-right-clear margin-bottom-15">
								<select id="dist" class="form-control prov">
								</select>
							</div>
							<div class="col-xs-12 padding-right-clear padding-left-clear">
								<input id="address" class="form-control" type="text" placeholder="请输入您的详细地址"  maxlength="15" />
							</div>
						</div>
					</div>
					
		<!--  		<c:if test="${isBindTelephone == true }">
						<div class="form-group">
							<label for="addressPhoneCode" class="col-xs-3 control-label">短信验证码</label>
							<div class="col-xs-8">
								<input id="addressPhoneCode" class="form-control" type="text">
								<button id="bindsendmessage" data-msgtype="10" data-tipsid="binderrortips" class="btn btn-sendmsg">发送验证码</button>
							</div>
						</div>
					</c:if>	-->	
					
					<%-- <c:if test="${isBindGoogle ==true}">
						<div class="form-group">
							<label for="addressTotpCode" class="col-xs-3 control-label">谷歌验证码</label>
							<div class="col-xs-8">
								<input id="addressTotpCode" class="form-control" type="text">
							</div>
						</div>
					</c:if>	 --%>
					
					<div class="form-group">
						<label for="binderrortips" class="col-xs-3 control-label"></label>
						<div class="col-xs-8">
							<span id="binderrortips" class="text-danger"></span>
						</div>
					</div>
					<div class="form-group">
						<label for="withdrawCnyAddrBtn" class="col-xs-3 control-label"></label>
						<div class="col-xs-8">
							<button id="withdrawCnyAddrBtn" class="btn btn-danger btn-block">确定提交</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	



<%@include file="../comm/footer.jsp" %>	

	<script type="text/javascript" src="/static/front/js/comm/msg.js"></script>
	<script type="text/javascript" src="/static/front/js/finance/account.assets.js"></script>
	<script type="text/javascript" src="/static/front/js/finance/city.min.js"></script>
	<script type="text/javascript" src="/static/front/js/finance/jquery.cityselect.js"></script>
</body>
</html>
