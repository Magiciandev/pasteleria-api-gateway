import os

# Archivo de salida
archivo_salida = "proyecto_completo.txt"

# Extensiones de código que queremos incluir (puedes añadir más si usas otras)
extensiones_validas = ('.java', '.py', '.properties', '.yml', '.yaml', '.sql', '.md', '.xml', '.sh', '.bat')

# Carpetas o archivos que queremos ignorar para que el archivo no pese gigas
ignorar = ['target', '.git', '.mvn', '.vscode', 'node_modules', archivo_salida]

with open(archivo_salida, "w", encoding="utf-8") as outfile:
    for raiz, carpetas, archivos in os.walk("."):
        # Filtrar carpetas a ignorar
        carpetas[:] = [d for d in carpetas if d not in ignorar]
        
        for archivo in archivos:
            if archivo.endswith(extensiones_validas) and archivo not in ignorar:
                ruta_completa = os.path.join(raiz, archivo)
                ruta_relativa = os.path.relpath(ruta_completa, ".")
                
                try:
                    with open(ruta_completa, "r", encoding="utf-8", errors="ignore") as infile:
                        contenido = infile.read()
                        
                        # Escribir un separador claro para que la IA sepa qué archivo es
                        outfile.write(f"\n\n{'='*80}\n")
                        outfile.write(f"ARCHIVO: {ruta_relativa}\n")
                        outfile.write(f"{'='*80}\n\n")
                        outfile.write(contenido)
                except Exception as e:
                    print(f"No se pudo leer {ruta_relativa}: {e}")

print(f"¡Listo! Se ha creado el archivo '{archivo_salida}' con todo tu código.")