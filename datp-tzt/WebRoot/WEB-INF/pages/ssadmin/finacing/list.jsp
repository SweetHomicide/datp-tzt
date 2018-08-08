<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../comm/include.inc.jsp"%>
<form id="pagerForm" method="post" action="ssadmin/fina/finaList.html">
	<input type="hidden" name="status" value="${param.status}"> <input
		type="hidden" name="keywords" value="${keywords}" /> <input
		type="hidden" name="pageNum" value="${currentPage}" /> <input
		type="hidden" name="numPerPage" value="${numPerPage}" /> <input
		type="hidden" name="orderField" value="${param.orderField}" /> <input
		type="hidden" name="orderDirection" value="${param.orderDirection}" />
</form>

<div class="pageHeader">
	<form onsubmit="return navTabSearch(this);"
		action="ssadmin/fina/finaList.html" method="post">
		<div class="searchBar">

			<table class="searchContent">
				<tr>
					<td>关键字：<input type="text" name="fname" value="${fname}"
						size="60" /> [理财名称]</td>
					<td></td>
					<td>理财类型： <select type="combox" name="ftype">
							<option value="" selected="true">全部</option>
							<c:forEach items="${typeList}" var="list">
								<c:if test="${list.fcode == ftype}">
									<option value="${list.fcode}" selected="true">${list.fvalue}</option>
								</c:if>
								<c:if test="${list.fcode !=  ftype}">
									<option value="${list.fcode}" >${list.fvalue}</option>
								</c:if>
							</c:forEach>
					</select>
					</td>
					<td>理财状态： <select type="combox" name="fstatus">
						<option value="" selected="true">全部</option>
							<c:forEach items="${fstatusList}" var="list">
								<c:if test="${list.fcode == fstatus}">
									<option value="${list.fcode}" selected="true">${list.fvalue}</option>
								</c:if>
								<c:if test="${list.fcode != fstatus}">
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
			<%-- <shiro:hasPermission name="ssadmin/auditIntroUser.html"> --%>
				<li><a class="add"
					href="ssadmin/fina/goAddModify.html?url=ssadmin/finacing/add"
					height="430" width="800" target="dialog" rel="add"><span>新增</span>
				</a>
				</li>
				<li><a class="edit"
					href="ssadmin/fina/goAddModify.html?url=ssadmin/finacing/update&fid={sid_user}"
					height="430" width="800" target="dialog" rel="update"><span>修改</span>
				</a>
				</li>
				<li><a class="delete"
					href="ssadmin/fina/del.html?fids={sid_user}"
					target="ajaxTodo" title="确定要删除吗?"><span>删除</span> </a>
				</li>
				<li><a class="edit"
					href="ssadmin/tradelog/web/search.html?ffinaId={sid_user}"
					height="500" width="800" target="navTab" rel="finacing/detailList"><span>查看记录</span>
				</a>
				</li>
			<%-- </shiro:hasPermission> --%>
		</ul>
	</div>
	<table class="table" width="120%" layoutH="138">
		<thead>
			<tr>
				<th width="100" orderField="fname"
					>名称</th>
				<th width="100" orderField="ftype"
					>类型</th>
				<th width="100" orderField="fassetsType"
					>支付虚拟币名称</th>
				<th width="100" orderField="fproportion"
					>收益（%）</th>
				<th width="60" orderField="fstatus"
					<c:if test='${param.orderField == "fstatus" }'> class="${param.orderDirection}"  </c:if>>状态</th>
				<!-- <th width="60">锁定天数</th> -->
				<th width="60">结算周期</th>
				<th width="60">开始时间</th>
				<th width="60">结束时间</th>
				<th width="60">创建时间</th>
				</tr>
		</thead>
		<tbody>
			<c:forEach items="${list}" var="finac" varStatus="num">
				<tr target="sid_user" rel="${finac.fid}">
					<td>${finac.fname}</td>
					<td>${finac.fdtype}</td>
					<td>${finac.fvitypeName}</td>
					<td>${finac.fproportion}</td>
					<c:if test="${finac.fstatus eq '00501'}">
					<td>发布</td>
					</c:if>
					<c:if test="${finac.fstatus eq '00502'}">
					<td>未发布</td>
					</c:if>
					<%-- <td>${finac.flimit}</td> --%>
					<td>${finac.fcycle}</td>
					<td>${finac.fbeginTime}</td>
					<td>${finac.fendTime}</td>
					<td>${finac.flastUpdateTime}</td>
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
