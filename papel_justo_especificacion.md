# Especificación Funcional — App Android “Papel Justo”

## Objetivo

Desarrollar una aplicación Android en español orientada al mercado argentino que permita comparar ofertas de papel higiénico de distintos supermercados y marcas usando una métrica unificada de costo real.

La aplicación debe ayudar especialmente a compradores adultos mayores a identificar fácilmente cuál producto ofrece mayor valor por dinero, incluso cuando las presentaciones usan diferentes métricas:

- Precio por rollo
- Precio por metro
- Precio por hoja
- Cantidad de capas
- Tamaño de cada hoja
- Cantidad de rollos por paquete
- Largo total del rollo
- Área total de papel

La app debe transformar todas las ofertas a una unidad estándar comparable y mostrar claramente cuál es la opción más conveniente.

---

# Público Objetivo

## Perfil principal

- Personas mayores de 55 años
- Compradores frecuentes de supermercado
- Usuarios con poca experiencia tecnológica
- Necesitan interfaces simples y claras
- Necesitan texto grande y navegación intuitiva

## Requisitos UX para adultos mayores

### Diseño visual

- Tipografía grande
- Alto contraste
- Botones grandes
- Pocas pantallas
- Flujo lineal
- Íconos claros
- Evitar exceso de texto técnico

### Accesibilidad

- Soporte para Android accessibility services
- Compatible con lector de pantalla
- Modo oscuro opcional
- Posibilidad de aumentar tamaño de fuente

---

# Problema a Resolver

Las marcas venden papel higiénico usando diferentes unidades:

| Producto | Precio | Información |
|---|---|---|
| Pack A | ARS 5000 | 4 rollos x 30m |
| Pack B | ARS 7200 | 12 rollos x 20m |
| Pack C | ARS 4500 | 300 hojas por rollo |
| Pack D | ARS 8000 | 16 rollos doble hoja |

El consumidor no puede comparar fácilmente cuál conviene más.

La app debe convertir todos los productos a una métrica estándar:

- Precio por metro cuadrado de papel
- Precio por 1000 hojas equivalentes
- Índice de valor ponderado

## Métrica Principal Recomendada

### “Costo por metro cuadrado efectivo”

Fórmula base:

```text
Área total = largo total × ancho de hoja × cantidad de capas
Costo unitario = precio / área total
```

La app debe mostrar:

```text
ARS por metro cuadrado efectivo
```

Mientras menor sea el valor:

✅ Mejor compra

---

# Inputs Permitidos

La app debe aceptar distintos esquemas de información porque los empaques argentinos son inconsistentes.

## Inputs posibles

### Básicos

- Precio
- Cantidad de rollos

### Opcionales

- Metros por rollo
- Cantidad de hojas
- Ancho de hoja
- Largo de hoja
- Cantidad de capas
- Tipo:
  - Hoja simple
  - Doble hoja
  - Triple hoja

---

# Estrategia de Normalización

## Casos ideales

Si existen:

- largo del rollo
- ancho de hoja
- capas

Entonces:

```text
Área efectiva = largo × ancho × capas
```

## Casos parciales

Si faltan datos:

### Heurísticas

Usar valores promedio del mercado argentino:

| Tipo | Ancho estimado |
|---|---|
| Estándar | 10 cm |
| Premium | 10.5 cm |

| Tipo | Factor capas |
|---|---|
| Simple hoja | 1 |
| Doble hoja | 1.8 |
| Triple hoja | 2.5 |

La app debe indicar visualmente:

- “Estimado”
- “Dato incompleto”

---

# Funcionalidades Principales

## 1. Comparador de Productos

Pantalla principal con lista de productos.

Cada tarjeta debe mostrar:

- Marca
- Precio
- Cantidad de rollos
- Métrica normalizada
- Indicador visual:
  - 🟢 Mejor opción
  - 🟡 Media
  - 🔴 Cara

---

## 2. Carga Manual Simplificada

Formulario extremadamente simple.

### Modo básico

Campos:

- Precio
- Rollos
- Metros por rollo

### Modo avanzado

Mostrar más campos opcionales.

---

## 3. Ranking Automático

La app ordena automáticamente por:

```text
Menor costo por área efectiva
```

---

## 4. Etiqueta “Mejor Compra”

El producto más conveniente recibe:

```text
🏆 Mejor Compra
```

---

## 5. Historial Local

Guardar comparaciones recientes localmente.

No requiere login.

---

## 6. Escaneo Futuro (Arquitectura preparada)

Diseñar arquitectura para futura integración con:

- OCR
- Cámara
- Código de barras

Aunque no se implemente inicialmente.

---

# Arquitectura Técnica

## Plataforma

- Android nativo
- Kotlin + Jetpack Compose

### Recomendación

Usar:

- Kotlin
- Jetpack Compose
- Material Design 3

---

## Persistencia

- Room Database

---

## Arquitectura sugerida

```text
MVVM
```

Capas:

- UI
- ViewModel
- Domain
- Repository
- Data

---

# Algoritmo Central

## Función principal

```kotlin
fun calculateCostPerSquareMeter(
    price: Double,
    rolls: Int,
    metersPerRoll: Double,
    sheetWidthMeters: Double,
    plyFactor: Double
): Double
```

## Fórmula

```text
totalArea =
rolls × metersPerRoll × sheetWidthMeters × plyFactor

cost =
price / totalArea
```

---

# Reglas de Negocio

## Menor valor gana

- Menor costo por área = mejor compra

## Datos faltantes

Si faltan parámetros:

- usar estimaciones
- informar confianza del cálculo

---

# Sistema de Confianza

## Alta confianza

Todos los datos completos.

## Media confianza

Falta 1 dato.

## Baja confianza

Faltan múltiples datos.

Mostrar:

```text
Precisión estimada:
Alta / Media / Baja
```

---

# Diseño UI

## Pantalla Inicio

### Componentes

- Botón “Agregar Producto”
- Lista comparativa
- Botón grande flotante

---

## Tarjetas de producto

### Mostrar

```text
Elite 12 rollos
$8500

$42 / m² efectivo

🟢 Mejor compra
```

---

## Colores

### Semáforo

- Verde → excelente
- Amarillo → medio
- Rojo → caro

---

## Navegación

Máximo:

- 2 niveles de navegación

### Impacto en diseño

- La pantalla de carga (básica + avanzada) es un único formulario expandible — no una pantalla separada.
- Flujo: Inicio (lista comparativa) → Formulario (un nivel, con sección avanzada colapsable).
- Nunca más de 2 pantallas en el stack de navegación.

---

# Internacionalización

## Idioma inicial

Español argentino.

Ejemplos:

- “rollos”
- “doble hoja”
- “más conveniente”

---

# Requisitos No Funcionales

## Rendimiento

- Funcionar offline
- Abrir en menos de 2 segundos

## Compatibilidad

- Android 9+

## Privacidad

- No requiere cuenta
- Datos locales únicamente

---

# Futuras Mejoras

## OCR

Escanear paquetes automáticamente.

## Integración supermercados

Comparar precios online.

## Compartir comparación

Enviar resultados por WhatsApp.

## Modo supermercado

Fuente extra grande y alto contraste.

---

# Entregables Esperados del Agente AI

## Debe generar

### 1. Código Android completo

- Kotlin
- Jetpack Compose
- MVVM

### 2. Base de datos local

- Room

### 3. Pantallas UI

- Diseño accesible
- Componentes reutilizables

### 4. Motor de cálculo

- Normalización
- Heurísticas

### 5. Tests

- Unit tests
- Validation tests

### 6. APK debug

Compilable y ejecutable.

---

# Prompt de Desarrollo para el Agente

## Objetivo del Agente

Construir una app Android accesible para adultos mayores en Argentina que compare productos de papel higiénico usando métricas normalizadas y determine automáticamente cuál es la opción más conveniente económicamente.

La aplicación debe:

- Ser extremadamente simple
- Funcionar offline
- Tener UI clara y grande
- Soportar datos incompletos
- Explicar visualmente la recomendación
- Priorizar accesibilidad

El agente debe generar:

- Arquitectura limpia
- Código mantenible
- UI moderna
- Persistencia local
- Algoritmo de comparación robusto
- Componentes reutilizables
- Preparación para futuras integraciones OCR/barcode

---

# Nombre Sugerido

Opciones:

- Papel Justo
- RolloSmart
- Conviene Más
- Papelómetro
- Mejor Precio Papel

## Recomendación principal

### “Papel Justo”
