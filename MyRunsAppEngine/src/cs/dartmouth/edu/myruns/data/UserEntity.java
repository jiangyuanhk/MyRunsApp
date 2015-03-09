package cs.dartmouth.edu.myruns.data;

import com.google.appengine.api.datastore.Text;

public class UserEntity {
	public static String ENTITY_KIND_PARENT = "UserParent";
	public static String ENTITY_PARENT_KEY = ENTITY_KIND_PARENT;
	public static String ENTITY_KIND_USER= "User";
	
	public static String FIELD_NAME_UID = "UID";
	public static String FIELD_NAME_UIMG = "Image";
	public static String FIELD_NAME_WHATSUP = "WhatsUp";
	public static String FIELD_NAME_REGTIME = "RegTime";
	
	public String mUserID;
	public Text mUserImg;
	public String mUserWhat;
	public String mUserRegTime;
	
	
	public String getmUserRegTime() {
		return mUserRegTime;
	}
	public void setmUserRegTime(String mUserRegTime) {
		this.mUserRegTime = mUserRegTime;
	}
	public String getmUserID() {
		return mUserID;
	}
	public void setmUserID(String mUserID) {
		this.mUserID = mUserID;
	}
	public Text getmUserImg() {
		return mUserImg;
	}
	public void setmUserImg(Text mUserImg) {
		this.mUserImg = mUserImg;
	}
	public String getmUserWhat() {
		return mUserWhat;
	}
	public void setmUserWhat(String mUserWhat) {
		this.mUserWhat = mUserWhat;
	}
	


}
