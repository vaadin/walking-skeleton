FROM eclipse-temurin:21-jdk AS build
ENV HOME=/app
RUN mkdir -p $HOME
WORKDIR $HOME
COPY . $HOME

# If you have a Vaadin Pro key, pass it as a secret with id "proKey".
# If you have a Vaadin Offline key, pass it as a secret with id "secretKey".
# Build the application with production profile

RUN --mount=type=cache,target=/root/.m2 \
    --mount=type=secret,id=proKey \
    --mount=type=secret,id=offlineKey \
    sh -c 'PRO_KEY=$(cat /run/secrets/proKey 2>/dev/null || echo "") && \
    OFFLINE_KEY=$(cat /run/secrets/offlineKey 2>/dev/null || echo "") && \
    ./mvnw clean package -Pproduction -DskipTests -Dvaadin.proKey=${PRO_KEY} -Dvaadin.offlineKey=${OFFLINE_KEY}'

FROM eclipse-temurin:21-jre-alpine
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=prod"]
