/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uma.ecplusproject.ecplusandroidapp.restws.domain;

import java.io.Serializable;
import java.util.Set;

/**
 *
 * @author gabriel
 */

public class PalabraRes implements Serializable {
    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
    private Long id;
    private String nombre;
    private Boolean iconoReemplazable;
    private String hash;
    private Set<String> audiovisuales;
    private String icono;
    private String iconoReemplazado;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Boolean getIconoReemplazable() {
        return iconoReemplazable;
    }

    public void setIconoReemplazable(Boolean iconoReemplazable) {
        this.iconoReemplazable = iconoReemplazable;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Set<String> getAudiovisuales() {
        return audiovisuales;
    }

    public void setAudiovisuales(Set<String> audiovisuales) {
        this.audiovisuales = audiovisuales;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getIconoReemplazado() {
        return iconoReemplazado;
    }

    public void setIconoReemplazado(String iconoReemplazado) {
        this.iconoReemplazado = iconoReemplazado;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PalabraRes)) {
            return false;
        }
        PalabraRes other = (PalabraRes) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PalabraRes{" + "id=" + id + ", nombre=" + nombre + ", iconoReemplazable=" + iconoReemplazable + ", hash=" + hash + ", audiovisuales=" + audiovisuales + ", icono=" + icono + ", iconoReemplazado=" + iconoReemplazado + '}';
    }

    
    
}

