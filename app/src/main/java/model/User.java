package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

    private static final long serialVersionUID = 49L;

    private List<Album> albums;

    public User() {
        albums = new ArrayList<Album>();
    }

    /**
     * This method is to add an album to the list
     *
     * @param a album to add
     */
    public void addAlbum(Album a) {
        albums.add(a);
    }

    /**
     * This method is to delete the album with the given index in the list
     *
     * @param index index to delete
     */
    public void deleteAlbum(int index) {
        albums.remove(index);
    }

    /**
     * This method is to get overall list of albums
     *
     * @return the overall list of albums
     */
    public List<Album> getAlbums() {
        return this.albums;
    }

    /**
     * This method check if the album has already existed in the album
     *
     * @param albumName name of the album
     * @return true indicates it exists, and false if it does not
     */
    public boolean checkIfAlbumExist(String albumName) {
        for(Album a : albums) {
            if(a.getName().toLowerCase().equals(albumName.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public Album getAlbumByName(String albumName){
        for(Album a : albums) {
            if(a.getName().equals(albumName)) {
                return a;
            }
        }
        return null;
    }

    /**
     * Read the user.dat file and return the User model containing the list of all users.
     * @return	return the User model of all users
     * @throws IOException		Exception for serialization
     * @throws ClassNotFoundException		Exception for serialization
     */
    public static User read() throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data/data/com.example.ziru.photos/user.dat"));
        User user = (User) ois.readObject();
        ois.close();
        return user;
    }

    /**
     * Given the User model, write this data into user.dat, overwriting anything on there.
     * @param user	The User model to write with
     * @throws IOException		Exception for serialization
     */
    public static void write (User user) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data/data/com.example.ziru.photos/user.dat"));
        oos.writeObject(user);
        oos.close();
    }
}
