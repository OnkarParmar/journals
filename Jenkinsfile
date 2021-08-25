pipeline {
    agent any
    environment {
        REPO = "333490196116.dkr.ecr.ap-south-1.amazonaws.com"
        PROJECT = "teamteach-journals"
        USER = "ec2-user"
        DOMAIN = "myfamilycoach.ml"
    }
    stages{
        stage('Build') {
            steps {
                sh 'echo $GIT_BRANCH'
                sh "mvn install -Ddocker -Dbranch=${GIT_BRANCH}"
            }
        }
        stage('Push to ECR') {
            steps {
                sh 'docker tag ${PROJECT}:${GIT_BRANCH} ${REPO}/${PROJECT}:${GIT_BRANCH}'
                sh '$(aws ecr get-login --no-include-email --region ap-south-1)'
                sh "docker push ${REPO}/${PROJECT}:${GIT_BRANCH}"
            }
        }
        stage('Pull & Run') {
            steps {
                sh 'echo \'$(aws ecr get-login --no-include-email --region ap-south-1)\' > ${GIT_BRANCH}.sh'
                sh 'echo docker pull $REPO/$PROJECT:$GIT_BRANCH >> ${GIT_BRANCH}.sh'
                sh 'echo docker rm -f $PROJECT >> ${GIT_BRANCH}.sh'
                sh 'echo docker run -p 8083:8083 -d --name $PROJECT $REPO/$PROJECT:$GIT_BRANCH >> ${GIT_BRANCH}.sh'
                sh 'cat ${GIT_BRANCH}.sh | ssh ${USER}@$GIT_BRANCH.$DOMAIN' 
            }
        }
        stage('Cleanup') {
            when { branch 'dev' }
            steps {
                sh 'ssh ${USER}@$GIT_BRANCH.$DOMAIN \'$(aws ecr get-login --no-include-email --region ap-south-1) ; docker image prune -a \''
            }
        }
    }
}
