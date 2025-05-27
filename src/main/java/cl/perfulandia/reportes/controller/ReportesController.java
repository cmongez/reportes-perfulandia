package cl.perfulandia.reportes.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.perfulandia.reportes.model.ReporteClientesRegistrados;
import cl.perfulandia.reportes.service.ReporteClientesRegistradosService;

@RestController
@RequestMapping("/reportes/clientes-registrados")
public class ReportesController {
    @Autowired
    private ReporteClientesRegistradosService reporteClientesRegistradosService;

    @PostMapping
    public ReporteClientesRegistrados generarReporte() {
        return reporteClientesRegistradosService.generarReporteClientesRegistrados();
    }

    @GetMapping
    public List<ReporteClientesRegistrados> listarReportes() {
        return reporteClientesRegistradosService.listarReportes();
    }

        // GET /reportes/clientes-registrados/mayor-a/{minClientes}
    @GetMapping("/mayor-a/{minClientes}")
    public List<ReporteClientesRegistrados> getReportesConMasDe(@PathVariable int minClientes) {
        return reporteClientesRegistradosService.reportesConMasDe(minClientes);
    }

    // GET /reportes/clientes-registrados/ultimos/{n}
    @GetMapping("/ultimos/{n}")
    public List<ReporteClientesRegistrados> getUltimosN(@PathVariable int n) {
        return reporteClientesRegistradosService.ultimosNReportes(n);
    }
}
