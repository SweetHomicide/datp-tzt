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
<link rel="stylesheet" href="/static/front/css/finance/recharge.css"
	type="text/css"></link>
</head>
<body>
	<input type="hidden" id="max_double" value="${requestScope.constant['maxwithdrawcny'] }">
	<input type="hidden" id="min_double" value="${requestScope.constant['minwithdrawcny'] }">
	


  <%@include file="../comm/header.jsp" %>

	<div class="container-full padding-top-30">
		<div class="container">
			
<%@include file="../comm/left_menu.jsp" %>

			<div class="col-xs-10 padding-right-clear">
				<div class="col-xs-12 padding-right-clear padding-left-clear rightarea withdraw recharge">
					<div class="col-xs-12 rightarea-con">
						<ul class="nav nav-tabs rightarea-tabs">
							<li class="active">
								<a href="/account/withdrawCny.html">人民币提现</a>
							</li>
							<%-- <c:forEach items="${requestScope.constant['allWithdrawCoins'] }" var="v">
								<li class="${v.fid==symbol?'active':'' }">
									<a href="/account/withdrawBtc.html?symbol=${v.fid }">${v.fShortName } 提现</a>
								</li>
							</c:forEach> --%>
							
						</ul>
						<div class="col-xs-12 padding-clear padding-top-30">
							<span class="recharge-type active"> <a
								href="/account/withdrawCny.html"> <span
									class="type-icon bank"></span> <span class="type">银行卡提现</span>
									<span class="type-ok"></span>
							</a>
							<!-- </span> <span class="recharge-type"> <a
								href="/account/withdrawOnline.html?type=2"> <span
									class="type-icon bank"></span> <span class="type">银联在线提现</span>
									<span class="type-ok"></span>
							</a>
							</span> -->
						</div>
						<div class="col-xs-12 padding-clear padding-top-30">
							<div class="col-xs-9 padding-clear form-horizontal">
								<div class="form-group ">
												<label for="" class="col-xs-3 control-label">提现资产</label>
												<div class="col-xs-6">
													<select class="form-control" id="withdrawType">
														<option value="">
															人民币
															</option>
															<c:forEach var="type" items="${listRechargeType}">
															 	<c:choose>
																 		<c:when test="${selectType==type.fvirtualcointype.fid}">
																			<option value="${type.fvirtualcointype.fid}" selected="selected">
																				${type.fvirtualcointype.fname}
																			</option>
																 		</c:when>
																 		<c:otherwise>
																			<option value="${type.fvirtualcointype.fid}">
																				${type.fvirtualcointype.fname}
																			</option>
																 		</c:otherwise>
															 		
															 	</c:choose>
															</c:forEach>
													</select>
												</div>
												
												<%-- <select class="form-control typeselect" id="recordType">
									<c:forEach items="${filters1 }" var="v">
									 	<c:choose>
									 		<c:when test="${select==v.value}">
												<option value="${v.key }" selected="selected">${v.value }</option>
									 		</c:when>
									 		<c:otherwise>
												<option value="${v.key }">${v.value }</option>
									 		</c:otherwise>
									 	</c:choose>
									</c:forEach>
								</select> --%>
										</div>
							    <div class="form-group ">
									<label for="withdrawAmount" class="col-xs-3 control-label">账户余额</label>
									<div class="col-xs-6">
										<c:if test="${fvirtualwallet == null }">		
									
										<span >￥</span><span id="spanftotalRmb"><fmt:formatNumber value="${requestScope.fwallet.ftotalRmb }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/></span>
										</c:if>	
										<c:if test="${fvirtualwallet != null }">		
									
										<span >${fvirtualwallet.fvirtualcointype.fSymbol}</span><span id="spanftotalRmb"> <fmt:formatNumber value="${fvirtualwallet.ftotal}" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/></span>
										</c:if>			
									</div>
								</div>
								<div class="form-group ">
									<label for="diyMoney" class="col-xs-3 control-label">提现银行卡</label>
									<div class="col-xs-6">
										<select id="withdrawBlank" class="form-control">
											 <c:forEach items="${fbankinfoWithdraws }" var="v">
													<option value="${v.fid }">${v.fname }&nbsp;&nbsp;尾号${v.fbankNumber }</option>
											</c:forEach>		
										</select>
										<a href="#" class="text-primary addtips" data-toggle="modal" data-target="#withdrawCnyAddress">立即新增</a>
									</div>
								</div>
								<div class="form-group ">
									<c:if test="${fvirtualwallet == null }">		
									<label for="withdrawBalance" class="col-xs-3 control-label">提现金额</label>
									</c:if>	
									<c:if test="${fvirtualwallet != null }">		
									<label for="withdrawBalance" class="col-xs-3 control-label">提现数量</label>
									</c:if>			
									<div class="col-xs-6">
										<input id="withdrawBalance" class="form-control" type="text" onBlur="vali()">
										<a href="javascript:void(0)" class="text-primary addtips" id="btnAll">提取全部</a>
										
										<span class="amounttips">
											<span>
												手续费&nbsp;&nbsp;&nbsp;
												<span id="free">0</span>
												<span>CNY</span>
												
											</span>
											<span>
												实际到账
												<span id="amount" class="text-danger">0</span>
												<span class="text-danger">CNY</span>
											</span>
											<c:if test="${fvirtualwallet != null }">	
													<span >兑换比例：<span class="text-danger">￥1.0=${fvirtualwallet.fvirtualcointype.fSymbol}${fsubscription.fprice}</span>
											</span>
											</c:if>	
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
										<label for="withdrawPhoneCode" class="col-xs-3 control-label">短信验证码</label>
										<div class="col-xs-6">
											<input id="withdrawPhoneCode" class="form-control" type="text">
											<button id="withdrawsendmessage" data-msgtype="4" data-tipsid="withdrawerrortips" class="btn btn-sendmsg">发送验证码</button>
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
									<label for="withdrawerrortips" class="col-xs-3 control-label"></label>
									<div class="col-xs-6">
										<span id="withdrawerrortips" class="text-danger">
											
										</span>
									</div>
								</div>
								<div class="form-group">
									<label for="withdrawCnyButton" class="col-xs-3 control-label"></label>
									<div class="col-xs-6">
										<button id="withdrawCnyButton" class="btn btn-danger btn-block">立即提现</button>
									</div>
								</div>
							</div>
							<div class="col-xs-12 padding-clear padding-top-30">
								<div class="panel panel-tips">
									<div class="panel-header text-center text-danger">
										<span class="panel-title">提现须知</span>
									</div>
									<div class="panel-body">
									    <p>&lt ${requestScope.constant['withdrawCNYDesc'] }</p>
										<p>&lt 最低提现金额为${requestScope.constant['minwithdrawcny'] }元。</p>
										<p>&lt 提现时会收取一定的手续费。</p>
										<p>&lt 正常24小时内到账，具体到账时间因收款银行略有不同，节假日到账时间略有延迟。</p>
										<p>&lt 为了您的帐户安全，每次人民币提现的最高限额为￥${requestScope.constant['maxwithdrawcny'] }，如果您有更高的需求，请与网站客服联系。</p>
										<p>&lt 为了您的帐户安全，每日最大提现次数为${MAX_Withdrawals_Time},如果您有更高的需求，请与网站客服联系。</p>
									</div>
								</div>
							</div>
							<div class="col-xs-12 padding-clear padding-top-30">
								<div class="panel border">
									<div class="panel-heading">
										<span class="text-danger">人民币提现记录</span>
										<span class="pull-right recordtitle" data-type="0" data-value="0">收起 -</span>
									</div>
									<div class="panel-body" id="recordbody0">
										<table class="table">
											<tr>
												<!-- <td>
													订单号
												</td> -->
												<td>
													提现时间
												</td>
												<td>
													提现资产
												</td>
												<td>
													提现金额
												</td>
												<td>
													手续费
												</td>
												<td>
													提现账户
												</td>
												
												<td>
													状态
												</td>
											</tr>
											
											<c:forEach items="${fcapitaloperations }" var="v" varStatus="vs">
														<tr>
															<%-- <td><font color="red">${v.fid }</font></td> --%>
															<td><fmt:formatDate value="${v.fcreateTime }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
															<c:if test="${v.fviType != null }">
																<td>${v.fviType.fSymbol}${v.ftotalBTC}</td>
															</c:if>
															<c:if test="${v.fviType== null }">
																<td>CNY<fmt:formatNumber value="${v.famount }"  pattern="#.##" minFractionDigits="2"/></td>
															</c:if>
															<td>￥<fmt:formatNumber value="${v.famount }"  pattern="#.##" minFractionDigits="2"/></td>
															<td>￥${v.ffees }</td>
															<td>开户行: ${v.fBank }<br>姓名: ${v.fPayee }<br>帐号:${v.fAccount }</td>
															
															<td style="color:#689700;"><span title="${v.fstatus==2?'平台已将款汇往您的账户，如暂时没有收到款项，是银行系统延时，请稍作等待。':'' }">
															${v.fstatus_s }
															<c:if test="${v.fstatus==1 }">
															&nbsp;|&nbsp;
															<a class="cancelWithdrawcny" href="javascript:void(0);" data-fid="${v.fid }">取消</a>
															</c:if>
															</span></td>
														</tr>
											</c:forEach>
											<c:if test="${fn:length(fcapitaloperations)==0 }">
													<tr>
														<td colspan="7" class="no-data-tips">
															<span>
																您暂时没有提现记录。
															</span>
														</td>
													</tr>
											</c:if>
										</table>
										
										<input type="hidden" value="${cur_page }" name="currentPage" id="currentPage"></input>
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
								<input id="address" class="form-control" type="text" placeholder="请输入您的详细地址" maxlength="15"/>
							</div>
						</div>
					</div>
					<!-- 2017-02-21 liuruichen 去掉短信验证与谷歌验证 -->
			<!-- 	<c:if test="${isBindTelephone == true }">	
						<div class="form-group">
							<label for="addressPhoneCode" class="col-xs-3 control-label">短信验证码</label>
							<div class="col-xs-8">
								<input id="addressPhoneCode" class="form-control" type="text">
								<button id="bindsendmessage" data-msgtype="10" data-tipsid="binderrortips" class="btn btn-sendmsg" onlick="">发送验证码</button>
							</div>
						</div>
				</c:if>		
					
				<c:if test="${isBindGoogle ==true}">		
						<div class="form-group">
							<label for="addressTotpCode" class="col-xs-3 control-label">谷歌验证码</label>
							<div class="col-xs-8">
								<input id="addressTotpCode" class="form-control" type="text">
							</div>
						</div>
				</c:if>		
					-->
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
	
	
	<input id="feesRate" type="hidden" value="${fee }">
	<input id="userBalance" type="hidden" value="${requestScope.fwallet.ftotalRmb }">
	<input id="exprice" type="hidden" value="${fsubscription.fprice}">


<%@include file="../comm/footer.jsp" %>	

	<script type="text/javascript" src="/static/front/js/comm/msg.js"></script>
    <script type="text/javascript" src="/static/front/js/finance/account.withdraw.js"></script>
	<script type="text/javascript" src="/static/front/js/finance/city.min.js"></script>
	<script type="text/javascript" src="/static/front/js/finance/jquery.cityselect.js"></script>
</body>
</html>
