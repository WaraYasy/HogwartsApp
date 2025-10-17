package es.potter.model;

/**
 * Representa un alumno de Hogwarts con sus características principales.
 * Esta clase modela la información de un estudiante incluyendo su identificación,
 * información personal, casa a la que pertenece y su patronus.
 * 
 * @author Wara Pacheco
 * @version 1.0
 * @since 2025-10-10
 */
public class Alumno {
    /** Identificador único del alumno con formato de 3 letras + 5 dígitos . Ejemplo: GRY00001 */
    private String id;

    /** Nombre del alumno */
    private String nombre;

    /** Apellidos del alumno */
    private String apellidos;
    
    /** Curso actual del alumno (1-7) */
    private int curso;
    
    /** Casa de Hogwarts a la que pertenece el alumno */
    private String casa;
    
    /** Patronus del alumno */
    private String patronus;

    /**
     * Constructor por defecto que inicializa un alumno vacío.
     * Establece todos los valores a null o 0 según corresponda.
     */
    public Alumno() {
        this.id = null;
        this.nombre = null;
        this.apellidos = null;
        this.curso = 0;
        this.casa = null;
        this.patronus = null;
    }

    /**
     * Constructor que inicializa un alumno con los datos proporcionados.
     * 
     * @param nombre el nombre del alumno
     * @param apellidos apellidos del alumno
     * @param curso el curso del alumno (1-7)
     * @param casa la casa de Hogwarts del alumno
     * @param patronus el patronus del alumno
     */
    public Alumno(String nombre, String apellidos, int curso, String casa, String patronus){
        setNombre(nombre);
        setApellidos(apellidos);
        setCurso(curso);
        setCasa(casa);
        setPatronus(patronus);
     }

    /**
     * Obtiene el identificador del alumno.
     * 
     * @return el ID del alumno
     */
    public String getId() {
        return id;
    }

    /**
     * Establece el ID del alumno. Solo se puede establecer una vez.
     * El ID debe seguir el formato de 3 letras mayúsculas + guión + 8 caracteres hexadecimales.
     *
     * @param id el ID del alumno con formato XXX-xxxxxxxx (ej: GRY-a4f3b2c1)
     * @throws IllegalStateException si el ID ya está establecido
     * @throws IllegalArgumentException si el ID es null, vacío o no sigue el formato correcto
     */
    public void setId(String id) {
        // Verifica si el ID ya ha sido asignado
        if (this.id != null) {
            throw new IllegalStateException("El ID ya ha sido asignado y no puede modificarse.");
        }

        // Validación del formato del ID
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID no puede ser nulo o vacío.");
        }

        // Validación del formato: 3 letras mayúsculas + guión + 8 caracteres hexadecimales
        // Ejemplo válido: GRY-a4f3b2c1
        if (!id.matches("^[A-Za-z]{3}-[a-f0-9]{8}$")) {
            throw new IllegalArgumentException("El ID debe tener el formato: XXX-xxxxxxxx (ej: GRY-a4f3b2c1)");
        }

        this.id = id;
    }

    /**
     * Obtiene el nombre del alumno.
     * 
     * @return el nombre del alumno
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del alumno.
     * 
     * @param nombre el nombre del alumno
     * @throws IllegalArgumentException si el nombre es null o está vacío
     */
    public void setNombre(String nombre) {
        // Validación del nombre no nulo o vacío
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        this.nombre = nombre;
    }

    /**
     * Obtiene los apellidos del alumno.
     *
     * @return los apellidos del alumno
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * Establece los apellidos del alumno.
     *
     * @param apellidos los apellidos del alumno
     * @throws IllegalArgumentException si el campo apellidos es null o está vacío
     */
    public void setApellidos(String apellidos) {
        // Validación del apellidos no nulo o vacío
        if (apellidos == null || apellidos.trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido no puede estar vacío.");
        }
        this.apellidos = apellidos;
    }
    /**
     * Obtiene el curso del alumno.
     * 
     * @return el curso del alumno (1-7)
     */
    public int getCurso() {
        return curso;
    }

    /**
     * Establece el curso del alumno.
     * 
     * @param curso el curso del alumno (debe estar entre 1 y 7)
     * @throws IllegalArgumentException si el curso no está entre 1 y 7
     */
    public void setCurso(int curso) {
        // Validación del curso entre 1 y 7
        if (curso < 1 || curso > 7) {
            throw new IllegalArgumentException("El curso debe estar entre 1 y 7.");
        }
        this.curso = curso;
    }

    /**
     * Obtiene la casa de Hogwarts del alumno.
     * 
     * @return la casa del alumno
     */
    public String getCasa() {
        return casa;
    }

    /**
     * Establece la casa de Hogwarts del alumno.
     * Las casas válidas son: Gryffindor, Slytherin, Hufflepuff, Ravenclaw.
     * 
     * @param casa la casa del alumno
     * @throws IllegalArgumentException si la casa es null o no es válida
     */
    public void setCasa(String casa) {
        // Validación de la casa no nula y válida
        if (casa == null) {
            throw new IllegalArgumentException("La casa no puede ser nula.");
        }
        // Validación de la casa válida
        String casaLower = casa.trim().toLowerCase();
        if (!casaLower.equals("gryffindor") && 
            !casaLower.equals("slytherin") && 
            !casaLower.equals("hufflepuff") && 
            !casaLower.equals("ravenclaw")) {
            throw new IllegalArgumentException("La casa debe ser Gryffindor, Slytherin, Hufflepuff o Ravenclaw.");
        }
        this.casa = casa;
    }

    /**
     * Obtiene el patronus del alumno.
     * 
     * @return el patronus del alumno
     */
    public String getPatronus() {
        return patronus;
    }

    /**
     * Establece el patronus del alumno.
     * 
     * @param patronus el patronus del alumno (puede ser null)
     * @throws IllegalArgumentException si el patronus está vacío pero no es null
     */
    public void setPatronus(String patronus) {
        // Validación del patronus no vacío si se proporciona
        if (patronus != null && patronus.trim().isEmpty()) {
            throw new IllegalArgumentException("El patronus no puede estar vacío si se proporciona.");
        }
        this.patronus = patronus;
    }

    public String toString(){
        return "Alumno "+this.nombre+". Casa: "+this.casa+"\n Curso: "+this.curso;
    }
}