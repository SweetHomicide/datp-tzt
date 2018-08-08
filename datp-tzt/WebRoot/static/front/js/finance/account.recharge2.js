//银联在线
var recharge2 = {
	submitFinForm : function(that) {			
		var type = document.getElementById("finType").value;
		var random = document.getElementById("random").value;
		var rechargeType=$('#rechargeType option:selected').val();//选中的值
		var accNo = document.getElementById("accNo").value;
		var orderId = document.getElementById("orderId").value;
		var money =document.getElementById("diyMoney").value;
		var phoneNum =document.getElementById("phoneNum").value;
	
		if(money==""|| phoneNum==""){
			alert("支付金额或短信验证码不能为空");
			$("#btnRechargeOnline").attr("disabled",false);
			return false;
		}
		if(accNo==""){
			$("#btnRechargeOnline").attr("disabled",false);
			alert("银行卡为空，请新增银行卡")
			return false;
		}
		var realMoney=0;
		if (money.toString().indexOf(".") != -1) {
			money = money.toString().split(".")[0];
			money = money + (random.substring(1));
		} else {
			realMoney = money + (random.substring(1));
		}
		var currentPage=$("#currentPage").val();
		var param={
				realMoney:realMoney,
				orderId:orderId,
				accNo:accNo,
				phoneNum:phoneNum,
				currentPage:currentPage,
				fviId:rechargeType
		};
		var url="/account/payOnline.html?random=" + Math.round(Math.random() * 100);
		
		$.ajax({
			 type: 'post',
	         url: url,
	         async: false,
	         data:param,
	         dataType: "json",
	         success: function(data) {
	        	 if(data.isCum){
	        		 $("#pay_form").attr("action", data.requestFrontUrl);
	        		 $("#bizType").attr("value",data.reqData.bizType);
	        		 $("#txnSubType").attr("value",data.reqData.txnSubType);
	        		 $("#orderId1").attr("value",data.reqData.orderId);
	        		 $("#backUrl").attr("value",data.reqData.backUrl);
	        		 $("#signature").attr("value",data.reqData.signature);
	        		 $("#accNo1").attr("value",data.reqData.accNo);
	        		 $("#txnType").attr("value",data.reqData.txnType);
	        		 $("#channelType").attr("value",data.reqData.channelType);
	        		 $("#frontUrl").attr("value",data.reqData.frontUrl);
	        		 $("#certId").attr("value",data.reqData.certId);
	        		 $("#encoding").attr("value",data.reqData.encoding);
	        		 $("#version").attr("value",data.reqData.version);
	        		 $("#accessType").attr("value",data.reqData.accessType);
	        		 $("#encryptCertId").attr("value",data.reqData.encryptCertId);
	        		 $("#txnTime").attr("value",data.reqData.txnTime);
	        		 $("#merId").attr("value",data.reqData.merId);
	        		 $("#accType").attr("value",data.reqData.accType);
	        		 $("#currencyCode").attr("value",data.reqData.currencyCode);
	        		 $("#signMethod").attr("value",data.reqData.signMethod);
	        		 $("#txnAmt").attr("value",data.reqData.txnAmt);
	        		 
	        		 $("#res_orderIdR").text(data.orderIdR);
	        		 $("#res_realMoney").text(data.realMoney);
	        		 $("#res_info").text(data.data);
	        		/* $('#modal_result').modal('show');*/
	        		  $('#showModal1').click();
	        		 $("#gotoPay").click();
	        	 }else{
	        		 $("#res_orderIdR").text(data.orderIdR);
	        		 $("#res_realMoney").text(data.realMoney);
	        		 $("#res_info").text(data.data);
	        		 $("#div-gotoPay").hide();
	        		 $('#showModal1').click();
	        		/* $('#modal_result').modal('show');*/
	        	 }
	         },
	         error:function(data){
	        	 alert(error);
	         }
			
		});
		
	},
	getTime:function(){
		closeWindow();
	},
	reloadrecharge:function(id){
		var url = "/account/reloadRechargeOnLine.html?random=" + Math.round(Math.random() * 100);
		var param = {
			id : id
		};
		jQuery.ajax({
	         type: 'post',
	         url: url,
	         async: false,
	         data:param,
	         dataType: "json",
	         success: function(data) {
	        	if(""==data.result){
	        		location.href="/account/rechargeOnline.html?type=2";
	        	}else{
	        		alert(data.result);
	        	}
	        		
	         },
	         error: function() {
	             alert('发生未知错误，请稍候再试？');
	             isLink = false;
	         }
	     });
	}
};
$(function() {
	$("#btnRechargeOnline").on("click", function() {
		$(this).attr("disabled",true);
		recharge2.submitFinForm(this);
	});
	$("#bindsendmessage1").on("click", function() {
		if($("#diyMoney").val()==""){
			alert("支付金额或手机号码为空");
			return false;
		}
		msg3.sendMsgCode($(this).data().msgtype, $(this).data().tipsid, this.id);
		$("#withdrawsendmessage").attr("disabled","true");
	});
	$(".reloadrecharge").on("click", function() {
		var fid = $(this).data().fid;
		recharge2.reloadrecharge(fid);
	});	
	$('#modal_result').on('hide.bs.modal', function () {
		location.href="/account/rechargeOnline.html?type=2";
	});
	$("#continue").on("click", function() {
		location.href="/account/rechargeOnline.html?type=2";
	});
	$("#gotoPay").on("click", function() {
		$('#pay_form').submit();
	});
	
	
});
/*function checkleng(num){
	var obj=num.value;
	if(obj.length>14){
		var res=obj.substring(0,15);
		num.value=res;
	}
}*/
