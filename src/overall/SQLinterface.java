package overall;
public interface SQLinterface {
	public void connect(String database, String username, String password);
	public void disconnect();
	public void createDatabase(String databaseName);
	public void createTable(String tableName, String[] columns);
	public void dropTable(String tableName);
	public void insert(String tableName, String[] columns, String[] values);
	public String select(String tableName, String[] columns, String condition);
	public String selectOrderBy(String tableName, String[] columns, String condition, String[] orderColumns, String[] order);
	public String selectLimit(String tableName, String[] columns, String condition, String limit);
	public void update(String tableName, String[] columns, String[] values, String condition);
	public void delete(String tableName, String condition);
	public String join(String type, String tableName1, String tableName2, String[] columns, String condition);
	public String joinOrderBy(String type, String tableName1, String tableName2, String[] columns, String condition, String[] orderColumns, String[] order);
}
