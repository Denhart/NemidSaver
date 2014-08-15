package dk.denhart.nemid;

/**
 * Created by Denhart on 11-08-2014.
 */
//To keep pincode in shared memory between Activities.
public class Singleton {
    private static Singleton mInstance = null;

    private String mString;

    private Singleton(){
        mString = "";
    }

    public static Singleton getInstance(){
        if(mInstance == null)
        {
            mInstance = new Singleton();
        }
        return mInstance;
    }

    public String getString(){
        return this.mString;
    }

    public void setString(String value){
        mString = value;
    }
}