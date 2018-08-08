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
<meta name="format-detection" content="telephone=no,email=no,address=no">
<%@include file="../comm/link.inc.jsp"%>

<link rel="stylesheet" href="/static/front/css/finance/withdraw.css"
	type="text/css"></link>
<link href="/static/front/css/trade/trade.css" rel="stylesheet"
	type="text/css" media="screen, projection" />
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

	<%@include file="../comm/header.jsp"%>

	<div class="container-full padding-top-30">
		<div class="container" style="background: #fff">
			<div style="display: none;">

				<%@include file="../comm/left_menu.jsp"%>
			</div>
			<div class="col-xs-12 padding-right-clear">
				<div
					class="col-xs-12 padding-right-clear padding-left-clear rightarea withdraw">
					<a class="btn glyphicon  glyphicon-chevron-left"
						href="/exchange/index.html?type=0">返回上一级</a>
					<div class="col-xs-12 rightarea-con">
						<div class="row extitle" style="border-bottom: 1px solid #eee;">
							<div class="col-xs-5" id="divxy3">
								<div class="all_coin_info1">
									<span class="lefticon col-xs-2 exicon"></span> <a
										href="javascript:void(0);" rel="drevil" title="请选择"
										class="cointype" id="cointype"> ${fsubscription.ftitle }</a> <span
										class="arrow-down" id="iconxy1"></span> <%-- <span
										class="lefticon col-xs-1"
										style="top:13px;width:30px;height:30px;background-image: url('${fsubscription.fvirtualcointypeCost.furl }') ;background-size:100%;"></span>
									<span class="lefticon col-xs-1"
										style="top: 13px; width: 50px; height: 30px; line-height: 30px;">-&gt</span>
									<span class="lefticon col-xs-1"
										style="margin-right:10px;top:13px;width:30px;height:30px;background-image: url('${fsubscription.fvirtualcointype.furl }') ;background-size:100%;"></span> --%>
								</div>


								<div id="div4xy"
									style="display: none; z-index: 1000; width: 720px; background: #fff; position: absolute; border: 1px solid #ccc; top: 65px; left: 0px;">
									<div class="row" style="padding-top: 15px;">
										<div class="col-xs-4 col-xs-offset-8" style="right: 10px">
											<div class="input-group form-group">
												<input type="text" class="form-control searchname"
													name="searchName" id="searchname" placeholder="输入兑换名称"
													onkeypress="if(event.keyCode==13) {searchType4(1);}" /> <span
													class="input-group-btn"> <input type="hidden"
													id="hidlog" />
													<button class="btn btn-primary" id="searchType"
														onclick="searchType4(1);">
														<i class="glyphicon glyphicon-search"></i>
													</button>
											</div>
										</div>
									</div>
									<hr>
									<div id="divxy2"></div>
									<div class="row" style="padding-bottom: 10px;">
										<div class="col-xs-12">
											<input type="hidden" value="${cur_page }" name="currentPage"
												id="currentPage"></input> <span
												style="float: right; margin-right: 10px;" id="pagex">
												${page} </span>
										</div>
									</div>
								</div>

							</div>


						</div>
						<div class="col-xs-12 padding-clear padding-top-30">
							<div class="col-xs-7 padding-clear form-horizontal" style="border:1px solid #eee;">

								<div class="form-group ">
									<label for="exAmount" class="col-xs-4 control-label">${coinName }账户余额</label>
									<div class="col-xs-6">
										<span class="form-control border-fff" id="exAmount"><fmt:formatNumber
												value="${coinAmt }" pattern="##.##" maxIntegerDigits="10"
												maxFractionDigits="4" /></span>
									</div>
								</div>

								<div class="form-group ">
									<label for="amount" class="col-xs-4 control-label">兑换数量</label>
									<div class="col-xs-6">
										<input id="amount" class="form-control" type="number" value=""
											onkeyup="checkleng(this);"> <span class="amounttips">
											<span> 需要支付<%-- ${coinName } --%>数量 <span id="free"
												class="text-danger">--</span>
										</span>
										</span>
									</div>
								</div>
								<div class="form-group ">
									<label for="tradePwd" class="col-xs-4 control-label">交易密码</label>
									<div class="col-xs-6">
										<input id="tradePwd" class="form-control" type="password">
									</div>
								</div>

								<div class="form-group">
									<label for="exerrortips" class="col-xs-4 control-label"></label>
									<div class="col-xs-6">
										<span id="exerrortips" class="text-danger"> </span>
									</div>
								</div>
								<div class="form-group">
									<label for="exButton" class="col-xs-4 control-label"></label>
									<div class="col-xs-6">
										<button id="exButton" class="btn btn-danger btn-block"
											onclick="javascript:submitExForm();">确认兑换</button>
									</div>
								</div>
							</div>
							<div class="col-xs-5" style="top:30px;">
								<div class="panel panel-tips">
									<div class="panel-header text-center text-danger">
										<span class="panel-title">兑换简介</span>
									</div>
									<div class="panel-body">
										<p style="color: red;">&lt ${fsubscription.ftitle }</p>
										<p style="color: red;">
											&lt 兑换时间：
											<fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss"
												value="${fsubscription.fbeginTime }" />
											~
											<fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss"
												value="${fsubscription.fendTime }" />
										</p>
										<p style="color: red;">&lt 换购价：${fsubscription.fprice }
											${fsubscription.fvirtualcointypeCost.fShortName}=1.0
											${fsubscription.fvirtualcointype.fShortName }</p>
										<p style="color: red;">
											&lt 可换购数量：
											<c:if
												test="${fsubscription.ftotal-fsubscription.fAlreadyByCount >0 }">
												<fmt:formatNumber
													value="${fsubscription.ftotal-fsubscription.fAlreadyByCount }"
													pattern="##.##" maxIntegerDigits="20" maxFractionDigits="4" />
											</c:if>
											<c:if
												test="${fsubscription.ftotal-fsubscription.fAlreadyByCount <=0 }">
												<fmt:formatNumber value="0" pattern="##.##"
													maxIntegerDigits="20" maxFractionDigits="4" />
											</c:if>
										</p>
										<p style="color: red;">
											<c:if test="${fsubscription.fbuyCount!=0 }">
												&lt 您剩余可兑换数量：${fsubscription.fvirtualcointype.fSymbol }${buyCount }
											</c:if>
										</p>
										<p style="color: red;">
											<c:if test="${fsubscription.fbuyTimes!=0 }">
												&lt 您剩余可换购次数：${buyTimes }
											</c:if>
										</p>
									</div>
								</div>
							</div>
							<div class="col-xs-12 padding-clear padding-top-30">

								<div class="col-xs-12 padding-clear padding-top-30">
									<div class="panel border">
										<div class="panel-heading">
											<span class="text-danger">我的兑换记录</span> <span
												class="pull-right recordtitle" data-type="0" data-value="0">收起
												-</span>
										</div>
										<div class="panel-body" id="recordbody0">
											<table class="table">
												<tr>
													<td>换购数量</td>
													<td>换购价格</td>
													<td>总价格</td>
													<td class="text-right">换购时间</td>
												</tr>

												<c:forEach items="${fsubscriptionlogs }" var="v"
													varStatus="vs">
													<tr>
														<td>${fsubscription.fvirtualcointype.fSymbol }<fmt:formatNumber
																value="${v.fcount }" pattern="##.##"
																maxIntegerDigits="10" maxFractionDigits="4" />
														</td>
														<td>${fsubscription.fvirtualcointypeCost.fSymbol }<fmt:formatNumber
																value="${v.fprice }" pattern="##.##"
																maxIntegerDigits="10" maxFractionDigits="4" />
														</td>
														<td>${fsubscription.fvirtualcointypeCost.fSymbol }<fmt:formatNumber
																value="${v.ftotalCost }" pattern="##.##"
																maxIntegerDigits="10" maxFractionDigits="4" />
														</td>
														<td class="text-right"><fmt:formatDate
																value="${v.fcreatetime }" pattern="yyyy-MM-dd HH:mm:ss" />
														</td>
													</tr>
												</c:forEach>

												<c:if test="${fn:length(fsubscriptionlogs) == 0 }">
													<tr>
														<td colspan="4" class="no-data-tips"><span>
																您暂时没有兑换记录。 </span></td>
													</tr>
												</c:if>
											</table>

											<%-- <div class="text-right">${pagin }</div> --%>
										</div>
										<span
												style="float: right; margin-right: 10px;" id="pagex">
												${page1} </span>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<input type="hidden" value="${fsubscription.fprice }" id="subRate" />
		<input type="hidden" value="${fsubscription.fid }" id="fid" /> <input
			type="hidden" value="${count}" id="count" />

		<%@include file="../comm/footer.jsp"%>
		<script type="text/javascript" src="/static/front/js/comm/msg.js"></script>
		<script type="text/javascript" src="/static/front/js/exchange/ex.js"></script>
		<script type="text/javascript" src="/static/front/js/trade/dongtai.js"></script>
</body>
</html>
