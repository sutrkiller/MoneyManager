package pv239.fi.muni.cz.moneymanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PasscodeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);
        EditText pinBox = (EditText)findViewById(R.id.pinEditText);
        pinBox.addTextChangedListener(new TextValidator(pinBox) {
            @Override
            public void validate(TextView textView, String text) {
                if (text.trim().length()<4) {
                    textView.setError("Minimum length is 4");
                } else  {
                    textView.setError(null);
                }
            }
        });


    }

    public void logInOnClick(View view) {
        EditText pinBox = (EditText)findViewById(R.id.pinEditText);
        if (pinBox.getError()==null) {
            if (ALockingClass.checkPin(pinBox.getText().toString())) {
                finish();
            } else {
                Toast.makeText(getApplicationContext(),"Pin is incorrect!",Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(),pinBox.getError(),Toast.LENGTH_SHORT).show();
        }
    }
}
