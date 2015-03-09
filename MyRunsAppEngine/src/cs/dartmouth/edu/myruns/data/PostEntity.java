package cs.dartmouth.edu.myruns.data;

import java.util.Date;

import com.google.appengine.api.datastore.Text;

public class PostEntity {
	public static String ENTITY_KIND_PARENT = "PostParent";
	public static String ENTITY_PARENT_KEY = ENTITY_KIND_PARENT;
	public static String ENTITY_KIND_POST = "Post";

	public static String FIELD_NAME_UID = "UID";
	//public static String FIELD_NAME_PID = "PID";
	public static String FIELD_NAME_UIMG = "Image";
	public static String FIELD_NAME_TITLE = "Title";
	public static String FIELD_NAME_CONTENT = "Content";
	public static String FIELD_NAME_TIME = "Time";
	public static String FIELD_NAME_PICTURE = "Picture";
	public static String FIELD_NAME_LOCATION = "Location";

	public String mUserID;
	//public String mPostID;
	public Text mUserImg;
	public String mPostTitle;
	public String mPostContent;
	public Long mPostTime;
	public Text mPostPicture;
	public String mPostLocation;
	
	
	public String getmPostContent() {
		return mPostContent;
	}

	public void setmPostContent(String mPostContent) {
		this.mPostContent = mPostContent;
	}

	public Text getmUserImg() {
		return mUserImg;
	}

	public void setmUserImg(Text mUserImg) {
		this.mUserImg = mUserImg;
	}
	public Long getmPostTime() {
		return mPostTime;
	}

	public void setmPostTime(Long mPostTime) {
		this.mPostTime = mPostTime;
	}

	public String getmUserID() {
		return mUserID;
	}

	public void setmUserID(String mUserID) {
		this.mUserID = mUserID;
	}

	public String getmPostTitle() {
		return mPostTitle;
	}

	public void setmPostTitle(String mPostTitle) {
		this.mPostTitle = mPostTitle;
	}

	public Text getmPostPicture() {
		return mPostPicture;
	}

	public void setmPostPicture(Text mPostPicture) {
		this.mPostPicture = mPostPicture;
	}

	public String getmPostLocation() {
		return mPostLocation;
	}

	public void setmPostLocation(String mPostLocation) {
		this.mPostLocation = mPostLocation;
	}
	
	

}
