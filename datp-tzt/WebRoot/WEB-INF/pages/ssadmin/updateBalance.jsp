<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="comm/include.inc.jsp"%>
<h2 class="contentTitle">修改定存信息</h2>


<div class="pageContent">

	<form method="post" action="ssadmin/updateBalance.html"
		class="pageForm required-validate"
		onsubmit="return validateCallback(this,dialogAjaxDone)">
		<div class="pageFormContent nowrap" layoutH="97">
			<dl>
				<dt>游戏简介：</dt>
				<dd>
				    <input type="hidden" name="fid" value="${fbalance.fid }"/>
					<textarea class="required" name="ftitle" rows="4" cols="70">${fbalance.ftitle}</textarea>
				</dd>
			</dl>
			<dl>
				<dt>推荐人收益比例：</dt>
				<dd>
					<input type="text" name="fintrolRate" class="required number"
						size="70" value="${fbalance.fintrolRate }" />
				</dd>
			</dl>
		</div>
		<div class="formBar">
			<ul>
				<li><div class="buttonActive">
						<div class="buttonContent">
							<button type="submit">保存</button>
						</div>
					</div></li>
				<li><div class="button">
						<div class="buttonContent">
							<button type="button" class="close">取消</button>
						</div>
					</div></li>
			</ul>
		</div>
	</form>

</div>


<script type="text/javascript">
function customvalidXxx(element){
	if ($(element).val() == "xxx") return false;
	return true;
}
</script>
