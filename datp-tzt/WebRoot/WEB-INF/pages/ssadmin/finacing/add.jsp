<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../comm/include.inc.jsp"%>
<h2 class="contentTitle">添加理财信息</h2>

<div class="pageContent">

	<form method="post" action="ssadmin/fina/save.html"
		class="pageForm required-validate"
		onsubmit="return validateCallback(this,dialogAjaxDone)">
		<div class="pageFormContent nowrap" layoutH="97">
		   <dl>
				<dt>名称：</dt>
				<dd>
					<input type="text" name="fname" maxlength="14"
						class="required" size="70" />
				</dd>
			</dl>
			<dl>
				<dt>类型：</dt>
				<dd>
					<select type="combox" name="ftype" class="required">
						<c:forEach items="${typeList}" var="type">
							<option value="${type.fcode}">${type.fvalue}</option>
						</c:forEach>
					</select>
				</dd>
			</dl>
			 <dl>
			<dt>支付虚拟币名称：</dt>
				<dd>
					<select type="combox" name="fvitypeId" class="required">
						<c:forEach items="${virtualcointypeList}" var="type">
							<option value="${type.fid}">${type.fname}</option>
						</c:forEach>
					</select>
				</dd>
			</dl>
			<dl>
				<dt>是否发布：</dt>
				<dd>
					<input type="checkbox" name="fstatus" />
				</dd>
			</dl>
			<dl>
				<dt>收益（%）：</dt>
				<dd>
					<input type="text" name="fproportion" class="required" />
				</dd>
			</dl>
			<!-- <dl>
				<dt>锁定天数：</dt>
				<dd>
					<input type="text" name="flimit" class="required" />
				</dd>
			</dl> -->
			<dl>
				<dt>结算周期(天)：</dt>
				<dd>
					<input type="text" name="fcycle" class="required" />
				</dd>
			</dl>
			<dl>
				<dt>最小金额：</dt>
				<dd>
					<input type="text" name="fminAmount" class="required number" onkeyup="checkNum(this)"/>
				</dd>
			</dl>
			<dl>
				<dt>最大金额：</dt>
				<dd>
					<input type="text" name="fmaxAmount" class="required number" onkeyup="checkNum(this)"/>
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
			<%-- <c:forEach var="list" items="${list }" varStatus="status" begin="0" step="1">
				<dl>
					<!-- <input type="hidden" name="detailList[0].ftitle" value="12222"  />
					<input type="hidden" name="detailList[1].ftitle" value="12223"  />
					<input type="hidden" name="detailList[2].ftitle" value="12224"  /> -->
					<input type="hidden" name="detailList[${status.index}].fid" value="${list.fid }"  />
					<dt>${status[index].fvalue}</dt>
					<dd>
						<textarea type="text" name="fmaxAmount" class="required " value="${finaIdList.fcontent }"></textarea>
					</dd>
				</dl>
			
			</c:forEach> --%>
			<c:forEach var="list" items="${list}"  varStatus="status">
				<dl>
				<dt>${ list.fvalue}</dt>
				<input type="hidden" name="detailList[${status.index}].ftitle" value="${ list.fvalue}"  /> 
				<dd>
					<textarea type="text" name="detailList[${status.index}].fcontent" class="required "></textarea>
				</dd>
			</c:forEach>
			<!-- <dl>
				<dt>交易规则：</dt>
				<dd>
					<textarea type="text" name="fmaxAmount" class="required "></textarea>
				</dd>
			</dl>
			<dl>
				<dt>产品介绍：</dt>
				<dd>
					<textarea type="text" name="fmaxAmount" class="required "></textarea>
				</dd>
			</dl> -->
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
