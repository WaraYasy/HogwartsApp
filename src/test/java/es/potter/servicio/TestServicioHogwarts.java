package es.potter.servicio;

import es.potter.dao.DaoAlumno;
import es.potter.database.TipoBaseDatos;
import es.potter.model.Alumno;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para ServicioHogwarts.
 *
 * IMPORTANTE: Estos tests requieren que las bases de datos estén configuradas
 * y disponibles según el archivo configuration.properties.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestServicioHogwarts {

    private static final Logger logger = LoggerFactory.getLogger(TestServicioHogwarts.class);
    private static final int TIMEOUT_SECONDS = 10;

    private static Alumno alumnoTest;
    private static String idAlumnoTest;

    @BeforeAll
    static void setup() {
        logger.info("=== Iniciando tests de ServicioHogwarts ===");

        // Crear alumno de prueba
        alumnoTest = new Alumno();
        alumnoTest.setNombre("Test");
        alumnoTest.setApellidos("Usuario");
        alumnoTest.setCurso(5);
        alumnoTest.setCasa("Gryffindor");
        alumnoTest.setPatronus("Test Patronus");
    }

    @AfterAll
    static void cleanup() {
        logger.info("=== Tests de ServicioHogwarts completados ===");
    }

    // ==================== TESTS DE CARGA ====================

    @Test
    @Order(1)
    @DisplayName("Test 1: Cargar alumnos desde MASTER")
    void testCargarAlumnos() throws ExecutionException, InterruptedException, TimeoutException {
        logger.info("→ Test: Cargar alumnos desde MASTER");

        CompletableFuture<ObservableList<Alumno>> future = ServicioHogwarts.cargarAlumnos();
        ObservableList<Alumno> alumnos = future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        assertNotNull(alumnos, "La lista de alumnos no debe ser null");
        logger.info("✓ Cargados {} alumnos desde MASTER", alumnos.size());

        // Verificar que hay alumnos
        assertFalse(alumnos.isEmpty(), "Debe haber al menos un alumno en MASTER");

        // Verificar que los alumnos tienen datos válidos
        Alumno primerAlumno = alumnos.get(0);
        assertNotNull(primerAlumno.getId(), "El alumno debe tener ID");
        assertNotNull(primerAlumno.getNombre(), "El alumno debe tener nombre");
        assertNotNull(primerAlumno.getCasa(), "El alumno debe tener casa");

        logger.info("✅ Test cargarAlumnos: OK");
    }

    @Test
    @Order(2)
    @DisplayName("Test 2: Cargar alumnos desde base específica (SQLite)")
    void testCargarAlumnosDesde() throws ExecutionException, InterruptedException, TimeoutException {
        logger.info("→ Test: Cargar alumnos desde SQLite");

        CompletableFuture<ObservableList<Alumno>> future =
                ServicioHogwarts.cargarAlumnosDesde(TipoBaseDatos.SQLITE);
        ObservableList<Alumno> alumnos = future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        assertNotNull(alumnos, "La lista de alumnos no debe ser null");
        logger.info("✓ Cargados {} alumnos desde SQLite", alumnos.size());
        logger.info("✅ Test cargarAlumnosDesde: OK");
    }

    // ==================== TESTS CRUD ====================

    @Test
    @Order(3)
    @DisplayName("Test 3: Crear nuevo alumno")
    void testNuevoAlumno() throws ExecutionException, InterruptedException, TimeoutException {
        logger.info("→ Test: Crear nuevo alumno");

        CompletableFuture<Boolean> future = ServicioHogwarts.nuevoAlumno(alumnoTest);
        Boolean resultado = future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        assertTrue(resultado, "El alumno debe crearse correctamente");
        assertNotNull(alumnoTest.getId(), "El alumno debe tener ID generado");

        idAlumnoTest = alumnoTest.getId();
        logger.info("✓ Alumno creado con ID: {}", idAlumnoTest);

        // Verificar que está en MASTER
        ObservableList<Alumno> alumnosMaster =
                ServicioHogwarts.cargarAlumnos().get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        boolean estaEnMaster = alumnosMaster.stream()
                .anyMatch(a -> a.getId().equals(idAlumnoTest));
        assertTrue(estaEnMaster, "El alumno debe estar en MASTER");

        // Verificar que está en su casa (Gryffindor)
        ObservableList<Alumno> alumnosGryffindor =
                ServicioHogwarts.cargarAlumnosDesde(TipoBaseDatos.GRYFFINDOR)
                        .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        boolean estaEnCasa = alumnosGryffindor.stream()
                .anyMatch(a -> a.getId().equals(idAlumnoTest));
        assertTrue(estaEnCasa, "El alumno debe estar en Gryffindor");

        // Verificar que está en SQLite
        ObservableList<Alumno> alumnosSQLite =
                ServicioHogwarts.cargarAlumnosDesde(TipoBaseDatos.SQLITE)
                        .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        boolean estaEnSQLite = alumnosSQLite.stream()
                .anyMatch(a -> a.getId().equals(idAlumnoTest));
        assertTrue(estaEnSQLite, "El alumno debe estar en SQLite");

        logger.info("✅ Test nuevoAlumno: OK - Alumno en las 3 bases");
    }

    @Test
    @Order(4)
    @DisplayName("Test 4: Modificar alumno existente")
    void testModificarAlumno() throws ExecutionException, InterruptedException, TimeoutException {
        logger.info("→ Test: Modificar alumno");

        assertNotNull(idAlumnoTest, "Debe existir un alumno de prueba");

        // Modificar datos
        Alumno alumnoModificado = new Alumno();
        alumnoModificado.setId(idAlumnoTest);
        alumnoModificado.setNombre("TestModificado");
        alumnoModificado.setApellidos("UsuarioModificado");
        alumnoModificado.setCurso(6);
        alumnoModificado.setCasa("Gryffindor");
        alumnoModificado.setPatronus("Patronus Modificado");

        CompletableFuture<Boolean> future =
                ServicioHogwarts.modificarAlumno(idAlumnoTest, alumnoModificado);
        Boolean resultado = future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        assertTrue(resultado, "El alumno debe modificarse correctamente");
        logger.info("✓ Alumno modificado");

        // Verificar cambios en MASTER
        ObservableList<Alumno> alumnosMaster =
                ServicioHogwarts.cargarAlumnos().get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        Alumno alumnoEnMaster = alumnosMaster.stream()
                .filter(a -> a.getId().equals(idAlumnoTest))
                .findFirst()
                .orElse(null);

        assertNotNull(alumnoEnMaster, "El alumno debe estar en MASTER");
        assertEquals("TestModificado", alumnoEnMaster.getNombre(), "El nombre debe estar modificado");
        assertEquals(6, alumnoEnMaster.getCurso(), "El curso debe estar modificado");

        logger.info("✅ Test modificarAlumno: OK");
    }

    @Test
    @Order(5)
    @DisplayName("Test 5: Eliminar alumno")
    void testEliminarAlumno() throws ExecutionException, InterruptedException, TimeoutException {
        logger.info("→ Test: Eliminar alumno");

        assertNotNull(idAlumnoTest, "Debe existir un alumno de prueba");

        // Buscar el alumno para eliminarlo
        ObservableList<Alumno> alumnos =
                ServicioHogwarts.cargarAlumnos().get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        Alumno alumnoAEliminar = alumnos.stream()
                .filter(a -> a.getId().equals(idAlumnoTest))
                .findFirst()
                .orElse(null);

        assertNotNull(alumnoAEliminar, "El alumno debe existir antes de eliminar");

        CompletableFuture<Boolean> future = ServicioHogwarts.eliminarAlumno(alumnoAEliminar);
        Boolean resultado = future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        assertTrue(resultado, "El alumno debe eliminarse correctamente");
        logger.info("✓ Alumno eliminado");

        // Verificar que NO está en MASTER
        ObservableList<Alumno> alumnosMaster =
                ServicioHogwarts.cargarAlumnos().get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        boolean estaEnMaster = alumnosMaster.stream()
                .anyMatch(a -> a.getId().equals(idAlumnoTest));
        assertFalse(estaEnMaster, "El alumno NO debe estar en MASTER");

        // Verificar que NO está en Gryffindor
        ObservableList<Alumno> alumnosGryffindor =
                ServicioHogwarts.cargarAlumnosDesde(TipoBaseDatos.GRYFFINDOR)
                        .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        boolean estaEnCasa = alumnosGryffindor.stream()
                .anyMatch(a -> a.getId().equals(idAlumnoTest));
        assertFalse(estaEnCasa, "El alumno NO debe estar en Gryffindor");

        // Verificar que NO está en SQLite
        ObservableList<Alumno> alumnosSQLite =
                ServicioHogwarts.cargarAlumnosDesde(TipoBaseDatos.SQLITE)
                        .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        boolean estaEnSQLite = alumnosSQLite.stream()
                .anyMatch(a -> a.getId().equals(idAlumnoTest));
        assertFalse(estaEnSQLite, "El alumno NO debe estar en SQLite");

        logger.info("✅ Test eliminarAlumno: OK - Alumno eliminado de las 3 bases");
    }

    // ==================== TESTS DE SINCRONIZACIÓN ====================

    @Test
    @Order(6)
    @DisplayName("Test 6: Sincronizar todas las bases desde MASTER")
    void testSincronizarDesdeMaster() throws ExecutionException, InterruptedException, TimeoutException {
        logger.info("→ Test: Sincronización completa desde MASTER");

        CompletableFuture<Boolean> future = ServicioHogwarts.sincronizarDesdeMaster();
        Boolean resultado = future.get(30, TimeUnit.SECONDS); // Timeout mayor para sincronización

        assertTrue(resultado, "La sincronización debe completarse correctamente");
        logger.info("✓ Sincronización completa exitosa");

        // Verificar que SQLite tiene todos los alumnos del MASTER
        ObservableList<Alumno> alumnosMaster =
                ServicioHogwarts.cargarAlumnos().get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        ObservableList<Alumno> alumnosSQLite =
                ServicioHogwarts.cargarAlumnosDesde(TipoBaseDatos.SQLITE)
                        .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        assertEquals(alumnosMaster.size(), alumnosSQLite.size(),
                "SQLite debe tener el mismo número de alumnos que MASTER");

        logger.info("✅ Test sincronizarDesdeMaster: OK");
    }

    @Test
    @Order(7)
    @DisplayName("Test 7: Sincronizar base específica (Slytherin)")
    void testSincronizarBaseDesdeMaster() throws ExecutionException, InterruptedException, TimeoutException {
        logger.info("→ Test: Sincronización de Slytherin desde MASTER");

        CompletableFuture<Boolean> future =
                ServicioHogwarts.sincronizarBaseDesdeMaster(TipoBaseDatos.SLYTHERIN);
        Boolean resultado = future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        assertTrue(resultado, "La sincronización de Slytherin debe completarse correctamente");
        logger.info("✓ Slytherin sincronizado");

        // Verificar que Slytherin solo tiene alumnos de Slytherin
        ObservableList<Alumno> alumnosSlytherin =
                ServicioHogwarts.cargarAlumnosDesde(TipoBaseDatos.SLYTHERIN)
                        .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        boolean todosSonSlytherin = alumnosSlytherin.stream()
                .allMatch(a -> "Slytherin".equalsIgnoreCase(a.getCasa()));

        assertTrue(todosSonSlytherin, "Todos los alumnos en Slytherin deben ser de casa Slytherin");
        logger.info("✓ Slytherin contiene {} alumnos (todos de Slytherin)", alumnosSlytherin.size());

        logger.info("✅ Test sincronizarBaseDesdeMaster: OK");
    }

    // ==================== TESTS DE CONSISTENCIA ====================

    @Test
    @Order(8)
    @DisplayName("Test 8: Verificar filtrado por casas")
    void testFiltradoPorCasas() throws ExecutionException, InterruptedException, TimeoutException {
        logger.info("→ Test: Verificar filtrado correcto por casas");

        // Cargar alumnos de cada casa
        ObservableList<Alumno> gryffindor =
                ServicioHogwarts.cargarAlumnosDesde(TipoBaseDatos.GRYFFINDOR)
                        .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        ObservableList<Alumno> slytherin =
                ServicioHogwarts.cargarAlumnosDesde(TipoBaseDatos.SLYTHERIN)
                        .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        ObservableList<Alumno> ravenclaw =
                ServicioHogwarts.cargarAlumnosDesde(TipoBaseDatos.RAVENCLAW)
                        .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        ObservableList<Alumno> hufflepuff =
                ServicioHogwarts.cargarAlumnosDesde(TipoBaseDatos.HUFFLEPUFF)
                        .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        // Verificar que cada casa solo tiene sus alumnos
        assertTrue(gryffindor.stream().allMatch(a -> "Gryffindor".equalsIgnoreCase(a.getCasa())),
                "Gryffindor debe contener solo alumnos de Gryffindor");
        assertTrue(slytherin.stream().allMatch(a -> "Slytherin".equalsIgnoreCase(a.getCasa())),
                "Slytherin debe contener solo alumnos de Slytherin");
        assertTrue(ravenclaw.stream().allMatch(a -> "Ravenclaw".equalsIgnoreCase(a.getCasa())),
                "Ravenclaw debe contener solo alumnos de Ravenclaw");
        assertTrue(hufflepuff.stream().allMatch(a -> "Hufflepuff".equalsIgnoreCase(a.getCasa())),
                "Hufflepuff debe contener solo alumnos de Hufflepuff");

        logger.info("✓ Gryffindor: {} alumnos", gryffindor.size());
        logger.info("✓ Slytherin: {} alumnos", slytherin.size());
        logger.info("✓ Ravenclaw: {} alumnos", ravenclaw.size());
        logger.info("✓ Hufflepuff: {} alumnos", hufflepuff.size());

        // Verificar que la suma de casas = alumnos en SQLite
        ObservableList<Alumno> sqlite =
                ServicioHogwarts.cargarAlumnosDesde(TipoBaseDatos.SQLITE)
                        .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        int totalCasas = gryffindor.size() + slytherin.size() + ravenclaw.size() + hufflepuff.size();
        assertEquals(totalCasas, sqlite.size(),
                "La suma de alumnos de casas debe ser igual al total en SQLite");

        logger.info("✅ Test filtradoPorCasas: OK");
    }

    @Test
    @Order(9)
    @DisplayName("Test 9: Verificar consistencia Master-Slave")
    void testConsistenciaMasterSlave() throws ExecutionException, InterruptedException, TimeoutException {
        logger.info("→ Test: Verificar consistencia entre MASTER y SLAVES");

        // Cargar desde MASTER
        ObservableList<Alumno> master =
                ServicioHogwarts.cargarAlumnos().get(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        // Cargar desde SQLite
        ObservableList<Alumno> sqlite =
                ServicioHogwarts.cargarAlumnosDesde(TipoBaseDatos.SQLITE)
                        .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        assertEquals(master.size(), sqlite.size(),
                "MASTER y SQLite deben tener el mismo número de alumnos");

        // Verificar que todos los IDs del MASTER están en SQLite
        boolean todosEnSQLite = master.stream()
                .allMatch(alumnoMaster ->
                        sqlite.stream().anyMatch(alumnoSQLite ->
                                alumnoSQLite.getId().equals(alumnoMaster.getId())
                        )
                );

        assertTrue(todosEnSQLite, "Todos los alumnos del MASTER deben estar en SQLite");

        logger.info("✅ Test consistenciaMasterSlave: OK - {} alumnos consistentes", master.size());
    }

    // ==================== TESTS DE MANEJO DE ERRORES ====================

    @Test
    @Order(10)
    @DisplayName("Test 10: Intentar crear alumno sin casa válida")
    void testAlumnoSinCasaValida() {
        logger.info("→ Test: Crear alumno sin casa válida");

        Alumno alumnoInvalido = new Alumno();
        alumnoInvalido.setNombre("Test");
        alumnoInvalido.setApellidos("Invalido");
        alumnoInvalido.setCurso(5);
        alumnoInvalido.setCasa("CasaInexistente"); // Casa que no existe
        alumnoInvalido.setPatronus("Test");

        assertThrows(IllegalArgumentException.class, () -> {
            try {
                ServicioHogwarts.nuevoAlumno(alumnoInvalido).get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            } catch (ExecutionException e) {
                throw e.getCause();
            }
        }, "Debe lanzar IllegalArgumentException para casa inválida");

        logger.info("✅ Test alumnoSinCasaValida: OK - Excepción capturada correctamente");
    }
}