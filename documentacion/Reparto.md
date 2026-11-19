# Kadai · Tablero de reparto de tareas

> **Lée antes:** `proyect_description.md` (qué hace la app) y `Pantallas_y_Tareas.md` (detalle de cada tarea: `P-xx` pantallas, `C-xx` componentes, `T-xx` técnicas, `D-xx` decisiones, `F-xx` Firebase).
>
> **Este archivo es el tablero oficial:** aquí se elige la tarea y se pone el nombre. **No hay reparto obligatorio**: coge lo que quieras de los módulos "POR ELEGIR" o especialízate en un módulo entero.
>
> ⚠️ Trabajamos en la rama `development`. **No conectes Firebase todavía**: hay repositorios falsos listos para trabajar sin backend.

---

## Cómo elegir tu tarea

1. Lee los dos archivos de arriba y este tablero.
2. Elige una o varias tareas de los módulos de abajo (o de "Tareas transversales").
3. **Escribe tu nombre** en la columna "Responsable" y cambia el estado a `En curso`.
4. Haz tu trabajo en una rama creada desde `development` (p. ej. `tu-nombre/P-03-registro`) y abre un PR a `development` cuando termine.
5. Al terminar, marca `Hecho` y pega el enlace del PR en la última columna.
6. Si tu pantalla necesita una ruta nueva, añádela en `Routes.kt` y `KadaiNavHost.kt` **avisando antes en el grupo** para que dos personas no editen lo mismo a la vez.

**Objetivo común:** terminar **todas las pantallas antes de integrar Firebase**.

---

## ✅ Ya está hecho (no elegir estas tareas)

| Tarea | Qué hay | Hecho por |
|---|---|---|
| T-01 | `MainActivity` limpio + `KadaiApp` con `Scaffold`, `NavHost` y barra inferior compartida | OSSAMA |
| T-03 | Dependencia `androidx.navigation.compose` añadida | OSSAMA |
| T-06 | Estructura por capas: `app/`, `ui/`, `domain/`, `data/`, `di/` | OSSAMA |
| T-07 | 12 modelos de dominio en `domain/model/` | OSSAMA |
| T-08 | 6 interfaces de repositorio en `domain/repository/` | OSSAMA |
| T-09 | **6 repositorios falsos + `MockData` + `AppContainer`** (funcionan sin Firebase) | OSSAMA |
| C-01 | `KadaiBottomNav.kt` (barra inferior extraída) | OSSAMA |
| — | Tema KADAI, `LoginScreen` y `HomeScreen` con diseño y datos de ejemplo | OSSAMA |
| T-02 | 🟡 Parcial: `Routes.kt` y `KadaiNavHost.kt` existen, pero **solo con LOGIN y HOME** | OSSAMA |

> Nota: en `Pantallas_y_Tareas.md` algunas de estas tareas siguen apareciendo como pendientes. **Manda este tablero** para el estado real.

---

## 🎯 Módulos para elegir

### 3.1. Autenticación — POR ELEGIR

| ID | Tarea | Responsable | Estado |
|---|---|---|---|
| P-01 | Splash / arranque |  | ⬜ Pendiente |
| P-02 | Inicio de sesión (mejorar el existente: validación, iconos, conectar callbacks) |  | ⬜ Pendiente |
| P-03 | Registro |  | ⬜ Pendiente |
| P-04 | Recuperar contraseña |  | ⬜ Pendiente |
| C-03 | Unificar campos de texto y toggle de contraseña |  | ⬜ Pendiente |
| C-11 | Iconos/assets reales (logo, Google, ojo de contraseña) |  | ⬜ Pendiente |

### 3.2. Inicio y reto diario — OSSAMA

| ID | Tarea | Responsable | Estado |
|---|---|---|---|
| P-05 | Home (adaptar: quitar datos hardcodeados y navegación interna) |  | 🟡 Diseño listo |
| P-06 | Detalle del reto |  | ⬜ Pendiente |
| P-07 | Subir / sustituir prueba |  | ⬜ Pendiente |
| P-08 | Pruebas del grupo (listado + votación) |  | ⬜ Pendiente |
| P-09 | Detalle de prueba (multimedia, voto, reacciones, favorito) |  | ⬜ Pendiente |
| P-16 | Historial de retos y resultados |  | ⬜ Pendiente |
| P-23 | Proponer reto |  | ⬜ Pendiente |
| C-10 | `ChallengeCard` (extraer de Home) |  | ⬜ Pendiente |
| C-12 | `ProofPicker` (selector foto/vídeo) |  | ⬜ Pendiente |

### 3.3. Grupos y ranking — OSSAMA

| ID | Tarea | Responsable | Estado |
|---|---|---|---|
| P-10 | Grupos (listado, crear, unirse por código) |  | ⬜ Pendiente |
| P-11 | Crear grupo |  | ⬜ Pendiente |
| P-12 | Detalle del grupo |  | ⬜ Pendiente |
| P-13 | Invitar / unirse a grupo |  | ⬜ Pendiente |
| P-14 | Gestionar miembros |  | ⬜ Pendiente |
| P-15 | Ranking del grupo |  | ⬜ Pendiente |
| C-08 | `GroupCard` / `MemberRow` |  | ⬜ Pendiente |

### 3.4. Perfil, galerías y funcionalidades sociales — POR ELEGIR

| ID | Tarea | Responsable | Estado |
|---|---|---|---|
| P-17 | Perfil |  | ⬜ Pendiente |
| P-18 | Editar perfil |  | ⬜ Pendiente |
| P-19 | Galería personal |  | ⬜ Pendiente |
| P-20 | Galería de otro usuario |  | ⬜ Pendiente |
| P-21 | Favoritos |  | ⬜ Pendiente |
| P-22 | Notificaciones |  | ⬜ Pendiente |
| P-24 | Administrar catálogo de retos (admin de la app) |  | ⬜ Pendiente |
| P-25 | Configuración / cuenta |  | ⬜ Pendiente |
| C-09 | `VoteButtons` / `ReactionBar` |  | ⬜ Pendiente |

---

## 🧩 Tareas transversales (también se pueden elegir)

| ID | Tarea | Responsable | Estado |
|---|---|---|---|
| T-04 | Añadir Coil (imágenes) y dependencias de ViewModel |  | ⬜ Pendiente |
| T-10 | ViewModels + `UiState` por pantalla (todavía no hay ninguno) |  | ⬜ Pendiente |
| T-11 | Permisos en `AndroidManifest` (INTERNET, multimedia, cámara, notificaciones) |  | ⬜ Pendiente |
| T-12 | Migrar textos a `strings.xml` |  | ⬜ Pendiente |
| T-13 / T-14 | Insets del sistema y accesibilidad |  | ⬜ Pendiente |
| T-15 | Mapa de navegación completo (qué callback lleva a cada pantalla) |  | ⬜ Pendiente |
| C-02 / C-04 / C-05 / C-06 / C-07 | Botones, TopBar, tarjetas, carga de imágenes, estados carga/error/vacío |  | ⬜ Pendiente |
| D-01 … D-10 | Decisiones pendientes del documento (zonas horarias, límites de vídeo, privacidad de galerías…) |  | ⬜ Pendiente |
| T-16 … T-19 | Pruebas unitarias, UI, integración y demo |  | Más adelante |
| F-01 … F-08 | Firebase (proyecto, Firestore, Storage, Functions, FCM) |  | **Después de las pantallas** |

---

## 📌 Reglas para todos

- **Sin Firebase aún.** Usa los repositorios falsos (`Fake*Repository`) y `MockData`. Cuando llegue el momento, se cambia solo `AppContainer`.
- Cada pantalla debe funcionar **con datos de ejemplo** y exponer callbacks, sin llamar a Firebase directamente.
- Antes de marcar `Hecho`, revisa la lista "Definición de terminado" al final de `Pantallas_y_Tareas.md`.
- Si una tarea ya la tiene otra persona, **no la dupliques**: elige otra o pregunta.
- Si haces una pantalla de un módulo ajeno, escribe tu nombre en esa fila y avisa al responsable del módulo.
