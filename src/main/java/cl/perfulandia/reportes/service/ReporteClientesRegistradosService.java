package cl.perfulandia.reportes.service;

// Importa clases necesarias para fechas y listas
import java.time.LocalDateTime;
import java.util.List;

// Importa componentes de Spring para inyección de dependencias y manejo HTTP
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// Importa tus propios DTOs, modelos y repositorios
import cl.perfulandia.reportes.DTO.UsuarioDTO;
import cl.perfulandia.reportes.model.ReporteClientesRegistrados;
import cl.perfulandia.reportes.repository.ReporteClientesRegistradosRepository;

// Marca la clase como un componente de servicio de Spring
@Service
public class ReporteClientesRegistradosService {

    // Inyección del repositorio para acceder y guardar reportes en la base de datos
    @Autowired
    private ReporteClientesRegistradosRepository reporteRepo;

    // Inyección de RestTemplate para hacer peticiones HTTP a otros microservicios
    @Autowired
    private RestTemplate restTemplate;

    // Método principal que genera y guarda un reporte de clientes registrados
    public ReporteClientesRegistrados generarReporteClientesRegistrados() {
        // URL del microservicio de usuarios (ajusta el puerto si tu microservicio de usuarios corre en otro)
        String usuariosServiceUrl = "http://localhost:8080/usuarios";
        
        // Realiza una petición GET para obtener la lista de usuarios como una lista de UsuarioDTO
        ResponseEntity<List<UsuarioDTO>> response = restTemplate.exchange(
                usuariosServiceUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<UsuarioDTO>>() {}
        );
        
        // Extrae la lista de usuarios de la respuesta
        List<UsuarioDTO> usuarios = response.getBody();

        // Cuenta cuántos usuarios tienen el rol "CLIENTE"
        int totalClientes = (int) usuarios.stream()
                .filter(u -> u.getRol() != null && "CLIENTE".equalsIgnoreCase(u.getRol().getNombre()))
                .count();

        // Crea una nueva entidad ReporteClientesRegistrados con la información del reporte
        ReporteClientesRegistrados reporte = new ReporteClientesRegistrados();
        reporte.setTotalClientes(totalClientes); // Asigna la cantidad de clientes encontrados
        reporte.setFechaGeneracion(LocalDateTime.now()); // Asigna la fecha y hora actual
        
        // Guarda el reporte en la base de datos y lo retorna
        return reporteRepo.save(reporte);
    }

    // Método para listar todos los reportes de clientes registrados guardados en la base de datos
    public List<ReporteClientesRegistrados> listarReportes() {
        return reporteRepo.findAll();
    }

    // JPQL: reportes con más de X clientes
    public List<ReporteClientesRegistrados> reportesConMasDe(int minClientes) {
        return reporteRepo.findReportesConMasDe(minClientes);
    }

    // SQl Nativa: últimos N reportes por fecha
    public List<ReporteClientesRegistrados> ultimosNReportes(int n) {
        return reporteRepo.findUltimosNReportes(n);
    }
}
