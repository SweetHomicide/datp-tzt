<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<%@include file="include.inc.jsp"%>
<%@include file="link.inc.jsp"%>
<link rel="stylesheet" href="/static/front/css/index/index.css" type="text/css"></link>
    <!-- 帮助 -->
	<div class="function">
		<ul class="help_list">
			<li>
				<a href="/question/question.html" target="_blank">
					<span style="width: 0px;">在线问答</span>
					<i class="help_fixed"></i>
				</a>
			</li>
			<li class="online_Service" action="udesk">
			<!-- http://wpa.b.qq.com/cgi/wpa.php?ln=1&key=XzkzODA3MTQ2NV80Mjc5ODBfNDAwOTAwOTk4OV8yXw -->
			
				<a target="_blank" href="http://wpa.qq.com/msgrd?v=3&uin=${requestScope.constant['serviceQQ']}&menu=yes">
					<span style="width: 0px;">在线客服</span>
					<i class="help_udesk"></i>
				</a>
				<%-- <a target="_blank" href="http://wpa.qq.com/msgrd?v=3&uin=${requestScope.constant['serviceQQ'] }&menu=yes">
					<span style="width: 0px;">在线客服</span>
					<i class="help_udesk"></i>
				</a> --%>
			</li>
			<li>
				<span style="width: 0px;cursor:text">${requestScope.constant['telephone'] }</span>
				<i class="tel_fixed"></i>
			</li>
		</ul>
	</div>
	
	<!-- 底部辅助  -->
	<div class="bottom_assist">
		<ul class="totop_list">
			<li>
				<a href="javascript:window.scrollTo(0,0);" clstag="thirdtype|keycount|cebianlan_thirdtype|top">
					<span style="width: 0px;">顶部</span>
					<i class="totop_fixed"></i>
				</a>
			</li>
		</ul>
	</div>
<div id="allFooter" class="container-full footer" style="position: static;">
	<div class="container footer-top">
		<div class="row  text-center">
			<div class="col-xs-2" style="margin-top:40px;">
				<image src="../static/front/images/client/1.png" />
				<p> Android下载</p>
			</div>
			<div class="col-xs-8">
			<div class="footer-left-top">
				<a target="_blank" href="/about/index.html?id=2">关于我们</a>
				<span class="plist">|</span>
				<a target="_blank" href="/about/index.html?id=1">使用帮助</a>
				<span class="plist">|</span>
				<a target="_blank" href="/about/index.html?id=52">资产发行</a>
				<span class="plist">|</span>
				<a target="_blank" href="/about/index.html?id=59">下载中心</a>
				<span class="plist">|</span>
				<a target="_blank" href="/about/index.html?id=53">交易总则</a>
				<span class="plist">|</span>
				<a target="_blank" href="/about/index.html?id=3">费率说明</a>
				<span class="plist">|</span>
				<a target="_blank" href="/about/index.html?id=4">用户协议</a>
				<span class="plist">|</span>
				<a target="_blank" href="/about/index.html?id=5">法律声明</a>
				<span class="plist">|</span>
				<a target="_blank" href="/about/index.html?id=65">更多QQ群</a>
			</div>
			
			<div class="footer-left-con clearfix">
				<c:forEach items="${requestScope.constant['quns'] }" var="v" begin="0" end="2">
				<span class="plist"></span>
				<span>${v.fname }:${v.furl }</span>
				</c:forEach>
			</div>
			
			<div class="footer-left-bot">
				<div style="margin-bottom: 10px;">
					<span>${requestScope.constant['webinfo'].fcopyRights }</span> <span class="plist">|</span>
					 <span> ${requestScope.constant['webinfo'].fbeianInfo }
					</span> <span class="plist">|</span> <span class="" style="display: inline-block; text-decoration: none; height: 20px; line-height: 20px;">
					业务合作： ${requestScope.constant['webinfo'].fsystemMail }
					</span>
				</div>
			</div>
			</div>
			<div class="col-xs-2" style="margin-top:40px;">
				<image src="../static/front/images/client/2.png" />
				<p> ios下载</p>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/static/front/js/footer/footer.js"></script>
<script type="text/javascript" src="/static/front/js/plugin/bootstrap.js"></script>
<script type="text/javascript" src="/static/front/js/comm/util.js"></script>
<script type="text/javascript" src="/static/front/js/comm/comm.js"></script>
<script type="text/javascript" src="/static/front/js/language/language_cn.js"></script>
<script type="text/javascript">
function weixin(){
	var url ="https://open.weixin.qq.com/connect/qrconnect?appid=${requestScope.constant['weixinAppID']}&redirect_uri=${requestScope.constant['fulldomain']}/link/wx/callback.html&response_type=code&scope=snsapi_login#wechat_redirect";
location.href=url;
}
</script>

