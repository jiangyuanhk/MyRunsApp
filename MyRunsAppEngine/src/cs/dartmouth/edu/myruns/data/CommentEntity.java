package cs.dartmouth.edu.myruns.data;

import com.google.appengine.api.datastore.Text;

public class CommentEntity {
	public static String ENTITY_KIND_PARENT = "CommentParent";
	public static String ENTITY_PARENT_KEY = ENTITY_KIND_PARENT;
	public static String ENTITY_KIND_COMMENT = "Comment";

	
	
	public static String FIELD_NAME_CID = "ID";
	public static String FIELD_NAME_PID = "Post_ID";
	public static String FIELD_NAME_UID = "User_ID";
	public static String FIELD_NAME_CONTENT = "Content";
	public static String FIELD_NAME_TIME = "Time";
	

	public String mUserID;
	public String mCommentID;
	public String mPostID;
	
	public String mContent;
	public long mTime;
	
	
	

	public String getmContent() {
		return mContent;
	}
	public void setmContent(String mContent) {
		this.mContent = mContent;
	}
	public long getmTime() {
		return mTime;
	}
	public void setmTime(long mTime) {
		this.mTime = mTime;
	}
	public String getmUserID() {
		return mUserID;
	}
	public void setmUserID(String mUserID) {
		this.mUserID = mUserID;
	}
	public String getmCommentID() {
		return mCommentID;
	}
	public void setmCommentID(String mCommentID) {
		this.mCommentID = mCommentID;
	}
	public String getmPostID() {
		return mPostID;
	}
	public void setmPostID(String mPostID) {
		this.mPostID = mPostID;
	}
	
	
	
	
	
}
