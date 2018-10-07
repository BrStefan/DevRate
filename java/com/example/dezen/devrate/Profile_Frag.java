package com.example.dezen.devrate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class Profile_Frag extends Fragment{

    private Button butChoose,butUpload,butSkill;
    private EditText text;
    private ImageView seePhoto;
    private final int IMG_REQUEST = 1;
    private Bitmap img;
    private String UploadURL = "http://xoldoxapp.000webhostapp.com/ImageUploadApp/insertphoto.php";
    private String SkillURL = "http://xoldoxapp.000webhostapp.com/insertskill.php";
    private String skill;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    ProgressDialog progressDialog,progressDialog1;
    ArrayList<String> Skill_List;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.profile_frag,container,false);

        butChoose = (Button) view.findViewById(R.id.Gallery_button);
        butUpload = (Button) view.findViewById(R.id.Upload_button);
        butSkill = (Button) view.findViewById(R.id.InsertSkill);
        seePhoto = (ImageView) view.findViewById(R.id.photoview);
        text = (EditText) view.findViewById(R.id.Skill);
        Skill_List = new ArrayList<String>();


        butChoose.setOnClickListener(
                new Button.OnClickListener () {
                    @Override
                    public void onClick(View view) {
                        selectImage();
                    }
                }
        );

        butUpload.setOnClickListener(
                new Button.OnClickListener () {
                    @Override
                    public void onClick(View view) {
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage("Uploading, please wait...");
                        progressDialog.show();
                        uploadImage();
                    }
                }
        );

        butSkill.setOnClickListener(
                new Button.OnClickListener () {
                    @Override
                    public void onClick(View view) {
                        insertSkill();
                    }
                }
        );

        initRecyclerView();
        return view;
    }

    private void initRecyclerView()
    {
        RecyclerView recyclerView = view.findViewById(R.id.rv);
        RecyclerViewAdapter_Skills adapter = new RecyclerViewAdapter_Skills(Skill_List, getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Profile");
    }

    private void selectImage()
    {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i,IMG_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null)
        {
            Uri path = data.getData();
            try
            {
                img = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), path);
                img = getResizedBitmap(img,150,150);
                seePhoto.setImageBitmap(img);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }
    }

    private void uploadImage()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UploadURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(),"Profile picture successfully changed!",Toast.LENGTH_LONG).show();
                            ((Main)getActivity()).ChangeImage(img);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();

                params.put("name",getName());
                params.put("image",imageToString(img));


                return params;

            }
        };

       com.codinginflow.volleysingletonexample.MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest) ;
    }

    private String imageToString(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] imgBytes = baos.toByteArray();
        return Base64.encodeToString(imgBytes,Base64.DEFAULT);
    }

    protected String getName()
    {
        sharedPref = getContext().getSharedPreferences("Nume",0);
        editor = sharedPref.edit();
        String Nume=sharedPref.getString("user",null);
        return Nume;
    }

    public Bitmap getResizedBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap resizedBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float scaleX = newWidth / (float) bitmap.getWidth();
        float scaleY = newHeight / (float) bitmap.getHeight();
        float pivotX = 0;
        float pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

        Canvas canvas = new Canvas(resizedBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

        return resizedBitmap;
    }

    void insertSkill()
    {
        skill = text.getText().toString();
        progressDialog1 = new ProgressDialog(getActivity());
        progressDialog1.setMessage("Inserting skill, please wait...");
        progressDialog1.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SkillURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                                JSONObject jsonObject = new JSONObject(response);
                                String result = jsonObject.getString("response");
                                progressDialog1.dismiss();
                                if (result.equalsIgnoreCase("nothing"))
                                {
                                    Toast.makeText(getContext(), "Please insert something in the skill field!", Toast.LENGTH_LONG).show();
                                }
                                else if(result.equalsIgnoreCase("exists"))
                                {
                                    Toast.makeText(getContext(), "Skill already introduced!", Toast.LENGTH_LONG).show();
                                }
                                else if (result.equalsIgnoreCase("ok"))
                                {
                                    Skill_List.add(skill);
                                    Toast.makeText(getContext(), "Image successfully uploaded!", Toast.LENGTH_LONG).show();
                                }
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();

                params.put("name",getName());
                params.put("skill",skill);

                return params;

            }
        };

        com.codinginflow.volleysingletonexample.MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest) ;
    }

}
