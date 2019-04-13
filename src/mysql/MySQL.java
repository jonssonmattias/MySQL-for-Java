package mysql;
import java.sql.*;
import overall.SQLinterface;
/**
 * The <i>MySQL</i> class is used to simplify the use of MySQL queries.
 * 
 * @version 1.0
 * @since 2019-04-13
 * @author Mattias Jönsson
 *
 */
public class MySQL implements SQLinterface{
	private Connection con = null;
	private Statement stmt = null;
	private String hostname,port;

	/**
	 * Creates a connection to the using the parameters
	 * 
	 * @param database the database to be connected to
	 * @param username the user of the database
	 * @param password the password for the user
	 * @param hostname where the database is hosted
	 * @param port the port number of the database
	 */
	public MySQL(String database, String username, String password,String hostname, String port) {
		this.hostname=hostname;
		this.port=port;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://"+hostname+":"+port+"/",username,password);
			stmt = con.createStatement();
		} catch (Exception e) {System.out.println(e);}
		connect(database,username,password);
	}

	/* (non-Javadoc)
	 * @see overall.SQLinterface#connect(java.lang.String)
	 */
	public void connect(String connectionString) {
		try{  
			con = DriverManager.getConnection(connectionString);
			stmt = con.createStatement();
		}catch(Exception e){ System.out.println(e);}  
	}

	/**
	 * Connects to a database using the parameters
	 * 
	 * @param database the database to be connected to
	 * @param username the user of the database
	 * @param password the password for the user
	 */
	public void connect(String database, String username, String password) {
		try{  
			if(!databaseExists(database))
				createDatabase(database);
			con = DriverManager.getConnection("jdbc:mysql://"+hostname+":"+port+"/"+database,username,password);
			stmt = con.createStatement();
		}catch(Exception e){ System.out.println(e);}  
	}

	/* (non-Javadoc)
	 * @see overall.SQLinterface#disconnect()
	 */
	public void disconnect() {
		try {stmt.close();
		}catch(Exception e){ System.out.println(e);}
	} 

	/* (non-Javadoc)
	 * @see overall.SQLinterface#createDatabase(java.lang.String)
	 */
	public void createDatabase(String databaseName) {
		try{  
			String sql = "CREATE DATABASE IF NOT EXISTS "+databaseName;
			stmt.execute(sql);
		}catch(Exception e){ System.out.println(e);} 
	}

	/* (non-Javadoc)
	 * @see overall.SQLinterface#createTable(java.lang.String, java.lang.String[])
	 */
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

	/* (non-Javadoc)
	 * @see overall.SQLinterface#insert(java.lang.String, java.lang.String[], java.lang.String[])
	 */
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

	/* (non-Javadoc)
	 * @see overall.SQLinterface#select(java.lang.String)
	 */
	public String select(String tableName) {
		return select(tableName, new String[] {"*"}, "1");
	}

	/* (non-Javadoc)
	 * @see overall.SQLinterface#select(java.lang.String, java.lang.String[])
	 */
	public String select(String tableName, String[] columns) {
		return select(tableName, columns, "1");
	}
	/* (non-Javadoc)
	 * @see overall.SQLinterface#select(java.lang.String, java.lang.String[], java.lang.String)
	 */
	public String select(String tableName, String[] columns, String condition) {
		String query = "SELECT ";
		for(int i=0;i<columns.length-1;i++)
			query+=columns[i]+", ";
		query+=columns[columns.length-1]+" FROM "+tableName+" WHERE "+condition+";";
		return runQuery(query);
	}

	/* (non-Javadoc)
	 * @see overall.SQLinterface#select(java.lang.String, java.lang.String[], java.lang.String, java.lang.String[], int[])
	 */
	public String select(String tableName, String[] columns, String condition, String[] orderColumns, int[] order) {
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

	/* (non-Javadoc)
	 * @see overall.SQLinterface#selectLimit(java.lang.String, java.lang.String[], java.lang.String, java.lang.String)
	 */
	public String selectLimit(String tableName, String[] columns, String condition, String limit) {
		String query = "SELECT ";
		for(int i=0;i<columns.length-1;i++)
			query+=columns[i]+", ";
		query+=columns[columns.length-1]+" FROM "+tableName+" WHERE "+condition+" LIMIT "+limit+";";
		return runQuery(query);
	}

	/* (non-Javadoc)
	 * @see overall.SQLinterface#update(java.lang.String, java.lang.String[], java.lang.String[], java.lang.String)
	 */
	public void update(String tableName, String[] columns, String[] values, String condition) {
		String query = "UPDATE "+tableName+" SET ";
		for(int i=0;i<columns.length-1&&i<values.length-1;i++) {
			query += "`"+columns[i]+"` = '"+values[i]+"', ";
		}
		query += "`"+columns[columns.length-1]+"` = '"+values[values.length-1]+"' "+"WHERE "+condition+";";
		runQuery(query);
	}

	/* (non-Javadoc)
	 * @see overall.SQLinterface#delete(java.lang.String, java.lang.String)
	 */
	public void delete(String tableName, String condition) {
		String query = "DELETE FROM `"+tableName+"` WHERE "+condition+";";
		runQuery(query); 
	}

	/* (non-Javadoc)
	 * @see overall.SQLinterface#join(int[], java.lang.String[], java.lang.String[][], java.lang.String[])
	 */
	public String join(int[] type, String[] tables, String[][] columns, String[] conditions) {
		return join(type,tables,columns,conditions,null,null);
	}

	/* (non-Javadoc)
	 * @see overall.SQLinterface#join(int[], java.lang.String[], java.lang.String[][], java.lang.String[], java.lang.String[], int[])
	 */
	public String join(int[] type, String[] tables, String[][] columns, String[] conditions, String[] orderColumns, int[] order) {
		String[] joinType=getType(type);

		String query="SELECT ";
		for(int i=0;i<tables.length;i++) {
			for(int j=0;j<columns[i].length;j++) {
				query+=tables[i]+"."+columns[i][j]+", ";
			}
		}
		query=query.substring(0, query.length()-2)+"\nFROM "+tables[0]+"\n";
		for(int i=0;i<joinType.length;i++)
			query+=joinType[i]+" "+conditions[i]+" ";
		if(orderColumns!=null && order!=null) {
			String[] orderType=getOrder(order);
			query+=" ORDER BY ";
			for(int i=0;i<orderColumns.length-1&&i<orderType.length-1;i++)
				query += "`"+orderColumns[i]+"` "+orderType[i]+", ";
			query += "`"+orderColumns[orderColumns.length-1]+"` "+orderType[orderType.length-1];
		}
		query+=";";
		return runQuery(query);
	}

	/**
	 * Converts the number to a string for {@link #select(String, String[], String, String[], int[])} and 
	 * {@link #join(int, String, String, String[], String[], String, String[], int[])}.
	 * 
	 * @param order arrays of numbers to be converted
	 * @return an array of the converted numbers
	 */
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

	/* (non-Javadoc)
	 * @see overall.SQLinterface#runQuery(java.lang.String)
	 */
	public String runQuery(String query) {
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
		System.out.println(query);
		return s;
	}

	/**
	 * Converts the number to a string for {@link #join(int[], String, String, String[], String[], String)} 
	 * and {@link #join(int[], String, String, String[], String[], String, String[], int[])}.
	 * 
	 * @param type
	 * @return
	 */
	private String[] getType(int[] type) {
		String[] joinType = new String[type.length];
		for(int i=0;i<type.length;i++) {
			switch(type[i]) {
			case 1: joinType[i]="INNER JOIN";break;
			case 2: joinType[i]="OUTER JOIN";break;
			case 3: joinType[i]="LEFT JOIN";break;
			case 4: joinType[i]="RIGHT JOIN";break;
			}
		}
		return joinType;
	}

	/**
	 * Checks if database exists
	 * 
	 * @param databaseName the name of the database
	 * @return if the database exists or not
	 */
	private boolean databaseExists(String databaseName) {
		try {
			String sql = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '"+databaseName+"';"; 
			return stmt.executeQuery(sql).next();
		} catch (SQLException e) {System.out.println(e);}
		return false;
	}
}
