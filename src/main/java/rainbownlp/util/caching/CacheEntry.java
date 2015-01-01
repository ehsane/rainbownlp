package rainbownlp.util.caching;

import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import rainbownlp.util.HibernateUtil;

@Entity
@Table( name = "CacheEntry" )
/**
 * This is a general class to store key/value
 * @author Ehsan
 *
 */
public class CacheEntry {
	private String keyValue;
	private String value;
	@Column(columnDefinition="TEXT")
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@Id
	public String getKeyValue() {
		return keyValue;
	}
	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}
	
	/**
	 * Loads cache entry by id
	 * @param pArtifactID
	 * @return
	 */
	synchronized public static CacheEntry getInstance(String pKey) {
		CacheEntry entry = get(pKey);
	    if(entry == null)
	    {
	    	entry = new CacheEntry();
	    	entry.setKeyValue(pKey);
	    	HibernateUtil.save(entry);
	    }
		return entry;
	}
	synchronized public static CacheEntry createInstance(String pKey, String value) {
		CacheEntry entry = get(pKey);
	    if(entry == null)
	    {
	    	entry = new CacheEntry();
	    	entry.setKeyValue(pKey);
	    	entry.setValue(value);
	    	HibernateUtil.saveWithNewSession(entry);
	    }
		return entry;
	}
	
	synchronized public static CacheEntry get(String pKey){
		String hql = "from CacheEntry where keyValue = :key";
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("key", pKey);
	    
		CacheEntry entry=(CacheEntry)HibernateUtil.executeGetOneValue(hql,params);;

		return entry;
	}
}
