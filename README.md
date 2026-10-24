** Trato Hecho - Aplicación Móvil Offline-First

**Trato Hecho** es una aplicación Android desarrollada en Kotlin que permite la gestión de clientes y cotizaciones/tratos comerciales con un enfoque *offline-first*. La aplicación garantiza el almacenamiento local permanente de la información y cuenta con una simulación de sincronización para enviar datos a un servidor remoto.

---

## Integrantes del Equipo

* **Nombre Estudiante 1**:*Juanjose Mardones*
* **Nombre Estudiante 2**:*Benjamin Sepulveda*

---

## Funcionalidades Implementadas

* **Arquitectura MVVM**: Separación clara de responsabilidades entre la interfaz de usuario, lógica de negocio y capa de datos mediante `ViewModel` y `ViewModelFactory`.
* **Persistencia de Datos (Room Database)**:
  * Almacenamiento local mediante base de datos SQLite administrada con Room.
  * Relación entre entidades: `Cliente` (1) a `Cotizacion` (N) vinculadas por `clienteId`.
* **Interfaz Reactiva con Jetpack Compose**:
  * **Lista de Clientes (`ClienteScreen`)**: Muestra los clientes registrados y su estado de sincronización.
  * **Formulario de Cliente (`FormularioClienteScreen`)**: Registro de nuevos clientes.
  * **Detalle del Cliente (`DetalleClienteScreen`)**: Consulta de datos específicos del cliente y listado de sus cotizaciones asociadas.
  * **Formulario de Cotización (`FormularioCotizacionScreen`)**: Adición de cotizaciones vinculadas a un cliente específico.
* **Navegación Dinámica (`NavGraph`)**: Flujo de navegación entre pantallas pasando argumentos dinámicos por ID.
* **Mecanismo Offline-First y Sincronización**:
  * Indicadores visuales en pantalla sobre el estado del registro (`Pendiente de sincronización` / `Sincronizado`).
  * Botón de sincronización en la barra superior (`TopAppBar`) que simula la actualización de datos locales hacia el servidor backend.

---

## Requisitos del Sistema

* **Android Studio**: Jellyfish | Koala o superior.
* **Versión de SDK Mínima**: API 24 (Android 7.0 Nougat).
* **Versión de SDK Objetitvo**: API 34 / 35.
* **JDK**: Java 17 o superior.

---

## Pasos para Ejecutar el Proyecto

1. **Clonar el repositorio**:
   ```bash
   git clone [https://github.com/TU_USUARIO/CasoTratoHecho.git](https://github.com/TU_USUARIO/CasoTratoHecho.git)
2. Sincronizar Gradle:

Deja que Android Studio descargue las dependencias necesarias. Si no inicia automáticamente, ve a File > Sync Project with Gradle Files.

3. Ejecutar en Emulador o Dispositivo Físico:

Asegúrate de tener configurado un emulador Android (AVD) o un dispositivo físico conectado en modo Depuración USB.

Presiona el botón Run  en la barra superior de Android Studio o usa el atajo
