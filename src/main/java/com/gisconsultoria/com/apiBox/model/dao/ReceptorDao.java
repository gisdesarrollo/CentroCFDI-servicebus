package com.gisconsultoria.com.apiBox.model.dao;

import com.gisconsultoria.com.apiBox.model.enums.PaisEnum;
import com.gisconsultoria.com.apiBox.model.enums.RegimenFiscalEnum;
import com.gisconsultoria.com.apiBox.model.enums.UsoCfdiEnum;

import javax.xml.bind.annotation.*;

/**
 * @author Luis Enrique Morales Soriano
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ReceptorDao {

    @XmlAttribute(name = "Rfc")
    private String rfc;

    @XmlAttribute(name = "Nombre")
    private String nombre;

    @XmlAttribute(name = "ResidenciaFiscal")
    private PaisEnum residenciaFiscal;
    
    //version 4.0
    @XmlAttribute(name = "RegimenFiscalReceptor")
    private String regimenFiscal;
    
    //version 4.0
    @XmlAttribute(name = "DomicilioFiscalReceptor")
    private String domicilioFiscal;

    @XmlTransient
    private boolean ResidenciaFiscalSpecified;

    @XmlAttribute(name = "NumRegIdTrib")
    private String numRegIdTrib;

    @XmlAttribute(name = "UsoCFDI")
    private UsoCfdiEnum usoCfdi;

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public PaisEnum getResidenciaFiscal() {
        return residenciaFiscal;
    }

    public void setResidenciaFiscal(PaisEnum residenciaFiscal) {
        this.residenciaFiscal = residenciaFiscal;
    }

    public boolean isResidenciaFiscalSpecified() {
        return ResidenciaFiscalSpecified;
    }

    public void setResidenciaFiscalSpecified(boolean residenciaFiscalSpecified) {
        ResidenciaFiscalSpecified = residenciaFiscalSpecified;
    }

    public String getNumRegIdTrib() {
        return numRegIdTrib;
    }

    public void setNumRegIdTrib(String numRegIdTrib) {
        this.numRegIdTrib = numRegIdTrib;
    }

    public UsoCfdiEnum getUsoCfdi() {
        return usoCfdi;
    }

    public void setUsoCfdi(UsoCfdiEnum usoCfdi) {
        this.usoCfdi = usoCfdi;
    }

	public String getRegimenFiscal() {
		return regimenFiscal;
	}

	public void setRegimenFiscal(String regimenFiscal) {
		this.regimenFiscal = regimenFiscal;
	}

	public String getDomicilioFiscal() {
		return domicilioFiscal;
	}

	public void setDomicilioFiscal(String domicilioFiscal) {
		this.domicilioFiscal = domicilioFiscal;
	}


    
}
