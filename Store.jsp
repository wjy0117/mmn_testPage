<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@page import="DB.*"%>
<%@page import="DataClass.*"%>
<%@page import="java.util.*"%>
<%@page import="Controller.*"%>

<!-- tagList불러와서 파일로 저장할 때 사용하는 라이브러리 모음 -->
<%--@ page import="java.sql.*" import="java.util.List"
	import="java.util.ArrayList" import="java.util.Map"
	import="java.util.HashMap" import="java.io.File" import="java.io.*"--%>

<!Doctype html>
<html lang="ko">

<head>

<meta charset="UTF-8">
<title>MMN-가게</title>

<link rel="stylesheet" href="resources/CSS/style_store.css">
<link rel="stylesheet" href="resources/slick-1.8.1/slick/slick.css">
<link rel="stylesheet"
	href="resources/slick-1.8.1/slick/slick-theme.css">
<link rel="stylesheet" href="resources/CSS/style_ImagePopUp.css">
<script src="https://code.jquery.com/jquery-1.12.4.min.js"></script>


<!-- tag input 관련 스타일과 jquery -->
<script src="./io_tagList.js"></script>

<!-- jquery -->
<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
<!-- jquery-ui -->
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<!-- jquery-ui css -->
<link rel="stylesheet"
	href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">

</head>

<body>
	<header>
		<div id="header">공통 헤더 이미지 머시기~</div>
	</header>
	<main>
		<div id="body">
			<%-- 
			<!-- tagList불러와서 파일로 저장할 때 사용하는 코드 -->
			<%
			Connection conn = null;// db접속객체
			PreparedStatement pstmt = null; // SQL실행객체
			ResultSet res = null; // 결과셋처리객체
			ArrayList<String> li = new ArrayList<String>();
			String encoding = "UTF-8";
			try {
				// mysql jdbc driver 로딩
				Class.forName("com.mysql.jdbc.Driver");
				// db연결 문자열 but 이방법은 보안에 취약하다.
				String url = "jdbc:mysql://192.168.250.44/mmn?characterEncoding=UTF-8&serverTimezone=UTC";
				String id = "junghan"; // mysql 접속아이디
				String pwd = "yeil!1234"; // mysql 접속 비번
				// db 접속
				conn = DriverManager.getConnection(url, id, pwd);
				System.out.println("db접속 성공");
				String sql = "select tagTbl.tagName from tagTbl";
				pstmt = conn.prepareStatement(sql);
				res = pstmt.executeQuery(sql);
				//(태그 리스트)파일을 저장할 위치 설정
				File file = new File("./io_tagList.js");
				//utf-8형식으로 f파일 생성
				//PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(f)));
				PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8")));
				//db에서 추출한 정보 리스트 문자열
				while (res.next()) {
					li.add(res.getString("tagName"));
				}
				//f파일 내용구성
				writer.println("List = [");
				for (int i = 0; i < li.size(); i++) {
					writer.print("'");
					writer.print(li.get(i));
					writer.println("', ");
				}
				writer.println("]");
				//파일종료
				writer.close();
				
				if (file.createNewFile()) {
					System.out.println("File created");
				} else {
					System.out.println("File already exists");
				}
			} catch (Exception e) {
				System.out.println("Failed");
				e.printStackTrace();
			} finally {
				try {
					if (pstmt != null) {
				pstmt.close();
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				try {
					if (conn != null) {
				conn.close();
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			--%>


			<%
			DB_Conn _db = new DB_Conn();
			_db.constructStoreMap();
			_db.constructRtdCnt_map();
			ArrayList<storeData> storeList;
			ArrayList<rtdCntData> rtdCntList;
			storeList = _db.storefindAll();
			rtdCntList = _db.rtdCntfindAll();
			System.out.println("size : " + storeList.size());
			int storeCode = Integer.parseInt(request.getParameter("storeCode") == null ? "0" : request.getParameter("storeCode"));
			storeData sd = _db.getStoreData(storeCode);
			String storeImgPath = sd.getStoreImgPath();
			int review_store = sd.getStoreCode();
			_db.constructMenuMap(review_store);
			ArrayList<menuData> list = _db.menufindAll();
			Collections.sort(rtdCntList);
			String keep_btn_path = "resources/UI/UI/keep_btn.png";
			String keep_btn_sel_path = "resources/UI/UI/keep_btn_sel.png";
			String userID = (String) session.getAttribute("memberID");
			System.out.println(userID);

			boolean flag = _db.haveWatchlist(userID, storeCode);
			%>

			<div id="store">
				<div id="store_photo">
					가게 이미지 및 음식 사진
					<div id="big_photo" class="photo">
						<img src="<%=storeImgPath%>">
					</div>
					<div id="small_photo" class="photo">이미지2</div>
					<div id="small_photo" class="photo">이미지3</div>
					<div id="small_photo" class="photo">이미지4</div>
					<div id="small_photo" class="photo">
						<button id="show">팝업열기</button>
					</div>
				</div>
				<div id="store_info">
					<div id="store_name"><%=sd.getStoreName()%></div>
					<div id="store_keep">
						<Button type="button" id="keepBtn" value="">
							<img
								src="<%=flag == true
		? "https://raw.githubusercontent.com/CampbellBiology/MMN2/master/MMN_test/src/main/webapp/resources/UI/UI/keep_btn_sel.png"
		: "https://raw.githubusercontent.com/CampbellBiology/MMN2/master/MMN_test/src/main/webapp/resources/UI/UI/keep_btn.png"%>"
								id="keepImg" onclick="keepClick();sendRequest();"
								onmouseover="onHover()" onmouseout="offHover()">
						</Button>
						<p id="text"></p>

					</div>

					<div id="store_detail"><%="가게 시작 시간 : " + sd.getOpenAt() + "<br>가게 마감 시간 : " + sd.getCloseAt() + "<br> 마지막 주문 가능 시간 : "
		+ (sd.getLastOrder() == null ? "정보 없음" : sd.getLastOrder()) + "<br>가게 주차 여부 : "
		+ (sd.getParking() == null ? "주차 정보 없음" : sd.getParking().equals("1") ? "주차 가능" : "주차 불가") + "<br> 휴무일 : "
		+ (sd.getOffDays() == null ? "정보 없음" : sd.getOffDays()) + "<br> 주소 : " + sd.getAddr() + "<br> 전화번호 : "
		+ sd.getPhone() + "<br> 홈페이지 : " + (sd.getWeb() == null ? "정보 없음" : sd.getWeb()) + "<br> 브레이크 타임 : "
		+ (sd.getBreakStart() == null ? "정보 없음" : sd.getBreakStart() + " - " + sd.getBreakEnd())%><br>
					</div>
				</div>

				<%
				//가게별 메뉴
				String mc = _db.get_menuCount();
				ArrayList<String> tc = _db.get_tagCount(sd.getStoreCode());
				%>
				<div id="store_static">
					<!-- 가장 많이 메뉴로 추가한 음식 1개 -->
					<div id="favorite" class="store_static">
						뭐뭇나 회원이 가장 많이 먹은 음식은?<br> <span><%=mc%></span>
					</div>
					<!-- 가장 뷰 수가 높은 태그 1개 -->

					<div id="related_tag" class="store_static">
						관련된 태그는?<br>
						<%
						for (int i = 0; i < tc.size(); i++) {
						%>
						<span> <%=tc.get(i)%>
						</span>
						<%
						}
						%>
					</div>
				</div>

				<div id="review_btn">
					<input id="review_write" type="button" value="리뷰 작성하기"
						onclick="writeClick()">
				</div>
			</div>
			<hr>

			<!-- 0417 이 페이지 작업중 -->
			<!-- 사용하는 id나 name -->
			<!--  -->
			<form method="post" id="review_form" action="review"
				style="display: none">
				<div id="create_riview">
					<div id="first_row">
						<!-- 가게코드 저장 -->
						<input name="review_storeCode" value="<%=sd.getStoreCode()%>"
							style="display: none">
						<!--  위에서와 동일하게 가게명 가져오기 -->
						<div id="review_store_name">
							<%=sd.getStoreName()%>
							<input type="text" name="review_input_id"
								placeholder="(임시) id 입력칸">
						</div>
						<div id="noname_check">
							<input name="noname_check" type="checkbox" value="noname">익명으로
							작성하기
						</div>
					</div>

					<div id="second_row">
						<div id="review_whatIAte">
							내가 먹은 메뉴 <select id="menu" onchange="select_menu()">
								<%
								int cnt = 0;
								for (menuData md : list) {
								%>
								<option value="<%=md.getFoodCode()%>"
									<%=cnt == 0 ? "selected" : ""%>>
									<%=md.getFoodName()%></option>
								<%
								cnt++;
								}
								%>
							</select> <a href="javascript:menu_del()">x</a> <input id="menuListView"
								name="menuListView" style="display: none">
						</div>
						<div id="score_title">
							평점 <input id="score_result" name="score_result"
								style="display: none">
						</div>
					</div>

					<div id="third_row">
						<div id="show_whatIAte">
							<!-- 
							<div id="whatiate1" class="whatiate">생삼겹</div>
							<div id="whatiate2" class="whatiate">생목살</div>
							<div id="whatiate3" class="whatiate">양념갈비</div>
							-->
							<ul id="lists_menu">
								<li id="whatiate_add" class="whatiate" style="display: none">
							</ul>
						</div>

						<div id="score">
							<div id="score_great" class="score">
								<button type="button" id="btn_score_great" value="great"
									class="score_btn">억수로 마싯다</button>
							</div>
							<div id="score_good" class="score">
								<button type="button" id="btn_score_good" value="good"
									class="score_btn">갠찮드라</button>
							</div>
							<div id="score_bad" class="score">
								<button type="button" id="btn_score_bad" value="bad"
									class="score_btn">영 파이다</button>
							</div>
						</div>

						<div id="forth_row">
							<div id="add_tag_title">
								태그를 입력해주세요.
								<!-- 태그입력 리스트 생성(문자열)-->
								<input id="tagListView" name="tagListView" style="display: none">
							</div>
							<!-- 에러문 출력 -->
							<span id="warning_msg" style="color: red; display: none">태그는
								5개로 제한되어 있습니다.</span>
							<div id="add_tag">
								<ul id="lists_tag"></ul>
								<div id="create_tag1" class="create_tag">
									<input type="text" id="id_input_tagName" onblur="input_blur()"
										placeholder="ex.#비오는날"> <a href="javascript:tag_del()">x</a>
								</div>
								<!-- <div id="create_tag2" class="create_tag">
									<button type="button">추가아이콘</button>
								</div>
								-->
								<!-- 
								<div id="create_tag3" class="create_tag"></div>
								<div id="create_tag4" class="create_tag"></div>
								-->
							</div>
						</div>

						<div id="fifth_row">
							<div>
								<input name="review_text" type="text"
									placeholder="리뷰를 작성해주세요. 최대 4000자" maxlength="4000">
							</div>
						</div>

						<div id="sixth_row">
							<div>
								이미지 등록 최대 5개(일단 1개) <input type="file" name="review_img"
									accept=".jpg, .png" onchange="readImg(this);">
							</div>
							<div id="regImages">
								<div id="reg_image1" class="reg_images">
									<img id="preview" style="height: 150px;" />
								</div>
								<div id="reg_image2" class="reg_images"></div>
								<div id="reg_image3" class="reg_images"></div>
								<div id="reg_image4" class="reg_images"></div>
								<div id="reg_image5" class="reg_images"></div>
							</div>
						</div>

						<div id="seventh_row">
							<button type="button" value="toMain">취소</button>
							<!-- <button type="submit">등록하기</button> -->
							<button id="review_click" type="button">등록하기</button>
						</div>
					</div>
				</div>
			</form>
			<div id="blank">여백</div>
			<div id="show_review">
				<div id="first_line">
					<div id="review_title">리뷰</div>
					<!-- 
					0421 리뷰sort 각 텍스트 클릭시 리뷰 내용이 변경되도록 설정. 
					-->
					<input id="review_SortText" name="review_SortText">
					<%
					//각 리뷰 별 계산하는 함수 불러오기.
					int review_count_All = _db.get_review_count_All();
					int review_count_great = _db.get_review_count_great();
					int review_count_good = _db.get_review_count_good();
					int review_count_bad = _db.get_review_count_bad();
					%>
					<div id="review_sort">
						<ul>
							<li><a href="javascript:sortAll">전체</a><span id="score_all"
								class="show_score"><%=review_count_All%> </span></li>
							<li><a href="javascript:sortGreat">억수로 마싯다</a><span
								id="score_great" class="show_score"><%=review_count_great%>
							</span></li>
							<li><a href="javascript:sortGood">갠찮드라</a><span
								id="score_good" class="show_score"><%=review_count_good%>
							</span></li>
							<li><a href="javascript:sortBad">영 파이다</a> <span
								id="score_bad" class="show_score"><%=review_count_bad%> </span></li>
						</ul>
					</div>
				</div>

				<form method="post" id="review_form2" action="reviewsort">
					<%
					//리뷰정보tbl (reviewIndex, contents, regDate, rating, anonymous) (추가, reviewImage)
					//유저정보tbl (userImagePath, userName)
					//menutargettbl 먹은 음식 리스트
					//1. 리뷰 인덱스 찾기 2. 메뉴코드 조회하여 메뉴명 알아내기(리뷰tbl-> 리뷰타겟tbl-> 메뉴tbl)
					//db에서 
					//? = _db.findUser()
					//int storeCode = sd.getStoreCode();
					/*
					arr.add(res.getString("reviewIndex"));
					arr.add(res.getString("contents"));
					arr.add(res.getString("regDate"));
					arr.add(res.getString("rating"));
					arr.add(res.getString("anonymous"));
					*/
					//.getAttribute로 flag를 변수로 설정.
					//그리고 메소드 실행시키면서 파라미터로 같이 전달.
					int sf = 0;
					/*try{
						sf = (Integer) request.getAttribute("sortFlag");
						switch(sf){
							case 5:
						break;
							case 4:
						break;
							case 3:
						break;
							default:
						sf = 0;
						}
					}catch(Exception e){
						sf = (Integer) request.getAttribute("sortFlag");
						switch(sf){
							case 5:
						break;
							case 4:
						break;
							case 3:
						break;
							default:
						sf = 0;
						}
					}*/
					//5번 반복하여 리뷰 정보와 메뉴 정보를 가져오기.
					//리뷰정보를 먼저가져와 리뷰 정보의 리뷰인덱스를 이용하여 메뉴 확인.
					for (int review = 0; review < 5; review++) {
						//리뷰정보를 리스트로 가져오는 함수실행.
						//reviewIndex, userId, contents, regDate, rating, anonymous, photoPath
						ArrayList<String> rd = _db.get_ReviewData(review, sf);

						int review_in = Integer.parseInt(rd.get(0));
						String review_id = rd.get(1);
						String review_con = rd.get(2);
						String review_date = rd.get(3);
						String review_rat = rd.get(4);
						String review_ano = rd.get(5);
						String review_photo = rd.get(6);
						if (review_ano.equals("1")) {
							review_id = "익명";
						}
						//review_index를 이용하여 메뉴정보 리스트를 가져오는 함수실행.
						ArrayList<String> menuList = _db.get_ReviewTarget(review_in);
					%>
					<div id="review_profile">
						프로필 사진
						<%=review_photo%>
						<!-- userid? username? -->
						<span id="review_userid"> <%=review_id%>
						</span>
					</div>
					<div id="review_detail">
						<div id="second_line">
							<div id="reg_date">
								<!-- 23.03.30 -->
								<%=review_date%>
							</div>
							<div id="show_rate">
								<!-- 억수로 마싯다-->
								<%=review_rat%>
							</div>
						</div>

						<div id="third_line">
							<div id="WIA_title">먹은 음식:</div>
							<%
							for (int menu = 0; menu < menuList.size(); menu++) {
							%>
							<div id="WIA_contents">
								<!-- 동적으로 추가-->
								<%=menuList.get(menu)%>
							</div>
							<%
							}
							%>
						</div>

						<div id="forth_line">
							<div id="riview_contents">
								<!-- 리뷰 내용이 될 부분<br>뫄뫄뫄뫄-->
								<%=review_con%>
							</div>
						</div>

						<div id="fifth_line">
							<div id="show_images">
								이미지 넣는 부분<br>
								<div id="show_image1" class="show_images"></div>
								<div id="show_image2" class="show_images"></div>
								<div id="show_image3" class="show_images"></div>
								<div id="show_image4" class="show_images"></div>
								<div id="show_image5" class="show_images"></div>
							</div>
						</div>
					</div>
					<div id="blank">여백</div>
					<%
					}
					%>
				</form>

				<!-- 여기서부터
팝업창입니다 -->

				<div class="background">
					<div class="window">
						<!-- 팝업페이지 -->
						<div class="popup">

							<button id="close">팝업닫기</button>

							<!-- 슬라이드 CSS 라이브러리 -->
							<div id="mainImage">
								<img src="resources/UI/storeImg/1.jpg" id="main_image"
									width="400px">
							</div>
							<!-- 우측 리뷰 영역 -->
							<div id="pop_riview">
								<div id="user_profile">
									<img src="resources/UI/profile/jennie.jpg">
								</div>
								<div id="user_name">제니</div>
								<div id="user_riview">뫄뫄뫄</div>
							</div>


							<!-- 아래 이미지 영역 -->
							<section class="center slider">
								<div>
									<img src="resources/UI/storeImg/1.jpg">
								</div>
								<div>
									<img src="resources/UI/storeImg/2.jpg">
								</div>
								<div>
									<img src="resources/UI/storeImg/3.jpg">
								</div>
								<div>
									<img src="resources/UI/storeImg/4.jpg">
								</div>
								<div>
									<img src="resources/UI/storeImg/5.jpg">
								</div>
								<div>
									<img src="resources/UI/storeImg/6.jpg">
								</div>
								<div>
									<img src="resources/UI/storeImg/7.jpg">
								</div>
								<div>
									<img src="resources/UI/storeImg/8.jpg">
								</div>
								<div>
									<img src="resources/UI/storeImg/9.jpg">
								</div>

							</section>


						</div>


					</div>
				</div>
			</div>
		</div>

		<!-- 모달 팝업 스크립트 -->
		<script>
			function show() {
				document.querySelector(".background").className = "background show";
			}
			function close() {
				document.querySelector(".background").className = "background";
			}
			document.querySelector("#show").addEventListener("click", show);
			document.querySelector("#close").addEventListener("click", close);
		</script>

		<!-- 슬라이드 CSS 라이브러리 스크립트 -->
		<script src="https://code.jquery.com/jquery-2.2.0.min.js"
			type="text/javascript"></script>
		<script src="resources/slick-1.8.1/slick/slick.js"
			type="text/javascript" charset="utf-8"></script>

		<script type="text/javascript">
			$(document).on('ready', function() {
				$(".center").slick({
					dots : true,
					infinite : true,
					centerMode : true,
					slidesToShow : 5,
					slidesToScroll : 3
				});
			});
		</script>

		<script>
		function sendRequest() {
			var httpRequest;
			function createRequest() {
				if (window.XMLHttpRequest) { // 익스플로러 7과 그 이상의 버전, 크롬, 파이어폭스, 사파리,
												// 오페라 등
					return new XMLHttpRequest();
				} else { // 익스플로러 6과 그 이하의 버전
					return new ActiveXObject("Microsoft.XMLHTTP");
				}
			}
			function receiveResponse() {
				// XMLHttpRequest 객체의 현재 상태가 요청 완료이고, 서버에 문서가 존재하면 받은 데이터를 출력함.
				if (httpRequest.readyState == XMLHttpRequest.DONE
						&& httpRequest.status == 200) {
					document.getElementById("text").innerHTML = httpRequest.responseText;
				}
			}
			httpRequest = createRequest(); // XMLHttpRequest 객체를 생성함.
			httpRequest.onreadystatechange = receiveResponse; // XMLHttpRequest 객체의 현재
																// 상태를 파악함.
			// GET 방식의 비동기식 요청으로 Http 요청을 생성함.
			httpRequest.open("POST", "watchlistAddOrDelete.jsp", true);
			httpRequest.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
			httpRequest.send("userId=<%=userID%>&storeCode=<%=storeCode%>"); // Http 요청을 보냄.
			}
		</script>



		<!-- 정윤 -->
		<!-- 사용하는 name또는 id -->

		<!-- (id) -->
		<!-- 
			menu, lists_menu, whatiate_add, 
			score_great, btn_score_good, btn_score_bad, score_result
			id_input_tagName, warning_msg, (생성)list_tag(번호) 
		-->
		<!-- 리뷰 메뉴 입력 관련 이벤트 처리 -->
		<script type="text/javascript">
			//메뉴명 담은 배열 생성
			var menu_list = [];
			var menu_count = 0;
			//선택된 메뉴명을 먹은 메뉴에 추가하는 이벤트
			function select_menu() {
				var sMenu = document.getElementById("menu");
				var Menu_data = sMenu.options[sMenu.selectedIndex].text;
				//확인용1 
				//console.log(Menu_data);
				//null이 아니면서 갯수가 3개이하이며, 중복이 없을 때 처리
				if (Menu_data !== "" && menu_count < 3) {//&& dup_check(document.getElementById("id_input_tagName").value)){
					//리스트 추가
					menu_list.push(Menu_data);
					//ul과 div 불러오기
					var input_MenuList = document.getElementById("lists_menu");
					var add_Menu = document.getElementById("whatiate_add");
					//리스트 요소 생성
					//생성한 리스트의 id는 list_menu+(숫자)
					var input_Menu = document.createElement('li');
					input_Menu.innerText = Menu_data;
					input_Menu.setAttribute("id", "list_menu" + menu_count);
					//생성한 요소를 null 이면 뒤, 설정시 그 요소의 앞에 위치 시킨다.
					input_MenuList.insertBefore(input_Menu, add_Menu);
					menu_count++;
					document.getElementById("menuListView").value = menu_list.toString();
				} else if (menu_count > 2) { //선택한 메뉴 갯수가 4개 이상이라면 숨겼던 div보여주고 text변경.
					//count가 4개 이상이라면 그 외 *개 보이게
					document.getElementById("whatiate_add").style.display = "block";
					//리스트에 추가
					menu_list.push(Menu_data);
					//text수정
					document.getElementById("whatiate_add").innerText = "그 외 " + (menu_count - 2) + "개";
				} else {
					//입력이나 처리 없음
					console.log("none");
				}
				//길이따라 count 재지정.
				menu_count = menu_list.length;
				//내용 확인용 함수 호출
				menu_list_view();
			}
			//메뉴 리스트 삭제
			//지금은 input 옆에 x, 클릭시 뒤에서부터 리스트 삭제.
			function menu_del() {
				//지울 리스트 요소 선택 (맨 마지막에 생성된 요소)
				var del_list = document.getElementById("list_menu" + (menu_count - 1).toString());
				if (menu_list.length !== 0 && menu_count < 4) {
					//보이는 li 지우기
					del_list.remove();
					//리스트에서 삭제
					menu_list.pop();
					//길이따라 count 재지정.
					menu_count = menu_list.length;
					//현재 갯수 확인용 로그
					console.log("현재 개수: " + (menu_count) + "개");
				} else if (menu_count > 2) {
					//선택한 메뉴가 4개 이상일때 삭제 방법
					//리스트에서 삭제
					menu_list.pop();
					//길이에 따라 재지정
					menu_count = menu_list.length;
					//갯수 확인용 로그
					console.log("현재 개수: " + (menu_count) + "개");
					//갯수에 따라 text 수정
					document.getElementById("whatiate_add").innerText = "그 외 " + (menu_count - 3) + "개";
					//3개라면 "그 외 3개"표시 안함.
					if ((menu_count - 3) === 0) {
						document.getElementById("whatiate_add").style.display = "none";
					}
				} else {
					//이미 없어짐.
					console.log("Value already missing in menu list");
				}
				//내용 확인용 함수 호출
				menu_list_view();
			}
			
			//내용 확인용 함수
			function menu_list_view() {
				//배열 내용출력
				for (var i = 0; i < menu_list.length; i++) {
					console.log(i + "(menu): " + menu_list[i]);
				}
			}
			
			//평점 클릭시 버튼마다 설정
			//평점 점수 5, 3, 1
			//클릭시 클릭한 평점에 따라 버튼 색변경
			$("#score_great").on("click", (e)=> {
				document.getElementById("score_result").value = 5;
				
				document.getElementById("btn_score_great").style.backgroundColor="#FF4500";
				document.getElementById("btn_score_good").style.backgroundColor="#D2B48C";
				document.getElementById("btn_score_bad").style.backgroundColor="#D2B48C";
				
			});
			
			$("#score_good").on("click", (e)=> {
				document.getElementById("score_result").value = 4;
				
				document.getElementById("btn_score_great").style.background="#D2B48C";
				document.getElementById("btn_score_good").style.background="#FF8C00";
				document.getElementById("btn_score_bad").style.background="#D2B48C";
				
			});
			$("#score_bad").on("click", (e)=> {
				document.getElementById("score_result").value = 3;
				
				document.getElementById("btn_score_great").style.background="#D2B48C";
				document.getElementById("btn_score_good").style.background="#D2B48C";
				document.getElementById("btn_score_bad").style.background="#DAA520";
				
			});
			
			//이미지 미리보기 메소드
			function readImg(input) {
				//input에 입력한 파일 가져오기
				if (input.files && input.files[0]) {
					//파일 읽어오는 라이브러리
					var reader = new FileReader();
				    reader.onload = function(e) {
					  //업로드한 이미지 주소를 img 태그의 이미지 주소로 설정
				      document.getElementById("preview").src = e.target.result;
				    };
				    //url 실행
				    reader.readAsDataURL(input.files[0]);
				} else {
				    document.getElementById("preview").src = "";
				}
			}
		</script>


		<!-- 리뷰 태그입력 처리를 위한 이벤트 처리 -->
		<script type="text/javascript">
			//태그명 담을 배열 생성
			var tag_list = [];
			var tag_count = 0;
		
			//tag 입력 자동완성 이벤트
			//jQuery 충돌로 안나옴
			/*
			$(function() {
				
				$("#id_input_tagName").autocomplete({ //오토 컴플릿트 시작
					source : List, // source는 data.js파일 내부의 List 배열
					focus : function(event, ui) { // 방향키로 자동완성단어 선택 가능하게 만들어줌	
						//console.log(ui.item);
						return false;
					},
					minLength : 1,// 최소 글자수
					delay : 10, //autocomplete 딜레이 시간(ms)
					//disabled: true, //자동완성 기능 끄기
				});
		    });
			*/
			
			//blur시에 리스트 추가 이벤트
			function input_blur() {
				document.getElementById("id_input_tagName").setAttribute("Placeholder", "ex.#비오는날");
				//경고문 안보이게
				document.getElementById("warning_msg").style.disabled = false;
				//input정보
				var x = document.getElementById("id_input_tagName");
				//입력이 없거나 태그가 5개 이하라면(0~4) 입력받음
				//code: 입력키, 입력이 ""이 아닐때, tag개수가 5개 이하일 때, 중복체크 1: 중복아님, 0:중복
				if (x.value !== "" && tag_count < 5 && dup_check(x.value) && List.includes(x.value)) {
					//입력한 내용을 배열에 추가
					tag_list.push(x.value);
					//태그목록 생성
					//var input_TagList = document.createElement('ul');
					var input_TagList = document.getElementById("lists_tag");
					var input_Tag = document.createElement('li');
					input_Tag.innerText = "#" + x.value;
					input_Tag.setAttribute("id", "list_tag" + tag_count);
					
					//위치 지정
					//특정위치 앞에 삽입(상속관계), div > ul > li
					input_TagList.insertBefore(input_Tag, null);
					
					//input창 초기화
					x.value = null;
					//카운트
					tag_count++;
					
					document.getElementById("tagListView").value = tag_list.toString();
				} else if (tag_count > 4) { //태그 검색 개수가 6개 이상이라면 비활성화
					x.disabled = true;
					document.getElementById("warning_msg").style.display = "block";
				} else if (!List.includes(x.value)) {
					document.getElementById("id_input_tagName").value = null;
					document.getElementById("id_input_tagName").setAttribute("Placeholder", "리스트에 없는 내용입니다.");
				} else { //빈 검색어라면 log에 none을 표시하고 추가되는 거 없음
					console.log("none");
				}
			}
			
			//중복확인 이벤트(t: 중복아님, f:중복)
			//입력 input의 value값(String)
			function dup_check(str) {
				var x = document.getElementById("id_input_tagName");
				for (var i = 0; i < tag_count; i++) {
					if (str === tag_list[i]) {
						return 0;
					}
				}
				return 1;
			}
			
			//input 옆에 x 클릭시 뒤에서부터 리스트 삭제 이벤트
			function tag_del() {
				//현재 배열 위치와 tag입력 개수 출력
				console.log("del: " + "list_tag"+(tag_count - 1).toString() + "\t" + tag_count);
				//tag_count != 0 체크하려했는데 실수지만 잘 
				if (tag_list.length !== 0) {
					document.getElementById("list_tag"+(tag_count - 1)).remove();
					tag_list.pop();
					tag_count--;
					document.getElementById("warning_msg").style.display = "none";
					document.getElementById("id_input_tagName").disabled = false;
					//input창 초기화
					document.getElementById("id_input_tagName").value = null;
					document.getElementById("tagListView").value = tag_list.toString();
				} else {
					console.log("Value already missing in tag list");
				}
			}
			
			//리뷰 작성하기 버튼을 클릭했을 때 submit처리, 서블릿 호출
			function writeClick(){
				document.getElementById("review_write").style.display = "none";
				document.getElementById("review_form").style.display = "block";
			}
			
			//등록하기 버튼을 클릭했을 때 submit처리, 서블릿 호출
			window.onload = function() {
			    document.getElementById("review_click").onclick = function() {
			        document.getElementById("review_form").submit();
			        return false;
			    };
			};
			
			//0421
			//리뷰 정렬 클릭마다 내용 변경.
			//전체 클릭시 최신순으로 5개 출력.
			function sortAll(){
				document.getElementById("review_SortText").value = "all";
				document.getElementById("review_form2").submit();
			}
			//억수로 마싯다 클릭시 평점 5인 리뷰 5개 출력.
			function sortGreat(){
				document.getElementById("review_SortText").value = "great";
				document.getElementById("review_form2").submit();
			}
			//갠찮드라 클릭시 평점 4인 리뷰 5개 출력.
			function sortGood(){
				document.getElementById("review_SortText").value = "good";
				document.getElementById("review_form2").submit();
			}
			//영 파이다 클릭시 평점 3인 리뷰 5개 출력.

			function sortBad(){
				document.getElementById("review_SortText").value = "bad";
				document.getElementById("review_form2").submit();
			}
		</script>

		<script type="text/javascript" src="resources/js/project01.js"></script>
	</main>
</body>
</html>

<!-- ========================================================================================================================================== -->

<%-- <%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@page import="DB.*"%>
<%@page import="DataClass.*"%>
<%@page import="java.util.*"%> --%>
<%-- 
<!Doctype html>
<html lang="ko">

<head>

</head>

<body>
	<header>
		<div id="header">공통 헤더 이미지 머시기~</div>
	</header>
	<main>
		<div id="body">

			

				<div id="store_static">
					<div id="favorite" class="store_static">
						뭐뭇나 회원이 가장 많이 먹은 음식은?
						<%="<br>"%>
						<%
						for (rtdCntData r : rtdCntList) {
						%>
						<%=_db.getFoodName(r.getFoodCode())%>
						<%
						}
						%>
					</div>
					<div id="related_tag" class="store_static">관련된 태그는?</div>
				</div>

				<div id="review_btn">
					<input type="button" value="리뷰 작성하기">
				</div>
			</div>
			<hr>

			<form method="post" action="review">

				<div id="create_riview">
					<div id="first_row">
						<div id="review_store_name">가게이름</div>
						<div id="noname_check">
							<input type="checkbox" value="noname">익명으로 작성하기
						</div>
					</div>

					<div id="second_row">
						<div id="review_whatIAte">
							내가 먹은 메뉴 <select>
								<%
								int cnt = 0;
								for (menuData md : list) {
								%>
								<option value="<%=md.getFoodCode()%>"
									<%=cnt == 0 ? "selected" : ""%>>
									<%=md.getFoodName()%></option>
								<%
								cnt++;
								}
								%>
							</select>
						</div>
						<div id="score_title">평점</div>
					</div>

					<div id="third_row">
						<div id="show_whatIAte">
							<div id="whatiate1" class="whatiate">생삼겹</div>
							<div id="whatiate2" class="whatiate">생목살</div>
							<div id="whatiate3" class="whatiate">양념갈비</div>
							<div id="whatiate4" class="whatiate">그 외 2개</div>
						</div>

						<div id="score">
							<div id="score_great" class="score">
								<button type="button" value="great" class="score_btn">억수로
									마싯다</button>
							</div>
							<div id="score_good" class="score">
								<button type="button" value="good" class="score_btn">갠찮드라</button>
							</div>
							<div id="score_bad" class="score">
								<button type="button" value="bad" class="score_btn">영
									파이다</button>
							</div>
						</div>

						<div id="forth_row">
							<div id="add_tag_title">태그를 입력해주세요.</div>
							<div id="add_tag">
								<div id="create_tag1" class="create_tag">
									<input type="text" placeholder="ex.#비오는날">
								</div>
								<div id="create_tag2" class="create_tag">
									<button type="button">추가아이콘</button>
								</div>
								<div id="create_tag3" class="create_tag"></div>
								<div id="create_tag4" class="create_tag"></div>
							</div>
						</div>

						<div id="fifth_row">
							<div>
								<input type="text" placeholder="리뷰를 작성해주세요. 최대 4000자">
							</div>
						</div>

						<div id="sixth_row">
							<div>이미지 등록 최대 5개</div>
							<div id="regImages">
								<div id="reg_image1" class="reg_images"></div>
								<div id="reg_image2" class="reg_images"></div>
								<div id="reg_image3" class="reg_images"></div>
								<div id="reg_image4" class="reg_images"></div>
								<div id="reg_image5" class="reg_images"></div>
							</div>
						</div>

						<div id="seventh_row">
							<button type="button" value="toMain">취소</button>
							<button type="submit">등록하기</button>
						</div>
					</div>
				</div>
			</form>

			<div id="blank">여백</div>

			<div id="show_review">
				<div id="first_line">
					<div id="review_title">리뷰</div>
					<div id="review_sort">
						<ul>
							<li>전체<span id="score_all" class="show_score">(12)</span></li>
							<li>억수로 마싯다<span id="score_great" class="show_score">(12)</span></li>
							<li>갠찮드라<span id="score_good" class="show_score">(12)</span></li>
							<li>영 파이다<span id="score_bad" class="show_score">(12)</span></li>
						</ul>
					</div>
				</div>

				<div id="review_profile">프로필 사진 + 이름</div>
				<div id="review_detail">
					<div id="second_line">
						<div id="reg_date">23.03.30</div>
						<div id="show_rate">억수로 마싯다</div>
					</div>

					<div id="third_line">
						<div id="WIA_title">먹은 음식:</div>
						<div id="WIA_contents">동적으로 추가</div>
					</div>

					<div id="forth_line">
						<div id="riview_contents">
							리뷰 내용이 될 부분<br>뫄뫄뫄뫄
						</div>
					</div>

					<div id="fifth_line">
						<div id="show_images">
							이미지 넣는 부분<br>
							<div id="show_image1" class="show_images"></div>
							<div id="show_image2" class="show_images"></div>
							<div id="show_image3" class="show_images"></div>
							<div id="show_image4" class="show_images"></div>
							<div id="show_image5" class="show_images"></div>
						</div>
					</div>
				</div>
				<div id="blank">여백</div> --%>


<!-- 여기서부터
팝업창입니다 -->


<!-- </main>

</body>

</html> -->