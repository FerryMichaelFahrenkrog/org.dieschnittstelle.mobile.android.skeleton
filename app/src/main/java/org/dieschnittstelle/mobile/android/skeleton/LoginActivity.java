package org.dieschnittstelle.mobile.android.skeleton;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextemailAdresse;
    private EditText editTextpassword;
    private Button btnLogin;
    private TextView txtHinweis;

    private final String userName = "Admin@gmx.de";
    private final String password = "123456";

    boolean isValid = false;

    private int counter = 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        editTextemailAdresse = findViewById(R.id.editEmail);
        editTextpassword = findViewById(R.id.editTextTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtHinweis = findViewById(R.id.txtWarnmeldung);


        editTextpassword.setInputType(InputType.TYPE_CLASS_NUMBER);

//        btnLogin.setEnabled(false);

        btnLogin.setOnClickListener(v -> {
            String inputEmail = editTextemailAdresse.getText().toString();
            String inputPassword = editTextpassword.getText().toString();

            if(inputEmail.isEmpty() || inputPassword.isEmpty()){
//              Snackbar.make(v, "Eingaben: " + inputEmail + " , " + inputPassword, Snackbar.LENGTH_SHORT).show();
              Snackbar.make(v, "Fülle beide Felder aus!", Snackbar.LENGTH_SHORT).show();
              txtHinweis.setText("FEHLER!");
            }else{
//                btnLogin.setEnabled(true);

                boolean korrektesEmailFormat = isValidEmail(inputEmail);

                if(korrektesEmailFormat){
                    isValid = validate(inputEmail, inputPassword);

                    if(!isValid){
                        counter--;
                        Snackbar.make(v, "Falscher Nutzername oder Passwort!", Snackbar.LENGTH_SHORT).show();
                        txtHinweis.setText("Versuche übrig: " + counter);

                        if(counter == 0){
                            btnLogin.setEnabled(false);
                            Snackbar.make(v, "Neu starten die Anwendung pls!", Snackbar.LENGTH_SHORT).show();
                        }
                    }else{
                        Snackbar.make(v, "Erfolgreich!", Snackbar.LENGTH_SHORT).show();

                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                    }
                }else{
                    txtHinweis.setText("Dies ist keine gültige Email!");
                }

            }

        });

    }

    private boolean validate(String name, String password){
        if(name.equals(userName) && password.equals(this.password)){
            return true;
        }
        else
        {
            return false;
        }
    }

    public static boolean isValidEmail(CharSequence target){
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
