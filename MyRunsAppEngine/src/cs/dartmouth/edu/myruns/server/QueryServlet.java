package cs.dartmouth.edu.myruns.server;

import java.io.IOException;
import java.util.ArrayList;


import edu.dartmouth.cs.gcmdemo.server.data.PostDatastore;
import edu.dartmouth.cs.gcmdemo.server.data.PostEntity;
import edu.dartmouth.cs.gcmdemo.server.data.UserDatastore;
import edu.dartmouth.cs.gcmdemo.server.data.UserEntity;

public class QueryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		ArrayList<PostEntity> postList = PostDatastore.query();
		ArrayList<UserEntity> userList = UserDatastore.query();
		
		req.setAttribute("postList", postList);
		req.setAttribute("userList", userList);
		
		getServletContext().getRequestDispatcher("/main.jsp").forward(req, resp);
		
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		doPost(req, resp);
	}
	
}
