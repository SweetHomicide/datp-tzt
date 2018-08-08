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

<link rel="stylesheet" href="/static/front/css/user/user.css" type="text/css"></link>
</head>
<body>
	



<%@include file="../comm/header.jsp" %>




	<div class="container-full padding-top-30">
		<div class="container">
			

<%@include file="../comm/left_menu.jsp" %>


			<div class="col-xs-10 padding-right-clear">
				<div class="col-xs-12 padding-right-clear padding-left-clear rightarea user">
					<div class="col-xs-12 rightarea-con">
						<div class="baseinfo-top-icon">
							<h4> </h4>
							<div class="userbaseinfo-lvmax">
								<div class="userbaseinfo-lv">
									<p>
										VIP
										<span>${level }</span>
									</p>
									<%-- <font color="red">（${isActive }）</font> --%>
								</div>
								<p class="text-right userbaseinfo-logintime">上次登录时间：
								<fmt:formatDate value="${fuser.flastLoginTime }" pattern="yyyy-MM-dd HH:mm:ss"/>
								</p>
								<p class="text-right userbaseinfo-logintime">上次登录IP：${fuser.flastLoginIp }</p>
							</div>
						</div>
						<div class="col-xs-12 padding-clear padding-top-30">
							<div class="panel">
								
								<div class="panel-body padding-left-clear padding-right-clear">
								
								
									<table class="table table-user">
										
										<div class="row">
											<div class="col-xs-3 user-tr" style="margin-left:8px;">
												<i class="iconlist google"></i>
												UID
											</div>
											<div class="col-xs-7 user-tr">${fuser.fid }</div>
											<div class="col-xs-1 user-tr"></div>
										</div>
										
										<tr>
											<td class="col-xs-3 user-tr">
												<i class="iconlist email"></i>
												绑定邮箱
											</td>
												
													<td class="col-xs-1 user-tr ${isBindEmail == true?'text-danger':''}">
													<c:choose>
													<c:when test="${isBindEmail }">
													已绑定
													</c:when>
													<c:otherwise>
													未绑定
													</c:otherwise>
													</c:choose>
													
													</td>
													<td class="col-xs-7 user-tr">
													<c:if test="${isBindEmail == true }">
													您绑定的邮箱为${email}
													</c:if>
													</td>
												<%-- 	<td class="col-xs-1 user-tr">
													<c:if test="${isBindEmail == false }">
														<a class="text-primary text-link" href="#" data-toggle="modal" data-target="#bindemail">绑定>></a>
													</c:if>	
													</td> --%>
												
											
										</tr>
										<tr>
											<td class="col-xs-3 user-tr">
												<i class="iconlist phone"></i>
												绑定手机
											</td>
											
													<td class="col-xs-1 user-tr ${isBindTelephone == true?'text-danger':''}">
                                                    <c:choose>
													<c:when test="${isBindTelephone }">
													已绑定
													</c:when>
													<c:otherwise>
													未绑定
													</c:otherwise>
													</c:choose>
                                                    </td>
													<td class="col-xs-7 user-tr">
													<c:if test="${isBindTelephone == true }">
													您绑定的手机号码为${telNumber}
													</c:if>
													</td>
												<%-- 	<td class="col-xs-1 user-tr">
													<c:if test="${isBindTelephone == false }">
														<a class="text-primary text-link" href="#" data-toggle="modal" data-target="#bindphone">绑定>></a>
													</c:if>
													<c:if test="${isBindTelephone == true}">
													<a class="text-primary text-link" href="#" data-toggle="modal" data-target="#unbindphone">修改&gt;&gt;</a>
													</c:if>
													</td> --%>
												
											
										</tr>
										<tr>
											<td class="col-xs-3 user-tr">
												<i class="iconlist google"></i>
												绑定谷歌验证码
											</td>
											
												
												
													<td class="col-xs-1 user-tr ${isBindGoogle == true?'text-danger':''}">
                                                    <c:choose>
													<c:when test="${isBindGoogle }">
													已绑定
													</c:when>
													<c:otherwise>
													未绑定
													</c:otherwise>
													</c:choose>
                                                    </td>
													<td class="col-xs-7 user-tr">提现、修改密码及安全设置时需要输入谷歌验证码(增强安全性，可选项)</td>
												<%-- 	<td class="col-xs-1 user-tr">
													<c:choose>
													<c:when test="${isBindGoogle }">
													<a class="text-primary text-link" href="#" data-toggle="modal" data-target="#unbindgoogle">查看&gt;&gt;</a>
													</c:when>
													<c:otherwise>
													<a id="googleunbind" class="text-primary text-link" href="#" data-toggle="modal" data-target="#bindgoogle">绑定>></a>
													</c:otherwise>
													</c:choose>
													</td> --%>
												
											
										</tr>
										<tr>
											<td class="col-xs-3 user-tr">
												<i class="iconlist loginpass"></i>
												登录密码
											</td>
											<td class="col-xs-1 user-tr text-danger">已设置</td>
											<td class="col-xs-7 user-tr">登录${requestScope.constant['webName']}时使用</td>
											<!-- <td class="col-xs-1 user-tr">
												<a class="text-primary text-link" href="#" data-toggle="modal" data-target="#unbindloginpass">修改>></a>
											</td> -->
										</tr>
										<tr id="traingtr">
											<td class="col-xs-3 user-tr">
												<i class="iconlist tradepass"></i>
												交易密码
											</td>
													<td class="col-xs-1 user-tr ${isTradePassword == true?'text-danger':''}">
                                                   <c:choose>
													<c:when test="${isTradePassword }">
													已设置
													</c:when>
													<c:otherwise>
													未设置
													</c:otherwise>
													</c:choose>
                                                   </td>
													<td class="col-xs-7 user-tr">账户资金变动时，需先验证交易密码</td>
													<%-- <td class="col-xs-1 user-tr">
													<c:if test="${isTradePassword == false }">
														<a class="text-primary text-link" href="#" data-toggle="modal" data-target="#bindtradepass">设置>></a>
													</c:if>
													<c:if test="${isTradePassword == true }">
													<a class="text-primary text-link" href="#" data-toggle="modal" data-target="#unbindtradepass">修改&gt;&gt;</a>
													</c:if>
													</td> --%>
												
											
										</tr>
									</table>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	
			
	<!-- 修改绑定手机 -->
	<c:if test="${isBindTelephone ==true}">
			<div class="modal modal-custom fade" id="unbindphone" tabindex="-1" role="dialog">
				<div class="modal-dialog" role="document">
					<div class="modal-mark"></div>
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close btn-modal" data-dismiss="modal" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<span class="modal-title">修改绑定手机</span>
						</div>
						<div class="modal-body form-horizontal">
							<div class="form-group text-center">
								<span class="modal-info-tips">
									您正在为账户
									<span class="text-danger">${loginName}</span>
									修改绑定手机
								</span>
							</div>
							<div class="form-group ">
								<label for="unbindphone-phone" class="col-xs-3 control-label">原手机号码</label>
								<div class="col-xs-6">
									<span id="unbindphone-phone" class="form-control border-fff" type="text">${telNumber}</span>
								</div>
							</div>
							<div class="form-group ">
								<label for="unbindphone-msgcode" class="col-xs-3 control-label">短信验证码</label>
								<div class="col-xs-6">
									<input id="unbindphone-msgcode" class="form-control" type="text">
									<button id="unbindphone-sendmessage" data-msgtype="3" data-tipsid="unbindphone-errortips" class="btn btn-sendmsg">发送验证码</button>
								</div>
							</div>
							<div class="form-group ">
<!-- 								<label for="unbindphone-areaCode" class="col-xs-3 control-label">所在地</label>
 -->								<div class="col-xs-6">
									<select class="form-control" id="unbindphone-areaCode" style="display:none;">
										<option value="+86">中国大陆(China)</option>
									</select>
								</div>
							</div>
							<div class="form-group ">
								<label for="unbindphone-newphone" class="col-xs-3 control-label">更换手机号码</label>
								<div class="col-xs-6">
									<span id="unbindphone-newphone-areacode" class="btn btn-areacode">+86</span>
									<input id="unbindphone-newphone" class="form-control padding-left-92" type="text">
								</div>
							</div>
							<c:if test="${isBindTelephone == true }">
							<div class="form-group ">
								<label for="unbindphone-newmsgcode" class="col-xs-3 control-label">短信验证码</label>
								<div class="col-xs-6">
									<input id="unbindphone-newmsgcode" class="form-control" type="text">
									<button id="unbindphone-newsendmessage" data-msgtype="2" data-tipsid="unbindphone-errortips" class="btn btn-sendmsg">发送验证码</button>
								</div>
							</div>
							</c:if>
							
							<c:if test="${isBindGoogle }">
								<div class="form-group">
									<label for="unbindphone-googlecode" class="col-xs-3 control-label">谷歌验证码</label>
									<div class="col-xs-6">
										<input id="unbindphone-googlecode" class="form-control" type="text">
									</div>
								</div>
							</c:if>
							
							<div class="form-group ">
								<label for="unbindphone-imgcode" class="col-xs-3 control-label">验证码</label>
								<div class="col-xs-6">
									<input id="unbindphone-imgcode" class="form-control" type="text">
									<img class="btn btn-imgcode" src="/servlet/ValidateImageServlet?r=<%=new java.util.Date().getTime() %>"></img>
								</div>
							</div>
							<div class="form-group">
								<label for="unbindphone-errortips" class="col-xs-3 control-label"></label>
								<div class="col-xs-6">
									<span id="unbindphone-errortips" class="text-danger"></span>
								</div>
							</div>
							<div class="form-group">
								<label for="unbindemail-Btn" class="col-xs-3 control-label"></label>
								<div class="col-xs-6">
									<button id="unbindphone-Btn" class="btn btn-danger btn-block">确定提交</button>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
	</c:if>	
	
	
		
		
			<!-- 谷歌查看 -->
			<c:if test="${isBindGoogle ==true}">
			<div class="modal modal-custom fade" id="unbindgoogle" tabindex="-1" role="dialog">
				<div class="modal-dialog" role="document">
					<div class="modal-mark"></div>
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close btn-modal" data-dismiss="modal" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<span class="modal-title">查看谷歌验证器</span>
						</div>
						<div class="modal-body form-horizontal">
							<div class="form-group unbindgoogle-hide-box" style="display:none">
								<div class="col-xs-12 clearfix">
									<div id="unbindgoogle-code" class="form-control border-fff ">
										<div class="form-qrcode" id="unqrcode"></div>
									</div>
								</div>
							</div>
							<div class="form-group unbindgoogle-hide-box" style="display:none">
								<label for="unbindgoogle-key" class="col-xs-3 control-label">密匙</label>
								<div class="col-xs-6">
									<span id="unbindgoogle-key" class="form-control border-fff"></span>
								</div>
							</div>
							<div class="form-group unbindgoogle-hide-box" style="display:none">
								<label for="unbindgoogle-device" class="col-xs-3 control-label">设备名称</label>
								<div class="col-xs-6">
									<span id="unbindgoogle-device" class="form-control border-fff">${device_name }</span>
								</div>
							</div>
							<div class="form-group unbindgoogle-show-box">
								<label for="unbindgoogle-topcode" class="col-xs-3 control-label">谷歌验证码</label>
								<div class="col-xs-6">
									<input id="unbindgoogle-topcode" class="form-control" type="text">
								</div>
							</div>
							<div class="form-group unbindgoogle-show-box">
								<label for="unbindgoogle-errortips" class="col-xs-3 control-label"></label>
								<div class="col-xs-6">
									<span id="unbindgoogle-errortips" class="text-danger"></span>
								</div>
							</div>
							<div class="form-group unbindgoogle-show-box">
								<label for="unbindgoogle-Btn" class="col-xs-3 control-label"></label>
								<div class="col-xs-6">
									<button id="unbindgoogle-Btn" class="btn btn-danger btn-block">确定提交</button>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			</c:if>
	
	
		<!-- 邮箱绑定 -->
		<div class="modal modal-custom fade" id="bindemail" tabindex="-1" role="dialog">
			<div class="modal-dialog" role="document">
				<div class="modal-mark"></div>
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close btn-modal" data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<span class="modal-title">绑定邮箱</span>
					</div>
					<div class="modal-body form-horizontal">
						<div class="form-group ">
							<label for="bindemail-email" class="col-xs-3 control-label">邮箱地址</label>
							<div class="col-xs-6">
								<input id="bindemail-email" class="form-control" type="text">
							</div>
						</div>
						<div class="form-group">
							<label for="bindemail-errortips" class="col-xs-3 control-label"></label>
							<div class="col-xs-6">
								<span id="bindemail-errortips" class="text-danger"></span>
							</div>
						</div>
						<div class="form-group">
							<label for="bindemail-Btn" class="col-xs-3 control-label"></label>
							<div class="col-xs-6">
								<button id="bindemail-Btn" class="btn btn-danger btn-block">确定提交</button>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	
	
		
			<!-- 绑定手机 -->
			<div class="modal modal-custom fade" id="bindphone" tabindex="-1" role="dialog">
				<div class="modal-dialog" role="document">
					<div class="modal-mark"></div>
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close btn-modal" data-dismiss="modal" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<span class="modal-title">绑定手机</span>
						</div>
						<div class="modal-body form-horizontal">
							<div class="form-group text-center">
								<span class="modal-info-tips">
									您正在为账户
									<span class="text-danger">${loginName}</span>
									绑定手机
								</span>
							</div>
							<div class="form-group ">
<!-- 								<label for="bindphone-areaCode" class="col-xs-3 control-label">所在地</label>
 -->								<div class="col-xs-6">
									<select class="form-control" id="bindphone-areaCode" style="display:none;">
										<option value="+86">中国大陆(China)</option>
									</select>
								</div>
							</div>
							<div class="form-group ">
								<label for="bindphone-newphone" class="col-xs-3 control-label">手机号码</label>
								<div class="col-xs-6">
									<span id="bindphone-newphone-areacode" class="btn btn-areacode">+86</span>
									<input id="bindphone-newphone" class="form-control padding-left-92" type="text">
								</div>
							</div>
							<div class="form-group ">
								<label for="bindphone-msgcode" class="col-xs-3 control-label">短信验证码</label>
								<div class="col-xs-6">
									<input id="bindphone-msgcode" class="form-control" type="text">
									<button id="bindphone-sendmessage" data-msgtype="2" data-tipsid="bindphone-errortips" class="btn btn-sendmsg">发送验证码</button>
								</div>
							</div>
							
							<c:if test="${isBindGoogle ==true}">
							<div class="form-group">
									<label for="bindphone-googlecode" class="col-xs-3 control-label">谷歌验证码</label>
									<div class="col-xs-6">
										<input id="bindphone-googlecode" class="form-control" type="text">
									</div>
								</div>
							</c:if>	
							
							<div class="form-group ">
								<label for="bindphone-imgcode" class="col-xs-3 control-label">验证码</label>
								<div class="col-xs-6">
									<input id="bindphone-imgcode" class="form-control" type="text">
									<img class="btn btn-imgcode" src="/servlet/ValidateImageServlet?r=<%=new java.util.Date().getTime() %>"></img>
								</div>
							</div>
							<div class="form-group">
								<label for="bindphone-errortips" class="col-xs-3 control-label"></label>
								<div class="col-xs-6">
									<span id="bindphone-errortips" class="text-danger"></span>
								</div>
							</div>
							<div class="form-group">
								<label for="bindemail-Btn" class="col-xs-3 control-label"></label>
								<div class="col-xs-6">
									<button id="bindphone-Btn" class="btn btn-danger btn-block">确定提交</button>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		
		
	
	
		
			<!-- 谷歌绑定 -->
			<div class="modal modal-custom fade" id="bindgoogle" tabindex="-1" role="dialog">
				<div class="modal-dialog" role="document">
					<div class="modal-mark"></div>
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close btn-modal" data-dismiss="modal" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<span class="modal-title">绑定谷歌验证器</span>
						</div>
						<div class="modal-body form-horizontal">
							<div class="form-group ">
								<div class="col-xs-12 clearfix">
									<div id="bindgoogle-code" class="form-control border-fff  form-qrcodebox">
										<div class="col-xs-12 clearfix form-qrcode-quotes form-qrcode-quotess"></div>
										<div class="form-qrcodebox-cld">
											<div class="form-qrcode-coded">
												<div class="form-qrcode-title text-center">
													<span>下载谷歌验证器</span>
												</div>
												<div class="form-qrcode">
													<img class="form-qrcode-img" src="${requestScope.constant['googleImages'] }"/>
												</div>
											</div>
											<div class="form-qrcode-tips">
												<span>
													若未安装谷歌验证器请
													<span class="text-danger">扫码下载</span>
													。
												</span>
											</div>
										</div>
										<div class="form-qrcodebox-cld">
											<div class="form-qrcode-codec">
												<div class="form-qrcode-title text-center">
													<span>绑定谷歌验证器</span>
												</div>
												<div class="form-qrcode" id="qrcode">
												</div>
											</div>
											<div class="form-qrcode-tips">
												<span>
													请扫码或手工输入密钥，将手机上生成的
													<span class="text-danger">动态验证码</span>
													填到下边输入框。
												</span>
											</div>
										</div>
										<div class="col-xs-12 clearfix form-qrcode-quotes form-qrcode-quotese"></div>
									</div>
								</div>
							</div>
							<div class="form-group ">
								<label for="bindgoogle-key" class="col-xs-3 control-label">密匙</label>
								<div class="col-xs-7">
									<span id="bindgoogle-key" class="form-control border-fff"></span>
									<input id="bindgoogle-key-hide" type="hidden">
								</div>
							</div>
							<div class="form-group ">
								<label for="bindgoogle-device" class="col-xs-3 control-label">设备名称</label>
								<div class="col-xs-7">
									<span id="bindgoogle-device" class="form-control border-fff">${device_name }</span>
								</div>
							</div>
							<div class="form-group ">
								<label for="bindgoogle-topcode" class="col-xs-3 control-label">谷歌验证码</label>
								<div class="col-xs-7">
									<input id="bindgoogle-topcode" class="form-control" type="text">
								</div>
							</div>
							<div class="form-group">
								<label for="bindgoogle-errortips" class="col-xs-3 control-label"></label>
								<div class="col-xs-7">
									<span id="bindgoogle-errortips" class="text-danger"></span>
								</div>
							</div>
							<div class="form-group">
								<label for="bindgoogle-Btn" class="col-xs-3 control-label"></label>
								<div class="col-xs-7">
									<button id="bindgoogle-Btn" class="btn btn-danger btn-block">确定提交</button>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		
		
	
	<!-- 登录密码修改 -->
	<div class="modal modal-custom fade" id="unbindloginpass" tabindex="-1" role="dialog">
		<div class="modal-dialog" role="document">
			<div class="modal-mark"></div>
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close btn-modal" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<span class="modal-title">修改登录密码</span>
				</div>
				<div class="modal-body form-horizontal">
					<div class="form-group ">
						<label for="unbindloginpass-oldpass" class="col-xs-3 control-label">旧登录密码</label>
						<div class="col-xs-6">
							<input id="unbindloginpass-oldpass" class="form-control" type="password">
						</div>
					</div>
					<div class="form-group ">
						<label for="unbindloginpass-newpass" class="col-xs-3 control-label">新登录密码</label>
						<div class="col-xs-6">
							<input id="unbindloginpass-newpass" class="form-control" type="password">
						</div>
					</div>
					<div class="form-group ">
						<label for="unbindloginpass-confirmpass" class="col-xs-3 control-label">确认新密码</label>
						<div class="col-xs-6">
							<input id="unbindloginpass-confirmpass" class="form-control" type="password">
						</div>
					</div>
				
				<c:if test="${isBindTelephone == true }">
					<div class="form-group">
						<label for="unbindloginpass-msgcode" class="col-xs-3 control-label">短信验证码</label>
						<div class="col-xs-6">
							<input id="unbindloginpass-msgcode" class="form-control" type="text">
							<button id="unbindloginpass-sendmessage" data-msgtype="6" data-tipsid="unbindloginpass-errortips" class="btn btn-sendmsg">发送验证码</button>
						</div>
					</div>
				</c:if>	
				
				<c:if test="${isBindGoogle }">
					<div class="form-group">
						<label for="unbindloginpass-googlecode" class="col-xs-3 control-label">谷歌验证码</label>
						<div class="col-xs-6">
							<input id="unbindloginpass-googlecode" class="form-control" type="text">
						</div>
					</div>
				</c:if>	
					
					<div class="form-group">
						<label for="unbindloginpass-errortips" class="col-xs-3 control-label"></label>
						<div class="col-xs-6">
							<span id="unbindloginpass-errortips" class="text-danger "></span>
						</div>
					</div>
					<div class="form-group">
						<label for="unbindloginpass-Btn" class="col-xs-3 control-label"></label>
						<div class="col-xs-6">
							<button id="unbindloginpass-Btn" class="btn btn-danger btn-block">确定提交</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
		
			<!-- 交易密码设置 -->
			<div class="modal modal-custom fade" id="bindtradepass" tabindex="-1" role="dialog">
				<div class="modal-dialog" role="document">
					<div class="modal-mark"></div>
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close btn-modal" data-dismiss="modal" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<span class="modal-title">设置交易密码</span>
						</div>
						<div class="modal-body form-horizontal">
							<div class="form-group ">
								<label for="bindtradepass-newpass" class="col-xs-3 control-label">新交易密码</label>
								<div class="col-xs-6">
									<input id="bindtradepass-newpass" class="form-control" type="password">
								</div>
							</div>
							<div class="form-group ">
								<label for="bindtradepass-confirmpass" class="col-xs-3 control-label">确认新密码</label>
								<div class="col-xs-6">
									<input id="bindtradepass-confirmpass" class="form-control" type="password">
								</div>
							</div>
							
							<c:if test="${isBindTelephone }">
							<div class="form-group">
									<label for="bindtradepass-msgcode" class="col-xs-3 control-label">短信验证码</label>
									<div class="col-xs-6">
										<input id="bindtradepass-msgcode" class="form-control" type="text">
										<button id="bindtradepass-sendmessage" data-msgtype="7" data-tipsid="bindtradepass-errortips" class="btn btn-sendmsg">发送验证码</button>
									</div>
								</div>
							</c:if>
							
							<c:if test="${isBindGoogle }">
							<div class="form-group">
									<label for="bindtradepass-googlecode" class="col-xs-3 control-label">谷歌验证码</label>
									<div class="col-xs-6">
										<input id="bindtradepass-googlecode" class="form-control" type="text">
									</div>
								</div>	
							</c:if>
							
							<div class="form-group">
								<label for="bindtradepass-errortips" class="col-xs-3 control-label"></label>
								<div class="col-xs-6">
									<span id="bindtradepass-errortips" class="text-danger"></span>
								</div>
							</div>
							<div class="form-group">
								<label for="bindtradepass-Btn" class="col-xs-3 control-label"></label>
								<div class="col-xs-6">
									<button id="bindtradepass-Btn" class="btn btn-danger btn-block">确定提交</button>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			
			
			<!-- 交易密码修改 -->
			<c:if test="${isTradePassword ==true}">
			<div class="modal modal-custom fade" id="unbindtradepass" tabindex="-1" role="dialog">
				<div class="modal-dialog" role="document">
					<div class="modal-mark"></div>
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close btn-modal" data-dismiss="modal" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<span class="modal-title">修改交易密码</span>
						</div>
						<div class="modal-body form-horizontal">
							<div class="form-group ">
								<label for="unbindtradepass-oldpass" class="col-xs-3 control-label">旧交易密码</label>
								<div class="col-xs-6">
									<input id="unbindtradepass-oldpass" class="form-control" type="password">
								</div>
							</div>
							<div class="form-group ">
								<label for="unbindtradepass-newpass" class="col-xs-3 control-label">新交易密码</label>
								<div class="col-xs-6">
									<input id="unbindtradepass-newpass" class="form-control" type="password">
								</div>
							</div>
							<div class="form-group ">
								<label for="unbindtradepass-confirmpass" class="col-xs-3 control-label">确认新密码</label>
								<div class="col-xs-6">
									<input id="unbindtradepass-confirmpass" class="form-control" type="password">
								</div>
							</div>
							
							<c:if test="${isBindTelephone }">
								<div class="form-group">
									<label for="unbindtradepass-msgcode" class="col-xs-3 control-label">短信验证码</label>
									<div class="col-xs-6">
										<input id="unbindtradepass-msgcode" class="form-control" type="text">
										<button id="unbindtradepass-sendmessage" data-msgtype="7" data-tipsid="unbindtradepass-errortips" class="btn btn-sendmsg">发送验证码</button>
									</div>
								</div>
							</c:if>
							
							<c:if test="${isBindGoogle }">
								<div class="form-group">
									<label for="unbindtradepass-googlecode" class="col-xs-3 control-label">谷歌验证码</label>
									<div class="col-xs-6">
										<input id="unbindtradepass-googlecode" class="form-control" type="text">
									</div>
								</div>
							</c:if>	
							
							<div class="form-group">
								<label for="unbindtradepass-errortips" class="col-xs-3 control-label"></label>
								<div class="col-xs-6">
									<span id="unbindtradepass-errortips" class="text-danger"></span>
								</div>
							</div>
							<div class="form-group">
								<label for="unbindtradepass-Btn" class="col-xs-3 control-label"></label>
								<div class="col-xs-6">
									<button id="unbindtradepass-Btn" class="btn btn-danger btn-block">确定提交</button>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</c:if>
	
	<input type="hidden" id="changePhoneCode" value="${fuser.ftelephone }" />
	<input type="hidden" id="isEmptyPhone" value="${isBindTelephone ==true?1:0 }" />
	<input type="hidden" id="isEmptygoogle" value="${isBindGoogle==true?1:0 }" />
	<input id="messageType" type="hidden" value="0" />

 

<%@include file="../comm/footer.jsp" %>	

	<script type="text/javascript" src="/static/front/js/comm/msg.js"></script>
	<script type="text/javascript" src="/static/front/js/plugin/jquery.qrcode.min.js"></script>
	<script type="text/javascript" src="/static/front/js/user/user.security.js"></script>
</body>
</html>
