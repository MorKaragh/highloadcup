FROM java:8

WORKDIR /app

ADD hlc.jar /app/hlc.jar

EXPOSE 80

CMD ["java","-jar","hlc.jar"]


