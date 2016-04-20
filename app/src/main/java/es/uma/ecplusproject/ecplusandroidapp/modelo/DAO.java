package es.uma.ecplusproject.ecplusandroidapp.modelo;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import es.uma.ecplusproject.ecplusandroidapp.R;

/**
 * Created by francis on 18/3/16.
 */
public class DAO {


    private static String [] sindromes = new String [] {
        "Angelman", "Pit", "Mckey", "MacGiver", "Anibal", "Skywalker", "McAfee", "Panda", "Down", "Diógenes"
    };

    private List<Palabra> listaPalabras;
    private List<Sindrome> listaSindromes;

    public List<Palabra> getPalabras() {
        if (listaPalabras == null) {
            crearListaDePalabras();
        }
        return listaPalabras;
    }

    private void crearListaDePalabras() {
        listaPalabras = new ArrayList<>();

        Palabra palabra = createPalabra("Abrigo", R.drawable.abrigo);
        listaPalabras.add(palabra);

        palabra = createPalabra("Abrir", R.drawable.abrir);
        listaPalabras.add(palabra);

        palabra = createPalabra("Agua", R.drawable.agua);
        listaPalabras.add(palabra);

        palabra = createPalabra("Amarillo", R.drawable.amarillo);
        listaPalabras.add(palabra);

        palabra = createPalabra("Apagado", R.drawable.apagado);
        listaPalabras.add(palabra);

        palabra = createPalabra("Apagar", R.drawable.apagar);
        listaPalabras.add(palabra);

        palabra = createPalabra("Aprender", R.drawable.aprender);
        listaPalabras.add(palabra);

        palabra = createPalabra("Árbol", R.drawable.arbol);
        listaPalabras.add(palabra);

        palabra = createPalabra("Manzana", R.drawable.manzana);
        listaPalabras.add(palabra);
    }

    @NonNull
    private Palabra createPalabra(String cadenaPalabra, int pictograma) {
        Palabra palabra = new Palabra(cadenaPalabra);
        palabra.addRecurso(new Pictograma(contexto.getResources().getDrawable(pictograma)));
        return palabra;
    }

    public List<Sindrome> getSindromes() {
        if (listaSindromes == null) {
            crearListaDeSindromes();
        }
        return listaSindromes;
    }

    private void crearListaDeSindromes() {
        listaSindromes = new ArrayList<>();
        Sindrome sind = new Sindrome("Angelman");
        listaSindromes.add(sind);

        sind = new Sindrome("Pitt Hopkins");
        listaSindromes.add(sind);

        sind = new Sindrome("Mowat Wilson");
        listaSindromes.add(sind);

        sind = new Sindrome("Phelan McDermid");
        listaSindromes.add(sind);

        sind = new Sindrome("Kleefstra");
        listaSindromes.add(sind);

        sind = new Sindrome("Clásico de Rett");
        listaSindromes.add(sind);

        sind = new Sindrome("Autismo severo");
        listaSindromes.add(sind);

        sind = new Sindrome("Microduplicación/microdeleción con afectación grave del lenguaje");
        listaSindromes.add(sind);

        sind = new Sindrome("Parálisis cerebral tetraparesia distónica");
        listaSindromes.add(sind);

        sind = new Sindrome("West");
        listaSindromes.add(sind);

        sind = new Sindrome("Dravet");
        listaSindromes.add(sind);
    }

    private static DAO dao;
    private static Context contexto;

    public static void setContext(Context contexto) {
        DAO.contexto=contexto;
    }

}
