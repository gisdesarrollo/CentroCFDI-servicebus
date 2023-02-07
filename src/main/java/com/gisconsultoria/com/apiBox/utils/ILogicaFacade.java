package com.gisconsultoria.com.apiBox.utils;

import com.gisconsultoria.com.apiBox.model.dto.Comprobante40XmlDto;
import com.gisconsultoria.com.apiBox.model.dto.ComprobanteXmlDto;

import java.io.File;
import java.text.ParseException;

/**
 * @author Luis Enrique Morales Soriano
 */
public interface ILogicaFacade {

    public boolean checarUuidRepetidoBD(String fecha, String uuid, File file, String xml) throws ParseException;

    public boolean checarRfcReceptor(ComprobanteXmlDto comprobante33,Comprobante40XmlDto comprobante40) throws Exception;

    public boolean guardarComprobanteBD33(ComprobanteXmlDto comprobante33, File file
            , String xml, String uuid)throws Exception;
    
    public boolean guardarComprobanteBD40(Comprobante40XmlDto comprobante40, File file
            , String xml, String uuid)throws Exception;

}
