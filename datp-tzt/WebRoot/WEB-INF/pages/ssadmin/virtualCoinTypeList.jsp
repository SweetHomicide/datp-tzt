<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="comm/include.inc.jsp"%>
<form id="pagerForm" method="post"
	action="ssadmin/virtualCoinTypeList.html">
	<input type="hidden" name="status" value="${param.status}"> <input
		type="hidden" name="keywords" value="${keywords}" /> <input
		type="hidden" name="pageNum" value="${currentPage}" /> <input
		type="hidden" name="numPerPage" value="${numPerPage}" /> <input
		type="hidden" name="orderField" value="${param.orderField}" /><input
		type="hidden" name="orderDirection" value="${param.orderDirection}" />
</form>

<div class="pageHeader">
	<form onsubmit="return navTabSearch(this);"
		action="ssadmin/virtualCoinTypeList.html" method="post">
		<div class="searchBar">

			<table class="searchContent">
				<tr>
					<td>关键字：<input type="text" name="keywords" value="${keywords}"
						size="60" />[名称、简称、描述]</td>
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
	<div class="panelBar">
		<ul class="toolBar">
			<shiro:hasPermission
				name="ssadmin/addVirtualCoinType">
				<li><a class="add"
					href="ssadmin/goVirtualCoinTypeJSP.html?url=ssadmin/addVirtualCoinType"
					height="350" width="800" target="dialog" rel="addVirtualCoinType"><span>新增</span>
				</a>
				</li>
		     </shiro:hasPermission>
			<shiro:hasPermission
				name="ssadmin/deleteVirtualCoinType.html">
				<li><a class="delete"
					href="ssadmin/deleteVirtualCoinType.html?uid={sid_user}&status=1"
					target="ajaxTodo" title="确定要禁用吗?"><span>禁用</span> </a>
				</li>
			</shiro:hasPermission>
			<shiro:hasPermission name="ssadmin/goWallet.html">
				<li><a class="edit"
					href="ssadmin/goVirtualCoinTypeJSP.html?url=ssadmin/goWallet&uid={sid_user}"
					height="250" width="700" target="dialog" rel="goWallet"><span>启用</span>
				</a></li>
			</shiro:hasPermission>
			<shiro:hasPermission name="ssadmin/updateVirtualCoinType.html">
				<li><a class="edit"
					href="ssadmin/goVirtualCoinTypeJSP.html?url=ssadmin/updateVirtualCoinType&uid={sid_user}"
					height="350" width="800" target="dialog"
					rel="updateVirtualCoinType"><span>修改</span> </a>
				</li>
			</shiro:hasPermission>
			<shiro:hasPermission name="ssadmin/updateCoinFees.html">
				<li><a class="edit"
					href="ssadmin/goVirtualCoinTypeJSP.html?url=ssadmin/updateCoinFees&uid={sid_user}"
					height="500" width="750" target="dialog"
					rel="updateVirtualCoinType"><span>修改手续费</span> </a>
				</li>
			</shiro:hasPermission>
			<shiro:hasPermission name="ssadmin/testWallet.html">
				<li><a class="edit"
					href="ssadmin/testWallet.html?uid={sid_user}" target="ajaxTodo"><span>测试钱包</span>
				</a></li>
			</shiro:hasPermission>
			<shiro:hasPermission name="ssadmin/createAddress.html">
				<li><a class="edit"
					href="ssadmin/goVirtualCoinTypeJSP.html?url=ssadmin/createAddress&uid={sid_user}"
					height="250" width="700" target="dialog" rel="createWalletAddress"><span>生成钱包地址</span>
				</a>
				</li>
			</shiro:hasPermission>
		</ul>
	</div>
	<table class="table" width="100%" layoutH="138">
		<thead>
			<tr>
				<th width="20">FID</th>
				<th width="20">类型</th>
				<th width="60" orderField="fname"
					<c:if test='${param.orderField == "fname" }'> class="${param.orderDirection}"  </c:if>>名称</th>
				<th width="60" orderField="fShortName"
					<c:if test='${param.orderField == "fShortName" }'> class="${param.orderDirection}"  </c:if>>简称</th>
				<th width="60" orderField="fstatus"
					<c:if test='${param.orderField == "fstatus" }'> class="${param.orderDirection}"  </c:if>>状态</th>
				<th width="30">符号</th>
				<th width="60">IP地址</th>
				<th width="60">端口号</th>
				<th width="60">是否可以交易</th>
				<th width="60">是否可以充值提现</th>
				<th width="60">小数位</th>
				<th width="60">交易时间</th>
				<th width="60">开盘价</th>
				<th width="60" orderField="faddTime"
					<c:if test='${param.orderField == "faddTime" }'> class="${param.orderDirection}"  </c:if>>创建时间</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${virtualCoinTypeList}" var="virtualCoinType"
				varStatus="num">
				<tr target="sid_user" rel="${virtualCoinType.fid}">
					<td>${virtualCoinType.fid}</td>
					<td>${virtualCoinType.ftype_s}</td>
					<td>${virtualCoinType.fname}</td>
					<td>${virtualCoinType.fShortName}</td>
					<td>${virtualCoinType.fstatus_s}</td>
					<td>${virtualCoinType.fSymbol}</td>
					<td>${virtualCoinType.fip}</td>
					<td>${virtualCoinType.fport}</td>
					<td>${virtualCoinType.fisShare}</td>
					<td>${virtualCoinType.FIsWithDraw}</td>
					<td>${virtualCoinType.fcount}</td>
					<td>${virtualCoinType.ftradetime}</td>
					<td><fmt:formatNumber value="${virtualCoinType.fprice}" pattern="##.######" maxIntegerDigits="10" maxFractionDigits="6"/></td>
					<td>${virtualCoinType.faddTime}</td>
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
