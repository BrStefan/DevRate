package com.example.dezen.devrate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity
{
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    protected  String userSP;
    protected String passSP;

    // method to Register Page
    public void ToRegister (View view)
    {
        Intent intent = new Intent (this, RegisterActivity.class);
        startActivity(intent);
    }

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    private EditText etEmail;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = MainActivity.this.getSharedPreferences("Nume",0);
        editor = sharedPref.edit();

        //If it's first time he opens the app, login him.
        String Nume=sharedPref.getString("user",null);
        if(Nume!=null)
        {
            Intent intent = new Intent (MainActivity.this, Main.class);
            startActivity(intent);
        }


        // Get Reference to variables
        etEmail = (EditText) findViewById(R.id.User);
        etPassword = (EditText) findViewById(R.id.Pass);

    }

    // Triggers when LOGIN Button clicked
    public void checkLogin(View arg0)
    {

        // Get text from email and password fields
        final String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();

        userSP=email;
        passSP=password;

        // Initialize  AsyncLogin() class with email and password
        new AsyncLogin().execute(email,password);

    }

    private class AsyncLogin extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                url = new URL("http://xoldoxapp.000webhostapp.com/login.php");
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
                return "exception";
            }
            try
            {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append POST parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", params[0])
                        .appendQueryParameter("password", params[1]);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
                return "exception";
            }
            try
            {
                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK)
                {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null)
                    {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return(result.toString());
                }
                else
                {
                    return("unsuccessful");
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return "exception";
            }
            finally
            {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            //this method will be running on UI thread
            pdLoading.dismiss();
            if(result.equalsIgnoreCase("Yes"))
            {
                //Save his data to SharedPreferences when he comes back, to redirect him to his account
                editor.putString("user", userSP);
                editor.commit();

                editor.putString("pass", passSP);
                editor.commit();

               //Successful login
                Intent intent = new Intent (MainActivity.this, Main.class);
                startActivity(intent);
                finish();
            }
            else if (result.equalsIgnoreCase("No"))
            {
                // Unsuccessful login
                Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_LONG).show();
            }
            else if (result.equalsIgnoreCase("Activate"))
            {
                // Unsuccessful login
                Toast.makeText(MainActivity.this, "Account not activated", Toast.LENGTH_LONG).show();
            }
            else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful"))
            {
                Toast.makeText(MainActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();
            }
        }
    }


}



