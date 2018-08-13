var reg = {
	checkRegUserName : function() {
		var regType = document.getElementById("regType").value;
		var regUserName = "";
		var desc = '';
		if (regType == 0) {
			// 验证手机号
			regUserName = util
					.trim(document.getElementById("register-phone").value);
			if (regUserName.indexOf(" ") > -1) {
				desc = language["comm.error.tips.8"];
			} else {
				if (regUserName == '') {
					desc = language["comm.error.tips.9"];
				} else if (!util.checkMobile(regUserName)) {
					desc = language["comm.error.tips.10"];
				}
			}
		} else {
			// 验证邮箱
			regUserName = util
					.trim(document.getElementById("register-email").value);
			if (regUserName.indexOf(" ") > -1) {
				desc = language["comm.error.tips.11"];
			} else {
				if (regUserName == '') {
					desc = language["comm.error.tips.12"];
				} else if (!util.checkEmail(regUserName)) {
					desc = language["comm.error.tips.13"];
				} else if (new RegExp("[,]", "g").test(regUserName)) {
					desc = language["comm.error.tips.14"];
				} else if (regUserName.length > 100) {
					desc = language["comm.error.tips.15"];
				}
			}
		}
		if (desc != "") {
			util.showerrortips("register-errortips", desc);
			return false;
		} else {
			util.hideerrortips("register-errortips");
		}

		var url = "/user/reg/chcekregname.html?name=" + encodeURI(regUserName)
				+ "&type=" + regType + "&random="
				+ Math.round(Math.random() * 100);
		$.get(url, null, function(data) {
			if (data.code == -1) {
				util.showerrortips("register-errortips", data.msg);
				return;
			} else {
				util.hideerrortips("register-errortips");
			}
		}, "json");
	},
	checkPassword : function() {
		var pwd = util.trim(document.getElementById("register-password").value);
		var desc = util.isPassword(pwd);
		if (desc != "") {
			util.showerrortips("register-errortips", desc);
			return false;
		} else {
			util.hideerrortips("register-errortips");
		}
		return true;
	},
	checkRePassword : function() {
		var pwd = util.trim(document.getElementById("register-password").value);
		var rePwd = util.trim(document.getElementById("register-confirmpassword").value);
		if(rePwd == ""){
			util.showerrortips("register-errortips", "确认密码不能为空");
			return false;
		}
		if (pwd !== rePwd) {
			desc = language["comm.error.tips.109"];
			util.showerrortips("register-errortips", desc);
			return false;
		}
		var desc = util.isPassword(pwd);
		if (desc != "") {
			util.showerrortips("register-errortips", desc);
			return false;
		} else {
			util.hideerrortips("register-errortips");
		}
		return true;
	},

	checkRegUserNameNoJquery : function() {
		var regType = document.getElementById("regType").value;
		var regUserName = "";
		var desc = '';
		if (regType == 0) {
			// 验证手机号
			regUserName = util
					.trim(document.getElementById("register-phone").value);
			if (regUserName.indexOf(" ") > -1) {
				desc = language["comm.error.tips.8"];
			} else {
				if (regUserName == '') {
					desc = language["comm.error.tips.9"];
				} else if (!util.checkMobile(regUserName)) {
					desc = language["comm.error.tips.10"];
				}
			}
		} else {
			// 验证邮箱
			regUserName = util
					.trim(document.getElementById("register-email").value);
			if (regUserName.indexOf(" ") > -1) {
				desc = language["comm.error.tips.11"];
			} else {
				if (regUserName == '') {
					desc = language["comm.error.tips.12"];
				} else if (!util.checkEmail(regUserName)) {
					desc = language["comm.error.tips.13"];
				} else if (new RegExp("[,]", "g").test(regUserName)) {
					desc = language["comm.error.tips.14"];
				} else if (regUserName.length > 100) {
					desc = language["comm.error.tips.15"];
				}
			}
		}
		if (desc != "") {
			util.showerrortips("register-errortips", desc);
			return false;
		} else {
			util.hideerrortips("register-errortips");
			return true;
		}
	},
	regSubmit : function() {
		/*
		 * var areaCode =
		 * document.getElementById("register-phone-areacode").innerHTML;
		 */
		var areaCode = "+86";
		var regUserName = "";
		var regType = document.getElementById("regType").value;
		if (regType == 0) {
			regUserName = util
					.trim(document.getElementById("register-phone").value);
			desc="请输入手机号码！";
		} else {
			regUserName = util
					.trim(document.getElementById("register-email").value);
			desc="请输入邮箱号码！";
		}
		if(regUserName==""){
			util.showerrortips("register-errortips", desc);
			return;
		}
		// 验证图片验证码
		validateCode = document.getElementById("register-imgcode").value;
		if (validateCode === "") {
			util.showerrortips("register-errortips", "请输入图片验证码");
			return;
		}
		
		// 验证图片验证码
		validateCode = document.getElementById("register-imgcode").value;
		if (validateCode === "") {
			util.showerrortips("register-errortips", "请输入图片验证码");
			return;
		}
		
		// 验证短信或邮箱验证码
		var regPhoneCode = 0;
		if (regType == 0) {
			regPhoneCode = document.getElementById("register-msgcode").value;
			if (regPhoneCode == "") {
				util.showerrortips("register-errortips", "请输入短信验证码");
				return;
			}
		}
		var regEmailCode = 0;
		if (regType == 1) {
			regEmailCode = document.getElementById("register-email-code").value;
			if (regEmailCode == "") {
				util.showerrortips("register-errortips", "请输入邮件验证码");
				return;
			}
		}
		
		// 验证密码和确认密码
		var pwd = util.trim(document.getElementById("register-password").value);
		if(!this.checkRegUserNameNoJquery() ){
			//util.showerrortips("register-errortips", language["comm.error.tips.1"]);
			return false;
		}
		if(!this.checkPassword()){
			//util.showerrortips("register-errortips", language["comm.error.tips.19"]);
			return false;
		}	
		if(!this.checkRePassword()){
			//util.showerrortips("register-errortips",language[" comm.error.tips.109"]);
			return false;
		}
		if (!document.getElementById("agree").checked) {
			util.showerrortips("register-errortips",
					language["comm.error.tips.23"]);
			return;
		}
		var intro_user = document.getElementById("register-intro").value;
		var url = "/user/reg/index.html?random="
				+ Math.round(Math.random() * 100);
		var param = {
			regName : regUserName,
			password : pwd,
			regType : regType,
			vcode : validateCode,
			phoneCode : regPhoneCode,
			ecode : regEmailCode,
			areaCode : areaCode,
			intro_user : intro_user,
			validateCode : validateCode
		};
		jQuery.post(url,param,function(data) {
			if (data.code < 0) {
				// 注册失败
				util.showerrortips("register-errortips",data.msg);
				/*if (data.code == -20) {*/
					// document.getElementById("register-imgcode").value
					// = "";
				/*}*/
				$(".btn-imgcode").click();
			} else {
				util.showerrortips("register-errortips",language["comm.succ.register"]);
				window.setTimeout("window.location='/user/realCertification.html'",	2000);
			}
		}, "json");

	},
/*
 * areaCodeChange : function(ele, setEle) { var code = $(ele).val(); $("#" +
 * setEle).html("+" + code); },
 */
};
$(function() {
	$("#register-sendmessage").removeAttr("disabled");
	document.getElementById("agree").checked=false;
	$("#register-phone").val("");
	$("#register-email").val("");
	$("#register-imgcode").val("");
	$("#register-password").val("");
	$("#register-confirmpassword").val("");
	$("#register-intro").val("");
	$("#register-msgcode").val("");
	
	$(".btn-sendmsg").on(
			"click",
			function() {
				// var areacode = $("#register-phone-areacode").html();
				var areacode = "+86";
				var phone = $("#register-phone").val();
				var vcode = $("#register-imgcode").val();
				msg.sendMsgCode($(this).data().msgtype, $(this).data().tipsid,
						this.id, areacode, phone, vcode);
			});
	$(".btn-imgcode").on("click",function() {
				this.src = "/servlet/ValidateImageServlet?r="
						+ Math.round(Math.random() * 100);
	});
	$("#register-phone").on("blur", function() {
		reg.checkRegUserName();
	});
	$("#register-email").on("blur", function() {
		reg.checkRegUserName();
	});
	$("#register-submit").on("click", function() {
		reg.regSubmit();
	});
	$(".register-item").on("click", function() {
		that = $(this);
		var className = that.attr("class");
		if (className.indexOf("active") >= 0) {
			return;
		}

		$(".register-item").removeClass("active");
		that.addClass("active");
		$("." + that.data().show).show();
		$("." + that.data().hide).hide();
		$("#regType").val(that.data().type);
		
		//清空数据
		$("#register-phone").val("");
		$("#register-email").val("");
		$("#register-errortips").text("");
		$("#register-imgcode").val("");
		$("#register-msgcode").val("");
		$("#register-password").val("");
		$("#register-confirmpassword").val("");
		$("#register-intro").val("");
		
		
		
		
		
		
		
	});
	/*
	 * $("#register-areaCode").on("change", function() {
	 * reg.areaCodeChange(this, "register-phone-areacode"); });
	 */
	$(".btn-sendemailcode").on(
			"click",
			function() {
				var address = $("#register-email").val();
				if (address == "") {
					util.showerrortips("register-errortips", "请输入邮箱地址");
					return;
				}
				email.sendcode($(this).data().msgtype, $(this).data().tipsid,
						this.id, address, $("#register-imgcode").val());
			});
});