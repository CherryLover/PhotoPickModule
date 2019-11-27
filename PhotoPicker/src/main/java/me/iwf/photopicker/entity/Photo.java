package me.iwf.photopicker.entity;

import java.io.File;

/**
 * Created by donglua on 15/6/30.
 */
public class Photo {

  private int id;
  private String path;
    private long modifiedTime = 0;

  public Photo(int id, String path) {
    this.id = id;
    this.path = path;
      modifiedTime = new File(path).lastModified();
  }

  public Photo() {
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Photo)) return false;

    Photo photo = (Photo) o;

    return id == photo.id;
  }

  @Override public int hashCode() {
    return id;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
      modifiedTime = new File(path).lastModified();
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

    public long modifiedTime() {
        return modifiedTime;
    }
}
