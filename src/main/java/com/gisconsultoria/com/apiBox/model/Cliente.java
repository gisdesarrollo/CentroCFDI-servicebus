package com.gisconsultoria.com.apiBox.model;

import javax.persistence.*;

import com.gisconsultoria.com.apiBox.model.enums.RegimenFiscalEnum;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Luis Enrique Morales Soriano
 */
@Entity
@Table(name = "cat_clientes")
public class Cliente implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Telefono1")
    private String telefono1;

    @Column(name = "Telefono2")
    private String telefono2;

    @Column(name = "Email")
    private String email;

    @Column(name = "FechaAlta")
    private Date fechaAlta;

    @Column(name = "Status")
    private int status;

    @Column(name = "Observaciones")
    private String observaciones;

    @Column(name = "RazonSocial")
    private String razonSocial;

    @Column(name = "Rfc")
    private String rfc;

    @Column(name = "CodigoPostal")
    private String codigoPostal;
    
    @Column(name = "RegimenFiscal")
    private int regimenFiscal;

    @Column(name = "Pais")
    private int pais;

    @ManyToOne
    @JoinColumn(name = "SucursalId", nullable = false)
    private Sucursal sucursal;

    public Cliente(){}

    public Cliente(Date fechaAlta, int status, String razonSocial, String rfc, int pais, Sucursal sucursal,int regimenFiscal,String codigoPostal) {
        this.fechaAlta = fechaAlta;
        this.status = status;
        this.razonSocial = razonSocial;
        this.rfc = rfc;
        this.pais = pais;
        this.sucursal = sucursal;
        this.regimenFiscal = regimenFiscal;
        this.codigoPostal = codigoPostal;
        
    }
    public Cliente(Date fechaAlta, int status, String razonSocial, String rfc, int pais, Sucursal sucursal,String codigoPostal) {
        this.fechaAlta = fechaAlta;
        this.status = status;
        this.razonSocial = razonSocial;
        this.rfc = rfc;
        this.pais = pais;
        this.sucursal = sucursal;
        this.codigoPostal = codigoPostal;
        
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTelefono1() {
        return telefono1;
    }

    public void setTelefono1(String telefono1) {
        this.telefono1 = telefono1;
    }

    public String getTelefono2() {
        return telefono2;
    }

    public void setTelefono2(String telefono2) {
        this.telefono2 = telefono2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public int getPais() {
        return pais;
    }

    public void setPais(int pais) {
        this.pais = pais;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

	public int getRegimenFiscal() {
		return regimenFiscal;
	}

	public void setRegimenFiscal(int regimenFiscal) {
		this.regimenFiscal = regimenFiscal;
	}
    
}
