package com.appgoogle.Marcelo;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.appgoogle.R;

public class GuardadosFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guardados, container, false);
        // Aquí puedes inicializar tus vistas y lógica del fragmento
        return view;
    }
}