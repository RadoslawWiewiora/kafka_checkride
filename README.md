# kafka_checkride
Check-ride project for Confluent bootcamp

### Setup local environment
To run check-ride project locally you need to use Docker file (loan-approval/src/main/resources/docker-compose.yml)
This files is taken from Confluent GitHub project (https://github.com/confluentinc/cp-all-in-one/blob/7.3.0-post/cp-all-in-one/docker-compose.yml) and then modify.

To run it open terminal and type: 
cd loan-approval/src/main/resources/
docker compose up

When Confluent is ready start Kafka stream application (loan-approval/src/main/java/LoanApprovalApp.java)

Control Center should be available at http://localhost:9021/clusters

Install connect JDBC plugin:

1. docker-compose exec -u root connect confluent-hub install confluentinc/kafka-connect-jdbc:10.0.0
2. docker-compose restart connect

Add Postgres connector:

Use config file (loan-approval/src/main/resources/connect/connector_Internal-Clients-Connector-Avro_config.json)

#### Test on local environment 
Open terminal and produce some loan application
Open another terminal and check results
