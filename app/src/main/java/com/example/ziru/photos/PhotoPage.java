package com.example.ziru.photos;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.Album;
import model.Photo;
import model.User;

public class PhotoPage extends AppCompatActivity {

    private User user = new User();
    private Photo photo;
    private Album album;
    private int albumIndex, photoIndex;
    private  ArrayAdapter<String> adapter;
    private Button backward;
    private Button forward;
    private Button addTag;
    private ImageView imageView;
    private Spinner tagSpinner;
    private EditText tagValue;
    private ListView tagList;
    private TextView filename;
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
        setContentView(R.layout.activity_photo);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        backward = (Button) findViewById(R.id.backward);
        forward = (Button) findViewById(R.id.forward);
        addTag = (Button) findViewById(R.id.addTag);
        imageView = (ImageView) findViewById(R.id.imageView);
        tagSpinner = (Spinner) findViewById(R.id.tagSpinner);
        tagValue = (EditText) findViewById(R.id.tagValue);
        tagList = (ListView) findViewById(R.id.tagList);
        filename = (TextView)findViewById(R.id.filename);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            albumIndex = bundle.getInt("albumIndex");
            photoIndex = bundle.getInt("photoIndex");
        }
        album = user.getAlbums().get(albumIndex);
        photo = album.getPhotos().get(photoIndex);
        imageView.setImageURI(Uri.parse(photo.getPath()));
        filename.setText(photo.getName());

        ArrayAdapter<CharSequence> spinner_adapter = ArrayAdapter.createFromResource(this,R.array.tagType, android.R.layout.simple_spinner_dropdown_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tagSpinner.setAdapter(spinner_adapter);
        tagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        final List<String> list_to_show = new ArrayList<String>();
        list_to_show.addAll(photo.getlocationTags());
        list_to_show.addAll(photo.getpersonTags());
        adapter = new ArrayAdapter<String>(this, R.layout.tag_detail, list_to_show);
        tagList.setAdapter(adapter);

        tagList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder confirmDialog = new AlertDialog.Builder(PhotoPage.this);
                confirmDialog
                        .setTitle("Delete Tag")
                        .setMessage("Are you sure you want to delete this tag?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String tag = list_to_show.get(position);
                                String[] tokens = tag.split("=");
                                if(tokens[0].equals("location "))
                                {
                                    photo.getlocationTags().remove(tag);
                                }
                                else
                                {
                                    photo.getpersonTags().remove(tag);
                                }
                                list_to_show.remove(tag);
                                adapter.notifyDataSetChanged();
                                try {
                                    User.write(user);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();

                return true;
            }
        });
        addTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tagValue.getText().toString().equals(""))
                {
                    AlertDialog confirmDialog = new AlertDialog.Builder(PhotoPage.this).create();
                    confirmDialog.setTitle("Warning!");
                    confirmDialog.setMessage("You have to enter a tag value");
                    confirmDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    confirmDialog.show();
                    return;
                }
                String newTag = tagSpinner.getSelectedItem().toString()+" = "+tagValue.getText().toString();
                if(tagSpinner.getSelectedItem().toString().equals("location"))
                {
                    if(checkTag(photo, newTag))
                    {
                        AlertDialog confirmDialog = new AlertDialog.Builder(PhotoPage.this).create();
                        confirmDialog.setTitle("Warning!");
                        confirmDialog.setMessage("Same tag already exists!");
                        confirmDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        confirmDialog.show();
                        return;
                    }
                    else
                    {
                        photo.addLocationTag(newTag);
                    }

                }
                else
                {
                    if(checkTag(photo, newTag))
                    {
                        AlertDialog confirmDialog = new AlertDialog.Builder(PhotoPage.this).create();
                        confirmDialog.setTitle("Warning!");
                        confirmDialog.setMessage("Same tag already exists!");
                        confirmDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        confirmDialog.show();
                        return;
                    }
                    else
                    {
                        photo.addPersonTag(newTag);
                    }
                }
                list_to_show.add(newTag);
                tagValue.setText("");
                adapter.notifyDataSetChanged();
                try {
                    User.write(user);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(photoIndex < album.getPhotos().size()-1) photoIndex++;
                else photoIndex = 0;
                photo = album.getPhotos().get(photoIndex);
                imageView.setImageURI(Uri.parse(photo.getPath()));
                filename.setText(photo.getName());
                list_to_show.clear();
                list_to_show.addAll(photo.getpersonTags());
                list_to_show.addAll(photo.getlocationTags());
                adapter.notifyDataSetChanged();
                tagValue.setText("");

            }
        });
        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(photoIndex>0) photoIndex--;
                else photoIndex = album.getPhotos().size()-1;
                photo = album.getPhotos().get(photoIndex);
                imageView.setImageURI(Uri.parse(photo.getPath()));
                filename.setText(photo.getName());
                list_to_show.clear();
                list_to_show.addAll(photo.getpersonTags());
                list_to_show.addAll(photo.getlocationTags());
                adapter.notifyDataSetChanged();
                tagValue.setText("");

            }
        });

    }

    public boolean checkTag(Photo photo, String tag)
    {
        for(String userTag:photo.getlocationTags())
        {
            if(userTag.toLowerCase().trim().equals(tag.toLowerCase().trim())) return true;
            else continue;
        }
        for(String userTag: photo.getpersonTags())
        {
            if(userTag.toLowerCase().trim().equals(tag.toLowerCase().trim())) return true;
            else continue;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Bundle bundle = new Bundle();
                bundle.putInt("albumIndex", albumIndex);
                Intent h = new Intent(PhotoPage.this, AlbumPage.class);
                h.putExtras(bundle);
                startActivity(h);
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }
}
