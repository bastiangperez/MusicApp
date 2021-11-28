package com.bastiangperez.bgp_musicplayer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    ImageView bgp_coverLogo;
    ListView bgp_lvListSongs;
    ArrayList<File> bgp_songListFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Pedimos permisos
        ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        bgp_lvListSongs = findViewById(R.id.lvListSongs);
        bgp_coverLogo = findViewById(R.id.ivLogo);

        if(isPermissionGranted())
        {
            showSong();
        }


        bgp_lvListSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Arrancamos la segunda acticity y le pasamos los tres datos necesarios: el id, el nombre y el array de archivos.
                String bgp_NameOfSong = (String) bgp_lvListSongs.getItemAtPosition(i);
                Intent playActivity = new Intent(getApplicationContext(), PlayActivity.class);
                playActivity.putExtra("bgp_nameOfSong", bgp_NameOfSong);
                playActivity.putExtra("bgp_songs", bgp_songListFile);
                playActivity.putExtra("id", i);
                startActivity(playActivity);

            }
        });
    }


    public ArrayList<File> findaSong(File file) {

        //Creamos el ArrayList que contendrá los archivos de audio de nuestro dispositivo.
        ArrayList<File> bgp_songList = new ArrayList<File>();

        //Listamos TODOS los archivos que tiene el teléfono y los almacenamos en un array:
        //Si es archivo mp3 o wav lo añadimos a la lista y la devolvemos.
        File[] bgp_files = file.listFiles();
        for (File singleFile : bgp_files) {
            if(singleFile.isDirectory() && !singleFile.isHidden()) {
                bgp_songList.addAll(findaSong(singleFile));
            }
            else {
                if(singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav"))
                {
                    bgp_songList.add(singleFile);
                }

            }
        }
        return bgp_songList;
    }

    public void showSong() {
        //Llamamos al método anterior y lo cargamos en un ArraList de Files.
        bgp_songListFile = findaSong(Environment.getExternalStorageDirectory());

        //Si la lista está vacía mostramos la pantalla de vacia, si no ejecutamos el programa.
        if (bgp_songListFile.isEmpty()){
            setContentView(R.layout.custom_listview_noitems);
        } else {
        //Creamos un array de Strings que almacenará los nombres de las canciones eliminando la extension.
        ArrayList<String> bgp_songListNames = new ArrayList<String>();

        for (File songFile: bgp_songListFile
             ) {
            bgp_songListNames.add(songFile.getName().toString().replace(".mp3","").replace(".wav",""));
        }

        //Creamos el adaptador customizado
        CustomAdapter adapter = new CustomAdapter(this,bgp_songListFile, bgp_songListNames, R.layout.custom_listview_items);
        bgp_lvListSongs.setAdapter(adapter);
    }
    }

    public boolean isPermissionGranted() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.CAMERA
                }, 1);
                return false;
            }
        } else {
            return true;
        }

    }

}