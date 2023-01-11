# Use the official maven/Java 8 image to create a build artifact.
# https://hub.docker.com/_/maven
FROM docker.io/maven:3.5-jdk-8-alpine as builder

# Copy local code to the container image.
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build a release artifact.
RUN mvn clean package -DskipTests

# Use the Official OpenJDK image for a lean production stage of our multi-stage build.
# https://hub.docker.com/_/openjdk
# https://docs.docker.com/develop/develop-images/multistage-build/#use-multi-stage-builds
FROM docker.io/openjdk:8-jre-alpine

# Install some fonts
ENV LANG en_US.UTF-8
RUN apk add --update ttf-dejavu ttf-droid ttf-freefont ttf-liberation && rm -rf /var/cache/apk/*

# Copy the jar to the production image from the builder stage.
COPY --from=builder /app/target/imageOverlay-*.jar /imageOverlay.jar

# Service must listen to $PORT environment variable.
# This default value facilitates local development.
ENV PORT 8080

# Run the web service on container startup.
CMD ["java","-Dserver.port=${PORT}","-jar","/imageOverlay.jar"]