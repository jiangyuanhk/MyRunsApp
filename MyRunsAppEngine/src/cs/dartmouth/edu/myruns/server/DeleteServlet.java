package cs.dartmouth.edu.myruns.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.dartmouth.cs.gcmdemo.server.data.PostDatastore;

public class DeleteServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		String id = req.getParameter("ID");
		System.out.println(id);
		PostDatastore.delete(id);
		resp.sendRedirect("/sendmsg.do?ID="+id);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		doGet(req, resp);
	}
}
