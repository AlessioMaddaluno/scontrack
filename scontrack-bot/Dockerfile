#
# BUILD STAGE
#

FROM maven:3.9.6-amazoncorretto-17 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn package -Dmaven.test.skip

#
# RUN STAGE
#

FROM amazoncorretto:17.0.9-al2023-headless
WORKDIR /app
COPY --from=build /build/target/*.jar scontrack-bot.jar

ENTRYPOINT ["java","-jar","./scontrack-bot.jar"]