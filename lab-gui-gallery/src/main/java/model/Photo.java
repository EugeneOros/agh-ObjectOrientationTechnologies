package model;

import java.io.ByteArrayInputStream;
import java.util.UUID;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

import static java.util.UUID.randomUUID;


public class Photo {

    private StringProperty name;

    private final ObjectProperty<Image> photoData;

    public Photo(String extension, byte[] photoData) {
        this.photoData = new SimpleObjectProperty<Image>(new Image(new ByteArrayInputStream(photoData)));
        this.name = new SimpleStringProperty(randomUUID().toString() + "." + extension);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public Image getPhotoData() {
        return photoData.get();
    }

    public StringProperty nameProperty(){
        return name;
    }

    public  ObjectProperty<Image> photoDataProperty(){
        return photoData;
    }

}
