package com.appgoogle.Sebastian;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.appgoogle.R;
import com.appgoogle.Yamir.GoogleActivity;
import com.appgoogle.Diego.DbHelper;

public class fragment_login_tab extends Fragment {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private DbHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_tab, container, false);

        // Inicializa las vistas
        emailEditText = view.findViewById(R.id.login_email);
        passwordEditText = view.findViewById(R.id.login_password);
        loginButton = view.findViewById(R.id.login_button);

        // Inicializa DatabaseHelper
        dbHelper = new DbHelper(getContext());

        // Configura el evento de clic para el botón
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Validar los datos contra la base de datos
            if (dbHelper.autenticarUsuario(email, password)) {
                // Mostrar alerta de datos correctos
                Toast.makeText(getContext(), "Datos correctos", Toast.LENGTH_SHORT).show();

                // Limpiar los campos de entrada
                emailEditText.setText("");
                passwordEditText.setText("");

                // Iniciar la nueva actividad
                Intent intent = new Intent(getActivity(), GoogleActivity.class);
                startActivity(intent);

            } else {
                // Mostrar alerta de error
                Toast.makeText(getContext(), "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
