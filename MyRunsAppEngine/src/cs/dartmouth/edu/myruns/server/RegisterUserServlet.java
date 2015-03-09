package cs.dartmouth.edu.myruns.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Text;

import edu.dartmouth.cs.gcmdemo.server.data.PostDatastore;
import edu.dartmouth.cs.gcmdemo.server.data.PostEntity;
import edu.dartmouth.cs.gcmdemo.server.data.UserDatastore;
import edu.dartmouth.cs.gcmdemo.server.data.UserEntity;

public class RegisterUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		String values = req.getParameter("new_user");
		String[] items = values.split("#");
		UserEntity newEntity= new UserEntity();
		newEntity.mUserID = items[0];
		newEntity.mUserImg = new Text(items[1]);
		newEntity.mUserRegTime = items[2];
		UserDatastore.add(newEntity);
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		doPost(req, resp);
	}

}
