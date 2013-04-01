<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>login</title>
	<script type="text/javascript">
	function SignIn(){
        var f=document.getElementById("form1");
   		document.getElementById("method").value="query";
   		f.action="loginAction.aspx"
   	   	f.submit();
	}
	</script>
  </head>
  
  <body>
    <form id="form1" name="form1" action="">
    <input type="hidden" id="method" name="method" />
      <table>
         <tr><td>用户名:</td><td><input type="text" id="userName" name="userName" /></td></tr>
         <tr><td>密码:</td><td><input type="password" id="userPass" name="userPass" /></td></tr>
         <tr><td><input type="button" value="登录" onclick="SignIn()" /></td><td><input type="button" value="注册" /> </td></tr>
      </table>
    </form>
  </body>
</html>
