node {
    def branchName = env.BRANCH_NAME
    if (branchName == 'gateway') {
        dir('gateway') {
            load 'Jenkinsfile'
        }
    } else if (branchName == 'team-service') {
        checkout scm
        sh 'ls'
    } else {
        echo "No specific Jenkinsfile for this branch"
    }
}