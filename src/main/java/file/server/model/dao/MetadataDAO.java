package file.server.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Repository;

import file.server.model.bean.Metadata;

@Repository
public class MetadataDAO extends GenericDao<Metadata>{

	public Metadata findByPathAndName(String path, String name) throws Exception {
		Metadata retVal = null;
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;
		String sql = null;
		
		try {
			conn = getConn();
			sql = String.format("SELECT * FROM %s WHERE path = ? AND name = ?", getTable());
			
			
			pst = conn.prepareStatement(sql);
			pst.setString(1, path);
			pst.setString(2, name);
			
			rs = pst.executeQuery();
			if(rs.next()) {
				retVal = fromResultSet(rs);
			}
		} catch(Exception e) {
			log.error("Error with query on database: "+sql, e);
			throw new Exception("Error on select by path and name from "+getTable());
		} finally {
			returnConn(conn);
			try { rs.close(); } catch(Exception e) { /** do nothing */ }
			try { pst.close(); } catch(Exception e) { /** do nothing */ }
		}
		
		return retVal;
	}
	
	@Override
	protected String getFieldsSQL(Metadata obj) {
		return " id = ?, is_dir = ?, mimetype = ?, name = ?, path = ?, current_revision = ? ";
	}

	@Override
	protected int setFields(PreparedStatement pst, Metadata obj)
			throws SQLException {
		int order = 0;
		pst.setLong(++order, obj.getId());
		pst.setBoolean(++order, obj.getIsDir());
		pst.setString(++order,obj.getMimeType());
		pst.setString(++order, obj.getName()); 
		pst.setString(++order, obj.getPath());
		pst.setLong(++order, obj.getCurrentRevision());
		
		return ++order;
	}

	@Override
	protected Metadata fromResultSet(ResultSet rs) throws SQLException {
		Metadata retVal = new Metadata();
		
		retVal.setId(rs.getLong("id"));
		retVal.setIsDir(rs.getBoolean("is_dir"));
		retVal.setMimeType(rs.getString("mimetype"));
		retVal.setName(rs.getString("name"));
		retVal.setPath(rs.getString("path"));
		retVal.setCurrentRevision(rs.getInt("current_revision"));
		
		return retVal;
	}
	

	@Override
	protected String getTable() {
		return "fsrv_file_metadata";
	}

	public List<Metadata> findByPath(String path) throws Exception {
		return findByColumn("path", path);
	}

	
	
}
