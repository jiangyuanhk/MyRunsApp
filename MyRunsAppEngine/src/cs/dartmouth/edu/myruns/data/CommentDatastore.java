package cs.dartmouth.edu.myruns.data;

import java.util.ArrayList;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;  
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;

public class CommentDatastore {
	
		
		private static final DatastoreService mDatastore = DatastoreServiceFactory
				.getDatastoreService();
		
		private static Key getParentKey() {
			return KeyFactory.createKey(CommentEntity.ENTITY_KIND_PARENT,
					CommentEntity.ENTITY_PARENT_KEY);
		}

		private static void createParentEntity() {
			Entity entity = new Entity(getParentKey());
			mDatastore.put(entity);
		}

		public static boolean add(CommentEntity comment) {
			Key parentKey = getParentKey();
			try {
				mDatastore.get(parentKey);
			} catch (Exception ex) {
				createParentEntity();
			}

			Entity entity = new Entity(getParentKey());
	
			
			entity.setProperty(CommentEntity.FIELD_NAME_PID, comment.mPostID);
			entity.setProperty(CommentEntity.FIELD_NAME_UID, comment.mUserID);
			entity.setProperty(CommentEntity.FIELD_NAME_CID, comment.mCommentID);
			entity.setProperty(CommentEntity.FIELD_NAME_CONTENT, comment.mContent);
			entity.setProperty(CommentEntity.FIELD_NAME_TIME, comment.mTime);

			mDatastore.put(entity);

			return true;
		}

		
		
		
		public static ArrayList<CommentEntity> query() {
			ArrayList<CommentEntity> resultList = new ArrayList<CommentEntity>();

			Query query = new Query(CommentEntity.ENTITY_KIND_COMMENT);
			query.setFilter(null);
			query.setAncestor(getParentKey());
			query.addSort(CommentEntity.FIELD_NAME_UID, SortDirection.DESCENDING);
			PreparedQuery pq = mDatastore.prepare(query);
			
			for (Entity entity : pq.asIterable()) {
				CommentEntity user = new CommentEntity();
				user.mUserID = (String)entity.getProperty(CommentEntity.FIELD_NAME_UID);
				user.mPostID = (String)entity.getProperty(CommentEntity.FIELD_NAME_PID);
				user.mCommentID = (String)entity.getProperty(CommentEntity.FIELD_NAME_CID);
				user.mContent = (String)entity.getProperty(CommentEntity.FIELD_NAME_CONTENT);
				user.mTime = (long) entity.getProperty(CommentEntity.FIELD_NAME_TIME);
				resultList.add(user);
			}
			return resultList;
		}
		
		
		public static ArrayList<CommentEntity> queryByPostID(String id) {
			ArrayList<CommentEntity> resultList = new ArrayList<CommentEntity>();
			Filter filter = new FilterPredicate(CommentEntity.FIELD_NAME_PID,
					FilterOperator.EQUAL, id);

			Query query = new Query(CommentEntity.ENTITY_KIND_COMMENT);
			query.setAncestor(getParentKey());
			query.addSort(CommentEntity.FIELD_NAME_TIME, SortDirection.DESCENDING);
			query.setFilter(filter);
			
			
			
			

			// Use PreparedQuery interface to retrieve results
			PreparedQuery pq = mDatastore.prepare(query);
			
			for (Entity entity : pq.asIterable()) {
				CommentEntity comment = new CommentEntity();
				comment.mUserID = (String)entity.getProperty(PostEntity.FIELD_NAME_UID);
				comment.mPostID = (String)entity.getProperty(CommentEntity.FIELD_NAME_PID);
				comment.mContent = (String)entity.getProperty(CommentEntity.FIELD_NAME_CONTENT);
				comment.mCommentID = (String)entity.getProperty(CommentEntity.FIELD_NAME_CID);
				comment.mTime = (Long)entity.getProperty(CommentEntity.FIELD_NAME_TIME);
				
				resultList.add(comment);
			}
			
			return resultList;
		}
		

		
		
//		public static boolean deleteAll(){
//			Query query = new Query(PostEntity.ENTITY_KIND_POST);
//			query.setFilter(null);
//			query.setAncestor(getParentKey());
//			query.addSort(PostEntity.FIELD_NAME_UID, SortDirection.ASCENDING);
//			PreparedQuery pq = mDatastore.prepare(query);
//			
//			boolean ret = false;
//			for(Entity entity:pq.asIterable()){
//				if (entity != null) {
//					// delete
//					mDatastore.delete(entity.getKey());
//					ret = true;
//				}else{
//					return false;
//				}
//			}
//			return ret;
//		}
	//
//		public static boolean delete(String id) {
//			// TODO Auto-generated method stub
//			Filter filter = new FilterPredicate(PostEntity.FIELD_NAME_UID,
//					FilterOperator.EQUAL, id);
	//
//			Query query = new Query(PostEntity.ENTITY_KIND_POST);
//			query.setFilter(filter);
	//
//			// Use PreparedQuery interface to retrieve results
//			PreparedQuery pq = mDatastore.prepare(query);
	//
//			Entity result = pq.asSingleEntity();
//			boolean ret = false;
//			if (result != null) {
//				// delete
//				mDatastore.delete(result.getKey());
//				ret = true;
//			}
//			return ret;
//			
//		}

	
}
