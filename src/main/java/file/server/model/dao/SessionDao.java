package file.server.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Repository
public class SessionDao {

	@Autowired
	protected ComboPooledDataSource pool;
	
	protected Log log = LogFactory.getLog(GenericDao.class);
	
	public boolean validateSession(String sid) throws Exception {
		boolean retVal = false;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;
		String sql = null;
		
		try {
			conn = getConn();
			sql = "SELECT 1 FROM ci_sessions WHERE session_id = ?";
			
			pst = conn.prepareStatement(sql);
			pst.setString(1, sid);
			
			rs = pst.executeQuery();
			if(rs.next()) {
				retVal = true;
			}
		} catch(Exception e) {
			log.error("Error with query on database: "+sql, e);
			throw new Exception("Error checking session on from ci_sessions",e);
		} finally {
			returnConn(conn);
			try { rs.close(); } catch(Exception e) { /** do nothing */ }
			try { pst.close(); } catch(Exception e) { /** do nothing */ }
		}
		
		return retVal;
	}
	
	
	protected Connection getConn() throws SQLException {
		return pool.getConnection();
	}
	
	protected void returnConn(Connection conn) {
		try { conn.close(); } catch(Exception e) { /** do nothing */ }
	}
}