var question = {
	questionText : function(focusid) {
		var focusblurid = $(focusid);
		var defval = focusblurid.val();
		focusblurid.focus(function() {
			var thisval = $(this).val();
			if (thisval == defval) {
				$(this).val("");
			}
		});
		focusblurid.blur(function() {
			var thisval = $(this).val();
			if (thisval == "") {
				$(this).val(defval);
			}
		});
	},

	submitQuestion : function() {
		var questiontype = document.getElementById("question-type").value;
		var questiondesc = $("#question-desc").val();
		if (util.trim(questiondesc) == "" || questiondesc.length < 10 || questiondesc == language["comm.error.tips.137"]) {
			desc = language["comm.error.tips.137"];
			util.showerrortips("errortips", desc);
			return;
		}
		var url = "/question/submitQuestion.html?random=" + Math.round(Math.random() * 100);
		var param = {
			questiontype : questiontype,
			questiondesc : questiondesc
		};
		jQuery.post(url, param, function(data) {
			if (data.code == 0) {
				util.showerrortips("errortips", data.msg);
				window.setTimeout(function(){
					window.location.reload(true);
				},2000);
				
			} else {
				util.showerrortips("errortips", data.msg);
			}
		}, "json");
	},

	delquestion : function(data) {
		var questionid=data.getAttribute("data-question");
		var url = "/question/delquestion.html?random=" + Math.round(Math.random() * 100);
		var param = {
			fid : questionid
		};
		jQuery.post(url, param, function(data) {
			if (data.code == 0) {
				location.reload();
			} else {
				util.showerrortips("errortips", data.msg);
			}
		}, "json");
	},
	
};

$(function() {
	/* 文本域 */
	question.questionText("#question-desc");
	/* 删除 */
	$(".delete").click(function() {
		var data = this;
		util.showconfirm(language["comm.error.tips.139"], {
			okbtn : function() {
				question.delquestion(data);
			},
			noshow : true
			});
//		if(confirm("确定要删除吗？"))
//		 {
//			question.delquestion(this);
//		 }else{
//			 return;
//		 }
	});
	/* 提交问题 */
	$("#submitQuestion").on("click", function() {
		question.submitQuestion(false);
	});
	

});