var certification = {
	submitRealCertificationForm : function() {
		var realname = document.getElementById("bindrealinfo-realname").value;
		var address = document.getElementById("bindrealinfo-address").value;
		var identitytype = document.getElementById("bindrealinfo-identitytype").value;
		var identityno = document.getElementById("bindrealinfo-identityno").value;
		var ckinfo = document.getElementById("bindrealinfo-ckinfo").checked;
		var desc = '';
		// 验证是否同意
		if (!ckinfo) {
			desc = "请确认个人信息的合法性！";
			util.showerrortips('certificationinfo-errortips', desc);
			return;
		}
		//验证姓名
		if (realname.length > 6 || realname.trim() == "") {
			desc = '请输入合法的真实姓名!';
			util.showerrortips('certificationinfo-errortips', desc);
			return;
		}
		// 验证证件类型
		if (identitytype != 0) {
			desc = "请选择证件类型不合法！";
			util.showerrortips('certificationinfo-errortips', desc);
			return;
		}
		// 验证身份证
		var isIDCard = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
		var re = new RegExp(isIDCard);
		if(identityno.length ==0){
			util.showerrortips('certificationinfo-errortips', language["comm.error.tips.117"]);
			return false;
		}
		if (!re.test(identityno)) {
			desc = "请确认证件类型";
			util.showerrortips('certificationinfo-errortips', language["comm.error.tips.119"]);
			return false;
		}
		// 隐藏错误消息
		util.hideerrortips('certificationinfo-errortips');
		// 提交信息
		
		var url = "/user/validateIdentity.html?random=" + Math.round(Math.random() * 100);
		var param = {
			realName : realname,
			identityType : identitytype,
			identityNo : identityno
		};
		jQuery.post(url, param, function(data) {
			if (data.code == 0) {
				util.showerrortips('certificationinfo-errortips', data.msg);
				location.reload();
			} else {
				util.showerrortips('certificationinfo-errortips', data.msg);
			}
		}, "json");

	},
};

$(function() {
	$("#bindrealinfo-Btn").on("click", function() {
		certification.submitRealCertificationForm(false);
	});
	$("#bindrealinfo").on("hide.bs.modal",function(){
		$("#bindrealinfo-realname").val("");
		$("#bindrealinfo-identityno").val("");
		$("#bindrealinfo-ckinfo").prop("checked",false);
		
	});
});
