package com.gisconsultoria.com.apiBox.utils;

import com.gisconsultoria.com.apiBox.model.dao.ComplementoDao;
import com.gisconsultoria.com.apiBox.model.dto.Comprobante40XmlDto;
import com.gisconsultoria.com.apiBox.model.dto.ComprobanteXmlDto;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Luis Enrique Morales Soriano
 */
@Service
public class ApBoxReadXmlFile implements IApBoxReadXmlFile{

    protected static final Logger LOG = Logger.getLogger(ApBoxReadXmlFile.class.getName());

    @Autowired
    private ILogicaFacade logicaFacade;

    @Override
    public void readXmlFile(File dir) throws IOException {

        LOG.info("LECTURA DEL ARCHIVO DENTRO DE LA CARPETA: ".concat(dir.getName()));

        try(Stream<Path> stream = Files.walk(Paths.get(dir.getAbsolutePath()))){
            Set<String> archivosXml = stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toSet());

            for(String xml : archivosXml){
                decodeXmlFile(dir, xml);
            }

        }catch (IOException ex){
            LOG.error("Ocurrió un error al momento de extraer los archivos: ", ex);
            throw new IOException("Ocurrió un error al momento de extraer los archivos: " +
                    ex.getMessage());
        } catch (ParserConfigurationException e) {
            LOG.error("OCURRIÓ UN ERROR DE EJECUCIÓN: ", e);
            e.printStackTrace();
        } catch (SAXException e) {
            LOG.error("OCURRIÓ UN ERROR DE EJECUCIÓN: ", e);
            e.printStackTrace();
        } catch (Exception e) {
            LOG.error("OCURRIÓ UN ERROR DE EJECUCIÓN: ", e);
            e.printStackTrace();
        }
    }

    @Override
    public void decodeXmlFile(File file, String xml) throws Exception {

        LOG.info("DECODIFICACIÓN DEL ARCHIVO: ".concat(xml));

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document document = dBuilder.parse(
                new File(file.getAbsolutePath() + "/" + xml));

        document.getDocumentElement().normalize();
        NodeList nodeList = document.getElementsByTagName("cfdi:Comprobante");

        Double version = 0.0;

        for(int i = 0; i < nodeList.getLength(); i++){
            Node node = nodeList.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE){
                Element element = (Element)node;
                version = Double.parseDouble(element.getAttribute("Version"));
            }
        }

        if(version == 3.2){
            throw new IOException("Versión incorrecta del comprobante");
        }else if(version == 3.3 || version == 4.0){
            try {
                unmarshallXmlToComprobanteXml(file, xml, version);
            }catch(JAXBException jaxbException){
                LOG.error("Error al momento de deserializar el xml", jaxbException);
                jaxbException.printStackTrace();
            }
        }

    }

    @Override
    public void unmarshallXmlToComprobanteXml(File file, String xml, Double version) throws Exception {

        LOG.info("DESERIALIZACIÓN DEL OBJETO");
        JAXBContext jaxbContext;
        ComprobanteXmlDto comprobante33;
        Comprobante40XmlDto comprobante40;
        
		if (version == 3.3) {
			LOG.info("XML version 3.3");
			jaxbContext = JAXBContext.newInstance(ComprobanteXmlDto.class, ComplementoDao.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			comprobante33 = (ComprobanteXmlDto) unmarshaller.unmarshal(new File(file.getAbsolutePath() + "/" + xml));
			comprobante40 = null;
			
		} else {
			LOG.info("XML version 4.0");
			jaxbContext = JAXBContext.newInstance(Comprobante40XmlDto.class, ComplementoDao.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			comprobante40 = (Comprobante40XmlDto) unmarshaller.unmarshal(new File(file.getAbsolutePath() + "/" + xml));
			comprobante33 = null;
			
		}
        LOG.info("XML DESEREALIZADO");

        DOMResult res = new DOMResult();
        
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        if(comprobante33 != null && comprobante40 == null) {
        	marshaller.marshal(comprobante33, res);
        }
        if(comprobante33 == null && comprobante40 !=null) {
        	marshaller.marshal(comprobante40, res);	
        }
        
        Document doc = (Document)res.getNode();
        NodeList nodeList = doc.getElementsByTagName("tfd:TimbreFiscalDigital");
        String fechaTimbrado = "";
        String uuid = "";

        LOG.info("LECTURA DEL NODO PARA BUSCAR LA FECHA Y UUID");
        for(int i = 0; i < nodeList.getLength(); i++){
            Node node = nodeList.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE){
                Element element = (Element)node;
                fechaTimbrado = element.getAttribute("FechaTimbrado");
                uuid = element.getAttribute("UUID");
            }
        }

        if(uuid.isEmpty()){
            LOG.error("Documento sin timbre fiscal");
            throw new Exception("Documento sin timbre fiscal");
        }
        
        	if(logicaFacade.checarUuidRepetidoBD(fechaTimbrado, uuid, file, xml)){
            	if(logicaFacade.checarRfcReceptor(comprobante33, comprobante40)){
                	LOG.info("GUARDANDO ARCHIVO: ".concat(xml).concat( "EN LA BD"));
                	if(version == 3.3) {
                	logicaFacade.guardarComprobanteBD33(comprobante33, file, xml, uuid);
                	}else {
                		logicaFacade.guardarComprobanteBD40(comprobante40, file, xml, uuid);
                	}
            	}
        	}
    }
}
