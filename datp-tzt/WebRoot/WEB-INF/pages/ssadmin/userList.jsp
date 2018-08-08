<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="comm/include.inc.jsp"%>
<form id="pagerForm" method="post" action="ssadmin/userList.html">
	<input type="hidden" name="status" value="${param.status}"> <input
		type="hidden" name="keywords" value="${keywords}" /><input
		type="hidden" name="uid" value="${uid}" /><input
		type="hidden" name="startDate" value="${startDate}" /> <input
		type="hidden" name="troUid" value="${troUid}" /> <input type="hidden"
		name="ftype" value="${ftype}" /> <input type="hidden" name="pageNum"
		value="${currentPage}" /> <input type="hidden" name="numPerPage"
		value="${numPerPage}" /> <input type="hidden" name="orderField"
		value="${param.orderField}" /> <input type="hidden"
		name="orderDirection" value="${param.orderDirection}" />
</form>


<div class="pageHeader">
	<form onsubmit="return navTabSearch(this);"
		action="ssadmin/userList.html" method="post">
		<div class="searchBar">

			<table class="searchContent">
				<tr>
					<td>会员信息：<input type="text" name="keywords"
						value="${keywords}" size="40" />
					</td>
					<td>推荐人UID：<input type="text" name="troUid" value="${troUid}"
						size="10" />
					</td>
					<td>会员状态： <select type="combox" name="ftype">
							<c:forEach items="${typeMap}" var="type">
								<c:if test="${type.key == ftype}">
									<option value="${type.key}" selected="true">${type.value}</option>
								</c:if>
								<c:if test="${type.key != ftype}">
									<option value="${type.key}">${type.value}</option>
								</c:if>
							</c:forEach>
					</select></td>
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
		<li><a class="edit"
					href="ssadmin/addUsers.html"
					target="ajaxTodo" title="确定要增加10个会员吗?"><span>增加10个会员</span>
				</a>
				</li>
			<shiro:hasPermission name="ssadmin/userForbbin1.html">
				<li><a class="delete"
					href="ssadmin/userForbbin.html?uid={sid_user}&status=1&rel=listUser"
					target="ajaxTodo" title="确定要禁用吗?"><span>禁用</span> </a>
				</li>
			</shiro:hasPermission>
			<shiro:hasPermission name="ssadmin/userForbbin2.html">
				<li><a class="edit"
					href="ssadmin/userForbbin.html?uid={sid_user}&status=2&rel=listUser"
					target="ajaxTodo" title="确定要解除禁用吗?"><span>解除禁用</span> </a>
				</li>
			</shiro:hasPermission>
			<shiro:hasPermission name="ssadmin/userForbbin3.html">
				<li><a class="edit"
					href="ssadmin/userForbbin.html?uid={sid_user}&status=3&rel=listUser"
					target="ajaxTodo" title="确定要重设登陆密码吗?"><span>重设登陆密码</span> </a>
				</li>
			</shiro:hasPermission>
			<shiro:hasPermission name="ssadmin/userForbbin4.html">
				<li><a class="edit"
					href="ssadmin/userForbbin.html?uid={sid_user}&status=4&rel=listUser"
					target="ajaxTodo" title="确定要重设交易密码吗?"><span>重设交易密码</span> </a>
				</li>
		   </shiro:hasPermission>
		   <shiro:hasPermission name="ssadmin/userForbbin5.html">
				<li><a class="edit"
					href="ssadmin/cancelGoogleCode.html?uid={sid_user}"
					target="ajaxTodo" title="确定要重设GOOGLE验证吗?"><span>重置GOOGLE</span>
				</a>
				</li>
								<li><a class="edit"
					href="ssadmin/addfvirtualaddress.html?uid={sid_user}"
					target="ajaxTodo" title="确定要增加钱包地址吗?"><span>增加钱包地址</span>
				</a>
				</li>
		   </shiro:hasPermission>
		   <shiro:hasPermission name="ssadmin/userForbbin6.html">
				<li><a class="edit"
					href="ssadmin/cancelTel.html?uid={sid_user}"
					target="ajaxTodo" title="确定要重置手机号码吗?"><span>重置手机号码</span>
				</a>
				</li>
		</shiro:hasPermission>
		<shiro:hasPermission name="ssadmin/userForbbin7.html">
			<li><a class="edit"
					href="ssadmin/goUserJSP.html?url=ssadmin/updateUserGrade&uid={sid_user}"
					height="250" width="700" target="dialog" rel="updateUserGrade"><span>修改等级</span>
				</a></li>	
	  </shiro:hasPermission>			
				<li class="line">line</li>
	<shiro:hasPermission name="ssadmin/userExport.html">
				<li><a class="icon" href="ssadmin/userExport.html"
					target="dwzExport" targetType="navTab" title="实要导出这些记录吗?"><span>导出</span>
				</a></li>
	</shiro:hasPermission>		
		</ul>
		<ul></ul>
	</div>
	<table class="table" width="120%" layoutH="138">
		<thead>
			<tr>
				<th width="40" orderField="fid"
					<c:if test='${param.orderField == "fid" }'> class="${param.orderDirection}"  </c:if>>会员UID</th>
				<th width="60">注册类型</th>	
				<th width="40" orderField="fIntroUser_id.fid"
					<c:if test='${param.orderField == "fIntroUser_id.fid" }'> class="${param.orderDirection}"  </c:if>>推荐人UID</th>
				<th width="60" orderField="floginName"
					<c:if test='${param.orderField == "floginName" }'> class="${param.orderDirection}"  </c:if>>登陆名</th>				
				<th width="60">昵称</th>
				<th width="60">真实姓名</th>
				<th width="60">邮箱地址</th>
				<th width="60" orderField="fstatus"
					<c:if test='${param.orderField == "fstatus" }'> class="${param.orderDirection}"  </c:if>>会员状态</th>
				<th width="60" orderField="fscore.flevel"
					<c:if test='${param.orderField == "fscore.flevel" }'> class="${param.orderDirection}"  </c:if>>会员等级</th>
				<th width="60" orderField="fpostRealValidate"
					<c:if test='${param.orderField == "fpostRealValidate" }'> class="${param.orderDirection}"  </c:if>>证件是否提交</th>
				<th width="60" orderField="fhasRealValidate"
					<c:if test='${param.orderField == "fhasRealValidate" }'> class="${param.orderDirection}"  </c:if>>证件是否已审</th>	
				<th width="60">证件类型</th>
				<th width="60">证件号码</th>
				<th width="60">手机号码</th>
				<th width="60">推广人数</th>
				<th width="60" orderField="fregisterTime"
					<c:if test='${param.orderField == "fregisterTime" }'> class="${param.orderDirection}"  </c:if>>注册时间</th>
			    <th width="60">注册IP</th>
			    <th width="60" orderField="flastLoginTime"
					<c:if test='${param.orderField == "flastLoginTime" }'> class="${param.orderDirection}"  </c:if>>上次登陆时间</th>
			    <th width="60">上次登陆IP</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${userList}" var="user" varStatus="num">
				<tr target="sid_user" rel="${user.fid}">
					<td>${user.fid}</td>
					<td>${user.fregtype_s}</td>
					<td>${user.fIntroUser_id.fid}</td>
					<td>${user.floginName}</td>
					<td>${user.fnickName}</td>
					<td>${user.frealName}</td>
					<td>${user.femail}</td>
					<td>${user.fstatus_s}</td>
					<td>${user.fscore.flevel}</td>
					<td>${user.fpostRealValidate}</td>
					<td>${user.fhasRealValidate}</td>
					<td>${user.fidentityType_s}</td>
					<td>${user.fidentityNo}</td>
					<td>${user.ftelephone}</td>
					<td>${user.fInvalidateIntroCount}</td>
					<td>${user.fregisterTime}</td>
					<td>${user.fregIp}</td>
					<td>${user.flastLoginTime}</td>
					<td>${user.flastLoginIp}</td>
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
