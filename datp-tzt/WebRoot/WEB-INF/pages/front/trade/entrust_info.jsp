<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="../comm/include.inc.jsp"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;
%>



<div class="col-xs-12">
	<div class="panel">
		<div class="panel-heading">
			<span class="text-danger">当前委托</span>
			<span class="pull-right fentruststitle" data-type="0" data-value="0">收起 -</span>
		</div>
		<div class="panel-body" id="fentrustsbody0" style="">
			<table class="table">
			
			<tr>
				<td>委托时间</td>
				<td>类型</td>
				<td>委托价格</td>
				<td>委托数</td>
				<td>手续费</td>
				<td>平均成交价</td>
				<td>成交数量</td>
				<td>成交金额</td>
				<td>总金额</td>
				<td>状态</td>
				<td class="width-65">操作</td>
			</tr>
				<c:if test="${fn:length(fentrusts1)==0 }">
					<tr>
						<td colspan="11" class="no-data-tips">
							<span>
								暂无委托
							</span>
						</td>
					</tr>
				</c:if>
					
				<c:forEach items="${fentrusts1 }" var="v" varStatus="vs">
					<tr>
						<td class="gray"><fmt:formatDate value="${v.fcreateTime }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
						<td class="${v.fentrustType==0?'text-danger':'text-success' }">${v.fentrustType_s}${v.fisLimit==true?'[市价]':'' }</td>
						<td>￥<fmt:formatNumber value="${v.fprize }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="${fvirtualcointype.fcount }"/></td>
						<td>${fvirtualcointype.fSymbol}<fmt:formatNumber value="${v.fcount }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/></td>
						<c:choose>
						<c:when test="${v.fentrustType==0 }">
						<td>${fvirtualcointype.fSymbol}<fmt:formatNumber value="${v.ffees}" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/></td>
						</c:when>
						<c:otherwise>
						<td>￥<fmt:formatNumber value="${v.ffees}" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/></td>
						</c:otherwise>
						</c:choose>
						<td>￥<fmt:formatNumber value="${((v.fcount-v.fleftCount)==0)?0:  v.fsuccessAmount/((v.fcount-v.fleftCount)) }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/></td>
						<td>${fvirtualcointype.fSymbol}<fmt:formatNumber value="${v.fcount-v.fleftCount }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/></td>
						<td>￥<fmt:formatNumber value="${v.fsuccessAmount }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/></td>
						<td>￥<fmt:formatNumber value="${v.famount }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/></td>
						<td>
						${v.fstatus_s }
						</td>
						<td>
						<c:if test="${v.fstatus==1 || v.fstatus==2}">
						<a href="javascript:void(0);" class="tradecancel" data-value="${v.fid}">取消</a>
						</c:if>
						<c:if test="${v.fstatus==3}">
						<a href="javascript:void(0);" class="tradelook" data-value="${v.fid}">查看</a>
						</c:if>
						</td>
                          </tr>
			</c:forEach>
				
			</table>
		</div>
	</div>
</div>
<div class="col-xs-12">
	<div class="panel">
		<div class="panel-heading">
			<span class="text-danger">最近成交记录</span>
			<span class="pull-right fentruststitle" data-type="1" data-value="0">收起 -</span>
		</div>
		<div class="panel-body" id="fentrustsbody1" style="">
			<table class="table">
				<tr>
					<td>成交时间</td>
					<td>买/卖</td>
					<td>成交价</td>
					<td>成交量</td>
					<td>总金额</td>
				</tr>
				<c:if test="${fn:length(successEntrusts)==0 }">
					<tr>
						<td colspan="5" class="no-data-tips">
							<span>
								暂无成交记录
							</span>
						</td>
					</tr>
				</c:if>
					
				<c:forEach items="${successEntrusts }" var="v" varStatus="vs" begin="0" end="30">
					<tr>
						<td><fmt:formatDate value="${v.fcreateTime }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
						<td class="${v.fEntrustType==0?'text-danger':'text-success' }">${v.fEntrustType==0?'买入':'卖出' }</td>
						<td class="${v.fEntrustType==0?'text-danger':'text-success' }">￥<fmt:formatNumber value="${v.fprize }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="${fvirtualcointype.fcount }"/></td>
						<td>${fvirtualcointype.fSymbol}<fmt:formatNumber value="${v.fcount }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/></td>
						<td>￥<fmt:formatNumber value="${v.fprize*v.fcount }" pattern="##.##" maxIntegerDigits="10" maxFractionDigits="4"/></td>
					</tr>	
			</c:forEach>
				
			</table>
		</div>
	</div>
</div>
