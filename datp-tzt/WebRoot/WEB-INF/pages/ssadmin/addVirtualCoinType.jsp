<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="comm/include.inc.jsp"%>
<h2 class="contentTitle">添加虚拟币类型信息</h2>


<div class="pageContent">

	<form method="post" action="ssadmin/saveVirtualCoinType.html"
		class="pageForm required-validate" enctype="multipart/form-data"
		onsubmit="return iframeCallback(this, dialogAjaxDone);">
		<div class="pageFormContent nowrap" layoutH="97">
			<dl>
				<dt>图标链接：</dt>
				<dd>
					<input type="file" class="inputStyle" value="" name="filedata"
						id="filedata" />
				</dd>
			</dl>
			<dl>
				<dt>类型：</dt>
				<dd>
					<select type="combox" name="ftype">
					<c:forEach items="${typeMap}" var="t">
						<option value="${t.key}">${t.value}</option>
					</c:forEach>
		            </select>
				</dd>
			</dl>
			<dl>
				<dt>币种名称：</dt>
				<dd>
					<input type="text" name="fname" class="required"
						size="70" />
				</dd>
			</dl>
			<dl>
				<dt>币种简称：</dt>
				<dd>
					<input type="text" name="fShortName"
						class="required" size="70" />
				</dd>
			</dl>
			<dl>
				<dt>符号：</dt>
				<dd>
					<input type="text" name="fSymbol" maxlength="10" class="required"
						size="40" />
				</dd>
			</dl>
			<!-- <dl>
				<dt>ACCESS_KEY：</dt>
				<dd>
					<input type="password" name="faccess_key"
						class="required" size="70" />
				</dd>
			</dl>
			<dl>
				<dt>SECRT_KEY：</dt>
				<dd>
					<input type="password" name="fsecrt_key"
						class="required" size="70" />
				</dd>
			</dl>
			<dl>
				<dt>IP地址：</dt>
				<dd>
					<input type="text" name="fip" class="required"
						size="70" />
				</dd>
			</dl>
			<dl>
				<dt>端口号：</dt>
				<dd>
					<input type="text" name="fport"
						class="required number" size="30" />
				</dd>
			</dl> -->
			<dl>
				<dt>小数位：</dt>
				<dd>
					<input type="text" name="fcount"
						class="required number" size="30" />
				</dd>
			</dl>
			<dl>
				<dt>交易时间：</dt>
				<dd>
					<input type="text" name="ftradetime"
						class="required"/>格式:xx.xx-xx.xx 可以精确到分,24-0表示可以交易,0-24表示禁止交易
				</dd>
			</dl>
			<dl>
				<dt>开盘价：</dt>
				<dd>
					<input type="text" name="fprice"
						class="required number" size="30" />
				</dd>
			</dl>
			<dl>
				<dt>是否可以充值提现：</dt>
				<dd>
					<input type="checkbox" name="FIsWithDraw" checked='1' />
				</dd>
			</dl>
			<dl>
				<dt>是否可以交易：</dt>
				<dd>
					<input type="checkbox" name="fisShare" checked='1' />
				</dd>
			</dl>
			<dl>
				<dt>是否为默认资产：</dt>
				<dd>
					<input type="checkbox" name="fisDefAsset" checked='1' />
				</dd>
			</dl>
			<input type="hidden" name="faccess_key" value="1"/>
			<input type="hidden" name="fsecrt_key" value="1"/>
			<input type="hidden" name="fip" value="127.0.0.1"/>
			<input type="hidden" name="fport" value="8081"/>
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
