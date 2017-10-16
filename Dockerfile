FROM openjdk:8
RUN mkdir /etc/serivce 
COPY ./target/management-ui.jar /srv/management-ui.jar
WORKDIR /srv

EXPOSE 8080

ENTRYPOINT /usr/bin/java -Dconfig="/etc/service/management-ui.edn" -jar /srv/management-ui.jar


