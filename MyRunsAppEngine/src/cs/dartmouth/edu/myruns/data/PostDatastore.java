package cs.dartmouth.edu.myruns.data;

import java.util.ArrayList;
import java.util.Date;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Text;


public class PostDatastore {
	private static final DatastoreService mDatastore = DatastoreServiceFactory
			.getDatastoreService();
	private static Long lastQueryTime;
	
	private static Key getParentKey() {
		return KeyFactory.createKey(PostEntity.ENTITY_KIND_PARENT,
				PostEntity.ENTITY_PARENT_KEY);
	}

	private static void createParentEntity() {
		Entity entity = new Entity(getParentKey());

		mDatastore.put(entity);
	}

	public static boolean add(PostEntity post) {
		Key parentKey = getParentKey();
		try {
			mDatastore.get(parentKey);
		} catch (Exception ex) {
			createParentEntity();
		}

		Entity entity = new Entity(PostEntity.ENTITY_KIND_POST,
				post.mPostTime, parentKey);
		
		entity.setProperty(PostEntity.FIELD_NAME_UID, post.mUserID);
		entity.setProperty(PostEntity.FIELD_NAME_UIMG, post.mUserImg);
		entity.setProperty(PostEntity.FIELD_NAME_TITLE, post.mPostTitle);
		entity.setProperty(PostEntity.FIELD_NAME_CONTENT, post.mPostContent);
		entity.setProperty(PostEntity.FIELD_NAME_TIME, post.mPostTime);
		entity.setProperty(PostEntity.FIELD_NAME_PICTURE, post.mPostPicture);
		entity.setProperty(PostEntity.FIELD_NAME_LOCATION, post.mPostLocation);
		

		

		mDatastore.put(entity);

		return true;
	}

	public static ArrayList<PostEntity> query() {
		ArrayList<PostEntity> resultList = new ArrayList<PostEntity>();

		Query query = new Query(PostEntity.ENTITY_KIND_POST);
		query.setFilter(null);
		query.setAncestor(getParentKey());
		query.addSort(PostEntity.FIELD_NAME_TIME, SortDirection.ASCENDING);
		PreparedQuery pq = mDatastore.prepare(query);

		int i = 0;
		for (Entity entity : pq.asIterable()) {
			if(i++==20){
				break;
			}
			PostEntity post = new PostEntity();
			post.mUserID = (String)entity.getProperty(PostEntity.FIELD_NAME_UID);
			post.mUserImg = (Text)entity.getProperty(PostEntity.FIELD_NAME_UIMG);
			post.mPostTitle = (String)entity.getProperty(PostEntity.FIELD_NAME_TITLE);
			post.mPostContent = (String)entity.getProperty(PostEntity.FIELD_NAME_CONTENT);
			post.mPostTime = (Long)entity.getProperty(PostEntity.FIELD_NAME_TIME);
			post.mPostPicture = (Text)entity.getProperty(PostEntity.FIELD_NAME_PICTURE);
			post.mPostLocation = (String)entity.getProperty(PostEntity.FIELD_NAME_LOCATION);
			resultList.add(post);
		}
		return resultList;
	}
	
	public static ArrayList<PostEntity> queryAll() {
		ArrayList<PostEntity> resultList = new ArrayList<PostEntity>();

		Query query = new Query(PostEntity.ENTITY_KIND_POST);
		query.setFilter(null);
		query.setAncestor(getParentKey());
		query.addSort(PostEntity.FIELD_NAME_TIME, SortDirection.DESCENDING);
		PreparedQuery pq = mDatastore.prepare(query);
		
		for (Entity entity : pq.asIterable()) {
			PostEntity post = new PostEntity();
			post.mUserID = (String)entity.getProperty(PostEntity.FIELD_NAME_UID);
			post.mUserImg = (Text)entity.getProperty(PostEntity.FIELD_NAME_UIMG);
			post.mPostTitle = (String)entity.getProperty(PostEntity.FIELD_NAME_TITLE);
			post.mPostContent = (String)entity.getProperty(PostEntity.FIELD_NAME_CONTENT);
			post.mPostTime = (Long)entity.getProperty(PostEntity.FIELD_NAME_TIME);
			post.mPostPicture = (Text)entity.getProperty(PostEntity.FIELD_NAME_PICTURE);
			post.mPostLocation = (String)entity.getProperty(PostEntity.FIELD_NAME_LOCATION);
			resultList.add(post);
		}
		return resultList;
	}
	
	
	public static boolean deleteAll(){
		Query query = new Query(PostEntity.ENTITY_KIND_POST);
		query.setFilter(null);
		query.setAncestor(getParentKey());
		query.addSort(PostEntity.FIELD_NAME_UID, SortDirection.ASCENDING);
		PreparedQuery pq = mDatastore.prepare(query);
		
		boolean ret = false;
		for(Entity entity:pq.asIterable()){
			if (entity != null) {
				// delete
				mDatastore.delete(entity.getKey());
				ret = true;
			}else{
				return false;
			}
		}
		return ret;
	}

	public static boolean delete(String id) {
		// TODO Auto-generated method stub
		Filter filter = new FilterPredicate(PostEntity.FIELD_NAME_UID,
				FilterOperator.EQUAL, id);

		Query query = new Query(PostEntity.ENTITY_KIND_POST);
		query.setFilter(filter);

		// Use PreparedQuery interface to retrieve results
		PreparedQuery pq = mDatastore.prepare(query);

		Entity result = pq.asSingleEntity();
		boolean ret = false;
		if (result != null) {
			// delete
			mDatastore.delete(result.getKey());
			ret = true;
		}
		return ret;
		
	}

}
