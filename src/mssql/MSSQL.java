package mssql;

import java.sql.*;
import overall.SQLinterface;

public class MSSQL implements SQLinterface {
	private Connection con = null;
	private Statement stmt = null;
	private String ip_adress,port;
	
//	public MSSQL(String database, String username, String password,String ip_adress, String port) {
//		this.ip_adress=ip_adress;
//		this.port=port;
//		try {
//			Class.forName("com.mysql.jdbc.Driver");
//			con = DriverManager.getConnection("jdbc:mysql://"+ip_adress+":"+port+"/",username,password);
//			stmt = con.createStatement();
//		} catch (Exception e) {System.out.println(e);}
//		connect(database,username,password);
//	}
	
	public static void main(String[] args) {
		MSSQL mssql = new MSSQL();
		mssql.createDatabase("Testing");
		mssql.createTable("test", new String[]{"Id INT NOT NULL AUTO_INCREMENT PRIMARY KEY","name VARCHAR(250) NOT NULL","email VARCHAR(250) NOT NULL"});
		mssql.dropTable("Testing");
		mssql.insert("test", new String[] {"name","email"}, new String[]{"Mattias","matte@lodde.se"});
		mssql.select("Testing", new String[]{"*"}, "1");
		mssql.select("Testing", new String[]{"*"}, "1", new String[] {"name"}, new int[] {1});
		mssql.update("test", new String[] {"name"}, new String[] {"Matte"}, "1=1");
		mssql.insert("test", new String[] {"name","email"}, new String[]{"Mattias","matte@lodde.se"});
		mssql.delete("test", "name=Matte");
//		mssql.join(1, tableName1, tableName2, columns, condition)
	}
	
	public void connect(String database, String username, String password) {
		
	}

	public void disconnect() {
		
	}

	public void createDatabase(String databaseName) {
		String query = "CREATE DATABASE IF NOT EXISTS "+databaseName+";";
		runQuery(query);
	}

	public void createTable(String tableName, String[] columns) {
		String query="CREATE TABLE IF NOT EXISTS "+tableName+" (";
		for(int i=0;i<columns.length-1;i++)query+=columns[i]+",";
		query+=columns[columns.length-1]+");";
		runQuery(query);
	}

	public void dropTable(String tableName) {
		String query = "DROP TABLE "+tableName;
		runQuery(query);
	}

	public void insert(String tableName, String[] columns, String[] values) {
		String query="INSERT INTO `"+tableName+"` (";
		for(int i=0;i<columns.length-1;i++)query+="`"+columns[i]+"`, ";
		query+="`"+columns[columns.length-1]+"`) VALUES (";

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
		String query = "SELECT ";
		for(int i=0;i<columns.length-1;i++)
			query+=columns[i]+", ";
		query+=columns[columns.length-1]+" FROM "+tableName+" WHERE "+condition+" ORDER BY ";
		for(int i=0;i<orderColumns.length-1&&i<order.length-1;i++)
			query += "`"+orderColumns[i]+"` "+order[i]+", ";
		query += "`"+orderColumns[orderColumns.length-1]+"` "+order[order.length-1]+";";
		return runQuery(query);
	}

	public String selectLimit(String tableName, String[] columns, String condition, String limit) {
		String query = "SELECT TOP "+limit;
		for(int i=0;i<columns.length-1;i++)
			query+=columns[i]+", ";
		query+=columns[columns.length-1]+" FROM "+tableName+" WHERE "+condition+";";
		return runQuery(query);
	}

	public void update(String tableName, String[] columns, String[] values, String condition) {
		String query = "UPDATE "+tableName+" SET ";
		for(int i=0;i<columns.length-1&&i<values.length-1;i++) {
			query += "`"+columns[i]+"` = '"+values[i]+"', ";
		}
		query += "`"+columns[columns.length-1]+"` = '"+values[values.length-1]+"' "+"WHERE "+condition+";";
		runQuery(query);
	}

	public void delete(String tableName, String condition) {
		String query = "DELETE FROM `"+tableName+"` WHERE "+condition+";";
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
		System.out.println(query);
		return query;
	}
}
