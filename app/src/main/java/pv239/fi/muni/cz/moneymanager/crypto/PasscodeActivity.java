package pv239.fi.muni.cz.moneymanager.crypto;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import pv239.fi.muni.cz.moneymanager.R;

public class PasscodeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        setContentView(R.layout.activity_passcode);
    }

    public void logInOnClick(View view) {
        EditText pinBox = (EditText)findViewById(R.id.pinEditText);
        if (pinBox.getText()==null) {
            Toast.makeText(getApplicationContext(), R.string.msg_incorrectPin,Toast.LENGTH_SHORT).show();
        }
        if (pinBox.getError()==null) {
            if (ALockingClass.checkPin(this,pinBox.getText().toString())) {
                finish();
                return;
            } else {
                Toast.makeText(getApplicationContext(),R.string.msg_incorrectPin,Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(),pinBox.getError(),Toast.LENGTH_SHORT).show();
        }
        pinBox.setText("");
    }
}
