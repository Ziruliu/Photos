package com.example.ziru.photos;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.Album;
import model.Photo;
import model.PhotoAdapter;
import model.User;

public class AlbumPage extends AppCompatActivity {

    private Spinner spinner;
    private Button move;
    private Button deletePhoto;
    private FloatingActionButton addPhoto;
    private ListView photosList;
    private TextView albumName;

    private boolean duplicateCaptured = false;
    private int albumIndex;
    private User user = new User();
    private List<Photo> photos = new ArrayList<Photo>();
    private List<String> spinnerList = new ArrayList<String>();
    private PhotoAdapter adapter;
    private ArrayAdapter<String> spinnerAdapter;
    private Album album;
    private static final int READ_REQUEST_CODE = 49;

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
        setContentView(R.layout.activity_album);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            albumIndex = bundle.getInt("albumIndex");
        }

        spinner = findViewById(R.id.spinner);
        move = findViewById(R.id.move);
        deletePhoto = findViewById(R.id.deletePhoto);
        addPhoto = findViewById(R.id.addPhoto);
        photosList = findViewById(R.id.photos_list);
        albumName = findViewById(R.id.albumName);

        album = user.getAlbums().get(albumIndex);
        albumName.setText("Name of Album: " + album.getName());

        photos = album.getPhotos();
        adapter = new PhotoAdapter(this, R.layout.activity_photo_in_list, photos);
        photosList.setAdapter(adapter);

        spinnerList.add("Select album...");
        for(Album a : user.getAlbums()){
            if(a == album){
                continue;
            } else {
                spinnerList.add(a.getName());
            }
        }


        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerList);
        spinner.setAdapter(spinnerAdapter);

        photosList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView ctv = (CheckedTextView) view.findViewById(R.id.photoName);
                if(!ctv.isChecked()){
                    ctv.setChecked(true);
                } else{
                    ctv.setChecked(false);
                }
            }
        });

        photosList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder confirmDialog = new AlertDialog.Builder(AlbumPage.this);
                confirmDialog
                        .setTitle("View Photo")
                        .setMessage("Are you sure you want to view photo - " + album.getPhotos().get(position).getName() + "?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Bundle bundle = new Bundle();
                                bundle.putInt("albumIndex", albumIndex);
                                bundle.putInt("photoIndex", position);

                                Intent intent = new Intent(AlbumPage.this, PhotoPage.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();

                return true;
            }
        });

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, READ_REQUEST_CODE);
            }
        });

        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name = spinner.getSelectedItem().toString();

                if (photosList.getCheckedItemCount() == 0){
                    AlertDialog alertDialog = new AlertDialog.Builder(AlbumPage.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Please select at least one photo to move. Try again!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    return;
                }

                if (name.equals("Select album...")){
                    AlertDialog alertDialog = new AlertDialog.Builder(AlbumPage.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Please select an album to move. Try again!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    return;
                }

                final AlertDialog.Builder confirmDialog = new AlertDialog.Builder(AlbumPage.this);
                confirmDialog
                        .setTitle("Move Photo(s)")
                        .setMessage("Are you sure you want to move selected photos to Album - " + name + "?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SparseBooleanArray checkedItemPositions = photosList.getCheckedItemPositions();
                                int itemCount = photosList.getCount();

                                for(int i=itemCount-1; i >= 0; i--){
                                    if(checkedItemPositions.get(i)){
                                        if(user.getAlbumByName(name).checkIfPhotoExist(album.getPhotos().get(i).getPath())){
                                            duplicateCaptured = true;
                                        }
                                        else{
                                            user.getAlbumByName(name).addPhoto(album.getPhotos().get(i));
                                            user.getAlbums().get(albumIndex).deletePhoto(i);
                                            duplicateCaptured = false;
                                        }
                                    }
                                }

                                album = user.getAlbums().get(albumIndex);
                                adapter.notifyDataSetChanged();
                                photosList.setAdapter(adapter);

                                photosList.clearChoices();

                                try {
                                    User.write(user);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                if(duplicateCaptured == true){
                                    Toast.makeText(AlbumPage.this, "Duplicate Photo(s) Not Successfully Moved", Toast.LENGTH_LONG).show();

                                }else{
                                    Toast.makeText(AlbumPage.this, "Photo(s) Successfully Moved", Toast.LENGTH_SHORT).show();
                                }
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

        deletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (photosList.getCheckedItemCount() == 0){
                    AlertDialog alertDialog = new AlertDialog.Builder(AlbumPage.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Please select at least one photo to delete. Try again!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    return;
                }

                final AlertDialog.Builder confirmDialog = new AlertDialog.Builder(AlbumPage.this);
                confirmDialog
                        .setTitle("Delete Photo(s)")
                        .setMessage("Are you sure you want to delete selected photos?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SparseBooleanArray checkedItemPositions = photosList.getCheckedItemPositions();
                                int itemCount = photosList.getCount();

                                for(int i=itemCount-1; i >= 0; i--){
                                    if(checkedItemPositions.get(i)){
                                        user.getAlbums().get(albumIndex).deletePhoto(i);
                                    }
                                }

                                album = user.getAlbums().get(albumIndex);
                                adapter.notifyDataSetChanged();
                                photosList.setAdapter(adapter);

                                photosList.clearChoices();

                                try {
                                    User.write(user);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                Toast.makeText(AlbumPage.this, "Photo(s) Successfully Deleted", Toast.LENGTH_SHORT).show();
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

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                String path = uri.getPath();
                String uriString = uri.toString();

                if (album.checkIfPhotoExist(uriString)) {
                    AlertDialog alertDialog = new AlertDialog.Builder(AlbumPage.this).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("This photo has already existed within the album. Please try again!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    return;
                }

                Photo newPhoto = new Photo(uriString);
                newPhoto.setName(path.substring(path.lastIndexOf(File.separator)+1));
                user.getAlbums().get(albumIndex).addPhoto(newPhoto);
                album = user.getAlbums().get(albumIndex);

                //serialize and refresh list
                try {
                    User.write(user);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                photos = album.getPhotos();
                adapter.notifyDataSetChanged();
                photosList.setAdapter(adapter);

                Toast.makeText(AlbumPage.this, "Photo Successfully Added", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
