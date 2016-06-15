package pv239.fi.muni.cz.moneymanager.crypto;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import pv239.fi.muni.cz.moneymanager.R;

/**
 * Activity for authentication on every run
 *
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 * @date 10/4/2016
 */

public class PasscodeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.log_in);
            getSupportActionBar().setCustomView(R.layout.password_title);
            TextView v = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.passwaord_title);
            v.setText(R.string.log_in);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
        }

        setContentView(R.layout.activity_passcode);
        EditText et = (EditText) findViewById(R.id.pinEditText);
        if (et != null) {
            et.setOnTouchListener(new PasswordEditTextOnTouchListener(this));
        }
    }

    public void logInOnClick(View view) {
        EditText pinBox = (EditText)findViewById(R.id.pinEditText);
        if ((pinBox != null ? pinBox.getText() : null) == null) {
            Toast.makeText(getApplicationContext(), R.string.msg_incorrectPin,Toast.LENGTH_SHORT).show();
        }
        if ((pinBox != null ? pinBox.getError() : null) == null) {
            if (ALockingClass.checkPin(this, pinBox != null ? pinBox.getText().toString() : null)) {
                setResult(RESULT_OK);
                finish();
                return;
            } else {
                Toast.makeText(getApplicationContext(),R.string.msg_incorrectPin,Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(),pinBox.getError(),Toast.LENGTH_SHORT).show();
        }
        if (pinBox != null) {
            pinBox.setText("");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getParent()==null) {
            setResult(RESULT_CANCELED);
        } else {
            getParent().setResult(RESULT_CANCELED);
        }
        finish();
    }
}
