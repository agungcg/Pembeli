package ptk111.com.pembeli;

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

import butterknife.Bind;
import butterknife.ButterKnife;
import ptk111.com.pembeli.Database.LokasiUser;

public class signup extends AppCompatActivity {

    private static final String SP = "ptk11.com.pembeli";
    private static final String TAG = login.class.getSimpleName();

    JSONArray arrayUser;
    LokasiUser lokasiUser = new LokasiUser();

    int iJumlah = 0;
    private int[] listId = new int[100];
    private String[] listNama = new String[100];
    private String[] listTelepon = new String[100];
    private String[] listUsername = new String[100];
    private String[] listPassword = new String[100];
    private double[] listLatitude = new double[100];
    private double[] listLongitude = new double[100];

    @Bind(R.id.input_nama)
    EditText _namaText;
    @Bind(R.id.input_telepon)
    EditText _teleponText;
    @Bind(R.id.input_email)
    EditText _emailText;
    @Bind(R.id.input_passwordSignup)
    EditText _passwordSignupText;
    @Bind(R.id.buttonBack)
    Button _backButton;
    @Bind(R.id.buttonCreate)
    Button _createButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        new signup.AmbilData().execute( "http://agungcahya.esy.es/server.php?operasi=viewPembeli" );

        ButterKnife.bind(this);

        _backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), login.class);
                startActivity(intent);
                finish();
            }
        });

        _createButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                View view = signup.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                create();
            }
        });
    }


    public void create() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onCreateFailed();
            return;
        }

        String email = _emailText.getText().toString();

        checkExist(email);
    }

    void checkExist(final String em) {
        int stop = 0;
        int i = 0;
        new signup.AmbilData().execute( "http://agungcahya.esy.es/server.php?operasi=viewPembeli" );
        while ((i < iJumlah) && (stop == 0)) {
            if (listUsername[i].equals(em.toString())) {
                stop = 1;
                Toast.makeText(signup.this, "Email sudah Terdaftar", Toast.LENGTH_SHORT).show();
            }
            i++;
        }
        if (stop == 0) {
            Toast.makeText(signup.this, "Akun Berhasil Dibuat", Toast.LENGTH_SHORT).show();
            //tambahAkun();
            String nama = _namaText.getText().toString();
            String telepon = _teleponText.getText().toString();
            String username = _emailText.getText().toString();
            String password = _passwordSignupText.getText().toString();

            lokasiUser.createAkun(nama,telepon,username,password);

            Intent intent = new Intent(getApplicationContext(), login.class);
            startActivity(intent);
            finish();
        }
    }

    public void onCreateSuccess() {
        Toast.makeText(getBaseContext(), "Login Berhasil", Toast.LENGTH_LONG).show();
        _createButton.setEnabled(true);
    }


    public void onCreateFailed() {
        Toast.makeText(getBaseContext(), "Login Gagal", Toast.LENGTH_LONG).show();
        _createButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String nama = _namaText.getText().toString();
        String telepon = _teleponText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordSignupText.getText().toString();

        if (nama.isEmpty()) {
            _namaText.setError("Nama tidak Boleh Kosong");
            valid = false;
        } else {
            _namaText.setError(null);
        }

        if (telepon.isEmpty() || telepon.length()<10 || telepon.length()>13) {
            _teleponText.setError("Nomer Telepon yang Anda Masukkan tidak Sah");
            valid = false;
        } else {
            _teleponText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Email yang Anda Masukkan tidak Sah");
            valid = false;
        } else {
            _emailText.setError(null);
        }


        if (password.isEmpty()) {
            _passwordSignupText.setError("Password tidak Boleh Kosong");
            valid = false;
        } else {
            _passwordSignupText.setError(null);
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
        String name, username, password, telepon;
        iJumlah = 1;
        try {

            arrayUser = new JSONArray(hasil);

            for (int i = 0; i < arrayUser.length(); i++) {
                JSONObject jsonChildNode = arrayUser.getJSONObject(i);
                id = jsonChildNode.optInt("id");
                name = jsonChildNode.optString("nama");
                telepon = jsonChildNode.optString("telepon");
                username = jsonChildNode.optString("username");
                password = jsonChildNode.getString("password");
                latitude = jsonChildNode.optDouble("latitude");
                longitude = jsonChildNode.optDouble("longitude");

                iJumlah = iJumlah + i;
                listId[i] = id;
                listNama[i] = name;
                listTelepon[i] = telepon;
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
