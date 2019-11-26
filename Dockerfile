FROM openjdk:8-jdk-alpine
MAINTAINER "Manojv" "manojv@ilimi.in"
RUN apk update \
    && apk add unzip \
    && apk add curl \
    && adduser -u 1001 -h /home/sunbird/ -D sunbird \
    && mkdir -p /home/sunbird/learner
COPY ./service/target/actor-service.jar /home/sunbird/learner/
RUN chown -R sunbird:sunbird /home/sunbird
EXPOSE 8088
USER sunbird
ENV HTTP_PROXY "http://172.22.218.218:8085"
ENV HTTPS_PROXY "http://172.22.218.218:8085"
ENV NO_PROXY "localhost,igx.mindtree.com,172.22.219.125,172.22.219.126,172.22.219.127,172.22.219.128,172.22.219.129,172.22.219.130,172.22.219.131,172.22.219.132,172.22.219.133,172.22.219.134,github.com,172.22.219.135,*.ekstep.in"
ENV http_proxy "http://172.22.218.218:8085"
ENV https_proxy "http://172.22.218.218:8085"
ENV no_proxy "localhost,igx.mindtree.com,172.22.219.125,172.22.219.126,172.22.219.127,172.22.219.128,172.22.219.129,172.22.219.130,172.22.219.131,172.22.219.132,172.22.219.133,172.22.219.134,github.com,172.22.219.135,*.ekstep.in"
WORKDIR /home/sunbird/learner/
CMD ["java",  "-cp", "actor-service.jar", "-Dactor_hostname=actor-service", "-Dbind_hostname=0.0.0.0", "org.sunbird.middleware.Application"]
