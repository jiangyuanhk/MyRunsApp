package cs.dartmouth.edu.myruns.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import com.google.appengine.api.datastore.Text;

import edu.dartmouth.cs.gcmdemo.server.data.PostDatastore;
import edu.dartmouth.cs.gcmdemo.server.data.PostEntity;

public class PostServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		String values = req.getParameter("new_post");
		String[] items = values.split("#");
		PostEntity newEntity= new PostEntity();
		newEntity.mUserID = items[0];
		newEntity.mUserImg = new Text(items[1]);
		newEntity.mPostTitle = items[2];
		newEntity.mPostContent = items[3];
		newEntity.mPostTime = Long.parseLong(items[4]);
		newEntity.mPostPicture = new Text(items[5]);
		newEntity.mPostLocation = items[6];
		PostDatastore.add(newEntity);
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		doPost(req, resp);
	}
}
