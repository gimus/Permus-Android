package com.gimus.permus.utility;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.net.Uri;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Utility {

    public static Bitmap getBitmapByName(Context C, String name) {
        int resID = C.getResources().getIdentifier(name, "drawable", C.getPackageName());
        return BitmapFactory.decodeResource(C.getResources(), resID);
    }

    public static void HideSoftKeyboard(Activity activity) {
        InputMethodManager imm = ((InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE));
        View o = activity.getWindow().getCurrentFocus();
        if (o != null)
            imm.hideSoftInputFromWindow(o.getWindowToken(), 0);

    }

    public static Bitmap getBitmapFromBase64String( String s){
        try{
            byte[] decodedString = Base64.decode(s, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }
        catch( Exception e) {
            return null;
        }
    }

    public static Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = null;
            try {
                url = new URL(imageUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap loadBitmap(Context context, Uri uri) {
        Bitmap bm = null;
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            bm = BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bm;
    }

    public static Bitmap clipInCircle(Bitmap bm, float r) {
        if (r == 0) r = bm.getWidth() / 2;

        Bitmap bc = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bc);
        Paint p = new Paint();
        p.setAlpha(255); //0x80
        Path mPath = new Path();
        mPath.addCircle(bm.getWidth() / 2, bm.getHeight() / 2, r, Path.Direction.CCW);

        //canvas.clipPath(mPath, Region.Op.UNION);
        //canvas.clipPath(mPath, Region.Op.DIFFERENCE);
        //canvas.clipPath(mPath, Region.Op.REPLACE);
        //canvas.clipPath(mPath, Region.Op.XOR);
        canvas.clipPath(mPath, Region.Op.INTERSECT);
        canvas.drawBitmap(bm, 0, 0, p);

        return bc;
    }

    private static File getAppDir(Context C, String dir) {
        ContextWrapper cw = new ContextWrapper(C);
        return cw.getDir(dir, Context.MODE_PRIVATE);
    }

    private static File getAppFile(Context C, String dir, String fileName) {
        return new File(getAppDir(C, dir), fileName);
    }

    private static String getAppFileAbsolutePath(Context C, String dir, String fileName) {
        return new File(getAppDir(C, dir), fileName).getAbsolutePath();
    }

    public static boolean fileExists(Context C, String dir, String fileName) {
        File f = getAppFile(C, dir, fileName);
        return f.exists();
    }

    public static String saveToInternalStorage(Context C, String dir, String fileNameNoExtension, Bitmap bitmapImage) {

        File f = getAppFile(C, dir, fileNameNoExtension + ".png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return f.getAbsolutePath();
    }

    public static Bitmap ReadBitmapFromInternalStorage(Context C, String dir, String fileNameNoExtension) {

        try {
            File f = getAppFile(C, dir, fileNameNoExtension + ".png");
            return BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getRawFileText(Context C, int resourceId) {
        InputStream in = C.getResources().openRawResource(resourceId);
        StringBuilder builder = new StringBuilder();
        try {
            int count = 0;
            byte[] bytes = new byte[32768];

            while ((count = in.read(bytes, 0, 32768)) > 0) {
                builder.append(new String(bytes, 0, count));
            }

            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public static Document getXmlDocument(String xml) {
        InputStream stream = null;
        SAXBuilder builder = new SAXBuilder();
        Document document = null;

        try {
            stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            try {
                document = (Document) builder.build(stream);
            } catch (JDOMException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return document;
    }
}
