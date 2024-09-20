package com.appgoogle.Marcelo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.appgoogle.R;
import com.appgoogle.Yamir.GoogleActivity;

public class GuardadosFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guardados, container, false);

        // Inicializa los botones
        Button buttonCasa = view.findViewById(R.id.lugar_casa);
        Button buttonTrabajo = view.findViewById(R.id.lugar_trabajo);

        // Establece los escuchadores de clics
        buttonCasa.setOnClickListener(v -> {
            // Regresar a GoogleActivity con la ubicación de Casa
            Intent intent = new Intent(getActivity(), GoogleActivity.class);
            intent.putExtra("location", "casa");
            startActivity(intent);
        });

        buttonTrabajo.setOnClickListener(v -> {
            // Regresar a GoogleActivity con la ubicación de Trabajo
            Intent intent = new Intent(getActivity(), GoogleActivity.class);
            intent.putExtra("location", "trabajo");
            startActivity(intent);
        });

        return view;
    }
}
