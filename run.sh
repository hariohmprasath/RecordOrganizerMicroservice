mvn clean install
docker build -t record-organizor .
docker tag record-organizor:latest 775448517459.dkr.ecr.us-east-1.amazonaws.com/record-organizor:latest
docker push 775448517459.dkr.ecr.us-east-1.amazonaws.com/record-organizor:latest
