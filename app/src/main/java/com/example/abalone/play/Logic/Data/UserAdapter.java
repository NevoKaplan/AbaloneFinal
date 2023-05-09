// Import required packages and classes
package com.example.abalone.play.Logic.Data;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.abalone.R;
import java.util.List;
import java.util.NoSuchElementException;

// Define a custom ArrayAdapter to display user data in a list view
public class UserAdapter extends ArrayAdapter<User> {

    // Define instance variables
    Context context;
    List<User> objects;
    UserTable userTable;

    // Constructor method
    public UserAdapter(Context context, int resource, List<User> objects, UserTable userTable) {
        super(context, resource, objects);
        this.objects = objects;
        this.context = context;
        this.userTable = userTable;
    }

    // Override getView method to customize how the data is displayed in each row of the list view
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        // If convertView is null, inflate the custom_row layout and create a ViewHolder object to hold the row's views
        if (convertView == null) {
            LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
            convertView = layoutInflater.inflate(R.layout.custom_row, parent, false);
            holder = new ViewHolder();
            holder.name = convertView.findViewById(R.id.tvName);
            holder.wins = convertView.findViewById(R.id.tvEmail);
            holder.imageView = convertView.findViewById(R.id.image);
            holder.id = convertView.findViewById(R.id.sId);
            holder.trashCan = convertView.findViewById(R.id.trashCan);
            convertView.setTag(holder);
        }
        // If convertView is not null, retrieve the ViewHolder object from its tag
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        // Retrieve the User object for the current position
        User temp = objects.get(position);
        // Set the name TextView to display the user's full name
        String fullName = (temp.getName() + " " + temp.getSurname()).trim();
        holder.name.setText(fullName);
        // Set the wins TextView to display the number of games the user has won
        String winAmount = "Won " + temp.getWins() + " Games";
        holder.wins.setText(winAmount);
        // Scale and set the user's profile image in the ImageView
        scaleImage(holder.imageView, temp.getImg());
        // Set the id TextView to display the user's ID
        long id = temp.getId();
        holder.id.setText(id + "");
        // Set an onClickListener for the trash can ImageView to delete the current user from the database
        holder.trashCan.setOnClickListener(view -> createSureDialog(fullName, id, position));
        return convertView;
    }

    // Method to create a confirmation dialog before deleting a user from the database
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

    // Method to scale the image to fit inside a bounding box
    private void scaleImage(ImageView view, Bitmap bitmap) throws NoSuchElementException {
    // Get bitmap from the ImageView

        // Get current dimensions AND the desired bounding box
        int width = 0;

        try {
            width = bitmap.getWidth();
        } catch (NullPointerException e) {
            throw new NoSuchElementException("Can't find bitmap on given view/drawable");
        }

        int height = bitmap.getHeight();
        int bounding = dpToPx(67);
// Log current dimensions and bounding box
        Log.i("Test", "original width = " + Integer.toString(width));
        Log.i("Test", "original height = " + Integer.toString(height));
        Log.i("Test", "bounding = " + Integer.toString(bounding));

// Determine how much to scale: the dimension requiring less scaling is
// closer to its side. This way the image always stays inside your
// bounding box AND either x/y axis touches it.
        float xScale = ((float) bounding) / width;
        float yScale = ((float) bounding) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;
// Log the scaling factors
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
// Log the dimensions of the scaled bitmap
        Log.i("Test", "scaled width = " + Integer.toString(width));
        Log.i("Test", "scaled height = " + Integer.toString(height));

// Apply the scaled bitmap
        view.setImageDrawable(result);

// Now change ImageView's dimensions to match the scaled image
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);

// Log completion of the scaling operation
        Log.i("Test", "done");
    }

    // Method to convert dp to pixels
    private int dpToPx(int dp) {
        float density = context.getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }

    // ViewHolder class for the list item
    static class ViewHolder {
        TextView name;
        TextView wins;
        ImageView imageView;
        TextView id;
        ImageView trashCan;
    }
}