package pv239.fi.muni.cz.moneymanager.crypto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import pv239.fi.muni.cz.moneymanager.R;

public abstract class ALockingClass extends AppCompatActivity {

  public static final String PREFS = "MoneyManagerPreferences";
   public static final String KEY = "MoneyManagerKey";
   public static final String LOGGED = "Logged";

    public static final int CheckPin = 1;
    public static final int CreatePin = 2;
    protected static SharedPreferences sharedPreferences;

    public static boolean storePin(Context context, String password) {
        sharedPreferences.edit().putString(KEY,password).putBoolean(LOGGED,true).commit();
        return true;
    }

    public static String retrievePin(Context context) {
        return sharedPreferences.getString(KEY,"");
    }

    public static boolean checkPin(Context context, String pin) {
        try {
            String res = Crypto.decryptPbkdf2(ALockingClass.retrievePin(context), pin);
            if (res.equals(context.getString(R.string.encryptString))) {

                sharedPreferences.edit().putBoolean(LOGGED, true).commit();
                return true;
            }
        } catch (Exception ex) {
            return false;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(LOGGED, false).commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isLogged()) {
            if (isPinStored()) {
                Intent pass = new Intent(getApplicationContext(), PasscodeActivity.class);
                startActivityForResult(pass, CheckPin, null);
            } else {
                Intent createPass = new Intent(getApplicationContext(), CreatePasscodeActivity.class);
                startActivityForResult(createPass, CreatePin, null);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    private boolean isLogged() {
        return sharedPreferences.getBoolean(LOGGED,false);
    }

    private boolean isPinStored() {
        return sharedPreferences.contains(KEY);
    }

    protected void deletePin() {
        sharedPreferences.edit().remove(KEY).clear().commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        sharedPreferences.edit().putBoolean(LOGGED,false).commit();
    }
}
