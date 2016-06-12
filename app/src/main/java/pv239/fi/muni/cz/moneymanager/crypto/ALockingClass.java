package pv239.fi.muni.cz.moneymanager.crypto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import pv239.fi.muni.cz.moneymanager.R;
import pv239.fi.muni.cz.moneymanager.db.MMDatabaseHelper;

/**
 * Parent locking activity ensuring authorization in every access to app
 *
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 * @date 10/4/2016
 */
public abstract class ALockingClass extends AppCompatActivity {

  public static final String PREFS = "MoneyManagerPreferences";
   public static final String KEY = "MoneyManagerKey";
   public static final String LOGGED = "Logged";

    public static final int CheckPin = 1;
    public static final int CreatePin = 2;
    protected static SharedPreferences sharedPreferences;

    public boolean createRunning = false;

    public static boolean storePin(String password) {
        sharedPreferences.edit().putString(KEY, password).putBoolean(LOGGED, true).apply();
        return true;
    }

    public static String retrievePin() {
        return sharedPreferences.getString(KEY,"");
    }

    public static boolean checkPin(Context context, String pin) {
        try {
            String res = Crypto.decryptPbkdf2(ALockingClass.retrievePin(), pin);
            if (res.equals(context.getString(R.string.encryptString))) {

                sharedPreferences.edit().putBoolean(LOGGED, true).apply();
                return true;
            }
        } catch (Exception ex) {
            return false;
        }
        return false;
    }

    public static boolean isPinStored() {
        return sharedPreferences.contains(KEY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        sharedPreferences = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(LOGGED, false).apply();

    }



    @Override
    protected void onResume() {
        super.onResume();
        if (!isLogged()) {
            if (isPinStored()) {
                Intent pass = new Intent(getApplicationContext(), PasscodeActivity.class);
                startActivityForResult(pass, CheckPin, null);
            } else {
                if (!createRunning) {
                    Intent createPass = new Intent(getApplicationContext(), CreatePasscodeActivity.class);
                    startActivityForResult(createPass, CreatePin, null);
                    createRunning = true;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CreatePin) {
            createRunning = false;
            if (resultCode == RESULT_CANCELED) {
                this.finish();
                System.exit(0);
            }
        }
    }

    private boolean isLogged() {
        return sharedPreferences.getBoolean(LOGGED,false);
    }

    /*
    protected void deletePin() {

        sharedPreferences.edit().remove(KEY).clear().apply();
    }
    */

    @Override
    protected void onStop() {
        super.onStop();
        sharedPreferences.edit().putBoolean(LOGGED, false).apply();
    }
}
