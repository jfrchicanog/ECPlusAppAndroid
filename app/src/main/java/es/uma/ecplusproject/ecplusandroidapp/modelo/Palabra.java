package es.uma.ecplusproject.ecplusandroidapp.modelo;

import java.util.List;

/**
 * Created by francis on 18/3/16.
 */
public class Palabra {
    private String texto;
    private List<RecursoAV> recursos;

    public Palabra(String texto) {
        this.texto = texto;
    }

    public String toString() {
        return texto;
    }
}
