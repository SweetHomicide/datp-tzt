var login = {
	loginNameOnblur : function() {
		var uName = document.getElementById("login-account").value;
		if (!util.checkEmail(uName) && !util.checkMobile(uName)) {
			util.showerrortips("login-errortips", language["comm.error.tips.1"]);
		} else {
			util.hideerrortips("login-errortips");
		}
	},
	checkLoginUserName : function() {
		var uName = document.getElementById("login-account").value;
		if (uName == "") {
			util.showerrortips("login-errortips", language["comm.error.tips.5"]);
			return false;
		} else if (!util.checkEmail(uName) && !util.checkMobile(uName)) {
			util.showerrortips("login-errortips", language["comm.error.tips.1"]);
			return false;
		}
		util.hideerrortips("login-errortips");
		return true;
	},
	checkLoginPassword : function() {
		var password = document.getElementById("login-password").value;
		if (password == "") {
			util.showerrortips("login-errortips", language["comm.error.tips.2"]);
			return false;
		} else if (password.length < 6) {
			util.showerrortips("login-errortips", language["comm.error.tips.3"]);
			return false;
		}
		util.hideerrortips("login-errortips");
		return true;
	},
	loginSubmit : function() {
		if (login.checkLoginUserName() && login.checkLoginPassword()) {
			var url = "/user/login/index.html?random=" + Math.round(Math.random() * 100);
			var uName = document.getElementById("login-account").value;
			var pWord = document.getElementById("login-password").value;
			var longLogin = 0;
			if (util.checkEmail(uName)) {
				longLogin = 1;
			}
			var forwardUrl = "";
			if (document.getElementById("forwardUrl") != null) {
				forwardUrl = document.getElementById("forwardUrl").value;
			}
			var param = {
				loginName : uName,
				password : pWord,
				type : longLogin
			};
			$.post(url, param, function(data) {
				if (data.code == -1) {
					util.showerrortips("login-errortips", data.msg);
					try{
						document.getElementById("loginPassword").value = "";
					}catch(e){}
				} else {
					if (forwardUrl.trim() == "") {
						window.location.href = "/index.html";
					} else {
						window.location.href = forwardUrl;
					}
				}
			}, "json");
		}
	}
};
$(function() {
	$("#login-password").on("focus", function() {
		login.loginNameOnblur();
		util.callbackEnter(login.loginSubmit);
	});
	$("#login-submit").on("click", function() {
		login.loginSubmit();
	});
});