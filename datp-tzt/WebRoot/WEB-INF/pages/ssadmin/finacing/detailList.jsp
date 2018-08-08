<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../comm/include.inc.jsp"%>
<%-- <form id="pagerForm" method="post"
	action="ssadmin/ssadmin/tradelog/web/search.html">
	<input type="hidden" name="status" value="${param.status}"> <input
		type="hidden" name="keywords" value="${keywords}" /><input
		type="hidden" name="ftype" value="${ftype}" />  <input
		type="hidden" name="parentId" value="${parentId}" /><input
		type="hidden" name="pageNum" value="1" /> <input type="hidden"
		name="numPerPage" value="${numPerPage}" /> <input type="hidden"
		name="orderField" value="${param.orderField}" /><input type="hidden"
		name="orderDirection" value="${param.orderDirection}" />
</form>
 --%>

<div class="pageHeader">
	<form onsubmit="return navTabSearch(this);"
		action="ssadmin/tradelog/web/search.html" method="post">
		<div class="searchBar">
			<table class="searchContent">
				<tr>
					<input type="hidden" name="parentId" value="${parentId}" />
					<td>标题：<input type="text" name="fuserName" value="${fuserName}"
						size="60" />[会员登录名称]</td>
					<td>交易类型： <select type="combox" name="ftype">
						<option value="" selected="true">全部</option>
							<c:forEach items="${typeList}" var="list">
								<c:if test="${list.fcode == ftype}">
									<option value="${list.fcode}" selected="true">${list.fvalue}</option>
								</c:if>
								<c:if test="${list.fcode != ftype}">
									<option value="${list.fcode}" >${list.fvalue}</option>
								</c:if>
							</c:forEach>
					</select>
					</td>
				</tr>
			</table>
			<div class="subBar">
				<ul>
					<li><div class="buttonActive">
							<div class="buttonContent">
								<button type="submit">查询</button>
							</div>
						</div></li>
				</ul>
			</div>
		</div>
	</form>
</div>
<div class="pageContent">
	<div class="panelBar">
		<ul class="toolBar">
		   
		</ul>
	</div>
	<table class="table" width="100%" layoutH="138">
		<thead>
			<tr>
			    <th width="60">理财产品名称</th>
				<th width="60">用户名登录名称</th>
				<th width="60">金额</th>
				<th width="60">交易类型</th>
				<th width="60">创建时间</th>
				<th width="60">结束时间</th>
				<th width="60">最后修改时间</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${list}" var="list"
				varStatus="num">
				<tr target="sid_user" rel="${list.fid}">
					<td>${list.finaName}</td>
					<td>${list.fuserName}</td>
					<td>${list.famount}</td>
					<c:if test="${list.ftype eq '00301' }">
					<td>买入</td>
					</c:if>
					<c:if test="${list.ftype eq '00302' }">
					<td>取出</td>
					</c:if>
					<td>${list.beginTime}</td>
					<td>${list.endTime}</td>
					<td>${list.flastUpdateTime}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>

	<div class="panelBar">
		<%-- <div class="pages">
			<span>总共: ${totalCount}条</span>
		</div>
		<div class="pagination" targetType="navTab" totalCount="${totalCount}"
			numPerPage="${numPerPage}" pageNumShown="5"
			currentPage="${currentPage}"></div> --%>
	</div>
</div>
