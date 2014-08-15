package dk.denhart.nemid;

/**
 * Created by Denhart on 11-08-2014.
 */
public class ImageDB {

    //private variables
    int _id;
    String _filename;
    String _readable_name;
    String _iv;

    // Empty constructor
    public ImageDB(){

    }
    // constructor
    public ImageDB(int id, String name, String _readable_name, String _iv){
        this._id = id;
        this._filename = name;
        this._readable_name = _readable_name;
        this._iv = _iv;
    }

    // constructor
    public ImageDB(String name, String _readable_name, String _iv){
        this._filename = name;
        this._readable_name = _readable_name;
        this._iv = _iv;
    }
    // getting ID
    public int getID(){
        return this._id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }

    // getting name
    public String getFileName(){
        return this._filename;
    }

    // setting name
    public void setFileName(String name){
        this._filename = name;
    }

    // getting phone number
    public String getReadableName(){
        return this._readable_name;
    }

    // setting phone number
    public void setReadableName(String phone_number){
        this._readable_name = phone_number;
    }

    public String getIV(){
        return this._iv;
    }

    // setting id
    public void setIV(String iv){
        this._iv = iv;
    }
}

