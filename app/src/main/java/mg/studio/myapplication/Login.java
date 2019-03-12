package mg.studio.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


public class Login extends AppCompatActivity {
    private EditText inputEmail, inputPassword;
    private ProgressDialog progressDialog;
    private SessionManager session;
    private Feedback feedback;
    private Button loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        feedback = new Feedback();
        /**
         * If the user just registered an account from Register.class,
         * the parcelable should be retrieved
         */
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            // Retrieve the parcelable
            Feedback feedback = bundle.getParcelable("feedback");
            // Get the from the object
            String userName = feedback.getName();
            TextView display = findViewById(R.id.display);
            display.setVisibility(View.VISIBLE);
            String prompt = userName.substring(0, 1).toUpperCase() + userName.substring(1) + " " + getString(R.string.account_created);
            display.setText(prompt);

        }

        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        loginButton = findViewById(R.id.btnLogin);


        /**
         * Prepare the dialog to display when the login button is pressed
         */
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);


        /**
         * Use the SessionManager class to check whether
         * the user already logged in, is yest  then go to the MainActivity
         */
        session = new SessionManager(getApplicationContext());

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty()) {

                    // Avoid multiple clicks on the button
                    loginButton.setClickable(false);

                    //Todo : ensure the user has Internet connection

                    // Display the progress Dialog
                    progressDialog.setMessage("Logging in ...");
                    if (!progressDialog.isShowing())
                        progressDialog.show();

                    //Todo: need to check weather the user has Internet before attempting checking the data
                    // Start fetching the data from the Internet
//            new OnlineCredentialValidation().execute(email,password);
                    OnMyLogin(email,password);

                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            R.string.enter_credentials, Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        if (session.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }


    }



    /**
     * Press the button register, go to Registration form
     *
     * @param view from the activity_login.xml
     */
    public void btnRegister(View view) {
        startActivity(new Intent(getApplicationContext(), Register.class));
        finish();
    }


//    /**
//     * login event
//     * @param v
//     */
    public void OnMyLogin(String email, String password){
        //The processing that determine whether the account/password is correct...

        //use the DBOpenHelper （daniel.db is the name of the database）
        DBOpenHelper helper = new DBOpenHelper(this,"daniel.db",null,1);
        SQLiteDatabase db = helper.getWritableDatabase();
        //Go to the database to query according to the account/password entered on the screen（user_tb is the table name）
        Cursor c = db.query("user_tb",null,"userID=? and pwd=?",new String[]{email,password},null,null,null);
        //if there exist the email and the password is correct.
        if(c!=null && c.getCount() >= 1){
//        String[] cols = c.getColumnNames();
        while(c.moveToNext()){
            feedback.setName(c.getString(c.getColumnIndex("userName")));
        }
            c.close();
            db.close();
            Intent intent = new Intent(getApplication(), MainActivity.class);
            intent.putExtra("feedback", feedback);
            startActivity(intent);
            finish();
        }
        //if it not exist.
        else{
            c.close();
            db.close();
            Toast.makeText(this, "The email or the password is wrong!", Toast.LENGTH_LONG).show();
            if (progressDialog.isShowing()) progressDialog.dismiss();
            loginButton.setClickable(true);
            return;
        }
    }
    
}