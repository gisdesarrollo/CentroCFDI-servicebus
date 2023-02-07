package com.gisconsultoria.com.apiBox.utils;

import java.io.File;
import java.io.IOException;

/**
 * @author Luis Enrique Morales Soriano
 */
public interface IApBoxReadXmlFile {

    public void readXmlFile(File file)  throws IOException;

    public void decodeXmlFile(File file, String xml) throws Exception;

    public void unmarshallXmlToComprobanteXml(File file, String xml,Double version) throws Exception;

}
