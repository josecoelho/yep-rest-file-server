package file.server.model.bean;

/**
 * All objects that will be persisted on database using GeneriDao must implement this interface
 * @author coelho
 */
public interface DBObj {

	public long getId();
	public void setId(long id);
	
}
