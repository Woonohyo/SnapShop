package io.realm;


import com.l3cache.snapshop.model.*;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.internal.ColumnType;
import io.realm.internal.ImplicitTransaction;
import io.realm.internal.LinkView;
import io.realm.internal.Row;
import io.realm.internal.Table;
import java.util.*;

public class NewsfeedDataRealmProxy extends NewsfeedData {

    @Override
    public String getContents() {
        realm.checkIfValid();
        return (java.lang.String) row.getString(Realm.columnIndices.get("NewsfeedData").get("contents"));
    }

    @Override
    public void setContents(String value) {
        realm.checkIfValid();
        row.setString(Realm.columnIndices.get("NewsfeedData").get("contents"), (String) value);
    }

    @Override
    public String getImageUrl() {
        realm.checkIfValid();
        return (java.lang.String) row.getString(Realm.columnIndices.get("NewsfeedData").get("imageUrl"));
    }

    @Override
    public void setImageUrl(String value) {
        realm.checkIfValid();
        row.setString(Realm.columnIndices.get("NewsfeedData").get("imageUrl"), (String) value);
    }

    @Override
    public String getName() {
        realm.checkIfValid();
        return (java.lang.String) row.getString(Realm.columnIndices.get("NewsfeedData").get("name"));
    }

    @Override
    public void setName(String value) {
        realm.checkIfValid();
        row.setString(Realm.columnIndices.get("NewsfeedData").get("name"), (String) value);
    }

    @Override
    public int getNumLike() {
        realm.checkIfValid();
        return (int) row.getLong(Realm.columnIndices.get("NewsfeedData").get("numLike"));
    }

    @Override
    public void setNumLike(int value) {
        realm.checkIfValid();
        row.setLong(Realm.columnIndices.get("NewsfeedData").get("numLike"), (long) value);
    }

    @Override
    public int getPid() {
        realm.checkIfValid();
        return (int) row.getLong(Realm.columnIndices.get("NewsfeedData").get("pid"));
    }

    @Override
    public void setPid(int value) {
        realm.checkIfValid();
        row.setLong(Realm.columnIndices.get("NewsfeedData").get("pid"), (long) value);
    }

    @Override
    public String getPrice() {
        realm.checkIfValid();
        return (java.lang.String) row.getString(Realm.columnIndices.get("NewsfeedData").get("price"));
    }

    @Override
    public void setPrice(String value) {
        realm.checkIfValid();
        row.setString(Realm.columnIndices.get("NewsfeedData").get("price"), (String) value);
    }

    @Override
    public int getRead() {
        realm.checkIfValid();
        return (int) row.getLong(Realm.columnIndices.get("NewsfeedData").get("read"));
    }

    @Override
    public void setRead(int value) {
        realm.checkIfValid();
        row.setLong(Realm.columnIndices.get("NewsfeedData").get("read"), (long) value);
    }

    @Override
    public String getShopUrl() {
        realm.checkIfValid();
        return (java.lang.String) row.getString(Realm.columnIndices.get("NewsfeedData").get("shopUrl"));
    }

    @Override
    public void setShopUrl(String value) {
        realm.checkIfValid();
        row.setString(Realm.columnIndices.get("NewsfeedData").get("shopUrl"), (String) value);
    }

    @Override
    public String getTitle() {
        realm.checkIfValid();
        return (java.lang.String) row.getString(Realm.columnIndices.get("NewsfeedData").get("title"));
    }

    @Override
    public void setTitle(String value) {
        realm.checkIfValid();
        row.setString(Realm.columnIndices.get("NewsfeedData").get("title"), (String) value);
    }

    @Override
    public int getUserId() {
        realm.checkIfValid();
        return (int) row.getLong(Realm.columnIndices.get("NewsfeedData").get("userId"));
    }

    @Override
    public void setUserId(int value) {
        realm.checkIfValid();
        row.setLong(Realm.columnIndices.get("NewsfeedData").get("userId"), (long) value);
    }

    @Override
    public int getUserLike() {
        realm.checkIfValid();
        return (int) row.getLong(Realm.columnIndices.get("NewsfeedData").get("userLike"));
    }

    @Override
    public void setUserLike(int value) {
        realm.checkIfValid();
        row.setLong(Realm.columnIndices.get("NewsfeedData").get("userLike"), (long) value);
    }

    @Override
    public String getWriteDate() {
        realm.checkIfValid();
        return (java.lang.String) row.getString(Realm.columnIndices.get("NewsfeedData").get("writeDate"));
    }

    @Override
    public void setWriteDate(String value) {
        realm.checkIfValid();
        row.setString(Realm.columnIndices.get("NewsfeedData").get("writeDate"), (String) value);
    }

    @Override
    public String getWriter() {
        realm.checkIfValid();
        return (java.lang.String) row.getString(Realm.columnIndices.get("NewsfeedData").get("writer"));
    }

    @Override
    public void setWriter(String value) {
        realm.checkIfValid();
        row.setString(Realm.columnIndices.get("NewsfeedData").get("writer"), (String) value);
    }

    public static Table initTable(ImplicitTransaction transaction) {
        if(!transaction.hasTable("class_NewsfeedData")) {
            Table table = transaction.getTable("class_NewsfeedData");
            table.addColumn(ColumnType.STRING, "contents");
            table.addColumn(ColumnType.STRING, "imageUrl");
            table.addColumn(ColumnType.STRING, "name");
            table.addColumn(ColumnType.INTEGER, "numLike");
            table.addColumn(ColumnType.INTEGER, "pid");
            table.addColumn(ColumnType.STRING, "price");
            table.addColumn(ColumnType.INTEGER, "read");
            table.addColumn(ColumnType.STRING, "shopUrl");
            table.addColumn(ColumnType.STRING, "title");
            table.addColumn(ColumnType.INTEGER, "userId");
            table.addColumn(ColumnType.INTEGER, "userLike");
            table.addColumn(ColumnType.STRING, "writeDate");
            table.addColumn(ColumnType.STRING, "writer");
            return table;
        }
        return transaction.getTable("class_NewsfeedData");
    }

    public static void validateTable(ImplicitTransaction transaction) {
        if(transaction.hasTable("class_NewsfeedData")) {
            Table table = transaction.getTable("class_NewsfeedData");
            if(table.getColumnCount() != 13) {
                throw new IllegalStateException("Column count does not match");
            }
            Map<String, ColumnType> columnTypes = new HashMap<String, ColumnType>();
            for(long i = 0; i < 13; i++) {
                columnTypes.put(table.getColumnName(i), table.getColumnType(i));
            }
            if (!columnTypes.containsKey("contents")) {
                throw new IllegalStateException("Missing column 'contents'");
            }
            if (columnTypes.get("contents") != ColumnType.STRING) {
                throw new IllegalStateException("Invalid type 'String' for column 'contents'");
            }
            if (!columnTypes.containsKey("imageUrl")) {
                throw new IllegalStateException("Missing column 'imageUrl'");
            }
            if (columnTypes.get("imageUrl") != ColumnType.STRING) {
                throw new IllegalStateException("Invalid type 'String' for column 'imageUrl'");
            }
            if (!columnTypes.containsKey("name")) {
                throw new IllegalStateException("Missing column 'name'");
            }
            if (columnTypes.get("name") != ColumnType.STRING) {
                throw new IllegalStateException("Invalid type 'String' for column 'name'");
            }
            if (!columnTypes.containsKey("numLike")) {
                throw new IllegalStateException("Missing column 'numLike'");
            }
            if (columnTypes.get("numLike") != ColumnType.INTEGER) {
                throw new IllegalStateException("Invalid type 'int' for column 'numLike'");
            }
            if (!columnTypes.containsKey("pid")) {
                throw new IllegalStateException("Missing column 'pid'");
            }
            if (columnTypes.get("pid") != ColumnType.INTEGER) {
                throw new IllegalStateException("Invalid type 'int' for column 'pid'");
            }
            if (!columnTypes.containsKey("price")) {
                throw new IllegalStateException("Missing column 'price'");
            }
            if (columnTypes.get("price") != ColumnType.STRING) {
                throw new IllegalStateException("Invalid type 'String' for column 'price'");
            }
            if (!columnTypes.containsKey("read")) {
                throw new IllegalStateException("Missing column 'read'");
            }
            if (columnTypes.get("read") != ColumnType.INTEGER) {
                throw new IllegalStateException("Invalid type 'int' for column 'read'");
            }
            if (!columnTypes.containsKey("shopUrl")) {
                throw new IllegalStateException("Missing column 'shopUrl'");
            }
            if (columnTypes.get("shopUrl") != ColumnType.STRING) {
                throw new IllegalStateException("Invalid type 'String' for column 'shopUrl'");
            }
            if (!columnTypes.containsKey("title")) {
                throw new IllegalStateException("Missing column 'title'");
            }
            if (columnTypes.get("title") != ColumnType.STRING) {
                throw new IllegalStateException("Invalid type 'String' for column 'title'");
            }
            if (!columnTypes.containsKey("userId")) {
                throw new IllegalStateException("Missing column 'userId'");
            }
            if (columnTypes.get("userId") != ColumnType.INTEGER) {
                throw new IllegalStateException("Invalid type 'int' for column 'userId'");
            }
            if (!columnTypes.containsKey("userLike")) {
                throw new IllegalStateException("Missing column 'userLike'");
            }
            if (columnTypes.get("userLike") != ColumnType.INTEGER) {
                throw new IllegalStateException("Invalid type 'int' for column 'userLike'");
            }
            if (!columnTypes.containsKey("writeDate")) {
                throw new IllegalStateException("Missing column 'writeDate'");
            }
            if (columnTypes.get("writeDate") != ColumnType.STRING) {
                throw new IllegalStateException("Invalid type 'String' for column 'writeDate'");
            }
            if (!columnTypes.containsKey("writer")) {
                throw new IllegalStateException("Missing column 'writer'");
            }
            if (columnTypes.get("writer") != ColumnType.STRING) {
                throw new IllegalStateException("Invalid type 'String' for column 'writer'");
            }
        }
    }

    public static List<String> getFieldNames() {
        return Arrays.asList("contents", "imageUrl", "name", "numLike", "pid", "price", "read", "shopUrl", "title", "userId", "userLike", "writeDate", "writer");
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("NewsfeedData = [");
        stringBuilder.append("{contents:");
        stringBuilder.append(getContents());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{imageUrl:");
        stringBuilder.append(getImageUrl());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{name:");
        stringBuilder.append(getName());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{numLike:");
        stringBuilder.append(getNumLike());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{pid:");
        stringBuilder.append(getPid());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{price:");
        stringBuilder.append(getPrice());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{read:");
        stringBuilder.append(getRead());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{shopUrl:");
        stringBuilder.append(getShopUrl());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{title:");
        stringBuilder.append(getTitle());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{userId:");
        stringBuilder.append(getUserId());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{userLike:");
        stringBuilder.append(getUserLike());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{writeDate:");
        stringBuilder.append(getWriteDate());
        stringBuilder.append("}");
        stringBuilder.append(",");
        stringBuilder.append("{writer:");
        stringBuilder.append(getWriter());
        stringBuilder.append("}");
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    @Override
    public int hashCode() {
        String realmName = realm.getPath();
        String tableName = row.getTable().getName();
        long rowIndex = row.getIndex();

        int result = 17;
        result = 31 * result + ((realmName != null) ? realmName.hashCode() : 0);
        result = 31 * result + ((tableName != null) ? tableName.hashCode() : 0);
        result = 31 * result + (int) (rowIndex ^ (rowIndex >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsfeedDataRealmProxy aNewsfeedData = (NewsfeedDataRealmProxy)o;

        String path = realm.getPath();
        String otherPath = aNewsfeedData.realm.getPath();
        if (path != null ? !path.equals(otherPath) : otherPath != null) return false;;

        String tableName = row.getTable().getName();
        String otherTableName = aNewsfeedData.row.getTable().getName();
        if (tableName != null ? !tableName.equals(otherTableName) : otherTableName != null) return false;

        return true;
    }

}
