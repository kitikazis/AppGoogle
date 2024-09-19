package com.appgoogle.Sebastian;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import com.appgoogle.Diego.DbHelper;

import android.widget.Toast;
import com.appgoogle.R;
import com.appgoogle.Yamir.GoogleActivity;

public class LoginUserTabFragment extends Fragment {

    private EditText userEditText;
    private EditText passEditText;
    private EditText confPassEditText;
    private Button registerButton;
    private DbHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_user_tab, container, false);

        // Inicializa las vistas
        userEditText = view.findViewById(R.id.signup_user);
        passEditText = view.findViewById(R.id.signup_password);
        confPassEditText = view.findViewById(R.id.signup_confirm);
        registerButton = view.findViewById(R.id.signup_button);

        // Inicializa DatabaseHelper
        dbHelper = new DbHelper(getContext());

        // Configura el evento de clic para el botón
        registerButton.setOnClickListener(v -> {
            String user = userEditText.getText().toString().trim();
            String pass = passEditText.getText().toString().trim();
            String conf_pass = confPassEditText.getText().toString().trim();

            // Validar los datos de entrada
            if (user.isEmpty() || pass.isEmpty() || conf_pass.isEmpty()) {
                Toast.makeText(getContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass.equals(conf_pass)) {
                Toast.makeText(getContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            // Intentar registrar el usuario en la base de datos
            boolean registroExitoso = dbHelper.registrarUsuario(user, pass);
            if (registroExitoso) {

                Toast.makeText(getContext(), "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();

                // Limpiar los campos de entrada
                userEditText.setText("");
                passEditText.setText("");
                confPassEditText.setText("");

                /* Iniciar la nueva actividad o realizar otra acción
                Intent intent = new Intent(getActivity(), GoogleActivity.class);
                startActivity(intent); */

            } else {
                Toast.makeText(getContext(), "El usuario ya existe", Toast.LENGTH_SHORT).show();
            }

        });

        return view;
    }
}