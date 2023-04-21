package InsertServlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import DataClass.*;
import DB.*;

/**
 * Servlet implementation class reviewServlet
 */
@WebServlet("/review")
public class reviewServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public reviewServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// response.getWriter().append("Served at: ").append(request.getContextPath());
		// System.out.println("리뷰 서블릿 호출");
		// 인코딩 지정
		request.setCharacterEncoding("utf-8");
		// 현재 파일 경로 저장
		String context = request.getContextPath();

		// DB객체 생성
		DB_Conn dao = new DB_Conn();

		// 리뷰데이터 모델클래스.
		reviewData sd = new reviewData();

		// 리뷰페이지에서 리뷰 내용을 가져와 DB에 입력
		// 각 페이지가 아직 연결되지 않아 세션가져오기 어려움
		// 임시로 아이디 입력칸 생성
		try {
			// 메뉴 리스트와 태그 리스트.
			String menuListView = request.getParameter("menuListView");
			String tagListView = request.getParameter("tagListView");

			// input 받아옴.
			String review_noname = request.getParameter("noname_check"); // null인가 아닌가.
			String review_store = request.getParameter("review_storeCode"); // request.getParameter("review_storeCode");
			String review_id = request.getParameter("review_input_id");
			String review_text = request.getParameter("review_text");
			String review_score = request.getParameter("score_result"); // request.getParameter("score_review");

			/*
			 * //받아온 결과 출력. System.out.println("menu: "+menuListView);
			 * System.out.println("tag: "+tagListView);
			 * 
			 * System.out.println("store: "+review_store);
			 * System.out.println("id: "+review_id);
			 * System.out.println("text: "+review_text);
			 * System.out.println("score: "+review_score); //true: value, false: null(익명체크)
			 * System.out.println("no: "+review_noname);
			 */
			if (!menuListView.equals("") && !tagListView.equals("") && !review_store.equals("") && !review_id.equals("")
					&& !review_text.equals("") && !review_score.equals("")) {
				// 데이터 reviewData 방식으로 저장
				sd.setStoreCode(review_store);
				sd.setUserId(review_id);
				sd.setContents(review_text);
				sd.setRating(review_score);
				sd.setPhotoPath("지금 사진등록을 못해");
				sd.setAnonymous(review_noname);

				// DB삽입
				dao.Insert_ReviewData(sd);
				dao.Insert_List(menuListView, review_id, tagListView, review_store);
				System.out.println("db 등록 끝");
			} else {
				alert(response, context);
			}
			response.sendRedirect(context + "/Store.jsp");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	// 리뷰 등록 실패 시 뜨는 alert 매서드
	public static void alert(HttpServletResponse response, String msg) {
		try {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter w = response.getWriter();
			w.write("<script>alert('" + "Empty content on the review"
					+ "');history.go(-1);</script>\" charset=\"utf-8\" </script>");
			w.flush();
			w.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
