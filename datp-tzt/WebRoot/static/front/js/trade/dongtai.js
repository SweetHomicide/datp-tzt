$(function(){	
    $("#div3").hover(function(){ 
    	var str=$("#div1").html();
		$("#div2").html(str);
    	$("#icon1").addClass("arrow-up");
    	$("#icon1").removeClass("arrow-down");
        $("#div4").show();
    },function(){ 
    	$("#icon1").addClass("arrow-down");
    	$("#icon1").removeClass("arrow-up");
    	$("#div4").hide();
    }) 
    
    $("#div-checkCoin").hover(function(){ 
    	var str=$("#divx1").html();
		$("#divx2").html(str);
    	$("#icon1").addClass("arrow-up");
    	$("#icon1").removeClass("arrow-down");
        $("#divx4").show();
    },function(){ 
    	$("#icon1").addClass("arrow-down");
    	$("#icon1").removeClass("arrow-up");
    	$("#divx4").hide();
    }) 
    
    $("#divy3").hover(function(){
    	var str=$("#divy1").html(); 
    	$("input").blur();
		$("#divy2").html(str);
    	$("#icon1").addClass("arrow-up");
    	$("#icon1").removeClass("arrow-down");
        $("#divy4").show();
    },function(){ 
    	$("#icon1").addClass("arrow-down");
    	$("#icon1").removeClass("arrow-up");
    	$("#divy4").hide();
    }) 
    
    
    $("#divz3").hover(function(){ 
    	var str=$("#divy1").html();
		$("#divz2").html(str);
    	$("#icon1").addClass("arrow-up");
    	$("#icon1").removeClass("arrow-down");
        $("#divz4").show();
    },function(){ 
    	$("#icon1").addClass("arrow-down");
    	$("#icon1").removeClass("arrow-up");
    	$("#divz4").hide();
    })
    
    $("#divxx3").hover(function(){ 
    	var str=$("#divxx1").html();
		$("#divxx2").html(str);
    	$("#iconxx1").addClass("arrow-up");
    	$("#iconxx1").removeClass("arrow-down");
        $("#divxx4").show();
    },function(){ 
    	$("#iconxx1").addClass("arrow-down");
    	$("#iconxx1").removeClass("arrow-up");
    	$("#divxx4").hide();
    }) 
    
    $("#divxy3").hover(function(){ 
    	var str=$("#divxy1").html();
		$("#divxy2").html(str);
    	$("#iconxy1").addClass("arrow-up");
    	$("#iconxy1").removeClass("arrow-down");
        $("#div4xy").show();
    },function(){ 
    	$("#iconxy1").addClass("arrow-down");
    	$("#iconxy1").removeClass("arrow-up");
    	$("#div4xy").hide();
    }) 
//    介绍
    $("#popupjieshao").hover(function(){ 
    	var str=$("#divjs").html();
		$("#divz2").html(str);
    	$("#icon1").addClass("arrow-up");
    	$("#icon1").removeClass("arrow-down");
        $("#Popup").show();
    },function(){ 
    	$("#icon1").addClass("arrow-down");
    	$("#icon1").removeClass("arrow-up");
    	$("#Popup").hide();
    });
    //虚拟币明细
    $("#detail_coinname").hover(function(){ 
    	var str=$("#hid_detail_coinName").html();
		$("#pop_content").html(str);
    	$("#icon1").addClass("arrow-up");
    	$("#icon1").removeClass("arrow-down");
        $("#popup").show();
    },function(){ 
    	$("#icon1").addClass("arrow-down");
    	$("#icon1").removeClass("arrow-up");
    	$("#popup").hide();
    });
    
});

function ajaxPage(page){
	var hidlog=$("#hidlog").val();
		$.ajax({
            type: "GET",
            url: "/trade/cointype.html?radom="+Math.round(Math.random() * 100),
            data:{
            	"currentPage":page,
            	"hidlog":hidlog
            },
            dataType: "json",
            success: function(data){
            	$("#pagex").html(data.page);
            	var strs=data.result;
            	$("#coinNames").html("");
	            	$("#coinNames").html(strs);
	            	var str=$("#div1").html();
	        		$("#div2").html(str);
	        		
            },
            error: function (msg) {
                alert(msg);
            }
        });
}
/**
 * 交易中心搜索
 * @returns
 */
function searchType(){
		var str=$.trim($(".searchname").val());
		$("#hidlog").val(str);
		ajaxPage(1);
}
function ajaxPage1(page){
	var hidlog=$("#hidlog").val();
		$.ajax({
            type: "GET",
            url: "/account/cointype.html?radom="+Math.round(Math.random() * 100),
            data:{
            	"currentPage":page,
            	"hidlog":hidlog
            },
            dataType: "json",
            success: function(data){
            	$("#pagex1").html(data.page);
            	var strs=data.result;
            	$("#coinNamex1").html("");
	            	$("#coinNamex1").html(strs);
	            	var str=$("#divx1").html();
	        		$("#divx2").html(str);
            },
            error: function (msg) {
                alert(msg);
            }
        });
}

/**
 * 充币搜索
 * @returns
 */
function searchType1(){
	var str=$.trim($(".searchname").val());
	$("#hidlog").val(str);
	ajaxPage1(1);
}

function ajaxPage2(page){
	var hidlog=$("#hidlog").val();
		$.ajax({
            type: "GET",
            url: "/account/cointype1.html?radom="+Math.round(Math.random() * 100),
            data:{
            	"currentPage":page,
            	"hidlog":hidlog
            },
            dataType: "json",
            success: function(data){
            	$("#pagey").html(data.page);
            	var strs=data.result;
            	$("#coinNamey1").html("");
	            	$("#coinNamey1").html(strs);
	            	var str=$("#divy1").html();
	        		$("#divy2").html(str);
            },
            error: function (msg) {
                alert(msg);
            }
        });
}
/**
 * 提币搜索操作
 * @returns
 */
function searchType2(){
	var str=$.trim($(".searchname").val());
	$("#hidlog").val(str);
	ajaxPage2(1);
}


function ajaxPage3(page){
	var hidlog=$("#hidlog").val();
		$.ajax({
            type: "GET",
            url: "/financial/accountcointype1.html?radom="+Math.round(Math.random() * 100),
            data:{
            	"currentPage1":page,
            	"hidlog":hidlog
            },
            dataType: "json",
		    success : function(data) {
				$("#pagexx").html(data.page);
				var strs = data.result;
				$("#coinNamexx1").html("");
				$("#coinNamexx1").html(strs);
				var str = $("#divxx1").html();
				$("#divxx2").html(str);
		    },
            error: function (msg) {
                alert(msg);
            }
        });
}
/**
 * 资金账号虚拟币搜索
 * @returns
 */
function searchType3(){
	var str=$.trim($(".searchname").val());
	$("#hidlog").val(str);
	ajaxPage3(1);
}

function ajaxPage4(page){
	var hidlog=$("#hidlog").val();
	var count=$("#count").val();
		$.ajax({
            type: "GET",
            url: "/exchange/index1.html?radom="+Math.round(Math.random() * 100),
            data:{
            	"currentPage":page,
            	"hidlog":hidlog,
            	count:count
            },
            dataType: "json",
		    success : function(data) {
				$("#pagex").html(data.page);
				var strs = data.result;
				$("#coinNamexy1").html("");
				$("#coinNamexy1").html(strs);
				var str = $("#divxy1").html();
				$("#divxy2").html(str);
		    },
            error: function (msg) {
                alert(msg);
            }
        });
}

/**
 * 兑换中心搜索
 * @returns
 */
function searchType4(){
	var str=$.trim($(".searchname").val());
	$("#hidlog").val(str);
	ajaxPage4(1);
}

function ajaxPage5(page){
	var hidlog=$("#hidlog").val();
	var count=$("#count").val();
		$.ajax({
            type: "GET",
            url: "/about/index2.html?radom="+Math.round(Math.random() * 100),
            data:{
            	"currentPage":page,
            	"hidlog":hidlog,
            	"count":count
            },
            dataType: "json",
            success: function(data){
            	$("#pagez").html(data.page);
            	var result=data.result;
            	$("#coinNamexy1").html("");
	            $("#coinNamexy1").html(result);
	            var str1=$("#coinNamexy1").html();
	            $("#divjs").html(str1);
	        	var str = $("#divjs").html();
	        	$("#divz2").html(str1);
            },
            error: function (msg) {
                alert(msg);
            }
        });
}
/**
 * 交易中心介绍搜索
 * @returns
 */
function introduceSearchType(){
	var str=$.trim($(".searchname").val());
	$("#hidlog").val(str);
	ajaxPage5(1);
}

function ajaxPage6(page){
	var hidlog=$("#hidlog").val();
	/*var count=$("#count").val();*/
		$.ajax({
            type: "GET",
            url: "/account/recordCointype.html?isRMB=1&radom="+Math.round(Math.random() * 100),
            data:{
            	"currentPage":page,
            	"hidlog":hidlog
            },
            dataType: "json",
            success: function(data){
            	$("#page").html(data.page);
            	var result=data.result;
            	$("#detail_coinName").html("");
	            $("#detail_coinName").html(result);
	            var str1=$("#detail_coinName").html();
	            $("#pop_content").html(str1);
            },
            error: function (msg) {
                alert(msg);
            }
        });
}
/**
 * 资金帐号虚拟币搜索
 * @returns
 */
function detailVirulCoinSearchType(){
	var str=$.trim($(".searchname").val());
	$("#hidlog").val(str);
	ajaxPage6(1);
}
