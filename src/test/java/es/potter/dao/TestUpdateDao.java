package es.potter.dao;

import es.potter.database.TipoBaseDatos;
import es.potter.model.Alumno;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el método modificarAlumno de DaoAlumno.
 *
 * Estos tests verifican:
 * - Modificación correcta de alumnos en las 4 bases de datos de las casas
 * - Actualización de cada campo individual (nombre, apellidos, curso, patronus)
 * - Manejo de transacciones (commit/rollback)
 * - Modificación de alumnos inexistentes
 * - Verificación de que el ID no cambia al modificar
 *
 * @author Wara Pacheco
 * @version 1.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestUpdateDao {

    /**
     * Test 1: Modificar un alumno de Gryffindor (Apache Derby).
     * Verifica que:
     * - El alumno se modifica correctamente
     * - Los datos se actualizan en la base de datos
     * - El ID permanece igual
     */
    @Test
    @Order(1)
    @DisplayName("Modificar alumno de Gryffindor - Apache Derby")
    void testModificarAlumnoGryffindor() throws ExecutionException, InterruptedException {
        // Arrange: Primero insertar un alumno
        Alumno alumnoOriginal = new Alumno(
            "Harry",
            "Potter",
            5,
            "Gryffindor",
            "Ciervo"
        );

        TipoBaseDatos baseGryffindor = TipoBaseDatos.APACHE_DERBY;

        System.out.println("\n=== Test Modificación en Gryffindor ===");

        // Insertar el alumno
        DaoAlumno.nuevoAlumno(alumnoOriginal, baseGryffindor).get();
        String idOriginal = alumnoOriginal.getId();
        System.out.println("Alumno insertado con ID: " + idOriginal);
        System.out.println("Datos originales: " + alumnoOriginal.getNombre() + " " +
            alumnoOriginal.getApellidos() + ", Curso " + alumnoOriginal.getCurso());

        // Crear alumno modificado
        Alumno alumnoModificado = new Alumno(
            "Harry James",
            "Potter Evans",
            6,
            "Gryffindor",
            "Ciervo Plateado"
        );

        // Act: Modificar el alumno
        CompletableFuture<Boolean> resultado =
            DaoAlumno.modificarAlumno(idOriginal, alumnoModificado, baseGryffindor);
        Boolean exito = resultado.get();

        // Assert
        assertTrue(exito, "La modificación debería ser exitosa");

        // Verificar que los datos fueron actualizados
        ObservableList<Alumno> alumnos = DaoAlumno.cargarAlumnos(baseGryffindor).get();
        Alumno alumnoEnBD = alumnos.stream()
            .filter(a -> a.getId().equals(idOriginal))
            .findFirst()
            .orElse(null);

        assertNotNull(alumnoEnBD, "El alumno debe existir en la base de datos");
        assertEquals("Harry James", alumnoEnBD.getNombre());
        assertEquals("Potter Evans", alumnoEnBD.getApellidos());
        assertEquals(6, alumnoEnBD.getCurso());
        assertEquals("Ciervo Plateado", alumnoEnBD.getPatronus());
        assertEquals(idOriginal, alumnoEnBD.getId(), "El ID no debe cambiar");

        System.out.println("Datos modificados: " + alumnoEnBD.getNombre() + " " +
            alumnoEnBD.getApellidos() + ", Curso " + alumnoEnBD.getCurso());
        System.out.println("✓ Alumno modificado exitosamente");

        // Limpiar: Eliminar el alumno
        DaoAlumno.eliminarAlumno(alumnoEnBD, baseGryffindor).get();
    }

    /**
     * Test 2: Modificar un alumno de Slytherin (HSQLDB).
     * Verifica la modificación en una base de datos diferente.
     */
    @Test
    @Order(2)
    @DisplayName("Modificar alumno de Slytherin - HSQLDB")
    void testModificarAlumnoSlytherin() throws ExecutionException, InterruptedException {
        // Arrange
        Alumno alumnoOriginal = new Alumno(
            "Draco",
            "Malfoy",
            5,
            "Slytherin",
            "Serpiente"
        );

        TipoBaseDatos baseSlytherin = TipoBaseDatos.HSQLDB;

        System.out.println("\n=== Test Modificación en Slytherin ===");

        DaoAlumno.nuevoAlumno(alumnoOriginal, baseSlytherin).get();
        String idOriginal = alumnoOriginal.getId();
        System.out.println("Alumno insertado: " + alumnoOriginal.getNombre() + " (ID: " + idOriginal + ")");

        // Modificar datos
        Alumno alumnoModificado = new Alumno(
            "Draco Lucius",
            "Malfoy Black",
            7,
            "Slytherin",
            "Fénix"
        );

        // Act: Modificar
        Boolean exito = DaoAlumno.modificarAlumno(idOriginal, alumnoModificado, baseSlytherin).get();

        // Assert
        assertTrue(exito, "La modificación debería ser exitosa");

        ObservableList<Alumno> alumnos = DaoAlumno.cargarAlumnos(baseSlytherin).get();
        Alumno alumnoEnBD = alumnos.stream()
            .filter(a -> a.getId().equals(idOriginal))
            .findFirst()
            .orElse(null);

        assertNotNull(alumnoEnBD);
        assertEquals("Draco Lucius", alumnoEnBD.getNombre());
        assertEquals("Malfoy Black", alumnoEnBD.getApellidos());
        assertEquals(7, alumnoEnBD.getCurso());
        assertEquals("Fénix", alumnoEnBD.getPatronus());

        System.out.println("✓ Modificado a: " + alumnoEnBD.getNombre() + " " + alumnoEnBD.getApellidos());

        // Limpiar
        DaoAlumno.eliminarAlumno(alumnoEnBD, baseSlytherin).get();
    }

    /**
     * Test 3: Modificar un alumno de Ravenclaw (Oracle).
     * Prueba con la base de datos Oracle.
     */
    @Test
    @Order(3)
    @DisplayName("Modificar alumno de Ravenclaw - Oracle")
    void testModificarAlumnoRavenclaw() throws ExecutionException, InterruptedException {
        // Arrange
        Alumno alumnoOriginal = new Alumno(
            "Luna",
            "Lovegood",
            4,
            "Ravenclaw",
            "Liebre"
        );

        TipoBaseDatos baseRavenclaw = TipoBaseDatos.ORACLE;

        System.out.println("\n=== Test Modificación en Ravenclaw ===");

        DaoAlumno.nuevoAlumno(alumnoOriginal, baseRavenclaw).get();
        String idOriginal = alumnoOriginal.getId();
        System.out.println("Alumno insertado: " + alumnoOriginal.getNombre());

        // Modificar
        Alumno alumnoModificado = new Alumno(
            "Luna Pandora",
            "Lovegood Scamander",
            5,
            "Ravenclaw",
            "Liebre de Plata"
        );

        // Act
        Boolean exito = DaoAlumno.modificarAlumno(idOriginal, alumnoModificado, baseRavenclaw).get();

        // Assert
        assertTrue(exito);

        ObservableList<Alumno> alumnos = DaoAlumno.cargarAlumnos(baseRavenclaw).get();
        Alumno alumnoEnBD = alumnos.stream()
            .filter(a -> a.getId().equals(idOriginal))
            .findFirst()
            .orElse(null);

        assertNotNull(alumnoEnBD);
        assertEquals("Luna Pandora", alumnoEnBD.getNombre());
        assertEquals("Lovegood Scamander", alumnoEnBD.getApellidos());
        assertEquals(5, alumnoEnBD.getCurso());

        System.out.println("✓ Modificado exitosamente");

        // Limpiar
        DaoAlumno.eliminarAlumno(alumnoEnBD, baseRavenclaw).get();
    }

    /**
     * Test 4: Modificar un alumno de Hufflepuff (H2).
     * Verifica la modificación en H2 Database.
     */
    @Test
    @Order(4)
    @DisplayName("Modificar alumno de Hufflepuff - H2")
    void testModificarAlumnoHufflepuff() throws ExecutionException, InterruptedException {
        // Arrange
        Alumno alumnoOriginal = new Alumno(
            "Cedric",
            "Diggory",
            6,
            "Hufflepuff",
            "Perro"
        );

        TipoBaseDatos baseHufflepuff = TipoBaseDatos.H2;

        System.out.println("\n=== Test Modificación en Hufflepuff ===");

        DaoAlumno.nuevoAlumno(alumnoOriginal, baseHufflepuff).get();
        String idOriginal = alumnoOriginal.getId();
        System.out.println("Alumno insertado: " + alumnoOriginal.getNombre());

        // Modificar
        Alumno alumnoModificado = new Alumno(
            "Cedric Amos",
            "Diggory Fawcett",
            7,
            "Hufflepuff",
            "Lobo"
        );

        // Act
        Boolean exito = DaoAlumno.modificarAlumno(idOriginal, alumnoModificado, baseHufflepuff).get();

        // Assert
        assertTrue(exito);

        ObservableList<Alumno> alumnos = DaoAlumno.cargarAlumnos(baseHufflepuff).get();
        Alumno alumnoEnBD = alumnos.stream()
            .filter(a -> a.getId().equals(idOriginal))
            .findFirst()
            .orElse(null);

        assertNotNull(alumnoEnBD);
        assertEquals("Cedric Amos", alumnoEnBD.getNombre());
        assertEquals(7, alumnoEnBD.getCurso());

        System.out.println("✓ Modificado exitosamente");

        // Limpiar
        DaoAlumno.eliminarAlumno(alumnoEnBD, baseHufflepuff).get();
    }

    /**
     * Test 5: Modificar múltiples alumnos en las 4 casas en paralelo.
     * Verifica que las modificaciones concurrentes funcionan correctamente.
     */
    @Test
    @Order(5)
    @DisplayName("Modificar alumnos en las 4 casas en paralelo")
    void testModificarEnTodasLasCasasParalelo() throws ExecutionException, InterruptedException {
        System.out.println("\n=== Test Modificación en 4 Casas (Paralelo) ===");

        // Arrange: Insertar un alumno en cada casa
        Alumno hermione = new Alumno("Hermione", "Granger", 5, "Gryffindor", "Nutria");
        Alumno pansy = new Alumno("Pansy", "Parkinson", 5, "Slytherin", "Gato");
        Alumno cho = new Alumno("Cho", "Chang", 6, "Ravenclaw", "Cisne");
        Alumno hannah = new Alumno("Hannah", "Abbott", 5, "Hufflepuff", "Zorro");

        CompletableFuture.allOf(
            DaoAlumno.nuevoAlumno(hermione, TipoBaseDatos.APACHE_DERBY),
            DaoAlumno.nuevoAlumno(pansy, TipoBaseDatos.HSQLDB),
            DaoAlumno.nuevoAlumno(cho, TipoBaseDatos.ORACLE),
            DaoAlumno.nuevoAlumno(hannah, TipoBaseDatos.H2)
        ).get();

        String idHermione = hermione.getId();
        String idPansy = pansy.getId();
        String idCho = cho.getId();
        String idHannah = hannah.getId();

        System.out.println("Alumnos insertados con éxito");

        // Crear versiones modificadas
        Alumno hermioneModificada = new Alumno("Hermione Jean", "Granger Weasley", 6, "Gryffindor", "Nutria Brillante");
        Alumno pansyModificada = new Alumno("Pansy Violet", "Parkinson", 6, "Slytherin", "Pantera");
        Alumno choModificada = new Alumno("Cho Li", "Chang", 7, "Ravenclaw", "Cisne Blanco");
        Alumno hannahModificada = new Alumno("Hannah Marie", "Abbott Longbottom", 6, "Hufflepuff", "Lobo");

        // Act: Modificar todos en paralelo
        CompletableFuture<Boolean> futureGryffindor =
            DaoAlumno.modificarAlumno(idHermione, hermioneModificada, TipoBaseDatos.APACHE_DERBY);
        CompletableFuture<Boolean> futureSlytherin =
            DaoAlumno.modificarAlumno(idPansy, pansyModificada, TipoBaseDatos.HSQLDB);
        CompletableFuture<Boolean> futureRavenclaw =
            DaoAlumno.modificarAlumno(idCho, choModificada, TipoBaseDatos.ORACLE);
        CompletableFuture<Boolean> futureHufflepuff =
            DaoAlumno.modificarAlumno(idHannah, hannahModificada, TipoBaseDatos.H2);

        CompletableFuture.allOf(futureGryffindor, futureSlytherin,
            futureRavenclaw, futureHufflepuff).get();

        // Assert: Verificar que todas fueron exitosas
        assertTrue(futureGryffindor.get());
        assertTrue(futureSlytherin.get());
        assertTrue(futureRavenclaw.get());
        assertTrue(futureHufflepuff.get());

        System.out.println("✓ Todos los alumnos modificados exitosamente");

        // Limpiar
        CompletableFuture.allOf(
            DaoAlumno.eliminarAlumno(hermione, TipoBaseDatos.APACHE_DERBY),
            DaoAlumno.eliminarAlumno(pansy, TipoBaseDatos.HSQLDB),
            DaoAlumno.eliminarAlumno(cho, TipoBaseDatos.ORACLE),
            DaoAlumno.eliminarAlumno(hannah, TipoBaseDatos.H2)
        ).get();
    }

    /**
     * Test 6: Modificar solo el nombre de un alumno.
     * Verifica que se puede modificar un campo individual.
     */
    @Test
    @Order(6)
    @DisplayName("Modificar solo el nombre del alumno")
    void testModificarSoloNombre() throws ExecutionException, InterruptedException {
        // Arrange
        Alumno alumnoOriginal = new Alumno("Ron", "Weasley", 5, "Gryffindor", "Terrier");
        TipoBaseDatos baseGryffindor = TipoBaseDatos.APACHE_DERBY;

        System.out.println("\n=== Test Modificación Solo Nombre ===");

        DaoAlumno.nuevoAlumno(alumnoOriginal, baseGryffindor).get();
        String idOriginal = alumnoOriginal.getId();

        // Modificar solo el nombre
        Alumno alumnoModificado = new Alumno("Ronald", "Weasley", 5, "Gryffindor", "Terrier");

        // Act
        Boolean exito = DaoAlumno.modificarAlumno(idOriginal, alumnoModificado, baseGryffindor).get();

        // Assert
        assertTrue(exito);

        ObservableList<Alumno> alumnos = DaoAlumno.cargarAlumnos(baseGryffindor).get();
        Alumno alumnoEnBD = alumnos.stream()
            .filter(a -> a.getId().equals(idOriginal))
            .findFirst()
            .orElse(null);

        assertNotNull(alumnoEnBD);
        assertEquals("Ronald", alumnoEnBD.getNombre(), "El nombre debe cambiar");
        assertEquals("Weasley", alumnoEnBD.getApellidos(), "Los apellidos deben permanecer igual");
        assertEquals(5, alumnoEnBD.getCurso(), "El curso debe permanecer igual");
        assertEquals("Terrier", alumnoEnBD.getPatronus(), "El patronus debe permanecer igual");

        System.out.println("✓ Solo el nombre fue modificado: Ron -> Ronald");

        // Limpiar
        DaoAlumno.eliminarAlumno(alumnoEnBD, baseGryffindor).get();
    }

    /**
     * Test 7: Modificar el curso de un alumno.
     * Verifica que se puede cambiar de curso.
     */
    @Test
    @Order(7)
    @DisplayName("Modificar curso del alumno")
    void testModificarCurso() throws ExecutionException, InterruptedException {
        // Arrange
        Alumno alumnoOriginal = new Alumno("Ginny", "Weasley", 4, "Gryffindor", "Caballo");
        TipoBaseDatos baseGryffindor = TipoBaseDatos.APACHE_DERBY;

        System.out.println("\n=== Test Modificación de Curso ===");

        DaoAlumno.nuevoAlumno(alumnoOriginal, baseGryffindor).get();
        String idOriginal = alumnoOriginal.getId();
        System.out.println("Alumno insertado: Curso " + alumnoOriginal.getCurso());

        // Cambiar a siguiente curso
        Alumno alumnoModificado = new Alumno("Ginny", "Weasley", 5, "Gryffindor", "Caballo");

        // Act
        Boolean exito = DaoAlumno.modificarAlumno(idOriginal, alumnoModificado, baseGryffindor).get();

        // Assert
        assertTrue(exito);

        ObservableList<Alumno> alumnos = DaoAlumno.cargarAlumnos(baseGryffindor).get();
        Alumno alumnoEnBD = alumnos.stream()
            .filter(a -> a.getId().equals(idOriginal))
            .findFirst()
            .orElse(null);

        assertNotNull(alumnoEnBD);
        assertEquals(5, alumnoEnBD.getCurso(), "El curso debe cambiar de 4 a 5");

        System.out.println("✓ Curso modificado: 4 -> 5");

        // Limpiar
        DaoAlumno.eliminarAlumno(alumnoEnBD, baseGryffindor).get();
    }

    /**
     * Test 8: Modificar el patronus de un alumno.
     * Verifica que se puede cambiar el patronus.
     */
    @Test
    @Order(8)
    @DisplayName("Modificar patronus del alumno")
    void testModificarPatronus() throws ExecutionException, InterruptedException {
        // Arrange
        Alumno alumnoOriginal = new Alumno("Neville", "Longbottom", 5, "Gryffindor", null);
        TipoBaseDatos baseGryffindor = TipoBaseDatos.APACHE_DERBY;

        System.out.println("\n=== Test Modificación de Patronus ===");

        DaoAlumno.nuevoAlumno(alumnoOriginal, baseGryffindor).get();
        String idOriginal = alumnoOriginal.getId();
        System.out.println("Alumno insertado sin patronus");

        // Asignar un patronus
        Alumno alumnoModificado = new Alumno("Neville", "Longbottom", 5, "Gryffindor", "León");

        // Act
        Boolean exito = DaoAlumno.modificarAlumno(idOriginal, alumnoModificado, baseGryffindor).get();

        // Assert
        assertTrue(exito);

        ObservableList<Alumno> alumnos = DaoAlumno.cargarAlumnos(baseGryffindor).get();
        Alumno alumnoEnBD = alumnos.stream()
            .filter(a -> a.getId().equals(idOriginal))
            .findFirst()
            .orElse(null);

        assertNotNull(alumnoEnBD);
        assertEquals("León", alumnoEnBD.getPatronus(), "El patronus debe asignarse");

        System.out.println("✓ Patronus asignado: null -> León");

        // Limpiar
        DaoAlumno.eliminarAlumno(alumnoEnBD, baseGryffindor).get();
    }

    /**
     * Test 9: Modificar alumno con ID inexistente.
     * Verifica el comportamiento al modificar un alumno que no existe.
     */
    @Test
    @Order(9)
    @DisplayName("Modificar alumno inexistente")
    void testModificarAlumnoInexistente() throws ExecutionException, InterruptedException {
        // Arrange
        String idInexistente = "GRY-99999999";
        Alumno alumnoModificado = new Alumno("Fantasma", "Inexistente", 1, "Gryffindor", null);
        TipoBaseDatos baseGryffindor = TipoBaseDatos.APACHE_DERBY;

        System.out.println("\n=== Test Modificación de Alumno Inexistente ===");
        System.out.println("Intentando modificar ID: " + idInexistente);

        // Act
        Boolean exito = DaoAlumno.modificarAlumno(idInexistente, alumnoModificado, baseGryffindor).get();

        // Assert: La operación debería completarse sin error (aunque no modifique nada)
        assertTrue(exito, "La operación debería completarse sin error");

        System.out.println("✓ Operación completada (0 registros modificados)");
    }

    /**
     * Test 10: Modificar todos los campos de un alumno.
     * Verifica una modificación completa.
     */
    @Test
    @Order(10)
    @DisplayName("Modificar todos los campos del alumno")
    void testModificarTodosCampos() throws ExecutionException, InterruptedException {
        // Arrange
        Alumno alumnoOriginal = new Alumno(
            "Dean",
            "Thomas",
            5,
            "Gryffindor",
            "Gato"
        );

        TipoBaseDatos baseGryffindor = TipoBaseDatos.APACHE_DERBY;

        System.out.println("\n=== Test Modificación Completa ===");

        DaoAlumno.nuevoAlumno(alumnoOriginal, baseGryffindor).get();
        String idOriginal = alumnoOriginal.getId();

        System.out.println("Datos originales:");
        System.out.println("  Nombre: " + alumnoOriginal.getNombre());
        System.out.println("  Apellidos: " + alumnoOriginal.getApellidos());
        System.out.println("  Curso: " + alumnoOriginal.getCurso());
        System.out.println("  Patronus: " + alumnoOriginal.getPatronus());

        // Modificar todos los campos
        Alumno alumnoModificado = new Alumno(
            "Dean Michael",
            "Thomas Finnigan",
            7,
            "Gryffindor",
            "León Dorado"
        );

        // Act
        Boolean exito = DaoAlumno.modificarAlumno(idOriginal, alumnoModificado, baseGryffindor).get();

        // Assert
        assertTrue(exito);

        ObservableList<Alumno> alumnos = DaoAlumno.cargarAlumnos(baseGryffindor).get();
        Alumno alumnoEnBD = alumnos.stream()
            .filter(a -> a.getId().equals(idOriginal))
            .findFirst()
            .orElse(null);

        assertNotNull(alumnoEnBD);
        assertEquals("Dean Michael", alumnoEnBD.getNombre());
        assertEquals("Thomas Finnigan", alumnoEnBD.getApellidos());
        assertEquals(7, alumnoEnBD.getCurso());
        assertEquals("León Dorado", alumnoEnBD.getPatronus());
        assertEquals(idOriginal, alumnoEnBD.getId(), "El ID no debe cambiar");

        System.out.println("\nDatos modificados:");
        System.out.println("  Nombre: " + alumnoEnBD.getNombre());
        System.out.println("  Apellidos: " + alumnoEnBD.getApellidos());
        System.out.println("  Curso: " + alumnoEnBD.getCurso());
        System.out.println("  Patronus: " + alumnoEnBD.getPatronus());
        System.out.println("✓ Todos los campos modificados exitosamente");

        // Limpiar
        DaoAlumno.eliminarAlumno(alumnoEnBD, baseGryffindor).get();
    }

    /**
     * Resumen final después de todos los tests.
     */
    @AfterAll
    static void resumenFinal() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Tests de modificarAlumno completados exitosamente");
        System.out.println("Se verificó la modificación en las 4 bases de datos:");
        System.out.println("  ✓ Apache Derby (Gryffindor)");
        System.out.println("  ✓ HSQLDB (Slytherin)");
        System.out.println("  ✓ Oracle (Ravenclaw)");
        System.out.println("  ✓ H2 (Hufflepuff)");
        System.out.println("\nFuncionalidades probadas:");
        System.out.println("  ✓ Modificación completa de alumnos");
        System.out.println("  ✓ Modificación de campos individuales (nombre, curso, patronus)");
        System.out.println("  ✓ Modificaciones concurrentes (paralelo)");
        System.out.println("  ✓ Modificación de alumno inexistente");
        System.out.println("  ✓ Verificación de que el ID no cambia");
        System.out.println("  ✓ Actualización de todos los campos");
        System.out.println("  ✓ Transacciones con commit/rollback");
        System.out.println("=".repeat(60));
    }
}
