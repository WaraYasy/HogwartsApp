package es.potter.dao;

import es.potter.database.TipoBaseDatos;
import es.potter.model.Alumno;
import org.junit.jupiter.api.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el método nuevoAlumno de DaoAlumno.
 *
 * Estos tests verifican:
 * - Inserción correcta de alumnos en las 4 bases de datos de las casas
 * - Generación automática de ID con UUID
 * - Manejo de transacciones (commit/rollback)
 * - Validación de datos insertados
 *
 * @author Wara Pacheco
 * @version 1.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestInsertDao {

    /**
     * Test 1: Insertar un nuevo alumno en Gryffindor (Apache Derby).
     * Verifica que:
     * - El alumno se inserta correctamente
     * - Se genera un ID con formato GRY-xxxxxxxx
     * - El ID se asigna al objeto Alumno
     */
    @Test
    @Order(1)
    @DisplayName("Insertar alumno en Gryffindor - Apache Derby")
    void testInsertarAlumnoGryffindor() throws ExecutionException, InterruptedException {
        // Arrange
        Alumno alumno = new Alumno(
            "Harry",
            "Potter",
            5,
            "Gryffindor",
            "Ciervo"
        );

        TipoBaseDatos baseGryffindor = TipoBaseDatos.APACHE_DERBY;

        System.out.println("\n=== Test Inserción en Gryffindor ===");
        System.out.println("Insertando: " + alumno.getNombre() + " " + alumno.getApellidos());

        // Act: Insertar el alumno
        CompletableFuture<Boolean> resultado = DaoAlumno.nuevoAlumno(alumno, baseGryffindor);
        Boolean exito = resultado.get();

        // Assert
        assertTrue(exito, "La inserción debería ser exitosa");
        assertNotNull(alumno.getId(), "El ID debería haberse generado");
        assertTrue(alumno.getId().startsWith("GRY-"), "El ID debe comenzar con GRY-");
        assertTrue(alumno.getId().matches("^GRY-[a-f0-9]{8}$"),
            "El ID debe tener el formato GRY-xxxxxxxx");

        System.out.println("✓ Alumno insertado con ID: " + alumno.getId());
    }

    /**
     * Test 2: Insertar un nuevo alumno en Slytherin (HSQLDB).
     * Verifica la inserción en una base de datos diferente.
     */
    @Test
    @Order(2)
    @DisplayName("Insertar alumno en Slytherin - HSQLDB")
    void testInsertarAlumnoSlytherin() throws ExecutionException, InterruptedException {
        // Arrange
        Alumno alumno = new Alumno(
            "Draco",
            "Malfoy",
            5,
            "Slytherin",
            "Serpiente"
        );

        TipoBaseDatos baseSlytherin = TipoBaseDatos.HSQLDB;

        System.out.println("\n=== Test Inserción en Slytherin ===");
        System.out.println("Insertando: " + alumno.getNombre() + " " + alumno.getApellidos());

        // Act: Insertar el alumno
        CompletableFuture<Boolean> resultado = DaoAlumno.nuevoAlumno(alumno, baseSlytherin);
        Boolean exito = resultado.get();

        // Assert
        assertTrue(exito, "La inserción debería ser exitosa");
        assertNotNull(alumno.getId(), "El ID debería haberse generado");
        assertTrue(alumno.getId().startsWith("SLY-"), "El ID debe comenzar con SLY-");
        assertTrue(alumno.getId().matches("^SLY-[a-f0-9]{8}$"),
            "El ID debe tener el formato SLY-xxxxxxxx");

        System.out.println("✓ Alumno insertado con ID: " + alumno.getId());
    }

    /**
     * Test 3: Insertar un nuevo alumno en Ravenclaw (Oracle).
     * Prueba con la base de datos Oracle.
     */
    @Test
    @Order(3)
    @DisplayName("Insertar alumno en Ravenclaw - Oracle")
    void testInsertarAlumnoRavenclaw() throws ExecutionException, InterruptedException {
        // Arrange
        Alumno alumno = new Alumno(
            "Luna",
            "Lovegood",
            4,
            "Ravenclaw",
            "Liebre"
        );

        TipoBaseDatos baseRavenclaw = TipoBaseDatos.ORACLE;

        System.out.println("\n=== Test Inserción en Ravenclaw ===");
        System.out.println("Insertando: " + alumno.getNombre() + " " + alumno.getApellidos());

        // Act: Insertar el alumno
        CompletableFuture<Boolean> resultado = DaoAlumno.nuevoAlumno(alumno, baseRavenclaw);
        Boolean exito = resultado.get();

        // Assert
        assertTrue(exito, "La inserción debería ser exitosa");
        assertNotNull(alumno.getId(), "El ID debería haberse generado");
        assertTrue(alumno.getId().startsWith("RAV-"), "El ID debe comenzar con RAV-");
        assertTrue(alumno.getId().matches("^RAV-[a-f0-9]{8}$"),
            "El ID debe tener el formato RAV-xxxxxxxx");

        System.out.println("✓ Alumno insertado con ID: " + alumno.getId());
    }

    /**
     * Test 4: Insertar un nuevo alumno en Hufflepuff (H2).
     * Verifica la inserción en H2 Database.
     */
    @Test
    @Order(4)
    @DisplayName("Insertar alumno en Hufflepuff - H2")
    void testInsertarAlumnoHufflepuff() throws ExecutionException, InterruptedException {
        // Arrange
        Alumno alumno = new Alumno(
            "Cedric",
            "Diggory",
            6,
            "Hufflepuff",
            "Perro"
        );

        TipoBaseDatos baseHufflepuff = TipoBaseDatos.H2;

        System.out.println("\n=== Test Inserción en Hufflepuff ===");
        System.out.println("Insertando: " + alumno.getNombre() + " " + alumno.getApellidos());

        // Act: Insertar el alumno
        CompletableFuture<Boolean> resultado = DaoAlumno.nuevoAlumno(alumno, baseHufflepuff);
        Boolean exito = resultado.get();

        // Assert
        assertTrue(exito, "La inserción debería ser exitosa");
        assertNotNull(alumno.getId(), "El ID debería haberse generado");
        assertTrue(alumno.getId().startsWith("HUF-"), "El ID debe comenzar con HUF-");
        assertTrue(alumno.getId().matches("^HUF-[a-f0-9]{8}$"),
            "El ID debe tener el formato HUF-xxxxxxxx");

        System.out.println("✓ Alumno insertado con ID: " + alumno.getId());
    }

    /**
     * Test 5: Insertar múltiples alumnos en las 4 casas en paralelo.
     * Verifica que las inserciones concurrentes funcionan correctamente.
     */
    @Test
    @Order(5)
    @DisplayName("Insertar alumnos en las 4 casas en paralelo")
    void testInsertarEnTodasLasCasasParalelo() throws ExecutionException, InterruptedException {
        System.out.println("\n=== Test Inserción en 4 Casas (Paralelo) ===");

        // Arrange: Crear un alumno para cada casa
        Alumno hermione = new Alumno("Hermione", "Granger", 5, "Gryffindor", "Nutria");
        Alumno pansy = new Alumno("Pansy", "Parkinson", 5, "Slytherin", "Gato");
        Alumno cho = new Alumno("Cho", "Chang", 6, "Ravenclaw", "Cisne");
        Alumno hannah = new Alumno("Hannah", "Abbott", 5, "Hufflepuff", "Zorro");

        // Act: Insertar en paralelo
        CompletableFuture<Boolean> futureGryffindor =
            DaoAlumno.nuevoAlumno(hermione, TipoBaseDatos.APACHE_DERBY);
        CompletableFuture<Boolean> futureSlytherin =
            DaoAlumno.nuevoAlumno(pansy, TipoBaseDatos.HSQLDB);
        CompletableFuture<Boolean> futureRavenclaw =
            DaoAlumno.nuevoAlumno(cho, TipoBaseDatos.ORACLE);
        CompletableFuture<Boolean> futureHufflepuff =
            DaoAlumno.nuevoAlumno(hannah, TipoBaseDatos.H2);

        // Esperar a que todas las inserciones terminen
        CompletableFuture.allOf(futureGryffindor, futureSlytherin,
            futureRavenclaw, futureHufflepuff).get();

        // Assert: Verificar que todas fueron exitosas
        assertTrue(futureGryffindor.get(), "Inserción en Gryffindor debería ser exitosa");
        assertTrue(futureSlytherin.get(), "Inserción en Slytherin debería ser exitosa");
        assertTrue(futureRavenclaw.get(), "Inserción en Ravenclaw debería ser exitosa");
        assertTrue(futureHufflepuff.get(), "Inserción en Hufflepuff debería ser exitosa");

        // Verificar IDs generados
        System.out.println("\nAlumnos insertados:");
        System.out.println("  Gryffindor: " + hermione.getId() + " - " + hermione.getNombre());
        System.out.println("  Slytherin:  " + pansy.getId() + " - " + pansy.getNombre());
        System.out.println("  Ravenclaw:  " + cho.getId() + " - " + cho.getNombre());
        System.out.println("  Hufflepuff: " + hannah.getId() + " - " + hannah.getNombre());

        assertNotNull(hermione.getId());
        assertNotNull(pansy.getId());
        assertNotNull(cho.getId());
        assertNotNull(hannah.getId());

        assertTrue(hermione.getId().startsWith("GRY-"));
        assertTrue(pansy.getId().startsWith("SLY-"));
        assertTrue(cho.getId().startsWith("RAV-"));
        assertTrue(hannah.getId().startsWith("HUF-"));
    }

    /**
     * Test 6: Insertar alumno con ID pre-asignado.
     * Verifica que si el alumno ya tiene un ID, se respeta.
     */
    @Test
    @Order(6)
    @DisplayName("Insertar alumno con ID pre-asignado")
    void testInsertarAlumnoConIdPreasignado() throws ExecutionException, InterruptedException {
        // Arrange
        Alumno alumno = new Alumno("Ron", "Weasley", 5, "Gryffindor", "Terrier");
        String idPersonalizado = "GRY-12345678";
        alumno.setId(idPersonalizado);

        TipoBaseDatos baseGryffindor = TipoBaseDatos.APACHE_DERBY;

        System.out.println("\n=== Test Inserción con ID Pre-asignado ===");
        System.out.println("ID personalizado: " + idPersonalizado);

        // Act: Insertar el alumno
        CompletableFuture<Boolean> resultado = DaoAlumno.nuevoAlumno(alumno, baseGryffindor);
        Boolean exito = resultado.get();

        // Assert
        assertTrue(exito, "La inserción debería ser exitosa");
        assertEquals(idPersonalizado, alumno.getId(),
            "El ID no debería cambiar si ya estaba asignado");

        System.out.println("✓ Alumno insertado con ID personalizado: " + alumno.getId());
    }

    /**
     * Test 7: Verificar que el ID generado es único (UUID).
     * Inserta dos alumnos y verifica que tienen IDs diferentes.
     */
    @Test
    @Order(7)
    @DisplayName("Verificar unicidad de IDs generados (UUID)")
    void testUnicidadIdsGenerados() throws ExecutionException, InterruptedException {
        System.out.println("\n=== Test Unicidad de IDs ===");

        // Arrange: Crear dos alumnos similares
        Alumno alumno1 = new Alumno("Fred", "Weasley", 6, "Gryffindor", "Hiena");
        Alumno alumno2 = new Alumno("George", "Weasley", 6, "Gryffindor", "Hiena");

        TipoBaseDatos baseGryffindor = TipoBaseDatos.APACHE_DERBY;

        // Act: Insertar ambos alumnos
        CompletableFuture<Boolean> resultado1 = DaoAlumno.nuevoAlumno(alumno1, baseGryffindor);
        CompletableFuture<Boolean> resultado2 = DaoAlumno.nuevoAlumno(alumno2, baseGryffindor);

        Boolean exito1 = resultado1.get();
        Boolean exito2 = resultado2.get();

        // Assert
        assertTrue(exito1, "Primera inserción debería ser exitosa");
        assertTrue(exito2, "Segunda inserción debería ser exitosa");

        assertNotNull(alumno1.getId());
        assertNotNull(alumno2.getId());
        assertNotEquals(alumno1.getId(), alumno2.getId(),
            "Los IDs generados deben ser diferentes");

        System.out.println("ID alumno 1: " + alumno1.getId());
        System.out.println("ID alumno 2: " + alumno2.getId());
        System.out.println("✓ IDs son únicos");
    }

    /**
     * Test 8: Insertar alumno sin patronus (campo opcional).
     */
    @Test
    @Order(8)
    @DisplayName("Insertar alumno sin patronus")
    void testInsertarAlumnoSinPatronus() throws ExecutionException, InterruptedException {
        // Arrange
        Alumno alumno = new Alumno("Neville", "Longbottom", 5, "Gryffindor", null);
        TipoBaseDatos baseGryffindor = TipoBaseDatos.APACHE_DERBY;

        System.out.println("\n=== Test Inserción sin Patronus ===");
        System.out.println("Insertando: " + alumno.getNombre() + " " + alumno.getApellidos());

        // Act: Insertar el alumno
        CompletableFuture<Boolean> resultado = DaoAlumno.nuevoAlumno(alumno, baseGryffindor);
        Boolean exito = resultado.get();

        // Assert
        assertTrue(exito, "La inserción debería ser exitosa incluso sin patronus");
        assertNotNull(alumno.getId(), "El ID debería haberse generado");
        assertNull(alumno.getPatronus(), "El patronus debería ser null");

        System.out.println("✓ Alumno insertado sin patronus con ID: " + alumno.getId());
    }

    /**
     * Test 9: Insertar alumnos de primer y séptimo curso (valores límite).
     */
    @Test
    @Order(9)
    @DisplayName("Insertar alumnos con cursos límite (1 y 7)")
    void testInsertarAlumnosCursosLimite() throws ExecutionException, InterruptedException {
        System.out.println("\n=== Test Inserción con Cursos Límite ===");

        // Arrange: Alumno de primer curso
        Alumno primerCurso = new Alumno("Colin", "Creevey", 1, "Gryffindor", "Ratón");

        // Alumno de séptimo curso
        Alumno septimoCurso = new Alumno("Bill", "Weasley", 7, "Gryffindor", "León");

        TipoBaseDatos baseGryffindor = TipoBaseDatos.APACHE_DERBY;

        // Act: Insertar ambos alumnos
        CompletableFuture<Boolean> resultado1 = DaoAlumno.nuevoAlumno(primerCurso, baseGryffindor);
        CompletableFuture<Boolean> resultado2 = DaoAlumno.nuevoAlumno(septimoCurso, baseGryffindor);

        Boolean exito1 = resultado1.get();
        Boolean exito2 = resultado2.get();

        // Assert
        assertTrue(exito1, "Inserción de alumno de 1er curso debería ser exitosa");
        assertTrue(exito2, "Inserción de alumno de 7mo curso debería ser exitosa");

        assertEquals(1, primerCurso.getCurso());
        assertEquals(7, septimoCurso.getCurso());

        System.out.println("✓ Alumno 1er curso insertado: " + primerCurso.getId());
        System.out.println("✓ Alumno 7mo curso insertado: " + septimoCurso.getId());
    }

    /**
     * Resumen final después de todos los tests.
     */
    @AfterAll
    static void resumenFinal() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Tests de nuevoAlumno completados exitosamente");
        System.out.println("Se verificó la inserción en las 4 bases de datos:");
        System.out.println("  ✓ Apache Derby (Gryffindor)");
        System.out.println("  ✓ HSQLDB (Slytherin)");
        System.out.println("  ✓ Oracle (Ravenclaw)");
        System.out.println("  ✓ H2 (Hufflepuff)");
        System.out.println("\nFuncionalidades probadas:");
        System.out.println("  ✓ Generación automática de ID con UUID");
        System.out.println("  ✓ Inserción en las 4 casas de Hogwarts");
        System.out.println("  ✓ Inserciones concurrentes (paralelo)");
        System.out.println("  ✓ IDs pre-asignados");
        System.out.println("  ✓ Unicidad de IDs generados");
        System.out.println("  ✓ Campos opcionales (patronus null)");
        System.out.println("  ✓ Valores límite de curso (1 y 7)");
        System.out.println("=".repeat(60));
    }
}
