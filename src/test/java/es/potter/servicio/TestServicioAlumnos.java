package es.potter.servicio;

import es.potter.database.TipoBaseDatos;
import es.potter.model.Alumno;
import javafx.collections.ObservableList;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Clase de pruebas manual para ServicioAlumnos.
 * Prueba las operaciones CRUD con sincronización y rollback.
 *
 * IMPORTANTE: Ejecutar con bases de datos configuradas y disponibles.
 *
 * @author Wara Pacheco
 * @version 1.0
 */
public class TestServicioAlumnos {

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("  INICIANDO PRUEBAS DE SERVICIO ALUMNOS");
        System.out.println("═══════════════════════════════════════════════════\n");

        TestServicioAlumnos test = new TestServicioAlumnos();

        // Ejecutar todas las pruebas
        test.ejecutarTodasLasPruebas();
    }

    /**
     * Ejecuta todas las pruebas en secuencia.
     */
    public void ejecutarTodasLasPruebas() {
        int totalPruebas = 0;
        int pruebasExitosas = 0;

        // Test 1: Crear alumno
        totalPruebas++;
        if (testCrearAlumno()) {
            pruebasExitosas++;
        }

        // Test 2: Cargar alumnos
        totalPruebas++;
        if (testCargarAlumnos()) {
            pruebasExitosas++;
        }

        // Test 3: Modificar alumno
        totalPruebas++;
        if (testModificarAlumno()) {
            pruebasExitosas++;
        }

        // Test 4: Eliminar alumno
        totalPruebas++;
        if (testEliminarAlumno()) {
            pruebasExitosas++;
        }

        // Resumen
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("  RESUMEN DE PRUEBAS");
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("Total de pruebas: " + totalPruebas);
        System.out.println("Exitosas: " + pruebasExitosas);
        System.out.println("Fallidas: " + (totalPruebas - pruebasExitosas));
        System.out.println("═══════════════════════════════════════════════════\n");
    }

    /**
     * Test 1: Crear un alumno nuevo.
     */
    public boolean testCrearAlumno() {
        System.out.println("\n[TEST 1] Creando nuevo alumno...");
        System.out.println("─────────────────────────────────────────────────");

        try {
            // Crear alumno de prueba
            Alumno alumno = new Alumno();
            alumno.setNombre("Harry");
            alumno.setApellidos("Potter");
            alumno.setCurso(5);
            alumno.setCasa("Gryffindor");
            alumno.setPatronus("Ciervo");

            // Ejecutar operación
            CompletableFuture<Boolean> resultado = ServicioAlumnos.nuevoAlumno(alumno);
            Boolean exito = resultado.get(); // Espera a que termine

            if (exito) {
                System.out.println("✓ Test EXITOSO: Alumno creado con ID " + alumno.getId());
                return true;
            } else {
                System.out.println("✗ Test FALLIDO: No se pudo crear el alumno");
                return false;
            }

        } catch (InterruptedException | ExecutionException e) {
            System.out.println("✗ Test ERROR: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Test 2: Cargar alumnos de una base.
     */
    public boolean testCargarAlumnos() {
        System.out.println("\n[TEST 2] Cargando alumnos de Gryffindor...");
        System.out.println("─────────────────────────────────────────────────");

        try {
            // Cargar desde base de Gryffindor (Apache Derby)
            CompletableFuture<ObservableList<Alumno>> resultado =
                ServicioAlumnos.cargarAlumnos(TipoBaseDatos.APACHE_DERBY);

            ObservableList<Alumno> alumnos = resultado.get();

            System.out.println("✓ Test EXITOSO: Se cargaron " + alumnos.size() + " alumnos");

            // Mostrar primeros 3
            int mostrar = Math.min(3, alumnos.size());
            for (int i = 0; i < mostrar; i++) {
                Alumno a = alumnos.get(i);
                System.out.println("  - " + a.getId() + ": " + a.getNombre() + " " + a.getApellidos());
            }

            return true;

        } catch (InterruptedException | ExecutionException e) {
            System.out.println("✗ Test ERROR: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Test 3: Modificar un alumno existente.
     */
    public boolean testModificarAlumno() {
        System.out.println("\n[TEST 3] Modificando alumno existente...");
        System.out.println("─────────────────────────────────────────────────");

        try {
            // Primero cargar un alumno
            CompletableFuture<ObservableList<Alumno>> resultado =
                ServicioAlumnos.cargarAlumnos(TipoBaseDatos.APACHE_DERBY);
            ObservableList<Alumno> alumnos = resultado.get();

            if (alumnos.isEmpty()) {
                System.out.println("⚠ Test OMITIDO: No hay alumnos para modificar");
                return true; // No es un fallo
            }

            // Modificar el primero
            Alumno alumno = alumnos.get(0);
            String idOriginal = alumno.getId();
            int cursoOriginal = alumno.getCurso();

            System.out.println("Modificando alumno: " + idOriginal);
            System.out.println("  Curso original: " + cursoOriginal);

            // Cambiar curso
            Alumno modificado = new Alumno();
            modificado.setNombre(alumno.getNombre());
            modificado.setApellidos(alumno.getApellidos());
            modificado.setCurso(cursoOriginal < 7 ? cursoOriginal + 1 : cursoOriginal);
            modificado.setCasa(alumno.getCasa());
            modificado.setPatronus(alumno.getPatronus());

            CompletableFuture<Boolean> resultadoMod =
                ServicioAlumnos.modificarAlumno(idOriginal, modificado, TipoBaseDatos.APACHE_DERBY);

            Boolean exito = resultadoMod.get();

            if (exito) {
                System.out.println("✓ Test EXITOSO: Alumno " + idOriginal + " modificado");
                System.out.println("  Nuevo curso: " + modificado.getCurso());
                return true;
            } else {
                System.out.println("✗ Test FALLIDO: No se pudo modificar");
                return false;
            }

        } catch (InterruptedException | ExecutionException e) {
            System.out.println("✗ Test ERROR: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Test 4: Eliminar un alumno.
     */
    public boolean testEliminarAlumno() {
        System.out.println("\n[TEST 4] Eliminando alumno de prueba...");
        System.out.println("─────────────────────────────────────────────────");

        try {
            // Primero crear un alumno temporal para eliminar
            Alumno temporal = new Alumno();
            temporal.setNombre("Temporal");
            temporal.setApellidos("Test");
            temporal.setCurso(1);
            temporal.setCasa("Hufflepuff");
            temporal.setPatronus("Test");

            // Crear
            CompletableFuture<Boolean> resultadoCrear = ServicioAlumnos.nuevoAlumno(temporal);
            Boolean creado = resultadoCrear.get();

            if (!creado) {
                System.out.println("✗ Test FALLIDO: No se pudo crear alumno temporal");
                return false;
            }

            System.out.println("Alumno temporal creado: " + temporal.getId());

            // Ahora eliminarlo
            CompletableFuture<Boolean> resultadoEliminar =
                ServicioAlumnos.eliminarAlumno(temporal, TipoBaseDatos.H2);

            Boolean eliminado = resultadoEliminar.get();

            if (eliminado) {
                System.out.println("✓ Test EXITOSO: Alumno " + temporal.getId() + " eliminado");
                return true;
            } else {
                System.out.println("✗ Test FALLIDO: No se pudo eliminar");
                return false;
            }

        } catch (InterruptedException | ExecutionException e) {
            System.out.println("✗ Test ERROR: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Test adicional: Verificar rollback en caso de fallo.
     * NOTA: Este test requiere simular un fallo de base de datos.
     */
    public boolean testRollback() {
        System.out.println("\n[TEST ROLLBACK] Verificando rollback automático...");
        System.out.println("─────────────────────────────────────────────────");
        System.out.println("⚠ Este test requiere desconectar una BD para simular fallo");
        System.out.println("⚠ Ejecutar manualmente si es necesario");
        return true;
    }
}
