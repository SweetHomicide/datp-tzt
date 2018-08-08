<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="comm/include.inc.jsp"%>
<h2 class="contentTitle">修改资讯信息</h2>


<div class="pageContent">

     <form method="post" action="ssadmin/updateArticle.html"
		class="pageForm required-validate" enctype="multipart/form-data"
		onsubmit="return iframeCallback(this, dialogAjaxDone);">		
		<div class="pageFormContent nowrap" layoutH="97">
			<dl>
				<dt>资讯标题：</dt>
				<dd>
				    <input type="hidden" name="fid" value="${farticle.fid}"/>
					<input type="text" name="ftitle" 
						class="required" size="70" value="${farticle.ftitle}"/>
				</dd>
			</dl>
			<dl>
				<dt>是否站外文章：</dt>
				<dd>
					<c:choose>
						<c:when test="${farticle.fisout}">
							<input type="checkbox" name="fisout" checked='1' />
						</c:when>
						<c:otherwise>
							<input type="checkbox" name="fisout" />
						</c:otherwise>
					</c:choose>
				</dd>
			</dl>
			<dl>
				<dt>站外链接：</dt>
				<dd>
					<input type="text" name="foutUrl" 
						 size="70"  value="${farticle.foutUrl}"/>
				</dd>
			</dl>
			<dl>
				<dt>类型：</dt>
				<dd>
					<input type="hidden" name="articleLookup.id" id="articleLookup.id" value="${farticle.farticletype.fid}"/>
				    <input type="text" class="required" name="articleLookup.articleType" value="${farticle.farticletype.fname}"
				     suggestFields="id,articleType" suggestUrl="ssadmin/articleTypeLookup.html" lookupGroup="orgLookup" readonly="readonly"/>
				    <a class="btnLook" href="ssadmin/articleTypeLookup.html" lookupGroup="articleLookup">查找带回</a>	
				</dd>
			</dl>
			<dl>
				<dt>图片：</dt>
				<dd>
					<input type="file" class="inputStyle" value="" name="filedata" id="filedata" />
				</dd>
			</dl>
			<dl>
				<dt>资讯内容：</dt>
				<dd>
					<textarea class="editor" name="fcontent" rows="20" cols="105"
						tools="simple" upImgUrl="ssadmin/upload.html"
						upImgExt="jpg,jpeg,gif,png">
						${farticle.fcontent}
				</textarea>
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
</script>
