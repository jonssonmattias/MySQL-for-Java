package mssql;
import java.sql.*;
import overall.SQLinterface;
/**
 * The <i>MSSQL</i> class is used to simplify the use of MS SQL queries.
 * 
 * @version 1.0
 * @since 2019-04-13
 * @author Mattias Jönsson
 *
 */
public class MSSQL implements SQLinterface {
	private Connection con = null;
	private Statement stmt = null;

	/**
	 * Creates connection string with the parameters and connects to the database.
	 * 
	 * @param database the database to be connected to
	 * @param user the user of the database
	 * @param password the password for the user
	 * @param hostname where the database is hosted
	 * @param port the port number of the database
	 */
	public MSSQL(String database, String user, String password,String hostname, String port) {
		String connectionString = String.format("jdbc:sqlserver://%s:%s;database=%s;user=%s;password=%s;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;", 
				hostname,port,database, user, password);
		connect(connectionString);
	}

	public static void main(String[] args) {
		MSSQL mssql = new MSSQL("jdbc:sqlserver://cryptofiletesting.database.windows.net:1433;"
				   + "database=Testing;user=Mattias@cryptofiletesting;password=CryptoFileHasACoolPassword1;"
				   + "encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;"
				   + "loginTimeout=30;");
		String name = "1');";
		name = xmlEscapeText(name);
		mssql.insert("test", new String[]{"name","email","password"}, new String[] {name,"2","3"});
	}
	
	public static String xmlEscapeText(String t) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < t.length(); i++){
			char c = t.charAt(i);
			switch(c){
			case '<': sb.append("&lt;"); break;
			case '>': sb.append("&gt;"); break;
			case '\"': sb.append("&quot;"); break;
			case '&': sb.append("&amp;"); break;
			case '\'': sb.append("&apos;"); break;
			default:
				if(c>0x7e) {
					sb.append("&#"+((int)c)+";");
				}else
					sb.append(c);
			}
		}
		return sb.toString();
	}
	
	/**
	 * Creates a connection string and connects to the database.
	 * 
	 * @param connectionString the connection string
	 */
	public MSSQL(String connectionString) {
		connect(connectionString);
	}
	
	/* (non-Javadoc)
	 * @see overall.SQLinterface#connect(java.lang.String)
	 */
	public void connect(String connectionString) {
		try {
			con = DriverManager.getConnection(connectionString);
			stmt = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
	}

	/* (non-Javadoc)
	 * @see overall.SQLinterface#disconnect()
	 */
	public void disconnect() {
		try {stmt.close();
		}catch(Exception e){ e.printStackTrace();}
	}

	/* (non-Javadoc)
	 * @see overall.SQLinterface#createDatabase(java.lang.String)
	 */
	public void createDatabase(String databaseName) {
		String query = "CREATE DATABASE EXISTS "+databaseName+";";
		runQuery(query);
	}

	/* (non-Javadoc)
	 * @see overall.SQLinterface#createTable(java.lang.String, java.lang.String[])
	 */
	public void createTable(String tableName, String[] columns) {
		String query="CREATE TABLE "+tableName+" (";
		for(int i=0;i<columns.length-1;i++)query+=columns[i]+",";
		query+=columns[columns.length-1]+");";
		runQuery(query);
	}

	/* (non-Javadoc)
	 * @see overall.SQLinterface#dropTable(java.lang.String)
	 */
	public void dropTable(String tableName) {
		String query = "DROP TABLE "+tableName;
		runQuery(query);
	}

	/* (non-Javadoc)
	 * @see overall.SQLinterface#insert(java.lang.String, java.lang.String[], java.lang.String[])
	 */
	public void insert(String tableName, String[] columns, Object[] values) {
		String query="INSERT INTO "+tableName+" (";
		for(int i=0;i<columns.length-1;i++)
			query+=columns[i]+", ";
		query+=columns[columns.length-1]+") VALUES (";
		for(int i=0;i<values.length-1;i++) {
			if(values[i] instanceof String)
				query+="'"+values[i]+"', ";
			else
				query+=values[i]+", ";
		}
		if(values[values.length-1] instanceof String)
			query+="'"+values[values.length-1]+"');";
		else
			query+=values[values.length-1]+");";
		runQuery(query);
	}

	/* (non-Javadoc)
	 * @see overall.SQLinterface#select(java.lang.String)
	 */
	public String select(String tableName) {
		return select(tableName, new String[] {"*"}, "1=1");
	}

	/* (non-Javadoc)
	 * @see overall.SQLinterface#select(java.lang.String, java.lang.String[])
	 */
	public String select(String tableName, String[] columns) {
		return select(tableName, columns, "1=1");
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

	/* (non-Javadoc)
	 * @see overall.SQLinterface#selectLimit(java.lang.String, java.lang.String[], java.lang.String, java.lang.String)
	 */
	public String selectLimit(String tableName, String[] columns, String condition, String limit) {
		String query = "SELECT TOP "+limit+" ";
		for(int i=0;i<columns.length-1;i++)
			query+=columns[i]+", ";
		query+=columns[columns.length-1]+" FROM "+tableName+" WHERE "+condition+";";
		return runQuery(query);
	}

	/* (non-Javadoc)
	 * @see overall.SQLinterface#update(java.lang.String, java.lang.String[], java.lang.String[], java.lang.String)
	 */
	public void update(String tableName, String[] columns, String[] values, String condition) {
		String query = "UPDATE "+tableName+" SET ";
		for(int i=0;i<columns.length-1&&i<values.length-1;i++) {
			query +=columns[i]+" = '"+values[i]+"', ";
		}
		query +=columns[columns.length-1]+" = '"+values[values.length-1]+"' "+"WHERE "+condition+";";
		runQuery(query);
	}

	/* (non-Javadoc)
	 * @see overall.SQLinterface#delete(java.lang.String, java.lang.String)
	 */
	public void delete(String tableName, String condition) {
		String query = "DELETE FROM "+tableName+" WHERE "+condition+";";
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
			case 1: orderType[i]="ASC";break;
			case 2: orderType[i]="DESC";break;
			}
		}
		return orderType;
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

	/* (non-Javadoc)
	 * @see overall.SQLinterface#runQuery(java.lang.String)
	 */
	public String runQuery(String query) {
		String s="";
		try {
			if(!query.startsWith("SELECT")) {
				stmt.execute(query);
			}
			else {
				ResultSet resultSet=stmt.executeQuery(query);  
				ResultSetMetaData metaData = resultSet.getMetaData();
				int numberOfColumns = metaData.getColumnCount();
				while(resultSet.next()) {
					for(int i=0;i<numberOfColumns;i++) 
						s+=resultSet.getString(i+1)+"\t\t";
					s+="\n";
				}
			} 
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return s;
	}
}
