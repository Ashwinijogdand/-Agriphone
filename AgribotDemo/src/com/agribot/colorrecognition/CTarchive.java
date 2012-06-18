package com.agribot.colorrecognition;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.agribot.dashboard.R;

public class CTarchive extends Activity {

    private ArchiveHelper mHelper; // TODO: .Close()?
    private SQLiteDatabase mDB;
   
        class ArchiveEntryAdapter extends BaseAdapter{
            private int mGalleryItemBackground;
            private Context mContext;

           
            public ArchiveEntryAdapter(Context c) {
                mContext = c;
               
                mHelper = new ArchiveHelper(c);
                mDB = mHelper.getReadableDatabase();
               
                TypedArray a = mContext.obtainStyledAttributes(R.styleable.GalleryItemStyle);
                mGalleryItemBackground = a.getResourceId(
                        R.styleable.GalleryItemStyle_android_galleryItemBackground, 0);
                a.recycle();
            }

            public int getCount() {
                return (int) DatabaseUtils.queryNumEntries(mDB, "archive");
            }

            public Object getItem(int position) {
                return position;
            }

            public long getItemId(int position) {
                return position;
            }

            private static final String Q_GET = "SELECT id, creation_date, color_name, color_value FROM archive ORDER BY creation_date LIMIT 1 OFFSET ";
           
            public View getView(int position, View convertView, ViewGroup parent) {
                ImageView i = new ImageView(mContext);

                Cursor c = mDB.rawQuery(Q_GET + Integer.toString(position), null);
                c.moveToFirst();
                int id = c.getInt(0);
                String name = c.getString(2);
               
           
                        try {
                                FileInputStream fis = mContext.openFileInput(Integer.toString(id) + ".jpg");
                        i.setImageBitmap(BitmapFactory.decodeFileDescriptor(fis.getFD()));
                        fis.close();
                        } catch (FileNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }
               
               
               
                i.setLayoutParams(new Gallery.LayoutParams( (int) (getWindowManager().getDefaultDisplay().getWidth() / 2 ),
                                android.widget.Gallery.LayoutParams.MATCH_PARENT ));
                        //i.setLayoutParams(new Gallery.LayoutParams(android.widget.Gallery.LayoutParams.WRAP_CONTENT,android.widget.Gallery.LayoutParams.WRAP_CONTENT));

                i.setScaleType(ImageView.ScaleType.FIT_CENTER);
       
                i.setBackgroundResource(mGalleryItemBackground);

               
               
                return i;
            }
           
           
        }

        int selected_rid = -1;
       
        private TextView colorNameTV, rgbTV, timestampTV, htmlTV, hsvTV;
        private Gallery g;
        private SurfaceView colorSurface;
        private ImageButton trashButton;
       
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
           
            setContentView(R.layout.archive);

            this.colorNameTV = (TextView) findViewById(R.id.ar_name);
            this.rgbTV = (TextView) findViewById(R.id.ar_rgb);
            this.timestampTV = (TextView) findViewById(R.id.ar_timestamp);
            this.htmlTV = (TextView) findViewById(R.id.ar_html);
            this.colorSurface = (SurfaceView) findViewById(R.id.ar_color_surface);
            this.trashButton = (ImageButton) findViewById(R.id.ar_trash_button);
            this.hsvTV = (TextView) findViewById(R.id.ar_hsv);
           
            trashButton.setOnClickListener(new OnClickListener() {
                       
                        public void onClick(View v) {
                                if ( selected_rid >= 0 ){
                                        mDB.delete("ARCHIVE", "id=" + selected_rid, null);
                                        ((BaseAdapter)g.getAdapter()).notifyDataSetChanged();
                                }
                               
                        }
                });
           
            g = (Gallery) findViewById(R.id.gallery);
            g.setAdapter(new ArchiveEntryAdapter(this));
           
            g.setOnItemSelectedListener(new OnItemSelectedListener() {

                private static final String Q_GET = "SELECT creation_date, color_name, color_value, id FROM archive ORDER BY creation_date LIMIT 1 OFFSET ";
                         
               
                       public void onItemSelected(AdapterView parent, View v, int position, long id) {

                                        Cursor c = mDB.rawQuery(Q_GET + Integer.toString(position), null);
                                        c.moveToFirst();
                                       
                                        NamedColor color = new NamedColor( c.getInt(2), null );
                                       
                                        colorNameTV.setText(c.getString(1));
                                        rgbTV.setText(color.asRGB());
                                        htmlTV.setText(color.asHTML());
                                        timestampTV.setText(c.getString(0));
                                        colorSurface.setBackgroundColor(color.getInteger());
                                        selected_rid = c.getInt(3);
                                        hsvTV.setText(color.asHSV());
                        }

                        
                        public void onNothingSelected(AdapterView parent) {
                        colorNameTV.setText("");
                        rgbTV.setText("");
                        htmlTV.setText("");
                        timestampTV.setText("");
                        colorSurface.setBackgroundColor(0);
                        selected_rid = -1;
                        hsvTV.setText("");
                        }
               
                });

        }
}

