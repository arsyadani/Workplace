package com.example.arsyadani.test;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * Created by Arsyadani on 19-Apr-18.
 */

public class ImageLoaderTask extends AsyncTask<Object, Void, Bitmap> {
    private String assetName;
    private WeakReference<VrPanoramaView> viewReference;
    private VrPanoramaView.Options viewOptions;

    public ImageLoaderTask(VrPanoramaView view, VrPanoramaView.Options viewOptions, String assetName) {
        viewReference = new WeakReference<>(view);
        this.viewOptions = viewOptions;
        this.assetName = assetName;
    }

    @Override
    protected Bitmap doInBackground(Object... params) {
        Bitmap bitmap = null;

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + assetName);

        if(file.exists()){
            System.out.println("File exist : " + file.getName());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;

            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        }

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        final VrPanoramaView vw = viewReference.get();

        if(bitmap != null && vw != null){
            vw.loadImageFromBitmap(bitmap, viewOptions);
        }
    }
}
