package com.gisconsultoria.com.apiBox.services;

import com.gisconsultoria.com.apiBox.model.Sucursal;
import com.gisconsultoria.com.apiBox.service.ISucursalService;
import com.gisconsultoria.com.apiBox.utils.IDescargaCfdi;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Luis Enrique Morales Soriano
 */
@Service
public class ApBoxXsaService {

	protected static final Logger LOG = Logger.getLogger(ApBoxXsaService.class.getName());

	@Value("${path.archivo.archivosXml}")
	private String pathArchivos;

	@Autowired
	private ISucursalService sucursalService;

	@Autowired
	private IDescargaCfdi descargaCfdi;

	public void ObtenerArchivos(Long sucursalId, Date fecha) throws IOException {

		Sucursal sucursal = sucursalService.findById(sucursalId);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cFechaInicial = Calendar.getInstance();
        cFechaInicial.setTime(fecha);
      ///se cambia a menos dos meses para descargar las facturas faltantes
      //cFechaInicial.add(Calendar.MONTH, -2);
        cFechaInicial.add(Calendar.DAY_OF_MONTH, -1);
		String fechaInicial = dateFormat.format(cFechaInicial.getTime());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fecha);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		String fechaFinal = dateFormat.format(calendar.getTime());
		File zip = null;
		int pageZise = 100;
		int totalCfdiEmitidos=0;
		
		try {
			// get TotalCfdiEmitidos
			totalCfdiEmitidos = descargaCfdi.getTotalCfdiGenerados(sucursal.getServidor(), sucursal.getKeyXsa(),
					fechaInicial, fechaFinal);
			if(totalCfdiEmitidos==0) {
				LOG.info("No se encontraron cfdi emitidos de la sucursal: "+sucursal.getNombre()+" con fecha: "+fechaInicial);
				throw new Exception("No se encontraron cfdi emitidos de la sucursal: "+sucursal.getNombre()+" con fecha: "+fechaInicial);
			}
			// totalPageNumber
			int totalPagesNumber = (int) (Math.floor(totalCfdiEmitidos / pageZise));
			// get CFDI por pageNumber
			LOG.info("Total de CFDI a descargar: " +totalCfdiEmitidos);
			for (int x = 0; x <= totalPagesNumber; x++) {
				InputStream zipStream = descargaCfdi.getCfdiZip(fechaInicial, fechaFinal, sucursal.getRfc(),pageZise,x);
				
				zip = File.createTempFile(sucursal.getRfc(),
						"_P".concat(String.valueOf(x) + "_").concat(fechaInicial).concat(".zip"));
				OutputStream out = new BufferedOutputStream(new FileOutputStream(zip));
				copyInputStream(zipStream, out);
				out.close();

				File path = new File(pathArchivos);
				LOG.info("Obteniendo archivos para la sucursal: " + sucursal.getNombre());
				if (zip.length() > 0) {
					unpackArchive(zip, path);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			//LOG.error("Error al momento de ejecucion");
		}
		sucursal.setFechaInicial(calendar.getTime());
		sucursalService.save(sucursal);
	}

	private void unpackArchive(File zip, File path) throws IOException {
		if (!zip.exists()) {
			throw new IOException(zip.getAbsolutePath() + " no existe el archivo");
		}
		if (!path.exists()) {
			throw new IOException("No se puede encontrar el directorio: " + path);
		}

		ZipFile zipFile = new ZipFile(zip);
		for (Enumeration entries = zipFile.entries(); entries.hasMoreElements();) {
			ZipEntry entry = (ZipEntry) entries.nextElement();

			if (entry.getName().contains(".xml") || entry.getName().contains(".XML")) {
				LOG.info("Obteniendo el archivo: " + entry.getName());
				File file = new File(path, File.separator + entry.getName());
				if (!buildDirectory(file.getParentFile())) {
					throw new IOException("No se puede crear el directorio: " + file.getParentFile());
				}
				if (!entry.isDirectory()) {
					copyInputStream(zipFile.getInputStream(entry),
							new BufferedOutputStream(new FileOutputStream(file)));
				} else {
					if (!buildDirectory(file)) {
						throw new IOException("No se puede crear el directorio: " + file);
					}
				}
			}
		}

		zipFile.close();

	}

	private void copyInputStream(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[2048];

		int len = in.read(buffer);
		while (len >= 0) {
			out.write(buffer, 0, len);
			len = in.read(buffer);
		}

		in.close();
		out.close();
	}

	private boolean buildDirectory(File file) {
		return file.exists() || file.mkdir();
	}
}
