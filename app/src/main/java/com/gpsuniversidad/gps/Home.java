package com.gpsuniversidad.gps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;

public class Home extends AppCompatActivity {

    Button btnConsultar, btnguardar, btnExportar;
    TextView txtMostrar,txtMostrar2,txtMostrar3,txtDatos;
    DbHelper DB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        txtMostrar=findViewById(R.id.txvUbicacion);
        txtMostrar2=findViewById(R.id.txvUbicacion2);
        txtMostrar3=findViewById(R.id.txvUbicacion3);
        txtDatos=findViewById(R.id.txtDatos);
        btnConsultar=findViewById(R.id.btnMostrar);
        btnguardar=findViewById(R.id.btnGuatdar);
        btnExportar=findViewById(R.id.btnExportar);

        DB = new DbHelper(this, "gps", null, 1);

        int permisionCheck = ContextCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permisionCheck== PackageManager.PERMISSION_DENIED){
            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (this,Manifest.permission.ACCESS_FINE_LOCATION)){
            }else{
                ActivityCompat.requestPermissions(this, new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        }

        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LocationManager locationManager = (LocationManager)
                        Home.this.getSystemService(Context.LOCATION_SERVICE);
                LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        txtMostrar.setText("Latitud "+location.getLatitude());
                        txtMostrar2.setText("Longitud "+ location.getLongitude());
                        txtMostrar3.setText("Altitud "+ location.getAltitude());
                    }

                    @Override
                    public void onProviderEnabled(@NonNull String provider) {
                        
                    }

                    @Override
                    public void onProviderDisabled(@NonNull String provider) { }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) { }

                };
                int permisionCheck = ContextCompat.checkSelfPermission
                        (Home.this, Manifest.permission.ACCESS_FINE_LOCATION);
                locationManager.requestLocationUpdates
                        (LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
        });

        btnguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String latitud=txtMostrar.getText().toString();
                String longitud=txtMostrar2.getText().toString();
                String altitud=txtMostrar3.getText().toString();
                String descripcion=txtDatos.getText().toString();

                Boolean insertar = DB.insertarDatos(latitud,longitud,altitud,descripcion);

                if (insertar==true){
                    Toast.makeText(Home.this, "Informacion guardada", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Home.this, "Error al guardar la informacion", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnExportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportarDatosbCSV();
            }
        });

    }
    public void exportarDatosbCSV() {

        File carpeta = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/ReporteEnExcelGPS");
        String archivoAgenda = carpeta.toString() + "/" + "GPS.csv";
        //File carpeta = new File(getExternalFilesDir(null).getAbsolutePath()+"/ExportarSQLiteCSV");
        //String archivoAgenda = carpeta.toString() + "/" + "Visitas.csv";

        boolean isCreate = false;
        if(!carpeta.exists()) {
            isCreate = carpeta.mkdir();
        }

        try {
            FileWriter fileWriter = new FileWriter(archivoAgenda);
            SQLiteDatabase db = DB.getWritableDatabase();
            Cursor fila = db.rawQuery("select * from descripcion", null);

            if(fila != null && fila.getCount() != 0) {
                fila.moveToFirst();
                do {


                    fileWriter.append(fila.getString(0));
                    fileWriter.append(",");
                    fileWriter.append(fila.getString(1));
                    fileWriter.append(",");
                    fileWriter.append(fila.getString(2));
                    fileWriter.append(",");
                    fileWriter.append(fila.getString(3));
                    fileWriter.append(",");
                    fileWriter.append(fila.getString(4));

                } while(fila.moveToNext());
            } else {
                Toast.makeText(Home.this, "NO HAY REGISTROS, EXPORTADO VACIO.", Toast.LENGTH_LONG).show();
            }

            db.close();
            fileWriter.close();
            Toast.makeText(Home.this, "EXPORTADO EN DOCUMENTOS", Toast.LENGTH_LONG).show();

        } catch (Exception e) { }
    }

}