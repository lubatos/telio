#### Stage 1: Build ####
FROM eclipse-temurin:17-jdk AS build
WORKDIR /project

COPY . /project
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

#### Stage 2: Runtime ####
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copiamos la estructura fast-jar
COPY --from=build /project/target/quarkus-app/ /app/

EXPOSE 8080
CMD ["java", "-jar", "/app/quarkus-run.jar"]
