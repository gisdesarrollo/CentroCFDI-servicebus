package com.gisconsultoria.com.apiBox.model.enums;

/**
 * @author Luis Enrique Morales Soriano
 */
public enum RegimenFiscalEnum {

    GeneralDeLeyPersonasMorales 										(601),
    PersonasMoralesConFinesNoLucrativos 								(603),
    SueldosYSalariosEIngresosAsimiladosASalarios 						(605),
    Arrendamiento 														(606),
    RegimenDeEnajenacionOAdquisicionDeBienes 							(607),
    DemasIngresos 														(608),
    Consolidacion 														(609),
    ResidentesEnElExtranjeroSinEstablecimientoPermanenteEnMexico 		(610),
    IngresosPorDividendosSociosYAccionistas 							(611),
    PersonasFisicasConActividadesEmpresarialesYProfesionales 			(612),
    IngresosPorIntereses 												(614),
    RegimenDeLosIngresosPorObtencionDePremios 							(615),
    SinObligacionesFiscales 											(616),
    SociedadesCooperativasDeProduccionQueOptanPorDiferirSusIngresos 	(620),
    IncorporacionFiscal 												(621),
    ActividadesAgricolasGanaderasSilvicolasYPesqueras 					(622),
    OpcionalParaGruposDeSociedades 										(623),
    Coordinados 														(624),
    RégimenDeLasActividadesEmpresarialesConIngresosATravésDePlataformasTecnológicas (625),
    RégimenSimplificadoDeConfianza                                      (626),
    Hidrocarburos 														(628),
    DeLosRegimenesFiscalesPreferentesYDeLasEmpresasMultinacionales 		(629),
    EnajenacionDeAccionesEnBolsaDeValores 								(630);

    public final int number;

    private RegimenFiscalEnum(int number){
        this.number = number;
    }

}
