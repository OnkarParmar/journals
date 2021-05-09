pipeline {
    agent any
    stages {
        stage('Build The Image') {
            steps {
                sh 'mvn clean install -Ddocker'
            }
        }

        stage('Push The Image') {
            steps {
                sh 'docker tag teamteach/journals:latest 333490196116.dkr.ecr.ap-south-1.amazonaws.com/teamteach-journals'
                sh '$(aws ecr get-login --no-include-email --region ap-south-1)'
                sh 'docker push 333490196116.dkr.ecr.ap-south-1.amazonaws.com/teamteach-journals'
            }
        }

        stage('Pull and Run (ssh to ec2)') {
            steps {
                sh 'ssh ec2-user@ms.digisherpa.ai \'$(aws ecr get-login --no-include-email --region ap-south-1) ; docker pull 333490196116.dkr.ecr.ap-south-1.amazonaws.com/teamteach-journals:latest; docker stop teamteach-journals ; docker rm teamteach-journals; docker run --net=host -d --name teamteach-journals 333490196116.dkr.ecr.ap-south-1.amazonaws.com/teamteach-journals:latest docker rmi $(docker images --filter "dangling=true" -q) \''
            }
        }

    }
}
