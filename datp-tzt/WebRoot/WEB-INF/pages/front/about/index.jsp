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

<link rel="stylesheet" href="/static/front/css/user/about.css" type="text/css"></link>
</head>
<body>



<%@include file="../comm/header.jsp" %>

	<div class="container-full">
		<div class="container about">
			<div class="col-xs-12 padding-top-30">
				<div class="col-xs-2 padding-left-clear">
					<span class="title">帮助分类</span>
					<div class="panel-group" role="tablist">
						
						<c:forEach items="${abouts }" var="v" varStatus="vs">
							<div class="panel panel-default">
								<div class="panel-heading" role="tab" id="collapseListGroupHeading1">
									<h4 class="panel-title">
										<span id="icon${v.key}" class="item-icon">${fabout.ftype == v.name?'-':'+'}</span>
										<a class="collapsed" role="button" data-toggle="collapse" href="#collapse${v.key}" aria-expanded="${fabout.ftype == v.name?'true':'false'}"> ${v.name } </a>
									</h4>
								</div>
								<div id="collapse${v.key}" data-icon="icon${v.key}"
								 class="panel-collapse collapse ${fabout.ftype == v.name?'in':''}" role="tabpanel">
									<ul class="list-group">
										<c:forEach items="${v.value }" var="vv">
											<li class="list-group-item ${fabout.fid==vv.fid?'active':'' }">
												<span class="item-icon">&lt</span>
												<a href="/about/index.html?id=${vv.fid }">${vv.ftitle }</a>
											</li>
										</c:forEach>
									</ul>
								</div>
							</div>
						</c:forEach>
					</div>
				</div>
				<div class="col-xs-10 padding-bottom-50">
					<div class="col-xs-12 bg-white split">
						<h3>${fabout.ftitle}</h3>
					</div>
					<div class="col-xs-12 bg-white">
					${fabout.fcontent}
					</div>
				</div>
			</div>
		</div>
	</div>
	
	
 
<%@include file="../comm/footer.jsp" %>	

	<script type="text/javascript">
		$(function() {
			$('.collapse').on('hide.bs.collapse', function(ele) {
				$("#"+$(ele.currentTarget).data().icon).html("+");
				return true;
			})
			$('.collapse').on('show.bs.collapse', function(ele) {
				$("#"+$(ele.currentTarget).data().icon).html("-");
				return true;
			})
		})
	</script>
</body>
</html>
