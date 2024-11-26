# Manual de Instalación y Uso para Pixel Jumpers (Desarrollado con Eclipse)

## **Descripción del Proyecto**
**Pixel Jumpers** es un videojuego 2D desarrollado en Java. El proyecto se está creando en el entorno de desarrollo Eclipse, y utiliza varias librerías como libGDX.

---

## **Requisitos Previos**
Antes de comenzar, asegúrese de tener instalados los siguientes componentes:

- **Eclipse IDE for Java Developers:** Descárguelo desde [eclipse.org](https://www.eclipse.org/downloads/).
- **Git:** Para clonar el repositorio. [Descargar Git](https://git-scm.com/downloads).

---

## **Pasos de Instalación**

### **1. Clonar el Repositorio**
Abra una terminal o consola y ejecute el siguiente comando para clonar el proyecto en su máquina local:
```bash
git clone https://github.com/y0moGh/Pixel-Jumpers.git
```

### **2. Importar el Proyecto en Eclipse**
1. Abra Eclipse y seleccione **Archivo > Importar**.
2. En la ventana emergente, elija **Proyecto de Gradle**.
3. Navegue a la carpeta donde clonó el repositorio (`Pixel-Jumpers`) y haga clic en **Finalizar**.

### **3. Configurar Dependencias**
El proyecto utiliza Gradle, Eclipse debería gestionar automáticamente las dependencias. De lo contrario:
- Asegúrese de que las librerías de libGDX y cualquier otra dependencia estén correctamente configuradas en el `Build Path` del proyecto.
- Para agregar librerías manualmente:
  - Haga clic derecho en el proyecto en el Explorador de Eclipse.
  - Seleccione **Propiedades > Java Build Path > Librerías > Agregar JARs o Agregar Librerías Externas**.
  - Añada los archivos necesarios.

### **4. Ejecutar el Proyecto**
1. Haga clic derecho sobre el proyecto en Eclipse y seleccione **Run As > Java Application**.
2. Si todo está configurado correctamente, el juego se ejecutará y mostrará la ventana principal.
