<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="../comm/include.inc.jsp"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Error_500_系统异常</title>
    <style type="text/css">
     body {
         margin: 8% auto 0;
         max-width: 550px;
         min-height: 200px;
         padding: 10px;
         font-family: Verdana,Arial,Helvetica,sans-serif;
         font-size: 14px;
     }
     p {
         color: #555;
         margin: 10px 10px;
     }

     img {
         border: 0px;
     }
     .d {
         color: #404040;
     }
    </style>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="renderer" content="webkit">
</head>
<body>
    <a href="${requestScope.constant['fulldomain'] }"><img src="${requestScope.constant['logoImage'] }" alt="${requestScope.constant['webName'] }" /></a>
    <p>抱歉! 访问发生异常!</p>
    <p class="d">请确认您输入的网址是否正确，如果问题持续存在，请与管理员联系。</p>
    <p><a href="${requestScope.constant['fulldomain'] }">返回网站首页</a></p>
</body>
</html>