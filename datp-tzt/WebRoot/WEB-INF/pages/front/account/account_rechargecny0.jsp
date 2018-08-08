<%@page import="java.text.DecimalFormat"%>
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

<link rel="stylesheet" href="/static/front/css/finance/recharge.css" type="text/css"></link>
</head>
<body>
	




 
 <%@include file="../comm/header.jsp" %>

	<div class="container-full padding-top-30">
		<div class="container">
			
<%@include file="../comm/left_menu.jsp" %>

			<div class="col-xs-10 padding-right-clear">
				<div class="col-xs-12 padding-right-clear padding-left-clear rightarea recharge">
					<div class="col-xs-12 rightarea-con">
						<ul class="nav nav-tabs rightarea-tabs">
							<li class="active">
								<a href="/account/rechargeCny.html?type=1">充值</a>
							</li>
							<%-- <c:forEach items="${requestScope.constant['allWithdrawCoins'] }" var="v">
								<li class="${v.fid==symbol?'active':'' }">
									<a href="/account/rechargeBtc.html?symbol=${v.fid }">${v.fShortName } 充值</a>
								</li>
							</c:forEach> --%>
							
						</ul>
						<div class="col-xs-12 padding-clear padding-top-30">

							<span class="recharge-type active"> <a
								href="javascript:void(0)" id="rechargeCny"> <span
									class="type-icon bank"></span> <span class="type">银行卡转账</span>
									<span class="type-ok"></span>
							</a>
							</span> <span class="recharge-type"> <a
								href="javascript:void(0)" id="rechargeCnyUnionPay"> <span
									class="type-icon bank"></span> <span class="type">银联在线转账</span><span
									class="type-ok"></span>
							</a>
							</span> <span class="recharge-type"> <a
								href="javascript:void(0)" id="rechargeweChatPay"> <span
									class="type-icon bank"></span> <span class="type">微信转账</span> <span
									class="type-ok"></span>
							</a>
							</span>
										 <span class="recharge-type"> <a
							href="javascript:void(0)" id="rechargeAliPay"> <span
								class="type-icon bank"></span> <span class="type">支付宝转账</span> <span
								class="type-ok"></span>
						</a>
						</span>
						</div>
						<div class="col-xs-12 padding-clear padding-top-30">
							<div class="recharge-box clearfix padding-top-30">
								<span class="recharge-process">
									<span id="rechargeprocess1" class="col-xs-4 active">
										<span class="recharge-process-line"></span>
										<span class="recharge-process-icon">1</span>
										<span class="recharge-process-text">请填写汇款金额</span>
									</span>
									<span id="rechargeprocess2" class="col-xs-4">
										<span class="recharge-process-line"></span>
										<span class="recharge-process-icon">2</span>
										<span class="recharge-process-text">前往网银汇款</span>
									</span>
									<span id="rechargeprocess4" class="col-xs-4">
										<span class="recharge-process-line"></span>
										<span class="recharge-process-icon">3</span>
										<span class="recharge-process-text">完成汇款</span>
									</span>
								</span>
								<div class="col-xs-12 padding-clear padding-top-30">
									<div id="rechage1" class="col-xs-7 padding-clear form-horizontal">
										<div class="form-group ">
												<label for="" class="col-xs-3 control-label">充值资产</label>
												<div class="col-xs-6">
													<select class="form-control" id="rechargeTypeCny">
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
										</div>
										<div class="form-group ">
											<label for="diyMoney" class="col-xs-3 control-label">支付金额</label>
											<div class="col-xs-6">
												<input id="diyMoney" class="form-control" type="text">
												<input type="hidden" value="0.00" id="random">
												<!--  <label for="diyMoney" class="control-label randomtips"></label>-->
											</div>
										</div>
										<div class="form-group">
											<label for="sbank" class="col-xs-3 control-label">选择银行</label>
											<div class="col-xs-6">
												<select id="sbank" class="form-control">
													<c:forEach items="${bankInfo }" var="v">
														<option value="${v.fid }">${v.fbankName }</option>
													</c:forEach>
												</select>
											</div>
										</div>
										<div class="form-group">
											<label for="rechargebtn" class="col-xs-3 control-label"></label>
											<div class="col-xs-6">
												<button id="rechargebtn" class="btn btn-danger btn-block">确定充值</button>
											</div>
										</div>
									</div>
									<div id="rechage2" class="col-xs-7 padding-clear form-horizontal" style="display:none;">
										<div class="form-group">
											<span class="col-xs-12 text-danger">1. 请登录您的银行网银，选择转账汇款，或去银行柜台办理转账汇款</span>
										</div>
										<div class="form-group">
											<span class="col-xs-12">2. 按照下面内容填写银行汇款信息表单：</span>
										</div>
										<div class="recharge-infobox">
											<div class="form-group">
												<span class="col-xs-5 control-label">收款人：</span>
												<span id="fownerName" class="col-xs-6 control-label text-left">xx</span>
											</div>
											<div class="form-group">
												<span class="col-xs-5 control-label">收款帐号：</span>
												<span id="fbankNumber" class="col-xs-6 control-label text-left">xx</span>
											</div>
											<div class="form-group">
												<span class="col-xs-5 control-label">收款帐号开户行：</span>
												<span id="fbankAddress" class="col-xs-6 control-label text-left">xx</span>
											</div>
											<div class="form-group">
												<span class="col-xs-5 control-label">付款金额：</span>
												<span id="bankMoney" class="col-xs-6 control-label text-danger text-left">xx</span>
											</div>
											<div class="form-group">
												<span class="col-xs-5 control-label">备注/附言/摘要：</span>
												<span id="bankInfo" class="col-xs-2  control-label  text-danger text-left">xx</span>
												<span class="col-xs-4 col-xs-offset-3  text-left text-danger rechage-tips">
													注意：转账时必须填写
													<span id="bankInfotips"></span>
												</span>
											</div>
										</div>
										<div class="form-group">
											<label for="diyMoney" class="col-xs-3 control-label"></label>
											<div class="col-xs-6">
												<button id="rechargenextbtn" class="btn btn-danger btn-block">我已完成充值，下一步</button>
											</div>
										</div>
									</div>
									<div id="rechage3" class="col-xs-7 padding-clear form-horizontal" style="display:none;">
										<div class="form-group ">
											<label for="fromBank" class="col-xs-3 control-label">您的汇出银行</label>
											<div class="col-xs-6">
												<select id="fromBank" class="form-control">
													<option value="-1">
														请选择银行类型
													</option>
													<c:forEach items="${bankTypes }" var="v">
														<option value="${v.key }">${v.value }</option>
													</c:forEach>
												</select>
											</div>
										</div>
										<div class="form-group ">
											<label for="fromAccount" class="col-xs-3 control-label">银行帐号</label>
											<div class="col-xs-6">
												<input id="fromAccount" class="form-control" type="text">
											</div>
										</div>
										<div class="form-group ">
											<label for="fromPayee" class="col-xs-3 control-label">开户名</label>
											<div class="col-xs-6">
												<input id="fromPayee" class="form-control" type="text">
											</div>
										</div>
										<div class="form-group ">
											<label for="fromPhone" class="col-xs-3 control-label">手机号码</label>
											<div class="col-xs-6">
												<input id="fromPhone" class="form-control" type="text" value="">
											</div>
										</div>
										<div class="form-group">
											<label for="rechargesuccessbtn" class="col-xs-3 control-label"></label>
											<div class="col-xs-6">
												<button id="rechargesuccessbtn" class="btn btn-danger btn-block">提交</button>
											</div>
										</div>
									</div>
									<div id="rechage4" class="col-xs-7 padding-clear form-horizontal" style="display:none;">
										<span class="rechare-success">
											<span class="success-icon">提交成功！</span>
											<a href="/account/rechargeCny.html?type=0">继续充值 >></a>
											<p>我们收到汇款后，会尽快处理，请耐心等待本站的人工处理。</p>
										</span>
									</div>
									<div class="col-xs-5 padding-clear text-center">
										<a target="_blank"  href="/about/index.html?id=44" class="recharge-help"> </a>
									</div>
								</div>
							</div>
							<div class="col-xs-12 padding-clear padding-top-30">
								<div class="panel panel-tips">
									<div class="panel-header text-center text-danger">
										<span class="panel-title">充值须知</span>
									</div>
									<div class="panel-body">
									    <p>&lt ${requestScope.constant['rechageBankDesc'] }</p>
										<p>&lt 工作时间平台确认汇款信息后30分钟内入账</p>
										<p>&lt 目前仅支持 ${minRecharge }元以上汇款, ${minRecharge }元以下汇款不予处理。</p>
									</div>
								</div>
							</div>
							<div class="col-xs-12 padding-clear padding-top-30">
								<div class="panel border">
									<div class="panel-heading">
										<span class="text-danger">充值记录</span>
										<span class="pull-right recordtitle" data-type="0" data-value="0">收起 -</span>
									</div>
									<div class="panel-body" id="recordbody0">
										<table class="table">
											<tr>
												<td>订单号</td>
												<td>充值时间</td>
												<td>到账金额</td>
												<td>支付金额(￥)</td>
												<td>状态</td>
												<td>操作</td>
											</tr>
											 <c:forEach items="${list}" var="v">
													<tr>
														<td class="gray">${v.fid }</td>
														<td><fmt:formatDate value="${v.fcreateTime }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
															<c:if test="${v.fviType != null }">
																<td>${v.fviType.fSymbol}<fmt:formatNumber value="${v.ftotalBTC}"  pattern="#.##" minFractionDigits="2"/></td>
															</c:if>
															<c:if test="${v.fviType== null }">
																<td>CNY<fmt:formatNumber value="${v.famount }"  pattern="#.##" minFractionDigits="2"/></td>
															</c:if>
														<td><fmt:formatNumber value="${v.famount }"  pattern="#.##" minFractionDigits="2"/></td>
														<td>${v.fstatus_s }</td>
														<td>
														<c:if test="${v.fstatus==1 }">
														<%-- <a class="completioninfo" href="javascript:void(0);" data-fid="${v.fid }">补全信息</a>
														&nbsp;|&nbsp; --%>
														<a class="rechargecontinue" href="javascript:void(0);" data-fid="${v.fid }">我已付款</a>
														<a class="rechargecancel" href="javascript:void(0);" data-fid="${v.fid }">取消</a>
														</c:if>
														<c:if test="${v.fstatus != 1 }">
														--
														</c:if>
														</td>
									                 </tr>
									          </c:forEach>
											  <c:if test="${fn:length(list)==0 }">
												<tr>
													<td colspan="6" class="no-data-tips" align="center">
														<span>
															您暂时没有充值数据
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
	<input type="hidden" value="${type }" name="finType" id="finType"></input>
	<input id="minRecharge" value="${minRecharge }" type="hidden">
	<input type="hidden" value="0" name="desc" id="desc"></input>


<%@include file="../comm/footer.jsp" %>	

	<script type="text/javascript" src="/static/front/js/finance/account.recharge.js"></script>
</body>
</html>
