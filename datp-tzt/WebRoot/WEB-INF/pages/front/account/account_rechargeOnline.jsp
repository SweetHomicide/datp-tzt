<%@ page language="java" import="java.util.*"
	import="java.text.SimpleDateFormat" pageEncoding="UTF-8"%>
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

<link rel="stylesheet" href="/static/front/css/finance/recharge.css"
	type="text/css"></link>
</head>
<body>
<style>
	.btnCenter{
		margin:0 auto;
	}
</style>
	<%@include file="../comm/header.jsp"%>

	<div class="container-full padding-top-30">
		<div class="container">
			<%@include file="../comm/left_menu.jsp"%>

			<div class="col-xs-10 padding-right-clear">
				<div
					class="col-xs-12 padding-right-clear padding-left-clear rightarea recharge">
					<div class="col-xs-12 rightarea-con">
						<ul class="nav nav-tabs rightarea-tabs">
							<li class="active"><a
								href="/account/rechargeCny.html?type=1">人民币充值</a></li>
							<%-- <c:forEach items="${requestScope.constant['allWithdrawCoins'] }"
								var="v">
								<li class="${v.fid==symbol?'active':'' }"><a
									href="/account/rechargeBtc.html?symbol=${v.fid }">${v.fShortName }
										充值</a></li>
							</c:forEach> --%>

						</ul>
						<div class="col-xs-12 padding-clear padding-top-30">
							<span class="recharge-type"> <a
								href="javascript:void(0)" id="rechargeCny"> <span
									class="type-icon bank"></span> <span class="type">银行卡转账</span>
									<span class="type-ok"></span>
							</a>
							</span> <span class="recharge-type active">  <a
								href="javascript:void(0)" id="rechargeCnyUnionPay"> <span
									class="type-icon bank"></span> <span class="type">银联在线转账</span>
									<span class="type-ok"></span>
							</a>
							</span>
							
							<span class="recharge-type"><a
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

								<div class="col-xs-12 padding-clear padding-top-30">
									<div class="col-xs-7">
										<div  class="form-horizontal" id="form1">
											
											<div class="form-group ">
												<label for="" class="col-xs-3 control-label">充值资产</label>
												<div class="col-xs-6">
													<select class="form-control" id="recharTypeUnion">
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
												<label for="txnAmt" class="col-xs-3 control-label">支付金额</label>
												<div class="col-xs-6">
													<input id="diyMoney"" class="form-control" type="number"
														name="txnAmt" placeholder="支付金额，单位为元" value="" maxlength="14" size="14"
														title="单位为元,不能带小数点 " required="required" min="1" onkeyup="checkleng(this)"/>
												<%-- <input type="hidden" value="0.${randomMoney }" id="random">
												<label for="txnAmt" class="control-label randomtips">.${randomMoney }</label> --%>
												<input type="hidden" value="0.00" id="random">
												</div>
											</div>
											
											
											<div class="form-group ">
												<label for="accNo" class="col-xs-3 control-label">付款方卡号</label>
												<div class="col-xs-6">
													<select class="form-control" id="accNo" required="required">													<!-- 需要后台获取传递过来 -->
														
														<c:forEach items="${fbankinfoWithdraws }" var="v">
																<option value="${v.fid }">${v.fname }&nbsp;&nbsp;尾号${v.fbankNumber }</option>
														</c:forEach>
													</select>
													
													
												</div>
												<div class="col-xs-3" style="line-height:30px">													
													<a href="#" class="text-primary addtips" data-toggle="modal" data-target="#withdrawCnyAddress">去新增>></a>
												</div>
											</div>
								
											<div class="form-group">
												<label for="addressPhoneCode1"
													class="col-xs-3 control-label">短信验证码</label>
												<div class="col-xs-8">
													<input id="phoneNum" name="phoneNum" class="form-control"
														type="number" required="required">
													<button id="bindsendmessage1" data-msgtype="10"
														data-tipsid="binderrortips" class="btn btn-sendmsg1"
														onclick="return false;">发送验证码</button>
												</div>
											</div>
											<div class="form-group">
												<label for="orderId" class="col-xs-3 control-label"></label>
												<div class="col-xs-6">
													<input id="orderId" class="form-control" type="hidden" 
														placeholder="支付编号" readonly="readonly"
														value="${orderId}"
														title="自行定义，8-32位数字字母 " required="required" />
												</div>
											</div>
											<div class="form-group ">
												<label for="" class="col-xs-3 control-label"></label>
												<div class="col-xs-6">
													<button id="btnRechargeOnline"  class="btn btn-danger form-control"
														>支付</button>
												</div>
											</div>
											
										</div>
										
																			
										
									</div>
									<div class="col-xs-5 padding-clear text-center">
										<a target="_blank" href="/about/index.html?id=44"
											class="recharge-help"> </a>
									</div>
									<div class="col-xs-12 padding-clear padding-top-30">
										<div class="panel panel-tips">
											<div class="panel-header text-center text-danger">
												<span class="panel-title">充值须知</span>
											</div>
											<div class="panel-body">
												<p>&lt 银联在线须知</p>
												<p>&lt 平台到账时间大约为5分钟内</p>
												
											</div>
										</div>
									</div>
									<div class="col-xs-12 padding-clear padding-top-30">
										<div class="panel border">
											<div class="panel-heading">
												<span class="text-danger">人民币银联充值记录</span> <span
													class="pull-right recordtitle" data-type="0" data-value="0">收起
													-</span>
											</div>
											<div class="panel-body" id="recordbody0">
												<table class="table">
													<tr>
														<td>订单号</td>
														<td>充值时间</td>
														<td>充值资产</td>
														<td>充值金额(￥)</td>
														<td>状态</td>
														<td>操作</td>
													</tr>
													<c:forEach items="${list}" var="v">
														<tr>
															<td class="gray">${v.fid }</td>
															<td><fmt:formatDate value="${v.fcreateTime }"
																	pattern="yyyy-MM-dd HH:mm:ss" /></td>
															<c:if test="${v.fviType != null }">
																<td>${v.fviType.fSymbol}${v.ftotalBTC}</td>
															</c:if>
															<c:if test="${v.fviType== null }">
																<td>--</td>
															</c:if>
															<td><fmt:formatNumber value="${v.famount }"  pattern="#.##" minFractionDigits="2"/></td>
															<td>${v.fstatus_s }</td>
															<td><c:if test="${v.fstatus==1 }">
																	<%-- <a class="completioninfo" href="javascript:void(0);" data-fid="${v.fid }">补全信息</a>
														&nbsp;|&nbsp; --%>
																	<a class="rechargecancel" href="javascript:void(0);"
																		data-fid="${v.fid }">取消</a>
																</c:if>
																<c:if test="${v.fstatus==2 }">
																	<%-- <a class="completioninfo" href="javascript:void(0);" data-fid="${v.fid }">补全信息</a>
														&nbsp;|&nbsp; --%>
																	<a class="reloadrecharge" href="javascript:void(0);"
																		data-fid="${v.fid }">更新状态</a>
																</c:if>
																
																 <c:if test="${v.fstatus == 3 }">
																
														--
														</c:if>
														<c:if test="${v.fstatus == 4 }">
																
														--
														</c:if>
														<c:if test="${v.fstatus == 5 }">
																
														--
														</c:if></td>
														</tr>
													</c:forEach>
													<c:if test="${fn:length(list)==0 }">
														<tr>
															<td colspan="6" class="no-data-tips" align="center">
																<span> 您暂时没有充值数据 </span>
															</td>
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
		</div>
		<input type="hidden" value="${type }" name="finType" id="finType"></input>
		<input type="hidden" value="0" name="desc" id="desc"></input>
				<button id="showModal1" class="btn btn-primary btn-lg" data-toggle="modal" data-target="#modal_result" style="display:none;">

</button>
		<div class="modal modal-custom fade" id="modal_result" tabindex="-1"
		role="dialog">
		<div class="modal-dialog" role="document">
			<div class="modal-mark"></div>
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" id="clQR" class="close btn-modal" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<span class="modal-title" id="exampleModalLabel">支付结果</span>
				</div>
				<div class="modal-body form-horizontal" style="text-align:center" >
					<div id="resultpage" class="form-horizontal">
											<div class="form-group ">
												<label for="txnAmt" class="col-xs-3 control-label">支付编号:</label>
												<div class="col-xs-6" style="margin-top:7px;">
													<span class="text-muted" style="margin-top: 20px;" id="res_orderIdR"></span>
												</div>
											</div>
											<div class="form-group">
												<label for="txnAmt" class="col-xs-3 control-label">交易金额:</label>
												<div class="col-xs-6" style="margin-top:7px;">
													<span class="text-muted" style="margin-top: 20px;" id="res_realMoney"></span>
												</div>
											</div>
											<div class="form-group">
												<label for="res_info" class="col-xs-3 control-label">支付结果:</label>
												<div class="col-xs-6" style="margin-top:7px;">
													<span class="text-danger" id="res_info"></span> <br>
												</div>
											</div>
											<div class="form-group">
												<label for="" class="col-xs-3 control-label"></label>
												<div class="col-xs-6">
													<button id="continue" class="btn btn-danger btn-block">继续充值</button>
												</div>
											</div>
											<div class="form-group" id="div-gotoPay">
												<label for="" class="col-xs-3 control-label"></label>
												<div class="col-xs-6">
													<span>如果无法自动跳转至开卡页面，</span>
													<a id="gotoPay" class="" href="javascript:void(0)">请点击这里</a>
												</div>
											</div>
											
											<form id="pay_form" action="" method="post" target="_blank" >
												<input id="bizType" name="bizType"  value=""  hidden=""/>
												<input id="txnSubType" name="txnSubType"  value=""  hidden=""/>
												<input id="orderId1" name="orderId"  value=""  hidden=""/>
												<input id="backUrl" name="backUrl" value="" hidden="hidden"/>
												<input id="signature" name="signature" value="" hidden="hidden"/>
												<input id="accNo1" name="accNo" value="" hidden="hidden"/>
												<input id="txnType" name="txnType" value="" hidden=""/>
												<input id="channelType" name="channelType" value="" hidden=""/>
												<input id="frontUrl" name="frontUrl" value="" hidden=""/>
												<input id="certId" name="certId" value="" hidden="hidden"/>
												<input id="encoding" name="encoding" value="" hidden="hidden"/>
												<input id="version" name="version" value="" hidden=""/>
												<input id="accessType" name="accessType" value="" hidden=""/>
												<input id="encryptCertId" name="encryptCertId" value="" hidden="hidden"/>
												<input id="txnTime" name="txnTime" value="" hidden="hidden"/>
												<input id="merId" name="merId" value="" hidden="hidden"/>
												<input id="accType" name="accType" value="" hidden=""/>
												<input id="currencyCode" name="currencyCode" value="" hidden=""/>
												<input id="signMethod" name="signMethod" value="" hidden="hidden"/>
												<input id="txnAmt" name="txnAmt" value="" hidden="hidden"/>
											</form>
											
										</div>	
				</div>
			
			</div>
		</div>
	</div>


		<!-- Modal -->
		<!-- <div class="modal fade bs-example-modal-sm" id="myModal" tabindex="-1"
			role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-sm">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal">
							<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
						</button>
						<h4 class="modal-title" id="myModalLabel">提示信息</h4>
					</div>
					<div class="modal-body">
						<span class="btnCenter" sytle="border:1px solid red;">
							<button id="btnSecu" type="button" class="btn btn-primary">支付成功</button>
							<button id="btnFail" type="button" class="btn btn-default">支付失败</button>
						</span>
						
					</div>
				</div>
			</div>
		</div> -->

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
	<!--  		<c:if test="${isBindTelephone == true }">	
						<div class="form-group">
							<label for="addressPhoneCode" class="col-xs-3 control-label">短信验证码</label>
							<div class="col-xs-8">
								<input id="addressPhoneCode" class="form-control" type="text">
								<button id="bindsendmessage" data-msgtype="10" data-tipsid="binderrortips" class="btn btn-sendmsg">发送验证码</button>
							</div>
						</div>
				</c:if>		-->	
				<c:if test="${isBindGoogle ==true}">		
						<div class="form-group">
							<label for="addressTotpCode" class="col-xs-3 control-label">谷歌验证码</label>
							<div class="col-xs-8">
								<input id="addressTotpCode" class="form-control" type="text">
							</div>
						</div>
				</c:if>		
					
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

		<%@include file="../comm/footer.jsp"%>
		<script type="text/javascript" src="/static/front/js/comm/msg.js"></script>
		
				<script type="text/javascript" src="/static/front/js/comm/msg3.js"></script>
		<script type="text/javascript"
			src="/static/front/js/plugin/jquery.qrcode.min.js"></script>
		<script src="/static/front/js/plugin/jquery.min.js"></script>
		<script type="text/javascript" src="/static/front/js/plugin/bootstrap.js"></script>
		<script type="text/javascript"
			src="/static/front/js/finance/city.min.js"></script>
		<script type="text/javascript"
			src="/static/front/js/finance/jquery.cityselect.js"></script>
		<script type="text/javascript"
			src="/static/front/js/finance/account.recharge.js"></script>
		<script type="text/javascript"
			src="/static/front/js/finance/account.recharge2.js"></script>
		<script type="text/javascript"
			src="/static/front/js/finance/account.withdraw.js"></script>
			
</body>
</html>
