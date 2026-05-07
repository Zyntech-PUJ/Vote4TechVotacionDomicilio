# Build stage
FROM maven:3.9-eclipse-temurin-17 as builder

WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /build/target/*.jar votacion-backend.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "votacion-backend.jar"]
