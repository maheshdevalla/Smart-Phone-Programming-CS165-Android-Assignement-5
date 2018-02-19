package edu.dartmouth.cs.actiontabs;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.google.android.gms.maps.model.LatLng;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;


public class DataBaseHelper extends SQLiteOpenHelper{
    private final static String DATABASE_NAME = "RUN";
    private final static int DATABASE_VERSION = 1;
    private String TABLE_NAME = "ManualEntry";

    public DataBaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*
     * called when the activity is created
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "create table if not exists " + TABLE_NAME + " (ID text primary key,Date text,Time text,Duration real," +
                "Distance real, Calories integer, HeartRate integer, Comment text, InputType text, ActivityType text," +
                "Climb real, AvgSpeed real, CurSpeed real, Positions BLOB)";
        sqLiteDatabase.execSQL(sql);
    }

    /*
     * called when needs to upgrade
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }

    /*
     * add an item into the database
     */
    public void addItem(databaseItem item){
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            String sql   =   "INSERT INTO " + TABLE_NAME + " (ID, Date, Time, Duration, Distance, Calories, " +
                    "HeartRate, Comment, InputType, ActivityType, Climb, AvgSpeed, CurSpeed, Positions)" +
                    "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

            SQLiteStatement insertStmt = db.compileStatement(sql);
            insertStmt.clearBindings();
            insertStmt.bindString(1, item.getID().equals("") ? "-1" : item.getID());
            insertStmt.bindString(2, item.getDate().equals("") ? "0" : item.getDate());
            insertStmt.bindString(3, item.getTime().equals("") ? "0" : item.getTime());
            insertStmt.bindDouble(4, item.getDuration() < 0 ? 0 : item.getDuration());
            insertStmt.bindDouble(5, item.getDistance() < 0 ? 0 : item.getDistance());
            insertStmt.bindDouble(6, item.getCalories() < 0 ? 0 : item.getCalories());
            insertStmt.bindDouble(7, item.getHeartRate() < 0 ? 0 : item.getHeartRate());
            insertStmt.bindString(8, item.getComment().equals("") ? "0" : item.getComment());
            insertStmt.bindString(9, item.getInputType().equals("") ? "0" : item.getInputType());
            insertStmt.bindString(10, item.getActivityType().equals("") ? "0" : item.getActivityType());
            insertStmt.bindDouble(11, item.getClimb() < 0 ? 0 : item.getClimb());
            insertStmt.bindDouble(12, item.getAvgSpeed() < 0 ? 0 : item.getAvgSpeed());
            insertStmt.bindDouble(13, item.getCurSpeed() < 0 ? 0 : item.getCurSpeed());
            insertStmt.bindBlob(14, getLocationByteArray(item.getLatlngs()));
            insertStmt.executeInsert();
            db.setTransactionSuccessful();
            db.endTransaction();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /*
     * delete an item in the database
     */
    public void deleteItem(String ID){
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete from "+TABLE_NAME+" where ID=?", new String[]{ID});
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Get all items in the database
     */
    public List<databaseItem> allItems(){
        List<databaseItem> result = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " order by ID ASC", new String[]{});
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                while(!cursor.isAfterLast()) {
                    databaseItem thisItem = new databaseItem(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getDouble(3), cursor.getDouble(4),
                            cursor.getInt(5), cursor.getInt(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getDouble(10),
                            cursor.getDouble(11), cursor.getDouble(12), setLocationListFromByteArray(cursor.getBlob(13)));
                    result.add(thisItem);
                    cursor.moveToNext();
                }
            }
        }catch (Exception e){
         e.printStackTrace();
        }
        return result;
    }

    // Convert Location ArrayList to byte array, to store in SQLite database
    public byte[] getLocationByteArray(List<LatLng> mLocationList) {
        int[] intArray = new int[mLocationList.size() * 2];

        for (int i = 0; i < mLocationList.size(); i++) {
            intArray[i * 2] = (int) (mLocationList.get(i).latitude * 1E6);
            intArray[(i * 2) + 1] = (int) (mLocationList.get(i).longitude * 1E6);
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(intArray.length
                * Integer.SIZE);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(intArray);

        return byteBuffer.array();
    }

    // Convert byte array to Location ArrayList
    public List<LatLng> setLocationListFromByteArray(byte[] bytePointArray) {
        List<LatLng> result = new ArrayList<>();
        if(bytePointArray.length == 0)
            return result;
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytePointArray);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();

        int[] intArray = new int[bytePointArray.length / Integer.SIZE];
        intBuffer.get(intArray);

        int locationNum = intArray.length / 2;

        for (int i = 0; i < locationNum; i++) {
            LatLng latLng = new LatLng((double) intArray[i * 2] / 1E6F,
                    (double) intArray[i * 2 + 1] / 1E6F);
            result.add(latLng);
        }
        return result;
    }
}
