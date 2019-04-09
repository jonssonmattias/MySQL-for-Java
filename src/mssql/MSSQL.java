package mssql;

import java.sql.*;
import overall.SQLinterface;

public class MSSQL implements SQLinterface {
	private Connection con = null;
	private Statement stmt = null;
	private String hostname,port;

	public MSSQL(String database, String user, String password,String hostname, String port) {
		this.hostname=hostname;
		this.port=port;	
		String connectionString = String.format("jdbc:sqlserver://%s:%s;;user=%s;password=%s;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;", 
												hostname,port, user, password);
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			con = DriverManager.getConnection(connectionString);
			stmt = con.createStatement();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		connect(database,user,password);
	}



	public void connect(String database, String user, String password) {
		String connectionString = String.format("jdbc:sqlserver://%s:%s;database=%s;user=%s;password=%s;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;", 
				hostname,port,database, user, password);
		try {
			con = DriverManager.getConnection(connectionString);
			stmt = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {
		try {stmt.close();
		}catch(Exception e){ System.out.println(e);}
		System.out.println("Disconnected");
	}

	public void createDatabase(String databaseName) {
		String query = "CREATE DATABASE EXISTS "+databaseName+";";
		runQuery(query);
	}

	public void createTable(String tableName, String[] columns) {
		String query="CREATE TABLE "+tableName+" (";
		for(int i=0;i<columns.length-1;i++)query+=columns[i]+",";
		query+=columns[columns.length-1]+");";
		runQuery(query);
	}

	public void dropTable(String tableName) {
		String query = "DROP TABLE "+tableName;
		runQuery(query);
	}

	public void insert(String tableName, String[] columns, String[] values) {
		String query="INSERT INTO "+tableName+" (";
		for(int i=0;i<columns.length-1;i++)query+=columns[i]+", ";
		query+=columns[columns.length-1]+") VALUES (";

		for(int i=0;i<values.length-1;i++)query+="'"+values[i]+"', ";
		query+="'"+values[values.length-1]+"');";
		runQuery(query);
	}

	public String select(String tableName) {
		return select(tableName, new String[] {"*"}, "1");
	}

	public String select(String tableName, String[] columns) {
		return select(tableName, columns, "1");
	}

	public String select(String tableName, String[] columns, String condition) {
		String query = "SELECT ";
		for(int i=0;i<columns.length-1;i++)
			query+=columns[i]+", ";
		query+=columns[columns.length-1]+" FROM "+tableName+" WHERE "+condition+";";
		return runQuery(query);
	}

	public String select(String tableName, String[] columns, String condition, String[] orderColumns, int[] order) {
		String[] orderType=getOrder(order);
		String query = "SELECT ";
		for(int i=0;i<columns.length-1;i++)
			query+=columns[i]+", ";
		query+=columns[columns.length-1]+" FROM "+tableName+" WHERE "+condition+" ORDER BY ";
		for(int i=0;i<orderColumns.length-1&&i<order.length-1;i++)
			query += "`"+orderColumns[i]+"` "+order[i]+", ";
		query +=orderColumns[orderColumns.length-1]+" "+orderType[orderType.length-1]+";";
		return runQuery(query);
	}

	public String selectLimit(String tableName, String[] columns, String condition, String limit) {
		String query = "SELECT TOP "+limit+" ";
		for(int i=0;i<columns.length-1;i++)
			query+=columns[i]+", ";
		query+=columns[columns.length-1]+" FROM "+tableName+" WHERE "+condition+";";
		return runQuery(query);
	}

	public void update(String tableName, String[] columns, String[] values, String condition) {
		String query = "UPDATE "+tableName+" SET ";
		for(int i=0;i<columns.length-1&&i<values.length-1;i++) {
			query +=columns[i]+" = '"+values[i]+"', ";
		}
		query +=columns[columns.length-1]+" = '"+values[values.length-1]+"' "+"WHERE "+condition+";";
		runQuery(query);
	}

	public void delete(String tableName, String condition) {
		String query = "DELETE FROM "+tableName+" WHERE "+condition+";";
		runQuery(query); 
	}

	public String join(int type, String tableName1, String tableName2, String[] columnsTable1, String[] columnsTable2, String condition) {
		String joinType=getType(type);

		String query="SELECT ";
		for(int i=0;i<columnsTable1.length;i++){
			query+=tableName1+"."+columnsTable1[i]+", ";
		};
		for(int i=0;i<columnsTable2.length-1;i++){
			query+=tableName2+"."+columnsTable2[i]+", ";
		}
		query+=tableName2+"."+columnsTable2[columnsTable2.length-1];
		query+=" FROM "+tableName1+" "+joinType+" JOIN "+tableName2+" ON "+condition;
		return runQuery(query);
	}

	@Override
	public String join(int type, String tableName1, String tableName2, String[] columnsTable1, String[] columnsTable2, String condition, String[] orderColumns, int[] order) {
		String joinType=getType(type);
		String[] orderType=getOrder(order);

		String query="SELECT ";
		for(int i=0;i<columnsTable1.length;i++){
			query+=tableName1+"."+columnsTable1[i]+", ";
		};
		for(int i=0;i<columnsTable2.length-1;i++){
			query+=tableName2+"."+columnsTable2[i]+", ";
		}
		query+=tableName2+"."+columnsTable2[columnsTable2.length-1];
		query+=" FROM "+tableName1+" "+joinType+" JOIN "+tableName2+" ON "+condition;
		query+=" ORDER BY ";
		for(int i=0;i<orderColumns.length-1&&i<orderType.length-1;i++)
			query += "`"+orderColumns[i]+"` "+orderType[i]+", ";
		query += "`"+orderColumns[orderColumns.length-1]+"` "+orderType[orderType.length-1]+";";
		return runQuery(query);
	}

	private String[] getOrder(int[] order) {
		String[] orderType = new String[order.length];
		for(int i=0;i<order.length;i++) {
			switch(order[i]) {
			case 1: orderType[i]="ASC";
			case 2: orderType[i]="DESC";
			}
		}
		return orderType;
	}

	private String getType(int type) {
		switch(type) {
		case 1: return "INNER";
		case 2: return "OUTER";
		case 3: return "LEFT";
		case 4: return "RIGHT";
		}
		return "";
	}

	public String runQuery(String query) {
		String s="";
		try {
			stmt.execute(query);
		} catch (SQLException e) {
			System.out.println(e+" 1");
		}
		System.out.println(query);
		return s;
	}
	private boolean databaseExists(String databaseName) {
		try {
			String sql = "SELECT name FROM master.dbo.sysdatabases WHERE name = "+databaseName; 
			return stmt.executeQuery(sql).next();
		} catch (SQLException e) {System.out.println(e);}
		return false;
	}
}
