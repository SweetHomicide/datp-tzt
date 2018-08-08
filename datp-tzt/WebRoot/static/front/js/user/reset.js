var reset = {
	findPassPhone : function(btnele) {
		var phone = $("#reset-phone").val();
		var msgcode = $("#reset-msgcode").val();
		var idcard = $("#reset-idcard").val();
		var idcardno = $("#reset-idcardno").val();
		var imgcode = $("#reset-imgcode").val();
		if (phone == "" || !util.checkMobile(phone)) {
			util.showerrortips("reset-errortips", "请输入正确的手机号码");
			return;
		}
		if (imgcode == "" || imgcode.length != 4) {
			util.showerrortips("reset-errortips", "图片验证码长度必须为4");
			return;
		}
		if (msgcode == "" || !/^[0-9]{6}$/.test(msgcode)) {
			util.showerrortips("reset-errortips", "短信验证码格式有误");
			return;
		}
		/*if (idcardno == "") {
			util.showerrortips("reset-errortips", "请输入证件号码");
			return;
		}*/
		util.hideerrortips("reset-errortips");
		btnele.disabled = true;
		var url = "/validate/resetPhoneValidation.html?random=" + Math.round(Math.random() * 100);
		var param = {
			phone : phone,
			msgcode : msgcode,
			idcard : idcard,
			idcardno : idcardno
		};
		$.post(url, param, function(data) {
			btnele.disabled = false;
			if (data.code < 0) {
				$(".btn-imgcode").click();
				util.showerrortips("reset-errortips", data.msg);
			} else {
				window.location.href = "/validate/resetPwdPhone.html";
			}
		});
	},
	resetPhonePass : function() {
		var regu = /^[0-9]{6}$/;
		var re = new RegExp(regu);
		var totpCode = 0;
		var newPassword = document.getElementById("reset-newpass").value;
		var newPassword2 = document.getElementById("reset-confirmpass").value;
		var msg = util.isPassword(newPassword);
		if (msg != "") {
			util.showerrortips("reset-success-errortips", msg);
			document.getElementById("reset-newpass").value = "";
			return;
		} else {
			util.hideerrortips("reset-success-errortips");
		}
		if (newPassword != newPassword2) {
			util.showerrortips("reset-success-errortips", language["comm.error.tips.21"]);
			return;
		} else {
			util.hideerrortips("reset-success-errortips");
		}

		if (document.getElementById("reset-googlecode") != null) {
			totpCode = util.trim(document.getElementById("reset-googlecode").value);
			if (!re.test(totpCode)) {
				util.showerrortips("reset-success-errortips", language["comm.error.tips.98"]);
				return;
			} else {
				util.hideerrortips("reset-success-errortips");
			}
		}
		var url = "/validate/resetPhonePassword.html?random=" + Math.round(Math.random() * 100);
		var param = {
			totpCode : totpCode,
			newPassword : newPassword,
			newPassword2 : newPassword2
		};
		$.post(url, param, function(data) {
			if (data.code < 0) {
				util.showerrortips("reset-success-errortips", data.msg);
				if (data.code == -3) {
					document.getElementById("reset-confirmpass").value = "";
				}
				if (data.code == -4) {
					document.getElementById("reset-newpass").value = "";
					document.getElementById("reset-confirmpass").value = "";
				}
				if (data.code == -8) {
					document.getElementById("totpCode").value = "";
				}
			} else if (data.code == 0) {
				$("#secondstep").hide();
				$("#successstep").show();
				$("#resetprocess2").removeClass("active");
				$("#resetprocess3").addClass("active");
			}
		});
	},
	findPassEmail : function(btnele) {
		var email = $("#reset-email").val();
		var idcard = $("#reset-idcard").val();
		var idcardno = $("#reset-idcardno").val();
		var imgcode = $("#reset-imgcode").val();
		if (email == "" || !util.checkEmail(email)) {
			util.showerrortips("reset-errortips", "请输入正确的邮箱地址");
			return;
		}
		if (idcardno == "") {
			util.showerrortips("reset-errortips", "请输入正确的证件号码");
			return;
		}
		if (imgcode == "" || imgcode.length != 4) {
			util.showerrortips("reset-errortips", "请输入正确的图片验证码");
			return;
		}
		util.hideerrortips("reset-errortips");
		btnele.disabled = true;
		var url = "/validate/sendEmail.html?random=" + Math.round(Math.random() * 100);
		var param = {
			email : email,
			idcard : idcard,
			idcardno : idcardno,
			imgcode : imgcode
		};
		$.post(url, param, function(data) {
			btnele.disabled = false;
			if (data.code < 0) {
				$(".btn-imgcode").click();
				util.showerrortips("reset-errortips", data.msg);
			} else {
				util.showerrortips("", "邮件发送成功，请点击邮件链接找回密码");
			}
		});
	},
	resetEmailPass : function() {
		var newPassword = document.getElementById("reset-newpass").value;
		var newPassword2 = document.getElementById("reset-confirmpass").value;
		var fid = document.getElementById("fid").value;
		var ev_id = document.getElementById("ev_id").value;
		var newuuid = document.getElementById("newuuid").value;
		var msg = util.isPassword(newPassword);
		if (msg != "") {
			util.showerrortips("reset-success-errortips", msg);
			document.getElementById("reset-newpass").value = "";
			return;
		} else {
			util.hideerrortips("reset-success-errortips");
		}
		if (newPassword != newPassword2) {
			util.showerrortips("reset-success-errortips", language["comm.error.tips.21"]);
			return;
		} else {
			util.hideerrortips("reset-success-errortips");
		}
		var url = "" ;
		if($("#mtype").val()=='0'){
			url = "/validate/resetPassword.html?random=" + Math.round(Math.random() * 100);
		}else{
			url = "/validate/resetPasswordPhone.html?random=" + Math.round(Math.random() * 100);
		}
		var param = {
			newPassword : newPassword,
			newPassword2 : newPassword2,
			fid : fid,
			ev_id : ev_id,
			newuuid : newuuid
		};
		$.post(url, param, function(data) {
			if (data.code < 0) {
				util.showerrortips("reset-success-errortips", data.msg);
				document.getElementById("reset-newpass").value = "";
				document.getElementById("reset-confirmpass").value = "";
			} else if (data.code == 0) {
				$("#secondstep").hide();
				$("#successstep").show();
				$("#resetprocess2").removeClass("active");
				$("#resetprocess3").addClass("active");
			}
		});
	},
	
};
$(function() {
	//清空手机找回密码
	$("#reset-phone").val("");
	$("#reset-imgcode").val("");
	$("#reset-msgcode").val("");
	$("#reset-idcardno").val("");
	$("#reset-errortips").val("");
	//清空邮箱找回密码
	$("#reset-email").val("");
	
	
	//清空设置登录密码
	$("#reset-newpass").val("");
	$("#reset-confirmpass").val("");
	$("#reset-success-errortips").text("");
	
	$(".btn-imgcode").on("click", function() {
		this.src = "/servlet/ValidateImageServlet?r=" + Math.round(Math.random() * 100);
	});
	$(".btn-sendmsg").on("click", function() {
		if (this.id == "reset-sendmessage") {
			var areacode = $("#reset-phone-areacode").html();
			var phone = $("#reset-phone").val();
			msg.sendMsgCode($(this).data().msgtype, $(this).data().tipsid, this.id, areacode, phone,$("#reset-imgcode").val());
		} else {
			msg.sendMsgCode($(this).data().msgtype, $(this).data().tipsid, this.id);
		}
	});
	$("#btn-next").on("click", function() {
		reset.findPassPhone(this);
	});
	$("#btn-success").on("click", function() {
		reset.resetPhonePass(this);
	});
	$("#btn-email-next").on("click", function() {
		reset.findPassEmail(this);
	});
	$("#btn-email-success").on("click", function() {
		reset.resetEmailPass(this);
	});
});