package com.appgoogle.Yamir;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.appgoogle.R;

public class VistaBotonesRutasActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_botones_rutas); // Esto infla el layout "vista_botones_rutas.xml"

        // Encuentra el botón en el layout
        Button butonRegresar = findViewById(R.id.butonRegresar);

        // Asigna un click listener al botón
        butonRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Regresar a la actividad anterior
                onBackPressed(); // Vuelve a la actividad anterior
            }
        });
    }
}
