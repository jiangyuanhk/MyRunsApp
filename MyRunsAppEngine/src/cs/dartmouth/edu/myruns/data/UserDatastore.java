package cs.dartmouth.edu.myruns.data;

import java.util.ArrayList;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Text;


public class UserDatastore {
	
	private static final DatastoreService mDatastore = DatastoreServiceFactory
			.getDatastoreService();
	
	private static Key getParentKey() {
		return KeyFactory.createKey(UserEntity.ENTITY_KIND_PARENT,
				UserEntity.ENTITY_PARENT_KEY);
	}

	private static void createParentEntity() {
		Entity entity = new Entity(getParentKey());
		mDatastore.put(entity);
	}

	public static boolean add(UserEntity user) {
		Key parentKey = getParentKey();
		try {
			mDatastore.get(parentKey);
		} catch (Exception ex) {
			createParentEntity();
		}

		Entity entity = new Entity(UserEntity.ENTITY_KIND_USER,
				user.mUserRegTime, parentKey);
		
		entity.setProperty(UserEntity.FIELD_NAME_UID, user.mUserID);
		entity.setProperty(UserEntity.FIELD_NAME_UIMG, user.mUserImg);
		entity.setProperty(UserEntity.FIELD_NAME_WHATSUP, user.mUserWhat);
		entity.setProperty(UserEntity.FIELD_NAME_REGTIME, user.mUserRegTime);

		mDatastore.put(entity);

		return true;
	}

	public static ArrayList<UserEntity> query() {
		ArrayList<UserEntity> resultList = new ArrayList<UserEntity>();

		Query query = new Query(UserEntity.ENTITY_KIND_USER);
		query.setFilter(null);
		query.setAncestor(getParentKey());
		query.addSort(UserEntity.FIELD_NAME_UID, SortDirection.DESCENDING);
		PreparedQuery pq = mDatastore.prepare(query);
		
		for (Entity entity : pq.asIterable()) {
			UserEntity user = new UserEntity();
			user.mUserID = (String)entity.getProperty(UserEntity.FIELD_NAME_UID);
			user.mUserImg = (Text)entity.getProperty(UserEntity.FIELD_NAME_UIMG);
			user.mUserWhat = (String)entity.getProperty(UserEntity.FIELD_NAME_WHATSUP);
			user.mUserRegTime = (String)entity.getProperty(UserEntity.FIELD_NAME_REGTIME);
			resultList.add(user);
		}
		return resultList;
	}
	
	public static String queryById(String id) {
		Filter filter = new FilterPredicate(UserEntity.FIELD_NAME_UID,
				FilterOperator.EQUAL, id);

		Query query = new Query(UserEntity.ENTITY_KIND_USER);
		query.setFilter(filter);

		// Use PreparedQuery interface to retrieve results
		PreparedQuery pq = mDatastore.prepare(query);

		Entity result = pq.asSingleEntity();
		
		Text txt = (Text) result.getProperty(UserEntity.FIELD_NAME_UIMG);
		String uimg = txt.getValue();
		return uimg;
	}
	
	
	
//	public static boolean deleteAll(){
//		Query query = new Query(PostEntity.ENTITY_KIND_POST);
//		query.setFilter(null);
//		query.setAncestor(getParentKey());
//		query.addSort(PostEntity.FIELD_NAME_UID, SortDirection.ASCENDING);
//		PreparedQuery pq = mDatastore.prepare(query);
//		
//		boolean ret = false;
//		for(Entity entity:pq.asIterable()){
//			if (entity != null) {
//				// delete
//				mDatastore.delete(entity.getKey());
//				ret = true;
//			}else{
//				return false;
//			}
//		}
//		return ret;
//	}
//
//	public static boolean delete(String id) {
//		// TODO Auto-generated method stub
//		Filter filter = new FilterPredicate(PostEntity.FIELD_NAME_UID,
//				FilterOperator.EQUAL, id);
//
//		Query query = new Query(PostEntity.ENTITY_KIND_POST);
//		query.setFilter(filter);
//
//		// Use PreparedQuery interface to retrieve results
//		PreparedQuery pq = mDatastore.prepare(query);
//
//		Entity result = pq.asSingleEntity();
//		boolean ret = false;
//		if (result != null) {
//			// delete
//			mDatastore.delete(result.getKey());
//			ret = true;
//		}
//		return ret;
//		
//	}

}
