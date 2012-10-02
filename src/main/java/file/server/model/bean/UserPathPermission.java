package file.server.model.bean;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserPathPermission implements DBObj {

	private long id;
	private String path;
	private long userId;
	private boolean owner;
	private boolean write;
	private boolean read;
	
	public UserPathPermission(){}
	public UserPathPermission(long userId, String path,boolean read, boolean write) {
		
		this.userId = userId;
		this.path = path;
		this.read = read;
		this.write = write;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long user_id) {
		this.userId = user_id;
	}

	public boolean isOwner() {
		return owner;
	}

	public void setOwner(boolean owner) {
		this.owner = owner;
	}

	public boolean isWrite() {
		return write;
	}

	public void setWrite(boolean write) {
		this.write = write;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}
}
