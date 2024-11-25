pipeline {
    agent any
    environment {
        CI = 'false'
        DOCKER_IMAGE = 'hajewoong/qt-sqace-backend'
        PATH = "$PATH:$HOME/bin"
        JAVA_HOME = "/usr/lib/jvm/java-17-openjdk-17.0.13.0.11-3.0.1.el8.x86_64"
    }
    stages {
        stage('Checkout') {
            steps {
                script {
                    def branchName = env.BRANCH_NAME
                    if (branchName == 'gateway') {
                        dir('gateway') {
                            load 'gateway/Jenkinsfile'  // gateway 디렉토리 내의 Jenkinsfile 로드
                        }
                    } else if (branchName == 'team-service') {
                        dir('team-service') {
                            load 'team-service/Jenkinsfile'  // team-service 디렉토리 내의 Jenkinsfile 로드
                        }
                    } else {
                        echo "No specific Jenkinsfile for this branch"
                    }
                }
            }
        }
    }
}
