Font Scale (Fabric, Minecraft 1.21.11)

Compilar:
1. Instala JDK 21.
2. Entra a https://fabricmc.net/develop , elige 1.21.11 y descarga la plantilla.
   Copia de mi proyecto: src/ (reemplaza el de la plantilla) y revisa que las versiones
   de gradle.properties coincidan con las de la plantilla.
3. En la carpeta del proyecto: gradlew build  (Windows: .\gradlew.bat build)
4. El .jar queda en build/libs/ (usa el que NO dice -sources).
5. Ponlo en la carpeta mods junto con Fabric Loader y Fabric API para 1.21.11.

Uso en el juego:
  /fontscale 5      -> texto x5
  /fontscale 1.5    -> admite decimales
  /fontscale reset  -> vuelve a x1
  /fontscale        -> muestra el valor actual

Menú con barra (Mod Menu):
  Instala Mod Menu para 1.21.11 -> Mods -> Font Scale -> botón de configuración.
  Tiene una barra de x1 a x100, accesos rápidos (x1, x2, x5, x10) y "Restablecer".
  Esta pantalla nunca se escala a sí misma, así que siempre podrás usarla.
  En gradle.properties verifica modmenu_version en https://modrinth.com/mod/modmenu
