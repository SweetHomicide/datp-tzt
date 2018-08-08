$(function(){	
	$(".help_list li").mousemove(function() {
        var long = 104;
        $(this).data("long") && (long = 134),
        $(this).find("span").stop().animate({
            width: long + "px"
        },
        200);
    }).mouseout(function() {
        $(this).find("span").stop().animate({
            width: "0px"
        },
        200);
    });
	
	$(".totop_list li").mousemove(function() {
        var long = 104;
        $(this).data("long") && (long = 134),
        $(this).find("span").stop().animate({
            width: long + "px"
        },
        200);
    }).mouseout(function() {
        $(this).find("span").stop().animate({
            width: "0px"
        },
        200);
    });
});