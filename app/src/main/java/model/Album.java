package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Album implements Serializable {

    private String name;
    private List<Photo> photos;

    public Album(String name) {
        this.name = name;
        photos = new ArrayList<Photo>();
    }

    /**
     * This method is to set album's name
     *
     * @param name name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method is to get the album's name
     *
     * @return name of album
     */
    public String getName() {
        return this.name;
    }

    /**
     * This method is to get the list of photos
     *
     * @return the overall list of photos
     */
    public List<Photo> getPhotos(){
        return this.photos;
    }

    /**
     * This method is to add a photo into list
     *
     * @param photo the photo to add
     */
    public void addPhoto(Photo photo) {
        photos.add(photo);
    }

    /**
     * This method is to delete the photo with input index in the list
     *
     * @param index index of photo
     */
    public void deletePhoto(int index) {
        photos.remove(index);
    }

    public boolean checkIfPhotoExist(String path) {
        for(Photo p : photos) {
            if(p.getPath().equals(path)) {
                return true;
            }
        }
        return false;
    }

    public String toString(){
        return this.name;
    }
}
