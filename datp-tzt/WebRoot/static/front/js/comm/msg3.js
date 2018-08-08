var msg3= {
	secs : 121,
	msgtype : 1,
	sendMsgCode : function(type, tipElement_id, button_id, areaCode, phone,vcode) {
		var that = this;
		var tipElement = document.getElementById(tipElement_id);
		var button = document.getElementById(button_id);
		var random = document.getElementById("random").value;
		var money =document.getElementById("diyMoney").value;
		if(money==""){
			alert("金额不能为空！");
			return false;
		}
		var realMoney=0;
		if (money.toString().indexOf(".") != -1) {
			realMoney = money.toString().split(".")[0];
			realMoney = money + (random.substring(1));
		} else {
			realMoney = money + (random.substring(1));
		}
		var orderId = document.getElementById("orderId").value;
		var accNo = document.getElementById("accNo").value;
		if (typeof (areaCode) == 'undefined') {
			areaCode = 0;
		}
		if (typeof (phone) == 'undefined') {
			phone = 0;
		} else {
			if (!util.checkMobile(phone)) {
				util.showerrortips(tipElement_id, language["comm.error.tips.10"]);
				return;
			}
		}
		var url = "/account/consumeSMS.html?random=" + Math.round(Math.random() * 100);
		$.post(url, {
			realMoney:realMoney,
			orderId:orderId,
			accNo:accNo,
			phone:phone,
		}, function(data) {
			if (data.code < 0) {  
				util.showerrortips(tipElement_id, data.msg);
				$(".btn-imgcode").click();
			} else if (data.code == 0) {
				util.showerrortips(tipElement_id, "");
				button.disabled = true;
				for ( var num = 1; num <= that.secs; num++) {
					window.setTimeout("msg3.updateNumber(" + num + ",'" + button_id + "',2)", num * 1000);
				}
			}
		}, "json");
	},
	updateNumber : function(num, button_id, isVoice) {
		var button = document.getElementById(button_id);
		if (num == this.secs) {
			button.innerHTML = language["comm.error.tips.33"];
			button.disabled = false;
		} else {
			var printnr = this.secs - num;
			button.innerHTML = language["comm.error.tips.32"].format(printnr);
		}
	}
};
