<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8">
<meta http-equiv="Content-Script-Type" content="text/javascript">
<meta http-equiv="Content-Style-Type" content="text/css">
<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
<meta name="viewport" content="width=800">
<meta name="og:title" content="HTML5, 자바스크립트 데이터그리드 AUIGrid">
<meta name="og:url" content="http://www.auisoft.net/">
<meta name="og:description" content="HTML5, 자바스크립트 데이타그리드(javascript datagrid) AUISoft">
<title>AUIGrid 데모, HTML5, 자바스크립트 데이터그리드 AUIGrid</title>
<link href="http://www.auisoft.net/aui.ico" rel="shortcut icon" />
<link href="/js/pantheon/aui/samples/demo.css" rel="stylesheet">
<link href="/js/pantheon/aui/samples/style/AUIGrid_style.css" rel="stylesheet">

<!-- ajax 요청을 위한 스크립트입니다.  -->
<!-- <script type="text/javascript" src="/js/pantheon/aui/samples/ajax.js"></script> -->
<!-- <script type="text/javascript" src="/js/pantheon/aui/samples/common.js"></script> -->

<!-- AUIGrid 라이센스 파일입니다. 그리드 출력을 위해 꼭 삽입하십시오. -->
<script type="text/javascript" src="/js/pantheon/aui/AUIGrid/AUIGridLicense.js"></script>

<!-- 실제적인 AUIGrid 라이브러리입니다.  --> 
<script type="text/javascript" src="/js/pantheon/aui/AUIGrid/AUIGrid.js"></script>

<!-- 제이쿼리 cdn -->
<script src="//ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js"></script>

<style type="text/css">
/* 커스텀 페이징 패널 정의 */
.my-grid-paging-panel {
	width:800px;
	margin:4px auto;
	font-size:12px;
	height:34px;
	overflow: hidden;
	border: 1px solid #ccc;
}

/* aui-grid-paging-number 클래스 재정의*/
.aui-grid-paging-panel .aui-grid-paging-number {
	border-radius : 4px;
}
</style>

<script type="text/javascript">
var myGridOption = {
	//그리드 id로 AUI그리드 전용 메소드 사용 시 이용 ex) AUIGrid.selectedItem(myGridOption.id);
	id: '',
	//AUI그리드 생성할 div 및 페이징 옵션
	div: {
		gridDiv: '#grid_wrap',
		paging: {
			pagingDiv: 'grid_paging',
			totalRowCount: 500,
			rowCount: 20,
			pageButtonCount: 5,
			currentPage: 1,
			totalPage: 0
		},
	},
	//데이터 연계 옵션
	proxy: {
		url: 'selectTest.do',
		param: {},
		type: 'post',
		dataType: 'json',
		//페이징 true, false 로 지정
		paging: true,
		//처음 시작시 데이터 로딩
		autoLoad: true
	},
	//AUI 그리드 옵션
	gridPros: {
		// 그룹핑 패널 사용
		useGroupingPanel : true,
	
		showRowNumColumn : false,
		
		displayTreeOpen : true,
		
		groupingMessage : "여기에 칼럼을 드래그하면 그룹핑이 됩니다."
	},
	//AUI 그리드 레이아웃
	columnLayout : [{
		dataField : "ID",
		headerText : "ID",
		width : 100
	}],
	//AUI 그리드 생성
	createGrid: function() {
		var me = this;
		
		//이후 객체.id 로 AUIGrid 컨트롤
		me.id = AUIGrid.create(me.div.gridDiv, me.columnLayout, me.gridPros);
		me.binding();
		
		if(me.proxy.autoLoad) {
			me.load();
		}
	},
	//AUI 그리드 이벤트 
	binding: function() {
		
	},
	//AUI 그리드 데이터 요청
	load: function(v1, v2) {
		var me = this;
		
		//autoLoad로 처음 화면 데이터 로딩 
		if(me.proxy.paging && !v1 && !v2) {
			me.proxy.param.page = me.div.paging.currentPage;		
			me.proxy.param.count= me.div.paging.rowCount;		
		}
		
		//moveToPage 로 데이터 요청
		if(v1 && v2) {
			me.proxy.param.page = v2;	
			me.div.paging.currentPage = v2;
			me.proxy.param.count = v1;		
			me.div.paging.rowCount = v1;		
		}
		
		AUIGrid.showAjaxLoader(me.id);
		
		$.ajax({
		    url: me.proxy.url,
		    type: me.proxy.type,
		    dataType: me.proxy.dataType,
		    data: me.proxy.param,
		    success:function(data){
		    	if(data.success) {
			    	AUIGrid.removeAjaxLoader(me.id);
			    	AUIGrid.setGridData(me.id, data.result);
			    	
			    	//paging = true 및 count를 서버에서 리턴받았을 시 페이징 시작
			    	if(data.count && me.proxy.paging) {
			    		me.div.paging.totalRowCount = data.count;
			    		/*높이 변경
 			    		AUIGrid.resize(me.id, 800, 300);
			    		$("#"+me.div.paging.pagingDiv).offset({top: 495})
			    		*/
			    		me.createPagingNavigator(me.div.paging.currentPage);
			    	}
		    	} else {
		    		AUIGrid.removeAjaxLoader(me.id);
		    		alert(data.message);
		    	}
		    }
		});
	},
	//사용자 정의 페이징 Navigator (css 및 태그를 개발자가 임의로 지정할 수 있다. (retStr 수정) load() 함수에서 ajax 요청이 끝나고 마지막에 발동시킨다.)
	createPagingNavigator: function(goPage) {
		var me = this;

		if(!me.div.paging.pagingDiv) { 
			return;
		}
		
		this.div.paging.totalPage = Math.ceil(this.div.paging.totalRowCount/this.div.paging.rowCount);
		
		var retStr = "";
		var prevPage = parseInt((goPage - 1)/me.div.paging.pageButtonCount) * me.div.paging.pageButtonCount;
		var nextPage = ((parseInt((goPage - 1)/me.div.paging.pageButtonCount)) * me.div.paging.pageButtonCount) + me.div.paging.pageButtonCount + 1;

		prevPage = Math.max(0, prevPage);
		nextPage = Math.min(nextPage, me.div.paging.totalPage);
		
		//셀렉트박스
		retStr += "<select onchange='javascript:myGridOption.moveToPage(myGridOption.div.paging.currentPage, this.value)'>"
		if(myGridOption.div.paging.rowCount == 20) {
			retStr += "<option value=20 selected=true>20</option>"
		} else {
			retStr += "<option value=20>20</option>"
		}
		if(myGridOption.div.paging.rowCount == 50) {
			retStr += "<option value=50 selected=true>50</option>"
		} else {
			retStr += "<option value=50>50</option>"
		}
		if(myGridOption.div.paging.rowCount == 100) {
			retStr += "<option value=100 selected=true>100</option>"
		} else {
			retStr += "<option value=100>100</option>"
		}
		if(myGridOption.div.paging.rowCount == 300) {
			retStr += "<option value=300 selected=true>300</option>"
		} else {
			retStr += "<option value=300>300</option>"
		}
		if(myGridOption.div.paging.rowCount == 500) {
			retStr += "<option value=500 selected=true>500</option>"
		} else {
			retStr += "<option value=500>500</option>"
		}
		retStr += "</select>"
		
		// 처음
		retStr += "<a href='javascript:myGridOption.moveToPage(1)'><span class='aui-grid-paging-number aui-grid-paging-first'>first</span></a>";

		// 이전
		retStr += "<a href='javascript:myGridOption.moveToPage(" + prevPage + ")'><span class='aui-grid-paging-number aui-grid-paging-prev'>prev</span></a>";

		for (var i=(prevPage+1), len=(me.div.paging.pageButtonCount+prevPage); i<=len; i++) {
			if (goPage == i) {
				retStr += "<span class='aui-grid-paging-number aui-grid-paging-number-selected'>" + i + "</span>";
			} else {
				retStr += "<a href='javascript:myGridOption.moveToPage(" + i + ")'><span class='aui-grid-paging-number'>";
				retStr += i;
				retStr += "</span></a>";
			}
			
			if (i >= me.div.paging.totalPage) {
				break;
			}

		}

		// 다음
		retStr += "<a href='javascript:myGridOption.moveToPage(" + nextPage + ")'><span class='aui-grid-paging-number aui-grid-paging-next'>next</span></a>";

		// 마지막
		retStr += "<a href='javascript:myGridOption.moveToPage(" + me.div.paging.totalPage + ")'><span class='aui-grid-paging-number aui-grid-paging-last'>last</span></a>";
		
		//el식 테스트
// 		retStr += "${loginVO.getId()}";

		document.getElementById(me.div.paging.pagingDiv).innerHTML = retStr;
	},
	//페이징 에서 어느 페이지로 갈 것인지 지정
	moveToPage: function(goPage, rowCount) {
		var me = this;
		
		if(!me.div.paging.pagingDiv) { 
			return;
		}
		
		// 현재 페이지 보관
		me.div.paging.currentPage = goPage;
		
		// selectBox에서 요청시, 1페이지부터 시작
		if(rowCount) {
			me.div.paging.rowCount = rowCount;
			me.div.paging.currentPage = 1;
			goPage = 1;
		}
		
		me.load(me.div.paging.rowCount, goPage);
	}
};

$(document).ready(function() {
	myGridOption.createGrid();
});
</script>

</head>
<body>

<div id="main">
	<div class="desc">
		<p>그리드의 페이징은 기본적으로 많은 수의 데이터 삽입 시 그리드가 자체적으로 페이징을 만듭니다.</p>
		<p>그러나 사용자(개발자)가 서버에서 10개 또는 20개 행의 데이터 단위로 페이징을 직접 구현할 수 있습니다.</p>
		<p>엄밀히 말하면 사용자 정의 페이징은 그리드의 페이징 기능이 아닙니다. 데이터 리프레쉬(갱신) 과 같습니다.</p>
		<p>즉, 하단 버턴 클릭 시 다른 데이터를 삽입하는 것과 같습니다.</p>
		<p>따라서 현재 페이지의 데이터만 그리드가 필터링, 그룹핑, 소팅 등을 처리합니다.</p>
		<p style="color:red;">페이징 시 요청 주소를 바꿔 주십시오. 이 샘플은 같은 데이터를 요청하여 출력시키고 있습니다. 따라서 변경이 되지 않는 것 처럼 보입니다.</p>
	</div>
	<div>
		<!-- 에이유아이 그리드가 이곳에 생성됩니다. -->
		<div id="grid_wrap" style="width:800px; height:480px; margin:0 auto;"></div>
		
		<!-- 그리드 페이징 네비게이터 -->
		<div id="grid_paging" class="aui-grid-paging-panel my-grid-paging-panel"></div>
	</div>
	<div class="desc_bottom">
		<p id="ellapse"></p>
		
	</div> 
</div>
<div id="footer">
     <div class="copyright">
        <p>Copyright © 2015 AUISoft </p>
    </div>
</div>

</body>
</html>