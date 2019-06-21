package com.swetajain.foody;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class FoodListAdapter extends BaseAdapter {
    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */

    private Context context;
    private int layout;
    private ArrayList<Food> foodArrayList;

    FoodListAdapter(Context context, int layout, ArrayList<Food> foodArrayList) {
        this.context = context;
        this.layout = layout;
        this.foodArrayList = foodArrayList;
    }

    @Override
    public int getCount() {
        return foodArrayList.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return foodArrayList.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @return A View corresponding to the data at the specified position.
     */


    private class ViewHolder {
        ImageView mImageView;
        TextView mTextViewName, mTextViewPrice;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder viewHolder = new ViewHolder();

        if (row == null) {
            LayoutInflater layoutInflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(layout, null);

            viewHolder.mTextViewName = row.findViewById(R.id.food_name);
            viewHolder.mTextViewPrice = row.findViewById(R.id.food_price);
            viewHolder.mImageView = row.findViewById(R.id.food_image);
            row.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) row.getTag();
        }

        Food food = foodArrayList.get(position);
        viewHolder.mTextViewName.setText(food.getName());
        viewHolder.mTextViewPrice.setText(food.getPrice());

        byte[] foodImage = food.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(foodImage, 0, foodImage.length);
        viewHolder.mImageView.setImageBitmap(bitmap);

        return row;
    }




}

