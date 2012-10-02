package file.server.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import file.server.model.bean.Metadata;
import file.server.model.bean.Revision;
import file.server.model.bean.UserPathPermission;

@Repository
public class UserPathPermissionDAO extends GenericDao<UserPathPermission>{

	public UserPathPermission findByUserAndPath(long userId, String path) throws Exception {
		UserPathPermission retVal = null;
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;
		String sql = null;
		
		try {
			conn = getConn();
			sql = String.format("SELECT * FROM %s WHERE user_id = ? AND path = ?", getTable());
			
			
			pst = conn.prepareStatement(sql);
			pst.setLong(1, userId);
			pst.setString(2, path);
			
			
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
	protected String getFieldsSQL(UserPathPermission obj) {
		return " id = ?, user_id = ?, path = ?, owner = ?, `write` = ?, `read` = ? ";
	}
	
	@Override
	protected int setFields(PreparedStatement pst, UserPathPermission obj)
			throws SQLException {
		int order = 0;
		pst.setLong(++order, obj.getId());
		pst.setLong(++order, obj.getUserId());
		pst.setString(++order, obj.getPath());
		pst.setBoolean(++order, obj.isOwner());
		pst.setBoolean(++order, obj.isWrite());
		pst.setBoolean(++order, obj.isRead());
		
		return ++order;
	}

	@Override
	protected UserPathPermission fromResultSet(ResultSet rs) throws SQLException {
		UserPathPermission retVal = new UserPathPermission();
		
		retVal.setId(rs.getLong("id"));
		retVal.setUserId(rs.getLong("user_id"));
		retVal.setPath(rs.getString("path"));
		retVal.setOwner(rs.getBoolean("owner"));
		retVal.setWrite(rs.getBoolean("write"));
		retVal.setRead(rs.getBoolean("read"));
		
		return retVal;
	}

	@Override
	protected String getTable() {
		return "fsrv_user_path_permission";
	}

}
