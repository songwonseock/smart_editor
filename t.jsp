<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js"></script>
<script type="text/javascript">

// var jsonData1 = {'a1': 'b1'};
// var jsonData2 = {'a1': 'b2'};
// var jsonData3 = {'a1': 'b3'};
// var array = ;
// console.log(JSON.stringify(array));
$.ajax({
    url:'/test2.do',
    type:'post',
    dataType:"json",
//     contentType: 'application/json; charset=utf-8',
    data: {'id':'id입니다', 'name': 'name입니다.'},
    success:function(data){
    }
})
</script>
</head>
<body>
<%
// Object tt = session.getAttribute("loginVO");
// System.out.println
// LoginVO = 
// Map<String, String> map = (Map<String, String>) tt;
// String asd;
// Object session = 
%>
<%-- ${loginVO.ID } --%>
<%-- ${name} --%>
<%-- ${testLoginVO.id} --%>
${loginVO.getId()}
 <form action="./test2.do" method="post" modelAttribute="TestVO">
  id: <input type="text" name="id" value="why"><br>
  name: <input type="text" name="name" value="wonsuk"><br>
  <input type="submit" value="Submit">
</form> 
</body>
</html>