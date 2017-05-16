package ptk111.com.pembeli;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import butterknife.ButterKnife;
import butterknife.Bind;



public class login extends AppCompatActivity {

    private static final String SELECT_PEMBELI = "http://giftstoreid.xyz/allpembeli.php";
    private static final String SP = "ptk11.com.pembeli";
    private static final String TAG = login.class.getSimpleName();
    private static final int REQUEST_SIGNUP = 0;

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

        SharedPreferences sp = getSharedPreferences(SP, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        if (sp.getString("st", "GAGAL").equals("active")) {
            Intent in = new Intent(login.this, MainActivity.class);
            startActivity(in);
            finish();
        }

        ButterKnife.bind(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /*
                View view = login.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                login();
                */
                Intent intent = new Intent(login.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        /*
        _signUpButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(login.this, RegisterAct.class);
                startActivity(intent);
                finish();
                //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        */
    }

    /*
    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(login.this,
                R.style.AppTheme.);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        checkExist(username, password);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    void checkExist(final String un, final  String pw) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, SELECT_PEMBELI, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.i(TAG, "onResponse: playerResult= " + response.toString());
                        String name;
                        String pass;
                        int stop = 0;
                        try {
                            JSONArray jsonArray = response.getJSONArray("users");

                            if (response.getString("success").equalsIgnoreCase("1")) {
                                int i;
                                i = 0;
                                while ((i < jsonArray.length()) && (stop == 0)) {
                                    JSONObject pembeli = jsonArray.getJSONObject(i);
                                    name = pembeli.getString("nama");
                                    pass = pembeli.getString("pass");
                                    if (name.equals(un.toString())) {
                                        stop = 1;
                                        if(pass.equals(pw.toString())){
                                            SharedPreferences sp = getSharedPreferences(SP, MODE_PRIVATE);
                                            SharedPreferences.Editor ed = sp.edit();
                                            ed.putString("un", un);
                                            ed.putString("pw", pw);
                                            ed.putString("st","active");
                                            ed.commit();

                                            Toast.makeText(LoginActivity.this, "Log in successful", Toast.LENGTH_SHORT).show();
                                            Intent in = new Intent(LoginActivity.this, MainMenu.class);
                                            startActivity(in);
                                            finish();
                                        }else{
                                            Toast.makeText(LoginActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    i++;
                                }

                                if (stop == 0) {
                                    Toast.makeText(LoginActivity.this, "Username doesn't exist", Toast.LENGTH_SHORT).show();
                                }else{

                                }
                            } else if (response.getString("success").equalsIgnoreCase("0")) {

                            }
                        } catch (JSONException e) {

                            e.printStackTrace();
                            Log.e(TAG, "parseLocationResult: Error=" + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //menampilkan error pada logcat
                        Log.e(TAG, "onErrorResponse: Error= " + error);
                        Log.e(TAG, "onErrorResponse: Error= " + error.getMessage());

                    }
                }
        );

        AppController.getInstance().addToRequestQueue(request);
    }
    */
}



