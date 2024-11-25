pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                script {
                    def branchName = env.BRANCH_NAME
                    if (branchName == 'gateway') {
                        dir('gateway') {
                            load 'Jenkinsfile'
                        }
                    } else if (branchName == 'team-service') {
                        dir('team-service') {
                            load 'Jenkinsfile'
                        }
                    } else {
                        echo "No specific Jenkinsfile for this branch"
                    }
                }
            }
        }
    }
}
