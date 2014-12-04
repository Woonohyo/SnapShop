package io.realm;


import com.l3cache.snapshop.data.*;
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
        return (java.lang.String) row.getString(Realm.columnIndices.get("NewsfeedData").get("contents"));
    }

    @Override
    public void setContents(String value) {
        row.setString(Realm.columnIndices.get("NewsfeedData").get("contents"), (String) value);
    }

    @Override
    public String getImageUrl() {
        return (java.lang.String) row.getString(Realm.columnIndices.get("NewsfeedData").get("imageUrl"));
    }

    @Override
    public void setImageUrl(String value) {
        row.setString(Realm.columnIndices.get("NewsfeedData").get("imageUrl"), (String) value);
    }

    @Override
    public String getName() {
        return (java.lang.String) row.getString(Realm.columnIndices.get("NewsfeedData").get("name"));
    }

    @Override
    public void setName(String value) {
        row.setString(Realm.columnIndices.get("NewsfeedData").get("name"), (String) value);
    }

    @Override
    public int getNumLike() {
        return (int) row.getLong(Realm.columnIndices.get("NewsfeedData").get("numLike"));
    }

    @Override
    public void setNumLike(int value) {
        row.setLong(Realm.columnIndices.get("NewsfeedData").get("numLike"), (long) value);
    }

    @Override
    public int getPid() {
        return (int) row.getLong(Realm.columnIndices.get("NewsfeedData").get("pid"));
    }

    @Override
    public void setPid(int value) {
        row.setLong(Realm.columnIndices.get("NewsfeedData").get("pid"), (long) value);
    }

    @Override
    public String getPrice() {
        return (java.lang.String) row.getString(Realm.columnIndices.get("NewsfeedData").get("price"));
    }

    @Override
    public void setPrice(String value) {
        row.setString(Realm.columnIndices.get("NewsfeedData").get("price"), (String) value);
    }

    @Override
    public int getRead() {
        return (int) row.getLong(Realm.columnIndices.get("NewsfeedData").get("read"));
    }

    @Override
    public void setRead(int value) {
        row.setLong(Realm.columnIndices.get("NewsfeedData").get("read"), (long) value);
    }

    @Override
    public String getShopUrl() {
        return (java.lang.String) row.getString(Realm.columnIndices.get("NewsfeedData").get("shopUrl"));
    }

    @Override
    public void setShopUrl(String value) {
        row.setString(Realm.columnIndices.get("NewsfeedData").get("shopUrl"), (String) value);
    }

    @Override
    public String getTimeStamp() {
        return (java.lang.String) row.getString(Realm.columnIndices.get("NewsfeedData").get("timeStamp"));
    }

    @Override
    public void setTimeStamp(String value) {
        row.setString(Realm.columnIndices.get("NewsfeedData").get("timeStamp"), (String) value);
    }

    @Override
    public String getTitle() {
        return (java.lang.String) row.getString(Realm.columnIndices.get("NewsfeedData").get("title"));
    }

    @Override
    public void setTitle(String value) {
        row.setString(Realm.columnIndices.get("NewsfeedData").get("title"), (String) value);
    }

    @Override
    public int getUserLike() {
        return (int) row.getLong(Realm.columnIndices.get("NewsfeedData").get("userLike"));
    }

    @Override
    public void setUserLike(int value) {
        row.setLong(Realm.columnIndices.get("NewsfeedData").get("userLike"), (long) value);
    }

    @Override
    public String getWriter() {
        return (java.lang.String) row.getString(Realm.columnIndices.get("NewsfeedData").get("writer"));
    }

    @Override
    public void setWriter(String value) {
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
            table.addColumn(ColumnType.STRING, "timeStamp");
            table.addColumn(ColumnType.STRING, "title");
            table.addColumn(ColumnType.INTEGER, "userLike");
            table.addColumn(ColumnType.STRING, "writer");
            return table;
        }
        return transaction.getTable("class_NewsfeedData");
    }

    public static void validateTable(ImplicitTransaction transaction) {
        if(transaction.hasTable("class_NewsfeedData")) {
            Table table = transaction.getTable("class_NewsfeedData");
            if(table.getColumnCount() != 12) {
                throw new IllegalStateException("Column count does not match");
            }
            Map<String, ColumnType> columnTypes = new HashMap<String, ColumnType>();
            for(long i = 0; i < 12; i++) {
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
            if (!columnTypes.containsKey("timeStamp")) {
                throw new IllegalStateException("Missing column 'timeStamp'");
            }
            if (columnTypes.get("timeStamp") != ColumnType.STRING) {
                throw new IllegalStateException("Invalid type 'String' for column 'timeStamp'");
            }
            if (!columnTypes.containsKey("title")) {
                throw new IllegalStateException("Missing column 'title'");
            }
            if (columnTypes.get("title") != ColumnType.STRING) {
                throw new IllegalStateException("Invalid type 'String' for column 'title'");
            }
            if (!columnTypes.containsKey("userLike")) {
                throw new IllegalStateException("Missing column 'userLike'");
            }
            if (columnTypes.get("userLike") != ColumnType.INTEGER) {
                throw new IllegalStateException("Invalid type 'int' for column 'userLike'");
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
        return Arrays.asList("contents", "imageUrl", "name", "numLike", "pid", "price", "read", "shopUrl", "timeStamp", "title", "userLike", "writer");
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("NewsfeedData = [");
        stringBuilder.append("{contents:");
        stringBuilder.append(getContents());
        stringBuilder.append("} ");
        stringBuilder.append("{imageUrl:");
        stringBuilder.append(getImageUrl());
        stringBuilder.append("} ");
        stringBuilder.append("{name:");
        stringBuilder.append(getName());
        stringBuilder.append("} ");
        stringBuilder.append("{numLike:");
        stringBuilder.append(getNumLike());
        stringBuilder.append("} ");
        stringBuilder.append("{pid:");
        stringBuilder.append(getPid());
        stringBuilder.append("} ");
        stringBuilder.append("{price:");
        stringBuilder.append(getPrice());
        stringBuilder.append("} ");
        stringBuilder.append("{read:");
        stringBuilder.append(getRead());
        stringBuilder.append("} ");
        stringBuilder.append("{shopUrl:");
        stringBuilder.append(getShopUrl());
        stringBuilder.append("} ");
        stringBuilder.append("{timeStamp:");
        stringBuilder.append(getTimeStamp());
        stringBuilder.append("} ");
        stringBuilder.append("{title:");
        stringBuilder.append(getTitle());
        stringBuilder.append("} ");
        stringBuilder.append("{userLike:");
        stringBuilder.append(getUserLike());
        stringBuilder.append("} ");
        stringBuilder.append("{writer:");
        stringBuilder.append(getWriter());
        stringBuilder.append("} ");
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    @Override
    public int hashCode() {
        int result = 17;
        String aString_0 = getContents();
        result = 31 * result + (aString_0 != null ? aString_0.hashCode() : 0);
        String aString_1 = getImageUrl();
        result = 31 * result + (aString_1 != null ? aString_1.hashCode() : 0);
        String aString_2 = getName();
        result = 31 * result + (aString_2 != null ? aString_2.hashCode() : 0);
        result = 31 * result + getNumLike();
        result = 31 * result + getPid();
        String aString_5 = getPrice();
        result = 31 * result + (aString_5 != null ? aString_5.hashCode() : 0);
        result = 31 * result + getRead();
        String aString_7 = getShopUrl();
        result = 31 * result + (aString_7 != null ? aString_7.hashCode() : 0);
        String aString_8 = getTimeStamp();
        result = 31 * result + (aString_8 != null ? aString_8.hashCode() : 0);
        String aString_9 = getTitle();
        result = 31 * result + (aString_9 != null ? aString_9.hashCode() : 0);
        result = 31 * result + getUserLike();
        String aString_11 = getWriter();
        result = 31 * result + (aString_11 != null ? aString_11.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsfeedDataRealmProxy aNewsfeedData = (NewsfeedDataRealmProxy)o;
        if (getContents() != null ? !getContents().equals(aNewsfeedData.getContents()) : aNewsfeedData.getContents() != null) return false;
        if (getImageUrl() != null ? !getImageUrl().equals(aNewsfeedData.getImageUrl()) : aNewsfeedData.getImageUrl() != null) return false;
        if (getName() != null ? !getName().equals(aNewsfeedData.getName()) : aNewsfeedData.getName() != null) return false;
        if (getNumLike() != aNewsfeedData.getNumLike()) return false;
        if (getPid() != aNewsfeedData.getPid()) return false;
        if (getPrice() != null ? !getPrice().equals(aNewsfeedData.getPrice()) : aNewsfeedData.getPrice() != null) return false;
        if (getRead() != aNewsfeedData.getRead()) return false;
        if (getShopUrl() != null ? !getShopUrl().equals(aNewsfeedData.getShopUrl()) : aNewsfeedData.getShopUrl() != null) return false;
        if (getTimeStamp() != null ? !getTimeStamp().equals(aNewsfeedData.getTimeStamp()) : aNewsfeedData.getTimeStamp() != null) return false;
        if (getTitle() != null ? !getTitle().equals(aNewsfeedData.getTitle()) : aNewsfeedData.getTitle() != null) return false;
        if (getUserLike() != aNewsfeedData.getUserLike()) return false;
        if (getWriter() != null ? !getWriter().equals(aNewsfeedData.getWriter()) : aNewsfeedData.getWriter() != null) return false;
        return true;
    }

}
