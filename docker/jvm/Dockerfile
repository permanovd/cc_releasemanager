FROM eclipse-temurin:17-jdk-jammy
RUN mkdir /opt/app

# To make a build.
RUN mkdir /opt/app/sources
COPY . /opt/app/sources
WORKDIR /opt/app/sources
RUN ./mvnw clean install -DskipTests
RUN cp /opt/app/sources/target/releasemanager.jar /opt/app/releasemanager.jar

CMD ["java", "-jar", "/opt/app/releasemanager.jar"]