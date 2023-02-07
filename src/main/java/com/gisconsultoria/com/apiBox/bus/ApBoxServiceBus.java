package com.gisconsultoria.com.apiBox.bus;

import com.gisconsultoria.com.apiBox.model.Sucursal;
import com.gisconsultoria.com.apiBox.service.ISucursalService;
import com.gisconsultoria.com.apiBox.services.ApBoxXsaService;
import com.gisconsultoria.com.apiBox.utils.IApBoxReadXmlFile;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Luis Enrique Morales Soriano
 */
@Configuration
@EnableScheduling
public class ApBoxServiceBus {

	protected final Logger LOG = Logger.getLogger(ApBoxServiceBus.class.getName());

	@Autowired
	private ISucursalService sucursalService;

	@Autowired
	private ApBoxXsaService apBoxXsaService;

	@Value("${path.archivo.archivosXml}")
	private File path;

	@Autowired
	private IApBoxReadXmlFile apBoxReadXmlFile;
/*NOTA: ACTIVAR EL REGIMEN FISCAL DEL CLIENTE PARA LA VERSION 4.0*/
	// activar a las media noche (0 0 0 * * *)
	 @Scheduled(cron = "05 05 15 * * *", zone = "America/Mexico_City")
	//@Scheduled(cron = "0 0 0 * * *", zone = "America/Mexico_City")
	public void executeServiceBus() {

		List<Sucursal> sucursales = sucursalService.getActiveSucursales();
		ZoneId zoneId = ZoneId.of("America/Mexico_City");
		Date fechaActual = Date.from(ZonedDateTime.now(zoneId).toInstant());
		Calendar calendar = Calendar.getInstance();

		if (sucursales != null) {
			try {
				LOG.info("DESCARGANDO ARCHIVOS DE LA API");
				/*for (Sucursal sucursal : sucursales) {
					/*
					 * //solo descarrga factura de una empresa por faltante
					 * if(sucursal.getRfc().equals("SLO030415G77")) {
					 * apBoxXsaService.ObtenerArchivos(sucursal.getId(),
					 * sucursal.getFechaInicial()); }
					 */
					/*if (fechaActual.after(sucursal.getFechaInicial())) {
						LocalDate fechaInicial = sucursal.getFechaInicial().toInstant().atZone(zoneId).toLocalDate();
						LocalDate fechaFinal = fechaActual.toInstant().atZone(zoneId).toLocalDate();

						long totalDias = ChronoUnit.DAYS.between(fechaFinal, fechaInicial);
						calendar.setTime(sucursal.getFechaInicial());
						for (long dias = 0; totalDias < dias; totalDias++) {
							calendar.add(Calendar.DAY_OF_MONTH, (int) dias);
							Sucursal readSucursal = sucursalService.findById(sucursal.getId());
							LocalDate fechaInicial2 = readSucursal.getFechaInicial().toInstant().atZone(zoneId)
									.toLocalDate();

							if (fechaInicial.compareTo(fechaInicial2) < 0) {
								apBoxXsaService.ObtenerArchivos(readSucursal.getId(), readSucursal.getFechaInicial());
							} else {
								apBoxXsaService.ObtenerArchivos(sucursal.getId(), sucursal.getFechaInicial());

							}
						}
					}
				}*/

				File[] lista = path.listFiles();
				LOG.info("TOTAL CFDI DESCARGADOS EN LA CARPETA : " + lista.length);

				LOG.info("Obteniendo archivos extraidos de todas las sucursales");
				apBoxReadXmlFile.readXmlFile(path);

				LOG.info("FINALIZA LA EJECUCIÓN CON FECHA: " + fechaActual);

			} catch (IOException exc) {
				LOG.error("Error al momento de la ejecución: " + exc.getMessage());
			}
		} else {
			LOG.error("No existen sucursales dentro de la Base de datos");
		}

	}

}
