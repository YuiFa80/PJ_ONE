<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Main Page :: Dynamic :: AccessInfo</title>
<script src="resources/resource.js"></script>

<script>
function accessOut(seCode, emCode) {

	/*form 생성*/
	const form = makeForm("","AccessOut","get");
	
	const inputSeCode = makeInputElement("hidden", "seCode", seCode, "");
	const inputEmCode = makeInputElement("hidden", "emCode", emCode, "");

    form.appendChild(inputSeCode);
    form.appendChild(inputEmCode);
    
    document.body.appendChild(form);
    form.submit();
}           
         function accessOut2(seCode, emCode){
        	 location.href = "AccessOut?seCode=" + seCode + "&emCode" + emCode;
         }
     </script>
</head>
<body>

	<h1>${accessInfo[0].seName}(${accessInfo[0].seCode})</h1>
	<h1>${accessInfo[0].emName}(${accessInfo[0].emCode})</h1>
	<h1>최근 접속 일시 : ${accessInfo[0].date}</h1>

	<form action="AccessOut" method="get">
		<input type="submit" value="로그아웃"> <input type="hidden"
			name="seCode" value="${accessInfo[0].seCode}"> <input
			type="hidden" name="emCode" value="${accessInfo[0].emCode}">
	</form>
</body>
</html>
