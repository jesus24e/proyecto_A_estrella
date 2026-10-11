## Proyecto tipo A

Resolver con A* el siguiente laberinto. Las celdas marcadas X, representan obstáculos insalvables.

El costo real por ir a cada celda adyacente es 1, excepto los explícitamente marcados con algún valor.

El estado inicial y final son asignados por el usuario.

|     | **1** | **2** | **3** | **4** | **5** | **6** | **7** | **8** | **9** | **10** |
|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:----:|
| **1**  |   |   | *4* |   | *2* |   |   | ❌ |   |   |
| **2**  | *4* |   | ❌ |   | *3* | ❌ | *2* |   |   | ❌ |
| **3**  | ❌ |   | ❌ | *2* |   | *4* | *3* | ❌ |   |   |
| **4**  | *3* | *3* | ❌ |   |   | *3* |   |   | *2* | ❌ |
| **5**  |   | *5* | *3* |   | ❌ |   | ❌ | ❌ |   |   |
| **6**  |   |   | ❌ | ❌ | *2* | ❌ |   | *4* |   | ❌ |
| **7**  | ❌ | ❌ |   | ❌ |   | *5* | *2* |   |   |   |
| **8**  |   | *3* |   | *2* |   | *3* | *2* |   | *3* | ❌ |
| **9**  |   |   | ❌ | ❌ | ❌ |   | ❌ | ❌ |   |   |
| **10** |   |   | *3* |   | *3* |   |   |   | *2* |   |

### Ejecución

```bash
javac Laberinto.java
java Laberinto
```

El programa pide la fila y columna de inicio y de fin (de 1 a 10) y después el modo:

- `a` → resuelve con A* (movimiento en 4 direcciones, heurística Manhattan) y muestra la ruta, los pasos y el costo total.
- `m` → mueve al jugador manualmente con `u`, `d`, `l`, `r` (`q` para salir).

El costo de la ruta es la suma de los costos de las celdas a las que se entra; la celda de inicio no se cuenta.

### Leyenda

- **Números en negrita** → coordenadas de fila y columna.
- *Números en cursiva* → costo de la celda.
- `❌` → obstáculo insalvable.
- Celda vacía → costo `1`.