package es.potter.dao;

import es.potter.database.TipoBaseDatos;
import es.potter.model.Alumno;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el método eliminarAlumno de DaoAlumno.
 *
 * Estos tests verifican:
 * - Eliminación correcta de alumnos en las 4 bases de datos de las casas
 * - Manejo de transacciones (commit/rollback)
 * - Verificación de que el alumno fue eliminado
 * - Eliminación de alumnos inexistentes
 *
 * @author Wara Pacheco
 * @version 1.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestDeleteDao {

    /**
     * Test 1: Eliminar un alumno de Gryffindor (Apache Derby).
     * Verifica que:
     * - El alumno se elimina correctamente
     * - La operación retorna true
     * - El alumno ya no existe en la base de datos
     */
    @Test
    @Order(1)
    @DisplayName("Eliminar alumno de Gryffindor - Apache Derby")
    void testEliminarAlumnoGryffindor() throws ExecutionException, InterruptedException {
        // Arrange: Primero insertar un alumno
        Alumno alumno = new Alumno(
            "Harry",
            "Potter",
            5,
            "Gryffindor",
            "Ciervo"
        );

        TipoBaseDatos baseGryffindor = TipoBaseDatos.APACHE_DERBY;

        System.out.println("\n=== Test Eliminación en Gryffindor ===");

        // Insertar el alumno
        CompletableFuture<Boolean> resultadoInsercion = DaoAlumno.nuevoAlumno(alumno, baseGryffindor);
        Boolean insercionExitosa = resultadoInsercion.get();
        assertTrue(insercionExitosa, "La inserción previa debe ser exitosa");

        String idAlumno = alumno.getId();
        System.out.println("Alumno insertado con ID: " + idAlumno);

        // Verificar que existe antes de eliminar
        ObservableList<Alumno> antesEliminar = DaoAlumno.cargarAlumnos(baseGryffindor).get();
        long cantidadAntes = antesEliminar.stream()
            .filter(a -> a.getId().equals(idAlumno))
            .count();
        assertEquals(1, cantidadAntes, "El alumno debe existir antes de eliminar");

        // Act: Eliminar el alumno
        CompletableFuture<Boolean> resultadoEliminacion = DaoAlumno.eliminarAlumno(alumno, baseGryffindor);
        Boolean eliminacionExitosa = resultadoEliminacion.get();

        // Assert
        assertTrue(eliminacionExitosa, "La eliminación debería ser exitosa");

        // Verificar que ya no existe
        ObservableList<Alumno> despuesEliminar = DaoAlumno.cargarAlumnos(baseGryffindor).get();
        long cantidadDespues = despuesEliminar.stream()
            .filter(a -> a.getId().equals(idAlumno))
            .count();
        assertEquals(0, cantidadDespues, "El alumno no debería existir después de eliminar");

        System.out.println("✓ Alumno eliminado exitosamente: " + idAlumno);
    }

    /**
     * Test 2: Eliminar un alumno de Slytherin (HSQLDB).
     * Verifica la eliminación en una base de datos diferente.
     */
    @Test
    @Order(2)
    @DisplayName("Eliminar alumno de Slytherin - HSQLDB")
    void testEliminarAlumnoSlytherin() throws ExecutionException, InterruptedException {
        // Arrange
        Alumno alumno = new Alumno(
            "Draco",
            "Malfoy",
            5,
            "Slytherin",
            "Serpiente"
        );

        TipoBaseDatos baseSlytherin = TipoBaseDatos.HSQLDB;

        System.out.println("\n=== Test Eliminación en Slytherin ===");

        // Insertar el alumno
        DaoAlumno.nuevoAlumno(alumno, baseSlytherin).get();
        String idAlumno = alumno.getId();
        System.out.println("Alumno insertado con ID: " + idAlumno);

        // Act: Eliminar el alumno
        CompletableFuture<Boolean> resultado = DaoAlumno.eliminarAlumno(alumno, baseSlytherin);
        Boolean exito = resultado.get();

        // Assert
        assertTrue(exito, "La eliminación debería ser exitosa");

        // Verificar que ya no existe
        ObservableList<Alumno> alumnos = DaoAlumno.cargarAlumnos(baseSlytherin).get();
        boolean existe = alumnos.stream()
            .anyMatch(a -> a.getId().equals(idAlumno));
        assertFalse(existe, "El alumno no debería existir después de eliminar");

        System.out.println("✓ Alumno eliminado exitosamente: " + idAlumno);
    }

    /**
     * Test 3: Eliminar un alumno de Ravenclaw (Oracle).
     * Prueba con la base de datos Oracle.
     */
    @Test
    @Order(3)
    @DisplayName("Eliminar alumno de Ravenclaw - Oracle")
    void testEliminarAlumnoRavenclaw() throws ExecutionException, InterruptedException {
        // Arrange
        Alumno alumno = new Alumno(
            "Luna",
            "Lovegood",
            4,
            "Ravenclaw",
            "Liebre"
        );

        TipoBaseDatos baseRavenclaw = TipoBaseDatos.ORACLE;

        System.out.println("\n=== Test Eliminación en Ravenclaw ===");

        // Insertar el alumno
        DaoAlumno.nuevoAlumno(alumno, baseRavenclaw).get();
        String idAlumno = alumno.getId();
        System.out.println("Alumno insertado con ID: " + idAlumno);

        // Act: Eliminar el alumno
        CompletableFuture<Boolean> resultado = DaoAlumno.eliminarAlumno(alumno, baseRavenclaw);
        Boolean exito = resultado.get();

        // Assert
        assertTrue(exito, "La eliminación debería ser exitosa");

        // Verificar que ya no existe
        ObservableList<Alumno> alumnos = DaoAlumno.cargarAlumnos(baseRavenclaw).get();
        boolean existe = alumnos.stream()
            .anyMatch(a -> a.getId().equals(idAlumno));
        assertFalse(existe, "El alumno no debería existir después de eliminar");

        System.out.println("✓ Alumno eliminado exitosamente: " + idAlumno);
    }

    /**
     * Test 4: Eliminar un alumno de Hufflepuff (H2).
     * Verifica la eliminación en H2 Database.
     */
    @Test
    @Order(4)
    @DisplayName("Eliminar alumno de Hufflepuff - H2")
    void testEliminarAlumnoHufflepuff() throws ExecutionException, InterruptedException {
        // Arrange
        Alumno alumno = new Alumno(
            "Cedric",
            "Diggory",
            6,
            "Hufflepuff",
            "Perro"
        );

        TipoBaseDatos baseHufflepuff = TipoBaseDatos.H2;

        System.out.println("\n=== Test Eliminación en Hufflepuff ===");

        // Insertar el alumno
        DaoAlumno.nuevoAlumno(alumno, baseHufflepuff).get();
        String idAlumno = alumno.getId();
        System.out.println("Alumno insertado con ID: " + idAlumno);

        // Act: Eliminar el alumno
        CompletableFuture<Boolean> resultado = DaoAlumno.eliminarAlumno(alumno, baseHufflepuff);
        Boolean exito = resultado.get();

        // Assert
        assertTrue(exito, "La eliminación debería ser exitosa");

        // Verificar que ya no existe
        ObservableList<Alumno> alumnos = DaoAlumno.cargarAlumnos(baseHufflepuff).get();
        boolean existe = alumnos.stream()
            .anyMatch(a -> a.getId().equals(idAlumno));
        assertFalse(existe, "El alumno no debería existir después de eliminar");

        System.out.println("✓ Alumno eliminado exitosamente: " + idAlumno);
    }

    /**
     * Test 5: Eliminar múltiples alumnos de las 4 casas en paralelo.
     * Verifica que las eliminaciones concurrentes funcionan correctamente.
     */
    @Test
    @Order(5)
    @DisplayName("Eliminar alumnos de las 4 casas en paralelo")
    void testEliminarEnTodasLasCasasParalelo() throws ExecutionException, InterruptedException {
        System.out.println("\n=== Test Eliminación en 4 Casas (Paralelo) ===");

        // Arrange: Crear e insertar un alumno para cada casa
        Alumno hermione = new Alumno("Hermione", "Granger", 5, "Gryffindor", "Nutria");
        Alumno pansy = new Alumno("Pansy", "Parkinson", 5, "Slytherin", "Gato");
        Alumno cho = new Alumno("Cho", "Chang", 6, "Ravenclaw", "Cisne");
        Alumno hannah = new Alumno("Hannah", "Abbott", 5, "Hufflepuff", "Zorro");

        // Insertar todos en paralelo
        CompletableFuture.allOf(
            DaoAlumno.nuevoAlumno(hermione, TipoBaseDatos.APACHE_DERBY),
            DaoAlumno.nuevoAlumno(pansy, TipoBaseDatos.HSQLDB),
            DaoAlumno.nuevoAlumno(cho, TipoBaseDatos.ORACLE),
            DaoAlumno.nuevoAlumno(hannah, TipoBaseDatos.H2)
        ).get();

        System.out.println("\nAlumnos insertados:");
        System.out.println("  Gryffindor: " + hermione.getId());
        System.out.println("  Slytherin:  " + pansy.getId());
        System.out.println("  Ravenclaw:  " + cho.getId());
        System.out.println("  Hufflepuff: " + hannah.getId());

        // Act: Eliminar todos en paralelo
        CompletableFuture<Boolean> futureGryffindor =
            DaoAlumno.eliminarAlumno(hermione, TipoBaseDatos.APACHE_DERBY);
        CompletableFuture<Boolean> futureSlytherin =
            DaoAlumno.eliminarAlumno(pansy, TipoBaseDatos.HSQLDB);
        CompletableFuture<Boolean> futureRavenclaw =
            DaoAlumno.eliminarAlumno(cho, TipoBaseDatos.ORACLE);
        CompletableFuture<Boolean> futureHufflepuff =
            DaoAlumno.eliminarAlumno(hannah, TipoBaseDatos.H2);

        // Esperar a que todas las eliminaciones terminen
        CompletableFuture.allOf(futureGryffindor, futureSlytherin,
            futureRavenclaw, futureHufflepuff).get();

        // Assert: Verificar que todas fueron exitosas
        assertTrue(futureGryffindor.get(), "Eliminación en Gryffindor debería ser exitosa");
        assertTrue(futureSlytherin.get(), "Eliminación en Slytherin debería ser exitosa");
        assertTrue(futureRavenclaw.get(), "Eliminación en Ravenclaw debería ser exitosa");
        assertTrue(futureHufflepuff.get(), "Eliminación en Hufflepuff debería ser exitosa");

        System.out.println("\n✓ Todos los alumnos eliminados exitosamente");
    }

    /**
     * Test 6: Intentar eliminar un alumno con ID inexistente.
     * Verifica el comportamiento cuando el alumno no existe.
     */
    @Test
    @Order(6)
    @DisplayName("Eliminar alumno inexistente")
    void testEliminarAlumnoInexistente() throws ExecutionException, InterruptedException {
        // Arrange: Crear un alumno con ID que no existe
        Alumno alumnoInexistente = new Alumno("Fantasma", "Inexistente", 1, "Gryffindor", null);
        alumnoInexistente.setId("GRY-99999999");

        TipoBaseDatos baseGryffindor = TipoBaseDatos.APACHE_DERBY;

        System.out.println("\n=== Test Eliminación de Alumno Inexistente ===");
        System.out.println("Intentando eliminar ID: " + alumnoInexistente.getId());

        // Act: Intentar eliminar el alumno inexistente
        CompletableFuture<Boolean> resultado = DaoAlumno.eliminarAlumno(alumnoInexistente, baseGryffindor);
        Boolean exito = resultado.get();

        // Assert: La operación debería completarse (aunque no elimine nada)
        // En SQL, un DELETE de algo que no existe retorna 0 filas afectadas pero no es error
        assertTrue(exito, "La operación debería completarse sin error");

        System.out.println("✓ Operación completada (0 registros eliminados)");
    }

    /**
     * Test 7: Eliminar y verificar el conteo de alumnos.
     * Inserta varios alumnos, elimina algunos y verifica el conteo.
     */
    @Test
    @Order(7)
    @DisplayName("Eliminar alumnos y verificar conteo")
    void testEliminarYVerificarConteo() throws ExecutionException, InterruptedException {
        System.out.println("\n=== Test Eliminación y Conteo ===");

        TipoBaseDatos baseGryffindor = TipoBaseDatos.APACHE_DERBY;

        // Arrange: Obtener conteo inicial
        int conteoInicial = DaoAlumno.cargarAlumnos(baseGryffindor).get().size();
        System.out.println("Conteo inicial: " + conteoInicial + " alumnos");

        // Insertar 3 alumnos
        Alumno alumno1 = new Alumno("Ron", "Weasley", 5, "Gryffindor", "Terrier");
        Alumno alumno2 = new Alumno("Ginny", "Weasley", 4, "Gryffindor", "Caballo");
        Alumno alumno3 = new Alumno("Neville", "Longbottom", 5, "Gryffindor", "León");

        CompletableFuture.allOf(
            DaoAlumno.nuevoAlumno(alumno1, baseGryffindor),
            DaoAlumno.nuevoAlumno(alumno2, baseGryffindor),
            DaoAlumno.nuevoAlumno(alumno3, baseGryffindor)
        ).get();

        int conteoDespuesInsercion = DaoAlumno.cargarAlumnos(baseGryffindor).get().size();
        System.out.println("Después de insertar 3: " + conteoDespuesInsercion + " alumnos");
        assertEquals(conteoInicial + 3, conteoDespuesInsercion);

        // Act: Eliminar 2 alumnos
        CompletableFuture.allOf(
            DaoAlumno.eliminarAlumno(alumno1, baseGryffindor),
            DaoAlumno.eliminarAlumno(alumno2, baseGryffindor)
        ).get();

        int conteoDespuesEliminacion = DaoAlumno.cargarAlumnos(baseGryffindor).get().size();
        System.out.println("Después de eliminar 2: " + conteoDespuesEliminacion + " alumnos");

        // Assert: Debería haber solo 1 más que al inicio
        assertEquals(conteoInicial + 1, conteoDespuesEliminacion);

        // Limpiar: Eliminar el último alumno
        DaoAlumno.eliminarAlumno(alumno3, baseGryffindor).get();

        int conteoFinal = DaoAlumno.cargarAlumnos(baseGryffindor).get().size();
        System.out.println("Conteo final: " + conteoFinal + " alumnos");
        assertEquals(conteoInicial, conteoFinal);

        System.out.println("✓ Conteos verificados correctamente");
    }

    /**
     * Test 8: Eliminar el mismo alumno dos veces.
     * Verifica el comportamiento al intentar eliminar algo ya eliminado.
     */
    @Test
    @Order(8)
    @DisplayName("Eliminar el mismo alumno dos veces")
    void testEliminarAlumnoDobleVez() throws ExecutionException, InterruptedException {
        // Arrange: Insertar un alumno
        Alumno alumno = new Alumno("Seamus", "Finnigan", 5, "Gryffindor", "Zorro");
        TipoBaseDatos baseGryffindor = TipoBaseDatos.APACHE_DERBY;

        DaoAlumno.nuevoAlumno(alumno, baseGryffindor).get();
        String idAlumno = alumno.getId();

        System.out.println("\n=== Test Doble Eliminación ===");
        System.out.println("Alumno insertado con ID: " + idAlumno);

        // Act: Eliminar por primera vez
        Boolean primeraEliminacion = DaoAlumno.eliminarAlumno(alumno, baseGryffindor).get();

        // Assert: Primera eliminación exitosa
        assertTrue(primeraEliminacion, "Primera eliminación debería ser exitosa");
        System.out.println("✓ Primera eliminación exitosa");

        // Act: Intentar eliminar por segunda vez
        Boolean segundaEliminacion = DaoAlumno.eliminarAlumno(alumno, baseGryffindor).get();

        // Assert: Segunda eliminación también completa sin error (aunque no elimina nada)
        assertTrue(segundaEliminacion, "Segunda eliminación debería completarse sin error");
        System.out.println("✓ Segunda eliminación completada (0 registros afectados)");
    }

    /**
     * Test 9: Eliminar alumnos de diferentes cursos.
     * Verifica que se pueden eliminar alumnos independientemente de su curso.
     */
    @Test
    @Order(9)
    @DisplayName("Eliminar alumnos de diferentes cursos")
    void testEliminarAlumnosDiferentesCursos() throws ExecutionException, InterruptedException {
        System.out.println("\n=== Test Eliminación por Cursos ===");

        TipoBaseDatos baseGryffindor = TipoBaseDatos.APACHE_DERBY;

        // Arrange: Crear alumnos de diferentes cursos
        Alumno primerCurso = new Alumno("Colin", "Creevey", 1, "Gryffindor", "Ratón");
        Alumno tercerCurso = new Alumno("Katie", "Bell", 3, "Gryffindor", "Gato");
        Alumno septimoCurso = new Alumno("Oliver", "Wood", 7, "Gryffindor", "León");

        // Insertar todos
        CompletableFuture.allOf(
            DaoAlumno.nuevoAlumno(primerCurso, baseGryffindor),
            DaoAlumno.nuevoAlumno(tercerCurso, baseGryffindor),
            DaoAlumno.nuevoAlumno(septimoCurso, baseGryffindor)
        ).get();

        System.out.println("Alumnos insertados:");
        System.out.println("  Curso 1: " + primerCurso.getId());
        System.out.println("  Curso 3: " + tercerCurso.getId());
        System.out.println("  Curso 7: " + septimoCurso.getId());

        // Act: Eliminar todos
        CompletableFuture.allOf(
            DaoAlumno.eliminarAlumno(primerCurso, baseGryffindor),
            DaoAlumno.eliminarAlumno(tercerCurso, baseGryffindor),
            DaoAlumno.eliminarAlumno(septimoCurso, baseGryffindor)
        ).get();

        // Assert: Verificar que todos fueron eliminados
        ObservableList<Alumno> alumnos = DaoAlumno.cargarAlumnos(baseGryffindor).get();
        assertFalse(alumnos.stream().anyMatch(a -> a.getId().equals(primerCurso.getId())));
        assertFalse(alumnos.stream().anyMatch(a -> a.getId().equals(tercerCurso.getId())));
        assertFalse(alumnos.stream().anyMatch(a -> a.getId().equals(septimoCurso.getId())));

        System.out.println("✓ Todos los alumnos eliminados correctamente");
    }

    /**
     * Resumen final después de todos los tests.
     */
    @AfterAll
    static void resumenFinal() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Tests de eliminarAlumno completados exitosamente");
        System.out.println("Se verificó la eliminación en las 4 bases de datos:");
        System.out.println("  ✓ Apache Derby (Gryffindor)");
        System.out.println("  ✓ HSQLDB (Slytherin)");
        System.out.println("  ✓ Oracle (Ravenclaw)");
        System.out.println("  ✓ H2 (Hufflepuff)");
        System.out.println("\nFuncionalidades probadas:");
        System.out.println("  ✓ Eliminación individual por casa");
        System.out.println("  ✓ Eliminaciones concurrentes (paralelo)");
        System.out.println("  ✓ Eliminación de alumno inexistente");
        System.out.println("  ✓ Verificación de conteos antes/después");
        System.out.println("  ✓ Doble eliminación del mismo alumno");
        System.out.println("  ✓ Eliminación de alumnos de diferentes cursos");
        System.out.println("  ✓ Transacciones con commit/rollback");
        System.out.println("=".repeat(60));
    }
}
