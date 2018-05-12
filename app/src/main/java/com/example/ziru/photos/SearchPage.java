package com.example.ziru.photos;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.Album;
import model.Photo;
import model.PhotoAdapter;
import model.User;

public class SearchPage extends AppCompatActivity {

    private User user = new User();
    private List<Photo> photos = new ArrayList<Photo>();

    private EditText locationTag;
    private EditText personTag;
    private ListView photo_results;
    private Button confirm;
    private Spinner and_or_spinner;

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
        setContentView(R.layout.activity_search);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        locationTag = (EditText) findViewById(R.id.locationTag);
        personTag = (EditText) findViewById(R.id.personTag);
        photo_results = (ListView) findViewById(R.id.photo_results);
        confirm = (Button) findViewById(R.id.confirm);
        and_or_spinner = (Spinner) findViewById(R.id.and_or_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.and_or, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        and_or_spinner.setAdapter(adapter);
        and_or_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(locationTag.getText().toString().equals("")&& personTag.getText().toString().equals(""))
                {
                    AlertDialog confirmDialog = new AlertDialog.Builder(SearchPage.this).create();
                    confirmDialog.setTitle("Warning!");
                    confirmDialog.setMessage("You have to input one of location tag and person tag.");
                    confirmDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    confirmDialog.show();
                }
                else
                {
                    if(and_or_spinner.getSelectedItem().toString().equals("and"))
                        photos = andSearch(locationTag.getText().toString().toLowerCase().trim(), personTag.getText().toString().toLowerCase().trim());
                    else photos = orSearch(locationTag.getText().toString().toLowerCase().trim(), personTag.getText().toString().toLowerCase().trim());
                    PhotoAdapter adapter = new PhotoAdapter(SearchPage.this, R.layout.activity_photo_in_list, photos);
                    photo_results.setAdapter(adapter);
                    personTag.setText("");
                    locationTag.setText("");
                }
            }
        });
    }
    private ArrayList<Photo> orSearch(String location, String person)
    {
        ArrayList<Photo> result = new ArrayList<Photo>();
        for (Album userAlbum : user.getAlbums())
        {
            for(Photo userPhoto: userAlbum.getPhotos())
            {
                for(String location_tag: userPhoto.getlocationTags()) {
                    if(location.equals("")) break;
                    String[] tokens = location_tag.toLowerCase().trim().split(" = ",2);
                    String[] token_helper = location.trim().split(",");
                    for(String token:token_helper) {
                        if (tokens[1].contains(token.trim())) {
                            boolean exist = false;
                            for (Photo temp : result) {
                                if (userPhoto.getName().trim().equals(temp.getName().trim()))
                                    exist = true;
                                else continue;
                            }
                            if (exist) break;
                            else result.add(userPhoto);
                        } else continue;
                    }
                }
                for(String person_tag: userPhoto.getpersonTags())
                {
                    if(person.equals("")) break;
                    String[] tokens2 = person_tag.toLowerCase().trim().split(" = ",2);
                    String[] token_helper = person.trim().split(",");
                    for(String token:token_helper) {
                        if (tokens2[1].contains(token.trim())) {
                            boolean exist = false;
                            for (Photo temp : result) {
                                if (userPhoto.getName().trim().equals(temp.getName().trim()))
                                    exist = true;
                                else continue;
                            }
                            if (exist) break;
                            else result.add(userPhoto);
                        } else continue;
                    }
                }
            }

        }
        return result;
    }
    private ArrayList<Photo> andSearch(String location, String person) {
        ArrayList<Photo> result = new ArrayList<Photo>();
        for (Album userAlbum : user.getAlbums()) {
            for (Photo userPhoto : userAlbum.getPhotos()) {
                int satisfied = 0, size = 0;
                if (!location.equals("")) {
                    String[] token_helper = location.trim().split(",");
                    size+= token_helper.length;
                    for (String token : token_helper) {
                        for (String location_tag : userPhoto.getlocationTags()) {
                            String[] tokens = location_tag.toLowerCase().trim().split(" = ", 2);
                            if (tokens[1].contains(token.trim())) {
                                satisfied++;
                            }
                        }
                    }
                }
                if (!person.equals("")) {
                    String[] token_helper2 = person.trim().split(",");
                    size+=token_helper2.length;
                    for (String token : token_helper2) {
                        for (String person_tag : userPhoto.getpersonTags()) {
                            String[] tokens2 = person_tag.toLowerCase().trim().split(" = ", 2);
                            if (tokens2[1].contains(token.trim())) {
                                satisfied++;
                            } else continue;
                        }
                    }
                }

                if (satisfied == size) {
                    boolean exist = false;
                    for (Photo temp : result) {
                        if (userPhoto.getName().trim().equals(temp.getName().trim()))
                            exist = true;
                        else continue;
                    }
                    if (exist) break;
                    else result.add(userPhoto);
                }
            }
        }
        return result;
    }
}
