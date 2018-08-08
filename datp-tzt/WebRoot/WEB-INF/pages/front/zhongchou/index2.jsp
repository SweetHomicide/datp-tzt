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
<%@include file="../comm/link.inc.jsp"%>

<link rel="stylesheet" href="/static/front/css/zhongchou/zc.css"
	type="text/css"></link>
</head>
<body>



	<%@include file="../comm/header.jsp"%>




	<div class="container-full">
		<div class="container">

			<div class="autobox">


				<ul class="nav nav-pills nav-stacked ">
					<li
						style="font-size: 18px; float: left;"><a
						href="/crowd/index.html?type=0"> 未开始</a></li>
					<li style="border-bottom: 2px solid red;font-size: 18px; float: left;"><a
						href="/crowd/index.html?type=1"> 进行中</a></li>
					<li style="font-size: 18px; float: left;"><a
						href="/crowd/index.html?type=2"> 已结束 </a></li>
				</ul>
				<div class="row">
					<div class="col-xs-12">
						<c:if test="${fsubscriptions.size() ne 0}">
						
						
						<c:forEach items="${fsubscriptions }" var="v">
							<div class="crowd_con crowd_con_list clear">
								<div class="crowd_con_l crowd_con_list_l">
									<div class="crowd_bi clear">
										<span class="crowd_bi_hlb"> <img
											src="${v.fvirtualcointype.furl }" width="50px" height="50px" />
										</span>
										<h2>
											<a href="/crowd/view.html?fid=${v.fid }">${v.ftitle }</a>
										</h2>
										<div
											class="crowd_hui crowd_hui_${v.fstatus=='众筹中'?'green':'red' }">
											<em></em>${v.fstatus }
										</div>
										<div
											style="float: right;width: 60px;height: 30px;border-radius: 2px;background-color: red;margin-top: 14px;
							margin-right: 10px;line-height: 28px;padding: 2px 8px;color: #fff;text-align: center;display: ${v.fisICO==true?'block':'none' };">ICO</div>
									</div>
									<ul class="crowd_subscribe clear">
										<li style="list-style-type: none;">
											<p>众筹总量(${v.fvirtualcointype.fShortName })</p> <span
											class="red"> <fmt:formatNumber
													value="${v.ftotal*v.ftotalqty }" pattern="##.##"
													maxIntegerDigits="20" maxFractionDigits="0" /></span>
										</li>
										<li style="list-style-type: none;">
											<p>众筹总份数(${v.ftotalqty }${v.fvirtualcointype.fShortName }=1份)</p>
											<span class="red"> <fmt:formatNumber
													value="${v.ftotal }" pattern="##.##" maxIntegerDigits="20"
													maxFractionDigits="0" /></span>
										</li>
										<li style="list-style-type: none;">
											<p>最多众筹${v.fisICO==true?'金额':'份数' }</p> <span class="red">
												<c:choose>
													<c:when test="${v.fbuyCount ==0 }">
								不限制
								</c:when>
													<c:otherwise>
														<c:if test="${v.fisICO==true}">${v.symbol1}</c:if>
														<fmt:formatNumber value="${v.fbuyCount }" pattern="##.##"
															maxIntegerDigits="20" maxFractionDigits="0" />
													</c:otherwise>
												</c:choose>
										</span>
										</li>
										<li style="list-style-type: none;">
											<p>最少众筹${v.fisICO==true?'金额':'份数' }</p> <span class="red">
												<c:if test="${v.fisICO==true}">${v.symbol1}</c:if> <fmt:formatNumber
													value="${v.fminbuyCount }" pattern="##.##"
													maxIntegerDigits="20" maxFractionDigits="0" />
										</span>
										</li>
										<li style="list-style-type: none;">
											<p>最多众筹次数</p> <span class="red"> <c:choose>
													<c:when test="${v.fbuyTimes ==0 }">
								不限制
								</c:when>
													<c:otherwise>
														<fmt:formatNumber value="${v.fbuyTimes }" pattern="##.##"
															maxIntegerDigits="20" maxFractionDigits="0" />
													</c:otherwise>
												</c:choose>
										</span>
										</li>

										<li style="list-style-type: none;">
											<p>单价/份</p> <span class="red"> <c:if
													test="${!v.fisICO }">
								${v.symbol1 } <fmt:formatNumber value="${v.fprice }"
														pattern="##.##" maxIntegerDigits="20"
														maxFractionDigits="4" />
												</c:if> <c:if test="${v.fisICO }">
								以众筹结果为准
								</c:if>
										</span>
										</li>

									</ul>
								</div>

								<div class="crowd_con_list_r crowd_con_list_r">
									<p class="crowd_con_lp">
										<c:if test="${v.fisICO }">
						已众筹${v.symbol }:
						</c:if>
										<c:if test="${!v.fisICO }">
						已众筹份数:
						</c:if>
										<b class="green"> <fmt:formatNumber
												value="${v.fAlreadyByCount }" pattern="##.##"
												maxIntegerDigits="20" maxFractionDigits="0" /></b>
									</p>

									<p class="crowd_con_lp">
										<c:if test="${!v.fisICO }">
							中签份数：<b class="green"> <fmt:formatNumber value="${v.fqty }"
													pattern="##.##" maxIntegerDigits="20" maxFractionDigits="4" /></b>
										</c:if>
									</p>
									<p class="crowd_con_lp">
										<c:if test="${!v.fisICO }">
							已参与${v.symbol } ：<b class="green"><fmt:formatNumber
													value="${v.fprice*v.fAlreadyByCount }" pattern="##.##"
													maxIntegerDigits="20" maxFractionDigits="4" /></b>
										</c:if>
									</p>

									<div class="login_button">
										<a href="/crowd/view.html?fid=${v.fid }"
											class="login_button_zc ${v.fstatus=='众筹中'?'':'login_button_zc_js' }">
											${v.fstatus=='众筹中'?'立即众筹':v.fstatus } </a>
									</div>
								</div>
							</div>
						</c:forEach>
						</c:if>
						<c:if test="${fsubscriptions.size()==0}">
						<span style="margin-top:10px;line-height:100px;font-size:17px;">暂无数据</span>
						</c:if>
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
	</div>



	<%@include file="../comm/footer.jsp"%>


</body>
</html>