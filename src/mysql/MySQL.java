package mysql;
import java.sql.*;
import overall.SQLinterface;

public class MySQL implements SQLinterface{
	private Connection con = null;
	private Statement stmt = null;
	private String ip_adress,port;

	public MySQL(String database, String username, String password,String ip_adress, String port) {
		this.ip_adress=ip_adress;
		this.port=port;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://"+ip_adress+":"+port+"/",username,password);
			stmt = con.createStatement();
		} catch (Exception e) {System.out.println(e);}
		connect(database,username,password);
	}

	public void connect(String database, String username, String password) {
		try{  
			if(!databaseExists(database))createDatabase(database);
			con = DriverManager.getConnection("jdbc:mysql://"+ip_adress+":"+port+"/"+database,username,password);
			stmt = con.createStatement();
		}catch(Exception e){ System.out.println(e);}  
	}

	public void disconnect() {
		try {stmt.close();
		}catch(Exception e){ System.out.println(e);}
	} 

	public void createDatabase(String databaseName) {
		try{  
			String sql = "CREATE DATABASE IF NOT EXISTS "+databaseName;
			stmt.execute(sql);
		}catch(Exception e){ System.out.println(e);} 
	}

	public void createTable(String tableName, String[] columns){
		try {
			String query="CREATE TABLE IF NOT EXISTS "+tableName+" (";
			for(int i=0;i<columns.length-1;i++)query+=columns[i]+",";
			query+=columns[columns.length-1]+");";
			System.out.println(query);
			stmt.execute(query);
		}catch(SQLException e){ System.out.println(e);}
	}

	public void dropTable(String tableName) {
		try {
			String query="DROP TABLE IF EXISTS "+tableName;
			stmt.execute(query);
		}catch(SQLException e){ System.out.println(e);}
	}

	public void insert(String tableName, String[] columns, String[] values) {
		try {
			String query="INSERT INTO `"+tableName+"` (";
			for(int i=0;i<columns.length-1;i++)query+="`"+columns[i]+"`, ";
			query+="`"+columns[columns.length-1]+"`) VALUES (";

			for(int i=0;i<values.length-1;i++)query+="'"+values[i]+"', ";
			query+="'"+values[values.length-1]+"');";

			stmt.execute(query);
		}catch(SQLException e){ System.out.println(e);}
	}

	public String select(String tableName, String[] columns, String condition) {
		String query = "SELECT ";
		for(int i=0;i<columns.length-1;i++)
			query+=columns[i]+", ";
		query+=columns[columns.length-1]+" FROM "+tableName+" WHERE "+condition+";";
		return runQuery(query);
	}

	public String selectOrderBy(String tableName, String[] columns, String condition, String[] orderColumns, String[] order) {
		String query = "SELECT ";
		for(int i=0;i<columns.length-1;i++)
			query+=columns[i]+", ";
		query+=columns[columns.length-1]+" FROM "+tableName+" WHERE "+condition+" ORDER BY ";
		for(int i=0;i<orderColumns.length-1&&i<order.length-1;i++)
			query += "`"+orderColumns[i]+"` "+order[i]+", ";
		query += "`"+orderColumns[orderColumns.length-1]+"` "+order[order.length-1]+";";
		System.out.println(query+"\n\n");
		return runQuery(query);
	}

	public String selectLimit(String tableName, String[] columns, String condition, String limit) {
		String query = "SELECT ";
		for(int i=0;i<columns.length-1;i++)
			query+=columns[i]+", ";
		query+=columns[columns.length-1]+" FROM "+tableName+" WHERE "+condition+" LIMIT "+limit+";";
		return runQuery(query);
	}

	public void update(String tableName, String[] columns, String[] values, String condition) {
		String query = "UPDATE "+tableName+" SET ";
		for(int i=0;i<columns.length-1&&i<values.length-1;i++) {
			query += "`"+columns[i]+"` = '"+values[i]+"', ";
		}
		query += "`"+columns[columns.length-1]+"` = '"+values[values.length-1]+"' "+"WHERE "+condition+";";
		runQuery(query);
		//			stmt.execute(query);  
	}

	public void delete(String tableName, String condition) {
		String query = "DELETE FROM `"+tableName+"` WHERE "+condition+";";
		runQuery(query); 
	}

	public String join(String type, String tableName1, String tableName2, String[] columns, String condition) {
		String query="SELECT ";
		for(int i=0;i<columns.length-1;i++)
			query+=columns[i]+", ";
		query+=columns[columns.length-1]+" FROM "+tableName1+" "+type+" "+tableName2+" ON "+condition;
		return runQuery(query);
	}

	public String joinOrderBy(String type, String tableName1, String tableName2, String[] columns, String condition, String[] orderColumns, String[] order) {
		String query="SELECT ";
		for(int i=0;i<columns.length-1;i++)
			query+=columns[i]+", ";
		query+=columns[columns.length-1]+" FROM "+tableName1+" "+type+" "+tableName2+" ON "+condition+" ORDER BY ";
		for(int i=0;i<orderColumns.length-1&&i<order.length-1;i++)
			query += "`"+orderColumns[i]+"` "+order[i]+", ";
		query += "`"+orderColumns[orderColumns.length-1]+"` "+order[order.length-1]+";";
		return runQuery(query);
	} 

	private String runQuery(String query) {
		String s="";
		try {
			stmt.execute(query);
			ResultSet resultSet=stmt.executeQuery(query);  
			ResultSetMetaData metaData = resultSet.getMetaData();
			int numberOfColumns = metaData.getColumnCount();
			while(resultSet.next()) {
				for(int i=0;i<numberOfColumns;i++) 
					s+=resultSet.getString(i+1)+"\t\t";
				s+="\n";
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
		return s;
	}

	private String printColumns(ResultSetMetaData metaData, int numberOfColumns) {
		String s="";
		try {
			for(int i=0;i<numberOfColumns;i++) s+=metaData.getColumnLabel(i+1)+"\t\t";
			s+="\n";
		} catch (SQLException e) {System.out.println(e);}
		return s;
	}

	private boolean databaseExists(String databaseName) {
		try {
			String sql = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '"+databaseName+"';"; 
			return stmt.executeQuery(sql).next();
		} catch (SQLException e) {System.out.println(e);}
		return false;
	}

	public static void main(String args[]){  
		MySQL db = new MySQL("java_test","root","mffmff11","127.0.0.1","3306");
		String tableName = "test";
		String[] columns = {"table1.name", "table2.age"};
		String[] values = {"Mattias", "19"};
		String[] order = {"ASC","DESC"};
		String[] orderColumns = {"name","age"};

		//		String[] col = {"Id INT NOT NULL AUTO_INCREMENT PRIMARY KEY","name VARCHAR(250) NOT NULL","email VARCHAR(250) NOT NULL"};

		//		db.createTable("table1",columns);
		//		db.createTable("table2",columns);

		//		db.dropTable("test2");

		//		db.insert("table1", columns, values);
		//		db.insert("table2", columns, values);
				
		//		System.out.println(db.select(tableName, new String[]{"*"},"1"));

		//		db.update(tableName, columns, values, "`Id` = 1");

		//		db.delete(tableName, "`Id` = 2");

		//		db.selectOrderBy(tableName, columns, "1", orderColumns, order );

		//		db.selectSpecificOrderBy(tableName, columns, "1", orderColumns, order);

		//		db.selectLimit(tableName, orderColumns, "1", "3");

		//		System.out.println(db.join("INNER JOIN", "table1", "table2", columns, "table1.age=table2.age"));

		//		db.joinOrderBy("INNER JOIN", "table1", "table2", columns, "table1.age=table2.age", orderColumns, order);
	}
}
