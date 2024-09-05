package com.appgoogle.Yamir;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.appgoogle.Marcelo.GuardadosFragment;
import com.appgoogle.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class GoogleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_explore) {
                // Acción para el botón "Explorar"
                return true;
            } else if (itemId == R.id.navigation_saved) {
                selectedFragment = new GuardadosFragment();
            } else if (itemId == R.id.navigation_contribute) {

                finish(); // Cerrar la aplicación
                return true;
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .addToBackStack(null)
                        .commit();
                return true;
            }
            return false;
        });
    }
}