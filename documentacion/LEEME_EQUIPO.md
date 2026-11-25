# Kadai · Léeme antes de empezar (mensaje para el equipo)

Hola equipo:

El proyecto ya tiene la **base** montada. No hay que esperar a Firebase ni a nadie para empezar: podéis coger tareas ahora mismo. Este documento explica lo que ya existe, lo que falta y cómo elegir vuestro trabajo.

Tenéis también otros tres archivos de documentación:

| Archivo | Para qué sirve |
|---|---|
| `proyect_description.md` | Qué hace la aplicación: reglas, requisitos RF-01…RF-26, modelo de datos, fases y criterios del MVP. |
| `Pantallas_y_Tareas.md` | El detalle de **todas** las tareas (`P-xx` pantallas, `C-xx` componentes, `T-xx` técnicas, `D-xx` decisiones, `F-xx` Firebase). |
| `Reparto.md` | **El tablero**: aquí se elige la tarea y se escribe el nombre de quien la hace. |

---

## 1. En qué rama trabajamos

Rama: **`development`** (creada desde `master` y ya subida a GitHub).

Trabaja en una rama tuya sacada de `development`, por ejemplo:

```
tu-nombre/P-03-registro
```

Cuando termines, abre un **Pull Request a `development`**.

---

## 2. Lo que ya hay en el proyecto (no hay que hacerlo otra vez)

- Proyecto Android nativo, **Kotlin + Jetpack Compose** (Material 3), tema oscuro KADAI. Hay una sola Activity (`MainActivity`), como es normal en Compose.
- `app/KadaiApp.kt`: estructura general con `Scaffold`, `NavHost` y barra inferior compartida (`KadaiBottomNav`). `MainActivity` solo aplica el tema y llama a `KadaiApp`.
- `ui/navigation/Routes.kt` + `ui/navigation/KadaiNavHost.kt`: navegación centralizada, pero **solo están las rutas LOGIN y HOME**. Cada pantalla nueva tendrá que registrar su ruta ahí (avisad en el grupo para no editar todos el mismo archivo a la vez).
- Dos pantallas con el **diseño ya hecho**:
  - `ui/screens/login/LoginScreen.kt` — login, Google, registro y "olvidé contraseña" como callbacks todavía sin conectar.
  - `ui/screens/home/HomeScreen.kt` — home con grupos, reto del día, actividad y estadísticas de ejemplo.
  - Están maquetadas con datos falsos: hay que adaptarlas para que reciban datos reales del repositorio y para que naveguen de verdad.
- `ui/components/`: `KadaiTextField`, `PulsingDot`, `RadialAccentBackground`, `MetaChip` y `KadaiBottomNav` (barra de 5 ítems: Inicio, Grupos, Reto, Avisos, Perfil).
- `domain/model/`: 12 modelos de dominio ya creados (User, Group, Membership, Challenge, DailyChallenge, Proof, GroupValidation, Vote, Score, Reaction, Favorite y Notification).
- `domain/repository/`: 6 interfaces (`UserRepository`, `GroupRepository`, `ChallengeRepository`, `ProofRepository`, `RankingRepository`, `NotificationRepository`). Definen qué datos se pueden pedir, sin decir de dónde vienen.
- `data/repository/`: 6 implementaciones `Fake*Repository`. ← **Lo importante**, explicado en el apartado 3.
- `data/mock/MockData.kt`: datos de prueba (5 usuarios, 4 grupos, retos, pruebas, votos, puntuaciones, reacciones, favoritos y notificaciones). El usuario actual de prueba es `user_alex`.
- `di/AppContainer.kt` + `app/KadaiApplication.kt`: contenedor manual que crea los 6 repositorios falsos. Ya está declarado en el `AndroidManifest`.
- `build.gradle.kts`: Navigation Compose ya está añadido (falta añadir Coil para imágenes y las dependencias de ViewModel).

---

## 3. Los repositorios falsos: trabaja sin backend

> **No hace falta Firebase, Firestore ni Storage** para desarrollar las pantallas.

Los repositorios falsos se comportan como los reales: devuelven `Flow` con los datos de `MockData` y guardan los cambios en memoria (`MutableStateFlow`). Puedes subir una prueba, votar, crear un grupo, marcar notificaciones como leídas… y la UI reacciona igual que con un backend.

Están en:

```
app/src/main/java/com/example/kadai/data/repository/Fake*Repository.kt
```

### Cómo se usa desde tu ViewModel

Todavía no hay ningún ViewModel: tendrás que crear el de tu pantalla.

```kotlin
class MiViewModel(application: Application) : AndroidViewModel(application) {
    private val container = (application as KadaiApplication).container
    private val groupRepository = container.groupRepository
    private val challengeRepository = container.challengeRepository
}
```

### Los 6 repositorios disponibles

| Repositorio | Métodos principales |
|---|---|
| `UserRepository` | `observeUser`, `getUser`, `saveUser`, `updateStreak` (perfil: nombre, foto, racha) |
| `GroupRepository` | `observeGroupsForUser`, `getGroup`, membresías, `createGroup`, `updateGroup`, `addMember`, `removeMember` |
| `ChallengeRepository` | `observeActiveChallenge`, `getActiveChallenge`, `getChallenge`, `getChallengeHistory` |
| `ProofRepository` | `observeProofsForChallenge`, `getProof`, `getProofsForGroup`, `submitProof`, `castVote`, `getValidationForGroup` |
| `RankingRepository` | `observeRankingForGroup`, `getTotalPointsForUser`, `getScoresForChallenge` |
| `NotificationRepository` | `observeNotifications`, `getUnreadCount`, `markAsRead`, `markAllAsRead` |

### Cosas que debes saber

- Los cambios se pierden al cerrar/reiniciar la app. Es normal, son falsos.
- No hay repositorio específico de reacciones, favoritos, propuestas de retos ni administración; esos datos están en `MockData`. Si tu pantalla los necesita, amplía la interfaz correspondiente y su `Fake` (avisa antes).
- El login de ahora es **simulado**: al pulsar "Iniciar sesión" se navega a Home sin comprobar nada. La autenticación real vendrá con Firebase, al final.
- Cuando llegue Firebase, se cambiarán las implementaciones en `AppContainer` y el dominio/las pantallas no tendrán que tocarse. Por eso es importante **no llamar a Firebase desde los Composables**.

---

## 4. Lo que falta por hacer

- **Crear las 25 pantallas** (`P-01` a `P-25`): splash, registro, recuperar contraseña, detalle del reto, subir prueba, pruebas del grupo, detalle de prueba, grupos, crear grupo, invitar/unirse, gestionar miembros, ranking, historial, perfil, editar perfil, galerías, favoritos, notificaciones, proponer reto, administrar catálogo y ajustes. El objetivo es **terminar todas las pantallas antes de integrar Firebase**.
- Extraer/crear componentes compartidos (`C-01` a `C-12`): botones, TopBar, tarjetas, carga de imágenes, estados de carga/error/vacío, selector de foto/vídeo, etc.
- Crear los **ViewModels** y los estados de UI (carga, error, éxito) de cada pantalla, usando los repositorios falsos.
- Registrar cada pantalla nueva en `Routes.kt` y `KadaiNavHost.kt`, y conectar la barra inferior con sus destinos.
- Permisos del manifest (internet, fotos/vídeos, cámara, notificaciones), textos en `strings.xml`, insets del sistema y accesibilidad.
- Cerrar las decisiones pendientes `D-01`…`D-10` (zona horaria, límites de vídeo, privacidad de galerías, etc.).
- **Al final**: Firebase (`F-01`…`F-08`): Auth, Firestore, Storage, Cloud Functions y notificaciones push. No empezar esto hasta que las pantallas estén listas.
- Pruebas y demo del MVP (`T-16`…`T-19`).

---

## 5. Cómo elegir tus tareas (esto es lo que queremos que hagáis)

1. Lee `proyect_description.md` y `Pantallas_y_Tareas.md`.
2. Abre `documentacion/Reparto.md` y mira los módulos:

   | Módulo | Estado |
   |---|---|
   | 3.1 Autenticación | POR ELEGIR |
   | 3.2 Inicio y reto diario | OSSAMA |
   | 3.3 Grupos y ranking | OSSAMA |
   | 3.4 Perfil y funcionalidades sociales | POR ELEGIR |

   También hay tareas transversales (ViewModels, componentes, permisos…).

3. Escribe **tu nombre** en la columna "Responsable" de la tarea que cojas y pon el estado "En curso". Si quieres un módulo entero, pon tu nombre en el título del módulo.
4. Avisa en el grupo de chat que has cogido esa tarea (así nadie la duplica) y crea tu rama desde `development`.
5. Cuando acabes, marca "Hecho" en el tablero y pasa el PR a `development`.

> No hace falta esperar a que otros terminen: las pantallas son independientes y los datos falsos ya están listos.

---

## 6. Reglas para no pisarnos

- Antes de editar `Routes.kt` o `KadaiNavHost.kt`, avisa en el grupo.
- No modifiquéis `AppContainer` ni los `Fake` salvo que vuestra tarea lo pida; si hace falta, avisad también.
- Nada de Firebase todavía: usad los repositorios falsos.
- Cada Composable lleva su `@Preview` con datos de ejemplo y recibe callbacks para navegar.
- Usad los colores y componentes del tema (`ui/theme`, `ui/components`); no dupliquéis estilos.
- Si veis que una tarea ya tiene responsable, no la cojáis: elegid otra.

---

## 7. Cuándo una tarea está terminada

- [ ] La pantalla compila y se ve en el Preview.
- [ ] Funciona con datos de `MockData`, sin Firebase.
- [ ] Tiene estados de carga, error y lista vacía.
- [ ] Respeta la barra del sistema (`statusBarsPadding` / `navigationBarsPadding`) y los iconos tienen `contentDescription`.
- [ ] Su ruta está en `KadaiNavHost` y se puede abrir desde la navegación.
- [ ] El nombre está puesto en `Reparto.md` y el PR está abierto a `development`.

---

¡Adelante, coged vuestras tareas en [`documentacion/Reparto.md`](Reparto.md)!
