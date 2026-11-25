# AGENTS.md · KADAI

Contexto permanente para agentes de IA que trabajen en este repositorio. No sustituye a la documentación del proyecto: la resume y fija las reglas que deben respetarse siempre.

## 1. Qué es KADAI

Aplicación Android nativa para organizar retos diarios entre grupos privados de amigos. Cada día se publica un reto global; los miembros suben una foto o vídeo como prueba, el grupo vota y se calculan puntos y un ranking acumulativo por grupo.

**Documentación obligatoria antes de implementar** (no dupliques su contenido):

| Archivo | Uso |
|---|---|
| `documentacion/proyect_description.md` | Requisitos, reglas de negocio, modelo de datos y fases. |
| `documentacion/Pantallas_y_Tareas.md` | Detalle de tareas `P-xx`, `C-xx`, `T-xx`, `D-xx`, `F-xx`. |
| `documentacion/Reparto.md` | **Fuente de verdad** de quién hace cada tarea. |
| `documentacion/LEEME_EQUIPO.md` | Puesta en marcha y uso de repositorios falsos. |
| `C:\Users\ossam\Downloads\trabajo_final_pamn.md` | Enunciado oficial de la asignatura (referencia académica). |

## 2. Estado técnico

- Kotlin + Jetpack Compose + Material 3, una única `MainActivity`.
- Navigation Compose con rutas centralizadas.
- Tema oscuro KADAI.
- Firebase **no integrado**: todo funciona con `Fake*Repository` y `MockData`.
- Usuario de sesión simulado: `AppContainer.currentUserId` (`user_alex`).
- Dependencias añadidas: ViewModel Compose, Lifecycle Runtime Compose y Coil.

## 3. Arquitectura: hexagonal + Clean Architecture

```
app/                     Contenedor Compose (Scaffold, NavHost, barra inferior)
ui/                      Presentation: pantallas, componentes, ViewModels, UiState
domain/model/            Entidades puras del dominio
domain/repository/       Puertos (interfaces) de acceso a datos
domain/usecase/          Reglas de negocio puras y reutilizables
data/repository/         Adaptadores Fake de los puertos
data/mock/               Datos de ejemplo
di/                      Composición manual de dependencias
```

Reglas de dependencia:

1. `domain` no depende de Android, Compose, Firebase ni de `data`.
2. `ui` depende de `domain` y del contenedor; nunca de `Fake*` ni de `MockData`.
3. `data` implementa interfaces de `domain`.
4. La composición de dependencias se hace solo en `di/AppContainer` y `app/KadaiApplication`.
5. No se crean abstracciones, interfaces o patrones sin necesidad real.

## 4. Convenciones de implementación

### Pantallas y estado
- Cada pantalla vive en `ui/screens/<modulo>/XxxScreen.kt`.
- Patrón: `XxxRoute` (obtiene el ViewModel y recoge el estado) + `XxxScreen` (Composable sin lógica de negocio).
- ViewModel por pantalla con `StateFlow<XxxUiState>`; usa `KadaiViewModel` y `collectAsStateWithLifecycle`.
- Estados explícitos de **carga, error, vacío y contenido**. No dejes botones sin acción.
- Los ViewModels combinan repositorios; no acceden a Firebase ni a `MockData`.
- No pongas datos de ejemplo dentro de las composables (solo en `@Preview`).

### UI/UX
- Usa `KadaiTheme` y los colores de `ui/theme/Color.kt`.
- Reutiliza `ui/components/`: `KadaiTopBar`, `KadaiCard`, `KadaiSectionHeader`, estados, botones, `ChallengeCard`, `GroupCard`, `MemberRow`, `VoteButtons`, `ReactionBar`, `ProofPicker`, `ProofMedia`, `KadaiBottomNav`.
- No dupliques estilos ni crees una segunda barra de navegación.
- Respeta insets (`statusBarsPadding`, `navigationBarsPadding`, `imePadding`) y añade `contentDescription` a iconos e imágenes.
- Textos visibles en español; la migración a `strings.xml` es una tarea transversal pendiente (T-12), no la mezcles con funcionalidades salvo que la tarea lo pida.
- Cada pantalla nueva incluye `@Preview` con datos de ejemplo.

### Datos
- Amplía las interfaces de `domain/repository` y sus `Fake` cuando falte una operación; avisa de cambios compartidos.
- Los mocks son en memoria y reactivos con `StateFlow`; respeta ese comportamiento.
- Mantén las pantallas preparadas para que Firebase sustituya los `Fake` sin reescribir UI ni dominio.

## 5. Git y proceso

- Rama de integración: `development`.
- Trabaja en una rama propia: `ossama/<tarea>`.
- Commits pequeños, verificados y descriptivos: `feat(groups): ...`, `fix(navigation): ...`.
- Antes de commitear: `git status`, revisar el diff y no incluir cambios ajenos, temporales ni credenciales.
- No uses `git reset --hard`, `git checkout .` ni comandos destructivos.
- No implementes tareas de otro integrante sin pedirlo.
- No conectes Firebase hasta que el tablero lo indique.

## 6. Verificación

```powershell
.\gradlew.bat testDebugUnitTest --console=plain
.\gradlew.bat assembleDebug --console=plain
```

- Corrige los errores antes de continuar; no desactives tests ni comprobaciones.
- Añade tests unitarios de dominio cuando implementes reglas de negocio.
- Si una comprobación no puede ejecutarse, dilo con el motivo exacto.

## 7. Reglas para agentes de IA

1. Lee `Reparto.md` y limita el alcance a las tareas asignadas.
2. Inspecciona el código existente antes de editar; respeta estilo, nombres y diseño.
3. No reconstruyas pantallas ni cambies la arquitectura sin justificación técnica.
4. No añadas dependencias si el proyecto ya cubre la necesidad.
5. No declares una tarea terminada si no compila o no se ha verificado.
6. Documenta las decisiones no evidentes; evita comentarios obvios y `TODO` como sustituto de implementación.
7. Para revisiones de código, usa el agente `.opencode/agents/android-reviewer.md` (solo lectura).
