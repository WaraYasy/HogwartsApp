# 🧙‍♂️ ServicioHogwarts - Guía Rápida para ti que estas haciendo el controlador

Guía para usar `ServicioHogwarts` .

---

## 📌 Conceptos Importantes

### **1. Todo es asíncrono**
Todos los métodos retornan `CompletableFuture` → No bloquean la UI.

### **2. Usar Platform.runLater()**
Siempre que actualices componentes JavaFX, envuélvelos en `Platform.runLater()`.

### **3. Arquitectura Master-Slave**
- **MASTER (MariaDB)**: Fuente de verdad
- **SLAVES (Casas + SQLite)**: Copias sincronizadas

---

## 🚀 Uso Básico

### **Cargar alumnos en una tabla**
```java
ServicioHogwarts.cargarAlumnos()
    .thenAccept(alumnos -> {
        Platform.runLater(() -> {
            tablaAlumnos.setItems(alumnos);
        });
    })
    .exceptionally(ex -> {
        Platform.runLater(() -> {
            mostrarError("Error al cargar: " + ex.getMessage());
        });
        return null;
    });
```

---

### **Crear un alumno**
```java
Alumno nuevoAlumno = new Alumno();
nuevoAlumno.setNombre("Harry");
nuevoAlumno.setApellidos("Potter");
nuevoAlumno.setCurso(5);
nuevoAlumno.setCasa("Gryffindor");
nuevoAlumno.setPatronus("Ciervo");

ServicioHogwarts.nuevoAlumno(nuevoAlumno)
    .thenAccept(exito -> {
        Platform.runLater(() -> {
            if (exito) {
                mostrarMensaje("✅ Alumno creado");
                cargarTabla(); // Recargar datos
            } else {
                mostrarError("❌ Error al crear");
            }
        });
    });
```

---

### **Modificar un alumno**
```java
// alumnoSeleccionado es el que tienes en la tabla
String idOriginal = alumnoSeleccionado.getId();

Alumno modificado = new Alumno();
modificado.setNombre("Hermione");
modificado.setApellidos("Granger");
modificado.setCurso(5);
modificado.setCasa("Gryffindor");
modificado.setPatronus("Nutria");

ServicioHogwarts.modificarAlumno(idOriginal, modificado)
    .thenAccept(exito -> {
        Platform.runLater(() -> {
            if (exito) {
                mostrarMensaje("✅ Alumno modificado");
                cargarTabla();
            }
        });
    });
```

---

### **Eliminar un alumn**
```java
// alumnoSeleccionado viene de la tabla
ServicioHogwarts.eliminarAlumno(alumnoSeleccionado)
    .thenAccept(exito -> {
        Platform.runLater(() -> {
            if (exito) {
                mostrarMensaje("✅ Alumno eliminado");
                cargarTabla();
            }
        });
    });
```

---

### **Sincronizar todas las bases**
```java
ServicioHogwarts.sincronizarDesdeMaster()
    .thenAccept(exito -> {
        Platform.runLater(() -> {
            if (exito) {
                mostrarMensaje("✅ Bases sincronizadas");
            } else {
                mostrarAdvertencia("⚠️ Sincronización parcial");
            }
        });
    });
```

---

### **Sincronizar una base específica**
```java
// Sincronizar solo SQLite
ServicioHogwarts.sincronizarBaseDesdeMaster(TipoBaseDatos.SQLITE)
    .thenAccept(exito -> {
        Platform.runLater(() -> {
            if (exito) {
                mostrarMensaje("✅ SQLite sincronizado");
            }
        });
    });
```

---

### **Cargar desde una base específica**
```java
// Cargar desde SQLite en lugar de MASTER
ServicioHogwarts.cargarAlumnosDesde(TipoBaseDatos.SQLITE)
    .thenAccept(alumnos -> {
        Platform.runLater(() -> {
            tablaAlumnos.setItems(alumnos);
        });
    });
```

---

## ⚡ Patrón Completo Recomendaado
```java
@FXML
private void onGuardar() {
    // 1. Validar datos
    if (txtNombre.getText().isEmpty()) {
        mostrarError("Campo requerido");
        return;
    }
    
    // 2. Crear alumno
    Alumno alumno = new Alumno();
    alumno.setNombre(txtNombre.getText());
    alumno.setApellidos(txtApellidos.getText());
    alumno.setCurso(Integer.parseInt(txtCurso.getText()));
    alumno.setCasa(cbCasa.getValue());
    alumno.setPatronus(txtPatronus.getText());
    
    // 3. Deshabilitar UI durante operación
    btnGuardar.setDisable(true);
    progressIndicator.setVisible(true);
    
    // 4. Llamar al servicio
    ServicioHogwarts.nuevoAlumno(alumno)
        .thenAccept(exito -> {
            Platform.runLater(() -> {
                if (exito) {
                    mostrarExito("Alumno creado correctamente");
                    limpiarFormulario();
                    cargarDatos();
                } else {
                    mostrarError("No se pudo guardar en todas las bases");
                }
                
                // 5. Rehabilitar UI
                btnGuardar.setDisable(false);
                progressIndicator.setVisible(false);
            });
        })
        .exceptionally(ex -> {
            Platform.runLater(() -> {
                mostrarError("Error: " + ex.getMessage());
                btnGuardar.setDisable(false);
                progressIndicator.setVisible(false);
            });
            return null;
        });
}
```

---

## ⚠️ Errores Comunes

### ❌ **ERROR: No usar Platform.runLater()**
```java
// MAL - Lanza IllegalStateException
ServicioHogwarts.cargarAlumnos()
    .thenAccept(alumnos -> {
        tablaAlumnos.setItems(alumnos); // ¡NO está en JavaFX thread!
    });

// BIEN
ServicioHogwarts.cargarAlumnos()
    .thenAccept(alumnos -> {
        Platform.runLater(() -> {
            tablaAlumnos.setItems(alumnos); // ✓ En JavaFX thread
        });
    });
```

### ❌ **ERROR: No manejar excepciones**
```java
// MAL - Si falla, no se entera nadie
ServicioHogwarts.nuevoAlumno(alumno)
    .thenAccept(exito -> { ... });

// BIEN - Captura errores
ServicioHogwarts.nuevoAlumno(alumno)
    .thenAccept(exito -> { ... })
    .exceptionally(ex -> {
        Platform.runLater(() -> mostrarError(ex.getMessage()));
        return null;
    });
```

### ❌ **ERROR: No deshabilitar botones**
```java
// MAL - El usuario puede hacer click múltiples veces
ServicioHogwarts.nuevoAlumno(alumno)
    .thenAccept(exito -> { ... });

// BIEN - Deshabilita durante la operación
btnGuardar.setDisable(true);
ServicioHogwarts.nuevoAlumno(alumno)
    .thenAccept(exito -> {
        Platform.runLater(() -> {
            btnGuardar.setDisable(false);
        });
    });
```

---

## 📦 Imports Necesarios
```java
import es.potter.servicio.ServicioHogwarts;
import es.potter.model.Alumno;
import es.potter.database.TipoBaseDatos;
import javafx.application.Platform;
import javafx.collections.ObservableList;
```

---

## ✅ Checklist

Antes de usar el servicio, asegúrate de:

- [ ] Importar `ServicioHogwarts`
- [ ] Usar `Platform.runLater()` para actualizar UI
- [ ] Manejar errores con `.exceptionally()`
- [ ] Deshabilitar botones durante operaciones
- [ ] Mostrar indicadores de progreso
- [ ] Validar datos antes de enviar
- [ ] Recargar tabla después de operaciones CRUD

---

## 🎯 Resumen

**3 reglas de oro:**

1. **Todo es async** → Usa `.thenAccept()`
2. **Actualiza UI** → Usa `Platform.runLater()`
3. **Maneja errores** → Usa `.exceptionally()`

**Patrón básico:**
```java
ServicioHogwarts.metodo(parametros)
    .thenAccept(resultado -> {
        Platform.runLater(() -> {
            // Actualizar UI aquí
        });
    })
    .exceptionally(ex -> {
        Platform.runLater(() -> {
            // Manejar error aquí
        });
        return null;
    });
```

---

## Autor
Equipo Potter