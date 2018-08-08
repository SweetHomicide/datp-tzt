<%@ page pageEncoding="UTF-8"%>
<div class="accordion" fillSpace="sidebar">
	<shiro:hasPermission name="user">
		<div class="accordionHeader">
			<h2>
				<span>Folder</span>会员管理
			</h2>
		</div>
		<div class="accordionContent">
			<ul class="tree treeFolder">
				<shiro:hasPermission name="ssadmin/userList.html">
					<li><a href="ssadmin/userList.html" target="navTab"
						rel="userList">会员列表</a></li>
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/userAuditList.html">
					<li><a href="ssadmin/userAuditList.html" target="navTab"
						rel="userAuditList">待审核会员列表</a></li>
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/logList.html">
					<li><a href="ssadmin/logList.html" target="navTab"
						rel="logList">会员操作日志列表</a></li>
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/introlinfoList.html">
					<li><a href="ssadmin/introlinfoList.html" target="navTab"
						rel="introlinfoList">推广收益列表</a></li>
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/bankinfoWithdrawList.html">
					<li><a href="ssadmin/bankinfoWithdrawList.html"
						target="navTab" rel="bankinfoWithdrawList">会员银行帐户列表</a></li>
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/virtualaddressWithdrawList.html">
					<li><a href="ssadmin/virtualaddressWithdrawList.html"
						target="navTab" rel="virtualaddressWithdrawList">会员虚拟币地址列表</a></li>
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/assetList.html">
					<li><a href="ssadmin/assetList.html" target="navTab"
						rel="assetList">会员资产记录列表</a></li>
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/subscriptionList1.html">
					<li><a href="ssadmin/subscriptionList1.html" target="navTab"
						rel="subscriptionList1">众筹列表</a></li>
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/subscriptionList.html">
					<li><a href="ssadmin/subscriptionList.html" target="navTab"
						rel="subscriptionList">兑换列表</a></li>
					<li><a href="ssadmin/fina/finaList.html" target="navTab" rel="view">理财列表</a></li>	
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/entrustList.html">
					<li><a href="ssadmin/entrustList.html" target="navTab"
						rel="entrustList">委托交易列表</a></li>
				</shiro:hasPermission>
				<!-- <shiro:hasPermission name="fina/view.html">
					<li><a href="fina/view.html" target="navTab" rel="view">理财列表</a></li>
				</shiro:hasPermission> -->
			</ul>
		</div>
	</shiro:hasPermission>

	<shiro:hasPermission name="article">
		<div class="accordionHeader">
			<h2>
				<span>Folder</span>资讯管理
			</h2>
		</div>
		<div class="accordionContent">
			<ul class="tree treeFolder">
				<shiro:hasPermission name="ssadmin/articleList.html">
					<li><a href="ssadmin/articleList.html" target="navTab"
						rel="articleList">资讯列表</a></li>
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/articleTypeList.html">
					<li><a href="ssadmin/articleTypeList.html" target="navTab"
						rel="articleTypeList">资讯类型</a></li>
				</shiro:hasPermission>
			</ul>
		</div>
	</shiro:hasPermission>

	<shiro:hasPermission name="capital">
		<div class="accordionHeader">
			<h2>
				<span>Folder</span>虚拟币操作管理
			</h2>
		</div>
		<div class="accordionContent">
			<ul class="tree treeFolder">
				<shiro:hasPermission name="ssadmin/virtualCoinTypeList.html">
					<li><a href="ssadmin/virtualCoinTypeList.html" target="navTab"
						rel="virtualCoinTypeList">虚拟币类型列表</a></li>
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/walletAddressList.html">
					<li><a href="ssadmin/walletAddressList.html" target="navTab"
						rel="walletAddressList" title="虚拟币可用地址列表">虚拟币可用地址列表</a></li>
				</shiro:hasPermission>

				<shiro:hasPermission name="ssadmin/virtualCaptualoperationList.html">
					<li><a href="ssadmin/virtualCaptualoperationList.html"
						target="navTab" rel="virtualCaptualoperationList">虚拟币操作总表</a></li>
				</shiro:hasPermission>

				<shiro:hasPermission name="ssadmin/virtualCapitalInList.html">
					<li><a href="ssadmin/virtualCapitalInList.html"
						target="navTab" rel="virtualCapitalInList">虚拟币充值列表</a></li>
				</shiro:hasPermission>

				<shiro:hasPermission name="ssadmin/virtualCapitalOutList.html">
					<li><a href="ssadmin/virtualCapitalOutList.html"
						target="navTab" rel="virtualCapitalOutList">待审核虚拟币提现列表</a></li>
				</shiro:hasPermission>

				<shiro:hasPermission name="ssadmin/virtualCapitalOutSucList.html">
					<li><a href="ssadmin/virtualCapitalOutSucList.html"
						target="navTab" rel="virtualCapitalOutSucList">虚拟币成功提现列表</a></li>
				</shiro:hasPermission>

				<shiro:hasPermission name="ssadmin/virtualwalletList.html">
					<li><a href="ssadmin/virtualwalletList.html" target="navTab"
						rel="virtualwalletList">会员虚拟币列表</a></li>
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/virtualoperationlogList.html">
					<li><a href="ssadmin/virtualoperationlogList.html"
						target="navTab" rel="virtualoperationlogList">虚拟币手工充值列表</a></li>
				</shiro:hasPermission>
			</ul>
		</div>
	</shiro:hasPermission>

	<shiro:hasPermission name="cnycapital">
		<div class="accordionHeader">
			<h2>
				<span>Folder</span>人民币操作管理
			</h2>
		</div>
		<div class="accordionContent">
			<ul class="tree treeFolder">
				<shiro:hasPermission name="ssadmin/capitaloperationList.html">
					<li><a href="ssadmin/capitaloperationList.html"
						target="navTab" rel="capitaloperationList">人民币操作总表</a></li>
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/capitalInSucList.html">
					<li><a href="ssadmin/capitalInSucList.html" target="navTab"
						rel="capitalInSucList">成功充值人民币列表</a></li>
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/capitalOutSucList.html">
					<li><a href="ssadmin/capitalOutSucList.html" target="navTab"
						rel="capitalOutSucList">成功提现人民币列表</a></li>
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/capitalInList.html">
					<li><a href="ssadmin/capitalInList.html" target="navTab"
						rel="capitalInList">待审核人民币充值列表</a></li>
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/capitalOutList.html">
					<li><a href="ssadmin/capitalOutList.html" target="navTab"
						rel="capitalOutList">待审核人民币提现列表</a></li>
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/walletList.html">
					<li><a href="ssadmin/walletList.html" target="navTab"
						rel="walletList">会员人民币列表</a></li>
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/operationLogList.html">
					<li><a href="ssadmin/operationLogList.html" target="navTab"
						rel="operationLogList">人民币手工充值列表</a></li>
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/withdrawFeesList.html">
					<li><a href="ssadmin/withdrawFeesList.html" target="navTab"
						rel="withdrawFeesList" title="人民币提现手续费列表">人民币提现手续费列表</a></li>
				</shiro:hasPermission>
			</ul>
		</div>
	</shiro:hasPermission>

	<shiro:hasPermission name="report">
		<div class="accordionHeader">
			<h2>
				<span>Folder</span>报表统计
			</h2>
		</div>
		<div class="accordionContent">
			<ul class="tree treeFolder">
				<shiro:hasPermission name="ssadmin/userReport.html">
					<li><a href="ssadmin/userReport.html" target="navTab"
						rel="userReport">会员注册统计表</a></li>
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/capitaloperationReport.html">
					<li><a
						href="ssadmin/capitaloperationReport.html?type=1&status=3&url=ssadmin/capitaloperationReport"
						target="navTab" rel="capitaloperationReport">人民币充值统计表</a></li>
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/capitaloperationOutReport.html">
					<li><a
						href="ssadmin/capitaloperationReport.html?type=2&status=3&url=ssadmin/capitaloperationOutReport"
						target="navTab" rel="capitaloperationOutReport">人民币提现统计表</a></li>
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/vcOperationInReport.html">
					<li><a
						href="ssadmin/vcOperationReport.html?type=1&status=3&url=ssadmin/vcOperationInReport"
						target="navTab" rel="vcOperationInReport">虚拟币充值统计表</a></li>
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/vcOperationOutReport.html">
					<li><a
						href="ssadmin/vcOperationReport.html?type=2&status=3&url=ssadmin/vcOperationOutReport"
						target="navTab" rel="vcOperationOutReport">虚拟币提现统计表</a></li>
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/totalReport.html">
					<li><a href="ssadmin/totalReport.html" target="navTab"
						rel="totalReport">综合统计表</a></li>
				</shiro:hasPermission>
			</ul>
		</div>
	</shiro:hasPermission>

	<shiro:hasPermission name="question">
		<div class="accordionHeader">
			<h2>
				<span>Folder</span>提问管理
			</h2>
		</div>
		<div class="accordionContent">
			<ul class="tree treeFolder">
				<shiro:hasPermission name="ssadmin/questionList.html">
					<li><a href="ssadmin/questionList.html" target="navTab"
						rel="questionList">提问记录列表</a></li>
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/questionForAnswerList.html">
					<li><a href="ssadmin/questionForAnswerList.html"
						target="navTab" rel="questionList">待回复提问列表</a></li>
				</shiro:hasPermission>
			</ul>
		</div>
	</shiro:hasPermission>
	<shiro:hasPermission name="financing">
		<div class="accordionHeader">
			<h2>
				<span>Folder</span>利息理财
			</h2>
		</div>
		<div class="accordionContent">
			<ul class="tree treeFolder">
				<shiro:hasPermission name="fina/view.html">
					<li><a href="fina/view.html" target="navTab" rel="view">理财列表</a></li>
				</shiro:hasPermission>
			</ul>
		</div>
	</shiro:hasPermission>
	<shiro:hasPermission name="system">
		<div class="accordionHeader">
			<h2>
				<span>Folder</span>系统管理
			</h2>
		</div>
		<div class="accordionContent">
			<ul class="tree treeFolder">
				<shiro:hasPermission name="ssadmin/systemArgsList.html">
					<li><a href="ssadmin/systemArgsList.html" target="navTab"
						rel="systemArgsList">系统参数列表</a></li>
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/qqList.html">
					<li><a href="ssadmin/qqList.html" target="navTab" rel="qqList">QQ群列表</a>
					</li>
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/systemBankList.html">
					<li><a href="ssadmin/systemBankList.html" target="navTab"
						rel="systemBankList">银行帐户列表</a></li>
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/aboutList.html">
					<li><a href="ssadmin/aboutList.html" target="navTab"
						rel="aboutList">帮助分类列表</a></li>
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/securityList.html">
					<li><a
						href="ssadmin/goSecurityJSP.html?url=ssadmin/securityTreeList&treeId=1"
						target="navTab" rel="securityTreeList">权限列表</a></li>
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/roleList.html">
					<li><a href="ssadmin/roleList.html" target="navTab"
						rel="roleList">角色列表</a></li>
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/adminList.html">
					<li><a href="ssadmin/adminList.html" target="navTab"
						rel="adminList">管理员列表</a></li>
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/limittradeList.html">
					<li><a href="ssadmin/limittradeList.html" target="navTab"
						rel="limittradeList">限价交易列表</a></li>
				</shiro:hasPermission>
				<shiro:hasPermission name="ssadmin/autotradeList.html">
					<li><a href="ssadmin/autotradeList.html" target="navTab"
						rel="autotradeList">自动交易列表</a></li>
				</shiro:hasPermission>
			</ul>
		</div>
	</shiro:hasPermission>

</div>