package pv239.fi.muni.cz.moneymanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import javax.crypto.SecretKey;

public class CreatePasscodeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        setContentView(R.layout.activity_create_passcode);

        final EditText first = (EditText) findViewById(R.id.editTextEntry);
        final EditText second = (EditText) findViewById(R.id.editTextConfirm);


        first.addTextChangedListener(new TextValidator(first) {
            @Override
            public void validate(TextView textView, String text) {
                if (text.trim().length()<4) {
                    textView.setError(getString(R.string.msg_minLengthPin));
                } else  {
                    textView.setError(null);
                }
            }
        });



       second.addTextChangedListener(new TextValidator(second) {
           @Override
           public void validate(TextView textView, String text) {
               if (text.trim().length() < 4) {
                   textView.setError(getString(R.string.msg_minLengthPin));
               } else if (!first.getText().toString().equals(text)) {
                   textView.setError(getString(R.string.msg_pinMismatch));
               } else {
                   textView.setError(null);
               }
           }
       });
    }

    public void createPinOnClick(View view) {
        EditText first = (EditText) findViewById(R.id.editTextEntry);
        EditText second = (EditText) findViewById(R.id.editTextConfirm);

        if (first.getText().toString().trim().isEmpty() || first.getError()!=null) return;
        if (second.getText().toString().trim().isEmpty() || second.getError()!=null) return;

        byte[] salt = Crypto.generateSalt();
        SecretKey secretKey = Crypto.deriveKeyPbkdf2(salt, first.getText().toString());
        String key = Crypto.encrypt(getString(R.string.encryptString),secretKey,salt);
        ALockingClass.storePin(key);
        finish();



    }
}
