package me.iwf.photopicker.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;

import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.R;
import me.iwf.photopicker.entity.Photo;
import me.iwf.photopicker.entity.PhotoDirectory;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_ID;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.DATE_ADDED;
import static android.provider.MediaStore.MediaColumns.SIZE;

/**
 * Created by donglua on 15/5/31.
 */
public class MediaStoreHelper {

  public final static int INDEX_ALL_PHOTOS = 0;


  public static void getPhotoDirs( androidx.fragment.app.FragmentActivity activity, Bundle args, PhotosResultCallback resultCallback) {
    activity.getSupportLoaderManager()
        .initLoader(0, args, new PhotoDirLoaderCallbacks(activity, resultCallback));
  }

  private static class PhotoDirLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

    private Context context;
    private PhotosResultCallback resultCallback;
      List<PhotoDirectory> directories = new ArrayList<>();
      PhotoDirectory photoDirectoryAll = new PhotoDirectory();


    public PhotoDirLoaderCallbacks(Context context, PhotosResultCallback resultCallback) {
      this.context = context;
      this.resultCallback = resultCallback;
        photoDirectoryAll.setName(context.getString(R.string.__picker_all_image));
        photoDirectoryAll.setId("ALL");
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
      return new PhotoDirectoryLoader(context, args.getBoolean(PhotoPicker.EXTRA_SHOW_GIF, false));
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

      if (data == null)  return;


      data.moveToFirst();

      while (data.moveToNext()) {

        int imageId  = data.getInt(data.getColumnIndexOrThrow(_ID));
        String bucketId = data.getString(data.getColumnIndexOrThrow(BUCKET_ID));
        String name = data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME));
        String path = data.getString(data.getColumnIndexOrThrow(DATA));
        long size = data.getInt(data.getColumnIndexOrThrow(SIZE));

        if (size < 1) continue;

        PhotoDirectory photoDirectory = new PhotoDirectory();
        photoDirectory.setId(bucketId);
        photoDirectory.setName(name);

        if (!directories.contains(photoDirectory)) {
          photoDirectory.setCoverPath(path);
          photoDirectory.addPhoto(imageId, path);
          photoDirectory.setDateAdded(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED)));
          directories.add(photoDirectory);
        } else {
          directories.get(directories.indexOf(photoDirectory)).addPhoto(imageId, path);
        }

          LogUtil.d(TAG, "文件修改时间：" + FileUtils.formatYearSecond(new Date(new File(path).lastModified())));
        photoDirectoryAll.addPhoto(imageId, path);
      }
      if (photoDirectoryAll.getPhotoPaths().size() > 0) {
        photoDirectoryAll.setCoverPath(photoDirectoryAll.getPhotoPaths().get(0));
      }
      directories.add(INDEX_ALL_PHOTOS, photoDirectoryAll);

        addExternalPicture(directories);
      if (resultCallback != null) {
        resultCallback.onResultCallback(directories);
      }
    }

      private static final String TAG = "PhotoDirLoaderCallbacks";

      /**
       * 获取外部存储 /Picture 文件夹下的图片。</br></>
       * 首先获取到 /Picture 文件夹下的所有图片，然后再找到从数据库中查找出的 所有的文件夹。
       * 如果已经包含当前文件夹，进行对比，；
       * 如果不包含当前文件夹，则把当前文件夹添加进所有文件夹；
       *
       * @param allDir
       */
      private void addExternalPicture(List<PhotoDirectory> allDir) {
          File externalPictureFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
          List<File> files = FileUtils.listImageInDir(externalPictureFile, false);

          if (files.isEmpty()) {
              return;
          }
          int count = 0;
          for (PhotoDirectory photoDirectory : allDir) {
              if ("Pictures".equals(photoDirectory.getName())) {
                  LogUtil.d(TAG, "数据库中存在当前文件夹: 图片文件大小为：" + photoDirectory.getPhotoPaths().size() + " 遍历文件夹获取到：" + files.size());
                  compareFilePath(photoDirectory, files);
              } else {
                  count += 1;
              }
          }
          if (count == allDir.size()) {
              addPictureFolder(files);
          }

      }

      private void compareFilePath(PhotoDirectory photoDirectory, List<File> files) {
          List<String> photoPaths = photoDirectory.getPhotoPaths();
          for (File file : files) {
              if (photoPaths.contains(file.getPath())) {
                  continue;
              }
              LogUtil.d(TAG, file.getPath() + " 未添加到数据库中");

              insertOrderByModifiedTime(photoDirectory, file);
              insertOrderByModifiedTime(photoDirectoryAll, file);
          }
      }

      /**
       * 根据文件的最后修改时间插入到已有的文件列表中
       *
       * @param photoDirectory 已有的文件列表
       * @param file           待插入列表
       */
      private void insertOrderByModifiedTime(PhotoDirectory photoDirectory, File file) {
          if (photoDirectory.getPhotos().isEmpty()) {
              photoDirectory.addPhoto(file.hashCode(), file.getPath());
              return;
          }
          int length = photoDirectory.getPhotos().size();
          int insertPosition = recursionBinarySearch(photoDirectory.getPhotos(), file.lastModified(), 0, length - 1);
          if (insertPosition > 0) {
              LogUtil.d(TAG, "前一个为：" + FileUtils.formatYearSecond(new Date(photoDirectory.getPhotos().get(insertPosition - 1).modifiedTime())));
              LogUtil.d(TAG, "后一个为：" + FileUtils.formatYearSecond(new Date(photoDirectory.getPhotos().get(insertPosition + 1).modifiedTime())));
          }
          if (insertPosition == 0) {
              LogUtil.d(TAG, "后一个为：" + FileUtils.formatYearSecond(new Date(photoDirectory.getPhotos().get(insertPosition + 1).modifiedTime())));
          }
          if (insertPosition < 0) {
              insertPosition = 0;
          }
          photoDirectory.addPhoto(insertPosition, file.hashCode(), file.getPath());
      }

      /**
       * 利用二分查找 找到适当的文件插入下标
       *
       * @param list         列表
       * @param modifiedTime 目标文件的修改日期
       * @param low          low
       * @param high         high
       * @return 下标
       */
      private int recursionBinarySearch(List<Photo> list, long modifiedTime, int low, int high) {
          if (low > high) {
              LogUtil.e(TAG, "这种情况是什么情况？");
              return Math.min(low, high);
          }
          // 待查找文件比 开始查找的文件修改日期 大
          if (modifiedTime > list.get(low).modifiedTime()) {
              return low - 1 >= 0 ? low - 1 : 0;
          }
          // 待查找的文件修改日期比 结束为止的文件修改日期 还小
          if (modifiedTime < list.get(high).modifiedTime()) {
              return high + 1;
          }

          int middle = (low + high) / 2;
          if (list.get(middle).modifiedTime() > modifiedTime) {
              //比关键字大则关键字在左区域
              return recursionBinarySearch(list, modifiedTime, low, middle - 1);
          } else if (list.get(middle).modifiedTime() < modifiedTime) {
              //比关键字小则关键字在右区域
              return recursionBinarySearch(list, modifiedTime, middle + 1, high);
          } else {
              return middle;
          }
      }

      /**
       * 添加一个文件夹内的所有图片到 图片文件夹 集合中
       *
       * @param files 目录中的所有图片
       */
      private void addPictureFolder(List<File> files) {
          PhotoDirectory photoDirectoryPicture = new PhotoDirectory();
          photoDirectoryPicture.setName("Pictures");
          photoDirectoryPicture.setId("Pictures");
          photoDirectoryPicture.setCoverPath(files.get(0).getPath());
          for (File file : files) {
              photoDirectoryPicture.addPhoto(file.hashCode(), file.getPath());
          }
      }

      @Override
      public void onLoaderReset(Loader<Cursor> loader) {

    }
  }


  public interface PhotosResultCallback {
    void onResultCallback(List<PhotoDirectory> directories);
  }

}
