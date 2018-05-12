package com.example.ziru.photos;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.Album;
import model.User;

public class HomeScreen extends AppCompatActivity {

    private FloatingActionButton addAlbum;
    private Button search;
    private Button rename;
    private Button deleteAlbum;
    private ListView list;

    private int index;
    private User user = new User();
    private List<Album> albums = new ArrayList<Album>();
    private ArrayAdapter<Album> arrayAdapter;
    File file = new File("data/data/com.example.ziru.photos/user.dat");

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
        setContentView(R.layout.activity_home_screen);

        if(!file.exists()) {

            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        albums = user.getAlbums();

        list = (ListView) findViewById(R.id.albums_list);
        arrayAdapter = new ArrayAdapter<Album>(this, R.layout.activity_album_in_list, R.id.checkableTextView, albums);
        list.setAdapter(arrayAdapter);

        addAlbum = (FloatingActionButton) findViewById(R.id.addAlbum);
        search = (Button) findViewById(R.id.search);
        rename = (Button) findViewById(R.id.rename);
        deleteAlbum = (Button) findViewById(R.id.deleteAlbum);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView ctv = (CheckedTextView) view.findViewById(R.id.checkableTextView);
                if(!ctv.isChecked()){
                    ctv.setChecked(true);
                } else{
                    ctv.setChecked(false);
                }
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder confirmDialog = new AlertDialog.Builder(HomeScreen.this);
                confirmDialog
                        .setTitle("Open Album")
                        .setMessage("Are you sure you want to open album - " + user.getAlbums().get(position).getName() + "?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Bundle bundle = new Bundle();
                                bundle.putInt("albumIndex", position);

                                Intent intent = new Intent(HomeScreen.this, AlbumPage.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();

                return true;
            }
        });

        addAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, CreateAlbum.class);
                startActivity(intent);
            }
        });

        rename.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                if (list.getCheckedItemCount() != 1){
                    AlertDialog alertDialog = new AlertDialog.Builder(HomeScreen.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Please select one album to rename. Try again!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    return;
                }

                SparseBooleanArray checkedItemPositions = list.getCheckedItemPositions();
                int itemCount = list.getCount();
                for(int i=itemCount-1; i >= 0; i--){
                    if(checkedItemPositions.get(i)){
                        index = i;
                    }
                }

                final AlertDialog.Builder userInputDialog = new AlertDialog.Builder(HomeScreen.this);
                userInputDialog.setTitle("Please enter a new name for the album.");
                final EditText input = new EditText(HomeScreen.this);
                userInputDialog.setView(input);
                userInputDialog
                        .setCancelable(false)
                        .setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                String newName = input.getText().toString();

                                if (newName == null || newName.length() == 0) {
                                    AlertDialog alertDialog = new AlertDialog.Builder(HomeScreen.this).create();
                                    alertDialog.setTitle("Alert");
                                    alertDialog.setMessage("Empty blank. Please try again!");
                                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    alertDialog.show();
                                    return;
                                }

                                if(user.checkIfAlbumExist(newName)){
                                    AlertDialog alertDialog = new AlertDialog.Builder(HomeScreen.this).create();
                                    alertDialog.setTitle("Alert");
                                    alertDialog.setMessage("Cannot have duplicate names of albums. Please try again!");
                                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    alertDialog.show();
                                    return;
                                }

                                user.getAlbums().get(index).setName(newName);
                                albums = user.getAlbums();
                                arrayAdapter.notifyDataSetChanged();
                                list.setAdapter(arrayAdapter);

                                list.clearChoices();

                                try {
                                    User.write(user);
                                    } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                Toast.makeText(HomeScreen.this, "Album Successfully Renamed", Toast.LENGTH_SHORT).show();
                            }
                        })

                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });

                AlertDialog alertDialogAndroid = userInputDialog.create();
                alertDialogAndroid.show();
            }
        });

        deleteAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (list.getCheckedItemCount() == 0){
                    AlertDialog alertDialog = new AlertDialog.Builder(HomeScreen.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Please select at least one album to delete. Try again!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    return;
                }

                final AlertDialog.Builder confirmDialog = new AlertDialog.Builder(HomeScreen.this);
                confirmDialog
                        .setTitle("Delete Album(s)")
                        .setMessage("Are you sure you want to delete selected albums?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SparseBooleanArray checkedItemPositions = list.getCheckedItemPositions();
                                int itemCount = list.getCount();

                                for(int i=itemCount-1; i >= 0; i--){
                                    if(checkedItemPositions.get(i)){
                                        user.deleteAlbum(i);
                                    }
                                }

                                albums = user.getAlbums();
                                arrayAdapter.notifyDataSetChanged();
                                list.setAdapter(arrayAdapter);

                                list.clearChoices();

                                try {
                                    User.write(user);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                Toast.makeText(HomeScreen.this, "Album(s) Successfully Deleted", Toast.LENGTH_SHORT).show();
                            }

                        })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        return;
                                    }
                                })
                        .show();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, SearchPage.class);
                startActivity(intent);
            }
        });
    }
}
