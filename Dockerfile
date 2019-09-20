FROM openjdk:jre-slim

MAINTAINER Marta Arcones "marta.arcones@gmail.com"

EXPOSE 4567

COPY build/libs/*.jar /usr/local/


WORKDIR /usr/local/

CMD java \
    -cp "*" \
	com.zooplus.cats.Main