package overall;

public interface SQLinterface {
	public void connect(String database, String username, String password);
	public void disconnect();
	public void createDatabase(String databaseName);
	public void createTable(String tableName, String[] columns);
	public void dropTable(String tableName);
	public void insert(String tableName, String[] columns, String[] values);
	public String select(String tableName);
	public String select(String tableName, String[] columns);
	public String select(String tableName, String[] columns, String condition);
	public String select(String tableName, String[] columns, String condition, String[] orderColumns, int[] order);
	public String selectLimit(String tableName, String[] columns, String condition, String limit);
	public void update(String tableName, String[] columns, String[] values, String condition);
	public void delete(String tableName, String condition);
	public String join(int type, String tableName1, String tableName2, String[] columnsTable1, String[] columnsTable2, String condition);
	public String join(int type, String tableName1, String tableName2, String[] columnsTable1, String[] columnsTable2, String condition, String[] orderColumns, int[] order);
	public String runQuery(String query);
}
