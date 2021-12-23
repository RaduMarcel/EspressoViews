import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.CallableStatement;


public class DBConnections {
	private static Logger logger = LogManager.getLogger(DBConnections.class.getName());	
	static Connection getNewConnection(String dbType,String serviceName, String host, String port, String connType, String userName, String password,String execImmediateExpression)  {
		System.out.println("execImmediateExpression "+execImmediateExpression);
		if (dbType.equals("Oracle")){
			// DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			try {
				Class.forName("oracle.jdbc.OracleDriver");
			}
			catch (ClassNotFoundException a) {
				ErrorMessage.showException(a,"MySQL JDBC Driver not found");
				return null;
			}
			//		Enumeration<Driver> theDriver = DriverManager.getDrivers();
			//		while (theDriver.hasMoreElements()) {
			//					+ theDriver.nextElement().getClass().getName() + "\n");
			//		}
			try {
				if (DataJumper.conn !=null) DataJumper.conn.close();
				String thinConn = "jdbc:oracle:thin:@(DESCRIPTION="
						+ "(ADDRESS_LIST=" + "(ADDRESS=(PROTOCOL=TCP)"
						+ "(HOST="+host+")" + "(PORT = "+port+")" + ")" + ")"
						+ "(CONNECT_DATA=" + "("+connType+"="+serviceName+")"
						+ "(SERVER=DEDICATED)" + ")" + ")";
				DataJumper.conn = DriverManager.getConnection(thinConn, userName,
						Encrpt.decrypt(password,DataBaseLogin3.kb));
				DataJumper.conn.setAutoCommit(false);
				Statement st = DataJumper.conn.createStatement();//Die national language setting der neuen session werden an die settings der datenbank angepasst 
				ResultSet rs = st.executeQuery("SELECT 'alter session set '||parameter ||'='''||value||'''' as nls_settings from NLS_DATABASE_PARAMETERS where parameter in (SELECT parameter from NLS_session_PARAMETERS)");
				Statement st2 = DataJumper.conn.createStatement();
				String alterSessionQuery="";
				while (rs.next()){
					alterSessionQuery=rs.getString("NLS_SETTINGS");
					//System.out.println(alterSessionQuery);
					st2.executeQuery(alterSessionQuery);
				}
				st.close();	rs.close(); st2.close();
				logger.debug("Connected sucessfully to "+ DataJumper.conn.getMetaData().getDatabaseProductName()+" server, version "+ DataJumper.conn.getMetaData().getDatabaseProductVersion());
				
				if (execImmediateExpression !=null && execImmediateExpression.length()>0 && dbType.equals("Oracle")){
					logger.debug("Script found to be executed after sucessfull login:\n"+ execImmediateExpression);
					
					if (execImmediateExpression.trim().toLowerCase().startsWith("exec "))
						execImmediateExpression = execImmediateExpression.substring(4);

					if (execImmediateExpression.trim().endsWith(";") == false)
						execImmediateExpression = execImmediateExpression+";";
					
					execImmediateExpression = "declare\nbegin\n"+execImmediateExpression+"\nend;";	
					logger.debug("Script modified to:\n"+ execImmediateExpression);
						try {
							CallableStatement scriptExec = DataJumper.conn.prepareCall( execImmediateExpression );
							scriptExec.execute();
							scriptExec.close();
							logger.debug("Script executed sucessfully");
						} catch (SQLException sq) {
							ErrorMessage.showException(sq,"Error while executing the script:\n"+execImmediateExpression);
							logger.error("Error while executing the script:\n"+execImmediateExpression+"\n"+sq.getMessage()+"\n"+ErrorMessage.showStackTrace(sq.getStackTrace()));	
						}
					
				}

				return DataJumper.conn;
			} catch (SQLException sq) {
				ErrorMessage.showException(sq,"Error while connecting to "+dbType+" DB server!");
				logger.error("Error occured while connecting to "+dbType+" DB server\n"+sq.getMessage()+"\n"+ErrorMessage.showStackTrace(sq.getStackTrace()));
				return null;
			}
			catch (Exception e) {
				ErrorMessage.showException(e,"Error while connecting to "+dbType+" DB server!");
				logger.error("Error occured while connecting to "+dbType+" DB server\n"+e.getMessage()+"\n"+ErrorMessage.showStackTrace(e.getStackTrace()));
				return null;
			}
		}
	if (dbType.equals("MySQL")){	
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		}
		catch (ClassNotFoundException a) {
			ErrorMessage.showException(a,"MySQL JDBC Driver not found");
		}	
				try {
					if (DataJumper.conn !=null) DataJumper.conn.close();
					String thinConn = "jdbc:mysql://address=(protocol=tcp)(host="+host+")(port="+port+")/"+serviceName+"?useSSL=false";
					DataJumper.conn = DriverManager.getConnection(thinConn, userName,
							Encrpt.decrypt(password,DataBaseLogin3.kb));
					DataJumper.conn.setAutoCommit(false);
					logger.debug("Connected sucessfully to "+ DataJumper.conn.getMetaData().getDatabaseProductName()+" server, version "+ DataJumper.conn.getMetaData().getDatabaseProductVersion());			
					return DataJumper.conn;
				} catch (SQLException sq) {
					ErrorMessage.showException(sq,"Error while connecting to MySQL server!");
					logger.error("Error while connecting to MySQL server!\n"+sq.getMessage()+"\n"+ErrorMessage.showStackTrace(sq.getStackTrace()));
					return null;
				}
				catch (Exception e) {
					ErrorMessage.showException(e,"Error while connecting to MySQL server!");
					logger.error("Error while connecting to MySQL server!\n"+e.getMessage()+"\n"+ErrorMessage.showStackTrace(e.getStackTrace()));
					return null;
				}

	}
	return null;
	}

	 static void finish(){
	try{
	if (DataJumper.conn!=null && !DataJumper.conn.isClosed() && DataJumper.createdTables.size()>0 ){
		dropAllTempTables();
		DataJumper.conn.close();
		}
	}catch (SQLException sq) {
			ErrorMessage.showException(sq,"DB error occured while tried to close the connection");
			logger.error("DB error occured while tried to close the connection"+sq.getMessage()+"\n"+ErrorMessage.showStackTrace(sq.getStackTrace()));
	}
	System.exit(0);
	}

	 static void dropAllTempTables(){
		try{
			Statement stmnt = DataJumper.conn.createStatement();
		for (String tempTableName: DataJumper.createdTables){
			if (DataJumper.conn.getMetaData().getTables(null, null, tempTableName, new String[]{"GLOBAL TEMPORARY","TABLE","LOCAL TEMPORARY"} ).next())
				stmnt.execute("drop table "+tempTableName);
		}DataJumper.createdTables.clear();
		
		}catch (SQLException sq) {
			ErrorMessage.showException(sq,"Konnte die temporären Tabellen "+DataJumper.createdTables+" nicht fehlerfrei löschen\n");
			logger.error("DB Error occurred while dropping the temporary tables "+DataJumper.createdTables+"\n"+sq.getMessage()+"\n"+ErrorMessage.showStackTrace(sq.getStackTrace()));
		}
		DataJumper.createdTables.clear();
	}
}
