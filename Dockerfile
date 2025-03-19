FROM maven:3.9.9-eclipse-temurin-21 AS build
COPY . /app/

# Assemble the skeletons
WORKDIR /app/assembly
RUN \
 --mount=type=cache,target=/root/.m2 \
 mvn -C clean package

# Build the generator
WORKDIR /app/walking-skeleton-generator
RUN \
 --mount=type=cache,target=/root/.m2 \
 mvn -C clean package -Pproduction
RUN mv target/*.jar target/generator.jar

# Generate config files
RUN echo "walking-skeleton.templates.flow.url=file:/walking-skeleton-flow.zip" >> /app/application.properties
RUN echo "walking-skeleton.templates.react.url=file:/walking-skeleton-react.zip" >> /app/application.properties
RUN echo "walking-skeleton.generator.context-path=/walking-skeleton-generator" >> /app/application.properties
RUN echo "server.port=8080" >> /app/application.properties

# Create the run image
FROM eclipse-temurin:21-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY --from=build /app/assembly/target/walking-skeleton-flow.zip /
COPY --from=build /app/assembly/target/walking-skeleton-react.zip /
COPY --from=build /app/walking-skeleton-generator/target/generator.jar /
COPY --from=build /app/application.properties /

ENTRYPOINT ["java", "-Xmx256m", "-jar", "/generator.jar"]
EXPOSE 8080
