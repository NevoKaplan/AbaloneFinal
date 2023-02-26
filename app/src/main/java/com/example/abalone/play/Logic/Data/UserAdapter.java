package com.example.abalone.play.Logic.Data;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abalone.R;

import java.util.List;

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
        holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        holder.imageView.setImageBitmap(temp.getImg());
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

    static class ViewHolder {
        TextView name;
        TextView email;
        ImageView imageView;
        TextView id;
        ImageView trashCan;
    }
}