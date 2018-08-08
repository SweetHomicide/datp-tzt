<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../comm/include.inc.jsp"%>
<h2 class="contentTitle">修改理财信息</h2>

<div class="pageContent">

	<form method="post" action="ssadmin/fina/save.html"
		class="pageForm required-validate"
		onsubmit="return validateCallback(this,dialogAjaxDone)">
		<div class="pageFormContent nowrap" layoutH="97">
		 <dl>
				<dt>名称：</dt>
				<dd>
					<input type="text" name="fname" maxlength="50"
						class="required" size="70"  value="${finacing.fname }"/>
				</dd>
			</dl>
			<dl>
				<dt>类型：</dt>
				<dd>
					<input type="hidden" name="fid" value="${finacing.fid }" />
					 <select
						type="combox" name="ftype" class="required">
						<c:forEach items="${typeList}" var="type">
							<c:if test="${type.fcode == finacing.ftype}">
								<option value="${type.fcode}" selected="true">${type.fvalue}</option>
							</c:if>
							<c:if test="${type.fcode != finacing.ftype}">
								<option value="${type.fcode}">${type.fvalue}</option>
							</c:if>
						</c:forEach>
					</select>
				</dd>
			</dl>
			<dl>
			<dt>支付虚拟币名称：</dt>
				<dd>
					<select
						type="combox" name="fvitypeId" class="required">
						<c:forEach items="${virtualcointypeList}" var="type">
							<c:if test="${type.fid == subscription.finacing.fid}">
								<option value="${type.fid}" selected="true">${type.fname}</option>
							</c:if>
							<c:if test="${type.fid != finacing.fid}">
								<option value="${type.fid}">${type.fname}</option>
							</c:if>
						</c:forEach>
					</select>
				</dd>
			</dl>
			
			<dl>
				<dt>是否发布：</dt>
				<dd>
					<c:choose>
						<c:when test="${finacing.fstatus eq '00501'}">
							<input type="checkbox" name="fstatus" checked='1' />
						</c:when>
						<c:otherwise>
							<input type="checkbox" name="fstatus" />
						</c:otherwise>
						
					</c:choose>
				</dd>
			</dl>
			<dl>
				<dt>收益（%）：</dt>
				<dd>
					<input  type="text" name="fproportion" value="${finacing.fproportion }"  class="required" />
				</dd>
			</dl>
			<%-- <dl>
				<dt>锁定天数：</dt>
				<dd>
					<input  type="text" name="flimit" value="${finacing.flimit }"  class="required" />
				</dd>
			</dl> --%>
			<dl>
				<dt>结算周期(天)：</dt>
				<dd>
					<input  type="text" name="fcycle" value="${finacing.fcycle }"  class="required" />
				</dd>
			</dl>
			<dl>
				<dt>最小金额：</dt>
				<dd>
					<input type="text" name="fminAmount" value="${finacing.fminAmount }" class="required number" onkeyup="checkNum(this)"/>
				</dd>
			</dl>
			<dl>
				<dt>最大金额：</dt>
				<dd>
					<input type="text" name="fmaxAmount" value="${finacing.fmaxAmount }" class="required number" onkeyup="checkNum(this)"/>
				</dd>
			</dl>
			<dl>
				<dt>开始时间：</dt>
				<dd>
					<input type="text" name="fbeginTime" class="required date"
						readonly="true" dateFmt="yyyy-MM-dd HH:mm:ss" size="40"
						value="${finacing.fbeginTime}" /> <a class="inputDateButton"
						href="javascript:;">选择</a>
				</dd>
			</dl>
			<dl>
				<dt>结束时间：</dt>
				<dd>
					<input type="text" name="fendTime" class="required date"
						readonly="true" dateFmt="yyyy-MM-dd HH:mm:ss" size="40"
						value="${finacing.fendTime}" /> <a class="inputDateButton"
						href="javascript:;">选择</a>
				</dd>
			</dl>
			<c:forEach var="list" items="${finaIdList}"  varStatus="status">
				<dl>
				<dt>${ list.ftitle}</dt>
				<input type="hidden" name="detailList[${status.index}].ftitle" value="${ list.ftitle}"  /> 
				<dd>
					<textarea type="text" name="detailList[${status.index}].fcontent" class="required ">${ list.fcontent}</textarea>
				</dd>
				<input type="hidden" name="detailList[${status.index}].fid" value="${ list.fid}"  /> 
			</c:forEach>
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
    obj.value = obj.value.replace(/^(\-)*(\d+)\.(\d\d\d\d).*$/,'$1$2.$3');//只能输入四个小数  
    if(obj.value.indexOf(".")< 0 && obj.value !=""){//以上已经过滤，此处控制的是如果没有小数点，首位不能为类似于 01、02的金额 
        obj.value= parseFloat(obj.value); 
    } 
 } 
</script>
