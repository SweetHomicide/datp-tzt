<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="comm/include.inc.jsp"%>
<form id="pagerForm" method="post" action="ssadmin/walletList.html">
	<input type="hidden" name="status" value="${param.status}"> <input
		type="hidden" name="keywords" value="${keywords}" /> <input
		type="hidden" name="pageNum" value="${currentPage}" /> <input
		type="hidden" name="numPerPage" value="${numPerPage}" /> <input
		type="hidden" name="orderField" value="${param.orderField}" /><input
		type="hidden" name="orderDirection" value="${param.orderDirection}" />
</form>


<div class="pageHeader">
	<form onsubmit="return navTabSearch(this);"
		action="ssadmin/walletList.html" method="post">
		<div class="searchBar">

			<table class="searchContent">
				<tr>
					<td>会员信息：<input type="text" name="keywords" value="${keywords}"
						size="60" /></td>
				</tr>
			</table>
			<div class="subBar">
				<ul>
					<li><div class="buttonActive">
							<div class="buttonContent">
								<button type="submit">查询</button>
							</div>
						</div>
					</li>
				</ul>
			</div>
		</div>
	</form>
</div>
<div class="pageContent">
	<div class="panelBar"></div>
	<table class="table" width="100%" layoutH="138">
		<thead>
			<tr>
				<th width="20">序号</th>
				<th width="60">登陆名</th>
				<th width="60">会员昵称</th>
				<th width="60">会员真实姓名</th>
				<th width="60" orderField="fwallet.ftotalRmb"
					<c:if test='${param.orderField == "fwallet.ftotalRmb" }'> class="${param.orderDirection}"  </c:if>>总金额</th>
				<th width="60" orderField="fwallet.ffrozenRmb"
					<c:if test='${param.orderField == "fwallet.ffrozenRmb" }'> class="${param.orderDirection}"  </c:if>>冻结金额</th>
<%-- 				<th width="60" orderField="fwallet.fborrowCny"
					<c:if test='${param.orderField == "fwallet.fborrowCny" }'> class="${param.orderDirection}"  </c:if>>已借款金额</th>
				<th width="60" orderField="fwallet.fcanLendCny"
					<c:if test='${param.orderField == "fwallet.fcanLendCny" }'> class="${param.orderDirection}"  </c:if>>可放款金额</th>
				<th width="60" orderField="fwallet.ffrozenLendCny"
					<c:if test='${param.orderField == "fwallet.ffrozenLendCny" }'> class="${param.orderDirection}"  </c:if>>冻结放款金额</th>
				<th width="60" orderField="fwallet.falreadyLendCny"
					<c:if test='${param.orderField == "fwallet.falreadyLendCny" }'> class="${param.orderDirection}"  </c:if>>已放款金额</th>
 --%>				<th width="60">最后修改时间</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${userList}" var="user" varStatus="num">
				<tr target="sid_user" rel="${user.fwallet.fid}">
					<td>${num.index +1}</td>
					<td>${user.floginName}</td>
					<td>${user.fnickName}</td>
					<td>${user.frealName}</td>
					<td><fmt:formatNumber value="${user.fwallet.ftotalRmb}" pattern="##.######" maxIntegerDigits="10" maxFractionDigits="4"/></td>
					<td><fmt:formatNumber value="${user.fwallet.ffrozenRmb}" pattern="##.######" maxIntegerDigits="10" maxFractionDigits="4"/></td>
<%-- 					<td>${user.fwallet.fborrowCny}</td>
					<td>${user.fwallet.fcanLendCny}</td>
					<td>${user.fwallet.ffrozenLendCny}</td>
					<td>${user.fwallet.falreadyLendCny}</td> --%>
					<td>${user.fwallet.flastUpdateTime}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>

	<div class="panelBar">
		<div class="pages">
			<span>总共: ${totalCount}条</span>
		</div>
		<div class="pagination" targetType="navTab" totalCount="${totalCount}"
			numPerPage="${numPerPage}" pageNumShown="5"
			currentPage="${currentPage}"></div>
	</div>
</div>
