# Stage 1: Build aplikasi menggunakan Maven dan JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Salin file konfigurasi dependensi pom.xml
COPY pom.xml .

# Unduh dependensi terlebih dahulu agar ter-cache oleh Docker
RUN mvn dependency:go-offline -B

# Salin source code aplikasi
COPY src ./src

# Build aplikasi Spring Boot menjadi file JAR tanpa menjalankan unit test
RUN mvn clean package -DskipTests

# Stage 2: Jalankan aplikasi menggunakan JRE 21 yang lebih ringan
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Salin file JAR hasil build dari Stage 1
COPY --from=build /app/target/*.jar app.jar

# Ekspos port default (Spring Boot akan mendeteksi env $PORT yang diberikan Render)
EXPOSE 8080

# Jalankan aplikasi Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
