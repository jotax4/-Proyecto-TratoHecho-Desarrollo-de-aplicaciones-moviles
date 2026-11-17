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



## Arquitectura y Separación de Repositorios

El ecosistema **Trato Hecho** está dividido en dos repositorios independientes (Arquitectura Cliente-Servidor desacoplada):

* **Frontend Móvil (Android):** [Enlace al repositorio Android](https://github.com/tu-usuario/tratohecho-android) — Desarrollado en Kotlin con Jetpack Compose, Room (caché local) y Retrofit.
* **Backend API REST (Spring Boot):** [Enlace al repositorio Backend](https://github.com/tu-usuario/tratohecho-backend) — Desarrollado en Kotlin con Spring Boot, Spring Data JPA y MySQL (`tratohecho_db`).

### ¿Por qué el Backend está en un repositorio aparte?

1. **Desacoplamiento tecnológico (Separation of Concerns):** La aplicación móvil y el servidor operan como sistemas autónomos que se comunican exclusivamente mediante contratos REST/JSON. Separarlos evita mezclar configuraciones y dependencias de distintos entornos (Android SDK/Gradle frente a JDK/Spring Boot).
2. **Ciclos de desarrollo y despliegue independientes:** Permite realizar mejoras, correcciones o despliegues en la API y la base de datos sin afectar el historial de versiones ni requerir recompilar el cliente Android (y viceversa).
3. **Control de versiones limpio:** Cada repositorio mantiene un historial de commits enfocado únicamente en su propia capa, facilitando la revisión de código, el mantenimiento y la integración continua (CI/CD).
4. **Escalabilidad y reutilización:** Al estar aislado, el servicio backend queda preparado para ser consumido por futuros clientes (por ejemplo, un panel administrativo web) sin duplicar lógica de negocio.





## cobertura unitaria >98% en viewmodel y repository
<img width="1290" height="1078" alt="image" src="https://github.com/user-attachments/assets/3b746944-1d96-4365-9911-76077e77c04e" />







## keystore y APK firmado en carpeta keystore
<img width="642" height="450" alt="image" src="https://github.com/user-attachments/assets/a2503a08-1c93-4a4f-8f8f-5c99eba22d4c" />
