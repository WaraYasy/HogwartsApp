
# Aplicación de Base de Datos de la Casa Hogwarts

## 📖 Descripción del repositorio
Este proyecto es una aplicación Java diseñada para la gestión de las Bases de Datos del Colegio de Hogwarts. Utiliza **JavaFX** para la interfaz de usuario, una base de datos para la gestión de información.

## 📂 Estructura del Proyecto

El proyecto está organizado siguiendo una arquitectura modular basada en **MVC (Modelo Vista Controlador)** y separa la lógica, los datos y la interfaz de usuario de forma clara.

---

### 📌 **1. Aplicación**
- `📁 es/potter/`
    - 📌 `HogwartsApp.java` → Clase principal de la aplicación JavaFX.
    - 📌 `Lanzador.java` → Punto de entrada para iniciar la aplicación.

---

### 📌 **2. Controladores**
Gestionan la lógica de la interfaz gráfica y la interacción entre la vista (FXML) y el modelo.
- `📁 es/potter/control/`
    - 📌 `ControladorPrincipal.java` → Controlador de la ventana principal.
    - 📌 `ControladorNuevoAlumno.java` → Controlador para el formulario de alta de nuevos alumnos.
    - 📌 `ControladorEditarAlumno.java` → Controlador para la edición de alumnos existentes.

---

### 📌 **3. Data Access Object (DAO)**
Encapsulan las operaciones de acceso a los datos y la comunicación con la base de datos.
- `📁 es/potter/dao/`
    - 📌 `DaoAlumno.java` → Implementa las operaciones CRUD para la entidad Alumno.

---

### 📌 **4. Base de Datos**
Contiene las clases que permiten conectarse a diferentes motores de base de datos y gestionar las conexiones de forma centralizada.
- `📁 es/potter/database/`
    - 📌 `ConexionFactory.java` → Fábrica para obtener instancias de conexión según el tipo de base de datos.
    - 📌 `TipoBaseDatos.java` → Enumeración con los tipos de bases de datos soportadas.
    - 📌 `ConexionApacheDerby.java` → Conexión con base de datos **Apache Derby**.
    - 📌 `ConexionH2.java` → Conexión con base de datos **H2**.
    - 📌 `ConexionHSQLDB.java` → Conexión con base de datos **HSQLDB**.
    - 📌 `ConexionMariaDB.java` → Conexión con base de datos **MariaDB**.
    - 📌 `ConexionOracle.java` → Conexión con base de datos **Oracle**.
    - 📌 `ConexionSqlite.java` → Conexión con base de datos **SQLite**.
    - 📌 `SQLiteManager.java` → Conexión con base de datos **SQLite**.

---

### 📌 **5. Modelos**
Contienen las clases que representan las entidades del sistema y sus atributos.
- `📁 es/potter/model/`
    - 📌 `Alumno.java` → Clase que define la entidad **Alumno**, con sus propiedades y métodos de acceso.

---

### 📌 **6. Servicios**
Incluye los servicios que proporcionan lógica de negocio adicional o funcionalidad auxiliar.
- `📁 es/potter/servicio/`
    - 📌 `ServicioHogwarts.java` → Clase que implementa servicios relacionados con la gestión de alumnos y casas.
    - 📄 `ComoUsarElServicio.md` → Documento explicativo sobre el uso del servicio.

---

### 📌 **7. Utilidades**
Contiene clases de apoyo o configuración reutilizable.
- `📁 es/potter/util/`
    - 📌 `Propiedades.java` → Gestión de parámetros y configuración general del sistema.

---

### 📌 **8. Archivos de Configuración**
- 📌 `configuration.properties` → Archivo con parámetros de conexión a base de datos y otras propiedades del sistema.
- 📌 `pom.xml` → Definición de dependencias Maven.
- 📌 `.gitignore` → Exclusiones de control de versiones.
- 📌 `README.md` → Documentación general del proyecto.

---

### 📌 **9. Recursos Estáticos**
Contiene archivos de estilo, interfaces y otros recursos necesarios para la interfaz gráfica.

#### 🎨 **CSS**
- `📁 es/potter/css/`
    - 📌 `estilo.css` → Define los estilos visuales aplicados a la UI JavaFX.

#### 🖼️ **FXML**
- `📁 es/potter/fxml/`
    - 📌 `ventanaPrincipal.fxml` → Interfaz principal.
    - 📌 `modalNuevoAlumno.fxml` → Ventana modal para agregar alumnos.
    - 📌 `modalEditarAlumno.fxml` → Ventana modal para editar alumnos.

#### 🧩 **Imágenes**
- `📁 es/potter/img/`
    - 🖼️ `gryffindor_.png`
    - 🖼️ `hogwarts_.png`
    - 🖼️ `hufflepuff_.png`
    - 🖼️ `ravenclaw_.png`
    - 🖼️ `slytherin_.png`

#### 🗄️ **SQL**
- `📁 es/potter/sql/`
    - 📄 `init.sql` → Script para inicializar la base de datos.

---

### 📌 **10. Internacionalización**
Archivos de traducción de la interfaz y mensajes del sistema.
- 📁 Raíz del proyecto:
    - 🌍 `mensajes_es.properties` → Español.
    - 🌍 `mensajes_en.properties` → Inglés.
    - 🌍 `mensajes_eu.properties` → Euskera.

---

### 📌 **11. Otros Archivos**
- `pom.xml` → Configuración del proyecto Maven y sus dependencias.
- `.gitignore` → Archivos y carpetas excluidos del repositorio.
- `README.md` → Documentación principal del proyecto.
---

## ⚙️ Requisitos de la aplicación
- ☕ **JDK 24**
- 🎭 **JavaFX 24**

## 🚀 Instalación y Ejecución
1. Abrir el enlace proporcionado para la conexion remota en Tailscale

    ### Conexión remota mediante Tailscale

    Esta aplicación se conecta a la base de datos a través de una red privada Tailscale.

    #### Requisitos previos
   - Tener una cuenta en [Tailscale](https://tailscale.com)
   - Instalar el cliente Tailscale en su sistema
   - Solicitar acceso a la red privada del proyecto (tailnet)

    #### Pasos para conectarse
   1. Instale Tailscale en su equipo.
   2. Inicie sesión con la cuenta autorizada o use el auth key proporcionado.
   3. Verifique la conexión:
      ```bash
      tailscale status


2. Clona el repositorio:
   ```sh
   git clone https://github.com/WaraYasy/HogwartsApp.git
   ```
   
3. Importa el proyecto en tu IDE preferido.

4. **Configura las conexiones a bases de datos:**

   Edita el archivo `src/main/resources/es/potter/configuration.properties` con tus credenciales.

   La aplicación soporta múltiples bases de datos simultáneamente. Configura las que necesites:

   ```properties
   # MariaDB (Base de datos principal - MASTER)
   db.mariadb.url=jdbc:mariadb://tu_ip_tailscale:3306/hogwarts
   db.mariadb.user=tu_usuario
   db.mariadb.password=tu_contraseña

   # Oracle (Gryffindor)
   db.oracle.url=jdbc:oracle:thin:@//tu_ip_tailscale:1521/hogwarts
   db.oracle.user=tu_usuario
   db.oracle.password=tu_contraseña

   # H2 (Slytherin)
   db.h2.url=jdbc:h2:tcp://tu_ip_tailscale:9092/hogwarts
   db.h2.user=tu_usuario
   db.h2.password=tu_contraseña

   # Apache Derby (Ravenclaw)
   db.derby.url=jdbc:derby://tu_ip_tailscale:1527/derby_data/hogwarts;create=true
   db.derby.user=tu_usuario
   db.derby.password=tu_contraseña

   # HSQLDB (Hufflepuff)
   db.hsqldb.url=jdbc:hsqldb:hsql://tu_ip_tailscale:9001/hogwarts
   db.hsqldb.user=tu_usuario
   db.hsqldb.password=tu_contraseña

   # SQLite (Embebida - NO requiere configuración)
   ```

   **Notas importantes:**
   - MariaDB es la base de datos principal (MASTER)
   - Cada casa tiene su propia base de datos (SLAVE)
   - SQLite se configura automáticamente, no necesitas modificarla


4. Ejecuta `Lanzador.java` para iniciar la aplicación.

---

## ✨ Autores
- 👤 **Erlantz Garcia**
- 👤 **Marco Muro**
- 👤 **Nizam Abdel-Ghaffar**
- 👤 **Salca Bachir**
- 👤 **Wara Pacheco**
- 👤 **Arantxa Main**
- 👤 **Telmo Castillo**