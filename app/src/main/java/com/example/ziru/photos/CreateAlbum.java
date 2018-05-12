package com.example.ziru.photos;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

import model.User;
import model.Album;

public class CreateAlbum extends AppCompatActivity {

    private Button confirm;
    private Button cancel;
    private EditText albumName;

    private User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            user = User.read();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_album);

        confirm = (Button) findViewById(R.id.confirm);
        cancel = (Button) findViewById(R.id.cancel);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                albumName = (EditText) findViewById(R.id.albumName);
                String name = albumName.getText().toString();

                if (name == null || name.length() == 0) {
                    AlertDialog alertDialog = new AlertDialog.Builder(CreateAlbum.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Name of album cannot be empty. Please try again!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    return;
                }

                if(user.checkIfAlbumExist(name)){
                    AlertDialog alertDialog = new AlertDialog.Builder(CreateAlbum.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Cannot have duplicate names of albums. Please try again!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    albumName.getText().clear();
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    return;
                }

                user.addAlbum(new Album(name));
                try {
                    User.write(user);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(CreateAlbum.this, HomeScreen.class);
                startActivity(intent);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}
