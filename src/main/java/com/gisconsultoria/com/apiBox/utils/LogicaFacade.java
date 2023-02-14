package com.gisconsultoria.com.apiBox.utils;

import com.gisconsultoria.com.apiBox.model.Cliente;
import com.gisconsultoria.com.apiBox.model.FacturaEmitida;
import com.gisconsultoria.com.apiBox.model.Sucursal;
import com.gisconsultoria.com.apiBox.model.dao.ImpuestoDao;
import com.gisconsultoria.com.apiBox.model.dto.Comprobante40XmlDto;
import com.gisconsultoria.com.apiBox.model.dto.ComprobanteXmlDto;
import com.gisconsultoria.com.apiBox.model.enums.PaisEnum;
import com.gisconsultoria.com.apiBox.model.enums.RegimenFiscalEnum;
import com.gisconsultoria.com.apiBox.service.IClienteService;
import com.gisconsultoria.com.apiBox.service.IFacturaEmitidaService;
import com.gisconsultoria.com.apiBox.service.ISucursalService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

/**
 * @author Luis Enrique Morales Soriano
 */
@Service
public class LogicaFacade implements ILogicaFacade {

    protected static final Logger LOG = Logger.getLogger(ApBoxReadXmlFile.class.getName());

//    @Autowired
//    private IRelXmlFacturaEmitidaService relXmlFacturaEmitidaService;

    @Autowired
    private IClienteService clienteService;

    @Autowired
    private ISucursalService sucursalService;

    @Autowired
    private IFacturaEmitidaService facturaEmitidaService;

    @Override
    public boolean checarUuidRepetidoBD(String fecha, String uuid,
                                        File file, String xml) throws ParseException {

        LOG.info("Verificando si existe el uuid: ".concat(uuid).concat(" en la base de datos"));

        File archivo = new File(file.getAbsolutePath() + "/" + xml);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-DD");
        Date fechaTimbrado = format.parse(fecha);
        calendar.setTime(fechaTimbrado);

        List<FacturaEmitida> facturas =
                facturaEmitidaService.findFirstFacturaEmitidaByUuid(uuid);

        
        //Validacion de una factura que ya fue  cargada a la Bd
        if (facturas != null) {
            for (FacturaEmitida factura : facturas) {
                if (factura != null) {
                    LOG.error("El folio ".concat(factura.getUuid().concat(" ya fue cargado al sistema")));
                    if (archivo.delete()) {
                        LOG.info("Archivo repetido, se eliminó correctamente");
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public boolean checarRfcReceptor(ComprobanteXmlDto comprobante33,Comprobante40XmlDto comprobante40) throws Exception {
    	Long Id= null;
    	String RFCEmisor= null; 
    	String RFCReceptor = null;
    	String NombreReceptor = null ;
    	String DomicilioFiscalReceptor = null;
    	String RegimenFiscalReceptor = null ;
    	
    	if(comprobante33 != null && comprobante40 == null) {
    	 
    		RFCEmisor = comprobante33.getEmisor().getRfc();
    		RFCReceptor = comprobante33.getReceptor().getRfc();
    		NombreReceptor = comprobante33.getReceptor().getNombre();
    	}
    	if(comprobante33 == null && comprobante40 != null) {
     
    		RFCEmisor = comprobante40.getEmisor().getRfc();
    		RFCReceptor = comprobante40.getReceptor().getRfc();
    		NombreReceptor = comprobante40.getReceptor().getNombre();
    		DomicilioFiscalReceptor = comprobante40.getReceptor().getDomicilioFiscal();
    		RegimenFiscalReceptor = comprobante40.getReceptor().getRegimenFiscal();
    	}
        Sucursal sucursal = sucursalService.getSucursalByRfc(RFCEmisor);
       
        

        if (sucursal == null) {
            throw new Exception("El RFC del emisor: ".concat(RFCEmisor)
                    .concat(" no fue encontrado en la base de datos"));
        }

        List<Cliente> clientes;

        if (RFCReceptor.equals("XEXX010101000") ||
                RFCReceptor.equals("XAXX010101000")) {
            clientes = clienteService.getListClienteByParamsRazonSocial(RFCReceptor,
                    NombreReceptor, sucursal.getId());
        } else {
            clientes = clienteService.getListClienteByParams(RFCReceptor
                    , sucursal.getId()); 
            
            
        }

        if (clientes.isEmpty()) {

            new Cliente();
            Cliente cliente;
            if(comprobante40 != null) {
            cliente = new Cliente(new Date(), 1, NombreReceptor,
                    RFCReceptor, PaisEnum.MEX.number, sucursal,Integer.parseInt(RegimenFiscalReceptor),DomicilioFiscalReceptor);
            }else {
            	
            	//Entonces Pertenece a un comprobante33
            	cliente = new Cliente(new Date(), 1, NombreReceptor,
                        RFCReceptor, PaisEnum.MEX.number, sucursal,DomicilioFiscalReceptor);
                
            }
            try {
                clienteService.save(cliente);
            } catch (DataIntegrityViolationException diExc) {
                LOG.error("Error al momento de guardar al cliente en la base de datos", diExc);
                throw new Exception("Error al momento de guardar al cliente en la base de datos",
                        diExc.getCause());
            }
        }
        
        //Actualizacion cuando ya existe un registro en la BD
        else {
    	   
    	    //Se realiza una consulta para identificar al Cliente por su RFC y Id
    	 
    	   Cliente cliente = clienteService.getClienteByParams(RFCReceptor, sucursal.getId());
         
    	   //Obtenemos los datos a actualizas
      	 if(comprobante40 != null) {
      		 cliente = new Cliente(cliente.getId(), cliente.getTelefono1(), cliente.getTelefono2(),cliente.getFechaAlta(), 1,NombreReceptor,RFCReceptor,cliente.getPais(), sucursal,
      				 Integer.parseInt(RegimenFiscalReceptor), DomicilioFiscalReceptor);
               }
      	 try {
      		 
      		 //Insertamos los nuevos datos y se guardan en la BD
               clienteService.save(cliente);
           } catch (DataIntegrityViolationException diExc) {
        	   
               LOG.error("Error al momento de guardar al cliente en la base de datos", diExc);
               throw new Exception("Error al momento de guardar al cliente en la base de datos",
                       diExc.getCause());
           }
    	   
       }

        return true;
    }

    //
    @Override
    public boolean guardarComprobanteBD33(ComprobanteXmlDto comprobante33, File file,
                                        String xml, String uuid) throws Exception {

        LOG.info("GUARDANDO INFORMACIÓN EN LA Base de Datos");

        try {

            Sucursal sucursal = sucursalService.getSucursalByRfc(comprobante33.getEmisor().getRfc());
            if (sucursal == null) {
                throw new Exception("No se encontró el RFC del emisor: "
                        .concat(comprobante33.getEmisor().getRfc()));
            }

            List<Cliente> clientes;

            if (comprobante33.getReceptor().getRfc().equals("XEXX010101000") ||
                    comprobante33.getReceptor().getRfc().equals("XAXX010101000")) {
                clientes = clienteService.getListClienteByParamsRazonSocial(
                        comprobante33.getReceptor().getRfc(),
                        comprobante33.getReceptor().getNombre(),
                        sucursal.getId()
                );
                if (clientes.isEmpty()) {
                    LOG.error("No se encontró receptor con RFC ".
                            concat(comprobante33.getEmisor().getRfc()).concat(" y Razón Social ").
                            concat(comprobante33.getEmisor().getNombre()));
                    throw new Exception("No se encontró receptor con RFC ".
                            concat(comprobante33.getEmisor().getRfc()).concat(" y Razón Social ").
                            concat(comprobante33.getEmisor().getNombre()));
                }
            } else {
                clientes = clienteService.getListClienteByParams(comprobante33.getReceptor().getRfc(),
                        sucursal.getId());
                if (clientes.isEmpty()) {
                    LOG.error("No se encontró receptor con RFC: "
                            .concat(comprobante33.getEmisor().getRfc()));
                    throw new Exception("No se encontró receptor con RFC: "
                            .concat(comprobante33.getEmisor().getRfc()));
                }
            }

            FacturaEmitida facturaEmitida;

            File archivo = new File(file.getAbsolutePath() + "/" + xml);
            byte[] encode = Base64.encodeBase64(FileUtils.readFileToByteArray(archivo));

            if (comprobante33.getFormaPago() == null || comprobante33.getMetodoPago() == null) {
                facturaEmitida = new FacturaEmitida(sucursal.getId(), clientes.get(0).getId(),
                        comprobante33.getFecha(), comprobante33.getFolio(),
                        0, 0,
                        0.0, comprobante33.getMoneda().number, comprobante33.getSerie(), comprobante33.getSubTotal(),
                        comprobante33.getTipoCambio(), comprobante33.getTipoComprobante().number,
                        comprobante33.getTotal(), comprobante33.getVersion().toString(),
                        uuid, encode, new Date());
            } else {
                facturaEmitida = new FacturaEmitida(sucursal.getId(), clientes.get(0).getId(),
                        comprobante33.getFecha(), comprobante33.getFolio(),
                        Integer.parseInt(comprobante33.getFormaPago()), comprobante33.getMetodoPago().tipo,
                        0.0, comprobante33.getMoneda().number, comprobante33.getSerie(), comprobante33.getSubTotal(),
                        comprobante33.getTipoCambio(), comprobante33.getTipoComprobante().number,
                        comprobante33.getTotal(), comprobante33.getVersion().toString(),
                        uuid, encode, new Date());
            }

            Double totalImpuestosTrasladados = 0.0;
            Double totalImpuestosRetenidos = 0.0;

            if (comprobante33.getImpuestos() != null) {
                for (ImpuestoDao impuestos : comprobante33.getImpuestos()) {
                    totalImpuestosRetenidos = impuestos.getTotalImpuestosRetenidos();
                    totalImpuestosTrasladados = impuestos.getTotalImpuestosTrasladados();
                }
                if (totalImpuestosRetenidos != null) {
                    facturaEmitida.setTotalImpuestosRetenidos(totalImpuestosRetenidos);
                } else {
                    facturaEmitida.setTotalImpuestosRetenidos(0.0);
                }
                facturaEmitida.setTotalImpuestosTrasladados(totalImpuestosTrasladados);
            } else {
                facturaEmitida.setTotalImpuestosRetenidos(totalImpuestosRetenidos);
                facturaEmitida.setTotalImpuestosTrasladados(totalImpuestosTrasladados);
            }

            facturaEmitidaService.save(facturaEmitida);

            if (archivo.delete()) {
                LOG.info("Archivo eliminado de la carpeta: " + file.getName());
            }
        } catch (Exception ex) {
            LOG.error("Ocurrió un error al momento de guardar el documento", ex);
            throw new Exception("Occurió un error al momento de guardar el documento", ex);
        }

        return true;
    }
    
    @Override
    public boolean guardarComprobanteBD40(Comprobante40XmlDto comprobante40, File file,
                                        String xml, String uuid) throws Exception {

        LOG.info("GUARDANDO INFORMACIÓN EN LA Base de Datos");

        try {

            Sucursal sucursal = sucursalService.getSucursalByRfc(comprobante40.getEmisor().getRfc());
            if (sucursal == null) {
                throw new Exception("No se encontró el RFC del emisor: "
                        .concat(comprobante40.getEmisor().getRfc()));
            }

            List<Cliente> clientes;

            if (comprobante40.getReceptor().getRfc().equals("XEXX010101000") ||
                    comprobante40.getReceptor().getRfc().equals("XAXX010101000")) {
                clientes = clienteService.getListClienteByParamsRazonSocial(
                        comprobante40.getReceptor().getRfc(),
                        comprobante40.getReceptor().getNombre(),
                        sucursal.getId()
                );
                if (clientes.isEmpty()) {
                    LOG.error("No se encontró receptor con RFC ".
                            concat(comprobante40.getEmisor().getRfc()).concat(" y Razón Social ").
                            concat(comprobante40.getEmisor().getNombre()));
                    throw new Exception("No se encontró receptor con RFC ".
                            concat(comprobante40.getEmisor().getRfc()).concat(" y Razón Social ").
                            concat(comprobante40.getEmisor().getNombre()));
                }
            } else {
                clientes = clienteService.getListClienteByParams(comprobante40.getReceptor().getRfc(),
                        sucursal.getId());
                if (clientes.isEmpty()) {
                    LOG.error("No se encontró receptor con RFC: "
                            .concat(comprobante40.getEmisor().getRfc()));
                    throw new Exception("No se encontró receptor con RFC: "
                            .concat(comprobante40.getEmisor().getRfc()));
                }
            }

            FacturaEmitida facturaEmitida;

            File archivo = new File(file.getAbsolutePath() + "/" + xml);
            byte[] encode = Base64.encodeBase64(FileUtils.readFileToByteArray(archivo));

            if (comprobante40.getFormaPago() == null || comprobante40.getMetodoPago() == null) {
                facturaEmitida = new FacturaEmitida(sucursal.getId(), clientes.get(0).getId(),
                        comprobante40.getFecha(), comprobante40.getFolio(),
                        0, 0,
                        0.0, comprobante40.getMoneda().number, comprobante40.getSerie(), comprobante40.getSubTotal(),
                        comprobante40.getTipoCambio(), comprobante40.getTipoComprobante().number,
                        comprobante40.getTotal(), comprobante40.getVersion().toString(),
                        uuid, encode, new Date());
            } else {
                facturaEmitida = new FacturaEmitida(sucursal.getId(), clientes.get(0).getId(),
                        comprobante40.getFecha(), comprobante40.getFolio(),
                        Integer.parseInt(comprobante40.getFormaPago()), comprobante40.getMetodoPago().tipo,
                        0.0, comprobante40.getMoneda().number, comprobante40.getSerie(), comprobante40.getSubTotal(),
                        comprobante40.getTipoCambio(), comprobante40.getTipoComprobante().number,
                        comprobante40.getTotal(), comprobante40.getVersion().toString(),
                        uuid, encode, new Date());
            }

            Double totalImpuestosTrasladados = 0.0;
            Double totalImpuestosRetenidos = 0.0;

            if (comprobante40.getImpuestos() != null) {
                for (ImpuestoDao impuestos : comprobante40.getImpuestos()) {
                    totalImpuestosRetenidos = impuestos.getTotalImpuestosRetenidos();
                    totalImpuestosTrasladados = impuestos.getTotalImpuestosTrasladados();
                }
                if (totalImpuestosRetenidos != null) {
                    facturaEmitida.setTotalImpuestosRetenidos(totalImpuestosRetenidos);
                } else {
                    facturaEmitida.setTotalImpuestosRetenidos(0.0);
                }
                facturaEmitida.setTotalImpuestosTrasladados(totalImpuestosTrasladados);
            } else {
                facturaEmitida.setTotalImpuestosRetenidos(totalImpuestosRetenidos);
                facturaEmitida.setTotalImpuestosTrasladados(totalImpuestosTrasladados);
            }

            facturaEmitidaService.save(facturaEmitida);

            if (archivo.delete()) {
                LOG.info("Archivo eliminado de la carpeta: " + file.getName());
            }
        } catch (Exception ex) {
            LOG.error("Ocurrió un error al momento de guardar el documento", ex);
            throw new Exception("Occurió un error al momento de guardar el documento", ex);
        }

        return true;
    }
    
}
