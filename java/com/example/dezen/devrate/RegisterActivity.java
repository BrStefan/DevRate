package com.example.dezen.devrate;


//* 21/06/2018 - 10:40 *\\


import android.app.ProgressDialog;
import android.content.Intent;
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

public class RegisterActivity extends AppCompatActivity
{
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etUser;
    private EditText etPassword2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registeractivity);

        // Get Reference to variables
        etUser = (EditText) findViewById(R.id.User);
        etPassword = (EditText) findViewById(R.id.Pass);
        etPassword2 = (EditText) findViewById(R.id.Pass2);
        etEmail = (EditText) findViewById(R.id.Email);
    }
    // Triggers when REGISTER Button clicked
    public void checkRegister(View arg0)
    {
        // Get data from fields
        final String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();
        final String password2 = etPassword2.getText().toString();
        final String user = etUser.getText().toString();

        // Initialize AsyncRegister() class with data received
        new RegisterActivity.AsyncRegister().execute(user,password,password2,email);

    }

    private class AsyncRegister extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(RegisterActivity.this);
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
                url = new URL("http://xoldoxapp.000webhostapp.com/register.php");
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
                        .appendQueryParameter("password", params[1])
                        .appendQueryParameter("password2", params[2])
                        .appendQueryParameter("email", params[3]);
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
                //Successful register
                Toast.makeText(RegisterActivity.this, "Register done", Toast.LENGTH_LONG).show();
                Intent intent = new Intent (RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
            else if (result.equalsIgnoreCase("Password not match"))
            {
                // Passwords mismatch
                Toast.makeText(RegisterActivity.this, "Those 2 password don't match", Toast.LENGTH_LONG).show();
            }
            else if (result.equalsIgnoreCase("Username exists"))
            {
                // Username exists
                Toast.makeText(RegisterActivity.this, "Username already taken", Toast.LENGTH_LONG).show();
            }
            else if (result.equalsIgnoreCase("Password lenght"))
            {
                // Pasword restricitons
                Toast.makeText(RegisterActivity.this, "Password must be between 4 and 30 characters", Toast.LENGTH_LONG).show();
            }
            else if (result.equalsIgnoreCase("Username lenght"))
            {
                // Username restricitons
                Toast.makeText(RegisterActivity.this, "Username must be between 4 and 30 characters", Toast.LENGTH_LONG).show();
            }
            else if (result.equalsIgnoreCase("Email exists"))
            {
                // Email exists
                Toast.makeText(RegisterActivity.this, "Email already used", Toast.LENGTH_LONG).show();
            }
            else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful"))
            {
                Toast.makeText(RegisterActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();
            }
        }
    }
}