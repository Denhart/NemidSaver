package dk.denhart.nemid;

/**
 * Created by Denhart on 11-08-2014.
 */
import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "nemidManager"; //"contactsManager";

    // Contacts table name
    private static final String TABLE_CARDS = "nemidCards"; //"contacts";
    private static final String TABLE_PINCODE = "pinCode";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String FILENAME = "filename";
    private static final String READABLE_NAME = "readableName";
    private static final String AES_IV_B64 = "iv";
    private static final String PIN_HASH = "pinhash";

    // Pin tables


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NEMID_TABLE = "CREATE TABLE " + TABLE_CARDS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + FILENAME + " TEXT," + READABLE_NAME + " TEXT,"
                + AES_IV_B64 + " TEXT" + ")";
        db.execSQL(CREATE_NEMID_TABLE);

        String CREATE_PIN_TABLE = "CREATE TABLE " + TABLE_PINCODE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + PIN_HASH + " TEXT" + ")";
        db.execSQL(CREATE_PIN_TABLE);
    }



    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PINCODE);
        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new image
    void addImage(ImageDB imageDB) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FILENAME, imageDB.getFileName()); // Contact Name
        values.put(READABLE_NAME, imageDB.getReadableName()); // Contact Phone
        values.put(AES_IV_B64, imageDB.getIV()); // AES IV

        // Inserting Row
        db.insert(TABLE_CARDS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single image
    ImageDB getImage(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CARDS, new String[] { KEY_ID,
                        FILENAME, READABLE_NAME, AES_IV_B64 }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ImageDB imageDB = new ImageDB(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3));
        // return contact
        return imageDB;
    }

    // Getting single image
    ImageDB getFilenameOnDisk(String readableName) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CARDS, new String[] { KEY_ID,
                        FILENAME, READABLE_NAME, AES_IV_B64 }, READABLE_NAME + "=?",
                new String[] { readableName }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ImageDB imageDB = new ImageDB(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3));
        // return contact
        return imageDB;
    }

    // Getting All images
    public List<ImageDB> getAllImages() {
        List<ImageDB> imageDBList = new ArrayList<ImageDB>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CARDS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ImageDB imageDB = new ImageDB();
                imageDB.setID(Integer.parseInt(cursor.getString(0)));
                imageDB.setFileName(cursor.getString(1));
                imageDB.setReadableName(cursor.getString(2));
                imageDB.setIV(cursor.getString(3));
                // Adding contact to list
                imageDBList.add(imageDB);
            } while (cursor.moveToNext());
        }

        // return image list
        return imageDBList;
    }

    public List<String> getAllImagesString() {
        List<String> imgList = new ArrayList<String>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CARDS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                 cursor.getString(2);
                imgList.add(cursor.getString(2));
            } while (cursor.moveToNext());
        }
        return imgList;
    }

    // Updating single image
    public int updateImage(ImageDB imageDB) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FILENAME, imageDB.getFileName());
        values.put(READABLE_NAME, imageDB.getReadableName());
        values.put(AES_IV_B64, imageDB.getIV());

        // updating row
        return db.update(TABLE_CARDS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(imageDB.getID()) });
    }

    // Deleting single image
    public void deleteImageEntry(ImageDB imageDB) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CARDS, KEY_ID + " = ?",
                new String[] { String.valueOf(imageDB.getID()) });
        db.close();
    }


    // Getting images Count
    public int getImageCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CARDS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    void addPin(String pincode) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PIN_HASH, pincode); // Contact Name

        // Inserting Row
        db.insert(TABLE_PINCODE, null, values);
        db.close(); // Closing database connection
    }

    // Getting single pin
    String getPin(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PINCODE, new String[] { KEY_ID,
                        PIN_HASH }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor.getString(1);
    }

    // Updating single pin
    public int updatePin(int id, String pincode) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PIN_HASH, pincode);


        // updating row
        return db.update(TABLE_PINCODE, values, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
    }

    // Deleting single pin
    public void deletePin(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PINCODE, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }

    boolean checkIfPinExist(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PINCODE, new String[] { KEY_ID,
                        PIN_HASH, }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if(cursor.moveToFirst()){
            return true;
        }else{
            return false;
        }

    }

}

