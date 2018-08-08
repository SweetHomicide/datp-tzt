function submitExForm() {
	var amount = util.trim(document.getElementById("amount").value);
	var tradePwd = util.trim(document.getElementById("tradePwd").value);
	var fid = util.trim(document.getElementById("fid").value);
	var reg = new RegExp("^[0-9]");
	if (!reg.test(amount)) {
		util.showerrortips("exerrortips","请输入兑换数量");
		return;
	} else {
		util.hideerrortips("exerrortips");
	}
	
	if (parseFloat(amount) < 1) {
		util.showerrortips("exerrortips", "最小兑换数量为1个");
		return;
	} else {
		util.hideerrortips("exerrortips");
	}
	
	if (tradePwd == "") {
		util.showerrortips("exerrortips", "交易密码不能为空");
		return;
	} else {
		util.hideerrortips("exerrortips");
	}
	
	var url = "/json/ex/submitEx.html?random="
			+ Math.round(Math.random() * 100);
	var param = {
		tradePwd : tradePwd,
		buyAmount : amount,
		fid:fid
	};
	$.post(url, param, function(result) {
		if (result != null) {
			if (result.code < 0) {
				util.showerrortips("exerrortips", result.msg);
			} else if (result.code == 0) {
				document.getElementById("exButton").disabled = "true";
				util.showerrortips("", "兑换成功 ！", {
					okbtn : function() {
						$('#alertTips').modal('hide');
						location.reload(true);
					}
				});
			} else {
				util.hideerrortips("exerrortips");
			}
		}
	});
};

function calculateRate() {
	var amount = $("#amount").val();
	var subRate = $("#subRate").val();
	if (amount == "") {
		amount = 0;
	}
	var amt = util.moneyformat(util.accMul(amount, subRate), 4);
	$("#free").html(amt);
};

function pageForEx(page)
{
	var searchName=document.getElementById('searchname').value;
	location.href='/exchange/index.html?type=0&searchName='+searchName+'&currentPage='+page+'';
	//exchange/index.html?type=0&
	
}
function testApi()
{
	  $.ajax({
          url: "/appApi_ex.html",
          type: "post",
          dataType: "json",
          data: { action: "getExLogs", fid: "402880c959be781a0159bea3fe8d0fa4",currentPage:"1" },
          async: false,
          success: function (data) {


          }
      })
}
$(function() {
	$("#amount").on("keypress", function(event) {
		return util.VerifyKeypress(this, event, 2);
	}).on("keyup", function() {
		calculateRate();
	});
	
	$(".recordtitle").on("click", function() {
		util.recordTab($(this));
	});
	
/*	$("#amount").val(0);*/
	$("#free").html("0");
});