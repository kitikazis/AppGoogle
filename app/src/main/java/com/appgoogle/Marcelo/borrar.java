package com.appgoogle.Marcelo;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.appgoogle.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;

public class borrar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barra); // Asegúrate de que el layout correcto esté vinculado

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_explore) {
                    // Acción para "Explorar"
                    return true;
                } else if (id == R.id.action_saved) {
                    // Acción para "Guardados"
                    return true;
                } else if (id == R.id.action_contribute) {
                    // Acción para "Contribuir"
                    return true;
                }
                return false;
            }
        });

    }
}
