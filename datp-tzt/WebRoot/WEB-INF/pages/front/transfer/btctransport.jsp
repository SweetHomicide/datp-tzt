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

<link rel="stylesheet" href="/static/front/css/finance/withdraw.css" type="text/css"></link>
<style type="text/css">
.col-xs-6 {
    width: 60%;
}
.withdraw .amounttips {
    width: 100%;
    height: 43px;
    line-height: 38px;
    font-size: 14px;
    border: #dcdcdc dotted 1px;
    display: block;
    margin-top: 5px;
}
</style>
</head>
<body>

  <%@include file="../comm/header.jsp" %>

	<div class="container-full padding-top-30">
		<div class="container">
			
<%@include file="../comm/left_menu.jsp" %>

			<div class="col-xs-10 padding-right-clear">
				<div class="col-xs-12 padding-right-clear padding-left-clear rightarea withdraw">
					<div class="col-xs-12 rightarea-con">
						<ul class="nav nav-tabs rightarea-tabs">
							<li class="active">
								<a href="/account/btcTransport.html">会员转账</a>
							</li>
						</ul>
						<div class="col-xs-12 padding-clear padding-top-30">
							<div class="col-xs-7 padding-clear form-horizontal">
							    <div class="form-group ">
									<label for="balanceAmount" class="col-xs-3 control-label">账户余额</label>
									<div class="col-xs-6">
										<span class="form-control border-fff" id="balanceAmount">0</span>
									</div>
								</div>
								<div class="form-group ">
									<label for="diyMoney" class="col-xs-3 control-label">数字资产类型</label>
									<div class="col-xs-6">
										<select id="transfertype" class="form-control">
											<option value="0">请选择转账类型</option>
											<c:forEach items="${fvirtualcointypes }" var="v">
												<option value="${v.fid }">
													${v.fname }(${v.fShortName })
												</option>
											</c:forEach>
										</select>
									</div>
								</div>
								<div class="form-group ">
									<label for="sendAddress" class="col-xs-3 control-label">会员UID</label>
									<div class="col-xs-6">
										<input id="sendAddress" class="form-control" type="text">
									</div>
								</div>
								<div class="form-group ">
									<label for="amount" class="col-xs-3 control-label">转账数量</label>
									<div class="col-xs-6">
										<input id="amount" class="form-control" type="text">
										<span class="amounttips">
											<span>
												手续费
												<span id="free" class="text-danger">--</span>
											</span>
										</span>
									</div>
								</div>
								<div class="form-group ">
									<label for="tradePwd" class="col-xs-3 control-label">交易密码</label>
									<div class="col-xs-6">
										<input id="tradePwd" class="form-control" type="password">
									</div>
								</div>
							
							<c:if test="${isBindTelephone == true }">	
								<div class="form-group">
									<label for="transferPhoneCode" class="col-xs-3 control-label">短信验证码</label>
									<div class="col-xs-6">
										<input id="transferPhoneCode" class="form-control" type="text">
										<button id="transfersendmessage" data-msgtype="14" data-tipsid="transfererrortips" class="btn btn-sendmsg">发送验证码</button>
									</div>
								</div>
							</c:if>	
							
							<c:if test="${isBindGoogle ==true}">
								<div class="form-group">
									<label for="transferTotpCode" class="col-xs-3 control-label">谷歌验证码</label>
									<div class="col-xs-6">
										<input id="transferTotpCode" class="form-control" type="text">
									</div>
								</div>
							</c:if>	
								
								<div class="form-group">
									<label for="transfererrortips" class="col-xs-3 control-label"></label>
									<div class="col-xs-6">
										<span id="transfererrortips" class="text-danger">
											
										</span>
									</div>
								</div>
								<div class="form-group">
									<label for="transferButton" class="col-xs-3 control-label"></label>
									<div class="col-xs-6">
										<button id="transferButton" class="btn btn-danger btn-block" onclick="javascript:submitTransferForm();">立即转账</button>
									</div>
								</div>
							</div>
							<div class="col-xs-12 padding-clear padding-top-30">
								<div class="panel panel-tips">
									<div class="panel-header text-center text-danger">
										<span class="panel-title">转账须知</span>
									</div>
									<div class="panel-body">
										<p>&lt ${requestScope.constant['transferDesc'] }</p>
									</div>
								</div>
							</div>
							<div class="col-xs-12 padding-clear padding-top-30">
								<div class="panel border">
									<div class="panel-heading">
										<span class="text-danger">我的转账记录</span>
										<span class="pull-right recordtitle" data-type="0" data-value="0">收起 -</span>
									</div>
									<div class="panel-body" id="recordbody0">
											<table class="table">
												<tr>
												    <th>数字资产类型</th>
												    <th>方向</th>
													<th>转出地址/转入UID</th>
													<th>转账数量</th>
													<th>手续费</th>
													<th>转账时间</th>
													<th>状态/操作</th>
												</tr>
												<c:if test="${fn:length(list) == 0 }">			
															<tr>
																<td colspan="7" class="no-data-tips">
																	<span>
																		无记录。
																	</span>
																</td>
															</tr>
														</c:if>
												<c:forEach items="${list }" var="v" varStatus="vs">
													<tr>
													     <td>${v.fvirtualcointype.fname }</td>
													    <c:if test="${v.fuser.fid == fuser.fid }">
													     <td>转出</td>
														<td>${v.faddress }</td>
													    </c:if>
													    <c:if test="${v.fuser.fid != fuser.fid }">
													    <td>转入</td>
														<td>${v.fuser.fid }</td>
													    </c:if>
													    <td><fmt:formatNumber
																	value="${v.famount }" pattern="##.##"
																	maxIntegerDigits="10" maxFractionDigits="6" /></td>
														<td><fmt:formatNumber
																	value="${v.ffees }" pattern="##.##"
																	maxIntegerDigits="10" maxFractionDigits="6" /></td>
														<td><fmt:formatDate value="${v.fcreatetime }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
													    <td>
													    ${v.fstatus_s }
													    <c:if test="${v.fstatus ==2 }">
													    &nbsp;&nbsp;|&nbsp;&nbsp;<a href="javascript:cancelTransport(${v.fid });">撤消</a>
													    </c:if>
													    </td>
													</tr>
												</c:forEach>
											</table>
										
											<div class="text-right">
												${pagin }
											</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	
<input type="hidden" value="" id="symbol" />
<input type="hidden" value="${requestScope.constant['transferRate'] }" id="transferRate"/>

<%@include file="../comm/footer.jsp" %>	
	<script type="text/javascript" src="/static/front/js/comm/msg.js"></script>
	<script type="text/javascript" src="/static/front/js/transfer/transfer.js"></script>
</body>
</html>
