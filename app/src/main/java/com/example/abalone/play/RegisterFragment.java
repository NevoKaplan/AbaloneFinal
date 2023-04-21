// Import necessary libraries and packages
package com.example.abalone.play;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.abalone.R;
import com.example.abalone.play.Control.Control;
import com.example.abalone.play.Logic.Data.User;
import com.example.abalone.play.Logic.Data.UserTable;

// Define the RegisterFragment class
public class RegisterFragment extends DialogFragment {

    // Declare variables
    private ImageView userImage;
    private final Context context;
    private final UserTable table;
    private View fragment;

    // Constructor for the RegisterFragment class
    public RegisterFragment(Context context) {
        this.context = context;
        table = new UserTable(context);
    }

    // Inflate the view for the fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.custom_dialog, container, false);
        createDialog();
        return fragment;
    }

    // Create the dialog for the fragment
    public void createDialog() {
        // Find the userImage view and set the click listeners for the update and reload buttons
        userImage = fragment.findViewById(R.id.userImage);
        fragment.findViewById(R.id.update).setOnClickListener(this::clickDialog);
        fragment.findViewById(R.id.reload).setOnClickListener(this::clickDialog);
        // Set the text for the textView to "Register"
        ((TextView) fragment.findViewById(R.id.textView)).setText("Register");
        // Set the click listener for the userImage view
        userImage.setOnClickListener(view ->
        {
            // Start the forUserSelect activity
            Intent intent = new Intent(context, forUserSelect.class);
            context.startActivity(intent);
        });
    }

    // Handle click events for the dialog
    public void clickDialog(View view) {
        // Check if a bitmap has been set
        boolean setBitmap = setBitmap();
        // Check which button was clicked
        if (view.getId() == R.id.update) {
            // Check if the first name and email fields have been filled in
            if (!((TextView) fragment.findViewById(R.id.FirstNameTextBox)).getText().toString().matches("") && !((TextView) fragment.findViewById(R.id.EmailTextBox)).getText().toString().matches("")) {
                // Get the values from the first name, surname, and email fields
                String name = ((TextView) fragment.findViewById(R.id.FirstNameTextBox)).getText().toString();
                String surname = ((TextView) fragment.findViewById(R.id.SurnameTextBox)).getText().toString();
                String email = ((TextView) fragment.findViewById(R.id.EmailTextBox)).getText().toString();
                ImageView img = fragment.findViewById(R.id.userImage);
                Bitmap bitmap;
                // Check if a bitmap has been set
                if (!setBitmap)
                    bitmap = ((BitmapDrawable)img.getDrawable()).getBitmap();
                else
                    bitmap = Control.getCurrentBitmap();
                // Create a new User object and add it to the user table
                User user = new User(name, surname, email, bitmap);
                table.createUser(user);
                // Display a toast message indicating that the user's details have been saved
                Toast.makeText(context, name + "'s details saved", Toast.LENGTH_SHORT).show();
                // Close the dialog
                dismiss();
            } else {
                // Display a toast message indicating that the user must fill in the first name and email fields
                Toast.makeText(context, "Must fill NAME & E-MAIL fields", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Set the bitmap for the userImage view
    private boolean setBitmap() {
        Bitmap bitmap = Control.getCurrentBitmap();
        if (bitmap != null) {
            userImage.setImageDrawable(null);
            userImage.setImageBitmap(Control.getCurrentBitmap());
            return true;
        }
        return false;
    }

}
