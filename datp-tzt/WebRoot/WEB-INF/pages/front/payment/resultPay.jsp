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
		.center{
			margin:0 auto;
			width:100%;
			text-align:center;
		}
	</style>
	<%@include file="../comm/header.jsp"%>
	<%@include file="../comm/left_menu.jsp"%>
	<div class="col-xs-10 padding-right-clear">
	
				<div
					class="col-xs-12 padding-right-clear padding-left-clear rightarea recharge">
					<div class="col-xs-12 rightarea-con">
						<ul class="nav nav-tabs rightarea-tabs">
							<li class="active"><a
								href="/account/rechargeCny.html?type=1">人民币充值</a></li>
						</ul>
						<div class="col-xs-12 padding-clear padding-top-30">
							<span class="recharge-type"> <a
								href="/account/rechargeCny.html?type=0"> <span
									class="type-icon bank"></span> <span class="type">银行卡转账</span>
									<span class="type-ok"></span>
							</a>
							</span> <span class="recharge-type active"> <a
								href="/account/rechargeOnline.html?type=2"> <span
									class="type-icon bank"></span> <span class="type">银联在线转账</span>
									<span class="type-ok"></span>
							</a>
							</span>
							
							<span class="recharge-type"> <a
								href="/account/weChatPay.html?type=0"> <span
									class="type-icon bank"></span> <span class="type">微信转账</span> <span
									class="type-ok"></span>
							</a>
							</span>
							<span class="recharge-type"> <a
							href="/account/AliPay.html?type=3"> <span
								class="type-icon bank"></span> <span class="type">支付宝转账</span> <span
								class="type-ok"></span>
						</a>
						</span>
						</div>
						<div class="col-xs-12 padding-clear padding-top-30">
							<div class="recharge-box clearfix padding-top-30">

								<div class="col-xs-12 padding-clear padding-top-30">
									<div class="col-xs-12 padding-clear form-horizontal">
								<!--开卡  -->
								<c:if test="${isCum==true}">
									<input type="hidden" id="iscum" value="${isCum}">
									<form id="pay_form" action="${requestFrontUrl}" method="post" target="_blank">
										<c:forEach items="${reqData}" var="li">
											<input name="${li.key}" value="${li.value}" hidden="hidden"/>
										</c:forEach>
									</form>
								</c:if>

								<c:if test="${isCum==true}">
																
									<div class="form-group ">
										<label for="txnAmt" class="col-xs-5 control-label"><h3>支付编号：</h3></label>
										<div class="col-xs-6">
											<span class="text-muted" style="margin-top:20px;"><h3>${orderIdR}</h3></span>
										</div>
									</div>
									<div class="form-group">
										<label for="txnAmt" class="col-xs-5 control-label"><h3>交易金额：</h3></label>
										<div class="col-xs-6">
											<span class="text-muted" style="margin-top:20px;"><h3>${realMoney}元</h3></span>
										</div>
									</div>
									<div class="form-group">
										<div class="col-xs-12">
											<span class="text-danger center"><h3>${data}</h1></span> <br>
										</div>
									</div>
									
								</c:if>
								
								<!--消费  -->
								
								<c:if test="${isCum==false}">
									<div class="form-group ">
										<label for="txnAmt" class="col-xs-5 control-label"><h3>订单号：</h3></label>
										<div class="col-xs-6">
											<span class="text-muted"><h3>${orderIdR}</h3></span>
										</div>
									</div>
									<div class="form-group">
										<div for="txnAmt" class="col-xs-5 control-label"><h3>交易金额：</h3></div> 
										<div class="col-xs-6">
											<span class="text-muted"><h3>${realMoney}元</h3></span>
										</div>
									</div>
									<c:if test="${flag==false}">
									<div class="form-group">
										<div class="col-xs-12">
											<span class="text-danger center"><h3>${data}</h3></span> <br>
										</div>
									</div>
									</c:if>
									<c:if test="${flag==true}">
									<div class="form-group">
										<div class="col-xs-12">
											<span class="text-success center"><h3>${data}</h3></span> <br>
										</div>
									</div>
									</c:if>
								</c:if>
								


								<div class="form-group">
									<label for="withdrawCnyButton" class="col-xs-4 control-label"></label>
									<div class="col-xs-4">
										<button id="continue"
											class="btn btn-danger btn-block">继续充值</button>
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
														<td>充值方式</td>
														<td>充值金额(￥)</td>
														<td>状态</td>
														<td>操作</td>
													</tr>
													<c:forEach items="${list}" var="v">
														<tr>
															<td class="gray">${v.fid }</td>
															<td><fmt:formatDate value="${v.fcreateTime }"
																	pattern="yyyy-MM-dd HH:mm:ss" /></td>
															<td>${v.fremittanceType }</td>
															<td>${v.famount }</td>
															<td>${v.fstatus_s }</td>
															<td><c:if test="${v.fstatus==1 }">
																	<%-- <a class="completioninfo" href="javascript:void(0);" data-fid="${v.fid }">补全信息</a>
														&nbsp;|&nbsp; --%>
																	<a class="rechargecancel" href="javascript:void(0);"
																		data-fid="${v.fid }">取消</a>
																</c:if>
																<c:if test="${v.fstatus==2 }">
																<%-- 	<a class="completioninfo" href="javascript:void(0);" data-fid="${v.fid }">补全信息</a>
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


		
	
	
	
	
		
		
	<script type="text/javascript">
	    
	 	$(document).ready(function() {  
	       	 var iscum = document.getElementById("iscum").value;
		     if(iscum){
       					$('#pay_form').submit();
		   		  }
	}); 
	 	$("#continue").on("click",function(){
	 		window.location.href="/account/rechargeOnline.html";
	 	});
	 	
	    </script>  
	</script>	
	
	<script type="text/javascript"
			src="/static/front/js/finance/account.recharge3.js"></script>
</body>
</html>