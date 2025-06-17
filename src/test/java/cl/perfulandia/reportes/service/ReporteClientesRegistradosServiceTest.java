package cl.perfulandia.reportes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import cl.perfulandia.reportes.DTO.RolDTO;
import cl.perfulandia.reportes.DTO.UsuarioDTO;
import cl.perfulandia.reportes.model.ReporteClientesRegistrados;
import cl.perfulandia.reportes.repository.ReporteClientesRegistradosRepository;

@SpringBootTest
public class ReporteClientesRegistradosServiceTest {
@Autowired
    private ReporteClientesRegistradosService service; // Inyecta el servicio que se va a probar

    @MockBean
    private ReporteClientesRegistradosRepository repository; // Crea un mock del repositorio

    @MockBean
    private RestTemplate restTemplate; // Crea un mock del RestTemplate usado para comunicarse con otros microservicios

    private UsuarioDTO cliente;
    private UsuarioDTO admin;

    
    @BeforeEach
    void setUp() {
        // Crea un usuario con rol CLIENTE
        cliente = new UsuarioDTO();
        cliente.setRol(new RolDTO(1L, "CLIENTE"));

        // Crea un usuario con rol ADMIN
        admin = new UsuarioDTO();
        admin.setRol(new RolDTO(2L, "ADMIN"));
    }
@Test
public void testGenerarReporteClientesRegistrados() {
    // Simula la respuesta del microservicio de usuarios con 2 clientes y 1 admin
    List<UsuarioDTO> mockUsuarios = List.of(cliente, admin, cliente);
    ResponseEntity<List<UsuarioDTO>> responseEntity = ResponseEntity.ok(mockUsuarios);

    // Crea una instancia explícita del tipo para evitar el warning de tipo genérico
    ParameterizedTypeReference<List<UsuarioDTO>> tipoRespuesta =
        new ParameterizedTypeReference<List<UsuarioDTO>>() {};

    // Define el comportamiento del mock: RestTemplate devuelve la lista simulada
    when(restTemplate.exchange(
            eq("http://localhost:8080/usuarios"),
            eq(HttpMethod.GET),
            isNull(),
            eq(tipoRespuesta)
    )).thenReturn(responseEntity);

    // Define que al guardar un reporte, se devuelve el mismo objeto sin modificaciones
    when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    // Llama al método que se quiere probar
    ReporteClientesRegistrados reporte = service.generarReporteClientesRegistrados();

    // Verifica que el reporte no sea nulo
    assertNotNull(reporte);
    // Verifica que el total de clientes contados sea 2
    assertEquals(2, reporte.getTotalClientes());
    // Verifica que la fecha de generación haya sido asignada
    assertNotNull(reporte.getFechaGeneracion());
}

    @Test
    public void testReportesConMasDe() {
        // Crea un reporte ficticio con 20 clientes
        ReporteClientesRegistrados reporte = new ReporteClientesRegistrados(1L, 20, LocalDateTime.now());

        // Define que el repositorio devuelve el reporte cuando se consulta con mínimo 10 clientes
        when(repository.findReportesConMasDe(10)).thenReturn(List.of(reporte));

        // Llama al método que se quiere probar
        List<ReporteClientesRegistrados> resultado = service.reportesConMasDe(10);

        // Verifica que se devuelva una lista con un elemento
        assertEquals(1, resultado.size());
        // Verifica que el total de clientes sea 20
        assertEquals(20, resultado.get(0).getTotalClientes());
    }

    @Test
    public void testUltimosNReportes() {
        // Crea dos reportes ficticios
        ReporteClientesRegistrados reporte1 = new ReporteClientesRegistrados(1L, 10, LocalDateTime.now());
        ReporteClientesRegistrados reporte2 = new ReporteClientesRegistrados(2L, 15, LocalDateTime.now().minusDays(1));

        // Define que el repositorio devuelva estos reportes al llamar a findUltimosNReportes
        when(repository.findUltimosNReportes(2)).thenReturn(List.of(reporte1, reporte2));

        // Llama al método que se desea probar
        List<ReporteClientesRegistrados> resultado = service.ultimosNReportes(2);

        // Verifica que se devuelvan exactamente dos reportes
        assertEquals(2, resultado.size());
    }
}
