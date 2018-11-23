FROM openjdk:8
COPY ./streams.examples/target/streams.examples-0.1.jar /tmp
WORKDIR /tmp
CMD ["java -jar streams.examples-0.1.jar"]
#ENTRYPOINT ["java","Main"]