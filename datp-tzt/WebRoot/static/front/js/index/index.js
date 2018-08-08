var count1=0;count2=0,count3=0,count4=0;
var login={
		indexLoginOnblur:function () {
		    var uName = document.getElementById("indexLoginName").value;
		    if(uName==""){
		    	util.showerrortips("indexLoginTips","用户名为空！");
		        return
		    }
		    if (!util.checkEmail(uName) && !util.checkMobile(uName)) {
		        util.showerrortips("indexLoginTips",language["comm.error.tips.1"]);
		    } else {
		    	util.hideerrortips("indexLoginTips");
		    }
		},
		loginIndexSubmit:function () {
		    util.hideerrortips("indexLoginTips");
		    var url = "/user/login/index.html?random=" + Math.round(Math.random() * 100);
		    var uName = document.getElementById("indexLoginName").value;
		    var pWord = document.getElementById("indexLoginPwd").value;
		    var longLogin = 0;
		    if(pWord == ""||uName==""){
		    	util.showerrortips("indexLoginTips","用户名或密码不能为空！");
		        return;
		    }
		    if (util.checkEmail(uName)) {
		        longLogin = 1;
		    }
		    if (!util.checkEmail(uName) && !util.checkMobile(uName)) {
		    	util.showerrortips("indexLoginTips", language["comm.error.tips.1"]);
		        return
		    }
		    if (pWord == "") {
		    	util.showerrortips("indexLoginTips", language["comm.error.tips.2"]);
		        return;
		    } else if (pWord.length < 6) {
		    	util.showerrortips("indexLoginTips", language["comm.error.tips.3"]);
		        return;
		    }
		    var param = {
		        loginName: uName,
		        password: pWord,
		        type: longLogin
		    };
		    jQuery.post(url, param, function (data) {
		        if (data.code == 0) {
		            if (document.getElementById("forwardUrl") != null && document.getElementById("forwardUrl").value != "") {
		                var forward = document.getElementById("forwardUrl").value;
		                forward = decodeURI(forward);
		                window.location.href = forward;
		            } else {
		                var whref = document.location.href;
		                if (whref.indexOf("#") != -1) {
		                    whref = whref.substring(0, whref.indexOf("#"));
		                }
		                window.location.href = whref;
		            }
		        } else if (data.code == -1) {
		        	util.showerrortips("indexLoginTips", data.msg);
		            document.getElementById("indexLoginPwd").value = "";
		        }
		    },"json");
		},
		refreshMarket:function(str){
			var str=str;
			var totalAmt="";
			var price="";
			var total="";
			var rose="";
			switch(str){
			case "price":
				count2=0;count3=0,count4=0;
				if((count1%2)==0){
					price='desc';
					$("#_price").children(".cagret-down").removeClass("cagret-down").addClass("cagret-down-red");
					$("#_price").children(".cagret-up-red").removeClass("cagret-up-red").addClass("cagret-up");
					
					$("#_totalAmt").children(".cagret-up-red").removeClass("cagret-up-red").addClass("cagret-up");
					$("#_totalAmt").children(".cagret-down-red").removeClass("cagret-down-red").addClass("cagret-down");
					
					$("#_total").children(".cagret-up-red").removeClass("cagret-up-red").addClass("cagret-up");
					$("#_total").children(".cagret-down-red").removeClass("cagret-down-red").addClass("cagret-down");
					
					$("#_rose").children(".cagret-up-red").removeClass("cagret-up-red").addClass("cagret-up");
					$("#_rose").children(".cagret-down-red").removeClass("cagret-down-red").addClass("cagret-down");
				}else{
					price='aes';
					$("#_price").children(".cagret-up").removeClass("cagret-up").addClass("cagret-up-red");
					$("#_price").children(".cagret-down-red").removeClass("cagret-down-red").addClass("cagret-down");
				}				
				$("#hidCol").val("price");
				$("#hidOrder").val(price);
				count1++;
				break;
			case "totalAmt":
				count1=0;count3=0,count4=0;
				if((count2%2)==0){
					totalAmt='desc';
					$("#_totalAmt").children(".cagret-down").removeClass("cagret-down").addClass("cagret-down-red");
					$("#_totalAmt").children(".cagret-up-red").removeClass("cagret-up-red").addClass("cagret-up");
					
					$("#_price").children(".cagret-down-red").removeClass("cagret-down-red").addClass("cagret-down");
					$("#_price").children(".cagret-up-red").removeClass("cagret-up-red").addClass("cagret-up");
					
					
					$("#_total").children(".cagret-up-red").removeClass("cagret-up-red").addClass("cagret-up");
					$("#_total").children(".cagret-down-red").removeClass("cagret-down-red").addClass("cagret-down");
					
					$("#_rose").children(".cagret-up-red").removeClass("cagret-up-red").addClass("cagret-up");
					$("#_rose").children(".cagret-down-red").removeClass("cagret-down-red").addClass("cagret-down");
					
					$("#hidCol").val("totalAmt");
					$("#hidOrder").val("desc");
				}else{
					totalAmt='aes';
					$("#_totalAmt").children(".cagret-up").removeClass("cagret-up").addClass("cagret-up-red");
					$("#_totalAmt").children(".cagret-down-red").removeClass("cagret-down-red").addClass("cagret-down");
				}				
				$("#hidCol").val("totalAmt");
				$("#hidOrder").val(totalAmt);
				count2++;
				break;
			case "total":
				count1=0;count2=0,count4=0;
				if((count3%2)==0){
					total='desc';
					$("#_total").children(".cagret-down").removeClass("cagret-down").addClass("cagret-down-red");
					$("#_total").children(".cagret-up-red").removeClass("cagret-up-red").addClass("cagret-up");
					
					$("#_price").children(".cagret-down-red").removeClass("cagret-down-red").addClass("cagret-down");
					$("#_price").children(".cagret-up-red").removeClass("cagret-up-red").addClass("cagret-up");
					
					
					$("#_totalAmt").children(".cagret-up-red").removeClass("cagret-up-red").addClass("cagret-up");
					$("#_totalAmt").children(".cagret-down-red").removeClass("cagret-down-red").addClass("cagret-down");
					
					$("#_rose").children(".cagret-up-red").removeClass("cagret-up-red").addClass("cagret-up");
					$("#_rose").children(".cagret-down-red").removeClass("cagret-down-red").addClass("cagret-down");
					
				}else{
					total='aes';
					$("#_total").children(".cagret-up").removeClass("cagret-up").addClass("cagret-up-red");
					$("#_total").children(".cagret-down-red").removeClass("cagret-down-red").addClass("cagret-down");
				}			
				$("#hidCol").val("total");
				$("#hidOrder").val(total);
				count3++;
				break;
			case "rose":
				count1=0;count2=0,count3=0;
				if((count4%2)==0){
					rose='desc';
					$("#_rose").children(".cagret-down").removeClass("cagret-down").addClass("cagret-down-red");
					$("#_rose").children(".cagret-up-red").removeClass("cagret-up-red").addClass("cagret-up");
					
					$("#_price").children(".cagret-down-red").removeClass("cagret-down-red").addClass("cagret-down");
					$("#_price").children(".cagret-up-red").removeClass("cagret-up-red").addClass("cagret-up");
					
					
					$("#_totalAmt").children(".cagret-up-red").removeClass("cagret-up-red").addClass("cagret-up");
					$("#_totalAmt").children(".cagret-down-red").removeClass("cagret-down-red").addClass("cagret-down");
					
					$("#_total").children(".cagret-up-red").removeClass("cagret-up-red").addClass("cagret-up");
					$("#_total").children(".cagret-down-red").removeClass("cagret-down-red").addClass("cagret-down");
					
				}else{
					rose='aes';
					$("#_rose").children(".cagret-up").removeClass("cagret-up").addClass("cagret-up-red");
					$("#_rose").children(".cagret-down-red").removeClass("cagret-down-red").addClass("cagret-down")
					
				}
				$("#hidCol").val("rose");
				$("#hidOrder").val(rose);
				count4++;
				break;
			}
			var url="/real/indexmarket.html?random=" + Math.round(Math.random() * 100);
			$.post(url,{price:price,totalAmt:totalAmt,total:total,rose:rose},function(data){
				$(".td1").remove();
				$.each(data.result,function(key,value){
					$("#row-value").append("<tr class='td1'><td class='col-xs-2' style='vertical-align: middle;'><div class='col-xs-3'><span> <i class='coin-logo' style='background-image: url("+value.url+");background-size:100% 100%;vertical-align: middle;text-align:right;'></i></span></div><div class='col-xs-9' style='text-align:left;padding-top:4px;'>"+value.name+"</div></td>" +
							"<td class='col-xs-1' style='vertical-align: middle;'>"+"￥"+Number(value.price)+"</td>"+
							"<td class='col-xs-2' style='vertical-align: middle;'>"+"￥"+Number(value.totalAmt)+"</td>"+
							"<td class='col-xs-2' style='vertical-align: middle;'>"+value.total+"</td>"+
							"<td class='col-xs-1' id='"+value.id+"_rose' style='vertical-align: middle;'>"+value.rose+"</td>"+
							"<td class='col-xs-2 text-center'><div id='"+value.name+"_plot'	style='width: 100%; height: 40px; display: inline-block; float: left;'></div></td>"+
							"<td class='col-xs-1' style='vertical-align: middle;'><a class='btn market-trading' href='/trade/coin.html?coinType="+value.id+"&tradeType=0'>去交易</a></td>"+
							"</tr>");
					$.plot($("#"+value.name+"_plot"), [{shadowSize:0, data:value.data}],{ grid: { borderWidth: 0}, xaxis: { mode: "time", ticks: false}, yaxis : {tickDecimals: 0, ticks: false},colors:['#f99f83']});
					if(Number(value.rose)>=0){
						$("#"+value.id+"_rose").removeClass("text-success").addClass("text-danger").html('+'+value.rose+'%');
					}else{
						$("#"+value.id+"_rose").removeClass("text-danger").addClass("text-success").html(value.rose+'%');
					}
				});
				$("#fenye").html(data.pagin);
			});
		},
		loginerror:function(){
			var errormsg =$("#errormsg").val();
			if(errormsg!="" && errormsg!="/"){
				util.showerrortips("", errormsg);
			}
		}
};
$(function(){
	$("#btnSearch").on("click",function(){
		searchfirst();
	});
	var res=$("#isHiddenDeal").val();
	if(res=="false"){
		login.refreshMarket();	
	}
	$("#searchCon").val("");
	
	login.loginerror();
	$("#indexLoginPwd").on("focus",function(){
		login.indexLoginOnblur();
		util.callbackEnter(login.loginIndexSubmit);
	});
	$("#loginbtn").on("click",function(){
		login.loginIndexSubmit();
	});
	$("#_price").on("click",function(){
		login.refreshMarket('price');
	});
	$("#_totalAmt").on("click",function(){
		login.refreshMarket('totalAmt');
	});
	$("#_total").on("click",function(){
		login.refreshMarket('total');
	});
	$("#_rose").on("click",function(){
		login.refreshMarket('rose');
	});
	
	/*login.refreshMarket();*/
	$("#indexLoginName").val("");
	$("#indexLoginPwd").val("");
});

function lottery(){
	    var url = "/gamecenter/golottery.html";
	    jQuery.post(url, null, function (data) {
	        if (data.code == 0) {
	            if (data.qty ==0) {
	            	util.showerrortips("", "很遗憾，没有中奖，再接再厉！", {
						okbtn : function() {
							location.reload(true);
						}
					});
	            } else {
	            	$("#showQty").html(data.qty);
	            	$("#showName").html(data.name);
	            	document.getElementById("showDiv").style.display = "block";
	            }
	        } else if (data.code == -1) {
	        	util.showerrortips("",data.msg, {
					okbtn : function() {
						location.reload(true);
					}
				});
	        }
	    },"json");
	
}
function searchfirst(){
	var con=$.trim($("#searchCon").val());
	firstAjaxPage(1,con);
}
function firstAjaxPage(current,con){
	var url="/real/indexmarket.html?random=" + Math.round(Math.random() * 100);
	var hidCol=$("#hidCol").val();
	var hidOrder=$("#hidOrder").val();
	var totalAmt="";
	var price="";
	var total="";
	var rose="";
	switch(hidCol){
	case "price":
			price=hidOrder;
		break;
	case "totalAmt":
		totalAmt=hidOrder;
		break;
	case "total":
		total=hidOrder;
		break;
	case "rose":
		rose=hidOrder;
		break;
	}
	$.ajax({
		url:url,
		data:{
			current:current,
			price:price,
			totalAmt:totalAmt,
			total:total,
			rose:rose,
			keyWord:con
		},
		success: function(data){
			$(".td1").remove();
			$.each(data.result,function(key,value){
				$("#row-value").append("<tr class='td1'><td class='col-xs-2' style='vertical-align: middle;'><div class='col-xs-3'><span> <i class='coin-logo' style='background-image: url("+value.url+");background-size:100% 100%;vertical-align: middle;text-align:right;'></i></span></div><div class='col-xs-9' style='text-align:left;padding-top:4px;'>"+value.name+"</div></td>" +
						"<td class='col-xs-1' style='vertical-align: middle;'>"+"￥"+Number(value.price)+"</td>"+
						"<td class='col-xs-2' style='vertical-align: middle;'>"+"￥"+Number(value.totalAmt)+"</td>"+
						"<td class='col-xs-2' style='vertical-align: middle;'>"+value.total+"</td>"+
						"<td class='col-xs-1' id='"+value.id+"_rose' style='vertical-align: middle;'>"+value.rose+"</td>"+
						"<td class='col-xs-2 text-center'><div id='"+value.name+"_plot'	style='width: 100%; height: 40px; display: inline-block; float: left;'></div></td>"+
						"<td class='col-xs-1' style='vertical-align: middle;'><a class='btn market-trading' href='/trade/coin.html?coinType="+value.id+"&tradeType=0'>去交易</a></td>"+
						"</tr>");
				$.plot($("#"+value.name+"_plot"), [{shadowSize:0, data:value.data}],{ grid: { borderWidth: 0}, xaxis: { mode: "time", ticks: false}, yaxis : {tickDecimals: 0, ticks: false},colors:['#f99f83']});
				if(Number(value.rose)>=0){
					$("#"+value.id+"_rose").removeClass("text-success").addClass("text-danger").html('+'+value.rose+'%');
				}else{
					$("#"+value.id+"_rose").removeClass("text-danger").addClass("text-success").html(value.rose+'%');
				}
			});
			$("#fenye").html(data.pagin);
	    }
	});
}



