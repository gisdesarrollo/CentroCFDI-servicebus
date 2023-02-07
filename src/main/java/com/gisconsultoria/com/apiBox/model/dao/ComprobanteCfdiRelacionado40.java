package com.gisconsultoria.com.apiBox.model.dao;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.gisconsultoria.com.apiBox.model.enums.TipoRelacionEnum;

/**
 * @author Alexander Garcia Martinez
 */
@XmlType(name = "CfdiRelacionado")
@XmlAccessorType(XmlAccessType.FIELD)
public class ComprobanteCfdiRelacionado40 {
	
	 @XmlElement(name = "CfdiRelacionado")
	    private List<CfdiRelacionadoDao> cfdiRelacionado;

	    @XmlAttribute(name = "TipoRelacion")
	    private TipoRelacionEnum tipoRelacion;

	   
	    public List<CfdiRelacionadoDao> getCfdiRelacionado() {
			return cfdiRelacionado;
		}

		public void setCfdiRelacionado(List<CfdiRelacionadoDao> cfdiRelacionado) {
			this.cfdiRelacionado = cfdiRelacionado;
		}

		public TipoRelacionEnum getTipoRelacion() {
	        return tipoRelacion;
	    }

	    public void setTipoRelacion(TipoRelacionEnum tipoRelacion) {
	        this.tipoRelacion = tipoRelacion;
	    }

}
