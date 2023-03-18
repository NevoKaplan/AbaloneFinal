package com.example.abalone.play.Logic.Data;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.abalone.R;

import java.util.List;
import java.util.NoSuchElementException;

public class UserAdapter extends ArrayAdapter<User> {

    Context context;
    List<User> objects;
    UserTable userTable;

    public UserAdapter(Context context, int resource, List<User> objects, UserTable userTable) {
        super(context, resource, objects);
        this.objects = objects;
        this.context = context;
        this.userTable = userTable;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
            convertView = layoutInflater.inflate(R.layout.custom_row, parent, false);

            holder = new ViewHolder();
            holder.name = convertView.findViewById(R.id.tvName);
            holder.email = convertView.findViewById(R.id.tvEmail);
            holder.imageView = convertView.findViewById(R.id.image);
            holder.id = convertView.findViewById(R.id.sId);
            holder.trashCan = convertView.findViewById(R.id.trashCan);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        User temp = objects.get(position);
        String fullName = (temp.getName() + " " + temp.getSurname()).trim();
        holder.name.setText(fullName);
        holder.email.setText(temp.getEmail() + "");
        //  holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        System.out.println("Bitmap (UserAdapter): " + temp.getImg().getHeight() + ", " + temp.getImg().getWidth());
        //Drawable d = new BitmapDrawable(context.getResources(), temp.getImg());
        //holder.imageView.setImageDrawable(d);
        scaleImage(holder.imageView, temp.getImg());
        long id = temp.getId();
        holder.id.setText(id + "");
        holder.trashCan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createSureDialog(fullName, id, position);
            }
        });
        return convertView;
    }

    public void createSureDialog(String name, long id, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Student")
                .setMessage("Are you sure you want to delete "  + name + "?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        userTable.deleteUserByID(id);
                        objects.remove(getItem(position));
                        notifyDataSetChanged();
                        Toast.makeText(context, "Student deleted", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void scaleImage(ImageView view, Bitmap bitmap) throws NoSuchElementException {
        // Get bitmap from the the ImageView

        /*try {
            Drawable drawing = view.getDrawable();
            bitmap = ((BitmapDrawable) drawing).getBitmap();
        } catch (NullPointerException e) {
            throw new NoSuchElementException("No drawable on given view");
        } catch (ClassCastException e) {
            // Check bitmap is Ion drawable
            bitmap = Ion.with(view).getBitmap();
        }*/

        // Get current dimensions AND the desired bounding box
        int width = 0;

        try {
            width = bitmap.getWidth();
        } catch (NullPointerException e) {
            throw new NoSuchElementException("Can't find bitmap on given view/drawable");
        }

        int height = bitmap.getHeight();
        int bounding = dpToPx(67);
        Log.i("Test", "original width = " + Integer.toString(width));
        Log.i("Test", "original height = " + Integer.toString(height));
        Log.i("Test", "bounding = " + Integer.toString(bounding));

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        float xScale = ((float) bounding) / width;
        float yScale = ((float) bounding) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;
        Log.i("Test", "xScale = " + Float.toString(xScale));
        Log.i("Test", "yScale = " + Float.toString(yScale));
        Log.i("Test", "scale = " + Float.toString(scale));

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format understood by the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        width = scaledBitmap.getWidth(); // re-use
        height = scaledBitmap.getHeight(); // re-use
        BitmapDrawable result = new BitmapDrawable(context.getResources(), scaledBitmap);
        Log.i("Test", "scaled width = " + Integer.toString(width));
        Log.i("Test", "scaled height = " + Integer.toString(height));

        // Apply the scaled bitmap
        view.setImageDrawable(result);

        // Now change ImageView's dimensions to match the scaled image
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);

        Log.i("Test", "done");
    }

    private int dpToPx(int dp) {
        float density = context.getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }

    static class ViewHolder {
        TextView name;
        TextView email;
        ImageView imageView;
        TextView id;
        ImageView trashCan;
    }
}