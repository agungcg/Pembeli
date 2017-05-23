package ptk111.com.pembeli;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.ButterKnife;
import butterknife.Bind;



public class login extends AppCompatActivity {

    private static final String SP = "ptk11.com.pembeli";
    private static final String TAG = login.class.getSimpleName();

    JSONArray arrayUser;

    int iJumlah = 0;
    private int[] listId = new int[100];
    private String[] listNama = new String[100];
    private String[] listUsername = new String[100];
    private String[] listPassword = new String[100];
    private double[] listLatitude = new double[100];
    private double[] listLongitude = new double[100];


    @Bind(R.id.input_username)
    EditText _usernameText;
    @Bind(R.id.input_password)
    EditText _passwordText;
    @Bind(R.id.buttonLogin)
    Button _loginButton;
    @Bind(R.id.buttonSignUp)
    TextView _signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        new login.AmbilData().execute( "http://agungcahya.esy.es/server.php?operasi=viewPembeli" );

        SharedPreferences sp = getSharedPreferences(SP, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        if (sp.getString("st", "GAGAL").equals("active")) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        ButterKnife.bind(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                View view = login.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                login();
            }
        });

        _signUpButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(login.this, "Sign UP", Toast.LENGTH_SHORT).show();
                /*
                // Start the Signup activity
                Intent intent = new Intent(login.this, RegisterAct.class);
                startActivity(intent);
                finish();
                //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                */
            }
        });
    }


    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        checkExist(username, password);
    }


    void checkExist(final String un, final  String pw) {
        int stop = 0;
        int i = 0;
        new login.AmbilData().execute( "http://agungcahya.esy.es/server.php?operasi=viewPembeli" );
        while ((i < iJumlah) && (stop == 0)) {
            if (listUsername[i].equals(un.toString())) {
                stop = 1;
                if(listPassword[i].equals(pw.toString())){
                    SharedPreferences sp = getSharedPreferences(SP, MODE_PRIVATE);
                    SharedPreferences.Editor ed = sp.edit();
                    ed.putInt("id",listId[i]);
                    ed.putString("nm",listNama[i]);
                    ed.putString("un", un);
                    ed.putString("pw", pw);
                    ed.putString("st","active");
                    ed.commit();
                    onLoginSuccess();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(login.this, "Kata Sandi yang Anda Masukkan Salah", Toast.LENGTH_SHORT).show();
                }
            }
            i++;
        }
        if (stop == 0) {
            Toast.makeText(login.this, "Email tidak Terdaftar", Toast.LENGTH_SHORT).show();
        }
    }


    public void onLoginSuccess() {
        Toast.makeText(getBaseContext(), "Login Berhasil", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }


    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login Gagal", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }


    public boolean validate() {
        boolean valid = true;

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (username.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            _usernameText.setError("Email yang Anda Masukkan tidak Sah");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty()) {
            _passwordText.setError("Kata Sandi tidak Boleh Kosong");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


    private class AmbilData extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... strUrl) {
            Log. v ( "yw" , "mulai ambil data" );
            String hasil= "" ;
            //ambil data dari internet
            InputStream inStream = null ;
            int len = 500 ; //buffer
            try {
                URL url = new URL(strUrl[ 0 ]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //timeout
                conn.setReadTimeout( 10000 /* milliseconds */ );
                conn.setConnectTimeout( 15000 /* milliseconds */ );
                conn.setRequestMethod( "GET" );
                conn.connect();
                int response = conn.getResponseCode();
                inStream = conn.getInputStream(); //ambil stream data
                //konversi stream ke string
                Reader r = null ;
                r = new InputStreamReader(inStream, "UTF-8" );
                char [] buffer = new char [len];
                r.read(buffer);
                hasil = new String(buffer);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inStream != null ) {
                    try {
                        inStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return hasil;
        }

        protected void onPostExecute(String result) {
            parseDataPembeli(result);
        }
    }


    private void parseDataPembeli(String hasil){
        int id;
        double latitude, longitude;
        String name, username, password;
        iJumlah = 1;
        try {

            arrayUser = new JSONArray(hasil);

            for (int i = 0; i < arrayUser.length(); i++) {
                JSONObject jsonChildNode = arrayUser.getJSONObject(i);
                id = jsonChildNode.optInt("id");
                name = jsonChildNode.optString("nama");
                username = jsonChildNode.optString("username");
                password = jsonChildNode.getString("password");
                latitude = jsonChildNode.optDouble("latitude");
                longitude = jsonChildNode.optDouble("longitude");

                iJumlah = iJumlah + i;
                listId[i] = id;
                listNama[i] = name;
                listUsername[i] = username;
                listPassword[i] = password;
                listLatitude[i] = latitude;
                listLongitude[i] = longitude;


            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}



