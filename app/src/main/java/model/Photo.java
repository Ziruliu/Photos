package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Photo implements Serializable  {

    private String path;
    private String name;
    private List<String> personTags;
    private List<String> locationTags;

    public Photo(String path) {
        this.path = path;
        this.name = "";
        personTags = new ArrayList<String>();
        locationTags = new ArrayList<String>();
    }

    /**
     * This method is set the name of photo
     *
     * @param name name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method is to get the name of photo
     *
     * @return name of photo
     */
    public String getName() {
        return this.name;
    }

    /**
     * This method is to set the path of photo
     *
     * @param path path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * This method is to get the path of photo
     *
     * @return path of photo
     */
    public String getPath() {
        return this.path;
    }

    public List<String> getpersonTags() {
        return personTags;
    }

    public List<String> getlocationTags() {
        return locationTags;
    }

    public void addPersonTag(String personTag){
        this.personTags.add(personTag.toLowerCase());
    }

    public void removePersonTag(String personTag){
        this.personTags.remove(personTag.toLowerCase());
    }

    public void addLocationTag(String locationTag){
        this.locationTags.add(locationTag.toLowerCase());
    }

    public void removeLocationTag(String locationTag){
        this.locationTags.remove(locationTag.toLowerCase());
    }
}
