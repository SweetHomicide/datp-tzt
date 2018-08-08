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

<link rel="stylesheet" href="/static/front/css/question/question.css" type="text/css"></link>
</head>
<body>
	

<%@include file="../comm/header.jsp" %>


	<div class="container-full padding-top-30 ">
		<div class="container question-maxbg">
			<!-- 左侧 -->
			<div class="col-xs-2 padding-left-clear">
				<div class="text-center question-left">
					<div class="question-online">
						<p id="question-pic">
							<span>
								意见反馈
							</span>
						</p>
					</div>
					<div class="question-menu"></div>
					<div class="question-img"></div>
					<div class="question-menu"></div>
				</div>
			</div>
			<!-- 右侧 -->
			<div class="col-xs-10 question-right rightarea ">
				<ul class="nav nav-tabs rightarea-tabs">
					<li class="">
						<a href="/question/question.html">
							问答
						</a>
					</li>
					<li class="active">
						<a href="/question/questionlist.html">
							列表
						</a>
					</li>
				</ul>
				<div class="col-xs-12 padding-clear">
					<table class="table table-striped">
						<tr>
							<td class="col-xs-1">
								问题编号
							</td>
							<td class="col-xs-2">
								提交时间
							</td>
							<td class="col-xs-2">
								问题类型
							</td>
							<td class="col-xs-2">
								问题描述
							</td>							
							<td class="col-xs-1">
								问题状态
							</td>
							<td class="col-xs-1">
								操作
							</td>
						</tr>
						
						<c:forEach items="${list }" var="v">
						<tr>
									<td>${v.fid }</td>
									<td>
										<fmt:formatDate value="${v.fcreateTime }" pattern="yyyy-MM-dd HH:mm:ss"/>
									</td>
									<td>${v.ftype_s }</td>
									<td>${v.fdesc }
									<c:if test="${v.fstatus==2 }">
									<br/>
									<font color="red">回复内容：${v.fanswer }</font>
									</c:if>
									</td>											
									<td>${v.fstatus_s }</td>
									<td>
										<a class="delete" href="javascript:void(0)" data-question="${v.fid }">
											删除
										</a>
										
									</td>
								</tr>
						</c:forEach>
						
						 <c:if test="${fn:length(list)==0 }">
							<tr class="no-data-tips text-center">
								<td colspan="6">
									<span>
										您暂时没有提问记录
									</span>
								</td>
							</tr>
						 </c:if>	
							
							<tr>
								<td class="text-right" colspan="12">
									<div class="text-right">
										${pagin }
									</div>
								</td>
							</tr>
						
					</table>
				</div>
			</div>
		</div>
	</div>


<%@include file="../comm/footer.jsp" %>	

	<script type="text/javascript" src="/static/front/js/question/question.js"></script>
</body>
</html>