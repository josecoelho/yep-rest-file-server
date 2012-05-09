package file.server.model.bean;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Revision implements DBObj {
	
	private long id;
	private long metadataId;
	private Date createdAt;

	
	public Revision() {}
	public Revision(long metadataId) {
		this.metadataId = metadataId;
		this.createdAt = new Date();
	}
	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getMetadataId() {
		return metadataId;
	}

	public void setMetadataId(long metadataId) {
		this.metadataId = metadataId;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	
}
