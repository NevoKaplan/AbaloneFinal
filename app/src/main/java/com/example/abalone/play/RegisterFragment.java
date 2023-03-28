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

public class RegisterFragment extends DialogFragment {

    private ImageView userImage;
    private final Context context;
    private final UserTable table;
    private View fragment;

    public RegisterFragment(Context context) {
        this.context = context;
        table = new UserTable(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.custom_dialog, container, false);
        createDialog();
        return fragment;
    }

    public void createDialog() {
        userImage = fragment.findViewById(R.id.userImage);
        fragment.findViewById(R.id.update).setOnClickListener(this::clickDialog);
        fragment.findViewById(R.id.reload).setOnClickListener(this::clickDialog);
        ((TextView) fragment.findViewById(R.id.textView)).setText("Register");
        userImage.setOnClickListener(view ->
        {
            Intent intent = new Intent(context, forUserSelect.class);
            context.startActivity(intent);
        });
    }

    public void clickDialog(View view) {
        boolean setBitmap = setBitmap();
        if (view.getId() == R.id.update) {
            if (!((TextView) fragment.findViewById(R.id.FirstNameTextBox)).getText().toString().matches("") && !((TextView) fragment.findViewById(R.id.EmailTextBox)).getText().toString().matches("")) {
                String name = ((TextView) fragment.findViewById(R.id.FirstNameTextBox)).getText().toString();
                String surname = ((TextView) fragment.findViewById(R.id.SurnameTextBox)).getText().toString();
                String email = ((TextView) fragment.findViewById(R.id.EmailTextBox)).getText().toString();
                ImageView img = fragment.findViewById(R.id.userImage);
                Bitmap bitmap;
                if (!setBitmap)
                    bitmap = ((BitmapDrawable)img.getDrawable()).getBitmap();
                else
                    bitmap = Control.getCurrentBitmap();
                User user = new User(name, surname, email, bitmap);
                table.createUser(user);
                Toast.makeText(context, name + "'s details saved", Toast.LENGTH_SHORT).show();
                dismiss();
            } else {
                Toast.makeText(context, "Must fill NAME & E-MAIL fields", Toast.LENGTH_SHORT).show();
            }
        }
    }

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
