package com.wisecoders.dbschema.mongodb.resultSet;

import com.wisecoders.dbschema.mongodb.MongoPreparedStatement;
import com.wisecoders.dbschema.mongodb.MongoResultSetMetaData;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;

/**
 * Copyright Wise Coders GmbH. The MongoDB JDBC driver is build to be used with  <a href="https://dbschema.com">DbSchema Database Designer</a>
 * Free to use by everyone, code modifications allowed only to the  <a href="https://github.com/wise-coders/mongodb-jdbc-driver">public repository</a>
 */
public class ArrayResultSet implements ResultSet {

    private Object[][] data = null;
    private String[] columnNames = null;
    private int currentRow = -1;
    private String tableName = null;
    private boolean isClosed = false;
    private MongoPreparedStatement statement = null;


    public ArrayResultSet() {
    }

    public ArrayResultSet(String[][] data, String[] columnNames) {
        if (data != null && data.length > 0 && data[0] != null) {
            int numRows = data.length;
            int numColumns = data[0].length;
            this.data = new String[numRows][numColumns];
            for (int i = 0; i < numRows; i++) {
                this.data[i] = Arrays.copyOf(data[i], data[i].length);
            }

        }
        this.columnNames = columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = Arrays.copyOf(columnNames, columnNames.length);
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setStatement(MongoPreparedStatement statement) {
        this.statement = statement;
    }

    public void addResultSet(ArrayResultSet toCopy) {
        if (toCopy.data == null || toCopy.data.length < 0) {
            return;
        }
        if (data == null) {
            data = new String[toCopy.data.length][toCopy.data[0].length];
            for (int i = 0; i < toCopy.data.length; i++) {
                data[i] = Arrays.copyOf(toCopy.data[i], toCopy.data[i].length);
            }
        } else {
            if (toCopy.data[0].length != data[0].length) {
                throw new IllegalArgumentException("Array toCopy column length (" + toCopy.data[0].length
                        + ") is not " + " the same as this result sets column length (" + toCopy.data[0].length + ")");
            }
            Object[][] newdata = new String[data.length + toCopy.data.length][data[0].length];
            for (int i = 0; i < data.length; i++) {
                newdata[i] = Arrays.copyOf(data[i], data[i].length);
            }
            for (int i = 0; i < toCopy.data.length; i++) {
                newdata[data.length + i] = Arrays.copyOf(toCopy.data[i], toCopy.data[i].length);
            }
            data = newdata;
        }
    }

    public void addRow(Object[] columnValues) {
        if (data == null) {
            data = new String[1][columnValues.length];
            data[0] = Arrays.copyOf(columnValues, columnValues.length);
        } else {
            int numRows = data.length;
            Object[][] newdata = new String[numRows + 1][data[0].length];
            for (int i = 0; i < numRows; i++) {
                newdata[i] = Arrays.copyOf(data[i], data[i].length);
            }
            newdata[numRows] = Arrays.copyOf(columnValues, columnValues.length);
            data = newdata;
        }
    }

    public int getRowCount() {
        if (data == null) {
            return 0;
        }
        return data.length;
    }

    @Override
    public <T> T unwrap(Class<T> iface) {

        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    /**
     * @see java.sql.ResultSet#next()
     */
    @Override
    public boolean next() throws SQLException {
        if (data == null) {
            return false;
        }
        if (currentRow < data.length - 1) {
            currentRow++;
            return true;
        }
        return false;
    }

    /**
     * @see java.sql.ResultSet#close()
     */
    @Override
    public void close() throws SQLException {
        this.isClosed = true;
    }

    /**
     * @see java.sql.ResultSet#wasNull()
     */
    @Override
    public boolean wasNull() throws SQLException {
        return false;
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        if (currentRow >= data.length) {
            throw new SQLException("ResultSet exhausted, request currentRow = " + currentRow);
        }
        int adjustedColumnIndex = columnIndex - 1;
        if (adjustedColumnIndex >= data[currentRow].length) {
            throw new SQLException("Column index does not exist: " + columnIndex);
        }
        final Object val = data[currentRow][adjustedColumnIndex];
        return val != null ? val.toString() : null;
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        return Boolean.parseBoolean(getString(columnIndex));
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException {

        return 0;
    }

    /**
     * @see java.sql.ResultSet#getShort(int)
     */
    @Override
    public short getShort(int columnIndex) throws SQLException {
        checkClosed();
        return Short.parseShort(getString(columnIndex));
    }

    /**
     * @see java.sql.ResultSet#getInt(int)
     */
    @Override
    public int getInt(int columnIndex) throws SQLException {
        checkClosed();
        return Integer.parseInt(getString(columnIndex));
    }

    /**
     * @see java.sql.ResultSet#getLong(int)
     */
    @Override
    public long getLong(int columnIndex) throws SQLException {
        checkClosed();
        return Long.parseLong(getString(columnIndex));
    }

    /**
     * @see java.sql.ResultSet#getFloat(int)
     */
    @Override
    public float getFloat(int columnIndex) throws SQLException {
        checkClosed();
        return Float.parseFloat(getString(columnIndex));
    }

    /**
     * @see java.sql.ResultSet#getDouble(int)
     */
    @Override
    public double getDouble(int columnIndex) throws SQLException {
        checkClosed();
        return Double.parseDouble(getString(columnIndex));
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {

        return null;
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {

        return null;
    }

    @Override
    public Date getDate(int columnIndex) throws SQLException {

        return null;
    }

    @Override
    public Time getTime(int columnIndex) throws SQLException {

        return null;
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {

        return null;
    }

    @Override
    public InputStream getAsciiStream(int columnIndex) throws SQLException {

        return null;
    }

    @Override
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {

        return null;
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLException {

        return null;
    }

    @Override
    public String getString(String columnLabel) throws SQLException {
        checkClosed();
        int index = -1;
        if (columnNames == null) {
            throw new SQLException("Use of columnLabel requires setColumnNames to be called first.");
        }
        for (int i = 0; i < columnNames.length; i++) {
            if (columnLabel.equals(columnNames[i])) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            throw new SQLException("Column " + columnLabel + " doesn't exist in this ResultSet");
        }
        return getString(index + 1);
    }

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        checkClosed();

        return false;
    }

    @Override
    public byte getByte(String columnLabel) throws SQLException {

        return 0;
    }

    @Override
    public short getShort(String columnLabel) throws SQLException {

        return 0;
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {

        return 0;
    }

    @Override
    public long getLong(String columnLabel) throws SQLException {

        return 0;
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {

        return 0;
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        return Double.parseDouble(getString(columnLabel));
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {

        return null;
    }

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        return getString(columnLabel).getBytes();
    }

    @Override
    public Date getDate(String columnLabel) throws SQLException {

        return null;
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException {

        return null;
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {

        return null;
    }

    @Override
    public InputStream getAsciiStream(String columnLabel) throws SQLException {

        return null;
    }

    @Override
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        return null;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {


    }

    @Override
    public String getCursorName() throws SQLException {

        return null;
    }

    /**
     * @see java.sql.ResultSet#getMetaData()
     */
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        checkClosed();

        int[] columnDisplaySizes = new int[columnNames.length];
        int[] columnJavaTypes = new int[columnNames.length];
        for (int i = 0; i < columnDisplaySizes.length; i++) {
            columnDisplaySizes[i] = columnNames[i].length();
            columnJavaTypes[i] = Types.VARCHAR;
        }
        if (data != null) {
            for (Object[] row : data) {
                for (int columnIdx = 0; columnIdx < row.length; columnIdx++) {
                    if (row[columnIdx] != null) {
                        int datalength = row[columnIdx].toString().length();
                        if (datalength > columnDisplaySizes[columnIdx]) {
                            columnDisplaySizes[columnIdx] = datalength;
                        }
                    }
                }
            }
        }

        return new MongoResultSetMetaData(tableName, columnNames, columnJavaTypes, columnDisplaySizes);
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        if (currentRow >= data.length) {
            throw new SQLException("ResultSet exhausted, request currentRow = " + currentRow);
        }
        int adjustedColumnIndex = columnIndex - 1;
        if (adjustedColumnIndex >= data[currentRow].length) {
            throw new SQLException("Column index does not exist: " + columnIndex);
        }
        return data[currentRow][adjustedColumnIndex];
    }

    @Override
    public Object getObject(String columnLabel) {

        return null;
    }

    @Override
    public int findColumn(String columnLabel) {

        return 0;
    }

    @Override
    public Reader getCharacterStream(int columnIndex) {

        return null;
    }

    @Override
    public Reader getCharacterStream(String columnLabel) {

        return null;
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) {

        return null;
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) {

        return null;
    }

    @Override
    public boolean isBeforeFirst() {

        return false;
    }

    @Override
    public boolean isAfterLast() {

        return false;
    }

    @Override
    public boolean isFirst() {

        return false;
    }

    @Override
    public boolean isLast() {

        return false;
    }

    @Override
    public void beforeFirst() {


    }

    @Override
    public void afterLast() {


    }

    @Override
    public boolean first() {

        return false;
    }

    @Override
    public boolean last() {

        return false;
    }

    @Override
    public int getRow() throws SQLException {

        return 0;
    }

    @Override
    public boolean absolute(int row) throws SQLException {

        return false;
    }

    @Override
    public boolean relative(int rows) throws SQLException {

        return false;
    }

    @Override
    public boolean previous() throws SQLException {

        return false;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {


    }

    @Override
    public int getFetchDirection() throws SQLException {

        return 0;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {


    }

    @Override
    public int getFetchSize() throws SQLException {

        return 0;
    }

    /**
     * @see java.sql.ResultSet#getType()
     */
    @Override
    public int getType() throws SQLException {
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    /**
     * @see java.sql.ResultSet#getConcurrency()
     */
    @Override
    public int getConcurrency() throws SQLException {
        return 0;
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        return false;
    }

    @Override
    public boolean rowInserted() throws SQLException {
        return false;
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        return false;
    }

    @Override
    public void updateNull(int columnIndex) throws SQLException {
    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
    }

    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {


    }

    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {


    }

    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {


    }

    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {


    }

    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {


    }

    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {


    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {


    }

    @Override
    public void updateString(int columnIndex, String x) throws SQLException {


    }

    @Override
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {


    }

    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException {


    }

    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException {


    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {


    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {


    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {


    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {


    }

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {


    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {


    }

    @Override
    public void updateNull(String columnLabel) throws SQLException {


    }

    @Override
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {


    }

    @Override
    public void updateByte(String columnLabel, byte x) throws SQLException {


    }

    @Override
    public void updateShort(String columnLabel, short x) throws SQLException {


    }

    @Override
    public void updateInt(String columnLabel, int x) throws SQLException {


    }

    @Override
    public void updateLong(String columnLabel, long x) throws SQLException {


    }

    @Override
    public void updateFloat(String columnLabel, float x) throws SQLException {


    }

    @Override
    public void updateDouble(String columnLabel, double x) throws SQLException {


    }

    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {


    }

    @Override
    public void updateString(String columnLabel, String x) throws SQLException {


    }

    @Override
    public void updateBytes(String columnLabel, byte[] x) throws SQLException {


    }

    @Override
    public void updateDate(String columnLabel, Date x) throws SQLException {


    }

    @Override
    public void updateTime(String columnLabel, Time x) throws SQLException {


    }

    @Override
    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {


    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {


    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {


    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {


    }

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {


    }

    @Override
    public void updateObject(String columnLabel, Object x) throws SQLException {


    }

    @Override
    public void insertRow() throws SQLException {


    }

    @Override
    public void updateRow() throws SQLException {


    }

    @Override
    public void deleteRow() throws SQLException {


    }

    @Override
    public void refreshRow() throws SQLException {


    }

    @Override
    public void cancelRowUpdates() throws SQLException {


    }

    @Override
    public void moveToInsertRow() throws SQLException {


    }

    @Override
    public void moveToCurrentRow() throws SQLException {


    }

    /**
     * @see java.sql.ResultSet#getStatement()
     */
    @Override
    public Statement getStatement() throws SQLException {
        return this.statement;
    }

    @Override
    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {

        return null;
    }

    @Override
    public Ref getRef(int columnIndex) throws SQLException {

        return null;
    }

    @Override
    public Blob getBlob(int columnIndex) throws SQLException {

        return null;
    }

    @Override
    public Clob getClob(int columnIndex) throws SQLException {

        return null;
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException {

        return null;
    }

    @Override
    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {

        return null;
    }

    @Override
    public Ref getRef(String columnLabel) throws SQLException {

        return null;
    }

    @Override
    public Blob getBlob(String columnLabel) throws SQLException {

        return null;
    }

    @Override
    public Clob getClob(String columnLabel) throws SQLException {

        return null;
    }

    @Override
    public Array getArray(String columnLabel) throws SQLException {

        return null;
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {

        return null;
    }

    @Override
    public Date getDate(String columnLabel, Calendar cal) throws SQLException {

        return null;
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {

        return null;
    }

    @Override
    public Time getTime(String columnLabel, Calendar cal) throws SQLException {

        return null;
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {

        return null;
    }

    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {

        return null;
    }

    @Override
    public URL getURL(int columnIndex) throws SQLException {

        return null;
    }

    @Override
    public URL getURL(String columnLabel) throws SQLException {

        return null;
    }

    @Override
    public void updateRef(int columnIndex, Ref x) throws SQLException {


    }

    @Override
    public void updateRef(String columnLabel, Ref x) throws SQLException {


    }

    @Override
    public void updateBlob(int columnIndex, Blob x) throws SQLException {


    }

    @Override
    public void updateBlob(String columnLabel, Blob x) throws SQLException {


    }

    @Override
    public void updateClob(int columnIndex, Clob x) throws SQLException {


    }

    @Override
    public void updateClob(String columnLabel, Clob x) throws SQLException {


    }

    @Override
    public void updateArray(int columnIndex, Array x) throws SQLException {


    }

    @Override
    public void updateArray(String columnLabel, Array x) throws SQLException {


    }

    @Override
    public RowId getRowId(int columnIndex) throws SQLException {

        return null;
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {

        return null;
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {


    }

    @Override
    public void updateRowId(String columnLabel, RowId x) throws SQLException {


    }

    @Override
    public int getHoldability() throws SQLException {

        return 0;
    }

    /**
     * @see java.sql.ResultSet#isClosed()
     */
    @Override
    public boolean isClosed() throws SQLException {
        return isClosed;
    }

    @Override
    public void updateNString(int columnIndex, String nString) throws SQLException {
        checkClosed();


    }

    @Override
    public void updateNString(String columnLabel, String nString) throws SQLException {


    }

    @Override
    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {


    }

    @Override
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {


    }

    @Override
    public NClob getNClob(int columnIndex) throws SQLException {

        return null;
    }

    @Override
    public NClob getNClob(String columnLabel) throws SQLException {

        return null;
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {

        return null;
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {

        return null;
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {


    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {


    }

    @Override
    public String getNString(int columnIndex) throws SQLException {

        return null;
    }

    @Override
    public String getNString(String columnLabel) throws SQLException {

        return null;
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {

        return null;
    }

    @Override
    public Reader getNCharacterStream(String columnLabel) throws SQLException {

        return null;
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {


    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {


    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {


    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {


    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {


    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {


    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {


    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {


    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {


    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {


    }

    @Override
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {


    }

    @Override
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {


    }

    @Override
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {


    }

    @Override
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {


    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {


    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {


    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {


    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {


    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {


    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {


    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {


    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {


    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {


    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {


    }

    @Override
    public void updateClob(int columnIndex, Reader reader) throws SQLException {


    }

    @Override
    public void updateClob(String columnLabel, Reader reader) throws SQLException {


    }

    @Override
    public void updateNClob(int columnIndex, Reader reader) throws SQLException {


    }

    @Override
    public void updateNClob(String columnLabel, Reader reader) throws SQLException {


    }

    private void checkClosed() throws SQLException {
        if (isClosed) {
            throw new SQLException("ResultSet was previously closed.");
        }
    }

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        return null;
    }

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        return null;
    }
}
