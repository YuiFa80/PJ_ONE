package webpos;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import beans.Action;
import services.auth.Authentication;


@WebServlet({"/Access","/AccessOut", "/S"})
public class FrontController extends HttpServlet {
	private static final long serialVersionUID = 1L;


	public FrontController() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doProcess(request, response);


	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		this.doProcess(request, response);

	}
     /* 요청한 jobCode에 해당하는 서비스 분기 */
	private void doProcess(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String jobCode = (req.getRequestURI().substring(req.getContextPath().length()+1));
		Action action = null;
		
		HttpSession session = req.getSession();
		System.out.println(session.getAttribute("emCode"));
		
		Authentication auth = null;
        
		if(jobCode.equals("Access")) {
			auth = new Authentication(req);
			action = auth.backController(1);
		}
		else if(jobCode.equals("AccessOut")) {
			auth = new Authentication(req);
			action = auth.backController(-1);
		}else {
			if(session.getAttribute("seCode")!= null) {
				auth = new Authentication(req);
				action = auth.backController(0);
			}else {
			action = new Action();
			action.setRedirect(true);
			action.setPage("index.html");
			}

		}

		if(action.isRedirect()) {
			res.sendRedirect(action.getPage());
		}else {
			RequestDispatcher dp = req.getRequestDispatcher(action.getPage());
			dp.forward(req, res); //apache에 전달 exception처리해줌 

		}
        
	} 
}



