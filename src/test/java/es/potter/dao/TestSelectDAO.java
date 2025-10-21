package es.potter.dao;

import es.potter.database.TipoBaseDatos;
import es.potter.model.Alumno;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el método cargarAlumnos de DaoAlumno.
 *
 * Estos tests verifican:
 * - Carga correcta de alumnos desde diferentes bases de datos
 * - Validación de datos cargados
 * - Manejo de bases de datos vacías
 *
 * @author Wara Pacheco
 * @version 1.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestSelectDAO {

    /**
     * Test 1: Cargar alumnos de Gryffindor desde Apache Derby.
     * Verifica que:
     * - Se conecta correctamente a la base de datos
     * - Se cargan los alumnos existentes
     * - Los datos tienen el formato correcto
     */
    @Test
    @Order(1)
    @DisplayName("Cargar alumnos de Gryffindor - Apache Derby")
    void testCargarAlumnosGryffindor() throws ExecutionException, InterruptedException {
        // Arrange
        TipoBaseDatos baseGryffindor = TipoBaseDatos.APACHE_DERBY;

        // Act: Cargar alumnos de Gryffindor
        CompletableFuture<ObservableList<Alumno>> resultado = DaoAlumno.cargarAlumnos(baseGryffindor);
        ObservableList<Alumno> alumnos = resultado.get();

        // Assert: Verificar que se cargaron datos
        assertNotNull(alumnos, "La lista de alumnos no debería ser null");

        System.out.println("\n=== Alumnos de Gryffindor ===");
        System.out.println("Total de alumnos cargados: " + alumnos.size());

        // Si hay alumnos, verificar que tienen el formato correcto
        if (!alumnos.isEmpty()) {
            for (Alumno alumno : alumnos) {
                assertNotNull(alumno.getId(), "El ID no debería ser null");
                assertTrue(alumno.getId().startsWith("GRY"),
                    "El ID debe comenzar con GRY para Gryffindor");
                assertNotNull(alumno.getNombre(), "El nombre no debería ser null");
                assertNotNull(alumno.getApellidos(), "Los apellidos no deberían ser null");
                assertTrue(alumno.getCurso() >= 1 && alumno.getCurso() <= 7,
                    "El curso debe estar entre 1 y 7");

                System.out.println("  - " + alumno.getId() + ": " +
                    alumno.getNombre() + " " + alumno.getApellidos() +
                    " (Curso " + alumno.getCurso() + ")");
            }
        } else {
            System.out.println("  (No hay alumnos en la base de datos)");
        }
    }

    /**
     * Test 2: Cargar alumnos de Slytherin desde HSQLDB.
     * Verifica la carga desde una base de datos diferente.
     */
    @Test
    @Order(2)
    @DisplayName("Cargar alumnos de Slytherin - HSQLDB")
    void testCargarAlumnosSlytherin() throws ExecutionException, InterruptedException {
        // Arrange
        TipoBaseDatos baseSlytherin = TipoBaseDatos.HSQLDB;

        // Act: Cargar alumnos de Slytherin
        CompletableFuture<ObservableList<Alumno>> resultado = DaoAlumno.cargarAlumnos(baseSlytherin);
        ObservableList<Alumno> alumnos = resultado.get();

        // Assert
        assertNotNull(alumnos, "La lista de alumnos no debería ser null");

        System.out.println("\n=== Alumnos de Slytherin ===");
        System.out.println("Total de alumnos cargados: " + alumnos.size());

        // Verificar formato de IDs
        for (Alumno alumno : alumnos) {
            assertTrue(alumno.getId().startsWith("SLY"),
                "El ID debe comenzar con SLY para Slytherin");
            assertEquals("Slytherin", alumno.getCasa(),
                "La casa debe ser Slytherin");

            System.out.println("  - " + alumno.getId() + ": " +
                alumno.getNombre() + " " + alumno.getApellidos());
        }
    }

    /**
     * Test 3: Cargar alumnos de Ravenclaw desde Oracle.
     * Prueba con la base de datos Oracle.
     */
    @Test
    @Order(3)
    @DisplayName("Cargar alumnos de Ravenclaw - Oracle")
    void testCargarAlumnosRavenclaw() throws ExecutionException, InterruptedException {
        // Arrange
        TipoBaseDatos baseRavenclaw = TipoBaseDatos.ORACLE;

        // Act: Cargar alumnos de Ravenclaw
        CompletableFuture<ObservableList<Alumno>> resultado = DaoAlumno.cargarAlumnos(baseRavenclaw);
        ObservableList<Alumno> alumnos = resultado.get();

        // Assert
        assertNotNull(alumnos, "La lista de alumnos no debería ser null");

        System.out.println("\n=== Alumnos de Ravenclaw ===");
        System.out.println("Total de alumnos cargados: " + alumnos.size());

        // Verificar formato de IDs
        for (Alumno alumno : alumnos) {
            assertTrue(alumno.getId().startsWith("RAV"),
                "El ID debe comenzar con RAV para Ravenclaw");
            assertEquals("Ravenclaw", alumno.getCasa(),
                "La casa debe ser Ravenclaw");

            System.out.println("  - " + alumno.getId() + ": " +
                alumno.getNombre() + " " + alumno.getApellidos());
        }
    }

    /**
     * Test 4: Cargar alumnos de Hufflepuff desde H2.
     * Verifica la carga desde H2 Database.
     */
    @Test
    @Order(4)
    @DisplayName("Cargar alumnos de Hufflepuff - H2")
    void testCargarAlumnosHufflepuff() throws ExecutionException, InterruptedException {
        // Arrange
        TipoBaseDatos baseHufflepuff = TipoBaseDatos.H2;

        // Act: Cargar alumnos de Hufflepuff
        CompletableFuture<ObservableList<Alumno>> resultado = DaoAlumno.cargarAlumnos(baseHufflepuff);
        ObservableList<Alumno> alumnos = resultado.get();

        // Assert
        assertNotNull(alumnos, "La lista de alumnos no debería ser null");

        System.out.println("\n=== Alumnos de Hufflepuff ===");
        System.out.println("Total de alumnos cargados: " + alumnos.size());

        // Verificar formato de IDs
        for (Alumno alumno : alumnos) {
            assertTrue(alumno.getId().startsWith("HUF"),
                "El ID debe comenzar con HUF para Hufflepuff");
            assertEquals("Hufflepuff", alumno.getCasa(),
                "La casa debe ser Hufflepuff");

            System.out.println("  - " + alumno.getId() + ": " +
                alumno.getNombre() + " " + alumno.getApellidos());
        }
    }

    /**
     * Test 5: Cargar todas las casas y verificar totales.
     * Carga alumnos de las cuatro casas y muestra estadísticas.
     */
    @Test
    @Order(5)
    @DisplayName("Cargar todas las casas - Estadísticas generales")
    void testCargarTodasLasCasas() throws ExecutionException, InterruptedException {
        System.out.println("\n=== Cargando todas las casas ===");

        // Cargar las cuatro casas en paralelo
        CompletableFuture<ObservableList<Alumno>> futureGryffindor =
            DaoAlumno.cargarAlumnos(TipoBaseDatos.APACHE_DERBY);
        CompletableFuture<ObservableList<Alumno>> futureSlytherin =
            DaoAlumno.cargarAlumnos(TipoBaseDatos.HSQLDB);
        CompletableFuture<ObservableList<Alumno>> futureRavenclaw =
            DaoAlumno.cargarAlumnos(TipoBaseDatos.ORACLE);
        CompletableFuture<ObservableList<Alumno>> futureHufflepuff =
            DaoAlumno.cargarAlumnos(TipoBaseDatos.H2);

        // Esperar a que todas terminen
        CompletableFuture.allOf(futureGryffindor, futureSlytherin,
            futureRavenclaw, futureHufflepuff).get();

        // Obtener resultados
        ObservableList<Alumno> gryffindor = futureGryffindor.get();
        ObservableList<Alumno> slytherin = futureSlytherin.get();
        ObservableList<Alumno> ravenclaw = futureRavenclaw.get();
        ObservableList<Alumno> hufflepuff = futureHufflepuff.get();

        // Calcular totales
        int totalGryffindor = gryffindor.size();
        int totalSlytherin = slytherin.size();
        int totalRavenclaw = ravenclaw.size();
        int totalHufflepuff = hufflepuff.size();
        int totalGeneral = totalGryffindor + totalSlytherin +
            totalRavenclaw + totalHufflepuff;

        // Mostrar estadísticas
        System.out.println("\n--- Estadísticas por Casa ---");
        System.out.println("Gryffindor:  " + totalGryffindor + " alumnos");
        System.out.println("Slytherin:   " + totalSlytherin + " alumnos");
        System.out.println("Ravenclaw:   " + totalRavenclaw + " alumnos");
        System.out.println("Hufflepuff:  " + totalHufflepuff + " alumnos");
        System.out.println("-----------------------------");
        System.out.println("TOTAL:       " + totalGeneral + " alumnos");

        // Verificaciones
        assertTrue(totalGeneral >= 0, "El total debe ser mayor o igual a 0");
        assertNotNull(gryffindor);
        assertNotNull(slytherin);
        assertNotNull(ravenclaw);
        assertNotNull(hufflepuff);
    }

    /**
     * Test 6: Cargar alumnos desde MariaDB.
     * Verifica la carga desde base de datos MariaDB/MySQL.
     */
    @Test
    @Order(6)
    @DisplayName("Cargar alumnos desde MariaDB")
    void testCargarAlumnosMariaDB() throws ExecutionException, InterruptedException {
        // Arrange
        TipoBaseDatos baseMariaDB = TipoBaseDatos.MARIADB;

        // Act: Cargar alumnos desde MariaDB
        CompletableFuture<ObservableList<Alumno>> resultado = DaoAlumno.cargarAlumnos(baseMariaDB);
        ObservableList<Alumno> alumnos = resultado.get();

        // Assert
        assertNotNull(alumnos, "La lista de alumnos no debería ser null");

        System.out.println("\n=== Alumnos en MariaDB ===");
        System.out.println("Total de alumnos cargados: " + alumnos.size());

        // Mostrar todos los alumnos
        for (Alumno alumno : alumnos) {
            assertNotNull(alumno.getId(), "El ID no debería ser null");
            assertNotNull(alumno.getNombre(), "El nombre no debería ser null");

            System.out.println("  - " + alumno.getId() + ": " +
                alumno.getNombre() + " " + alumno.getApellidos() +
                " (" + alumno.getCasa() + ", Curso " + alumno.getCurso() + ")");
        }

        if (alumnos.isEmpty()) {
            System.out.println("  (No hay alumnos en MariaDB)");
        }
    }

    /**
     * Test 7: Cargar alumnos desde SQLite.
     * Verifica la carga desde base de datos embebida SQLite.
     */
    @Test
    @Order(7)
    @DisplayName("Cargar alumnos desde SQLite")
    void testCargarAlumnosSQLite() throws ExecutionException, InterruptedException {
        // Arrange
        TipoBaseDatos baseSQLite = TipoBaseDatos.SQLITE;

        // Act: Cargar alumnos desde SQLite
        CompletableFuture<ObservableList<Alumno>> resultado = DaoAlumno.cargarAlumnos(baseSQLite);
        ObservableList<Alumno> alumnos = resultado.get();

        // Assert
        assertNotNull(alumnos, "La lista de alumnos no debería ser null");

        System.out.println("\n=== Alumnos en SQLite (Base de datos embebida) ===");
        System.out.println("Total de alumnos cargados: " + alumnos.size());

        // Mostrar todos los alumnos agrupados por casa
        long hufflepuff = alumnos.stream().filter(a -> a.getId().startsWith("HUF")).count();
        long ravenclaw = alumnos.stream().filter(a -> a.getId().startsWith("RAV")).count();
        long gryffindor = alumnos.stream().filter(a -> a.getId().startsWith("GRY")).count();
        long slytherin = alumnos.stream().filter(a -> a.getId().startsWith("SLY")).count();

        System.out.println("\nDistribución por casa:");
        System.out.println("  Hufflepuff:  " + hufflepuff + " alumnos");
        System.out.println("  Ravenclaw:   " + ravenclaw + " alumnos");
        System.out.println("  Gryffindor:  " + gryffindor + " alumnos");
        System.out.println("  Slytherin:   " + slytherin + " alumnos");

        // Mostrar algunos ejemplos
        System.out.println("\nPrimeros 5 alumnos:");
        alumnos.stream().limit(5).forEach(alumno ->
            System.out.println("  - " + alumno.getId() + ": " +
                alumno.getNombre() + " " + alumno.getApellidos() +
                " (" + alumno.getCasa() + ")")
        );

        // Verificar que hay datos
        assertTrue(alumnos.size() > 0, "Debería haber al menos un alumno en SQLite");
    }

    /**
     * Test 8: Verificar que los datos cargados tienen todos los campos completos.
     */
    @Test
    @Order(8)
    @DisplayName("Verificar integridad de datos cargados")
    void testIntegridadDatosCargados() throws ExecutionException, InterruptedException {
        // Cargar alumnos de Gryffindor como ejemplo
        CompletableFuture<ObservableList<Alumno>> resultado =
            DaoAlumno.cargarAlumnos(TipoBaseDatos.APACHE_DERBY);
        ObservableList<Alumno> alumnos = resultado.get();

        System.out.println("\n=== Verificando integridad de datos ===");

        for (Alumno alumno : alumnos) {
            // Verificar que todos los campos obligatorios están presentes
            assertNotNull(alumno.getId(), "ID no puede ser null");
            assertFalse(alumno.getId().isEmpty(), "ID no puede estar vacío");

            assertNotNull(alumno.getNombre(), "Nombre no puede ser null");
            assertFalse(alumno.getNombre().isEmpty(), "Nombre no puede estar vacío");

            assertNotNull(alumno.getApellidos(), "Apellidos no pueden ser null");
            assertFalse(alumno.getApellidos().isEmpty(), "Apellidos no pueden estar vacíos");

            assertTrue(alumno.getCurso() >= 1 && alumno.getCurso() <= 7,
                "Curso debe estar entre 1 y 7");

            assertNotNull(alumno.getCasa(), "Casa no puede ser null");

            // Patronus puede ser null o vacío (es opcional)

            System.out.println("✓ Datos completos: " + alumno.getId() +
                " - " + alumno.getNombre() + " " + alumno.getApellidos());
        }

        System.out.println("\nTodos los alumnos tienen datos íntegros (" +
            alumnos.size() + " verificados)");
    }

    /**
     * Resumen final después de todos los tests.
     */
    @AfterAll
    static void resumenFinal() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Tests de cargarAlumnos completados exitosamente");
        System.out.println("Se verificó la carga desde 6 bases de datos:");
        System.out.println("  - Apache Derby (Gryffindor)");
        System.out.println("  - HSQLDB (Slytherin)");
        System.out.println("  - Oracle (Ravenclaw)");
        System.out.println("  - H2 (Hufflepuff)");
        System.out.println("  - MariaDB (Multi-casa)");
        System.out.println("  - SQLite (Embebida)");
        System.out.println("=".repeat(50));
    }
}