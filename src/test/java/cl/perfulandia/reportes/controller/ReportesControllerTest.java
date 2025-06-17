package cl.perfulandia.reportes.controller;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.perfulandia.reportes.model.ReporteClientesRegistrados;
import cl.perfulandia.reportes.service.ReporteClientesRegistradosService;

@WebMvcTest(ReportesController.class) // Indica que se está probando el controlador de Reportes
public class ReportesControllerTest {
  @Autowired
    private MockMvc mockMvc; // Permite realizar peticiones HTTP simuladas

    @MockBean
    private ReporteClientesRegistradosService service; // Crea un mock del servicio

    @Autowired
    private ObjectMapper objectMapper; // Convierte objetos Java a JSON

    private ReporteClientesRegistrados reporte;

    @BeforeEach
    void setUp() {
        // Configura un reporte de ejemplo antes de cada prueba
        reporte = new ReporteClientesRegistrados();
        reporte.setId(1L);
        reporte.setTotalClientes(10);
        reporte.setFechaGeneracion(LocalDateTime.now());
    }

    @Test
    public void testGenerarReporte() throws Exception {
        // Define el comportamiento del mock: cuando se llama al servicio, retorna el reporte
        when(service.generarReporteClientesRegistrados()).thenReturn(reporte);

        // Realiza una petición POST a /reportes/clientes-registrados y verifica la respuesta
        mockMvc.perform(post("/reportes/clientes-registrados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalClientes").value(10));
    }

    @Test
    public void testListarReportes() throws Exception {
        // Simula una lista con un solo reporte
        when(service.listarReportes()).thenReturn(List.of(reporte));

        // Realiza una petición GET a /reportes/clientes-registrados
        mockMvc.perform(get("/reportes/clientes-registrados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].totalClientes").value(10));
    }

        @Test
    public void testGetReportesConMasDe() throws Exception {
        // Simula que el servicio devuelve una lista con un reporte
        when(service.reportesConMasDe(5)).thenReturn(List.of(reporte));

        // Realiza una petición GET a /reportes/clientes-registrados/mayor-a/5
        mockMvc.perform(get("/reportes/clientes-registrados/mayor-a/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].totalClientes").value(10));
    }

    @Test
    public void testGetUltimosN() throws Exception {
        // Simula que el servicio devuelve dos reportes
        ReporteClientesRegistrados r2 = new ReporteClientesRegistrados(2L, 8, LocalDateTime.now().minusDays(1));
        when(service.ultimosNReportes(2)).thenReturn(List.of(reporte, r2));

        // Realiza una petición GET a /reportes/clientes-registrados/ultimos/2
        mockMvc.perform(get("/reportes/clientes-registrados/ultimos/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

}
