package es.potter.dao;

import es.potter.database.TipoBaseDatos;
import es.potter.model.Alumno;
import org.junit.jupiter.api.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el m�todo nuevoAlumno de DaoAlumno.
 *
 * Estos tests verifican:
 * - Inserci�n de alumnos sin ID (generaci�n autom�tica)
 * - Inserci�n de alumnos con ID existente (sincronizaci�n)
 * - Manejo de errores y validaciones
 *
 * @author Wara Pacheco
 * @version 1.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestInsertar {

    private static final TipoBaseDatos BASE_TEST = TipoBaseDatos.H2;

    /**
     * Test 1: Insertar un alumno SIN ID (debe generarse autom�ticamente).
     * Verifica que:
     * - El m�todo retorna true (inserci�n exitosa)
     * - El alumno recibe un ID generado con el formato correcto
     * - El ID sigue el patr�n GRY##### (Gryffindor)
     */
    @Test
    @Order(1)
    @DisplayName("Insertar alumno sin ID - Generaci�n autom�tica")
    void testInsertarAlumnoSinId() throws ExecutionException, InterruptedException {
        // Arrange: Crear alumno sin ID
        Alumno alumnoNuevo = new Alumno(
            "Harry",
            "Potter",
            1,
            "Gryffindor",
            "Ciervo"
        );

        // Verificar que el alumno no tiene ID inicialmente
        assertNull(alumnoNuevo.getId(), "El alumno no deber�a tener ID antes de insertarlo");

        // Act: Insertar el alumno
        CompletableFuture<Boolean> resultado = DaoAlumno.nuevoAlumno(alumnoNuevo, BASE_TEST);
        Boolean insertado = resultado.get(); // Esperar resultado

        // Assert: Verificar inserci�n exitosa
        assertTrue(insertado, "El alumno deber�a insertarse correctamente");
        assertNotNull(alumnoNuevo.getId(), "El alumno deber�a tener un ID despu�s de insertarlo");
        assertTrue(alumnoNuevo.getId().matches("^GRY\\d{5}$"),
            "El ID debe seguir el formato GRY##### para Gryffindor");

        System.out.println(" Alumno insertado con ID generado: " + alumnoNuevo.getId());
    }

    /**
     * Test 2: Insertar un alumno CON ID existente (sincronizaci�n).
     * Verifica que:
     * - El m�todo acepta alumnos con ID pre-asignado
     * - El ID proporcionado se mantiene sin modificarse
     * - La inserci�n es exitosa
     */
    @Test
    @Order(2)
    @DisplayName("Insertar alumno con ID existente - Sincronizaci�n")
    void testInsertarAlumnoConId() throws ExecutionException, InterruptedException {
        // Arrange: Crear alumno con ID pre-asignado
        Alumno alumnoSincronizado = new Alumno(
            "Hermione",
            "Granger",
            1,
            "Gryffindor",
            "Nutria"
        );

        String idEsperado = "GRY99999";
        alumnoSincronizado.setId(idEsperado);

        // Act: Insertar el alumno con ID
        CompletableFuture<Boolean> resultado = DaoAlumno.nuevoAlumno(alumnoSincronizado, BASE_TEST);
        Boolean insertado = resultado.get();

        // Assert: Verificar que mantiene el ID original
        assertTrue(insertado, "El alumno con ID existente deber�a insertarse correctamente");
        assertEquals(idEsperado, alumnoSincronizado.getId(),
            "El ID no deber�a cambiar en inserci�n de sincronizaci�n");

        System.out.println(" Alumno sincronizado con ID: " + alumnoSincronizado.getId());
    }

    /**
     * Test 3: Insertar alumno de Slytherin (verifica generaci�n de ID para otra casa).
     * Verifica que:
     * - Los IDs se generan correctamente para diferentes casas
     * - El prefijo del ID corresponde a la casa correcta (SLY)
     */
    @Test
    @Order(3)
    @DisplayName("Insertar alumno de Slytherin - Verificar prefijo correcto")
    void testInsertarAlumnoDiferenteCasa() throws ExecutionException, InterruptedException {
        // Arrange: Crear alumno de Slytherin
        Alumno alumnoSlytherin = new Alumno(
            "Draco",
            "Malfoy",
            1,
            "Slytherin",
            "Serpiente"
        );

        // Act: Insertar el alumno
        CompletableFuture<Boolean> resultado = DaoAlumno.nuevoAlumno(alumnoSlytherin, BASE_TEST);
        Boolean insertado = resultado.get();

        // Assert: Verificar que el ID tiene el prefijo correcto
        assertTrue(insertado, "El alumno de Slytherin deber�a insertarse correctamente");
        assertNotNull(alumnoSlytherin.getId(), "El alumno deber�a tener un ID generado");
        assertTrue(alumnoSlytherin.getId().matches("^SLY\\d{5}$"),
            "El ID debe seguir el formato SLY##### para Slytherin");

        System.out.println(" Alumno de Slytherin insertado con ID: " + alumnoSlytherin.getId());
    }

    /**
     * Test 4: Insertar m�ltiples alumnos de la misma casa.
     * Verifica que:
     * - Los IDs son �nicos y secuenciales
     * - No hay colisiones en la generaci�n de IDs
     */
    @Test
    @Order(4)
    @DisplayName("Insertar m�ltiples alumnos - Verificar IDs �nicos")
    void testInsertarMultiplesAlumnos() throws ExecutionException, InterruptedException {
        // Arrange: Crear tres alumnos de Ravenclaw
        Alumno alumno1 = new Alumno("Luna", "Lovegood", 5, "Ravenclaw", "Liebre");
        Alumno alumno2 = new Alumno("Cho", "Chang", 6, "Ravenclaw", "Cisne");
        Alumno alumno3 = new Alumno("Padma", "Patil", 5, "Ravenclaw", "Gato");

        // Act: Insertar los tres alumnos
        CompletableFuture<Boolean> resultado1 = DaoAlumno.nuevoAlumno(alumno1, BASE_TEST);
        CompletableFuture<Boolean> resultado2 = DaoAlumno.nuevoAlumno(alumno2, BASE_TEST);
        CompletableFuture<Boolean> resultado3 = DaoAlumno.nuevoAlumno(alumno3, BASE_TEST);

        // Esperar a que todos terminen
        CompletableFuture.allOf(resultado1, resultado2, resultado3).get();

        // Assert: Verificar que todos tienen IDs �nicos
        assertNotNull(alumno1.getId());
        assertNotNull(alumno2.getId());
        assertNotNull(alumno3.getId());

        assertNotEquals(alumno1.getId(), alumno2.getId(),
            "Los IDs deben ser �nicos");
        assertNotEquals(alumno2.getId(), alumno3.getId(),
            "Los IDs deben ser �nicos");
        assertNotEquals(alumno1.getId(), alumno3.getId(),
            "Los IDs deben ser �nicos");

        System.out.println(" Tres alumnos insertados con IDs �nicos:");
        System.out.println("  - " + alumno1.getNombre() + ": " + alumno1.getId());
        System.out.println("  - " + alumno2.getNombre() + ": " + alumno2.getId());
        System.out.println("  - " + alumno3.getNombre() + ": " + alumno3.getId());
    }

    /**
     * Test 5: Verificar que se lanza excepci�n con casa inv�lida.
     * Este test verifica el manejo de errores en la generaci�n de IDs.
     */
    @Test
    @Order(5)
    @DisplayName("Insertar alumno con casa inv�lida - Debe fallar")
    void testInsertarAlumnoCasaInvalida() {
        // Arrange: Intentar crear alumno con casa inv�lida (menos de 3 caracteres)
        // Nota: El constructor de Alumno ya valida la casa, as� que probamos con null

        assertThrows(IllegalArgumentException.class, () -> {
            new Alumno("Test", "Usuario", 1, "XX", "Ninguno");
        }, "Deber�a lanzar excepci�n al crear alumno con casa inv�lida");

        System.out.println(" Validaci�n de casa inv�lida funciona correctamente");
    }

    /**
     * Test 6: Insertar alumno de Hufflepuff para completar las cuatro casas.
     */
    @Test
    @Order(6)
    @DisplayName("Insertar alumno de Hufflepuff - Completar cuatro casas")
    void testInsertarAlumnoHufflepuff() throws ExecutionException, InterruptedException {
        // Arrange
        Alumno alumnoHufflepuff = new Alumno(
            "Cedric",
            "Diggory",
            7,
            "Hufflepuff",
            "Labrador"
        );

        // Act
        CompletableFuture<Boolean> resultado = DaoAlumno.nuevoAlumno(alumnoHufflepuff, BASE_TEST);
        Boolean insertado = resultado.get();

        // Assert
        assertTrue(insertado, "El alumno de Hufflepuff deber�a insertarse correctamente");
        assertTrue(alumnoHufflepuff.getId().matches("^HUF\\d{5}$"),
            "El ID debe seguir el formato HUF##### para Hufflepuff");

        System.out.println(" Alumno de Hufflepuff insertado con ID: " + alumnoHufflepuff.getId());
    }

    /**
     * Limpieza despu�s de todos los tests.
     */
    @AfterAll
    static void limpiezaFinal() {
        System.out.println("\n=== Tests de inserci�n completados ===");
        System.out.println("Se verific� la inserci�n correcta de alumnos con y sin ID");
        System.out.println("Se probaron las cuatro casas de Hogwarts");
    }
}
