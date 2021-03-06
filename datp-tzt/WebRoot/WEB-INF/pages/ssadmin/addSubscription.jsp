<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="comm/include.inc.jsp"%>
<h2 class="contentTitle">添加兑换信息</h2>

<div class="pageContent">

	<form method="post" action="ssadmin/saveSubscription.html"
		class="pageForm required-validate"
		onsubmit="return validateCallback(this,dialogAjaxDone)">
		<div class="pageFormContent nowrap" layoutH="97">
		   <dl>
				<dt>简介：</dt>
				<dd>
					<input type="text" name="ftitle" maxlength="14"
						class="required" size="70" />
				</dd>
			</dl>
			<dl>
				<dt>兑换虚拟币名称：</dt>
				<dd>
					<select type="combox" name="vid" class="required">
						<c:forEach items="${allType}" var="type">
							<option value="${type.fid}">${type.fname}</option>
						</c:forEach>
					</select>
				</dd>
			</dl>
			 <dl>
			<dt>支付虚拟币名称：</dt>
				<dd>
					<select type="combox" name="vid1" class="required">
						<c:forEach items="${allType}" var="type">
							<option value="${type.fid}">${type.fname}</option>
						</c:forEach>
					</select>
				</dd>
			</dl>
			<dl>
				<dt>是否开放兑换：</dt>
				<dd>
					<input type="checkbox" name="fisopen" />
				</dd>
			</dl>
			<dl>
				<dt>是否与人民币转换：</dt>
				<dd>
					<input type="checkbox" name="fisRMB" />
				</dd>
			</dl>
			<dl>
				<dt>兑换总数量：</dt>
				<dd>
					<input type="text" name="ftotal" class="required number" />
				</dd>
			</dl>
			<dl>
				<dt>兑换价格：</dt>
				<dd>
					<input type="text" name="fprice" class="required number" onkeyup="checkNum(this)"/>
				</dd>
			</dl>
			<dl>
				<dt>每人最大兑换数量：</dt>
				<dd>
					<input type="text" name="fbuyCount" class="required digits" /><span>0为无限</span>
				</dd>
			</dl>
			<dl>
				<dt>每人最多兑换次数：</dt>
				<dd>
					<input type="text" name="fbuyTimes" class="required digits" /><span>0为无限</span>
				</dd>
			</dl>
			<dl>
				<dt>开始时间：</dt>
				<dd>
					<input type="text" name="fbeginTime" class="required date"
						readonly="true" dateFmt="yyyy-MM-dd HH:mm:ss" size="40" /> <a
						class="inputDateButton" href="javascript:;">选择</a>
				</dd>
			</dl>
			<dl>
				<dt>结束时间：</dt>
				<dd>
					<input type="text" name="fendTime" class="required date"
						readonly="true" dateFmt="yyyy-MM-dd HH:mm:ss" size="40" /> <a
						class="inputDateButton" href="javascript:;">选择</a>
				</dd>
			</dl>
		</div>
		<div class="formBar">
			<ul>
				<li><div class="buttonActive">
						<div class="buttonContent">
							<button type="submit">保存</button>
						</div>
					</div>
				</li>
				<li><div class="button">
						<div class="buttonContent">
							<button type="button" class="close">取消</button>
						</div>
					</div>
				</li>
			</ul>
		</div>
	</form>

</div>


<script type="text/javascript">
function customvalidXxx(element){
	if ($(element).val() == "xxx") return false;
	return true;
}
function checkNum(obj) {  
	obj.value = obj.value.replace(/[^\d.]/g,"");  //清除“数字”和“.”以外的字符  
    obj.value = obj.value.replace(/\.{2,}/g,"."); //只保留第一个. 清除多余的  
    obj.value = obj.value.replace(".","$#$").replace(/\./g,"").replace("$#$","."); 
    obj.value = obj.value.replace(/^(\-)*(\d+)\.(\d\d\d\d).*$/,'$1$2.$3');//只能输入两个小数  
    if(obj.value.indexOf(".")< 0 && obj.value !=""){//以上已经过滤，此处控制的是如果没有小数点，首位不能为类似于 01、02的金额 
        obj.value= parseFloat(obj.value); 
    } 
 } 
</script>
