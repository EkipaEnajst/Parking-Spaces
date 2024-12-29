FROM eclipse-temurin:17
WORKDIR /opt/parkingspaces/
RUN mkdir api
COPY ./api ./api
CMD ["java", "-jar", "./api/target/parkingspaces-1.0-SNAPSHOT.jar"]