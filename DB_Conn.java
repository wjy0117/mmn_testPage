package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import DataClass.Insert_joinData;
import DataClass.loginData;
import DataClass.menuData;
import DataClass.reviewData;
import DataClass.rtdCntData;
import DataClass.storeByTagDataPrint;
import DataClass.storeData;
import DataClass.tagData;
import DataClass.tagListData;
import DataClass.watchlistData;

public class DB_Conn {
	String _Sql;
	final int Max_FoodCode = 10001;

	Connection conn = null;

	HashMap<Integer, storeData> store_map = new HashMap<>();
	HashMap<Integer, menuData> menu_map = new HashMap<>();
	HashMap<Integer, rtdCntData> rtdCnt_map = new HashMap<>();

	public DB_Conn() {
		Connection();
	}

	public DB_Conn(String _Sql) {
		Connection();
		this._Sql = _Sql;
	}

	void Connection() {
		try {
			// mysql jdbc driver 로딩
			Class.forName("com.mysql.jdbc.Driver");

			// db연결 문자열 but 이방법은 보안에 취약하다. ..
			String url = "jdbc:mysql://192.168.250.44/mmn?characterEncoding=UTF-8&serverTimezone=UTC";
			String id = "junghan"; // mysql 접속아이디
			String pwd = "yeil!1234"; // mysql 접속 비번

//			String url = "jdbc:mysql://localhost/mmn?characterEncoding=UTF-8&serverTimezone=UTC";
//			String id = "root"; // mysql 접속아이디
//			String pwd = "1234"; // mysql 접속 비번

			// db 접속

			conn = DriverManager.getConnection(url, id, pwd);
			System.out.println("db접속 성공");
		} catch (Exception e) {
			// db관련작업은 반드시 익셉션 처리
			System.out.println("db접속 실패");
			e.printStackTrace();
		}
	}

	// 1.회원가입
	// db에서 회원정보를 삽입
	public void Insert_UserData(Insert_joinData _Data) {
		PreparedStatement pstmt = null; // SQL실행객체

		try {
			String sql = "INSERT INTO userTbl(userID, userPW, userName, userEmail, isMaster)" + " VALUES(?,?,?,?,?)";

			// sql 실행객체 생성
			pstmt = conn.prepareStatement(sql);

			// ? 에 입력될 값 매핑
			pstmt.setString(1, _Data.userID);
			pstmt.setString(2, _Data.userPW);
			pstmt.setString(3, _Data.userName);
			pstmt.setString(4, _Data.userEmail);
			pstmt.setString(5, _Data.isMaster);

			// executeQuery() select 명령어
			// executeUpdate select 이외 명령어
			pstmt.executeUpdate();

		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			// 리소스 정리작업
			try {
				if (pstmt != null) {
					pstmt.close();
				}

			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	// 2. 로그인 정보 매칭 확인
	// 0 : 마스터계정 로그인 성공 1 : 일반계정 로그인 성공 2: 비밀번호 다름 3: 없는 아이디
	public int loginMathcing(loginData _data) {
		try {
			Statement stmt = null;
			ResultSet res = null;
			stmt = conn.createStatement();
			String sql = "SELECT * FROM userTbl where userID = " + "'" + _data.userID + "'";
			res = stmt.executeQuery(sql);

			String userPW = null;
			String isMaster = null;

			// 아이디가 존재하면
			while (res.next()) {
				// DB의 패스워드와 마스터여부를 가져와서 비교한다.
				userPW = res.getString("userPW");
				isMaster = res.getString("isMaster");
			}

			// userPW에 입력된 값이 있다면
			if (userPW != null) {
				// 입력된 패스워드와 데이터베이스의 패스워드가 일치한다면
				if (userPW.equals(_data.userPW)) {
					// 만약 마스터 계정이면 0을 리턴한다.
					if (isMaster.equals("Y")) {
						return 0;
						// 마스터 계정이 아니면 1을 리턴한다.
					} else {
						return 1;
					}
					// 입력된 패스워드와 데이터베이스의 패스워드가 일치하지 않다면 2를 리턴한다.
				} else {
					return 2;
				}
			}

			// 입력된 패스워드가 없다면 3을 리턴한다.
			return 3;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 12;
	}

	// 3. 스토어정보 hashmap 만들기
	// HashMap인 store_map을 만들어간다.
	// DB에서 스토어테이블의 각각의 컬럼 정보를 가져온다.
	public void constructStoreMap() {
		Statement stmt = null;
		ResultSet res = null;
		try {
			stmt = conn.createStatement();
			String sql = "SELECT * FROM storeTbl";
			res = stmt.executeQuery(sql);

			while (res.next()) {
				int storeCode = res.getInt("storeCode");
				String storeName = res.getString("storeName");
				int cateCode = res.getInt("cateCode");
				String openAt = res.getString("openAt");
				String closeAt = res.getString("closeAt");
				String offDays = res.getString("offDays");
				String lastOrder = res.getString("lastOrder");
				String phone = res.getString("phone");
				String addr = res.getString("addr");
				String parking = res.getString("parking");
				String storeImgPath = res.getString("storeImagePath");
				String web = res.getString("web");
				String breakStart = res.getString("breakStart");
				String breakEnd = res.getString("breakEnd");

				// storeDate 클래스의 객체를 생성한다.
				storeData sd = new storeData(storeCode, storeName, cateCode, openAt, closeAt, offDays, lastOrder, phone,
						addr, parking, storeImgPath, web, breakStart, breakEnd);
				// store_map의 키값인 storeCode와 value인 storeData의 객체를 집어넣는다.
				store_map.put(storeCode, sd);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null)
					res.close();
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 4. 메뉴 hashmap 만들기
	// 파라미터 가게코드
	// HashMap 인 menu_map을 만들어간다.
	public void constructMenuMap(int store) {
		Statement stmt = null;
		ResultSet res = null;
		try {
			stmt = conn.createStatement();
			// SELECT * FROM menuTbl where menutbl.storeCode = 1;
			String sql = "SELECT * FROM menuTbl where menutbl.storeCode = " + store;
			res = stmt.executeQuery(sql);

			while (res.next()) {
				int storeCode = res.getInt("storeCode");
				int foodCode = res.getInt("foodCode");
				String foodName = res.getString("foodName");
				int price = res.getInt("price");

				// 클래스 menuData의 객체를 생성한다.
				menuData md = new menuData(storeCode, foodCode, foodName, price);
				// menu_map에 value인 위 객체와 키값인 foodCode를 집어넣는다.
				menu_map.put(foodCode, md);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null)
					res.close();
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 5. 가장많이 먹은 음식 카운트 (후보 1)
	// tmp는 음식 먹은 횟수
	// RtdCnt ReviewTargetData Count
	// HashMap인 rtdCnt_map을 만든다.
	public void constructRtdCnt_map() {
		Statement stmt = null;
		ResultSet res = null;
		try {
			stmt = conn.createStatement();
			String sql = "SELECT * FROM reviewTargetTbl";
			res = stmt.executeQuery(sql);

			// tmp는 카운트 변수이다. 예를 들어, foodCode = 1 일때, tmp[1]은 foodCode가 1인 리뷰타겟의 개수이다.
			// Max_FoodCode는 무수히 큰 적당한 수(10001)로 대입된 변수다. 모든 foodCode를 표현하기 위함이다.
			int[] tmp = new int[Max_FoodCode];

			while (res.next()) {
				int foodCode = res.getInt("foodCode");

				// foodCode일때 배열 값을 1씩 증가시킨다. 리뷰 타겟의 개수를 증가시키는 것과 같다.
				tmp[foodCode]++;
			}

			// 모든 foodCode를 순회한다.
			for (int i = 0; i < Max_FoodCode; i++) {
				if (tmp[i] == 0)
					continue;
				// rtdCntData는 reviewTargetData의 개수를 세는 클래스다.
				// i는 foodCode이고 tmp[i]는 카운트 변수다.
				rtdCntData rcd = new rtdCntData(i, tmp[i]);
				// rtdCnt_map 을 construct 해 간다.
				rtdCnt_map.put(i, rcd);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null)
					res.close();
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 6. 푸드코드에 맞는 메뉴명 리턴.
	// foodCode가 주어졌을 때 음식 이름을 리턴한다.
	public String getFoodName(int foodCode) {
		Statement stmt = null;
		ResultSet res = null;
		String foodName = "";
		try {
			stmt = conn.createStatement();
			// 메뉴테이블에서 foodCode 를 입력해 메뉴들을 가져온다.
			String sql = "SELECT * FROM menuTbl Where foodCode = " + foodCode;
			res = stmt.executeQuery(sql);
			while (res.next()) {
				// 음식 이름을 foodName에 입력시킨다.
				foodName = res.getString("foodName");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null)
					res.close();
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return foodName;
	}

	// 7. 평균평점을 리턴하는 함수
	public double getAverageRating(int storeCode) {
		// 평점의 합
		int ratingSum = 0;
		// 리뷰의 개수
		int cnt = 0;

		Statement stmt = null;
		ResultSet res = null;
		try {
			stmt = conn.createStatement();
			String sql = "SELECT * FROM reviewTbl Where storeCode = " + storeCode;
			res = stmt.executeQuery(sql);

			// 가게코드가 storeCode인 리뷰들을 순회한다.
			while (res.next()) {
				// 평점에 해당하는 값을 변수 rating에 입력시킨다.
				int rating = res.getInt("rating");
				// 평점을 모두 더해준다.
				ratingSum += rating;
				// 리뷰의 개수
				cnt++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null)
					res.close();
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 가게코드가 storeCode인 리뷰가 하나도 없다면 음수를 리턴한다.
		if (cnt == 0)
			return -1;

		return ((double) ratingSum) / ((double) cnt);
	}

	// 8. HashMap인 store_map을 ArrayList로 바꿔준다.
	public ArrayList<storeData> storefindAll() {
		return new ArrayList<>(store_map.values());
	}

	// 9. HashMap인 menu_map을 ArrayList로 바꿔준다.
	public ArrayList<menuData> menufindAll() {
		return new ArrayList<>(menu_map.values());
	}

	// 10. HashMap인 rtdCnt_map을 ArrayList로 바꿔준다.
	public ArrayList<rtdCntData> rtdCntfindAll() {
		return new ArrayList<>(rtdCnt_map.values());
	}

	// 11. 가게 코드로 가게정보 가져와서 클래스에 담는다.
	public storeData getStoreData(int storeCode) {
		storeData sd = new storeData();

		Statement stmt = null;
		ResultSet res = null;
		try {
			String sql = "SELECT * FROM storeTbl where storeCode = " + storeCode;
			stmt = conn.createStatement();
			res = stmt.executeQuery(sql);

			while (res.next()) {
				String storeName = res.getString("storeName");
				int cateCode = res.getInt("cateCode");
				String openAt = res.getString("openAt");
				String closeAt = res.getString("closeAt");
				String offDays = res.getString("offDays");
				String lastOrder = res.getString("lastOrder");
				String phone = res.getString("phone");
				String addr = res.getString("addr");
				String parking = res.getString("parking");
				String storeImgPath = res.getString("storeImagePath");
				String web = res.getString("web");
				String breakStart = res.getString("breakStart");
				String breakEnd = res.getString("breakEnd");

				// storeData 클래스의 객체를 생성한다.
				sd = new storeData(storeCode, storeName, cateCode, openAt, closeAt, offDays, lastOrder, phone, addr,
						parking, storeImgPath, web, breakStart, breakEnd);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null)
					res.close();
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return sd;
	}

	// 12. 메인 페이지에서 메뉴 검색을 할 때 storeData ArrayList를 가져온다.(자동완성 & 가게정보 가져오기)
	// query가 검색한 내용
	public ArrayList<storeData> getMenuInfo(String query) {
		ArrayList<storeData> list = new ArrayList<storeData>();
		String[] tmp = query.split(" ");

		Statement stmt = null;
		ResultSet res = null;
		try {
			// like가 sql문에서 특정단어가 포함된 단어로 검색.
			String sql = "SELECT * FROM menuTbl WHERE foodName LIKE \"%";
			for (int i = 0; i < tmp.length; i++) {
				sql += tmp[i] + "%";
			}
			sql += "\"";

			stmt = conn.createStatement();
			res = stmt.executeQuery(sql);
			while (res.next()) {
				list.add(getStoreData(res.getInt("storeCode")));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null)
					res.close();
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return list;
	}

	// 13. 메인페이지에서 가게검색을 할 때 storeData ArrayList를 가져온다.(자동완성 & 가게정보 가져오기)
	public ArrayList<storeData> getStoreInfo(String query) {
		ArrayList<storeData> list = new ArrayList<storeData>();
		// 공백마다 여러개의 단어로 검색 가능, And 검색(둘 다 포함)
		String[] tmp = query.split(" ");

		Statement stmt = null;
		ResultSet res = null;
		try {
			// like가 sql문에서 특정단어가 포함된 단어로 검색.
			String sql = "SELECT * FROM storeTbl WHERE storeName LIKE \"%";
			for (int i = 0; i < tmp.length; i++) {
				sql += tmp[i] + "%";
			}
			sql += "\"";
			stmt = conn.createStatement();
			res = stmt.executeQuery(sql);
			while (res.next()) {
				int storeCode = res.getInt("storeCode");
				String storeName = res.getString("storeName");
				int cateCode = res.getInt("cateCode");
				String openAt = res.getString("openAt");
				String closeAt = res.getString("closeAt");
				String offDays = res.getString("offDays");
				String lastOrder = res.getString("lastOrder");
				String phone = res.getString("phone");
				String addr = res.getString("addr");
				String parking = res.getString("parking");
				String storeImgPath = res.getString("storeImagePath");
				String web = res.getString("web");
				String breakStart = res.getString("breakStart");
				String breakEnd = res.getString("breakEnd");

				// storeData 클래스의 객체를 생성한다.
				storeData sd = new storeData(storeCode, storeName, cateCode, openAt, closeAt, offDays, lastOrder, phone,
						addr, parking, storeImgPath, web, breakStart, breakEnd);

				list.add(sd);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null)
					res.close();
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return list;
	}

	// 14. 메인페이지에서 태그검색을 할 때 tagData ArrayList를 가져온다.(자동완성)
	public ArrayList<tagData> getTagInfo(String query) {
		ArrayList<tagData> list = new ArrayList<tagData>();
		String[] tmp = query.split(" ");

		Statement stmt = null;
		ResultSet res = null;
		try {
			String sql = "SELECT * FROM tagTbl WHERE tagName LIKE \"%";
			for (int i = 0; i < tmp.length; i++) {
				sql += tmp[i] + "%";
			}
			sql += "\"";

			stmt = conn.createStatement();
			res = stmt.executeQuery(sql);
			while (res.next()) {
				tagData td = new tagData();
				td.setTagId(res.getInt("tagID"));
				td.setTagName(res.getString("tagName"));
				td.setTagViews(res.getInt("tagView"));

				list.add(td);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null)
					res.close();
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return list;
	}

	// 15. 관심목록에 가게데이터를 추가한다.
	// userID와 storeCode를 기본키로 사용하여 삽입
	public void addWatchlistInfo(String userID, int storeCode) {
		PreparedStatement pstmt = null;

		try {
			String sql = "INSERT INTO watchlistTbl values (?, ?)";

			// sql 실행객체 생성
			pstmt = conn.prepareStatement(sql);

			// ? 에 입력될 값 매핑
			pstmt.setString(1, userID);
			pstmt.setInt(2, storeCode);

			// executeQuery() select 명령어
			// executeUpdate select 이외 명령어
			pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}

			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	// 16. 관심목록에 가게데이터를 삭제한다.
	// userID와 storeCode를 기본키로 사용하여 삭제
	public void deleteWatchlistInfo(String userID, int storeCode) {

		PreparedStatement pstmt = null;
		try {
			String sql = "delete from watchlistTbl where userID = '" + userID + "' and storeCode =" + storeCode;
			pstmt = conn.prepareStatement(sql);

			// executeQuery() select 명령어
			// executeUpdate select 이외 명령어
			pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}

			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	// 17. 해당 유저의 관심목록에 등록된 가게데이터를 ArrayList로 가져온다.
	public ArrayList<watchlistData> getWatchListInfo(String userID) {
		ArrayList<watchlistData> list = new ArrayList<watchlistData>();

		Statement stmt = null;
		ResultSet res = null;
		try {
			String sql = "SELECT * FROM watchlistTbl WHERE userID = '" + userID + "'";

			stmt = conn.createStatement();
			res = stmt.executeQuery(sql);
			while (res.next()) {
				int storeCode = res.getInt("storeCode");

				watchlistData wd = new watchlistData(userID, storeCode);
				list.add(wd);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null)
					res.close();
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return list;
	}

	// 18. 카테고리 코드에 맞는 카테고리 이름을 가져온다.
	public String getCategoryName(int cateCode) {
		String ret = "";
		Statement stmt = null;
		ResultSet res = null;
		try {
			String sql = "SELECT * FROM categoryTbl WHERE cateCode =" + cateCode;

			stmt = conn.createStatement();
			res = stmt.executeQuery(sql);
			while (res.next()) {
				ret = res.getString("cateName");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null)
					res.close();
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return ret;
	}

	// 19. 태그페이지에서 태그별 가게정보를 표시한다.
	public ArrayList<storeByTagDataPrint> getStoreListByTag(int tagID, String userID) {
		ArrayList<storeByTagDataPrint> ret = new ArrayList<>();

		Statement stmt = null;
		ResultSet res = null;
		try {
			String sql = "SELECT * FROM tag_storeTbl where tagID = " + tagID;
			stmt = conn.createStatement();
			res = stmt.executeQuery(sql);

			while (res.next()) {
				// ArrayList에 storeByTagDataPrint방식으로 추가하기 위한 객체
				storeByTagDataPrint sbdp = new storeByTagDataPrint();
				int storeCode = res.getInt("storeCode");
				// 가게정보 중 일부(이미지경로, 업종 코드 등)를 가져오기 위한 객체
				storeData sd = getStoreData(storeCode);

				sbdp.setStoreCode(storeCode);
				sbdp.setStoreImgPath(sd.getStoreImgPath());
				sbdp.setCateName(getCategoryName(sd.getCateCode()));
				sbdp.setAverageRating(getAverageRating(storeCode));
				sbdp.setStoreName(sd.getStoreName());
				sbdp.setTagID(tagID);
				sbdp.setWatchlist(haveWatchlist(userID, storeCode));
				sbdp.setRd(getReviewByStoreCode(storeCode));
				sbdp.setAddr(sd.getAddr());
				sbdp.setNickName(getNickname(userID));

				ret.add(sbdp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null)
					res.close();
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Collections.sort(ret);

		return ret;
	}

	// 20. 메인페이지에서 가게이미지 hover했을 때 가게정보를 띄워준다.
	public ArrayList<tagListData> getTagListByTag(int tagID) {
		ArrayList<tagListData> ret = new ArrayList<>();

		Statement stmt = null;
		ResultSet res = null;
		try {
			String sql = "SELECT * FROM tag_storeTbl where tagID = " + tagID;
			stmt = conn.createStatement();
			res = stmt.executeQuery(sql);

			while (res.next()) {
				tagListData tld = new tagListData();
				int storeCode = res.getInt("storeCode");

				storeData sd = getStoreData(storeCode);
				reviewData rd = getReviewByStoreCode(storeCode);

				tld.setAverageRating(getAverageRating(storeCode));
				tld.setStoreImagePath(sd.getStoreImgPath());
				tld.setStoreName(sd.getStoreName());
				tld.setReviewContent(rd.getContents());
				tld.setStoreCode(sd.getStoreCode());

				ret.add(tld);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null)
					res.close();
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Collections.sort(ret);

		return ret;
	}

	// 21. 관심목록 null 체크
	public boolean haveWatchlist(String userID, int storeCode) {
		Statement stmt = null;
		ResultSet res = null;
		boolean ret = false;
		try {
			String sql = "SELECT * FROM watchlistTbl WHERE userID = '" + userID + "'" + "and storeCode = " + storeCode;

			stmt = conn.createStatement();
			res = stmt.executeQuery(sql);
			while (res.next()) {
				ret = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null)
					res.close();
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return ret;
	}

	// 22. 태그페이지에서 리뷰의 일부를 표시하기 위한 데이터를 가져온다.
	public reviewData getReviewByStoreCode(int storeCode) {
		reviewData rd = new reviewData();

		Statement stmt = null;
		ResultSet res = null;
		try {
			String sql = "SELECT * FROM reviewTbl where storeCode = " + storeCode;
			stmt = conn.createStatement();
			res = stmt.executeQuery(sql);

			while (res.next()) {
				rd.setUserId(res.getString("userID"));
				rd.setContents(res.getString("contents"));
				rd.setStoreCode("" + storeCode);
				rd.setRating("" + res.getInt("rating"));
				rd.setPhotoPath(res.getString("PhotoPath"));
				rd.setIndex(res.getInt("reviewIndex"));
				rd.setDisplay(res.getString("display"));
				rd.setDate(res.getString("regDate"));
				rd.setAnonymous(res.getString("anonymous"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null)
					res.close();
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return rd;
	}

	// 23. 태그페이지에서 유저명을 표시하기 위한 데이터를 가져온다.
	public String getNickname(String userID) {
		String ret = "";

		Statement stmt = null;
		ResultSet res = null;
		try {
			String sql = "SELECT * FROM userTbl where userID = '" + userID + "'";
			stmt = conn.createStatement();
			res = stmt.executeQuery(sql);

			while (res.next()) {
				ret = res.getString("userName");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null)
					res.close();
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return ret;
	}

	// 24. 태그페이지에서 유저프로필 사진을 표시하기 위한 데이터를 가져온다.
	public String getUserImagePath(String userID) {
		String ret = "";

		Statement stmt = null;
		ResultSet res = null;
		try {
			String sql = "SELECT * FROM userTbl where userID = '" + userID + "'";
			stmt = conn.createStatement();
			res = stmt.executeQuery(sql);

			while (res.next()) {
				ret = res.getString("userImagePath");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null)
					res.close();
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return ret;
	}

	// 25. 태그페이지의 태그제목 표시를 위한 데이터를 가져온다.
	public String getTagName(int tagID) {
		String ret = "";

		Statement stmt = null;
		ResultSet res = null;
		try {
			String sql = "SELECT * FROM tagTbl where tagID = " + tagID;
			stmt = conn.createStatement();
			res = stmt.executeQuery(sql);

			while (res.next()) {
				ret = res.getString("tagName");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null)
					res.close();
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return ret;
	}

	// 26. 태그페이지에서 정렬을 위한 데이터를 가져온다.
	public ArrayList<tagData> getTagDataList() {
		ArrayList<tagData> ret = new ArrayList<>();

		Statement stmt = null;
		ResultSet res = null;
		try {
			String sql = "SELECT * FROM tagTbl";
			stmt = conn.createStatement();
			res = stmt.executeQuery(sql);

			while (res.next()) {
				tagData tmp = new tagData();
				tmp.setTagId(res.getInt("tagID"));
				tmp.setTagName(res.getString("tagName"));
				tmp.setTagViews(res.getInt("tagView"));

				ret.add(tmp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null)
					res.close();
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return ret;
	}

	// 27. 가게페이지에서 리뷰내용 db에 입력/저장하는 메소드(리뷰데이터)
	// userid로 username 추출해서 표시 예정
	public void Insert_ReviewData(reviewData _Data) {
		PreparedStatement pstmt = null; // SQL실행객체

		try {
			// anonymous으로 리뷰작성 시 익명 체크 시 value값으로 null체크하여 1(true), 체크 안한 경우 null값으로 디폴트로
			// 0입력
			if (_Data.anonymous == null) {
				String sql = "insert into reviewtbl(storeCode, userId, contents, rating, photoPath)"
						+ " VALUES(?,?,?,?,null)";

				// sql 실행객체 생성
				pstmt = conn.prepareStatement(sql);

				// ? 에 입력될 값 매핑
				pstmt.setString(1, _Data.storeCode);
				pstmt.setString(2, _Data.userID);
				pstmt.setString(3, _Data.contents);
				pstmt.setString(4, _Data.rating);
				// pstmt.setString(5, _Data.photoPath);

				pstmt.executeUpdate();
			} else {
				String sql = "insert into reviewtbl(storeCode, userId, contents, rating, anonymous, photoPath)"
						+ " VALUES(?,?,?,?,true,null)";

				// sql 실행객체 생성
				pstmt = conn.prepareStatement(sql);

				// ? 에 입력될 값 매핑
				pstmt.setString(1, _Data.storeCode);
				pstmt.setString(2, _Data.userID);
				pstmt.setString(3, _Data.contents);
				pstmt.setString(4, _Data.rating);
				// pstmt.setString(6, _Data.photoPath);

				pstmt.executeUpdate();
			}

		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			// 리소스 정리작업
			try {
				if (pstmt != null) {
					pstmt.close();
				}

			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	// 28. 가게페이지에서 메뉴리스트와 태그리스트를 DB에 입력/저장하는 메소드
	public void Insert_List(String menuList, String uid, String tagList, String review_store) {
		// TODO Auto-generated method stub
		PreparedStatement pstmt = null;

		String[] mli = menuList.split(",");
		String[] tli = tagList.split(",");
		String msql, tsql;
		try {
			// 메뉴 리스트 입력
			for (int i = 0; i < mli.length; i++) {
				// 해당 유저가 가장 최근에 작성한 리뷰데이터에 메뉴정보가 입력되도록 order by로 정렬하여 데이터 입력
				msql = "insert into reviewtargettbl(_index, foodCode) values ((select reviewtbl.reviewIndex from reviewtbl where userId='"
						+ uid + "' "
						+ "order by regDate desc limit 0, 1), (select menutbl.foodCode from menutbl where (menutbl.foodName = '"
						+ mli[i] + "'" + " and storeCode = " + review_store + ")))";
				pstmt = conn.prepareStatement(msql);
				pstmt.executeUpdate();

			}
			// 태그 리스트 입력
			for (int i = 0; i < tli.length; i++) {
				tsql = "insert into tag_storetbl(storeCode, tagID) values ((select tagtbl.tagID from tagtbl where tagName = '"
						+ tli[i] + "'), " + review_store + ")";
				pstmt = conn.prepareStatement(tsql);
				pstmt.executeUpdate();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 리소스 정리작업
			try {
				pstmt.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	// 29. 가게페이지에서 리뷰내용 출력을 위한 정보 조회하는 메소드(리뷰 tbl)
	//사용하는 리뷰 칼럼 : reviewIndex, userId, contents, regDate, rating, anonymous, photoPath
	//for문으로 해당 함수를 호출하여 여러 개의 리뷰 레코드 반환
	public ArrayList<String> get_ReviewData(int i, int sf) {
		ArrayList<String> arr = new ArrayList<>();
		Statement stmt = null;
		ResultSet res = null;
		String sql = null;

		try {
			//전달받은 변수에 따라 평점+최신 순 또는 전체 최신순으로 1개씩 리뷰 레코드 리턴
			switch (sf) {
			case 5:
				sql = "select reviewIndex, userId, contents, regDate, rating, anonymous, photoPath from mmn.reviewtbl where rating = 5 order by regDate desc limit "
						+ i + ", 1;";
				break;
			case 4:
				sql = "select reviewIndex, userId, contents, regDate, rating, anonymous, photoPath from mmn.reviewtbl where rating = 4 order by regDate desc limit "
						+ i + ", 1;";
				break;
			case 3:
				sql = "select reviewIndex, userId, contents, regDate, rating, anonymous, photoPath from mmn.reviewtbl where rating = 3 order by regDate desc limit "
						+ i + ", 1;";
				break;
			//5,4,3 이외의 모든 값(0도 포함)
			default:
				sql = "select reviewIndex, userId, contents, regDate, rating, anonymous, photoPath from reviewtbl order by regDate desc limit "
						+ i + ", 1";
			}
			// 리뷰내용조회 쿼리문

			// sql 실행객체 생성
			stmt = conn.createStatement();
			// select문 실행 명령어
			res = stmt.executeQuery(sql);

			while (res.next()) {
				// reviewData rd = new reviewData();
				// rd.setIndex(res.getInt("reviewIndex"));
				// rd.setContents(res.getString("contents"));
				// rd.setDate(res.getString("regDate"));
				// rd.setRating(res.getString("rating"));
				// rd.setAnonymous(res.getString("anonymous"));
				// rd.setPhotoPath(res.getString("photoPath"));

				arr.add(res.getString("reviewIndex"));
				arr.add(res.getString("userId"));
				arr.add(res.getString("contents"));
				arr.add(res.getString("regDate"));
				switch (res.getString("rating")) {
				case "5":
					arr.add("억수로 마싯다");
					break;
				case "4":
					arr.add("갠찮드라");
					break;
				case "3":
					arr.add("영 파이다");
					break;
				default:
					arr.add("평가없음");
				}
				arr.add(res.getString("anonymous"));
				arr.add(res.getString("photoPath"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null) {
					res.close();
				}
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 가져온 정보 리턴
		return arr;
	}

	// 30. 리뷰내용 출력을 위해 리뷰인덱스 받아서 리뷰타겟(메뉴)를 가져온다.(menu list)
	// 기록) (제거)storeCode와 reviewIndex
	public ArrayList<String> get_ReviewTarget(int ri) {
		ArrayList<String> arr = new ArrayList<>();
		Statement stmt = null;
		ResultSet res = null;
		try {
			// String sql = "select * from mmn.reviewtbl order by regDate desc limit "+i+",
			// 5;";
			String sql = "SELECT menutbl.foodName FROM mmn.menutbl, mmn.reviewtargettbl where menutbl.foodCode = reviewtargettbl.foodCode and reviewtargettbl._index = "
					+ ri;
			// sql 실행객체 생성
			stmt = conn.createStatement();
			// select문 실행 명령어
			res = stmt.executeQuery(sql);

			while (res.next()) {
				arr.add(res.getString("foodName"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null) {
					res.close();
				}
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 가져온 정보 리턴
		return arr;
	}

	/*
	 * SELECT reviewtbl.reviewIndex FROM mmn.reviewtbl order by regDate desc limit
	 * 0, 1; select count(*) from mmn.reviewtbl where rating = 5; SELECT
	 * reviewtbl.reviewIndex FROM mmn.reviewtbl where reviewtbl.reviewIndex =
	 * (select count(*) from mmn.reviewtbl where rating = 5);
	 * 
	 * select count(*) from mmn.reviewtbl where rating = 4; SELECT
	 * reviewtbl.reviewIndex FROM mmn.reviewtbl where reviewtbl.reviewIndex =
	 * (select count(*) from mmn.reviewtbl where rating = 4);
	 * 
	 * select count(*) from mmn.reviewtbl where rating = 3; SELECT
	 * reviewtbl.reviewIndex FROM mmn.reviewtbl where reviewtbl.reviewIndex =
	 * (select count(*) from mmn.reviewtbl where rating = 3);
	 */

	// 31. 리뷰 개수 카운트하는 함수
	public int get_review_count_All() {
		Statement stmt = null;
		ResultSet res = null;
		int reviewcount = 0;
		try {
			// sql 실행객체 생성
			stmt = conn.createStatement();
			String sql_all = "select count(*)as count from mmn.reviewtbl";
			// select문 실행 명령어
			res = stmt.executeQuery(sql_all);
			while (res.next()) {
				reviewcount = res.getInt("reviewIndex");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null) {
					res.close();
				}
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return reviewcount;
	}

	// 32~34. 평점별 리뷰 개수 카운트
	// 32. great
	public int get_review_count_great() {
		Statement stmt = null;
		ResultSet res = null;
		int reviewcount = 0;
		try {
			// sql 실행객체 생성
			stmt = conn.createStatement();
			String sql_all = "select count(*)as count from mmn.reviewtbl where rating = 5";
			// select문 실행 명령어
			res = stmt.executeQuery(sql_all);
			while (res.next()) {
				reviewcount = res.getInt("count");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null) {
					res.close();
				}
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return reviewcount;
	}

	// 33. good
	public int get_review_count_good() {
		Statement stmt = null;
		ResultSet res = null;
		int reviewcount = 0;
		try {
			// sql 실행객체 생성
			stmt = conn.createStatement();
			String sql_all = "select count(*)as count from mmn.reviewtbl where rating = 4";
			// select문 실행 명령어
			res = stmt.executeQuery(sql_all);
			while (res.next()) {
				reviewcount = res.getInt("count");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null) {
					res.close();
				}
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return reviewcount;
	}

	// 34. bad
	public int get_review_count_bad() {
		Statement stmt = null;
		ResultSet res = null;
		int reviewcount = 0;
		try {
			// sql 실행객체 생성
			stmt = conn.createStatement();
			String sql_all = "select count(*)as count from mmn.reviewtbl where rating = 3";
			// select문 실행 명령어
			res = stmt.executeQuery(sql_all);
			while (res.next()) {
				reviewcount = res.getInt("count");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null) {
					res.close();
				}
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return reviewcount;
	}

	// 35. 리뷰 정보를 통해 가게페이지에서 가게별 가장많이 먹은 메뉴를 보여주기 위한 메뉴 개수를 카운트하여 리턴
	// td.setTagId(res.getInt("tagID"));
	// td.setTagName(res.getString("tagName"));
	// td.setTagViews(res.getInt("tagView"));
	public String get_menuCount() {
		String max_menu = null;
		Statement stmt = null;
		ResultSet res = null;
		try {
			// sql 실행객체 생성
			stmt = conn.createStatement();
			String sql = "select menutbl.foodName, count(*)as foodCount from menutbl, reviewtargettbl where menutbl.foodCode = reviewtargettbl.foodCode group by menutbl.foodName order by foodCount desc  limit 0, 1;";
			// select문 실행 명령어
			res = stmt.executeQuery(sql);
			while (res.next()) {
				// reviewcount = res.getInt("reviewIndex");
				max_menu = res.getString("foodName");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null) {
					res.close();
				}
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return max_menu;
	}

	// 36. 가게 코드를 통해 가게페이지에서 가장많이 사용한 태그명을 3개 추출
	public ArrayList<String> get_tagCount(int storeCode) {
		ArrayList<String> max_tag = new ArrayList<>();
		Statement stmt = null;
		ResultSet res = null;
		try {
			// sql 실행객체 생성
			stmt = conn.createStatement();
			String sql = "SELECT tagtbl.* FROM mmn.tagtbl where tagID = (select tag_storetbl.tagID from mmn.tag_storetbl where storeCode = "+storeCode+")";
			// select문 실행 명령어
			res = stmt.executeQuery(sql);
			while (res.next()) {
				// reviewcount = res.getInt("reviewIndex");
				max_tag.add(res.getString("tagName"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (res != null) {
					res.close();
				}
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return max_tag;
	}
}
