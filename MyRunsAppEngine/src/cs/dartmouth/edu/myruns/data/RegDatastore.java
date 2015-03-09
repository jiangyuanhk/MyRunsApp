package cs.dartmouth.edu.myruns.data;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;

public class RegDatastore {
	private static final DatastoreService mDatastore = DatastoreServiceFactory
			.getDatastoreService();
	private static final String ENTITY_KIND_DEVICE = "Device";
	private static final String DEVICE_REG_ID_PROPERTY = "regId";

	public static void register(String regId) {
		Entity entity = new Entity(ENTITY_KIND_DEVICE, regId);
		entity.setProperty(DEVICE_REG_ID_PROPERTY, regId);
		mDatastore.put(entity);
	}

	public static List<String> getDevices() {
		List<String> devices;

		Query query = new Query(ENTITY_KIND_DEVICE);
		Iterable<Entity> entities = mDatastore.prepare(query).asIterable();
		devices = new ArrayList<String>();
		for (Entity entity : entities) {
			String device = (String) entity	.getProperty(DEVICE_REG_ID_PROPERTY);
			devices.add(device);
		}

		return devices;
	}

}
