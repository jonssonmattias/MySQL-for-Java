package overall;

/**
 * 
 * 
 * @version 1.0
 * @since 2019-04-13
 * @author Mattias Jönsson
 *
 */
public interface SQLinterface {
	/**
	 * Connects to a database using the connection string
	 * 
	 * @param connectionString the connection string
	 */
	public void connect(String connectionString);
	/**
	 * Disconnects from the database
	 */
	public void disconnect();
	/**
	 * Creates a database
	 * 
	 * @param databaseName
	 */
	public void createDatabase(String databaseName);
	/**
	 * Creates a table
	 * 
	 * @param tableName the name of the table
	 * @param columns the columns of the table
	 */
	public void createTable(String tableName, String[] columns);
	/**
	 * Drops a table
	 * 
	 * @param tableName the name of the table to be droped
	 */
	public void dropTable(String tableName);
	/**
	 * Insert row into a table
	 * 
	 * @param tableName the name of the table the row will be inserted to
	 * @param columns the columns to be inserted on
	 * @param values the values to insert
	 */
	public void insert(String tableName, String[] columns, Object[] values);
	/**
	 * Selects all from a given table
	 * 
	 * @param tableName the table name
	 * @return the data returned from the query
	 */
	public String select(String tableName);
	/**
	 * Selects certain columns from a given table
	 * 
	 * @param tableName the name of the table
	 * @param columns the columns to be selected from
	 * @return the data returned from the query
	 */
	public String select(String tableName, String[] columns);
	/**
	 * Selects certain columns from a given table with a condition
	 * 
	 * @param tableName the name of the table
	 * @param columns the columns to be selected
	 * @param condition the condition of the selection
	 * @return the data returned from the query
	 */
	public String select(String tableName, String[] columns, String condition);
	/**
	 * Selects certain columns from a given table with a condition in an order
	 * 
	 * @param tableName the name of the order
	 * @param columns the columns to be selected
	 * @param condition the condition of the selection
	 * @param orderColumns the columns to be set in an order
	 * @param order the order the columns will be set in
	 * @return the data returned from the query
	 */
	public String select(String tableName, String[] columns, String condition, String[] orderColumns, int[] order);
	/**
	 * Selects certain columns from a given table with a condition and a limit of the selection 
	 * 
	 * @param tableName the name of the table
	 * @param columns the columns to be selected
	 * @param condition the condition of the selection
	 * @param limit the limit of the selection
	 * @return the data returned from the query
	 */
	public String selectLimit(String tableName, String[] columns, String condition, String limit);
	/**
	 * Updates one or many row/s in a given table
	 * 
	 * @param tableName the name of the table
	 * @param columns the columns effected by the update 
	 * @param values the new values
	 * @param condition the condition of the update
	 */
	public void update(String tableName, String[] columns, String[] values, String condition);
	/**
	 * Deletes one or many row/s in a given table
	 * 
	 * @param tableName the name of the table
	 * @param condition the condition of the deletion 
	 */
	public void delete(String tableName, String condition);
	/**
	 * Joins two or more tables, based on a related column between them.
	 * 
	 * @param type the types of join
	 * @param tables the tables to be joined
	 * @param columns the columns the join is based on
	 * @param conditions the condition of the join
	 * @return the data returned from the query
	 */
	public String join(int[] type, String[] tables, String[][] columns, String[] conditions);
	/**
	 * Joins two or more tables, based on a related column between them. And sets an order on it.
	 * 
	 * @param type the types of join
	 * @param tables the tables to be joined
	 * @param columns the columns the join is based on
	 * @param conditions the condition of the join
	 * @param orderColumns the columns to be set in an order 
	 * @param order the order the columns will be set in
	 * @return the data returned from the query
	 */
	public String join(int[] type, String[] tables, String[][] columns, String[] conditions, String[] orderColumns, int[] order);
	/**
	 * Runs the query.
	 * 
	 * @param query the query to be run
	 * @return the data returned from the query if there is any otherwise null
	 */
	public String runQuery(String query);
}
