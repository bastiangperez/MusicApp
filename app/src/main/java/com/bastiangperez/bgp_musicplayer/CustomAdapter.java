package com.bastiangperez.bgp_musicplayer;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.io.File;
import java.util.List;

//Creamos adaptador personalizado que recibirá el array de banderas, los datos almacenados en el array y nos inflará la vista de la listview basándose
// en el layout customizado que hemos hecho (en la carpeta layout).
public class CustomAdapter extends BaseAdapter {
    Context context;
    List<String> bgp_namesString;
    List<File> bgp_songFiles;
    int logo;
    LayoutInflater inflter;

    public CustomAdapter(Context applicationContext,List<File> bgp_songFiles, List<String> bgp_namesString, int logo ) {
        this.context = applicationContext;
        this.bgp_songFiles=bgp_songFiles;
        this.bgp_namesString = bgp_namesString;
        this.logo = logo;
        inflter = (LayoutInflater.from(applicationContext));
    }

    public CustomAdapter(MainActivity applicationContext, int custom_listview_items, String[] items) {
    }

    @Override
    public int getCount() {
        return bgp_namesString.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflter.inflate(R.layout.custom_listview_items, null);
        //Añadimos la animación de inicio de texto.
        Animation animation = null;
        animation = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.down_to_up);
        animation.setDuration(900);
        view.startAnimation(animation);
        animation = null;

        //Creamos las viñetas de las caratulas para que aparezcan en el listView.
        ImageView icon = (ImageView) view.findViewById(R.id.ivLogo);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        Uri uri = Uri.parse(bgp_songFiles.get(i).toString());
        mmr.setDataSource(String.valueOf(uri));
        byte[] bgp_cover = mmr.getEmbeddedPicture();
        if(bgp_cover != null){
            icon.setImageBitmap(BitmapFactory.decodeByteArray(bgp_cover,0, bgp_cover.length));
        } else {
            icon.setImageResource(R.drawable.logo);
        }

        //TextView names = (TextView) view.findViewById(R.id.tvText);
        TextView bgp_nameList = (TextView) view.findViewById(R.id.tvName);
        bgp_nameList.setSelected(true);
        bgp_nameList.setText(bgp_namesString.get(i));
        return view;
    }
}
