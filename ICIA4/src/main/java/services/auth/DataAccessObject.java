package services.auth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import beans.Employee;

public class DataAccessObject {
	PreparedStatement psmt;
	Connection connection;
    ResultSet rs;
	//Default로 지정 
	DataAccessObject(){
        this.psmt = null;
        this.connection = null;
        this.rs = null;
	}


	/* Driver Loading & Create Connection 
	 * 0. Driver Information, url : ip+port+sid, userId, userPassword
	 * 1. Driver Loading
	 * 2. Driver Manager를 통한 Connection 생성*/
	//statement, preparedstatement, callstatement 중 preparedStatement 사용
	
	Connection getConnection() {
		String[] url = {"jdbc:oracle:thin:@192.168.0.251:1521:xe","SHL","1234"};
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			connection = DriverManager.getConnection(url[0], url[1], url[2]);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return connection;
	}

	/* Transaction 상태 변경 */
	void modifyTranStats(boolean status) {
		try {//connection값이 있고 닫혀있지 않을때
			if(connection != null && connection.isClosed()) {
				connection.setAutoCommit(status);
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}

	/* Transaction 처리 -- Commit, Rollback */
	void setTransaction(boolean tran) {

		try {
			if(connection != null && connection.isClosed()) {
				if(tran) {
					connection.commit();
				}
				else {
					connection.rollback();
				}
			}
		}
		catch(SQLException e){
			e.printStackTrace();
		}
	}

	/* Close Connection */
      Connection closeConnection() {
		try {
			if(!psmt.isClosed()) psmt.close();
			if(connection != null && connection.isClosed()) {
				connection.close();
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		return connection;
	} 
	
	/* 매장코드 존재 여부 */
	boolean isStore(Employee emp) {
		boolean result = false;
		String query = " SELECT COUNT(*) AS CNT FROM SE WHERE SE_CODE = ? ";
		
		try {
	         this.psmt = this.connection.prepareStatement(query);
	        this.psmt.setNString(1, emp.getSeCode());
	         this.rs = this.psmt.executeQuery();
	         
	         while(rs.next()) {
	        	result = this.convertToBool(this.rs.getInt("CNT"));
	         }
		}catch(SQLException e){
			e.printStackTrace();
		}
		finally {
			try {if(!rs.isClosed()) {rs.close();}} catch(SQLException e) {e.printStackTrace();}
			
			
		}
		return result;
	}
	/* 직원코드 존재 여부*/
	boolean isEmp(Employee emp) {
		
		boolean result = false;
		String query = " SELECT COUNT(*) AS CNT FROM EM WHERE EM_CODE = ? ";
		
		try {
	         this.psmt = this.connection.prepareStatement(query);
	        this.psmt.setNString(1, emp.getEmCode());
	         this.rs = this.psmt.executeQuery();
	         
	         while(rs.next()) {
	        	result = this.convertToBool(this.rs.getInt("CNT"));
	         }
		}catch(SQLException e){
			e.printStackTrace();
		}
		finally {
			try {if(!rs.isClosed()) {rs.close();}} catch(SQLException e) {e.printStackTrace();}
			
			
		}
		return result;
	
	}
	
	/* 직원패스워드 일치 체크 */
	boolean isEmpPassword(Employee emp) {
		
		boolean result = false;
		String query = " SELECT COUNT(*) AS CNT FROM EM WHERE EM_PASSWORD = ? ";
		
		try {
	         this.psmt = this.connection.prepareStatement(query);
	        this.psmt.setNString(1, emp.getEmPassword());
	         this.rs = this.psmt.executeQuery();
	         
	         while(rs.next()) {
	        	result = this.convertToBool(this.rs.getInt("CNT"));
	         }
		}catch(SQLException e){
			e.printStackTrace();
		}
		finally {
			try {if(!rs.isClosed()) {rs.close();}} catch(SQLException e) {e.printStackTrace();}
			
			
		}
		return result;
		
	}
	
	/* ACCESSHISTORY에 INSERT */
    boolean insAccessHistory(Employee emp) {
    	boolean result = false;
    	String dml = "INSERT INTO AH(AH_SECODE, AH_EMCODE, AH_ACCESSTIME, AH_ACCESSTYPE) VALUES(?,?,DEFAULT,?)";
    	
    	try {
	         this.psmt = this.connection.prepareStatement(dml);
	          this.psmt.setNString(1, emp.getSeCode());
	          this.psmt.setNString(2, emp.getEmCode());
	          this.psmt.setInt(3, emp.getType());
	          
	          result = this.convertToBool(this.psmt.executeUpdate());
	           
	      
		}catch(SQLException e){
			e.printStackTrace();
		}
		finally {
			
			try {if(!psmt.isClosed()) {psmt.close();}} catch(SQLException e) {e.printStackTrace();}
			
		}
		return result;
    }
    /* 직원정보 기록 확인 */
    ArrayList<Employee> getAccessInfo(Employee emp){
    	ArrayList<Employee> list = new ArrayList<Employee>();
    	
    	String query = " SELECT SECODE, SENAME, EMCODE, EMNAME, ACCESSTIME "
    			        + "FROM ACCESSINFO "
    			        + "WHERE  ACCESSTIME = (SELECT TO_CHAR(MAX(AH_ACCESSTIME),'YYYY-MM-DD HH24:MI:SS') "
    			        +                      "FROM AH "
    			        +                       "WHERE AH_SECODE = ? AND AH_EMCODE = ?)";
    	
    	try { 
    		psmt = connection.prepareStatement(query);
    		this.psmt.setNString(1, emp.getSeCode());
    		this.psmt.setNString(2, emp.getEmCode()); 
    		rs = psmt.executeQuery();
    		
    		while(rs.next()) {
    			
    	    Employee ep = new Employee();
    	    
    	    
    	    ep.setSeCode(rs.getNString("SECODE"));
    	    ep.setSeName(rs.getNString("SENAME"));
    	    ep.setEmCode(rs.getNString("EMCODE"));
    	    ep.setEmName(rs.getNString("EMNAME"));
    	    ep.setDate(rs.getNString("ACCESSTIME"));
    	    list.add(ep);
    	    
    		}
    	}catch(SQLException e) {
    		e.printStackTrace();
    	}finally {
    		try {if(!rs.isClosed()) {rs.close();}} catch(SQLException e) {e.printStackTrace();}
    	}
    			
    	
    	return list;
    }
    
    /*bean에서는 1또는 0 으로 리턴하기때문에 boolean으로 바꿔줌*/
	boolean convertToBool (int value) { 
		return (value > 0)? true : false;
	}
}
