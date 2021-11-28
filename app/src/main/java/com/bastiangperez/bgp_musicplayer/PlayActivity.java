package com.bastiangperez.bgp_musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

public class PlayActivity extends AppCompatActivity {

    MediaPlayer bgp_mediaPlayer;
    ListView bgp_lvListSongs;
    ImageView bgp_ivPlay;
    ImageView bgp_ivStop;
    ImageView bgp_ivRepeat;
    SeekBar bgp_volumeBar;
    SeekBar bgp_progressBar;
    Handler handler;
    AudioManager bgp_audioManager;
    ImageView  bgp_btnVolume;
    TextView bgp_tvName;
    boolean bgp_repeat = false;
    boolean bgp_pressed = false;
    ImageView bgp_coverImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        //Cargamos todos los elementos que estarán presentes en la vista.
        bgp_ivStop = findViewById(R.id.ivStop);
        bgp_ivPlay = findViewById(R.id.ivPlay);
        bgp_ivRepeat = findViewById(R.id.ivRepeat);
        bgp_volumeBar = findViewById(R.id.seekBarVolume);
        bgp_btnVolume = findViewById(R.id.ivVolume);
        bgp_tvName = findViewById(R.id.tvName);
        bgp_progressBar = findViewById(R.id.sbProgress);
        bgp_coverImage = findViewById(R.id.imageView);
        bgp_audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        handler = new Handler();

        //Hacemos que el botón repeat por defecto muestre el boton blanco que significa que no ha sido pulsado.
        bgp_ivRepeat.setBackgroundResource(R.drawable.ic_baseline_loop_24);

        //Configuramos todo lo relacionado con el botón de volumen y su regulación.
        configVolume();

        //Obtenemos los datos procedentes de la MainActivity.
        int bgp_idSong = getIntent().getExtras().getInt("id");
        String bgp_songName = getIntent().getExtras().getString("bgp_nameOfSong");
        ArrayList<File>  bgp_songList = (ArrayList) getIntent().getExtras().getParcelableArrayList("bgp_songs");

        //Mostramos en el textView el nombre de la canción que se está reproduciendo extrayendolo de la lista de Archivos y su id.
        bgp_tvName.setSelected(true);
        bgp_songName = bgp_songList.get(bgp_idSong).getName();
        bgp_tvName.setText(bgp_songName);
        //Creamos la Uri que se le pasará al mediaplayer para que reconozca la canción seleccionada y creamos el reproductor.
        Uri uri = Uri.parse(bgp_songList.get(bgp_idSong).toString());
        bgp_mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);

        //Configuramos la barra de progreso de la canción.
        configProgressBar();

        //Ejecutamos el método de Play automático al pulsar la canción seleccionada
        playPauseSong(bgp_idSong);

        //Configuramos la cover Image de las canciones en el playActivity:
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(String.valueOf(uri));
        byte[] bgp_cover = mmr.getEmbeddedPicture();
        if(bgp_cover != null){
            bgp_coverImage.setImageBitmap(BitmapFactory.decodeByteArray(bgp_cover,0, bgp_cover.length));
        } else {
            bgp_coverImage.setImageResource(R.drawable.logo);
        }



        bgp_btnVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bgp_pressed)  {
                    bgp_volumeBar.setVisibility(View.INVISIBLE);
                    bgp_pressed= false;
                } else {
                    bgp_volumeBar.setVisibility(View.VISIBLE);
                    bgp_pressed = true;
                }
            }
        });

        bgp_ivRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                repeatSong(bgp_idSong);
            }
        });

        bgp_ivStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stopSong(bgp_idSong);
                bgp_ivPlay.setVisibility(View.VISIBLE);
            }
        });

        bgp_ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPauseSong(bgp_idSong);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        bgp_mediaPlayer.stop();
        bgp_mediaPlayer.reset();
    }

    private void configProgressBar() {
        bgp_progressBar.setMax(bgp_mediaPlayer.getDuration());
        bgp_progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean userChoice) {
                if(userChoice) {
                    bgp_mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        UpdateSeekBar updateSeekBar = new UpdateSeekBar();
        handler.post(updateSeekBar);
    }

    private void configVolume() {
        //Conseguimos el valor máximo que puede alcanzar el volumen
        int maxVolume = bgp_audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        //Obtenemos el valor actual de la música.
        int curVolume = bgp_audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        //Fijamos ese volumen máximo en la barra de volumen
        bgp_volumeBar.setMax(maxVolume);

        //Establecemos el progreso en la misma barra de volumen.
        bgp_volumeBar.setProgress(curVolume);

        //Configuramos para que al modificar el volumen realice el cambio de volumen.
        bgp_volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //we just wanto to set  the volume
                bgp_audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,i,0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void playPauseSong(int id) {
        if (bgp_mediaPlayer.isPlaying()) {
            Toast.makeText(this,"Pausa", Toast.LENGTH_SHORT).show();
            bgp_mediaPlayer.pause();
            bgp_ivPlay.setBackgroundResource(R.drawable.ic_baseline_play_circle_outline_24);
        } else {
            bgp_mediaPlayer.start();
            bgp_ivPlay.setBackgroundResource(R.drawable.ic_baseline_pause_circle_outline_24);
            Toast.makeText(this, "Reproducción", Toast.LENGTH_SHORT).show();
        }
    }


    public void stopSong(int id) {
        if (bgp_mediaPlayer.isPlaying()) {
            bgp_mediaPlayer.stop();
            bgp_mediaPlayer.prepareAsync();
            bgp_ivPlay.setBackgroundResource(R.drawable.ic_baseline_play_circle_outline_24);
            Toast.makeText(this,"La canción ha sido detenida", Toast.LENGTH_SHORT).show();
    }else {
            Toast.makeText(this,"La canción ya está detenida.", Toast.LENGTH_SHORT).show();
        }
    }

    public void repeatSong(int id)  {
        if(bgp_repeat){
            bgp_mediaPlayer.setLooping(false);
            bgp_ivRepeat.setBackgroundResource(R.drawable.ic_baseline_loop_24);
            Toast.makeText(this, "Has desactivado la repetición de la canción", Toast.LENGTH_SHORT).show();
            bgp_repeat= true;
        } else {
            bgp_mediaPlayer.setLooping(true);
            bgp_ivRepeat.setBackgroundResource(R.drawable.ic_baseline_loopoff_24);
            Toast.makeText(this, "Has activado la repetición de la canción", Toast.LENGTH_SHORT).show();
            bgp_repeat = true;
        }

    }

    public class UpdateSeekBar implements Runnable {

        @Override
        public void run() {
            bgp_progressBar.setProgress(bgp_mediaPlayer.getCurrentPosition());
            handler.postDelayed(this, 100);
        }
    }

}