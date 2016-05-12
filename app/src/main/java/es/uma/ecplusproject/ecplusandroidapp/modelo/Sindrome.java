package es.uma.ecplusproject.ecplusandroidapp.modelo;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by francis on 18/3/16.
 */
public class Sindrome implements Serializable {
    private String texto;
    private String descripcion;

    public Sindrome(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion=descripcion;
    }

    public String toString() {
        return texto;
    }
}
