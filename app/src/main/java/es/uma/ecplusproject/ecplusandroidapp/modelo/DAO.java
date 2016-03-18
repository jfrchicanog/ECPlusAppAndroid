package es.uma.ecplusproject.ecplusandroidapp.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by francis on 18/3/16.
 */
public class DAO {

    private static String [] palabras = new String[] {
        "Manzana", "Galleta", "Leche", "Zumo", "Bañar", "Correr", "Chocolate", "Peine", "Manos", "Brazo"
    };

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
        for (String palabra: palabras) {
            listaPalabras.add(new Palabra(palabra));
        }
    }

    public List<Sindrome> getSindromes() {
        if (listaSindromes == null) {
            crearListaDeSindromes();
        }
        return listaSindromes;
    }

    private void crearListaDeSindromes() {
        listaSindromes = new ArrayList<>();
        for (String sindrome: sindromes) {
            listaSindromes.add(new Sindrome(sindrome));
        }
    }
}
