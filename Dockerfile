# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copia archivos de configuración de Maven
COPY pom.xml .

# Descarga dependencias (cacheable layer)
RUN mvn dependency:go-offline

# Copia código fuente
COPY src ./src

# Compila la aplicación
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copia el JAR compilado desde el builder
COPY --from=builder /app/target/ecommerce-monolith-*.jar app.jar

# Expone el puerto (Render asigna el puerto via variable de entorno PORT)
EXPOSE 8080

# Comando de inicio - las variables de entorno se pasan automáticamente
CMD ["java", "-jar", "app.jar"]
