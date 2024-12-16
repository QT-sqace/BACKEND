node {
    def branchName = env.BRANCH_NAME
    if (branchName == 'gateway') {
        checkout scm
        dir('gateway') {
            load 'Jenkinsfile'
        }
    } else if (branchName == 'chat-service') {
        checkout scm
        dir('chat_service') {
            load 'Jenkinsfile'
        }
    } else {
        echo "No specific Jenkinsfile for this branch"
    }
}