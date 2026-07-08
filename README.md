<div align="center">
  <h1>🎰 Dubilets v2.0</h1>
  <p><strong>Un sistema de cajas de recompensa moderno, interactivo y visual para Minecraft 1.21+</strong></p>

  [![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.x-brightgreen.svg)](https://papermc.io/)
  [![FancyHolograms](https://img.shields.io/badge/Dependency-FancyHolograms-blue.svg)](https://modrinth.com/plugin/fancyholograms)
  [![MySQL](https://img.shields.io/badge/Database-MySQL-orange.svg)](https://www.mysql.com/)
</div>

##  Dependencias

Para que el plugin funcione en su máximo esplendor, necesita de:
- **Paper/Spigot 1.21.1+**
- **FancyHolograms** (Indispensable para las letras y textos flotantes de las máquinas).
- **Vault** (Opcional, para ciertas comprobaciones de permisos o economía si se expande).

## 🛠️ Comandos y Permisos

| Comando | Descripción | Permiso |
|---------|-------------|---------|
| `/dubilets help` | Muestra un panel de ayuda interactivo. | `survivaldub.dubilets.admin` |
| `/dubilets create <nombre>` | Crea una nueva máquina mirando a un bloque. | `survivaldub.dubilets.admin` |
| `/dubilets delete <nombre>` | Elimina una máquina existente. | `survivaldub.dubilets.admin` |
| `/dubilets give <jugador> <cantidad>` | Añade "Dubets" (fichas) a un jugador. | `survivaldub.dubilets.admin` |
| `/dubilets take <jugador> <cantidad>` | Resta Dubets a un jugador. | `survivaldub.dubilets.admin` |
| `/dubilets set <jugador> <cantidad>` | Fija el balance de Dubets de un jugador. | `survivaldub.dubilets.admin` |
| `/dubilets info <jugador>` | Consulta cuántos Dubets tiene un jugador. | `survivaldub.dubilets.admin` |

> **Nota para jugadores:** Los usuarios estándar no necesitan comandos. Simplemente deben hacer **Clic Derecho** sobre el bloque de la máquina teniendo los *Dubets* necesarios en su cuenta para empezar a jugar.

##  Configuración (config.yml)

El plugin permite añadir un número ilimitado de premios, cada uno con comandos y visuales propios.

```yaml
dubilets:
  enable: true
  prizes:
    '1':
      name: "&bEspada de Diamante"
      probability: 20.0
      icon: "DIAMOND_SWORD"    # Aparecerá como un ItemDisplay nativo rotando
      category: "COMMON"
      commands:
        - "give %player% diamond_sword 1"
    '2':
      name: "&aRango VIP"
      probability: 5.0
      icon: "EMERALD"
      category: "RARE"
      commands:
        - "lp user %player% parent add vip"
```

##  Instalación

1. Descarga el archivo compilado `Dubilets.jar`.
2. Súbelo a la carpeta `plugins/` de tu servidor.
3. Asegúrate de tener instalada la última versión de **FancyHolograms**.
4. Inicia el servidor por primera vez para generar los archivos de configuración y deténlo.
5. Abre `config.yml`, configura los accesos a tu base de datos **MySQL** (`host`, `port`, `user`, `password`, `name`).
6. Reinicia el servidor. ¡Las máquinas ya están listas para crearse!


**Notas Adicionales**: Minecraft 26.1.2 Funciona perfecto con la api de la 1.21, por eso se usó la misma api, los unicos cambios realizados fueron el /dubilets de ayuda para q se vea más bonito
y la animación del #ICON_... Fue removida porque era nativa de Decent Holograms. Ahora se usa el Display del objeto configurado en el config.yml. 
---
<div align="center">
  <i>Desarrollado para SurvivalDub. Actualizado para la 26.1.2 por Danettw C: </i>
</div>
