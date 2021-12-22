package services.auth;

import java.sql.Connection;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import beans.Action;
import beans.Employee;

public class Authentication {
	private HttpServletRequest req;
	Employee emp;
	private HttpSession session; 


	public Authentication(HttpServletRequest req){
		this.req = req;

	}

	public Action backController(int jobCode) {
		Action action = null;
		switch(jobCode) {

		case 1 :
			action = this.accessCtl();
			break;
		case -1 :
			action = this.accessOutCtl();
			break;
		case 0 :
			action = this.afterAccess();
			break;

		default :
		}
		return action;
	}
	private Action afterAccess() {
		ArrayList<Employee> list = null;
		Action action = new Action();
		Employee emp = new Employee();
		DataAccessObject dao = new DataAccessObject();
		Connection conn = dao.getConnection();
		session = this.req.getSession();

		emp.setSeCode((String)session.getAttribute("seCode"));
		emp.setEmCode((String)session.getAttribute("emCode"));

		if((list = dao.getAccessInfo(emp)) != null) {
			req.setAttribute("accessInfo", list);
			action.setRedirect(false);
			action.setPage("main.jsp");
		}
		return action;
	}

	private Action accessCtl() {
		Action action = new Action();
		ArrayList<Employee> list = null;
		DataAccessObject dao = null;
		boolean tranState = false;
		//1. 클라이언트 데이터 --> Employee :: seCode, emCode, emPassword
		this.emp = new Employee();
		this.emp.setSeCode(this.req.getParameter("seCode"));
		this.emp.setEmCode(this.req.getParameter("emCode"));
		this.emp.setEmPassword(this.req.getParameter("emPassword"));
		this.emp.setType(9);


		/*2. DAO 연동*/
		dao = new DataAccessObject();
		Connection conn = dao.getConnection();
		dao.modifyTranStats(tranState);

		/* 2-1. STORES :: SECODE 존재 여부) */
		if(dao.isStore(emp)) {
			/* 2-2. EMPLOYEE :: EMCODE 존재 여부 */
			if(dao.isEmp(emp)) {
				/* 2-3. EMPLOYEE :: PASSWORD일치 여부 :: RETURN : 1 >> P2-4*/
				if(dao.isEmpPassword(emp)) {
					/* 2-4. ACCESSHISTORY :: INSERT :: RETURN :1 */
					if(dao.insAccessHistory(emp)) {
						tranState = true;
						session = this.req.getSession();
						session.setAttribute("seCode", emp.getSeCode());
						session.setAttribute("emCode", emp.getEmCode());
						/* 2-5. 정보 취합 --> ARRAYLIST<EMPLOYEE> != null 아닐때 로그인 성공*/
						if((list = dao.getAccessInfo(emp)) != null) {
							req.setAttribute("accessInfo", list);
						}
					}
				}
			}
		}

		/*  로그인 성공 :: main.jsp
		 *   로그인 실패 :: index.html */
		action.setRedirect(tranState? false : true);
		action.setPage(tranState? "main.jsp" : "index.html");

		dao.setTransaction(tranState);
		dao.modifyTranStats(tranState);
		dao.closeConnection();
		return action;
	}

	private Action accessOutCtl() {
		ArrayList<Employee> list = null;
		Action action = new Action();
		boolean tranState = false;
		DataAccessObject dao = null;
		HttpSession session = this.req.getSession();

		//1. 클라이언트 데이터 --> Employee : seCode, emCode
		this.emp = new Employee();
		this.emp.setSeCode(this.req.getParameter("seCode"));
		this.emp.setEmCode(this.req.getParameter("emCode"));
		this.emp.setType(-9);


		/* dao호출 */
		dao = new DataAccessObject();
		Connection conn = dao.getConnection();
		dao.modifyTranStats(tranState);

		if(dao.insAccessHistory(emp)) {
			tranState = true;
			session.invalidate();
		}

		action.setRedirect(tranState? true : false);
		action.setPage("index.html");

		return action;
	}

}
