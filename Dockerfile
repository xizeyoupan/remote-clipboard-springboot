FROM node:18-alpine as frontend

RUN apk add --update git
WORKDIR /app

RUN git clone https://github.com/xizeyoupan/remote-clipboard-vue.git
RUN git clone https://github.com/xizeyoupan/vuefinder.git

WORKDIR /app/vuefinder
RUN npm i && npm run build

WORKDIR /app/remote-clipboard-vue
RUN npm i && npm run build


FROM maven:3.9-eclipse-temurin-17-alpine as backend

COPY . /app/
COPY --from=frontend /app/remote-clipboard-vue/dist /app/src/main/resources/static/dist

WORKDIR /app
RUN mvn -B package


from eclipse-temurin:17-jdk-alpine as prod

ARG UID
ARG GID
ARG PORT

ENV UID=${UID:-1010}
ENV GID=${GID:-1010}
ENV PORT=${PORT:-3000}

RUN addgroup -g ${GID} --system rc \
    && adduser -G rc --system -D -s /bin/sh -u ${UID} rc

COPY --from=backend /app/target/remote-clipboard-springboot-1.0-SNAPSHOT.jar /app/app.jar

RUN chown -R rc:rc /app
USER rc

EXPOSE ${PORT}

ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=prod"]
