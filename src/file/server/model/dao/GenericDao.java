package file.server.model.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import file.server.model.bean.DBObj;

public abstract class GenericDao<Type extends DBObj> {
	
	@Autowired
	protected ComboPooledDataSource pool;
	
	protected Log log = LogFactory.getLog(GenericDao.class);
	
	protected Connection getConn() throws SQLException {
		return pool.getConnection();
	}
	
	protected void returnConn(Connection conn) {
		try { conn.close(); } catch(Exception e) { /** do nothing */ }
	}
	
	
	public void save(Type obj) throws Exception {
		if(obj.getId() ==0) {
			insert(obj);
		} else {
			update(obj);
		}
	}
	
	public Type get(long id) throws Exception {
		Type retVal = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;
		String sql = null;
		
		try {
			conn = getConn();
			sql = String.format("SELECT * FROM %s WHERE id = ?", getTable());
			
			pst = conn.prepareStatement(sql);
			pst.setLong(1, id);
			
			rs = pst.executeQuery();
			if(rs.next()) {
				retVal = fromResultSet(rs);
			}
		} catch(Exception e) {
			log.error("Error with query on database: "+sql, e);
			throw new Exception("Error loading item id from "+getTable());
		} finally {
			returnConn(conn);
			try { rs.close(); } catch(Exception e) { /** do nothing */ }
			try { pst.close(); } catch(Exception e) { /** do nothing */ }
		}
		
		return retVal;
	}
	
	protected List<Type> findByColumn(String nameColumn, Object value) throws Exception {
		return findByColumn(nameColumn, value, 0, 0);
	}
	
	/**
	 * @param nameColumn name of column to be filtered
	 * @param value value of column
	 * @return found object or null if not found
	 * @throws Exception 
	 */
	protected List<Type> findByColumn(String nameColumn, Object value,int start,int limit) throws Exception {
		List<Type> retVal = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;
		String sql = null;
		String limitSql = "";
		try {
			conn = getConn();
			
			
			limitSql = start > 0 || limit > 0 ? "LIMIT "+start : "";
			limitSql += limit > 0 ? ","+limit : "";
			
			sql = String.format("SELECT * FROM %s WHERE %s = ? %s", getTable(), nameColumn, limitSql );
			
			
			pst = conn.prepareStatement(sql);
			pst.setObject(1, value);
			
			rs = pst.executeQuery();
			retVal = new FastList<Type>();
			while(rs.next()) {
				retVal.add(fromResultSet(rs));
			}
		} catch(Exception e) {
			log.error("Error with query on database: "+sql, e);
			throw new Exception("Error loading data by column from "+getTable());
		} finally {
			returnConn(conn);
			try { rs.close(); } catch(Exception e) { /** do nothing */ }
			try { pst.close(); } catch(Exception e) { /** do nothing */ }
		}
		
		return retVal;
	}
	
	/**
	 * @param nameColumn name of column to be filtered
	 * @param value value of column
	 * @return found object or null if not found
	 * @throws Exception 
	 */
	protected Type getByColumn(String nameColumn, Object value) throws Exception {
		List<Type> res = findByColumn(nameColumn, value,0,1);
		if(res.size() > 0) {
			return res.get(0);
		}
		return null;
	}
	
	public Map<Long,Type> getMap() throws Exception {
		Map<Long,Type> retVal = new FastMap<Long, Type>();
		Type obj = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;
		String sql = null;
		
		try {
			conn = getConn();
			sql = String.format("SELECT * FROM %s", getTable());
			
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			
			while(rs.next()) {
				obj = fromResultSet(rs);
				
				retVal.put(obj.getId(),obj);
			}
			
			
		} catch(Exception e) {
			log.error("Error with query on database: "+sql, e);
			throw new Exception("Error loading data from "+getTable());
		} finally {
			returnConn(conn);
			try { rs.close(); } catch(Exception e) { /** do nothing */ }
			try { pst.close(); } catch(Exception e) { /** do nothing */ }
		}
		
		return retVal;
	}
	
	public boolean update(Type obj) throws Exception {
		boolean retVal = false;
		PreparedStatement pst = null;
		Connection conn = null;
		String sql = null;
		int order = 1;
		
		try {
			conn = getConn();
			sql = String.format("UPDATE %s SET %s WHERE id = ?", getTable(),getFieldsSQL(obj));
			pst = conn.prepareStatement(sql);
			
			order = setFields(pst,obj);
			
			pst.setLong(order, obj.getId());
			
			if(pst.executeUpdate() > 0) {
				retVal = true;
			}
		} catch(Exception e) {
			log.error("Error with query on database: "+sql, e);
			throw new Exception("Error updating "+getTable());
		} finally {
			returnConn(conn);
			try { pst.close(); } catch(Exception e) { /** do nothing */ }
		}
		
		return retVal;
	}
	
	public boolean insert(Type obj) throws Exception {
		boolean retVal = false;
		PreparedStatement pst = null;
		Connection conn = null;
		String sql = null;
		ResultSet rs = null;
		
		try {
			conn = getConn();
			sql = String.format("INSERT INTO %s SET %s", getTable(),getFieldsSQL(obj));
			pst = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			
			setFields(pst,obj);
			
			pst.executeUpdate();
			rs = pst.getGeneratedKeys();
			
			if(rs.next()) {
				obj.setId(rs.getLong(1));
				
				retVal = true;
			}
		} catch(Exception e) {
			log.error("Error with query on database: "+sql, e);
			throw new Exception("Error inserting "+getTable());
		} finally {
			returnConn(conn);
			try { pst.close(); } catch(Exception e) { /** do nothing */ }
		}
		
		return retVal;
	}
	
	/**
	 * Subclass must implements this method returning all fields to be used on SET sql.
	 * It will be used on insert and update methods
	 * 
	 * E.g.:
	 * 
	 * id = ?, name = ?
	 * 
	 * @return SET part of SQL
	 */
	protected abstract String getFieldsSQL(Type obj);
	
	/**
	 * Subclass must implements this method setting data on the same order defined on getSetSQL method
	 * @param obj
	 * @return max order used to set a field
	 */
	protected abstract int setFields(PreparedStatement pst, Type obj) throws SQLException;

	/**
	 * Subclass must implements this method returning a populated object from result set
	 * @param rs
	 * @return populated object
	 * @throws SQLException
	 */
	protected abstract Type fromResultSet(ResultSet rs) throws SQLException;
	
	/**
	 * Subclass must implements this method returning the name of table that represets
	 * @return
	 */
	protected abstract String getTable();
}
