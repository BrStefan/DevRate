package com.example.dezen.devrate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

public class Main extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    private ImageView profile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);

        sharedPref = Main.this.getSharedPreferences("Nume",MODE_PRIVATE);
        editor = sharedPref.edit();
        String Nume=sharedPref.getString("user",null);
        if(Nume == null)
        {
            Intent i = new Intent(this,MainActivity.class);
            startActivity(i);
        }

        displaySelectedScreen(R.id.nav_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = LayoutInflater.from(this).inflate(R.layout.nav_header_main, null);
        navigationView.addHeaderView(header);

        TextView Username = (TextView) header.findViewById(R.id.nume_profil);
        Username.setText(Nume);

        //load profile picture for account
        profile = (ImageView) header.findViewById(R.id.profile_pic);
        String url = String.format("http://xoldoxapp.000webhostapp.com/ImageUploadApp/uploads/%s.jpg",Nume);
        Picasso.get().load(url).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(profile);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Fragment frag = null;
        //noinspection SimplifiableIfStatement
        if (id == R.id.nav_profile)
        {
            frag= new Profile_Frag();
            return true;
        }
        else if(id == R.id.nav_home)
        {
            frag = new Home_Frag();
            return true;
        }
        else if(frag!=null)
        {
            frag = new Home_Frag();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displaySelectedScreen(int id)
    {
        Fragment fragment=null;
        switch (id)
        {
            case R.id.nav_profile:
                fragment = new Profile_Frag();
                break;

            case R.id.nav_home:
                fragment = new Home_Frag();
                break;

            default:
                fragment = new Home_Frag();
                break;
        }

        if(fragment!=null)
        {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_main,fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(id!=R.id.nav_disconnect) displaySelectedScreen(id);
        else
        {
            editor.putString("user", null);
            editor.commit();

            editor.putString("pass", null);
            editor.commit();

            Intent intent = new Intent (Main.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }

    public void ChangeImage(Bitmap image)
    {

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        profile = (ImageView) hView.findViewById(R.id.profile_pic);
        profile.setImageBitmap(image);
    }
}
