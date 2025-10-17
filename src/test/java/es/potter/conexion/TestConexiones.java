package es.potter.conexion;

import es.potter.database.ConexionFactory;
import es.potter.database.TipoBaseDatos;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Test para verificar la conectividad con todas las bases de datos.
 * Muestra qué bases están disponibles y cuáles no.
 *
 * @author Wara Pacheco
 * @version 1.0
 */
public class TestConexiones {

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("  TEST DE CONEXIONES A BASES DE DATOS");
        System.out.println("═══════════════════════════════════════════════════\n");

        TestConexiones test = new TestConexiones();
        test.probarTodasLasConexiones();
    }

    /**
     * Prueba la conexión a todas las bases de datos.
     */
    public void probarTodasLasConexiones() {
        TipoBaseDatos[] bases = TipoBaseDatos.values();
        int disponibles = 0;
        int noDisponibles = 0;

        for (TipoBaseDatos base : bases) {
            System.out.println("─────────────────────────────────────────────────");
            System.out.println("Probando: " + base.name());
            System.out.println("─────────────────────────────────────────────────");

            if (probarConexion(base)) {
                System.out.println("Estado: DISPONIBLE\n");
                disponibles++;
            } else {
                System.out.println("Estado: NO DISPONIBLE\n");
                noDisponibles++;
            }
        }

        // Resumen
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("  RESUMEN");
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("Total de bases de datos: " + bases.length);
        System.out.println("Disponibles: " + disponibles);
        System.out.println("No disponibles: " + noDisponibles);
        System.out.println("═══════════════════════════════════════════════════\n");

        if (noDisponibles > 0) {
            System.out.println("NOTA: Para iniciar las bases de datos faltantes, revisa");
            System.out.println("      la configuración de servidores en tu entorno.");
        }
    }

    /**
     * Prueba la conexión a una base de datos específica.
     *
     * @param base el tipo de base de datos a probar
     * @return true si la conexión fue exitosa, false en caso contrario
     */
    private boolean probarConexion(TipoBaseDatos base) {
        try {
            // Intentar conectar con timeout de 3 segundos
            CompletableFuture<java.sql.Connection> futureConexion =
                    ConexionFactory.getConnectionAsync(base);

            java.sql.Connection conexion = futureConexion.get(3, TimeUnit.SECONDS);

            if (conexion != null && !conexion.isClosed()) {
                System.out.println("Conexión establecida exitosamente");

                // Probar una consulta simple
                try (var stmt = conexion.createStatement()) {
                    var rs = stmt.executeQuery("SELECT 1");
                    if (rs.next()) {
                        System.out.println("Consulta de prueba: OK");
                    }
                } catch (Exception e) {
                    System.out.println("Advertencia: Consulta de prueba falló: " + e.getMessage());
                }

                conexion.close();
                return true;
            }

            return false;

        } catch (TimeoutException e) {
            System.out.println("Error: Timeout - El servidor no responde");
            return false;
        } catch (ExecutionException e) {
            Throwable causa = e.getCause();
            if (causa != null) {
                String mensaje = causa.getMessage();
                if (mensaje.contains("No suitable driver")) {
                    System.out.println("Error: Driver no encontrado o servidor no iniciado");
                } else if (mensaje.contains("Connection refused")) {
                    System.out.println("Error: Conexión rechazada - Servidor no está corriendo");
                } else if (mensaje.contains("Communications link failure")) {
                    System.out.println("Error: No se puede conectar - Servidor no disponible");
                } else {
                    System.out.println("Error: " + mensaje);
                }
            } else {
                System.out.println("Error: " + e.getMessage());
            }
            return false;
        } catch (InterruptedException e) {
            System.out.println("Error: Conexión interrumpida");
            Thread.currentThread().interrupt();
            return false;
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
            return false;
        }
    }
}
