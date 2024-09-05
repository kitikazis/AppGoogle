package com.appgoogle.Yamir;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_explore) {
                getSupportFragmentManager().popBackStack(null, getSupportFragmentManager().POP_BACK_STACK_INCLUSIVE);
                return true;
            } else if (itemId == R.id.navigation_saved) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new GuardadosFragment())
                        .addToBackStack(null)
                        .commit();
                return true;
            } else if (itemId == R.id.navigation_contribute) {
                // Mostrar el diálogo de confirmación personalizado
                showCustomExitDialog();
                return true;
            }

            return false;
        });
    }

    // Método para mostrar el diálogo de confirmación personalizado
    private void showCustomExitDialog() {
        // Inflar el diseño del diálogo personalizado
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.custom_exit_dialog, null);

        // Crear el AlertDialog
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        // Configurar los botones del diálogo
        Button cancelButton = dialogView.findViewById(R.id.button_cancel);
        Button exitButton = dialogView.findViewById(R.id.button_exit);

        cancelButton.setOnClickListener(v -> alertDialog.dismiss()); // Cierra el diálogo
        exitButton.setOnClickListener(v -> finish()); // Cierra la aplicación

        // Mostrar el diálogo
        alertDialog.show();
    }
}
