package com.example.compras;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class FragmentUno extends Fragment {

    private View fragmento;
    private ImageView camisetas, apple, auriculares, bolsos;
    private ImageView camaras, celulares, gafas, instrumentos;
    private ImageView laptops, relojes, videojuegos, libros;
    public FragmentUno() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmento =  inflater.inflate(R.layout.fragment_uno, container, false);

       camisetas = (ImageView)fragmento.findViewById(R.id.camisetas);
       apple = (ImageView)fragmento.findViewById(R.id.apple);
       auriculares = (ImageView)fragmento.findViewById(R.id.auriculares);
       bolsos = (ImageView)fragmento.findViewById(R.id.bolsos);
       camaras = (ImageView)fragmento.findViewById(R.id.camaras);
       celulares = (ImageView)fragmento.findViewById(R.id.celulares);
       gafas = (ImageView)fragmento.findViewById(R.id.gafas);
       instrumentos = (ImageView)fragmento.findViewById(R.id.instrumentos);
       laptops = (ImageView)fragmento.findViewById(R.id.laptops);
       relojes = (ImageView)fragmento.findViewById(R.id.relojes);
       videojuegos = (ImageView)fragmento.findViewById(R.id.videojuegos);
       libros = (ImageView)fragmento.findViewById(R.id.libros);


       camisetas.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getContext(), AgregarproductoActivity.class);
               intent.putExtra("categoria", "camisetas");
               startActivity(intent);
           }
       });

        apple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AgregarproductoActivity.class);
                intent.putExtra("categoria", "apple");
                startActivity(intent);
            }
        });

        auriculares.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AgregarproductoActivity.class);
                intent.putExtra("categoria", "auriculares");
                startActivity(intent);
            }
        });

        bolsos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AgregarproductoActivity.class);
                intent.putExtra("categoria", "bolsos");
                startActivity(intent);
            }
        });

        camaras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AgregarproductoActivity.class);
                intent.putExtra("categoria", "camaras");
                startActivity(intent);
            }
        });

        celulares.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AgregarproductoActivity.class);
                intent.putExtra("categoria", "celulares");
                startActivity(intent);
            }
        });

        gafas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AgregarproductoActivity.class);
                intent.putExtra("categoria", "gafas");
                startActivity(intent);
            }
        });

        instrumentos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AgregarproductoActivity.class);
                intent.putExtra("categoria", "instrumentos");
                startActivity(intent);
            }
        });

        laptops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AgregarproductoActivity.class);
                intent.putExtra("categoria", "laptops");
                startActivity(intent);
            }
        });

        relojes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AgregarproductoActivity.class);
                intent.putExtra("categoria", "relojes");
                startActivity(intent);
            }
        });

        videojuegos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AgregarproductoActivity.class);
                intent.putExtra("categoria", "videojuegos");
                startActivity(intent);
            }
        });

        libros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AgregarproductoActivity.class);
                intent.putExtra("categoria", "libros");
                startActivity(intent);
            }
        });

        return fragmento;
    }
}