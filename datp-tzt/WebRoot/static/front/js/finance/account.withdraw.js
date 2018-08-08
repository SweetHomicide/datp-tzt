var withdraw = {
	btc : {
		submitWithdrawBtcAddrForm : function() {
			var coinName = document.getElementById("coinName").value;
			var withdrawAddr = util.trim(document.getElementById("withdrawBtcAddr").value);
			var rechargeType=$('#rechargeType option:selected').val();//选中的值
			var withdrawRemark = util.trim(document.getElementById("withdrawBtcRemark").value);
			var withdrawBtcPass = util.trim(document.getElementById("withdrawBtcPass").value);
			var withdrawBtcAddrTotpCode = 0;
			var withdrawBtcAddrPhoneCode = 0;
			var symbol = document.getElementById("symbol").value;
			if (withdrawAddr == "") {
				util.showerrortips("binderrortips", language["comm.error.tips.63"]);
				return;
			} else {
				util.hideerrortips("binderrortips");
			}
			var start = withdrawAddr.substring(0, 1);
			if (withdrawAddr.length<26||withdrawAddr.length>34) {
				util.showerrortips("binderrortips", language["comm.error.tips.64"].format(coinName));
				return;
			}
			if (document.getElementById("withdrawBtcAddrTotpCode") != null) {
				withdrawBtcAddrTotpCode = util.trim(document.getElementById("withdrawBtcAddrTotpCode").value);
				if (!/^[0-9]{6}$/.test(withdrawBtcAddrTotpCode)) {
					util.showerrortips("binderrortips", language["comm.error.tips.65"]);
					document.getElementById("withdrawBtcAddrTotpCode").value = "";
					return;
				} else {
					util.hideerrortips("binderrortips");
				}
			}
			if (document.getElementById("withdrawBtcAddrPhoneCode") != null) {
				withdrawBtcAddrPhoneCode = util.trim(document.getElementById("withdrawBtcAddrPhoneCode").value);
				if (!/^[0-9]{6}$/.test(withdrawBtcAddrPhoneCode)) {
					util.showerrortips("binderrortips", language["comm.error.tips.66"]);
					document.getElementById("withdrawBtcAddrPhoneCode").value = "";
					return;
				} else {
					util.hideerrortips("binderrortips");
				}
			}
			var url = "/user/modifyWithdrawBtcAddr.html?random=" + Math.round(Math.random() * 100);
			var param = {
				withdrawAddr : withdrawAddr,
				totpCode : withdrawBtcAddrTotpCode,
				phoneCode : withdrawBtcAddrPhoneCode,
				symbol : symbol,
				withdrawBtcPass : withdrawBtcPass,
				withdrawRemark : withdrawRemark,
				fviId:rechargeType
			};
			$.post(url, param, function(result) {
				if (result != null) {
					if (result.code == -1) {
						util.showerrortips("", language["comm.error.tips.34"], {
							okbtn : function() {
								window.location.href = '/user/security.html#traingtr';
							},
							noshow : true
						});
					} else if (result.code == -4) {
						util.showerrortips("binderrortips", result.msg);
					} else if (result.code == -2) {
						util.showerrortips("binderrortips", result.msg);
						document.getElementById("withdrawBtcAddrTotpCode").value = "";
					} else if (result.code == -3) {
						util.showerrortips("binderrortips", result.msg);
						document.getElementById("withdrawBtcAddrPhoneCode").value = "";
					} else if (result.code == 0) {
						window.location.reload(true);
					}
				}
			}, "json");
		},
		submitWithdrawBtcForm : function() {
			var coinName = document.getElementById("coinName").value;
			var withdrawAddr = util.trim($(".input_select").attr("data"));
			var withdrawAmount = util.trim(document.getElementById("withdrawAmount").value);
			var tradePwd = util.trim(document.getElementById("tradePwd").value);
			var max_double = util.trim(document.getElementById("max_double").value);
			var min_double = util.trim(document.getElementById("min_double").value);
			var totpCode = 0;
			var phoneCode = 0;
			var symbol = document.getElementById("symbol").value;
			if (document.getElementById("btcbalance") != null && document.getElementById("btcbalance").value == 0) {
				util.showerrortips("withdrawerrortips", language["comm.error.tips.54"]);
				return;
			} else {
				util.hideerrortips("withdrawerrortips");
			}
			if (withdrawAddr == "") {
				util.showerrortips("withdrawerrortips", language["comm.error.tips.55"]);
				return;
			} else {
				util.hideerrortips("withdrawerrortips");
			}
			var reg = new RegExp("^[0-9]+\.{0,1}[0-9]{0,8}$");
			if (!reg.test(withdrawAmount)) {
				util.showerrortips("withdrawerrortips", language["comm.error.tips.56"]);
				return;
			} else {
				util.hideerrortips("withdrawerrortips");
			}
			if (parseFloat(withdrawAmount) < parseFloat(min_double)) {
				util.showerrortips("withdrawerrortips", "最小提现数量为" + min_double + "个" + coinName);
				return;
			} else {
				util.hideerrortips("withdrawerrortips");
			}
			if (parseFloat(withdrawAmount) > parseFloat(max_double)) {
				util.showerrortips("withdrawerrortips", "最大提现数量为" + max_double + "个" + coinName);
				return;
			} else {
				util.hideerrortips("withdrawerrortips");
			}
			if (tradePwd == "") {
				util.showerrortips("withdrawerrortips", language["comm.error.tips.58"]);
				return;
			} else {
				util.hideerrortips("withdrawerrortips");
			}
			if (document.getElementById("withdrawTotpCode") != null) {
				totpCode = util.trim(document.getElementById("withdrawTotpCode").value);
				if (!/^[0-9]{6}$/.test(totpCode)) {
					util.showerrortips("withdrawerrortips", language["comm.error.tips.59"]);
					return;
				} else {
					util.hideerrortips("withdrawerrortips");
				}
			}
			if (document.getElementById("withdrawPhoneCode") != null) {
				phoneCode = util.trim(document.getElementById("withdrawPhoneCode").value);
				if (!/^[0-9]{6}$/.test(phoneCode)) {
					util.showerrortips("withdrawerrortips", language["comm.error.tips.60"]);
					return;
				} else {
					util.hideerrortips("withdrawerrortips");
				}
			}
			if (document.getElementById("withdrawPhoneCode") == null) {
				util.showerrortips("withdrawerrortips", "您没有绑定手机，请去<a href='/user/security.html'>安全中心</a>绑定手机后提现。");
				return;
			}
			var url = "/account/withdrawBtcSubmit.html?random=" + Math.round(Math.random() * 100);
			var param = {
				withdrawAddr : withdrawAddr,
				withdrawAmount : withdrawAmount,
				tradePwd : tradePwd,
				totpCode : totpCode,
				phoneCode : phoneCode,
				symbol : symbol
			};
			$.post(url, param, function(result) {
				if (result != null) {
					if (result.code < 0) {
						util.showerrortips("withdrawerrortips", result.msg);
					} else if (result.code == 0) {
						document.getElementById("withdrawBtcButton").disabled = "true";
						util.showerrortips("", "您的转出订单已提交 ！", {
							okbtn : function() {
								$('#alertTips').modal('hide');
								location.reload(true);
							}
						});
					} else {
						util.hideerrortips("withdrawerrortips");
					}
				}
			});
		},
		cancelWithdrawBtc : function(id) {
			util.showconfirm(language["comm.error.tips.67"], {
				okbtn : function() {
					$('#confirmTips').modal('hide');
					var url = "/account/cancelWithdrawBtc.html?random=" + Math.round(Math.random() * 100);
					var param = {
						id : id
					};
					$.post(url, param, function(data) {
						window.location.reload(true);
					});
				},
				noshow : true
			});
		}
	},
	cny : {
		submitWithdrawCnyAddress : function(type) {
			var payeeAddr = document.getElementById("payeeAddr").value;
			var openBankTypeAddr = $("#openBankTypeAddr").val();
			var withdrawAccount = util.trim(document.getElementById("withdrawAccountAddr").value);
			var address = util.trim(document.getElementById("address").value);
			var prov = util.trim(document.getElementById("prov").value);
			var city = util.trim(document.getElementById("city").value);
			var dist = util.trim(document.getElementById("dist").value);
			var totpCode = 0;
			var phoneCode = 0;
			if (payeeAddr == "" || payeeAddr == "请输入您的银行卡开户人" || payeeAddr == "请输入您的支付宝开户人") {
				util.showerrortips("binderrortips", language["comm.error.tips.129"]);
				return;
			}
			if (openBankTypeAddr == -1) {
				util.showerrortips("binderrortips", language["comm.error.tips.70"]);
				return;
			}
			var reg = /^(\d{16}|\d{17}|\d{18}|\d{19})$/;
			if(!reg.test(withdrawAccount)){
				//银行卡号不合法
				util.showerrortips("binderrortips", language["comm.error.tips.134"]);
				return;
			}
			if (withdrawAccount == "" || withdrawAccount.length > 200 || withdrawAccount == language["comm.error.tips.62"]) {
				util.showerrortips("binderrortips", language["comm.error.tips.71"]);
				return;
			}
			var withdrawAccount2 = util.trim(document.getElementById("withdrawAccountAddr2").value);
			if (withdrawAccount != withdrawAccount2) {
				util.showerrortips("binderrortips", language["comm.error.tips.72"]);
				return;
			}
			if ((prov == "" || prov == "请选择") || (city == "" || city == "请选择") || address == "") {
				util.showerrortips("binderrortips", language["comm.error.tips.73"]);
				return;
			}
			if (address.length > 300) {
				util.showerrortips("binderrortips", language["comm.error.tips.73"]);
				return;
			}

			if (document.getElementById("addressTotpCode") != null) {
				totpCode = util.trim(document.getElementById("addressTotpCode").value);
				if (!/^[0-9]{6}$/.test(totpCode)) {
					util.showerrortips("binderrortips", language["comm.error.tips.65"]);
					document.getElementById("addressTotpCode").value = "";
					return;
				}
			}
			if (document.getElementById("addressPhoneCode") != null) {
				phoneCode = util.trim(document.getElementById("addressPhoneCode").value);
				if (!/^[0-9]{6}$/.test(phoneCode)) {
					util.showerrortips("binderrortips", language["comm.error.tips.66"]);
					document.getElementById("addressPhoneCode").value = "";
					return;
				}
			}
			util.hideerrortips("binderrortips");
			var url = "/user/updateOutAddress.html?random=" + Math.round(Math.random() * 100);
			jQuery.post(url, {
				account : withdrawAccount,
				openBankType : openBankTypeAddr,
				totpCode : totpCode,
				phoneCode : phoneCode,
				address : address,
				prov : prov,
				city : city,
				dist : dist,
				payeeAddr : payeeAddr
			}, function(result) {
				if (result != null) {
					if (result.code == 0) {
						window.location.reload(true);
					} else {
						util.showerrortips("binderrortips", result.msg);
					}
				}
			}, "json");
		},
		submitWithdrawCnyForm : function(ele) {
			var withdrawBlank = $("#withdrawBlank").val();
			var withdrawBalance = util.trim(document.getElementById('withdrawBalance').value);
			var totalwithdrawBalance=withdrawBalance;
			var tradePwd = util.trim(document.getElementById("tradePwd").value);
			var totpCode = 0;
			var phoneCode = 0;
			var min = document.getElementById("min_double").value;
			var max = document.getElementById("max_double").value;
			var reg = new RegExp("^[0-9]+\.{0,1}[0-9]{0,8}$");
			var exprice=$("#exprice").val();
			if(exprice!=""&&($("#withdrawType option:selected").val()!="")){
				withdrawBalance=util.moneyformat(withdrawBalance/exprice,2);
			}else{
				exprice=0;
			}
			if (!reg.test(withdrawBalance)) {
				util.showerrortips("withdrawerrortips", language["comm.error.tips.74"]);
				return;
			}
			if (parseFloat(withdrawBalance) < parseFloat(min)) {
				util.showerrortips("withdrawerrortips", language["comm.error.tips.77"].format(min));
				return;
			}
			if (parseFloat(withdrawBalance) > parseFloat(max)) {
				util.showerrortips("withdrawerrortips", language["comm.error.tips.78"].format(max));
				return;
			}
			if (tradePwd == "" || tradePwd.length > 200) {
				util.showerrortips("withdrawerrortips", language["comm.error.tips.79"]);
				return;
			}
			if (document.getElementById("withdrawTotpCode") != null) {
				totpCode = util.trim(document.getElementById("withdrawTotpCode").value);
				if (!/^[0-9]{6}$/.test(totpCode)) {
					util.showerrortips("withdrawerrortips", language["comm.error.tips.80"]);
					return;
				}

			}
			if (document.getElementById("withdrawPhoneCode") != null) {
				phoneCode = util.trim(document.getElementById("withdrawPhoneCode").value);
				if (!/^[0-9]{6}$/.test(phoneCode)) {
					util.showerrortips("withdrawerrortips", language["comm.error.tips.81"]);
					return;
				}
			}
			if (document.getElementById("withdrawPhoneCode") == null) {
				util.showerrortips("withdrawerrortips", "您没有绑定手机，请去<a href='/user/security.html'>安全中心</a>绑定手机后提现。");
				return;
			}
			
			ele.disabled = true;
			let withdrawType=$('#withdrawType option:selected').val();//选中的值
			var url = "/account/withdrawCnySubmit.html?random=" + Math.round(Math.random() * 100);
			var param = {
				tradePwd : tradePwd,
				withdrawBalance : totalwithdrawBalance,
				phoneCode : phoneCode,
				totpCode : totpCode,
				withdrawBlank : withdrawBlank,
				fprice:exprice,
				symbol:withdrawType
			};
			jQuery.post(url, param, function(result) {
				ele.disabled = false;
				if (result != null) {
					if (result.code == 0 || result.code == "0") {
						/*util.showerrortips("", "您的提现订单已提交，请耐心等待管理员审核 ！", {
							okbtn : function() {
								$('#alertTips').modal('hide');
								location.reload(true);
							}
						});*/
						alert("您的提现订单已提交，请耐心等待管理员审核 ！");
						location.reload(true);
					} else {
						util.showerrortips("withdrawerrortips", result.msg);
					}
				}
			}, "json");
		},
		cancelWithdrawCny : function(outId) {
			util.showconfirm(language["comm.error.tips.67"], {
				okbtn : function() {
					$('#confirmTips').modal('hide');
					var url = "/account/cancelWithdrawcny.html?random=" + Math.round(Math.random() * 100);
					var param = {
						id : outId
					};
					$.post(url, param, function(data) {
						window.location.reload(true);
					});
				},
				noshow : true
			});
		},
		calculatefeesRate : function() {
			var amount = $("#withdrawBalance").val();
			var exprice=$("#exprice").val();
			var feesRate = $("#feesRate").val();
			if (amount == "") {
				amount = 0;
			}
			var feeamt = "";
			if(exprice!=""&&($("#withdrawType option:selected").val()!="")){
				feeamt= util.moneyformat(amount/exprice*feesRate,2);
				amount=util.moneyformat(amount/exprice,2);
			}else{
				feeamt = util.moneyformat(util.accMul(amount, feesRate), 2);
			}
			
			$("#free").html(feeamt);
			$("#amount").html(util.moneyformat(parseFloat(amount) - parseFloat(feeamt), 2));
		},
		getTotal:function(){
			var total=$("#spanftotalRmb").text();
			$("#withdrawBalance").val("");
			$("#withdrawBalance").val(total);
		}
		
	}
};
$(function() {
	$(".btn-sendmsg").removeAttr("disabled");
	
	$(".btn-sendmsg").on("click", function() {
		msg.sendMsgCode($(this).data().msgtype, $(this).data().tipsid, this.id);
	});
	$("#withdrawBtcAddrBtn").on("click", function() {
		withdraw.btc.submitWithdrawBtcAddrForm();
	});
	$("#withdrawBtcButton").on("click", function() {
		withdraw.btc.submitWithdrawBtcForm();
	});
	$("#withdrawAmount").on("keypress", function(event) {
		return util.VerifyKeypress(this, event, 4);
	});
	/*$("#withdrawAmount").on("keypress", function(event) {
		return util.VerifyKeypress(this, event, 4);
	}).on("keyup", function() {
		withdraw.btc.calc();
	});*/
	$(".cancelWithdrawBtc").on("click", function(event) {
		withdraw.btc.cancelWithdrawBtc($(this).data().fid);
	});

	$(".recordtitle").on("click", function() {
		util.recordTab($(this));
	});
	$("#withdrawCnyAddrBtn").on("click", function() {
		withdraw.cny.submitWithdrawCnyAddress();
	});
	$("#withdrawBalance").on("keypress", function(event) {
		return util.VerifyKeypress(this, event, 2);
	}).on("keyup", function() {
		withdraw.cny.calculatefeesRate();
	});
	$("#withdrawCnyButton").on("click", function(event) {
		withdraw.cny.submitWithdrawCnyForm(this);
	});
	$(".cancelWithdrawcny").on("click", function(event) {
		withdraw.cny.cancelWithdrawCny($(this).data().fid);
	});
	$("#withdrawAccountAddr2").bind("copy cut paste", function(e) {
		return false;
	});
	$("#btnAll").on("click", function(e) {
		withdraw.cny.getTotal();
		withdraw.cny.calculatefeesRate();
	});
	$("#withdrawType").change(function() {
		var rechargeType=$('#withdrawType option:selected').val();//选中的值
		var url="";
		if(rechargeType==""){
			 url="/account/withdrawCny.html";
		}else{
			 url="/account/withdrawbtcToCny.html?VirtualId="+rechargeType;
		}
		window.location.href = url;
	});
	
});

function vali(){
	var withdrawBalance= Number($("#withdrawBalance").val());
	var total=Number($("#spanftotalRmb").text());
	if(withdrawBalance>total){
		alert("提现金额不应大于账户余额!");
		return false;
	}
}
function valicount(){
	var accountBalance=Number($("#accountBalance").text());
	var withdrawAmount=$("#withdrawAmount").val();
	if(withdrawAmount>accountBalance){
		alert("转出额度不应大于账户余额！");
	}
}