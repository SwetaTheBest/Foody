package com.swetajain.foody;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    EditText editName, editPrice;
    ImageView imageView;
    Button chooseButton, addButton, viewListButton;
    public static SQLiteHelper sSQLiteHelper;
    final static int REQUEST_GALLERY_CODE = 888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        sSQLiteHelper = new SQLiteHelper(this, "FoodDatabase.sqlite",
                null, 1);
        sSQLiteHelper.queryData();

        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_GALLERY_CODE
                );
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sSQLiteHelper.insertData(
                            editName.getText().toString().trim(),
                            editPrice.getText().toString().trim(),
                            imageViewToByte(imageView)
                    );

                    Toast.makeText(getApplicationContext(),
                            "Added Successfully!", Toast.LENGTH_SHORT).show();
                    editName.setText("");
                    editPrice.setText("");
                    imageView.setImageResource(R.mipmap.ic_launcher);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        viewListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FoodList.class));
            }
        });


    }//end of onCreate

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_GALLERY_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_GALLERY_CODE);
            } else Toast.makeText(getApplicationContext(),
                    "You don't have permission to access this resource!", Toast.LENGTH_SHORT).show();
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_GALLERY_CODE
                && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();

            try {
                InputStream inputStream = null;
                if (uri != null) {
                    inputStream = getContentResolver().openInputStream(uri);
                }
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    public static byte[] imageViewToByte(ImageView imageView) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    public void init() {
        editName = findViewById(R.id.name);
        editPrice = findViewById(R.id.price);
        imageView = findViewById(R.id.imageView);
        addButton = findViewById(R.id.add);
        chooseButton = findViewById(R.id.choose);
        viewListButton = findViewById(R.id.view_list);
    }

}//end of class
