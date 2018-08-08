var recharge = {
	submitFinForm : function(that) {
		var sbank = document.getElementById("sbank").value;
		var type = document.getElementById("finType").value;
		var random = document.getElementById("random").value;
		var minMoney = document.getElementById("minRecharge").value;
		var money = 0;
		if (sbank == "") {
			util.showerrortips("", language["comm.error.tips.83"]);
			return;
		}
		money = document.getElementById("diyMoney").value;
		if (money.toString().indexOf(".") != -1) {
			money = money.toString().split(".")[0];
			money = money + (random.substring(1));
		} else {
			money = money + (random.substring(1));
		}
		if (Number(money) < Number(minMoney) || isNaN(money)) {
			util.showerrortips("", language["comm.error.tips.84"].format(minMoney));
			return;
		}
		var url = "/account/alipayManual.html?random=" + Math.round(Math.random() * 100);
		var param = {
			money : money,
			type : type,
			sbank : sbank
		};
		that.disabled = true;
		jQuery.post(url, param, function(result) {
			that.disabled = false;
			if (result != null) {
				if (result.code < 0) {
					util.showerrortips("", result.msg);
				} else {
					if (type == 1) {// 支付宝
						document.getElementById("fownerName").innerHTML = result.fownerName;
						document.getElementById("fbankNumber").innerHTML = result.fbankNumber;
						document.getElementById("fbankAddress").innerHTML = result.fbankAddress;
						document.getElementById("bankMoney").innerHTML = result.money;
						document.getElementById("bankInfo").innerHTML = result.tradeId;
						document.getElementById("desc").value = result.tradeId;
						document.getElementById("bankInfotips").innerHTML = result.tradeId;
						document.getElementById("rechage1").style.display = "none";
						document.getElementById("rechage2").style.display = "block";
						recharge.refTenbody();
					} else if (type == 0) {
						document.getElementById("fownerName").innerHTML = result.fownerName;
						document.getElementById("fbankNumber").innerHTML = result.fbankNumber;
						document.getElementById("fbankAddress").innerHTML = result.fbankAddress;
						document.getElementById("bankMoney").innerHTML = result.money;
						document.getElementById("bankInfo").innerHTML = result.tradeId;
						document.getElementById("desc").value = result.tradeId;
						document.getElementById("bankInfotips").innerHTML = result.tradeId;
						document.getElementById("rechage1").style.display = "none";
						document.getElementById("rechage2").style.display = "block";
						recharge.refTenbody();
					}
					$(".recharge-process span").removeClass("active");
					$("#rechargeprocess2").addClass("active");
				}
			}
		});
	},
	submitPaymentInformation : function() {
		var type = document.getElementById("finType").value;
		var des = language["comm.error.tips.93"];
		if (type == 0) {
			des = language["comm.error.tips.93"];
		} else if (type == 1) {
			des = language["comm.error.tips.94"];
		}
		document.getElementById("rechage2").style.display = "none";
		document.getElementById("rechage4").style.display = "block";
		$(".recharge-process span").removeClass("active");
		$("#rechargeprocess4").addClass("active");
		var desc = document.getElementById("desc").value;
		var url = "/account/rechargeCnySubmit.html?random=" + Math.round(Math.random() * 100);
		var param = {
			bank : '无',
			account : '无',
			payee : '无',
			phone : '无',
			type : 0,
			desc : desc

		};
		jQuery.post(url, param, function(result) {
			if (result != null) {
				if (result.code < 0) {
					util.showerrortips("", result.msg);
				} else if (result.code == 0) {
					document.getElementById("rechage3").style.display = "none";
					document.getElementById("rechage4").style.display = "block";
					$(".recharge-process span").removeClass("active");
					$("#rechargeprocess4").addClass("active");
					recharge.refTenbody();
				}
			}
		});
	},
	submitTransferAccounts : function() {

		var bank = $("#fromBank").val();
		var account = document.getElementById("fromAccount").value;
		var payee = document.getElementById("fromPayee").value;
		var phone = document.getElementById("fromPhone").value;
		var type = document.getElementById("finType").value;
		var desc = document.getElementById("desc").value;
		if (bank == "" || bank == "-1" || account == "" || payee == "" || phone == "") {
			util.showerrortips("", language["comm.error.tips.95"]);
			return;
		}
		var reg = /^(\d{16}|\d{17}|\d{18}|\d{19})$/;
		if(!reg.test(account)){
			//银行卡号不合法
			util.showerrortips("",language["comm.error.tips.134"]);
			return;
		}
		var url = "/account/rechargeCnySubmit.html?random=" + Math.round(Math.random() * 100);
		var param = {
			bank : bank,
			account : account,
			payee : payee,
			phone : phone,
			type : type,
			desc : desc

		};
		jQuery.post(url, param, function(result) {
			if (result != null) {
				if (result.code < 0) {
					util.showerrortips("", result.msg);
				} else if (result.code == 0) {
					document.getElementById("rechage3").style.display = "none";
					document.getElementById("rechage4").style.display = "block";
					$(".recharge-process span").removeClass("active");
					$("#rechargeprocess4").addClass("active");
					recharge.refTenbody();
				}
			}
		});
	},
	refTenbody : function() {
		var currentPage = document.getElementById("currentPage").value;
		var type = document.getElementById("finType").value;
		var url = "/account/refTenbody.html?currentPage=" + currentPage + "&type=" + type + "&random=" + Math.round(Math.random() * 100);
		jQuery("#recordbody0").load(url, null, function(data) {
			$(".completioninfo").on("click", function() {
				var fid = $(this).data().fid;
				recharge.updateFinTransactionReceive(fid);
			});
			$(".rechargecancel").on("click", function() {
				var fid = $(this).data().fid;
				recharge.cancelRechargeCNY(fid);
			});
		});
	},
	updateFinTransactionReceive : function(fid) {
		document.getElementById("desc").value = fid;
		document.getElementById("rechage1").style.display = "none";
		document.getElementById("rechage2").style.display = "none";
		document.getElementById("rechage3").style.display = "block";
		document.getElementById("rechage4").style.display = "none";
		$(".recharge-process span").removeClass("active");
		$("#rechargeprocess3").addClass("active");
	},
	cancelRechargeCNY : function(id) {
		var url = "/account/cancelRechargeCnySubmit.html?random=" + Math.round(Math.random() * 100);
		var param = {
			id : id
		};
		jQuery.post(url, param, function(result) {
			location.reload(true);
		});
	},
	commission : function() {
		var amount = $("#diyMoney").val();
		var fee = 0;
		var fee = util.accMul(amount, fee);
		$("#ffee").html(fee);
		$("#realamount").html(amount - fee);
	},
	bankitemcheck : function(ele, value) {
		$(".bank-item-checked", ".bank-item").hide(0, function() {
			ele.find(".bank-item-checked").show();
		});
		$("#bankid").val(value);
	},
	submitOnlineFinForm : function(that) {
		var sbank = document.getElementById("bankid").value;
		var type = document.getElementById("finType").value;
		var minMoney = document.getElementById("minRecharge").value;
		var money = 0;
		if (sbank == "" || sbank == "0") {
			util.showerrortips("errortips", language["comm.error.tips.83"]);
			return;
		}
		money = document.getElementById("diyMoney").value;
		if (Number(money) < Number(minMoney) || isNaN(money)) {
			util.showerrortips("errortips", language["comm.error.tips.84"].format(minMoney));
			return;
		}
		var url = "/onlinepay/sumapay.html?random=" + Math.round(Math.random() * 100);
		var param = {
			money : money,
			type : type,
			sbank : sbank
		};
		that.disabled = true;
		var isLink = false;
		jQuery.ajax({
	         type: 'post',
	         url: url,
	         async: false,
	         data:param,
	         dataType: "json",
	         success: function(result) {
	        	if (result.code < 0) {
					util.showerrortips("errortips", result.msg);
				} else {
					if (type == 2) {// 在线充值
						jQuery(that).attr("href",result.msg);
						isLink=true;
					}
				}
	         },
	         error: function() {
	             alert('发生未知错误，请稍候再试？');
	             isLink = false;
	         }
	     });
	     return isLink;
		
	},
	submitQrCodeFinForm : function(that) {
		var accounts = document.getElementById("accounts").value;
		var imgcode = document.getElementById("imgcode").value;
		var type = document.getElementById("finType").value;
		var random = document.getElementById("random").value;
		var minMoney = document.getElementById("minRecharge").value;
		var money = 0;
		if (accounts == "") {
			util.showerrortips("errortips", "请输入您的帐号！");
			return;
		}
		if (imgcode == "") {
			util.showerrortips("errortips", "请输入验证码！");
			return;
		}
		money = document.getElementById("diyMoney").value;
		if (money.toString().indexOf(".") != -1) {
			money = money.toString().split(".")[0];
			money = money + (random.substring(1));
		} else {
			money = money + (random.substring(1));
		}
		if (Number(money) < Number(minMoney) || isNaN(money)) {
			util.showerrortips("errortips", language["comm.error.tips.84"].format(minMoney));
			return;
		}
		var url = "/account/alipayTransfer.html?random=" + Math.round(Math.random() * 100);
		var param = {
			money : money,
			type : type,
			accounts : accounts,
			imageCode:imgcode
		};
		that.disabled = true;
		jQuery.post(url, param, function(result) {
			that.disabled = false;
			if (result != null) {
				if (result.code < 0) {
					util.showerrortips("errortips", result.msg);
				} else {
					util.showerrortips("", "请及时进行充值操作 ！", {
						okbtn : function() {
							$('#alertTips').modal('hide');
							location.reload(true);
						}
					});
				}
			}
		}, "json");
	},
	getvirtualaddress:function(){
		var symbol=$("#symbol").val();
		var url="/account/getVirtualAddress.html?symbol="+symbol+"&random=" + Math.round(Math.random() * 100);
		$.get(url,function(data){
			if(data.code<0){
				util.showerrortips("", data.msg);
			}else{
				window.location.reload(true);
			}
		},"json");
	}
};
$(function() {
	$(".recordtitle").on("click", function() {
		util.recordTab($(this));
	});
	$("#rechargebtn").on("click", function() {
		recharge.submitFinForm(this);
	});
	$("#rechargenextbtn").on("click", function() {
		recharge.submitPaymentInformation();
	});
	$("#rechargesuccessbtn").on("click", function() {
		recharge.submitTransferAccounts();
	});
	$(".completioninfo").on("click", function() {
		var fid = $(this).data().fid;
		recharge.updateFinTransactionReceive(fid);
	});
	$(".rechargecancel").on("click", function() {
		var fid = $(this).data().fid;
		recharge.cancelRechargeCNY(fid);
	});
	$("#diyMoney").on("keypress", function(event) {
		return util.VerifyKeypress(this, event, 0);
	});
	$("#diyMoney", ".online").on("keyup", function() {
		recharge.commission();
	});
	$(".bank-item").on("click", function() {
		var that = $(this);
		recharge.bankitemcheck(that, that.data().fid);
	});
	$("#rechargeonlinebtn").on("click", function() {
		return recharge.submitOnlineFinForm(this);
	});
	$("#qrcoderechargebtn").on("click", function() {
		recharge.submitQrCodeFinForm(this);
	});
	$("#virtualaddress").on("click", function() {
		recharge.getvirtualaddress(this);
	});
});