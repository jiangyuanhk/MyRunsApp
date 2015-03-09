package cs.dartmouth.edu.myruns.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.dartmouth.cs.gcmdemo.gcm.Message;
import edu.dartmouth.cs.gcmdemo.gcm.Sender;
import edu.dartmouth.cs.gcmdemo.server.data.PostDatastore;
import edu.dartmouth.cs.gcmdemo.server.data.PostEntity;
import edu.dartmouth.cs.gcmdemo.server.data.RegDatastore;
import edu.dartmouth.cs.gcmdemo.server.data.UserDatastore;

public class GetHistoryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		String name = req.getParameter("new_hist");
		ArrayList<PostEntity> postList = PostDatastore.query();
		StringBuilder res = new StringBuilder();
		for(PostEntity post:postList){
			String id = post.getmUserID();
			res.append(id+"#");
			String uimg = UserDatastore.queryById(id);
			res.append(uimg+"#");
			String title = post.getmPostTitle();
			res.append(title+"#");
			String content = post.getmPostContent();
			res.append(content+"#");
			String postimg = post.getmPostPicture().getValue();
			res.append(postimg+"#");
			String postloc = post.getmPostLocation();
			res.append(postloc+"#");
			res.append("&");
		}
		res.deleteCharAt(res.length()-1);
		List<String> devices = RegDatastore.getDevices();

		Message message = new Message(devices);
		message.addData("message", res.toString());

		// Have to hard-coding the API key when creating the Sender
		Sender sender = new Sender(Globals.GCMAPIKEY);
		// Send the message to device, at most retrying MAX_RETRY times
		sender.send(res.toString(), 100);
		//resp.sendRedirect("/sendmsg.do?post="+res.toString());
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		doPost(req, resp);
	}

}
