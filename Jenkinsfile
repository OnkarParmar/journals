pipeline {
    agent { label 'test-automation' }
    stages {
        stage('Build The Image') {
            steps {
                sh '/opt/maven/bin/mvn --version'
                sh '/opt/maven/bin/mvn install -Ddocker'
            }
        }

        stage('Push The Image') {
            steps {
                sh '/usr/bin/docker tag teamteach/journals:latest teamteach/journals:latest'
                sh '/usr/bin/docker push teamteach/journals:latest'
            }
        }

        stage('Pull and Run (ssh to ec2)') {
            steps {
                sh '/opt/bin/drm teamteach journals'
                sh '/usr/bin/docker-compose -f docker-compose.yml up -d'
            }
        }

    }
    environment {
        JAVA_HOME="/usr/lib/jvm/java-14-openjdk-amd64"
    }
}
