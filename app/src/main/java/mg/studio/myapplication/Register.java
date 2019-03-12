package mg.studio.myapplication;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


public class Register extends AppCompatActivity {
    private static final String TAG = Register.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private SessionManager session;
    private ProgressDialog pDialog;
    private String name;
    Feedback feedback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        inputFullName = findViewById(R.id.name);
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        btnRegister = findViewById(R.id.btnRegister);
        btnLinkToLogin = findViewById(R.id.btnLinkToLoginScreen);


        // Preparing the Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        // Session manager
        session = new SessionManager(getApplicationContext());
        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    // Avoid repeated clicks by disabling the button
                    btnRegister.setClickable(false);
                    //Register the user
                    registerUser(name, email, password);

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        Login.class);
                startActivity(i);
                finish();
            }
        });

    }

    /**
     * Register a new user to the server database
     * @param name     username
     * @param email    email address, which should be unique to the user
     * @param password length should be < 50 characters
     */
    private void registerUser(final String name, final String email,
                              final String password) {

        pDialog.setMessage("Registering ...");
        if (!pDialog.isShowing()) pDialog.show();
        //Todo: Need to check Internet connection
        OnMyRegist(name, email, password);
    }


//    /**
//     * register event
//     * @param v
//     */
    public void OnMyRegist(String uname, String email, String password){
        feedback = new Feedback();
        //The processing that determine whether the account/password is correct...

        //use the DBOpenHelper
        DBOpenHelper helper = new DBOpenHelper(this,"daniel.db",null,1);
        SQLiteDatabase db = helper.getWritableDatabase();
        //search the database by userID.
        Cursor c = db.query("user_tb",null,"userID=?",new String[]{email},null,null,null);
        //it means that the username has already existed if c.getCount() >= 1.
        if(c!=null && c.getCount() >= 1){
            Toast.makeText(this, "The usename is already used.", Toast.LENGTH_SHORT).show();
            c.close();
            btnRegister.setClickable(true);
        }
        //if else, it means the username can be used and insert the data.
        else{
            //insert data
            ContentValues values= new ContentValues();
            values.put("userID",email);
            values.put("pwd",password);
            values.put("userName",uname);
            long rowid = db.insert("user_tb",null,values);

            feedback.setName(uname);
            Toast.makeText(this, "Success！", Toast.LENGTH_SHORT).show();//The toast to tell the user it is successed.
            Intent intent = new Intent(getApplication(), Login.class);
            intent.putExtra("feedback", feedback);
            startActivity(intent);
            finish();
        }
        if (pDialog.isShowing()) pDialog.dismiss();
        db.close();
    }

}

