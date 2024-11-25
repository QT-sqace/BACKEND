pipeline {
    agent { label 'default' }  // built-in-node 노드에서 작업을 실행
    stages {
        stage('Checkout') {
            steps {
                script {
                    def branchName = env.BRANCH_NAME
                    if (branchName == 'gateway') {
                        dir('gateway') {
                            load 'Jenkinsfile'  // gateway 디렉토리 내의 Jenkinsfile 실행
                        }
                    } else if (branchName == 'team-service') {
                        dir('team-service') {
                            load 'Jenkinsfile'  // team-service 디렉토리 내의 Jenkinsfile 실행
                        }
                    } else {
                        echo "No specific Jenkinsfile for this branch"
                    }
                }
            }
        }
    }
}
