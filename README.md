# kafka_checkride
Check-ride project for Confluent bootcamp

### Setup local environment
To run check-ride project locally you need to use Docker file (loan-approval/src/main/resources/docker-compose.yml)
This files is taken from Confluent GitHub project (https://github.com/confluentinc/cp-all-in-one/blob/7.3.0-post/cp-all-in-one/docker-compose.yml)

To run it open terminal and type: 
cd loan-approval/src/main/resources/
docker compose up

When Confluent is ready start Kafka stream application (loan-approval/src/main/java/LoanApprovalApp.java)

Control Center should be available at http://localhost:9021/clusters

#### Test on local environment 
Open terminal and produce some loan application
Open another terminal and check results
