<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="../comm/include.inc.jsp"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;
%>

<!doctype html>
<html>
<head> 
<base href="<%=basePath%>"/>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<%@include file="../comm/link.inc.jsp" %>
<style type="text/css">
/* main_img */
.img_gallery {
    height: 600px;
    border-top: 1px solid #d7d7d7;
    overflow: hidden;
    position: relative;
}

.main_img {
    height: 600px;
    overflow: hidden;
    position: relative;
}

.main_img ul {
    width: 9999px;
    height: 600px;
    overflow: hidden;
    position: abs olute;
    top: 0;
    left: 0
}

.main_img li {
    float: left;
    width: 100%;
    height: 600px;
}

.main_img li span {
    display: block;
    width: 100%;
    height: 600px
}

.main_img li a {
    display: block;
    width: 100%;
    height: 600px
}

.main_img li .img_1 {
    background: url('${requestScope.constant['blockImage1'] }') center top no-repeat
}

.main_img li .img_2 {
    background: url('${requestScope.constant['blockImage2'] }') center top no-repeat
}

.main_img li .img_3 {
    background: url('${requestScope.constant['blockImage3'] }') center top no-repeat
}

.img_font {
    position: absolute;
    bottom: 30px;
    left: 30%;
    color: #f00;
    width: 100%;
    padding: 10px;
}

.img_font span {
    display: none;
}

#btn_prev, #btn_next {
    z-index: 11111;
    position: absolute;
    display: block;
    width: 73px !important;
    height: 74px !important;
    top: 50%;
    margin-top: -37px;
    display: none;
}

#btn_prev {
    background: url(/static/front/images/activity/hover_left.png) no-repeat left top;
    left: 100px;
}

#btn_next {
    background: url(/static/front/images/activity/hover_right.png) no-repeat right top;
    right: 100px;
}


.container-fluid {
    padding-right: 15px;
    padding-left: 15px;
    margin-right: auto;
    margin-left: auto;
}
/* * {
    -webkit-box-sizing: border-box;
    -moz-box-sizing: border-box;
    box-sizing: border-box;
}
* {
    -webkit-tap-highlight-color: transparent;
}
*/
.col-md-4 {
    width: 33.33333333%;
}
.col-md-4 {
    float: left;
}
.col-md-4 {
    position: relative;
    min-height: 1px;
    padding-right: 15px;
    padding-left: 15px;
}
img {
    vertical-align: middle;
}
img {
    border: 0;
}
.l_pic_icon {
    width: 100%;
}

.l_pic_icon img {
    width: 100%;
    max-width: 630px;
}

.l_pic {
    margin-top: 53px;
}

.l_pic_tit {
    width: 100%;
    text-align: center;
    margin-top: 15px;
    line-height: 20px;
}

.l_pic_tit span {
    font-weight: bold;
}
</style>
</head>
<body>



<%@include file="../comm/header.jsp" %>

	<div class="container-full">
	
	<div class="img_gallery">

    <div class="main_img">
        <ul>
            <li><span class="img_1"></span></li>
            <li><span class="img_2"></span></li>
            <li><span class="img_3"></span></li>
        </ul>
        <a href="javascript:;" id="btn_prev"></a>
        <a href="javascript:;" id="btn_next"></a>
    </div>

</div>

<div class="container-fluid">
    <div class="col-md-4 col-sm-6 l_pic">
        <div class="l_pic_icon"><img src="/static/front/images/activity/jijin_06.jpg"></div>
       <div class="l_pic_tit"><br></div>
    </div>
    <div class="col-md-4 col-sm-6 l_pic">
        <div class="l_pic_icon"><img src="/static/front/images/activity/jijin_07.jpg"></div>
       <div class="l_pic_tit"><br></div>
    </div>
    <div class="col-md-4 col-sm-6 l_pic">
        <div class="l_pic_icon"><img src="/static/front/images/activity/jijin_09.jpg"></div>
       <div class="l_pic_tit"><br></div>
    </div>
</div>
		
	</div>
 
<%@include file="../comm/footer.jsp" %>	
<script type="text/javascript" src="/static/front/js/jquery.touchSlider.js"></script>
<script type="text/javascript">
    $(document).ready(function () {

        $(".img_gallery").hover(function () {
            $("#btn_prev,#btn_next").fadeIn()
        }, function () {
            $("#btn_prev,#btn_next").fadeOut()
        });

        $dragBln = false;

        $(".main_img").touchSlider({
            flexible: true,
            speed: 2000,
            btn_prev: $("#btn_prev"),
            btn_next: $("#btn_next"),
            paging: $(".point a"),
            counter: function (e) {
                $(".point a").removeClass("on").eq(e.current - 1).addClass("on");//图片顺序点切换
                $(".img_font span").hide().eq(e.current - 1).show();//图片文字切换
            }
        });

        $(".main_img").bind("mousedown", function () {
            $dragBln = false;
        });

        $(".main_img").bind("dragstart", function () {
            $dragBln = true;
        });

        $(".main_img a").click(function () {
            if ($dragBln) {
                return false;
            }
        });

        timer = setInterval(function () {
            $("#btn_next").click();
        }, 5000);

        $(".img_gallery").hover(function () {
            clearInterval(timer);
        }, function () {
            timer = setInterval(function () {
                $("#btn_next").click();
            }, 5000);
        });

        $(".main_img").bind("touchstart", function () {
            clearInterval(timer);
        }).bind("touchend", function () {
            timer = setInterval(function () {
                $("#btn_next").click();
            }, 5000);
        });

    });
</script>
</body>
</html>
