pipeline {
    agent any

    environment {
        PROJECT = "teamteach-journals"
        USER = "ec2-user"
<<<<<<< HEAD
        AWS_ACCOUNT = sh(script: 'aws sts get-caller-identity --query Account --output text', , returnStdout: true).trim()
        AWS_REGION = sh(script: 'aws configure get region', , returnStdout: true).trim()
        REPO = "${AWS_ACCOUNT}.dkr.ecr.${AWS_REGION}.amazonaws.com/${PROJECT}"
        ECR_LOGIN = "aws ecr get-login --no-include-email --region $AWS_REGION"
=======
        REGION = "$REGION"
        ECR_LOGIN = "aws ecr get-login --no-include-email --region $REGION"
>>>>>>> 0ed837b45b51c51de143f505ad57c155f150bd31
    }
    
    stages{
        stage('Build') {
            when { anyOf {
                expression { env.GIT_BRANCH == env.BRANCH_ONE }
                expression { env.GIT_BRANCH == env.BRANCH_TWO }
            } }
            steps {
                sh 'echo $REPO'
                sh 'echo $GIT_BRANCH'
                sh "cp src/main/resources/application-${GIT_BRANCH}.yml src/main/resources/application.yml"
                sh "mvn install -Ddocker -Dbranch=${GIT_BRANCH}"
            }
        }
        stage('Push to ECR') {
            when { anyOf {
                expression { env.GIT_BRANCH == env.BRANCH_ONE }
                expression { env.GIT_BRANCH == env.BRANCH_TWO }
            } }
            steps {
<<<<<<< HEAD
                sh 'docker tag ${PROJECT}:${GIT_BRANCH} ${REPO}:${GIT_BRANCH}'
                sh '$($ECR_LOGIN)'
                sh "docker push ${REPO}:${GIT_BRANCH}"
=======
                sh 'docker images'
                sh 'docker tag ${PROJECT}:${GIT_BRANCH} ${AWS_REPO}/${PROJECT}:${GIT_BRANCH}'
                sh '$($ECR_LOGIN)'
                sh "docker push ${AWS_REPO}/${PROJECT}:${GIT_BRANCH}"
>>>>>>> 0ed837b45b51c51de143f505ad57c155f150bd31
            }
        }
        stage('Pull & Run') {
            when { anyOf {
                expression { env.GIT_BRANCH == env.BRANCH_ONE }
                expression { env.GIT_BRANCH == env.BRANCH_TWO }
            } }
            steps {
                sh 'echo \$(${ECR_LOGIN}) > ${GIT_BRANCH}.sh'
<<<<<<< HEAD
                sh 'echo docker pull $REPO:$GIT_BRANCH >> ${GIT_BRANCH}.sh'
                sh 'echo docker rm -f $PROJECT >> ${GIT_BRANCH}.sh'
                sh 'echo docker run -e TZ=$TIMEZONE --net=host -p 8083:8083 -d --name $PROJECT $REPO:$GIT_BRANCH >> ${GIT_BRANCH}.sh'
                sh 'cat ${GIT_BRANCH}.sh | ssh ${USER}@${GIT_BRANCH}.$MS_DOMAIN' 
=======
                sh 'echo docker pull $AWS_REPO/$PROJECT:$GIT_BRANCH >> ${GIT_BRANCH}.sh'
                sh 'echo docker rm -f $PROJECT >> ${GIT_BRANCH}.sh'
                sh 'echo docker run -p 8083:8083 -d --name $PROJECT $AWS_REPO/$PROJECT:$GIT_BRANCH >> ${GIT_BRANCH}.sh'
                sh 'cat ${GIT_BRANCH}.sh'
                sh 'cat ${GIT_BRANCH}.sh | ssh ${USER}@$GIT_BRANCH.$DOMAIN' 
>>>>>>> 0ed837b45b51c51de143f505ad57c155f150bd31
            }
        }
    }
}
