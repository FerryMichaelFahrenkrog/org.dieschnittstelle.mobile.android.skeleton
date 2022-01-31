package org.dieschnittstelle.mobile.android.skeleton;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

import impl.RetrofitRemoteDataItemCRUDOperationsImpl;
import impl.RoomLocalDataItemCRUDOperationsImpl;
import impl.SyncedDataItemCRUDOperationsImpl;
import model.IDataItemCRUDOperations;
import model.User;
import tasks.AuthenticateUserTask;

// AUSGELAGERT IN MAIN!!!!!
/*
public class LoginActivity extends AppCompatActivity {
    private EditText editTextemailAdresse;
    private EditText editTextpassword;

    private Button btnLogin;
    private TextView txtHinweis;
    private ProgressBar progressBarLogin;

    public static IDataItemCRUDOperations crudOperations;

    private final String userName = "Admin@gmx.de";
    private final String password = "123456";

    boolean isValid = false;

    private int counter = 5;

    private boolean SKIP_LOGIN = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        editTextemailAdresse = findViewById(R.id.userName);
        editTextpassword = findViewById(R.id.emailpassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtHinweis = findViewById(R.id.txtWarnmeldung);
        progressBarLogin = findViewById(R.id.progressBarLogin);

        RoomLocalDataItemCRUDOperationsImpl roomTodoCRUDOperations = new RoomLocalDataItemCRUDOperationsImpl(this);
        RetrofitRemoteDataItemCRUDOperationsImpl retrofitTodoCRUDOperations = new RetrofitRemoteDataItemCRUDOperationsImpl();

        crudOperations = new SyncedDataItemCRUDOperationsImpl(roomTodoCRUDOperations, retrofitTodoCRUDOperations);

        hebeHinweismeldungHervor();

        btnLogin.setEnabled(false);


        editTextpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editTextpassword.getText().length() > 0 && editTextemailAdresse.getText().length() > 0) {
                    btnLogin.setEnabled(true);
                } else {
                    btnLogin.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnLogin.setOnClickListener(v -> {
            showLoginDialog();
        });

    }

    public void showLoginDialog() {
        //Werte auslesen und zwischenspeichern
        String inputEmail = editTextemailAdresse.getText().toString();
        String inputPassword = editTextpassword.getText().toString();

        //Checken, ob irgendein Feld leer ist, wenn ja, Snackbar + WARNMELDUNG
        if (inputEmail.isEmpty() || inputPassword.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Fülle beide Felder aus!", Toast.LENGTH_SHORT).show();
            txtHinweis.setText("Fülle beide Felder aus!!");
        } else {
            //Wenn beide Felder befüllt sind - checke die Formate
            boolean korrektesEmailFormat = isValidEmail(inputEmail);
            boolean korrektesPWFormat = isValidPassword(inputPassword);

            //Wenn die Formate korrekt sind, dann...
            if (korrektesEmailFormat && korrektesPWFormat) {
                isValid = validate(inputEmail, inputPassword);

                if (isValid == false) {
                    counter--;
                    Toast.makeText(getApplicationContext(), "Falscher Nutzername oder Passwort!", Toast.LENGTH_SHORT).show();
                    txtHinweis.setText("Versuche übrig: " + counter);

                    if (counter == 0) {
                        btnLogin.setEnabled(false);
                        Toast.makeText(getApplicationContext(), "Neu starten die Anwendung pls!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    User currentUser = new User(inputEmail, inputPassword);

                    new AuthenticateUserTask(progressBarLogin, crudOperations, isAuthenticated -> {
                        if (isAuthenticated) {
                            Toast.makeText(getApplicationContext(), "LOGIN Erfolgreich", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            txtHinweis.setText("KEIN RICHTIGER USER!!!!");
                        }
                    }).execute(currentUser);
                }

            } else if (korrektesEmailFormat == false) {
                txtHinweis.setText("Dies ist keine gültige Email! (Fehlt @ oder .)?");
            } else if (korrektesPWFormat == false) {
                txtHinweis.setText("Dies ist keine gültiges PW! (6 ZEICHEN!)");
            } else {
                txtHinweis.setText("Format nicht korrekt!");
            }

            editTextemailAdresse.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                    btnLogin.setEnabled(false);
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    txtHinweis.setText("");
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            editTextpassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    txtHinweis.setText("");
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

    private boolean validate(String name, String password) {
        if (name.equals(userName) && password.equals(this.password)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private boolean isValidPassword(String password) {
        String emailRegex = "^[0-9]{6}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (password == null)
            return false;
        return pat.matcher(password).matches();
    }

    public void hebeHinweismeldungHervor() {
        if (txtHinweis.toString() != "") {
            txtHinweis.setTextColor(Color.RED);
        }
    }
}
 */
