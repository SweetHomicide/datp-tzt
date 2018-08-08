<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="../comm/include.inc.jsp"%>

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
			<td><fmt:formatDate value="${v.fcreateTime }"
					pattern="yyyy-MM-dd HH:mm:ss" /></td>
			<c:if test="${v.fviType != null }">
				<td>${v.fviType.fSymbol}<fmt:formatNumber
						value="${v.ftotalBTC}" pattern="#.##" minFractionDigits="2" /></td>
			</c:if>
			<c:if test="${v.fviType== null }">
				<td>CNY<fmt:formatNumber value="${v.famount }" pattern="#.##"
						minFractionDigits="2" /></td>
			</c:if>
			<td><fmt:formatNumber value="${v.famount }" pattern="#.##"
					minFractionDigits="2" /></td>
			<td>${v.fstatus_s }</td>
			<td><c:if test="${v.fstatus==1 }">
					<%-- <a class="completioninfo" href="javascript:void(0);" data-fid="${v.fid }">补全信息</a>
														&nbsp;|&nbsp; --%>
					<a class="rechargecontinue" href="javascript:void(0);"
						data-fid="${v.fid }">我已付款</a>
					<a class="rechargecancel" href="javascript:void(0);"
						data-fid="${v.fid }">取消</a>
				</c:if> <c:if test="${v.fstatus != 1 }">
														--
														</c:if></td>
		</tr>
	</c:forEach>
	<c:if test="${fn:length(list)==0 }">
		<tr>
			<td colspan="6" class="no-data-tips" align="center"><span>
					您暂时没有充值数据 </span></td>
		</tr>
	</c:if>
</table>

<input type="hidden" value="${cur_page }" name="currentPage"
	id="currentPage"></input>
<div class="text-right">${pagin }</div>