package com.appgoogle.Marcelo;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.appgoogle.Marcelo.guardados;
import com.appgoogle.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;

public class barra_inferior extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barra);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_explore) {
                    // Acción para Explorar
                    return true;
                } else if (id == R.id.action_saved) {
                    // Llamar a TuVistaActivity
                    Intent intent = new Intent(barra_inferior.this, guardados.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.action_contribute) {
                    // Acción para Contribuir
                    return true;
                }
                return false;
            }
        });
    }
}