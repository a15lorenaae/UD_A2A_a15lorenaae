package lorena.ud_a2a_a15lorenaae;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.preference.DialogPreference;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UD_A2A_a15lorenaae extends AppCompatActivity {
    private MediaPlayer mediaplayer;
    private String audio_seleccionado = "";
    private String nomefoto="mifoto.jpg";
    private boolean pause;
    Button botongravar;
    ImageButton botonfoto;
    Button botonreproducir;
    Spinner listaaudios;
    private MediaRecorder mediaRecorder;
    private String arquivogravar;
    private final int REQUEST_CODE_GRAVACION_OK=1;

    String carptaAudio=Environment.getExternalStorageDirectory().getAbsolutePath()+"/UD-A2A/MUSICA/";
    String carpetaImagen=Environment.getExternalStorageDirectory().getAbsolutePath()+"/UD-A2A/FOTO/";
    //String carptaAudio="/storage/sdcard/UD-A2A/MUSICA/";
    File f=new File(carptaAudio);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ud__a2_a_a15lorenaae);
        mediaplayer = new MediaPlayer();
        botonfoto = (ImageButton) findViewById(R.id.botonimagen);
        botongravar = (Button) findViewById(R.id.botongravar);
        botonreproducir = (Button) findViewById(R.id.botonreproducir);
        listaaudios=(Spinner)findViewById(R.id.listaaudios);
        listaaudios.setAdapter(iniciaradaptador());
        listaaudios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<String> adaptador = (ArrayAdapter<String>) parent.getAdapter();
                audio_seleccionado = adaptador.getItem(position);
                Toast.makeText(UD_A2A_a15lorenaae.this, "Escolleches o seguinte audio" + audio_seleccionado, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        botonreproducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String audioreproducir = carptaAudio + audio_seleccionado;
                try {
                    mediaplayer.reset();
                    mediaplayer.setDataSource(audioreproducir);
                    mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaplayer.prepare();
                    mediaplayer.start();

                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(),
                            "ERRO:" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                AlertDialog.Builder dialog = new AlertDialog.Builder(UD_A2A_a15lorenaae.this)
                        .setMessage("Reproducindo").setPositiveButton("PREME PARA PARAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mediaplayer.stop();
                                mediaplayer.release();
                            }
                        });
                dialog.show();
            }

        });
        botongravar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeStamp= DateFormat.getDateTimeInstance().format(new Date()).replaceAll(":","").replaceAll("/","_").replaceAll(" ","_");
                mediaRecorder=new MediaRecorder();
                arquivogravar=carptaAudio+timeStamp+".3gp";
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setMaxDuration(10000);
                mediaRecorder.setAudioEncodingBitRate(32768);
                mediaRecorder.setAudioSamplingRate(8000);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                mediaRecorder.setOutputFile(arquivogravar);
                try{
                    mediaRecorder.prepare();

                } catch (Exception e) {
                    e.printStackTrace();
                    mediaRecorder.reset();
                }
                mediaRecorder.start();
                AlertDialog.Builder dialog=new AlertDialog.Builder(UD_A2A_a15lorenaae.this)
                        .setMessage("GRAVANDO").setPositiveButton("PREME PARA PARAR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            mediaRecorder.stop();
                                mediaRecorder.release();
                                mediaRecorder=null;
                            }
                        });
                dialog.show();
            }
        });
        botonfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intento=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intento,REQUEST_CODE_GRAVACION_OK);

            }
        });
            }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==REQUEST_CODE_GRAVACION_OK){
            if(resultCode==RESULT_OK){
                Bitmap imagen=(Bitmap)data.getExtras().get("data");
                ImageView imgview=(ImageView)findViewById(R.id.imagen);
                imgview.setImageBitmap(imagen);
                File carpimagen=new File(carpetaImagen);
                File arquivo=new File(carpimagen,nomefoto);
                if(!carpimagen.exists()) carpimagen.mkdirs();
                else carpimagen.delete();
                FileOutputStream fos=null;
                try{
                    fos=new FileOutputStream(arquivo);
                    imagen.compress(Bitmap.CompressFormat.JPEG,10,fos);
                    fos.flush();
                    fos.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }
    }
        private ArrayAdapter<String> iniciaradaptador() {
            ArrayList<String> notasaudio = new ArrayList<String>();
            ArrayAdapter<String> adaptador = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_1, notasaudio);
            File[] directorio=f.listFiles();
            for (int i=0;i<directorio.length;i++) {

                    adaptador.add(directorio[i].getName());

            }
                return adaptador;
            }

        }
