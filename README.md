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

### Leyenda

- **Números en negrita** → coordenadas de fila y columna.
- *Números en cursiva* → costo de la celda.
- `❌` → obstáculo insalvable.
- Celda vacía → costo `1`.