FROM maven:3.9.6-eclipse-temurin-17-alpine AS dependencies
WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline -B


FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /build

COPY --from=dependencies /root/.m2 /root/.m2
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests -B


FROM eclipse-temurin:17-jre-alpine

RUN apk add --no-cache dumb-init curl && \
    addgroup -S appgroup && \
    adduser -S appuser -G appgroup -h /app

WORKDIR /app

COPY --from=build --chown=appuser:appgroup /build/target/*.jar app.jar

USER appuser

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

EXPOSE 5000

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -fs http://localhost:${PORT:-5000}/actuator/health || exit 1

ENTRYPOINT ["dumb-init", "--"]
CMD ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]