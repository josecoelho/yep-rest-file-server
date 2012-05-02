package file.server.model.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import file.server.model.bean.Metadata;
import file.server.model.bean.Revision;

@Repository
public class RevisionDAO extends GenericDao<Revision>{

	public Metadata loadRevisions(Metadata metadata) throws Exception {
		
		if(metadata != null) {
			List<Revision> revisions = findByColumn("metadata_id", metadata.getId());
			metadata.setRevisions(revisions);
		}
		
		return metadata;
	}
	
	
	@Override
	protected String getFieldsSQL(Revision obj) {
		return " id = ?, metadata_id = ?, created_at = ? ";
	}

	@Override
	protected int setFields(PreparedStatement pst, Revision obj)
			throws SQLException {
		int order = 0;
		pst.setLong(++order, obj.getId());
		pst.setLong(++order, obj.getMetadataId());
		pst.setDate(++order, new java.sql.Date(obj.getCreatedAt().getTime()));
		
		return ++order;
	}

	@Override
	protected Revision fromResultSet(ResultSet rs) throws SQLException {
		Revision retVal = new Revision();
		
		retVal.setId(rs.getLong("id"));
		retVal.setMetadataId(rs.getLong("metadata_id"));
		retVal.setCreatedAt(rs.getDate("created_at"));
		
		
		return retVal;
	}

	@Override
	protected String getTable() {
		return "fsrv_file_revision";
	}

}
