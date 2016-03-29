package pv239.fi.muni.cz.moneymanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import javax.crypto.SecretKey;

public class CreatePasscodeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_passcode);

        final EditText first = (EditText) findViewById(R.id.editTextEntry);
        final EditText second = (EditText) findViewById(R.id.editTextConfirm);


        first.addTextChangedListener(new TextValidator(first) {
            @Override
            public void validate(TextView textView, String text) {
                if (text.trim().length()<4) {
                    textView.setError("Minimum length is 4");
                } else  {
                    textView.setError(null);
                }
            }
        });



       second.addTextChangedListener(new TextValidator(second) {
           @Override
           public void validate(TextView textView, String text) {
               if (text.trim().length() < 4) {
                   textView.setError("Minimum length is 4");
               } else if (!first.getText().toString().equals(text)) {
                   textView.setError("PINs do not match!");
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
        String key = Crypto.encrypt("true",secretKey,salt);
        ALockingClass.storePin(key);
        finish();



    }
}
