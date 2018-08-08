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

<link rel="stylesheet" href="/static/front/css/finance/withdraw.css"
	type="text/css"></link>
<link rel="stylesheet" href="/static/front/css/finance/recharge.css"
	type="text/css"></link>
</head>
<body>
<%@include file="../comm/header.jsp"%>
<div class="container-full padding-top-30">
	<div class="container">
		<%@include file="../comm/left_menu.jsp"%>

		<div class="col-xs-10 padding-right-clear">
			<div
				class="col-xs-12 padding-right-clear padding-left-clear rightarea recharge">
				<div class="col-xs-12 rightarea-con">
					<ul class="nav nav-tabs rightarea-tabs">
						<li class="active"><a href="/account/rechargeCny.html?type=1">充值</a></li>
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
								class="type-icon bank"></span> <span class="type">银行卡转账</span> <span
								class="type-ok"></span>
						</a>
						</span> <span class="recharge-type"> <a
								href="javascript:void(0)" id="rechargeCnyUnionPay"> <span
								class="type-icon bank"></span> <span class="type">银联在线转账</span>
								<span
									class="type-ok"></span>
						</a>
						</span> <span class="recharge-type"> <a
								href="javascript:void(0)" id="rechargeweChatPay">  <span
								class="type-icon bank"></span> <span class="type">微信转账</span> <span
								class="type-ok"></span>
						</a>
						</span> <span class="recharge-type active"> <a
							href="javascript:void(0)" id="rechargeAliPay"><span
								class="type-icon bank"></span> <span class="type">支付宝转账</span> <span
								class="type-ok"></span>
						</a>
						</span>
					</div>
					<div class="col-xs-12 padding-clear padding-top-30">
						<div class="recharge-box clearfix padding-top-30">

							<div class="col-xs-12 padding-clear padding-top-30">
								<div class="col-xs-7">
									<form id="ailForm" role="form" class="form-horizontal"
										method="post" action="">

										<div class="form-group ">
												<label for="" class="col-xs-3 control-label">充值资产</label>
												<div class="col-xs-6">
													<select class="form-control" id="rechargeTypeAliPay">
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

										<input id="merId" class="form-control" type="hidden"
											name="merId" placeholder="" value="777290058140778"
											title="商户号" required="required" />


										<div class="form-group " id="divTxnAmt">
											<label for="txnAmt" class="col-xs-3 control-label">支付金额</label>
											<div class="col-xs-6">
												<input id="txnAmt" class="form-control" type="number" min="1"
													required="required" name="txnAmt" placeholder="支付金额，单位为（元）"
													value="" title="单位为元,不能带小数点 " required="required" onkeyup="checkleng(this)"/>
											</div>
										</div>


										<div class="form-group ">
											<label for="" class="col-xs-3 control-label"></label>
											<div class="col-xs-6">
												<button type="button" class="btn btn-danger btn-block"
													data-toggle="modal" id="btn_pay">确定充值</button>
												<button type="button" class="btn btn-danger btn-block"
													style="display: none" data-target="#withdrawQR" id="ShowQR"
													data-toggle="modal">继续充值</button>
											</div>
										</div>
									</form>


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
										<div class="panel-body"></div>
									</div>
								</div>
								<div class="col-xs-12 padding-clear padding-top-30">
									<div class="panel border">
										<div class="panel-heading">
											<span class="text-danger">人民币充值记录</span> <span
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
															</c:if> <c:if test="${v.fstatus != 1 }">
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


	<div class="modal modal-custom fade" id="withdrawQR" tabindex="-1"
		role="dialog" aria-labelledby="exampleModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-mark"></div>
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" id="clQR" class="close btn-modal"
						data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<span class="modal-title" id="exampleModalLabel">扫描二维码</span>
				</div>
				<div class="modal-body form-horizontal" style="text-align: center">
					<!-- 二维码 -->
					<div class="form-group " id="QRPay"></div>
					<div class="col-xs-8" id="divPay">
						<button type="button" class="btn btn-danger btn-block"
							style="display: none" data-toggle="modal" id="btn_pay_con">继续充值</button>
					</div>
				</div>
				<div class="form-group">
					<div style="height: 50px"></div>
				</div>
			</div>
		</div>
	</div>
	
	
	
	<%@include file="../comm/footer.jsp"%>


	<script type="text/javascript"
		src="/static/front/js/plugin/jquery.qrcode.min.js"></script>
	<script type="text/javascript"
		src="/static/front/js/finance/account.recharge.js"></script>

	</body>




	<script>
		var setT;//请求定时器
		var verValT;//验证定时器
		var borderFlag=false;
		$(function() {
			$("#btn_pay").click(
					function() {
						var rechargeType=$('#rechargeTypeAliPay option:selected').val();//选中的值
						var txAmt = $("#txnAmt").val();
						if (txAmt == "") {
							util.showerrortips("", "金额不能为空！");
							return;
						}
						if (txAmt == "0") {
							util.showerrortips("", "金额不能为0！");
							return;
						}

						$("#btn_pay").attr("disabled", "disabled");
						$("#txnAmt").attr("disabled", "disabled");
						var url = "/account/AliPayQR.html?random="
								+ Math.round(Math.random() * 100);
						$.post(url, {
							fviId:rechargeType,
							txnAmt : $("#txnAmt").val()
						}, function(data) {
							var dataurl = data.url;
							var tradeno = data.out_trade_no;
							$("#QRPay").qrcode({
								width : 200, //宽度 
								height : 200, //高度 
								text : dataurl
							//任意内容 
							});

							//$('#QRPay').css("padding-left", "163px");
							$('#divPay').css("padding-left", "185px");
							$("#ShowQR").click();
							//调用查询订单方法
							//使用定时器调度查询								
							setT = window.setInterval(function() {
								OrderQuery(tradeno)
							}, 3000);
							//如果有性能问题可加一个延时定时器来关闭请求定时器

						});
					});




		});

		//查询订单
		function OrderQuery(tradeno) {
			var url = "/account/AliPayQuery.html?random="
					+ Math.round(Math.random() * 100);			
					$.post(
							url,
							{
								out_trade_no : tradeno
							},
							function(data) {
								var retSuccess = data.success;
								if (retSuccess == '1') {
									var QRHeight = $("#QRPay").height();
									$("#btn_pay_con").css("display", "block");
									$('#QRPay').css("padding-left", "0px");
									var htmlSuccess = "<h1 style='text-align:center'>充值成功</h1>";
									$('#QRPay').html(htmlSuccess);
									$("#QRPay").height(QRHeight);
									//divTxnAmt
									//	$('#divTxnAmt').css("display", "none");

									window.clearInterval(setT);
								} else if (retSuccess == '2') {
								}
							});
		}
		$('#btn_pay_con').click(function() {
			location.href=location.href;
		});
		$('#clQR').click(function() {
			location.href=location.href;
		});
//验证数字
		$("#txnAmt").on("keypress", function(event) {
			return util.VerifyKeypress(this, event, 0);
		});
	</script>
</html>