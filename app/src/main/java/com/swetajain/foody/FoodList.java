package com.swetajain.foody;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

import static com.swetajain.foody.MainActivity.sSQLiteHelper;

public class FoodList extends AppCompatActivity {
    GridView mGridView;
    int newPosition;
    ArrayList<Food> mFoodArrayList;
    FoodListAdapter mFoodListAdapter = null;
    ImageView imageViewFood;
    Cursor cursor = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_food);

        mGridView = findViewById(R.id.grid_view);
        mFoodArrayList = new ArrayList<>();
        mFoodListAdapter = new FoodListAdapter
                (this, R.layout.food_item, mFoodArrayList);
        mGridView.setAdapter(mFoodListAdapter);

        //get data from SQLite


        cursor = sSQLiteHelper.getData();
        mFoodArrayList.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String price = cursor.getString(2);
            byte[] image = cursor.getBlob(3);
            mFoodArrayList.add(new Food(name, price, image, id));
        }

        mFoodListAdapter.notifyDataSetChanged();

        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final CharSequence[] items = {"UPDATE", "DELETE"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(FoodList.this);
                newPosition = position;
                dialog.setTitle("Choose an action");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Cursor cursor1 =
                                sSQLiteHelper.getData();
                        ArrayList<Integer> idArrayList;
                        idArrayList = new ArrayList<>();
                        while (cursor1.moveToNext()) {
                            idArrayList.add(cursor1.getInt(0));
                        }
                        if (which == 0) {
                            //update
                            Toast.makeText(getApplicationContext(),
                                    "update..", Toast.LENGTH_SHORT).show();
                            //show dialogue update here
                            showDialogUpdate(FoodList.this, idArrayList.get(position));


                        } else {
                            //delete
                            Toast.makeText(getApplicationContext(),
                                    "Delete..", Toast.LENGTH_SHORT).show();
                            showDialogDelete(idArrayList.get(position));

                        }
                    }
                });
                dialog.show();
                return true;
            }
        });
    }

    private void showDialogDelete(final int idFood) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(FoodList.this);
        builder.setTitle("Warning!");
        builder.setMessage("Are you sure you want to delete this item?");
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    sSQLiteHelper.deleteData(idFood);
                    Toast.makeText(getApplicationContext(),
                            "Deleted successfully !", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Error", e.getMessage());
                }
                updateFoodList();
            }
        });

        builder.show();

    }


    private void showDialogUpdate(Activity activity, final int updatePosition) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_food_activity);
        dialog.setTitle(R.string.update);

        imageViewFood = dialog.findViewById(R.id.imageViewUpdate);
        final EditText editName = dialog.findViewById(R.id.nameUpdate);
        final EditText editPrice = dialog.findViewById(R.id.priceUpdate);
        final Button updateBtn = dialog.findViewById(R.id.updateButton);

        //set width and height

        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.7);

        Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);
        dialog.show();

        byte[] oldImage = mFoodArrayList.get(newPosition).getImage();
        Bitmap oldBitmap = BitmapFactory.decodeByteArray(oldImage, 0, oldImage.length);
        imageViewFood.setImageBitmap(oldBitmap);
        final String oldName = mFoodArrayList.get(newPosition).getName();
        final String oldPrice = mFoodArrayList.get(newPosition).getPrice();

        imageViewFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //request image files

                ActivityCompat.requestPermissions(FoodList.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MainActivity.REQUEST_GALLERY_CODE);

            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = editName.getText().toString().trim();
                String newDescription =  editPrice.getText().toString().trim();
                if (newName.isEmpty())
                    newName = oldName;
                if (newDescription.isEmpty())
                    newDescription = oldPrice;

                try {
                    sSQLiteHelper.updateData(
                            updatePosition,newName,newDescription,
                            MainActivity.imageViewToByte(imageViewFood)

                    );

                    dialog.dismiss();

                    Toast.makeText(getApplicationContext(),
                            "Updated successfully!", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Update error", e.getMessage());
                }
                updateFoodList();
            }
        });

    }

    private void updateFoodList() {
        //retrieve data from SQLite
        Cursor cursor = sSQLiteHelper.getData();
        mFoodArrayList.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String price = cursor.getString(2);
            byte[] image = cursor.getBlob(3);

            mFoodArrayList.add(new Food(name, price, image, id));
        }
        mFoodListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == MainActivity.REQUEST_GALLERY_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                if (uri != null) {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imageViewFood.setImageBitmap(bitmap);

                }


            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Activity Result", e.getMessage());
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == MainActivity.REQUEST_GALLERY_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, MainActivity.REQUEST_GALLERY_CODE);

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

}
