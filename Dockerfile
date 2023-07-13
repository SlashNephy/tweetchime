FROM gradle:8.2.1-jdk17@sha256:a3e4681988d93f01c62e74e030d07852cc67e1ff459378c2516eb8a94a62e78b AS cache
WORKDIR /app
ENV GRADLE_USER_HOME /app/gradle
COPY *.gradle.kts gradle.properties /app/
RUN gradle shadowJar --parallel --console=verbose

FROM gradle:8.2.1-jdk17@sha256:a3e4681988d93f01c62e74e030d07852cc67e1ff459378c2516eb8a94a62e78b AS build
WORKDIR /app
COPY --from=cache /app/gradle /home/gradle/.gradle
COPY *.gradle.kts gradle.properties /app/
COPY src/main/ /app/src/main/
RUN gradle shadowJar --parallel --console=verbose

FROM amazoncorretto:18.0.1 as runtime
WORKDIR /app

COPY --from=build /app/build/libs/tweetchime-all.jar /app/tweetchime.jar

ENTRYPOINT ["java", "-jar", "/app/tweetchime.jar"]
