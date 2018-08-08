var recharge = {
	submitFinForm : function(that) {
		var codeA = document.getElementById("codeA").value;
		var codeB = document.getElementById("codeB").value;
		if (codeA == null || codeA == "") {
			util.showerrortips("", "A段充值码不能为空");
			return;
		}
		if (codeB == null || codeB == "") {
			util.showerrortips("", "B段充值码不能为空");
			return;
		}

		var url = "/json/payCode/recharge.html?random="
				+ Math.round(Math.random() * 100);
		var param = {
			key : codeA,
			value : codeB
		};
		that.disabled = true;
		jQuery.post(url, param, function(result) {
			that.disabled = false;
			if (result != null) {
				if (result.code < 0) {
					util.showerrortips("", result.msg);
				} else {
					util.showerrortips("", result.msg, {
						okbtn : function() {
							location.reload(true);
						}
					});
				}
			}
		});
	},
	submitProxyForm : function(that) {
		var famt = document.getElementById("famt").value;
		var tradePwd = document.getElementById("tradePwd").value;
		if (isNaN(famt) ||　famt　=='' || famt ==0 || famt == null) {
			util.showerrortips("", "充值金额不合法");
			return;
		}
		
		if (tradePwd == null || tradePwd == "") {
			util.showerrortips("", "交易密码不能为空");
			return;
		}

		var url = "/json/payCode/savePaycode.html?random="
				+ Math.round(Math.random() * 100);
		var param = {
			tradePassword : tradePwd,
			amount : famt
		};
		that.disabled = true;
		jQuery.post(url, param, function(result) {
			that.disabled = false;
			if (result != null) {
				if (result.code == 1) {
					util.showerrortips("", result.msg);
				} else {
					util.showerrortips("", result.msg, {
						okbtn : function() {
							location.reload(true);
						}
					});
				}
			}
		});
	}
};

$(function() {
	$(".recordtitle").on("click", function() {
		util.recordTab($(this));
	});
	$("#qrcoderechargebtn").on("click", function() {
		recharge.submitFinForm(this);
	});
	$("#proxyrechargebtn").on("click", function() {
		recharge.submitProxyForm(this);
	});
	$("#famt").on("keypress", function(event) {
		return util.VerifyKeypress(this, event, 2);
	});
});

function submitSure(fid) {
	var url = "/json/payCode/sendPaycode.html?random="
			+ Math.round(Math.random() * 100);
	var param = {
		fid : fid
	};
	jQuery.post(url,param,function(result){
		
		if (result.code == 1) {
			util.showerrortips("", result.msg);
			} else if (result.code == 0) {
				util.showerrortips("", result.msg, {
					okbtn : function() {
						location.reload(true);
					}
				});
			}
   },"json") ;
}

function submitCancel(fid) {
	var url = "/json/payCode/cancelPaycode.html?random="
			+ Math.round(Math.random() * 100);
	var param = {
		fid : fid
	};
	jQuery.post(url,param,function(result){
	
		if (result.code == 1) {
			util.showerrortips("", result.msg);
			} else if (result.code == 0) {
				util.showerrortips("", result.msg, {
					okbtn : function() {
						location.reload(true);
					}
				});
			}
   },"json") ;
}